package com.hypermedia.HyperMediaAuthor;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private BufferedImage image;

	private ImagePanelEventListener listener;
	
	public void setImage(BufferedImage image) {
		if (null != listener) {
			listener.beforeDrawImage(image);
		}

		this.image = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (null != image) {
			g.drawImage(image, 0, 0, this);
		}
	}

	public void setImagePanelEventListener(ImagePanelEventListener listener) {
		this.listener = listener;
	}

	public void removeImagePanelEventListener() {
		listener = null;
	}
}
