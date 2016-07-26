package ass2.ass2.spec;

import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
    Drawing d;
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
        d = new Drawing();
    }
    
    public double[] getPosition() {
        return myPos;
    }

    public void draw(GL2 gl, Texture myTexture[]){
        // TODO: draw one tree

        gl.glPushMatrix();

        gl.glTranslated(myPos[0], myPos[1] + 1,  myPos[2]);
        gl.glRotated(90, 1, 0, 0);

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // draw the leaves
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[3].getTextureId());
        d.drewEyes(gl, 0.5, 20, 24, myTexture[3]);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

        // draw the cylinder
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[5].getTextureId());
        d.drawCylinder(gl, 20, 0.2, 1, 0.1, myTexture[5]);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

        gl.glPopMatrix();
    }

}
