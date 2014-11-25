/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.guicontrollers;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import mygame.Main;

/**
 *
 * @author JiriSimecek
 */
public class AbstractGuiController extends AbstractAppState implements ScreenController {

    protected final Main app;

    public AbstractGuiController(Application app) {
        this.app = (Main) app;
        app.getStateManager().attach(this);
    }
    
    public void bind(Nifty nifty, Screen screen) {
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }
    
}
