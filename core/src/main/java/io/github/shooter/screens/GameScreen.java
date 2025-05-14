package io.github.shooter.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.shooter.Main;
import io.github.shooter.game.Bullet;
import io.github.shooter.game.GameMap;
import io.github.shooter.game.Player;
import io.github.shooter.game.weapons.Gun;
import io.github.shooter.multiplayer.ClientListener;
import io.github.shooter.multiplayer.GameClient;
import io.github.shooter.multiplayer.GameClient.PlayerData;

// TODO: Seperate code out into different classes to make this file more readable
public class GameScreen implements Screen {

    private static final float WORLD_WIDTH = 1312, WORLD_HEIGHT = 1232;
    private static final float PLAYER_RADIUS = 16f;

    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    
    private final OrthographicCamera uiCamera;
    private final Viewport uiViewport;
    
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    private final Player player;
    private final GameMap map;
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    private final Vector2 vel = new Vector2();
    private final float[] accel = {0, 0, 0, 0};
    private final float slide = 20f;

    private long lastShot;

    private Vector3 mousePosition = new Vector3();
    private Vector2 aimDirection = new Vector2();

    private final boolean multiplayer;
    private final String serverAddress;
    private GameClient client;

    private final InputAdapter input;

    public GameScreen(Main game, boolean multiplayer, String serverAddress) {
        this.game = game;
        this.multiplayer = multiplayer;
        this.serverAddress = (serverAddress == null || serverAddress.isEmpty()) ? "localhost" : serverAddress;

        camera = new OrthographicCamera();
        camera.zoom = 0.30f;
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera);
        uiViewport.apply();

        shapeRenderer = new ShapeRenderer();
        batch = game.batch;

        map = new GameMap();
        player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, PLAYER_RADIUS);

        input = new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int button) {
                if (button == Input.Buttons.LEFT) {
                    player.setIsFiring(true);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    player.setIsFiring(false);
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(input);
        if (!multiplayer) {
            return;
        }
        try {
            client = new GameClient(serverAddress, true);
            ClientListener l = new ClientListener(client);
            l.setBulletListener((id, x, y, dx, dy, damage) -> {
                Bullet bullet = new Bullet(x, y, dx, dy, id);
                bullet.setDamage(damage);
                bullet.setRadius(Math.min(5f + (damage / 10f), 10f));
                bullets.add(bullet);
            });
            l.setPlayerHitListener((src, dmg) -> player.takeDamage(dmg));
            client.getClient().addListener(l);
        } catch (IOException e) {
            System.err.println("Connect failed: " + e.getMessage());
        }
    }

    @Override
    public void render(float dt) {
        if (multiplayer && client != null) {
            client.initializeEnemyTextures();
        }
        
        updateBullets(dt);
        if (multiplayer) {
            checkBulletCollisions();
        }
        if (!player.isAlive() && player.shouldRespawn()) {
            if (multiplayer && client != null) {
                Map<Integer, Circle> otherPlayersHitboxes = new HashMap<>();
                for (Map.Entry<Integer, PlayerData> entry : client.getOtherPlayers().entrySet()) {
                    if (entry.getValue().alive) {
                        otherPlayersHitboxes.put(entry.getKey(), entry.getValue().hitbox);
                    }
                }
                player.respawn(WORLD_WIDTH, WORLD_HEIGHT, otherPlayersHitboxes);
            } else {
                player.respawn(WORLD_WIDTH, WORLD_HEIGHT);
            }
        }

        if (player.isAlive()) {
            handleInput();
            player.update(dt, WORLD_WIDTH, WORLD_HEIGHT, map.getObstacles());

            mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePosition);
            aimDirection.set(mousePosition.x - player.getX(), mousePosition.y - player.getY()).nor();
            float angleDeg = MathUtils.atan2(aimDirection.y, aimDirection.x) * MathUtils.radiansToDegrees;
            player.setRotationAngleDeg(angleDeg);
            player.setSpeed(200f);

            if (player.isFiring()) {
                if (!player.getCurrentGun().isReloading()) {
                    player.setSpeed(100f);
                }
                Vector2 bulletDirection = new Vector2(aimDirection);
                if (player.fireAt(aimDirection.x, aimDirection.y)) {
                    Gun currentGun = player.getCurrentGun();
                    float damage = currentGun.getDamage();
                    float speed = currentGun.getBulletSpeed();

                    // spread
                    float spread = currentGun.getSpread();
                    if (spread > 0) {
                        float angle = MathUtils.atan2(bulletDirection.y, bulletDirection.x);
                        float spreadRadians = MathUtils.degreesToRadians * spread;
                        float randomSpread = MathUtils.random(-spreadRadians / 2, spreadRadians / 2);
                        angle += randomSpread;
                        bulletDirection.x = MathUtils.cos(angle);
                        bulletDirection.y = MathUtils.sin(angle);
                    }

                    int owner = (multiplayer && client != null) ? client.getClientId() : 0;
                    Bullet bullet = new Bullet(player.getX(), player.getY(),
                            bulletDirection.x * speed,
                            bulletDirection.y * speed, owner);
                    bullet.setDamage(damage);
                    bullet.setRadius(Math.min(5f + (damage / 10f), 10f));
                    bullets.add(bullet);
                    if (multiplayer && client != null) {
                        client.sendBulletShot(player.getX(), player.getY(),
                                bulletDirection.x * speed,
                                bulletDirection.y * speed,
                                damage);
                    }
                }
            }
            player.handleGunInput();
        }

        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.render(batch); // draw map
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);

        for (Bullet b : bullets) {
            shapeRenderer.setColor(1f, 0.3f, 0f, 1f); // orange
            shapeRenderer.circle(b.getX(), b.getY(), b.getRadius());

            shapeRenderer.setColor(1f, 0.5f, 0f, 0.6f);
            shapeRenderer.circle(b.getX() - 0.01f * b.getVelX(), b.getY() - 0.01f * b.getVelY(), b.getRadius() * 0.8f);

            shapeRenderer.setColor(1f, 0.7f, 0f, 0.3f);
            shapeRenderer.circle(b.getX() - 0.02f * b.getVelX(), b.getY() - 0.02f * b.getVelY(), b.getRadius() * 0.6f);
        }
        if (player.isAlive()) {
            Gun currentGun = player.getCurrentGun();
            currentGun.gunRecoil();
            shapeRenderer.setColor(currentGun.getColor());
            float gunLength = PLAYER_RADIUS * currentGun.getLength();
            float gunWidth = PLAYER_RADIUS * currentGun.getThickness();
            Vector2 gunEnd = new Vector2(player.getX(), player.getY()).add(new Vector2(aimDirection).scl(gunLength));
            shapeRenderer.rectLine(player.getX(), player.getY(), gunEnd.x, gunEnd.y, gunWidth);
        }

        if (multiplayer && client != null) {
            for (PlayerData otherPlayer : client.getOtherPlayers().values()) {
                if (otherPlayer.alive) {
                    float enemyAngle = otherPlayer.rotation * MathUtils.degreesToRadians;
                    Vector2 enemyDirection = new Vector2(MathUtils.cos(enemyAngle), MathUtils.sin(enemyAngle));
                    Vector2 gunEnd = new Vector2(otherPlayer.x, otherPlayer.y).add(
                            new Vector2(enemyDirection).scl(PLAYER_RADIUS * 1.5f));
                    shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f); // everything grey TODO: Change color based off weapon they are using
                    shapeRenderer.rectLine(otherPlayer.x, otherPlayer.y, gunEnd.x, gunEnd.y, PLAYER_RADIUS * 0.3f);
                }
            }
        }

        shapeRenderer.end();

        batch.begin();
        player.render(batch);
        if (multiplayer && client != null) {
            for (PlayerData otherPlayer : client.getOtherPlayers().values()) {
                otherPlayer.enemyPlayer.render(batch);
            }
        }
        
        batch.end();

        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        
        String healthText = "Health: " + (int) player.getHealth();
        batch.setColor(1, 1, 1, 1);
        game.font.getData().setScale(2.5f);
        game.font.draw(batch, healthText, 50, WORLD_HEIGHT - 40);

        if (multiplayer && client != null) {
            client.updatePing();
            String pingText = "Ping: " + client.getPing() + " ms";
            float textWidth = game.font.getData().spaceXadvance * pingText.length() * 1.2f;
            game.font.draw(batch, pingText, WORLD_WIDTH - textWidth - 120, WORLD_HEIGHT - 40);
        }
        
        game.font.getData().setScale(1.0f);

        Vector3 playerScreenPos = new Vector3(player.getX(), player.getY(), 0);
        camera.project(playerScreenPos);
        uiCamera.unproject(playerScreenPos);
        player.renderGunInfo(batch, game.font, playerScreenPos.x + 40, playerScreenPos.y - 40);

        if (!player.isAlive()) {
            String respawnText = "Respawning in " + (int) (player.getTimeToRespawn() / 1000 + 1);
            float textWidth = game.font.getRegion().getRegionWidth() * respawnText.length() * 0.5f;
            game.font.draw(batch, respawnText,
                    WORLD_WIDTH / 2 - textWidth / 2, WORLD_HEIGHT / 2);
        }
        batch.end();

        if (multiplayer && client != null) {
            if (player.isAlive()) {
                client.sendPlayerUpdate(player.getX(), player.getY(), player.getHealth(), true, player.getRotationAngleDeg());
            } else {
                client.sendPlayerUpdate(player.getX(), player.getY(), 0, false, player.getRotationAngleDeg());
            }
        }
    }

    private void handleInput() {
        vel.set(0, 0);
        float speed = player.getSpeed();
        accel[0] = Gdx.input.isKeyPressed(Keys.A) ? Math.max(accel[0] - speed / slide, -speed)
                : Math.min(accel[0] + speed / slide, 0);
        accel[1] = Gdx.input.isKeyPressed(Keys.D) ? Math.min(accel[1] + speed / slide, speed)
                : Math.max(accel[1] - speed / slide, 0);
        accel[2] = Gdx.input.isKeyPressed(Keys.S) ? Math.max(accel[2] - speed / slide, -speed)
                : Math.min(accel[2] + speed / slide, 0);
        accel[3] = Gdx.input.isKeyPressed(Keys.W) ? Math.min(accel[3] + speed / slide, speed)
                : Math.max(accel[3] - speed / slide, 0);
        vel.x = accel[0] + accel[1];
        vel.y = accel[2] + accel[3];
        
        if (vel.x != 0 && vel.y != 0) {
            vel.nor().scl(speed);
        }
        
        player.setVelocity(vel);
    }

    private void updateBullets(float dt) {
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext();) {
            Bullet b = it.next();
            b.update(dt);
            if (b.isOutOfBounds(WORLD_WIDTH, WORLD_HEIGHT) || b.isExpired()) {
                it.remove();
                continue;
            }
            Circle bc = new Circle(b.getX(), b.getY(), b.getRadius());
            for (Rectangle r : map.getObstacles()) {
                if (Intersector.overlaps(bc, r)) {
                    b.stop();
                    it.remove();
                    break;
                }
            }
        }
    }
    

    private void checkBulletCollisions() {
        if (!player.isAlive() || client == null) {
            return;
        }
        Map<Integer, PlayerData> others = client.getOtherPlayers();
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext();) {
            Bullet b = it.next();
            if (b.isStopped()) {
                continue;
            }
            
            Circle bc = new Circle(b.getX(), b.getY(), b.getRadius());
            if (b.getOwnerId() != client.getClientId() && Intersector.overlaps(bc, player.getHitbox())) {
                player.takeDamage(b.getDamage());
                b.stop();
                it.remove();
                continue;
            }
            
            if (b.getOwnerId() == client.getClientId()) {
                for (Map.Entry<Integer, PlayerData> e : others.entrySet()) {
                    if (e.getValue().alive && Intersector.overlaps(bc, e.getValue().hitbox)) {
                        client.sendPlayerHit(e.getKey(), b.getDamage());
                        b.stop();
                        it.remove();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
        uiViewport.update(w, h, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        map.dispose();
        player.dispose();
        if (client != null) {
            client.close();
        }
    }
}
