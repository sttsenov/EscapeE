/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.escapegame;

/**
 *
 * @author sttsenov
 */
public class HealthPotion extends Entity{
    
    /**
     * Creates a HealthPotion object with specific position on the board.
     * @param x Starting X position for the health potion.
     * @param y Starting Y position for the health potion.
     */
    public HealthPotion(int x, int y){
        setPosition(x, y);
    }
}
