package me.swerve.screen;

import me.swerve.camera.FPSCamera;
import me.swerve.game.GameManager;
import me.swerve.ui.UIManager;
import org.scope.camera.Camera;
import org.scope.input.InputManager;
import org.scope.render.model.type.SkyBox;
import org.scope.render.shader.ShaderManager;
import org.scope.render.shader.ShaderProgram;
import org.scope.scene.Scene;

public class GameScreen implements Scene {
    private FPSCamera camera;

    private GameManager gameManager;
    private UIManager uiManager;

    private ShaderProgram objectShader;
    private ShaderProgram uiShader;

    private SkyBox skybox;

    @Override
    public void init() {
        // Create our FPS Camera! This may become a part of the engines default cameras
        camera = new FPSCamera(3.5f,2.0f, 0.0f, 2.0f, 60.0f, 0.05f);
        Camera.setCurrentCamera(camera);

        // These two Shaders are instantiated by the engine, they're already accessible from these names, we set them to values to avoid to many calls to a map in the render loop.
        objectShader = ShaderManager.getShader("default");
        uiShader = ShaderManager.getShader("uiD");

        // Loads a skybox using the default skybox shader "skyboxd" at the given directory.
        skybox = new SkyBox(ShaderManager.getShader("skyboxd"), "game/textures/skybox");

        // Instantiate game and ui managers!
        gameManager = new GameManager();
        uiManager = new UIManager();
    }

    @Override
    public void input(InputManager input) {
        gameManager.input(input, camera);
    }

    @Override
    public void render(double deltaTime) {
        gameManager.render(camera, objectShader);

        skybox.render(camera);

        uiManager.render(camera, uiShader);
    }

    @Override
    public void update(double deltaTime) {
        gameManager.update(deltaTime);
    }

    @Override
    public void cleanup() {

    }
}
