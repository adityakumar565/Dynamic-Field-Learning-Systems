package Visualizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataExporter {
    
    // Save history of all agents in a single epoch
    public static void exportEpoch(int epoch, double[][][] history, String folder) {
        ensureFolderExists(folder);
        String filename = folder + "/epoch_" + epoch + ".csv";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Tick,AgentID,X,Y\n");
            if (history == null || history.length == 0) return;
            
            int maxTicks = history.length;
            int numAgents = history[0][0].length;
            
            for (int t = 0; t < maxTicks; t++) {
                for (int i = 0; i < numAgents; i++) {
                    double x = history[t][0][i];
                    double y = history[t][1][i];
                    writer.write(t + "," + i + "," + x + "," + y + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Append best cost to optimization curve
    public static void appendCost(int epoch, double bestCost, String folder) {
        ensureFolderExists(folder);
        String filename = folder + "/optimization_curve.csv";
        boolean append = (epoch != 1); // Overwrite on epoch 1
        try (FileWriter writer = new FileWriter(filename, append)) {
            if (epoch == 1) {
                writer.write("Epoch,BestCost\n");
            }
            writer.write(epoch + "," + bestCost + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Export target location for moving targets
    public static void exportTarget(int epoch, double targetX, double targetY, String folder) {
        ensureFolderExists(folder);
        String filename = folder + "/targets.csv";
        boolean append = (epoch != 1); 
        try (FileWriter writer = new FileWriter(filename, append)) {
            if (epoch == 1) {
                writer.write("Epoch,TargetX,TargetY\n");
            }
            writer.write(epoch + "," + targetX + "," + targetY + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureFolderExists(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
