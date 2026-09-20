package test.Field;

import fields.fieldV1impl.FieldAlgebra;
import fields.fieldV1impl.HashTableFieldsImplementation;
import interfaces.field.FieldAlgebraInterface;
import interfaces.field.FieldInterface;
import java.util.Arrays;

public class FieldsTest {

    public static void main(String[] args) {
        System.out.println("========== FIELD ALGEBRA TESTS ==========");
        double[] vector1 = new double[] { 1.0, 2.0 };
        double[] vector2 = new double[] { 3.0, 4.0 };
        double[] result = new double[] { 0.0, 0.0 };

        FieldAlgebraInterface fieldAlgebra = new FieldAlgebra();

        fieldAlgebra.addInPlace(vector1, vector2);
        System.out.println("addInPlace (V1 becomes V1+V2): " + Arrays.toString(vector1));

        // Reset V1
        vector1 = new double[] { 1.0, 2.0 };

        fieldAlgebra.add(vector1, vector2, result);
        System.out.println("add (V1 + V2 = Result): " + Arrays.toString(result));

        double[] result2 = fieldAlgebra.add(vector1, vector2);
        System.out.println("add (Result2 = V1 + V2): " + Arrays.toString(result2));

        double dotProduct = fieldAlgebra.dotProduct(vector1, vector2);
        System.out.println("Dot Product: " + dotProduct);

        double norm = fieldAlgebra.norm(vector1);
        System.out.println("Norm: " + norm);

        double[] scaledVector = fieldAlgebra.scale(vector1, 2.0);
        System.out.println("Scaled Vector: " + Arrays.toString(scaledVector));

        double[] unity = fieldAlgebra.getUnity(2);
        System.out.println("Unity: " + Arrays.toString(unity));

        double[] zero = fieldAlgebra.getZero(2);
        System.out.println("Zero: " + Arrays.toString(zero));

        System.out.println("\n========== HASH TABLE FIELDS TESTS ==========");

        FieldInterface field = new HashTableFieldsImplementation(100, 2, 2, fieldAlgebra);

        // Single Puts
        field.put(new double[] { 0, 0 }, zero);
        field.put(new double[] { 1, 1 }, unity);
        field.put(new double[] { 1, 2 }, vector1);
        field.put(new double[] { 3, 4 }, vector2);
        System.out.println("After Single Puts:");
        System.out.println(field);

        // Batch Puts (DOD Layout: [dimension][numElements])
        // 4 new states: [10, 10], [11, 11], [12, 12], [13, 13]
        double[][] batchStates = new double[][] {
                { 10.0, 11.0, 12.0, 13.0 }, // Dimension 0 (X)
                { 10.0, 11.0, 12.0, 13.0 } // Dimension 1 (Y)
        };
        // 4 corresponding fields: [100, 100], [110, 110], [120, 120], [130, 130]
        double[][] batchFields = new double[][] {
                { 100.0, 110.0, 120.0, 130.0 }, // Dimension 0 (X)
                { 100.0, 110.0, 120.0, 130.0 } // Dimension 1 (Y)
        };

        field.putBatch(batchStates, batchFields);
        System.out.println("\nAfter Batch Puts:");
        System.out.println(field);

        // Batch Gets (DOD Layout)
        double[][] resultBatch = new double[][] {
                new double[4], // Dimension 0
                new double[4] // Dimension 1
        };

        // Fetch the 4 states we just added in the batch
        field.getBatch(batchStates, resultBatch);

        System.out.println("\nBatch Get Results (DOD Matrix):");
        System.out.println("Dim 0: " + Arrays.toString(resultBatch[0]));
        System.out.println("Dim 1: " + Arrays.toString(resultBatch[1]));
    }
}
