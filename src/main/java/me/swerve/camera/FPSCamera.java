package me.swerve.camera;

import lombok.Getter;
import lombok.Setter;
import me.swerve.level.LevelManager;
import me.swerve.tile.Tile;
import org.joml.Vector3f;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.collisions.coliders.AABB;
import org.scope.input.InputManager;

import static org.lwjgl.glfw.GLFW.*;

public class FPSCamera extends Camera {

    @Getter private boolean firstMouse;

    @Getter @Setter private float lastX;
    @Getter @Setter private float lastY;

    @Getter @Setter private float sensitivity;
    @Getter @Setter private float cameraSpeed;

    @Getter private final Vector3f incrementVector = new Vector3f();
    @Getter private final Vector3f holderVector = new Vector3f();

    private float xOffSet;
    private float yOffSet;

    private final AABB collider = new AABB();

    public FPSCamera(float speed, float x, float y, float z, float fov, float sensitivity) {
        super(x, y, z, fov);

        this.cameraSpeed = speed;
        this.sensitivity = sensitivity;

        collider.setCenter(new Vector3f(getCurrentCamera().getCameraPosition().x, getCurrentCamera().getCameraPosition().y, getCurrentCamera().getCameraPosition().z), new Vector3f(0.25f, 0.25f, 0.25f));

        init();
    }

    public void init() {
        glfwSetInputMode(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), (w, x, y) -> {
            if (firstMouse) {
                setLastX((float) x);
                setLastY((float) y);
                firstMouse = false;
            }

            xOffSet = (float) (x - getLastX());
            yOffSet = (float) (getLastY() - y);

            setLastX((float) x);
            setLastY((float) y);

            xOffSet *= getSensitivity();
            yOffSet *= getSensitivity();

            setYaw(getYaw() + xOffSet);
            setPitch(getPitch() + yOffSet);

            if (getPitch() > 89.0f) setPitch(89.0f);
            if (getPitch() < -89.0f) setPitch(-89.0f);
        });
    }

    public void input(InputManager input, double delta) {
        float cameraSpeed = (float) (getCameraSpeed() * delta);
        holderVector.set(getCameraPosition());

        if (input.isKeyPressed(GLFW_KEY_W)) getCameraPosition().add(incrementVector.set(getCameraFront()).mul(cameraSpeed));
        if (input.isKeyPressed(GLFW_KEY_S)) getCameraPosition().sub(incrementVector.set(getCameraFront()).mul(cameraSpeed));
        if (input.isKeyPressed(GLFW_KEY_A)) getCameraPosition().sub(incrementVector.set(getCameraFront()).cross(getCameraUp()).normalize().mul(cameraSpeed));
        if (input.isKeyPressed(GLFW_KEY_D)) getCameraPosition().add(incrementVector.set(getCameraFront()).cross(getCameraUp()).normalize().mul(cameraSpeed));

        collider.setCenter(new Vector3f(getCurrentCamera().getCameraPosition().x, getCurrentCamera().getCameraPosition().y, getCurrentCamera().getCameraPosition().z), new Vector3f(0.20f, 0.20f, 0.20f));

        boolean colliding = false;
        if (LevelManager.getInstance().getLevel() != null) for (Tile t : LevelManager.getInstance().getLevel().getMap().getMapTiles()) if (t.getCollider().intersects(collider)) colliding = true;

        if (colliding) getCameraPosition().set(holderVector);

        getCameraPosition().y = 0.0f;
    }
}
