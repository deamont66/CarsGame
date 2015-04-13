/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public class GoKart extends AbstractVehicle {

    ColorRGBA color;

    public GoKart(AssetManager manager) {
        super(manager);
    }

    public GoKart(AssetManager manager, ColorRGBA color) {
        this(manager);
        this.color = color;

        Material kartColorMat = getAssetManager().loadMaterial("Materials/GoKart/coloredMetalRED.j3m");
        kartColorMat.setColor("Diffuse", color);
        kartColorMat.setColor("Ambient", color.mult(.2f));

        Utils.findGeom(model, "Front Faring1").setMaterial(kartColorMat);
        Utils.findGeom(model, "Front Faring2").setMaterial(kartColorMat);
        Utils.findGeom(model, "Front Faring.0011").setMaterial(kartColorMat);
        Utils.findGeom(model, "Front Faring.0012").setMaterial(kartColorMat);
        Utils.findGeom(model, "Front stearing3").setMaterial(kartColorMat);
        Utils.findGeom(model, "Front stearing4").setMaterial(kartColorMat);
        Utils.findGeom(model, "Front stearing7").setMaterial(kartColorMat);
        Utils.findGeom(model, "Front stearing8").setMaterial(kartColorMat);
        Utils.findGeom(model, "petrol container1").setMaterial(kartColorMat);
        Utils.findGeom(model, "Main Frame1").setMaterial(kartColorMat);
        Utils.findGeom(model, "Engine1").setMaterial(kartColorMat);
        Utils.findGeom(model, "Engine3").setMaterial(kartColorMat);
        Utils.findGeom(model, "Engine cap1").setMaterial(kartColorMat);
    }

    @Override
    protected void initVehicle() {
        setModel((Node) getAssetManager().loadModel("Models/Vehicles/kart 1.j3o"), "chassis", "Wheel front_L1", "Wheel back_L1", "Wheel front_R1", "Wheel back_R1");

        setCenterOfMassOffset(new Vector3f(0, 0.0f, .25f));
        setWheelOffset(new Vector3f(1.6f, .7f, 1.3f));
        setStiffness(120f);
        setSuspensionRestLength(0.05f);
        setFrictionSlip(5);
    }

    @Override
    protected void initVehicleControl() {
        super.initVehicleControl();
        model.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    public void setSteeringWheelAngle(float angle) {
        if (model != null) {
            Quaternion temp = new Quaternion().fromAngles(0, angle * 4 / 3, 0);
            Quaternion newRot = new Quaternion().fromAngles(-0.6f, 0, 0);
            newRot.multLocal(temp);
            
            Node wheel = Utils.findNode(model, "stearing wheel");            
            wheel.setLocalRotation(newRot);
        }
    }

    public ColorRGBA getColor() {
        return color;
    }
}