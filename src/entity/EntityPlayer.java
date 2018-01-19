/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import base.engine.Game;
import entity.component.*;
import graphics.Renderer;
import models.ModelLoader;
import models.RawModel;
import org.joml.Vector3f;
import shaders.StaticShader;

/**
 *
 * @author Bailey
 */
public class EntityPlayer extends Entity{

    private RawModel model;

    public EntityPlayer(Vector3f position) {
        super(EnumEntityType.PLAYER, "front", position, 0, 0, 0, 1);
        super.addComponent(new ComponentGravity(this));
        super.addComponent(new ComponentController(this, 0));
        ComponentMesh mesh = new ComponentMesh(this, ModelLoader.generateCube(0.66f, 1, 0.66f));
        super.addComponent(new ComponentCollision(this, mesh));
        super.addComponent(new ComponentRender(this, null));
        super.addComponent(new ComponentFPSController(this));
        super.addComponent(new ComponentLight(this, new Vector3f(0, 0, 1)));

        model = Game.modelManager.getModel(ModelLoader.generateQuad(0.66f, 1));
    }

    @Override
    public void render(Renderer r, StaticShader shader) {
//   r.render(Game.modelManager.getModel(ModelLoader.generateCube(0.66f, 1, 0.66f)), Game.materialManager.getMaterial("front"), super.getPosition(), new Vector3f(super.getRotX(), super.getRotY(), super.getRotZ()), super.getScale(), shader);
    }
    
}
