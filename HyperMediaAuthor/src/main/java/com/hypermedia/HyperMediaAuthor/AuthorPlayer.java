package com.hypermedia.HyperMediaAuthor;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect2d;

public class AuthorPlayer extends ImagePlayer implements MouseMotionListener, ImagePanelEventListener {

    private Point pt;
    private Rectangle rect;

    private Boolean bDragged;
    
    private AuthorPlayerEventListener listener;

    private HashMap<Integer, Rectangle> hmTracking;
    
    /**
	 * Create the panel.
	 */
	public AuthorPlayer() {
        super();

        pt = new Point();
        rect = new Rectangle();

        Initialize();
    }

    @Override
    public void Initialize() {
        super.Initialize();

        setMouseMotionListener(this);
        getPanel().setImagePanelEventListener(this);

        bDragged = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("Mouse Moved"
            + " (" + e.getX() + "," + e.getY() + ")"
            + " detected on "
            + e.getComponent().getClass().getName());
        
        if ((ImagePanel)e.getSource() == getPanel() && bDragged) {
            mouseDragged(e);
            bDragged = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("Mouse Dragged"
            + " (" + e.getX() + "," + e.getY() + ")"
            + " detected on "
            + e.getComponent().getClass().getName());
        
        if ((ImagePanel)e.getSource() == getPanel()) {
            if (!isLoaded()) return;

            try {
            	do {
	            	if (null != hmTracking && hmTracking.containsKey(getCurFrameNum())) {
	            		Rectangle tRect = hmTracking.get(getCurFrameNum());
	            		if (tRect.x <= e.getX() && e.getX() <= tRect.x + tRect.width && tRect.y <= e.getY() && e.getY() <= tRect.y + tRect.height) {
	            			tRect.x += (e.getX() - (tRect.x + tRect.width / 2));
	            			tRect.y += (e.getY() - (tRect.y + tRect.height / 2));
	            			
	            			continue;
	            		}
	            	}
	            	
	                Point ptFrom = pt;
	                Point ptTo = new Point(e.getX(), e.getY());
	                
	                if (!bDragged) {
	                    pt.setLocation(e.getX(), e.getY());
	                    bDragged = true;
	                }
	
	                if (ptFrom.x < ptTo.x) {
	                    if (ptFrom.y < ptTo.y) {
	                        rect.setLocation(ptFrom);
	                        rect.setSize(ptTo.x - ptFrom.x, ptTo.y - ptFrom.y);
	                    } else {
	                        rect.setLocation(ptFrom.x, ptTo.y);
	                        rect.setSize(ptTo.x - ptFrom.x, ptFrom.y - ptTo.y);
	                    }
	                } else {
	                    if (ptFrom.y < ptTo.y) {
	                        rect.setLocation(ptTo.x, ptFrom.y);
	                        rect.setSize(ptFrom.x - ptTo.x, ptTo.y - ptFrom.y);
	                    } else {
	                        rect.setLocation(ptTo);
	                        rect.setSize(ptFrom.x - ptTo.x, ptFrom.y - ptTo.y);
	                    }
	                }
            	} while (false);

                updateImage();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void beforeDrawImage(BufferedImage image) {
        if (null != hmTracking && hmTracking.containsKey(getCurFrameNum())) {
            drawRectangle(image, hmTracking.get(getCurFrameNum()), Color.MAGENTA);
        }
        
        drawRectangle(image, rect, Color.cyan);
        
        if (null != listener) {
        	listener.frameChanged(getCurFrameNum(), image);
        }
    }
    
    public void setAuthorPlayerEventListener(AuthorPlayerEventListener listener) {
    	this.listener = listener; 
    }
    
    public Rectangle getDraggedRectangle() {
        return rect;
    }
    
    public BufferedImage drawRectangle(BufferedImage image, Rectangle rect, Color color) {
        if (0 == rect.getWidth() || 0 == rect.getHeight()) return image;

        Graphics graph = image.getGraphics();
        graph.setColor(color);
        graph.drawRect(rect.x, rect.y, rect.width, rect.height);
        graph.dispose();

        return image;
    }

    public void setTrackingFrames(HashMap<Integer, Rectangle> hm) {
        this.hmTracking = hm;
    }

    public HashMap<Integer, Rectangle> trackMotion(final Rectangle rect, final int nbFrameFrom, int nbFrameTo) {
        int nbCurFrame = getCurFrameNum();
        HashMap<Integer, Rectangle> hm = trackMotion(rect, nbCurFrame, nbFrameTo, true);
        hm.putAll(trackMotion(rect, nbCurFrame, nbFrameFrom, false));
        hm.put(nbCurFrame, new Rectangle(rect));

        this.rect.width = this.rect.height = 0;

        return hm;
    }

    public HashMap<Integer, Rectangle> trackMotion(final Rectangle rect, final int nbCurFrame, int nbFrameTo, boolean bForward) {
        HashMap<Integer, Rectangle> hm = new HashMap<Integer, Rectangle>();
        
        MotionTracker tracker = new MotionTracker();
        FrameController frameCtl = getFrameController();
        Rect2d boundingBox = new Rect2d(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

        try {
            Mat mat = frameCtl.getFrameMat(nbCurFrame);

            tracker.createTracker(MotionTracker.TRACKER_MEDIANFLOW);
            tracker.initialize(mat, boundingBox);

            if (bForward) {
                for (int i = nbCurFrame + 1; i <= nbFrameTo; i++) {
                    mat = frameCtl.getFrameMat(i);
                    if (!tracker.updateTracker(mat, boundingBox)) {
                        break;
                    }
    
                    Rectangle rt = new Rectangle((int)boundingBox.x(), (int)boundingBox.y(), (int)boundingBox.width(), (int)boundingBox.height());
                    hm.put(i, rt);
                }
            } else {
                for (int i = nbCurFrame - 1; nbFrameTo <= i; i--) {
                    mat = frameCtl.getFrameMat(i);
                    if (!tracker.updateTracker(mat, boundingBox)) {
                        break;
                    }
    
                    Rectangle rt = new Rectangle((int)boundingBox.x(), (int)boundingBox.y(), (int)boundingBox.width(), (int)boundingBox.height());
                    hm.put(i, rt);
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            tracker.closeTracker();
        }

        return hm;
    }
}
