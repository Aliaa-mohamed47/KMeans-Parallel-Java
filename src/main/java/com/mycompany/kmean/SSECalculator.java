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

public class SSECalculator {
    
    public static double calculateSSE(List<Cluster> clusters) {
        double totalSSE = 0.0;
        
        for (Cluster cluster : clusters) {
            totalSSE += calculateClusterSSE(cluster);
        }
        
        return totalSSE;
    }
    
    public static double calculateClusterSSE(Cluster cluster) {
        double sse = 0.0;
        Point center = cluster.getCenter();
        List<Point> points = cluster.getPoints();
        
        for (Point point : points) {
            double distance = point.distance(center);
            sse += distance * distance; // مربع المسافة
        }
        
        return sse;
    }

    public static void printDetailedSSE(List<Cluster> clusters) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 SSE ANALYSIS (Clustering Quality)");
        System.out.println("=".repeat(60));
        
        double totalSSE = 0.0;
        
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            double clusterSSE = calculateClusterSSE(cluster);
            totalSSE += clusterSSE;
            
            System.out.printf("  Cluster %d: SSE = %.2f (points: %d)\n", 
                i, clusterSSE, cluster.getPoints().size());
        }
        
        System.out.println("-".repeat(60));
        System.out.printf("  📈 TOTAL SSE: %.2f\n", totalSSE);
        System.out.println("=".repeat(60) + "\n");
    }
    
    public static void compareSSE(List<Cluster> seqClusters, List<Cluster> parClusters) {
        double seqSSE = calculateSSE(seqClusters);
        double parSSE = calculateSSE(parClusters);
        double difference = Math.abs(seqSSE - parSSE);
        double percentDiff = (difference / seqSSE) * 100;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔍 SSE COMPARISON");
        System.out.println("=".repeat(60));
        System.out.printf("  Sequential SSE: %.2f\n", seqSSE);
        System.out.printf("  Parallel SSE:   %.2f\n", parSSE);
        System.out.printf("  Difference:     %.2f (%.3f%%)\n", difference, percentDiff);
        
        if (percentDiff < 0.1) {
            System.out.println("  ✅ Excellent! Quality is preserved!");
        } else if (percentDiff < 1.0) {
            System.out.println("  ✓ Good. Minor difference acceptable.");
        } else {
            System.out.println("  ⚠ Warning: Significant quality difference!");
        }
        
        System.out.println("=".repeat(60) + "\n");
    }
}
