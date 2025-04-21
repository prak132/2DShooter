package io.github.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import io.github.shooter.Main;

public class IPInputScreen implements Screen {
    Main game;
    OrthographicCamera camera;
    StringBuilder ipAddress = new StringBuilder("127.0.2.2");
    
    public IPInputScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Enter server IP address:", 300, 350);
        game.font.draw(game.batch, ipAddress.toString(), 300, 300);
        game.font.draw(game.batch, "Press ENTER when done", 300, 250);
        game.font.draw(game.batch, "Press ESCAPE to cancel", 300, 200);
        game.batch.end();
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.startGame(true, false, ipAddress.toString());
        }

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }

        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                ipAddress.append(i - Input.Keys.NUM_0);
            }
        }

        for (int i = Input.Keys.NUMPAD_0; i <= Input.Keys.NUMPAD_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                ipAddress.append(i - Input.Keys.NUMPAD_0);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD) || 
            Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_DOT)) {
            ipAddress.append(".");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && ipAddress.length() > 0) {
            ipAddress.deleteCharAt(ipAddress.length() - 1);
        }
    }

    @Override public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}