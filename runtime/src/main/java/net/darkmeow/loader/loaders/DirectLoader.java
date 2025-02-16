package net.darkmeow.loader.loaders;

import net.darkmeow.loader.core.Constants;
import net.darkmeow.loader.core.Loader;
import net.darkmeow.loader.ui.GuiLoading;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class DirectLoader {

    @SuppressWarnings("all")
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--build-info")) {
            System.out.println("Build Device: " + Constants.BUILD_DEVICE);
            System.out.println("Build Date: " + Constants.BUILD_DATE);
            System.out.flush();
            return;
        }

        final URL url = Loader.loadMod();

        if (url != null) {
            GuiLoading.display();

            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{ url }, DirectLoader.class.getClassLoader())) {
                Class<?> mainClass = classLoader.loadClass(Constants.DIRECT_CLASS);
                Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
                mainMethod.invoke(null, (Object) args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}