package net.darkmeow.loader.ui;

import net.darkmeow.loader.core.Constants;
import net.darkmeow.loader.loaders.DirectLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
public class GuiLoading {

    public static void display() {
        if (!Constants.DISABLE_SPLASH) {
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

    public JFrame frame;

    public void init() {
        frame = new JFrame();

        frame.setTitle("");

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setUndecorated(true);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(new BorderLayout());

        BufferedImage img;
        try {
            img = ImageIO.read(Objects.requireNonNull(DirectLoader.class.getResourceAsStream("/splash.png")));
        } catch (Throwable e) {
            e.printStackTrace();
            frame.dispose();
            return;
        }

        JLabel label = new JLabel(new ImageIcon(img));
        panel.add(label, BorderLayout.CENTER);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        frame.toFront();
    }

    public void close() {
        frame.dispose();
    }
}
