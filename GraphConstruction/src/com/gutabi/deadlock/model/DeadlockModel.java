package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

public class DeadlockModel {
	
	public static final int GRID_WIDTH = 480;
	public static final int GRID_HEIGHT = 820;
	public static final int GRID_DELTA = 30;
	//public static final int GRID_DELTAY = 30;
//	public static final float GRID_FACTOR = 1.0f;
//	public static final float GRID_RADIUSX = GRID_FACTOR * GRID_DELTAX;
//	public static final float GRID_RADIUSY = GRID_FACTOR * GRID_DELTAY;
	
	//private DeadlockActivity act;
	//private TextView status;
	
	public List<List<PointF>> roadSegments;
	public List<PointF> inters;
	
	public List<PointF> curSeg;
	public PointF curPoint;
	//public PointF curRoundedPoint;
	public double totalDist = 0.0;
	
	//public double curDist = 0.0;
	
	public DeadlockModel() {
		
		//this.act = act;
		//status = act.status;
		
		roadSegments = new ArrayList<List<PointF>>();
		inters = new ArrayList<PointF>();
		
	}
	
}
