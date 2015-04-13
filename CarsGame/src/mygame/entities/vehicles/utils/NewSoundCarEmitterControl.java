/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities.vehicles.utils;

import com.jme3.audio.AudioNode;
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
public class NewSoundCarEmitterControl extends AbstractControl {

    private float m_Topspeed = 250;
    private final EnumMap<AudioType, AudioNode> sounds = new EnumMap<AudioType, AudioNode>(AudioType.class);
    private final FrictionVehicleControl vehicle;
    private Settings settings = Settings.getSettings();
    public float pitchMultiplier = 1f;                                          // Used for altering the pitch of audio clips
    public float lowPitchMin = 1f;                                              // The lowest possible pitch for the low sounds
    public float lowPitchMax = 7f;                                              // The highest possible pitch for the low sounds
    public float highPitchMultiplier = 0.25f;                                   // Used for altering the pitch of high sounds
    private int m_GearNum;
    private float m_GearFactor;
    private float m_RevRangeBoundary = 1f;
    private static int NoOfGears = 5;
    private float Revs;

    public NewSoundCarEmitterControl(FrictionVehicleControl vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    protected void controlUpdate(float tpf) {
        GearChanging();
        CalculateRevs(tpf);
        // The pitch is interpolated between the min and max values, according to the car's revs.
        float pitch = ULerp(lowPitchMin, lowPitchMax, Revs);

        // clamp to minimum pitch (note, not clamped to max for high revs while burning out)
        pitch = Math.min(lowPitchMax, pitch);

        /*if (engineSoundStyle == EngineAudioOptions.Simple) {
         // for 1 channel engine sound, it's oh so simple:
         m_HighAccel.pitch = pitch * pitchMultiplier * highPitchMultiplier;
         m_HighAccel.dopplerLevel = useDoppler ? dopplerLevel : 0;
         m_HighAccel.volume = 1;
         } else {*/
        // for 4 channel engine sound, it's a little more complex:

        // adjust the pitches based on the multipliers
        float lowAccelPitch = pitch * pitchMultiplier;
        float lowDecelPitch = pitch * pitchMultiplier;
        float highAccelPitch = pitch * highPitchMultiplier * pitchMultiplier;
        float highDecelPitch = pitch * highPitchMultiplier * pitchMultiplier;

        // get values for fading the sounds based on the acceleration
        float accFade = Math.abs(vehicle.accelerationInput);
        float decFade = 1 - accFade;

        // get the high fade value based on the cars revs
        float highFade = inverseLerp(0.2f, 0.8f, Revs);
        float lowFade = 1 - highFade;

        // adjust the values to be more realistic
        highFade = 1 - ((1 - highFade) * (1 - highFade));
        lowFade = 1 - ((1 - lowFade) * (1 - lowFade));
        accFade = 1 - ((1 - accFade) * (1 - accFade));
        decFade = 1 - ((1 - decFade) * (1 - decFade));

        // adjust the source volumes based on the fade values
        float lowAccelVolume = lowFade * accFade;
        float lowDecelVolume = lowFade * decFade;
        float highAccelVolume = highFade * accFade;
        float highDecelVolume = highFade * decFade;

        float globalVolume = settings.getVolume();
        play(AudioType.LowAccel, lowAccelVolume * globalVolume, lowAccelPitch);
        play(AudioType.LowDecel, lowDecelVolume * globalVolume, lowDecelPitch);
        play(AudioType.HighAccel, highAccelVolume * globalVolume, highAccelPitch);
        play(AudioType.HighDecel, highDecelVolume * globalVolume, highDecelPitch);
        //}

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
                audioNode.setVolume(Utils.clamp(volume, 0f, 2f));
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

    private void CalculateGearFactor(float tfp) {
        float f = (1 / (float) NoOfGears);
        // gear factor is a normalised representation of the current speed within the current gear's range of speeds.
        // We smooth towards the 'target' gear factor, so that revs don't instantly snap up or down when changing gear.
        float targetGearFactor = inverseLerp(f * m_GearNum, f * (m_GearNum + 1), Math.abs(vehicle.getCurrentVehicleSpeedKmHour() / m_Topspeed));
        m_GearFactor = lerp(m_GearFactor, targetGearFactor, tfp * 5f);
    }

    private void GearChanging() {
        float f = Math.abs(vehicle.getCurrentVehicleSpeedKmHour() / m_Topspeed);
        float upgearlimit = (1 / (float) NoOfGears) * (m_GearNum + 1);
        float downgearlimit = (1 / (float) NoOfGears) * m_GearNum;

        if (m_GearNum > 0 && f < downgearlimit) {
            m_GearNum--;
        }

        if (f > upgearlimit && (m_GearNum < (NoOfGears - 1))) {
            m_GearNum++;
        }
    }

    private void CalculateRevs(float tfp) {
        // calculate engine revs (for display / sound)
        // (this is done in retrospect - revs are not used in force/power calculations)
        CalculateGearFactor(tfp);
        float gearNumFactor = m_GearNum / (float) NoOfGears;
        float revsRangeMin = ULerp(0f, m_RevRangeBoundary, CurveFactor(gearNumFactor));
        float revsRangeMax = ULerp(m_RevRangeBoundary, 1f, gearNumFactor);
        Revs = ULerp(revsRangeMin, revsRangeMax, m_GearFactor);
    }

    // simple function to add a curved bias towards 1 for a value in the 0-1 range
    private static float CurveFactor(float factor) {
        return 1 - (1 - factor) * (1 - factor);
    }

    private static float ULerp(float from, float to, float value) {
        return (1.0f - value) * from + value * to;
    }

    private static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    private static float inverseLerp(float from, float to, float value) {
        if (from < to) {
            if (value < from) {
                return 0f;
            }
            if (value > to) {
                return 1f;
            }
            value -= from;
            value /= to - from;
            return value;
        } else {
            if (from <= to) {
                return 0f;
            }
            if (value < to) {
                return 1f;
            }
            if (value > from) {
                return 0f;
            }
            return 1f - (value - to) / (from - to);
        }
    }

    public static enum AudioType {

        HighAccel, HighDecel, LowAccel, LowDecel,
    }
}
