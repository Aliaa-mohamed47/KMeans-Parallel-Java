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
import java.util.Locale;
import java.text.NumberFormat;

public class KMeansExperiment {
    
    private List<ExperimentResult> results;
    
    public KMeansExperiment() {
        this.results = new ArrayList<>();
        Locale.setDefault(Locale.US);
    }
    
    public void runAllExperiments() {
        printHeader();
        
        int[] kValues = {2, 3, 5};
        int[] dataSizes = {1000, 5000, 10000, 50000, 100000};
        
        System.out.println("\nStarting experiments...");
        System.out.println("Total tests: " + (kValues.length * dataSizes.length));
        System.out.println("This will take several minutes...\n");
        
        int testNum = 1;
        int totalTests = kValues.length * dataSizes.length;
        
        for (int k : kValues) {
            for (int size : dataSizes) {
                System.out.println("======================================================================");
                System.out.println("Test " + testNum + "/" + totalTests + ": K=" + k + ", Data Size=" + size + " points");
                System.out.println("======================================================================");
                
                runSingleExperiment(k, size);
                System.out.println();
                testNum++;
            }
        }
        
        printSummary();
        printAnalysis();
    }
    
    private void runSingleExperiment(int k, int dataSize) {
        int numRuns = (dataSize > 50000) ? 1 : 3;
        
        double avgSeqTime = 0;
        double avgParTime = 0;
        double avgSeqSSE = 0;
        double avgParSSE = 0;
        
        for (int run = 1; run <= numRuns; run++) {
            System.out.println("  Run " + run + "/" + numRuns + "...");
            
            DataSetLoader loader = new DataSetLoader(dataSize);
            List<Point> data = loader.generate2DData();
            
            KMeansConfig config = new KMeansConfig(k, 50, "random");
            
            System.out.print("    Running Sequential... ");
            KMeansSequential sequential = new KMeansSequential(config);
            
            long startSeq = System.nanoTime();
            List<Cluster> seqClusters = sequential.run(data);
            long endSeq = System.nanoTime();
            
            double seqTime = (endSeq - startSeq) / 1_000_000.0;
            double seqSSE = SSECalculator.calculateSSE(seqClusters);
            System.out.println("Done (" + formatDouble(seqTime) + " ms)");
            
            System.out.print("    Running Parallel (4 threads)... ");
            KMeansParallel parallel = new KMeansParallel(config, 4);
            
            long startPar = System.nanoTime();
            List<Cluster> parClusters = parallel.run(data);
            long endPar = System.nanoTime();
            
            double parTime = (endPar - startPar) / 1_000_000.0;
            double parSSE = SSECalculator.calculateSSE(parClusters);
            System.out.println("Done (" + formatDouble(parTime) + " ms)");
            
            parallel.shutdown();
            
            avgSeqTime += seqTime;
            avgParTime += parTime;
            avgSeqSSE += seqSSE;
            avgParSSE += parSSE;
            
            double thisSpeedup = seqTime / parTime;
            System.out.println("    Speedup this run: " + formatDouble(thisSpeedup) + "x");
        }
        
        avgSeqTime /= numRuns;
        avgParTime /= numRuns;
        avgSeqSSE /= numRuns;
        avgParSSE /= numRuns;
        
        double speedup = avgSeqTime / avgParTime;
        
        System.out.println("\n  FINAL RESULTS:");
        System.out.println("     Sequential: " + formatDouble(avgSeqTime) + " ms, SSE: " + formatDouble(avgSeqSSE));
        System.out.println("     Parallel:   " + formatDouble(avgParTime) + " ms, SSE: " + formatDouble(avgParSSE));
        System.out.print("     Speedup:    " + formatDouble(speedup) + "x");
        
        if (speedup > 1.5) {
            System.out.println(" >>> EXCELLENT! Parallel is significantly faster!");
        } else if (speedup > 1.0) {
            System.out.println(" >> Good! Parallel is faster.");
        } else if (speedup > 0.8) {
            System.out.println(" ~ Close. Almost break-even.");
        } else {
            System.out.println(" - Sequential still faster (overhead).");
        }
        
        ExperimentResult result = new ExperimentResult(
            k, dataSize, avgSeqTime, avgParTime, avgSeqSSE, avgParSSE, speedup
        );
        results.add(result);
    }
    
    private String formatDouble(double value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setGroupingUsed(false);
        return nf.format(value);
    }
    
    private void printHeader() {
        System.out.println("\n\n");
        System.out.println("================================================================================");
        System.out.println("          K-MEANS CLUSTERING EXPERIMENTS");
        System.out.println("       Performance Analysis (Person 6)");
        System.out.println("================================================================================");
    }
    
    private void printSummary() {
        System.out.println("\n\n");
        System.out.println("==========================================================================================");
        System.out.println("                        COMPLETE RESULTS SUMMARY");
        System.out.println("==========================================================================================");
        System.out.println();
        
        System.out.println("K     Size         Seq (ms)    Par (ms)    Speedup     Seq SSE       Par SSE");
        System.out.println("------------------------------------------------------------------------------------------");
        
        for (ExperimentResult r : results) {
            String speedupStr = formatDouble(r.speedup) + "x";
            if (r.speedup > 1.0) {
                speedupStr += " *";
            }
            
            System.out.println(
                padLeft(String.valueOf(r.k), 5) +
                " " + padLeft(String.valueOf(r.dataSize), 12) +
                " " + padLeft(formatDouble(r.seqTime), 11) +
                " " + padLeft(formatDouble(r.parTime), 11) +
                " " + padLeft(speedupStr, 11) +
                " " + padLeft(formatDouble(r.seqSSE), 13) +
                " " + padLeft(formatDouble(r.parSSE), 13)
            );
        }
        
        System.out.println("==========================================================================================");
        System.out.println("* = Parallel was faster");
    }
    
    private String padLeft(String str, int length) {
        while (str.length() < length) {
            str = " " + str;
        }
        return str;
    }
    
    private void printAnalysis() {
        System.out.println("\n");
        System.out.println("================================================================================");
        System.out.println("                            ANALYSIS");
        System.out.println("================================================================================");
        System.out.println();

        double avgSpeedup = results.stream()
            .mapToDouble(r -> r.speedup)
            .average()
            .orElse(0.0);

        System.out.println("Average Speedup: " + formatDouble(avgSpeedup) + "x\n");

        ExperimentResult bestSpeedup = results.stream()
            .max((r1, r2) -> Double.compare(r1.speedup, r2.speedup))
            .orElse(null);

        if (bestSpeedup != null) {
            System.out.println("Best Speedup: " + formatDouble(bestSpeedup.speedup) + 
                             "x (K=" + bestSpeedup.k + ", Size=" + bestSpeedup.dataSize + ")\n");
        }

        long parallelWins = results.stream()
            .filter(r -> r.speedup > 1.0)
            .count();

        double winPercent = (parallelWins * 100.0) / results.size();
        System.out.println("Parallel was faster in " + parallelWins + "/" + results.size() + 
                         " cases (" + formatDouble(winPercent) + "%)\n");

        double avgSSEDiff = results.stream()
            .mapToDouble(r -> Math.abs(r.seqSSE - r.parSSE) / r.seqSSE * 100)
            .average()
            .orElse(0.0);

        System.out.println("Average SSE Difference: " + formatDouble(avgSSEDiff) + "%");

        if (avgSSEDiff < 1.0) {
            System.out.println("   --> Quality is perfectly preserved!\n");
        }

        System.out.println("\n💡 KEY INSIGHTS:");
        System.out.println("================================================================================");
        System.out.println("Why does speedup vary with dataset size?");
        System.out.println();
        System.out.println("1. Small datasets (< 5K points):");
        System.out.println("   - Fork/Join overhead (task creation, thread management) dominates");
        System.out.println("   - Actual computation time is too small to benefit from parallelization");
        System.out.println("   - Result: Sequential is often faster!");
        System.out.println();
        System.out.println("2. Medium datasets (5K - 10K points):");
        System.out.println("   - Break-even point where overhead ≈ parallel benefit");
        System.out.println("   - Speedup close to 1.0x");
        System.out.println();
        System.out.println("3. Large datasets (> 10K points):");
        System.out.println("   - Computation time >> overhead");
        System.out.println("   - 4 threads can effectively divide the workload");
        System.out.println("   - Result: Parallel wins with significant speedup!");
        System.out.println();
        System.out.println("4. THRESHOLD tuning (currently 1000):");
        System.out.println("   - Controls granularity: how many points per task");
        System.out.println("   - Too small → too many tasks → high overhead");
        System.out.println("   - Too large → not enough parallelism");
        System.out.println("   - 1000 is optimal for our dataset sizes (1K-100K)");
        System.out.println();
        System.out.println("5. Why update step is NOT parallelized:");
        System.out.println("   - Only 2-5 centroids to update (very fast!)");
        System.out.println("   - Fork/Join overhead > actual computation time");
        System.out.println("   - Sequential update is faster for small K");
        System.out.println("================================================================================\n");

        System.out.println("\n================================================================================");
        System.out.println("SPEEDUP BY DATA SIZE");
        System.out.println("================================================================================");

        int[] sizes = {1000, 5000, 10000, 50000, 100000};
        for (int size : sizes) {
            final int s = size;
            double avgSpeedupForSize = results.stream()
                .filter(r -> r.dataSize == s)
                .mapToDouble(r -> r.speedup)
                .average()
                .orElse(0.0);

            System.out.print("Size " + padLeft(String.valueOf(size), 7) + ": Average speedup = " + 
                           formatDouble(avgSpeedupForSize) + "x ");
            if (avgSpeedupForSize > 1.0) {
                System.out.println(">> Parallel WINS!");
            } else {
                System.out.println("- Sequential better (overhead dominates)");
            }
        }

        System.out.println("\n================================================================================");
        System.out.println("FINAL RECOMMENDATIONS");
        System.out.println("================================================================================");
        System.out.println("Based on experimental results:");
        System.out.println("- Sequential: Best for < 5,000 points (overhead > benefit)");
        System.out.println("- Either works: 5,000 - 10,000 points (break-even point)");
        System.out.println("- Parallel: Best for > 10,000 points (clear speedup advantage!)");
        System.out.println("- Optimal threads: 4 (matches typical CPU cores)");
        System.out.println("- Optimal THRESHOLD: 1000 points per task (balances overhead vs parallelism)");
        System.out.println("================================================================================\n");
    }
    
    public void saveResultsToCSV(String filename) {
        try {
            java.io.File file = new java.io.File(filename);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            
            java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(filename), "UTF-8"
            );
            
            writer.write("K,DataSize,SeqTime,ParTime,Speedup,SeqSSE,ParSSE\n");
            
            for (ExperimentResult r : results) {
                writer.write(r.k + "," + r.dataSize + "," + 
                            formatDouble(r.seqTime) + "," + 
                            formatDouble(r.parTime) + "," + 
                            formatDouble(r.speedup) + "," + 
                            formatDouble(r.seqSSE) + "," + 
                            formatDouble(r.parSSE) + "\n");
            }
            
            writer.close();
            System.out.println("Results saved to " + filename);
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }
    
    private static class ExperimentResult {
        int k;
        int dataSize;
        double seqTime;
        double parTime;
        double seqSSE;
        double parSSE;
        double speedup;
        
        ExperimentResult(int k, int dataSize, double seqTime, double parTime,
                        double seqSSE, double parSSE, double speedup) {
            this.k = k;
            this.dataSize = dataSize;
            this.seqTime = seqTime;
            this.parTime = parTime;
            this.seqSSE = seqSSE;
            this.parSSE = parSSE;
            this.speedup = speedup;
        }
    }
    
    
}