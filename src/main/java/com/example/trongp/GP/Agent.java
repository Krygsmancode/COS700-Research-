package com.example.trongp.GP;

import com.example.trongp.GameState;
import com.example.trongp.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.example.trongp.Point;
// In Agent class
public class Agent {
    private Strategy strategy;
    private int agentNumber;
    private Random random;
    private double fitness;

    public Agent(Strategy strategy, int agentNumber) {
        this.strategy = strategy;
        this.agentNumber = agentNumber;
        this.random = new Random();
        this.fitness = 0.0; 
    }

    public int getNumber() {
        return agentNumber;
    }


    private boolean isElite = false;

    public boolean isElite() {
        return isElite;
    }

    public void setElite(boolean isElite) {
        this.isElite = isElite;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public int makeMove(GameState gameState, int agentNumber) {
        // Use the agent number provided, which should be 1 or 2
        this.agentNumber = agentNumber;
    
        // Execute the strategy to determine the initial move.
        int move = strategy.execute(gameState, this.agentNumber);
        System.out.println("Agent #" + this.agentNumber + " initial move: " + move);
    
        // Validate the move based on the game state.
        move = validateMove(gameState, this.agentNumber, move);
        if (move == -1) {
            System.out.println("Move after validation is unsafe for Agent #" + this.agentNumber);
            // Attempt to find a safe move if the original or validated move is not safe.
            move = findSafeMove(gameState, this.agentNumber);
            if (move == -1) {
                System.out.println("No safe moves available. Agent #" + this.agentNumber + " has lost.");
                gameState.setGameOver(true);  // Consider letting GameState handle game over
                return -1;
            }
        }
    
        // Log the final selected move after validation and potentially finding a safe alternative.
        System.out.println("Agent #" + this.agentNumber + " final move: " + move);
        return move;
    }
    

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
         //       System.out.println("Safe move found for Agent #" + agentNumber + ": " + move);
                return move; // Return the index of the safe move
            }
        }
      //  System.out.println("No safe moves found for Agent #" + agentNumber);
        return -1; // No safe moves available
    }
    
    
    
    private Point getNextPosition(int x, int y, int move) {
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

    private int validateMove(GameState gameState, int agentNumber, int move) {
        java.awt.Point currentPos = gameState.getCurrentPosition(agentNumber);
        if (currentPos == null) {
            System.out.println("Error: No current position found for Agent #" + agentNumber);
            return -1; // Return -1 indicating failure to find a safe move
        }

        Point newPos = getNextPosition(currentPos.x, currentPos.y, move);
        if (newPos != null && gameState.isPositionSafe(newPos.x, newPos.y)) {
     //       System.out.println("Safe move found for Agent #" + agentNumber + ": " + move);
            return move; // Return the index of the safe move
        }
     //   System.out.println("No safe moves found for Agent #" + agentNumber);
        return -1; // No safe moves available
    }

    private boolean isGridFull(GameState gameState) {
        for (int y = 0; y < gameState.getHeight(); y++) {
            for (int x = 0; x < gameState.getWidth(); x++) {
                if (gameState.getGrid()[y][x] == 0) {
                    return false; // There's still an empty cell
                }
            }
        }
        return true; // All cells are occupied
    }
    
    private int findSafeMove(GameState gameState, int x, int y) {
        List<Integer> safeMoves = new ArrayList<>();
        for (int m = 0; m < 4; m++) {
            java.awt.Point awtPos = gameState.getNextPosition(x, y, m);
            if (awtPos == null) continue;
            com.example.trongp.Point pos = new com.example.trongp.Point(awtPos.x, awtPos.y);
            boolean safe = gameState.isPositionSafe(pos.x, pos.y);
            System.out.println("Checking move " + m + " to position (" + pos.x + ", " + pos.y + "): " + (safe ? "Safe" : "Unsafe"));
            if (safe) {
                safeMoves.add(m);
            }
        }
        if (!safeMoves.isEmpty()) {
            int selectedMove = safeMoves.get(random.nextInt(safeMoves.size()));
            System.out.println("Safe moves available: " + safeMoves + ". Selected move: " + selectedMove);
            return selectedMove;
        }
    
        // No safe moves available
        System.out.println("No safe moves found from position (" + x + ", " + y + ")");
        return -1;
    }
    
    

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Strategy getStrategy() {
        return strategy;
        
    }

    public Agent clone() {
        Agent cloned = new Agent(this.strategy.clone(), Population.getNextAgentId());
        cloned.setFitness(this.fitness);  // Ensure fitness is copied over.
        cloned.setElite(this.isElite); // Copy the elite status
     //   System.out.println("Cloning Agent #" + this.getNumber() + " to Agent #" + cloned.getNumber() + " with Fitness: " + this.getFitness());
        return cloned;
    }
    
    
    public Agent cloneWithNewNumber(int newNumber) {
        Agent clonedAgent = this.clone();
        clonedAgent.setNumber(newNumber);
        return clonedAgent;
    }
    

    void setNumber(int newNumber) {
        this.agentNumber = newNumber;
    }

    public int getMove(GameState gameState) {
        return strategy.execute(gameState, agentNumber);
    }

    
}
