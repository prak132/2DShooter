package io.github.shooter;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.shooter.multiplayer.GameServer;
import io.github.shooter.screens.GameScreen;
import io.github.shooter.screens.MenuScreen;

/**
 * The main game class that manages game-wide resources and screen transitions.
 */
public class Main extends Game {

    /**
     * The main game instance
     */
    public SpriteBatch batch;
    /**
     * The font used for rendering text in the game
     */
    public BitmapFont font;
    /**
     * The game server instance for multiplayer functionality
     */
    private GameServer server;
    /**
     * The player's username
     */
    private String username = "Player";

    /**
     * Initializes batch and font and shows the main menu screen.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        setScreen(new MenuScreen(this));
    }

    /**
     * Starts the game screen. If hosting, tries to start the server.
     *
     * @param multiplayer whether the game should run in multiplayer mode
     * @param hostServer true if this instance should act as the server host
     * @param serverAddress the address of the server to connect to
     */
    public void startGame(boolean multiplayer, boolean hostServer, String serverAddress) {
        if (hostServer) {
            try {
                server = new GameServer();
                System.out.println("Server started. Your local IP address is needed for friends to connect.");
                try {
                    java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
                    System.out.println("Your computer name: " + localHost.getHostName());
                    System.out.println("Your IP address: " + localHost.getHostAddress());
                } catch (Exception e) {
                    System.out.println("Could not determine IP address automatically.");
                }
            } catch (IOException e) {
                System.err.println("Error starting server: " + e.getMessage());
            }
        }

        setScreen(new GameScreen(this, multiplayer, serverAddress));
    }

    /**
     * Sets the player's username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username;
        }
    }

    /**
     * Gets the current player's username.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Called each frame to render the current screen.
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Called when the game closes.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }

        if (server != null) {
            server.stop();
        }
    }

    /**
     * Resets the game state while keeping players connected.
     * This is called when a player reaches 10 kills and players choose to play again.
     */
    public void resetGame() {
        Screen currentScreen = getScreen();
        if (currentScreen instanceof GameScreen) {
            GameScreen gameScreen = (GameScreen) currentScreen;
            gameScreen.resetGameState();
            setScreen(gameScreen);
        }
    }
}
