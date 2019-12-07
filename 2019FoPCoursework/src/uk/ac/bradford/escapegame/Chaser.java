package uk.ac.bradford.escapegame;

/**
 * The Chaser class extends the Entity class, and is used to track and use the
 * position of Chasers in the game. Does not define its own attributes. This
 * class exists to enforce a logical difference between players and monsters,
 * helping ensure that they are not used interchangeably.
 * @author prtrundl
 */
public class Chaser extends Entity {
   
    /**
     * Creates a Chaser object with specified position on the game board.
     * @param x Starting X position for the chaser.
     * @param y Starting Y position for the chaser.
     */
    public Chaser(int x, int y) {
        setPosition(x, y);
    }
    
}
