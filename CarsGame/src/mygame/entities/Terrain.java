/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.entities;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import mygame.Main;

/**
 *
 * @author JiriSimecek
 */
public class Terrain {

    private final TerrainQuad terrainQuad;
    
    public Terrain(AssetManager assetManager, String heightMap, String diffuseTexture, int size, float heightScale) {
        terrainQuad = init(assetManager, heightMap, diffuseTexture, size, heightScale);
    }
    
    private TerrainQuad init(AssetManager assetManager, String heightMap, String diffuseTexture, int size, float heightScale) {
        Material mat_terrain;
        /**
         * 1. Create terrain material and load four textures into it.
         */
        mat_terrain = new Material(assetManager,
                "Common/MatDefs/Terrain/TerrainLighting.j3md");
        mat_terrain.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey(diffuseTexture, true)));
//        mat_terrain = assetManager.loadMaterial("Materials/terrainTest.j3m");

        /**
         * 2. Create the height map
         */
        Texture heightMapImage = assetManager.loadTexture(heightMap);
        AbstractHeightMap heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), heightScale);
        heightmap.load();

        /**
         * 3. We have prepared material and heightmap. Now we create the actual
         * terrain: 3.1) Create a TerrainQuad and name it "my terrain". 3.2) A
         * good value for terrain tiles is 64x64 -- so we supply 64+1=65. 3.3)
         * We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1). 3.5) We supply the
         * prepared heightmap itself.
         */
        int patchSize = 65;
        TerrainQuad terrain = new TerrainQuad("my terrain", patchSize, size + 1, heightmap.getHeightMap());
        /**
         * 4. We give the terrain its material, position & scale it, and attach
         * it.
         */
        terrain.setMaterial(mat_terrain);
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        terrain.setLocalTranslation(0, -63f, 0);
        terrain.setLocalScale(1f, 1f, 1f);

        /**
         * 5. The LOD (level of detail) depends on were the camera is:
         */
        TerrainLodControl control = new TerrainLodControl(terrain, Main.APP.getCamera());
        terrain.addControl(control);

        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(terrain);
        RigidBodyControl rbc = new RigidBodyControl(terrainShape, 0);
        rbc.setUserObject("terrain");
        terrain.addControl(rbc);
        
        return terrain;
    }
    
    public void addTo(Node rootNode, BulletAppState physics) {
        rootNode.attachChild(terrainQuad);
        physics.getPhysicsSpace().add(terrainQuad);
    }

    public TerrainQuad getTerrainQuad() {
        return terrainQuad;
    }
    
    public float getWorldHeightAtPoint(Vector2f point) {
        return getTerrainQuad().getHeight(point) - 63f;
    } 
   
}
