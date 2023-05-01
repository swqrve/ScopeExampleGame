package me.swerve.level;

import lombok.Getter;
import me.swerve.game.GameManager;
import me.swerve.generator.WaveGenerator;
import me.swerve.map.Map;
import org.bson.Document;
import org.joml.Vector3f;
import org.scope.camera.Camera;
import org.scope.logger.Debug;
import org.scope.util.ConstManager;
import org.scope.util.FileUtil;

import java.util.HashMap;

public class LevelManager {
    @Getter private static LevelManager instance;
    @Getter private final java.util.Map<Integer, Level> levels = new HashMap<>();

    @Getter private int currentLevelIndex = 1;
    public LevelManager() {
        instance = this;

        loadLevels();

        Vector3f c = Camera.getCurrentCamera().getCameraPosition();
        c.x = getLevel().getMap().getStartTile().getX();
        c.z = getLevel().getMap().getStartTile().getZ();
    }


    private void loadLevels() {
        for (int i = 1; i <= (int) ConstManager.getConstant("TOTAL-LEVELS"); i++) {
            Document generatorSettings = FileUtil.readDocFromFile("game/levels/level_" + i + "_settings.json");

            WaveGenerator generator = new WaveGenerator(
                    generatorSettings.getInteger("totalWaves"),
                    generatorSettings.getInteger("restPeriod"),
                    WaveGenerator.WaveStrength.valueOf(generatorSettings.getString("strength"))
            );

            Map levelMap = new Map("level-" + i, "game/levels/level_" + i + ".txt");

            levels.put(i, new Level(generator, levelMap));
        }
    }

    public Level getLevel(int index) {
        if (levels.get(index) == null) {
            Debug.log(Debug.LogLevel.ERROR, "A level with index " + index + " doesn't exist!");
            return null;
        }

        return levels.get(index);
    }

    public Level getLevel() {
        if (levels.get(currentLevelIndex) == null) {
            Debug.log(Debug.LogLevel.ERROR, "A level with index " + currentLevelIndex + " doesn't exist!");
            return null;
        }

        return levels.get(currentLevelIndex);
    }

    public void nextLevel() {
        // getLevel().cleanUp();
        if (getLevel(currentLevelIndex + 1) == null) {
            GameManager.getInstance().setCurrentState(GameManager.GameState.WON);
            return;
        }

        currentLevelIndex++;
    }
}
