package io.github.shooter.game.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Represents a generic gun in the game.
 */
public abstract class Gun {

    protected String name;
    protected float damage;
    protected float fireRate; // RPS
    protected int magazineSize;
    protected float reloadTime; // sec
    protected float bulletSpeed;
    protected float spread; // deg
    protected Color gunColor = Color.GRAY;
    protected float gunMaxLength = 1.5f;
    protected float gunLength = 1.5f;
    protected float gunThickness = 0.4f;

    protected int currentAmmo;
    protected boolean isReloading;
    protected long lastShotTime;
    protected long reloadStartTime;

    /**
     * Constructs a new Gun with the specified properties.
     *
     * @param name The name of the gun.
     * @param damage The damage dealt by each shot.
     * @param fireRate The rate of fire in rounds per second.
     * @param magazineSize The size of the magazine.
     * @param reloadTime The time taken to reload in seconds.
     * @param bulletSpeed The speed of the bullets in units per second.
     * @param spread The spread of the bullets in degrees.
     */
    public Gun(String name, float damage, float fireRate, int magazineSize,
            float reloadTime, float bulletSpeed, float spread) {
        this.name = name;
        this.damage = damage;
        this.fireRate = fireRate;
        this.magazineSize = magazineSize;
        this.reloadTime = reloadTime;
        this.bulletSpeed = bulletSpeed;
        this.spread = spread;

        this.currentAmmo = magazineSize;
        this.isReloading = false;
    }

    /**
     * Fires the gun.
     *
     * @return true if the gun was fired.
     */
    public boolean fire() {
        if (isReloading) {
            return false;
        }

        if (currentAmmo <= 0) {
            reload();
            return false;
        }

        long currentTime = TimeUtils.millis();
        long timeSinceLastShot = currentTime - lastShotTime;

        if (timeSinceLastShot < (1000 / fireRate)) {
            return false;
        }

        currentAmmo--;
        lastShotTime = currentTime;
        gunLength = gunMaxLength / 1.2f;
        return true;
    }

    /**
     * Reloads the gun.
     */
    public void reload() {
        if (!isReloading && currentAmmo < magazineSize) {
            isReloading = true;
            reloadStartTime = TimeUtils.millis();
        }
    }

    /**
     * Updates the gun state.
     */
    public void update() {
        if (isReloading) {
            long currentTime = TimeUtils.millis();
            if (currentTime - reloadStartTime > reloadTime * 1000) {
                currentAmmo = magazineSize;
                isReloading = false;
            }
        }
    }

    /**
     * Applies recoil to the gun.
     */
    public void gunRecoil() {
        gunLength += (gunMaxLength - gunLength) / 3.0f;
    }

    /**
     * Returns the color of the gun.
     * 
     * @return the color of the gun
     */
    public Color getColor() {
        return gunColor;
    }

    /**
     * Returns the length of the gun.
     * 
     * @return the length of the gun
     */
    public float getLength() {
        return gunLength;
    }

    /**
     * Returns the thickness of the gun.
     * 
     * @return the thickness of the gun
     */
    public float getThickness() {
        return gunThickness;
    }

    /**
     * Returns the bullet speed.
     * 
     * @return the bullet speed
     */
    public float getBulletSpeed() {
        return bulletSpeed;
    }

    /**
     * Returns the bullet spread.
     * 
     * @return the bullet spread
     */
    public float getSpread() {
        return spread;
    }

    /**
     * Returns the bullet damage.
     * 
     * @return the bullet damage
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Returns the bullet ammo.
     * 
     * @return the bullet ammo
     */
    public int getCurrentAmmo() {
        return currentAmmo;
    }

    /**
     * Returns the bullet magazine size.
     * 
     * @return the bullet magazine size
     */
    public int getMagazineSize() {
        return magazineSize;
    }

    /**
     * Returns if the gun is reloading.
     * 
     * @return true if the gun is reloading
     */
    public boolean isReloading() {
        return isReloading;
    }

    /**
     * Returns the gun's reload time.
     * 
     * @return the gun's reload time
     */
    public float getReloadTime() {
        return reloadTime;
    }

    /**
     * Returns the bullet reload time that is remaining.
     * 
     * @return the bullet reload time that is remaining
     */
    public float getReloadTimeRemaining() {
        if (!isReloading) {
            return 0;
        }
        long currentTime = TimeUtils.millis();
        float elapsed = (currentTime - reloadStartTime) / 1000f;
        return Math.max(0, reloadTime - elapsed);
    }

    /**
     * Returns the gun name.
     * 
     * @return the gun name
     */
    public String getName() {
        return name;
    }
}
