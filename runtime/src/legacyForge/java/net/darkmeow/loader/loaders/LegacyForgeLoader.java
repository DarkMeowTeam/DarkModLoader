package net.darkmeow.loader.loaders;

import net.darkmeow.loader.core.Loader;
import net.darkmeow.loader.ui.GuiLoading;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class LegacyForgeLoader implements IFMLLoadingPlugin {
    public LegacyForgeLoader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        final URL mod = Loader.loadMod();

        if (mod != null) {
            GuiLoading.display();

            Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);
            addURLMethod.invoke(LaunchClassLoader.class.getClassLoader(), mod);
            addURLMethod.invoke(Launch.classLoader, mod);

            Class.forName("org.spongepowered.asm.launch.MixinBootstrap", true, LaunchClassLoader.class.getClassLoader()).getDeclaredMethod("init").invoke(null);
        }
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
