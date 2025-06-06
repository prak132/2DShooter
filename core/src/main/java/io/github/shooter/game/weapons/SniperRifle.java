package io.github.shooter.game.weapons;

import com.badlogic.gdx.graphics.Color;

/**
 * Represents a sniper rifle weapon.
 */
public class SniperRifle extends Gun {
   
    /**
     * Creates a new sniper rifle with its properties.
     */
    public SniperRifle() {
        super(
                "Sniper Rifle",
                20.0f,
                1.0f,
                5,
                3.0f,
                900.0f,
                0.5f
        );

        this.gunColor = Color.DARK_GRAY;
        this.gunLength = 2.2f;
        this.gunMaxLength = 2.2f;
        this.gunThickness = 0.3f;
    }
}
