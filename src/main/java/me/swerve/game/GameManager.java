package me.swerve.game;

import lombok.Getter;
import lombok.Setter;
import me.swerve.entity.enemy.Enemy;
import me.swerve.entity.projectile.Projectile;
import me.swerve.entity.tower.Tower;
import me.swerve.level.LevelManager;
import me.swerve.map.Map;
import me.swerve.tile.Tile;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.collisions.Raycast;
import org.scope.input.InputManager;
import org.scope.render.model.ModelManager;
import org.scope.render.model.struct.Material;
import org.scope.render.model.struct.Texture;
import org.scope.render.model.type.Cube;
import org.scope.render.model.type.Quad;
import org.scope.render.shader.ShaderProgram;
import org.scope.util.ConstManager;

public class GameManager {
    public enum GameState { WAITING, PLAYING, LOST, WON }

    @Getter private static GameManager instance;

    @Getter @Setter private GameState currentState;

    @Getter @Setter private float currentCash = 4.0f;
    @Getter @Setter private float health = 3.0f;

    private final Matrix4f matrix;

    public GameManager() {
        instance = this;

        ConstManager.createConstant("TOTAL-LEVELS", 2);

        new LevelManager();

        currentState = GameState.WAITING;

        initAssets();

        matrix = new Matrix4f();
    }

    public void input(InputManager input, Camera camera) {
        if (input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) ScopeEngine.getInstance().end();
        if (input.isKeyPressed(GLFW.GLFW_KEY_C)) camera.setFov(30.0f); else camera.setFov(60.0f);

        if (input.isMousePressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            Raycast cast = new Raycast(camera.getCameraPosition(), camera.getDirection());
            for (Tile t : LevelManager.getInstance().getLevel().getMap().getMapTiles()) if (cast.intersects(t.getCollider()) && t.getType() == Tile.TileType.PLACEABLE) {
                t.onRightClick();
                break;
            }
        }

    }

    public void render(Camera camera, ShaderProgram shader) {
        shader.setMatrix4f("view", camera.getViewMatrix());
        shader.setMatrix4f("projection", camera.getCameraProjection());

        shader.setBool("isAParticle", false);
        shader.setBool("isAnimated", false);
        shader.setBool("usesLighting", false);

        LevelManager.getInstance().getLevel().render(shader, camera, matrix);
    }

    public void update(double deltaTime) {
        if (currentState == GameState.LOST) {
            ScopeEngine.getInstance().end();
            return;
        }

        LevelManager.getInstance().getLevel().update(deltaTime);
        if (health <= 0.0f) setCurrentState(GameState.LOST);
    }


    private void initAssets() {
        Map.setMaterial(new Material(new Texture("game/textures/atlas.png"), 32.0f));
        Projectile.setMaterial(new Material(new Texture("game/textures/bullet.png"), 32.0f));

        if (ModelManager.getModel("cube") == null) new Cube();
        Map.setModel(ModelManager.getModel("cube"));

        if (ModelManager.getModel("quad") == null) new Quad();
        Tower.setModel(ModelManager.getModel("quad"));
        Enemy.setModel(ModelManager.getModel("quad"));
        Projectile.setModel(ModelManager.getModel("quad"));
    }
}
