package io.github.shooter.multiplayer;

import com.esotericsoftware.kryo.Kryo;

public class Network {
    public static final int port = 54555;

    public static class PlayerUpdate {
        public int id;
        public float x, y;
        public boolean alive = true;
        public float health = 100f;
        public float rotation = 0f;
    }
    
    public static class BulletUpdate {
        public int playerId;
        public float x, y;
        public float dirX, dirY;
        public float damage = 25f;
    }
    
    public static class PlayerHit {
        public int targetId;
        public int sourceId;
        public float damage = 25f;
    }
    
    public static class PingRequest {
        public long timestamp;
    }
    
    public static class PingResponse {
        public long timestamp;
    }
    
    public static class PlayerDisconnected {
        public int id;
    }

    public static void register(Kryo kryo) {
        kryo.register(PlayerUpdate.class);
        kryo.register(BulletUpdate.class);
        kryo.register(PlayerHit.class);
        kryo.register(PingRequest.class);
        kryo.register(PingResponse.class);
        kryo.register(PlayerDisconnected.class);
    }
}
