package com.example.trongp.GP;

import com.example.trongp.GameState;
import com.example.trongp.Point;
import java.util.Random;

public class HandCraftedAgent extends Agent {
    private Random random;

    public HandCraftedAgent(int agentNumber, Random random) {
        super(null, agentNumber,random); // No strategy tree needed
        this.random = random;
    }

    @Override
    public int makeMove(GameState gameState, int agentNumber) {
        // Implement a basic heuristic. For this example, the agent will prefer to move in the following order: Up, Right, Down, Left
        int[] preferredMoves = {0, 3, 1, 2}; // Up, Right, Down, Left

        for (int move : preferredMoves) {
            if (isMoveSafe(gameState, agentNumber, move)) {
                return move;
            }
        }
        return -1; // No safe moves available
    }

    private boolean isMoveSafe(GameState gameState, int agentNumber, int move) {
        java.awt.Point currentPos = gameState.getCurrentPosition(agentNumber);
        if (currentPos == null) {
            return false;
        }

        Point newPos = getNextPosition(currentPos.x, currentPos.y, move);
        return newPos != null && gameState.isPositionSafe(newPos.x, newPos.y);
    }

    // Reuse the getNextPosition method from the Agent class
    @Override
    public Point getNextPosition(int x, int y, int move) {
        return super.getNextPosition(x, y, move);
    }

    @Override
public Agent clone() {
    HandCraftedAgent clonedAgent = new HandCraftedAgent(this.getNumber(), this.random);
    clonedAgent.setFitness(this.getFitness());
    clonedAgent.setElite(this.isElite());
    return clonedAgent;
}

@Override
public Agent cloneWithNewNumber(int newNumber) {
    HandCraftedAgent clonedAgent = new HandCraftedAgent(newNumber, this.random);
    clonedAgent.setFitness(this.getFitness());
    clonedAgent.setElite(this.isElite());
    return clonedAgent;
}
}
