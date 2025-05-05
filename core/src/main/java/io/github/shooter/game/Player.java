package io.github.shooter.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Player {

    private final Circle  hitbox;
    private final Vector2 velocity;
    private final Texture texture;

    private float speed  = 200f;
    private float health = 100f;
    private boolean alive = true;
    private long respawnTime = 0;
    private static final long RESPAWN_DELAY = 3000;

    public Player(float x, float y, float radius) {
        hitbox   = new Circle(x, y, radius);
        velocity = new Vector2();
        texture  = new Texture("Player1.png");
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    public void update(float delta, float screenW, float screenH, Array<Rectangle> obstacles) {
        // Overriding whats already in handleInput() in GameScreen
        /*
        Vector2 input = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) input.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) input.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) input.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) input.x += 1;

        if (!input.isZero()) input.nor().scl(speed);
        velocity.set(input);
        */
        Vector2 move = new Vector2(velocity).scl(delta);
        Circle next  = new Circle(hitbox.x + move.x, hitbox.y + move.y, hitbox.radius);

        boolean block = false;
        for (Rectangle r : obstacles)
            if (Intersector.overlaps(next, r)) { block = true; break; }

        if (!block) {
            hitbox.x += move.x;
            hitbox.y += move.y;
        }

        if (hitbox.x - hitbox.radius < 0)            hitbox.x = hitbox.radius;
        if (hitbox.x + hitbox.radius > screenW)      hitbox.x = screenW - hitbox.radius;
        if (hitbox.y - hitbox.radius < 0)            hitbox.y = hitbox.radius;
        if (hitbox.y + hitbox.radius > screenH)      hitbox.y = screenH - hitbox.radius;
    }

    /** draw sprite scaled to hit‑circle size */
    public void render(SpriteBatch batch) {
        float size = hitbox.radius * 2f;
        batch.draw(texture, hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, size, size);
    }

    public void takeDamage(float dmg) {
        if (!alive) return;
        if ((health -= dmg) <= 0) {
            health = 0; alive = false;
            respawnTime = TimeUtils.millis() + RESPAWN_DELAY;
        }
    }
    public boolean shouldRespawn()        { return !alive && TimeUtils.millis() > respawnTime; }
    public void respawn(float w, float h) { alive = true; health = 100; hitbox.x = 100 + (float)Math.random()*(w-200); hitbox.y = 100 + (float)Math.random()*(h-200); }
    public void setVelocity(Vector2 v)    { velocity.set(v); }
    public Circle  getHitbox()            { return hitbox; }
    public float   getX()                 { return hitbox.x; }
    public float   getY()                 { return hitbox.y; }
    public float   getHealth()            { return health; }
    public boolean isAlive()              { return alive; }
    public long    getTimeToRespawn()     { return respawnTime - TimeUtils.millis(); }
    public void dispose()                 { texture.dispose(); }
}
