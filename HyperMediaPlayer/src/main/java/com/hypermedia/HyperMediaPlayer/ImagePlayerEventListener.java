package com.hypermedia.HyperMediaPlayer;

import java.util.EventListener;

public interface ImagePlayerEventListener extends EventListener {
	
	/**
     * Invoked when the player has done playing
     */
    public void playDone();
}
    