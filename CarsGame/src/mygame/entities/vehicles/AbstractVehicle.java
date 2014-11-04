/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public abstract class AbstractVehicle {

    private final AssetManager assetManager;
    protected VehicleControl control;
    protected Node model;
    protected String chassiName, wheel_LF, wheel_LR, wheel_RF, wheel_RR;
    private boolean initialized = false;
    protected CollisionShape chassiCollisionShape;
    protected Vector3f wheelOffset = new Vector3f(1f, 0.5f, 2f);
    protected float suspensionRestLength = 0.3f;
    protected float wheelRadius = 0.5f;
    protected float mass = 400f;
    //setting suspension values for wheels, this can be a bit tricky
    //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
    protected float stiffness = 60.0f;//200=f1 car
    protected float compValue = .3f; //(should be lower than damp)
    protected float dampValue = .4f;
    protected float frictionSlip = 10000f;
    protected Vector3f wheelDirection = new Vector3f(0, -1, 0);
    protected Vector3f wheelAxle = new Vector3f(-1, 0, 0);

    public AbstractVehicle(AssetManager manager) {
        this.assetManager = manager;
        init();
    }

    private void init() {
        initVehicle();
        initVehicleControl();
        initialized = true;
    }

    protected void initVehicleControl() {
        if (chassiCollisionShape == null) {
            Geometry chassi = Utils.findGeom(model, chassiName);
            chassi.setLocalTranslation(0, 1, 0);
            chassiCollisionShape = CollisionShapeFactory.createDynamicMeshShape(chassi);
        }
        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        compoundShape.addChildShape(chassiCollisionShape, new Vector3f(0, 1, 0));

        //create vehicle node
        control = new VehicleControl(compoundShape, mass);
        model.addControl(control);

        control.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        control.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        control.setSuspensionStiffness(stiffness);
        control.setMaxSuspensionForce(10000.0f);

        //Create four wheels and add them at their locations

        Geometry wheels1 = Utils.findGeom(model, wheel_LF);
        wheels1.center();
        control.addWheel(wheels1.getParent(), new Vector3f(-wheelOffset.x, wheelOffset.y, wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, true);

        Geometry wheels2 = Utils.findGeom(model, wheel_RF);
        wheels2.center();
        control.addWheel(wheels2.getParent(), new Vector3f(wheelOffset.x, wheelOffset.y, wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, true);

        Geometry wheels3 = Utils.findGeom(model, wheel_LR);
        wheels3.center();
        control.addWheel(wheels3.getParent(), new Vector3f(-wheelOffset.x, wheelOffset.y, -wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, false);

        Geometry wheels4 = Utils.findGeom(model, wheel_RR);
        wheels4.center();
        control.addWheel(wheels4.getParent(), new Vector3f(wheelOffset.x, wheelOffset.y, -wheelOffset.z),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, false);
    }

    protected abstract void initVehicle();

    public void setModel(Node model, String chassi, String wheel_LF, String wheel_LR, String wheel_RF, String wheel_RR) {
        this.model = model;
        this.chassiName = chassi;
        this.wheel_LF = wheel_LF;
        this.wheel_LR = wheel_LR;
        this.wheel_RF = wheel_RF;
        this.wheel_RR = wheel_RR;
    }

    protected void setWheelRadius(float wheelRadius) {
        if (initialized) {
            return;
        }
        this.wheelRadius = wheelRadius;
    }

    protected void setWheelOffset(Vector3f wheelOffset) {
        if (initialized) {
            return;
        }
        this.wheelOffset = wheelOffset;
    }

    protected void setWheelAxle(Vector3f wheelAxle) {
        if (initialized) {
            return;
        }
        this.wheelAxle = wheelAxle;
    }

    protected void setWheelDirection(Vector3f wheelDirection) {
        if (initialized) {
            return;
        }
        this.wheelDirection = wheelDirection;
    }

    public void setFrictionSlip(float frictionSlip) {
        if(initialized) {
            for (int i = 0; i < control.getNumWheels(); i++) {
                control.setFrictionSlip(i, frictionSlip);
            }
        }
        this.frictionSlip = frictionSlip;
    }
    
    

    protected void setChassiCollisionShape(CollisionShape chassiCollisionShape) {
        if (initialized) {
            return;
        }
        this.chassiCollisionShape = chassiCollisionShape;
    }

    public void setCompValue(float compValue) {
        if (initialized) {
            for (int i = 0; i < control.getNumWheels(); i++) {
                control.setSuspensionCompression(i, compValue * 2.0f * FastMath.sqrt(stiffness));
            }
        }
        this.compValue = compValue;
    }

    public void setDampValue(float dampValue) {
        if (initialized) {
            for (int i = 0; i < control.getNumWheels(); i++) {
                control.setSuspensionDamping(i, dampValue * 2.0f * FastMath.sqrt(stiffness));
            }
        }
        this.dampValue = dampValue;
    }

    public void setStiffness(float stiffness) {
        if (initialized) {
            for (int i = 0; i < control.getNumWheels(); i++) {
                control.setSuspensionStiffness(i, stiffness);
                control.setSuspensionCompression(i, compValue * 2.0f * FastMath.sqrt(stiffness));
                control.setSuspensionDamping(i, dampValue * 2.0f * FastMath.sqrt(stiffness));
            }
        }
        this.stiffness = stiffness;
    }

    public void setMass(float mass) {
        if (initialized) {
            control.setMass(mass);
        }
        this.mass = mass;
    }

    protected float getMass() {
        return mass;
    }
    
    public Node getVehicleModelNode() {
        return model;
    }

    protected void setSuspensionRestLength(float suspensionRestLength) {
        if (initialized) {
            return;
        }
        this.suspensionRestLength = suspensionRestLength;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void attachTo(Node rootNode, PhysicsSpace physicsSpace) {
        if (initialized) {
            rootNode.attachChild(model);
            physicsSpace.add(control);
        }
    }

    public VehicleControl getVehicleControl() {
        if (initialized) {
            return control;
        }
        throw new RuntimeException("Car is not initialized yet.");
    }
}
