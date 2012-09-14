package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(ev);
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		dragged(ev);
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released(ev);
	}
	
	@Override
	public void mouseMoved(MouseEvent ev) {
		moved(ev);
	}
	
	public void pressed(final MouseEvent ev) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.pressed(ev);
			}}
		);
	}
	
	public void dragged(final MouseEvent ev) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.dragged(ev);
			}}
		);
	}
	
	public void released(final MouseEvent ev) {
		CONTROLLER.queue(new Runnable(){
			@Override
			public void run() {
				CONTROLLER.released(ev);
			}}
		);
	}
	
	public void moved(final MouseEvent ev) {
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
