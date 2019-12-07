package uk.ac.bradford.escapegame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The GameEngine class is responsible for managing information about the game,
 * creating levels, the player, monsters, as well as updating information when a
 * key is pressed while the game is running.
 *
 * @author prtrundl
 */
public class GameEngine {

    /**
     * An enumeration type to represent different types of tiles that make up
     * the level. Each type has a corresponding image file that is used to draw
     * the right tile to the screen for each tile in a level. All types except
     * for Walls are open for player movement. Walls will block player (but not monster)
     * movement. Nests look like dirt but trigger the creation of a chaser when
     * the player is next to them, then turn to dirt tiles. The Car tile is the
     * entry and exit point for the level.
     */
    public enum TileType {
        CAR, DIRT, NEST, GRASS, ROAD, WALL
    }

    /**
     * The width of the level, measured in tiles. Changing this may cause the
     * display to draw incorrectly, and as a minimum the size of the GUI would
     * need to be adjusted.
     */
    public static final int GRID_WIDTH = 25;

    /**
     * The height of the level, measured in tiles. Changing this may cause the
     * display to draw incorrectly, and as a minimum the size of the GUI would
     * need to be adjusted.
     */
    public static final int GRID_HEIGHT = 18;

    /**
     * The chance of dirt being created instead of grass when generating the
     * level. 1.0 is 100% chance, 0.0 is 0% chance. This can be changed to
     * affect the difficulty.
     */
    private static final double DIRT_CHANCE = 0.3;

    /**
     * The chance of a nest being generated instead of dirt when generating the
     * level. 1.0 is 100% chance, 0.0 is 0% chance. This can be changed to
     * affect the difficulty.
     */
    private static final double NEST_CHANCE = 0.1;

    /**
     * A random number generator that can be used to include randomised choices
     * in the creation of levels, in choosing places to spawn the player, monsters
     * and fuel, and to randomise movement or other factors. It has a seed of 911
     * set to support debugging - a seeded generator will create the same values
     * each time the program is run to help recreate bugs. Remove the seed for a
     * random set of values every time.
     */
    private Random rng = new Random(911);

    /**
     * The number of levels cleared by the player in this game. Can be used to
     * generate harder games as the player clears levels.
     */
    private int cleared = 0;

    /**
     * Tracks the current turn number. Used to control monster movement.
     */
    private int turnNumber = 1;

    /**
     * The number of chasers added to the level - one is added every time the
     * player moves into a Nest tile. This is used to track the position in the
     * array of Chaser objects that new ones should be added. Whenever a Chaser
     * is created this value should in incremented by one to ensure Chasers are
     * not overwritten.
     */
    private int numChasers = 0;

    /**
     * The GUI associated with a GameEngine object. THis link allows the engine
     * to pass level (tiles) and entity information to the GUI to be drawn.
     */
    private GameGUI gui;

    /**
     * The 2 dimensional array of tiles the represent the current level. The
     * size of this array should use the GRID_HEIGHT and GRID_WIDTH attributes
     * when it is created.
     */
    private TileType[][] tiles;

    /**
     * An ArrayList of Point objects used to create and track possible locations
     * to spawn the player, monsters and items.
     */
    private ArrayList<Point> spawns;

    /**
     * A Human object that is the current player. This object stores the state
     * information for the player, including the current position (which is a
     * pair of co-ordinates that corresponds to a tile in the current level)
     */
    private Human player;


    /**
     * A Fuel object that must be collected by the player in order to move to
     * the next level. Once the player moves into the same tile as the Fuel
     * object it should be removed from the game (set to null) and the boolean
     * fuelCollected variable should be set to true.
     */
    private Fuel fuel;
    
    private Ghost ghost;
    
    private HealthPotion hp;

    /**
     * A boolean variable to track if the player has collected the Fuel on this
     * level yet. It is used to check if a new level should be generated when
     * the player stands on the Car tile.
     */
    private boolean fuelCollected = false;
    
    private boolean ghostCollected = false;
    
    private boolean hpCollected = false;

    /**
     * An array of Seeker objects that represents the seekers in the current
     * level. Elements in this array should be of the type Seeker, meaning that
     * a monster is alive and needs to be drawn or moved, or should be null
     * which means nothing is drawn or processed for movement. Null values in
     * this array are skipped during drawing and movement processing.
     */
    private Seeker[] seekers;

    /**
     * An array of Chaser objects that represents the chasers in the current
     * level. Elements in this array should be of the type Chaser, meaning that
     * a monster is alive and needs to be drawn or moved, or should be null
     * which means nothing is drawn or processed for movement. Null values in
     * this array are skipped during drawing and movement processing.
     */
    private Chaser[] chasers;

    /**
     * Constructor that creates a GameEngine object and connects it with a
     * GameGUI object.
     *
     * @param gui The GameGUI object that this engine will pass information to
     * in order to draw levels and entities to the screen.
     */
    public GameEngine(GameGUI gui) {
        this.gui = gui;

    }

    /**
     * Generates a new level. The method builds a 2D array of TileTypes that
     * will be used to draw tiles to the screen and to add a variety of elements
     * into each level. Tiles can be car, grass, dirt, nests, road or wall. This
     * method should contain the implementation of an algorithm to create an
     * interesting and varied level each time it is called.
     *
     * @return A 2D array of TileTypes representing the tiles in the current
     * level of the map. The size of this array should use the width and height
     * attributes of the level specified by GRID_WIDTH and GRID_HEIGHT.
     */
    private TileType[][] generateLevel() {
        
        Random rndX = new Random();
        Random rndY = new Random();
        Random random = new Random();
        Random specific = new Random();

        TileType[][] map = new TileType[25][18]; //x, width = 25 y, height = 18
        
        TileType[] ttp = TileType.values(); //an array of enums
        
        int rngX, rngY, roadCount = 0, wallCount = 0, weirdTile;
                
        rngX = rndX.nextInt(25); //0 to 25
        rngY = rndY.nextInt(9) + 9; //9 to 18
        weirdTile = specific.nextInt(3);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                //x gets a random enum value that is NOT a car
                TileType x = TileType.values()[random.nextInt(ttp.length - 1) + 1];

                // we assign a random enum to every element of the array

                if (i != rngX || j != rngY){
                    if(x.equals(TileType.ROAD)){
                        roadCount++;
                        if(roadCount > 0){
                            x = TileType.GRASS;
                        }
                    } else if(x.equals(TileType.WALL)){
                        wallCount++;
                        if(wallCount > 100){
                            switch(weirdTile){
                                case 0:
                                    x = TileType.GRASS;
                                    break;
                                case 1:
                                    x = TileType.DIRT;
                                    break;
                                case 2:
                                    x = TileType.NEST;
                                    break;
                                default:
                                    x = TileType.NEST;
                                    break;
                            }

                        }
                    }
                    map[i][j] = x;
                }
                map[rngX][rngY] = TileType.CAR;
            }
            
        }
        return map;
    }

    /**
     * Generates spawn points for entities. The method processes the tiles array
     * and finds tiles that are suitable for spawning, i.e. grass, dirt, road
     * and nest tiles. Suitable tiles should be added to the ArrayList that will
     * be returned as Point objects - Points are a simple kind of object that
     * contain an X and a Y co-ordinate stored using the int primitive type.
     *
     * @return An ArrayList containing Point objects representing suitable X and
     * Y co-ordinates in the current level that entities can be spawned in.
     */
    private ArrayList<Point> getSpawns() {

        ArrayList<Point> spawner;
        spawner = new ArrayList<Point>();
        
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] != TileType.CAR && tiles[i][j] != TileType.WALL && tiles[i][j] != TileType.NEST){
                    
                    Point p = new Point(); 
                    
                    p.x = i;
                    p.y = j;
                    spawner.add(p);
                }
            }
        }

        return spawner;
    }

    /**
     * Spawns seekers in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add seekers,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple entities spawning in the
     * same location. The method creates seekers by instantiating the Seeker
     * class, setting health and the X and Y position for the seeker using the
     * Point object removed from the spawns ArrayList.
     *
     * @return An array of Seeker objects representing the aliens for the
     * current level
     */

    private Seeker[] spawnSeekers() {
        Seeker[] enemy = new Seeker[5];
        Random rng = new Random();
        
        for (int i = 0; i < spawns.size(); i++) {
            Point p;
            int x = rng.nextInt(spawns.size());
            p = spawns.get(x);

            if(i < enemy.length){
               enemy[i] = new Seeker(p.x, p.y);  
            }
             
            spawns.remove(x);
        }

        return enemy;
    }
    

    
    /**
     * Spawns a Human entity in the game. The method instantiates the Human class and
     * assigns values for the health and position of the player. The players position
     * should be set to the tile representing the Car in the game, although you may
     * set the player's position to any location while testing and
     * debugging the spawning behaviour of this method.
     * @return A Human object representing the player in the game
     */
    private Human spawnPlayer() {
        int playerX = 0,playerY = 0;

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] == TileType.CAR){
                    playerX = i;
                    playerY = j;
                }
            }  
        }
        
        Human player = new Human(100, playerX, playerY);
        return player;            
    }
    
    
    /**
     * 
     * @return 
     */
    private Ghost spawnGhostItem() {
        Random rngesus = new Random();
        Point p;
            
        int x = rngesus.nextInt(spawns.size());
        p = spawns.get(x);
        spawns.remove(x);
        
        Ghost ghost = new Ghost(p.x, p.y);
        return ghost;
    }
    
    
    /**
     * Spawns a Fuel object in the game. The method uses the spawns ArrayList to
     * pick a suitable position for the Fuel in the game, removing the chosen position
     * from the ArrayList to avoid multiple entities being create din the same position.
     * The method works by instantiating the Fuel class and returning the Fuel object.
     * @return A Fuel object representing the fuel in the game
     */
    private Fuel spawnFuel() {
        Random rngesus = new Random();
        Point p;
            
        int x = rngesus.nextInt(spawns.size());
        p = spawns.get(x);
        spawns.remove(x);
        
        Fuel fuel = new Fuel(p.x, p.y);
        return fuel;
    }
    
    
    /**
     * Spawns a HealthPotion object in the game. The method uses the spawns ArrayList to
     * pick a suitable position for the HealthPotion in the game, removing the chosen position
     * from the ArrayList to avoid multiple entities being create din the same position.
     * The method works by instantiating the HealthPotion class and returning the HealthPotion object.
     * @return A HealthPotion object representing the fuel in the game
     */
    private HealthPotion spawnHP(){
        Random rngesus = new Random();
        Point p;
            
        int x = rngesus.nextInt(spawns.size());
        p = spawns.get(x);
        spawns.remove(x);
        
        HealthPotion hp = new HealthPotion(p.x, p.y);
        return hp;
    }

    /**
     * Handles the movement of the player when attempting to move left in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the left arrow key on the keyboard. The method checks whether the
     * tile to the left of the player is empty for movement and if it is updates
     * the player object's X and Y locations with the new position. If the tile
     * to the left of the player is not empty the method will not update the
     * player position, but could make other changes to the game.
     */
    public void movePlayerLeft() {
        
        if(player.getX() != 0){
            if(ghostCollected){
                player.setPosition(player.getX() -1, player.getY());
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }
                

                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                     
                    if(cleared >= 1){
                        object.stop();
                    }
                    newLevel();
                }
            } else if(!tiles[player.getX() - 1][player.getY()].equals(TileType.WALL) && ghostCollected == false){
                player.setPosition(player.getX() -1, player.getY());
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }
                
                if(ghost != null){
                    if(player.getX() == ghost.getX() && player.getY() == ghost.getY()){
                        ghostCollected = true;
                        ghost = null;

                    }
                }
                
                
                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                     
                    if(cleared >= 1){
                        object.stop();
                    }
                    newLevel();
                }
            }    
        }
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the right arrow key on the keyboard. The method checks whether
     * the tile to the right of the player is empty for movement and if it is
     * updates the player object's X and Y locations with the new position. If
     * the tile to the right of the player is not empty the method will not
     * update the player position, but could make other changes to the game.
     */
    public void movePlayerRight() {
        
        if(player.getX() != 24){
            if(ghostCollected){
                player.setPosition(player.getX() +1, player.getY());
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }

                
                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                     
                    if(cleared >= 1){
                        object.stop();
                    }
                    newLevel();
                }
                
            } else if(!tiles[player.getX() + 1][player.getY()].equals(TileType.WALL) && ghostCollected == false){
                player.setPosition(player.getX() +1, player.getY());
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }
      
                if(ghost != null){
                    if(player.getX() == ghost.getX() && player.getY() == ghost.getY()){
                        ghostCollected = true;
                        ghost = null;
 
                    }
                }
                
                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                     
                    if(cleared >= 1){
                        object.stop();
                    }
                    newLevel();
                }
                
            }
        }
      
    }

    /**
     * Handles the movement of the player when attempting to move up in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the up arrow key on the keyboard. The method checks whether the
     * tile above the player is empty for movement and if it is updates the
     * player object's X and Y locations with the new position. If the tile
     * above the player is not empty the method will not update the player
     * position, but could make other changes to the game.
     */
    public void movePlayerUp() {
        
        if(player.getY() != 0){
            if(ghostCollected){
                player.setPosition(player.getX(), player.getY() - 1);
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }

                
                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                     
                    if(cleared >= 1){
                        object.stop();
                    }
                    newLevel();
                }

            } else if(!tiles[player.getX()][player.getY() - 1].equals(TileType.WALL) && ghostCollected == false){
                player.setPosition(player.getX(), player.getY() - 1);
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }
                
                                
                if(ghost != null){
                    if(player.getX() == ghost.getX() && player.getY() == ghost.getY()){
                        ghostCollected = true;
                        ghost = null;  
                    }
                }
                
                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                     
                    if(cleared >= 1){
                        object.stop();
                    }
                    newLevel();
                }
                
            }    
        }
        
        
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the down arrow key on the keyboard. The method checks whether the
     * tile below the player is empty for movement and if it is updates the
     * player object's X and Y locations with the new position. If the tile
     * below the player is not empty the method will not update the player
     * position, but could make other changes to the game.
     */
    public void movePlayerDown() {

        if(player.getY() != 17){
            if(ghostCollected){
                player.setPosition(player.getX(), player.getY() + 1);
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }
                

                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                    
                    if(cleared >= 1){
                        object.stop();
                    }
                    
                    newLevel();
                }
                
            } else if(!tiles[player.getX()][player.getY() + 1].equals(TileType.WALL) && ghostCollected == false){
                player.setPosition(player.getX(), player.getY() + 1);
                
                if(fuel != null){
                    if(player.getX() == fuel.getX() && player.getY() == fuel.getY()){
                        fuelCollected = true;
                        fuel = null;
                    }
                }
                
                if(hp != null){
                    if(player.getX() == hp.getX() && player.getY() == hp.getY()){
                        hpCollected = true;
                        hp = null;
                        player.changeHealth(+20);
                    }
                }
                
                if(ghost != null){
                    if(player.getX() == ghost.getX() && player.getY() == ghost.getY()){
                        ghostCollected = true;
                        ghost = null;  
                    }
                }
                
                if(tiles[player.getX()][player.getY()].equals(TileType.CAR) && fuelCollected){
                    if(cleared >= 1){
                        object.stop();
                    }
                    newLevel();
                }
            }
        }
    }

    /**
     * Moves all seekers on the current level. The method checks for non-null
     * elements in the seekers array and calls the moveSeeker method for each one
     * that is not null.
     */
    private void moveSeekers() {

        for (int i = 0; i < seekers.length; i++) {
            moveSeeker(seekers[i]);
            if(player.getX() == seekers[i].getX() && player.getY() == seekers[i].getY()){
                player.changeHealth(-10);
                threadSound weirdNoice = new threadSound();
                weirdNoice.start();
            }
        }
       
    }


    /**
     * Moves a specific seeker in the game closer to the player. The method
     * updates the X and Y attributes of the seeker to reflect its new position.
     * The new position should be closer to the player, by finding the
     * difference between the X or Y co-ordinate of this seeker and the player,
     * determining if it is positive or negative and then moving up/down or
     * left/right to change the value towards zero. If a seeker attempts to move
     * into a tile containing the player, the player should have their health
     * reduced by some amount.
     *
     * @param a The Seeker that needs to be moved
     */

    private void moveSeeker(Seeker a) {
        boolean attack = false;
        if(a.getX() > player.getX() && !tiles[a.getX() - 1][a.getY()].equals(TileType.WALL)
                && checkEntitySeeker(a, -1, 0) && checkEntityChaser(a, -1, 0)){

            if(a.getX() - 1 == player.getX() && player.getY() == a.getY()){
                attack = true;
                
                threadSound weirdNoice = new threadSound();
                weirdNoice.start();
            } else {
                moveSeekerRight(a); 
            }

        } else if(a.getX() < player.getX() && !tiles[a.getX() + 1][a.getY()].equals(TileType.WALL)
                && checkEntitySeeker(a, 1, 0) && checkEntityChaser(a, 1, 0)){
            
            if(a.getX() + 1 == player.getX() && player.getY() == a.getY()){
                attack = true;
                
                threadSound weirdNoice = new threadSound();
                weirdNoice.start();
            } else {
                moveSeekerLeft(a); 
            }

        } else if(a.getY() > player.getY() && !tiles[a.getX()][a.getY() - 1].equals(TileType.WALL)
                && checkEntitySeeker(a, 0, -1) && checkEntityChaser(a, 0, -1)){
            
            if(a.getY() - 1 == player.getY() && player.getX() == a.getX()){
                attack = true;
                threadSound weirdNoice = new threadSound();
                //noise
                weirdNoice.start();
            } else {
                moveSeekerTop(a); 
            }

        } else if(a.getY() < player.getY() && !tiles[a.getX()][a.getY() + 1].equals(TileType.WALL)
                && checkEntitySeeker(a, 0, 1) && checkEntityChaser(a, 0, 1)){
                        
            if(a.getY() + 1 == player.getY() && player.getX() == a.getX()){
                attack = true;
                threadSound weirdNoice = new threadSound();
                weirdNoice.start();
            } else {
                moveSeekerDown(a);
            }
        }
        if(attack){
            player.changeHealth(-10);
        }
    }


    public void moveSeekerTop(Seeker a){
        a.setPosition(a.getX(), a.getY() - 1);
    }
    
    public void moveSeekerDown(Seeker a){
        a.setPosition(a.getX(), a.getY() + 1);
    }
    
    public void moveSeekerLeft(Seeker a){
        a.setPosition(a.getX() + 1, a.getY());
    }
    
    public void moveSeekerRight(Seeker a){
        a.setPosition(a.getX() - 1, a.getY());
    }
    
    
    /**
     * Checks if a Seeker is going to collide with another seeker object.
     * @param a entity object
     * @param x checks left and right
     * @param y checks up and down
     * @return a boolean that checks if a collision is going to happen
     */
    
    private boolean checkEntitySeeker(Entity a, int x, int y){
        boolean collision = true;

        for (int i = 0; i < seekers.length; i++) {
            if(seekers[i].getX() == a.getX() + x && seekers[i].getY() == a.getY() + y){
                collision = false;
                break;
            }
        }
        return collision;
    }
    
    /**
     * Checks if a Chaser is going to collide with another Chaser object.
     * @param a entity object
     * @param x checks left and right
     * @param y checks up and down
     * @return a boolean that checks if a collision is going to happen
     */
    private boolean checkEntityChaser(Entity a, int x, int y){
        boolean collision = true;

        for (int i = 0; i < chasers.length; i++) {
            if(chasers[i] != null){
                if(chasers[i].getX() == a.getX() + x && chasers[i].getY() == a.getY() + y){
                    collision = false;
                    break;
                }
            }    
        }
        return collision;
    }
    
    /**
     * Checks if the we are not spawning an entity object outside of the range of the map
     * @param x checks if x is not outside of the map's x
     * @param y checks if y is not outside of the map's y
     * @return true if we are not outside of the map and false if we are outside the map
     */
    private boolean checkDimensions(int x, int y){
        boolean answer, 
                checkBigger,
                checkLower;
        
        if((player.getX() + x < GRID_WIDTH) && (player.getY() + y < GRID_HEIGHT)){
            checkBigger = true;
        } else {
            checkBigger = false;
        }
        
        if((player.getX() + x > 0) && (player.getY() + y > 0)){
            checkLower = true;
        } else{
            checkLower = false;
        }
        
        if(checkBigger && checkLower){
            answer = true;
        } else {
            answer = false;
        }
        return answer;
    }
    
    /**
     * Goes through all the Chasers and makes sure you that not a single Chaser is being spawned into a wall
     * when the level changes. If the chaser is going to be spawned into a wall it checks for an available space
     * and moves there instead.
     * @param a Chaser
     * @return 
     */
    private boolean checkSpawnPlace(Chaser a){
        boolean answer = false;
        
        if(tiles[a.getX()][a.getY()].equals(TileType.WALL) || tiles[a.getX()][a.getY()].equals(TileType.CAR)){
            answer = true;
        }
        
        if(answer){
            
            if(!tiles[a.getX() + 1][a.getY()].equals(TileType.WALL) && !tiles[a.getX() + 1][a.getY()].equals(TileType.CAR)
                    && checkEntityChaser(a, 1, 0) && checkEntitySeeker(a, 1, 0)){
                a.setPosition(a.getX() + 1, a.getY());
                
            } else if (!tiles[a.getX() - 1][a.getY()].equals(TileType.WALL) && !tiles[a.getX() - 1][a.getY()].equals(TileType.CAR)
                    && checkEntityChaser(a, -1, 0) && checkEntitySeeker(a, -1, 0)){
                a.setPosition(a.getX() - 1, a.getY());
                
            } else if (!tiles[a.getX()][a.getY() + 1 ].equals(TileType.WALL) && !tiles[a.getX()][a.getY() + 1].equals(TileType.CAR)
                    && checkEntityChaser(a, 0, 1) && checkEntitySeeker(a, 0, 1)){
                a.setPosition(a.getX(), a.getY() + 1);
                
            } else if(!tiles[a.getX()][a.getY() - 1].equals(TileType.WALL) && !tiles[a.getX()][a.getY() - 1].equals(TileType.CAR)
                    && checkEntityChaser(a, 0, -1) && checkEntitySeeker(a, 0, -1)){
                a.setPosition(a.getX(), a.getY() - 1);
            } else {
                moveChaser(a);
            }
            
            answer = false;
        }
        
        return answer;
    }
    
    
    /**
     * Spawns a suitable number of Chaser objects that relies on the level 
     * and spawns it in a suitable position
     * @return altered chasers array
     */

    
    private Chaser[] spawnChaser() {
        if(tiles[player.getX()][player.getY()].equals(TileType.NEST) && numChasers < cleared && 
                player.getX() != 24 && player.getX() != 0 
                && player.getY() != 17 && player.getY() != 0){
            if(!tiles[player.getX()][player.getY() + 1].equals(TileType.WALL) && checkDimensions(0, 1)){ //spawns a Chaser below you
                chasers[numChasers] = new Chaser(player.getX(), player.getY() + 1);
                numChasers++;
            }else if(!tiles[player.getX()][player.getY() - 1].equals(TileType.WALL) && checkDimensions(0, -1)){ //spawns a Chaser above you
                chasers[numChasers] = new Chaser(player.getX(), player.getY() - 1);
                numChasers++;
            }else if(!tiles[player.getX() + 1][player.getY()].equals(TileType.WALL) && checkDimensions(1, 0)){ //spawns a Chaser to your right
                chasers[numChasers] = new Chaser(player.getX() + 1, player.getY());
                numChasers++;
            }else if(!tiles[player.getX() - 1][player.getY()].equals(TileType.WALL) && checkDimensions(-1, 0)){ //spawns a Chaser to your left
                chasers[numChasers] = new Chaser(player.getX() - 1, player.getY());
                numChasers++;
            }else if(!tiles[player.getX() + 1][player.getY() + 1].equals(TileType.WALL) && checkDimensions(1, 1)){ 
                chasers[numChasers] = new Chaser(player.getX() + 1, player.getY() + 1);
                numChasers++;
            } else if(!tiles[player.getX() + 1][player.getY() - 1].equals(TileType.WALL) && checkDimensions(1, -1)){
                chasers[numChasers] = new Chaser(player.getX() + 1, player.getY() - 1);
                numChasers++;
            } else if(!tiles[player.getX() - 1][player.getY() + 1].equals(TileType.WALL) && checkDimensions(-1, 1)){
                chasers[numChasers] = new Chaser(player.getX() - 1, player.getY() + 1);
                numChasers++;
            } else if(!tiles[player.getX() - 1][player.getY() - 1].equals(TileType.WALL) && checkDimensions(-1, -1)){
                chasers[numChasers] = new Chaser(player.getX() - 1, player.getY() - 1);
                numChasers++;
            }       
        }
        
        return chasers;
    }
    
    /**
     * Moves all chasers on the current level. The method checks for non-null
     * elements in the chasers array and calls the moveChaser method for each one
     * that is not null.
     */
    private void moveChasers() {
        for (int i = 0; i < spawnChaser().length; i++) {
            if(spawnChaser()[i] != null ){
                moveChaser(spawnChaser()[i]);
            }
        }
    }

    /**
     * Moves a specific chaser in the game closer to the player. The method
     * updates the X and Y attributes of the chaser to reflect its new position.
     * The new position should be closer to the player, by finding the
     * difference between the X or Y co-ordinate of this chaser and the player,
     * determining if it is positive or negative and then moving up/down or
     * left/right to decrease the value towards a distance of zero. If a chaser
     * attempts to move into a tile with the player, the players health should
     * be reduced by some amount instead.
     *
     * @param a The Chaser that needs to be moved
     */
    private void moveChaser(Chaser a) {
        boolean attack = false;        
        if(a.getX() > player.getX() && !tiles[a.getX() - 1][a.getY()].equals(TileType.WALL)
                && checkEntityChaser(a, -1, 0) && checkEntitySeeker(a, -1, 0)){
            
            if(a.getX() - 1 == player.getX() && player.getY() == a.getY()){
                attack = true;
            } else {
                a.setPosition(a.getX() - 1, a.getY());
            }

        } else if(a.getX() < player.getX() && !tiles[a.getX() + 1][a.getY()].equals(TileType.WALL)
                && checkEntityChaser(a, 1, 0) && checkEntitySeeker(a, 1, 0)){
                        
            if(a.getX() + 1 == player.getX() && player.getY() == a.getY()){
                attack = true;
            } else {
                a.setPosition(a.getX() + 1, a.getY());
            }

        } else if(a.getY() > player.getY() && !tiles[a.getX()][a.getY() - 1].equals(TileType.WALL)
                && checkEntityChaser(a, 0, -1) && checkEntitySeeker(a, 0, -1)){
                       
            if(a.getY() - 1 == player.getY() && player.getX() == a.getX()){
                attack = true;
            } else {
                a.setPosition(a.getX(), a.getY() - 1);
            }

        } else if(a.getY() < player.getY() && !tiles[a.getX()][a.getY() + 1].equals(TileType.WALL)
                && checkEntityChaser(a, 0, 1) && checkEntitySeeker(a, 0, 1)){
                                   
            if(a.getY() + 1 == player.getY() && player.getX() == a.getX()){
                attack = true;
            } else {
                a.setPosition(a.getX(), a.getY() + 1);
            }
        }
        
        if(attack){
            player.changeHealth(-20);
        }
    }

    /**
     * Called in response to the player collecting the Fuel and returning to the Car tile.
     * The method increases the valued of cleared by one, sets fuelCollected to false, generates a new
     * level by calling the generateLevel method, fills the spawns ArrayList with 
     * suitable spawn locations, then spawns Seekers, clears the chasers array
     * and spawns the Fuel. Finally it places the player in the new level by
     * calling the placePlayer() method. Note that a new player object should
     * not be created here as this will reset the player's health to maximum.
     */
    
    
    threadBackground object;
    
    private void newLevel() {

            cleared++;
            fuelCollected = false;
            hpCollected = false;
            ghostCollected = false;
            
            seekers = null;
            tiles = null;
            
            object = new threadBackground();
            //threadBackground object = new threadBackground();
            object.start();
            
            
            tiles = generateLevel();
            spawns = getSpawns();
            
            if(cleared % 3 == 0){
                ghost = spawnGhostItem();
            } else {
                ghost = null;
            }

            seekers = spawnSeekers();
            fuel = spawnFuel();
            hp = spawnHP();
            placePlayer();
            
            for (int i = 0; i < spawnChaser().length; i++) {
                if(spawnChaser()[i] != null){
                    checkSpawnPlace(spawnChaser()[i]);
                }
            }
            
            
    }

    /**
     * Places the player in a level by setting the player objects X and Y position
     * values to the tile that contains the Car.
     */
    private void placePlayer() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] == TileType.CAR){
                    player.setPosition(i, j);
                    player.changeHealth(+30);
                }
            }    
        }

    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. This method moves any monsters then checks if the player is dead,
     * exiting the game or resetting it. It checks if the player has collected
     * the fuel and returned to the Car to win the level and calls the newLevel()
     * method if it does. Finally it requests the GUI to redraw the game level
     * by passing it the tiles, player, monsters and fuel for the current
     * level.
     */
    public void doTurn() {
        
        if (turnNumber % 5 == 0) {
            moveSeekers();
        }
        if(turnNumber % 2 == 0){
            moveChasers();
        }
        if (player.getHealth() < 1) {
            System.exit(0);
        }
        gui.updateDisplay(tiles, player, seekers, chasers, fuel, hp, ghost);
        turnNumber++;
    }

    /**
     * Starts a game. This method generates a level, finds spawn positions in
     * the level, spawns monsters, fuel and the player and then requests the
     * GUI to update the level on screen using the information on tiles, player,
     * monsters and fuel.
     */
    public void startGame() {
        
        tiles = generateLevel();
        spawns = getSpawns();
        seekers = spawnSeekers();
        chasers = new Chaser[50];       //maximum 50 Chasers
        player = spawnPlayer();
        ghost = spawnGhostItem();
        fuel = spawnFuel();
        hp = spawnHP();  
        gui.updateDisplay(tiles, player, seekers, chasers, fuel, hp, ghost);
    }
    
    
   
    
    class threadBackground extends Thread 
    { 
        public void run() 
        { 
            try
            { 
                // Displaying the thread that is running 
                 gui.playMusic("C:\\Users\\sttsenov\\Documents\\NetBeansProjects\\2019FoPCoursework\\assets\\DesiJourney.wav");
            } 
            catch (Exception e) 
            { 
                // Throwing an exception 
                System.out.println ("Exception is caught"); 
            } 
        } 
    } 
    
    
    
    class threadSound extends Thread 
    { 
        public void run() 
        { 
            try
            { 
                // Displaying the thread that is running 
                 gui.playNoise("C:\\Users\\sttsenov\\Documents\\NetBeansProjects\\2019FoPCoursework\\assets\\mmm-2.wav");
            } 
            catch (Exception e) 
            { 
                // Throwing an exception 
                System.out.println ("Exception is caught"); 
            } 
        } 
    } 
    
}
