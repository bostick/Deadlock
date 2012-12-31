package com.gutabi.deadlock.quadranteditor;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;

@SuppressWarnings("serial")
public class QuadrantEditorContentPane extends Container {
	
	QuadrantEditor screen;
	
	public QuadrantEditorCanvas canvas;
	
	private JavaListener jl;
	
	public QuadrantEditorContentPane(QuadrantEditor screen) {
		this.screen = screen;
		
		canvas = new QuadrantEditorCanvas(screen);
		canvas.setLocation(0, 0);
		
		jl = new JavaListener();
		addMouseListener(jl);
		addMouseMotionListener(jl);
		addKeyListener(jl);
	}
	
	class JavaListener implements KeyListener, MouseListener, MouseMotionListener {
		
		public void keyPressed(KeyEvent ev) {
			;
		}

		public void keyReleased(KeyEvent ev) {
			
		}

		public void keyTyped(KeyEvent ev) {
			;
		}
		
		public void mousePressed(MouseEvent ev) {
			
		}

		public void mouseReleased(MouseEvent ev) {
			
		}
		
		public void mouseDragged(MouseEvent ev) {
			
		}

		public void mouseMoved(MouseEvent ev) {
			Point p = new Point(ev.getX(), ev.getY());
			if (canvas.aabb.hitTest(p)) {
				canvas.moved(new InputEvent(screen.contentPaneToCanvas(p)));
			}
		}

		public void mouseClicked(MouseEvent ev) {
			Point p = new Point(ev.getX(), ev.getY());
			if (canvas.aabb.hitTest(p)) {
				canvas.clicked(new InputEvent(screen.contentPaneToCanvas(p)));
			}
		}

		public void mouseEntered(MouseEvent ev) {
			
		}

		public void mouseExited(MouseEvent ev) {
			
		}
		
	}
	
	public void enableKeyListener() {
		addKeyListener(jl);
	}
	
	public void disableKeyListener() {
		removeKeyListener(jl);
	}
	
	public void postDisplay() {
		
		canvas.postDisplay();
		
	}
	
	public void paint(Graphics g) {
		
		super.paint(g);
		
		RenderingContext ctxt = new RenderingContext((Graphics2D)g);
		
		canvas.paint(ctxt);
		
	}
	
}
