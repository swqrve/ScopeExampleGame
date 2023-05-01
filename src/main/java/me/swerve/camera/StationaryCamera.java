package me.swerve.camera;

import org.scope.camera.Camera;
import org.scope.input.InputManager;

public class StationaryCamera extends Camera {
    public StationaryCamera(float x, float y, float z, float fov) {
        super(x, y, z, fov);
    }

    @Override public void input(InputManager input, double delta) { } // Stationary Camera So we don't need to track any input for it !
}
