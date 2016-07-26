package ass2.ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;

import java.util.Random;


/**
 * COMMENT: Comment Game 
 *
 * @author Joshua
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

    private Terrain myTerrain;
    private Avatar myAvatar;
    private Camera myCamera;
    private Texture myTexture[];
    private VBO myVBO;

    private boolean iniAvatar;
    private boolean vboExist;
    private boolean jump;
    int x, y;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
        myAvatar = new Avatar();
        myCamera = new Camera(myTerrain, myAvatar);
        myTexture = new Texture[6];

        Random r = new Random();
        x = r.nextInt(myTerrain.size().width);
        y = r.nextInt(myTerrain.size().height);
        myVBO = new VBO(myTerrain,x,y,0.2);

        iniAvatar = true;
        vboExist = true;
        jump = false;
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {

        // initialisation
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);

        // create a panel to draw on
        GLJPanel panel = new GLJPanel();
        panel.addGLEventListener(this);

        // add a GL Event listener to handle rendering
        panel.addKeyListener(this);

        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();

        getContentPane().add(panel);
        setSize(1024, 768);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        myCamera.move();

        myTexture[0] = new Texture(gl, "yell.jpg","jpg", true);
        myTexture[1] = new Texture(gl, "flake.jpg","jpg", true);
        myTexture[2] = new Texture(gl, "col.jpg","jpg", true);
        myTexture[3] = new Texture(gl, "grass.bmp","bmp", true);
        myTexture[4] = new Texture(gl, "check.jpg","jpg", true);
        myTexture[5] = new Texture(gl, "rock.bmp","bmp", true);

        if (iniAvatar) { // run at the first time to put the Avatar on the right point
            myAvatar.x = myCamera.x;
            myAvatar.z = myCamera.z;
            myAvatar.y = myCamera.attitude + 1;
            myAvatar.rotation = myCamera.rotation;

            iniAvatar = false;
        }
        myAvatar.draw(gl, myTexture);

        myTerrain.draw(gl, myTexture);

        if (vboExist) {
            try {
                myVBO.draw(gl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();
        gl.glDeleteBuffers(2,myVBO.bufferIds,0);

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();

        // Enable depth testing.
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Turn on OpenGL lighting.
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHT1);

        gl.glEnable(GL2.GL_NORMALIZE);

        gl.glEnable(GL2.GL_TEXTURE_2D);

        gl.glFrontFace(GL.GL_CCW);

        myVBO.init(gl);
    }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU glu = new GLU();

        glu.gluPerspective(50.0, (float)width/(float)height, 0.1, 20.0);
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

            case KeyEvent.VK_UP:
                // TODO:
                myCamera.x -= MathUtil.getRelativeX(myCamera.rotation)*0.1;
                myAvatar.x = myCamera.x;
                myCamera.z -= MathUtil.getRelativeZ(myCamera.rotation)*0.1;
                myAvatar.z = myCamera.z;

                myAvatar.y = myCamera.attitude + 1;

                // the move of Avatar
                if (myAvatar.hR > -myAvatar.dg && myAvatar.hflag)
                    myAvatar.hR -= 3;
                else if(myAvatar.hR == -myAvatar.dg)
                    myAvatar.hflag = false;
                if (myAvatar.hR < myAvatar.dg && !myAvatar.hflag)
                    myAvatar.hR += 3;
                else if(myAvatar.hR == myAvatar.dg)
                    myAvatar.hflag = true;

                break;

            case KeyEvent.VK_DOWN:
                // TODO:
                myCamera.x += MathUtil.getRelativeX(myCamera.rotation)*0.1;
                myAvatar.x = myCamera.x;
                myCamera.z += MathUtil.getRelativeZ(myCamera.rotation)*0.1;
                myAvatar.z = myCamera.z;

                myAvatar.y = myCamera.attitude + 1;

                // the move of Avatar
                if (myAvatar.hR > -myAvatar.dg && !myAvatar.hflag)
                    myAvatar.hR -= 3;
                else if(myAvatar.hR == -myAvatar.dg)
                    myAvatar.hflag = true;
                if (myAvatar.hR < myAvatar.dg && myAvatar.hflag)
                    myAvatar.hR += 3;
                else if(myAvatar.hR == myAvatar.dg)
                    myAvatar.hflag = false;
                break;

            case KeyEvent.VK_RIGHT:
                // TODO:
                if (myCamera.rotation < 35.5)
                    myCamera.rotation += 0.5;
                else myCamera.rotation = 0;

                myAvatar.rotation = myCamera.rotation;

                // the move of Avatar
                if (myAvatar.tR == 0) myAvatar.tR = 0.025;
                else myAvatar.tR = -myAvatar.tR;

                break;

            case KeyEvent.VK_LEFT:
                // TODO:
                if (myCamera.rotation > 0)
                    myCamera.rotation -= 0.5;
                else myCamera.rotation = 35.5;

                myAvatar.rotation = myCamera.rotation;

                // the move of Avatar
                if (myAvatar.tR == 0) myAvatar.tR = -0.025;
                else myAvatar.tR = -myAvatar.tR;

                break;

            case KeyEvent.VK_D:
                if (myCamera.rotation > 0)
                    myCamera.rotation -= 0.5;
                else myCamera.rotation = 35.5;
                break;

            case KeyEvent.VK_A:
                if (myCamera.rotation < 35.5)
                    myCamera.rotation += 0.5;
                else myCamera.rotation = 0;
                break;

            case  KeyEvent.VK_W:
                if (myCamera.lookup < 9.9)
                    myCamera.lookup += 0.1;
                break;

            case KeyEvent.VK_S:
                if (myCamera.lookup > 0.1)
                    myCamera.lookup -= 0.1;
                break;

            case KeyEvent.VK_C:
                myCamera.firstPerspective = !myCamera.firstPerspective;
                break;

            case KeyEvent.VK_B:
                myTerrain.beCool();
                break;

            case KeyEvent.VK_SPACE:
                if (!jump) {
                    myCamera.lookup -= 0.8;
                    //myAvatar.y += 0.2;
                    jump = true;
                }
                if (vboExist &&
                        myAvatar.x - x > -0.1 && myAvatar.x - x < 0.1 &&
                        myAvatar.z - y > -0.1 && myAvatar.z - y < 0.1) {
                    System.out.println("You found it! Thanks for Playing!");
                    vboExist = !vboExist;
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // be natural when it stop moving
        myAvatar.hR = 0;
        myAvatar.tR = 0;
        if (jump) {
            myCamera.lookup += 0.8;
            //myAvatar.y -= 0.2;
            jump = false;
        }

        myCamera.rotation = myAvatar.rotation;
    }
}
