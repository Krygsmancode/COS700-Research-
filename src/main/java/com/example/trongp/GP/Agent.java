package com.example.trongp.GP;

import com.example.trongp.GameState;
import com.example.trongp.Point;
import java.util.Random;

public class Agent {
    private Strategy strategy;
    private int agentNumber;
    private  Random random;
    private double fitness;
    private static int nextId = 1; // Static counter for unique IDs
    private int id; // Unique identifier



    public Agent(Strategy strategy, int agentNumber, Random random) {
        this.id = nextId++;
        this.strategy = strategy;
        this.agentNumber = agentNumber;
        this.random = random;
        this.fitness = 0.0; 
    }

    public int getNumber() {
        return agentNumber;
    }

    private boolean isElite = false;

    public boolean isElite() {
        return isElite;
    }
    public int getId() {
        return id;
    }

    public void setElite(boolean isElite) {
        this.isElite = isElite;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public int makeMove(GameState gameState, int agentNumber) {
        // Use the agent number provided, which should be 1 or 2
        // this.agentNumber = agentNumber;

        // Execute the strategy to determine the move.
        int move = strategy.execute(gameState, this.agentNumber);
     //   System.out.println("Agent #" + this.agentNumber + " initial move: " + move);

        // Validate the move without adjusting it
        if (!isMoveSafe(gameState, this.agentNumber, move)) {
       //     System.out.println("Move is unsafe or invalid for Agent #" + this.agentNumber);
            // Agent loses due to invalid or unsafe move
            return -1;
        }

        // Log the final selected move
     //   System.out.println("Agent #" + this.agentNumber + " final move: " + move);
        return move;
    }

    private boolean isMoveSafe(GameState gameState, int agentNumber, int move) {
        java.awt.Point currentPos = gameState.getCurrentPosition(agentNumber);
        if (currentPos == null) {
     //       System.out.println("Error: No current position found for Agent #" + agentNumber);
            return false; // Invalid position; move is unsafe
        }

        Point newPos = getNextPosition(currentPos.x, currentPos.y, move);
        if (newPos != null && gameState.isPositionSafe(newPos.x, newPos.y)) {
            // Move is safe
            return true;
        }
        // Move is unsafe
        return false;
    }

    Point getNextPosition(int x, int y, int move) {
        switch (move) {
            case 0: // Up
                return new Point(x, y - 1);
            case 1: // Down
                return new Point(x, y + 1);
            case 2: // Left
                return new Point(x - 1, y);
            case 3: // Right
                return new Point(x + 1, y);
            default:
                return null;
        }
    }

    /* Commented out the findSafeMove mechanic
    private int findSafeMove(GameState gameState, int agentNumber) {
        // Fetch current position of the agent
        java.awt.Point currentPos = gameState.getCurrentPosition(agentNumber);
        if (currentPos == null) {
            System.out.println("Error: No current position found for Agent #" + agentNumber);
            return -1; // Return -1 indicating failure to find a safe move
        }

        for (int move = 0; move < 4; move++) { // Check all four directions
            Point newPos = getNextPosition(currentPos.x, currentPos.y, move);
            if (newPos != null && gameState.isPositionSafe(newPos.x, newPos.y)) {
                return move; // Return the index of the safe move
            }
        }
        return -1; // No safe moves available
    }
    */

    /* Commented out the validateMove method that adjusts the move
    private int validateMove(GameState gameState, int agentNumber, int move) {
        java.awt.Point currentPos = gameState.getCurrentPosition(agentNumber);
        if (currentPos == null) {
            System.out.println("Error: No current position found for Agent #" + agentNumber);
            return -1; // Return -1 indicating failure to find a safe move
        }

        Point newPos = getNextPosition(currentPos.x, currentPos.y, move);
        if (newPos != null && gameState.isPositionSafe(newPos.x, newPos.y)) {
            return move; // Return the index of the safe move
        }
        return -1; // No safe moves available
    }
    */

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    @Override
    public Agent clone() {
        Strategy clonedStrategy = (this.strategy != null) ? this.strategy.clone() : null;
        Agent clonedAgent = new Agent(clonedStrategy, this.getNumber(), this.random);
        clonedAgent.setFitness(this.getFitness());
        clonedAgent.setElite(this.isElite());
        return clonedAgent;
    }
    
    
    public Agent cloneWithNewNumber(int newNumber) {
        Strategy clonedStrategy = (this.strategy != null) ? this.strategy.clone() : null;
        Agent clonedAgent = new Agent(clonedStrategy, newNumber, this.random);
        clonedAgent.setFitness(this.getFitness());
        clonedAgent.setElite(this.isElite());
        return clonedAgent;
    }

    void setNumber(int newNumber) {
        this.agentNumber = newNumber;
    }

    public int getMove(GameState gameState) {
        return strategy.execute(gameState, agentNumber);
    }

    public int getRemainingSafeMoves(GameState gameState) {
        int remainingSafeMoves = 0;
        java.awt.Point currentPosition = gameState.getCurrentPosition(this.getNumber());  // Assuming getCurrentPosition exists

        // Check all four possible moves: up, down, left, right
        for (int move = 0; move < 4; move++) {
            Point nextPosition = getNextPosition(currentPosition.x, currentPosition.y, move);
            if (gameState.isPositionSafe(nextPosition.x, nextPosition.y)) {
                remainingSafeMoves++;
            }
        }

        return remainingSafeMoves;
    }
}
