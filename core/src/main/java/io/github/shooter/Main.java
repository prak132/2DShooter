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

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        setScreen(new MenuScreen(this));
    }
    
    public void startGame(boolean multiplayer, boolean hostServer) {
        if (hostServer) {
            try {
                server = new GameServer();
            } catch (IOException e) {
                System.err.println("Error staring server: " + e.getMessage());
            }
        }
        
        setScreen(new GameScreen(this, multiplayer));
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