package me.swerve.map;

import lombok.Getter;
import lombok.Setter;
import me.swerve.tile.Tile;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.scope.logger.Debug;
import org.scope.render.model.struct.Material;
import org.scope.render.model.struct.Model;
import org.scope.render.shader.ShaderProgram;
import org.scope.util.FileUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;

public class Map {
    @Getter @Setter private static Model model;
    @Getter @Setter private static Material material;

    @Getter private final List<Tile> mapTiles = new ArrayList<>();
    @Getter private final java.util. Map<Integer, Vector2i> pathMarkers = new HashMap<>();

    @Getter private Tile startTile;
    @Getter private Tile endTile;

    @Getter private final String mapName;

    private final int wallHeight = 2;

    private int ssbo;
    private Matrix4f[] matrices;
    private float[] atlasIndex;
    private FloatBuffer dataBuffer;

    public Map(String mapName, String fileName) {
        this.mapName = mapName;

        if (!parseMap(FileUtil.loadResourceAsList(fileName))) return;

        ssbo = glGenBuffers();

        matrices = new Matrix4f[30 * 30];
        for (int i = 0; i < 30 * 30; i++) matrices[i] = new Matrix4f();

        atlasIndex = new float[30 * 30];

        dataBuffer = MemoryUtil.memAllocFloat(20 * (30 * 30));
    }

    public void render(ShaderProgram shader) {
        shader.setBool("isAParticle", false);
        shader.setBool("usesLighting", false);

        shader.setBool("instanced", true);
        shader.setFloat("atlasWidthSize", 512.0f);
        shader.setFloat("totalAtlasDimension", 3072.0f );

        material.setUniforms(shader, "material");

        int index = 0;
        for (Tile tile : mapTiles) {
            if (tile == null || !tile.isActive()) continue;

            matrices[index].identity().translate(tile.getX(), tile.getY(), tile.getZ()).scale(0.5f);
            atlasIndex[index] = tile.getType().getTextureAtlasID();

            index++;
        }

        ((Buffer) dataBuffer).position(0);

        for (int i = 0; i < index; i++) {
            matrices[i].get(dataBuffer);
            dataBuffer.put((dataBuffer.position() + 16), atlasIndex[i]);
            ((Buffer) dataBuffer).position(dataBuffer.position() + 20);
        }

        ((Buffer) dataBuffer).rewind();

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, dataBuffer, GL_DYNAMIC_DRAW);

        material.getTexture().bind(GL_TEXTURE0);

        model.bindVAO();
        glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 36, index);

        shader.setBool("instanced", false);
    }

    public boolean parseMap(List<String> mapStrings) {
        boolean pathMode = false;
        int currentMapSize = 0;

        for (int i = 0; i < mapStrings.size(); i++) {
            if (!pathMode) currentMapSize++;

            if (mapStrings.get(i).charAt(0) == '-') {
                pathMode = true;
                continue;
            }

            for (int j = 0; j < mapStrings.get(i).length(); j++) {
                if (pathMode) {
                    if (!Character.isDigit(mapStrings.get(i).charAt(j))) continue;
                    if (Integer.parseInt(String.valueOf(mapStrings.get(i).charAt(j))) == 0) continue;

                    pathMarkers.put(Integer.parseInt(String.valueOf(mapStrings.get(i).charAt(j))), new Vector2i(i - currentMapSize, j));
                    continue;
                }

                if (i >= 30 || j >= 30) {
                    Debug.log(Debug.LogLevel.ERROR, "Failed to load map: " + mapName + " The size was to large!");
                    return false;
                }

                if (Tile.TileType.getFromValue(mapStrings.get(i).charAt(j)) == null) {
                    Debug.log(Debug.LogLevel.ERROR, "Failed to load tile with type id: " + mapStrings.get(i).charAt(j) + ". That doesn't exist! Map: " + mapName + " has failed to load.");
                    return false;
                }

                Tile tile = new Tile(Tile.TileType.getFromValue(mapStrings.get(i).charAt(j)), i, -1.5f, j);
                mapTiles.add(tile);

                if (mapStrings.get(i).charAt(j) == Tile.TileType.START.getId()) startTile = tile;
                if (mapStrings.get(i).charAt(j) == Tile.TileType.START.getId()) endTile = tile;

                if (tile.getType() == Tile.TileType.WALL) for (int f = 0; f <= wallHeight; f++) mapTiles.add(new Tile(Tile.TileType.WALL, i, -1.5f + f, j));
            }
        }

        if (startTile == null) {
            Debug.log(Debug.LogLevel.ERROR, "Failed to load map: " + mapName + " There is no start tile!");
            return false;
        }

        if (endTile == null) {
            Debug.log(Debug.LogLevel.ERROR, "Failed to load map: " + mapName + " There is no end tile!");
            return false;
        }

        return true;
    }
}
