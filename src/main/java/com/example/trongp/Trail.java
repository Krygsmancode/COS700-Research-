package com.example.trongp;

import java.util.ArrayList;
import java.util.List;

public class Trail {
    private List<Point> trailPoints;
    private int lastMove;

    public Trail() {
        this.trailPoints = new ArrayList<>();
        this.lastMove = 0; // Default direction (e.g., Up)
    }

    public void addPoint(int x, int y) {
        if (!trailPoints.isEmpty()) {
            Point lastPoint = trailPoints.get(trailPoints.size() - 1);
            if (x == lastPoint.getX()) {
                lastMove = y > lastPoint.getY() ? 1 : 0; // Down or Up
            } else if (y == lastPoint.getY()) {
                lastMove = x > lastPoint.getX() ? 3 : 2; // Right or Left
            }
        }
        trailPoints.add(new Point(x, y));
    }

    public int getTrailLength() {
        return trailPoints.size();
    }

    public boolean containsPoint(int x, int y) {
        return trailPoints.contains(new Point(x, y));
    }

    public int getLastMove() {
        return lastMove;
    }

    public void addPoint(java.awt.Point point) {
        addPoint(point.x, point.y);
       
    }

    public void clear() {
        trailPoints.clear();
    }
}
