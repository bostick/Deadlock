package com.gutabi.deadlock.core.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Point;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public Graph graph = new Graph();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	
	public void clear() {
		graph = new Graph();
	}
	
//	public void clear_M() {
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				clear();
//			}
//		});
//	}
	
}
