package fields.fieldV1impl;

import interfaces.field.FieldAlgebraInterface;
import interfaces.field.FieldInterface;
import interfaces.stateSpace.StateSpaceInterface;

/**
 * HashTableFieldsImplementation
 * 
 * It is a container class which stores the mapping between each state and
 * the field associated with each state. It also holds a reference to the field
 * algebra which is used to perform the field vector space operations.
 * 
 * The implementation uses a Hash table (open addressing) to map a state to an index 
 * which yields the field value at the index.
 */
public class HashTableFieldsImplementation implements FieldInterface {

    public FieldAlgebraInterface fieldAlgebra;

    private int maxSize; // number of states the field can hold
    private int numberOfStates; // number of states the field currently holds
    private boolean[] isActive; // tells whether the index is active or not.
    private int fieldDimension;
    private int stateDimension;
    
    // DOD Structure of Arrays
    private double[][] fields;           // [fieldDimension][maxSize]
    private double[][] statesCollection; // [stateDimension][maxSize]

    public HashTableFieldsImplementation(int maxSize, int stateDimension, int fieldDimension, FieldAlgebraInterface fieldAlgebra) {
        this.maxSize = maxSize;
        this.stateDimension = stateDimension;
        this.fieldDimension = fieldDimension;
        this.numberOfStates = 0;
        this.fieldAlgebra = fieldAlgebra;

        this.isActive = new boolean[maxSize];
        this.fields = new double[fieldDimension][maxSize];
        this.statesCollection = new double[stateDimension][maxSize];
    }

    public HashTableFieldsImplementation(int maxSize, int stateDimension, int fieldDimension) {
        this(maxSize, stateDimension, fieldDimension, new FieldAlgebra());
    }

    // --- Hashing & Collision Helpers ---

    private int VectorHasher(double[] vector) {
        int P = 1;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] > P) {
                P = (int) vector[i];
            }
        }
        P++; 

        long hash = 0; 
        for (int i = 0; i < vector.length; i++) {
            hash = (hash * P + (long) vector[i]) % maxSize;
        }
        return (int) ((hash + maxSize) % maxSize);
    }

    // Overloaded hasher for batch operations directly from column arrays
    private int VectorHasher(double[][] statesBatch, int batchIndex) {
        int P = 1;
        for (int d = 0; d < stateDimension; d++) {
            if (statesBatch[d][batchIndex] > P) {
                P = (int) statesBatch[d][batchIndex];
            }
        }
        P++; 

        long hash = 0; 
        for (int d = 0; d < stateDimension; d++) {
            hash = (hash * P + (long) statesBatch[d][batchIndex]) % maxSize;
        }
        return (int) ((hash + maxSize) % maxSize);
    }

    private boolean matchState(int index, double[] state) {
        for (int d = 0; d < stateDimension; d++) {
            if (statesCollection[d][index] != state[d]) {
                return false;
            }
        }
        return true;
    }

    // Overloaded match for batch operations directly from column arrays
    private boolean matchState(int index, double[][] statesBatch, int batchIndex) {
        for (int d = 0; d < stateDimension; d++) {
            if (statesCollection[d][index] != statesBatch[d][batchIndex]) {
                return false;
            }
        }
        return true;
    }

    // --- Single Vector Operations ---

    public boolean put(double[] state, double[] field) {
        if (numberOfStates >= maxSize) {
            return false;
        }
        int index = VectorHasher(state);
        int startIndex = index;
        while (isActive[index]) {
            if (matchState(index, state)) {
                for (int d = 0; d < fieldDimension; d++) {
                    fields[d][index] = field[d];
                }
                return true; 
            }
            index = (index + 1) % maxSize;
            if (index == startIndex) return false;
        }

        for (int d = 0; d < stateDimension; d++) {
            statesCollection[d][index] = state[d];
        }
        for (int d = 0; d < fieldDimension; d++) {
            fields[d][index] = field[d];
        }
        isActive[index] = true;
        numberOfStates++;
        return true;
    }

    public boolean get(double[] state, double[] resultField) {
        int index = VectorHasher(state);
        int startIndex = index;
        while (isActive[index]) {
            if (matchState(index, state)) {
                for (int d = 0; d < fieldDimension; d++) {
                    resultField[d] = fields[d][index];
                }
                return true;
            }
            index = (index + 1) % maxSize;
            if (index == startIndex) break;
        }
        return false;
    }

    // --- Batch Vector Operations ---

    private boolean putSingleFromBatch(double[][] batchStates, double[][] batchFields, int batchIndex) {
        if (numberOfStates >= maxSize) return false;
        int index = VectorHasher(batchStates, batchIndex);
        int startIndex = index;
        while (isActive[index]) {
            if (matchState(index, batchStates, batchIndex)) {
                for (int d = 0; d < fieldDimension; d++) {
                    fields[d][index] = batchFields[d][batchIndex];
                }
                return true;
            }
            index = (index + 1) % maxSize;
            if (index == startIndex) return false;
        }

        for (int d = 0; d < stateDimension; d++) {
            statesCollection[d][index] = batchStates[d][batchIndex];
        }
        for (int d = 0; d < fieldDimension; d++) {
            fields[d][index] = batchFields[d][batchIndex];
        }
        isActive[index] = true;
        numberOfStates++;
        return true;
    }

    private boolean getSingleFromBatch(double[][] batchStates, double[][] resultFields, int batchIndex) {
        int index = VectorHasher(batchStates, batchIndex);
        int startIndex = index;
        while (isActive[index]) {
            if (matchState(index, batchStates, batchIndex)) {
                for (int d = 0; d < fieldDimension; d++) {
                    resultFields[d][batchIndex] = fields[d][index];
                }
                return true;
            }
            index = (index + 1) % maxSize;
            if (index == startIndex) break;
        }
        return false;
    }

    public void putBatch(double[][] batchStates, double[][] batchFields) {
        int numElements = batchStates[0].length;
        for (int i = 0; i < numElements; i++) {
            putSingleFromBatch(batchStates, batchFields, i);
        }
    }

    public void putBatch(StateSpaceInterface stateSpaceInterface, double[][] batchFields) {
        stateSpace.stateSpacev1impl.StateSpace ss = (stateSpace.stateSpacev1impl.StateSpace) stateSpaceInterface;
        for (int i = 0; i < ss.numberOfStates; i++) {
            if (ss.isAlive[i]) {
                putSingleFromBatch(ss.states, batchFields, i);
            }
        }
    }

    public void getBatch(double[][] batchStates, double[][] resultFields) {
        int numElements = batchStates[0].length;
        for (int i = 0; i < numElements; i++) {
            getSingleFromBatch(batchStates, resultFields, i);
        }
    }

    public void getBatch(StateSpaceInterface stateSpaceInterface, double[][] resultFields) {
        stateSpace.stateSpacev1impl.StateSpace ss = (stateSpace.stateSpacev1impl.StateSpace) stateSpaceInterface;
        for (int i = 0; i < ss.numberOfStates; i++) {
            if (ss.isAlive[i]) {
                getSingleFromBatch(ss.states, resultFields, i);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < maxSize; i++) {
            if (isActive[i]) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append("(");
                
                // state
                sb.append("[");
                for (int d = 0; d < stateDimension; d++) {
                    sb.append(statesCollection[d][i]);
                    if (d < stateDimension - 1) sb.append(", ");
                }
                sb.append("]");
                
                sb.append(" -> ");
                
                // value
                sb.append("[");
                for (int d = 0; d < fieldDimension; d++) {
                    sb.append(fields[d][i]);
                    if (d < fieldDimension - 1) sb.append(", ");
                }
                sb.append("]");
                
                sb.append(" : ");
                sb.append(i);
                
                sb.append(")");
                first = false;
            }
        }
        return sb.toString();
    }
}
