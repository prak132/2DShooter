package io.github.shooter.multiplayer;

import com.esotericsoftware.kryo.Kryo;

public class Network {
    public static final int port = 54555;

    public static class PlayerUpdate {
        public int id;
        public float x, y;
        public boolean alive = true;
        public float health = 100f;
    }
    
    public static class BulletUpdate {
        public int playerId;
        public float x, y;
        public float dirX, dirY;
    }
    
    public static class PlayerHit {
        public int targetId;
        public int sourceId;
        public float damage = 25f;
    }

    public static void register(Kryo kryo) {
        kryo.register(PlayerUpdate.class);
        kryo.register(BulletUpdate.class);
        kryo.register(PlayerHit.class);
    }
}
