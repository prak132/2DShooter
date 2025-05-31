package io.github.shooter.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import io.github.shooter.multiplayer.GameClient.PlayerData;
import io.github.shooter.multiplayer.Network.BulletUpdate;
import io.github.shooter.multiplayer.Network.PingResponse;
import io.github.shooter.multiplayer.Network.PlayerDisconnected;
import io.github.shooter.multiplayer.Network.PlayerHit;
import io.github.shooter.multiplayer.Network.PlayerUpdate;

/**
 * Listens for server messages and updates the GameClient accordingly. Handles
 * player updates, bullet events, player hits, ping responses, and player
 * disconnections.
 */
public class ClientListener extends Listener {

    /**
     * The GameClient instance that ts listener updates
     */
    private GameClient gameClient;

    /**
     * Listener for bullet fired events
     */
    private BulletListener bulletListener;
    /**
     * Listener for player hit events
     */
    private PlayerHitListener playerHitListener;

    /**
     * Creates a listener that will update the given GameClient.
     *
     * @param gameClient The game client to update on network events.
     */
    public ClientListener(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    /**
     * Sets the listener for bullet fired events.
     *
     * @param listener Listener called when bullets are fired by other players.
     */
    public void setBulletListener(BulletListener listener) {
        this.bulletListener = listener;
    }

    /**
     * Sets the listener for player hit events.
     *
     * @param listener Listener called when this client is hit by another
     * player.
     */
    public void setPlayerHitListener(PlayerHitListener listener) {
        this.playerHitListener = listener;
    }

    /**
     * Handles received network objects and updates game state accordingly.
     */
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PlayerUpdate) {
            PlayerUpdate update = (PlayerUpdate) object;
            gameClient.updateOtherPlayer(update.id, update.x, update.y, update.health, update.alive, update.rotation, update.username, update.kills);
        } else if (object instanceof BulletUpdate) {
            BulletUpdate update = (BulletUpdate) object;
            if (update.playerId != gameClient.getClientId() && bulletListener != null) {
                bulletListener.onBulletFired(update.playerId, update.x, update.y, update.dirX, update.dirY, update.damage);
            }
        } else if (object instanceof PlayerHit) {
            PlayerHit hit = (PlayerHit) object;
            if (hit.targetId == gameClient.getClientId() && playerHitListener != null) {
                playerHitListener.onPlayerHit(hit.sourceId, hit.damage);
            }

            if (hit.fatal && hit.sourceId != gameClient.getClientId()) {
                PlayerData killerData = gameClient.getOtherPlayers().get(hit.sourceId);
                if (killerData != null) {
                    if (hit.newKillCount > 0) {
                        killerData.enemyPlayer.setKills(hit.newKillCount);
                    } else {
                        killerData.enemyPlayer.setKills(killerData.enemyPlayer.getKills() + 1);
                    }
                }
            }
        } else if (object instanceof PingResponse) {
            PingResponse response = (PingResponse) object;
            gameClient.receivePingResponse(response.timestamp);
        } else if (object instanceof PlayerDisconnected) {
            PlayerDisconnected disconnected = (PlayerDisconnected) object;
            gameClient.removePlayer(disconnected.id);
        }
    }

    /**
     * Listener interface for receiving bullet fired events from other players.
     */
    public interface BulletListener {

        void onBulletFired(int playerId, float x, float y, float dirX, float dirY, float damage);
    }

    /**
     * Listener interface for receiving notifications when this player is hit.
     */
    public interface PlayerHitListener {

        void onPlayerHit(int sourceId, float damage);
    }
}
