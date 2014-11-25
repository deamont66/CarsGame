/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.List;
import java.util.Map;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public class SoundCarEmitterControl extends AbstractControl {

    private final List<AudioNode> audioNodes;
    private float engineRPM;

    /**
     * List of audio nodes (have to be positional), index indentificate base rpm
     * based on table:
     * <pre>
     * 0 ....... idle
     * 1 ....... 1300
     * 2 ....... 1900
     *
     * n ....... 1200+(n-1)*600
     * </pre> optimal rpm for each audioNode.
     *
     * @param sounds
     */
    public SoundCarEmitterControl(List<AudioNode> sounds) {
        if (sounds == null || sounds.isEmpty()) {
            throw new IllegalArgumentException("List of sounds cannot be empty.");
        }
        audioNodes = sounds;
        setEngineRPM(0);
    }

    public void setEngineRPM(float engineRPM) {
        this.engineRPM = engineRPM;
        System.out.println(engineRPM);
        // set the playback speed and volume for each sample

        for (int index = 0; index < audioNodes.size(); index++) {
            AudioNode audioNode = audioNodes.get(index);

            float playbackRate = playbackSpeedForSampleAtIndex(index, engineRPM);
            audioNode.setPitch(playbackRate);

            float sampleVolume = volumeForSampleAtIndex(index, engineRPM);
            audioNode.setVolume(sampleVolume);
            
            if(audioNode.getStatus() != AudioSource.Status.Playing) {
//                audioNode.play();
            }
        }
    }

    protected float playbackSpeedForSampleAtIndex(int index, float rpm) {
        float sampleNativeRPM = ((float) index + 1.0f) * 600.0f;

        return (float) Utils.clamp(rpm / sampleNativeRPM, .5f, 1f);
    }

    protected float volumeForSampleAtIndex(int index, float rpm) {
        float sampleNativeRPM = ((float) index + 1.0f) * 600.0f;

        // special case 1: the first sample should be at full volume constantly until rpm == sampleNativeRPM
        if ((index == 0) && (rpm <= sampleNativeRPM)) {
            return 1.0f;
        }

        // special case 2: the last sample should play at full volume from sampleNativeRPM upwards
        if ((index == (audioNodes.size() - 1)) && (rpm >= sampleNativeRPM)) {
            return 1.0f;
        }

        // volume should increase linearly from 0 at 0rpm to 1 at sample native rpm
        float r = rpm / 1000.0f;
        float i = (float) index;
        float volume = r / (i + 1.0f);

        // volume should decrease logarithmically (base10) for RPM values > sample native RPM upwards
        if (volume > 1.3) {
            volume = 1.3f - (float) Math.log10(r - i);
        }

        volume = Utils.clamp(volume, 0.0f, 1.0f);
        return volume;
    }

    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
