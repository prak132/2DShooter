package io.github.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.shooter.Main;

/**
 * Screen shown when a player wins.
 */
public class VictoryScreen implements Screen {

    /**
     * The main game instance
     */
    private final Main game;
    /**
     * The stage for rendering UI elements
     */
    private final Stage stage;
    /**
     * The skin used for UI styling
     */
    private final Skin skin;
    /**
     * The name of the winning player
     */
    private final String winnerName;
    /**
     * Whether the local player is the winner
     */
    private final boolean isWinner;
    
    /**
     * The game screen instance to return 
     */
    private final GameScreen gameScreen;

    // personal deboucner
    /**
     * Flag to prevent multiple clicks
     */
    private boolean playAgainClicked = false;

    /**
     * Creates a victory screen showing who won
     *
     * @param game The main game instance
     * @param winnerName The name of the winning player
     * @param isWinner Whether the local player is the winner
     * @param gameScreen The game screen instance to return to
     */
    public VictoryScreen(Main game, String winnerName, boolean isWinner, GameScreen gameScreen) {
        this.game = game;
        this.winnerName = winnerName;
        this.isWinner = isWinner;
        this.gameScreen = gameScreen;

        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        skin.add("default-font", game.font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.font;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = buttonStyle.over = buttonStyle.down = new Drawable() {
            @Override
            public float getLeftWidth() { return 0; }
            @Override
            public void setLeftWidth(float width) {}
            @Override
            public float getRightWidth() { return 0; }
            @Override
            public void setRightWidth(float width) {}
            @Override
            public float getTopHeight() { return 0; }
            @Override
            public void setTopHeight(float height) {}
            @Override
            public float getBottomHeight() { return 0; }
            @Override
            public void setBottomHeight(float height) {}
            @Override
            public float getMinWidth() { return 0; }
            @Override
            public void setMinWidth(float minWidth) {}
            @Override
            public float getMinHeight() { return 0; }
            @Override
            public void setMinHeight(float minHeight) {}
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {}
        };
        buttonStyle.font = game.font;
        skin.add("default", buttonStyle);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);

        Label titleLabel = new Label(isWinner ? "Victory!" : "Defeat!", skin);
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(2.0f);

        String message = isWinner ? "You Won!" : winnerName + " Won!";
        Label messageLabel = new Label(message, skin);
        messageLabel.setAlignment(Align.center);
        messageLabel.setFontScale(1.5f);

        TextButton playAgainButton = new TextButton("Play Again", skin);
        playAgainButton.getLabel().setFontScale(1.5f);
        
        TextButton exitButton = new TextButton("Exit Game", skin);
        exitButton.getLabel().setFontScale(1.5f);

        float pad = 30f;
        root.add(titleLabel).padBottom(pad).row();
        root.add(messageLabel).padBottom(pad).row();
        root.add(playAgainButton).width(250).height(60).padTop(pad).row();
        root.add(exitButton).width(250).height(60).padTop(pad);

        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!playAgainClicked) {
                    playAgainClicked = true;
                    // stop spam clicking / debouncing escque thing
                    Gdx.app.postRunnable(() -> {
                        try {
                            gameScreen.resetGameState();
                            game.setScreen(gameScreen);
                        } catch (Exception e) {
                            System.err.println("Error resetting game: " + e.getMessage());
                            playAgainClicked = false;
                        }
                    });
                }
            }
        });
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
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

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        playAgainClicked = false;
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}