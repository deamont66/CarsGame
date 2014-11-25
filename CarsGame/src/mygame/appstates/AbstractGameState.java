/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import mygame.Main;

/**
 *
 * @author JiriSimecek
 */
public class AbstractGameState extends AbstractAppState {
    
    protected Main app;
    protected Node rootNode = new Node("AbstractGameRootNode-" + this.getClass().getName());
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (Main) app;
        ((SimpleApplication) app).getRootNode().attachChild(rootNode);
    }
    
    @Override
    public void update(float tpf) {
        
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.app.getInputManager().clearMappings();
        this.app.getRootNode().detachAllChildren();
    }
}
