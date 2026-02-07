/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 */
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class ClusterVisualization extends JFrame {
    
    private List<Cluster> clusters;
    private static final int PADDING = 60;
    private static final int POINT_SIZE = 6;
    private static final int CENTER_SIZE = 12;
    
    private static final Color[] CLUSTER_COLORS = {
        new Color(239, 68, 68),   // Red
        new Color(59, 130, 246),  // Blue
        new Color(34, 197, 94),   // Green
        new Color(249, 115, 22),  // Orange
        new Color(168, 85, 247),  // Purple
        new Color(236, 72, 153),  // Pink
        new Color(14, 165, 233),  // Cyan
        new Color(234, 179, 8),   // Yellow
        new Color(132, 204, 22),  // Lime
        new Color(244, 63, 94)    // Rose
    };
    
    public ClusterVisualization(List<Cluster> clusters) {
        this.clusters = clusters;
        initUI();
    }
    
    private void initUI() {
        setTitle("Cluster Visualization - " + clusters.size() + " Clusters");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setLayout(new BorderLayout());
        add(new VisualizationPanel(), BorderLayout.CENTER);
        add(createLegendPanel(), BorderLayout.EAST);
        add(createStatsPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(200, 0));
        
        JLabel title = new JLabel("Legend");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        
        for (int i = 0; i < clusters.size(); i++) {
            JPanel legendItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            legendItem.setOpaque(false);
            legendItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            
            JPanel colorBox = new JPanel();
            colorBox.setBackground(CLUSTER_COLORS[i % CLUSTER_COLORS.length]);
            colorBox.setPreferredSize(new Dimension(20, 20));
            colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            JLabel label = new JLabel("Cluster " + i);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            legendItem.add(colorBox);
            legendItem.add(label);
            panel.add(legendItem);
        }
        
        panel.add(Box.createVerticalStrut(20));
        
        JPanel centerItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        centerItem.setOpaque(false);
        centerItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JPanel centerBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);
                g2.fillOval(2, 2, 16, 16);
                g2.setColor(Color.WHITE);
                g2.drawOval(2, 2, 16, 16);
            }
        };
        centerBox.setPreferredSize(new Dimension(20, 20));
        centerBox.setOpaque(false);
        
        JLabel centerLabel = new JLabel("Centroid");
        centerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        centerItem.add(centerBox);
        centerItem.add(centerLabel);
        panel.add(centerItem);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, clusters.size() + 1, 10, 0));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        int totalPoints = clusters.stream().mapToInt(c -> c.getPoints().size()).sum();
        
        JPanel totalPanel = new JPanel(new GridLayout(2, 1));
        totalPanel.setOpaque(false);
        totalPanel.add(createStatLabel("Total Points", new Font("Segoe UI", Font.BOLD, 11)));
        totalPanel.add(createStatLabel(String.valueOf(totalPoints), new Font("Segoe UI", Font.BOLD, 16)));
        panel.add(totalPanel);
        
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            JPanel clusterPanel = new JPanel(new GridLayout(2, 1));
            clusterPanel.setOpaque(false);
            clusterPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)));
            
            clusterPanel.add(createStatLabel("Cluster " + i, new Font("Segoe UI", Font.PLAIN, 11)));
            clusterPanel.add(createStatLabel(cluster.getPoints().size() + " pts", 
                new Font("Segoe UI", Font.BOLD, 14)));
            
            panel.add(clusterPanel);
        }
        
        return panel;
    }
    
    private JLabel createStatLabel(String text, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(new Color(71, 85, 105));
        return label;
    }
    
    private class VisualizationPanel extends JPanel {
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background
            g2.setColor(new Color(248, 250, 252));
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Grid
            drawGrid(g2);
            
            // Title
            g2.setColor(new Color(51, 65, 85));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString("K-Means Clustering Result", PADDING, 30);
            
            // Find bounds
            double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
            
            for (Cluster cluster : clusters) {
                for (Point p : cluster.getPoints()) {
                    minX = Math.min(minX, p.getX());
                    maxX = Math.max(maxX, p.getX());
                    minY = Math.min(minY, p.getY());
                    maxY = Math.max(maxY, p.getY());
                }
            }
            
            // Add margin
            double rangeX = maxX - minX;
            double rangeY = maxY - minY;
            minX -= rangeX * 0.1;
            maxX += rangeX * 0.1;
            minY -= rangeY * 0.1;
            maxY += rangeY * 0.1;
            
            int plotWidth = getWidth() - 2 * PADDING;
            int plotHeight = getHeight() - 2 * PADDING - 40;
            
            // Draw clusters
            for (int i = 0; i < clusters.size(); i++) {
                Cluster cluster = clusters.get(i);
                Color clusterColor = CLUSTER_COLORS[i % CLUSTER_COLORS.length];
                
                // Draw points
                g2.setColor(new Color(clusterColor.getRed(), clusterColor.getGreen(), 
                    clusterColor.getBlue(), 150));
                
                for (Point p : cluster.getPoints()) {
                    int x = (int) (PADDING + (p.getX() - minX) / (maxX - minX) * plotWidth);
                    int y = (int) (PADDING + 40 + plotHeight - (p.getY() - minY) / (maxY - minY) * plotHeight);
                    
                    g2.fill(new Ellipse2D.Double(x - POINT_SIZE/2, y - POINT_SIZE/2, 
                        POINT_SIZE, POINT_SIZE));
                }
                
                // Draw centroid
                Point center = cluster.getCenter();
                int cx = (int) (PADDING + (center.getX() - minX) / (maxX - minX) * plotWidth);
                int cy = (int) (PADDING + 40 + plotHeight - (center.getY() - minY) / (maxY - minY) * plotHeight);
                
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(cx - CENTER_SIZE/2, cy - CENTER_SIZE/2, CENTER_SIZE, CENTER_SIZE);
                
                g2.setColor(clusterColor);
                g2.fillOval(cx - CENTER_SIZE/2, cy - CENTER_SIZE/2, CENTER_SIZE, CENTER_SIZE);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(cx - CENTER_SIZE/2, cy - CENTER_SIZE/2, CENTER_SIZE, CENTER_SIZE);
            }
            
            // Draw axes
            g2.setColor(new Color(100, 116, 139));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(PADDING, PADDING + 40 + plotHeight, 
                       PADDING + plotWidth, PADDING + 40 + plotHeight); // X-axis
            g2.drawLine(PADDING, PADDING + 40, 
                       PADDING, PADDING + 40 + plotHeight); // Y-axis
            
            // Axis labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("X", PADDING + plotWidth + 10, PADDING + 40 + plotHeight + 5);
            g2.drawString("Y", PADDING - 10, PADDING + 30);
        }
        
        private void drawGrid(Graphics2D g2) {
            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(1));
            
            int plotWidth = getWidth() - 2 * PADDING;
            int plotHeight = getHeight() - 2 * PADDING - 40;
            
            // Vertical lines
            for (int i = 0; i <= 10; i++) {
                int x = PADDING + (plotWidth * i) / 10;
                g2.drawLine(x, PADDING + 40, x, PADDING + 40 + plotHeight);
            }
            
            // Horizontal lines
            for (int i = 0; i <= 10; i++) {
                int y = PADDING + 40 + (plotHeight * i) / 10;
                g2.drawLine(PADDING, y, PADDING + plotWidth, y);
            }
        }
    }
}
