package net.darkmeow.loader.core.ui;

import net.darkmeow.loader.DirectLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
public class GuiLoading {

    public static void display() {
        new Thread(() -> {
            try {
                GuiLoading gui = new GuiLoading();
                gui.init();
                Thread.sleep(5000);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        for (int attempts = 0; attempts < 100; attempts++) {
                            if (Arrays.stream(Window.getWindows()).anyMatch(window -> "LWJGL".equals(window.getName()))) {
                                return null; // 找到窗口，结束任务
                            }
                            Thread.sleep(200);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        gui.close();
                    }
                };

                worker.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public JFrame frame;

    public void init() {
        frame = new JFrame();

        //noinspection SpellCheckingInspection
        frame.setTitle("JinLiang Shield");

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

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString(" ");
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 4));
        panel.add(progressBar, BorderLayout.SOUTH);

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
