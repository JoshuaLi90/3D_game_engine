package ass2.ass2.spec;

import javax.media.opengl.GL2;

/**
 * Created by Joshua on 18/10/2015.
 */
public class Drawing {
    public double[] p1;// = new double[3];
    public double[] p2;// = new double[3];
    public double[] p3;// = new double[3];
    public double[] p4;// = new double[3];

    public double offset = 0;

    Drawing ()
    {;}

    // for the ground
    Drawing (double[] p1, double[] p2, double[] p3, double[] p4, double offest) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.offset = offest;
        //this.myTexture = myTexture;
    }

    // drawing the ground
    public void draw(GL2 gl, Terrain ter, Texture myTexture) {

        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_BORDER);
        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_BORDER);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_MIRRORED_REPEAT);
        
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_NEAREST);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());

        if (offset != -99) {
        //gl.glEnable(GL2.GL_POLYGON_OFFSET_POINT);
        //gl.glEnable(GL2.GL_POLYGON_OFFSET_LINE);
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);

        gl.glPolygonOffset(-20, -20);
        }
        
        gl.glBegin(GL2.GL_TRIANGLES);
        
        //double[] p1 = {x,this.getGridAltitude(x, z),z};
        //double[] p2 = {x,this.getGridAltitude(x, z+1),z+1};
        //double[] p3 = {x+1,this.getGridAltitude(x+1, z),z};
        //double[] p4 = {x+1,this.getGridAltitude(x+1, z+1),z+1};

        if (offset == -99 || ter.coolStyle){
            double rt = 0.2;
            p1[1] = ter.getSoft(p1[0], p1[2], rt);
            p2[1] = ter.getSoft(p2[0], p2[2], rt);
            p3[1] = ter.getSoft(p3[0], p3[2], rt);
            p4[1] = ter.getSoft(p4[0], p4[2], rt);
        }
        else {
            p1[1] = ter.altitude(p1[0], p1[2]) + offset;
            p2[1] = ter.altitude(p2[0], p2[2]) + offset;
            p3[1] = ter.altitude(p3[0], p3[2]) + offset;
            p4[1] = ter.altitude(p4[0], p4[2]) + offset;
        }

        double[] n1 = MathUtil.normal(p1, p2, p4);
        double[] n2 = MathUtil.normal(p2, p3, p4);


        gl.glEnable(GL2.GL_TEXTURE_GEN_R);
        //gl.glEnable(GL2.GL_TEXTURE_GEN_S);

        //gl.glColor3f(1f, 0f, 0f);
        gl.glNormal3d(n1[0],n1[1],n1[2]);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(p1[0], p1[1], p1[2]);
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(p2[0], p2[1], p2[2]);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(p4[0], p4[1], p4[2]);

        //gl.glColor3f(0f, 1f, 0f);
        gl.glNormal3d(n2[0],n2[1],n2[2]);
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(p2[0], p2[1], p2[2]);
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(p3[0], p3[1], p3[2]);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(p4[0], p4[1], p4[2]);

       //gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_R);
        
        gl.glEnd();
        
        if (offset != -99) {
        //gl.glDisable(GL2.GL_POLYGON_OFFSET_POINT);
        //gl.glDisable(GL2.GL_POLYGON_OFFSET_LINE);
        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

        gl.glPolygonOffset(0, 0);
        }

        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
    }

    // dym cut into small chunk
    public void styleDraw(GL2 gl, Terrain ter, int iteTimes, Texture myTexture) {
        double[] midpoint = new double[3];
        double[] midtop = new double[3];
        double[] midleft = new double[3];
        double[] middown = new double[3];
        double[] midright = new double[3];

        // middle point
        double midx = (p1[0] + p2[0] + p3[0] + p4[0])/4;
        double midz = (p1[2] + p2[2] + p3[2] + p4[2])/4;
        midpoint[0] = midx;
        midpoint[2] = midz;

        // top
        double midx1 = (p2[0] + p3[0])/2;
        double midz1 = (p2[2] + p3[2])/2;
        midtop[0] = midx1;
        midtop[2] = midz1;

        // left
        double midx2 = (p3[0] + p4[0])/2;
        double midz2 = (p3[2] + p4[2])/2;
        midleft[0] = midx2;
        midleft[2] = midz2;

        // bottom
        double midx3 = (p4[0] + p1[0])/2;
        double midz3 = (p4[2] + p1[2])/2;
        middown[0] = midx3;
        middown[2] = midz3;

        // right
        double midx4 = (p1[0] + p2[0])/2;
        double midz4 = (p1[2] + p2[2])/2;
        midright[0] = midx4;
        midright[2] = midz4;

        if (iteTimes == 0) {
            Drawing lefttop = new Drawing(midpoint, midtop, p3, midleft, this.offset);
            lefttop.draw(gl, ter, myTexture);
            Drawing righttop = new Drawing(midright, p2, midtop, midpoint, this.offset);
            righttop.draw(gl, ter, myTexture);
            Drawing leftbot = new Drawing(middown, midpoint, midleft, p4, this.offset);
            leftbot.draw(gl, ter, myTexture);
            Drawing rightbot = new Drawing(p1, midright, midpoint, middown, this.offset);
            rightbot.draw(gl, ter, myTexture);
        }
        if (iteTimes > 0) {
            double t1 = abs(p1, p2, ter) + abs(p4, p3, ter);
            double t2 = abs(p2, p3, ter) + abs(p1, p4, ter);
            if (t1 > 1.5 * t2){
                Drawing up = new Drawing(midright, p2, p3, midleft, this.offset);
                up.styleDraw(gl, ter, iteTimes - 1, myTexture);
                Drawing down = new Drawing(p1, midright, midleft, p4, this.offset);
                down.styleDraw(gl, ter, iteTimes - 1, myTexture);
            }
            else if (t2 > 1.5 * t1) {
                Drawing left = new Drawing(middown, midtop, p3, p4, this.offset);
                left.styleDraw(gl, ter, iteTimes - 1, myTexture);
                Drawing right = new Drawing(p1, p2, midtop, middown, this.offset);
                right.styleDraw(gl, ter, iteTimes - 1, myTexture);
            }
            else {
                Drawing lefttop = new Drawing(midpoint, midtop, p3, midleft, this.offset);
                lefttop.styleDraw(gl, ter, iteTimes - 1, myTexture);
                Drawing righttop = new Drawing(midright, p2, midtop, midpoint, this.offset);
                righttop.styleDraw(gl, ter, iteTimes - 1, myTexture);
                Drawing leftbot = new Drawing(middown, midpoint, midleft, p4, this.offset);
                leftbot.styleDraw(gl, ter, iteTimes - 1, myTexture);
                Drawing rightbot = new Drawing(p1, midright, midpoint, middown, this.offset);
                rightbot.styleDraw(gl, ter, iteTimes - 1, myTexture);
            }
        }
    }

    // math
    public double abs(double[] a, double[] b, Terrain ter) {
        double t0 = a[0] - b[0];
        if (t0 < 0) t0 = -t0;
        double t1 = ter.altitude(a[0],a[2]) - ter.altitude(a[0],b[2]);
        if (t1 < 0) t1 = -t1;
        double t2 = a[2] - b[2];
        if (t2 < 0) t2 = -t2;

        double temp = (t0 * t0) + (t1 * t1) + (t2 * t2);
        return Math.sqrt(temp);
    }

    // drawing a cylinder
    public void drawCylinder(GL2 gl, int slices, double zFront, double zBack, double size, Texture myTexture) {

        double angleIncrement = (Math.PI * 2.0) / slices;

        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_NEAREST);

        gl.glPushMatrix();
        gl.glBegin(GL2.GL_QUAD_STRIP);
        {
            for (int i = 0; i <= slices; i++) {
                double angle0 = i * angleIncrement;
                //double angle1 = (i + 1) * angleIncrement;
                double xPos0 = Math.cos(angle0)*size;
                double yPos0 = Math.sin(angle0)*size;
                double sCoord = 1.0 / slices * i; //Or * 2 to repeat label

                gl.glNormal3d(xPos0, yPos0, 0);
                gl.glTexCoord2d(sCoord, 1);
                gl.glVertex3d(xPos0, yPos0, zFront);
                gl.glTexCoord2d(sCoord, 0);
                gl.glVertex3d(xPos0, yPos0, zBack);

            }
        }
        gl.glEnd();
        gl.glPopMatrix();
        
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

    }

    // drawing a sphere
    public void drewEyes(GL2 gl, double radius, int stacks, int slices, Texture myTexture) {

        double x1, x2, z1, z2, y1, y2;

        for (int i = 0; i < stacks; i++) {
            double t = -0.25 + i * (0.5 / stacks);
            double t1 = -0.25 + (i+1) * (0.5 / stacks);

            gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
            gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
            //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_NEAREST);

            gl.glPushMatrix();
            gl.glBegin(GL2.GL_TRIANGLE_STRIP);
            for (int j = 0; j <= slices; j++) {

                x1 = radius * MathUtil.r(t) * Math.cos(j* 2.0 * Math.PI / slices);
                x2 = radius * MathUtil.r(t1) * Math.cos(j* 2.0 * Math.PI / slices);
                y1 = radius * MathUtil.getY(t);

                z1 = radius * MathUtil.r(t) * Math.sin(j* 2.0 * Math.PI / slices);
                z2 = radius * MathUtil.r(t1) * Math.sin(j* 2.0 * Math.PI / slices);
                y2 = radius * MathUtil.getY(t1);

                double normal[] = {x1, y1, z1};


                MathUtil.normalize(normal);
                gl.glNormal3dv(normal, 0);

                double tCoord = 1.0 / stacks * i; //Or * 2 to repeat label
                double sCoord = 1.0 / slices * j;
                gl.glTexCoord2d(sCoord, tCoord);
                gl.glVertex3d(x1, y1, z1);
                normal[0] = x2;
                normal[1] = y2;
                normal[2] = z2;

                MathUtil.normalize(normal);
                gl.glNormal3dv(normal, 0);

                tCoord = 1.0 / stacks * (i + 1); //Or * 2 to repeat label
                gl.glTexCoord2d(sCoord, tCoord);
                gl.glVertex3d(x2, y2, z2);

            }
            gl.glEnd();
            gl.glPopMatrix();
            
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        }
    }
}
