package io.github.shooter.game.weapons;

import com.badlogic.gdx.graphics.Color;

/**
 * Represents a submachine gun weapon.
 */
public class SubmachineGun extends Gun {

    /**
     * Creates a new submachine gun with it's properties.
     */
    public SubmachineGun() {
        super(
                "Submachine Gun",
                2.5f,
                10.0f,
                45,
                1.5f,
                400.0f,
                6.0f
        );

        this.gunColor = Color.LIGHT_GRAY;
        this.gunLength = 1.2f;
        this.gunMaxLength = 1.2f;
        this.gunThickness = 0.5f;
    }
}
