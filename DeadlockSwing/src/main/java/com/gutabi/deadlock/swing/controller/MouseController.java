package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.gutabi.core.DPoint;
import com.gutabi.deadlock.core.controller.ControlMode;
import com.gutabi.deadlock.core.controller.InputEvent;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
		
	public void pressed(final DPoint p) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.inputStart(new InputEvent(p));
				PLATFORMVIEW.repaint();
			}}
		);
	}
	
	public void dragged(final DPoint p) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.inputMove(new InputEvent(p));
				PLATFORMVIEW.repaint();
			}}
		);
	}
	
	public void released() {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.inputEnd();
				PLATFORMVIEW.repaint();
			}}
		);
	}
	
	public void pressed_M(final DPoint p) throws Exception {
		CONTROLLER.queueAndWait(new Runnable() {
			public void run() {
				pressed(p);
				PLATFORMVIEW.repaint();
			}
		});
	}
	
	public void dragged_M(final DPoint p) throws Exception {
		CONTROLLER.queueAndWait(new Runnable() {
			public void run() {
				dragged(p);
				PLATFORMVIEW.repaint();
			}
		});
	}
	
	public void released_M() throws Exception {
		CONTROLLER.queueAndWait(new Runnable() {
			public void run() {
				released();
				PLATFORMVIEW.repaint();
			}
		});
	}
	
	@Override
	public void mousePressed(MouseEvent ev) {
		switch (CONTROLLER.mode) {
		case IDLE:
//			if () {
//				PLATFORMCONTROLLER.mode = ControlMode.ZOOMING;
//			} else {
				CONTROLLER.mode = ControlMode.DRAWING;
				pressed(new DPoint(ev.getX(), ev.getY()));
			//}
			break;
		case DRAWING:
			assert false;
			break;
		case ZOOMING:
			assert false;
			break;
		case RUNNING:
			;
			break;
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		switch (CONTROLLER.mode) {
		case IDLE:
			break;
		case DRAWING:
			dragged(new DPoint(ev.getX(), ev.getY()));
			break;
		case ZOOMING:
			assert false;
			break;
		case RUNNING:
			;
			break;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		switch (CONTROLLER.mode) {
		case IDLE:
			break;
		case DRAWING:
			released();
			CONTROLLER.mode = ControlMode.IDLE;
			break;
		case ZOOMING:
			assert false;
			break;
		case RUNNING:
			;
			break;
		}
		
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
