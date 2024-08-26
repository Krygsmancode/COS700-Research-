package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public abstract class Node implements Cloneable{
    protected int depth;
    protected static Random random = new Random();

    public Node(int depth) {
        this.depth = depth;
    }

    public abstract int evaluate(GameState gameState, int agentNumber);
    public abstract Node crossover(Node other);
    public abstract void mutate();
    public abstract String toString();

    public int getMaxDepth() {
        return depth;
    }

    @Override
    public Node clone() {
        try {
            return (Node) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
