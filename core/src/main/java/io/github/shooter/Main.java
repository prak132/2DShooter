package io.github.shooter;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.shooter.multiplayer.GameServer;
import io.github.shooter.screens.GameScreen;
import io.github.shooter.screens.MenuScreen;

/**
 * The main game class that manages game-wide resources and screen transitions.
 * Sets up rendering tools and fonts, starts on the menu screen,
 * handles starting new games (single or multiplayer),
 * manages the server if hosting multiplayer.
 */
public class Main extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    private GameServer server;
    private String username = "Player";

    /**
     * Called once when the game starts.
     * Initializes batch and font and shows the main menu screen.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        setScreen(new MenuScreen(this));
    }

    /**
     * Starts the game screen.
     * If hosting, tries to start the server and prints IP info.
     *
     * @param multiplayer  whether the game should run in multiplayer mode
     * @param hostServer   true if this instance should act as the server host
     * @param serverAddress the address of the server to connect to (null if hosting)
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
     * Cleans up resources like the batch, font, active screen, and stops the server if running.
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
}
