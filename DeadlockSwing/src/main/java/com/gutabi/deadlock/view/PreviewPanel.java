package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.world.Preview;

//@SuppressWarnings({"serial", "static-access"})
@SuppressWarnings({"serial"})
public class PreviewPanel extends JPanel {
	
	public PreviewPanel() {
		setSize(new Dimension(Preview.PREVIEW_WIDTH, Preview.PREVIEW_HEIGHT));
		setPreferredSize(new Dimension(Preview.PREVIEW_WIDTH, Preview.PREVIEW_HEIGHT));
		setMaximumSize(new Dimension(Preview.PREVIEW_WIDTH, Preview.PREVIEW_HEIGHT));
	}
	
	public Point lastPressPreviewPoint;
	public Point lastDragPreviewPoint;
	public Point penDragPreviewPoint;
	long lastPressTime;
	long lastDragTime;
	
	public void pressed(InputEvent ev) {
		
		requestFocusInWindow();
		
		Point p = ev.p;
		
		lastPressPreviewPoint = p;
		lastPressTime = System.currentTimeMillis();
		
		lastDragPreviewPoint = null;
		lastDragTime = -1;
		
	}
	
	public void dragged(InputEvent ev) {
		
		VIEW.previewPanel.requestFocusInWindow();
		
		Point p = ev.p;
		
		penDragPreviewPoint = lastDragPreviewPoint;
		lastDragPreviewPoint = p;
		lastDragTime = System.currentTimeMillis();
		
		APP.preview.dragged(ev);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		APP.preview.paint((Graphics2D)g);
	}

}