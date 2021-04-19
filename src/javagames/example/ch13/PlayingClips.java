
package javagames.example.ch13;

import javagames.util.ResourceLoader;

import javax.sound.sampled.*;
import java.io.*;

public class PlayingClips {

	private volatile boolean open;
	private volatile boolean started;

	private LineListener lineListener;

	public PlayingClips() {
		lineListener = event -> listen(event);
	}

	private byte[] readBytes(InputStream in) {
		BufferedInputStream buff = new BufferedInputStream(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int read;
		try {
			while ((read = buff.read()) != -1 ) {
				out.write(read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public void playClipsWithWaiting() {
		try {
			Clip clip = AudioSystem.getClip();
			InputStream in = ResourceLoader.load(
					PlayingClips.class, "res/assets/sound/WEAPON_scifi_fire_02.wav", "notneeded"
			);

			byte[] rawBytes = readBytes(in);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(rawBytes);
			AudioInputStream audioIS =  AudioSystem.getAudioInputStream(inputStream);
			clip.addLineListener(this::listen);

			synchronized (this) {
				clip.open(audioIS);
				while (!open) {
					wait();
				}
			}

			for (int i = 0; i < 10; i++) {

				clip.setFramePosition(0);
				synchronized (this) {
					clip.start();
					while (!started) {
						wait();
					}
				}

				clip.drain();

				synchronized (this) {
					clip.stop();
					while(started) {
						wait();
					}
				}
			}

			synchronized (this) {
				clip.close();
				while (open) {
					wait();
				}
			}



		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void playClipsWithoutWaiting() {

		try {
			Clip clip = AudioSystem.getClip();
			InputStream in = ResourceLoader.load(
					PlayingClips.class, "res/assets/sound/WEAPON_scifi_fire_02.wav", "notneeded"
			);

			byte[] rawBytes = readBytes(in);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(rawBytes);
			AudioInputStream audioIS =  AudioSystem.getAudioInputStream(inputStream);
			clip.addLineListener(this::listen);

			clip.open(audioIS);

			for (int i = 0; i < 10; i++ ) {
				clip.start();
				while (!clip.isActive()) {
					Thread.sleep(50);
				}
				clip.stop();
				clip.flush();
				clip.setFramePosition(0);
				clip.start();
				clip.drain();
			}

			clip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public synchronized void listen(LineEvent event) {
		LineEvent.Type type = event.getType();
		System.out.println("Got event: " + type);
		if (type == LineEvent.Type.OPEN) {
			open = true;
		} else if (type == LineEvent.Type.CLOSE) {
			open = false;
		} else  if (type == LineEvent.Type.START) {
			started = true;
		} else if(type == LineEvent.Type.STOP) {
			started = false;
		}
		notifyAll();
	}

	public static void main(String[] args) {
		PlayingClips pc = new PlayingClips();
		pc.playClipsWithoutWaiting();
		pc.playClipsWithWaiting();
	}
}