/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 */

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private Point center;
    private List<Point> points;

    public Cluster(Point center) {
        this.center = center;
        this.points = new ArrayList<>();
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public void clearPoints() {
        points.clear();
    }

    //    FIX: Proper Empty Cluster Handling
    public void updateCenter(List<Point> allPoints) {

        if (points.isEmpty()) {
            // إعادة تهيئة المركز بنقطة عشوائية من الداتا
            int idx = (int)(Math.random() * allPoints.size());
            Point randomCenter = allPoints.get(idx);

            System.out.println(
                "⚠ Empty cluster detected → Reinitialized to random point: (" +
                String.format("%.2f", randomCenter.getX()) + ", " +
                String.format("%.2f", randomCenter.getY()) + ")"
            );

            this.center = new Point(randomCenter.getX(), randomCenter.getY());
            return;
        }

        double sumX = 0;
        double sumY = 0;

        for (Point p : points) {
            sumX += p.getX();
            sumY += p.getY();
        }

        double newX = sumX / points.size();
        double newY = sumY / points.size();

        this.center = new Point(newX, newY);
    }
}
