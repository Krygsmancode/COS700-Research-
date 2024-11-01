package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public abstract class Node {
    protected Random random;
    protected int depth;

    public Node(int depth, Random random) {
        this.depth = depth;
        this.random = random;
    }

    public abstract int evaluate(GameState gameState, int agentNumber);
    public abstract Node crossover(Node other);
    public abstract void mutate(boolean isPhase2); 
    public abstract String toString();

    public int getMaxDepth() {
        return depth;
    }

    public boolean isCompatibleForCrossover(Node other) {
        return this.getClass().equals(other.getClass());
    }

    @Override
    public abstract Node clone();
}
