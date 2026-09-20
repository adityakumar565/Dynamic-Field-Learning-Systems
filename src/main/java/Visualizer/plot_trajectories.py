import os
import glob
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

def get_latest_run_folder(base_dir="data/runs"):
    if not os.path.exists(base_dir):
        print(f"Base directory {base_dir} does not exist.")
        return None
    
    subdirs = [os.path.join(base_dir, d) for d in os.listdir(base_dir) if os.path.isdir(os.path.join(base_dir, d))]
    if not subdirs:
        print("No run folders found.")
        return None
        
    latest_run = max(subdirs, key=os.path.getmtime)
    return latest_run

def plot_optimization_curve(csv_folder, plots_folder):
    filepath = os.path.join(csv_folder, 'optimization_curve.csv')
    if not os.path.exists(filepath):
        print(f"File not found: {filepath}")
        return
        
    df = pd.read_csv(filepath)
    plt.figure(figsize=(10, 5))
    plt.plot(df['Epoch'], df['BestCost'], marker='o', linestyle='-', color='indigo', linewidth=2)
    plt.title('Optimization Curve (Best Cost per Epoch)', fontsize=14)
    plt.xlabel('Epoch', fontsize=12)
    plt.ylabel('Best Cost', fontsize=12)
    plt.grid(True, linestyle='--', alpha=0.7)
    plt.tight_layout()
    plt.savefig(os.path.join(plots_folder, 'optimization_curve.png'), dpi=300)
    plt.close()

def plot_best_agents_evolution(csv_folder, plots_folder):
    csv_files = glob.glob(os.path.join(csv_folder, 'epoch_*.csv'))
    if not csv_files:
        print("No epoch CSV files found.")
        return
        
    plt.figure(figsize=(12, 12))
    
    # Sort files by epoch number
    csv_files.sort(key=lambda f: int(os.path.basename(f).replace('epoch_', '').replace('.csv', '')))
    max_epoch = len(csv_files)
    
    cmap = plt.colormaps['viridis']
    
    # Read targets if available
    targets_file = os.path.join(csv_folder, 'targets.csv')
    targets_df = None
    if os.path.exists(targets_file):
        targets_df = pd.read_csv(targets_file)
        
    for idx, filepath in enumerate(csv_files):
        epoch_num = int(os.path.basename(filepath).replace('epoch_', '').replace('.csv', ''))
        df = pd.read_csv(filepath)
        
        target_x, target_y = 50.0, 50.0
        if targets_df is not None:
            target_row = targets_df[targets_df['Epoch'] == epoch_num]
            if not target_row.empty:
                target_x = float(target_row['TargetX'].iloc[0])
                target_y = float(target_row['TargetY'].iloc[0])
        
        max_tick = df['Tick'].max()
        final_states = df[df['Tick'] == max_tick].copy()
        
        # Calculate distance to dynamic bomb for all agents at final tick
        final_states['DistToBomb'] = np.sqrt((final_states['X'] - target_x)**2 + (final_states['Y'] - target_y)**2)
        best_agent_id = final_states.loc[final_states['DistToBomb'].idxmin()]['AgentID']
        
        best_agent_data = df[df['AgentID'] == best_agent_id].sort_values('Tick')
        
        x = best_agent_data['X'].values
        y = best_agent_data['Y'].values
        
        # Only plot every Nth epoch to avoid clutter, but definitely plot first and last
        if epoch_num == 1 or epoch_num == max_epoch or epoch_num % 5 == 0:
            color = cmap(epoch_num / max_epoch)
            plt.plot(x, y, color=color, alpha=0.6, linewidth=2, label=f'Epoch {epoch_num}')
            
            # Add arrows using quiver
            # Sample every 10 ticks for arrows to avoid overlap
            step = 10
            if len(x) > step:
                x_sub = x[:-1:step]
                y_sub = y[:-1:step]
                u_sub = np.diff(x)[::step]
                v_sub = np.diff(y)[::step]
                
                # Normalize arrows
                norm = np.sqrt(u_sub**2 + v_sub**2)
                norm[norm == 0] = 1 # prevent div by zero
                u_sub = u_sub / norm
                v_sub = v_sub / norm
                
                plt.quiver(x_sub, y_sub, u_sub, v_sub, color=color, scale=30, width=0.005, headwidth=5, alpha=0.8)

            # Only plot the bomb if we're sparsely plotting the epoch, to avoid clutter
            plt.scatter(target_x, target_y, color=color, marker='*', s=200, alpha=0.8, zorder=10)

    # Plot origin
    plt.scatter(0.0, 0.0, color='green', marker='o', s=150, label='Start (0,0)', zorder=10)
    
    plt.title('Evolution of Best Agent Path (Stars denote Target)', fontsize=16)
    plt.xlabel('X Coordinate', fontsize=14)
    plt.ylabel('Y Coordinate', fontsize=14)
    plt.grid(True, linestyle='--', alpha=0.5)
    plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
    
    plt.tight_layout()
    plt.savefig(os.path.join(plots_folder, 'best_agents_evolution.png'), dpi=300)
    plt.close()

import sys

if __name__ == "__main__":
    if len(sys.argv) > 1:
        latest_run = sys.argv[1]
    else:
        latest_run = get_latest_run_folder()
        
    if latest_run and os.path.exists(latest_run):
        csv_folder = os.path.join(latest_run, "csv")
        plots_folder = os.path.join(latest_run, "plots")
        
        if not os.path.exists(plots_folder):
            os.makedirs(plots_folder)
            
        print(f"Processing data from: {csv_folder}")
        plot_optimization_curve(csv_folder, plots_folder)
        plot_best_agents_evolution(csv_folder, plots_folder)
        print(f"Visualizations saved to: {plots_folder}")
    else:
        print("Could not find any run data to process.")
