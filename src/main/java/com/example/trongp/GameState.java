package com.example.trongp;

import com.example.trongp.GP.Agent;
//import random 
import java.util.Random;

public class GameState {
    private int[][] grid;
    private int agent1X, agent1Y, agent2X, agent2Y;
    private boolean gameOver;
    private Trail agent1Trail;
    private Trail agent2Trail;
    private boolean agent1Wins;
    private boolean agent2Wins;


    public GameState(int width, int height) {
        this.grid = new int[width][height];
        this.gameOver = false;
        this.agent1Trail = new Trail();
        this.agent2Trail = new Trail();

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

        agent1Trail.addPoint(agent1X, agent1Y);
        agent2Trail.addPoint(agent2X, agent2Y);
    }

    public void update(int agent1Move, int agent2Move) {
        if (!gameOver) {
            moveAgent(agent1Move, 1);
            moveAgent(agent2Move, 2);
            checkGameOver();
        }
    }

    private void moveAgent(int move, int agentNumber) {
        int x = (agentNumber == 1) ? agent1X : agent2X;
        int y = (agentNumber == 1) ? agent1Y : agent2Y;

        switch (move) {
            case 0: y--; break; // Up
            case 1: y++; break; // Down
            case 2: x--; break; // Left
            case 3: x++; break; // Right
        }

        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length || grid[x][y] != 0) {
            gameOver = true;
            if (agentNumber == 1) {
                agent1Wins = false;
                agent2Wins = true;
            } else {
                agent1Wins = true;
                agent2Wins = false;
            }
          //  System.out.println("Collision detected! Agent " + agentNumber + " has collided.");
        } else {
            if (agentNumber == 1) {
                agent1X = x;
                agent1Y = y;
            } else {
                agent2X = x;
                agent2Y = y;
            }
            grid[x][y] = agentNumber;
        }
    }

    private void checkGameOver() {
        if (gameOver) {
     //       System.out.println("Game over detected!");
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean didAgentWin(Agent agent) {
        return (agent.getNumber() == 1 && agent1Wins) || (agent.getNumber() == 2 && agent2Wins);
    }

    public int calculateTrailLength(Agent agent) {
        return agent.getNumber() == 1 ? agent1Trail.getTrailLength() : agent2Trail.getTrailLength();
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return grid.length;
    }

    public int getHeight() {
        return grid[0].length;
    }

    public double getAgent1Y() {
        return agent1Y;
    }

    public double getAgent2X() {
        return agent2X;
    }

    public double getAgent1X() {
        return agent1X;
    }

    public double getAgent2Y() {
        return agent2Y;
    }

    public int getAgentMove(int opponentNumber) {
        return opponentNumber == 1 ? agent1Trail.getLastMove() : agent2Trail.getLastMove();
    }

    public void renderGrid() {
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                System.out.print(grid[x][y] + " ");
            }
            System.out.println();
        }
    }

    public int getAgent1Direction() {
        return agent1Trail.getLastMove();
    }

    public int getAgent2Direction() {
        return agent2Trail.getLastMove();
    }
    
}
