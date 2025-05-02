package io.github.shooter.multiplayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Circle;
import com.esotericsoftware.kryonet.Client;

import io.github.shooter.multiplayer.Network.BulletUpdate;
import io.github.shooter.multiplayer.Network.PlayerHit;
import io.github.shooter.multiplayer.Network.PlayerUpdate;

public class GameClient {
    private Client client;
    private int clientId;
    
    private Map<Integer, PlayerData> otherPlayers = new HashMap<>();

    public GameClient(String serverAddress) throws IOException {
        this(serverAddress, true);
    }
    
    public GameClient(String serverAddress, boolean showMessages) throws IOException {
        if (showMessages) {
            System.out.println("Attempting to connect to: " + serverAddress);
        }
        
        client = new Client();
        Network.register(client.getKryo());
        client.addListener(new ClientListener(this));
        client.start();
        
        try {
            // funny issue I found
            if (serverAddress.endsWith(".255")) {
                throw new IOException("Cannot connect to broadcast address: " + serverAddress);
            }
            
            client.connect(5000, serverAddress, Network.port);
            clientId = client.getID();
            
            if (showMessages) {
                System.out.println("Connected to server at " + serverAddress + " with ID: " + clientId);
            }
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            if (client != null) {
                client.close();
            }
            throw e;
        }
    }

    public int getClientId() {
        return clientId;
    }
    
    public Client getClient() {
        return client;
    }

    public void sendPlayerUpdate(float x, float y, float health, boolean alive) {
        PlayerUpdate update = new PlayerUpdate();
        update.id = clientId;
        update.x = x;
        update.y = y;
        update.health = health;
        update.alive = alive;
        client.sendTCP(update);
    }
    
    public void sendBulletShot(float x, float y, float dirX, float dirY) {
        BulletUpdate update = new BulletUpdate();
        update.playerId = clientId;
        update.x = x;
        update.y = y;
        update.dirX = dirX;
        update.dirY = dirY;
        client.sendTCP(update);
    }
    
    public void sendPlayerHit(int targetId) {
        PlayerHit hit = new PlayerHit();
        hit.sourceId = clientId;
        hit.targetId = targetId;
        client.sendTCP(hit);
    }
    
    public void updateOtherPlayer(int playerId, float x, float y, float health, boolean alive) {
        if (playerId != clientId) {
            PlayerData data = otherPlayers.get(playerId);
            if (data == null) {
                data = new PlayerData(x, y);
                otherPlayers.put(playerId, data);
            } else {
                data.x = x;
                data.y = y;
                data.health = health;
                data.alive = alive;
            }
        }
    }
    
    public Map<Integer, PlayerData> getOtherPlayers() {
        return otherPlayers;
    }
    
    public void close() {
        if (client != null) {
            client.close();
        }
    }
    
    public static class PlayerData {
        public float x, y;
        public float health = 100f;
        public boolean alive = true;
        public Circle hitbox;
        
        public PlayerData(float x, float y) {
            this.x = x;
            this.y = y;
            this.hitbox = new Circle(x, y, 15f);
        }
        
        public void update(float x, float y) {
            this.x = x;
            this.y = y;
            this.hitbox.setPosition(x, y);
        }
    }
}