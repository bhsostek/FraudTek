/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.engine;

import base.controller.ControllerManager;
import base.util.*;
import editor.IntellisenseEngine;
import entity.*;
import ScriptingEngine.ScriptingEngine;
import graphics.*;
import graphics.gui.GuiRenderer;
import graphics.gui.Gui;
import math.Maths;
import networking.ServerManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import particles.ParticleEngine;
import steam.SteamManager;
import camera.CameraManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import lighting.LightingEngine;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import input.EnumMouseButton;
import input.Keyboard;
import input.Mouse;
import input.MousePicker;
import java.util.HashMap;
import java.util.LinkedList;

import models.ModelManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import textures.MaterialManager;

import static org.lwjgl.opengl.GL11.*;

public class Game {
    
    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;    
    private static String NAME = "Game";
    private static boolean FULLSCREEN = false;
    private static boolean VSYNC = false;

    private static final String version = "1.0";

    public static float MouseX = 0;
    public static float MouseY = 0;
    
    public static String Path = "";
    private static final Class refrence = Engine.class;
    
    // This prevents our window from crashing later on.
    private static GLFWKeyCallback keyCallback;
    private static GLFWMouseButtonCallback mouseButtonCallback;
    
    private static long window;
    public static Loader loader;
    private static Renderer renderer;

    public static CameraManager cameraManager;
    
    public static MousePicker mouse;
    
    private static Debouncer mouseClick = new Debouncer(false);
    
    public static ScriptingEngine scriptingEngine;
    
    //Engines
    private static DynamicCollection<Engine> engines = new DynamicCollection<Engine>() {
        @Override
        public void onAdd(Engine object) {

        }

        @Override
        public void onRemove(Engine object) {

        }
    };
    
    private static HashMap<String, IncludeFolder> srcFolders = new HashMap<String, IncludeFolder>();

    public static LogManager logManager;
    public static EntityManager entityManager;
    public static LightingEngine lightingEngine;
    private static SteamManager steamManager;
    public static SpriteBinder spriteBinder;
    public static TextureManager textureManager;
    public static ModelManager modelManager;
    public static ControllerManager controllerManager;
    public static VAOManager vaoManager;
    public static ServerManager serverManager;
    public static MaterialManager materialManager;
    public static ParticleEngine particleManager;
    //VR Headsets

    //tmp
    static GuiRenderer uiRenderer;
    public static LinkedList<Gui> guis = new LinkedList<Gui>();

    public static Entity player;

    public static void main(String[] args){
        init(args);
        run();
        logManager.println();
        logManager.println("Shutting down Engine.");
        logManager.println();
        shutdown();
        logManager.println();
        logManager.println("Cleaning up Engine.");
        logManager.println();
        cleanUp();
        logManager.println();
        logManager.println("Terminating Window.");
        logManager.println();
        logManager.tick();
        logManager.onShutdown();
        glfwTerminate();
    }
    
    private static void init(String[] args){
        //Very first thing initialized
        logManager = new LogManager();
        logManager.println();
        logManager.println("Engine Initialization");
        logManager.println();
        logManager.println("FraudTek version:"+version);
        engines.add(logManager);
        if(!glfwInit()){
            throw new IllegalStateException("Failed to initialize GLFW");
        }
        
        //set reference
        Game.Path = StringUtils.removeEXEandJAR(StringUtils.getRelativePath(refrence));
        
        //set natives path
        try{
            addDir((Game.Path+"/natives"));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Gson gson = new Gson();
        JsonObject launchOptions = gson.fromJson(StringUtils.unify(StringUtils.loadData(Game.Path+"/Scripting/launch.json")), JsonObject.class);
        if(launchOptions.has("name")){
            NAME = gson.fromJson(launchOptions.get("name"), String.class);
            logManager.println("Game Name:"+NAME);
        }
        //Take args in OR read from launch
        //WIDTH
        if(args.length < 1){
            if (launchOptions.has("width")) {
                WIDTH = gson.fromJson(launchOptions.get("width"), Integer.class);
                logManager.println("Target Width:" + WIDTH);
            }
        }else{
            WIDTH = Integer.parseInt(args[0]);
            logManager.println("ARGS Width:" + WIDTH);
        }
        //HEIGHT
        if(args.length < 2) {
            if (launchOptions.has("height")) {
                HEIGHT = gson.fromJson(launchOptions.get("height"), Integer.class);
                logManager.println("Target Height:" + HEIGHT);
            }
        }else{
            HEIGHT = Integer.parseInt(args[1]);
            logManager.println("ARGS Height:" + HEIGHT);
        }

        //FULLSCREEN
        if(args.length < 3) {
            if (launchOptions.has("fullscreen")) {
                FULLSCREEN = gson.fromJson(launchOptions.get("fullscreen"), Boolean.class);
                logManager.println("Window is Fullscreen:" + FULLSCREEN);
            }
        }else{
            FULLSCREEN = Boolean.parseBoolean(args[2]);
            logManager.println("ARGS Fullscreen:" + FULLSCREEN);
        }

        //VSYNC
        if(args.length < 4) {
            if (launchOptions.has("vsync")) {
                VSYNC = gson.fromJson(launchOptions.get("vsync"), Boolean.class);
                logManager.println("Window is using vsync:" + VSYNC);
            }
        }else{
            VSYNC = Boolean.parseBoolean(args[3]);
            logManager.println("ARGS vsync:" + VSYNC);
        }

        if(!FULLSCREEN){
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            window = glfwCreateWindow(WIDTH, HEIGHT, NAME, 0, 0);
        }else{
            //set to native resolutiuon
            WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
            HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
            window = glfwCreateWindow(WIDTH, HEIGHT, NAME, glfwGetPrimaryMonitor(), 0);
        }
        if(window == 0){
            logManager.println("Failed to create Window");
            throw new IllegalStateException("Failed to create Window");
        }
        
        //Center the winodw
        if(!FULLSCREEN){
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (videoMode.width() - WIDTH)/2, (videoMode.height() - HEIGHT)/2);
        }
  
        glfwSetWindowTitle(window, NAME);
        
        //Shows then selects the window
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        
        // Sets our keycallback to equal our newly created Input class()
        glfwSetKeyCallback(window, keyCallback = new Keyboard());
        
        GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback = new Mouse());

        //Initialize OpenGL
        GL.createCapabilities();

        //Enable or disable vsync
        glfwSwapInterval(VSYNC ? 1: 0);

        logManager.println();
        logManager.println("Looking for Source Folders...");
        logManager.println();
        //init folder
        JsonArray jsonArray = gson.fromJson(StringUtils.unify(StringUtils.loadData(Game.Path+"/Scripting/managedFolders.json")), JsonArray.class);
        for(JsonElement element : jsonArray){
            JsonObject object = element.getAsJsonObject();
            if(object.has("name")){
                String name = gson.fromJson(object.get("name"), String.class);
                IncludeFolder src = new IncludeFolder(name);
                src.generateFolder();
                srcFolders.put(name, src);
            }
        }
        logManager.println();
        logManager.println("Initializing all engines associated with the game.");
        logManager.println();
        loader = new Loader();
        textureManager = new TextureManager();
        engines.add(textureManager);
        renderer = new Renderer();
        lightingEngine = new LightingEngine();
        engines.add(lightingEngine);
        cameraManager = new CameraManager();
        engines.add(cameraManager);
        mouse = new MousePicker(Maths.getProjectionMatrix());
        entityManager = new EntityManager();
        engines.add(entityManager);
        steamManager = new SteamManager();
        engines.add(steamManager);
        spriteBinder = new SpriteBinder();
        engines.add(spriteBinder);
        modelManager = new ModelManager();
        engines.add(modelManager);
        controllerManager = new ControllerManager();
        engines.add(controllerManager);
        vaoManager = new VAOManager();
        engines.add(vaoManager);
        serverManager = new ServerManager();
        engines.add(serverManager);
        materialManager = new MaterialManager();
        engines.add(materialManager);
        particleManager = new ParticleEngine();
        engines.add(particleManager);
        engines.synch();

        logManager.println();
        logManager.println("Adding refrences to the scripting engine.");
        scriptingEngine = new ScriptingEngine(engines);
        uiRenderer = new GuiRenderer(loader);
        //Register for scripting
        scriptingEngine.addRefrence("guis", guis);
        scriptingEngine.addRefrence("MouseRay", mouse);
        scriptingEngine.addRefrence("Camera", cameraManager.getCam());
        scriptingEngine.addRefrence("ControllerManager", controllerManager);


        //Last addition
        scriptingEngine.addRefrence("ScriptingEngine", scriptingEngine);
        particleManager.postInit();

        scriptingEngine.addScript(gson.fromJson(launchOptions.get("mainScript"), String.class));


//        entityManager.addEntity(new EntityModel(ModelLoader.loadModel("dragon"), "white", new Vector3f(0, 1, 0), 0, 0, 0, 1f));
//        cameraManager.transition(new DynamicCamera(new Vector3f(-4, 5, -12), new Vector3f(90, 90, 0)), 300);

//        guis.add(new Gui(spriteBinder.loadSprite("DRENCHED").textureID, new Vector2f(0.0f, 0.0f), new Vector2f(1f, 1f)));

//        player = new EntityPlayer(new Vector3f(0, 0, 0));
//        entityManager.addEntity(player);


//        IntellisenseEngine.generateHTML();

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
    }
    private static void run(){
        //Game Loop
        long last = System.currentTimeMillis();
        long delta = 0;
        int fps = 0;

        while(!glfwWindowShouldClose(window)){
            long now = System.currentTimeMillis();
            delta+=(now-last);
            last = now;
            glfwPollEvents();
            tick();
            render();
            fps++;

            if(delta >= 1000){
                logManager.println("[FPS: "+fps+"]");
                delta -= 1000;
                fps = 0;
            }

            if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GL_TRUE){
                glfwSetWindowShouldClose(window, true);
            }
        }
    }
    
    private static void tick(){
        engines.synch();
        mouse.tick();

        scriptingEngine.tick();

        for(Engine e : engines.getCollection(Engine.class)){
            e.tick();
        }
        
        if(mouseClick.risingAction(Mouse.pressed(EnumMouseButton.LEFT))){
            mousePressed();
        }
        
        if(mouseClick.fallingAction(Mouse.pressed(EnumMouseButton.LEFT))){
            mouseReleased();
        }
    }
    
    private static void mousePressed(){
    
    }
    
    private static void mouseReleased(){
        
    }
    
    private static void render(){
        renderer.prepare();
        scriptingEngine.render();
        particleManager.render();
        uiRenderer.render(guis);
        glfwSwapBuffers(window);
    }
    
    private static void shutdown(){
        //Remove Log manager
        engines.remove(logManager);
        for(Engine e : engines.getCollection(Engine.class)){
            e.onShutdown();
        }
    }
    
    private static void cleanUp(){
        uiRenderer.cleanUp();
        loader.cleanUp();
    }

    public static long getWindowPointer(){
        return window;
    }

    public static IncludeFolder getFolder(String name){
        return srcFolders.get(name);
    }
    
    public static void addDir(String s) throws IOException {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[])field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length+1];
            System.arraycopy(paths,0,tmp,0,paths.length);
            tmp[paths.length] = s;
            field.set(null,tmp);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    } 
    
}
