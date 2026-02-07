# KMeans-Parallel-Java

**Parallel Implementation of K-Means Clustering in Java**

---

## Overview

This project implements the **K-Means clustering algorithm** with both **sequential** and **parallel** versions in Java.  
It provides a **Graphical User Interface (GUI)** for easy interaction, data visualization, and running experiments.

---

## Features

- Sequential and Parallel K-Means algorithms
- K-Means++ initialization and multiple restarts
- GUI for:
  - Loading datasets
  - Configuring number of clusters
  - Running clustering
  - Visualizing clusters and results
- Experiment tracking and comparison of sequential vs parallel execution
- SSE (Sum of Squared Errors) calculation

---

## Usage (GUI)

1. Run the GUI main class: `KMeansGUI.java`
2. Load your dataset (`.csv` file)
3. Choose the number of clusters (`k`) and other configurations
4. Click **Run** to perform clustering
5. Visualize results and compare sequential vs parallel performance

---

## Team

- **Aliaa Mohamed**  
- **Gehad**  
- **Fatma**  
- **Alaa**  
- **Ghada**  
- **Waad**  
- **Hend**

---

## Built With

- Java 17+
- Swing GUI
- Maven for project management
- Parallel streams / ExecutorService for parallel computation

---

## Notes

- Ensure your dataset is in CSV format and properly formatted
- Parallel execution significantly reduces computation time for large datasets
- Compilation generates `.class` files under `target/classes` (Maven default)

---

## License

This project is for educational purposes.
