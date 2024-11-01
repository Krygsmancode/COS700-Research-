package com.example.trongp;
import java.util.*;
import java.awt.Point;


import com.example.trongp.GP.Agent;
import com.example.trongp.GP.GPParameters;
import com.example.trongp.GP.Strategy;


public class GameState {
    private int[][] grid;
    private int agent1X, agent1Y, agent2X, agent2Y;
    private boolean gameOver;
    private Trail agent1Trail;
    private Trail agent2Trail;
    private boolean agent1Wins;
    private Random random = new Random(GPParameters.SEED);
    private boolean agent2Wins;
    private boolean soloMode = false;
    private Map<Integer, List<Point>> agentTrails;

    public GameState(int width, int height, boolean soloMode) {
        this.grid = new int[height][width]; // Corrected grid initialization
        this.gameOver = false;
        this.agent1Trail = new Trail();
        this.agent2Trail = new Trail();
        this.soloMode = soloMode;

        // Initialize agentTrails
        agentTrails = new HashMap<>();
        agentTrails.put(1, new ArrayList<>());

        if (!soloMode) {
            // Initialize trail for Agent 2 in competitive mode
            agentTrails.put(2, new ArrayList<>());
        }

        if (soloMode) {
            initSoloAgent();
        } else {
            initAgents();
        }
    }
    
    private void initSoloAgent() {
        agent1X = getWidth() / 2; // Start at the center of the grid
        agent1Y = getHeight() / 2;
        grid[agent1Y][agent1X] = 1;
        agent1Trail.addPoint(new Point(agent1X, agent1Y));
    }
    
    
    
    
    private void initAgents() {
        // Fixed starting positions for both agents
        // Agent 1 starts at top-left corner
        agent1X = 0;
        agent1Y = 0;
        grid[agent1Y][agent1X] = 1;
        agentTrails.get(1).add(new Point(agent1X, agent1Y));
    
        // Agent 2 starts at bottom-right corner
        agent2X = getWidth() - 1;
        agent2Y = getHeight() - 1;
        grid[agent2Y][agent2X] = 2;
        agentTrails.get(2).add(new Point(agent2X, agent2Y));
    }
    
    
    
    
    public Point getNextPosition(int x, int y, int move) {
        switch (move) {
            case 0: // Up
                y--;
                break;
            case 1: // Down
                y++;
                break;
            case 2: // Left
                x--;
                break;
            case 3: // Right
                x++;
                break;
            default:
                // Invalid move, return null
                System.out.println("Invalid move received: " + move);
                return null;
        }
        return new Point(x, y);
    }
    
    
    
    public Point getCurrentPosition(int agentNumber) {
        if (agentNumber == 1) {
            return new Point(agent1X, agent1Y);
        } else if (agentNumber == 2 && !soloMode) {
            return new Point(agent2X, agent2Y);
        } else {
            // Return null if agentNumber is not recognized or in solo mode for agent 2
            System.out.println("Invalid agent number or agent not available: " + agentNumber);
            return null;
        }
    }
    private boolean checkCollision(Point pos) {
        int x = pos.x;
        int y = pos.y;
        return x < 0 || x >= getWidth() || y < 0 || y >= getHeight() || grid[y][x] != 0;
    }
    
    
    public void update(int agent1Move, int agent2Move) {
        Random random = new Random(); // Random object for making decisions
    
        if (gameOver) {
            System.out.println("Game is over. No update required.");
            displayGridCoverage(); // Display grid coverage when the game is over
            return; // Exit the method if the game is over
        }
    
        // Log current positions
        System.out.println("Agent 1 Position: (" + agent1X + "," + agent1Y + ")");
        if (!soloMode) {
            System.out.println("Agent 2 Position: (" + agent2X + "," + agent2Y + ")");
        }
    
        if (agent1Move == -1) {
            System.out.println("Agent 1 has no valid moves. Game over.");
            gameOver = true;
            agent1Wins = false;
            agent2Wins = !soloMode;
            displayGridCoverage();
            return;
        }
    
        // Agent 1's turn
        Point agent1NextPosition = getNextPosition(agent1X, agent1Y, agent1Move);
        if (agent1NextPosition == null) {
            System.out.println("Agent 1 made an invalid move. Game over.");
            gameOver = true;
            agent1Wins = false;
            agent2Wins = !soloMode;
            displayGridCoverage();
            return;
        }
        boolean agent1Collision = checkCollision(agent1NextPosition);
    
        if (agent1Collision) {
            gameOver = true;
            agent1Wins = false;
            agent2Wins = !soloMode; // In solo mode, agent2Wins should be false
            System.out.println("Agent 1 collided.");
            displayGridCoverage(); // Display grid coverage when the game is over
            return;
        } else {
            updateAgentPosition(1, agent1NextPosition);
            agentTrails.get(1).add(new Point(agent1X, agent1Y));
        }
    
        if (!soloMode) {
            // Agent 2's turn
            if (agent2Move == -1) {
                System.out.println("Agent 2 has no valid moves. Game over.");
                gameOver = true;
                agent1Wins = true;
                agent2Wins = false;
                displayGridCoverage();
                return;
            }
    
            Point agent2NextPosition = getNextPosition(agent2X, agent2Y, agent2Move);
            boolean agent2Collision = checkCollision(agent2NextPosition);
    
            System.out.println("Agent 2 intends to move to: (" + agent2NextPosition.x + "," + agent2NextPosition.y + ")");
            System.out.println("Agent 2 Collision: " + agent2Collision);
    
            if (agent1NextPosition.equals(agent2NextPosition)) {
                gameOver = true;
                if (random.nextBoolean()) { // Randomly choose the winning agent
                    agent1Wins = true;
                    agent2Wins = false;
                    System.out.println("Agents collided with each other. Random winner: Agent 1.");
                } else {
                    agent1Wins = false;
                    agent2Wins = true;
                    System.out.println("Agents collided with each other. Random winner: Agent 2.");
                }
                displayGridCoverage(); // Display grid coverage when the game is over
                return;
            }
    
            if (agent2Collision) {
                gameOver = true;
                agent1Wins = true;
                agent2Wins = false;
                System.out.println("Agent 2 collided.");
                displayGridCoverage(); // Display grid coverage when the game is over
            } else {
                updateAgentPosition(2, agent2NextPosition);
                agentTrails.get(2).add(new Point(agent2X, agent2Y));
            }
        }
    
        // Display the updated grid state after each move
        printGrid();
        displayGridCoverage();
    }
    

 
    public void displayGridCoverage() {
        int totalCells = getWidth() * getHeight();
        // help me calculate the number of covered cells
        int coveredCells = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] != 0) {
                    coveredCells++;
                }
            }
        }
        double coveragePercentage = (coveredCells / (double) totalCells) * 100;
        System.out.printf("Agent covered %d out of %d cells (%.2f%% of the grid).\n", coveredCells, totalCells, coveragePercentage);
    }
    
    public void printGrid() {
        System.out.println("Current Grid State:");
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                int cellValue = grid[y][x];
                char displayChar;
                if (cellValue == 0) {
                    displayChar = '.'; // Open space
                } else if (cellValue == 1) {
                    // Check if this cell is the current position of agent 1
                    if (x == agent1X && y == agent1Y) {
                        displayChar = 'A'; // Current position of agent 1
                    } else {
                        displayChar = 'R'; // Trail left by agent 1
                    }
                } else if (cellValue == 2) {
                    // Check if this cell is the current position of agent 2
                    if (x == agent2X && y == agent2Y) {
                        displayChar = 'B'; // Current position of agent 2
                    } else {
                        displayChar = 'S'; // Trail left by agent 2
                    }
                } else {
                    displayChar = '?'; // Unexpected value
                }
                System.out.print(displayChar + " ");
            }
            System.out.println();
        }
    }
    

    private void updateAgentPosition(int agentNumber, Point pos) {
        int x = pos.x;
        int y = pos.y;
      //  System.out.println("Updating Agent " + agentNumber + " Position to (" + x + ", " + y + ")");
      //  System.out.println("Agent " + agentNumber + " Trail Length: " + (agentTrails.get(agentNumber).size() + 1));
        grid[y][x] = agentNumber;
        agentTrails.get(agentNumber).add(new Point(x, y));
    
        if (agentNumber == 1) {
            agent1X = x;
            agent1Y = y;
        } else if (agentNumber == 2) {
            agent2X = x;
            agent2Y = y;
        }
    }
    
    
    

       public List<Point> getAgentTrail(int agentNumber) {
        return agentTrails.get(agentNumber);
    }

 

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean didAgentWin(Agent agent) {
        return (agent.getNumber() == 1 && agent1Wins) || (agent.getNumber() == 2 && agent2Wins);
    }
    

    public int calculateTrailLength(Agent agent) {
        int agentNumber = agent.getNumber();
        int trailLength = 0;
        int[][] grid = getGrid();
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == agentNumber) {
                    trailLength++;
                }
            }
        }
        return trailLength;
    }
    

    public int[][] getGrid() {
        return grid;
    }
    public int getWidth() {
        return grid[0].length; // Number of columns
    }
    
    public int getHeight() {
        return grid.length; // Number of rows
    }
    


    public double getAgent1Y() {
        return agent1Y;
    }

    public double getAgent2X() {
        return soloMode ? -1 : agent2X; // Return -1 or any indicator of non-availability
    }
    
    public double getAgent2Y() {
        return soloMode ? -1 : agent2Y;
    }

    public double getAgent1X() {
        return agent1X;
    }


    public int getAgent1Direction() {
        return agent1Trail.getLastMove();
    }
    public int getAgent2Direction() {
        return soloMode ? -1 : agent2Trail.getLastMove();
    }
    public boolean isPositionSafe(int x, int y) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
            return grid[y][x] == 0;
        }
        return false;
    }
    
    
    
    
    
    

    public boolean hasCollisionOccurred() {
        if (soloMode) {
            if (agent1X < 0 || agent1X >= getWidth() || agent1Y < 0 || agent1Y >= getHeight()) {
                return true;
            }
            // Check if the agent is moving into a non-empty cell
            if (grid[agent1Y][agent1X] != 1) {
                return true;
            }
        }
        return false;
    }
    

    public double getAgent1PreviousMove() {
        return agent1Trail.getLastMove();
    }

    public double getAgent2PreviousMove() {
        return agent2Trail.getLastMove();
    }

    public boolean isSoloMode() {
        return soloMode;
        
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void reset() {
        grid = new int[getHeight()][getWidth()];
        gameOver = false;
        agent1Trail.clear();
        agent2Trail.clear();
        agent1Wins = false;
        agent2Wins = false;
        agent1X = 0;
        agent1Y = 0;
        agent2X = 0;
        agent2Y = 0;
        agentTrails.get(1).clear();
        agentTrails.get(2).clear();
        if (soloMode) {
            initSoloAgent();
        } else {
            initAgents();
        }
    }
    public boolean isFullGridCovered() {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                // Assuming 0 indicates an empty cell (no agent presence)
                if (grid[y][x] == 0) {
                    return false; // If any cell is empty, the grid is not fully covered
                }
            }
        }
        return true; // All cells are covered if no empty cell was found
    }

 
    
}
