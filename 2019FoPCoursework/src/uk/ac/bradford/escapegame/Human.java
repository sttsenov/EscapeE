package uk.ac.bradford.escapegame;

/**
 * The Human class is used to track and use the position and health of the
 * player in the game.
 * @author prtrundl
 */
public class Human extends Entity {
    
    /**
     * The maximum health of the player
     */
    private int maxHealth;
    
    /**
     * The current health of the player
     */
    private int health;
    
    /**
     * Returns the maximum health value for the player.
     * @return an integer value equal to the maximum health for the player.
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Returns the current health value for the player.
     * @return an integer value equal to the current health for the player.
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Changes the players health by the specified amount.
     * @param amount the amount to change the players health by. Negative values
     * will reduce health, positive values will increase it.
     */
    public void changeHealth(int amount) {
        health += amount;
        if (health > maxHealth)
            health = maxHealth;
        if (health < 0)
            health = 0;
    }
           
    /**
     * Creates a player object with specified maximum health and
     * position on the game board.
     * @param h Maximum health for the player, also used as
     * the starting health.
     * @param x Starting X position for the player.
     * @param y Starting Y position for the player.
     */
    public Human(int h, int x, int y) {
        maxHealth = h;
        health = h;
        setPosition(x, y);
    }
    
}
