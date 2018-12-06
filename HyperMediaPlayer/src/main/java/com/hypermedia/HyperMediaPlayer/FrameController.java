package com.hypermedia.HyperMediaPlayer;

import java.awt.image.BufferedImage;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacpp.opencv_imgproc;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.Size;

public class FrameController {

	private static final int DEFAULT_FRAME_WIDTH = 352;
    private static final int DEFAULT_FRAME_HEIGHT = 288;
    
    private String pathName;
    private RgbReader reader;
    
    private BufferedImage curImage;

	public FrameController() {
		this.pathName = null;
        reader = null;
        curImage = null;
    }

	public FrameController(String pathName) throws Exception {
        setPathName(pathName);
	}

	public void setPathName(String pathName) throws Exception{
		this.pathName = pathName;
        this.reader = new RgbReader(pathName, DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
        curImage = null;
	}

	public String getPathName() {
		return this.pathName;
    }
    
    public int getTotalFrameCnt() {
        return this.reader.getTotalFrameCnt();
    }

	public int getCurFrameNum() {
        return this.reader.getCurFrameNum();
    }

    public Mat getFrameMat(int nbFrame) throws Exception {
        return reader.getFrame(nbFrame);
    }

    public BufferedImage getFrameImage(int nbFrame) throws Exception {
        Mat mat = getFrameMat(nbFrame);
        curImage = Java2DFrameUtils.toBufferedImage(mat);
        
        return curImage;
    }

    public Mat getFrameMat(int nbFrame, int width, int height) throws Exception {
        Mat img = reader.getFrame(nbFrame);
        opencv_imgproc.resize(img, img, new Size(width, height));
        
        return img;
    }

    public BufferedImage getFrameImage(int nbFrame, int width, int height) throws Exception {
        Mat mat = getFrameMat(nbFrame, width, height);
        curImage = Java2DFrameUtils.toBufferedImage(mat);
        
        return curImage;
    }
}
