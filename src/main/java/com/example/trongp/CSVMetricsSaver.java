package com.example.trongp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class CSVMetricsSaver implements AutoCloseable {
    private PrintWriter writer;

    public CSVMetricsSaver(String fileName) throws IOException {
        File file = new File(fileName);
        boolean fileExists = file.exists();
        
        writer = new PrintWriter(new FileWriter(file, true)); // Append mode

        if (!fileExists) {
            writeHeader();
        }
    }

    private void writeHeader() {
        writer.println("Seed,Trial,Red Win Rate,Red Loss Rate,Red Average Trail Length,Red Max Trail Length,"
                + "Red Min Trail Length,Red Trail Length Std Dev,Blue Win Rate,Blue Loss Rate,"
                + "Blue Average Trail Length,Blue Max Trail Length,Blue Min Trail Length,"
                + "Blue Trail Length Std Dev,Current Best Fitness,Overall Best Fitness");
    }

    public void saveMetrics(int seed, int trialNumber, double redWinRate, double redLossRate,
                            double redAvgTrail, double redMaxTrail, double redMinTrail, double redTrailStdDev,
                            double blueWinRate, double blueLossRate, double blueAvgTrail, double blueMaxTrail,
                            double blueMinTrail, double blueTrailStdDev, double currentBestFitness,
                            double overallBestFitness) {
        writer.printf(Locale.US, "%d,%d,%.2f%%,%.2f%%,%.2f,%.2f,%.2f,%.2f,"
                + "%.2f%%,%.2f%%,%.2f,%.2f,%.2f,%.2f,%.3f,%.3f%n",
                seed,
                trialNumber,
                redWinRate * 100, redLossRate * 100,
                redAvgTrail, redMaxTrail, redMinTrail, redTrailStdDev,
                blueWinRate * 100, blueLossRate * 100,
                blueAvgTrail, blueMaxTrail, blueMinTrail, blueTrailStdDev,
                currentBestFitness,
                overallBestFitness
        );
    }

    @Override
    public void close() {
        writer.close();
    }
}
