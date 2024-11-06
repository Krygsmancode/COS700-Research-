package com.example.trongp.GP;

import com.example.trongp.GameState;
import com.example.trongp.Point;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class HandCraftedAgent extends Agent {
    private Random random;

    public HandCraftedAgent(int agentNumber, Random random) {
        super(null, agentNumber, random); // No strategy tree needed
        this.random = random;
    }

    @Override
    public int makeMove(GameState gameState, int agentNumber) {
        // Collect all safe moves
        List<Integer> safeMoves = new ArrayList<>();
        for (int move = 0; move < 4; move++) {
            if (isMoveSafe(gameState, agentNumber, move)) {
                safeMoves.add(move);
            }
        }
        // Randomly select a safe move
        if (!safeMoves.isEmpty()) {
            return safeMoves.get(random.nextInt(safeMoves.size()));
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
