/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.guicontrollers;

import com.jme3.app.Application;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import mygame.KeyBindingInputListener;
import mygame.Settings;
import mygame.gamestates.MenuGameState;
import mygame.gamestates.PreGameSettingsGameState;
import mygame.gamestates.TestGameState;

/**
 *
 * @author JiriSimecek
 */
public class MenuGuiController extends AbstractGuiController {

    public MenuGuiController(Application app) {
        super(app);
    }

    @Override
    public void onStartScreen() {
        Settings settings = Settings.getSettings();
        if (app.getNifty().getCurrentScreen().getScreenId().equals("videoSettings")) {
            int[] resolution = settings.getResolution();
            updateElementText("resolution", resolution[0] + "x" + resolution[1]);
            updateElementText("vsync", settings.isVSyncEnabled());
            updateElementText("fullscreen", settings.isFullscreenEnabled());
            updateElementText("volume", (settings.getVolume() * 100) + "%");
        } else if (app.getNifty().getCurrentScreen().getScreenId().equals("renderingSettings")) {
            updateSamplesText(settings.getNumberOfSamples());
            updateElementText("ShadowsType", settings.getShadowType());
            updateElementText("ShadowsResolution", settings.getShadowMapSize());
            updateElementText("PostProcess", settings.isPostProcessingEnabled());
            updateFilterText("FXAA", settings);
            updateFilterText("BLOOM", settings);
            updateFilterText("DOF", settings);
            updateFilterText("LSF", settings);
            updateFilterText("SSAO", settings);
            updateFilterText("FOG", settings);
        } else if (app.getNifty().getCurrentScreen().getScreenId().equals("keybindingSettings")) {
            updateKeyBindingText("key_accelerate", settings);
            updateKeyBindingText("key_left", settings);
            updateKeyBindingText("key_right", settings);
            updateKeyBindingText("key_brake", settings);
            updateKeyBindingText("key_handbrake", settings);
            updateKeyBindingText("key_camera", settings);
        }
    }

    public void play() {
        MenuGameState menu = app.getStateManager().getState(MenuGameState.class);
        if (menu != null) {
            app.getStateManager().detach(menu);
        }
        app.getStateManager().attach(new PreGameSettingsGameState());
    }

    public void stop() {
        MenuGameState menu = app.getStateManager().getState(MenuGameState.class);
        if (menu != null) {
            app.getStateManager().detach(menu);
        }
        app.stop();
    }

    public void goToScreen(String screenId) {
        app.getNifty().gotoScreen(screenId);
    }
    
    public void saveAndApply(String displayReset) {
        boolean reset = Boolean.valueOf(displayReset);
        try {
            Settings.getSettings().save();
        } catch (IOException ex) {
        }
        AppSettings appSettings = Settings.getSettings().getAppSettings();
        app.setSettings(appSettings);
        if (reset) {
            app.restart();
        }
    }

    public void changeResolution() {
        int[] resolution = Settings.getSettings().getResolution();
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();
        boolean next = false;
        for (int i = 0; i < modes.length + 1; i++) {
            if (i == modes.length) {
                i = 0;
            }
            if (modes[i].getWidth() == resolution[0] && modes[i].getHeight() == resolution[1]) {
                next = true;
            } else if (next && modes[i].getBitDepth() == 32) {
                Settings.getSettings().setResolution(modes[i].getWidth(), modes[i].getHeight());
                updateElementText("resolution", modes[i].getWidth() + "x" + modes[i].getHeight());
                break;
            }
        }
    }

    public void changeSamples() {
        int[] samples = new int[]{1, 2, 4, 8};
        boolean next = false;
        for (int i = 0; i < samples.length + 1; i++) {
            if (i == samples.length) {
                i = 0;
            }
            if (samples[i] == Settings.getSettings().getNumberOfSamples()) {
                next = true;
            } else if (next) {
                Settings.getSettings().setNumberOfSamples(samples[i]);
                updateSamplesText(samples[i]);
                break;
            }
        }
    }
    
    public void changeVolume() {
        float[] values = new float[]{0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
        String[] texts = new String[]{"0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"};
        
        boolean next = false;
        for (int i = 0; i < values.length + 1; i++) {
            if (i == values.length) {
                i = 0;
            }
            if (values[i] == Settings.getSettings().getVolume()) {
                next = true;
            } else if (next) {
                Settings.getSettings().setVolume(values[i]);
                updateElementText("volume", texts[i]);
                break;
            }
        }
    }

    public void changeFullscreen() {
        Settings.getSettings().setFullscreenEnabled(!Settings.getSettings().isFullscreenEnabled());
        updateElementText("fullscreen", Settings.getSettings().isFullscreenEnabled());
    }

    public void changeVSync() {
        Settings.getSettings().setVSyncEnabled(!Settings.getSettings().isVSyncEnabled());
        updateElementText("vsync", Settings.getSettings().isVSyncEnabled());
    }
    private KeyBindingInputListener sih;

    public void changeKeyBinding(String eventId) {
        Screen screen = app.getNifty().getCurrentScreen();
        if (sih != null) {
            Button button = screen.findNiftyControl(sih.getEventId(), Button.class);
            button.setText(Settings.getKeyName(Settings.getSettings().getKeyBinding(sih.getEventId())));
            app.getInputManager().removeRawInputListener(sih);
        }
        Button button = screen.findNiftyControl(eventId, Button.class);
        button.setText("<press any key>");
        sih = new KeyBindingInputListener(eventId, this);
        app.getInputManager().addRawInputListener(sih);
    }

    @Override
    public void keyBindCallBack(KeyInputEvent evt, String eventId) {

        Screen screen = app.getNifty().getCurrentScreen();
        String keyName = Settings.getKeyName(evt.getKeyCode());
        if (keyName == null || keyName.isEmpty()) {
            keyName = String.valueOf(evt.getKeyCode());
        }
        Button button = screen.findNiftyControl(eventId, Button.class);
        button.setText(keyName);

        Settings.getSettings().setKeyBinding(eventId, evt.getKeyCode());

        app.getInputManager().removeRawInputListener(sih);
        sih = null;
    }

    public void changeFilter(String filterName) {
        Settings settings = Settings.getSettings();
        settings.setFilterEnabled(filterName, !settings.isFilterEnabled(filterName));
        updateFilterText(filterName, settings);
    }

    public void changePostProcess() {
        Settings settings = Settings.getSettings();
        settings.setPostProcessingEnabled(!settings.isPostProcessingEnabled());
        updateElementText("PostProcess", settings.isPostProcessingEnabled());
    }

    public void changeShadowsResolution() {
        int[] samples = new int[]{512, 1024, 2048, 4096};
        boolean next = false;
        for (int i = 0; i < samples.length + 1; i++) {
            if (i == samples.length) {
                i = 0;
            }
            if (samples[i] == Settings.getSettings().getShadowMapSize()) {
                next = true;
            } else if (next) {
                Settings.getSettings().setShadowMapSize(samples[i]);
                updateElementText("ShadowsResolution", Settings.getSettings().getShadowMapSize());
                break;
            }
        }
    }

    public void changeShadowsType() {
        Settings.ShadowType[] samples = new Settings.ShadowType[]{Settings.ShadowType.NONE, Settings.ShadowType.FILTER, Settings.ShadowType.RENDERER};
        boolean next = false;
        for (int i = 0; i < samples.length + 1; i++) {
            if (i == samples.length) {
                i = 0;
            }
            if (samples[i] == Settings.getSettings().getShadowType()) {
                next = true;
            } else if (next) {
                Settings.getSettings().setShadowType(samples[i]);
                updateElementText("ShadowsType", Settings.getSettings().getShadowType());
                break;
            }
        }
    }

    private void updateKeyBindingText(String key, Settings settings) {
        Button text = app.getNifty().getCurrentScreen().findNiftyControl(key, Button.class);
        if (text != null) {
            text.setText(String.valueOf(Settings.getKeyName(settings.getKeyBinding(key))));
        }
    }

    private void updateFilterText(String filterName, Settings settings) {
        updateElementText(filterName, settings.isFilterEnabled(filterName));
    }

    private void updateSamplesText(int samples) {
        updateElementText("samples", ((samples == 1) ? ("off") : String.valueOf(samples) + "x"));
    }

    private void updateElementText(String elementName, Object newText) {
        Element text = app.getNifty().getCurrentScreen().findElementByName(elementName);
        if (text != null) {
            text.getRenderer(TextRenderer.class).setText(String.valueOf(newText));
        }
    }
}
