package me.swerve.entity.tower.types;

import me.swerve.entity.enemy.Enemy;
import me.swerve.entity.projectile.Projectile;
import me.swerve.entity.tower.Tower;
import org.joml.Matrix4f;
import org.scope.camera.Camera;
import org.scope.render.model.struct.Material;
import org.scope.render.model.struct.Texture;
import org.scope.render.shader.ShaderProgram;


public class BaseTower extends Tower {
    private static Material material;

    public BaseTower(int x, int z) {
        super(0.5f, 1.0f, 3.0f, 10.0f, x, z);
        if (material == null) material = new Material(new Texture("game/textures/Tower.png"));
    }

    @Override
    public void render(ShaderProgram shader, Camera camera, Matrix4f matrix) {
        render(material, getModel(), shader, camera, matrix);
    }

    @Override
    public void shotFired(Enemy e, float damage) {
        new Projectile(e, getX(), 0.5f, getY(), 1.0f, 3.0f);
    }
}
