
/**
 * Assignment for course 2IV60 Computer Graphics of students:
 * Theodoros Margomenos
 * Marcelo Almeida
 */

import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;
import robotrace.Base;
import robotrace.Vector;
import static java.lang.Math.*;
import static java.lang.System.out;
import java.nio.FloatBuffer;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LINE_STRIP;
import static javax.media.opengl.GL.GL_TRUE;
import javax.media.opengl.GL2;
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
        robots[0] = new Robot(Material.GOLD, new Vector(1,2,0)
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER, new Vector(2,1,0)
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD, new Vector(-1,0,0)
            /* add other parameters that characterize this robot */);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE, new Vector(-2,-2,0)
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
            new float[] {1f, 1f, 0f, 1.0f}
        ),
        
        /**
         * Silver material properties.
         */
        SILVER (
            new float[] {0.51f, 0.51f, 0.51f, 1.0f},
            new float[] {0.6f, 0.6f, 0.6f, 1.0f}
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
        private int angle;          //angle for rotation
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
        
        /**
         * The coordinates where the robot is initially placed at, specified 
         * at the constructor of RobotRace.
         */
        private Vector basePosition; 
        
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
            Vector temp;
            /**
             * Draw robot as a stick figure.
             */
            if (gs.showStick == true) {
                /** Head */
                /** Push matrix on the stack. */ 
                gl.glPushMatrix(); /** Push matrix on the stack. */
                /** Initialize temp variable with the coordinates of where the
                head should be positioned at.*/
                temp = basePosition.add(headPosition); 
                /** Translates the head from the headPosition to the coordinates
                 * specified in the basePosition vector. */
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                /** Rotate the head by the specified values. */
                gl.glRotatef(this.angle, this.rx, this.ry, this.rz);
                /** Resize the head by the specified values. */
                gl.glScalef(this.sx+0.10f, this.sy+0.10f, this.sz+0.20f);
                 /** Renders a solid cube. */
                glut.glutSolidCube(1);
                /** Pop matrix from the stack. */
                gl.glPopMatrix(); 
                
                /** Shoulders */
                gl.glPushMatrix();
                temp = basePosition.add(shoulderPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle, this.rx, this.ry, this.rz);
                gl.glScalef(this.sx+0.7f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Left arm */
                gl.glPushMatrix();
                temp = basePosition.add(leftArmPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Right arm */
                gl.glPushMatrix();
                temp = basePosition.add(rightArmPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Torso */
                gl.glPushMatrix();                
                temp = basePosition.add(torsoPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+0.8f, this.sy+0.05f, this.sz+0.05f);           
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Bottom */
                gl.glPushMatrix();
                temp = basePosition.add(bottomPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle, this.rx, this.ry, this.rz);
                gl.glScalef(this.sx+0.43f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Left leg */
                gl.glPushMatrix();
                temp = basePosition.add(leftLegPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+1f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Right leg */
                gl.glPushMatrix();
                temp = basePosition.add(rightLegPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+1f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();             
            } 
            /**
             * Draw robot fully.
             */
            else {
                /** Head */
                gl.glPushMatrix();
                temp = basePosition.add(headPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle, this.rx, this.ry, this.rz);
                gl.glScalef(this.sx+0.25f, this.sy+0.25f, this.sz+0.30f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Shoulders */
                gl.glPushMatrix();
                temp = basePosition.add(shoulderPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle, this.rx, this.ry, this.rz);
                gl.glScalef(this.sx+0.8f, this.sy+0.20f, this.sz+0.20f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Left arm */
                gl.glPushMatrix();
                temp = basePosition.add(leftArmPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.15f, this.sz+0.15f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Right arm */
                gl.glPushMatrix();
                temp = basePosition.add(rightArmPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.15f, this.sz+0.15f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Torso */
                gl.glPushMatrix();                
                temp = basePosition.add(torsoPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+0.8f, this.sy+0.20f, this.sz+0.40f);           
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Bottom */
                gl.glPushMatrix();
                temp = basePosition.add(bottomPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(this.angle, this.rx, this.ry, this.rz);
                gl.glScalef(this.sx+0.43f, this.sy+0.20f, this.sz+0.20f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Left leg */
                gl.glPushMatrix();
                temp = basePosition.add(leftLegPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(angle+90, this.rx+0, this.ry+1, this.rz+0);
                gl.glScalef(this.sx+1f, this.sy+0.18f, this.sz+0.18f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                /** Right leg */
                gl.glPushMatrix();
                temp = basePosition.add(rightLegPosition);
                gl.glTranslatef((float)temp.x(), (float)temp.y(), (float)temp.z());
                gl.glRotatef(angle+90, rx+0, ry+1, rz+0);
                gl.glScalef(this.sx+1f, this.sy+0.18f, this.sz+0.18f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();                
            }
        }
        
        public void setMaterialColor(){
            //Sets the current color from an already existing array of color values.
            gl.glColor3fv(material.diffuse, 0);
            //Specifies material parameters for the lighting model.
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
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
                gs.cnt.y() + gs.vDist * Math.cos(gs.phi) * Math.sin(gs.theta),
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
            gl.glShadeModel(GL_FLAT);
            gl.glMap1f(GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpointBuf);
            gl.glEnable(GL_MAP1_VERTEX_3);
    
            // The test track is selected
            if (0 == trackNr) {
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
                gl.glBegin(GL.GL_POINTS);
                for (int i = 0; i < 4; i++)
                {
                  gl.glVertex3fv(ctrlpointBuf);
                  ctrlpointBuf.position(i * 3);
                }
                gl.glEnd();
                gl.glFlush();
                
                
            // The O-track is selected
            } else if (1 == trackNr) {
                int radius = 2;
                //gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                gl.glColor3f(1.0f, 1.0f, 1.0f);
                gl.glBegin(GL.GL_LINE_LOOP);
                for (int i=0; i < 360; i++)
                {
                   float degInRad = i;
                   gl.glVertex2f((float) (cos(degInRad)*radius), (float) sin(degInRad)*radius);
                }
                gl.glEnd();
                gl.glFlush();
                
            // The L-track is selected
            } else if (2 == trackNr) {
                // code goes here ...
                
            // The C-track is selected
            } else if (3 == trackNr) {
                // code goes here ...
                
            // The custom track is selected
            } else if (4 == trackNr) {
                // code goes here ...
                
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
        
        /**
         * Can be used to set up a display list.
         */
        public Terrain() {
            // code goes here ...
        }
        
        /**
         * Draws the terrain.
         */
        public void draw() {
            // code goes here ...
        }
        
        /**
         * Computes the elevation of the terrain at ({@code x}, {@code y}).
         */
        public float heightAt(float x, float y) {
            return 0; // <- code goes here
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
