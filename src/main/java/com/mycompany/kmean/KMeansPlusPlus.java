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
import java.util.Random;

public class KMeansPlusPlus {

    public static List<Point> initializeCentroids(List<Point> points, int k) {
        List<Point> centroids = new ArrayList<>();
        Random rand = new Random();
        
        Point firstCentroid = points.get(rand.nextInt(points.size()));
        centroids.add(new Point(firstCentroid.getX(), firstCentroid.getY()));
        
        for (int i = 1; i < k; i++) {
            double[] distances = new double[points.size()];
            double totalDistance = 0;
            
            for (int j = 0; j < points.size(); j++) {
                Point p = points.get(j);
                double minDist = Double.MAX_VALUE;
                
                for (Point centroid : centroids) {
                    double dist = p.distance(centroid);
                    minDist = Math.min(minDist, dist);
                }
                
                distances[j] = minDist * minDist;
                totalDistance += distances[j];
            }
            
            double randomValue = rand.nextDouble() * totalDistance;
            double sum = 0;
            
            for (int j = 0; j < points.size(); j++) {
                sum += distances[j];
                if (sum >= randomValue) {
                    Point p = points.get(j);
                    centroids.add(new Point(p.getX(), p.getY()));
                    break;
                }
            }
        }
        
        System.out.println("✓ K-Means++ initialization completed");
        return centroids;
    }
}
