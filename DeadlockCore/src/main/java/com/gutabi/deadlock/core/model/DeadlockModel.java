package com.gutabi.deadlock.core.model;

import com.gutabi.core.Graph;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public Graph graph = new Graph();
	
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
