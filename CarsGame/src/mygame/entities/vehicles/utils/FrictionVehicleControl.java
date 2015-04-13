/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles.utils;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import mygame.Utils;

public class FrictionVehicleControl extends VehicleControl {

    public FrictionVehicleControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }

    public FrictionVehicleControl(CollisionShape shape) {
        super(shape);
    }

    public FrictionVehicleControl() {
        super();
    }

    protected class WheelState {

        public WheelState() {
            brakeForce = rollingFriction;
            engineForce = 0f;
        }
        public float brakeForce;
        public float engineForce;
    }
    protected ArrayList<WheelState> wheelStates = new ArrayList<WheelState>();
    protected float rollingFriction = 20f;
    public float accelerationInput = 0;

    @Override
    public VehicleWheel addWheel(Spatial spat, Vector3f connectionPoint, Vector3f direction, Vector3f axle, float suspensionRestLength, float wheelRadius, boolean isFrontWheel) {
        wheelStates.add(new WheelState());
        return super.addWheel(spat, connectionPoint, direction, axle, suspensionRestLength, wheelRadius, isFrontWheel);
    }

    @Override
    public void removeWheel(int wheel) {
        wheelStates.remove(wheel);
        super.removeWheel(wheel);

    }

    
    @Override
    public void accelerate(float force) {
        accelerationInput = Utils.clamp(force, 0, 1);
        for (int i = 0; i < wheels.size(); i++) {
            accelerate(i, force);
        }
    }

    @Override
    public void accelerate(int wheel, float force) {
        WheelState ws = wheelStates.get(wheel);
        ws.engineForce = force;
        applyWheelState(wheel, ws);
    }

    @Override
    public void brake(float force) {
        for (int i = 0; i < wheels.size(); i++) {
            brake(i, force);
        }
    }

    @Override
    public void brake(int wheel, float force) {
        WheelState ws = wheelStates.get(wheel);
        ws.brakeForce = force < rollingFriction ? rollingFriction : force;
        applyWheelState(wheel, ws);
    }

    protected void applyWheelState(int wheel, WheelState ws) {
        if (Math.abs(ws.engineForce) - ws.brakeForce > 0) {
            super.accelerate(wheel, ws.engineForce - ws.brakeForce);
            super.brake(wheel, 0f);
        } else {
            super.accelerate(wheel, 0f);
            super.brake(wheel, -(ws.engineForce - ws.brakeForce));
        }
    }
}
