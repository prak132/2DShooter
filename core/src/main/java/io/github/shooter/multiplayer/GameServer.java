package io.github.shooter.multiplayer;

import com.esotericsoftware.kryonet.Server;
import java.io.IOException;

public class GameServer {
    private Server server;

    public GameServer() throws IOException {
        server = new Server();
        Network.register(server.getKryo());
        server.addListener(new ServerListener(server));
        server.bind(Network.port);
        server.start();
        System.out.println("Server started on port " + Network.port);
    }
}
