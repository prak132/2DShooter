package io.github.shooter.game.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.TimeUtils;

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
    
    public void reload() {
        if (!isReloading && currentAmmo < magazineSize) {
            isReloading = true;
            reloadStartTime = TimeUtils.millis();
        }
    }
    
    public void update() {
        if (isReloading) {
            long currentTime = TimeUtils.millis();
            if (currentTime - reloadStartTime > reloadTime * 1000) {
                currentAmmo = magazineSize;
                isReloading = false;
            }
        }
    }

    public void gunRecoil() {
        gunLength += (gunMaxLength - gunLength) / 3.0f;
    }
    
    public Color getColor() { return gunColor; }
    public float getLength() { return gunLength; }
    public float getThickness() { return gunThickness; }
    
    public float getBulletSpeed() {
        return bulletSpeed;
    }
    
    public float getSpread() {
        return spread;
    }
    
    public float getDamage() {
        return damage;
    }
    
    public int getCurrentAmmo() {
        return currentAmmo;
    }
    
    public int getMagazineSize() {
        return magazineSize;
    }
    
    public boolean isReloading() {
        return isReloading;
    }
    
    public float getReloadTime() {
        return reloadTime;
    }
    
    public float getReloadTimeRemaining() {
        if (!isReloading) {
            return 0;
        }
        long currentTime = TimeUtils.millis();
        float elapsed = (currentTime - reloadStartTime) / 1000f;
        return Math.max(0, reloadTime - elapsed);
    }
    
    public String getName() {
        return name;
    }
}