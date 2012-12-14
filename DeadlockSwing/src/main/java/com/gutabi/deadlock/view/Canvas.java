package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics;
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
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			APP.world.postDisplay();
			break;
		}
	}
	
	public void pressed(InputEvent ev) {
		
		requestFocusInWindow();
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			APP.world.pressed(ev);
			break;
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		requestFocusInWindow();
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			APP.world.dragged(ev);
			break;
		}
		
	}
	
	public void released(InputEvent ev) {
		
		requestFocusInWindow();
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			APP.world.released(ev);
			break;
		}
		
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	
	public void moved(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		switch (CONTROLLER.mode) {
		case MENU:
			APP.menu.moved(ev);
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			APP.world.moved(ev);
			break;
		}
		
	}
	
	Point lastClickedCanvasPoint;
	
	public void clicked(InputEvent ev) {
		
		requestFocusInWindow();
		
		switch (CONTROLLER.mode) {
		case MENU:
			APP.menu.clicked(ev);
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			break;
		}
		
	}
	
	public void exited(InputEvent ev) {
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			APP.world.exited(ev);
			break;
		}
		
	}
	
	public void paint(Graphics g) {
		bs.show();
	}
	
}
