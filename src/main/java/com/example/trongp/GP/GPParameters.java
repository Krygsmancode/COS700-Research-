package com.example.trongp.GP;

public class GPParameters {
    public static final int POPULATION_SIZE = 10;
    public static final int MAX_DEPTH = 5;
    public static final double MUTATION_RATE = 0.13;
    public static final int TOURNAMENT_SIZE = 3;

    public static final int GAMES_TO_PLAY = 10;
    public static final int GENERATIONS = 100;
    public static final double CROSSOVER_RATE = 0.8;
    public static final int CELL_SIZE = 20;
    public static final int GRID_SIZE = 10;
    public static final double WIN_WEIGHT = 10; // High weight for winning
    public static final double TRAIL_LENGTH_WEIGHT = 0.1; // Low weight for trail length
}
