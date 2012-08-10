package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DPoint;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(new DPoint(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		dragged(new DPoint(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released();
	}
	
	public void pressed(final DPoint p) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.pressed(p);
				PLATFORMVIEW.repaint();
			}}
		);
	}
	
	public void dragged(final DPoint p) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.dragged(p);
				PLATFORMVIEW.repaint();
			}}
		);
	}
	
	public void released() {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.released();
				PLATFORMVIEW.repaint();
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
