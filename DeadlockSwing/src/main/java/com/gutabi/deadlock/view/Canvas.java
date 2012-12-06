package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

//@SuppressWarnings("serial")
@SuppressWarnings({"serial", "static-access"})
public class Canvas extends java.awt.Canvas {
	
	BufferStrategy bs;
	
	public Canvas() {
		
		setSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		setPreferredSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		setMaximumSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		
	}
	
	public void postDisplay() {
		createBufferStrategy(2);
		bs = getBufferStrategy();
	}
	
	Point lastPressCanvasPoint;
	long lastPressTime;
	
	Point lastDragCanvasPoint;
	Point lastDragWorldPoint;
	long lastDragTime;
	
	public void pressed(InputEvent ev) {
		
		requestFocusInWindow();
		
		Point p = ev.p;
		
		lastPressCanvasPoint = p;
		lastPressTime = System.currentTimeMillis();
		
		lastDragCanvasPoint = null;
		lastDragTime = -1;
		
	}
	
	public void dragged(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		boolean lastDragCanvasPointWasNull = (lastDragCanvasPoint == null);
		
		Point p = ev.p;
		
		lastDragCanvasPoint = p;
		lastDragWorldPoint = VIEW.canvasToWorld(p);
		lastDragTime = System.currentTimeMillis();
		
		MODEL.cursor.setPoint(lastDragWorldPoint);
		
		switch (CONTROLLER.mode) {
		case IDLE: {
			
			if (lastDragCanvasPointWasNull) {
				// first drag
				CONTROLLER.draftStart(lastPressCanvasPoint);
				CONTROLLER.draftMove(lastDragCanvasPoint);
				
				VIEW.repaintCanvas();
				
			} else {
				assert false;
			}
			
			break;
		}
		case DRAFTING:
			
			CONTROLLER.draftMove(lastDragCanvasPoint);
			
			VIEW.repaintCanvas();
			break;
			
		case RUNNING:
		case PAUSED:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case MENU:
			;
			break;
		}
		
	}
	
	long lastReleaseTime;
	
	public void released(InputEvent ev) {
		
		requestFocusInWindow();
		
		lastReleaseTime = System.currentTimeMillis();
		
		switch (CONTROLLER.mode) {
		case IDLE: {
			
			if (lastReleaseTime - lastPressTime < 500 && lastDragCanvasPoint == null) {
				// click
				
			}
			
			break;
		}
		case DRAFTING:
			CONTROLLER.draftEnd();
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case RUNNING:
		case PAUSED:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case MENU:
			;
			break;
		}
		
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedWorldPoint;
	
	public void moved(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		lastMovedCanvasPoint = ev.p;
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case PAUSED:
			
			lastMovedWorldPoint = VIEW.canvasToWorld(lastMovedCanvasPoint);
			
			break;
		case DRAFTING:
			
			assert false;
			lastMovedWorldPoint = VIEW.canvasToWorld(lastMovedCanvasPoint);
			
			break;
		case IDLE:
		case RUNNING:
			
			lastMovedWorldPoint = VIEW.canvasToWorld(lastMovedCanvasPoint);
			
			Entity closest = MODEL.world.hitTest(lastMovedWorldPoint);
			
			synchronized (MODEL) {
				MODEL.hilited = closest;
			}
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
			}
			
			VIEW.repaintCanvas();
			break;
			
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			
			lastMovedWorldPoint = VIEW.canvasToWorld(lastMovedCanvasPoint);
			
			MODEL.hilited = null;
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
			}
			
			VIEW.repaintCanvas();
			break;
		}
		
	}
	
	public void entered(InputEvent ev) {
		
		Point p = ev.p;
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
			
		case PAUSED:
		case DRAFTING:
		case RUNNING:
			
			lastMovedWorldPoint = VIEW.canvasToWorld(p);
			
			break;
		case IDLE:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			
			lastMovedWorldPoint = VIEW.canvasToWorld(p);
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
			}
			
			VIEW.repaintCanvas();
			break;
		}
		
	}
	
	public void exited(InputEvent ev) {
		
		switch (CONTROLLER.mode) {
		case PAUSED:
		case DRAFTING:
		case RUNNING:
		case MENU:
			break;
		case IDLE:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			
			if (MODEL.cursor != null) {
				MODEL.cursor.setPoint(null);
			}
			
			VIEW.repaintCanvas();
			break;
		}
		
	}
	
	public void paint(Graphics g) {
		
		bs.show();
	}
	
}
