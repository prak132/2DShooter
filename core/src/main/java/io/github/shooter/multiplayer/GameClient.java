package io.github.shooter.multiplayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Client;

import io.github.shooter.game.EnemyPlayer;
import io.github.shooter.multiplayer.Network.BulletUpdate;
import io.github.shooter.multiplayer.Network.PingRequest;
import io.github.shooter.multiplayer.Network.PlayerHit;
import io.github.shooter.multiplayer.Network.PlayerUpdate;

/**
 * Handles client-side connection to game server, sending and receiving
 * multiplayer data.
 */
public class GameClient {

    private Client client;
    private int clientId;

    private Map<Integer, PlayerData> otherPlayers = new HashMap<>();
    private static final float PLAYER_RADIUS = 16f;

    private long lastPingSent;
    private long currentPing = 0;
    private static final long PING_INTERVAL = 1000;

    /**
     * Connects to server at given address with messages shown by default. The
     * show messages parameter was really for a finished game where you don't
     * want the user to see the info in the console but in a more intuitive way.
     *
     * @param serverAddress IP or hostname of server to connect to
     * @throws IOException if connection fails
     */
    public GameClient(String serverAddress) throws IOException {
        this(serverAddress, true);
    }

    /**
     * Connects to server at given address.
     *
     * @param serverAddress IP or hostname of server to connect to
     * @param showMessages if true, prints connection info to console
     * @throws IOException if connection fails
     */
    public GameClient(String serverAddress, boolean showMessages) throws IOException {
        if (showMessages) {
            System.out.println("Attempting to connect to: " + serverAddress);
        }

        client = new Client();
        Network.register(client.getKryo());
        client.addListener(new ClientListener(this));
        client.start();

        try {
            String host = serverAddress;
            int port = Network.port;
            // ngrok for port forwarding
            if (host.contains(":")) {
                String[] parts = host.split(":");
                host = parts[0];
                try {
                    port = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port in URL, using default port " + Network.port);
                }
            }

            if (host.contains("/")) {
                host = host.split("/")[0];
            }

            // funny issue I found
            if (host.endsWith(".255")) {
                throw new IOException("Cannot connect to broadcast address: " + host);
            }

            if (showMessages) {
                System.out.println("Connecting to host: " + host + " on port: " + port);
            }

            client.connect(5000, host, port);
            clientId = client.getID();

            if (showMessages) {
                System.out.println("Connected to server at " + host + " with ID: " + clientId);
            }
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            if (client != null) {
                client.close();
            }
            throw e;
        }
    }

    /**
     * Returns this client's network ID.
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Returns the Kryonet client object.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sends player's position, health, rotation, username, and kills to server.
     */
    public void sendPlayerUpdate(float x, float y, float health, boolean alive, float rotation, String username, int kills) {
        PlayerUpdate update = new PlayerUpdate();
        update.id = clientId;
        update.x = x;
        update.y = y;
        update.health = health;
        update.alive = alive;
        update.rotation = rotation;
        update.username = username;
        update.kills = kills;
        client.sendTCP(update);
    }

    /**
     * Sends bullet shot info to server.
     */
    public void sendBulletShot(float x, float y, float dirX, float dirY, float damage) {
        BulletUpdate update = new BulletUpdate();
        update.playerId = clientId;
        update.x = x;
        update.y = y;
        update.dirX = dirX;
        update.dirY = dirY;
        update.damage = damage;
        client.sendTCP(update);
    }

    /**
     * Sends info about hitting another player. Fatal hits are when a player
     * dies.
     */
    public void sendPlayerHit(int targetId, float damage) {
        PlayerHit hit = new PlayerHit();
        hit.sourceId = clientId;
        hit.targetId = targetId;
        hit.damage = damage;
        PlayerData target = otherPlayers.get(targetId);
        if (target != null && target.alive && target.health <= damage) {
            hit.fatal = true;
        }

        client.sendTCP(hit);
    }

    /**
     * Updates or adds info about another player.
     */
    public void updateOtherPlayer(int playerId, float x, float y, float health, boolean alive, float rotation, String username, int kills) {
        if (playerId != clientId) {
            PlayerData data = otherPlayers.get(playerId);
            if (data == null) {
                data = new PlayerData(playerId, x, y);
                otherPlayers.put(playerId, data);
            } else {
                data.update(x, y, health, alive, rotation, username, kills);
            }
        }
    }

    /**
     * Initializes textures for all enemy players
     */
    public void initializeEnemyTextures() {
        for (PlayerData data : otherPlayers.values()) {
            data.enemyPlayer.initializeTexture();
        }
    }

    /**
     * Returns map of other players by their IDs
     */
    public Map<Integer, PlayerData> getOtherPlayers() {
        return otherPlayers;
    }

    /**
     * Removes player from map and logs
     */
    public void removePlayer(int playerId) {
        if (otherPlayers.containsKey(playerId)) {
            otherPlayers.remove(playerId);
            System.out.println("Player " + playerId + " has been removed from the game.");
        }
    }

    /**
     * Disposes all enemy player textures and clears the list
     */
    public void disposeAllEnemyPlayers() {
        EnemyPlayer.disposeTexture();
        otherPlayers.clear();
    }

    /**
     * Closes client connection and disposes enemy players
     */
    public void close() {
        disposeAllEnemyPlayers();
        if (client != null) {
            client.close();
        }
    }

    /**
     * Sends ping request every second to measure latency.
     */
    public void updatePing() {
        if (client != null && client.isConnected()) {
            long currentTime = TimeUtils.millis();
            if (currentTime - lastPingSent > PING_INTERVAL) {
                PingRequest request = new PingRequest();
                request.timestamp = currentTime;
                client.sendTCP(request);
                lastPingSent = currentTime;
            }
        }
    }

    /**
     * Calculates ping from timestamp received in response
     */
    public void receivePingResponse(long timestamp) {
        currentPing = TimeUtils.millis() - timestamp;
    }

    /**
     * Returns current ping in milliseconds
     */
    public long getPing() {
        return currentPing;
    }

    /**
     * Manually set current ping (rarely needed) I don't know why I added this
     * to be honest.
     */
    public void setCurrentPing(long currentPing) {
        this.currentPing = currentPing;
    }

    /**
     * Stores data and state about another player in the game.
     */
    public static class PlayerData {

        public int id;
        public float x, y;
        public float health = 100f;
        public boolean alive = true;
        public float rotation = 0f;
        public Circle hitbox;
        public EnemyPlayer enemyPlayer;

        /**
         * Creates new player data with position and default values.
         */
        public PlayerData(int id, float x, float y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.hitbox = new Circle(x, y, PLAYER_RADIUS);
            this.enemyPlayer = new EnemyPlayer(x, y, PLAYER_RADIUS);
        }

        /**
         * Updates position, health, alive state, rotation.
         */
        public void update(float x, float y, float health, boolean alive, float rotation) {
            this.x = x;
            this.y = y;
            this.health = health;
            this.alive = alive;
            this.rotation = rotation;
            this.hitbox.setPosition(x, y);
            this.enemyPlayer.update(x, y, health, alive);
            this.enemyPlayer.setRotationAngleDeg(rotation);
        }

        /**
         * Same as update, but also updates username and kill count.
         */
        public void update(float x, float y, float health, boolean alive, float rotation, String username, int kills) {
            update(x, y, health, alive, rotation);
            this.enemyPlayer.update(x, y, health, alive, username, kills);
        }
    }
}
