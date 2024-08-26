package com.example.trongp.GP;

import com.example.trongp.GameState;

public class Agent implements Cloneable{
    private Node strategy;
    private double fitness = 0;
    private int number;  // Identifier for the agent (1 or 2)


    public Agent(Node strategy, int number) {
        this.strategy = strategy;
        this.fitness = 0.0;
        this.number = number;
    }

    @Override
    public Agent clone() {
        try {
            Agent cloned = (Agent) super.clone();
            cloned.strategy = this.strategy.clone(); // Clone the strategy deeply
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone is supported but was not successful", e);
        }
    }

    public int makeMove(GameState gameState) {
        return strategy.evaluate(gameState, this.number); // Strategy evaluates based on the game state and agent's perspective.
    }
    public int getNumber() {
        return number;
    }

    public void setStrategy(Node strategy) {
        this.strategy = strategy;
    }

    public Node getStrategy() {
        return strategy;
    }

    public String getStrategyTree() {
        return strategy.toString();
    }

    public void recordGameResult(boolean won, double trailLength) {
        double result = won ? 1.0 : 0.0;
        fitness += (result + trailLength);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
      public boolean compete(Agent opponent) {
        // Implementation of a competitive game logic between two agents
        // For simplicity, let's assume a direct call to a game simulation environment
        GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE);
        boolean thisAgentWins = false;
        while (!gameState.isGameOver()) {
            int thisMove = this.makeMove(gameState);
            int opponentMove = opponent.makeMove(gameState);
            gameState.update(thisMove, opponentMove);
            if (gameState.isGameOver()) {
                thisAgentWins = gameState.didAgentWin(this);
            }
        }
        return thisAgentWins;
    }
}
