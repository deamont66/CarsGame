/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JiriSimecek
 */
public class SoundCarEmitterNode extends Node {

    private final List<String> sounds = new ArrayList<String>();
    private final SoundCarEmitterControl emitter;
    
    public SoundCarEmitterNode(SoundCarEmitterControl emitter) {
        addControl(emitter);
        this.emitter = emitter;
    }
    
    public SoundCarEmitterNode(AssetManager assetManager) {
        super("carEmitterNode");
//        sounds.add("Sounds/v8_2/1052_P.wav");   // 0
        sounds.add("Sounds/v8_2/1205.wav");   // 1300
        sounds.add("Sounds/v8_2/1808.wav");   // 1900
        sounds.add("Sounds/v8_2/2536.wav");   // 2500
        sounds.add("Sounds/v8_2/3107.wav");   // 3100
        sounds.add("Sounds/v8_2/3806.wav");   // 3700
        sounds.add("Sounds/v8_2/4358.wav");   // 4300
        sounds.add("Sounds/v8_2/4989.wav");   // 4900
        sounds.add("Sounds/v8_2/5712.wav");   // 5500
        sounds.add("Sounds/v8_2/6112.wav");   // 6100
        sounds.add("Sounds/v8_2/6997.wav");   // 6700
        sounds.add("Sounds/v8_2/7487.wav");   // 7300
        
        List<AudioNode> audioNodes = new ArrayList<AudioNode>();
        for (String audioFile : sounds) {
            AudioNode audioNode = new AudioNode(assetManager, audioFile);
            audioNode.setVolume(0f);
            audioNode.setLooping(true);
            this.attachChild(audioNode);
            audioNodes.add(audioNode);            
        }
        this.emitter = new SoundCarEmitterControl(audioNodes);
        addControl(emitter);
    }
    
    
    public void setEngineRPM(float rpm) {
        emitter.setEngineRPM(rpm);
    }
}
