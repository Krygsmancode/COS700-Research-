package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public class DecisionNode extends Node {
    static final int NUM_FEATURES =  13; // Increased number of features
    Node left;
    Node right;
    int decisionFeature;
    double threshold; // Added threshold

    public DecisionNode(int decisionFeature, double threshold, Node left, Node right, Random random) {
        super(Math.max(left.getMaxDepth(), right.getMaxDepth()) + 1, random);
        this.decisionFeature = decisionFeature % NUM_FEATURES; // Ensure index is within bounds
        this.threshold = threshold;
        this.left = left;
        this.right = right;
    }

    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        // Ensure we do not proceed if the game is over
        if (gameState.isGameOver()) {
            System.out.println("Game over, no further decisions required for agent #" + agentNumber);
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
        // Ensure we do not proceed if the game is over
        if (gameState.isGameOver()) {
            System.out.println("Game over, no features to extract for agent #" + agentNumber);
            return new double[NUM_FEATURES]; // Return default features
        }
    
        double[] features = new double[NUM_FEATURES];
    
        int agentX = (int) (agentNumber == 1 ? gameState.getAgent1X() : gameState.getAgent2X());
        int agentY = (int) (agentNumber == 1 ? gameState.getAgent1Y() : gameState.getAgent2Y());
        int gridHeight = gameState.getHeight();
        int gridWidth = gameState.getWidth();
    
        // Immediate surroundings (already binary [0, 1])
        features[0] = isCellFree(gameState, agentX, agentY - 1); // Up
        features[1] = isCellFree(gameState, agentX, agentY + 1); // Down
        features[2] = isCellFree(gameState, agentX - 1, agentY); // Left
        features[3] = isCellFree(gameState, agentX + 1, agentY); // Right
    
        // Distances to walls (normalized to [0, 1])
        features[4] = distanceToWall(gameState, agentX, agentY, "UP") / (double) gridHeight;
        features[5] = distanceToWall(gameState, agentX, agentY, "DOWN") / (double) gridHeight;
        features[6] = distanceToWall(gameState, agentX, agentY, "LEFT") / (double) gridWidth;
        features[7] = distanceToWall(gameState, agentX, agentY, "RIGHT") / (double) gridWidth;
    
        // Number of safe moves (normalized to [0, 1] with max 4 moves possible)
        features[8] = numberOfSafeMoves(gameState, agentNumber) / 4.0;
    
        // Agent's previous move (normalized to [0, 1] assuming values from 0-3)
        features[9] = getPreviousMove(gameState, agentNumber) / 3.0;
    
        // Distance to center (normalized based on the maximum possible distance)
        double maxDistanceToCenter = Math.sqrt(Math.pow(gridWidth / 2.0, 2) + Math.pow(gridHeight / 2.0, 2));
        features[10] = distanceToCenter(gameState, agentX, agentY) / maxDistanceToCenter;
    
        if (!gameState.isSoloMode()) {
            // Distances to opponent and enemy trail (normalized based on grid dimensions)
            double maxDistance = Math.sqrt(Math.pow(gridWidth, 2) + Math.pow(gridHeight, 2));
            features[11] = 1 - distanceToOpponent(gameState, agentX, agentY, agentNumber) / maxDistance;
            features[12] =1 - distanceToEnemyTrail(gameState, agentX, agentY, agentNumber) / maxDistance;
        } else {
            features[11] = 0.0; // Neutral value indicating no opponent
            features[12] = 0.0; // Neutral value indicating no enemy trail
        }
    
        // Logging normalized features
        // for (int i = 0; i < features.length; i++) {
        //     System.out.println("  Feature[" + i + "] (" + getFeatureName(i) + "): " + features[i]);
        // }
    
        return features;
    }
    
    
    private String getFeatureName(int index) {
        switch (index) {
            case 0: return "IsCellFreeUp";
            case 1: return "IsCellFreeDown";
            case 2: return "IsCellFreeLeft";
            case 3: return "IsCellFreeRight";
            case 4: return "DistanceToWallUp";
            case 5: return "DistanceToWallDown";
            case 6: return "DistanceToWallLeft";
            case 7: return "DistanceToWallRight";
            case 8: return "NumberOfSafeMoves";
            case 9: return "PreviousMove";
            case 10: return "DistanceToCenter";
            case 11: return "DistanceToOpponent";
            case 12: return "DistanceToEnemyTrail";
            default: return "UnknownFeature";
        }
    }
    

    private String getExpectedRange(int featureIndex) {
        switch (featureIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
                return "[0.0, 1.0] (Binary, 0 = Occupied, 1 = Free)";
            case 4:
            case 5:
            case 6:
            case 7:
                return "[0, Grid Height/Width] (Distance to walls)";
            case 8:
                return "[0, 4] (Number of safe moves available)";
            case 9:
                return "[0.0, 3.0] (Previous move direction)";
            case 10:
                return "[0, max(distance to center)] (Distance to grid center)";
            case 11:
                return "[0, max(distance to opponent)] (Distance to opponent)";
            case 12:
                return "[0, max(distance to enemy trail)] (Distance to enemy trail)";
            default:
                return "[Unknown]";
        }
    }
    
    
    

    
    private double getPreviousMove(GameState gameState, int agentNumber) {
        if (agentNumber == 1) {
            return gameState.getAgent1PreviousMove();
        } else {
            return gameState.getAgent2PreviousMove();
        }
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
                return Double.MAX_VALUE;
        }
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
            System.err.println("Crossover attempted between incompatible types. Returning a clone.");
            return this.clone();
        }
        DecisionNode otherNode = (DecisionNode) other;
    
        Node newLeft = random.nextDouble() < GPParameters.CROSSOVER_RATE ? this.left.crossover(otherNode.left) : this.left.clone();
        Node newRight = random.nextDouble() < GPParameters.CROSSOVER_RATE ? this.right.crossover(otherNode.right) : this.right.clone();
        double newThreshold = random.nextBoolean() ? this.threshold : otherNode.threshold;
    
        return new DecisionNode(
                random.nextBoolean() ? this.decisionFeature : otherNode.decisionFeature,
                newThreshold,
                newLeft,
                newRight,
                random
        );
    }
    

    @Override
    public Node clone() {
        Node clonedLeft = this.left.clone();
        Node clonedRight = this.right.clone();
        return new DecisionNode(this.decisionFeature, this.threshold, clonedLeft, clonedRight, random);
    }

    
    @Override
    public void mutate() {
        if (random.nextDouble() < GPParameters.MUTATION_RATE) {
            // Swap left and right subtrees
            Node temp = left;
            left = right;
            right = temp;
        } else {
            // Mutate the decision feature and threshold
            decisionFeature = random.nextInt(NUM_FEATURES);
            threshold = random.nextDouble();
        }
    
        // Mutate child nodes
        left.mutate();
        right.mutate();
        this.depth = Math.max(left.getMaxDepth(), right.getMaxDepth()) + 1;
    }
    
    
    
    
    @Override
    public String toString() {
        String featureStr;
        switch (decisionFeature) {
            case 0: featureStr = "DistToWallUp"; break;
            case 1: featureStr = "DistToWallDown"; break;
            case 2: featureStr = "DistToWallLeft"; break;
            case 3: featureStr = "DistToWallRight"; break;
            case 4: featureStr = "DistToOpponent"; break;
            case 5: featureStr = "DistToEnemyTrail"; break;
            case 6: featureStr = "NumberOfSafeMoves"; break;
            case 7: featureStr = "DistToCenter"; break;
            default: featureStr = "UnknownFeature"; break;
        }
        
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
