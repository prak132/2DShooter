package io.github.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.shooter.Main;

public class IPInputScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private TextField ipField;
    
    public IPInputScreen(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        
        skin = new Skin();
        skin.add("default-font", game.font);
        
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.font;
        skin.add("default", labelStyle);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
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
        
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = game.font;
        textFieldStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        textFieldStyle.cursor = buttonStyle.up;
        skin.add("default", textFieldStyle);
        
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);
        
        Label titleLabel = new Label("Enter Server IP Address", skin);
        titleLabel.setAlignment(Align.center);
        
        // default ip addres for testing at home
        ipField = new TextField("127.0.2.2", skin);
        ipField.setAlignment(Align.center);
        
        TextButton connectButton = new TextButton("Connect", skin);
        TextButton backButton = new TextButton("Back", skin);
        
        float pad = 20f;
        root.add(titleLabel).padBottom(pad).row();
        root.add(ipField).width(300).padBottom(pad).row();
        
        Table buttonTable = new Table();
        buttonTable.add(connectButton).width(140).padRight(pad);
        buttonTable.add(backButton).width(140);
        
        root.add(buttonTable).padTop(pad);
        
        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                connect();
            }
        });
        
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        
        stage.setKeyboardFocus(ipField);
    }
    
    private void connect() {
        String ip = ipField.getText().trim();
        if (!ip.isEmpty()) {
            game.startGame(true, false, ip);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // backups
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            connect();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
        
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}