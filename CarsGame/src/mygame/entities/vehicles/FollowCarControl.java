/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public class FollowCarControl extends AbstractControl {

    private final Camera cam;
    private Vector3f camBehindOffset = new Vector3f();
    private Vector3f camFrontOffset = new Vector3f();
    private Vector3f camInsideOffset = new Vector3f();
    private CameraMode cameraMode = CameraMode.BEHIND;

    public FollowCarControl(Camera cam) {
        this.cam = cam;
    }

    @Override
    protected void controlUpdate(float tpf) {
        VehicleControl vehicle = spatial.getControl(VehicleControl.class);
        if (vehicle != null) {
            if (cameraMode == CameraMode.BEHIND) {
                // normalized direction of the car
                Vector3f direction = vehicle.getLinearVelocity().normalize();
                // quternion rotation from direction
                Quaternion directionQuaternion = new Quaternion();
                directionQuaternion.lookAt(direction, Vector3f.UNIT_Y);
                
                Quaternion cloneQuat = cam.getRotation().clone();
                float amount = 100;
                if (vehicle.getLinearVelocity().length() < 2) {
                    amount = 0f;
                } else if ((vehicle.getLinearVelocity().length() - 2) != 0) {
                    amount = tpf / (1 / (vehicle.getLinearVelocity().length() - 2));
                }
//                System.out.println(amount);
                cloneQuat.slerp(directionQuaternion, Utils.clamp(amount, 0, 1));
                // we rotate offset to our direction
                Vector3f offset = cloneQuat.mult(camBehindOffset);
                // set camera on the position of the car and apply computed offset
                cam.setLocation(spatial.getLocalTranslation().subtract(offset));
                // lookAt the same direction like a car is headed
                cam.setRotation(cloneQuat);
            } else if(cameraMode == CameraMode.FRONT) {
                Quaternion vehicleRotation = vehicle.getPhysicsRotation();//.multLocal(new Quaternion().fromAngles(0, (float) Math.PI, 0));
                // we rotate offset to our direction
                Vector3f offset = vehicleRotation.mult(camFrontOffset);
                Quaternion oldRot = cam.getRotation().clone();
                oldRot.slerp(vehicleRotation, Utils.clamp(tpf * 50, 0, 1));
                // set camera on the position of the car and apply computed offset
                cam.setLocation(spatial.getLocalTranslation().subtract(offset));
                cam.setRotation(oldRot);
            } else if(cameraMode == CameraMode.INSIDE) {
                Quaternion vehicleRotation = vehicle.getPhysicsRotation();//.multLocal(new Quaternion().fromAngles(0, (float) Math.PI, 0));
                // we rotate offset to our direction
                Vector3f offset = vehicleRotation.mult(camInsideOffset);
                Quaternion oldRot = cam.getRotation().clone();
                oldRot.slerp(vehicleRotation, Utils.clamp(tpf * 50, 0, 1));                
                // set camera on the position of the car and apply computed offset
                cam.setLocation(spatial.getLocalTranslation().subtract(offset));
                cam.setRotation(oldRot);
            } 
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related
    }

    public void setBehindCameraOffset(Vector3f camOffset) {
        this.camBehindOffset = camOffset;
    }
    
    public void setInsideCameraOffset(Vector3f camOffset) {
        this.camInsideOffset = camOffset;
    }
    
    public void setFrontCameraOffset(Vector3f camOffset) {
        this.camFrontOffset = camOffset;
    }

    public void setCameraMode(CameraMode cameraMode) {
        this.cameraMode = cameraMode;
    }

    public CameraMode getCameraMode() {
        return cameraMode;
    }

    public static enum CameraMode {

        BEHIND, INSIDE, FRONT;

        public static CameraMode getNext(CameraMode current) {
            if (current == BEHIND) {
                return INSIDE;
            } else if (current == INSIDE) {
                return FRONT;
            } else if (current == FRONT) {
                return BEHIND;
            }
            return BEHIND;
        }
    }
}
