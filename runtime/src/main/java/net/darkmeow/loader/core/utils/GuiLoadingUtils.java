package net.darkmeow.loader.core.utils;

import net.darkmeow.loader.core.ui.GuiLoading;

@SuppressWarnings("CallToPrintStackTrace")
public class GuiLoadingUtils {
    public static void displayLoadingGui() {
        new Thread(() -> {
            try {
                GuiLoading gui = new GuiLoading();
                gui.init();
                Thread.sleep(5000);
                gui.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
