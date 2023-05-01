package me.swerve.entity.ui;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

public class TextEntity {
    @Getter @Setter private String text;

    @Getter private final Vector2f currentPosition = new Vector2f();
    @Getter private final Vector2f startPosition = new Vector2f();

    public TextEntity(String text, float x, float y) {
        this.text = text;

        startPosition.set(x, y);
        currentPosition.set(x, y);
    }

}
