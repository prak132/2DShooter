package io.github.shooter.game.weapons;

import com.badlogic.gdx.graphics.Color;

public class SubmachineGun extends Gun {
    
    public SubmachineGun() {
        super(
            "Submachine Gun",
            5.0f,
            15.0f,
            45,
            1.5f,
            500.0f,
            6.0f
        );
        
        this.gunColor = Color.LIGHT_GRAY;
        this.gunLength = 1.2f;
        this.gunMaxLength = 1.2f;
        this.gunThickness = 0.5f;
    }
}