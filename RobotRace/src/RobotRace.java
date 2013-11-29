
import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;
import robotrace.Base;
import robotrace.Vector;
import static java.lang.Math.*;
import static java.lang.System.out;
import java.nio.FloatBuffer;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_TRUE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
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
        robots[0] = new Robot(Material.GOLD
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD
            /* add other parameters that characterize this robot */);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE
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
        
        // Enable anti-aliasing.
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glEnable(GL_POLYGON_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
        
    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        /** Calculating the fovy angle. First calculate the hight with the 
         * expression = gs.vWidth * (gs.w/gs.h). This expression is a proportion
         * related to the aspect of the view. Second, caltulate the atan of
         * (height/2)/gs.vDist, to determine half of the angle. Third, double
         * the angle and convert it from radians to degrees. */
        double fovy = (180/Math.PI) * (2 * Math.atan(gs.vWidth * (gs.w/gs.h) / (2 * gs.vDist))); 

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
               
        // Update the view according to the camera mode
        camera.update(gs.camMode);
        
        // The definition of the camera variables is in the Camera class.
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());
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
        
        // Draw the axis frame
        if (gs.showAxes) {
            drawAxisFrame();
        }
        
        // Draw the first robot
        robots[0].draw(true);
        robots[0].tx = 0f;
        //robots[0].material;
        robots[1].draw(true);
        robots[1].tx = 2f;
        //robots[1].material;
        robots[2].draw(true);
        robots[2].tx = 4f;
        //robots[2].material;
        robots[3].draw(true);
        robots[3].tx = -2f;
        //robots[3].material

        // Draw race track
        raceTrack.draw(gs.trackNr);
        
        // Draw terrain
        terrain.draw();
        
        /**
        // Unit box around origin.
        glut.glutWireCube(1f);

        // Move in x-direction.
        gl.glTranslatef(2f, 0f, 0f);
        
        // Rotate 30 degrees, around z-axis.
        gl.glRotatef(30f, 0f, 0f, 1f);
        
        // Scale in z-direction.
        gl.glScalef(1f, 1f, 2f);

        // Translated, rotated, scaled box.
        glut.glutWireCube(1f);
        */
    }
    
    
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        float radius = 0.1f; //Sphere radius
        int numSlices = 10; //Number of slices
        int numStacks = 10; //Number of stacks
        float base = 0.1f; //Base radius of cones
        float height = 0.2f; //Height of cones
        float shininess = 1f; //Shininess coefficient
        
        
        // Draw the (red) X axis.
        setMaterialColor(1f, 0f, 0f, 1, shininess);
        gl.glPushMatrix();
        gl.glTranslatef(0.5f, 0, 0);
        gl.glScalef(1, 0.05f, 0.05f);
        glut.glutSolidCube(1);
        gl.glPopMatrix();
        
        // Draw the (green) Y axis.
        setMaterialColor(0f, 1f, 0f, 1, shininess);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0.5f, 0);
        gl.glScalef(0.05f, 1, 0.05f);
        glut.glutSolidCube(1);
        gl.glPopMatrix();
        
        // Draw the (blue) Z axis.
        setMaterialColor(0f, 0f, 1f, 1, shininess);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0.5f);
        gl.glScalef(0.05f, 0.05f, 1);
        glut.glutSolidCube(1);
        gl.glPopMatrix();
        
        // Draw the cone for the X axis (red).
        setMaterialColor(1f, 0f, 0f, 1, shininess);
        gl.glPushMatrix();
        gl.glTranslatef(1, 0, 0);
        gl.glRotatef(90, 0, 1, 0);
        glut.glutSolidCone(base, height, numSlices, numStacks);
        gl.glPopMatrix();
        
        // Draw the cone for the Y axis (green).
        setMaterialColor(0f, 1f, 0f, 1, shininess);
        gl.glPushMatrix();
        gl.glTranslatef(0, 1, 0);
        gl.glRotatef(90, -1, 0, 0);
        glut.glutSolidCone(base, height, numSlices, numStacks);
        gl.glPopMatrix();
        
        // Draw the cone for the Z axis (blue).
        setMaterialColor(0f, 0f, 1f, 1, shininess);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 1);
        glut.glutSolidCone(base, height, numSlices, numStacks);
        gl.glPopMatrix();
        
        //Origin sphere
        setMaterialColor(1f, 1f, 0f, 1, shininess);
        gl.glPushMatrix();
        gl.glScalef(2, 2, 2);
        glut.glutSolidSphere(radius, numSlices, numStacks);
        gl.glPopMatrix();
        
        // The light is defined after creating the axis in order to be
        // able to apply the directions in the new configuration.
        configureLighting();
    }
    
    /**
     * Materials that can be used for the robots.
     */
    public enum Material {
        
        /** 
         * Gold material properties.
         * Modify the default values to make it look like gold.
         */
        GOLD (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f}),
        
        /**
         * Silver material properties.
         * Modify the default values to make it look like silver.
         */
        SILVER (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f}),
        
        /** 
         * Wood material properties.
         * Modify the default values to make it look like wood.
         */
        WOOD (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f}),
        
        /**
         * Orange material properties.
         * Modify the default values to make it look like orange.
         */
        ORANGE (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f});
        
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
        private float tx, ty, tz; //used for translation
        private int angle; //angle for rotation
        private float rx, ry, rz; //used for rotation
        private float sx, sy, sz; //used for scaling
        /** The material from which this robot is built. */
        private final Material material;
        
        /**
         * Constructs the robot with initial parameters.
         */
        public Robot(Material material
            /* add other parameters that characterize this robot */) {
            this.material = material;
            
            // code goes here ...
        }
        
        /**
         * Draws this robot (as a {@code stickfigure} if specified).
         */
        public void draw(boolean stickFigure) {
            //Draw robot as stick figure
            setMaterialColor(0.2f, 0.5f, 0.8f, 1, 0.8f);
            if (gs.showStick == true) {
                //Head
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0f, this.ty+0.05f, this.tz+1.9f);
                gl.glRotatef(this.rx, this.ry, this.rz, this.angle);
                gl.glScalef(this.sx+0.10f, this.sy+0.10f, this.sz+0.20f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Shoulders
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0f, this.ty+0, this.tz+1.7f);
                gl.glRotatef(this.rx, this.ry, this.rz, this.angle);
                gl.glScalef(this.sx+0.7f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Left hand
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0.3f, this.ty+0, this.tz+1.25f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Right hand
                gl.glPushMatrix();
                gl.glTranslatef(this.tx-0.3f, this.ty+0, this.tz+1.25f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Torso
                gl.glPushMatrix();                
                gl.glTranslatef(this.tx+0f, this.ty+0, this.tz+1.4f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+0.75f, this.sy+0.05f, this.sz+0.05f);           
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Bottom
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0f, this.ty+0, this.tz+1f);
                gl.glRotatef(this.rx, this.ry, this.rz, this.angle);
                gl.glScalef(this.sx+0.43f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Left leg
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0.1f, this.ty+0, this.tz+0.5f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+1f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Right leg
                gl.glPushMatrix();
                gl.glTranslatef(this.tx-0.1f, this.ty+0, this.tz+0.5f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+1f, this.sy+0.05f, this.sz+0.05f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
            } //Draw robot fully
            else {
                //Head
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0f, this.ty+0.05f, this.tz+2.0f);
                gl.glRotatef(this.rx, this.ry, this.rz, this.angle);
                gl.glScalef(this.sx+0.25f, this.sy+0.25f, this.sz+0.30f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Shoulders
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0f, this.ty+0, this.tz+1.7f);
                gl.glRotatef(this.rx, this.ry, this.rz, this.angle);
                gl.glScalef(this.sx+0.8f, this.sy+0.20f, this.sz+0.20f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Left hand
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0.3f, this.ty+0, this.tz+1.25f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.15f, this.sz+0.15f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Right hand
                gl.glPushMatrix();
                gl.glTranslatef(this.tx-0.3f, this.ty+0, this.tz+1.25f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+0.9f, this.sy+0.15f, this.sz+0.15f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Torso
                gl.glPushMatrix();                
                gl.glTranslatef(this.tx+0f, this.ty+0, this.tz+1.4f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+0.6f, this.sy+0.20f, this.sz+0.40f);           
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Bottom
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0f, this.ty+0, this.tz+1f);
                gl.glRotatef(this.rx, this.ry, this.rz, this.angle);
                gl.glScalef(this.sx+0.43f, this.sy+0.20f, this.sz+0.20f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Left leg
                gl.glPushMatrix();
                gl.glTranslatef(this.tx+0.1f, this.ty+0, this.tz+0.5f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+1f, this.sy+0.18f, this.sz+0.18f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
                
                //Right leg
                gl.glPushMatrix();
                gl.glTranslatef(this.tx-0.1f, this.ty+0, this.tz+0.5f);
                gl.glRotatef(this.rx+90, this.ry+0, this.rz+1, this.angle+0);
                gl.glScalef(this.sx+1f, this.sy+0.18f, this.sz+0.18f);
                glut.glutSolidCube(1);
                gl.glPopMatrix();                
                
            }
        }
    }
    
    /**
     * Implementation of a camera with a position and orientation. 
     */
    private class Camera {
        // TODO: remove this variable!!!
        int removeThis = 10; // Just to facilitate visualisation. 
        
        /** The position of the camera. 
         * This point is calculated considering the Vector V (from gs.cnt to
         * the eye).
         */
        public Vector eye = new Vector(
                // The projection of the V vector to the X axis plus the gs.cnt gives the eye's X
                - gs.cnt.x() + gs.vDist * Math.cos(gs.phi) * Math.cos(gs.theta),
                // The projection of the V vector to the Y axis plus the gs.cnt gives the eye's Y
                removeThis - gs.cnt.y() + gs.vDist * Math.cos(gs.phi) * Math.sin(gs.theta),
                // The projection of the V vector to the Z axis plus the gs.cnt gives the eye's Z
                removeThis - gs.cnt.z() + gs.vDist * Math.sin(gs.phi));
        
        /** The point to which the camera is looking. 
         * This point was set to be the origin, but according to the 
         * instructions it is supposed to be the center defined by gs.cnt. */
        public Vector center = Vector.O;
        
        /** The up vector. */
        public Vector up = Vector.Z;
        
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
            // code goes here ...
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the helicopter mode.
         */
        private void setHelicopterMode() {
            // code goes here ...
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the motorcycle mode.
         */
        private void setMotorCycleMode() {
            // code goes here ...
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the first person mode.
         */
        private void setFirstPersonMode() {
            // code goes here ...
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
            // code goes here ...
        }
        
        /**
         * Draws this track, based on the selected track number.
         */
        public void draw(int trackNr) {
            
            // The test track is selected
            if (0 == trackNr) {
                // code goes here ...
            
            // The O-track is selected
            } else if (1 == trackNr) {
                // code goes here ...
                
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
    
    public void setMaterialColor(float r, float g, float b, float a, float shininess){
        float[] rgba = {r, g, b, a};
        gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, rgba, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL_FRONT, GL_SHININESS, shininess);    
    }
    
    public void configureLighting(){
        /* Enable lightning choosing the color, intensity and position of all
         * tree different types of light. Besides, place the light source above
         * and to the left of the eye/camera, as required.
         */
        float lightPositionX = 30 + (float)camera.eye.x();
        float lightPositionY = (float)camera.eye.y();
        float lightPositionZ = (float)camera.eye.z();
        float[] ambientLight = {0.2f, 0.2f, 0.2f, 1.0f};
        float[] diffuseLight = {0.8f, 0.8f, 0.8f, 1.0f};
        float[] specularLight = {1f, 1f, 1f, 1.0f};
        float[] lightPosition = {lightPositionX, lightPositionY, lightPositionZ, 1};
//        float[] lightDirection = {-1, 0, 0, 0};
        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, ambientLight, 0);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, diffuseLight, 0);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, specularLight, 0);
        gl.glLightfv(GL_LIGHT1, GL_POSITION, lightPosition, 0);
//        gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDirection, 0);
        gl.glEnable(GL_LIGHT1);
        gl.glEnable(GL_LIGHTING);
        
        // Normalize the normals
        gl.glEnable(GL_NORMALIZE);
    }
}
