package javagames.example.ch11;

import javagames.util.SimpleFramework;

import java.awt.*;

public class TextLayoutExample extends SimpleFramework {

	private Font font;
	private int maxWidth;

	public TextLayoutExample() {
		appTitle = "Text layout example";
		appWidth = 500;
		appHeight = 500;
		appBackground = Color.white;
		appFPSColor = Color.black;
		appMaintainRatio = true;
	}

	@Override
	protected void initialize() {
		super.initialize();
		font = new Font( "Arial", Font.BOLD, 40 );
		FontMetrics fm = getFontMetrics(font);
		maxWidth = Integer.MIN_VALUE;
		for (int i = '!'; i < 'z'; i++) {
			String letter = " " + (char) i;
			maxWidth = Math.max(maxWidth, fm.stringWidth(letter));
		}

	}

	@Override
	protected void render(Graphics g) {
		super.render(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(Color.green);
		FontMetrics fm = g2d.getFontMetrics();
		int x = 20;
		int y = 50;
		int height = fm.getHeight();
		y += fm.getAscent();
		int count = 0;
		for (int i = '!'; i <= 'z'; i++) {
			String letter = " " + (char) i;
			g2d.drawString(letter, x + count * maxWidth, y);
			count++;
			if (count % 10 == 0) {
				count = 0;
				y += height;
			}
		}

	}

	public static void main(String[] args) {
		launchApp(new TextLayoutExample());
	}
}