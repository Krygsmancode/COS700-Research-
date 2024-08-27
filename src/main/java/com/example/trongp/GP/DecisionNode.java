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
        this.decisionFeature = new Random().nextInt(6); // Assuming you have 6 different features
        // Recursively create left and right child nodes with reduced depth
        this.left = NodeFactory.createRandomNode(maxDepth - 1);
        this.right = NodeFactory.createRandomNode(maxDepth - 1);
    }

    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        double[] features = extractFeatures(gameState, agentNumber);
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
        //create function 
        return Math.sqrt(Math.pow(agentX - opponentX, 2) + Math.pow(agentY - opponentY, 2));

    }

    private double distanceToEnemyTrail(GameState gameState, double agentX, double agentY, int agentNumber) {
       
        double opponentX = agentNumber == 1 ? gameState.getAgent2X() : gameState.getAgent1X();
        double opponentY = agentNumber == 1 ? gameState.getAgent2Y() : gameState.getAgent1Y();
        int opponentNumber = agentNumber == 1 ? 2 : 1;
        int opponentMove = gameState.getAgentMove(opponentNumber);
        double newOpponentX = opponentX;
        double newOpponentY = opponentY;
        switch (opponentMove) {
            case 0:
                newOpponentY -= 1;
                break;
            case 1:
                newOpponentY += 1;
                break;
            case 2:
                newOpponentX -= 1;
                break;
            case 3:
                newOpponentX += 1;
                break;
        }
        return Math.sqrt(Math.pow(agentX - newOpponentX, 2) + Math.pow(agentY - newOpponentY, 2));
    }

    private double distanceToWall(GameState gameState, double agentX, double agentY, String string) {
        int[][] grid = gameState.getGrid();
        int width = gameState.getWidth();
        int height = gameState.getHeight();
        switch (string) {
            case "UP":
                for (int i = (int) agentY; i >= 0; i--) {
                    if (grid[(int) agentX][i] != 0) {
                        return agentY - i;
                    }
                }
                return agentY;
            case "DOWN":
                for (int i = (int) agentY; i < height; i++) {
                    if (grid[(int) agentX][i] != 0) {
                        return i - agentY;
                    }
                }
                return height - agentY;
            case "LEFT":
                for (int i = (int) agentX; i >= 0; i--) {
                    if (grid[i][(int) agentY] != 0) {
                        return agentX - i;
                    }
                }
                return agentX;
            case "RIGHT":
                for (int i = (int) agentX; i < width; i++) {
                    if (grid[i][(int) agentY] != 0) {
                        return i - agentX;
                    }
                }
                return width - agentX;
            default:
                return 0;
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

    @Override
    public void mutate() {
        if (random.nextDouble() < GPParameters.MUTATION_RATE) {
            decisionFeature = random.nextInt(6); // 6 features
        }
        left.mutate();
        right.mutate();
    }

    @Override
    public String toString() {
        return "DecisionNode(" + decisionFeature + ")[" + left.toString() + ", " + right.toString() + "]";
    }
}
