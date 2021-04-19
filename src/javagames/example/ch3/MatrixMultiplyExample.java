package javagames.example.ch3;


import javagames.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class MatrixMultiplyExample extends JFrame implements Runnable {

	private volatile boolean running;
	private Thread gameThread;
	private BufferStrategy bs;
	private RelativeMouseInput mouse;
	private KeyboardInput keyboard;
	private FrameRate frameRate;
	private Canvas canvas;

	private int[] stars;
	private boolean drawStars;

	private float earthRot, earthDelta;
	private float moonRot, moonDelta;


	public void createAndShowGUI() {
		canvas = new Canvas();
		canvas.setBackground(Color.black);
		canvas.setSize(600, 500);
		canvas.setIgnoreRepaint(true);
		setIgnoreRepaint(true);
		getContentPane().add(canvas);
		pack();
		setTitle("Animation");
		setVisible(true);
		canvas.requestFocus();

		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();

		// add mouse listener
		mouse = new RelativeMouseInput(canvas);
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);
		canvas.addMouseWheelListener(mouse);

		// add keyboard listener
		keyboard = new KeyboardInput();
		canvas.addKeyListener(keyboard);

		frameRate  = new FrameRate();

		// create and start gameThread
		gameThread = new Thread(this);
		gameThread.setName("game thread");
		gameThread.start();
	}

	private void sleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		stars = new int[1000];
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < stars.length; i += 2) {
			stars[i] = rand.nextInt(canvas.getWidth());
			stars[i + 1] = rand.nextInt(canvas.getHeight());
		}
		drawStars = true;
		earthDelta = (float) Math.toRadians(0.5f);
		moonDelta = (float) Math.toRadians(2.5f);
	}

	@Override
	public void run() {
		running = true;
		frameRate.initialize();
		init();
		while (running) {
			processInput();
			gameUpdate();
			render();
			sleep();
		}
	}

	private void processInput() {
		keyboard.poll();
		mouse.poll();
	}

	private void gameUpdate() {
		if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
			drawStars = !drawStars;
		}
	}

	private void render() {
		do {
			do {
				Graphics g = null;
				try {
					g = bs.getDrawGraphics();
					g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					renderFrame(g);
				} finally {
					if (g != null) {
						g.dispose();
					}
				}
			} while (bs.contentsRestored());
			bs.show();
		} while (bs.contentsLost());
	}

	private void renderFrame(Graphics g) {

		// draw frameRate
		g.setColor(Color.green);
		frameRate.calculate();
		g.drawString(frameRate.getFps(), 20, 20);

		if (drawStars) {
			g.setColor(Color.white);
			for (int i = 0; i < stars.length; i += 2) {
				g.fillRect(stars[i], stars[i + 1], 1,1);
			}
		}

		//draw the sun
		Matrix3x3f sunMat = Matrix3x3f.identity();
		sunMat = sunMat.mul(Matrix3x3f.translate((float) canvas.getWidth()/2, (float) canvas.getHeight()/2));
		Vector2f sun = sunMat.mul(new Vector2f());
		g.setColor(Color.yellow);
		g.fillOval((int) sun.x - 50, (int) sun.y - 50, 100, 100 );

		// draw the earth orbit
		g.setColor(Color.white);
		g.drawOval((int)sun.x - canvas.getWidth()/4, (int)sun.y - canvas.getWidth()/4, canvas.getWidth()/2, canvas.getWidth()/2);

		// draw the earth
		Matrix3x3f earthMat = Matrix3x3f.translate(canvas.getWidth()/4, 0);
		earthMat = earthMat.mul(Matrix3x3f.rotate(earthRot));
		earthMat = earthMat.mul(sunMat);
		Vector2f earth = earthMat.mul(new Vector2f());

		g.setColor(Color.blue);
		g.fillOval((int)earth.x - 10, (int)earth.y - 10, 20, 20);

		earthRot += earthDelta;

		// draw the moon
		Matrix3x3f moonMat = Matrix3x3f.translate(30, 0);
		moonMat = moonMat.mul(Matrix3x3f.rotate(moonRot));
		moonMat = moonMat.mul(earthMat);
		Vector2f moon = moonMat.mul(new Vector2f());

		g.setColor(Color.lightGray);
		g.fillOval((int)moon.x - 5, (int)moon.y - 5,10, 10 );

		moonRot += moonDelta;
	}

	public void shutDown() {
		running = false;
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.exit(0);
	}

	public static void main(String[] args) {
		MatrixMultiplyExample app = new MatrixMultiplyExample();
		app.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				app.shutDown();
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				app.createAndShowGUI();
			}
		});
	}
}