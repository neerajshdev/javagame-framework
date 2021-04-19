package javagames.example.ch1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;


class DisplayModeExample extends JFrame {

    private JComboBox displayModes;
    private GraphicsDevice graphicsDevice;
    private DisplayMode currentDisplayMode;

    private DisplayModeExample () {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = ge.getDefaultScreenDevice();
        currentDisplayMode = graphicsDevice.getDisplayMode();
    }

    private void createAndShowGui() {
        setLayout(new FlowLayout());
        setUndecorated(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        displayModes = new JComboBox(listDisplayModes());
        add(displayModes);
        JButton enterButton = new JButton("Enter full screen");
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEnterFullScr();
            }
        });
        add(enterButton);
        JButton exitButton = new JButton("Exit full screen");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExitFullScr();
            }
        });
        add(exitButton);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void onEnterFullScr() {
        if (graphicsDevice.isFullScreenSupported()) {
            DisplayMode newMode = getSelectedMode();
            graphicsDevice.setFullScreenWindow(this);
            graphicsDevice.setDisplayMode(newMode);
        }
    }

    private void onExitFullScr() {
        if (graphicsDevice.isDisplayChangeSupported()) {
            graphicsDevice.setDisplayMode(currentDisplayMode);
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    private DisplayMode getSelectedMode() {
        DisplayMode mode = ((DisplayModeWrapper) Objects.requireNonNull(displayModes.getSelectedItem())).dm;
        int width = mode.getWidth();
        int height = mode.getHeight();
        int bitDepth = mode.getBitDepth();
        int refresh = DisplayMode.REFRESH_RATE_UNKNOWN;
        return new DisplayMode(width, height, bitDepth, refresh);
    }
    private DisplayModeWrapper[] listDisplayModes() {
        ArrayList<DisplayModeWrapper> list = new ArrayList<>();
        for (DisplayMode mode : graphicsDevice.getDisplayModes()) {
            if (mode.getBitDepth() == 32) {
                DisplayModeWrapper wrappedDM = new DisplayModeWrapper(mode);
                if (!list.contains(wrappedDM))
                    list.add(wrappedDM);
            }
        }

        return list.toArray(new DisplayModeWrapper[0]);
    }

    public static void main(String[] args) {
        DisplayModeExample app = new DisplayModeExample();
        app.createAndShowGui();
    }
}