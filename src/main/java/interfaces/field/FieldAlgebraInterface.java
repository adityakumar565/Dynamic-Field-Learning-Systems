package interfaces.field;

public interface FieldAlgebraInterface {

    /** START : addition */
    public void addInPlace(double[] a, double[] b);

    public void add(double[] a, double[] b, double[] result);

    public double[] add(double[] a, double[] b);

    public void addInPlace(double[][] fieldsSet1, double[][] fieldsSet2);

    public void add(double[][] fieldsSet1, double[][] fieldsSet2, double[][] result);

    public double[][] add(double[][] fieldsSet1, double[][] fieldsSet2);

    /** END : addition */

    /** START : dot product */
    public double dotProduct(double[] a, double[] b);

    public void dotProduct(double[][] fieldsSet1, double[][] fieldsSet2, double[] result);

    public double[] dotProduct(double[][] fieldsSet1, double[][] fieldsSet2);

    /** END : dot product */

    /** START : norm */
    public double norm(double[] a);

    public void norm(double[][] fieldsSet, double[] result);

    public double[] norm(double[][] fieldsSet);

    /** END : norm */

    /** START : scaling */
    public void scaleInPlace(double[] a, double scalar);

    public void scale(double[] a, double scalar, double[] result);

    public double[] scale(double[] a, double scalar);

    public void scaleInPlace(double[][] fieldsSet, double scalar);

    public void scale(double[][] fieldsSet, double scalar, double[][] result);

    public double[][] scale(double[][] fieldsSet, double scalar);

    /** END : scaling */

    /** START : special vectors */
    public double[] getUnity(int dimension);

    public double[] getZero(int dimension);

    /** END : special vectors */

    /** START : Component Wise Multiplocation */

    public void componentWiseMultiplicationInPlace(double[][] a, double[][] b);

    public void componentWiseMultiplication(double[][] a, double[][] b, double[][] result);

    public double[][] componentWiseMultiplication(double[][] a, double[][] b);

    /** END : Component Wise Multiplocation */
}
