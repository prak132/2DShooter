package io.github.shooter.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.shooter.Main;
import io.github.shooter.game.Bullet;
import io.github.shooter.game.Player;
import io.github.shooter.multiplayer.ClientListener;
import io.github.shooter.multiplayer.GameClient;
import io.github.shooter.multiplayer.GameClient.PlayerData;

public class GameScreen implements Screen {
    // todo: add map skin
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 touchPoint;
    private ShapeRenderer shapeRenderer;
    
    private Player player;
    private float playerRadius = 15f;
    private float playerSpeed = 200f;
    private Vector2 playerVelocity;
    
    private ArrayList<Bullet> bullets;
    private long lastShotTime;
    private long shotCooldown = 250; // ms

    // multiplayer
    private boolean multiplayer;
    private String serverAddress = "localhost";
    private GameClient client;

    public GameScreen(Main game, boolean multiplayer, String serverAddress) {
        this.game = game;
        this.multiplayer = multiplayer;
        if (serverAddress != null && !serverAddress.isEmpty()) {
            this.serverAddress = serverAddress;
        }
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        touchPoint = new Vector3();
        player = new Player(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, playerRadius);
        playerVelocity = new Vector2(0, 0);
        
        shapeRenderer = new ShapeRenderer();
        
        bullets = new ArrayList<Bullet>();
    }

    @Override
    public void show() {
        if (multiplayer) {
            try {
                client = new GameClient(serverAddress);
                ClientListener.BulletListener bulletListener = new ClientListener.BulletListener() {
                    @Override
                    public void onBulletFired(int playerId, float x, float y, float dirX, float dirY) {
                        bullets.add(new Bullet(x, y, dirX, dirY, playerId));
                    }
                };
                
                ClientListener.PlayerHitListener hitListener = new ClientListener.PlayerHitListener() {
                    @Override
                    public void onPlayerHit(int sourceId, float damage) {
                        player.takeDamage(damage);
                    }
                };
                
                ClientListener clientListener = new ClientListener(client);
                clientListener.setBulletListener(bulletListener);
                clientListener.setPlayerHitListener(hitListener);
                client.getClient().addListener(clientListener);
            } catch (IOException e) {
                System.err.println("Failed to connect to server: " + e.getMessage());
            }
        }
    }

    @Override 
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!player.isAlive() && player.shouldRespawn()) {
            player.respawn(WORLD_WIDTH, WORLD_HEIGHT);
        }
        
        if (player.isAlive()) {
            handleInput(delta);
            player.update(delta, WORLD_WIDTH, WORLD_HEIGHT);
        }
        
        updateBullets(delta);
        
        if (multiplayer) {
            checkBulletCollisions();
        }
        
        camera.update();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        
        if (player.isAlive()) {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.circle(player.getX(), player.getY(), player.getRadius());
        }
        
        if (multiplayer && client != null) {
            Map<Integer, PlayerData> otherPlayers = client.getOtherPlayers();
            for (PlayerData otherPlayer : otherPlayers.values()) {
                if (otherPlayer.alive) {
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.circle(otherPlayer.x, otherPlayer.y, playerRadius);
                    otherPlayer.update(otherPlayer.x, otherPlayer.y);
                }
            }
        }
        
        shapeRenderer.setColor(Color.RED);
        for (Bullet bullet : bullets) {
            shapeRenderer.circle(bullet.getX(), bullet.getY(), bullet.getRadius());
        }
        
        shapeRenderer.end();
        
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // show health stuff
        // todo remove?
        game.font.draw(game.batch, "Health: " + (int)player.getHealth(), 10, WORLD_HEIGHT - 30);
        
        if (!player.isAlive()) {
            String respawnMessage = "You died! Respawning in " + (int)(player.getTimeToRespawn() / 1000 + 1) + "...";
            game.font.draw(game.batch, respawnMessage, WORLD_WIDTH / 2 - 100, WORLD_HEIGHT / 2);
        }
        
        game.batch.end();
        
        if (multiplayer && client != null && player.isAlive()) {
            client.sendPlayerUpdate(player.getX(), player.getY(), player.getHealth(), player.isAlive());
        }
    }
    
    private void handleInput(float delta) {
        playerVelocity.set(0, 0);
        if (Gdx.input.isKeyPressed(Keys.A)) {
            playerVelocity.x -= playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            playerVelocity.x += playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Keys.W)) {
            playerVelocity.y += playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            playerVelocity.y -= playerSpeed;
        }
        player.setVelocity(playerVelocity);
        
        // mouse stuff
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && TimeUtils.timeSinceMillis(lastShotTime) > shotCooldown) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            float dirX = mousePos.x - player.getX();
            float dirY = mousePos.y - player.getY();
            shoot(dirX, dirY);
            lastShotTime = TimeUtils.millis();
        }
    }

    private void shoot(float dirX, float dirY) {
        if (!player.isAlive()) return;
        int ownerId = multiplayer && client != null ? client.getClientId() : 0;
        bullets.add(new Bullet(player.getX(), player.getY(), dirX, dirY, ownerId));
        if (multiplayer && client != null) {
            client.sendBulletShot(player.getX(), player.getY(), dirX, dirY);
        }
    }
    
    private void updateBullets(float delta) {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.update(delta);
            if (bullet.isOutOfBounds(WORLD_WIDTH, WORLD_HEIGHT) || bullet.isExpired()) {
                iter.remove();
            }
        }
    }
    
    private void checkBulletCollisions() {
        if (!player.isAlive() || client == null) return;
        Map<Integer, PlayerData> otherPlayers = client.getOtherPlayers();
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            if (bullet.getOwnerId() != client.getClientId()) {
                Circle bulletCircle = new Circle(bullet.getX(), bullet.getY(), bullet.getRadius());
                if (Intersector.overlaps(bulletCircle, player.getHitbox())) {
                    player.takeDamage(25f);
                    iter.remove();
                    continue;
                }
            }
            
            // bullet player col
            if (bullet.getOwnerId() == client.getClientId()) {
                Circle bulletCircle = new Circle(bullet.getX(), bullet.getY(), bullet.getRadius());
                for (Map.Entry<Integer, PlayerData> entry : otherPlayers.entrySet()) {
                    int otherPlayerId = entry.getKey();
                    PlayerData otherPlayer = entry.getValue();
                    if (otherPlayer.alive && Intersector.overlaps(bulletCircle, otherPlayer.hitbox)) {
                        client.sendPlayerHit(otherPlayerId);
                        iter.remove();
                        break;
                    }
                }
            }
        }
    }

    @Override 
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    
    @Override 
    public void dispose() {
        shapeRenderer.dispose();
        if (client != null) {
            client.close();
        }
    }
}