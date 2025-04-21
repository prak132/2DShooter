package io.github.shooter.multiplayer;

import com.esotericsoftware.kryo.Kryo;

public class Network {
    public static final int port = 54555;

    public static class PlayerUpdate {
        public int id;
        public float x, y;
    }

    public static void register(Kryo kryo) {
        kryo.register(PlayerUpdate.class);
    }
}
