package io.github.shooter.game.weapons;

/**
 * Factory class for making the different guns.
 */
public class GunFactory {

    /**
     * Enum representing the different types of guns.
     */
    public enum GunType {
        ASSAULT_RIFLE,
        SNIPER_RIFLE,
        SUBMACHINE_GUN
    }

    /**
     * Creates a gun of the specified type.
     *
     * @param type the type of gun to create
     * @return an instance of the gun
     */
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
