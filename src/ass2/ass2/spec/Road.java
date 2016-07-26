package ass2.ass2.spec;

import javax.media.opengl.GL2;
import java.util.ArrayList;
import java.util.List;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Double> myPoints;
    private double myWidth;

    double[] point0 = new double[3];
    double[] midpoint = new double[3];
    double[] point1 = new double[3];

    double[] planeNormal = {0,1,0};
    double[] tangent = new double[4];
    double[] sizedTangent = new double[4];
    double[] realTangent;
    double[] normalisedTangent;
    double[] vector4 = new double[4];

    double[] lefttop = new double[3];
    double[] righttop = new double[3];

    double[] leftTemp = new double[3];
    double[] rightTemp = new double[3];

    double roadSegment = 0.02;
    double t;
    short ti;
    public short iter = 5;

    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);        
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i; // only after points [0,1)
        
        //i *= 6; // iæ˜¯æ•´æ•°ä½� ä¹˜ä»¥6
        
        double x0 = myPoints.get(i++);//0
        double y0 = myPoints.get(i++);//1

        double x1 = myPoints.get(i++);//2
        double y1 = myPoints.get(i++);//3

        double x2 = myPoints.get(i++);//4
        double y2 = myPoints.get(i++);//5

        double x3 = myPoints.get(i++);//6
        double y3 = myPoints.get(i++);//7
                                      // 8
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }

    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) { // m = 3
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }


    public void draw(GL2 gl, Terrain ter, Texture myTexture) {
        //TODO: Draw road here

        // y is not calculate in this function
        tangent[1] = 0;
        ti = 0;

        while (myPoints.size() > ti + 2) { // after ti + 6
            for (t = ti; t <= ti + 0.98; t += roadSegment) {

                // this point
                point0 = point(t);
                midpoint[0] = point0[0];
                midpoint[2] = point0[1];

                // the next point
                point1 = point(t + roadSegment);
                if (t+ roadSegment > ti+1) {
                    point1 = point(ti+1);
                }

                // direction vector
                tangent[0] = point1[0] - point0[0];
                tangent[2] = point1[1] - point0[1];
                tangent[3] = 1;

                // get the wide-side tangent
                realTangent = MathUtil.crossProduct(planeNormal, tangent);
                normalisedTangent = MathUtil.normaliseVector(realTangent);

                //
                // k  0  0  0    [0]
                // 0  k  0  0  * [1]
                // 0  0  k  0    [2]
                // 0  0  0  1     1
                //
                vector4[0] = normalisedTangent[0];
                vector4[1] = 0;
                vector4[2] = normalisedTangent[2];
                vector4[3] = 1;

                sizedTangent = MathUtil.multiply(MathUtil.scaleMatrix(myWidth / 2), vector4);

                // get the new coordinates
                double[] rightNext = {
                        sizedTangent[0] + midpoint[0],
                        0,
                        sizedTangent[2] + midpoint[2]};

                double[] leftNext = {
                        -sizedTangent[0] + midpoint[0],
                        0,
                        -sizedTangent[2] + midpoint[2]};

                // print out the road
                if (t != 0) {
                Drawing d = new Drawing(rightNext, rightTemp, leftTemp, leftNext, 0.0);
                d.styleDraw(gl, ter, iter, myTexture);}

                rightTemp = rightNext;
                leftTemp = leftNext;
            }

            // last flame
            righttop[0] = myPoints.get(ti+6) + sizedTangent[0];
            righttop[2] = myPoints.get(ti+7) + sizedTangent[2];

            lefttop[0] = myPoints.get(ti+6) - sizedTangent[0];
            lefttop[2] = myPoints.get(ti+7) - sizedTangent[2];

            // y will be calculated inside the drawing function
            Drawing d = new Drawing(righttop, rightTemp, leftTemp, lefttop, 0.0);
            d.styleDraw(gl, ter, iter, myTexture);

            rightTemp = righttop;
            leftTemp = lefttop;

            //System.out.println(rightTemp[0] + " " + rightTemp[2] + ", " + leftTemp[0] + " " + leftTemp[2]);
            //System.out.println(righttop[0] + " " + righttop[2] + ", " + lefttop[0] + " " + lefttop[2]);
            //System.out.println(myPoints.get(ti+6) + " " + myPoints.get(ti+7));
            //System.out.println(sizedTangent[0] + " " + sizedTangent[2]);

            ti+=6;
        }
    }
    public void beCool() {
        if (iter == 5) {
            iter = 6;
        }
        else if (iter == 6) {
            iter = 5;
        }
    }
}
