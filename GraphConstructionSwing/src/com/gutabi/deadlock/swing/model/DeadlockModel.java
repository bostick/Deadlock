package com.gutabi.deadlock.swing.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Point;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public Graph graph = new Graph();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	public void init() {
		
	}
	
	public void clear() {
		//assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		graph = new Graph();
		allStrokes.clear();
	}
	
	public void clear_M() {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				clear();
			}
		});
	}
}
