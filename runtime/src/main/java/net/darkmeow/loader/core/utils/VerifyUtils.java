package net.darkmeow.loader.core.utils;

import net.darkmeow.loader.core.Constants;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class VerifyUtils {

    @SuppressWarnings("all")
    public static boolean isAllowLoad(URL url) {
        final String verifyClass = Constants.VERIFY_CLASS;

        if (!verifyClass.isEmpty()) {
            try (URLClassLoader tempClassLoader = new URLClassLoader(new URL[]{ url }, null)) {
                try {
                    Method checkMethod = tempClassLoader.loadClass(verifyClass).getDeclaredMethod("isAllowLoad");
                    checkMethod.setAccessible(true);

                    return (boolean) checkMethod.invoke(null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

}
