package com.example.trongp;

import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import java.awt.Color;
import org.jfree.data.xy.XYDataItem;


import javax.swing.JFrame;

public class FitnessPlotter {
    private static final XYSeries fitnessSeriesRed = new XYSeries("Red Agent Fitness");
    private static final XYSeries fitnessSeriesBlue = new XYSeries("Blue Agent Fitness");
    private static final XYSeries regressionSeriesRed = new XYSeries("Regression Line Red");
    private static final XYSeries regressionSeriesBlue = new XYSeries("Regression Line Blue");
    private static final XYSeriesCollection dataset = new XYSeriesCollection();
    private static final JFreeChart chart;
    private static final ChartPanel chartPanel;
    private static final JFrame frame;
    private static final XYSeries averageFitnessSeries = new XYSeries("Average Fitness");
    private static final XYSeries maxFitnessSeries = new XYSeries("Max Fitness");
    private static final XYSeries varianceSeries = new XYSeries("Fitness Variance");


    static {
        dataset.addSeries(fitnessSeriesRed);
        dataset.addSeries(fitnessSeriesBlue);
        dataset.addSeries(regressionSeriesRed);
        dataset.addSeries(regressionSeriesBlue);
        dataset.addSeries(averageFitnessSeries);
        dataset.addSeries(maxFitnessSeries);
        dataset.addSeries(varianceSeries);

        chart = ChartFactory.createXYLineChart(
                "Fitness Over Generations",
                "Generation",
                "Fitness",
                dataset
        );
        XYPlot plot = chart.getXYPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED); // Red Agent Fitness line
        renderer.setSeriesPaint(1, Color.BLUE); // Blue Agent Fitness line
        renderer.setSeriesPaint(2, Color.BLACK); // Red Regression line
        renderer.setSeriesPaint(3, Color.GRAY); // Blue Regression line
        plot.setRenderer(renderer);

        chartPanel = new ChartPanel(chart);
        frame = new JFrame("Fitness Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void updatePlot(int generation, double fitnessRed, double fitnessBlue) {
        fitnessSeriesRed.add(generation, fitnessRed);
        fitnessSeriesBlue.add(generation, fitnessBlue);
        updateRegressionLine(fitnessSeriesRed, regressionSeriesRed);
        updateRegressionLine(fitnessSeriesBlue, regressionSeriesBlue);

        System.out.println("Plot Update: Generation " + generation + " Red Fitness = " + fitnessRed + ", Blue Fitness = " + fitnessBlue);
        frame.revalidate();
        frame.repaint();
    }

    private static void updateRegressionLine(XYSeries originalSeries, XYSeries regressionSeries) {
        regressionSeries.clear();
        if (originalSeries.getItemCount() < 2) {
            System.out.println("Insufficient data to calculate regression line (require at least 2 points).");
            return;
        }
        @SuppressWarnings("unchecked")
        double[][] regressionData = (double[][]) originalSeries.getItems().stream()
            .map(item -> (XYDataItem) item)
            .map(item -> new double[]{((XYDataItem) item).getXValue(), ((XYDataItem) item).getYValue()})
            .toArray(double[][]::new);
        try {
            double[] coefficients = Regression.getOLSRegression(regressionData);
            double b = coefficients[0]; // intercept
            double m = coefficients[1]; // slope
            for (int i = 0; i <= originalSeries.getMaxX(); i++) {
                regressionSeries.add(i, m * i + b);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error calculating regression line: " + e.getMessage());
        }
    }
    public static void updatePopulationStats(int generation, double avgFitness, double maxFitness, double fitnessVariance) {
        averageFitnessSeries.add(generation, avgFitness);
        maxFitnessSeries.add(generation, maxFitness);
        varianceSeries.add(generation, fitnessVariance);
    }
}
