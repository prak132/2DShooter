package io.github.shooter.game;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.shooter.game.weapons.Gun;
import io.github.shooter.game.weapons.GunFactory;
import io.github.shooter.game.weapons.GunFactory.GunType;

/**
 * The Player class represents the player character in the game.
 * Handles movement, shooting, health, respawning, and rendering.
 * Pretty much your in-game avatar with guns and all.
 */
public class Player {

    private final Circle hitbox;
    private final Vector2 velocity;
    private final Texture texture;
    private final float maxSpeed = 125f;

    private float speed = 125f;
    private float health = 200f;
    private boolean alive = true;
    private long respawnTime = 0;
    private static final long RESPAWN_DELAY = 3000;
    private float rotationAngleDeg = 0f;
    private String username = "Player";
    private int kills = 0;

    private Array<Gun> guns;
    private int currentGunIndex;
    private boolean isFiring;

    private static final float MIN_SPAWN_DISTANCE = 300f;
    private static final int MAX_RESPAWN_ATTEMPTS = 20;

    /**
     * Creates a new player at the specified position with given size.
     * Also loads default texture and sets up guns.
     *
     * @param x initial x position
     * @param y initial y position
     * @param radius size of the player's hitbox
     */
    public Player(float x, float y, float radius) {
        hitbox = new Circle(x, y, radius);
        velocity = new Vector2();
        texture = new Texture("Player1.png");
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        guns = new Array<>();
        guns.add(GunFactory.createGun(GunType.ASSAULT_RIFLE));
        guns.add(GunFactory.createGun(GunType.SNIPER_RIFLE));
        guns.add(GunFactory.createGun(GunType.SUBMACHINE_GUN));
        currentGunIndex = 0;
        isFiring = false;
    }
    
    /**
     * Same as other constructor but lets you set the username.
     *
     * @param x initial x position
     * @param y initial y position
     * @param radius size of the player's hitbox
     * @param username player's display name
     */
    public Player(float x, float y, float radius, String username) {
        this(x, y, radius);
        this.username = username;
    }

    /**
     * Update player's position and gun state.
     * Also prevents the player from moving through obstacles or off screen.
     *
     * @param delta time since last update (seconds)
     * @param screenW screen width (for boundary checks)
     * @param screenH screen height (for boundary checks)
     * @param obstacles obstacles to collide with
     */
    public void update(float delta, float screenW, float screenH, Array<Rectangle> obstacles) {
        getCurrentGun().update();
        
        Vector2 move = new Vector2(velocity).scl(delta);
        
        hitbox.x += move.x;
        for (Rectangle r : obstacles) {
            if (Intersector.overlaps(hitbox, r)) {
                if (move.x > 0) {
                    hitbox.x = r.x - hitbox.radius;
                } else if (move.x < 0) {
                    hitbox.x = r.x + r.width + hitbox.radius;
                }
                break;
            }
        }
        
        hitbox.y += move.y;
        for (Rectangle r : obstacles) {
            if (Intersector.overlaps(hitbox, r)) {
                if (move.y > 0) {
                    hitbox.y = r.y - hitbox.radius;
                } else if (move.y < 0) {
                    hitbox.y = r.y + r.height + hitbox.radius;
                }
                break;
            }
        }

        if (hitbox.x - hitbox.radius < 0) hitbox.x = hitbox.radius;
        if (hitbox.x + hitbox.radius > screenW) hitbox.x = screenW - hitbox.radius;
        if (hitbox.y - hitbox.radius < 0) hitbox.y = hitbox.radius;
        if (hitbox.y + hitbox.radius > screenH) hitbox.y = screenH - hitbox.radius;
    }

    /**
     * Checks keyboard input for switching guns or reloading.
     */
    public void handleGunInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) switchToGun(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) switchToGun(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) switchToGun(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) switchToGun((currentGunIndex + 1) % guns.size);
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) switchToGun((currentGunIndex - 1 + guns.size) % guns.size);
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            getCurrentGun().reload();
            speed = maxSpeed;
        }
    }

    /**
     * Tries to fire the current gun if the player is alive.
     *
     * @return true if the gun fired successfully, false otherwise
     */
    public boolean attemptToFire() {
        if (!alive) return false;
        return getCurrentGun().fire();
    }

    /**
     * Alias for attemptToFire.
     *
     * @param dirX x direction of fire
     * @param dirY y direction of fire
     * @return true if fired, false otherwise
     */
    public boolean fireAt(float dirX, float dirY) {
        if (!alive) return false;
        return attemptToFire();
    }

    /**
     * Switches to a different gun by index.
     *
     * @param index index of the gun to switch to
     */
    public void switchToGun(int index) {
        if (index >= 0 && index < guns.size) currentGunIndex = index;
    }

    /**
     * Gets the gun the player is currently holding.
     *
     * @return the current Gun
     */
    public Gun getCurrentGun() {
        return guns.get(currentGunIndex);
    }

    /**
     * Draws the player sprite on the screen, rotated to face the right way.
     *
     * @param batch the SpriteBatch to draw with
     */
    public void render(SpriteBatch batch) {
        if (!alive) return;
        
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
     * Renders some info about the current gun (like reload status).
     *
     * @param batch SpriteBatch to draw with
     * @param font BitmapFont used for drawing text
     * @param x X coordinate to start drawing at
     * @param y Y coordinate to start drawing at
     */
    public void renderGunInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        Gun gun = getCurrentGun();
        String statusText = "";
        if (gun.isReloading()) {
            statusText += " Reloading: " + String.format("%.1f", gun.getReloadTimeRemaining()) + "s";
        }
        font.getData().setScale(2.5f);
        font.draw(batch, statusText, x, y);
    }

    /**
     * Apply damage to the player. If health hits zero, they die.
     *
     * @param dmg amount of damage to apply
     */
    public void takeDamage(float dmg) {
        if (!alive) return;
        if ((health -= dmg) <= 0) {
            health = 0;
            alive = false;
            respawnTime = TimeUtils.millis() + RESPAWN_DELAY;
        }
    }

    /**
     * Respawns the player at a safe location away from other players.
     *
     * @param w width of the playable area
     * @param h height of the playable area
     * @param otherPlayers map of other players to avoid when spawning
     */
    public void respawn(float w, float h, Map<Integer, ?> otherPlayers) {
        alive = true;
        health = 200f;
        
        if (otherPlayers == null || otherPlayers.isEmpty()) {
            hitbox.x = 100 + (float) Math.random() * (w - 200);
            hitbox.y = 100 + (float) Math.random() * (h - 200);
            return;
        }
        
        for (int attempt = 0; attempt < MAX_RESPAWN_ATTEMPTS; attempt++) {
            float x = 100 + (float) Math.random() * (w - 200);
            float y = 100 + (float) Math.random() * (h - 200);
            boolean positionIsGood = true;
            for (Object playerObj : otherPlayers.values()) {
                if (playerObj instanceof Circle) {
                    Circle otherHitbox = (Circle) playerObj;
                    float distance = Vector2.dst(x, y, otherHitbox.x, otherHitbox.y);
                    if (distance < MIN_SPAWN_DISTANCE) {
                        positionIsGood = false;
                        break;
                    }
                }
            }
            
            if (positionIsGood) {
                hitbox.x = x;
                hitbox.y = y;
                return;
            }
        }
        
        hitbox.x = 100 + (float) Math.random() * (w - 200);
        hitbox.y = 100 + (float) Math.random() * (h - 200);
    }
    
    /**
     * Convenience method to respawn without worrying about other players.
     *
     * @param w width of playable area
     * @param h height of playable area
     */
    public void respawn(float w, float h) {
        respawn(w, h, null);
    }

    /**
     * Checks if it's time to respawn the player.
     *
     * @return true if the player is dead and the respawn delay has passed
     */
    public boolean shouldRespawn() {
        return !alive && TimeUtils.millis() > respawnTime;
    }

    /**
     * Updates the velocity vector to control movement.
     *
     * @param v new velocity vector
     */
    public void setVelocity(Vector2 v) {
        velocity.set(v);
    }

    /**
     * Returns the player's hitbox circle.
     *
     * @return the hitbox circle
     */
    public Circle getHitbox() {
        return hitbox;
    }

    /**
     * Gets the player's current X position.
     *
     * @return X coordinate
     */
    public float getX() {
        return hitbox.x;
    }

    /**
     * Gets the player's current Y position.
     *
     * @return Y coordinate
     */
    public float getY() {
        return hitbox.y;
    }

    /**
     * Returns how much health the player has left.
     *
     * @return current health
     */
    public float getHealth() {
        return health;
    }

    /**
     * Checks if the player is alive.
     *
     * @return true if alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * How long left until the player can respawn (ms).
     *
     * @return time in milliseconds
     */
    public long getTimeToRespawn() {
        return respawnTime - TimeUtils.millis();
    }

    /**
     * Marks if the player is firing or not.
     *
     * @param firing true if firing, false if not
     */
    public void setIsFiring(boolean firing) {
        this.isFiring = firing;
    }

    /**
     * Checks if the player is currently firing.
     *
     * @return true if firing
     */
    public boolean isFiring() {
        return isFiring;
    }

    /**
     * Clean up texture resources when done.
     */
    public void dispose() {
        texture.dispose();
    }

    /**
     * Sets the rotation angle the player is facing (in degrees).
     *
     * @param angle angle in degrees
     */
    public void setRotationAngleDeg(float angle) {
        this.rotationAngleDeg = angle;
    }

    /**
     * Gets the current rotation angle.
     *
     * @return angle in degrees
     */
    public float getRotationAngleDeg() {
        return rotationAngleDeg;
    }

    /**
     * Gets the current movement speed.
     *
     * @return speed value
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Changes the player's movement speed.
     *
     * @param spd new speed value
     */
    public void setSpeed(float spd) {
        this.speed = spd;
    }

    /**
     * Gets the player's username.
     *
     * @return the username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the player's username (if not empty).
     *
     * @param username new username string
     */
    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username;
        }
    }

    /**
     * Returns how many kills the player has.
     *
     * @return kill count
     */
    public int getKills() {
        return kills;
    }

    /**
     * Increments the kill count by one.
     */
    public void incrementKills() {
        this.kills++;
    }

    /**
     * Sets the kill count explicitly.
     *
     * @param kills new kill count
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Renders the player's username above their character.
     *
     * @param batch SpriteBatch to draw with
     * @param font BitmapFont to draw text
     */
    public void renderUsername(SpriteBatch batch, BitmapFont font) {
        if (!alive) return;

        String text = username;
        float textWidth = font.getScaleX() * text.length() * 8;
        float x = hitbox.x - textWidth / 2;
        float y = hitbox.y + hitbox.radius * 2 + 15;

        font.draw(batch, text, x, y);
    }
}
