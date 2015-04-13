/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LodControl;
import mygame.Main;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public class Tree {

    private final Spatial treeSpatial;
    
    public Tree() {
        treeSpatial = Main.APP.getAssetManager().loadModel("Models/Tree/Tree.mesh.j3o");
        treeSpatial.setShadowMode(RenderQueue.ShadowMode.Cast);
        treeSpatial.setLocalScale(2f);
        
        Utils.findGeom(treeSpatial, "Tree-geom-1").addControl(new LodControl());
        Utils.findGeom(treeSpatial, "Tree-geom-2").addControl(new LodControl());
        CompoundCollisionShape treeShape = new CompoundCollisionShape();
        treeShape.addChildShape(new BoxCollisionShape(new Vector3f(0.5f, 2.5f, 0.5f)), new Vector3f(0, 2.5f, 0));
        treeSpatial.addControl(new RigidBodyControl(treeShape, 0));
    }
    
    public void addTo(Node node, BulletAppState physics) {
        physics.getPhysicsSpace().add(treeSpatial);
        node.attachChild(treeSpatial);
    }

    public Spatial getTreeSpatial() {
        return treeSpatial;
    }
    
    public void setLocalTranslation(float x, float y, float z) {
        treeSpatial.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(x, y, z));
    }
}
