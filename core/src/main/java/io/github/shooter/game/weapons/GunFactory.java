package io.github.shooter.game.weapons;

public class GunFactory {

    public enum GunType {
        ASSAULT_RIFLE,
        SNIPER_RIFLE,
        SUBMACHINE_GUN
    }

    public static Gun createGun(GunType type) {
        switch (type) {
            case ASSAULT_RIFLE:
                return new AssaultRifle();
            case SNIPER_RIFLE:
                return new SniperRifle();
            case SUBMACHINE_GUN:
                return new SubmachineGun();
            default:
                return new AssaultRifle();
        }
    }
}
