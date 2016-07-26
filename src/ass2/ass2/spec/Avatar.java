package ass2.ass2.spec;

import javax.media.opengl.GL2;

/**
 * Created by Joshua on 14/10/2015.
 */
public class Avatar {

    double x;
    double y;
    double z;
    double rotation;
    Drawing d;


    double hR; // dym by control of the move arm
    boolean hflag; // change movement or not
    double dg; // range of movement
    double tR; // dym by control of move leg

    Avatar() {
        x = 0;
        y = 0;
        z = 0;
        rotation = 0;
        d = new Drawing();

        hR = 0;
        hflag = true;
        dg = 12;
        tR = 0;

    }

    public void draw(GL2 gl, Texture myTexture[]){
        // TODO: draw Avatar here

        gl.glPushMatrix();

        gl.glTranslated(x, y, z);
        gl.glRotatef(-90, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(-(float)(rotation*10), 0.0f, 0.0f, 1.0f);

        //System.out.print(x + " " + y + " " + rotation + "\n");

        // head
        gl.glRotatef(90, 1.0f, 0.0f, 0.0f);
        gl.glTranslated(0.0f, -0.05, 0.0f);
        d.drewEyes(gl, 0.1, 20, 24, myTexture[1]);

        // body
        gl.glPushMatrix();
        gl.glTranslated(0, -0.3, 0);
        d.drewEyes(gl, 0.15, 20, 24, myTexture[1]);
        gl.glPopMatrix();

        // arm left
        gl.glPushMatrix();
        gl.glTranslated(0, -0.1, 0);
        gl.glRotated(-45, 1, 0, 0);
        gl.glRotated(hR, 0, 1, 0);
        d.drawCylinder(gl, 10, -0.4, -0.15, 0.02, myTexture[0]);
        gl.glPopMatrix();

        // arm right
        gl.glPushMatrix();
        gl.glTranslated(0, -0.1, 0);
        gl.glRotated(45, 1, 0, 0);
        gl.glRotated(hR, 0, 1, 0);
        d.drawCylinder(gl, 10, 0.15, 0.4, 0.02, myTexture[0]);
        gl.glPopMatrix();

        // leg left
        gl.glPushMatrix();
        gl.glTranslated(0, -0.45, -0.08);
        gl.glTranslated(0, tR, 0);
        gl.glRotated(-90, 1, 0, 0);
        gl.glRotated(-hR, 0, 1, 0);
        d.drawCylinder(gl, 10, -0.4, 0, 0.02, myTexture[0]);
        gl.glPopMatrix();

        // leg right
        gl.glPushMatrix();
        gl.glTranslated(0, -0.45, 0.08);
        gl.glTranslated(0, -tR, 0);
        gl.glRotated(90, 1, 0, 0);
        gl.glRotated(-hR, 0, 1, 0);
        d.drawCylinder(gl, 10, 0, 0.4, 0.02, myTexture[0]);
        gl.glPopMatrix();

        gl.glPopMatrix();
    }

}
