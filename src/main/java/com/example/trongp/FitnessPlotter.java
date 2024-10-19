package com.example.trongp;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import java.awt.Color;
import javax.swing.JFrame;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataItem;

public class FitnessPlotter {
    // Phase 1 variables
    private XYSeries phase1Series;
    private XYSeriesCollection datasetPhase1;
    private JFreeChart chartPhase1;
    private ChartPanel chartPanelPhase1;
    private JFrame framePhase1;
    private XYSeries phase1VarianceSeries;


    // Phase 2 variables
    private XYSeries redFitnessSeries;
    private XYSeries blueFitnessSeries;
    private XYSeries redMeanFitnessSeries;
    private XYSeries blueMeanFitnessSeries;
    private XYSeries redVarianceSeries;
    private XYSeries blueVarianceSeries;
    private XYSeries redMeanFitnessRegression;
    private XYSeries blueMeanFitnessRegression;
    private XYSeries redVarianceRegression;
    private XYSeries blueVarianceRegression;
    private XYSeriesCollection datasetPhase2;
    private JFreeChart chartPhase2;
    private ChartPanel chartPanelPhase2;
    private JFrame framePhase2;

    public FitnessPlotter() {
        // Phase 1 initialization
        phase1Series = new XYSeries("Best Fitness");
        phase1VarianceSeries = new XYSeries("Fitness Variance");
        
        datasetPhase1 = new XYSeriesCollection();
        datasetPhase1.addSeries(phase1Series);
        datasetPhase1.addSeries(phase1VarianceSeries);
    
        // Create the chart after the dataset is fully prepared
        chartPhase1 = ChartFactory.createXYLineChart(
                "Phase 1 Fitness Over Generations",
                "Generation",
                "Fitness",
                datasetPhase1
        );
    
        XYPlot plotPhase1 = chartPhase1.getXYPlot();
        NumberAxis rangeAxisPhase1 = (NumberAxis) plotPhase1.getRangeAxis();
        rangeAxisPhase1.setAutoRangeIncludesZero(false);
    
        XYLineAndShapeRenderer rendererPhase1 = new XYLineAndShapeRenderer();
        rendererPhase1.setSeriesPaint(0, Color.GREEN); // Best Fitness
        rendererPhase1.setSeriesPaint(1, Color.ORANGE); // Fitness Variance
        plotPhase1.setRenderer(rendererPhase1);
    
        chartPanelPhase1 = new ChartPanel(chartPhase1);
        framePhase1 = new JFrame("Phase 1 Fitness Plot");
        framePhase1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePhase1.getContentPane().add(chartPanelPhase1);
        framePhase1.pack();
        framePhase1.setVisible(true);
    
        // Phase 2 initialization
        redFitnessSeries = new XYSeries("Red Agent Best Fitness");
        blueFitnessSeries = new XYSeries("Blue Agent Best Fitness");
        redMeanFitnessSeries = new XYSeries("Red Agent Mean Fitness");
        blueMeanFitnessSeries = new XYSeries("Blue Agent Mean Fitness");
        redVarianceSeries = new XYSeries("Red Agent Fitness Variance");
        blueVarianceSeries = new XYSeries("Blue Agent Fitness Variance");
        redMeanFitnessRegression = new XYSeries("Red Mean Fitness Regression");
        blueMeanFitnessRegression = new XYSeries("Blue Mean Fitness Regression");
        redVarianceRegression = new XYSeries("Red Variance Regression");
        blueVarianceRegression = new XYSeries("Blue Variance Regression");
    
        datasetPhase2 = new XYSeriesCollection();
        datasetPhase2.addSeries(redFitnessSeries);
        datasetPhase2.addSeries(blueFitnessSeries);
        datasetPhase2.addSeries(redMeanFitnessSeries);
        datasetPhase2.addSeries(blueMeanFitnessSeries);
        datasetPhase2.addSeries(redVarianceSeries);
        datasetPhase2.addSeries(blueVarianceSeries);
        datasetPhase2.addSeries(redMeanFitnessRegression);
        datasetPhase2.addSeries(blueMeanFitnessRegression);
        datasetPhase2.addSeries(redVarianceRegression);
        datasetPhase2.addSeries(blueVarianceRegression);
        
        chartPhase2 = ChartFactory.createXYLineChart(
                "Phase 2 Fitness and Variance Over Generations",
                "Generation",
                "Fitness / Variance",
                datasetPhase2
        );
    
        XYPlot plotPhase2 = chartPhase2.getXYPlot();
        NumberAxis rangeAxisPhase2 = (NumberAxis) plotPhase2.getRangeAxis();
        rangeAxisPhase2.setAutoRangeIncludesZero(false);
    
        XYLineAndShapeRenderer rendererPhase2 = new XYLineAndShapeRenderer();
        rendererPhase2.setSeriesPaint(0, Color.RED);    // Red Agent Best Fitness
        rendererPhase2.setSeriesPaint(1, Color.BLUE);   // Blue Agent Best Fitness
        rendererPhase2.setSeriesPaint(2, Color.PINK);   // Red Mean Fitness
        rendererPhase2.setSeriesPaint(3, Color.CYAN);   // Blue Mean Fitness
        rendererPhase2.setSeriesPaint(4, Color.MAGENTA); // Red Variance
        rendererPhase2.setSeriesPaint(5, Color.DARK_GRAY); // Blue Variance
        plotPhase2.setRenderer(rendererPhase2);
    
        chartPanelPhase2 = new ChartPanel(chartPhase2);
        framePhase2 = new JFrame("Phase 2 Fitness Plot");
        framePhase2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePhase2.getContentPane().add(chartPanelPhase2);
        framePhase2.pack();
        framePhase2.setVisible(true);
    }
    

    // Method to update Phase 1 plot
    public void updatePhase1Plot(int generation, double bestFitness, double meanFitness, double variance) {
        phase1Series.add(generation, bestFitness);
        phase1VarianceSeries.add(generation, variance);
        framePhase1.revalidate();
        framePhase1.repaint();
    }
    
    public void updatePhase2Plot(int generation, double bestRedFitness, double bestBlueFitness,
    double avgRedFitness, double avgBlueFitness,
    double varianceRed, double varianceBlue) {
redFitnessSeries.add(generation, bestRedFitness);
blueFitnessSeries.add(generation, bestBlueFitness);

redMeanFitnessSeries.add(generation, avgRedFitness);
blueMeanFitnessSeries.add(generation, avgBlueFitness);

redVarianceSeries.add(generation, varianceRed);
blueVarianceSeries.add(generation, varianceBlue);

// Update regression lines if necessary

framePhase2.revalidate();
framePhase2.repaint();
}

    private void updateRegressionLine(XYSeries dataSeries, XYSeries regressionSeries) {
        regressionSeries.clear();
        if (dataSeries.getItemCount() < 2) {
            return; // Not enough data for regression
        }

        double[][] data = new double[dataSeries.getItemCount()][2];
        for (int i = 0; i < dataSeries.getItemCount(); i++) {
            XYDataItem item = dataSeries.getDataItem(i);
            data[i][0] = item.getXValue();
            data[i][1] = item.getYValue();
        }

        double[] coefficients = Regression.getOLSRegression(data);
        double intercept = coefficients[0];
        double slope = coefficients[1];

        double startX = dataSeries.getMinX();
        double endX = dataSeries.getMaxX();

        regressionSeries.add(startX, slope * startX + intercept);
        regressionSeries.add(endX, slope * endX + intercept);
    }
}
