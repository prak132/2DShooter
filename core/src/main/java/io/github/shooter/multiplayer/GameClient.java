package io.github.shooter.multiplayer;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;

import io.github.shooter.multiplayer.Network.PlayerUpdate;

public class GameClient {
    private Client client;

    public GameClient() throws IOException {
        client = new Client();
        Network.register(client.getKryo());
        client.addListener(new ClientListener());
        client.start();
        
        try {
            client.connect(5000, "localhost", Network.port);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void sendPlayerUpdate(float x, float y) {
        PlayerUpdate update = new PlayerUpdate();
        update.x = x;
        update.y = y;
        client.sendTCP(update);
    }
}