package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.io.IOException;
import mygame.gamestates.MenuGameState;

public class Main extends SimpleApplication {

    private final String version = "0.2";
    private Nifty nifty;
    private int fps, frameCounter = 0;
    private float secondCounter = 0.0f;

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = Settings.getSettings().getAppSettings();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setDisplayFps(false);
        setDisplayStatView(false);
        getFlyByCamera().setEnabled(false);

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

        nifty = niftyDisplay.getNifty();
        
        guiViewPort.addProcessor(niftyDisplay);

        MenuGameState state = new MenuGameState();
        stateManager.attach(state);
    }

    @Override
    public void simpleUpdate(float tpf) {
        secondCounter += getTimer().getTimePerFrame();
        frameCounter++;
        if (secondCounter >= 1.0f) {
            fps = (int) (frameCounter / secondCounter);
            secondCounter = 0.0f;
            frameCounter = 0;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            Settings.getSettings().save();
        } catch (IOException ex) {
        }
    }

    @Override
    public void stop(boolean waitFor) {
        super.stop(waitFor);
        System.exit(0);
    }

    public Nifty getNifty() {
        return nifty;
    }

    public String getVersion() {
        return version;
    }

    public int getFps() {
        return fps;
    }
}