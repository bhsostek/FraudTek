package graphics.gui;

import base.engine.Game;
import graphics.Loader;
import math.Maths;
import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.Shader;

import java.util.List;

/**
 * Created by Bailey on 9/5/2017.
 */
public class GuiRenderer {

    private final RawModel quad;
    private Shader shader;

    public GuiRenderer(Loader loader){
        float[] positions = new float[]{-1, 1, -1, -1, 1, 1, 1, -1};
        quad = loader.loadToVAO(positions);
        shader = new Shader("ui");
    }

    public void render(List<Gui> textures){
//        shader.start();
//        GL30.glBindVertexArray(quad.getVaoID());
//        GL20.glEnableVertexAttribArray(0);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
//        //rendering
//        for(Gui texture : textures){
//            Game.logManager.println(texture.getTextureID()+"");
//            GL13.glActiveTexture(GL13.GL_TEXTURE0);
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
//            Matrix4f matrix = Maths.createTransformationMatrix(texture.getPosition(), 0, texture.getScale());
//            shader.loadTransformation(matrix);
//            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
//        }
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glDisable(GL11.GL_BLEND);
//        GL20.glDisableVertexAttribArray(0);
//        GL30.glBindVertexArray(0);
//        shader.stop();
        shader.start();
        shader.loadData("viewMatrix", Maths.createViewMatrix(Game.cameraManager.getCam()));
        shader.loadData("cameraRot", Maths.getCameraRot(Game.cameraManager.getCam()));

        for(Gui texture : textures) {
            shader.bindVAOFromID(quad.getVaoID());
            shader.loadData("textureSampler", texture.getTextureID());
            shader.render(new Vector3f(texture.getPosition().x(),texture.getPosition().y(), 0), 0, 0, 0, 0, 1);
            shader.unBindVAO();
        }
        shader.stop();
    }

    public void cleanUp(){

    }

}
