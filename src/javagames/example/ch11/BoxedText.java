package javagames.example.ch11;

import javagames.util.SimpleFramework;
import javagames.util.Utility;

import java.awt.*;

public class BoxedText extends SimpleFramework {

    public BoxedText() {
        appTitle = "Boxed Text";
        appBackground = Color.white;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);

       // Draw the top line
        String line1 = "My name is neeraj sharma.";
        String line2 = "This is the second heading";
        Font top_line_font = new Font("Arial",Font.PLAIN, 20);
        g.setFont(top_line_font);
        g.setColor(Color.black);
        Utility.drawString(g, 20, textPos, line1, line2);
    }

    public static void main(String[] args) {
        launchApp(new BoxedText());
    }
}
