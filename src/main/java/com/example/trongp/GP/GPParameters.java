package com.example.trongp.GP;

public class GPParameters {
    public static final int POPULATION_SIZE = 100;  // Increased for more diversity
    public static final int MAX_DEPTH = 6;  // Kept the same, adjust based on strategy complexity
    public static final double MUTATION_RATE = 0.02;  // Slightly increased to encourage diversity
    public static final int TOURNAMENT_SIZE = 5;  // Increased to enhance selection pressure
    public static final int SEED = 42;

    public static final int GAMES_TO_PLAY = 1;
    public static final int GENERATIONS = 200;  // Increased for more evolutionary time
    public static final double CROSSOVER_RATE = 0.8;
    public static final int CELL_SIZE = 20;
    public static final int GRID_SIZE = 5;  // Increased to add more complexity to the environment
    public static final double WIN_WEIGHT = 10;  // Adjusted to emphasize winning more
    public static final double TRAIL_WEIGHT = 1;  // Adjusted for balance after normalization
}
