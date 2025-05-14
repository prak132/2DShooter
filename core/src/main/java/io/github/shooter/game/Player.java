package io.github.shooter.game;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.shooter.game.weapons.Gun;
import io.github.shooter.game.weapons.GunFactory;
import io.github.shooter.game.weapons.GunFactory.GunType;

public class Player {

    private final Circle hitbox;
    private final Vector2 velocity;
    private final Texture texture;
    private final float maxSpeed = 125f;

    private float speed = 125f;
    private float health = 200f;
    private boolean alive = true;
    private long respawnTime = 0;
    private static final long RESPAWN_DELAY = 3000;
    private float rotationAngleDeg = 0f;

    private Array<Gun> guns;
    private int currentGunIndex;
    private boolean isFiring;

    private static final float MIN_SPAWN_DISTANCE = 300f;
    private static final int MAX_RESPAWN_ATTEMPTS = 20;

    public Player(float x, float y, float radius) {
        hitbox = new Circle(x, y, radius);
        velocity = new Vector2();
        texture = new Texture("Player1.png");
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        guns = new Array<>();
        guns.add(GunFactory.createGun(GunType.ASSAULT_RIFLE));
        guns.add(GunFactory.createGun(GunType.SNIPER_RIFLE));
        guns.add(GunFactory.createGun(GunType.SUBMACHINE_GUN));
        currentGunIndex = 0;
        isFiring = false;
    }

    public void update(float delta, float screenW, float screenH, Array<Rectangle> obstacles) {
        getCurrentGun().update();
        
        Vector2 move = new Vector2(velocity).scl(delta);
        
        hitbox.x += move.x;
        for (Rectangle r : obstacles) {
            if (Intersector.overlaps(hitbox, r)) {
                if (move.x > 0) {
                    hitbox.x = r.x - hitbox.radius;
                } else if (move.x < 0) {
                    hitbox.x = r.x + r.width + hitbox.radius;
                }
                break;
            }
        }
        
        hitbox.y += move.y;
        for (Rectangle r : obstacles) {
            if (Intersector.overlaps(hitbox, r)) {
                if (move.y > 0) {
                    hitbox.y = r.y - hitbox.radius;
                } else if (move.y < 0) {
                    hitbox.y = r.y + r.height + hitbox.radius;
                }
                break;
            }
        }

        if (hitbox.x - hitbox.radius < 0) hitbox.x = hitbox.radius;
        if (hitbox.x + hitbox.radius > screenW) hitbox.x = screenW - hitbox.radius;
        if (hitbox.y - hitbox.radius < 0) hitbox.y = hitbox.radius;
        if (hitbox.y + hitbox.radius > screenH) hitbox.y = screenH - hitbox.radius;
    }

    public void handleGunInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) switchToGun(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) switchToGun(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) switchToGun(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) switchToGun((currentGunIndex + 1) % guns.size);
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) switchToGun((currentGunIndex - 1 + guns.size) % guns.size);
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            getCurrentGun().reload();
            speed = maxSpeed;
        }
    }

    public boolean attemptToFire() {
        if (!alive) return false;
        return getCurrentGun().fire();
    }

    public boolean fireAt(float dirX, float dirY) {
        if (!alive) return false;
        return attemptToFire();
    }

    public void switchToGun(int index) {
        if (index >= 0 && index < guns.size) currentGunIndex = index;
    }

    public Gun getCurrentGun() {
        return guns.get(currentGunIndex);
    }

    public void render(SpriteBatch batch) {
        if (!alive) return;
        
        float size = hitbox.radius * 2f;
        batch.draw(texture,
                hitbox.x - hitbox.radius, hitbox.y - hitbox.radius,
                hitbox.radius, hitbox.radius,
                size, size,
                1f, 1f,
                rotationAngleDeg + 90f,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false
        );
    }

    public void renderGunInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        Gun gun = getCurrentGun();
        String statusText = "";
        if (gun.isReloading()) {
            statusText += " Reloading: " + String.format("%.1f", gun.getReloadTimeRemaining()) + "s";
        }
        font.getData().setScale(2.5f);
        font.draw(batch, statusText, x, y);
    }

    public void takeDamage(float dmg) {
        if (!alive) return;
        if ((health -= dmg) <= 0) {
            health = 0;
            alive = false;
            respawnTime = TimeUtils.millis() + RESPAWN_DELAY;
        }
    }

    public void respawn(float w, float h, Map<Integer, ?> otherPlayers) {
        alive = true;
        health = 200f;
        
        if (otherPlayers == null || otherPlayers.isEmpty()) {
            hitbox.x = 100 + (float) Math.random() * (w - 200);
            hitbox.y = 100 + (float) Math.random() * (h - 200);
            return;
        }
        
        for (int attempt = 0; attempt < MAX_RESPAWN_ATTEMPTS; attempt++) {
            float x = 100 + (float) Math.random() * (w - 200);
            float y = 100 + (float) Math.random() * (h - 200);
            boolean positionIsGood = true;
            for (Object playerObj : otherPlayers.values()) {
                if (playerObj instanceof Circle) {
                    Circle otherHitbox = (Circle) playerObj;
                    float distance = Vector2.dst(x, y, otherHitbox.x, otherHitbox.y);
                    if (distance < MIN_SPAWN_DISTANCE) {
                        positionIsGood = false;
                        break;
                    }
                }
            }
            
            if (positionIsGood) {
                hitbox.x = x;
                hitbox.y = y;
                return;
            }
        }
        
        hitbox.x = 100 + (float) Math.random() * (w - 200);
        hitbox.y = 100 + (float) Math.random() * (h - 200);
    }
    
    public void respawn(float w, float h) {
        respawn(w, h, null);
    }

    public boolean shouldRespawn() {
        return !alive && TimeUtils.millis() > respawnTime;
    }

    public void setVelocity(Vector2 v) {
        velocity.set(v);
    }

    public Circle getHitbox() {
        return hitbox;
    }

    public float getX() {
        return hitbox.x;
    }

    public float getY() {
        return hitbox.y;
    }

    public float getHealth() {
        return health;
    }

    public boolean isAlive() {
        return alive;
    }

    public long getTimeToRespawn() {
        return respawnTime - TimeUtils.millis();
    }

    public void setIsFiring(boolean firing) {
        this.isFiring = firing;
    }

    public boolean isFiring() {
        return isFiring;
    }

    public void dispose() {
        texture.dispose();
    }

    public void setRotationAngleDeg(float angle) {
        this.rotationAngleDeg = angle;
    }

    public float getRotationAngleDeg() {
        return rotationAngleDeg;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float spd) {
        this.speed = spd;
    }
}
