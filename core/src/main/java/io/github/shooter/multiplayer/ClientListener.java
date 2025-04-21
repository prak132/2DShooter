package com.mygdx.shooter2d.multiplayer;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;

public class ClientListener extends Listener {
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Network.PlayerUpdate) {
            Network.PlayerUpdate update = (Network.PlayerUpdate) object;
            System.out.println("Other player moved to: " + update.x + ", " + update.y);
        }
    }
}
