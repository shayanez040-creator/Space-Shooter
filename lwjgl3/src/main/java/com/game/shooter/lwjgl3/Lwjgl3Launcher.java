package com.game.shooter.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.game.shooter.SpaceShooterGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static void createApplication() {
        new Lwjgl3Application(new SpaceShooterGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // ✅ Window title
        config.setTitle("SpaceShooter");

        // ✅ FIXED window size (IMPORTANT)
        config.setWindowedMode(800, 600);
        config.setResizable(false);

        // ✅ Stable rendering
        config.useVsync(true);
        config.setForegroundFPS(60);

        // ✅ Window icon (optional)
        config.setWindowIcon(
            "libgdx128.png",
            "libgdx64.png",
            "libgdx32.png",
            "libgdx16.png"
        );

        // ❌ REMOVE OpenGL emulation for simplicity (not needed now)
        // config.setOpenGLEmulation(...);

        return config;
    }
}

