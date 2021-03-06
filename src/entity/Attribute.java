/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 *
 * @author Bailey
 * @param <var>
 */
public class Attribute <var>{
    private final String id;
    private String index = "";
    private Object dataType;

    private boolean isPrivate = false;

    public Attribute(String preface, var inData){
        dataType = inData;
        this.id = preface;
    }
    
    public var getData(){
        return (var)this.dataType;
    }
    
    public void setData(var i){
        if(((var)dataType) instanceof Float){
            this.dataType = Float.parseFloat(i+"");
        }else {
            this.dataType = i;
        }
    }
    
    public String getID(){
        return this.id;
    }
    
    public void setIndex(int index){
        this.index = index+"";
    }

    public int getIndex(){
        if(index.isEmpty()) {
            return 0;
        }else{
            return Integer.parseInt(index);
        }
    }

    public void setPrivate(boolean isPrivate){
        this.isPrivate = isPrivate;
    }

    public boolean isPrivate(){
        return this.isPrivate;
    }

    public boolean setDataFromJson(JsonObject data){
        Gson gson = new Gson();
        if(data.has(this.id+index)){
            if(((var)this.dataType) instanceof Attribute[]){
                for(Attribute a : ((Attribute[])this.dataType)){
                    a.setDataFromJson(data.getAsJsonObject(id+index));
                }
                return true;
            }
            this.dataType = (var)gson.fromJson(data.get(this.id+index), ((var)this.dataType).getClass());
            return true;
        }
        return false;
    }
}
