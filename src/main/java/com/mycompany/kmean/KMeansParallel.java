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
import java.util.concurrent.ForkJoinPool;

public class KMeansParallel {
    private KMeansConfig config;
    private List<Cluster> clusters;
    private ForkJoinPool pool;
    
    public KMeansParallel(KMeansConfig config) {
        this.config = config;
        this.clusters = new ArrayList<>();
        this.pool = new ForkJoinPool(4);
    }
    
    public KMeansParallel(KMeansConfig config, int parallelism) {
        this.config = config;
        this.clusters = new ArrayList<>();
        this.pool = new ForkJoinPool(parallelism);
    }
    
    public List<Cluster> run(List<Point> points) {
        initializeCentroids(points);
        
        boolean converged = false;
        int iteration = 0;
        
        System.out.println("Starting Parallel K-Means with " + 
                pool.getParallelism() + " threads");
        
        while (!converged && iteration < config.getMaxIterations()) {
            clearAllClusters();
            
            assignPointsParallel(points);
            
            List<Point> oldCenters = saveCurrentCenters();

            updateCenters(points);
            
            converged = checkConvergence(oldCenters);
            
            iteration++;
            
            if (iteration % 10 == 0) {
                System.out.println("  Iteration " + iteration + " completed");
            }
        }
        
        System.out.println("Converged after " + iteration + " iterations");
        return clusters;
    }
    
    public void initializeCentroids(List<Point> points) {
        clusters.clear();

        if (config.getInitMethod().equals("kmeans++")) {
            List<Point> centroids = KMeansPlusPlus.initializeCentroids(points, config.getK());
            for (Point c : centroids) {
                clusters.add(new Cluster(c));
            }
        } else {
            Random rand = new Random();
            for (int i = 0; i < config.getK(); i++) {
                Point p = points.get(rand.nextInt(points.size()));
                clusters.add(new Cluster(new Point(p.getX(), p.getY())));
            }
        }
    }
    
    private void clearAllClusters() {
        for (Cluster c : clusters) {
            c.clearPoints();
        }
    }
 
    private void assignPointsParallel(List<Point> points) {
        AssignPointsTask task = new AssignPointsTask(
                points,
                clusters,
                0,
                points.size()
        );
        pool.invoke(task);
    }
    
    public void updateCenters(List<Point> allPoints) {
        for (Cluster c : clusters) {
            c.updateCenter(allPoints);
        }
    }

    
    private List<Point> saveCurrentCenters() {
        List<Point> oldCenters = new ArrayList<>();
        for (Cluster c : clusters) {
            Point center = c.getCenter();
            oldCenters.add(new Point(center.getX(), center.getY()));
        }
        return oldCenters;
    }
 
    private boolean checkConvergence(List<Point> oldCenters) {
        double threshold = 0.001;
        
        for (int i = 0; i < clusters.size(); i++) {
            Point oldCenter = oldCenters.get(i);
            Point newCenter = clusters.get(i).getCenter();
            
            double movement = oldCenter.distance(newCenter);
            
            if (movement > threshold) {
                return false;
            }
        }
        
        return true;
    }
    
    public List<Cluster> getClusters() {
        return clusters;
    }
    
    public void shutdown() {
        pool.shutdown();
    }
}