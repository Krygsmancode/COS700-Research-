package com.example.trongp.GP;

import com.example.trongp.GameState;

public class Agent implements Cloneable {
    private Strategy strategy;
    private double fitness;
    private int number;

    public Agent(Strategy strategy, int number) {
        this.strategy = strategy;
        this.fitness = 0.0;
        this.number = number;
    }

    public int makeMove(GameState gameState, int agentNumber) {
        return strategy.getRoot().evaluate(gameState, agentNumber);
    }

    @Override
    public Agent clone() {
        try {
            Agent clone = (Agent) super.clone();
            clone.strategy = new Strategy(this.strategy.getRoot().clone()); // Clone the strategy
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Agent cloning failed", e);
        }
    }

    public void recordGameResult(boolean won, double trailLength) {
        this.fitness += (won ? 1.0 : 0) + trailLength;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getNumber() {
        return number;
    }
    public boolean compete(Agent opponent) {
        // Initialize the game state
        GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE);
    
        // Simulate the game until it's over
        while (!gameState.isGameOver()) {
            int thisAgentMove = makeMove(gameState, this.number);
            int opponentMove = opponent.makeMove(gameState, opponent.getNumber());
            gameState.update(thisAgentMove, opponentMove);
        }
    
        // Determine the winner (true if this agent wins, false if the opponent wins)
        return gameState.didAgentWin(this);
    }
    
}
