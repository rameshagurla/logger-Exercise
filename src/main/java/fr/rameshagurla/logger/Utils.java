package fr.rameshagurla.logger;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class Utils {

    private Utils() {
    }

    static String getString(String bundlePath, String key, String defaultValue) {
        int pos = bundlePath.indexOf(".properties");
        try {
            return  ResourceBundle.getBundle(bundlePath.substring(0,pos)).getString(key);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }
    static String getCallingClassName(int stackLevel) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackLevel >= stackTrace.length)
            return null;
        String[] source = stackTrace[stackLevel].getClassName().split("\\.");
        return source[source.length - 1];
    }
}
