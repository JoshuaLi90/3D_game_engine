package ass2.ass2.spec;

import com.jogamp.common.nio.Buffers;


import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


/**
 * Created by Joshua on 19/10/2015.
 */
public class VBO {

    // size of short is 2
    // size of float is 16

    private float positions[] =  {
            -1,1,0, // left     0
            0,0,0,  // bottom   1
            0,2,0,  // top      2
            1,1,0,  // right    3
            0,1,-1, // forward  4
            0,1,1   // backward 5
    };

    private float normals[] = {
            -1,0,0, // 0
            0,-1,0,
            0,1,0,

            1,0,0,
            0,0,-1,
            0,0,1  // 5
    };

    //There should be a matching entry in this array for each entry in
    //the positions array
    private float colors[] = {
            1,0,0,
            0,1,0,
            0,0,1,

            1,0,0,
            0,1,0,
            0,0,1
    };

    //Best to use smallest data type possible for indexes
    //We could even use byte here...
    private short indexes[] = {
            5,0,2,
            1,0,5,
            1,5,3,
            3,5,2,

            4,3,2,
            4,1,3,
            0,1,4,
            2,0,4
    };
    private float texCoords[] = {
            1, 1,//0
            0, 0,
            0, 0,

            1, 1,
            0, 1,
            1, 0 // 5
    };

    //These are not vertex buffer objects, they are just java containers
    private FloatBuffer  posData;
    private FloatBuffer colorData;
    private ShortBuffer indexData;
    private FloatBuffer texBuffer;
    private FloatBuffer normBuffer;

    //We will be using 2 vertex buffer objects
    public int bufferIds[];

    public VBO(Terrain ter, double x, double z, double times){
        float max = 0;
        float temp = 0;
        for (int i = 0; i < positions.length; i += 3) { // translate && scale
            positions[i] *= times;
            positions[i] += x;

            positions[i+2] *= times;
            positions[i+2] += z;

            positions[i+1] *= times;
            temp = (float)ter.altitude(positions[i],positions[i+2]);
            if (temp > max)
                max = temp;
            //positions[i+1] += (float)ter.altitude(positions[i],positions[i+2]); // åœ°é�¢ä¸�å¹³ï¼�ï¼�ï¼�

            //System.out.print(positions[i] + " " + positions[i+1] + " " + positions[i+2] + "\n");
        }
        for (int i = 0; i < positions.length; i += 3) {
            positions[i+1] += max;
        }
        posData = Buffers.newDirectFloatBuffer(positions);
        colorData = Buffers.newDirectFloatBuffer(colors);
        indexData = Buffers.newDirectShortBuffer(indexes);
        texBuffer = Buffers.newDirectFloatBuffer(texCoords);
        normBuffer = Buffers.newDirectFloatBuffer(normals);

        bufferIds = new int[2];
    }

    public void draw(GL2 gl) throws Exception {

        //int myShaderProgram = Shader.initShaders(gl, "VertexTex.glsl", "FragmentTex.glsl");
        //gl.glUseProgramObjectARB(myShaderProgram);

        //Texture myTexture = new Texture(gl, "flake.jpg","jpg",true);

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);

        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINE);
        gl.glActiveTexture(GL.GL_TEXTURE0);


        //Bind the buffer we want to use
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);


        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

        // This tells OpenGL the locations for the co-ordinates and color arrays.
        gl.glVertexPointer(3, //3 coordinates per vertex
                GL.GL_FLOAT, //each co-ordinate is a float
                0, //There are no gaps in data between co-ordinates
                0); //Co-ordinates are at the start of the current array buffer
        gl.glColorPointer(3, GL.GL_FLOAT, 0,
                positions.length * 4); //colors are found after the position co-ordinates in the current array buffer

        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0,
                positions.length * 4 + colors.length * 4);

        gl.glNormalPointer(GL.GL_FLOAT, 0,
                positions.length*4 + colors.length*4 + texCoords.length*4);
        //gl.glNormalPointer(GL.GL_FLOAT,0,0); + colors.length * 16 + texCoords.length*16

        //Also need to bind the current element array buffer
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

        for (int i = 0; i < 8; i++) {
            gl.glDrawElements(GL2.GL_TRIANGLES, 3, GL2.GL_UNSIGNED_SHORT, (i*3 * 2));
        }

        //Disable
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,0);
    }

    public void init(GL2 gl) {
        //Generate 2 VBO buffer and get their IDs
        gl.glGenBuffers(2, bufferIds, 0);

        //This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);

        //This is just setting aside enough empty space
        //for all our data

        // parent Buffer
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer
                positions.length * 4 + colors.length * 4 + texCoords.length * 4 + normals.length * 4, //size needed
                null,    //We are not actually loading data here yet
                GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it

        //positions
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
                0, //From byte offset 0
                positions.length * 4, posData);

        //color
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
                positions.length * 4,  //Load after the position data
                colors.length * 4, colorData);
        //texture
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
                positions.length * 4 + colors.length * 4,
                texCoords.length * 4, texBuffer);

        //normal
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER,
                positions.length * 4 + colors.length * 4 + texCoords.length * 4,
                normals.length * 4, normBuffer);


        //The element array
        //Element arrays hold indexes to an array buffer
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,
                indexes.length * 2,
                indexData, GL2.GL_STATIC_DRAW);
    }
}
