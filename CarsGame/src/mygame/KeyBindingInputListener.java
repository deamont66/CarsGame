/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import java.util.ArrayList;
import java.util.List;
import mygame.guicontrollers.AbstractGuiController;

/**
 *
 * @author JiriSimecek
 */
public class KeyBindingInputListener implements RawInputListener {

    private AbstractGuiController guiControler;
    private String eventId;

    public KeyBindingInputListener(String eventId, AbstractGuiController keyBindingHandler) {
        this.guiControler = keyBindingHandler;
        this.eventId = eventId;
    }

    public void onKeyEvent(KeyInputEvent evt) {
        try {
            guiControler.keyBindCallBack(evt, eventId);
        } catch (Exception ex) {
        }
    }

    public String getEventId() {
        return eventId;
    }
    
    public void setGuiController(AbstractGuiController guiControler) {
        this.guiControler = guiControler;
    }
    
    public void beginInput() {
    }

    public void endInput() {
    }

    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    public void onMouseMotionEvent(MouseMotionEvent evt) {
    }

    public void onMouseButtonEvent(MouseButtonEvent evt) {
    }

    public void onTouchEvent(TouchEvent evt) {
    }
}
