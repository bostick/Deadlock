package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		dragged(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	@Override
	public void mouseMoved(MouseEvent ev) {
		moved(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void pressed(final InputEvent ev) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.pressed(ev);
			}}
		);
	}
	
	public void dragged(final InputEvent ev) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.dragged(ev);
			}}
		);
	}
	
	public void released(final InputEvent ev) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.released(ev);
			}}
		);
	}
	
	public void moved(final InputEvent ev) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.moved(ev);
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
	
}
