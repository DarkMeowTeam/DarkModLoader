package net.darkmeow.loader;

import net.darkmeow.loader.core.Constants;
import net.darkmeow.loader.core.Loader;
import net.darkmeow.loader.core.utils.GuiLoadingUtils;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class DirectLoader {
    public static final URL UNPACKED_MOD = Loader.loadMod(Constants.MOD_NAME);

    @SuppressWarnings("all")
    public static void main(String[] args) {
        GuiLoadingUtils.displayLoadingGui();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{UNPACKED_MOD}, DirectLoader.class.getClassLoader())) {
            Class<?> mainClass = classLoader.loadClass(new java.util.jar.Manifest(DirectLoader.class.getResourceAsStream("/META-INF/MANIFEST.MF")).getMainAttributes().getValue("DarkLoader-DirectClass"));
            Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
