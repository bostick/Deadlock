package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.SwingUtilities;

import com.gutabi.deadlock.controller.ControlMode;

//@SuppressWarnings("serial")
@SuppressWarnings({"serial", "static-access"})
public class WorldCanvas extends Canvas {
	
	BufferStrategy bs;
	
	public WorldCanvas() {
		
		setSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		setPreferredSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		setMaximumSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		
	}
	
	public void postDisplay() {
		createBufferStrategy(2);
		bs = getBufferStrategy();
	}
	
	public void paint(Graphics g) {
		
		if (SwingUtilities.isEventDispatchThread()) {
			if (CONTROLLER.mode == ControlMode.RUNNING) {
				return;
			}
		}
		
		bs.show();
	}
	
}
