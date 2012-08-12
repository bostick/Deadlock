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
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(new Point(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		dragged(new Point(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released();
	}
	
	public void pressed(final Point p) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.pressed(p);
				VIEW.repaint();
			}}
		);
	}
	
	public void dragged(final Point p) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.dragged(p);
				VIEW.repaint();
			}}
		);
	}
	
	public void released() {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.released();
				VIEW.repaint();
			}}
		);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		;
	}
	
}