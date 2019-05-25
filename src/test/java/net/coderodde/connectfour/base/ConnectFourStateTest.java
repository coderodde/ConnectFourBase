package net.coderodde.connectfour.base;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ConnectFourStateTest {
    
    @Test
    public void testMoveConnectFourState() {
        ConnectFourState state1 = new ConnectFourState();
        ConnectFourState state2 = state1.move(3, PlayerColor.RED_PLAYER);
        ConnectFourState state3 = state2.move(2, PlayerColor.WHITE_PLAYER);
        ConnectFourState state4 = state3.move(2, PlayerColor.RED_PLAYER);
        
        assertEquals(PlayerColor.RED_PLAYER, state2.getPlayer(3, 5));
        assertEquals(PlayerColor.WHITE_PLAYER, state3.getPlayer(2, 5));
        assertEquals(PlayerColor.RED_PLAYER, state4.getPlayer(2, 4));
        assertNull(state4.getPlayer(2, 3));
        
        for (int x = 0; x < state4.getWidth(); x++) {
            assertFalse(state1.columnIsFull(x));
            assertFalse(state2.columnIsFull(x));
            assertFalse(state3.columnIsFull(x));
            assertFalse(state4.columnIsFull(x));
        }
    }
    
    @Test
    public void testCheckVictoryHorizontal() {
        ConnectFourState state = new ConnectFourState(7, 6, 3);
        state = state.move(0, PlayerColor.RED_PLAYER);
        state = state.move(2, PlayerColor.RED_PLAYER);
        state = state.move(3, PlayerColor.RED_PLAYER);
        assertNull(state.checkVictory());
        
        state = state.move(1, PlayerColor.RED_PLAYER);
        assertEquals(PlayerColor.RED_PLAYER, state.checkVictory());
    }
    
    @Test
    public void testCheckVictoryVertical() {
        ConnectFourState state = new ConnectFourState(4, 4, 3);
        assertNull(state.checkVictory());
        
        state = state.move(2, PlayerColor.RED_PLAYER);
        state = state.move(2, PlayerColor.WHITE_PLAYER);
        
        assertNull(state.checkVictory());
        
        state = state.move(2, PlayerColor.WHITE_PLAYER);
        state = state.move(2, PlayerColor.WHITE_PLAYER);
        assertTrue(state.columnIsFull(2));
        
        assertEquals(PlayerColor.WHITE_PLAYER, state.checkVictory());
    }
    
    @Test
    public void testCheckAscendingDiagonal() {
        ConnectFourState state = new ConnectFourState(5, 5, 3);
        state = state.move(1, PlayerColor.RED_PLAYER);
        state = state.move(2, PlayerColor.WHITE_PLAYER);
        state = state.move(3, PlayerColor.WHITE_PLAYER);
        state = state.move(2, PlayerColor.RED_PLAYER);
        state = state.move(3, PlayerColor.WHITE_PLAYER);
        
        assertNull(state.checkVictory());
        
        state = state.move(3, PlayerColor.RED_PLAYER);
        
        assertEquals(PlayerColor.RED_PLAYER, state.checkVictory());
    }
    
    @Test
    public void testCheckDescendingDiagonal() {
        ConnectFourState state = new ConnectFourState(3, 3, 3);
        state = state.move(0, PlayerColor.RED_PLAYER);
        state = state.move(1, PlayerColor.RED_PLAYER);
        state = state.move(2, PlayerColor.WHITE_PLAYER);
        state = state.move(0, PlayerColor.RED_PLAYER);
        state = state.move(1, PlayerColor.WHITE_PLAYER);
        
        assertNull(state.checkVictory());
        
        state = state.move(0, PlayerColor.WHITE_PLAYER);
        
        assertEquals(PlayerColor.WHITE_PLAYER, state.checkVictory());
    }
}
