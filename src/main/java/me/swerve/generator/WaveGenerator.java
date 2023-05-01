package me.swerve.generator;

import lombok.Getter;
import me.swerve.entity.enemy.Enemy;
import me.swerve.entity.enemy.type.Walker;
import me.swerve.game.GameManager;
import me.swerve.level.LevelManager;
import me.swerve.map.Map;
import org.joml.Matrix4f;
import org.scope.camera.Camera;
import org.scope.render.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

public class WaveGenerator {
    public enum WaveStrength {
        WEAK(3), NORMAL(5), STRONG(12);

        @Getter private int baseWave;

        WaveStrength(int baseWave) {
            this.baseWave =  baseWave;
        }
    };

    @Getter private final List<Enemy> enemies = new ArrayList<>();
    List<DelayedAction> delayedActions = new ArrayList<>();

    @Getter private int currentWave = 0;

    private boolean startedSpawn;

    private final int totalWaves;
    private final int restPeriod;

    private WaveStrength strength;

    private long restPeriodStart = System.currentTimeMillis();

    public WaveGenerator(int totalWaves, int restPeriod, WaveStrength strength) {
        this.totalWaves = totalWaves;
        this.restPeriod = restPeriod;

        this.strength = strength;
    }

    public void render(ShaderProgram shader, Camera camera, Matrix4f matrix) {
        for (Enemy e : enemies) {
            if (!e.isActive()) continue;
            e.render(shader, camera, matrix);
        }
    }

    public void update(Map map, double deltaTime) {
        for (DelayedAction action : delayedActions) action.update();

        for (Enemy e : enemies) e.update(map, deltaTime);

        if (GameManager.getInstance().getCurrentState() == GameManager.GameState.WAITING && (System.currentTimeMillis() - restPeriodStart) >= (1000.0f * restPeriod)) {
            currentWave++;
            if (currentWave > totalWaves) {
                LevelManager.getInstance().nextLevel();
                return;
            }
            GameManager.getInstance().setCurrentState(GameManager.GameState.PLAYING);
        }

        if (GameManager.getInstance().getCurrentState() == GameManager.GameState.PLAYING) {
            if (startedSpawn) {
                if (getActiveDelayedActionCount() == 0 && getActiveEnemyCount() == 0) {
                    restPeriodStart = System.currentTimeMillis();
                    GameManager.getInstance().setCurrentState(GameManager.GameState.WAITING);
                    enemies.clear();
                    delayedActions.clear();

                    startedSpawn = false;
                }

                return;
            }

            int numEnemiesToSpawn = strength.getBaseWave() * currentWave;

            for (int i = 0; i < numEnemiesToSpawn; i++) {
                float delay = (i * (1.0f / (numEnemiesToSpawn + 1)) * strength.getBaseWave() * 1000.0f) + 500.0f;
                delayedActions.add(new DelayedAction(delay, () -> enemies.add(new Walker(map.getStartTile().getX(), -0.25f, map.getStartTile().getZ()))));
            }

            startedSpawn = true;
        }
    }

    private int getActiveDelayedActionCount() {
        int i = 0;
        for (DelayedAction a : delayedActions) if (a.isActive()) i++;
        return i;
    }

    private int getActiveEnemyCount() {
        int i = 0;
        for (Enemy a : enemies) if (a.isActive()) i++;
        return i;
    }
}

class DelayedAction {
    @Getter private boolean active;

    private final long startTime;
    private final float delay;

    private final Runnable action;

    public DelayedAction(float delay, Runnable run) {
        this.startTime = System.currentTimeMillis();
        this.delay = delay;

        this.action = run;

        active = true;
    }


    public void update() {
        if (!active) return;

        if ((System.currentTimeMillis() - startTime) >= delay) {
            action.run();
            active = false;
        }
    }




}
