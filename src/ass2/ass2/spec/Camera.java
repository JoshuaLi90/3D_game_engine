package ass2.ass2.spec;

import javax.media.opengl.glu.GLU;

/**
 * Created by Joshua on 14/10/2015.
 */
public class Camera {

    double x = 1;
    double z = 2;
    double rotation = 22;
    double attitude = 0;

    private Terrain myTerrain;
    private Avatar myAvatar;

    double rx = 0;
    double rz = 0;
    GLU glu;

    public boolean firstPerspective = false;

    double lookup = 1.5;

    Camera(Terrain t, Avatar a){

        myTerrain = t;
        myAvatar = a;
        glu = new GLU();
    }

    public void move(){
        //TODO: calculate the move of the camera

        attitude = myTerrain.altitude(x, z);

        rx = MathUtil.getRelativeX(rotation);
        rz = MathUtil.getRelativeZ(rotation);

        if (firstPerspective)
            glu.gluLookAt (x, attitude+1.5, z,
                    x-rx, attitude + lookup, z-rz, 0, 1, 0);
        else
            glu.gluLookAt (
                    myAvatar.x + 2*rx, myAvatar.y+lookup, myAvatar.z + 2*rz,
                    myAvatar.x - rx, myAvatar.y, myAvatar.z - rz, 0, 1, 0);
        //System.out.println(x + " " + z + " " + attitude);
    }
}
