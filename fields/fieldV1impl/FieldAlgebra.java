package fields.fieldV1impl;

import interfaces.field.FieldAlgebraInterface;

/**
 * 
 * RealFieldAlgebra
 * 
 * An implementation of the Field Algebra for real numbers.
 * 
 * This contains all the operation to enforce that a field must be
 * an inner product vector space
 * 
 * This class is not meant to be used directly. It is meant to be used by
 * Field objects.
 * 
 * It contains the utility methods for the Field Operations
 * 
 * The methods for addition, multiplication(inner product), norm, scaling
 * 
 */
public class FieldAlgebra implements FieldAlgebraInterface {

    /** START : addition */

    // in place
    public void addInPlace(double[] a, double[] b) {
        if (a == null || b == null) return;
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    // zero-allocation output parameter
    public void add(double[] a, double[] b, double[] result) {
        if (result == null) return;
        int len = result.length;
        for (int i = 0; i < len; i++) {
            double valA = (a == null) ? 0.0 : a[i];
            double valB = (b == null) ? 0.0 : b[i];
            result[i] = valA + valB;
        }
    }

    // new value
    public double[] add(double[] a, double[] b) {
        if (a == null && b == null) return null;
        int len = (a != null) ? a.length : b.length;
        double[] result = new double[len];
        for (int i = 0; i < len; i++) {
            double valA = (a == null) ? 0.0 : a[i];
            double valB = (b == null) ? 0.0 : b[i];
            result[i] = valA + valB;
        }
        return result;
    }

    // batch in place (Assumes DOD layout: [dimension][numElements])
    public void addInPlace(double[][] fieldsSet1, double[][] fieldsSet2) {
        if (fieldsSet1 == null || fieldsSet2 == null) return;
        int dim = fieldsSet1.length;
        if (dim == 0) return;
        int numElements = fieldsSet1[0].length;
        for (int d = 0; d < dim; d++) {
            if (fieldsSet1[d] == null || fieldsSet2[d] == null) continue;
            for (int i = 0; i < numElements; i++) {
                fieldsSet1[d][i] += fieldsSet2[d][i];
            }
        }
    }

    // batch zero-allocation output parameter
    public void add(double[][] fieldsSet1, double[][] fieldsSet2, double[][] result) {
        if (result == null) return;
        int dim = result.length;
        if (dim == 0) return;
        int numElements = result[0].length;
        for (int d = 0; d < dim; d++) {
            if (result[d] == null) continue;
            for (int i = 0; i < numElements; i++) {
                double valA = (fieldsSet1 == null || fieldsSet1[d] == null) ? 0.0 : fieldsSet1[d][i];
                double valB = (fieldsSet2 == null || fieldsSet2[d] == null) ? 0.0 : fieldsSet2[d][i];
                result[d][i] = valA + valB;
            }
        }
    }

    // batch new value (Assumes DOD layout: [dimension][numElements])
    public double[][] add(double[][] fieldsSet1, double[][] fieldsSet2) {
        if (fieldsSet1 == null && fieldsSet2 == null) return null;
        double[][] ref = (fieldsSet1 != null) ? fieldsSet1 : fieldsSet2;
        int dim = ref.length;
        if (dim == 0) return new double[0][0];
        int numElements = ref[0].length;
        
        double[][] newBatch = new double[dim][numElements];
        for (int d = 0; d < dim; d++) {
            for (int i = 0; i < numElements; i++) {
                double valA = (fieldsSet1 == null || fieldsSet1[d] == null) ? 0.0 : fieldsSet1[d][i];
                double valB = (fieldsSet2 == null || fieldsSet2[d] == null) ? 0.0 : fieldsSet2[d][i];
                newBatch[d][i] = valA + valB;
            }
        }
        return newBatch;
    }

    /** END : addition */

    /** START : dot product */

    // Dot product of a single vector (returns a scalar)
    public double dotProduct(double[] a, double[] b) {
        if (a == null && b == null) return 0.0;
        int len = (a != null) ? a.length : b.length;
        double result = 0;
        for (int i = 0; i < len; i++) {
            double valA = (a == null) ? 1.0 : a[i];
            double valB = (b == null) ? 1.0 : b[i];
            result += valA * valB;
        }
        return result;
    }

    // Batch dot product zero-allocation output parameter
    // Assumes DOD layout: [dimension][numElements]
    public void dotProduct(double[][] fieldsSet1, double[][] fieldsSet2, double[] result) {
        if (result == null) return;
        int numElements = result.length;
        for (int i = 0; i < numElements; i++) {
            result[i] = 0.0;
        }
        
        double[][] ref = (fieldsSet1 != null) ? fieldsSet1 : fieldsSet2;
        if (ref == null) return;
        int dim = ref.length;
        
        for (int d = 0; d < dim; d++) {
            for (int i = 0; i < numElements; i++) {
                double valA = (fieldsSet1 == null || fieldsSet1[d] == null) ? 1.0 : fieldsSet1[d][i];
                double valB = (fieldsSet2 == null || fieldsSet2[d] == null) ? 1.0 : fieldsSet2[d][i];
                result[i] += valA * valB;
            }
        }
    }

    // Batch dot product (returns an array of scalars, one for each vector in the batch)
    // Assumes DOD layout: [dimension][numElements]
    public double[] dotProduct(double[][] fieldsSet1, double[][] fieldsSet2) {
        if (fieldsSet1 == null && fieldsSet2 == null) return null;
        double[][] ref = (fieldsSet1 != null) ? fieldsSet1 : fieldsSet2;
        if (ref.length == 0) return new double[0];
        int numElements = ref[0].length;
        
        double[] result = new double[numElements];
        int dim = ref.length;

        for (int d = 0; d < dim; d++) {
            for (int i = 0; i < numElements; i++) {
                double valA = (fieldsSet1 == null || fieldsSet1[d] == null) ? 1.0 : fieldsSet1[d][i];
                double valB = (fieldsSet2 == null || fieldsSet2[d] == null) ? 1.0 : fieldsSet2[d][i];
                result[i] += valA * valB;
            }
        }
        return result;
    }

    /** END : dot product */

    /** START : norm */

    // Norm of a single vector (returns a scalar)
    public double norm(double[] a) {
        if (a == null) return 0.0;
        return Math.sqrt(dotProduct(a, a));
    }

    // Batch norm zero-allocation output parameter
    public void norm(double[][] fieldsSet, double[] result) {
        if (fieldsSet == null) {
            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = 0.0;
                }
            }
            return;
        }
        dotProduct(fieldsSet, fieldsSet, result);
        for (int i = 0; i < result.length; i++) {
            result[i] = Math.sqrt(result[i]);
        }
    }

    // Batch norm (returns an array of scalars, one for each vector in the batch)
    public double[] norm(double[][] fieldsSet) {
        if (fieldsSet == null) return null;
        double[] result = dotProduct(fieldsSet, fieldsSet);
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                result[i] = Math.sqrt(result[i]);
            }
        }
        return result;
    }

    /** END : norm */

    /** START : scaling (optional but usually required for vector space) */

    // scale in place
    public void scaleInPlace(double[] a, double scalar) {
        if (a == null) return;
        for (int i = 0; i < a.length; i++) {
            a[i] *= scalar;
        }
    }

    // scale zero-allocation output parameter
    public void scale(double[] a, double scalar, double[] result) {
        if (result == null) return;
        for (int i = 0; i < result.length; i++) {
            double valA = (a == null) ? 0.0 : a[i];
            result[i] = valA * scalar;
        }
    }

    // scale new value
    public double[] scale(double[] a, double scalar) {
        if (a == null) return null;
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * scalar;
        }
        return result;
    }

    // scale batch in place with a single scalar
    public void scaleInPlace(double[][] fieldsSet, double scalar) {
        if (fieldsSet == null) return;
        int dim = fieldsSet.length;
        if (dim == 0) return;
        int numElements = fieldsSet[0].length;
        for (int d = 0; d < dim; d++) {
            if (fieldsSet[d] == null) continue;
            for (int i = 0; i < numElements; i++) {
                fieldsSet[d][i] *= scalar;
            }
        }
    }

    // scale batch zero-allocation output parameter
    public void scale(double[][] fieldsSet, double scalar, double[][] result) {
        if (result == null) return;
        int dim = result.length;
        if (dim == 0) return;
        int numElements = result[0].length;
        for (int d = 0; d < dim; d++) {
            if (result[d] == null) continue;
            for (int i = 0; i < numElements; i++) {
                double valA = (fieldsSet == null || fieldsSet[d] == null) ? 0.0 : fieldsSet[d][i];
                result[d][i] = valA * scalar;
            }
        }
    }

    // scale batch new value with a single scalar
    public double[][] scale(double[][] fieldsSet, double scalar) {
        if (fieldsSet == null) return null;
        int dim = fieldsSet.length;
        if (dim == 0) return new double[0][0];
        int numElements = fieldsSet[0].length;
        double[][] result = new double[dim][numElements];
        for (int d = 0; d < dim; d++) {
            if (fieldsSet[d] == null) continue;
            for (int i = 0; i < numElements; i++) {
                result[d][i] = fieldsSet[d][i] * scalar;
            }
        }
        return result;
    }

    public double[] getUnity(int dimension) {
        double[] unity = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            unity[i] = 1.0;
        }
        return unity;
    }

    public double[] getZero(int dimension) {
        double[] zero = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            zero[i] = 0.0;
        }
        return zero;
    }

    /** END : scaling */

    /** START : Component Wise Multiplication */

    public void componentWiseMultiplicationInPlace(double[][] a, double[][] b) {
        if (a == null || b == null) return; 
        int dim = a.length;
        if (dim == 0) return;
        int numElements = a[0].length;
        for (int d = 0; d < dim; d++) {
            if (a[d] == null || b[d] == null) continue;
            for (int i = 0; i < numElements; i++) {
                a[d][i] *= b[d][i];
            }
        }
    }

    public void componentWiseMultiplication(double[][] a, double[][] b, double[][] result) {
        if (result == null) return;
        int dim = result.length;
        if (dim == 0) return;
        int numElements = result[0].length;
        for (int d = 0; d < dim; d++) {
            if (result[d] == null) continue;
            for (int i = 0; i < numElements; i++) {
                double valA = (a == null || a[d] == null) ? 1.0 : a[d][i];
                double valB = (b == null || b[d] == null) ? 1.0 : b[d][i];
                result[d][i] = valA * valB;
            }
        }
    }

    public double[][] componentWiseMultiplication(double[][] a, double[][] b) {
        if (a == null && b == null) return null;
        double[][] ref = (a != null) ? a : b;
        int dim = ref.length;
        if (dim == 0) return new double[0][0];
        int numElements = ref[0].length;
        
        double[][] result = new double[dim][numElements];
        for (int d = 0; d < dim; d++) {
            for (int i = 0; i < numElements; i++) {
                double valA = (a == null || a[d] == null) ? 1.0 : a[d][i];
                double valB = (b == null || b[d] == null) ? 1.0 : b[d][i];
                result[d][i] = valA * valB;
            }
        }
        return result;
    }

    /** END : Component Wise Multiplication */
}
