package io.github.shooter.multiplayer;

import com.esotericsoftware.kryo.Kryo;

/**
 * Holds all network message types and helper method to register them.
 */
public class Network {

    public static final int port = 54555;

    /**
     * Info about a player sent to update their position, health, etc.
     */
    public static class PlayerUpdate {

        public int id;
        public float x, y;
        public boolean alive = true;
        public float health = 100f;
        public float rotation = 0f;
        public String username = "Player";
        public int kills = 0;
    }

    /**
     * Info about a bullet fired, including position and direction.
     */
    public static class BulletUpdate {

        public int playerId;
        public float x, y;
        public float dirX, dirY;
        public float damage = 25f;
    }

    /**
     * Sent when a player gets hit, with damage info.
     */
    public static class PlayerHit {

        public int targetId;
        public int sourceId;
        public float damage = 25f;
        public boolean fatal = false;
    }

    /**
     * Sent by client to check ping latency.
     */
    public static class PingRequest {

        public long timestamp;
    }

    /**
     * Sent by server back to client to reply to ping.
     */
    public static class PingResponse {

        public long timestamp;
    }

    /**
     * Sent when a player disconnects from server.
     */
    public static class PlayerDisconnected {

        public int id;
    }

    /**
     * Shows who killed who, used for kill feed display.
     */
    public static class KillFeed {

        public int killerId;
        public int victimId;
        public String killerName;
        public String victimName;
    }

    /**
     * Registers all network classes to Kryo serializer. Must be called on both
     * server and client before use.
     *
     * @param kryo Kryo instance to register classes with
     */
    public static void register(Kryo kryo) {
        kryo.register(PlayerUpdate.class);
        kryo.register(BulletUpdate.class);
        kryo.register(PlayerHit.class);
        kryo.register(PingRequest.class);
        kryo.register(PingResponse.class);
        kryo.register(PlayerDisconnected.class);
        kryo.register(KillFeed.class);
    }
}
