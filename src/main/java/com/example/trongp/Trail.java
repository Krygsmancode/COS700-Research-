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
}
