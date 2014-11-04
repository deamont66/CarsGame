package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import mygame.appstates.TestGameState;
import mygame.entities.vehicles.Impreza;

public class Main extends SimpleApplication {

    private DepthOfFieldFilter dofFilter;
    private Impreza car;

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setVSync(false);
        settings.setTitle("CourseWork 0.01 Alpha");
        settings.setResolution(1280, 720);

        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);

        flyCam.setMoveSpeed(25);
        cam.setLocation(new Vector3f(2.3358383f, 4.598832f, -16.907782f));
        cam.setRotation(new Quaternion(0.18012555f, -0.10304355f, 0.018978154f, 0.9780474f));

        
        
        initLightsAndFilters();

        TestGameState game = new TestGameState();
        stateManager.attach(game);
    }
    
    private void initLightsAndFilters() {
        DirectionalLight dL = new DirectionalLight();
        dL.setDirection(new Vector3f(-0.44923937f, -0.415695f, -0.7908107f)); // -0.1400655f, -0.4112364f, 0.9007033f
        rootNode.addLight(dL);

        AmbientLight aL = new AmbientLight();
        rootNode.addLight(aL);

        DirectionalLightShadowRenderer shadowRender = new DirectionalLightShadowRenderer(getAssetManager(), 512, 3);
        shadowRender.setLight(dL);
        shadowRender.setEdgesThickness(3);
        shadowRender.setShadowCompareMode(CompareMode.Hardware);
        shadowRender.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        viewPort.addProcessor(shadowRender);

        // post processing (filters)
        FilterPostProcessor fpp = new FilterPostProcessor(getAssetManager());

        FXAAFilter fxaaFilter = new FXAAFilter();
        fpp.addFilter(fxaaFilter);

// <editor-fold defaultstate="collapsed" desc="other filters (SSAP, BLOOM, LIGHT SCATERRING (direct), DEPTH of FIELD, and shadow filter)">
//        SSAOFilter ssaoFilter = new SSAOFilter();
//        fpp.addFilter(ssaoFilter);


        BloomFilter bloomFilter = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloomFilter);

        // START OF LIGHT SCATTERING
        LightScatteringFilter fsFilter = new LightScatteringFilter();
        fsFilter.setLightPosition(dL.getDirection().mult(-3000));
        fsFilter.setLightDensity(1.f);
        fpp.addFilter(fsFilter);
        // END OF LIGHT SCATTERING 

        FogFilter fog = new FogFilter(ColorRGBA.White, 0.5f, 10);
//        fpp.addFilter(fog);
        
        dofFilter = new DepthOfFieldFilter();
        fpp.addFilter(dofFilter);

        // Creates shadow filter, this technique doesnt work on intel integrated card :(, sadly. 
//        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 2048, 3);
//        shadowFilter.setLambda(0.55f);
//        shadowFilter.setLight(dL);
//        shadowFilter.setShadowCompareMode(CompareMode.Hardware);
//        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Dither);
//        fpp.addFilter(shadowFilter);
// </editor-fold>

        viewPort.addProcessor(fpp);
    }


    @Override
    public void simpleUpdate(float tpf) {
        Ray r = new Ray(getCamera().getLocation(), getCamera().getDirection());
        CollisionResults result = new CollisionResults();
        rootNode.collideWith(r, result);
        if (result.size() > 0) {
            dofFilter.setFocusDistance(result.getClosestCollision().getDistance() / 10);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void stop(boolean waitFor) {
        super.stop(waitFor);
        System.exit(0);
    }
}