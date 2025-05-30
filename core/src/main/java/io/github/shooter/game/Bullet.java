package io.github.shooter.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Represents a bullet shot by a player.
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
     * Updates bullet position based on velocity and time delta.
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
     * Stops the bullet by zeroing velocity and marking it stopped.
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

    /**
     * Sets the damage for a bullet.
     */
    public void setDamage(float d) {
        damage = d;
    }

    /**
     * Gets the damage of the bullet.
     *
     * @return Damage value
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Sets the lifetime of a bullet.
     *
     * @param ms Lifetime in milliseconds
     */
    public void setLifetime(long ms) {
        lifetime = ms;
    }

    /**
     * Set the bullet radius.
     * 
     * @param r Radius in pixels
     */
    public void setRadius(float r) {
        radius = r;
    }

    /**
     * Gets the position of the bullet.
     *
     * @return Position in a 2D vector.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Gets the position of the bullet.
     *
     * @return X-Coordinate
     */
    public float getX() {
        return position.x;
    }

    /**
     * Gets the position of the bullet.
     *
     * @return Y-Coordinate
     */
    public float getY() {
        return position.y;
    }

    /**
     * Gets the velocity of the bullet.
     *
     * @return X-Component of the Velocity
     */
    public float getVelX() {
        return velocity.x;
    }

    /**
     * Gets the velocity of the bullet.
     *
     * @return Y-Component of the Velocity
     */
    public float getVelY() {
        return velocity.y;
    }

    /**
     * Gets the radius of the bullet's hitbox.
     *
     * @return Radius in pixels
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Gets the owner of the bullet.
     *
     * @return Owner ID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Checks if the bullet has stopped meaning its "dead".
     *
     * @return true if the bullet is stopped
     */
    public boolean isStopped() {
        return stopped;
    }
}
