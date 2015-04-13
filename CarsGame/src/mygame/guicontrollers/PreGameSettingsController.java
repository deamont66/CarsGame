/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.guicontrollers;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.tools.Color;
import mygame.gamestates.MenuGameState;
import mygame.gamestates.PreGameSettingsGameState;
import mygame.gamestates.TestGameState;

/**
 *
 * @author JiriSimecek
 */
public class PreGameSettingsController extends AbstractGuiController {

    String[] colorNames = new String[]{"Black", "White", "Red", "Lime", "Blue", "Yellow", "Cyan", "Magenta", "Silver", "Gray", "Maroon", "Olive", "Green", "Purple", "Teal", "Navy"};
    Color[] colors = new Color[]{new Color(0, 0, 0, 1), new Color(1, 1, 1, 1), new Color(1, 0, 0, 1), new Color(0, 1, 0, 1), new Color(0, 0, 1, 1), new Color(1, 1, 0, 1), new Color(0, 1, 1, 1), new Color(1, 0, 1, 1), new Color(0.75f, 0.75f, 0.75f, 1), new Color(0.5f, 0.5f, 0.5f, 1), new Color(0.5f, 0, 0, 1), new Color(0.5f, 0.5f, 0, 1), new Color(0, 0.5f, 0, 1), new Color(0.5f, 0, 0.5f, 1), new Color(0, 0.5f, 0.5f, 1), new Color(0, 0, 0.5f, 1)};
    Color lastColor = new Color(1, 0, 0, 1);

    public PreGameSettingsController(Application app) {
        super(app);
    }

    public void colorChange() {
        boolean next = false;
        for (int i = 0; i < colors.length + 1; i++) {
            if (i == colors.length) {
                i = 0;
            }
            if (colors[i].getRed() == lastColor.getRed() && colors[i].getBlue() == lastColor.getBlue() && colors[i].getGreen() == lastColor.getGreen()) {
                next = true;
            } else if (next) {
                lastColor = colors[i];
                updateElementText("colorText", colorNames[i]);
                Element element = app.getNifty().getCurrentScreen().findElementByName("colorPanel");
                element.getRenderer(PanelRenderer.class).setBackgroundColor(colors[i]);
                PreGameSettingsGameState state = this.app.getStateManager().getState(PreGameSettingsGameState.class);
                if (state != null) {
                    state.respawnCarWithColor(lastColor);
                }
                break;
            }
        }
    }

    private void updateElementText(String elementName, Object newText) {
        Element text = app.getNifty().getCurrentScreen().findElementByName(elementName);
        if (text != null) {
            text.getRenderer(TextRenderer.class).setText(String.valueOf(newText));
        }
    }
    
    public void backToMenu() {
        PreGameSettingsGameState menu = app.getStateManager().getState(PreGameSettingsGameState.class);
        if (menu != null) {
            app.getStateManager().detach(menu);
        }
        app.getStateManager().attach(new MenuGameState());
    }
    
    public void startGame() {
        PreGameSettingsGameState menu = app.getStateManager().getState(PreGameSettingsGameState.class);
        if (menu != null) {
            app.getStateManager().detach(menu);
        }
        TestGameState gameState = new TestGameState();
        ColorRGBA jmeColor = new ColorRGBA(lastColor.getRed(), lastColor.getGreen(),lastColor.getBlue(), lastColor.getAlpha());
        gameState.setVehicleColor(jmeColor);
        app.getStateManager().attach(gameState);
    }
}
