package com.hypermedia.HyperMediaPlayer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An utility class for playing back audio files using Java Sound API. 
 *
 */
public class AudioController implements LineListener {
	private static final int SECONDS_IN_HOUR = 60 * 60;
	private static final int SECONDS_IN_MINUTE = 60;
	
	/**
	 * this flag indicates whether the playback completes or not.
	 */
	private boolean playCompleted;

	/**
	 * this flag indicates whether the playback is stopped or not.
	 */
	private boolean isStopped;

	private boolean isPaused;

	private Clip audioClip;

	/**
	 * Load audio file before playing back
	 * 
	 * @param audioFilePath
	 *            Path of the audio file.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public void load(String pathName)
			throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		
		if (null != audioClip && audioClip.isOpen()) audioClip.close();
        
        File f = new File(pathName);
        if (!f.isDirectory()) {
            System.err.println(pathName + " is not directory.");
            throw new IOException(pathName + " is not directory. The input path should be a directory.");
        }
        
        File[] files = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }
        });

        if (files.length < 1) {
            System.err.println("Can't find .rgb file in '" + pathName + "'.");
            throw new IOException("Can't find .rgb file in '" + pathName + "'.");
        }

        isStopped = true;
        isPaused = false;
        
        File audioFile = files[0];
        
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        
		audioClip = (Clip) AudioSystem.getLine(info);
		audioClip.addLineListener(this);
		audioClip.open(audioStream);
	}
	
	public long getClipMicrosecondLength() {
		return audioClip.getMicrosecondLength();
	}
	
	public long getClipSecondLength() {
		return getClipMicrosecondLength() / 1_000_000;
	}
	
	public String getClipLengthString() {
		String length = "";
		long hour = 0;
		long minute = 0;
		long seconds = getClipMicrosecondLength() / 1_000_000;
		
		System.out.println(seconds);
		
		if (seconds >= SECONDS_IN_HOUR) {
			hour = seconds / SECONDS_IN_HOUR;
			length = String.format("%02d:", hour);
		} else {
			length += "00:";
		}
		
		minute = seconds - hour * SECONDS_IN_HOUR;
		if (minute >= SECONDS_IN_MINUTE) {
			minute = minute / SECONDS_IN_MINUTE;
			length += String.format("%02d:", minute);
			
		} else {
			minute = 0;
			length += "00:";
		}
		
		long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;
		
		length += String.format("%02d", second);
		
		return length;
	}

	/**
	 * Play a given audio file.
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	void play() throws IOException {

		audioClip.start();

		playCompleted = false;
		isStopped = false;

		//while (!playCompleted) {
			// wait for the playback completes
		//	try {
		//		Thread.sleep(1000);
		//	} catch (InterruptedException ex) {
		//		ex.printStackTrace();
		//		if (isStopped) {
		//			audioClip.stop();
		//			break;
		//		}
		//		if (isPaused) {
		//			audioClip.stop();
		//		} else {
		//			System.out.println("!!!!");
		//			audioClip.start();
		//		}
		//	}
		//}

		//audioClip.close();

	}
	
	/*
	 * Play a given audio file.
	 * 
	 * @param startPoint A milliseconds  
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	void play(long milliseconds) throws IOException {
		if (!(isStopped || isPaused)) {
			audioClip.stop();
		}

        try {
            setMillisecondPosition(milliseconds);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

		play();
	}

	/**
	 * Stop playing back.
	 */
	public void stop() {
		isStopped = true;
	}

	public void pause() {
		isPaused = true;
		audioClip.stop();
	}

	public void resume() {
		isPaused = false;
		audioClip.start();
	}

	/**
	 * Listens to the audio line events to know when the playback completes.
	 */
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.STOP) {
			System.out.println("STOP EVENT");
			if (isStopped || !isPaused) {
				playCompleted = true;
			}
		}
	}
	
	public Clip getAudioClip() {
		return audioClip;
    }
    
    public boolean isPlaying() {
        return !(isStopped || isPaused);
    }

    public void setMillisecondPosition(long milliseconds) throws Exception {
        //if (isPlaying()) throw new Exception("Can't move position since Audio clip is running.");

        audioClip.setMicrosecondPosition(milliseconds * 1_000);
    }
}