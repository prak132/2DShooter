package io.github.shooter.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import io.github.shooter.multiplayer.Network.BulletUpdate;
import io.github.shooter.multiplayer.Network.PingRequest;
import io.github.shooter.multiplayer.Network.PingResponse;
import io.github.shooter.multiplayer.Network.PlayerDisconnected;
import io.github.shooter.multiplayer.Network.PlayerHit;
import io.github.shooter.multiplayer.Network.PlayerUpdate;

/**
 * Listens for messages from clients on the server side.
 * Handles different network packets and broadcasts them as needed.
 */
public class ServerListener extends Listener {
    private Server server;

    /**
     * Creates listener for the given server.
     * @param server the Kryonet server to send messages on
     */
    public ServerListener(Server server) {
        this.server = server;
    }

    /**
     * Called when server gets message from client.
     * Checks type and forwards to other clients as needed.
     * @param connection the client connection that sent this
     * @param object the message object received
     */
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
        else if (object instanceof PingRequest) {
            PingRequest request = (PingRequest) object;
            PingResponse response = new PingResponse();
            response.timestamp = request.timestamp;
            connection.sendTCP(response);
        }
    }
    
    /**
     * Called when client connects.
     * Just prints client ID for now.
     * @param connection the client that connected
     */
    @Override
    public void connected(Connection connection) {
        System.out.println("Client connected: " + connection.getID());
    }
    
    /**
     * Called when client disconnects.
     * Notifies others that this player left.
     * @param connection the client that disconnected
     */
    @Override
    public void disconnected(Connection connection) {
        System.out.println("Client disconnected: " + connection.getID());
        PlayerDisconnected disconnected = new PlayerDisconnected();
        disconnected.id = connection.getID();
        server.sendToAllExceptTCP(connection.getID(), disconnected);
    }
}