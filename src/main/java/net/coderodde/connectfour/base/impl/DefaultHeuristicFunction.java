package net.coderodde.connectfour.base.impl;

import net.coderodde.connectfour.base.ConnectFourState;
import net.coderodde.connectfour.base.PlayerColor;
import net.coderodde.connectfour.base.HeuristicFunction;

/**
 * This class implements the default Connect Four state evaluator. The white 
 * player wants to maximize, the red player wants to minimize.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (May 24, 2019)
 */
public final class DefaultHeuristicFunction implements HeuristicFunction {

    /**
     * The weight matrix. Maps each position to its weight. We need this in 
     * order to 
     */
    private final double[][] weightMatrix;
    
    /**
     * The winning length.
     */
    private final int winningLength;
    
    /**
     * Constructs the default heuristic function.
     * @param weightMatrix the weight matrix.
     * @param winningPatternLength the winning pattern length.
     */
    public DefaultHeuristicFunction(final int width,
                                    final int height,
                                    final double maxWeight,
                                    final int winningPatternLength) {
        this.weightMatrix = getWeightMatrix(width, height, maxWeight);
        this.winningLength = winningPatternLength;
    }
    
    /**
     * Evaluates the given input {@code state} and returns the estimate.
     * @param state the state to estimate.
     * @return the estimate.
     */
    @Override
    public double evaluate(ConnectFourState state) {
        // 'redPatternCounts[i]' gives the number of patterns of length 'i':
        int[] redPatternCounts = new int[state.getWinningLength() + 1];
        int[] whitePatternCounts = new int[redPatternCounts.length];
        
        // Do not consider patterns of length one!
        for (int targetLength = 2; 
                targetLength <= winningLength; 
                targetLength++) {
            int count = findRedPatternCount(state, targetLength);
            
            if (count == 0) {
                // Once here, it is not possible to find patterns of larger 
                // length than targetLength:
                break;
            }
            
            redPatternCounts[targetLength] = count;
        }
        
        for (int targetLength = 2;
                targetLength <= state.getWinningLength();
                targetLength++) {
            int count = findWhitePatternCount(state, targetLength);
            
            if (count == 0) {
                // Once here, it is not possible to find patterns of larger
                // length than targetLength:
                break;
            }
            
            whitePatternCounts[targetLength] = count;
        }
        
        double score = computeBaseScore(redPatternCounts, 
                                              whitePatternCounts);
        
        if (Double.isInfinite(score)) {
            return score;
        }
        
        return score + getWeights(weightMatrix, state);
    }
    
    /**
     * Finds the number of red patterns of length {@code targetLength}.
     * @param state the target state.
     * @param targetLength the length of the pattern to find.
     * @return the number of red patterns of length {@code targetLength}.
     */
    private static final int findRedPatternCount(ConnectFourState state,
                                                 int targetLength) {
        return findPatternCount(state, targetLength, PlayerColor.RED_PLAYER);
    }
    
    /**
     * Finds the number of white patterns of length {@code targetLength}. 
     * @param state the target state.
     * @param targetLength the length of the pattern to find.
     * @return the number of white patterns of length {@code targetLength}.
     */
    private static final int findWhitePatternCount(ConnectFourState state,
                                                   int targetLength) {
        return findPatternCount(state, targetLength, PlayerColor.WHITE_PLAYER);
    }
    
    /**
     * Implements the target pattern counting function for both the player 
     * colors.
     * @param state the state to search.
     * @param targetLength the length of the patterns to count.
     * @param playerColor the target player color.
     * @return the number of patterns of length {@code targetLength} and color
     * {@code playerColor}.
     */
    private static final int findPatternCount(ConnectFourState state,
                                              int targetLength,
                                              PlayerColor playerColor) {
        int count = 0;
        
        count += findHorizontalPatternCount(state, 
                                            targetLength, 
                                            playerColor);
        
        count += findVerticalPatternCount(state, 
                                          targetLength, 
                                          playerColor);
        
        count += findAscendingDiagonalPatternCount(state, 
                                                   targetLength,
                                                   playerColor);
        
        count += findDescendingDiagonalPatternCount(state, 
                                                    targetLength,
                                                    playerColor);
        
        return count;
    }
    
    /**
     * Scans the input state for diagonal <b>descending</b> patterns and 
     * returns the number of such patterns.
     * @param state the target state.
     * @param patternLength the target pattern length.
     * @param playerColor the target player color.
     * @return the number of patterns.
     */
    private static final int 
        findDescendingDiagonalPatternCount(ConnectFourState state,
                                           int patternLength,
                                           PlayerColor playerColor) {
        int patternCount = 0;

        for (int y = 0; y < state.getWinningLength() - 1; y++) {
            inner:
            for (int x = 0;
                    x <= state.getWidth() - state.getWinningLength(); 
                    x++) {
                for (int i = 0; i < patternLength; i++) {
                    if (state.readCell(x + i, y + i) != playerColor) {
                        continue inner;
                    }
                }

                patternCount++;
            }
        }

        return patternCount;
    }
     
    /**
     * Scans the input state for diagonal <b>ascending</b> patterns and returns
     * the number of such patterns.
     * @param state the target state.
     * @param patternLength the target pattern length.
     * @param playerColor the target player color.
     * @return the number of patterns.
     */
    private static final int 
        findAscendingDiagonalPatternCount(ConnectFourState state,
                                          int patternLength,
                                          PlayerColor playerColor) {
        int patternCount = 0;
        
        for (int y = state.getHeight() - 1;
                y > state.getHeight() - state.getWinningLength();
                y--) {
            
            inner:
            for (int x = 0; 
                    x <= state.getWidth() - state.getWinningLength();
                    x++) {
                for (int i = 0; i < patternLength; i++) {
                    if (state.readCell(x + i, y - i) != playerColor) {
                        continue inner;
                    }
                }
                
                patternCount++;
            }
        }
        
        return patternCount;
    } 
     
    /**
     * Scans the input state for diagonal <b>horizontal</b> patterns and returns
     * the number of such patterns.
     * @param state the target state.
     * @param patternLength the target pattern length.
     * @param playerColor the target player color.
     * @return the number of patterns.
     */
    private static final int findHorizontalPatternCount(
            ConnectFourState state,
            int patternLength,
            PlayerColor playerColor) {
        int patternCount = 0;
        
        for (int y = state.getHeight() - 1; y >= 0; y--) {
            
            inner:
            for (int x = 0; x <= state.getWidth() - patternLength; x++) {
                if (state.readCell(x, y) == null) {
                    continue inner;
                }
                
                for (int i = 0; i < patternLength; i++) {
                    if (state.readCell(x + i, y) != playerColor) {
                        continue inner;
                    }
                }
                
                patternCount++;
            }
        }
        
        return patternCount;
    }
    
    /**
     * Scans the input state for diagonal <b>vertical</b> patterns and returns
     * the number of such patterns.
     * @param state the target state.
     * @param patternLength the target pattern length.
     * @param playerColor the target player color.
     * @return the number of patterns.
     */
    private static final int findVerticalPatternCount(ConnectFourState state,
                                                      int patternLength,
                                                      PlayerColor playerColor) {
        int patternCount = 0;
        
        outer:
        for (int x = 0; x < state.getWidth(); x++) {
            inner:
            for (int y = state.getHeight() - 1;
                    y > state.getHeight() - state.getWinningLength(); 
                    y--) {
                if (state.readCell(x, y) == null) {
                    continue outer;
                }
                
                for (int i = 0; i < patternLength; i++) {
                    if (state.readCell(x, y - i) != playerColor) {
                        continue inner;
                    }
                }
                
                patternCount++;
            }
        }
        
        return patternCount;
    }
    
    /**
     * Gets the state weight. We use this in order to discourage the positions
     * that are close to borders/far away from the center of the game board.
     * @param weightMatrix the weighting matrix.
     * @param state the state to weight.
     * @return the state weight.
     */
    private static final double getWeights(final double[][] weightMatrix,
                                           final ConnectFourState state) {
        double score = 0.0;
        
        outer:
        for (int y = state.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < state.getWidth(); x++) {
                PlayerColor playerColor = state.readCell(x, y);
                
                if (playerColor == null) {
                    continue outer;
                }
                
                if (playerColor == PlayerColor.RED_PLAYER) {
                    score -= weightMatrix[y][x];
                } else {
                    score += weightMatrix[y][x];
                }
            }
        }
        
        return score;
    }
    
    /**
     * Computes the base scorer that relies on number of patterns. For example,
     * {@code redPatternCounts[i]} will denote the number of patterns of length 
     * [@code i}.
     * @param redPatternCounts the pattern count map for red patterns.
     * @param whitePatternCounts the pattern count map for white patterns.
     * @return the base estimate.
     */
    private static final double computeBaseScore(
            int[] redPatternCounts,
            int[] whitePatternCounts) {
        final int winningLength = redPatternCounts.length - 1;
        double value = 0.0;
        
        for (int length = 2; length < redPatternCounts.length; length++) {
            final int redCount = redPatternCounts[length];
            value -= redCount * (1.0 / (winningLength - length));
            
            if (Double.isInfinite(value)) {
                // Red (minimizing) player wins:
                return value;
            }
            
            final int whiteCount = whitePatternCounts[length];
            value += whiteCount * (1.0 / (winningLength - length));
            
            if (Double.isInfinite(value)) {
                // White (maximizing) player wins:
                return value;
            }
        }
        
        return value;
    }
    
    /**
     * Computes the weight matrix. The closer the entry in the board is to the
     * center of the board, the closer the weight of that position will be to
     * {@code maxWeight}.
     * 
     * @param width the width of the matrix.
     * @param height the height of the matrix.
     * @param maxWeight the maximum weight. The minimum weight will be always
     * 1.0.
     * @return the weight matrix. 
     */
    private static final double[][] getWeightMatrix(final int width,
                                                    final int height,
                                                    final double maxWeight) {
        final double[][] weightMatrix = new double[height][width];
        
        for (int y = 0; y < weightMatrix.length; y++) {
            for (int x = 0; x < weightMatrix[0].length; x++) {
                int left = x;
                int right = weightMatrix[0].length - x - 1;
                int top = y;
                int bottom = weightMatrix.length - y - 1;
                int horizontalDifference = Math.abs(left - right);
                int verticalDifference = Math.abs(top - bottom);
                weightMatrix[y][x] =
                        1.0 + (maxWeight - 1.0) / 
                              (horizontalDifference + verticalDifference);
            }
        }
        
        return weightMatrix;
    }
    
//    public static void main(String[] args) {
//        double[][] wm  = getWeightMatrix(7, 6, 10.0);
//        
//        for (double[] row : wm) {
//            for (double d : row) {
//                System.out.print(d + " ");
//            }
//            System.out.println();
//        }
//    }
}
