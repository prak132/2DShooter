package io.github.shooter.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import io.github.shooter.multiplayer.Network.BulletUpdate;
import io.github.shooter.multiplayer.Network.PlayerHit;
import io.github.shooter.multiplayer.Network.PlayerUpdate;

public class ServerListener extends Listener {
    private Server server;

    public ServerListener(Server server) {
        this.server = server;
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PlayerUpdate) {
            PlayerUpdate update = (PlayerUpdate) object;
            server.sendToAllExceptTCP(connection.getID(), update);
        }
        else if (object instanceof BulletUpdate) {
            BulletUpdate update = (BulletUpdate) object;
            server.sendToAllExceptTCP(connection.getID(), update);
        }
        else if (object instanceof PlayerHit) {
            PlayerHit hit = (PlayerHit) object;
            server.sendToAllTCP(hit);
        }
    }
    
    @Override
    public void connected(Connection connection) {
        System.out.println("Client connected: " + connection.getID());
    }
    
    @Override
    public void disconnected(Connection connection) {
        System.out.println("Client disconnected: " + connection.getID());
    }
}