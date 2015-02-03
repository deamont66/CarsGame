package mygame.gamestates;

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
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.*;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.*;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.water.WaterFilter;
import de.lessvoid.nifty.elements.Element;
import java.util.Iterator;
import mygame.CodeListener;

import mygame.Settings;
import mygame.entities.vehicles.AbstractVehicle;
import mygame.entities.vehicles.BasicVehicle;
import mygame.entities.vehicles.Ferrari;
import mygame.entities.vehicles.FollowCarControl;
import mygame.entities.vehicles.GoKart;
import mygame.entities.vehicles.SoundCarEmitterNode;
import mygame.entities.vehicles.TestVehicle;
import mygame.guicontrollers.GameGuiController;

/**
 *
 * @author JiriSimecek
 */
public class TestGameState extends AbstractGameState {

    private BulletAppState physics;
    private DepthOfFieldFilter dofFilter;
    private WaterFilter water;
    private AbstractVehicle car;
    private float steeringValue, accelerationValue, rearBrake, brake;
    private SoundCarEmitterNode soundCarEmitterNode;
    private FollowCarControl followCarControl;
    private Geometry waterGeo;
    private float waterHeight = 0f;
    private float time = 0.0f;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app.getNifty().fromXml("Interface/inGame.xml", "start", new GameGuiController(this.app));

        this.physics = new BulletAppState();
        this.physics.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
        stateManager.attach(this.physics);
//        this.physics.getPhysicsSpace().enableDebug(app.getAssetManager());

        this.physics.getPhysicsSpace().addTickListener(new PhysicsTickListener() {
            public void prePhysicsTick(PhysicsSpace space, float tpf) {
                car.getVehicleControl().accelerate(0, accelerationValue);
                car.getVehicleControl().accelerate(1, accelerationValue);
                car.getVehicleControl().steer(steeringValue);
                if (rearBrake > 0 || brake > 0) {
                    car.getVehicleControl().accelerate(0);
                }
                if(brake > 0 && car.getVehicleControl().getCurrentVehicleSpeedKmHour() < 1) {
                    car.getVehicleControl().accelerate(-100);
                } else {
                    car.getVehicleControl().brake(brake);
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

    @Override
    public void initializeRenderersAndFPPs() {
        this.app.getViewPort().clearProcessors();
        for (Iterator<Light> it = this.app.getRootNode().getWorldLightList().iterator(); it.hasNext();) {
            Light light = it.next();
            this.app.getRootNode().removeLight(light);
        }
        if (waterGeo != null) {
            rootNode.detachChild(waterGeo);
        }

        Settings settings = Settings.getSettings();
        DirectionalLight dL = new DirectionalLight();
        dL.setDirection(new Vector3f(-0.44923937f, -0.415695f, -0.7908107f)); // -0.1400655f, -0.4112364f, 0.9007033f
        this.app.getRootNode().addLight(dL);

        AmbientLight aL = new AmbientLight();
        this.app.getRootNode().addLight(aL);

        if (settings.getShadowType() == Settings.ShadowType.RENDERER || (settings.getShadowType() == Settings.ShadowType.FILTER && !settings.isPostProcessingEnabled())) {
            DirectionalLightShadowRenderer shadowRender = new DirectionalLightShadowRenderer(this.app.getAssetManager(), settings.getShadowMapSize(), 3);
            shadowRender.setLight(dL);
            shadowRender.setShadowCompareMode(CompareMode.Hardware);
            shadowRender.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
            this.app.getViewPort().addProcessor(shadowRender);
        }

        if (!settings.isPostProcessingEnabled()) {
            SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(this.app.getAssetManager());
            waterProcessor.setReflectionScene(rootNode);
            waterProcessor.setLightPosition(dL.getDirection());

            Vector3f waterLocation = new Vector3f(0, waterHeight + 0.7f, 0);
            waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
            waterProcessor.setWaterColor(new ColorRGBA(0.0078f, 0.3176f, 0.5f, 1.0f));
            waterProcessor.setTexScale(45f);
            waterProcessor.setWaterDepth(40);         // transparency of water
            waterProcessor.setDistortionScale(0.05f); // strength of waves
            waterProcessor.setWaveSpeed(0.05f);       // speed of waves

            Quad quad = new Quad(3000, 3000);
            quad.scaleTextureCoordinates(new Vector2f(60f, 60f));

            waterGeo = new Geometry("water", quad);
            waterGeo.setLocalTranslation(-1500, waterHeight + 0.7f, 1500);
            waterGeo.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
            waterGeo.setMaterial(waterProcessor.getMaterial());
            waterGeo.setShadowMode(RenderQueue.ShadowMode.Off);

            rootNode.attachChild(waterGeo);
            this.app.getViewPort().addProcessor(waterProcessor);
        }

        if (settings.isPostProcessingEnabled()) {
            FilterPostProcessor fpp = new FilterPostProcessor(this.app.getAssetManager());

            water = new WaterFilter(rootNode, dL.getDirection());
            water.setWaterHeight(waterHeight - 0.4f);
            fpp.addFilter(water);

            PosterizationFilter filter = new PosterizationFilter();
            filter.setNumColors(8);
            filter.setStrength(0.3f);
//            fpp.addFilter(filter);

            TranslucentBucketFilter translucent = new TranslucentBucketFilter();
            fpp.addFilter(translucent);

            if (settings.isFilterEnabled("FXAA")) {
                FXAAFilter fxaaFilter = new FXAAFilter();
                fpp.addFilter(fxaaFilter);
            }

// <editor-fold defaultstate="collapsed" desc="other filters (SSAP, BLOOM, LIGHT SCATERRING (direct), DEPTH of FIELD, and shadow filter)">
            if (settings.isFilterEnabled("SSAO")) {
                SSAOFilter ssaoFilter = new SSAOFilter();
                fpp.addFilter(ssaoFilter);
            }
            if (settings.isFilterEnabled("BLOOM")) {
                BloomFilter bloomFilter = new BloomFilter(BloomFilter.GlowMode.Objects);
                fpp.addFilter(bloomFilter);
            }

            // START OF LIGHT SCATTERING
            if (settings.isFilterEnabled("LSF")) {
                LightScatteringFilter fsFilter = new LightScatteringFilter();
                fsFilter.setLightPosition(dL.getDirection().mult(-3000));
                fsFilter.setLightDensity(1.f);
                fpp.addFilter(fsFilter);
            }
            // END OF LIGHT SCATTERING

            if (settings.isFilterEnabled("FOG")) {
                FogFilter fog = new FogFilter(ColorRGBA.White, 0.5f, 100);
                fpp.addFilter(fog);
            }
            if (settings.isFilterEnabled("DOF")) {
                dofFilter = new DepthOfFieldFilter();
                fpp.addFilter(dofFilter);
            }

            if (settings.getShadowType() == Settings.ShadowType.FILTER) {
                // Creates shadow filter, this technique doesnt work on intel integrated card :(, sadly. 
                DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(this.app.getAssetManager(), settings.getShadowMapSize(), 3);
                shadowFilter.setLight(dL);
                shadowFilter.setShadowCompareMode(CompareMode.Hardware);
                shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
                fpp.addFilter(shadowFilter);
            }
// </editor-fold>


            this.app.getViewPort().addProcessor(fpp);
        }
    }

    @Override
    public void initModels() {
        rootNode.attachChild(SkyFactory.createSky(this.app.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", false));

        Spatial s = this.app.getAssetManager().loadModel("Models/ramp.j3o");
        s.setLocalTranslation(200, 0, 0);
        s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        s.addControl(new RigidBodyControl(0));
        physics.getPhysicsSpace().add(s);
        rootNode.attachChild(s);

        s = this.app.getAssetManager().loadModel("Models/upRoad.j3o");
        s.setLocalTranslation(212, 0, 0);
        s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        s.addControl(new RigidBodyControl(0));
        physics.getPhysicsSpace().add(s);
        rootNode.attachChild(s);

        s = this.app.getAssetManager().loadModel("Models/upRoad.j3o");
        s.setLocalTranslation(224, 0, 0);
        s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        s.addControl(new RigidBodyControl(0));
        physics.getPhysicsSpace().add(s);
        rootNode.attachChild(s);

        // car        
        car = new GoKart(this.app.getAssetManager(), ColorRGBA.Pink);
        car.attachTo(rootNode, physics.getPhysicsSpace());

        // --- Camera ---
        // Disable the default flyby cam
        app.getFlyByCamera().setEnabled(false);
        // Create Camera Control
        followCarControl = new FollowCarControl(app.getCamera());
        // Offset cam a little, e.g. behind and above the target:
        followCarControl.setBehindCameraOffset(new Vector3f(0, -5, 13));
        followCarControl.setFrontCameraOffset(new Vector3f(0, -0.5f, -1.8f));
        followCarControl.setInsideCameraOffset(new Vector3f(0, -2f, 1));
        // Add control to carNode
        car.getVehicleModelNode().addControl(followCarControl);
//        car.getVehicleControl().setPhysicsLocation(new Vector3f(4, 40, 4));
        car.getVehicleControl().setPhysicsLocation(new Vector3f(0, 5, 0));

//        loadTerrain(app.getAssetManager(), "Textures/Terrains/alps/heightMapSmooth.png", "Textures/Terrains/alps/diffuseMap.png", 1024, 0.5f);
        loadTerrain(app.getAssetManager(), "Textures/Terrains/temp/heightmap_1.png", "Textures/Terrains/temp/textureShadow.png", 1024, 0.5f);

        // tree
        Spatial tree = this.app.getAssetManager().loadModel("Models/Tree/Tree.mesh.j3o");
        tree.setShadowMode(RenderQueue.ShadowMode.Cast);
        tree.setLocalScale(2f);
        tree.setLocalTranslation(-35, 16.5f, 10);
        CompoundCollisionShape treeShape = new CompoundCollisionShape();
        treeShape.addChildShape(new BoxCollisionShape(new Vector3f(0.5f, 2.5f, 0.5f)), new Vector3f(0, 2.5f, 0));
        tree.addControl(new RigidBodyControl(treeShape, 0));
        physics.getPhysicsSpace().add(tree);

        rootNode.attachChild(tree);

        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = this.app.getAssetManager().loadMaterial("Materials/bricks.j3m");
        geom.setShadowMode(RenderQueue.ShadowMode.Cast);
        geom.setMaterial(mat);
        geom.move(0, 30, 0);
        geom.rotate(FastMath.DEG_TO_RAD * 45f, 0, FastMath.DEG_TO_RAD * 45f);
        TangentBinormalGenerator.generate(geom);
        RigidBodyControl boxBody = new RigidBodyControl(100);
        geom.addControl(boxBody);
        physics.getPhysicsSpace().add(geom);
        rootNode.attachChild(geom);
    }

    @Override
    public void initKeyEvents() {
        app.getInputManager().clearMappings();

        Settings settings = Settings.getSettings();
        app.getInputManager().addMapping("Accelerate", new KeyTrigger(settings.getKeyBinding("key_accelerate")));
        app.getInputManager().addMapping("Brake", new KeyTrigger(settings.getKeyBinding("key_brake")));
        app.getInputManager().addMapping("Left", new KeyTrigger(settings.getKeyBinding("key_left")));
        app.getInputManager().addMapping("Right", new KeyTrigger(settings.getKeyBinding("key_right")));
        app.getInputManager().addMapping("Handbrake", new KeyTrigger(settings.getKeyBinding("key_handbrake")));
        app.getInputManager().addMapping("Camera", new KeyTrigger(settings.getKeyBinding("key_camera")));

        app.getInputManager().addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        app.getInputManager().addMapping("Debug", new KeyTrigger(KeyInput.KEY_1));
        app.getInputManager().addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));

        app.getInputManager().addListener(actionListener, new String[]{"Accelerate", "Brake", "Left", "Right", "Handbrake", "Camera", "Reset", "Exit", "Debug"});

        codeListener.addCodeMapping("unstuck");
        codeListener.addCodeMapping("car1");
        codeListener.addCodeMapping("car2");
        codeListener.addCodeMapping("car3");
        codeListener.addCodeMapping("car4");
        
        app.getInputManager().removeRawInputListener(codeListener);
        app.getInputManager().addRawInputListener(codeListener);
    }

    @Override
    public void update(float tpf) {
        if (dofFilter != null) {
            dofFilter.setFocusDistance(Vector3f.ZERO.distance(new Vector3f(0, -3, 13)) / 10);
        }
        if (water != null) {
            time += tpf;
            waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 0.2f - 0.1f;
//            water.setWaterHeight(waterHeight);
        }
        if(car instanceof GoKart) {
            ((GoKart)car).setSteeringWheelAngle(steeringValue);
        }

        app.getListener().setLocation(app.getCamera().getLocation());
        app.getListener().setRotation(app.getCamera().getRotation());
    }

    @Override
    public void cleanup() {
        super.cleanup();
        
        this.app.getStateManager().detach(physics);
        this.app.getInputManager().removeRawInputListener(codeListener);
        car.cleanup();
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
        terrain.setLocalTranslation(0, -63f, 0);
        terrain.setLocalScale(1f, 1f, 1f);
        rootNode.attachChild(terrain);

        /**
         * 5. The LOD (level of detail) depends on were the camera is:
         */
        TerrainLodControl control = new TerrainLodControl(terrain, app.getCamera());
        terrain.addControl(control);


        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(terrain);
        RigidBodyControl rbc = new RigidBodyControl(terrainShape, 0);
        rbc.setFriction(0.2f);
        terrain.addControl(rbc);
        physics.getPhysicsSpace().add(terrain);
    }

    public float getCarSpeed() {
        return car.getVehicleControl().getCurrentVehicleSpeedKmHour();
    }
    
    private ActionListener actionListener = new ActionListener() {
        float accelerationForce = 8000.0f;
        float brakeForce = 1000.0f;

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Exit") && !isPressed) {
                if (!TestGameState.this.isPaused()) {
                    TestGameState.this.pause();
                } else {
                    TestGameState.this.resume();
                    Element element = app.getNifty().getCurrentScreen().findElementByName("videoSettings");
                    element.setVisible(false);
                    element = app.getNifty().getCurrentScreen().findElementByName("renderingSettings");
                    element.setVisible(false);
                    element = app.getNifty().getCurrentScreen().findElementByName("keybindingSettings");
                    element.setVisible(false);
                    element = app.getNifty().getCurrentScreen().findElementByName("backToMenu");
                    element.setVisible(false);
                    element = app.getNifty().getCurrentScreen().findElementByName("exitGame");
                    element.setVisible(false);
                }
                Element element = app.getNifty().getCurrentScreen().findElementByName("ingameMenu");
                element.setVisible(TestGameState.this.isPaused());

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
                    accelerationValue = accelerationForce;
                } else {
                    accelerationValue = 0;
                }
            } else if (name.equals("Brake")) {
                if (isPressed) {
                    brake = brakeForce;
                } else {
                    brake = 0;
                }
            } else if (name.equals("Reset") && !isPressed) {
                initKeyEvents();
                initializeRenderersAndFPPs();
            } else if (name.equals("Handbrake")) {
                rearBrake = isPressed ? 5000 : 0;
            } else if (name.equals("Camera") && !isPressed) {
                followCarControl.setCameraMode(FollowCarControl.CameraMode.getNext(followCarControl.getCameraMode()));
            } else if (name.equals("Debug") && !isPressed) {
                System.out.println(TestGameState.this.car.getVehicleControl().getPhysicsLocation());
                System.out.println(TestGameState.this.car.getVehicleControl().getPhysicsRotation());
            }
        }
    };
    private CodeListener codeListener = new CodeListener() {
        @Override
        public void onCodeDown(String name, String code) {
            if (name.equals("unstuck")) {
                car.getVehicleControl().setPhysicsLocation(car.getVehicleControl().getPhysicsLocation().add(new Vector3f(0, 5, 0)));
                car.getVehicleControl().setPhysicsRotation(Quaternion.IDENTITY);
            } else if(name.equals("car1")) {
                Vector3f oldPos = car.getVehicleControl().getPhysicsLocation();
                Quaternion oldRot = car.getVehicleControl().getPhysicsRotation();
                
                car.getVehicleModelNode().removeControl(followCarControl);
                rootNode.detachChild(car.getVehicleModelNode());
                physics.getPhysicsSpace().remove(car.getVehicleControl());
                car.cleanup();
                
                car = new GoKart(app.getAssetManager());
                car.attachTo(rootNode, physics.getPhysicsSpace());
                car.getVehicleModelNode().addControl(followCarControl);
                car.getVehicleControl().setPhysicsLocation(oldPos);
                car.getVehicleControl().setPhysicsRotation(oldRot);
            } else if(name.equals("car2")) {
                Vector3f oldPos = car.getVehicleControl().getPhysicsLocation();
                Quaternion oldRot = car.getVehicleControl().getPhysicsRotation();
                
                car.getVehicleModelNode().removeControl(followCarControl);
                rootNode.detachChild(car.getVehicleModelNode());
                physics.getPhysicsSpace().remove(car.getVehicleControl());
                car.cleanup();
                
                car = new TestVehicle(app.getAssetManager());
                car.attachTo(rootNode, physics.getPhysicsSpace());
                car.getVehicleModelNode().addControl(followCarControl);
                car.getVehicleControl().setPhysicsLocation(oldPos);
                car.getVehicleControl().setPhysicsRotation(oldRot);
            } else if(name.equals("car3")) {
                Vector3f oldPos = car.getVehicleControl().getPhysicsLocation();
                Quaternion oldRot = car.getVehicleControl().getPhysicsRotation();
                
                car.getVehicleModelNode().removeControl(followCarControl);
                rootNode.detachChild(car.getVehicleModelNode());
                physics.getPhysicsSpace().remove(car.getVehicleControl());
                car.cleanup();

                car = new BasicVehicle(app.getAssetManager());
                car.attachTo(rootNode, physics.getPhysicsSpace());
                car.getVehicleModelNode().addControl(followCarControl);
                car.getVehicleControl().setPhysicsLocation(oldPos);
                car.getVehicleControl().setPhysicsRotation(oldRot);
            } else if(name.equals("car4")) {
                Vector3f oldPos = car.getVehicleControl().getPhysicsLocation();
                Quaternion oldRot = car.getVehicleControl().getPhysicsRotation();
                
                car.getVehicleModelNode().removeControl(followCarControl);
                rootNode.detachChild(car.getVehicleModelNode());
                physics.getPhysicsSpace().remove(car.getVehicleControl());
                car.cleanup();

                car = new Ferrari(app.getAssetManager());
                car.attachTo(rootNode, physics.getPhysicsSpace());
                car.getVehicleModelNode().addControl(followCarControl);
                car.getVehicleControl().setPhysicsLocation(oldPos);
                car.getVehicleControl().setPhysicsRotation(oldRot);
            }
        }
    };
}
