package io.github.shooter.multiplayer;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;

import io.github.shooter.multiplayer.Network.PlayerUpdate;

public class GameClient {
    private Client client;
    private int clientId;

    public GameClient(String serverAddress) throws IOException {
        System.out.println("Attempting to connect to: " + serverAddress);
        
        client = new Client();
        Network.register(client.getKryo());
        client.addListener(new ClientListener());
        client.start();
        
        try {
            // funny issue I found
            if (serverAddress.endsWith(".255")) {
                throw new IOException("Cannot connect to broadcast address: " + serverAddress);
            }
            
            client.connect(5000, serverAddress, Network.port);
            clientId = client.getID();
            System.out.println("Connected to server at " + serverAddress + " with ID: " + clientId);
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

    public void sendPlayerUpdate(float x, float y) {
        PlayerUpdate update = new PlayerUpdate();
        update.id = clientId;
        update.x = x;
        update.y = y;
        client.sendTCP(update);
    }
    
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}