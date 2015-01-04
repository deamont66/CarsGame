/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import java.util.Iterator;
import mygame.Main;

/**
 *
 * @author JiriSimecek
 */
public abstract class AbstractGameState extends AbstractAppState {
    
    protected Main app;
    protected Node rootNode;
    private boolean paused = false;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (Main) app;
        this.rootNode = this.app.getRootNode();
        initializeRenderersAndFPPs();
    }
    
    public void initializeRenderersAndFPPs() {
        this.app.getViewPort().clearProcessors();
    }
    
    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        super.cleanup();
        
        this.app.getInputManager().clearMappings();
        this.app.getRootNode().detachAllChildren();
        
        this.app.getViewPort().clearProcessors();
        for (Iterator<Light> it = this.app.getRootNode().getWorldLightList().iterator(); it.hasNext();) {
            Light light = it.next();
            this.app.getRootNode().removeLight(light);
        }
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void pause() {
        paused = true;
        BulletAppState bullet = app.getStateManager().getState(BulletAppState.class);
        if(bullet != null) {
            bullet.setSpeed(0);
        }
    }
    
    public void resume() {
        paused = false;
        BulletAppState bullet = app.getStateManager().getState(BulletAppState.class);
        if(bullet != null) {
            bullet.setSpeed(1);
        }
    }
}
