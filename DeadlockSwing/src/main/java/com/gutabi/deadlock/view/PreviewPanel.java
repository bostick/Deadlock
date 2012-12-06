package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;

@SuppressWarnings({"serial", "static-access"})
//@SuppressWarnings({"serial"})
public class PreviewPanel extends JPanel {
	
	public PreviewPanel() {
		setSize(new Dimension(VIEW.PREVIEW_WIDTH, VIEW.PREVIEW_HEIGHT));
		setPreferredSize(new Dimension(VIEW.PREVIEW_WIDTH, VIEW.PREVIEW_HEIGHT));
		setMaximumSize(new Dimension(VIEW.PREVIEW_WIDTH, VIEW.PREVIEW_HEIGHT));
	}
	
	Point lastPressPreviewPoint;
	Point lastDragPreviewPoint;
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
		
		Point penDragPreviewPoint = lastDragPreviewPoint;
		lastDragPreviewPoint = p;
		lastDragTime = System.currentTimeMillis();
		
		if (penDragPreviewPoint != null) {
			
			double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
			double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
			
			VIEW.pan(new Point(dx, dy));
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
		}
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (VIEW.previewImage != null) {
			
			g.drawImage(VIEW.previewImage, 0, 0, null);
			
			Point prevLoc = VIEW.worldToPreview(VIEW.worldViewport.ul);
			
			Point prevDim = VIEW.worldToPreview(new Point(VIEW.worldViewport.width, VIEW.worldViewport.height));
			
			g.setColor(Color.BLUE);
			g.drawRect(
					(int)prevLoc.x, (int)prevLoc.y, (int)prevDim.x, (int)prevDim.y);
			
		}
		
	}

}