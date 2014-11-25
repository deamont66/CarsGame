/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.guicontrollers;

import com.jme3.app.Application;
import de.lessvoid.nifty.elements.render.TextRenderer;
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

    public void quit() {
        MenuGameState menu = app.getStateManager().getState(MenuGameState.class);
        if (menu != null) {
            app.getStateManager().detach(menu);
        }
        app.getNifty().gotoScreen("game");
        app.getStateManager().attach(new TestGameState());
    }
}
