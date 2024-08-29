package com.example.trongp;

import javax.swing.*;

import com.example.trongp.GP.GPParameters;

import java.awt.*;

public class GameRenderer extends JPanel {
    private static final int CELL_SIZE = GPParameters.CELL_SIZE;
    private int[][] grid;

    public GameRenderer(int[][] grid) {
        this.grid = grid;
        setPreferredSize(new Dimension(grid.length * CELL_SIZE, grid[0].length * CELL_SIZE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                if (grid[x][y] == 1) {
                    g.setColor(Color.RED); // Red agent trail
                } else if (grid[x][y] == 2) {
                    g.setColor(Color.BLUE); // Blue agent trail
                } else {
                    g.setColor(Color.WHITE); // Empty space
                }
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public void updateGrid(int[][] newGrid) {
        this.grid = newGrid;
        repaint();
    }
}
