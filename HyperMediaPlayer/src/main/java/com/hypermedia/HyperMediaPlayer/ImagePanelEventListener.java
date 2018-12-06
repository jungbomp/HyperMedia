package com.hypermedia.HyperMediaPlayer;

import java.awt.image.BufferedImage;
import java.util.EventListener;

public interface ImagePanelEventListener extends EventListener {

    /**
     * Invoked when the panel draw a image onto a panel
     * but the image hasn't drawn yet.
     * @param image the image to be drawn
     */
    public void beforeDrawImage(BufferedImage image);

}