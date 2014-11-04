/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.appstates;

import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import mygame.entities.vehicles.Impreza;
import mygame.entities.vehicles.LookAtControl;

/**
 *
 * @author JiriSimecek
 */
public class TestGameState extends AbstractGameState {

    private BulletAppState physics;
    private Impreza car;
    private float steeringValue, accelerationValue;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        
        this.physics = new BulletAppState();
        this.physics.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
        stateManager.attach(this.physics);
//        this.physics.getPhysicsSpace().enableDebug(this.app.getAssetManager());
        this.physics.getPhysicsSpace().addTickListener(new PhysicsTickListener() {
            
            public void prePhysicsTick(PhysicsSpace space, float tpf) {
                car.getVehicleControl().accelerate(accelerationValue);
                car.getVehicleControl().steer(steeringValue);
            }

            public void physicsTick(PhysicsSpace space, float tpf) {
                
            }
        });
                
        initModels();

        initKeyEvents();
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.app.getStateManager().detach(physics);
    }

    private void initModels() {
        rootNode.attachChild(SkyFactory.createSky(this.app.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", false));

        Spatial cone = this.app.getAssetManager().loadModel("Models/Cone/Cone.j3o");
        cone.move(10, 0, 0);
//        rootNode.attachChild(cone);

        // car        
        car = new Impreza(this.app.getAssetManager());
        car.attachTo(rootNode, physics.getPhysicsSpace());
        car.getVehicleModelNode().addControl(new LookAtControl(app.getCamera()));

        // tree
        Spatial tree = this.app.getAssetManager().loadModel("Models/Tree/Tree.mesh.j3o");
        tree.setShadowMode(RenderQueue.ShadowMode.Cast);
        tree.setLocalScale(2f);
        tree.setLocalTranslation(-5, -4.75f, 5);
        CompoundCollisionShape treeShape = new CompoundCollisionShape();
        treeShape.addChildShape(new BoxCollisionShape(new Vector3f(0.5f, 2.5f, 0.5f)), new Vector3f(0, 2.5f, 0));
        tree.addControl(new RigidBodyControl(treeShape, 0));
        physics.getPhysicsSpace().add(tree);

        rootNode.attachChild(tree);

        // ground
        Box b1 = new Box(100, .25f, 100);
        b1.scaleTextureCoordinates(new Vector2f(100, 100));
        Geometry ground = new Geometry("ground", b1);

        Material matG = this.app.getAssetManager().loadMaterial("Materials/bricks2.j3m");
        ground.setMaterial(matG);
        ground.setShadowMode(RenderQueue.ShadowMode.Receive);
        ground.setLocalTranslation(0, -5, 0);
        TangentBinormalGenerator.generate(ground);
        ground.addControl(new RigidBodyControl(0));
        physics.getPhysicsSpace().add(ground);

        rootNode.attachChild(ground);

        // box
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = this.app.getAssetManager().loadMaterial("Materials/bricks.j3m");
        geom.setShadowMode(RenderQueue.ShadowMode.Cast);
        geom.setMaterial(mat);
        geom.move(0, 10, 0);
        geom.rotate(FastMath.DEG_TO_RAD * 45f, 0, FastMath.DEG_TO_RAD * 45f);
        TangentBinormalGenerator.generate(geom);
        RigidBodyControl boxBody = new RigidBodyControl(100);
        geom.addControl(boxBody);
        physics.getPhysicsSpace().add(geom);

        rootNode.attachChild(geom);
    }

    private void initKeyEvents() {
        app.getInputManager().addMapping("Accelerate", new KeyTrigger(KeyInput.KEY_U));
        app.getInputManager().addMapping("Brake", new KeyTrigger(KeyInput.KEY_J));
        app.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_H));
        app.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_K));
        app.getInputManager().addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        app.getInputManager().addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));

        app.getInputManager().addListener(new ActionListener() {
            float accelerationForce = 1000.0f;
            Vector3f jumpForce = new Vector3f(0, 3000, 0);
            float brakeForce = 1000.0f;

            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("Exit") && !isPressed) {
                    app.stop();
                } else if (name.equals("Left")) {
                    if (isPressed) {
                        steeringValue += .5f;
                    } else {
                        steeringValue += -.5f;
                    }
//                    car.getVehicleControl().steer(steeringValue);
                } else if (name.equals("Right")) {
                    if (isPressed) {
                        steeringValue += -.5f;
                    } else {
                        steeringValue += .5f;
                    }
//                    car.getVehicleControl().steer(steeringValue);
                } else if (name.equals("Accelerate")) {
                    if (isPressed) {
                        accelerationValue += accelerationForce;
                    } else {
                        accelerationValue -= accelerationForce;
                    }
//                    car.getVehicleControl().accelerate(accelerationValue);
                } else if (name.equals("Brake")) {
                    if (isPressed) {
                        accelerationValue -= brakeForce;
                    } else {
                        accelerationValue += brakeForce;
                    }
                } else if (name.equals("Reset") && !isPressed) {
                    app.getStateManager().detach(TestGameState.this);
                    app.getStateManager().attach(new TestGameState());
                }
            }
        }, "Accelerate", "Brake", "Left", "Right", "Reset", "Exit");
    }
}
