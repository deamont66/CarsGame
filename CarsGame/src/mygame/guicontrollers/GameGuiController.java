/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.guicontrollers;

import com.jme3.app.Application;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import mygame.appstates.TestGameState;

/**
 *
 * @author JiriSimecek
 */
public class GameGuiController extends AbstractGuiController {

    public GameGuiController(Application app) {
        super(app);
    }

    public String getVersion() {
        return app.getVersion();
    }

    public String getLoadingState() {
        return "Loading...";
    }
    
    public String getFps() {
        return Integer.toString(app.getFps());
    }

    @Override
    public void update(float tpf) {
        if(app.getNifty().getCurrentScreen().getScreenId().equals("game")) {
            Element text = app.getNifty().getCurrentScreen().findElementByName("fpsText");
            text.getRenderer(TextRenderer.class).setText(getFps() + " FPS");
            
            Element speed = app.getNifty().getCurrentScreen().findElementByName("speed");
            float carSpeed = app.getStateManager().getState(TestGameState.class).getCarSpeed();
            speed.getRenderer(TextRenderer.class).setText((int) carSpeed + "");
        }
        if(app.getNifty().getCurrentScreen().getScreenId().equals("loading")) {
            Element text = app.getNifty().getCurrentScreen().findElementByName("loading");
            text.getRenderer(TextRenderer.class).setText(getLoadingState());
        }
    }
}
