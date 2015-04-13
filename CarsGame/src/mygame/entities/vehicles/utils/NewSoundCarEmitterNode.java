/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles.utils;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.scene.Node;

/**
 *
 * @author JiriSimecek
 */
public class NewSoundCarEmitterNode extends Node {
    
    private final String HighAccel  = "Sounds/newVehicle/AccelerationHigh.wav";
    private final String HighDecel  = "Sounds/newVehicle/DecelerationHigh.wav";
    private final String LowAccel = "Sounds/newVehicle/AccelerationLow.wav";
    private final String LowDecel  = "Sounds/newVehicle/DecelerationLow.wav";
    
    private final AssetManager assetManager;
    private final NewSoundCarEmitterControl control;
    
    public NewSoundCarEmitterNode(AssetManager assetManager, FrictionVehicleControl vehicle) {
        super("carEmitterNode");
        
        this.assetManager = assetManager;
        
        control = new NewSoundCarEmitterControl(vehicle);
        control.setAudioNode(NewSoundCarEmitterControl.AudioType.HighAccel, createAudio(HighAccel));
        control.setAudioNode(NewSoundCarEmitterControl.AudioType.HighDecel, createAudio(HighDecel));
        control.setAudioNode(NewSoundCarEmitterControl.AudioType.LowAccel, createAudio(LowAccel));
        control.setAudioNode(NewSoundCarEmitterControl.AudioType.LowDecel, createAudio(LowDecel));
        
        addControl(control);
    }
    
    private AudioNode createAudio(String audioFile) {
        AudioNode audio = new AudioNode(assetManager, audioFile);
        audio.setLooping(true);
        audio.setVolume(0);
        audio.play();
        audio.getAudioData();
        attachChild(audio);
        return audio;
    }
    
    public void cleanup() {
        removeControl(control);
        control.cleanup();
    }
}
