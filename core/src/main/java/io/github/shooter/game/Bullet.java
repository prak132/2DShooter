package io.github.shooter.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Bullet {

    private final Vector2 position;
    private final Vector2 velocity;

    private float radius  = 5f;
    private float speed   = 400f;
    private final long creationTime;
    private final int  ownerId;

    private float damage    = 25f;   // default dmg
    private long  lifetime  = 1000;  // ms

    private boolean stopped = false; // new

    public Bullet(float x, float y, float dirX, float dirY, int ownerId) {
        position = new Vector2(x, y);
        // if caller already provided a velocity vector (dx,dy)
        if (Math.abs(dirX) > 10 || Math.abs(dirY) > 10) {
            velocity = new Vector2(dirX, dirY);
            speed = velocity.len();
        } else {
            velocity = new Vector2(dirX, dirY).nor().scl(speed);
        }
        creationTime = TimeUtils.millis();
        this.ownerId = ownerId;
    }

    /* ----------------------------------------------------------- */
    public void update(float delta) {
        if (stopped) return;                       // no movement if stopped
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }

    /** Stop the bullet in place (zero velocity). */
    public void stop() {
        velocity.setZero();
        stopped = true;
    }

    /* ----------------------------------------------------------- */
    public boolean isOutOfBounds(float w, float h) {
        return position.x < -radius || position.x > w + radius ||
               position.y < -radius || position.y > h + radius;
    }
    public boolean isExpired() {
        return TimeUtils.timeSinceMillis(creationTime) > lifetime;
    }

    /* getters / setters ----------------------------------------- */
    public void   setDamage(float d)     { damage = d; }
    public float  getDamage()            { return damage; }
    public void   setLifetime(long ms)   { lifetime = ms; }
    public void   setRadius(float r)     { radius = r;  }
    public Vector2 getPosition()         { return position; }
    public float  getX()                 { return position.x; }
    public float  getY()                 { return position.y; }
    public float  getVelX()              { return velocity.x; }
    public float  getVelY()              { return velocity.y; }
    public float  getRadius()            { return radius;  }
    public int    getOwnerId()           { return ownerId; }
    public boolean isStopped()           { return stopped; }
}
