package me.swerve.ui;

import me.swerve.entity.ui.TextEntity;
import me.swerve.game.GameManager;
import me.swerve.level.LevelManager;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.render.shader.ShaderProgram;
import org.scope.render.text.TextManager;
import org.scope.render.text.type.TextSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;

public class UIManager {
    List<TextEntity> elementsToRender = new ArrayList<>();
    private final TextSource source;


    public UIManager() {
        float uiCenterX = (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getWidth() / 2;
        float uiCenterY = (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight() / 2;

        source = TextManager.getInstance().getTextSource("Gothica");

        elementsToRender.addAll(Arrays.asList(
                new TextEntity(
                        "FPS: ",
                        0.0f,
                        (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight() - source.getLargestCharsHeight("FPS: " + ScopeEngine.getInstance().getEngineManager().getFps(), 0.5f)
                ),

                new TextEntity(
                        "Wave #",
                        0.0f,
                        (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight() - source.getLargestCharsHeight("FPS: " + ScopeEngine.getInstance().getEngineManager().getFps(), 0.5f) - source.getLargestCharsHeight("Wave #" + LevelManager.getInstance().getLevel().getWaveGenerator().getCurrentWave(), 0.5f)
                ),

                new TextEntity(
                        "Money: $",
                        0.0f,
                        (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight() - source.getLargestCharsHeight("FPS: " + ScopeEngine.getInstance().getEngineManager().getFps(), 0.5f) - source.getLargestCharsHeight("Wave #" + LevelManager.getInstance().getLevel().getWaveGenerator().getCurrentWave(), 0.5f) - source.getLargestCharsHeight("Money: $", 0.5f)
                ),

                new TextEntity(
                        "Health:",
                        0.0f,
                        (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight() - source.getLargestCharsHeight("FPS: " + ScopeEngine.getInstance().getEngineManager().getFps(), 0.5f) - source.getLargestCharsHeight("Wave #" + LevelManager.getInstance().getLevel().getWaveGenerator().getCurrentWave(), 0.5f) - source.getLargestCharsHeight("Money: $", 0.5f) - source.getLargestCharsHeight("Health: ", 0.5f)
                )

        ));
    }
    public void render(Camera camera, ShaderProgram shader) {
        if (LevelManager.getInstance().getLevel() == null) return;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (TextEntity e : elementsToRender)  {
            if (e.getText().contains("FPS")) e.setText("FPS: " + ScopeEngine.getInstance().getEngineManager().getFps());
            if (e.getText().contains("Wave")) e.setText("Wave #" + LevelManager.getInstance().getLevel().getWaveGenerator().getCurrentWave());
            if (e.getText().contains("$")) e.setText("Money: " + GameManager.getInstance().getCurrentCash() + "$");
            if (e.getText().contains("Health:")) e.setText("Health: " + GameManager.getInstance().getHealth());


            source.renderText(
                    camera,
                    shader,
                    e.getText(),
                    e.getCurrentPosition().x,
                    e.getCurrentPosition().y,
                    0.5f, 1.0f, 1.0f, 1.0f
            );
        }

        glDisable(GL_BLEND);
    }

}
