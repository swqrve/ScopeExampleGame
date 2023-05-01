package me.swerve.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import me.swerve.entity.enemy.Enemy;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.scope.camera.Camera;
import org.scope.entity.Entity;
import org.scope.render.model.struct.Material;
import org.scope.render.model.struct.Model;
import org.scope.render.shader.ShaderProgram;
import org.scope.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class Projectile extends Entity {
    @Getter private static final List<Projectile> projectiles = new ArrayList<>();
    @Setter private static Material material;
    @Setter private static Model model;


    @Getter @Setter private boolean active = true;

    @Getter private final Enemy target;

    private final float damage;
    private final float speed;

    public Projectile(Enemy target, float x, float y, float z, float damage, float speed) {
        this.target = target;

        this.damage = damage;
        this.speed = speed;

        getPosition().set(x, y, z);

        projectiles.add(this);
    }

    public void render(Model model, ShaderProgram shader, Camera camera, Matrix4f matrix) {
        if (!active) return;

        material.setUniforms(shader, "material");

        matrix.identity().translate(getX(), getY(), getZ());
        camera.getViewMatrix().transpose3x3(matrix);
        matrix.scale(0.15f);

        shader.setMatrix4f("model", matrix);
        shader.setBool("usesLighting", false);

        model.render(camera, shader, material.getTexture());
    }

    public void update(double deltaTime) {
        if (!active) return;

        if (target == null || !target.isActive()) {
            setActive(false);
            return;
        }

        Vector2f targetPos = getTarget().getPosition();

        if (targetPos == null) {
            setActive(false);
            return;
        }

        if (getPosition().distance(targetPos.x, 0.0f, targetPos.y) > 0.15f) {
            setPosition(
                    MathUtil.lerp(getX(), targetPos.x, (float) (speed * deltaTime / getPosition().distance(targetPos.x, 0.0f, targetPos.y))),
                    MathUtil.lerp(getY(), 0.0f, (float) (speed * deltaTime / getPosition().distance(targetPos.x, 0.0f, targetPos.y))),
                    MathUtil.lerp(getZ(), targetPos.y, (float) (speed * deltaTime / getPosition().distance(targetPos.x, 0.0f, targetPos.y)))
            );

            return;
        }

        target.setHealth(target.getHealth() - damage);
        setActive(false);
    }
}
