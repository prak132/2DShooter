package io.github.shooter.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import io.github.shooter.multiplayer.Network.PlayerUpdate;

public class ClientListener extends Listener {
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PlayerUpdate) {
            PlayerUpdate update = (PlayerUpdate) object;
            System.out.println("Other player moved to: " + update.x + ", " + update.y);
        }
    }
}
