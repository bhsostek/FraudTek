/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camera;

import base.engine.Game;
import base.engine.Engine;

import javax.script.ScriptEngine;

import org.joml.Vector3f;

/**
 *
 * @author Bailey
 */
public class CameraManager extends Engine{

    private Camera camera;

    private boolean isTransitioning = false;
    private Vector3f initial_pos;
    private Vector3f initial_rot;
    private Camera target = null;
    private Camera lastCam = null;
    private int targetTicks = 0;
    private int currentTicks = 0;
    
    public CameraManager() {
        super("CameraManager");
        this.camera = new DynamicCamera();
    }

    @Override
    public void init() {

    }

    @Override
    public void tick() {
        if(camera!=null){
            if(isTransitioning){
                if(currentTicks < targetTicks){
                    Vector3f pos_dif = new Vector3f(target.getPosition()).sub(initial_pos).mul((float)currentTicks / (float)targetTicks);
                    Vector3f rot_dif = new Vector3f(target.getRotation()).sub(initial_rot).mul((float)currentTicks / (float)targetTicks);
                    camera.setPosition(new Vector3f(initial_pos).add(pos_dif));
                    camera.setRotation(new Vector3f(initial_rot).add(rot_dif));
                    currentTicks++;
                }else{
                    //Transition has ended, so turn of transition flag
                    this.isTransitioning = false;
                }
            }
            camera.tick();
        }
    }

    public void lastCam(int time){
        transition(lastCam, time);
        lastCam.setPosition(Game.player.getPosition());
    }

    @Override
    public void registerForScripting(ScriptEngine engine) {
        engine.put(this.getName(), this);
    }

    @Override
    public void onShutdown() {

    }

    public boolean isTransitioning(){
        return this.isTransitioning;
    }

    public void transition(Camera target, int frames){
        if(isTransitioning){
            return;
        }
        this.target = target;
        this.initial_pos = new Vector3f(camera.getPosition());
        this.initial_rot = new Vector3f(camera.getRotation());
        this.targetTicks = frames;
        this.currentTicks = 0;
        this.lastCam = this.camera;
        this.camera = new DynamicCamera(initial_pos, initial_rot);
        isTransitioning = true;
    }
    
    public void setCamera(Camera camera){
        this.camera = camera;
    }
    
    public Camera getCam(){
        return this.camera;
    }
    
}
