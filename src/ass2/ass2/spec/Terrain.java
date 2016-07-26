package ass2.ass2.spec;

//import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import javax.media.opengl.GL2;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;

    private float[] mySunlight;

    double p1, p2, p3, p4;
    double rx, rz;
    double on12, on24;
    double diax, diaz;
    double gap, rate;

    double n1[] = new double[3];
    double n2[] = new double[3];

    public boolean coolStyle;

    float pSin, nCos;

    float[] ambient = {0.9f, 0.9f, 0.9f, 1f};
    float[] specular = {2f, 2f, 2f, 1f};

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
        //myBezier = new Bezier(this);
        coolStyle = false;
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        if (
            x > size().width - 1 ||
            x < 0 ||
            z > size().height - 1 ||
            z < 0 )
            return getGridAltitude(0, 0);
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        // TODO: calculate the height

        //
        //  p1   p3
        //     /
        //  p2   p4
        //
        p1 = getGridAltitude((int)Math.floor(x), (int)Math.floor(z));
        p2 = getGridAltitude((int)Math.floor(x), (int)Math.floor(z) + 1);//getGridAltitude((int)x, (int)z + 1);
        p3 = getGridAltitude((int)Math.floor(x) + 1, (int)Math.floor(z));//getGridAltitude((int)x + 1, (int)z);
        p4 = getGridAltitude((int)Math.floor(x) + 1, (int)Math.floor(z) + 1);//getGridAltitude((int)x + 1, (int)z + 1);

        rx = MathUtil.relativeLocation(x);
        rz = MathUtil.relativeLocation(z);

        on12 = p1 + (p2 - p1) * rz;
        //double on13 = p1 + (p3 - p1) * rx;
        on24 = p2 + (p4 - p2) * rx;
        //double on34 = p3 + (p4 - p3) * rz;

        diax = p2 + (p3 - p2) * rx;
        diaz = p2 + (p3 - p2) * (1-rz);

        // on the diagonal
        if (rx == 1 - rz) return diax;

        // left hand side
        else if (rx < 1 - rz) {
            gap = diaz - on12;
            rate = rx / (1-rz);
            return on12 + (gap * rate);
        }

        // right hand side
        else if (rx > 1 - rz) {
            gap = diax - on24;
            rate = (1-rz) / rx;
            return on24 + (gap * rate);
        }
        return 0; // should be never happened
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        Tree tree = new Tree(x, altitude(x, z), z);
        myTrees.add(tree);
    }


    /**
     * Add a road
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }

    public void draw(GL2 gl, Texture myTexture[]){
        //gl.glPushMatrix();

        pSin = (float) (Math.sin(getSunPos())); // increase then decrease, always larger than 0
        nCos = (float) (-Math.cos(getSunPos()));// increasing from -1 to 1

        float[] sunColor = {1.0f,pSin + 0.2f,pSin + 0.2f,1.0f};
        gl.glClearColor(pSin, pSin, pSin, 1.0f);

        float[] sunPos = {(float) (
                nCos * mySize.getWidth()),
                pSin * 10,
                (float)(mySize.getHeight()/2), // from midtop to midbottom
                0};

        // Light properties.
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, sunPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sunColor, 0);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);


        if (coolStyle) {
            Drawing d;
            for (int z = 0; z < mySize.width - 1; z++) {
                for (int x = 0; x < mySize.height - 1; x++) {
                    double[] v1 = {x, 0, z};
                    double[] v2 = {x, 0, z + 1};
                    double[] v3 = {x + 1, 0, z};
                    double[] v4 = {x + 1, 0, z + 1};
                    d = new Drawing(v1, v2, v4, v3, -99);
                    d.styleDraw(gl, this, 3, myTexture[3]);
                }
            }
        }
        else {
            gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_NEAREST);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[3].getTextureId());

            gl.glBegin(GL2.GL_TRIANGLES);
            {
                for (int z = 0; z < mySize.width - 1; z++) {
                    for (int x = 0; x < mySize.height - 1; x++) {

                        double[] v1 = {x, this.getGridAltitude(x, z), z};
                        double[] v2 = {x, this.getGridAltitude(x, z + 1), z + 1};
                        double[] v3 = {x + 1, this.getGridAltitude(x + 1, z), z};
                        double[] v4 = {x + 1, this.getGridAltitude(x + 1, z + 1), z + 1};

                        n1 = MathUtil.normal(v1, v2, v3);
                        n2 = MathUtil.normal(v4, v2, v3);

                        // left
                        //gl.glColor3f(0, 1, 0);
                        //gl.glEnable(GL2.GL_TEXTURE_GEN_S);
                        gl.glEnable(GL2.GL_TEXTURE_GEN_T);

                        gl.glNormal3d(n1[0], n1[1], n1[2]);

                        gl.glTexCoord2d(0, 1);
                        gl.glVertex3d(x, getGridAltitude(x, z), z);
                        gl.glTexCoord2d(0, 0);
                        gl.glVertex3d(x, getGridAltitude(x, z + 1), z + 1);
                        gl.glTexCoord2d(1, 1);
                        gl.glVertex3d(x + 1, getGridAltitude(x + 1, z), z);

                        // right
                        //gl.glColor3f(0, 1, 0);
                        gl.glNormal3d(-n2[0], -n2[1], -n2[2]);

                        gl.glTexCoord2d(1, 1);
                        gl.glVertex3d(x + 1, getGridAltitude(x + 1, z), z);
                        gl.glTexCoord2d(0, 0);
                        gl.glVertex3d(x, getGridAltitude(x, z + 1), z + 1);
                        gl.glTexCoord2d(1, 0);
                        gl.glVertex3d(x + 1, getGridAltitude(x + 1, z + 1), z + 1);

                        //gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                        gl.glDisable(GL2.GL_TEXTURE_GEN_T);
                    }
                }
            }
            gl.glEnd();
        }

        // draw tree
        for (Tree t : myTrees) {
            gl.glColor3f(0,1,0);
            t.draw(gl, myTexture);
        }

        // draw road
        for (Road r : myRoads) {
            r.draw(gl, this, myTexture[2]);
        }

        //gl.glPopMatrix();
    }

    public double getSoft(double x, double z, double r) {
        double result = altitude(x, z);
        double tl = r*0.6;
        if (x > r && x < size().width-r && z > r && z < size().height-r) {
            //result *= 2;
            result += altitude(x - r, z) + altitude(x, z + r) + altitude(x + r, z) + altitude(x, z - r);
            result += altitude(x - tl, z + tl) + altitude(x + tl, z + tl) + altitude(x - tl, z - tl) + altitude(x + tl, z - tl);
            result /= 9;
        }
        else if (x > r && x < size().width - r) {
            result += altitude(x - r, z) + altitude(x + r ,z);
            result /= 3;
        }
        else if (z > r && z < size().width - r) {
            result += altitude(x, z - r) + altitude(x ,z + r);
            result /= 3;
        }
        return result-0.05;
    }

    private float getSunPos() {
        Calendar calendar = Calendar.getInstance();
        //System.out.print(calendar.get(Calendar.SECOND) + " " + calendar.get(Calendar.MILLISECOND) + "\n");
        float result =  (float) (0.003 * (calendar.get(Calendar.SECOND)*1000 + calendar.get(Calendar.MILLISECOND)));// 180'
        //System.out.print(minute + "\n");
        return MathUtil.getRadians(result);
    }

    public void beCool() {
        coolStyle = !coolStyle;
        for (Road r : myRoads)
            r.beCool();
    }
}
