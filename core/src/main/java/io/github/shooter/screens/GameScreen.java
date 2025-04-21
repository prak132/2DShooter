package io.github.shooter.screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import io.github.shooter.Main;
import io.github.shooter.multiplayer.GameClient;

public class GameScreen implements Screen {
    Main game;
    OrthographicCamera camera;
    Vector3 touchPoint;

    private boolean multiplayer;
    private String serverAddress = "localhost";
    private GameClient client;

    public GameScreen(Main game, boolean multiplayer, String serverAddress) {
        this.game = game;
        this.multiplayer = multiplayer;
        if (serverAddress != null && !serverAddress.isEmpty()) {
            this.serverAddress = serverAddress;
        }    
        camera = new OrthographicCamera();
        // Make it dynamic to resizing and stuff
        camera.setToOrtho(false, 800, 600);
        touchPoint = new Vector3();
    }

    @Override
    public void show() {
        if (multiplayer) {
            try {
                client = new GameClient(serverAddress);
            } catch (IOException e) {
                System.err.println("Failed to connect to server: " + e.getMessage());
            }
        }
    }

    @Override 
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
    }

    @Override 
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}