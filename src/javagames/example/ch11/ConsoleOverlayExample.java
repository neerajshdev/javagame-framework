

package javagames.example.ch11;

import javagames.util.SimpleFramework;
import javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class ConsoleOverlayExample extends SimpleFramework {


	private BufferedImage console;
	private int xConsole;
	private int yConsole;
	private float alpha;

	private Font consoleFont;
	private int fontHgt;

	private Vector<String> text;
	private float currY;

	private boolean boxes;
	private boolean hide;
	private float hidden;

	public ConsoleOverlayExample() {
		appBackground = Color.LIGHT_GRAY;
		appTitle = "Console overlay example";
	}

	@Override
	protected void initialize() {
		super.initialize();

		consoleFont = new Font( "Courier New", Font.BOLD, 20 );
		FontMetrics fm = getFontMetrics(consoleFont);
		fontHgt = fm.getHeight();

		text = new Vector<>();
		text.add( "Press the H key to" );
		text.add( "Show and Hide the console." );
		text.add( "Hover the mouse over the" );
		text.add( "Text to change the transparency." );
		text.add( "Press space bar to more text." );
		text.add( "Press 'B' to toggle hidden boxed." );

		text.add(0, text.lastElement());

		console = new BufferedImage(
				canvas.getWidth() - 40,
				fontHgt * (text.size() + 1 ),
				BufferedImage.TYPE_INT_ARGB
		);

		xConsole = 20;
		yConsole = 50;
	}

	@Override
	protected void processInput(float t) {
		super.processInput(t);

		if ( keyboard.keyDownOnce( KeyEvent.VK_SPACE ) ) {
			currY = fontHgt;
			text.remove(0);
			text.add( text.firstElement() );
		}

		if ( keyboard.keyDownOnce( KeyEvent.VK_B ) ) {
			boxes = !boxes;
		}

		if ( keyboard.keyDownOnce( KeyEvent.VK_H ) ) {
			hide = !hide;
		}

		Point pos = mouse.getPosition();
		int minX = xConsole;
		int minY = yConsole;
		int maxX = console.getWidth();
		int maxY = console.getHeight();

		if ( pos.x > minX && pos.x < maxX && pos.y > minY && pos.y < maxY ) {
			alpha = 1.0f;
		} else {
			alpha = 0.75f;
		}
	}

	@Override
	protected void updateObjects(float t) {
		super.updateObjects(t);

		if (hide && hidden == 1) {
			return;
		}

		if (hide && hidden < 1) {
			hidden += t;
			if (hidden > 1) hidden = 1;
		} else if (!hide && hidden > 0) {
			hidden -= t;
			if (hidden < 0) hidden = 0;
		}

		if ( currY > 0 ) {
			currY -= fontHgt * t;
		}

		Graphics2D g = console.createGraphics();
		g.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
				);

		g.setColor(Color.darkGray);
		g.fillRect(0, 0, console.getWidth(), console.getHeight());
		int x = 20;
		int y = (int) currY;
		g.setColor(Color.lightGray);
		g.setFont(consoleFont);
		Utility.drawString(g, x, y, text);

		if ( boxes ) {
			g.setColor(Color.green);
			g.drawRect(0, 0, console.getWidth() - 1, fontHgt);
			g.drawRect(0, text.size() * fontHgt, console.getWidth() - 1, fontHgt - 1);
		} else {
			g.setColor(Color.darkGray);
			g.fillRect(0, 0, console.getWidth() - 1 , fontHgt - 1);
			g.fillRect(0, text.size() * fontHgt, console.getWidth() - 1, fontHgt - 1);
		}
		if (hidden > 0) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));

			// hide left side
			int xLeft = (int) (console.getWidth() * 0.5f * hidden);
			g.fillRect(0, 0, xLeft, console.getHeight());

			// hide the right side
			g.fillRect(console.getWidth() - xLeft, 0, xLeft,  console.getHeight());
		}


		g.dispose();
	}

	@Override
	protected void render(Graphics g) {
		super.render(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.drawImage(console, xConsole, yConsole, null);
	}

	public static void main(String[] args) {
		launchApp(new ConsoleOverlayExample());
	}
}