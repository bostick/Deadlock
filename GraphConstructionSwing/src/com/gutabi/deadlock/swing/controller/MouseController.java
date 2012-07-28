package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.controller.InputEvent;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	public void init() {
		VIEW.panel.addMouseListener(this);
		VIEW.panel.addMouseMotionListener(this);
	}
	
	public void pressed(Point p) {
		CONTROLLER.inputStart(new InputEvent(p));
	}
	
	public void dragged(Point p) {
		CONTROLLER.inputMove(new InputEvent(p));
	}
	
	public void released() {
		released(true);
	}
	
	public void released(boolean massage) {
		CONTROLLER.inputEnd(massage);
	}
	
	public void pressed_M(final Point p) throws InterruptedException, InvocationTargetException {
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				pressed(p);
			}
		});
		VIEW.repaint();
	}
	
	public void dragged_M(final Point p) throws InterruptedException, InvocationTargetException {
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				dragged(p);
			}
		});
		VIEW.repaint();
	}
	
	public void released_M() throws InterruptedException, InvocationTargetException {
		released_M(false);
	}
	
	public void released_M(final boolean massage) throws InterruptedException, InvocationTargetException {
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				released(massage);
			}
		});
		VIEW.repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(new Point(ev.getX(), ev.getY()));
		VIEW.repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		dragged(new Point(ev.getX(), ev.getY()));
		VIEW.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released();
		VIEW.repaint();
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
