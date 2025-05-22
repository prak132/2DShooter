package io.github.shooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class EnemyPlayer {
    private final Circle hitbox;
    private static Texture texture;
    private float rotationAngleDeg;
    private boolean alive;
    private float health;
    private boolean textureInitialized = false;
    private String username = "Player";
    private int kills = 0;

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
    
    public void update(float x, float y, float health, boolean alive, String username, int kills) {
        update(x, y, health, alive);
        this.username = username;
        this.kills = kills;
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
    
    public void renderUsername(SpriteBatch batch, BitmapFont font) {
        if (!alive) return;
        String text = username;
        float textWidth = font.getScaleX() * text.length() * 8;
        float x = hitbox.x - textWidth / 2;
        float y = hitbox.y + hitbox.radius * 2 + 15;
        font.draw(batch, text, x, y);
    }

    public void setRotationAngleDeg(float angle) {
        this.rotationAngleDeg = angle;
    }
    
    public boolean isAlive() {
        return alive;
    }

    public float getHealth() {
        return health;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }

    public static void disposeTexture() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}