package javagames.util;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SimpleFrameworkTemplate extends SimpleFramework {


    private String[] names;
    private GraphicsEnvironment ge;
    private  int name_index;

    SimpleFrameworkTemplate() {
        appTitle = "framework template";
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        names = ge.getAvailableFontFamilyNames();

        String font_name = names[name_index];
        appFPSFont = new Font(font_name, Font.BOLD | Font.ITALIC, 20);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
            name_index = (name_index + 1) % names.length;
            appFPSFont = new Font(names[name_index], Font.PLAIN, 20);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
            name_index--;
            if (name_index < 0) name_index = 0;
            appFPSFont = new Font(names[name_index], Font.PLAIN, 20);
            System.out.println(names[name_index]);
        }

    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
    }

    @Override
    protected void terminate() {
        super.terminate();
    }

    public static void main(String[] args) {
        launchApp(new SimpleFrameworkTemplate());
    }
}
