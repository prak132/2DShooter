package io.github.shooter.screens;

import com.badlogic.gdx.Screen;

import io.github.shooter.Main;

public class GameScreen implements Screen {
    Main game;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override public void show() {}
    @Override public void render(float delta) {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
