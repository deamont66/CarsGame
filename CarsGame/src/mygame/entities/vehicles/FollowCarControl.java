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

/**
 *
 * @author JiriSimecek
 */
public class FollowCarControl extends AbstractControl {
    
    private final Camera cam;
    private Vector3f camOffset = new Vector3f();

    public FollowCarControl(Camera cam) {
        this.cam = cam;
    }

    @Override
    protected void controlUpdate(float tpf) {
        VehicleControl vehicle = spatial.getControl(VehicleControl.class);
        if(vehicle != null) {
            // normalized direction of the car
            Vector3f direction = vehicle.getLinearVelocity().normalize();
            // quternion rotation from direction
            Quaternion directionQuaternion = new Quaternion();
            directionQuaternion.lookAt(direction, Vector3f.UNIT_Y);
            // we rotate offset to our direction
            Vector3f offset = directionQuaternion.mult(camOffset);
            // set camera on the position of the car and apply computed offset
            cam.setLocation(spatial.getLocalTranslation().subtract(offset));
            // lookAt the same direction like a car is headed
            cam.setRotation(directionQuaternion);
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related
    }

    public void setCameraOffset(Vector3f camOffset) {
        this.camOffset = camOffset;
    }
}
