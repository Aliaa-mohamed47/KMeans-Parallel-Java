/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataSetLoader {
    private List<Point> points;
    private int numPoints;

    public DataSetLoader(int numPoints) {
        this.numPoints = numPoints;
        points = new ArrayList<>();
    }

    public List<Point> generate2DData() {
        double[][] centers = {{2,2}, {8,3}, {5,8}};
        int pointsPerCluster = numPoints / centers.length;
        Random rand = new Random();

        for (double[] center : centers) {
            double cx = center[0], cy = center[1];
            for (int i=0; i<pointsPerCluster; i++) {
                double x = cx + rand.nextGaussian();
                double y = cy + rand.nextGaussian();
                points.add(new Point(x, y));
            }
        }
        return points;
    }

    public void saveToCSV(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("x,y,clusterId\n");
            for (Point p : points) {
                writer.append(p.getX() + "," + p.getY() + "," + p.getClusterId() + "\n");
            }
            System.out.println("Data saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Point> loadFromCSV(String filename) {
        List<Point> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double x = Double.parseDouble(values[0]);
                double y = Double.parseDouble(values[1]);
                data.add(new Point(x, y));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}