/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Standard implementation of car.
 * @author JiriSimecek
 */
public class BasicVehicle extends AbstractVehicle {

    public BasicVehicle(AssetManager assetManager) {
        super(assetManager);
    }
    
    protected void initVehicle() {
        setModel((Node) getAssetManager().loadModel("Models/Vehicles/Car/car.j3o"), "Chassis", "wheel_LF", "wheel_LR", "wheel_RF", "wheel_RR");
        setWheelOffset(new Vector3f(0.7f, 1f, 1.28f));
        setWheelRadius(.3f);
        setFrictionSlip(1f);
    }
}
