package uk.ac.bradford.escapegame;

/**
 * The Seeker class extends the Entity class, and is used to track and use the
 * position of Seekers in the game. Does not define its own attributes. This
 * class exists to enforce a logical difference between players and monsters,
 * helping ensure that they are not used interchangeably.
 * @author prtrundl
 */
public class Seeker extends Entity {
   
    /**
     * Creates a Seeker object with specified position on the game board.
     * @param x Starting X position for the seeker.
     * @param y Starting Y position for the seeker.
     */
    public Seeker(int x, int y) {
        setPosition(x, y);
    }
    
}
