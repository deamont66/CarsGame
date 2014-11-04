/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author JiriSimecek
 */
public class Utils {

    public static Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }

    public static Node findNode(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            if (node.getName().startsWith(name)) {
                return node;
            } else {
                for (int i = 0; i < node.getQuantity(); i++) {
                    Spatial child = node.getChild(i);
                    Node result = findNode(child, name);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public static void centerAllGeometries(Node node) {
        for (int i = 0; i < node.getQuantity(); i++) {
            Spatial child = node.getChild(i);
            if (child instanceof Node) {
                centerAllGeometries((Node) child);
            } else {
                ((Geometry) child).center();
            }
        }
    }
}
