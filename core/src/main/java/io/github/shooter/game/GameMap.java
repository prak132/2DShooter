package io.github.shooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameMap {

    private final Texture background;
    private final Array<Rectangle> obstacles;

    public GameMap() {
        background = new Texture("map.png");
        obstacles  = new Array<>();
        // annoying for testing
        /*
        // x,y,w,h
        obstacles.add(new Rectangle(100, 100, 90, 70));    // top‑left crate
        obstacles.add(new Rectangle(260, 200, 80, 80));    // tree
        obstacles.add(new Rectangle(550, 90, 120, 90));    // top‑right crate
        obstacles.add(new Rectangle(130, 380, 120, 80));   // middle crate
        obstacles.add(new Rectangle(290, 400, 100, 90));   // middle tree
        obstacles.add(new Rectangle(500, 420, 140, 90));   // right crate
        obstacles.add(new Rectangle(310, 660, 100, 100));  // bottom tree
        */
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0);
    }

    public Array<Rectangle> getObstacles() {
        return obstacles;
    }

    public void dispose() {
        background.dispose();
    }
}
