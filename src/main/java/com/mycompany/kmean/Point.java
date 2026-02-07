/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 */
public class Point {
    private double x;
    private double y;
    private int clusterId;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.clusterId = -1; 
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public int getClusterId() { return clusterId; }
    public void setClusterId(int clusterId) { this.clusterId = clusterId; }

    public double distance(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ") -> Cluster " + clusterId;
    }
}