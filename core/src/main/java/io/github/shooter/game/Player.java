package io.github.shooter.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Player {
    private Circle hitbox;
    private Vector2 velocity;
    private float speed = 200f;
    private float health = 100f;
    private boolean alive = true;
    private long respawnTime = 0;
    private static final long RESPAWN_DELAY = 3000; // ms
    
    public Player(float x, float y, float radius) {
        hitbox = new Circle(x, y, radius);
        velocity = new Vector2(0, 0);
    }
    
    
    public void update(float delta, float screenWidth, float screenHeight) {
        hitbox.x += velocity.x * delta;
        hitbox.y += velocity.y * delta;
        if (hitbox.x - hitbox.radius < 0) {
            hitbox.x = hitbox.radius;
        }
        if (hitbox.x + hitbox.radius > screenWidth) {
            hitbox.x = screenWidth - hitbox.radius;
        }
        if (hitbox.y - hitbox.radius < 0) {
            hitbox.y = hitbox.radius;
        }
        if (hitbox.y + hitbox.radius > screenHeight) {
            hitbox.y = screenHeight - hitbox.radius;
        }
    }
    
    public void takeDamage(float damage) {
        if (!alive) return;
        
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
            respawnTime = TimeUtils.millis() + RESPAWN_DELAY;
        }
    }
    
    public boolean shouldRespawn() {
        return !alive && TimeUtils.millis() > respawnTime;
    }
    
    public void respawn(float screenWidth, float screenHeight) {
        alive = true;
        health = 100f;
        hitbox.x = 100 + (float)Math.random() * (screenWidth - 200);
        hitbox.y = 100 + (float)Math.random() * (screenHeight - 200);
    }
    
    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
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
    
    public float getRadius() {
        return hitbox.radius;
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
}