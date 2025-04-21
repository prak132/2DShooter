package io.github.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import io.github.shooter.Main;

public class MenuScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;

    // TODO: Make everything dynamic so it can adapt to different screen sizes

    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to 2D Shooter", 240, 400);
        game.font.draw(game.batch, "S - Single Player", 240, 300);
        game.font.draw(game.batch, "M - Host Multiplayer", 240, 250);
        game.font.draw(game.batch, "J - Join Multiplayer", 240, 200);
        
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            game.font.draw(game.batch, "Your IP: " + localHost.getHostAddress(), 240, 150);
        } catch (Exception e) {
            game.font.draw(game.batch, "Could not determine your IP", 240, 150);
        }
        
        game.batch.end();
        
        if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            game.startGame(false, false, null);
        }
        
        if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            game.startGame(true, true, "localhost");
        }
        
        if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.J)) {
            game.setScreen(new IPInputScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}