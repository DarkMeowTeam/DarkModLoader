package net.darkmeow.loader;

import net.darkmeow.loader.core.Loader;
import net.darkmeow.loader.core.ui.GuiLoading;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class LegacyForgeLoader implements IFMLLoadingPlugin {
    public LegacyForgeLoader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GuiLoading.display();

        final URL unPackedMod = Loader.loadMod();

        LaunchClassLoader launchClassLoader = Launch.classLoader;
        ClassLoader appClassLoader = LaunchClassLoader.class.getClassLoader();

        Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURLMethod.setAccessible(true);
        addURLMethod.invoke(appClassLoader, unPackedMod);
        addURLMethod.invoke(launchClassLoader, unPackedMod);
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
