package com.example.trongp.GP;
public class GPParameters {
    public static final int POPULATION_SIZE = 100;
    public static final int phase1MaxDepth = 7;
    public static final int phase2MaxDepth = 10; 

    public static final double MUTATION_RATE = 0.01;
    public static final int TOURNAMENT_SIZE =5;
    public static final int SEED = 1231;
    public static final int GAMES_TO_PLAY = 1;
    public static final int GENERATIONS = 10;
    public static final double CROSSOVER_RATE = 0.8;
    public static final int CELL_SIZE = 20;
    public static final int GRID_SIZE = 8;
    
    public static final double SOLO_TRAIL_WEIGHT = 10.0;       // Increase to promote more movement and exploration
    public static final double WIN_WEIGHT = 20.0;              // Increased to emphasize winning importance
    public static final double TRAIL_WEIGHT = 5.0;             // Reduced for better balancing against other metrics
    public static final double elitismRate = 0.2;
    public static final double EXPLORATION_WEIGHT = 10.0;      // Increased to further emphasize exploration
    public static final double REVISIT_PENALTY_WEIGHT = 10.0; // Increased to strongly discourage revisiting
    public static final double FULL_GRID_BONUS = 1000.0;       // Bonus for covering the entire grid
    public static final int SOLO_GRID_SIZE = 5;                // Grid size for solo mode
    
    public static final double LOSS_PENALTY = 0.0;           // Reduced to reduce discouragement from losing
    public static final double DRAW_WEIGHT = 5.0;              // Reduced as draws are less desirable
    public static final int PHASE1_GENERATIONS = 5;
    public static final int SOLO_MAX_STEPS = 40;              // Increased to allow for more exploration
    public static final int Final_games = 5;
}
