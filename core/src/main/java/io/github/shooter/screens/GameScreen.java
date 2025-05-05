package io.github.shooter.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import io.github.shooter.game.GameMap;
import io.github.shooter.game.Player;
import io.github.shooter.multiplayer.ClientListener;
import io.github.shooter.multiplayer.GameClient;
import io.github.shooter.multiplayer.GameClient.PlayerData;

public class GameScreen implements Screen {

    private static final float WORLD_WIDTH = 2000, WORLD_HEIGHT = 2000;
    private static final float PLAYER_RADIUS = 40f;

    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    private final Player player;
    private final GameMap map;
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    private final Vector2 vel = new Vector2();
    private final float[] accel = { 0, 0, 0, 0 };
    private final float speed = 200f, slide = 12f;

    private long lastShot;
    private final long shotCooldown = 250;

    private final boolean multiplayer;
    private final String serverAddress;
    private GameClient client;

    private final InputAdapter input;

    public GameScreen(Main game, boolean multiplayer, String serverAddress) {
        this.game = game;
        this.multiplayer = multiplayer;
        this.serverAddress = (serverAddress == null || serverAddress.isEmpty()) ? "localhost" : serverAddress;

        camera = new OrthographicCamera();
        camera.zoom = 0.2f;
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        shapeRenderer = new ShapeRenderer();
        batch = game.batch;

        map = new GameMap();
        player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, PLAYER_RADIUS);

        input = new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int button) {
                if (button == Input.Buttons.LEFT && TimeUtils.timeSinceMillis(lastShot) > shotCooldown) {
                    Vector3 w = camera.unproject(new Vector3(sx, sy, 0));
                    shoot(w.x - player.getX(), w.y - player.getY());
                    lastShot = TimeUtils.millis();
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(input);
        if (!multiplayer)
            return;
        try {
            client = new GameClient(serverAddress, true);
            ClientListener l = new ClientListener(client);
            l.setBulletListener((id, x, y, dx, dy) -> bullets.add(new Bullet(x, y, dx, dy, id)));
            l.setPlayerHitListener((src, dmg) -> player.takeDamage(dmg));
            client.getClient().addListener(l);
        } catch (IOException e) {
            System.err.println("Connect failed: " + e.getMessage());
        }
    }

    @Override
    public void render(float dt) {
        updateBullets(dt);
        if (multiplayer)
            checkBulletCollisions();
        if (!player.isAlive() && player.shouldRespawn())
            player.respawn(WORLD_WIDTH, WORLD_HEIGHT);

        if (player.isAlive()) {
            handleInput();
            player.update(dt, WORLD_WIDTH, WORLD_HEIGHT, map.getObstacles());
        }

        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.render(batch);
        player.render(batch);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);

        for (Bullet b : bullets) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(b.getX(), b.getY(), b.getRadius());
            shapeRenderer.setColor(1, 0, 0, 0.5f);
            shapeRenderer.circle(b.getX() -  0.01f * b.getVelX(), b.getY() -  0.01f * b.getVelY(), b.getRadius() / 1.1f);
            shapeRenderer.setColor(1, 0, 0, 0.2f);
            shapeRenderer.circle(b.getX() -  0.02f * b.getVelX(), b.getY() -  0.02f * b.getVelY(), b.getRadius() / 1.2f);
        }
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 dir = new Vector2(mouse.x - player.getX(), mouse.y - player.getY()).nor();
        float gunLen = PLAYER_RADIUS * 1.0f;
        float gunWidth = PLAYER_RADIUS / 2.0f;
        Vector2 gunEnd = new Vector2(player.getX(), player.getY()).add(dir.scl(gunLen));
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rectLine(player.getX(), player.getY(), gunEnd.x, gunEnd.y, gunWidth);
        shapeRenderer.end();

        batch.begin();
        game.font.draw(batch, "Health: " + (int) player.getHealth(),
                camera.position.x - WORLD_WIDTH / 2 + 15,
                camera.position.y + WORLD_HEIGHT / 2 - 20);
        if (!player.isAlive())
            game.font.draw(batch,
                    "Respawning in " + (int) (player.getTimeToRespawn() / 1000 + 1),
                    camera.position.x - 90, camera.position.y);
        batch.end();

        if (multiplayer && client != null && player.isAlive())
            client.sendPlayerUpdate(player.getX(), player.getY(), player.getHealth(), true);
    }

    private void handleInput() {
        vel.set(0, 0);
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
        player.setVelocity(vel);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && TimeUtils.timeSinceMillis(lastShot) > shotCooldown) {
            Vector3 w = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            shoot(w.x - player.getX(), w.y - player.getY());
            lastShot = TimeUtils.millis();
        }
    }

    private void shoot(float dx, float dy) {
        if (!player.isAlive())
            return;
        int owner = (multiplayer && client != null) ? client.getClientId() : 0;
        bullets.add(new Bullet(player.getX(), player.getY(), dx, dy, owner));
        if (multiplayer && client != null)
            client.sendBulletShot(player.getX(), player.getY(), dx, dy);
    }

    private void updateBullets(float dt) {
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext();) {
            Bullet b = it.next();
            b.update(dt);
            if (b.isOutOfBounds(WORLD_WIDTH, WORLD_HEIGHT) || b.isExpired())
                it.remove();
        }
    }

    private void checkBulletCollisions() {
        if (!player.isAlive() || client == null)
            return;
        Map<Integer, PlayerData> others = client.getOtherPlayers();
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext();) {
            Bullet b = it.next();
            Circle bc = new Circle(b.getX(), b.getY(), b.getRadius());

            if (b.getOwnerId() != client.getClientId() && Intersector.overlaps(bc, player.getHitbox())) {
                player.takeDamage(25f);
                it.remove();
                continue;
            }
            if (b.getOwnerId() == client.getClientId()) {
                for (Map.Entry<Integer, PlayerData> e : others.entrySet()) {
                    if (e.getValue().alive && Intersector.overlaps(bc, e.getValue().hitbox)) {
                        client.sendPlayerHit(e.getKey());
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
        if (client != null)
            client.close();
    }
}
