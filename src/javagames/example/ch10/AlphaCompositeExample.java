package javagames.example.ch10;

import javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.HashMap;

public class AlphaCompositeExample extends SimpleFramework {


	private BufferedImage srcImage;
	private BufferedImage destImage;
	private BufferedImage sprite;

	private float srcAlpha;
	private float destAlpha;
	private float extAlpha;

	private int ruleSelectedIndex;

	private String[] ruleName = new String[] {
			"SRC", "DST", "SRC_IN", "DST_IN", "SRC_OUT", "DST_OUT", "SRC_OVER", "DST_OVER"
			, "SRC_ATOP", "DST_ATOP", "XOR", "CLEAR"
	};

	private int ruleSet[] = new int[] {
			AlphaComposite.SRC, AlphaComposite.DST, AlphaComposite.SRC_IN, AlphaComposite.DST_IN,
			AlphaComposite.SRC_OUT, AlphaComposite.DST_OUT, AlphaComposite.SRC_OVER, AlphaComposite.DST_OVER,
			AlphaComposite.SRC_ATOP, AlphaComposite.DST_ATOP, AlphaComposite.XOR, AlphaComposite.CLEAR
	};


	HashMap<String, Integer> rulmap = new HashMap<>();


	AlphaCompositeExample() {
		appTitle  = "Alpha Composite Rule";
		appBackground = Color.darkGray;
		appWidth = 800;
		appHeight = 600;
		appFont = new Font("Courier", Font.BOLD, 15);
	}


	@Override
	protected void initialize() {
		super.initialize();

		srcAlpha = 1.0f;
		destAlpha = 1.0f;
		extAlpha = 1.0f;

		srcImage = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
		destImage = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
		sprite = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < ruleName.length; i++) {
			String key = ruleName[i];
			int value = ruleSet[i];
			rulmap.put(key, value);
		}

		createImages();
	}

	private void createImages() {
		// source Image
		Graphics2D g = srcImage.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g.fillRect(0, 0, srcImage.getWidth(), srcImage.getHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

		Polygon p = new Polygon();
		p.addPoint(0, 0);
		p.addPoint(srcImage.getWidth(), 0);
		p.addPoint(srcImage.getWidth(), srcImage.getHeight()/2);

		g.setColor(new Color(1.0f, 1.0f, 0, srcAlpha));
		g.fill(p);
		g.dispose();

		// destination Image
		g = destImage.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g.fillRect(0, 0, destImage.getWidth(), destImage.getHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));


		p = new Polygon();
		p.addPoint(0, 0);
		p.addPoint(destImage.getWidth(), 0);
		p.addPoint(0, destImage.getHeight() / 2);

		g.setColor(new Color(0, 0, 1.0f, destAlpha));
		g.fill(p);

		int alphaRule =  rulmap.get(ruleName[ruleSelectedIndex]);
		g.setComposite(AlphaComposite.getInstance(alphaRule, extAlpha));
		g.drawImage(srcImage, 0, 0, null);
		g.dispose();

		// sprite
		g = sprite.createGraphics();
		g.fillRect(0,0, sprite.getWidth(), sprite.getHeight());
		int dx = sprite.getWidth()/ 8;
		int dy = sprite.getHeight()/ 8;
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if ((row + col) % 2 == 0) {
					g.setColor(Color.black);
					g.fillRect(col * dx, row * dy, dx, dy);
				}
			}
		}

		g.drawImage(destImage, 0, 0, null);
		g.dispose();

	}



	@Override
	protected void processInput(float t) {
		super.processInput(t);

		if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
			ruleSelectedIndex ++;
			if (ruleSelectedIndex > ruleName.length - 1 )
				ruleSelectedIndex = 0;
		}

		if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
			ruleSelectedIndex --;
			if (ruleSelectedIndex < 0) {
				ruleSelectedIndex = ruleName.length - 1;
			}
		}

		if (keyboard.keyDown(KeyEvent.VK_Q)) {
				srcAlpha += 0.002222;
				if (srcAlpha > 1.0f)
					srcAlpha = 1.0f;
		}
		if (keyboard.keyDown(KeyEvent.VK_A)) {
				srcAlpha -= 0.002222;
				if (srcAlpha < 0)
					srcAlpha = 0;
		}

		if (keyboard.keyDown(KeyEvent.VK_W)) {
			destAlpha += 0.002222;
			if (destAlpha > 1.0f)
				destAlpha = 1.0f;
		}
		if (keyboard.keyDown(KeyEvent.VK_S)) {
			destAlpha -= 0.002222;
			if (destAlpha < 0)
				destAlpha = 0;
		}

		if (keyboard.keyDown(KeyEvent.VK_E)) {
			extAlpha += 0.002222;
			if (extAlpha > 1.0f)
				extAlpha = 1.0f;
		}
		if (keyboard.keyDown(KeyEvent.VK_D)) {
			extAlpha -= 0.002222;
			if (extAlpha < 0)
				extAlpha = 0;
		}


		createImages();

	}

	@Override
	protected void render(Graphics g) {
		super.render(g);
		 Graphics2D g2d = (Graphics2D) g;
		 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int xPos = 20;
		int yGap = 30;
		int yPos = 50;
		for (int i = 0; i < ruleName.length; i++ ) {

			if (ruleSelectedIndex == i) {
				g2d.setColor(Color.RED);
			} else  {
				g2d.setColor(Color.white);
			}

			g2d.drawString(ruleName[i], xPos, yPos + yGap * i);
		}
		int x = canvas.getWidth() - (sprite.getWidth() + 100);
		int y = 100;
		g2d.drawImage(sprite, x,y, null );
	}

	public static void main(String[] args) {
		launchApp(new AlphaCompositeExample());
	}
}