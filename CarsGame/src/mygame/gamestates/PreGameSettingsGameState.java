/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gamestates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.filters.PosterizationFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Spatial;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.tools.Color;
import java.util.Iterator;
import mygame.Settings;
import mygame.entities.Tree;
import mygame.entities.vehicles.AbstractVehicle;
import mygame.entities.vehicles.GoKart;
import mygame.guicontrollers.PreGameSettingsController;

/**
 *
 * @author JiriSimecek
 */
public class PreGameSettingsGameState extends AbstractGameState {

    private BulletAppState physics;
    private AbstractVehicle car;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app.getNifty().fromXml("Interface/preGameSettings.xml", "start", new PreGameSettingsController(this.app));
        
        this.physics = new BulletAppState();
        this.physics.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
        this.physics.setSpeed(0.0f);
        stateManager.attach(this.physics);
        
        this.app.getCamera().setRotation(new Quaternion(0, 1, 0, 0));
        this.app.getCamera().setLocation(new Vector3f(-1.970217f, 1.6225399f, 10.0f));
        
        initModels();
        initKeyEvents();
    }    
    
    @Override
    public void initModels() {
        rootNode.attachChild(SkyFactory.createSky(this.app.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", false));
        
        car = new GoKart(this.app.getAssetManager(), ColorRGBA.Red);
        car.attachTo(rootNode, physics.getPhysicsSpace());
        car.getVehicleControl().setPhysicsRotation(new Quaternion(0.056701127f, -0.32208675f, -0.019517124f, 0.9448091f));
        car.getVehicleControl().brake(10000f);
        car.getVehicleControl().steer(-0.4f);
        
        Spatial road = this.app.getAssetManager().loadModel("Models/roadUp20.j3o");
        road.setLocalTranslation(0, -5, -2);
        road.rotate(0, (float) 0, 0);
        CollisionShape roadShape = CollisionShapeFactory.createMeshShape(road);
        RigidBodyControl roadPhysicsControl = new RigidBodyControl(roadShape, 0);
        roadPhysicsControl.setUserObject("road");
        road.addControl(roadPhysicsControl);
        physics.getPhysicsSpace().add(road);
        rootNode.attachChild(road);
        
        Tree tree = new Tree();
        tree.setLocalTranslation(5, 1, -8);
        tree.addTo(rootNode, physics);
    }
    
    @Override
    public void initializeRenderersAndFPPs() {
        this.app.getViewPort().clearProcessors();
        for (Iterator<Light> it = this.app.getRootNode().getWorldLightList().iterator(); it.hasNext();) {
            Light light = it.next();
            this.app.getRootNode().removeLight(light);
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
        
        if (settings.isPostProcessingEnabled()) {
            FilterPostProcessor fpp = new FilterPostProcessor(this.app.getAssetManager());
            
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
    public void cleanup() {
        super.cleanup();
        
        this.app.getStateManager().detach(physics);
        car.cleanup();
    }
    
    @Override
    public void initKeyEvents() {
        app.getInputManager().clearMappings();
        
        Settings settings = Settings.getSettings();
        app.getInputManager().addMapping("Left", new KeyTrigger(settings.getKeyBinding("key_left")));
        app.getInputManager().addMapping("Right", new KeyTrigger(settings.getKeyBinding("key_right")));
        
        app.getInputManager().addMapping("Play", new KeyTrigger(KeyInput.KEY_RETURN));
        
        app.getInputManager().addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
        
        app.getInputManager().addListener(actionListener, new String[]{"Left", "Right", "Up", "Down", "Play", "Exit"});  
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Left")) {
                // změna na předchozí barvu
            } else if(name.equals("Right") && !isPressed) {
                // změna na další barvu
            } else if (name.equals("Play") && !isPressed) {
                PreGameSettingsGameState menu = app.getStateManager().getState(PreGameSettingsGameState.class);
                if (menu != null) {
                    app.getStateManager().detach(menu);
                }
                TestGameState gameState = new TestGameState();
                gameState.setVehicleColor(((GoKart) car).getColor());
                app.getStateManager().attach(gameState);
            } else if(name.equals("Exit") && !isPressed) {
                PreGameSettingsGameState menu = app.getStateManager().getState(PreGameSettingsGameState.class);
                if (menu != null) {
                    app.getStateManager().detach(menu);
                }
                app.getStateManager().attach(new MenuGameState());
            }
        }
    };
        
    public void respawnCarWithColor(Color color) {
        rootNode.detachChild(car.getVehicleModelNode());
        physics.getPhysicsSpace().remove(car.getVehicleControl());
        car.cleanup();
        
        ColorRGBA jmeColor = new ColorRGBA(color.getRed(), color.getGreen(),color.getBlue(), color.getAlpha());
        car = new GoKart(this.app.getAssetManager(), jmeColor);
        car.attachTo(rootNode, physics.getPhysicsSpace());
        car.getVehicleControl().setPhysicsRotation(new Quaternion(0.056701127f, -0.32208675f, -0.019517124f, 0.9448091f));
        car.getVehicleControl().brake(10000f);
        car.getVehicleControl().steer(-0.4f);
    }
}
