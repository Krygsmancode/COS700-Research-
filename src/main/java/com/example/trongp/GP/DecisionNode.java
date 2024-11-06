package com.example.trongp.GP;

import com.example.trongp.GameState;
import com.example.trongp.Point;

import java.util.List;
import java.util.Random;

public class DecisionNode extends Node {
    static final int NUM_FEATURES =  13; // Increased number of features
    Node left;
    Node right;
    int decisionFeature;
    double threshold; // Added threshold
    private Random random;

    public DecisionNode(int decisionFeature, double threshold, Node left, Node right, Random random) {
        super(Math.max(left.getMaxDepth(), right.getMaxDepth()) + 1, random);
        this.decisionFeature = decisionFeature % NUM_FEATURES; // Ensure index is within bounds
        this.threshold = threshold;
        this.left = left;
        this.right = right;
        this.random = random;
    }

    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        // Ensure we do not proceed if the game is over
        if (gameState.isGameOver()) {
      //      System.out.println("Game over, no further decisions required for agent #" + agentNumber);
            return -1; // Indicate no move is required
        }

        double[] features = extractFeatures(gameState, agentNumber);
        double featureValue = features[decisionFeature];

        // System.out.println("Agent #" + agentNumber + " DecisionNode: Feature[" + decisionFeature + "] = " + featureValue +
        //         ", Threshold = " + threshold);

        // Compare feature value with threshold and decide on child node
        Node childNode = featureValue > threshold ? left : right;
        String decision = featureValue > threshold ? "left" : "right";

        // Log whether the agent is taking a left or right path, and explain why
        // System.out.println("Agent #" + agentNumber + " Decision: " + decision +
        //         " (Feature[" + decisionFeature + "] = " + featureValue +
        //         (featureValue > threshold ? " > " : " <= ") +
        //         "Threshold = " + threshold + ")");

        return childNode.evaluate(gameState, agentNumber);
    }
    
    private double[] extractFeatures(GameState gameState, int agentNumber) {
        double[] features = new double[NUM_FEATURES];
    
        int agentX = (int) (agentNumber == 1 ? gameState.getAgent1X() : gameState.getAgent2X());
        int agentY = (int) (agentNumber == 1 ? gameState.getAgent1Y() : gameState.getAgent2Y());
        int gridHeight = gameState.getHeight();
        int gridWidth = gameState.getWidth();
    
        // Immediate surroundings
        features[0] = isCellFree(gameState, agentX, agentY - 1); // Up
        features[1] = isCellFree(gameState, agentX, agentY + 1); // Down
        features[2] = isCellFree(gameState, agentX - 1, agentY); // Left
        features[3] = isCellFree(gameState, agentX + 1, agentY); // Right
    
        // Distances to walls
        features[4] = distanceToWall(gameState, agentX, agentY, "UP") / (double) gridHeight;
        features[5] = distanceToWall(gameState, agentX, agentY, "DOWN") / (double) gridHeight;
        features[6] = distanceToWall(gameState, agentX, agentY, "LEFT") / (double) gridWidth;
        features[7] = distanceToWall(gameState, agentX, agentY, "RIGHT") / (double) gridWidth;
    
        // Number of safe moves
        features[8] = numberOfSafeMoves(gameState, agentNumber) / 4.0;
    
        // Previous move
        features[9] = getPreviousMove(gameState, agentNumber) / 3.0;
    
        // Distance to center
        double maxDistanceToCenter = Math.sqrt(Math.pow(gridWidth / 2.0, 2) + Math.pow(gridHeight / 2.0, 2));
        features[10] = distanceToCenter(gameState, agentX, agentY) / maxDistanceToCenter;
    
        if (!gameState.isSoloMode()) {
            // Distances to opponent and enemy trail
            double maxDistance = Math.sqrt(Math.pow(gridWidth, 2) + Math.pow(gridHeight, 2));
            features[11] = distanceToOpponent(gameState, agentX, agentY, agentNumber) / maxDistance;
            features[12] = distanceToEnemyTrail(gameState, agentX, agentY, agentNumber) / maxDistance;
        } else {
            features[11] = 0.0;
            features[12] = 0.0;
        }
    
        return features;
    }
    

    private static final String[] FEATURE_NAMES = {
        "IsCellFreeUp",
        "IsCellFreeDown",
        "IsCellFreeLeft",
        "IsCellFreeRight",
        "DistanceToWallUp",
        "DistanceToWallDown",
        "DistanceToWallLeft",
        "DistanceToWallRight",
        "NumberOfSafeMoves",
        "PreviousMove",
        "DistanceToCenter",
        "DistanceToOpponent",
        "DistanceToEnemyTrail"
    };
    
    
    
    private String getFeatureName(int index) {
        if (index >= 0 && index < FEATURE_NAMES.length) {
            return FEATURE_NAMES[index];
        } else {
            return "UnknownFeature";
        }
    }
    
    
    private double getPreviousMove(GameState gameState, int agentNumber) {
        int previousMove;
        if (agentNumber == 1) {
            previousMove = (int) gameState.getAgent1PreviousMove();
        } else {
            previousMove = (int) gameState.getAgent2PreviousMove();
        }
        // Handle initial case
        if (previousMove == -1) {
            return 0.0; // Or any default value within [0.0, 1.0]
        }
        return previousMove / 3.0;
    }
    
    

    private double isCellFree(GameState gameState, int x, int y) {
        if (x >= 0 && x < gameState.getWidth() && y >= 0 && y < gameState.getHeight()) {
            return gameState.getGrid()[y][x] == 0 ? 1.0 : 0.0;
        }
        return 0.0; // Consider out-of-bounds as not free
    }


private double distanceToCenter(GameState gameState, int agentX, int agentY) {
    double centerX = gameState.getWidth() / 2.0;
    double centerY = gameState.getHeight() / 2.0;
    return Math.sqrt(Math.pow(agentX - centerX, 2) + Math.pow(agentY - centerY, 2));
}

private double numberOfSafeMoves(GameState gameState, int agentNumber) {
    int agentX = agentNumber == 1 ? (int) gameState.getAgent1X() : (int) gameState.getAgent2X();
    int agentY = agentNumber == 1 ? (int) gameState.getAgent1Y() : (int) gameState.getAgent2Y();
    int safeMoves = 0;

   // System.out.println("Calculating number of safe moves for Agent #" + agentNumber + " at position (" + agentX + "," + agentY + ")");

    if (gameState.isPositionSafe(agentX, agentY - 1)) { // Up
        safeMoves++;
 //       System.out.println("  Move Up is safe.");
    }
    if (gameState.isPositionSafe(agentX, agentY + 1)) { // Down
        safeMoves++;
  //      System.out.println("  Move Down is safe.");
    }
    if (gameState.isPositionSafe(agentX - 1, agentY)) { // Left
        safeMoves++;
  //      System.out.println("  Move Left is safe.");
    }
    if (gameState.isPositionSafe(agentX + 1, agentY)) { // Right
        safeMoves++;
   //     System.out.println("  Move Right is safe.");
    }

   // System.out.println("Total Safe Moves: " + safeMoves);
    return safeMoves;
}





private double distanceToOpponent(GameState gameState, int agentX, int agentY, int agentNumber) {
    int opponentX = agentNumber == 1 ? (int) gameState.getAgent2X() : (int) gameState.getAgent1X();
    int opponentY = agentNumber == 1 ? (int) gameState.getAgent2Y() : (int) gameState.getAgent1Y();
    return Math.sqrt(Math.pow(agentX - opponentX, 2) + Math.pow(agentY - opponentY, 2));
}

private double distanceToEnemyTrail(GameState gameState, int agentX, int agentY, int agentNumber) {
    int opponentNumber = agentNumber == 1 ? 2 : 1;
    List<java.awt.Point> enemyTrail = gameState.getAgentTrail(opponentNumber);
    double minDistance = Double.MAX_VALUE;

    for (java.awt.Point trailPoint : enemyTrail) {
        double distance = Math.sqrt(Math.pow(agentX - trailPoint.x, 2) + Math.pow(agentY - trailPoint.y, 2));
        if (distance < minDistance) {
            minDistance = distance;
        }
    }
    return minDistance;
}


    private double distanceToWall(GameState gameState, int agentX, int agentY, String direction) {
        if (direction.equals("UP")) {
            return agentY; // Corrected calculation
        } else if (direction.equals("DOWN")) {
            return gameState.getHeight() - agentY - 1; // Corrected calculation
        } else if (direction.equals("LEFT")) {
            return agentX;
        } else { // "RIGHT"
            return gameState.getWidth() - agentX - 1;
        }
    }
    
    
    @Override
    public Node crossover(Node other) {
        if (!(other instanceof DecisionNode)) {
    //        System.err.println("Crossover attempted between incompatible types. Returning a clone.");
            return this.clone();
        }
        DecisionNode otherNode = (DecisionNode) other;
        if (this.random == null || otherNode.random == null) {
            throw new IllegalStateException("Random instance is null during crossover.");
        }
        Node newLeft = this.random.nextDouble() < GPParameters.CROSSOVER_RATE ? this.left.crossover(otherNode.left) : this.left.clone();
        Node newRight = this.random.nextDouble() < GPParameters.CROSSOVER_RATE ? this.right.crossover(otherNode.right) : this.right.clone();
        double newThreshold = this.random.nextBoolean() ? this.threshold : otherNode.threshold;
    
        return new DecisionNode(this.random.nextBoolean() ? this.decisionFeature : otherNode.decisionFeature, newThreshold, newLeft, newRight, this.random);
    }
    

    @Override
    public Node clone() {
        Node clonedLeft = this.left.clone();
        Node clonedRight = this.right.clone();
        return new DecisionNode(this.decisionFeature, this.threshold, clonedLeft, clonedRight, this.random);
    }

    
    @Override
    public void mutate(boolean isPhase2) {
        double mutationRate = isPhase2 ? GPParameters.PHASE2_MUTATION_RATE : GPParameters.MUTATION_RATE;
    
        // Mutate this node
        if (random.nextDouble() < mutationRate) {
            // Swap left and right subtrees
            Node temp = left;
            left = right;
            right = temp;
    
            // Mutate the decision feature and threshold
            decisionFeature = NodeFactory.selectFeatureIndex(random, isPhase2);
            threshold = random.nextDouble();
        }
    
        // Mutate child nodes based on mutation rate
        if (random.nextDouble() < mutationRate) {
            left.mutate(isPhase2);
        }
        if (random.nextDouble() < mutationRate) {
            right.mutate(isPhase2);
        }
    
        this.depth = Math.max(left.getMaxDepth(), right.getMaxDepth()) + 1;
    }
    
    
    
    private int selectFeatureIndex(Random random, boolean isPhase2) {
        if (isPhase2) {
            // Increase probability of selecting opponent features
            double randValue = random.nextDouble();
            if (randValue < 0.5) {
                return 11 + random.nextInt(2); // Features 11 and 12
            } else {
                return random.nextInt(11); // Features 0 to 10
            }
        } else {
            return random.nextInt(11); // Features 0 to 10
        }
    }
    

    @Override
    public String toString() {
        String featureStr = getFeatureName(decisionFeature);
        return String.format("Decision(%s > %.2f)\n  If True:\n    %s\n  If False:\n    %s",
                             featureStr, threshold, left.toString().replace("\n", "\n    "),
                             right.toString().replace("\n", "\n    "));
    }
    
    

    public double getThreshold() {
        return threshold;
    }

    public Object getDecisionFeature() {
        return decisionFeature;
       
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }
    
}
