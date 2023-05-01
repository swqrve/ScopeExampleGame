package me.swerve;

import me.swerve.screen.MenuScreen;
import org.scope.logger.Debug;
import org.scope.util.EnginePreferences;
import org.scope.util.Platform;
import org.scope.util.ScopeLauncher;

public class TowerDefense {

    public static void main(String[] args) {
        EnginePreferences preferences = new EnginePreferences(Platform.WINDOWS, 1280, 720, false, "Tower Defense!", true, "Engine", true, 5, 5);
        ScopeLauncher.launch(new MenuScreen(), args, preferences);

        System.out.println("------------------------------------------------------------------------------");
        Debug.log(Debug.LogLevel.INFO, "Now printing all saved logs...", false);
        Debug.printDebugLog(Debug.LogLevel.ALL);
        System.out.println("------------------------------------------------------------------------------");
    }
}
