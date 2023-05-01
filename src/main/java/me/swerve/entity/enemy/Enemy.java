package me.swerve.entity.enemy;

import lombok.Getter;
import lombok.Setter;
import me.swerve.game.GameManager;
import me.swerve.map.Map;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.scope.camera.Camera;
import org.scope.entity.SDEntity;
import org.scope.render.model.struct.Material;
import org.scope.render.model.struct.Model;
import org.scope.render.shader.ShaderProgram;
import org.scope.util.MathUtil;

public abstract class Enemy extends SDEntity {
    @Getter @Setter private static Model model;

    @Getter @Setter private int currentNodeTarget = 1;
    @Getter @Setter private float renderingLevel;

    @Getter private final float speed;

    @Getter @Setter private float health;

    @Getter @Setter private boolean active = true;
    public Enemy(float speed, float health, float x, float y, float z) {
        this.speed = speed;
        this.renderingLevel = y;
        this.health = health;

        getPosition().set(x, z);
    }

    public abstract void render(ShaderProgram shader, Camera camera, Matrix4f matrix);

    public void render(Material mat, Model model, ShaderProgram shader, Camera camera, Matrix4f matrix) {
        mat.setUniforms(shader, "material");

        matrix.identity().translate(getX(), renderingLevel, getY());
        camera.getViewMatrix().transpose3x3(matrix);
        matrix.scale(1.50f);

        shader.setMatrix4f("model", matrix);
        shader.setBool("usesLighting", false);

        model.render(camera, shader, mat.getTexture());
    }


    public void update(Map map, double deltaTime) {
        if (!active) return;

        Vector2i target = map.getPathMarkers().get(currentNodeTarget);

        if (target == null) {
            GameManager.getInstance().setHealth(GameManager.getInstance().getHealth() - 1);
            setActive(false);
            return;
        }

        if (health <= 0.0f) {
            onDeath();
            setActive(false);
            return;
        }

        if (getPosition().distance(map.getPathMarkers().get(currentNodeTarget).x, map.getPathMarkers().get(currentNodeTarget).y) > 0.01f) {
            setPosition(
                    MathUtil.lerp(getX(), (float) target.x, (float) (speed * deltaTime / getPosition().distance(target.x, target.y))),
                    MathUtil.lerp(getY(), (float) target.y, (float) (speed * deltaTime / getPosition().distance(target.x, target.y)))
            );
        } else currentNodeTarget++;
    }

    public abstract void onDeath();
}
