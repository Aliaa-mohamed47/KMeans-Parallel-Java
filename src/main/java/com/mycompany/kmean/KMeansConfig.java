/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 * 
 */
public class KMeansConfig {
    private int k;
    private int maxIterations;
    private String initMethod;

    public KMeansConfig(int k, int maxIterations, String initMethod) {
        this.k = k;
        this.maxIterations = maxIterations;
        this.initMethod = initMethod;
    }

    public int getK() { return k; }
    public void setK(int k) { this.k = k; }

    public int getMaxIterations() { return maxIterations; }
    public void setMaxIterations(int maxIterations) { this.maxIterations = maxIterations; }

    public String getInitMethod() { return initMethod; }
    public void setInitMethod(String initMethod) { this.initMethod = initMethod; }

    @Override
    public String toString() {
        return "KMeansConfig { k=" + k + ", maxIterations=" + maxIterations + ", initMethod='" + initMethod + "' }";
    }
}