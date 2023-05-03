package fr.rameshagurla.logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

public final class Logger {

    private static final int DEFAULT_DEPTH = 4;
    private static java.util.logging.Logger appLogger = java.util.logging.Logger.getLogger("Unknown");
    private static String appName = "Unknown";
    private static String basePackage = null;
    private static boolean initialized = false;
    private Logger() {
    }

    public static Level getLevel() {
        return appLogger.getLevel();
    }
    public static void setLevel(Level newLevel) {
        appLogger.setLevel(newLevel);
    }
    public static void init(String relativePath) {
        init(relativePath, Level.INFO);
    }
    public static void init(String relativePath, Level level) {
        Locale.setDefault(Locale.ENGLISH);
        loadConfigFromFile(relativePath);
        Logger.setLevel(level);
        Logger.initialized = true;
    }
    private static void loadConfigFromFile(String relativePath) {
        try {
            InputStream is = Logger.class.getClassLoader().getResourceAsStream(relativePath);
            if (is == null) {
                Logger.log(Level.SEVERE, "Logger config file not found at path {0}", relativePath);
                return;
            }
            LogManager.getLogManager().readConfiguration(is);

            appName = Utils.getString(relativePath, "app_name", "Unknown");
            basePackage = Utils.getString(relativePath, "default_package", null);
            String outputFile = Utils.getString(relativePath, "output_file", null);

            appLogger = java.util.logging.Logger.getLogger(appName);

            if (outputFile != null) {
                Handler handler = new FileHandler(outputFile, true);
                handler.setFormatter(new SimpleFormatter());
                appLogger.addHandler(handler);
            }

        } catch (IOException e) {
            Logger.log(e);
        }
    }
    public static void log(Throwable e) {
        Logger.log(Logger.DEFAULT_DEPTH, Level.SEVERE, e.toString(), e);
    }
    public static void log(Level lvl, Throwable e) {
        Logger.log(Logger.DEFAULT_DEPTH, lvl, e.toString(), e);
    }
    public static void log(Throwable e, String msg) {
        Logger.log(Logger.DEFAULT_DEPTH, Level.SEVERE, msg + ": {0}", e);
    }
    public static void log(Level lvl, Throwable e, String msg) {
        Logger.log(Logger.DEFAULT_DEPTH, lvl, msg + ": {0}", e);
    }
    public static void log(String message, Object... objects) {
        Logger.log(Logger.DEFAULT_DEPTH, Level.INFO, message, objects);
    }
    public static void log(Level lvl, String message, Object... objects) {
        Logger.log(Logger.DEFAULT_DEPTH, lvl, message, objects);
    }
    private static void log(int depth, Level lvl, String message, Object... objects) {
        if (!initialized) {
            initialized = true;
            Logger.log(Level.WARNING, "Logger was not initialized please do so before using.");
        }
        if (lvl.intValue() < appLogger.getLevel().intValue())
            return;
        message = String.format("[%s-%s] %s", appName, Utils.getCallingClassName(depth), message);
        appLogger.log(lvl, message, objects);
        if (objects.length > 0 && objects[0] instanceof Throwable) {
            Throwable throwable = (Throwable) objects[0];
            if (lvl == Level.SEVERE) {
                boolean inPackage = false;
                for (StackTraceElement ste : throwable.getStackTrace()) {
                    Logger.log(depth + 1, Level.SEVERE, "\t {0}", ste);
                    if (Logger.basePackage != null) {
                        if (!inPackage && ste.getClassName().startsWith(Logger.basePackage))
                            inPackage = true;
                        else if (inPackage && !ste.getClassName().startsWith(Logger.basePackage))
                            break;
                    }
                }
            }
            if (throwable.getCause() != null)
                Logger.log(depth + 1, lvl, "Caused by: {0}", throwable.getCause());
        }

    }
}
