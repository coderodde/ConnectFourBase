package net.coderodde.connectfour.base;

/**
 * This class implements the Connect Four game state.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (May 24, 2019)
 */
public class ConnectFourState {
    
    /**
     * The default number of columns.
     */
    private static final int DEFAULT_WIDTH = 7;
    
    /**
     * The default number of rows.
     */
    private static final int DEFAULT_HEIGHT = 6;
    
    /**
     * The default length of the winning line.
     */
    private static final int DEFAULT_WINNING_LENGTH = 4;
     
    /**
     * Caches the lower bar for printing the state to the console/command line.
     */
    private final String lowerBar;
    
    /**
     * The board state.
     */
    private PlayerColor[][] state;
    
    /**
     * The length of a horizontal/vertical/diagonal line leading to victory.
     */
    private final int winningLength;
    
    /**
     * Constructs an empty game board with given dimensions.
     * @param width the number of columns in the constructed state.
     * @param height the number of rows in the constructed state.
     */
    public ConnectFourState(int width, int height, int winningLength) {
        this.state = new PlayerColor[checkHeight(height)]
                               [checkWidth(width)];
        this.winningLength = checkWinningLength(winningLength);
        
        if (winningLength > Math.min(width, height)) {
            throw new IllegalArgumentException(
                    "The dimensions of the board are not sufficiently large " +
                    "in order to accommodate the winning pattern.");
        }
        
        this.lowerBar = createLowerBar(width);
    }
    
    /**
     * Constructs an empty game board with default dimensions.
     */
    public ConnectFourState() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_WINNING_LENGTH);
    }
    
    /**
     * Constructs a game board with given state.
     * 
     * @param state the state of the newly constructed game board.
     */
    private ConnectFourState(PlayerColor[][] state, int winningLength) {
        this.state = state;
        this.winningLength = winningLength;
        this.lowerBar = createLowerBar(state[0].length);
    }
    
    /**
     * Checks that the column is full.
     * @param x the coordinate of the column.
     * @return {@code true} only if the column is full.
     */
    public boolean columnIsFull(int x) {
        return state[0][x] != null;
    }
    
    /**
     * Makes a move and returns the board representing that move.
     * 
     * @param x the target column.
     * @param player the player to make the move.
     * @return a new board accommodating the new move.
     */
    public ConnectFourState move(int x, PlayerColor player) {
        if (columnIsFull(x)) {
            throw new IllegalStateException(
                    "Trying to put a token to a full column.");
        }
        
        PlayerColor[][] cloneState = cloneState();
        
        for (int y = cloneState.length - 1; y >= 0; y--) {
            if (cloneState[y][x] == null) {
                cloneState[y][x] = player;
                return new ConnectFourState(cloneState, winningLength);
            }
        }
        
        throw new IllegalStateException(
                "Trying to put the token to a full column.");
    }
    
    /**
     * Checks to see whether any of the players have won. If so, the player 
     * enumeration will be returned. In no player wins yet, {@code null} is 
     * returned.
     * @return the player or {@code null} if no players have won yet.
     */
    public PlayerColor checkVictory() {
        if (checkVictory(PlayerColor.WHITE_PLAYER)) {
            return PlayerColor.WHITE_PLAYER;
        }
        
        if (checkVictory(PlayerColor.RED_PLAYER)) {
            return PlayerColor.RED_PLAYER;
        }
        
        return null;
    }
    
    public boolean isFull() {
        for (int x = 0; x < state[0].length; x++) {
            if (!columnIsFull(x)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        final int width = 2 * getWidth() + 1;
        final int height = getHeight() + 1;
        
        StringBuilder stringBuilder =
                new StringBuilder(height - 1 + (width + 1) * height);
        
        for (int y = 0; y < getHeight(); y++) {
            stringBuilder.append('\u2502');
            
            for (int x = 0; x < getWidth(); x++) {
                stringBuilder.append(playerToString(state[y][x]))
                             .append('\u2502');
            }
            
            stringBuilder.append('\n');
        }
        
        stringBuilder.append(lowerBar);
        return stringBuilder.toString();
    }
    
    private boolean checkVictory(PlayerColor player) {
        return checkVictoryHorizontal(player)
                || checkVictoryVertical(player)
                || checkVictoryAscendingDiagonal(player) 
                || checkVictoryDescendingDiagonal(player);
    }
    
    private boolean checkVictoryDescendingDiagonal(PlayerColor player) {
        for (int startY = state.length - 1; 
                startY >= winningLength - 1;
                startY--) {
            for (int startX = winningLength - 1;
                    startX < getWidth(); 
                    startX++) {
                int count = 0;
                
                for (int i = 0; i < winningLength; i++) {
                    PlayerColor currentPlayer = state[startY - i][startX - i];
                    
                    if (currentPlayer != player) {
                        // Drop the state and start counting from the next
                        // position:
                        count = 0;
                    } else if (++count == winningLength) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    private boolean checkVictoryAscendingDiagonal(PlayerColor player) {
        for (int startY = state.length - 1;
                startY >= winningLength - 1;
                startY--) {
            for (int startX = 0; 
                    startX <= state[0].length - winningLength;
                    startX++) {
                int count = 0;
                
                for (int i = 0; i < winningLength; i++) {
                    PlayerColor currentPlayer = state[startY - i][startX + i];
                    
                    if (currentPlayer != player) {
                        // Drop the state and start counting from the next
                        // position:
                        count = 0;
                    } else {
                        if (++count == winningLength) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private boolean checkVictoryVertical(PlayerColor player) {
        for (int x = 0; x < state[0].length; x++) {
            int count = 0;
            
            for (int y = state.length - 1; y >= 0; y--) {
                PlayerColor currentPlayer = state[y][x];
                
                if (currentPlayer != player) {
                    // Drop the state and start counting from beginning:
                    count = 0;
                } else {
                    if (++count == winningLength) {
                        return true;
                    }
                }
            }
        }
    
        return false;
    }
    
    private boolean checkVictoryHorizontal(PlayerColor player) {
        for (int y = getHeight() - 1; y >= 0; y--) {
            int count = 0;
            
            for (int x = 0; x < state[0].length; x++) {
                PlayerColor currentPlayer = state[y][x];
                
                if (currentPlayer != player) {
                    // Drop the state and start counting from beginning:
                    count = 0;
                } else {
                    if (++count == winningLength) {
                        return true;
                    }
                }
            }
        }
            
        return false;
    }
    
    /**
     * Reads a cell in the board.
     * @param x the column.
     * @param y the row.
     * @return the player occupying that cell, or {@code null} if the cell is 
     * empty.
     */
    public PlayerColor getPlayer(int x, int y) {
        return state[y][x];
    }
    
    public PlayerColor readCell(final int x, final int y) {
        return state[y][x];
    }
    
    public int getHeight() {
        return state.length;
    }
    
    public int getWidth() {
        return state[0].length;
    }
    
    public int getWinningLength() {
        return winningLength;
    }
    
    private PlayerColor[][] cloneState() {
        PlayerColor[][] cloneState = new PlayerColor[state.length]
                                          [state[0].length];
        
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state[y].length; x++) {
                cloneState[y][x] = state[y][x];
            }
        }
        
        return cloneState;
    }
    
    private static final int checkHeight(int height)  {
        if (height < 1) {
            throw new IllegalArgumentException("height = " + height);
        }
        
        return height;
    }
    
    private static final int checkWidth(int width) {
        if (width < 1) {
            throw new IllegalArgumentException("width = " + width);
        }
        
        return width;
    }
    
    private static final int checkWinningLength(int winningLength) {
        if (winningLength < 3) {
            throw new IllegalArgumentException(""
                    + "winningLength = " + winningLength);
        }
        
        return winningLength;
    }
    
    private String createLowerBar(int width) {
        StringBuilder stringBuilder = new StringBuilder(2 * width + 1);
        stringBuilder.append('\u2558');
        
        for (int i = 0; i < width - 1; i++) {
            stringBuilder.append("\u2550\u2567");
        }
        
        stringBuilder.append("\u2550\u255b");
        
        return stringBuilder.toString();
    }
    
    private String playerToString(PlayerColor player) {
        if (player == null) {
            return " ";
        }
        
        switch (player) {
            case RED_PLAYER:
                return "X";
            case WHITE_PLAYER:
                return "O";
            default:
                throw new IllegalStateException("Should not ever get here.");
        }
    }
}
