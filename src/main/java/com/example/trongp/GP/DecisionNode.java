package com.example.trongp.GP;

import com.example.trongp.GameState;

public class DecisionNode extends Node {
    private Node left;
    private Node right;
    private int decisionFeature;

    public DecisionNode(int decisionFeature, Node left, Node right) {
        super(Math.max(left.getMaxDepth(), right.getMaxDepth()) + 1);
        this.decisionFeature = decisionFeature;
        this.left = left;
        this.right = right;
    }

    @Override
    public Node clone() {
        DecisionNode cloned = (DecisionNode) super.clone();
        cloned.left = this.left.clone(); // Ensure deep clone of the left child
        cloned.right = this.right.clone(); // Ensure deep clone of the right child
        return cloned;
    }
    


    public DecisionNode(int maxDepth) {
        this(random.nextInt(6), NodeFactory.createRandomNode(maxDepth - 1, false), NodeFactory.createRandomNode(maxDepth - 1, false));
    }
    

    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        double[] features = extractFeatures(gameState, agentNumber);
        double threshold = 0.3 + (0.4 * random.nextDouble());
        return (features[decisionFeature] > threshold) ? left.evaluate(gameState, agentNumber) : right.evaluate(gameState, agentNumber);
    }
    
    
    


    private double[] extractFeatures(GameState gameState, int agentNumber) {
        double[] features = new double[10];
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
        features[6] = freePathLength(gameState, agentX, agentY, "UP");
    features[7] = freePathLength(gameState, agentX, agentY, "DOWN");
    features[8] = freePathLength(gameState, agentX, agentY, "LEFT");
    features[9] = freePathLength(gameState, agentX, agentY, "RIGHT");
        return features;
    }
    private double freePathLength(GameState gameState, double agentX, double agentY, String direction) {
        int length = 0;
        int x = (int) agentX;
        int y = (int) agentY;
        while (true) {
            switch (direction) {
                case "UP": y--; break;
                case "DOWN": y++; break;
                case "LEFT": x--; break;
                case "RIGHT": x++; break;
            }
            if (x < 0 || x >= gameState.getWidth() || y < 0 || y >= gameState.getHeight() || gameState.getGrid()[x][y] != 0) {
                break;
            }
            length++;
        }
        return length;
    }

    private double distanceToWall(GameState gameState, double agentX, double agentY, String direction) {
        int gridX = (int) (agentX / GPParameters.CELL_SIZE);
        int gridY = (int) (agentY / GPParameters.CELL_SIZE);

        switch (direction) {
            case "UP":
                return gridY;
            case "DOWN":
                return gameState.getHeight() - gridY - 1;
            case "LEFT":
                return gridX;
            case "RIGHT":
                return gameState.getWidth() - gridX - 1;
            default:
                return Double.MAX_VALUE;
        }
    }

    private double distanceToOpponent(double agentX, double agentY, double opponentX, double opponentY) {
        return Math.sqrt(Math.pow(agentX - opponentX, 2) + Math.pow(agentY - opponentY, 2));
    }

    private double distanceToEnemyTrail(GameState gameState, double agentX, double agentY, int agentNumber) {
        int gridX = (int) (agentX / GPParameters.CELL_SIZE);
        int gridY = (int) (agentY / GPParameters.CELL_SIZE);
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < gameState.getWidth(); i++) {
            for (int j = 0; j < gameState.getHeight(); j++) {
                if (gameState.getGrid()[i][j] != 0 && gameState.getGrid()[i][j] != agentNumber) {
                    int distance = Math.abs(gridX - i) + Math.abs(gridY - j);
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
        }

        return minDistance;
    }

    @Override
    public Node crossover(Node other) {
        if (other instanceof DecisionNode) {
            DecisionNode otherNode = (DecisionNode) other;
            Node newLeft = this.left.crossover(otherNode.left);
            Node newRight = this.right.crossover(otherNode.right);
    
            // Check if resulting nodes exceed max depth
            if (newLeft.getMaxDepth() + 1 <= GPParameters.MAX_DEPTH &&
                newRight.getMaxDepth() + 1 <= GPParameters.MAX_DEPTH) {
                return new DecisionNode(this.decisionFeature, newLeft, newRight);
            } else {
                // If the resulting tree is too deep, return one of the original parents
                return random.nextBoolean() ? this : other;
            }
        } else {
            return this;  // If the other node is not a DecisionNode, just return this node
        }
    }
    

    @Override
    public void mutate() {
        if (random.nextDouble() < GPParameters.MUTATION_RATE) {
            decisionFeature = random.nextInt(6);
        }
    
        if (left.getMaxDepth() < GPParameters.MAX_DEPTH - 1) {
            left.mutate();
        }
        
        if (right.getMaxDepth() < GPParameters.MAX_DEPTH - 1) {
            right.mutate();
        }
    }
    

    @Override
    public String toString() {
        return "DecisionNode(" + decisionFeature + ")[" + left.toString() + ", " + right.toString() + "]";
    }

    
}
