package io.github.shooter;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.shooter.multiplayer.GameServer;
import io.github.shooter.screens.GameScreen;
import io.github.shooter.screens.MenuScreen;

public class Main extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    private GameServer server;
    private String username = "Player";

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        setScreen(new MenuScreen(this));
    }

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
    
    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username;
        }
    }
    
    public String getUsername() {
        return username;
    }

    @Override
    public void render() {
        super.render();
    }

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
