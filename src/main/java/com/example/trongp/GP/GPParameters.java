package com.example.trongp.GP;

public class GPParameters {
    public static final int POPULATION_SIZE = 200;
    public static final int MAX_DEPTH = 10;
    public static final double MUTATION_RATE = 0.1;
    public static final int TOURNAMENT_SIZE = 3;
    public static final int SEED = 42;

    public static final int GAMES_TO_PLAY = 5;
    public static final int GENERATIONS = 2000;
    public static final double CROSSOVER_RATE = 0.8;
    public static final int CELL_SIZE = 20;
    public static final int GRID_SIZE = 15;
    public static final double WIN_WEIGHT = 2.0; // High weight for winning
    public static final double TRAIL_WEIGHT = 0.2; // Low weight for trail length
}
