package com.mygdx.shooter2d.multiplayer;

import com.esotericsoftware.kryonet.Client;
import java.io.IOException;

public class GameClient {
    private Client client;

    public GameClient() {
        client = new Client();
        Network.register(client.getKryo());
        client.addListener(new ClientListener());
        client.start();

        try {
            client.connect(5000, "localhost", Network.port);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerUpdate(float x, float y) {
        Network.PlayerUpdate update = new Network.PlayerUpdate();
        update.x = x;
        update.y = y;
        client.sendUDP(update);
    }
}
