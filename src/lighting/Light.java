/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lighting;

import org.joml.Vector3f;

/**
 *
 * @author Bailey
 */
public class Light{
    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation = new Vector3f(1,1,1);

    public Light(Vector3f position, Vector3f color) {
        this.position = position;
        this.color = color;
    }
    
    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        this.position = position;
        this.color = color;
        this.attenuation = attenuation;
    }
    
    public void setAttenuation(Vector3f attenuation){
        this.attenuation = attenuation;
    }

    public Vector3f getAttenuation(){
        return this.attenuation;
    }
    
    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
