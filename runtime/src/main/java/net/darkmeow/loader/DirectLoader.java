package net.darkmeow.loader;

import net.darkmeow.loader.core.Loader;
import net.darkmeow.loader.core.ui.GuiLoading;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class DirectLoader {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        GuiLoading.display();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{Loader.loadMod()}, DirectLoader.class.getClassLoader())) {
            Class<?> mainClass = classLoader.loadClass(new java.util.jar.Manifest(DirectLoader.class.getResourceAsStream("/META-INF/MANIFEST.MF")).getMainAttributes().getValue("DarkLoader-DirectClass"));
            Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
