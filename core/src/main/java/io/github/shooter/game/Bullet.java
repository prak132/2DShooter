package io.github.shooter.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Represents a bullet shot by a player. Keeps track of position, velocity,
 * damage, and lifetime.
 */
public class Bullet {

    private final Vector2 position;
    private final Vector2 velocity;

    private float radius = 5f;
    private float speed = 400f;
    private final long creationTime;
    private final int ownerId;

    private float damage = 25f;
    private long lifetime = 1000;

    private boolean stopped = false;

    /**
     * Creates a new bullet at given position, heading in the given direction.
     * If dirX or dirY are big (over 10), treat them as velocity vector
     * directly, otherwise normalize direction and multiply by default speed.
     *
     * @param x X position start
     * @param y Y position start
     * @param dirX X direction or velocity component
     * @param dirY Y direction or velocity component
     * @param ownerId ID of player who fired this bullet
     */
    public Bullet(float x, float y, float dirX, float dirY, int ownerId) {
        position = new Vector2(x, y);
        if (Math.abs(dirX) > 10 || Math.abs(dirY) > 10) {
            velocity = new Vector2(dirX, dirY);
            speed = velocity.len();
        } else {
            velocity = new Vector2(dirX, dirY).nor().scl(speed);
        }
        creationTime = TimeUtils.millis();
        this.ownerId = ownerId;
    }

    /**
     * Updates bullet position based on velocity and time delta. Doesn't move if
     * bullet is stopped.
     *
     * @param delta Time since last update (seconds)
     */
    public void update(float delta) {
        if (stopped) {
            return;
        }
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }

    /**
     * Stops the bullet by zeroing velocity and marking it stopped. Usually
     * called when it hits something.
     */
    public void stop() {
        velocity.setZero();
        stopped = true;
    }

    /**
     * Checks if the bullet is out of the visible bounds plus some padding.
     *
     * @param w Width of the game area
     * @param h Height of the game area
     * @return true if bullet is out of bounds
     */
    public boolean isOutOfBounds(float w, float h) {
        return position.x < -radius || position.x > w + radius
                || position.y < -radius || position.y > h + radius;
    }

    /**
     * Checks if the bullet's lifetime has expired.
     *
     * @return true if bullet has been alive longer than its lifetime
     */
    public boolean isExpired() {
        return TimeUtils.timeSinceMillis(creationTime) > lifetime;
    }

    public void setDamage(float d) {
        damage = d;
    }

    public float getDamage() {
        return damage;
    }

    public void setLifetime(long ms) {
        lifetime = ms;
    }

    public void setRadius(float r) {
        radius = r;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getVelX() {
        return velocity.x;
    }

    public float getVelY() {
        return velocity.y;
    }

    public float getRadius() {
        return radius;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public boolean isStopped() {
        return stopped;
    }
}
