# Project Context: Dynamic Field Learning Systems (DFLS)

This document serves as the foundational architectural specification and context guide for building **DFLS**. This project is a blisteringly fast, data-oriented physics and continuous planning engine that optimizes dynamic state-space traversal using batch array processing and Evolutionary Machine Learning.

## 1. Project Overview & Current State (V1.0 Completed)
* **V1.0 Status**: The V1.0 Prototype is 100% complete and frozen on the `prototype` Git branch under the tag `v1.0-prototype`.
* **Git Strategy**: 
  - `prototype`: Contains the frozen V1 DOD implementation.
  - `dev` -> `st` -> `prod`: Forward-flowing deployment branches where the V2 rewrite will take place.
* **Build System**: The project uses a standard Maven layout (`src/main/java`). To execute without Maven, use `.\compile_and_run.ps1` which recursively finds and compiles `.java` files using raw `javac`.
* **Visualizer**: Python scripts (`plot_trajectories.py`) render convergence paths and optimization curves from CSV dumps.

## 2. V1.0 Core Architecture (Reference)
The V1 prototype proved the mathematical feasibility of array-flattened continuous topologies using the following loop:
1. **State Space**: Continuous `X,Y` memory blocks.
2. **Topology**: Rule engine generating adjacent states (actions).
3. **Generator**: Reads local environment fields based on topology.
4. **Transition**: Aggregates fields to pick optimal next steps.
5. **Evolutionary Optimizer**: Outer-loop that uses cost functions (like distance) to mutate agent behavior across epochs.
*(Crucial limitation of V1: The arrays were hardcoded to `double[][][]`, tightly coupling dimensions and blocking N-dimensional dynamic scaling).*

## 3. V2.0 Architecture Goals (The Next Phase)

When an AI agent assists with V2.0, they must prioritize the following architectural rules:

### A. Dynamic Bridge Architecture
The core priority of V2 is replacing hardcoded `double[][][]` arrays with dynamic "Bridge" interfaces. Bridges must:
- Maintain Data-Oriented cache locality (zero object allocation during ticks).
- Dynamically scale to N-dimensions without hardcoded loops.
- Support chunked "dirty-tracking" so only modified sectors of the grid/field are recalculated.

### B. Hardware Acceleration (C++ / CUDA)
The mathematical implementations of the `Generator` and `Transition` modules must be designed in a way that allows them to be stripped out and rewritten as OpenCL or CUDA kernels.
- The Java orchestrator (`World`) will manage the memory boundaries.
- JNI or Project Panama will be used to dispatch batch matrices to the GPU/C++ backend.

### C. Advanced ML
The naive evolutionary elitism implemented in V1 must be replaced with robust batch-averaging gradient systems capable of handling non-Markovian decisions in extremely dynamic topologies (e.g. moving targets that require prediction rather than reactive field climbing).