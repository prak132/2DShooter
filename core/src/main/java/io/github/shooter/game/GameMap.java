package io.github.shooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameMap {

    private final Texture background;
    private final Array<Rectangle> obstacles;
    private final TiledMap map;

    public GameMap() {
        background = new Texture("map.png");
        obstacles = new Array<>();

        // Load the Tiled map
        map = new TmxMapLoader().load("Collisions.tmx");

        // Load collisions from "Collisions" layer
        MapLayer collisionLayer = map.getLayers().get("Collisions");

        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {

                // Check if object has collidable property or assume all are collidable
                MapProperties props = object.getProperties();
                boolean isCollidable = props.containsKey("collidable") ? (Boolean) props.get("collidable") : true;
                

                if (!isCollidable) continue; // skip if not collidable

                // Handle Rectangle objects
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    obstacles.add(rect);
                }

                // Handle Polygon objects (convert to bounding rectangle)
                if (object instanceof PolygonMapObject) {
                    Polygon poly = ((PolygonMapObject) object).getPolygon();
                    Rectangle rect = poly.getBoundingRectangle();
                    obstacles.add(rect);
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0);
    }

    public Array<Rectangle> getObstacles() {
        return obstacles;
    }

    public void dispose() {
        background.dispose();
        map.dispose();
    }
}
