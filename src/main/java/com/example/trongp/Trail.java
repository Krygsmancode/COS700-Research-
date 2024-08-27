package com.example.trongp;

import java.util.ArrayList;
import java.util.List;

public class Trail {
    private List<Point> trailPoints;

    public Trail() {
        this.trailPoints = new ArrayList<>();
    }

    public void addPoint(int x, int y) {
        trailPoints.add(new Point(x, y));
    }

    public int getTrailLength() {
        return trailPoints.size();
    }

    public boolean containsPoint(int x, int y) {
        return trailPoints.contains(new Point(x, y));
    }

    public int getLastMove() {
        if (trailPoints.size() < 2) {
            return -1;
        }
        Point lastPoint = trailPoints.get(trailPoints.size() - 1);
        Point secondLastPoint = trailPoints.get(trailPoints.size() - 2);

        if (lastPoint.getX() == secondLastPoint.getX()) {
            return lastPoint.getY() > secondLastPoint.getY() ? 1 : 0;
        } else {
            return lastPoint.getX() > secondLastPoint.getX() ? 3 : 2;
        }
    }
}
