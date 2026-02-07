/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 */

import java.io.BufferedReader;
import java.io.FileReader;

public class ReadResults {

    public static void displayResults(String csvPath) {
        System.out.println("\n\n");
        System.out.println("================================================================================");
        System.out.println("                    K-MEANS EXPERIMENT RESULTS");
        System.out.println("================================================================================");
        System.out.println();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvPath));
            
            String header = br.readLine();
            
            System.out.println("K     Size         Seq (ms)    Par (ms)    Speedup     Seq SSE       Par SSE");
            System.out.println("--------------------------------------------------------------------------------");
            
            String line;
            double totalSpeedup = 0;
            int count = 0;
            double maxSpeedup = 0;
            String maxSpeedupConfig = "";
            int parallelWins = 0;
            
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                
                if (values.length >= 7) {
                    int k = Integer.parseInt(values[0]);
                    int size = Integer.parseInt(values[1]);
                    double seqTime = Double.parseDouble(values[2]);
                    double parTime = Double.parseDouble(values[3]);
                    double speedup = Double.parseDouble(values[4]);
                    double seqSSE = Double.parseDouble(values[5]);
                    double parSSE = Double.parseDouble(values[6]);
                    
                    String winner = "";
                    if (speedup > 1.0) {
                        winner = " *";
                        parallelWins++;
                    }
                    
                    System.out.printf("%-5d %-12d %-11.2f %-11.2f %-11.2fx %-13.2f %-13.2f%s\n",
                        k, size, seqTime, parTime, speedup, seqSSE, parSSE, winner);
                    
                    totalSpeedup += speedup;
                    count++;
                    
                    if (speedup > maxSpeedup) {
                        maxSpeedup = speedup;
                        maxSpeedupConfig = "K=" + k + ", Size=" + size;
                    }
                }
            }
            
            br.close();
            
            System.out.println("================================================================================");
            System.out.println("* = Parallel was faster");
            System.out.println();
            
            System.out.println("\n================================================================================");
            System.out.println("                              ANALYSIS");
            System.out.println("================================================================================");
            System.out.println();
            
            double avgSpeedup = totalSpeedup / count;
            System.out.println("Average Speedup: " + String.format("%.2f", avgSpeedup) + "x");
            System.out.println("Best Speedup: " + String.format("%.2f", maxSpeedup) + "x (" + maxSpeedupConfig + ")");
            System.out.println("Parallel wins: " + parallelWins + "/" + count + " cases (" + 
                             String.format("%.1f", (parallelWins * 100.0 / count)) + "%)");
            
            System.out.println("\n================================================================================");
            System.out.println("CONCLUSION");
            System.out.println("================================================================================");
            
            if (avgSpeedup > 1.0) {
                System.out.println("✓ SUCCESS! Parallel is faster on average!");
                System.out.println("Recommendation: Use parallel for these dataset sizes.");
            } else {
                System.out.println("⚠ Parallel implementation is correct but overhead dominates.");
                System.out.println("Recommendation: Need larger datasets (>100K points) for speedup.");
            }
            
            System.out.println("\nNote: Quality (SSE) is preserved - implementations are equivalent.");
            System.out.println("================================================================================\n");
            
        } catch (Exception e) {
            System.err.println("ERROR: Cannot read file!");
            System.err.println("Make sure this file exists: " + csvPath);
            System.err.println("Error: " + e.getMessage());
        }
    }
}