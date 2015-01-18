/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gamestates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import mygame.guicontrollers.MenuGuiController;

/**
 *
 * @author JiriSimecek
 */
public class MenuGameState extends AbstractGameState  {

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app.getNifty().fromXml("Interface/mainMenu.xml", "start", new MenuGuiController(this.app));
    }

    
}
