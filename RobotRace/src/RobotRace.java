
/**
 * Assignment for course 2IV60 Computer Graphics of students:
 * Theodoros Margomenos
 * Marcelo Almeida
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;
import robotrace.Base;
import robotrace.Vector;
import static java.lang.Math.*;
import static java.lang.System.out;
import java.nio.FloatBuffer;
import java.util.Calendar;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FALSE;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL.GL_LINE_STRIP;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_POINTS;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL.GL_TRUE;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2ES1.GL_DECAL;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2ES1.GL_TEXTURE_ENV;
import static javax.media.opengl.GL2ES1.GL_TEXTURE_ENV_MODE;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_FLAT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import javax.swing.Timer;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 * 
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 * 
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards;
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 * 
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the same folder as this
 * file. These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 * 
 * gl.glColor3f(1f, 1f, 1f);
 * track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd(); 
 * 
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */
public class RobotRace extends Base {
    
    /** Array of the four robots. */
    private final Robot[] robots;
    
    /** Instance of the camera. */
    private final Camera camera;
    
    /** Instance of the race track. */
    private final RaceTrack raceTrack;
    
    /** Instance of the terrain. */
    private final Terrain terrain;
    
    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */
    public RobotRace() {
        
        // Create a new array of four robots
        robots = new Robot[4];
        
        // Initialize robot 0        
        robots[0] = new Robot(Material.GOLD, new Vector(5,0,1)
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER, new Vector(6,0,1)
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD, new Vector(7,0,1)
            /* add other parameters that characterize this robot */);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE, new Vector(8,0,1)
            /* add other parameters that characterize this robot */);
        
        // Initialize the camera
        camera = new Camera();
        
        // Initialize the race track
        raceTrack = new RaceTrack();
        
        // Initialize the terrain
        terrain = new Terrain();
    }
    
    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {        
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
             
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
        
        gl.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        
        
        configureLighting();
    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        /** Calculating the fovy angle. First calculate the height with the 
         * expression = gs.vWidth * (gs.w/gs.h). This expression is a proportion
         * related to the aspect of the view. Then, calculates the atan of
         * (height/2)/gs.vDist, to determine half of the angle. Then, double
         * the angle and convert it from radians to degrees. */
        double fovy = (180/PI) * (2 * atan(gs.vWidth * (gs.w/gs.h) / (2 * gs.vDist))); 

        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);
        
        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        glu.gluPerspective(fovy, (float)gs.w / (float)gs.h, 0.1*gs.vDist, 10.0*gs.vDist);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
               
        // Update the view according to the camera mode.
        camera.update(gs.camMode);
        
        // The definition of each variable is in the camera class.
        glu.gluLookAt(
            // ===== Eye position =====
            camera.eye.x(),         camera.eye.y(),          camera.eye.z(),
            // ===== Center position =====
            camera.center.x(),      camera.center.y(),       camera.center.z(),
            // ===== Up vector =====
            camera.up.x(),          camera.up.y(),           camera.up.z());

    }
    
    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        
        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }
        
        // Draw the 4 robots.
        robots[0].setMaterialColor(); //GOLD
        robots[0].draw(false); //Draw robot fully initially.
        robots[1].setMaterialColor(); //SILVER
        robots[1].draw(false); //Draw robot fully initially.
        robots[2].setMaterialColor(); //WOOD
        robots[2].draw(false); //Draw robot fully initially.
        robots[3].setMaterialColor(); //ORANGE
        robots[3].draw(false); //Draw robot fully initially.
 
        // Draw race track
        raceTrack.draw(gs.trackNr);
        
        // Draw terrain
        terrain.draw();
    }
       
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        float radius = 0.1f; //Sphere radius
        int numSlices = 20; //Number of slices
        int numStacks = 20; //Number of stacks
        float base = 0.1f; //Base radius of cones
        float height = 0.2f; //Height of cones
        /** Definition of the colors to be given to each axis */
        float[] xAxisColor = {1f, 0f, 0f, 1f};
        float[] yAxisColor = {0f, 1f, 0f, 1f};
        float[] zAxisColor = {0f, 0f, 1f, 1f};
        float[] sphereColor = {1f, 1f, 0f, 1f};
        
        /** Draw the red X axis.  */
        setMaterialColor(xAxisColor);
        gl.glPushMatrix();
        gl.glTranslatef(0.5f, 0, 0);
        gl.glScalef(1, 0.05f, 0.05f);
        glut.glutSolidCube(1);
        gl.glPopMatrix();
        /** Draw the red cone.  */
        gl.glPushMatrix();
        gl.glTranslatef(1, 0, 0);
        gl.glRotatef(90, 0, 1, 0);
        glut.glutSolidCone(base, height, numSlices, numStacks);
        gl.glPopMatrix();
        
        /** Draw the green Y axis.  */
        setMaterialColor(yAxisColor);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0.5f, 0);
        gl.glScalef(0.05f, 1, 0.05f);
        glut.glutSolidCube(1);
        gl.glPopMatrix();
        /** Draw the green cone.  */
        gl.glPushMatrix();
        gl.glTranslatef(0, 1, 0);
        gl.glRotatef(90, -1, 0, 0);
        glut.glutSolidCone(base, height, numSlices, numStacks);
        gl.glPopMatrix();
        
        /** Draw the blue Z axis.  */
        setMaterialColor(zAxisColor);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0.5f);
        gl.glScalef(0.05f, 0.05f, 1);
        glut.glutSolidCube(1);
        gl.glPopMatrix();
        /** Draw the blue cone  */
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 1);
        glut.glutSolidCone(base, height, numSlices, numStacks);
        gl.glPopMatrix();
        
        /** Draw the yellow origin sphere.  */
        setMaterialColor(sphereColor);
        gl.glPushMatrix();
        gl.glScalef(2, 2, 2);
        glut.glutSolidSphere(radius, numSlices, numStacks);
        gl.glPopMatrix();
    }
    
    /**
     * Materials that can be used for the robots.
     */
    public enum Material {
        
        /** 
         * Gold material properties.
         */
        GOLD (
            new float[] {0.75f, 0.6f, 0.23f, 1.0f},
            new float[] {0.75f, 0.6f, 0.23f, 1.0f}
        ),
        
        /**
         * Silver material properties.
         */
        SILVER (
            new float[] {0.51f, 0.51f, 0.51f, 1.0f},
            new float[] {0.51f, 0.51f, 0.51f, 1.0f}
        ),
        
        /** 
         * Wood material properties.
         */
        WOOD (
            new float[] {0.4f, 0.25f, 0f, 1.0f},
            new float[] {0f, 0f, 0f, 1.0f}
        ),
        
        /**
         * Orange material properties.
         */
        ORANGE (
            new float[] {1f, 0.4f, 0f, 1.0f},
            new float[] {0.5f, 0.2f, 0f, 1.0f}
        );
        
        /** The diffuse RGBA reflectance of the material. */
        float[] diffuse;
        
        /** The specular RGBA reflectance of the material. */
        float[] specular;
        
        /**
         * Constructs a new material with diffuse and specular properties.
         */
        private Material(float[] diffuse, float[] specular) {
            this.diffuse = diffuse;
            this.specular = specular;
        }
    }
    
    /**
     * Represents a Robot, to be implemented according to the Assignments.
     */
    private class Robot {
        private float tx, ty, tz;   //used for translation
        private float angle = 0;          //angle for rotation
        private float rx, ry, rz;   //used for rotation
        private float sx, sy, sz;   //used for scaling
        /** The material from which this robot is built. */
        private final Material material;
        
        /**
         * Defining the structure of the robot.
         */
        private Vector headPosition = new Vector(0, 0, 1.9);
        private Vector shoulderPosition = new Vector(0, 0, 1.7);
        private Vector leftArmPosition = new Vector(0.3, 0, 1.25);
        private Vector rightArmPosition = new Vector(-0.3, 0, 1.25);
        private Vector torsoPosition = new Vector(0, 0, 1.4);
        private Vector bottomPosition = new Vector(0, 0, 1);
        private Vector leftLegPosition = new Vector(0.1, 0, 0.5);
        private Vector rightLegPosition = new Vector(-0.1, 0, 0.5);
        
        private Vector leftShoulderJoint = new Vector(-0.4, 0, 1.7);
        private Vector rightShoulderJoint = new Vector(0.4, 0, 1.7);
        private Vector leftLegJoint = new Vector(0.1, 0, 1);
        private Vector rightLegJoint = new Vector(-0.1, 0, 1);       
        
        private Vector basePosition;
        /**
         * The coordinates where the robot is initially placed at, specified 
         * at the constructor of RobotRace.
         */
        
        /**
         * Constructs the robot with initial parameters.
         */
        public Robot(Material material, Vector basePosition) {
            this.material = material; /** Sets the material of the robot to the 
            given material. */
            this.basePosition = basePosition; /** Sets the position where the 
            robot is placed at the given basePosition. */
            
        }
        
        /**
         * Draws this robot (as a {@code stickfigure} if specified).
         */
        public void draw(boolean stickFigure) {
            /**Here each part is drawn taking the basePosition as the main
             * position of the robot and translating regarding to it.
             */
            float limbStartAngle = 45f;
            
            boolean showStick = gs.showStick;
            drawHead(showStick);
            drawShoulder(showStick);
            drawArm(leftArmPosition, leftShoulderJoint, limbStartAngle, showStick);
            drawArm(rightArmPosition, rightShoulderJoint, -limbStartAngle, showStick);
            drawTorso(showStick);
            drawBottom(showStick);
            drawLeg(leftLegPosition, leftLegJoint, -limbStartAngle, showStick);
            drawLeg(rightLegPosition, rightLegJoint, limbStartAngle, showStick);
        }
        
        public void setMaterialColor(){
            gl.glColor3fv(material.diffuse, 0);
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
        }
        
        private void drawArm(Vector armPosition, Vector jointPosition, float initialAngle, boolean showStick){
            Vector temp;
            gl.glPushMatrix();
                temp = basePosition.add(jointPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(defineLimbRotation(initialAngle), 1, 0, 0);
                gl.glTranslatef(-(float)temp.x(), -(float)temp.y(), -(float)temp.z());
                gl.glPushMatrix();
                    temp = basePosition.add(armPosition);
                    gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                    gl.glPushMatrix();
                        if(showStick)
                            gl.glScalef(0.05f, 0.05f, 0.9f);
                        else
                            gl.glScalef(0.15f, 0.15f, 0.9f);
                        glut.glutSolidCube(1);
                    gl.glPopMatrix();
                gl.glPopMatrix();
            gl.glPopMatrix();
        }
        
        private void drawHead(boolean showStick){
            Vector temp;
            float[] scaleFactors;
            if (showStick)
                scaleFactors = new float[] {0.10f, 0.10f, 0.20f};
            else
                scaleFactors = new float[] {0.25f, 0.25f, 0.30f};
            gl.glPushMatrix();
                temp = basePosition.add(headPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glScalef(scaleFactors[0], scaleFactors[1], scaleFactors[2]);
                glut.glutSolidCube(1);
            gl.glPopMatrix();
            
            gl.glEnable(GL_TEXTURE_2D);
            gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
            gl.glBindTexture(GL_TEXTURE_2D, head.getTextureObject());
            head.bind(gl);
            gl.glBegin(GL_QUADS);
                gl.glTexCoord2d(0, 0);
                gl.glVertex3d(temp.x() + scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() - scaleFactors[2]/2);
                gl.glTexCoord2d(1, 0);
                gl.glVertex3d(temp.x() - scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() - scaleFactors[2]/2);
                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(temp.x() - scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() + scaleFactors[2]/2);
                gl.glTexCoord2d(0, 1);
                gl.glVertex3d(temp.x() + scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() + scaleFactors[2]/2);
            gl.glEnd(); 
            gl.glDisable(GL_TEXTURE_2D);
            
        }
        
        private void drawShoulder(boolean showStick){
            Vector temp;
            gl.glPushMatrix();
                temp = basePosition.add(shoulderPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                if(showStick)
                    gl.glScalef(0.7f, 0.05f, 0.05f);
                else
                    gl.glScalef(0.8f, 0.20f, 0.20f);
                glut.glutSolidCube(1);
            gl.glPopMatrix();
        }
        
        private void drawBottom(boolean showStick){
            Vector temp;
            gl.glPushMatrix();
                temp = basePosition.add(bottomPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                if(showStick)
                    gl.glScalef(0.43f, 0.05f, 0.05f);
                else
                    gl.glScalef(0.43f, 0.20f, 0.20f);
                glut.glutSolidCube(1);
            gl.glPopMatrix();
        }
        
        private void drawTorso(boolean showStick){
            Vector temp;
            float[] scaleFactors;
            if (showStick)
                scaleFactors = new float[] {0.05f, 0.05f, 0.8f};
            else
                scaleFactors = new float[] {0.4f, 0.2f, 0.8f};
            gl.glPushMatrix();                
                temp = basePosition.add(torsoPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glScalef(scaleFactors[0], scaleFactors[1], scaleFactors[2]);
                glut.glutSolidCube(1);
            gl.glPopMatrix();
            
            gl.glEnable(GL_TEXTURE_2D);
            gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
            //gl.glBindTexture(GL_TEXTURE_2D, torso.getTextureObject());
            torso.bind(gl);
            gl.glBegin(GL_QUADS);
                gl.glTexCoord2d(0, 0);
                gl.glVertex3d(temp.x() + scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() - scaleFactors[2]/2);
                gl.glTexCoord2d(1, 0);
                gl.glVertex3d(temp.x() - scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() - scaleFactors[2]/2);
                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(temp.x() - scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() + scaleFactors[2]/2);
                gl.glTexCoord2d(0, 1);
                gl.glVertex3d(temp.x() + scaleFactors[0]/2, scaleFactors[1]/2 + 0.0001f, temp.z() + scaleFactors[2]/2);
            gl.glEnd(); 
            
            gl.glBegin(GL_QUADS);
                gl.glTexCoord2d(0, 0);
                gl.glVertex3d(temp.x() - scaleFactors[0]/2, - scaleFactors[1]/2 - 0.0001f, temp.z() - scaleFactors[2]/2);
                gl.glTexCoord2d(1, 0);
                gl.glVertex3d(temp.x() + scaleFactors[0]/2, - scaleFactors[1]/2 - 0.0001f, temp.z() - scaleFactors[2]/2);
                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(temp.x() + scaleFactors[0]/2, - scaleFactors[1]/2 - 0.0001f, temp.z() + scaleFactors[2]/2);
                gl.glTexCoord2d(0, 1);
                gl.glVertex3d(temp.x() - scaleFactors[0]/2, - scaleFactors[1]/2 - 0.0001f, temp.z() + scaleFactors[2]/2);
            gl.glEnd(); 
            
            gl.glDisable(GL_TEXTURE_2D);
        }
        
        private void drawLeg(Vector legPosition, Vector jointPosition, float initialAngle, boolean showStick){
            Vector temp;
            gl.glPushMatrix();
                temp = basePosition.add(jointPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(defineLimbRotation(initialAngle), 1, 0, 0);
                gl.glTranslatef(-(float)temp.x(), -(float)temp.y(), -(float)temp.z());
                gl.glPushMatrix();
                    temp = basePosition.add(legPosition);
                    gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                    gl.glPushMatrix();
                        if(showStick)
                            gl.glScalef(0.05f, 0.05f, 1f);
                        else
                            gl.glScalef(0.18f, 0.18f, 1f);
                        glut.glutSolidCube(1);
                    gl.glPopMatrix();
                gl.glPopMatrix();
            gl.glPopMatrix();
        }
        
        private float defineLimbRotation(float initialAngle) {
            float limitAngle = 45f;
            float angle = (100*gs.tAnim + initialAngle) % (4 * limitAngle);
            if (angle >= limitAngle && angle < 3 * limitAngle) {
                angle = 2 * limitAngle - angle;
            }
            else if (angle >= 3 * limitAngle && angle < 5 * limitAngle) {
                angle = angle - 4 * limitAngle;
            }
            return angle;
        }
    }
    
    /**
     * Implementation of a camera with a position and orientation. 
     */
    private class Camera {
        /** The position of the camera.*/
        public Vector eye = getEyePosition();
        
        /** The point to which the camera is looking. */
        public Vector center = getCenterPosition();
        
        /** The up vector. */
        public Vector up = Vector.Z;
        
        private int robotToFocus = 0;
        private int changeRobotToFocus = 0;
        private int iterationsToChangeCamera = 100;
        /**
         * Updates the camera viewpoint and direction based on the
         * selected camera mode.
         */
        public void update(int mode) {
            robots[0].toString();
            
            // Helicopter mode
            if (1 == mode) {  
                setHelicopterMode();
                
            // Motor cycle mode
            } else if (2 == mode) { 
                setMotorCycleMode();
                
            // First person mode
            } else if (3 == mode) { 
                setFirstPersonMode();
                
            // Auto mode
            } else if (4 == mode) { 
                // code goes here...
                
            // Default mode
            } else {
                setDefaultMode();
            }
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the camera's default mode.
         */
        private void setDefaultMode() {
            eye = getEyePosition();
            center = getCenterPosition();
            up = Vector.Z;
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the helicopter mode.
         */
        private void setHelicopterMode() {
            eye = robots[robotToFocus].basePosition.add(new Vector(0,0,10));
            center = robots[robotToFocus].basePosition;
            up = Vector.Y;
            updateFocusedRobot();
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the motorcycle mode.
         */
        private void setMotorCycleMode() {
            eye = robots[robotToFocus].basePosition.add(new Vector(10,0,0));
            center = robots[robotToFocus].basePosition;
            up = Vector.Z;
            updateFocusedRobot();
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the first person mode.
         */
        private void setFirstPersonMode() {
            Vector headPosition = robots[robotToFocus].basePosition.add(
                    new Vector(0, -3, robots[robotToFocus].headPosition.z()));
            eye = headPosition;
            center = headPosition.add(new Vector(0,2,0));
            up = Vector.Z;
            updateFocusedRobot();
        }
        
        private Vector getEyePosition() {
            return new Vector(
                // The projection of the V vector to the X axis plus the gs.cnt gives the eye's X
                gs.cnt.x() + gs.vDist * Math.cos(gs.phi) * Math.cos(gs.theta),
                // The projection of the V vector to the Y axis plus the gs.cnt gives the eye's Y
                gs.cnt.y() - gs.vDist * Math.cos(gs.phi) * Math.sin(gs.theta),
                // The projection of the V vector to the Z axis plus the gs.cnt gives the eye's Z
                gs.cnt.z() + gs.vDist * Math.sin(gs.phi)
            );
        }
        
        private Vector getCenterPosition() {
            /**
             * Returns the center position of the global state.
             */
            return new Vector(
                gs.cnt.x(),
                gs.cnt.y(),
                gs.cnt.z()
            );
        }
        
        private void updateFocusedRobot(){
            changeRobotToFocus++;
            if(changeRobotToFocus == iterationsToChangeCamera){
                robotToFocus = 1;
            }
            else if(changeRobotToFocus == 2 * iterationsToChangeCamera){
                robotToFocus = 2;
            }
            else if(changeRobotToFocus == 3 * iterationsToChangeCamera){
                robotToFocus = 3;
            }
            else if(changeRobotToFocus == 4 * iterationsToChangeCamera){
                robotToFocus = 0;
                changeRobotToFocus = 0;
            }
        }
    }
    
    /**
     * Implementation of a race track that is made from Bezier segments.
     */
    private class RaceTrack {
        
        /** Array with control points for the O-track. */
        private Vector[] controlPointsOTrack;
        
        /** Array with control points for the L-track. */
        private Vector[] controlPointsLTrack;
        
        /** Array with control points for the C-track. */
        private Vector[] controlPointsCTrack;
        
        /** Array with control points for the custom track. */
        private Vector[] controlPointsCustomTrack;
        
        /**
         * Constructs the race track, sets up display lists.
         */
        public RaceTrack() {   
            
        }
        
        /**
         * Draws this track, based on the selected track number.
         */
        public void draw(int trackNr) {
            float ctrlpoints[][] = new float[][]
            {
            { -4.0f, -4.0f, 0.0f },
            { -2.0f, 4.0f, 0.0f },
            { 2.0f, -4.0f, 0.0f },
            { 4.0f, 4.0f, 0.0f } };
            FloatBuffer ctrlpointBuf = //
            FloatBuffer.allocate(ctrlpoints[0].length * ctrlpoints.length);
            

            // need to convert 2d array to buffer type
            for (int i = 0; i < ctrlpoints.length; i++)
            {
              for (int j = 0; j < 3; j++)
              {
                ctrlpointBuf.put(ctrlpoints[i][j]);
              }
            }
            ctrlpointBuf.rewind();

            //gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //makes stuff black
            //gl.glShadeModel(GL_FLAT); //makes it look all trippy
            gl.glMap1f(GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpointBuf);
            gl.glEnable(GL_MAP1_VERTEX_3);
    
            // The test track is selected
            if (0 == trackNr) {
                float radius = (float) 8.5; //track radius
                float curves = (int) radius*100; //The number of curves used
                float v = 1; //height               
                
                // Track bottom
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);                
                for (int i=0; i < curves; i++)
                {
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) 0.0);
                }
                gl.glEnd();
                gl.glFlush();
                
                // Track top
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);                
                for (int i=0; i < curves; i++)
                {
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) v);
                }
                gl.glEnd();
                gl.glFlush();
                
                // Track outer sides
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);                
                for (int i=0; i < curves+120; i++)
                {
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) 0.0);
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) v);
                }
                gl.glEnd();
                gl.glFlush();
                
                // Track inner sides
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);                
                for (int i=0; i < curves; i++)
                {
                   gl.glVertex3f((float) ((radius-3.5) * cos(i)), (float) ((radius-3.5) * sin(i)), (float) 0.0);
                   gl.glVertex3f((float) ((radius-3.5) * cos(i)), (float) ((radius-3.5) * sin(i)), (float) v);
                }
                gl.glEnd();
                gl.glFlush();                
                
            // The O-track is selected
            } else if (1 == trackNr) {
                float radius = (float) 8.5; //track radius
                float curves = (int) radius*100; //The number of curves used
                float v = 1; //height               
                
                // Track bottom
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);                
                for (int i=0; i < curves; i++)
                {
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) 0.0);
                }
                gl.glEnd();
                gl.glFlush();
                
                // Track top
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);                
                for (int i=0; i < curves; i++)
                {
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) v);
                }
                gl.glEnd();
                gl.glFlush();
                
                // Track outer sides
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);                
                for (int i=0; i < curves+120; i++)
                {
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) 0.0);
                   gl.glVertex3f((float) (radius * cos(i)), (float) (radius * sin(i)), (float) v);
                }
                gl.glEnd();
                gl.glFlush();
                
                // Track inner sides
                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);                
                for (int i=0; i < curves; i++)
                {
                   gl.glVertex3f((float) ((radius-3.5) * cos(i)), (float) ((radius-3.5) * sin(i)), (float) 0.0);
                   gl.glVertex3f((float) ((radius-3.5) * cos(i)), (float) ((radius-3.5) * sin(i)), (float) v);
                }
                gl.glEnd();
                gl.glFlush();
                
            // The L-track is selected
            } else if (2 == trackNr) {
                
                
            // The C-track is selected
            } else if (3 == trackNr) {
                // code goes here ...
                
            // The custom track is selected
            } else if (4 == trackNr) {
                // code goes here ...
                //gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                gl.glColor3f(1.0f, 1.0f, 1.0f);
                gl.glBegin(GL.GL_LINE_STRIP);
                for (int i = 0; i <= 30; i++)
                {
                  gl.glEvalCoord1f((float) i / (float) 30.0);
                }
                gl.glEnd();
                /* The following code displays the control points as dots. */
                gl.glPointSize(5.0f);
                gl.glColor3f(1.0f, 1.0f, 0.0f);
                gl.glBegin(GL_POINTS);
                for (int i = 0; i < 4; i++)
                {
                  gl.glVertex3fv(ctrlpointBuf);
                  ctrlpointBuf.position(i * 3);
                }
                gl.glEnd();
                gl.glFlush();
                
            }
        }
        
        /**
         * Returns the position of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getPoint(double t) {
            return Vector.O; // <- code goes here
        }
        
        /**
         * Returns the tangent of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getTangent(double t) {
            return Vector.O; // <- code goes here
        }
        
    }
    
    /**
     * Implementation of the terrain.
     */
    private class Terrain {
        
        Integer[] numbersList = new Integer[10];
        /**
         * Can be used to set up a display list.
         */
        public Terrain() {
           
        }
        
        /**
         * Draws the terrain.
         */
        public void draw() {
            drawTree(new Vector(-1,0,1), 1, 1, 1);
            drawTree(new Vector(8,-9,1), 1.5, 1.5, 1.5);
            drawTree(new Vector(-10,0,1), 2, 2, 2);
            drawClock();
        }
        
        /**
         * Computes the elevation of the terrain at ({@code x}, {@code y}).
         */
        public float heightAt(float x, float y) {
            return 0; // <- code goes here
        }
        
        private void drawTree(Vector basePosition, double cilinderScale, double firstConeScale, double secondConeScale){
            double cylinderRadius = 0.3;
            double cylinderHeight = 3;
            double coneBase = 1.5;
            double coneHeight = 2;
            int slices = 100;
            int stacks = 10;
            gl.glPushMatrix();
                gl.glTranslated(basePosition.x(), basePosition.y(), basePosition.z());
                gl.glPushMatrix();
                    gl.glColor3f(0.4f, 0.2f, 0f);
                    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {0, 0, 0}, 0);
                    gl.glScaled(cilinderScale, cilinderScale, cilinderScale);
                    glut.glutSolidCylinder(cylinderRadius, cylinderHeight, slices, stacks);
                gl.glPopMatrix();
                gl.glTranslated(0, 0, cilinderScale * cylinderHeight/2);
                gl.glPushMatrix();
                    gl.glColor3f(0.23f, 0.37f, 0.04f);
                    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {0.23f, 0.37f, 0.04f}, 0);
                    gl.glScaled(firstConeScale, firstConeScale, firstConeScale);
                    glut.glutSolidCone(coneBase, coneHeight, slices, stacks);
                gl.glPopMatrix();
                gl.glTranslated(0, 0, cilinderScale * cylinderHeight/4);
                gl.glPushMatrix();
                    gl.glColor3f(0.23f, 0.37f, 0.04f);
                    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {0.23f, 0.37f, 0.04f}, 0);
                    gl.glScaled(secondConeScale, secondConeScale, secondConeScale);
                    glut.glutSolidCone(coneBase/2, coneHeight, slices, stacks);
                gl.glPopMatrix();
                
            gl.glPopMatrix();
        }
        
        private void drawClock(){
            gl.glMatrixMode(GL_PROJECTION);
            gl.glPushMatrix();
                gl.glLoadIdentity();
                gl.glOrtho(0, 100, 0, 100, -1, 1);
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glPushMatrix();
                    gl.glLoadIdentity();
                    drawHours();
                    drawMinutes();
                    drawSeconds();
                gl.glPopMatrix();
            gl.glMatrixMode(GL_PROJECTION);
            gl.glPopMatrix();
            
        }
        
        private void drawHours() {
            
        }
        
        private void drawMinutes() {
            
        }
        
        private void drawSeconds() {
            drawTime();
        }
        
        private void drawTime() {
            gl.glLineWidth(5);
            gl.glBegin(GL_LINES);
                gl.glVertex2d(0, 1);
                gl.glVertex2d(0, 6);

                gl.glVertex2d(0, 8);
                gl.glVertex2d(0, 12);

                gl.glVertex2d(1, 13);
                gl.glVertex2d(6, 13);

                gl.glVertex2d(7, 12);
                gl.glVertex2d(7, 8);

                gl.glVertex2d(7, 6);
                gl.glVertex2d(7, 1);

                gl.glVertex2d(1, 0);
                gl.glVertex2d(6, 0);

            gl.glEnd();
        }
        
        private void buildDigitalNumbers(){
            for (int i = 0; i <= 9; i++){
                numbersList[i] = gl.glGenLists(i);
            }
            gl.glListBase(10);
            gl.glNewList(numbersList[0], GL_COMPILE);
                
            gl.glEndList();
        }
    }
    
    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
    }
    
    public void setMaterialColor(float[] ambientAndDiffuse){
        float[] specular = {1, 1, 1, 1};
        //Sets the current color from an already existing array of color values.
        gl.glColor3fv(ambientAndDiffuse, 0);
        //Specifies material parameters for the lighting model.
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, specular, 0); //specifies material parameters for the lighting model.
    }
    
    public void configureLighting(){
        /* Enable lightning of all tree different types of light. 
         * Besides, place the light source above
         * and to the left of the eye/camera, as required.
         */
        float lightPositionX = -20 + (float)camera.eye.x();
        float lightPositionY = 15 + (float)camera.eye.y();
        float lightPositionZ = 20 + (float)camera.eye.z();
        float[] ambientLight = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] diffuseLight = {1f, 1f, 1f, 1.0f};
        float[] specularLight = {1f, 1f, 1f, 1.0f};
        float[] lightPosition = {lightPositionX, lightPositionY, lightPositionZ, 1};
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight, 0);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, specularLight, 0);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
        gl.glLightModelfv(GL_AMBIENT, new float[] {0.2f, 0.2f, 0.2f, 1f}, 0);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_LIGHTING);
        
        /**Configure the lighting in order to be able to define ambient and
         * diffuse colors using the glColor command (enabled with the
         * glEnable(GL_COLOR_MATERIAL)). The shininess is also defined for all
         * elements at once. If it is necessary to change it, it can be called
         * elsewhere.
         */
        gl.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 10);
        
        // Ajust the normals so that it is not necessary to define each one.
        gl.glEnable(GL_NORMALIZE);
    }
}
