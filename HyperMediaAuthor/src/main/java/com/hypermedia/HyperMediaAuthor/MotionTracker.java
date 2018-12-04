package com.hypermedia.HyperMediaAuthor;

import org.bytedeco.javacpp.opencv_core.Rect2d;
import org.bytedeco.javacpp.opencv_tracking.Tracker;
import org.bytedeco.javacpp.opencv_tracking.TrackerBoosting;
import org.bytedeco.javacpp.opencv_tracking.TrackerCSRT;
import org.bytedeco.javacpp.opencv_tracking.TrackerGOTURN;
import org.bytedeco.javacpp.opencv_tracking.TrackerKCF;
import org.bytedeco.javacpp.opencv_tracking.TrackerMIL;
import org.bytedeco.javacpp.opencv_tracking.TrackerMOSSE;
import org.bytedeco.javacpp.opencv_tracking.TrackerMedianFlow;
import org.bytedeco.javacpp.opencv_tracking.TrackerTLD;

import static org.bytedeco.javacpp.opencv_core.Mat;

public class MotionTracker {
    public static final int TRACKER_BOOSTING = 1;
    public static final int TRACKER_MIL = 2;
    public static final int TRACKER_KCF = 3;
    public static final int TRACKER_TLD = 4;
    public static final int TRACKER_MEDIANFLOW = 5;
    public static final int TRACKER_GOTURN = 6;
    public static final int TRACKER_MOSSE = 7;
    public static final int TRACKER_CSRT = 8;
	
    private Tracker tracker;
    private Boolean bInitialized;


	public MotionTracker() {
        tracker = null;
        bInitialized = false;
	}

	public MotionTracker(int trackerType) throws Exception {
        createTracker(trackerType);
	}

	public void createTracker(int trackerType) {

        switch (trackerType) {
            case TRACKER_BOOSTING:
                tracker = TrackerBoosting.create();
                break;
            
            case TRACKER_MIL:
                tracker = TrackerMIL.create();
                break;
            
            case TRACKER_KCF:
                tracker = TrackerKCF.create();
                break;

            case TRACKER_TLD:
                tracker = TrackerTLD.create();
                break;

            case TRACKER_MEDIANFLOW:
                tracker = TrackerMedianFlow.create();
                break;
            
            case TRACKER_GOTURN:
                tracker = TrackerGOTURN.create();
                break;

            case TRACKER_MOSSE:
                tracker = TrackerMOSSE.create();
                break;

            case TRACKER_CSRT:
                tracker = TrackerCSRT.create();
                break;
                
            default:
                tracker = null;
        }

        bInitialized = false;
    }

    public boolean initialize(Mat image, Rect2d boundingBox) throws Exception {
        if (null == tracker) throw new Exception("Tracker is not created.");

        bInitialized = tracker.init(image, boundingBox);
        return bInitialized;
    }

    public boolean updateTracker(Mat image, Rect2d boundingBox) throws Exception {
        if (null == tracker) throw new Exception("Tracker is not created.");
        if (!bInitialized) throw new Exception("Tracker is not initialized.");

        return tracker.update(image, boundingBox);
    }

    public void closeTracker() {
        tracker.close();
    }
}
