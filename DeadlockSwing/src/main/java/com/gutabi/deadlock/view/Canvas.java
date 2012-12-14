package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;

@SuppressWarnings("serial")
//@SuppressWarnings({"serial", "static-access"})
public class Canvas extends java.awt.Canvas {
	
	public BufferStrategy bs;
	
	public Canvas() {
		
	}
	
	public void postDisplay() {
		
		createBufferStrategy(2);
		bs = getBufferStrategy();
		
		APP.screen.canvasPostDisplay();
	}
	
	public void pressed(InputEvent ev) {
		
		requestFocusInWindow();
		
		APP.screen.pressed(ev);
	}
	
	public void dragged(InputEvent ev) {
		
		requestFocusInWindow();
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		APP.screen.dragged(ev);
	}
	
	public void released(InputEvent ev) {
		
		requestFocusInWindow();
		
		APP.screen.released(ev);
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	
	public void moved(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		APP.screen.moved(ev);
	}
	
	Point lastClickedCanvasPoint;
	
	public void clicked(InputEvent ev) {
		
		requestFocusInWindow();
		
		APP.screen.clicked(ev);
	}
	
	public void exited(InputEvent ev) {
		APP.screen.exited(ev);
	}
	
	public void paint(Graphics g) {
		APP.screen.paint(new PaintEvent(this, new RenderingContext((Graphics2D)g, RenderingContextType.CANVAS)));
	}
	
}
