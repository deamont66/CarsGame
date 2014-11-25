/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import mygame.entities.vehicles.AbstractVehicle;
import mygame.entities.vehicles.Ferrari;
import mygame.entities.vehicles.FollowCarControl;
import mygame.entities.vehicles.SoundCarEmitterNode;

/**
 *
 * @author JiriSimecek
 */
public class TestGameState extends AbstractGameState {

    private BulletAppState physics;
    private AbstractVehicle car;
    private float steeringValue, accelerationValue, rearBrake;
    private SoundCarEmitterNode soundCarEmitterNode;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.rootNode = this.app.getRootNode();
//        this.carSound = new AudioNode(app.getAssetManager(), "Sounds/3800_3.8L_V6/3800_V6_Front_4000rpm.wav");
//        start = new AudioNode(app.getAssetManager(), "Sounds/3800_3.8L_V6/3800_V6_3.8l_start.wav");
//        start2 = new AudioNode(app.getAssetManager(), "Sounds/3800_3.8L_V6/3800_V6_3.8l_started.wav");
//        start2.setTimeOffset(6f);
//
//        carSound.setLooping(true);
//        carSound.play();

        this.physics = new BulletAppState();
        this.physics.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(this.physics);
//        this.physics.getPhysicsSpace().enableDebug(this.app.getAssetManager());
        this.physics.getPhysicsSpace().addTickListener(new PhysicsTickListener() {
            public void prePhysicsTick(PhysicsSpace space, float tpf) {
                car.getVehicleControl().accelerate(accelerationValue);
                car.getVehicleControl().steer(steeringValue);
                if (rearBrake > 0) {
                    car.getVehicleControl().accelerate(0);
                }
                car.getVehicleControl().brake(2, rearBrake);
                car.getVehicleControl().brake(3, rearBrake);
            }

            public void physicsTick(PhysicsSpace space, float tpf) {
            }
        });

        initModels();

        initKeyEvents();
    }
    int gear = 1;
    float pitchOffset = 0.5f;
    float maxspeed = 180f;
    float gearStep = 80;

    @Override
    public void update(float tpf) {
        soundCarEmitterNode.setEngineRPM(Math.abs(car.getVehicleControl().getCurrentVehicleSpeedKmHour()) * 100);

        app.getListener().setLocation(app.getCamera().getLocation());
        app.getListener().setRotation(app.getCamera().getRotation());
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.app.getStateManager().detach(physics);
    }

    private void initModels() {
        rootNode.attachChild(SkyFactory.createSky(this.app.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", false));

        // car        
        car = new Ferrari(this.app.getAssetManager());
        car.attachTo(rootNode, physics.getPhysicsSpace());

        // --- Sound ---
        soundCarEmitterNode = new SoundCarEmitterNode(app.getAssetManager());
        car.getVehicleModelNode().attachChild(soundCarEmitterNode);

        // --- Camera ---
        // Disable the default flyby cam
        app.getFlyByCamera().setEnabled(true);
        // Create Camera Control
        FollowCarControl followCarControl = new FollowCarControl(app.getCamera());
        // Offset cam a little, e.g. behind and above the target:
        followCarControl.setCameraOffset(new Vector3f(0, -3, 13));
        // Add control to carNode
        car.getVehicleModelNode().addControl(followCarControl);
        car.getVehicleControl().setPhysicsLocation(new Vector3f(0, 60, 0));

        loadTerrain(app.getAssetManager(), "Textures/Terrains/alps/heightMapSmooth.png", "Textures/Terrains/alps/diffuseMap.png", 1024, 1f);

        // tree
        Spatial tree = this.app.getAssetManager().loadModel("Models/Tree/Tree.mesh.j3o");
        tree.setShadowMode(RenderQueue.ShadowMode.Cast);
        tree.setLocalScale(2f);
        tree.setLocalTranslation(-5, 25, 5);
        CompoundCollisionShape treeShape = new CompoundCollisionShape();
        treeShape.addChildShape(new BoxCollisionShape(new Vector3f(0.5f, 2.5f, 0.5f)), new Vector3f(0, 2.5f, 0));
        tree.addControl(new RigidBodyControl(treeShape, 0));
        physics.getPhysicsSpace().add(tree);

        rootNode.attachChild(tree);

        // ground
//        Box b1 = new Box(100, .25f, 100);
//        b1.scaleTextureCoordinates(new Vector2f(100, 100));
//        Geometry ground = new Geometry("ground", b1);
//
//        Material matG = this.app.getAssetManager().loadMaterial("Materials/bricks2.j3m");
//        ground.setMaterial(matG);
//        ground.setShadowMode(RenderQueue.ShadowMode.Receive);
//        ground.setLocalTranslation(0, -5, 0);
//        TangentBinormalGenerator.generate(ground);
//        ground.addControl(new RigidBodyControl(0));
////        physics.getPhysicsSpace().add(ground);
//
////        rootNode.attachChild(ground);

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
        app.getInputManager().addMapping("Handbrake", new KeyTrigger(KeyInput.KEY_SPACE));
        app.getInputManager().addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        app.getInputManager().addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));

        app.getInputManager().addListener(new ActionListener() {
            float accelerationForce = 2000.0f;
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
                } else if (name.equals("Right")) {
                    if (isPressed) {
                        steeringValue += -.5f;
                    } else {
                        steeringValue += .5f;
                    }
                } else if (name.equals("Accelerate")) {
                    if (isPressed) {
                        accelerationValue -= accelerationForce;
                    } else {
                        accelerationValue += accelerationForce;
                    }
                } else if (name.equals("Brake")) {
                    if (isPressed) {
                        accelerationValue += brakeForce;
                    } else {
                        accelerationValue -= brakeForce;
                    }
                } else if (name.equals("Reset") && !isPressed) {
                    app.getStateManager().detach(TestGameState.this);
                    app.getStateManager().attach(new TestGameState());
                } else if (name.equals("Handbrake")) {
                    if (isPressed) {
                        rearBrake = 1000;
                    } else {
                        rearBrake = 0;
                    }
                }
            }
        }, "Accelerate", "Brake", "Left", "Right", "Handbrake", "Reset", "Exit");
    }

    private void loadTerrain(AssetManager assetManager, String heightMap, String diffuseTexture, int size, float heightScale) {
        Material mat_terrain;

        /**
         * 1. Create terrain material and load four textures into it.
         */
        mat_terrain = new Material(assetManager,
                "Common/MatDefs/Terrain/TerrainLighting.j3md");
        mat_terrain.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey(diffuseTexture, true)));
//        mat_terrain = assetManager.loadMaterial("Materials/terrainTest.j3m");

        /**
         * 2. Create the height map
         */
        Texture heightMapImage = assetManager.loadTexture(heightMap);
        AbstractHeightMap heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), heightScale);
        heightmap.load();

        /**
         * 3. We have prepared material and heightmap. Now we create the actual
         * terrain: 3.1) Create a TerrainQuad and name it "my terrain". 3.2) A
         * good value for terrain tiles is 64x64 -- so we supply 64+1=65. 3.3)
         * We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1). 3.5) We supply the
         * prepared heightmap itself.
         */
        int patchSize = 65;
        TerrainQuad terrain = new TerrainQuad("my terrain", patchSize, size + 1, heightmap.getHeightMap());

        /**
         * 4. We give the terrain its material, position & scale it, and attach
         * it.
         */
        terrain.setMaterial(mat_terrain);
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        terrain.setLocalTranslation(0, -50, 0);
        terrain.setLocalScale(1f, 1f, 1f);
        rootNode.attachChild(terrain);

        /**
         * 5. The LOD (level of detail) depends on were the camera is:
         */
        TerrainLodControl control = new TerrainLodControl(terrain, app.getCamera());
        terrain.addControl(control);


        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(terrain);
        terrain.addControl(new RigidBodyControl(terrainShape, 0));
        physics.getPhysicsSpace().add(terrain);
    }
}
