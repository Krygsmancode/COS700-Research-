package com.example.trongp;

import com.example.trongp.GP.Agent;
import com.example.trongp.GameState;

import com.example.trongp.GP.GPParameters;


public class TronController {

    private GameState gameState;


    // public void initialize() {
    //     this.gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE);
    // }

    // public boolean runSingleGame(Agent agent1, Agent agent2, boolean render) {
    //     while (!gameState.isGameOver()) {
    //         int redMove = agent1.makeMove(gameState, 1);
    //         int blueMove = agent2.makeMove(gameState, 2);
    
    //         System.out.println("Agent 1 move: " + redMove);
    //         System.out.println("Agent 2 move: " + blueMove);
    
    //         gameState.update(redMove, blueMove);
    
    //         // If rendering is required, add code here for visualization
    //     }
    
    //     boolean agent1Won = gameState.didAgentWin(agent1);
    
    //     // Record the game result
      
    //     return agent1Won;
    // }
    
    

    // public void runSimulation(int generations) {
    //     for (int generation = 1; generation <= generations; generation++) {
    //         System.out.println("Generation " + generation + " in progress...");
            
    //         // Initialize game state and agents
    //         initialize();
        
            
    //         // Simulate a single game between the two agents
         
            
    //         System.out.println("-----------------------------------");
    //     }
    // }
    
    

    // public int getTrailLength(Agent agent) {
    //     return gameState.calculateTrailLength(agent);
    // }
}
