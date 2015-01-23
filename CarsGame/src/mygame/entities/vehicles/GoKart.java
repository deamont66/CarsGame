/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

/**
 *
 * @author JiriSimecek
 */
public class GoKart extends AbstractVehicle {

    public GoKart(AssetManager manager) {
        super(manager);
    }

    
    @Override
    protected void initVehicle() {
        setModel((Node) getAssetManager().loadModel("Models/Vehicles/kart 1.j3o"), "chassis", "Wheel front_L1", "Wheel back_L1", "Wheel front_R1", "Wheel back_R1");
        
        setCenterOfMassOffset(new Vector3f(0, 0.0f, .25f));
        setWheelOffset(new Vector3f(1.6f, .7f, 1.3f));
        setFrictionSlip(5);
        
    }

    @Override
    protected void initVehicleControl() {
        super.initVehicleControl();
        model.setShadowMode(RenderQueue.ShadowMode.Cast);
    }
    
    
    
}
