/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 */
import java.util.List;

public class KMeansWithRestarts {
    
    private KMeansConfig config;
    private int numRestarts;
    private boolean useParallel;
   
    public KMeansWithRestarts(KMeansConfig config, int numRestarts, boolean useParallel) {
        this.config = config;
        this.numRestarts = numRestarts;
        this.useParallel = useParallel;
    }
    
    public List<Cluster> runWithRestarts(List<Point> points) {
        List<Cluster> bestClusters = null;
        double bestSSE = Double.MAX_VALUE;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("K-MEANS WITH " + numRestarts + " RANDOM RESTARTS");
        System.out.println("=".repeat(60));
        
        for (int restart = 0; restart < numRestarts; restart++) {
            System.out.println("\nRestart " + (restart + 1) + "/" + numRestarts + "...");
            
            List<Cluster> clusters;
            if (useParallel) {
                KMeansParallel kmeans = new KMeansParallel(config, 4);
                clusters = kmeans.run(points);
                kmeans.shutdown();
            } else {
                KMeansSequential kmeans = new KMeansSequential(config);
                clusters = kmeans.run(points);
            }
            
            double sse = SSECalculator.calculateSSE(clusters);
            System.out.println("  SSE: " + String.format("%.2f", sse));
            
            if (sse < bestSSE) {
                bestSSE = sse;
                bestClusters = clusters;
                System.out.println("  ✓ New best result!");
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("BEST RESULT:");
        System.out.println("  Best SSE: " + String.format("%.2f", bestSSE));
        System.out.println("=".repeat(60) + "\n");
        
        return bestClusters;
    }
 
    public static void compareRestarts(List<Point> points, int k) {
        KMeansConfig config = new KMeansConfig(k, 50, "random");
        
        System.out.println("\n>> SINGLE RUN:");
        KMeansSequential single = new KMeansSequential(config);
        List<Cluster> singleResult = single.run(points);
        double singleSSE = SSECalculator.calculateSSE(singleResult);
        System.out.println("SSE: " + String.format("%.2f", singleSSE));
        
        System.out.println("\n>> MULTIPLE RESTARTS:");
        KMeansWithRestarts multi = new KMeansWithRestarts(config, 5, false);
        List<Cluster> multiResult = multi.runWithRestarts(points);
        double multiSSE = SSECalculator.calculateSSE(multiResult);
        
        double improvement = ((singleSSE - multiSSE) / singleSSE) * 100;
        System.out.println("\n" + "=".repeat(60));
        System.out.println("COMPARISON:");
        System.out.println("  Single run SSE:  " + String.format("%.2f", singleSSE));
        System.out.println("  5 restarts SSE:  " + String.format("%.2f", multiSSE));
        System.out.println("  Improvement:     " + String.format("%.1f%%", improvement));
        System.out.println("=".repeat(60) + "\n");
    }
}