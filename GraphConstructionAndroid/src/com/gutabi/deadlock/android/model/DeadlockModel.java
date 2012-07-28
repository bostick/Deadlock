package com.gutabi.deadlock.android.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Point;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public static final int GRID_WIDTH = 480;
	public static final int GRID_HEIGHT = 820;
	public static final int GRID_DELTA = 30;
	
	public Graph graph = new Graph();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	
//	private List<PointF> pointsToBeProcessed = new ArrayList<PointF>();
//	
//	public List<PointF> getPointsToBeProcessed() {
//		return pointsToBeProcessed;
//	}
	
}
