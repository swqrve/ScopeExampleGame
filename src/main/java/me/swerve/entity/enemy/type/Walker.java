package me.swerve.entity.enemy.type;

import me.swerve.entity.enemy.Enemy;
import me.swerve.game.GameManager;
import org.joml.Matrix4f;
import org.scope.camera.Camera;
import org.scope.render.model.struct.Material;
import org.scope.render.model.struct.Texture;
import org.scope.render.shader.ShaderProgram;


public class Walker extends Enemy {
    private static Material material;
    public Walker(float x, float y, float z) {
        super(1.0f, 3.0f, x, y, z);
        if (material == null) material = new Material(new Texture("game/textures/walker.png"));
    }

    @Override
    public void render(ShaderProgram shader, Camera camera, Matrix4f matrix) {
        render(material, getModel(), shader, camera, matrix);
    }

    @Override
    public void onDeath() {
        GameManager.getInstance().setCurrentCash(GameManager.getInstance().getCurrentCash() + 2.0f);
    }
}
