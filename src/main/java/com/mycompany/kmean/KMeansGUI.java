/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kmean;

/**
 *
 * @author Aliaa Mohamed
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class KMeansGUI extends JFrame {
    
    // Components
    private JSpinner kSpinner;
    private JSpinner dataSizeSpinner;
    private JSpinner maxIterSpinner;
    private JSpinner threadsSpinner;
    private JComboBox<String> modeCombo;
    private JButton runButton;
    private JButton experimentButton;
    private JButton visualizeButton;
    private JTextArea resultsArea;
    private JProgressBar progressBar;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> initCombo;
    private javax.swing.JCheckBox restartsCheckbox;


    // Data
    private List<Point> currentDataset;
    private List<Cluster> currentClusters;
    private List<ExperimentResult> experimentResults;
    
    public KMeansGUI() {
        experimentResults = new ArrayList<>();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("K-Means Clustering - Advanced Analysis Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 242, 245));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(37, 99, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("K-Means Clustering Analysis");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Sequential vs Parallel Performance Comparison");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(219, 234, 254));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);

        panel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);

        String[] initMethods = {"random", "kmeans++"};
        initCombo = new JComboBox<>(initMethods);
        initCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(createConfigRow("Initialization:", initCombo), BorderLayout.EAST);

        panel.add(Box.createVerticalStrut(10), BorderLayout.EAST);

        restartsCheckbox = new javax.swing.JCheckBox("Use Multiple Restarts (5x)");
        restartsCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        restartsCheckbox.setForeground(Color.WHITE);
        restartsCheckbox.setOpaque(false);
        restartsCheckbox.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.add(restartsCheckbox, BorderLayout.EAST);

        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        panel.add(createLeftPanel());
        panel.add(createRightPanel());
        
        return panel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        
        panel.add(createConfigPanel(), BorderLayout.NORTH);
        panel.add(createControlPanel(), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Configuration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Number of Clusters (K)
        panel.add(createConfigRow("Number of Clusters (K):", 
            kSpinner = createSpinner(2, 2, 10, 1)));
        panel.add(Box.createVerticalStrut(15));
        
        // Data Size
        panel.add(createConfigRow("Dataset Size:", 
            dataSizeSpinner = createSpinner(1000, 100, 100000, 100)));
        panel.add(Box.createVerticalStrut(15));
        
        // Max Iterations
        panel.add(createConfigRow("Max Iterations:", 
            maxIterSpinner = createSpinner(50, 10, 500, 10)));
        panel.add(Box.createVerticalStrut(15));
        
        // Number of Threads
        panel.add(createConfigRow("Parallel Threads:", 
            threadsSpinner = createSpinner(4, 1, 16, 1)));
        panel.add(Box.createVerticalStrut(15));
        
        // Execution Mode
        String[] modes = {"Both (Sequential & Parallel)", "Sequential Only", "Parallel Only"};
        modeCombo = new JComboBox<>(modes);
        modeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(createConfigRow("Execution Mode:", modeCombo));
        
        return panel;
    }
    
    private JPanel createConfigRow(String label, JComponent component) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setPreferredSize(new Dimension(180, 30));
        
        row.add(lbl, BorderLayout.WEST);
        row.add(component, BorderLayout.CENTER);
        
        return row;
    }
    
    private JSpinner createSpinner(int value, int min, int max, int step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        return spinner;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Actions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Run Single Test
        runButton = createStyledButton(" Run Single Test", new Color(37, 99, 235));
        runButton.addActionListener(e -> runSingleTest());
        panel.add(runButton);
        panel.add(Box.createVerticalStrut(15));
        
        // Run Full Experiments
        experimentButton = createStyledButton("Run Full Experiments", new Color(16, 185, 129));
        experimentButton.addActionListener(e -> runFullExperiments());
        panel.add(experimentButton);
        panel.add(Box.createVerticalStrut(15));
        
        // Visualize Results
        visualizeButton = createStyledButton("Visualize Clusters", new Color(139, 92, 246));
        visualizeButton.setEnabled(false);
        visualizeButton.addActionListener(e -> visualizeClusters());
        panel.add(visualizeButton);
        panel.add(Box.createVerticalStrut(20));
        
        // Progress Bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(progressBar);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        
        panel.add(createResultsPanel(), BorderLayout.CENTER);
        panel.add(createTablePanel(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Results Console");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultsArea.setBackground(new Color(248, 250, 252));
        resultsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultsArea.setText("Ready to run experiments...\n\nClick 'Run Single Test' to test current configuration\nor 'Run Full Experiments' for comprehensive analysis.");
        
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(0, 250));
        
        JLabel titleLabel = new JLabel("Comparison Table");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        String[] columns = {"K", "Size", "Seq (ms)", "Par (ms)", "Speedup", "Winner"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultsTable.setRowHeight(30);
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultsTable.getTableHeader().setBackground(new Color(241, 245, 249));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        
        JLabel leftLabel = new JLabel("K-Means Clustering Project");
        leftLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftLabel.setForeground(new Color(100, 116, 139));
        
        JLabel rightLabel = new JLabel("Author: Aliaa Mohamed");
        rightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rightLabel.setForeground(new Color(100, 116, 139));
        
        panel.add(leftLabel, BorderLayout.WEST);
        panel.add(rightLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void runSingleTest() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                disableButtons();
                publish("=".repeat(60) + "\n");
                publish("Starting Single Test...\n");
                publish("=".repeat(60) + "\n\n");

                int k = (Integer) kSpinner.getValue();
                int dataSize = (Integer) dataSizeSpinner.getValue();
                int maxIter = (Integer) maxIterSpinner.getValue();
                int threads = (Integer) threadsSpinner.getValue();
                String mode = (String) modeCombo.getSelectedItem();

                // ✅ NEW: Get bonus features
                String initMethod = (String) initCombo.getSelectedItem();
                boolean useRestarts = restartsCheckbox.isSelected();

                publish("Configuration:\n");
                publish("  K = " + k + "\n");
                publish("  Data Size = " + dataSize + "\n");
                publish("  Max Iterations = " + maxIter + "\n");
                publish("  Threads = " + threads + "\n");
                publish("  Mode = " + mode + "\n");
                publish("  Initialization = " + initMethod + " " + 
                       (initMethod.equals("kmeans++") ? "✨ BONUS" : "") + "\n");
                publish("  Multiple Restarts = " + useRestarts + " " +
                       (useRestarts ? "✨ BONUS" : "") + "\n\n");

                progressBar.setValue(10);

                publish("Generating dataset...\n");
                DataSetLoader loader = new DataSetLoader(dataSize);
                currentDataset = loader.generate2DData();
                publish("✓ Generated " + dataSize + " points\n\n");

                progressBar.setValue(30);

                KMeansConfig config = new KMeansConfig(k, maxIter, initMethod);

                double seqTime = 0, parTime = 0, seqSSE = 0, parSSE = 0;

                // ═══════════ SEQUENTIAL ═══════════
                if (!mode.equals("Parallel Only")) {
                    if (useRestarts) {
                        publish("Running Sequential with 5 Random Restarts...\n");
                        KMeansWithRestarts kmeans = new KMeansWithRestarts(config, 5, false);

                        long start = System.nanoTime();
                        List<Cluster> seqClusters = kmeans.runWithRestarts(currentDataset);
                        long end = System.nanoTime();

                        seqTime = (end - start) / 1_000_000.0;
                        seqSSE = SSECalculator.calculateSSE(seqClusters);

                        publish("✓ Sequential (5 restarts) completed in " + 
                               String.format("%.2f", seqTime) + " ms\n");
                        publish("  Best SSE: " + String.format("%.2f", seqSSE) + "\n\n");

                        if (mode.equals("Sequential Only")) {
                            currentClusters = seqClusters;
                        }
                    } else {
                        publish("Running Sequential K-Means...\n");
                        KMeansSequential sequential = new KMeansSequential(config);

                        long start = System.nanoTime();
                        List<Cluster> seqClusters = sequential.run(currentDataset);
                        long end = System.nanoTime();

                        seqTime = (end - start) / 1_000_000.0;
                        seqSSE = SSECalculator.calculateSSE(seqClusters);

                        publish("✓ Sequential completed in " + 
                               String.format("%.2f", seqTime) + " ms\n");
                        publish("  SSE: " + String.format("%.2f", seqSSE) + "\n\n");

                        if (mode.equals("Sequential Only")) {
                            currentClusters = seqClusters;
                        }
                    }
                }

                progressBar.setValue(60);

                // ═══════════ PARALLEL ═══════════
                if (!mode.equals("Sequential Only")) {
                    if (useRestarts) {
                        publish("Running Parallel with 5 Random Restarts (" + threads + " threads)...\n");
                        KMeansWithRestarts kmeans = new KMeansWithRestarts(config, 5, true);

                        long start = System.nanoTime();
                        List<Cluster> parClusters = kmeans.runWithRestarts(currentDataset);
                        long end = System.nanoTime();

                        parTime = (end - start) / 1_000_000.0;
                        parSSE = SSECalculator.calculateSSE(parClusters);

                        publish("✓ Parallel (5 restarts) completed in " + 
                               String.format("%.2f", parTime) + " ms\n");
                        publish("  Best SSE: " + String.format("%.2f", parSSE) + "\n\n");

                        currentClusters = parClusters;
                    } else {
                        publish("Running Parallel K-Means (" + threads + " threads)...\n");
                        KMeansParallel parallel = new KMeansParallel(config, threads);

                        long start = System.nanoTime();
                        List<Cluster> parClusters = parallel.run(currentDataset);
                        long end = System.nanoTime();

                        parTime = (end - start) / 1_000_000.0;
                        parSSE = SSECalculator.calculateSSE(parClusters);

                        publish("✓ Parallel completed in " + 
                               String.format("%.2f", parTime) + " ms\n");
                        publish("  SSE: " + String.format("%.2f", parSSE) + "\n\n");

                        currentClusters = parClusters;
                        parallel.shutdown();
                    }
                }

                progressBar.setValue(90);

                // ═══════════ COMPARISON ═══════════
                if (mode.equals("Both (Sequential & Parallel)")) {
                    double speedup = seqTime / parTime;
                    publish("=".repeat(60) + "\n");
                    publish("PERFORMANCE COMPARISON\n");
                    publish("=".repeat(60) + "\n");
                    publish("Sequential: " + String.format("%.2f", seqTime) + " ms" +
                           (useRestarts ? " (5 restarts)" : "") + "\n");
                    publish("Parallel:   " + String.format("%.2f", parTime) + " ms" +
                           (useRestarts ? " (5 restarts)" : "") + "\n");
                    publish("Speedup:    " + String.format("%.2fx", speedup) + "\n");
                    publish("Time Saved: " + String.format("%.1f%%", 
                           ((seqTime - parTime) / seqTime * 100)) + "\n");

                    // ✅ Quality comparison
                    publish("\nQuality Comparison:\n");
                    publish("Sequential SSE: " + String.format("%.2f", seqSSE) + "\n");
                    publish("Parallel SSE:   " + String.format("%.2f", parSSE) + "\n");
                    double sseDiff = Math.abs(seqSSE - parSSE) / seqSSE * 100;
                    publish("SSE Difference: " + String.format("%.2f%%", sseDiff) + "\n");

                    String winner = speedup > 1.0 ? "Parallel ✓" : "Sequential ✓";
                    publish("\nWinner: " + winner + "\n");

                    // ✅ Bonus indicators
                    if (initMethod.equals("kmeans++") || useRestarts) {
                        publish("\n✨ Bonus features used:\n");
                        if (initMethod.equals("kmeans++")) {
                            publish("  • K-means++ initialization\n");
                        }
                        if (useRestarts) {
                            publish("  • Multiple random restarts (5x)\n");
                        }
                    }

                    tableModel.addRow(new Object[]{
                        k, dataSize, 
                        String.format("%.2f", seqTime),
                        String.format("%.2f", parTime),
                        String.format("%.2fx", speedup),
                        winner
                    });
                }

                progressBar.setValue(100);
                publish("\n✓ Test completed successfully!\n");

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    resultsArea.append(chunk);
                }
                resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
            }

            @Override
            protected void done() {
                enableButtons();
                visualizeButton.setEnabled(currentClusters != null);
                progressBar.setValue(0);
            }
        };

        worker.execute();
    }
    
    private void runFullExperiments() {
        int result = JOptionPane.showConfirmDialog(this,
            "This will run comprehensive experiments with multiple configurations.\n" +
            "It may take several minutes. Continue?",
            "Confirm Full Experiments",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result != JOptionPane.YES_OPTION) return;
        
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                disableButtons();
                tableModel.setRowCount(0);
                experimentResults.clear();
                
                publish("\n" + "=".repeat(70) + "\n");
                publish("STARTING FULL EXPERIMENTS\n");
                publish("=".repeat(70) + "\n\n");
                
                int[] kValues = {2, 3, 5};
                int[] dataSizes = {1000, 5000, 10000, 50000, 100000};
                int totalTests = kValues.length * dataSizes.length;
                int testNum = 0;
                
                for (int k : kValues) {
                    for (int size : dataSizes) {
                        testNum++;
                publish("\n>> Test " + testNum + "/" + totalTests + 
                               ": K=" + k + ", Size=" + size + "\n");
                        
                        progressBar.setValue((testNum * 100) / totalTests);
                        
                        DataSetLoader loader = new DataSetLoader(size);
                        List<Point> data = loader.generate2DData();
                        
                        KMeansConfig config = new KMeansConfig(k, 50, "random");
                        
                        KMeansSequential sequential = new KMeansSequential(config);
                        long start = System.nanoTime();
                        List<Cluster> seqClusters = sequential.run(data);
                        long end = System.nanoTime();
                        double seqTime = (end - start) / 1_000_000.0;
                        double seqSSE = SSECalculator.calculateSSE(seqClusters);
                        
                        KMeansParallel parallel = new KMeansParallel(config, 4);
                        start = System.nanoTime();
                        List<Cluster> parClusters = parallel.run(data);
                        end = System.nanoTime();
                        double parTime = (end - start) / 1_000_000.0;
                        double parSSE = SSECalculator.calculateSSE(parClusters);
                        parallel.shutdown();
                        
                        double speedup = seqTime / parTime;
                        String winner = speedup > 1.0 ? "Parallel" : "Sequential";
                        
                        publish("  Seq: " + String.format("%.2f", seqTime) + " ms | ");
                        publish("Par: " + String.format("%.2f", parTime) + " ms | ");
                        publish("Speedup: " + String.format("%.2fx", speedup) + "\n");
                        
                        tableModel.addRow(new Object[]{
                            k, size,
                            String.format("%.2f", seqTime),
                            String.format("%.2f", parTime),
                            String.format("%.2fx", speedup),
                            winner
                        });
                        
                        experimentResults.add(new ExperimentResult(
                            k, size, seqTime, parTime, seqSSE, parSSE, speedup
                        ));
                    }
                }
                
                publish("\n" + "=".repeat(70) + "\n");
                publish("ALL EXPERIMENTS COMPLETED!\n");
                publish("=".repeat(70) + "\n");
                
                double avgSpeedup = experimentResults.stream()
                    .mapToDouble(r -> r.speedup)
                    .average().orElse(0);
                
                publish("\nSUMMARY:\n");
                publish("  Average Speedup: " + String.format("%.2fx", avgSpeedup) + "\n");
                publish("  Total Tests: " + totalTests + "\n");
                
                long wins = experimentResults.stream()
                    .filter(r -> r.speedup > 1.0)
                    .count();
                publish("  Parallel Wins: " + wins + "/" + totalTests + "\n");
                
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    resultsArea.append(chunk);
                }
                resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
            }
            
            @Override
            protected void done() {
                enableButtons();
                progressBar.setValue(0);
                JOptionPane.showMessageDialog(KMeansGUI.this,
                    "Experiments completed successfully!\nCheck the results table and console.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };
        
        worker.execute();
    }
    
    private void visualizeClusters() {
        if (currentClusters == null) {
            JOptionPane.showMessageDialog(this,
                "No clusters to visualize. Run a test first!",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        new ClusterVisualization(currentClusters).setVisible(true);
    }
    
    private void disableButtons() {
        runButton.setEnabled(false);
        experimentButton.setEnabled(false);
        visualizeButton.setEnabled(false);
    }
    
    private void enableButtons() {
        runButton.setEnabled(true);
        experimentButton.setEnabled(true);
    }
    
    private static class ExperimentResult {
        int k, dataSize;
        double seqTime, parTime, seqSSE, parSSE, speedup;
        
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
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new KMeansGUI());
    }
}