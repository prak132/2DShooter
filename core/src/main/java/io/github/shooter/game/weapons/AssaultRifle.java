package io.github.shooter.game.weapons;

import com.badlogic.gdx.graphics.Color;

/**
 * Represents an assault rifle weapon.
 */
public class AssaultRifle extends Gun {

    /**
     * Creates a new assault rifle with its properties.
     */
    public AssaultRifle() {
        super(
                "Assault Rifle",
                6.0f,
                8.0f,
                30,
                2.0f,
                300.0f,
                3.0f
        );

        this.gunColor = Color.GRAY;
        this.gunLength = 1.5f;
        this.gunMaxLength = 1.5f;
        this.gunThickness = 0.4f;
    }
}
