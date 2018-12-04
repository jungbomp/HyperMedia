package com.hypermedia.HyperMediaAuthor;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.EventListener;

public interface AuthorPlayerEventListener extends EventListener {
	
	/**
     * Invoked when the mouse dragged
     * @param rect the Rectangle of dragged area
     */
    public void mouseDragged(Rectangle rect);
    
    /**
     * Invoked when the panel draw a image onto a panel
     * but the image hasn't drawn yet.
     * @param nbFrame A number of current frame
     * @param image the image to be drawn
     */
    public void frameChanged(int nbFrame, BufferedImage img);
}
