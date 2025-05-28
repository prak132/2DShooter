package io.github.shooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Represents the game map, including background and obstacles.
 * Loads collision data from a Tiled map to figure out where players can't walk.
 */
public class GameMap {
    
    private final Texture background;
    private final Array<Rectangle> obstacles;
    private final TiledMap map;
    
    /**
     * Loads the background and collision objects from the Tiled map file.
     * It grabs all collision shapes and puts their bounding rectangles into obstacles.
     */
    public GameMap() {
        background = new Texture("map.png");
        obstacles = new Array<>();
        
        map = new TmxMapLoader().load("Collisions.tmx");
        
        MapLayer collisionLayer = map.getLayers().get("Collisions");
        
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                MapProperties props = object.getProperties();
                boolean isCollidable = props.containsKey("collidable") ? (Boolean) props.get("collidable") : true;
                
                if (!isCollidable) continue;
                
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    obstacles.add(rect);
                }
                
                if (object instanceof PolygonMapObject) {
                    Polygon poly = ((PolygonMapObject) object).getPolygon();
                    Rectangle rect = poly.getBoundingRectangle();
                    obstacles.add(rect);
                }
            }
        }
    }

    /**
     * Draws the background texture on the screen.
     * 
     * @param batch The sprite batch used for drawing.
     */
    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0);
    }

    /**
     * Returns the list of obstacles on the map.
     * You can use these to check collisions with players or bullets.
     * 
     * @return Array of rectangular obstacles.
     */
    public Array<Rectangle> getObstacles() {
        return obstacles;
    }

    /**
     * Clean up resources when you're done with the map.
     */
    public void dispose() {
        background.dispose();
        map.dispose();
    }
}
