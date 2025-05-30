package io.github.shooter.multiplayer;

import java.io.IOException;

import com.esotericsoftware.kryonet.Server;

/**
 * This class handles starting and stopping the game server.
 */
public class GameServer {

    private Server server;

    /**
     * Creates and starts the server. Registers network message classes and
     * starts listening on the set port.
     *
     * @throws IOException if something goes wrong with binding the port
     */
    public GameServer() throws IOException {
        server = new Server();
        Network.register(server.getKryo());
        server.addListener(new ServerListener(server));
        server.bind(Network.port);
        server.start();
        System.out.println("Server started on port " + Network.port);
    }

    /**
     * Stops the server if it is running.
     */
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
