package me.swerve.level;

import lombok.Getter;
import me.swerve.entity.enemy.type.Walker;
import me.swerve.entity.projectile.Projectile;
import me.swerve.entity.tower.Tower;
import me.swerve.generator.WaveGenerator;
import me.swerve.map.Map;
import org.joml.Matrix4f;
import org.scope.camera.Camera;
import org.scope.render.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

public class Level {
    @Getter private final List<Tower> towers = new ArrayList<>();

    @Getter private final WaveGenerator waveGenerator;
    @Getter private final Map map;

    public Level(WaveGenerator generator, Map map) {
        this.waveGenerator = generator;
        this.map = map;
    }

    public void render(ShaderProgram shader, Camera camera, Matrix4f matrix) {
        map.render(shader);

        shader.setBool("instanced", false);

        waveGenerator.render(shader, camera, matrix);
        towers.forEach(tower -> tower.render(shader, camera, matrix));
        Projectile.getProjectiles().forEach(p -> p.render(Walker.getModel(), shader, camera, matrix));
    }

    public void update(double deltaTime) {
        map.getMapTiles().forEach(t -> t.update(deltaTime));
        waveGenerator.update(map, deltaTime);
        towers.forEach(tower -> tower.update(waveGenerator.getEnemies()));
        Projectile.getProjectiles().forEach(p -> p.update(deltaTime));
    }
}
