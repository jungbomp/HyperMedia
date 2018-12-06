package com.hypermedia.HyperMediaPlayer;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class LinkInfoVO {
    private String linkName;
	private String originPathName;
    private HashMap<Integer, Rectangle> frames;
    private String destinationPathName;
	private int destinationFrameFrom;
	
	public LinkInfoVO() {

	}
	
	public LinkInfoVO(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		linkName = json.get("linkName").getAsString();
		originPathName = json.get("oriPathName").getAsString();
		
		frames = new HashMap<Integer, Rectangle>();
		JsonArray oriFrames = json.get("oriFrames").getAsJsonArray();
		Iterator<JsonElement> iter = oriFrames.iterator();
		while (iter.hasNext()) {
			JsonElement frame = iter.next();
			JsonObject boxBounding = frame.getAsJsonObject();

			Rectangle rect = new Rectangle();
			rect.x = boxBounding.get("x").getAsInt();
			rect.y = boxBounding.get("y").getAsInt();
			rect.width = boxBounding.get("width").getAsInt();
			rect.height = boxBounding.get("height").getAsInt();

			frames.put(boxBounding.get("frameNum").getAsInt(), rect);
		}

		destinationPathName = json.get("destPathName").getAsString();
		destinationFrameFrom = json.get("destFrameNum").getAsInt();
	}

    public String getLinkName() {
		return linkName;
	}
    
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

    public String getOriginPathName() {
		return originPathName;
	}
    
	public void setOriginPathName(String originPathName) {
		this.originPathName = originPathName;
	}
	
	public HashMap<Integer, Rectangle> getFrame() {
		return frames;
	}
	
	public void setFrame(HashMap<Integer, Rectangle> frames) {
		this.frames = new HashMap<Integer, Rectangle>(frames);
	}
	
	public String getDestinationPathName() {
		return destinationPathName;
	}
	
	public void setDestinationPathName(String destinationPathName) {
		this.destinationPathName = destinationPathName;
	}
	
	public int getDestinationFrameFrom() {
		return destinationFrameFrom;
	}
	
	public void setDestinationFrameFrom(int destinationFrameFrom) {
		this.destinationFrameFrom = destinationFrameFrom;
	}
	
	public String toJson() {
		Gson gson = new Gson();
		JsonObject json = toJsonObject();

		return gson.toJson(json);
	}

	public JsonObject toJsonObject() {
		JsonObject object = new JsonObject();

		object.addProperty("linkName", getLinkName());
		object.addProperty("oriPathName", getOriginPathName());
		object.addProperty("frameCnt", frames.size());

		JsonArray oriFrames = new JsonArray();		
		ArrayList<Integer> keyList = new ArrayList<Integer>(frames.keySet());
		
		Collections.sort(keyList, new Comparator<Integer>() {
			public int compare(Integer i1, Integer i2) {
				return (i1 - i2);
			}
		});
		
		for (Integer key : keyList) {
			Rectangle rect = frames.get(key);

			JsonObject frame = new JsonObject();
			frame.addProperty("frameNum", key);
			frame.addProperty("x", rect.x);
			frame.addProperty("y", rect.y);
			frame.addProperty("width", rect.width);
			frame.addProperty("height", rect.height);
			oriFrames.add(frame);
		}

		object.add("oriFrames", oriFrames);

		object.addProperty("destPathName", getDestinationPathName());
		object.addProperty("destFrameNum", getDestinationFrameFrom());

		return object;
	}

	public static JsonObject toJsonObject(ArrayList<LinkInfoVO> infos) {
		JsonObject object = new JsonObject();

		object.addProperty("linkCnt", infos.size());

		JsonArray links = new JsonArray();
		for (LinkInfoVO info : infos) {
			links.add(info.toJsonObject());
		}

		object.add("links", links);

		return object;
	}

	public static String toJson(ArrayList<LinkInfoVO> infos) {
		Gson gson = new Gson();
		JsonObject json = toJsonObject(infos);

		return gson.toJson(json);

	}

	public static ArrayList<LinkInfoVO> toLinkInfoVO(JsonElement element) {
		ArrayList<LinkInfoVO> infos = new ArrayList<LinkInfoVO>();

		JsonArray links = element.getAsJsonObject().get("links").getAsJsonArray();
		Iterator<JsonElement> iter = links.iterator();
		while (iter.hasNext()) {
			JsonElement ele = iter.next();
			infos.add(new LinkInfoVO(ele));
		}

		return infos;
	}

	public static ArrayList<LinkInfoVO> toLinkInfoVO(String jsonStr) {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(jsonStr);

		return toLinkInfoVO(element);
	}
}