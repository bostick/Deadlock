package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
//	public void init() {
//		
//		VIEW.canvas.addMouseListener(this);
//		VIEW.canvas.addMouseMotionListener(this);
//		
//		VIEW.previewPanel.addMouseListener(this);
//		VIEW.previewPanel.addMouseMotionListener(this);
//		
//	}
	
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
	
	public void mouseClicked(MouseEvent ev) {
		clicked(new InputEvent(ev.getComponent(), new Point(ev.getX(), ev.getY())));
	}
	
	public void pressed(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.pressed(ev);
			
		} else if (c == VIEW.previewPanel) {
			
			VIEW.previewPanel.pressed(ev);
			
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.dragged(ev);
			
		} else if (c == VIEW.previewPanel) {
			
			VIEW.previewPanel.dragged(ev);
			
		}
		
	}
	
	public void released(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.released(ev);
			
		}
		
	}
	
	public void moved(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.moved(ev);
			
		}
		
	}
	
	public void clicked(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.clicked(ev);
			
		}
		
	}
	
	public void entered(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.entered(ev);
			
		}
		
	}
	
	public void exited(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.exited(ev);
			
		}
		
	}

}
