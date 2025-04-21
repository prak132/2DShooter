package io.github.shooter.multiplayer;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class ServerListener extends Listener {
    private Server server;

    public ServerListener(Server server) {
        this.server = server;
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Network.PlayerUpdate) {
            Network.PlayerUpdate update = (Network.PlayerUpdate) object;
            server.sendToAllExceptTCP(connection.getID(), update);
        }
    }
}
