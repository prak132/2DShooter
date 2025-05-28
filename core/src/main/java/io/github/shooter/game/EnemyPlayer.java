package io.github.shooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

/**
 * Represents an enemy player in the game.
 * Holds position, health, and status info and handles rendering.
 */
public class EnemyPlayer {
    private final Circle hitbox;
    private static Texture texture;
    private float rotationAngleDeg;
    private boolean alive;
    private float health;
    private boolean textureInitialized = false;
    private String username = "Player";
    private int kills = 0;

    /**
     * Creates a new enemy player at given position with specified radius.
     * Starts alive with full health.
     * 
     * @param x X coordinate for spawn position.
     * @param y Y coordinate for spawn position.
     * @param radius Radius of the hitbox.
     */
    public EnemyPlayer(float x, float y, float radius) {
        hitbox = new Circle(x, y, radius);
        alive = true;
        health = 100f;
        rotationAngleDeg = 0f;
    }
    
    /**
     * Loads the enemy player texture once if not loaded already.
     * Call this before rendering any enemy players to avoid glitches.
     */
    public void initializeTexture() {
        if (!textureInitialized) {
            if (texture == null) {
                texture = new Texture("EnemyPlayer.png");
                texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            }
            textureInitialized = true;
        }
    }

    /**
     * Updates position, health, and alive status.
     * 
     * @param x New X coordinate.
     * @param y New Y coordinate.
     * @param health Current health value.
     * @param alive Whether the enemy is alive.
     */
    public void update(float x, float y, float health, boolean alive) {
        hitbox.setPosition(x, y);
        this.health = health;
        this.alive = alive;
    }
    
    /**
     * Updates position, health, alive status, plus username and kills.
     * Useful for syncing more info in multiplayer.
     * 
     * @param x New X coordinate.
     * @param y New Y coordinate.
     * @param health Current health value.
     * @param alive Whether the enemy is alive.
     * @param username Enemy's username.
     * @param kills Number of kills.
     */
    public void update(float x, float y, float health, boolean alive, String username, int kills) {
        update(x, y, health, alive);
        this.username = username;
        this.kills = kills;
    }

    /**
     * Renders the enemy player sprite at its current position and rotation.
     * Does nothing if enemy is dead or texture isn't ready.
     * 
     * @param batch Sprite batch to draw with.
     */
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
    
    /**
     * Draws the enemy player's username above their sprite.
     * Skips if the enemy isn't alive.
     * 
     * @param batch Sprite batch for drawing text.
     * @param font BitmapFont to use for rendering the name.
     */
    public void renderUsername(SpriteBatch batch, BitmapFont font) {
        if (!alive) return;
        String text = username;
        float textWidth = font.getScaleX() * text.length() * 8;
        float x = hitbox.x - textWidth / 2;
        float y = hitbox.y + hitbox.radius * 2 + 15;
        font.draw(batch, text, x, y);
    }

    /** Sets the rotation angle for rendering. */
    public void setRotationAngleDeg(float angle) {
        this.rotationAngleDeg = angle;
    }
    
    /** Returns whether the enemy is alive. */
    public boolean isAlive() {
        return alive;
    }

    /** Returns current health. */
    public float getHealth() {
        return health;
    }
    
    /** Gets the username. */
    public String getUsername() {
        return username;
    }
    
    /** Sets a new username. */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /** Returns how many kills this enemy has. */
    public int getKills() {
        return kills;
    }
    
    /** Updates kills count. */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Cleans up the static texture resource when the game shuts down.
     * Call this to avoid memory leaks.
     */
    public static void disposeTexture() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}