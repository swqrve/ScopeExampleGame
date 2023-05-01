package me.swerve.entity.tower;

import lombok.Getter;
import lombok.Setter;
import me.swerve.entity.enemy.Enemy;
import org.joml.Matrix4f;
import org.scope.camera.Camera;
import org.scope.entity.SDEntity;
import org.scope.render.model.struct.Material;
import org.scope.render.model.struct.Model;
import org.scope.render.shader.ShaderProgram;

import java.util.List;

public abstract class Tower extends SDEntity {
    @Getter @Setter private static Model model;

    @Getter private final float damage;
    @Getter private final float fireRate;
    @Getter private final float range;

    @Getter private final float cost;

    @Getter private long lastFireTime = System.currentTimeMillis();

    public Tower(float damage, float fireRate, float range, float cost, float x, float z) {
        this.damage = damage;
        this.fireRate = fireRate;
        this.range = range;

        this.cost = cost;

        getPosition().set(x, z);
    }

    public abstract void render(ShaderProgram shader, Camera camera, Matrix4f matrix);

    public void render(Material mat, Model model, ShaderProgram shader, Camera camera, Matrix4f matrix) {
        mat.setUniforms(shader, "material");

        matrix.identity().translate(getX(), 0.0f, getY());
        camera.getViewMatrix().transpose3x3(matrix);
        matrix.scale(1.50f);

        shader.setMatrix4f("model", matrix);
        shader.setBool("usesLighting", false);

        model.render(camera, shader, mat.getTexture());
    }

    public void update(List<Enemy> enemies) {
        if ((System.currentTimeMillis() - lastFireTime) <= (fireRate * 1000.0f)) return;

        Enemy closestEnemy = null;

        for (Enemy w : enemies) if (w.getPosition().distance(getPosition()) <= range) {
            if (!w.isActive()) continue;

            if (closestEnemy == null) {
                closestEnemy = w;
                continue;
            }

            if (w.getPosition().distance(getPosition()) < closestEnemy.getPosition().distance(getPosition())) closestEnemy = w;
        }

        if (closestEnemy == null) return;

        shotFired(closestEnemy, damage);

        lastFireTime = System.currentTimeMillis();
    }

    public abstract void shotFired(Enemy e, float damage);
}
