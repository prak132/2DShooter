package io.github.shooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class EnemyPlayer {
    private final Circle hitbox;
    private static Texture texture;
    private float rotationAngleDeg;
    private boolean alive;
    private float health;
    private boolean textureInitialized = false;

    public EnemyPlayer(float x, float y, float radius) {
        hitbox = new Circle(x, y, radius);
        alive = true;
        health = 100f;
        rotationAngleDeg = 0f;
    }
    
    public void initializeTexture() {
        if (!textureInitialized) {
            if (texture == null) {
                texture = new Texture("EnemyPlayer.png");
                texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            }
            textureInitialized = true;
        }
    }

    public void update(float x, float y, float health, boolean alive) {
        hitbox.setPosition(x, y);
        this.health = health;
        this.alive = alive;
    }

    public void render(SpriteBatch batch) {
        if (!alive || !textureInitialized) return;
        
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

    public void setRotationAngleDeg(float angle) {
        this.rotationAngleDeg = angle;
    }

    public float getX() {
        return hitbox.x;
    }

    public float getY() {
        return hitbox.y;
    }

    public Circle getHitbox() {
        return hitbox;
    }

    public boolean isAlive() {
        return alive;
    }

    public float getHealth() {
        return health;
    }

    public static void disposeTexture() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}