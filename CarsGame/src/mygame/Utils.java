/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Quaternion;
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
    
    public static float clamp(float value, float min, float max) {
        if(value > max) {
            return max;
        } else if(value < min) {
            return min;
        }
        return value;
    }
    
    public static Quaternion nlerp(Quaternion source, Quaternion dest, float lerpFactor, boolean shortest) {
                Quaternion correctedDest = dest;

                if (shortest && source.dot(dest) < 0) {
                        correctedDest = new Quaternion(-dest.getX(), -dest.getY(), -dest.getZ(), -dest.getW());
                }

                correctedDest.subtractLocal(source);
                correctedDest.multLocal(lerpFactor);
                correctedDest.add(source);
                correctedDest.normalizeLocal();
                return correctedDest;
        }

        public static Quaternion slerp(Quaternion source, Quaternion dest, float lerpFactor, boolean shortest) {
                final float EPSILON = 1e3f;

                float cos = source.dot(dest);
                Quaternion correctedDest = dest;

                if (shortest && cos < 0) {
                        cos = -cos;
                        correctedDest = new Quaternion(-dest.getX(), -dest.getY(), -dest.getZ(), -dest.getW());
                }

                if (Math.abs(cos) >= 1 - EPSILON) {
                        return Utils.nlerp(source, correctedDest, lerpFactor, false);
                }

                float sin = (float) Math.sqrt(1.0f - cos * cos);
                float angle = (float) Math.atan2(sin, cos);
                float invSin = 1.0f / sin;

                float srcFactor = (float) Math.sin((1.0f - lerpFactor) * angle) * invSin;
                float destFactor = (float) Math.sin((lerpFactor) * angle) * invSin;

                Quaternion ret = new Quaternion(source);
                ret.multLocal(srcFactor);
                correctedDest.multLocal(destFactor);
                ret.addLocal(correctedDest);
                return ret;
        }
}
