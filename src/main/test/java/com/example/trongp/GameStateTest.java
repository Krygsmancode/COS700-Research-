
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameStateTest {
    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState(10, 10); // Adjust grid size as needed
    }

    @Test
    void testInitialPositions() {
        assertFalse(gameState.isGameOver(), "Game should not be over at start");
        assertNotEquals(gameState.getAgent1X(), gameState.getAgent2X());
        assertNotEquals(gameState.getAgent1Y(), gameState.getAgent2Y());
    }

    @Test
    void testMoveAgent() {
        int initialX = gameState.getAgent1X();
        int initialY = gameState.getAgent1Y();
        gameState.update(3, 3); // Assume move right for both agents
        assertEquals(initialX + 1, gameState.getAgent1X(), "Agent should move right by 1 step");
    }

    @Test
    void testGameOverByCollision() {
        gameState.update(0, 1); // Moves to force a collision or some condition
        assertTrue(gameState.isGameOver(), "Game should be over after collision");
    }
}
