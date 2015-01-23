/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author JiriSimecek
 */
public class TestVehicle extends AbstractVehicle {

    public TestVehicle(AssetManager manager) {
        super(manager);
    }

    @Override
    protected void initVehicle() {
        setModel((Node) getAssetManager().loadModel("Models/Vehicles/Test/carTest.j3o"), "Cube", "front_left1", "rear_left1", "front_right1", "rear_right1");
        setWheelOffset(new Vector3f(1.1f, 1.0f, 0.95f));
        setCenterOfMassOffset(new Vector3f(0, 0.5f, 0));
        setFrictionSlip(3f);
    }
    
}
