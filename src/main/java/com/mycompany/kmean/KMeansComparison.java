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

public class KMeansComparison {


    public static void compareRuntime(
            List<Point> dataset,
            int k,
            int maxIterations
    ) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" SEQUENTIAL vs PARALLEL RUNTIME COMPARISON");
        System.out.println("=".repeat(60));
        System.out.println(" Dataset size: " + dataset.size() + " points");
        System.out.println(" Number of clusters (K): " + k);
        System.out.println(" Max iterations: " + maxIterations);
        System.out.println("=".repeat(60) + "\n");

        KMeansConfig config = new KMeansConfig(k, maxIterations, "random");

        System.out.println(" Running SEQUENTIAL K-Means...");
        KMeansSequential seqKMeans = new KMeansSequential(config);

        long startSeq = System.currentTimeMillis();
        List<Cluster> seqClusters = seqKMeans.run(dataset);
        long endSeq = System.currentTimeMillis();
        long seqTime = endSeq - startSeq;

        System.out.println(" Sequential completed in " + seqTime + " ms");


        System.out.println("\n Running PARALLEL K-Means...");
        KMeansParallel parKMeans = new KMeansParallel(config);

        long startPar = System.currentTimeMillis();
        List<Cluster> parClusters = parKMeans.run(dataset);
        long endPar = System.currentTimeMillis();
        long parTime = endPar - startPar;

        System.out.println(" Parallel completed in " + parTime + " ms");
        parKMeans.shutdown();

        System.out.println("\n" + "=".repeat(60));
        System.out.println(" PERFORMANCE RESULTS");
        System.out.println("=".repeat(60));

        double speedup = (double) seqTime / parTime;
        double timeSaved = ((double)(seqTime - parTime) / seqTime) * 100;

        System.out.println("  Sequential Time: " + seqTime + " ms");
        System.out.println(" Parallel Time:   " + parTime + " ms");
        System.out.println(" Speedup:         " + String.format("%.2fx", speedup));
        System.out.println(" Time Saved:      " + String.format("%.1f%%", timeSaved));

        if (speedup > 2.0) {
            System.out.println("\n Excellent! Parallelization is very effective.");
        } else if (speedup > 1.0) {
            System.out.println("\n Good speedup achieved.");
        } else {
            System.out.println("\n  Overhead dominates. Try larger dataset.");
        }

        System.out.println("=".repeat(60) + "\n");

    }

    public static void printClusterSizes(List<Cluster> clusters) {
        System.out.println("\n Cluster Sizes:");
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("  Cluster " + i + ": " +
                    clusters.get(i).getPoints().size() + " points");
        }
        System.out.println();
    }
}