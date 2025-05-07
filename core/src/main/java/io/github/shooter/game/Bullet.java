package io.github.shooter.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private float radius = 5f;
    private float speed = 400f;
    private long creationTime;
    private int ownerId;
    private float damage = 25f; // dmg
    private long lifetime = 1000; // ms
    
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
    
    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }
    
    public boolean isOutOfBounds(float screenWidth, float screenHeight) {
        return position.x < -radius || position.x > screenWidth + radius || 
               position.y < -radius || position.y > screenHeight + radius;
    }
    
    public boolean isExpired() {
        return TimeUtils.timeSinceMillis(creationTime) > lifetime;
    }
    
    public void setDamage(float damage) {
        this.damage = damage;
    }
    
    public float getDamage() {
        return damage;
    }
    
    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    public void setRadius(float radius) {
        this.radius = radius;
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
}