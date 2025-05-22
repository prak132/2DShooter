package io.github.shooter.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import io.github.shooter.multiplayer.Network.BulletUpdate;
import io.github.shooter.multiplayer.Network.PingResponse;
import io.github.shooter.multiplayer.Network.PlayerDisconnected;
import io.github.shooter.multiplayer.Network.PlayerHit;
import io.github.shooter.multiplayer.Network.PlayerUpdate;

public class ClientListener extends Listener {
    private GameClient gameClient;
    
    private BulletListener bulletListener;
    private PlayerHitListener playerHitListener;
    
    public ClientListener(GameClient gameClient) {
        this.gameClient = gameClient;
    }
    
    public void setBulletListener(BulletListener listener) {
        this.bulletListener = listener;
    }
    
    public void setPlayerHitListener(PlayerHitListener listener) {
        this.playerHitListener = listener;
    }
    
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PlayerUpdate) {
            PlayerUpdate update = (PlayerUpdate) object;
            gameClient.updateOtherPlayer(update.id, update.x, update.y, update.health, update.alive, update.rotation, update.username, update.kills);
        }
        else if (object instanceof BulletUpdate) {
            BulletUpdate update = (BulletUpdate) object;
            if (update.playerId != gameClient.getClientId() && bulletListener != null) {
                bulletListener.onBulletFired(update.playerId, update.x, update.y, update.dirX, update.dirY, update.damage);
            }
        }
        else if (object instanceof PlayerHit) {
            PlayerHit hit = (PlayerHit) object;
            if (hit.targetId == gameClient.getClientId() && playerHitListener != null) {
                playerHitListener.onPlayerHit(hit.sourceId, hit.damage);
            }
        }
        else if (object instanceof PingResponse) {
            PingResponse response = (PingResponse) object;
            gameClient.receivePingResponse(response.timestamp);
        }
        else if (object instanceof PlayerDisconnected) {
            PlayerDisconnected disconnected = (PlayerDisconnected) object;
            gameClient.removePlayer(disconnected.id);
        }
    }
    
    public interface BulletListener {
        void onBulletFired(int playerId, float x, float y, float dirX, float dirY, float damage);
    }
    
    public interface PlayerHitListener {
        void onPlayerHit(int sourceId, float damage);
    }
}
