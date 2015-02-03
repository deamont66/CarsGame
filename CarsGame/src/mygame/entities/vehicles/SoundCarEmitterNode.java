/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.scene.Node;

/**
 *
 * @author JiriSimecek
 */
public class SoundCarEmitterNode extends Node {

    private final String IDLE = "Sounds/vehicle/SportCarEngineIdleMono.wav";
    
    private final String LOW  = "Sounds/vehicle/SportCarEngineLowMono.wav";
    private final String MID  = "Sounds/vehicle/SportCarEngineMidMono.wav";
    private final String FAST = "Sounds/vehicle/SportCarEngineFastMono.wav";
    
    private final String REV  = "Sounds/vehicle/SportCarEngineRevMono.wav";
    private final String REV2 = "Sounds/vehicle/SportCarEngineRev2Mono.wav";
    
    private final AssetManager assetManager;
    private final SoundCarEmitterControl control;
    
    public SoundCarEmitterNode(AssetManager assetManager, VehicleControl vehicle) {
        super("carEmitterNode");
        
        this.assetManager = assetManager;
        
        control = new SoundCarEmitterControl(vehicle);
        control.setAudioNode(SoundCarEmitterControl.AudioType.IDLE, createAudio(IDLE));
        control.setAudioNode(SoundCarEmitterControl.AudioType.LOW, createAudio(LOW));
        control.setAudioNode(SoundCarEmitterControl.AudioType.MID, createAudio(MID));
        control.setAudioNode(SoundCarEmitterControl.AudioType.FAST, createAudio(FAST));
        control.setAudioNode(SoundCarEmitterControl.AudioType.REV, createAudio(REV));
        control.setAudioNode(SoundCarEmitterControl.AudioType.REV2, createAudio(REV2));
        
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
