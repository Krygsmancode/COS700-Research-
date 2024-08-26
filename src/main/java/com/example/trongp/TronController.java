package com.example.trongp;

import com.example.trongp.GP.Agent;
import com.example.trongp.GameState;
import com.example.trongp.GP.GPParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TronController {
    private GameState gameState;
    private GraphicsContext gc;

    public TronController(GraphicsContext gc) {
        this.gc = gc;
        this.gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE);
    }

    public void runVisualizationGame(Agent redAgent, Agent blueAgent) {
        initialize();
        while (!gameState.isGameOver()) {
            int redMove = redAgent.makeMove(gameState);
            int blueMove = blueAgent.makeMove(gameState);
            gameState.update(redMove, blueMove);
            renderGame();
            try {
                Thread.sleep(500);  // Delay for visualization purposes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void initialize() {
        gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE);
    }

    private void renderGame() {
        int cellSize = 10;  // Assume each cell in the grid is 10x10 pixels
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GPParameters.GRID_SIZE * cellSize, GPParameters.GRID_SIZE * cellSize);

        for (int i = 0; i < GPParameters.GRID_SIZE; i++) {
            for (int j = 0; j < GPParameters.GRID_SIZE; j++) {
                if (gameState.getGrid()[i][j] == 1) {
                    gc.setFill(Color.RED);
                } else if (gameState.getGrid()[i][j] == 2) {
                    gc.setFill(Color.BLUE);
                } else {
                    continue;
                }
                gc.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }
}
