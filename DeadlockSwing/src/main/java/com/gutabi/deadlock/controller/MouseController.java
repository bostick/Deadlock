package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	public void init() {
		
		VIEW.canvas.addMouseListener(this);
		VIEW.canvas.addMouseMotionListener(this);
		
		VIEW.previewPanel.addMouseListener(this);
		VIEW.previewPanel.addMouseMotionListener(this);
		
	}
	
	public void mousePressed(MouseEvent ev) {
		pressed(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void mouseDragged(MouseEvent ev) {
		dragged(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void mouseReleased(MouseEvent ev) {
		released(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void mouseMoved(MouseEvent ev) {
		moved(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void mouseEntered(MouseEvent ev) {
		entered(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void mouseExited(MouseEvent ev) {
		exited(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void pressed(final InputEvent ev) {
		CONTROLLER.pressed(ev);
	}
	
	public void dragged(final InputEvent ev) {
		CONTROLLER.dragged(ev);
	}
	
	public void released(final InputEvent ev) {
		CONTROLLER.released(ev);
	}
	
	public void moved(final InputEvent ev) {
		CONTROLLER.moved(ev);
	}
	
	public void entered(final InputEvent ev) {
		CONTROLLER.entered(ev);
	}
	
	public void exited(final InputEvent ev) {
		CONTROLLER.exited(ev);
	}
	
	public void mouseClicked(MouseEvent arg0) {
		;
	}
	
}
