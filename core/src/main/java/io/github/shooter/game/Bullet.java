package io.github.shooter.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

// imagine having to java doc this

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private float radius = 5f;
    private float speed = 400f;
    private long creationTime;
    private int ownerId;
    
    public Bullet(float x, float y, float dirX, float dirY, int ownerId) {
        position = new Vector2(x, y);
        velocity = new Vector2(dirX, dirY).nor().scl(speed);
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
        return TimeUtils.timeSinceMillis(creationTime) > 1000;
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