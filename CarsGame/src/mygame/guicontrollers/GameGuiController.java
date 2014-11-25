/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.guicontrollers;

import com.jme3.app.Application;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

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

    public String getFps() {
        return Integer.toString(app.getFps());
    }

    @Override
    public void update(float tpf) {
        if(!app.getNifty().getCurrentScreen().getScreenId().equals("game")) {
            return;
        }
        Element text = app.getNifty().getCurrentScreen().findElementByName("fpsText");
        text.getRenderer(TextRenderer.class).setText(getFps() + " FPS");
    }
}
