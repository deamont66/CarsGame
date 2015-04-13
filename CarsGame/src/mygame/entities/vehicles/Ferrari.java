package mygame.entities.vehicles;

import mygame.entities.vehicles.utils.FrictionVehicleControl;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public class Ferrari extends AbstractVehicle {

    public Ferrari(AssetManager manager) {
        super(manager);
    }
    
    @Override
    protected void initVehicle() {
        setModel((Node) getAssetManager().loadModel("Models/Ferrari/Car.j3o"), "chassis", "wheel_LF", "wheel_LR", "wheel_RF", "wheel_RR");
        setMass(1000f);
        setStiffness(20f);
        setDampingCoefficient(0.3f);
        setCompressionCoefficient(0.25f);
    }
    
    @Override
    protected void initVehicleControl() {
        model.setShadowMode(RenderQueue.ShadowMode.Cast);
        Geometry chasis = Utils.findGeom(model, "Car");
        BoundingBox box;
        //Create a hull collision shape for the chassis
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        //Create a vehicle control
        control = new FrictionVehicleControl(carHull, mass);
        model.addControl(control);

        //Setting default values for wheels
        control.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        control.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        control.setSuspensionStiffness(stiffness);
        control.setMaxSuspensionForce(6000);

        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..

        Geometry wheel_fr = Utils.findGeom(model, "WheelFrontRight");
        wheel_fr.center();
        box = (BoundingBox) wheel_fr.getModelBound();
        
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        control.addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.4f, wheelRadius, true);

        Geometry wheel_fl = Utils.findGeom(model, "WheelFrontLeft");
        wheel_fl.center();
        box = (BoundingBox) wheel_fl.getModelBound();
        control.addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.25f, wheelRadius, true);

        Geometry wheel_br = Utils.findGeom(model, "WheelBackRight");
        wheel_br.center();
        box = (BoundingBox) wheel_br.getModelBound();
        control.addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.25f, wheelRadius, false);

        Geometry wheel_bl = Utils.findGeom(model, "WheelBackLeft");
        wheel_bl.center();
        box = (BoundingBox) wheel_bl.getModelBound();
        control.addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.25f, wheelRadius, false);

        control.getWheel(0).setFrictionSlip(2);
        control.getWheel(1).setFrictionSlip(2);
        control.getWheel(2).setFrictionSlip(1.8f);
        control.getWheel(3).setFrictionSlip(1.8f);
    }
    
}
