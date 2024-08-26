package com.example.trongp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.trongp.GP.Agent;

public class GameState {
   private int[][] grid;
    private int agent1X, agent1Y, agent2X, agent2Y;
    private boolean gameOver;
    private List<Point> agent1Trail;
    private List<Point> agent2Trail;


 public GameState(int width, int height) {
        this.grid = new int[width][height];
        this.gameOver = false;
        this.agent1Trail = new ArrayList<>();
        this.agent2Trail = new ArrayList<>();

        Random rand = new Random();
        do {
            agent1X = rand.nextInt(width);
            agent1Y = rand.nextInt(height);
        } while (grid[agent1X][agent1Y] != 0);

        do {
            agent2X = rand.nextInt(width);
            agent2Y = rand.nextInt(height);
        } while ((agent2X == agent1X && agent2Y == agent1Y) || grid[agent2X][agent2Y] != 0);

        grid[agent1X][agent1Y] = 1;
        grid[agent2X][agent2Y] = 2;

        agent1Trail.add(new Point(agent1X, agent1Y));
        agent2Trail.add(new Point(agent2X, agent2Y));
    }

    public void initializeGrid(int rows, int columns) {
        grid = new int[rows][columns]; // Initializes all cells to 0
    }

    public void update(int agent1Move, int agent2Move) {
        moveAgent(agent1Move, 1);
        moveAgent(agent2Move, 2);
        checkGameOver();
    }

    private void moveAgent(int move, int agentNumber) {
        int x = (agentNumber == 1) ? agent1X : agent2X;
        int y = (agentNumber == 1) ? agent1Y : agent2Y;

        switch (move) {
            case 0: // Up
                y = (y > 0) ? y - 1 : y;
                break;
            case 1: // Down
                y = (y < grid[0].length - 1) ? y + 1 : y;
                break;
            case 2: // Left
                x = (x > 0) ? x - 1 : x;
                break;
            case 3: // Right
                x = (x < grid.length - 1) ? x + 1 : x;
                break;
            default:
                throw new IllegalStateException("Unexpected move value: " + move);
        }

        if (grid[x][y] != 0) {
            gameOver = true; // End game if collision detected
        } else {
            if (agentNumber == 1) {
                agent1X = x;
                agent1Y = y;
                agent1Trail.add(new Point(agent1X, agent1Y));
            } else {
                agent2X = x;
                agent2Y = y;
                agent2Trail.add(new Point(agent2X, agent2Y));
            }
            grid[x][y] = agentNumber;
        }
    }

    private void checkGameOver() {
        boolean collisionDetected = (agent1X == agent2X && agent1Y == agent2Y)
                || isCollision(agent1X, agent1Y)
                || isCollision(agent2X, agent2Y);

        if (collisionDetected) {
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }
    public void renderGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------------");
    }

    public boolean didAgentWin(Agent agent) {
        return agent.getNumber() == 1 ? agent1X == agent2X && agent1Y == agent2Y : agent2X == agent1X && agent2Y == agent1Y;
    }

    public int calculateTrailLength(Agent agent) {
        return agent.getNumber() == 1 ? agent1Trail.size() : agent2Trail.size();
    }

    public int getWidth() {
        return grid.length;
    }
    

    public int getHeight() {
        return grid[0].length;
    }

    public int getAgent1X() {
        return agent1X;
    }

    public int getAgent1Y() {
        return agent1Y;
    }

    public int getAgent2X() {
        return agent2X;
    }

    public int getAgent2Y() {
        return agent2Y;
    }

    public int[][] getGrid() {
        return grid;
    }

    private boolean isCollision(int x, int y) {
        return grid[x][y] != 0;
    }

    
}
