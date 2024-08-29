package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public class DecisionNode extends Node {
    private Node left;
    private Node right;
    private int decisionFeature;

    public DecisionNode(int decisionFeature, Node left, Node right) {
        super(1);  // or appropriate depth
        this.decisionFeature = decisionFeature;
        this.left = left;
        this.right = right;
    }

    public DecisionNode(int maxDepth) {
        super(maxDepth);
        this.decisionFeature = random.nextInt(6); // Properly assign a valid feature index
        
        // Debugging statement to check the maxDepth and decisionFeature
        System.out.println("Creating DecisionNode with maxDepth: " + maxDepth + " and decisionFeature: " + decisionFeature);
    
        // Recursively create left and right child nodes with reduced depth
        if (maxDepth > 0) {
            this.left = NodeFactory.createRandomNode(maxDepth - 1);
            this.right = NodeFactory.createRandomNode(maxDepth - 1);
        } else {
            this.left = new ActionNode(); // Terminal node
            this.right = new ActionNode(); // Terminal node
        }
    }
    
    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        double[] features = extractFeatures(gameState, agentNumber);
        
        // Debugging output to understand what is happening
      //  System.out.println("Evaluating DecisionNode. DecisionFeature: " + decisionFeature + ", Features length: " + features.length);
        
        // Ensure decisionFeature is within the bounds of the features array
        if (decisionFeature >= features.length) {
            System.err.println("Error: decisionFeature index " + decisionFeature + " is out of bounds for features array of length " + features.length);
            return -1; // or some safe default value
        }
    
        if (features[decisionFeature] > 0.5) {
            return left.evaluate(gameState, agentNumber);
        } else {
            return right.evaluate(gameState, agentNumber);
        }
    }
    

    private double[] extractFeatures(GameState gameState, int agentNumber) {
        double[] features = new double[6];
        double agentX = agentNumber == 1 ? gameState.getAgent1X() : gameState.getAgent2X();
        double agentY = agentNumber == 1 ? gameState.getAgent1Y() : gameState.getAgent2Y();
        features[0] = distanceToWall(gameState, agentX, agentY, "UP");
        features[1] = distanceToWall(gameState, agentX, agentY, "DOWN");
        features[2] = distanceToWall(gameState, agentX, agentY, "LEFT");
        features[3] = distanceToWall(gameState, agentX, agentY, "RIGHT");
        double opponentX = agentNumber == 1 ? gameState.getAgent2X() : gameState.getAgent1X();
        double opponentY = agentNumber == 1 ? gameState.getAgent2Y() : gameState.getAgent1Y();
        features[4] = distanceToOpponent(agentX, agentY, opponentX, opponentY);
        features[5] = distanceToEnemyTrail(gameState, agentX, agentY, agentNumber);
        return features;
    }

    private double distanceToOpponent(double agentX, double agentY, double opponentX, double opponentY) {

        return Math.sqrt(Math.pow(agentX - opponentX, 2) + Math.pow(agentY - opponentY, 2));
    }
    private double distanceToEnemyTrail(GameState gameState, double agentX, double agentY, int agentNumber) {
        double opponentX = agentNumber == 1 ? gameState.getAgent2X() : gameState.getAgent1X();
        double opponentY = agentNumber == 1 ? gameState.getAgent2Y() : gameState.getAgent1Y();
        int opponentDirection = agentNumber == 1 ? gameState.getAgent2Direction() : gameState.getAgent1Direction();
    
        switch (opponentDirection) {
            case 0: // Up
                return Math.abs(opponentY - agentY);
            case 1: // Down
                return Math.abs(agentY - opponentY);
            case 2: // Left
                return Math.abs(opponentX - agentX);
            case 3: // Right
                return Math.abs(agentX - opponentX);
            default:
                return Double.MAX_VALUE; // Should never reach here, but return a large number just in case
        }
    }
    

    private double distanceToWall(GameState gameState, double agentX, double agentY, String direction) {
        if (direction.equals("UP")) {
            return agentY;
        } else if (direction.equals("DOWN")) {
            return gameState.getHeight() - agentY - 1;  // Subtract 1 because coordinates are zero-indexed
        } else if (direction.equals("LEFT")) {
            return agentX;
        } else {  // "RIGHT"
            return gameState.getWidth() - agentX - 1;  // Subtract 1 because coordinates are zero-indexed
        }
    }
    

    @Override
    public Node crossover(Node other) {
        if (other instanceof DecisionNode) {
            DecisionNode otherNode = (DecisionNode) other;
            if (random.nextBoolean()) {
                Node newLeft = this.left.crossover(otherNode.left);
                return new DecisionNode(this.decisionFeature, newLeft, this.right);
            } else {
                Node newRight = this.right.crossover(otherNode.right);
                return new DecisionNode(this.decisionFeature, this.left, newRight);
            }
        } else {
            return this;
        }
    }

    public void mutate() {
        if (random.nextDouble() < GPParameters.MUTATION_RATE) {
            decisionFeature = random.nextInt(6); // Ensure decisionFeature stays within valid bounds
           
        }
        left.mutate();
        right.mutate();
    }

    @Override
    public String toString() {
        return "DecisionNode(" + decisionFeature + ")[" + left.toString() + ", " + right.toString() + "]";
    }
}
