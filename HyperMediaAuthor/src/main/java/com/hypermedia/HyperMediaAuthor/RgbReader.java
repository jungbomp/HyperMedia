package com.hypermedia.HyperMediaAuthor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.opencv_core.*;

public class RgbReader {

    // Default frame width QGA
    private static final int DEFAULT_FRAME_WIDTH = 352;
    private static final int DEFAULT_FRAME_HEIGHT = 288;

    private String pathName;
    private int nbCurFrame;
    private ArrayList<String> frameNames = null;
    private Mat curFrame;

    private int width; // Frame width
    private int height; // Frame height

    public RgbReader(String pathName, int width, int height) throws Exception {
        this.pathName = pathName;
        this.nbCurFrame = -1;
        this.width = width;
        this.height = height;

        File f = new File(pathName);
        if (!f.isDirectory()) {
            System.out.println(pathName + " is not directory.");
            throw new Exception(pathName + " is not directory. The input path should be a directory.");
        }

        // Retrieve .rgb files from
        String[] fileList = f.list();
        frameNames = new ArrayList<String>();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].endsWith(".rgb")) {
                frameNames.add(fileList[i]);
            }
        }

        if (frameNames.size() < 1) {
            throw new Exception("Can't find .rgb file in '" + pathName + "'.");
        }

        Collections.sort(frameNames);
    }

    public int getTotalFrameCnt() {
        return frameNames.size();
    }

    public int getCurFrameNum() {
        return nbCurFrame;
    }

    public RgbReader(String pathName) throws Exception {
        this(pathName, DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
    }

    // Retrieve Mat of frame by number of frame
    public Mat getFrame(int nbFrame) throws Exception {
        if (nbFrame == this.nbCurFrame)
            return curFrame.clone();

        Mat rgbImage = new Mat(height, width, CV_8UC3); // Generate 3 channels matrix

        File file = null;
        FileInputStream fis = null;
        try {
            file = new File(pathName + "/" + frameNames.get(nbFrame));
            fis = new FileInputStream(file);
            byte[] buf = new byte[(int) file.length()];
            fis.read(buf);
            fis.close();
            byte[] imgBuffer = new byte[buf.length];

            int R = 0, G = width * height, B = width * height * 2;
            for (int i = 0; i < width * height; i++) {
                imgBuffer[i * 3 + 0] = buf[B++];
                imgBuffer[i * 3 + 1] = buf[G++];
                imgBuffer[i * 3 + 2] = buf[R++];
            }

            BytePointer ptr = rgbImage.data();
            ptr.put(imgBuffer);

            curFrame = rgbImage.clone();
            nbCurFrame = nbFrame;

        } catch (FileNotFoundException e) {
            System.out.println("Can't find file '" + frameNames.get(nbFrame) + "'.");
            throw e;
        } finally {
            fis.close();
        }

        return rgbImage;
    }

    public static void main(String[] args) throws Exception {
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma());

        try {
            RgbReader loader = new RgbReader("../AIFilm/AIFilmOne");
            Mat image = loader.getFrame(10);
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

            Frame rotatedFrame = converter.convert(image);
            frame.showImage(rotatedFrame);
        } catch (Exception e) {

        }
    }
}
