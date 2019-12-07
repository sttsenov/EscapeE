package uk.ac.bradford.escapegame;

/**
 * The Fuel class extends the Entity class, and is used to track and use the
 * position of the Fuel in the game.
 * @author prtrundl
 */
public class Fuel extends Entity {
   
    /**
     * Creates a Fuel object with specified position on the game board.
     * @param x Starting X position for the fuel.
     * @param y Starting Y position for the fuel.
     */
    public Fuel(int x, int y) {
        setPosition(x, y);
    }
    
}