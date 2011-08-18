package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

public class DeadlockModel {
	
	public static final int GRID_WIDTH = 320;
	public static final int GRID_HEIGHT = 480;
	public static final int GRID_DELTA = 30;
	//public static final int GRID_DELTAY = 30;
//	public static final float GRID_FACTOR = 1.0f;
//	public static final float GRID_RADIUSX = GRID_FACTOR * GRID_DELTAX;
//	public static final float GRID_RADIUSY = GRID_FACTOR * GRID_DELTAY;
	
	//private DeadlockActivity act;
	//private TextView status;
	
	public List<List<PointF>> roads;
	
	public List<PointF> curRoad;
	public PointF curPoint;
	//public PointF curRoundedPoint;
	public double totalDist = 0.0;
	
	//public double curDist = 0.0;
	
	public DeadlockModel() {
		
		//this.act = act;
		//status = act.status;
		
		roads = new ArrayList<List<PointF>>();
		
	}
	
//	public void setStatusText(String text) { 
//		act.status.setText(text);
//	}
	
//	public static PointF roundToGrid(PointF point) {
//		
//		float x = point.x;
//		float y = point.y;
//		
//		x = Math.round(x/GRID_DELTA)*GRID_DELTA;
//		y = Math.round(y/GRID_DELTA)*GRID_DELTA;
//		
//		return new PointF(x, y);
//		
//	}
	
}
