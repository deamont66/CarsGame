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
import mygame.KeyBindingInputHandler;
import mygame.Settings;
import mygame.appstates.MenuGameState;
import mygame.appstates.TestGameState;

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
            updateResolutionText(resolution[0] + "x" + resolution[1]);
            updateVSyncText(settings.isVSyncEnabled());
            updateFullscreenText(settings.isFullscreenEnabled());
        } else if (app.getNifty().getCurrentScreen().getScreenId().equals("renderingSettings")) {
            updateSamplesText(settings.getNumberOfSamples());
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
        app.getNifty().gotoScreen("game");
        app.getStateManager().attach(new TestGameState());
    }

    public void goToScreen(String screenId) {
        app.getNifty().gotoScreen(screenId);
    }

    public void saveAndApply() {
        try {
            Settings.getSettings().save();
        } catch (IOException ex) {
        }
        AppSettings appSettings = Settings.getSettings().getAppSettings();
        app.setSettings(appSettings);
        app.restart();
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
                updateResolutionText(modes[i].getWidth() + "x" + modes[i].getHeight());
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

    public void changeFullscreen() {
        Settings.getSettings().setFullscreenEnabled(!Settings.getSettings().isFullscreenEnabled());
        updateFullscreenText(Settings.getSettings().isFullscreenEnabled());
    }

    public void changeVSync() {
        Settings.getSettings().setVSyncEnabled(!Settings.getSettings().isVSyncEnabled());
        updateVSyncText(Settings.getSettings().isVSyncEnabled());
    }

    private void updateKeyBindingText(String key, Settings settings) {
        Button text = app.getNifty().getCurrentScreen().findNiftyControl(key, Button.class);
        if (text != null) {
            text.setText(String.valueOf(Settings.getKeyName(settings.getKeyBinding(key))));
        }
    }

    private void updateResolutionText(String resText) {
        Element text = app.getNifty().getCurrentScreen().findElementByName("resolution");
        if (text != null) {
            text.getRenderer(TextRenderer.class).setText(resText);
        }
    }

    private void updateSamplesText(int samples) {
        Element text = app.getNifty().getCurrentScreen().findElementByName("samples");
        if (text != null) {
            text.getRenderer(TextRenderer.class).setText(((samples == 1) ? ("off") : String.valueOf(samples) + "x"));
        }
    }

    private void updateFullscreenText(boolean fullscreen) {
        Element text = app.getNifty().getCurrentScreen().findElementByName("fullscreen");
        if (text != null) {
            text.getRenderer(TextRenderer.class).setText(String.valueOf(fullscreen));
        }
    }

    private void updateVSyncText(boolean vsync) {
        Element text = app.getNifty().getCurrentScreen().findElementByName("vsync");
        if (text != null) {
            text.getRenderer(TextRenderer.class).setText(String.valueOf(vsync));
        }
    }

    public void stop() {
        MenuGameState menu = app.getStateManager().getState(MenuGameState.class);
        if (menu != null) {
            app.getStateManager().detach(menu);
        }
        app.stop();
    }
    private KeyBindingInputHandler sih;

    public void changeKeyBinding(String eventId) {
        Screen screen = app.getNifty().getCurrentScreen();
        if (sih != null) {
            Button button = screen.findNiftyControl(sih.getEventId(), Button.class);
            button.setText("");
            app.getInputManager().removeRawInputListener(sih);
        }
        Button button = screen.findNiftyControl(eventId, Button.class);
        button.setText("<press any key>");
        sih = new KeyBindingInputHandler(this, eventId);
        app.getInputManager().addRawInputListener(sih);
    }

    public void keyBindCallBack(KeyInputEvent evt, String eventId) {
        /* Callback, triggered from settingsKeyHandler */
        Screen screen = app.getNifty().getCurrentScreen();
        Button button = screen.findNiftyControl(eventId, Button.class);
        button.setText("" + Settings.getKeyName(evt.getKeyCode()));

        /* Performe mappings from Nifty GUI to KeyBindings */
        Settings.getSettings().setKeyBinding(eventId, evt.getKeyCode());

        app.getInputManager().removeRawInputListener(sih);
        sih = null;
    }
}
