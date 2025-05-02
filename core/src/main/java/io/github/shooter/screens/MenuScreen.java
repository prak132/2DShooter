package io.github.shooter.screens;

import java.net.InetAddress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.shooter.Main;


public class MenuScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;

    public MenuScreen(Main game) {
        this.game = game;
        
        //screenviewport auto scales
        stage = new Stage(new ScreenViewport());

        //skin setup
        skin = new Skin();
        skin.add("default-font", game.font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.font;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        // blank background, can replace w/ skin when we add

        buttonStyle.up = buttonStyle.over = buttonStyle.down = new Drawable() {
            @Override public float getLeftWidth() { return 0; }
            @Override public void setLeftWidth(float width) {}
            @Override public float getRightWidth() { return 0; }
            @Override public void setRightWidth(float width) {}
            @Override public float getTopHeight() { return 0; }
            @Override public void setTopHeight(float height) {}
            @Override public float getBottomHeight() { return 0; }
            @Override public void setBottomHeight(float height) {}
            @Override public float getMinWidth() { return 0; }
            @Override public void setMinWidth(float minWidth) {}
            @Override public float getMinHeight() { return 0; }
            @Override public void setMinHeight(float minHeight) {}
            @Override public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {}
        };

        buttonStyle.font = game.font;
        skin.add("default", buttonStyle);

        // layout
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);

        Label title = new Label("Welcome to 2D Shooter", skin);
        title.setAlignment(Align.center);

        TextButton singlePlayerBtn = new TextButton("Single Player", skin);
        TextButton hostBtn         = new TextButton("Host Multiplayer", skin);
        TextButton joinBtn         = new TextButton("Join Multiplayer", skin);

        String ipText;
        try 
        {
            InetAddress localHost = InetAddress.getLocalHost();
            ipText = "Your IP: " + localHost.getHostAddress();
        } 
        catch (Exception e) 
        {
            ipText = "can't determine your IP";
        }
        Label ipLabel = new Label(ipText, skin);
        ipLabel.setAlignment(Align.center);

        // add rows w/ spacing
        float pad = 20f;
        root.add(title).padBottom(pad * 2).row();
        root.add(singlePlayerBtn).padBottom(pad).width(300).row();
        root.add(hostBtn).padBottom(pad).width(300).row();
        root.add(joinBtn).padBottom(pad * 2).width(300).row();
        root.add(ipLabel);

        // listeners - idk how these work, youtbe tutorial
        
        singlePlayerBtn.addListener(new ChangeListener() 
        {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) 
            {
                game.startGame(false, false, null);
            }
        });

        hostBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.startGame(true, true, "localhost");
            }
        });

        joinBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new IPInputScreen(game));
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // back up keyboard shortcuts
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) game.startGame(false, false, null);
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) game.startGame(true, true, "localhost");
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) game.setScreen(new IPInputScreen(game));

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    
    @Override 
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
