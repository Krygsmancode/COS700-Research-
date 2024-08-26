package com.example.trongp;

import com.example.trongp.GP.EvolutionEngine;
import com.example.trongp.GP.GPParameters;
import com.example.trongp.GP.Agent;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.application.Platform;

public class TronGameApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Canvas gameCanvas = new Canvas(600, 600);
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        Group root = new Group(gameCanvas);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Tron Game Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Run evolutionary algorithm in a separate thread to avoid blocking JavaFX thread
        new Thread(() -> {
            EvolutionEngine evolutionEngine = new EvolutionEngine(GPParameters.POPULATION_SIZE, GPParameters.GENERATIONS);
            evolutionEngine.runEvolution();

            Agent bestRedAgent = evolutionEngine.getBestRedAgent();
            Agent bestBlueAgent = evolutionEngine.getBestBlueAgent();

            // Use JavaFX runLater to update UI elements on the JavaFX thread
            Platform.runLater(() -> {
                TronController tronController = new TronController(gc);
                tronController.runVisualizationGame(bestRedAgent, bestBlueAgent);
            });

        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
