package ass2.ass2.spec;

/**
 * Created by Joshua on 14/10/2015.
 */
public class MathUtil {

    /**
     * Normalise an angle to the range [-180, 180)
     *
     * @param angle
     * @return
     */
    public static double normalizeAngle (double angle) {
        return ((angle + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
    }

    /**
     * Multiply two matrices
     *
     * @param p A 3x3 matrix
     * @param q A 3x3 matrix
     * @return
     */
    public static double[][] multiply(double[][] p, double[][] q) {

        double[][] m = new double[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                m[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                    m[i][j] += p[i][k] * q[k][j];
                }
            }
        }

        return m;
    }

    /**
     * Multiply a vector by a matrix
     *
     * @param m A 3x3 matrix
     * @param v A 3x1 vector
     * @return
     */
    public static double[] multiply(double[][] m, double[] v) {

        double[] u = new double[3];

        for (int i = 0; i < 3; i++) {
            u[i] = 0;
            for (int j = 0; j < 3; j++) {
                u[i] += m[i][j] * v[j];
            }
        }

        return u;
    }

    public static boolean testInteger(double input) {
        if((int) input == input) {
            //System.out.println("yes");
            return true;
        }
        else {
            //System.out.println("no");
            return false;
        }
    }

    public static double relativeLocation(double input) {
        return input - (int)input;
    }

    public static double[] crossProduct(double[] v1, double[] v2){
        double[] result = {
                v1[1] * v2[2] - v1[2] * v2[1],
                v1[2] * v2[0] - v1[0] * v2[2],
                v1[0] * v2[1] - v1[1] * v2[0]};
        return result;
    }

    public static double[] normal(double[] va, double[] vb, double[] vc) {
        double[] v1 = {
                va[0] - vb[0],
                va[1] - vb[1],
                va[2] - vb[2]};
        double[] v2 = {
                va[0] - vc[0],
                va[1] - vc[1],
                va[2] - vc[2]};
        return crossProduct(v1, v2);
    }

    public static double[] normaliseVector(double[] v){
        double d = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (d == 0.0) {
            return null;
        }
        double[] result = { v[0] / d, v[1] / d, v[2] / d};
        return result;
    }

    public static void normalize(double[] v) {
        double d = Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
        if (d != 0.0)
        {
            v[0]/=d;
            v[1]/=d;
            v[2]/=d;
        }
    }

    public static int factorial(int input) {
        int sum = 1;
        if(input < 0)
            throw new IllegalArgumentException();
        if(input == 1){
            return 1;
        }
        sum = input * factorial(input - 1);
        return sum;
    }

    public static double[][] scaleMatrix(double size) {
        double[][] r = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                r[i][j] = 0;
            }
        }

        r[0][0] = size;
        r[1][1] = size;
        r[2][2] = size;
        r[3][3] = 1;

        return r;
    }

    public static float getRadians(float degree) {
        return (float)Math.toRadians(degree);
    }

    public static double getRelativeX(double rotation) {
        return Math.cos(normalizeAngle((rotation/36))*2*Math.PI);
    }

    public static double getRelativeZ(double rotation) {
        return Math.sin(normalizeAngle((rotation/36))*2*Math.PI);
    }

    public static double r(double t){
        return Math.cos(2 * Math.PI * t);
    }

    public static double getY(double t){
        return Math.sin(2 * Math.PI * t);
    }
}
