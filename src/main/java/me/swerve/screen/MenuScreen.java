package me.swerve.screen;

import me.swerve.camera.StationaryCamera;
import me.swerve.entity.ui.TextEntity;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.input.InputManager;
import org.scope.render.model.struct.Texture;
import org.scope.render.model.type.SDQuad;
import org.scope.render.shader.ShaderManager;
import org.scope.render.shader.ShaderProgram;
import org.scope.render.text.type.TextSource;
import org.scope.scene.Scene;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;

public class MenuScreen implements Scene {
    private Camera menuCamera;

    private List<TextEntity> menuText;

    private TextSource textSource;

    private ShaderProgram objectShader;
    private ShaderProgram uiShader;

    private SDQuad backgroundModel;
    private Texture backgroundTexture;

    private Matrix4f modelMatrix;

    private float uiCenterX;
    private float uiCenterY;

    public void init() {
        // Text is rendered onto screen space so cameras fov and position don't matter, but it is necessary for background objects, particles, etc.
        menuCamera = new StationaryCamera(0.0f, 0, 0.0f, 60);
        Camera.setCurrentCamera(menuCamera);

        // The font source for rendering the menu text!
        textSource = new TextSource("Gothica", "game/font/gothica.ttf", 0, 48);

        // These two Shaders are instantiated by the engine, they're already accessible from these names, we set them to values to avoid to many calls to a map in the render loop.
        objectShader = ShaderManager.getShader("default");
        uiShader = ShaderManager.getShader("uiD");

        // Both the text and the background image need to know the window size
        uiCenterX = (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getWidth() / 2;
        uiCenterY = (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight() / 2;

        // Set up the message for the menu and the proper sizing!
        menuText = Arrays.asList(
                new TextEntity("Simple Tower Defense Game", uiCenterX, uiCenterY),
                new TextEntity("Press Space Key to Continue!", uiCenterX, uiCenterY - 40.0f),
                new TextEntity("Created in Scope Engine!", uiCenterX, uiCenterY - 80.0f)
        );

        // Create the model matrix to be reused throughout the render loop
        modelMatrix = new Matrix4f();

        // Create the background model (2D Quad)
        backgroundModel = new SDQuad();
        backgroundTexture = new Texture("game/textures/background.jpg");
    }

    public void input(InputManager input) {
        // If the user presses space, go to the next screen!
        if (input.isKeyPressed(GLFW.GLFW_KEY_SPACE)) ScopeEngine.getInstance().setCurrentScene(new GameScreen());
        // Close the game if the user hits escape!
        if (input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) ScopeEngine.getInstance().end();
    }

    public void render(double deltaTime) {
        // Set Proper Matrices
        objectShader.setMatrix4f("view", menuCamera.getViewMatrix());

        objectShader.setMatrix4f("projection", menuCamera.getCameraProjection());
        uiShader.setMatrix4f("projection", menuCamera.getCameraSpriteProjection());

        // Set Proper Shader Settings
        uiShader.setBool("aIsText", false);

        // Render a Quad to serve as the "Background Sprite"
        modelMatrix.identity().translate(0.0f, 0.0f, -0.5f).scale(uiCenterX * 2.0f, uiCenterY * 2.0f, 1.0f);
        uiShader.setMatrix4f("model", modelMatrix);
        backgroundModel.render(menuCamera, uiShader, backgroundTexture);

        // UI should ALWAYS be rendered last, or else other objects will block the view.
        glEnable(GL_BLEND); // This isn't done inside the render text since making enable disable calls is expensive, so if you render multiple text objects enabling for each one is a waste. (This may be moved into the TextManager to manage it)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        for (TextEntity e : menuText) textSource.renderText(menuCamera, uiShader, e.getText(), e.getCurrentPosition().x - textSource.getTextWidth(e.getText(), 1.0f), e.getCurrentPosition().y + textSource.getLargestCharsHeight(e.getText(), 1.0f), 1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
    }

    public void update(double deltaTime) {

    }


    public void cleanup() {
        backgroundModel.cleanup();
    }
}
