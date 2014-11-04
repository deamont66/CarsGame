/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;

/**
 *
 * @author JiriSimecek
 */
public class AbstractGameState extends AbstractAppState {
    
    protected SimpleApplication app;
    protected Node rootNode = new Node("AbstractGameRootNode-" + this.getClass().getName());
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (SimpleApplication) app;
        ((SimpleApplication) app).getRootNode().attachChild(rootNode);
        System.out.println(rootNode.getName());
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
