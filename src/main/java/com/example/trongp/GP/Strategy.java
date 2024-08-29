package com.example.trongp.GP;

import com.example.trongp.GameState;

public class Strategy {
    private Node root;

    public Strategy(int maxDepth) {
        this.root = NodeFactory.createRandomNode(maxDepth);
        if (this.getMaxDepth() > maxDepth) {
            System.err.println("Error: Created strategy exceeds max depth!");
        }
    }

    public Strategy(Node root) {
        this.root = root;
    }

    public int execute(GameState gameState, int agentNumber) {
        return root.evaluate(gameState, agentNumber);
    }

    public String getTreeRepresentation() {
        return root.toString();
    }

    public Strategy crossover(Strategy other) {
        Node newRoot = this.root.crossover(other.getRoot());
        Strategy offspring = new Strategy(newRoot);
        if (offspring.getMaxDepth() > GPParameters.MAX_DEPTH) {
            System.err.println("Error: Crossover resulted in strategy exceeding max depth!");
        }
        return offspring;
    }

    public void mutate() {
        root.mutate();
        if (this.getMaxDepth() > GPParameters.MAX_DEPTH) {
            System.err.println("Error: Mutation resulted in strategy exceeding max depth!");
            // Optionally regenerate the tree here if necessary
            this.root = NodeFactory.createRandomNode(GPParameters.MAX_DEPTH);
        }
    }

    public int getMaxDepth() {
        return root.getMaxDepth();
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public int decideMove(GameState gameState, int agentNumber) {
        return root.evaluate(gameState, agentNumber);
    }
}
