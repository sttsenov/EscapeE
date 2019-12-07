package uk.ac.bradford.escapegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.File;
import javax.swing.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import uk.ac.bradford.escapegame.GameEngine.TileType;

/**
 * The GameGUI class is responsible for rendering graphics to the screen to display
 * the game grid, player, monsters and the fuel. The GameGUI class passes keyboard
 * events to a registered InputHandler to be handled.
 * @author prtrundl
 */
public class GameGUI extends JFrame {
    
    /**
     * The three final int attributes below set the size of some graphical elements,
     * specifically the display height and width of tiles in the level and the height
     * of health bars for Ship objects in the game. Tile sizes should match the size
     * of the image files used in the game.
     */
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int HEALTH_BAR_HEIGHT = 3;
    
    /**
     * The canvas is the area that graphics are drawn to. It is an internal class
     * of the GameGUI class.
     */
    Canvas canvas;
    /**
     * Constructor for the GameGUI class. It calls the initGUI method to generate the
     * required objects for display.
     */
    public GameGUI() {
        start();
        initGUI();
        
    }
    
    
    /**
     * Registers an object to be passed keyboard events captured by the GUI.
     * @param i the InputHandler object that will process keyboard events to
     * make the game respond to input
     */
    public void registerKeyHandler(InputHandler i) {
        addKeyListener(i);
    }
    
    /**
     * Method to create and initialise components for displaying elements of the
     * game on the screen.
     */
    JButton startButton = new JButton("Start");
    JButton exitButton = new JButton("Exit");
    JButton aboutButton = new JButton("About");
    MyCanvas tl = new MyCanvas();
    
    boolean pressed = false;
    
    JPanel panel = new JPanel();
    
    private void initGUI(){
        add(canvas = new Canvas());     //adds canvas to this frame
        setTitle("EscapE");
        setSize(816, 615);
        setLocationRelativeTo(null);        //sets position of frame on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Method to create and initialise a starting menu filled with buttons that
     * perform specific actions when clicked
     */
    public void start(){
        //Layout of the buttons
        BorderLayout x = new BorderLayout();
        
        setTitle("EscapE");
        setSize(816, 615);
        
        Color myPurple = new Color(71, 45, 156);
        
        //Start Button
        startButton.setSize(50,50);
        startButton.setBackground(myPurple);
        startButton.setForeground(Color.CYAN);
        
        //About Button
        aboutButton.setSize(50, 50);
        aboutButton.setBackground(Color.CYAN);
        aboutButton.setForeground(myPurple);
        
        //Exit Button
        exitButton.setSize(50, 50);
        
        panel.setLayout(x);
        
        //Adding all the buttons and positioning them
        panel.add(startButton, BorderLayout.CENTER);
        panel.add(aboutButton, BorderLayout.PAGE_START);
        panel.add(exitButton, BorderLayout.PAGE_END);

        add(panel);
        
        setVisible(true);
        
        //Listens for when the bu
        startButton.addActionListener(new startApp());
        aboutButton.addActionListener(new aboutApp());
        exitButton.addActionListener(new exitApp());
        
        setLocationRelativeTo(null);        //sets position of frame on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    
    /**
     * Listens for when the "start" button is pressed then goes into the 
     * initGUI() method that is called in the GameGUI() 
     */    
    class startApp implements ActionListener{

        public void actionPerformed(ActionEvent e)
        {         
            setTitle("EscapE");
            setSize(816, 615);
            remove(panel);
            repaint();
            requestFocus();
            return;
        }
    }
    
        
    /**
     * Listens for when the "exit" button is pressed then kills the program
     */    
    class exitApp implements ActionListener{

        public void actionPerformed(ActionEvent e)
        {   
            dispose();
        }
    }
    
    /**
     * Listens for when the "about" button is pressed then widens the size of the
     * frame and writes text on a specific position. If the button is pressed again
     * it returns to the normal size of the window.
     */        
    class aboutApp implements ActionListener{

        public void actionPerformed(ActionEvent e)
        {   
            if(!pressed){       
                setTitle("EscapE");
                setSize(1116, 615);        
                add(tl);
                pressed = true;
            }
            else{
                setTitle("EscapE");
                setSize(816, 615);
                pressed = false;
            }
        }
    }
    
    class MyCanvas extends JComponent {

        @Override
        public void paintComponent(Graphics g) {
            if(g instanceof Graphics2D){
                
                 Graphics2D g2 = (Graphics2D)g;
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);

                 g2.drawString("つ ◕_◕ つ",920,20); 
                 g2.drawString("I am not a designer, please do not judge me", 820, 50);
                 g2.drawString("Instructions on how to play this game:", 840, 80);
                 g2.drawString("Use the arrow keys to move around the map", 830, 130);
                 g2.drawString("Collect the fuel and return to the car to finish a level", 811, 160);
                 g2.drawString("Collect ghost item to pass through walls in the level", 810, 180);
                 g2.drawString("Beware of ants", 900, 210);
                 g2.drawString("Thank you Paul Trundle", 880, 350);
                 g2.drawString("You are the object of my hearth", 860, 380);
                 g2.drawString("<3", 940, 410);
                }
            }
        }
    

    
    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    /**
     * The method playNoise is used when a short sound file is being player. 
     * The origin of the code is StackOverflow's user greenLizard
     * <https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java>
     * No changes were made to the original code.
     * @param filename the name of the file that is going to be played
     */
    public void playNoise(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }

    /**
     * The method playMusic() is used to play the audio file for the background music
     * The origin of the code is StackOverflow's user greenLizard
     * <https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java>
     * No changes were made to the original code except for the different name of the
     * variables.
     * @param filename the name of the file that is going to be played
     */
    
    private final int BUFFER_SIZE_2 = 128000;
    private File soundFile_2;
    private AudioInputStream audioStream_2;
    private AudioFormat audioFormat_2;
    private SourceDataLine sourceLine_2;
    
    
    public void playMusic(String filename){

        String strFilename = filename;

        try {
            soundFile_2 = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream_2 = AudioSystem.getAudioInputStream(soundFile_2);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat_2 = audioStream_2.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat_2);
        try {
            sourceLine_2 = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine_2.open(audioFormat_2);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine_2.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE_2];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream_2.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine_2.write(abData, 0, nBytesRead);
            }
        }

        sourceLine_2.drain();
        sourceLine_2.close();
    }

    
    
    /**
     * Method to update the graphical elements on the screen, usually after entities
     * have moved when a keyboard event was handled. The method
     * requires four arguments and displays corresponding information on the screen.
     * @param tiles A 2-dimensional array of TileTypes. This is the tiles of the
     * current level that should be drawn to the screen.
     * @param player A Human object. This object is used to draw the player in
 the right tile and display its health. null can be passed for this argument,
 in which case no player will be drawn.
     * @param aliens An array of Seeker objects that is processed to draw
 aliens in tiles with a health bar. null can be passed for this argument in which
 case no aliens will be drawn. Elements in the aliens array can also be null,
 in which case nothing will be drawn for that element of the array.
     * @param asteroids An array of Asteroid objects that is processed to draw the
     * asteroids on the map. null elements in the array, or a null array are both
     * permitted, and any null arrays or null elements in the array will be skipped.
     */
    
    /**
     * Method to update the graphical elements on the screen, usually after entities
     * have moved when a keyboard event was handled. The method
     * requires four arguments and displays corresponding information on the screen.
     * @param tiles A 2-dimensional array of TileTypes. This is the tiles of the
     * current level that should be drawn to the screen.
     * @param player A Human object. This object is used to draw the player in
 the right tile and display its health. null can be passed for this argument,
 in which case no player will be drawn.
     * @param seekers An array of Seeker objects that is processed to draw 
     * seekers in tiles. null can be passed for this argument in which case no
     * seekers will be drawn. Elements in the seekers array can also be null,
     * in which case nothing will be drawn for that element of the array.
     * @param chasers An array of Chaser objects that is processed to draw 
     * chasers in tiles. null can be passed for this argument in which case no
     * chasers will be drawn. Elements in the chasers array can also be null,
     * in which case nothing will be drawn for that element of the array.
     * @param fuel A Fuel object. This is used to draw the fuel on the map.
     * @param healthpot A health potion object. This is used to draw a health potion on the map.
     * @param ghost
     */
    public void updateDisplay(TileType[][] tiles, Human player, Seeker[] seekers, Chaser[] chasers, Fuel fuel, HealthPotion healthpot, Ghost ghost) {
        canvas.update(tiles, player, seekers, chasers, fuel, healthpot, ghost);
    }
    
}

/**
 * Internal class used to draw elements within a JPanel. The Canvas class loads
 * images from an asset folder inside the main project folder.
 * @author prtrundl
 */
class Canvas extends JPanel {

    private BufferedImage car;
    private BufferedImage chaser;
    private BufferedImage dirt;
    private BufferedImage fuel;
    private BufferedImage grass;
    private BufferedImage nest;
    private BufferedImage road;
    private BufferedImage runner;
    private BufferedImage seeker;
    private BufferedImage wall;
    private BufferedImage healthpot;
    private BufferedImage ghost;
    
    TileType[][] currentTiles;  //the current 2D array of tiles to display
    Human currentPlayer;        //the current player object to be drawn
    Seeker[] currentSeekers;    //the current array of seekers to draw
    Chaser[] currentChasers;    //the current array of chasers
    Fuel currentFuel;           //the current fuel on the map
    HealthPotion currentHealthPot; //the current health potion on the map
    Ghost currentGhost; //the current final boss to be drawn
    
    /**
     * Constructor that loads tile images for use in this class
     */
    public Canvas() {
        loadTileImages();
    }
    
    /**
     * Loads tiles images from a fixed folder location within the project directory
     */
    private void loadTileImages() {
        try {
            car = ImageIO.read(new File("assets/car.png"));
            assert car.getHeight() == GameGUI.TILE_HEIGHT &&
                    car.getWidth() == GameGUI.TILE_WIDTH;
            wall = ImageIO.read(new File("assets/wall.png"));
            assert wall.getHeight() == GameGUI.TILE_HEIGHT &&
                    wall.getWidth() == GameGUI.TILE_WIDTH;
            chaser = ImageIO.read(new File("assets/chaser.png"));
            assert chaser.getHeight() == GameGUI.TILE_HEIGHT &&
                    chaser.getWidth() == GameGUI.TILE_WIDTH;
            dirt = ImageIO.read(new File("assets/dirt.png"));
            assert dirt.getHeight() == GameGUI.TILE_HEIGHT &&
                    dirt.getWidth() == GameGUI.TILE_WIDTH;
            fuel = ImageIO.read(new File("assets/fuel.png"));
            assert fuel.getHeight() == GameGUI.TILE_HEIGHT &&
                    fuel.getWidth() == GameGUI.TILE_WIDTH;
            grass = ImageIO.read(new File("assets/grass.png"));
            assert grass.getHeight() == GameGUI.TILE_HEIGHT &&
                    grass.getWidth() == GameGUI.TILE_WIDTH;
            nest = ImageIO.read(new File("assets/nest.png"));
            assert nest.getHeight() == GameGUI.TILE_HEIGHT &&
                    nest.getWidth() == GameGUI.TILE_WIDTH;
            road = ImageIO.read(new File("assets/road.png"));
            assert road.getHeight() == GameGUI.TILE_HEIGHT &&
                    road.getWidth() == GameGUI.TILE_WIDTH;
            runner = ImageIO.read(new File("assets/runner.png"));
            assert runner.getHeight() == GameGUI.TILE_HEIGHT &&
                    runner.getWidth() == GameGUI.TILE_WIDTH;
            seeker = ImageIO.read(new File("assets/seeker.png"));
            assert seeker.getHeight() == GameGUI.TILE_HEIGHT &&
                    seeker.getWidth() == GameGUI.TILE_WIDTH;
            healthpot = ImageIO.read(new File("assets/health potion.png"));
            assert healthpot.getHeight() == GameGUI.TILE_HEIGHT &&
                    healthpot.getWidth() == GameGUI.TILE_WIDTH;
            assert ghost.getHeight() == GameGUI.TILE_HEIGHT &&
                    ghost.getWidth() == GameGUI.TILE_WIDTH;
            ghost = ImageIO.read(new File("assets/final boss.png"));
        } catch (IOException e) {
            System.out.println("Exception loading images: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
    
    /**
     * Updates the current graphics on the screen to display the tiles, fuel, player and monsters
     * @param t The 2D array of TileTypes representing the current level of the dungeon
     * @param player The current player object, used to draw the player and its health
     * @param mon The array of monsters to display them and their health
     */
    public void update(TileType[][] t, Human p, Seeker[] al, Chaser[] ch, Fuel f, HealthPotion hp, Ghost fb) {
        currentTiles = t;
        currentPlayer = p;
        currentSeekers = al;
        currentChasers = ch;
        currentFuel = f;
        currentHealthPot = hp;
        currentGhost = fb;
        repaint();
    }
    
    /**
     * Override of method in super class, it draws the custom elements for this
     * game such as the tiles, player, aliens and asteroids.
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
    }

    /**
     * Draws graphical elements to the screen to display the current level
     * tiles, the player, asteroids and the aliens. If the tiles, player or
     * alien objects are null they will not be drawn.
     * @param g Graphics object to use for drawing
     */
    private void drawMap(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Random r = new Random(555);
        if (currentTiles != null) {
            for (int i = 0; i < currentTiles.length; i++) {
                for (int j = 0; j < currentTiles[i].length; j++) {
                    switch (currentTiles[i][j]) {
                        case CAR:
                            g2.drawImage(car, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                            break;
                        case DIRT:
                            g2.drawImage(dirt, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                            break;
                        case NEST:
                            g2.drawImage(nest, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                            break;
                        case GRASS:
                            g2.drawImage(grass, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                            break;
                        case ROAD:
                            g2.drawImage(road, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                            break;
                        case WALL:
                            g2.drawImage(wall, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                            break;
                    }
                }
            }
        }
        if (currentSeekers != null)
            for(Seeker s : currentSeekers)
                if (s != null) {
                    g2.drawImage(seeker, s.getX() * GameGUI.TILE_WIDTH, s.getY() * GameGUI.TILE_HEIGHT, null);
                }
        if (currentChasers != null)
            for(Chaser c : currentChasers)
                if (c != null) {
                    g2.drawImage(chaser, c.getX() * GameGUI.TILE_WIDTH, c.getY() * GameGUI.TILE_HEIGHT, null);
                }
        if (currentPlayer != null) {
            g2.drawImage(runner, currentPlayer.getX() * GameGUI.TILE_WIDTH, currentPlayer.getY() * GameGUI.TILE_HEIGHT, null);
            drawHealthBar(g2, currentPlayer);
        }
        if (currentGhost != null) {
            g2.drawImage(ghost, currentGhost.getX() * GameGUI.TILE_WIDTH, currentGhost.getY() * GameGUI.TILE_HEIGHT, null);
        }
        if (currentFuel != null) {
            g2.drawImage(fuel, currentFuel.getX() * GameGUI.TILE_WIDTH, currentFuel.getY() * GameGUI.TILE_HEIGHT, null);
        }
        if (currentHealthPot != null) {
            g2.drawImage(healthpot, currentHealthPot.getX() * GameGUI.TILE_WIDTH, currentHealthPot.getY() * GameGUI.TILE_HEIGHT, null);
        }
    }
    
    /**
     * Draws a health bar for the given entity at the bottom of the tile that
     * the entity is located in.
     * @param g2 The graphics object to use for drawing
     * @param e The entity that the health bar will be drawn for
     */
    private void drawHealthBar(Graphics2D g2, Human h) {
        double remainingHealth = (double)h.getHealth() / (double)h.getMaxHealth();
        g2.setColor(Color.RED);
        g2.fill(new Rectangle2D.Double(h.getX() * GameGUI.TILE_WIDTH, h.getY() * GameGUI.TILE_HEIGHT + 29, GameGUI.TILE_WIDTH, GameGUI.HEALTH_BAR_HEIGHT));
        g2.setColor(Color.GREEN);
        g2.fill(new Rectangle2D.Double(h.getX() * GameGUI.TILE_WIDTH, h.getY() * GameGUI.TILE_HEIGHT + 29, GameGUI.TILE_WIDTH * remainingHealth, GameGUI.HEALTH_BAR_HEIGHT));
    }

}
    

