/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles;

import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.EnumMap;
import java.util.Map;
import mygame.Settings;
import mygame.Utils;

/**
 *
 * @author JiriSimecek
 */
public class SoundCarEmitterControl extends AbstractControl {

    private final int MAX_SPEED = 150;
    private final EnumMap<AudioType, AudioNode> sounds = new EnumMap<AudioType, AudioNode>(AudioType.class);
    private final VehicleControl vehicle;
    private Settings settings = Settings.getSettings();

    public SoundCarEmitterControl(VehicleControl vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        float speed = Math.abs(vehicle.getCurrentVehicleSpeedKmHour());
        float low = MAX_SPEED / 3;
        float mid = MAX_SPEED / 3 * 2;

        float rev2Pitch, revPitch, idlePitch, lowPitch, midPitch, fastPitch;
        rev2Pitch = revPitch = idlePitch = lowPitch = midPitch = fastPitch = 1f;

        float rev2Volume, revVolume, idleVolume, lowVolume, midVolume, fastVolume;
        rev2Volume = revVolume = idleVolume = lowVolume = midVolume = fastVolume = 0f;

        /*if (speed < -10) {
            float tempSpeed = (speed + 10) * -1;
            rev2Pitch = tempSpeed / 10 / 2 + 0.5f;
            rev2Volume = 1f;
            revPitch = 1f;
            revVolume = (tempSpeed < 5) ? Math.abs(tempSpeed / (5) - 1) : 0f;
//            revVolume = Math.abs(tempSpeed / (10) - 1);

        } else if (speed < -2) {
            float tempSpeed = (speed) * -1;
            rev2Volume = (tempSpeed > 5) ? (tempSpeed  - (5)) / (5) : 0f;
//            rev2Volume = (tempSpeed  - (10)) / (5);
            rev2Pitch = 0.5f;
            revPitch = tempSpeed / 10 / 2 + 0.5f;
            revVolume = 1f;
        } else */if (speed < 10) {
            idleVolume = 1f;
        } else if (speed < low) {
            lowPitch = speed / low / 2 + 0.5f;
            lowVolume = 1f;
            midVolume = (speed > low /2) ? (speed  - (low / 2)) / (low /2) : 0f;
            midPitch = 0.5f;
        } else if (speed < mid) {
            float tempSpeed = speed - low;
            lowPitch = 1f;
            lowVolume = (tempSpeed < low /2) ? Math.abs(tempSpeed / (low /2) - 1) : 0f;
            midPitch = tempSpeed / low / 2 + 0.5f;
            midVolume = 1f;
            fastVolume = (tempSpeed > low /2) ? (tempSpeed  - (low / 2)) / (low /2) : 0f;
            fastPitch = 0.5f;
        } else {
            float tempSpeed = speed - mid;
            midPitch = 1f;
            midVolume = (tempSpeed < low /2) ? Math.abs(tempSpeed / (low /2) - 1) : 0f;
            fastPitch = tempSpeed / low / 2 + 0.5f;
            fastVolume = 1f;
        }
        float globalVolume = settings.getVolume();
        play(AudioType.REV2, rev2Volume * globalVolume, rev2Pitch);
        play(AudioType.REV, revVolume * globalVolume, revPitch);
        play(AudioType.IDLE, idleVolume * globalVolume, idlePitch);
        play(AudioType.LOW, lowVolume * globalVolume, lowPitch);
        play(AudioType.MID, midVolume * globalVolume, midPitch);
        play(AudioType.FAST, fastVolume * globalVolume, fastPitch);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void setAudioNode(AudioType type, AudioNode sound) {
        sounds.put(type, sound);
    }

    private void play(AudioType sound, float volume, float pitch) {
        for (Map.Entry<AudioType, AudioNode> entry : sounds.entrySet()) {
            AudioType audioType = entry.getKey();
            AudioNode audioNode = entry.getValue();
            if (audioType == sound) {
                audioNode.setPitch(Utils.clamp(pitch, 0.5f, 2f));
                audioNode.setVolume(Utils.clamp(volume, 0f, 1f));
            }
        }
    }

    public void cleanup() {
        for (Map.Entry<AudioType, AudioNode> entry : sounds.entrySet()) {
            AudioNode audioNode = entry.getValue();
            audioNode.stop();
        }
        sounds.clear();
    }

    public static enum AudioType {

        IDLE, LOW, MID, FAST, REV, REV2
    }
}
