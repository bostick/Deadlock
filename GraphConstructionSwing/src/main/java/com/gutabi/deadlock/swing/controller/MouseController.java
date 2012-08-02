package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.gutabi.core.Point;
import com.gutabi.deadlock.core.controller.InputEvent;
import static com.gutabi.deadlock.swing.Main.PLATFORMCONTROLLER;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
		
	public void pressed(Point p) {
		CONTROLLER.inputStart(new InputEvent(p));
	}
	
	public void dragged(Point p) {
		CONTROLLER.inputMove(new InputEvent(p));
	}
	
	public void released() {
		CONTROLLER.inputEnd();
	}
	
	public void pressed_M(final Point p) throws InterruptedException, InvocationTargetException {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pressed(p);
			}
		});
		PLATFORMVIEW.repaint();
	}
	
	public void dragged_M(final Point p) throws InterruptedException, InvocationTargetException {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dragged(p);
			}
		});
		PLATFORMVIEW.repaint();
	}
	
	public void released_M() throws InterruptedException, InvocationTargetException {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				released();
			}
		});
		PLATFORMVIEW.repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent ev) {
		switch (PLATFORMCONTROLLER.mode) {
		case IDLE:
//			if () {
//				PLATFORMCONTROLLER.mode = ControlMode.ZOOMING;
//			} else {
				//PLATFORMCONTROLLER.mode = ControlMode.DRAWING;
				pressed(new Point(ev.getX(), ev.getY()));
				PLATFORMVIEW.repaint();
			//}
			break;
		case DRAWING:
			assert false;
			break;
		case ZOOMING:
			assert false;
			break;
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		dragged(new Point(ev.getX(), ev.getY()));
		PLATFORMVIEW.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released();
		PLATFORMVIEW.repaint();
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
