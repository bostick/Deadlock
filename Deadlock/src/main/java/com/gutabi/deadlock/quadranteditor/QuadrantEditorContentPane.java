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

@SuppressWarnings("serial")
public class QuadrantEditorContentPane extends Container {
	
	QuadrantEditor screen;
	
	private JavaListener jl;
	
	public QuadrantEditorContentPane(QuadrantEditor screen) {
		this.screen = screen;
		
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
			
		}

		public void mouseClicked(MouseEvent ev) {
			screen.clicked(new InputEvent(new Point(ev.getX(), ev.getY())));
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
	
	public void paint(Graphics g) {
		
		super.paint(g);
		
		RenderingContext ctxt = new RenderingContext((Graphics2D)g);
		
		screen.canvas.paint(ctxt);
		
	}
	
}
