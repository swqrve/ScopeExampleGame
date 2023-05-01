package me.swerve.tile;

import lombok.Getter;
import lombok.Setter;
import me.swerve.entity.tower.types.BaseTower;
import me.swerve.game.GameManager;
import me.swerve.level.LevelManager;
import org.joml.Vector3f;
import org.scope.collisions.coliders.AABB;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    @Getter private static final List<Tile> tiles = new ArrayList<>();

    private boolean hasTurret = false;

    public enum TileType {
        GRASS('G', 0), PLACEABLE('P', 1), WALL('W', 2), START('S', 3), END('E', 4), PATH('H', 5);

        @Getter private final char id;
        @Getter private final int textureAtlasID;

        TileType(char id, int textureAtlasID) {
            this.id = id;
            this.textureAtlasID = textureAtlasID;
        }

        public static TileType getFromValue(char id) {
            for (TileType e : values()) if (e.id == id) return e;
            return null;
        }
    }

    @Getter @Setter private TileType type;

    @Getter private final float x;
    @Getter private final float y;
    @Getter private final float z;

    @Getter @Setter private boolean active = true;

    @Getter private final AABB collider = new AABB();

    public Tile(TileType type, float x, float y, float z) {
        this.type = type;

        this.x = x;
        this.y = y;
        this.z = z;

        collider.setCenter(new Vector3f(getX(), getY(), getZ()), new Vector3f(1.0f, 1.0f, 1.0f));

        tiles.add(this);
    }

    public void update(double deltaTime) {
        if (!active) return;

        switch (type) {
            case START:
                break;
            case END:
                break;
            case PLACEABLE:
                break;
        }
    }

    public void onRightClick() {
        if (type == TileType.PLACEABLE) {
            if (hasTurret) return;
            if (GameManager.getInstance().getCurrentCash() < 2.0f) return;

            GameManager.getInstance().setCurrentCash(GameManager.getInstance().getCurrentCash() - 2.0f);
            LevelManager.getInstance().getLevel().getTowers().add(new BaseTower((int) x, (int) z));

            hasTurret = true;
        }
    }
}
