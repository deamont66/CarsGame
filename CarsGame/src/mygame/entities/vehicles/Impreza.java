package mygame.entities.vehicles;

import mygame.entities.vehicles.utils.FrictionVehicleControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public class Impreza extends AbstractVehicle {

//    SpotLight frontLight;

    public Impreza(AssetManager manager) {
        super(manager);
    }

    @Override
    protected void initVehicle() {
        setModel((Node) getAssetManager().loadModel("Models/Vehicles/Impreza/impreza.j3o"), "Car", "wheel_LF", "wheel_LR", "wheel_RF", "wheel_RR");
        setWheelOffset(new Vector3f(0.95f, 0.8f, 1.60f));
        setWheelRadius(.4f);
        setCompressionCoefficient(0.4f);
        setDampingCoefficient(0.8f);
        setFrictionSlip(2f);

//        frontLight = new SpotLight();
//        frontLight.setColor(ColorRGBA.White.multLocal(10f));
//        frontLight.setSpotInnerAngle(FastMath.QUARTER_PI / 5 * 4);
//        frontLight.setSpotOuterAngle(FastMath.QUARTER_PI);
//        frontLight.setSpotRange(300f);
    }

    @Override
    protected void initVehicleControl() {
        model.setShadowMode(RenderQueue.ShadowMode.Cast);
//        Geometry frontLightGeo = Utils.findGeom(model, "frontLight");
//        frontLightGeo.getMaterial().setTexture("GlowMap", getAssetManager().loadTexture("Textures/impreza/obj/light_ma.png"));
        if (chassiCollisionShape == null) {
            Node others = Utils.findNode(model, "all");
            others.setLocalTranslation(0, 0.2f, 0.5f);
            Geometry chassi = Utils.findGeom(model, chassiName);
            chassiCollisionShape = CollisionShapeFactory.createDynamicMeshShape(chassi);
        }
        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        compoundShape.addChildShape(chassiCollisionShape, new Vector3f(0, 0.2f, 0.5f));

        //create vehicle node
        control = new FrictionVehicleControl(compoundShape, mass);
        model.addControl(control);

        control.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        control.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        control.setSuspensionStiffness(stiffness);
        control.setMaxSuspensionForce(10000.0f);
        control.setFrictionSlip(frictionSlip);

        //Create four wheels and add them at their locations

        Node wheels1 = Utils.findNode(model, wheel_LF);
        Utils.centerAllGeometries(wheels1);
        control.addWheel(wheels1, new Vector3f(-wheelOffset.x, wheelOffset.y, wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, true);

        Node wheels2 = Utils.findNode(model, wheel_RF);
        Utils.centerAllGeometries(wheels2);
        control.addWheel(wheels2, new Vector3f(wheelOffset.x, wheelOffset.y, wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, true);

        Node wheels3 = Utils.findNode(model, wheel_LR);
        Utils.centerAllGeometries(wheels3);
        control.addWheel(wheels3, new Vector3f(-wheelOffset.x, wheelOffset.y, -wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, false);

        Node wheels4 = Utils.findNode(model, wheel_RR);
        Utils.centerAllGeometries(wheels4);
        control.addWheel(wheels4, new Vector3f(wheelOffset.x, wheelOffset.y, -wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, false);
    }
}
