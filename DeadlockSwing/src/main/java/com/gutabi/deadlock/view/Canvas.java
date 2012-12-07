package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;

@SuppressWarnings("serial")
//@SuppressWarnings({"serial", "static-access"})
public class Canvas extends java.awt.Canvas {
	
	BufferStrategy bs;
	
	public Canvas() {
		
//		setSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
//		setPreferredSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
//		setMaximumSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		
	}
	
	public void postDisplay() {
		
		createBufferStrategy(2);
		bs = getBufferStrategy();
		
		switch (CONTROLLER.mode) {
		case WORLD:
			MODEL.world.postDisplay();
			break;
		case MENU:
			break;
		}
		
	}
	
//	Point lastPressCanvasPoint;
//	Point lastDragCanvasPoint;
	
	public void pressed(InputEvent ev) {
		
		requestFocusInWindow();
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case WORLD:
			MODEL.world.pressed(ev);
			break;
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		requestFocusInWindow();
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case WORLD:
			MODEL.world.dragged(ev);
			break;
		}
		
	}
	
	public void released(InputEvent ev) {
		
		requestFocusInWindow();
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case WORLD:
			MODEL.world.released(ev);
			break;
		}
		
	}
	
	public Point lastMovedCanvasPoint;
	
	public void moved(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		lastMovedCanvasPoint = ev.p;
		
		switch (CONTROLLER.mode) {
		case MENU:
			MODEL.menu.moved(ev);
			break;
		case WORLD:
			MODEL.world.moved(ev);
			break;
		}
		
	}
	
	Point lastClickedCanvasPoint;
	
	public void clicked(InputEvent ev) {
		
		requestFocusInWindow();
		
		switch (CONTROLLER.mode) {
		case MENU:
			MODEL.menu.clicked(ev);
			break;
		case WORLD:
			break;
		}
		
	}
	
	public void entered(InputEvent ev) {
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case WORLD:
			MODEL.world.entered(ev);
			break;
		}
		
	}
	
	public void exited(InputEvent ev) {
		
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case WORLD:
			MODEL.world.exited(ev);
			break;
		}
		
	}
	
	public void paint(Graphics g) {
		bs.show();
	}
	
}
