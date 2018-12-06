package com.hypermedia.HyperMediaPlayer;

import java.awt.Rectangle;
import java.util.HashMap;

public class LinkContext {
	private String linkName;
	private String mediaName;
	private String pathName;
	
	private int startFrame;
	private int endFrame;
	private int curFrame;
	
	HashMap<Integer, HashMap<String, Rectangle>> linkFrames;
	HashMap<String, LinkInfoVO> links;
	
	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	public String getMediaName() {
		return mediaName;
	}
	
	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}
	
	public String getPathName() {
		return pathName;
	}
	
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	
	public int getStartFrame() {
		return startFrame;
	}
	
	public void setStartFrame(int startFrame) {
		this.startFrame = startFrame;
	}
	
	public int getEndFrame() {
		return endFrame;
	}
	
	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}
	
	public int getCurFrame() {
		return curFrame;
	}
	
	public void setCurFrame(int curFrame) {
		this.curFrame = curFrame;
	}
	
	public HashMap<Integer, HashMap<String, Rectangle>> getLinkFrames() {
		return linkFrames;
	}

	public void setLinkFrames(HashMap<Integer, HashMap<String, Rectangle>> linkFrames) {
		this.linkFrames = linkFrames;
	}

	public HashMap<String, LinkInfoVO> getLinks() {
		return links;
	}

	public void setLinks(HashMap<String, LinkInfoVO> links) {
		this.links = links;
	}
}
