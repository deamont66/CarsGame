/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.input.KeyInput;
import com.jme3.system.AppSettings;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 *
 * @author JiriSimecek
 */
public class Settings {

    private static Settings instance = null;

    private Settings() {
    }

    public static Settings getSettings() {
        if (instance == null) {
            instance = new Settings();
            try {
                instance.init();
            } catch (IOException ex) {
                System.err.println("Settings cannot be loaded from file. " + ex);
            }
        }
        return instance;
    }
    private Properties settings = new Properties();

    private void init() throws IOException {
        settings.load(new FileInputStream("settings.cfg"));
    }

    public void save() throws IOException {
        settings.store(new FileOutputStream("settings.cfg"), "");
    }

    public void setResolution(int width, int height) {
        settings.setProperty("resolutionWidth", String.valueOf(width));
        settings.setProperty("resolutionHeight", String.valueOf(height));
    }

    public int[] getResolution() {
        return new int[]{Integer.valueOf(settings.getProperty("resolutionWidth", "1280")), Integer.valueOf(settings.getProperty("resolutionHeight", "720"))};
    }

    public void setVSyncEnabled(boolean enabled) {
        settings.setProperty("vsync", String.valueOf(enabled));
    }

    public boolean isVSyncEnabled() {
        return Boolean.valueOf(settings.getProperty("vsync", "false"));
    }

    public void setFullscreenEnabled(boolean enabled) {
        settings.setProperty("fullscreen", String.valueOf(enabled));
    }

    public boolean isFullscreenEnabled() {
        return Boolean.valueOf(settings.getProperty("fullscreen", "false"));
    }

    public void setNumberOfSamples(int numberOfSamples) {
        settings.setProperty("samples", String.valueOf(numberOfSamples));
    }

    public int getNumberOfSamples() {
        return Integer.valueOf(settings.getProperty("samples", "1"));
    }

    public int getKeyBinding(String key) {
        String value = settings.getProperty("key_" + key);
        if (value == null) {
            value = "-1";
        }
        return Integer.valueOf(value);
    }

    public void setKeyBinding(String key, int keyValue) {
        settings.setProperty("key_" + key, String.valueOf(keyValue));
    }
    
    public float getVolume() {
        return Float.valueOf(settings.getProperty("volume", "1"));
    }
    
    public void setVolume(float volume) {
        settings.setProperty("volume", String.valueOf(volume));
    }

    public String getTitle() {
        return "Cars";
    }

    public AppSettings getAppSettings() {
        AppSettings s = new AppSettings(true);
        s.setResolution(getResolution()[0], getResolution()[1]);
        s.setVSync(isVSyncEnabled());
        s.setFullscreen(isFullscreenEnabled());
        s.setSamples(getNumberOfSamples());
        s.setTitle(getTitle());
        return s;
    }

    public boolean isFilterEnabled(String filter) {
        return Boolean.valueOf(settings.getProperty("filter_" + filter, "false"));
    }

    public void setFilterEnabled(String filter, boolean enabled) {
        settings.setProperty("filter_" + filter, String.valueOf(enabled));
    }

    public boolean isPostProcessingEnabled() {
        return Boolean.valueOf(settings.getProperty("PostProcessing", "false"));
    }

    public void setPostProcessingEnabled(boolean enabled) {
        settings.setProperty("PostProcessing", String.valueOf(enabled));
    }

    public ShadowType getShadowType() {
        return ShadowType.valueOf(settings.getProperty("shadows_type", "RENDERER"));
    }
    
    public void setShadowType(ShadowType type) {
        settings.setProperty("shadows_type", type.name());
    }
    
    public int getShadowMapSize() {
        return Integer.valueOf(settings.getProperty("shadows_res", "512"));
    }
    
    public void setShadowMapSize(int size) {
        settings.setProperty("shadows_res", String.valueOf(size));
    }

    public static String getKeyName(int keyCode) {
        Class keyClass = KeyInput.class;
        for (Field field : keyClass.getFields()) {
            try {
                if (keyCode == field.getInt(null)) {
                    return field.getName();
                }
            } catch (Exception ex) {
                // Shh
            }
        }
        return "";
    }

    public static enum ShadowType {
        FILTER, RENDERER, NONE;
    }
}
