package com.example.trongp.GP;
public class GPParameters {
    public static  int POPULATION_SIZE = 200;
    public static  int phase1MaxDepth = 2;
    public static  int phase2MaxDepth = 5;
    public static double MUTATION_RATE = 0.05;
    public static  int TOURNAMENT_SIZE =4;
    public static  double CROSSOVER_RATE = 0.9;
    public static  double PHASE2_MUTATION_RATE = 0.05; 


    public static int SEED = 0;
    public static final int GAMES_TO_PLAY = 5;
    public static final int GENERATIONS = 2000;
    public static final int CELL_SIZE = 20;
    public static final int GRID_SIZE = 6;
    static final int SIMILARITY_THRESHOLD = 10; // Adjust as needed

    
    public static final double SOLO_TRAIL_WEIGHT = 10.0;       // Increase to promote more movement and exploration
    public static final double WIN_WEIGHT = 20.0;              // Increased to emphasize winning importance
    public static final double TRAIL_WEIGHT = 5.0;             // Reduced for better balancing against other metrics
    public static final double elitismRate = 0.1;
    public static final double EXPLORATION_WEIGHT = 10.0;      // Increased to further emphasize exploration
    public static final double REVISIT_PENALTY_WEIGHT = 10.0; // Increased to strongly discourage revisiting
    public static final double FULL_GRID_BONUS = 1000.0;       // Bonus for covering the entire grid
    public static final int SOLO_GRID_SIZE = 6;                // Grid size for solo mode
    
    public static final double LOSS_PENALTY = 10.0;           // Reduced to reduce discouragement from losing
    public static final double DRAW_WEIGHT = 5.0;              // Reduced as draws are less desirable
    public static final int PHASE1_GENERATIONS = 150;
    public static final int SOLO_MAX_STEPS = 40;              // Increased to allow for more exploration
    public static final int SURVIVAL_BONUS = 10;


}
