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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author JiriSimecek
 */
public abstract class CodeListener implements RawInputListener {

    final StringBuilder lastInput = new StringBuilder();
    final Map<String, String> codes = new HashMap<String, String>();
    
    public void addCodeMapping(String code) {
        addCodeMapping(code, code);
    }
    
    public void addCodeMapping(String name, String code) {
        String reverseCode = new StringBuffer(code).reverse().toString();
        codes.put(name, reverseCode);
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

    public void onKeyEvent(KeyInputEvent evt) {
        if (evt.isPressed() && !evt.isRepeating()) {
            lastInput.insert(0, evt.getKeyChar());
            lastInput.setLength(16);
            for (Map.Entry<String, String> entry : codes.entrySet()) {
                String name = entry.getKey();
                String code = entry.getValue();
                if(lastInput.toString().toLowerCase().startsWith(code.toLowerCase())) {
                    onCodeDown(name, new StringBuffer(code).reverse().toString());
                }
            }
            System.out.println(lastInput);
        }
    }

    public void onTouchEvent(TouchEvent evt) {
    }
    
    public abstract void onCodeDown(String name, String code);
}
