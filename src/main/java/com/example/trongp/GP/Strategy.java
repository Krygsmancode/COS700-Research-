package com.example.trongp.GP;

import com.example.trongp.GameState;

public class Strategy {
    private Node root;

    public Strategy(int maxDepth) {
        this.root = NodeFactory.createRandomNode(maxDepth, true);
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
        Node newRoot = this.root.crossover(other.root);
        return new Strategy(newRoot);
    }

    public void mutate() {
        root.mutate();
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
