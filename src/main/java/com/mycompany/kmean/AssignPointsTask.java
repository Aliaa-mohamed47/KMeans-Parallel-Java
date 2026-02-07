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
import java.util.concurrent.RecursiveAction;

public class AssignPointsTask extends RecursiveAction {
    
    private static final int THRESHOLD = 1000;
    
    private List<Point> points;
    private List<Cluster> clusters;
    private int start;
    private int end;
    
    public AssignPointsTask(List<Point> points, List<Cluster> clusters, 
                                    int start, int end) {
        this.points = points;
        this.clusters = clusters;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected void compute() {
        int size = end - start;
        
        if (size <= THRESHOLD) {
            assignPointsBatched();
        } else {
            int mid = (start + end) / 2;
            
            AssignPointsTask left = new AssignPointsTask(
                points, clusters, start, mid
            );
            AssignPointsTask right = new AssignPointsTask(
                points, clusters, mid, end
            );
            
            invokeAll(left, right);
        }
    }
    
    private void assignPointsBatched() {
        List<List<Point>> localAssignments = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            localAssignments.add(new ArrayList<>());
        }
        
        for (int i = start; i < end; i++) {
            Point p = points.get(i);
            
            int bestClusterIndex = -1;
            double minDist = Double.MAX_VALUE;
            
            for (int j = 0; j < clusters.size(); j++) {
                double dist = p.distance(clusters.get(j).getCenter());
                if (dist < minDist) {
                    minDist = dist;
                    bestClusterIndex = j;
                }
            }
            
            localAssignments.get(bestClusterIndex).add(p);
            p.setClusterId(bestClusterIndex);
        }
        
        for (int i = 0; i < clusters.size(); i++) {
            if (!localAssignments.get(i).isEmpty()) {
                Cluster cluster = clusters.get(i);
                synchronized (cluster) {
                    for (Point p : localAssignments.get(i)) {
                        cluster.addPoint(p);
                    }
                }
            }
        }
    }
}