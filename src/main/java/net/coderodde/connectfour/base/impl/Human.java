package net.coderodde.connectfour.base.impl;

import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import net.coderodde.connectfour.base.Bot;
import net.coderodde.connectfour.base.ConnectFourState;
import net.coderodde.connectfour.base.PlayerColor;

/**
 * This class implements a human bot controllable from the console.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (May 25, 2019)
 */
public final class Human implements Bot {

    /**
     * The color of this human.
     */
    private final PlayerColor myPlayerColor;
    
    /**
     * The command prompt for the user input.
     */
    private final String commandPrompt;
    
    /**
     * The scanner for reading the input.
     */
    private final Scanner scanner;
    
    /**
     * Constructs the human player object.
     * @param myPlayerColor the color of this player.
     * @param commandPrompt the command prompt.
     * @param scanner the scanner object for reading the move commands.
     */
    public Human(PlayerColor myPlayerColor, 
                 String commandPrompt, 
                 Scanner scanner) {
        this.myPlayerColor = 
                Objects.requireNonNull(myPlayerColor, 
                                       "The input player is null.");
        
        this.commandPrompt = 
                Objects.requireNonNull(
                        commandPrompt, 
                        "The input command prompt is null.");
        
        this.scanner = Objects.requireNonNull(scanner, 
                                              "The input scanner is null.");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ConnectFourState computeNextState(ConnectFourState state) {
        while (true) {
            try {
                System.out.print(commandPrompt);
                String command = scanner.nextLine().trim();
                
                switch (command) {
                    case "quit":
                    case "exit":
                        System.out.println("C'ya!");
                        return null;
                }
                
                int columnIndex = -1;
                
                try {
                    columnIndex = Integer.parseInt(command);
                    checkColumn(columnIndex, state);
                } catch (NumberFormatException ex) {
                    continue;
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                    continue;
                }
                
                // Convert human indexing from 1 to computer indexing from 0:
                columnIndex--;
                
                if (state.columnIsFull(columnIndex)) {
                    System.out.println(
                            "Column " + (columnIndex + 1) + " is full.");
                    continue;
                }
                
                return state.move(columnIndex, myPlayerColor);
            } catch (InputMismatchException ex) {
                throw new IllegalStateException("Unrecognized column number: " +
                                                ex.getMessage());
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public PlayerColor getPlayerColor() {
        return myPlayerColor;
    }
    
    private void checkColumn(int columnIndex, ConnectFourState state) {
        if (columnIndex < 1) {
            throw new IllegalArgumentException(
                    "Too small column index: " + columnIndex + ". Must be at " + 
                    "least 1.");
        }
        
        if (columnIndex > state.getWidth()) {
            throw new IllegalArgumentException(
                    "Too large column index: " + columnIndex + ". Must be at " +
                    "mosst " + state.getWidth() + ".");
        }
    }
}
