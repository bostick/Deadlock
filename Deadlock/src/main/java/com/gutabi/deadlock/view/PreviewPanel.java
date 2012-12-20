package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;

//@SuppressWarnings({"serial", "static-access"})
@SuppressWarnings({"serial"})
public class PreviewPanel extends ComponentBase {
	
	public static final int PREVIEW_WIDTH = 100;
	public static final int PREVIEW_HEIGHT = 100;
	
	private JPanel c;
	
	public PreviewPanel() {
		
		final RenderingContext ctxt = new RenderingContext(RenderingContextType.PREVIEW);
		
		c = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ctxt.g2 = (Graphics2D)g;
				APP.screen.paint(new PaintEvent(PreviewPanel.this, ctxt));
//				prev.paint(ctxt);
			}
		};
		c.setSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
		c.setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
		c.setMaximumSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
		
		JavaListener jl = new JavaListener();
		c.addMouseListener(jl);
		c.addMouseMotionListener(jl);
		c.addKeyListener(jl);
		
	}
	
	class JavaListener implements KeyListener, MouseListener, MouseMotionListener {
		
		public void keyPressed(KeyEvent ev) {
			;
		}

		public void keyReleased(KeyEvent ev) {
			if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
				insertKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
				deleteKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
				qKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_W) {
				wKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_G) {
				gKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_1) {
				d1Key(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_2) {
				d2Key(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_3) {
				d3Key(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
				plusKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
				minusKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
				downKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
				upKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
				enterKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_A) {
				aKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_S) {
				sKey(new InputEvent(PreviewPanel.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_D) {
				dKey(new InputEvent(PreviewPanel.this, null));
			}
		}

		public void keyTyped(KeyEvent ev) {
			;
		}
		
		public void mousePressed(MouseEvent ev) {
			pressed(new InputEvent(PreviewPanel.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseReleased(MouseEvent ev) {
			released(new InputEvent(PreviewPanel.this, new Point(ev.getX(), ev.getY())));
		}
		
		public void mouseDragged(MouseEvent ev) {
			dragged(new InputEvent(PreviewPanel.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseMoved(MouseEvent ev) {
			moved(new InputEvent(PreviewPanel.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseClicked(MouseEvent ev) {
			clicked(new InputEvent(PreviewPanel.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseEntered(MouseEvent ev) {
			entered(new InputEvent(PreviewPanel.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseExited(MouseEvent ev) {
			exited(new InputEvent(PreviewPanel.this, new Point(ev.getX(), ev.getY())));
		}
		
	}
	
	public int getWidth() {
		return c.getWidth();
	}
	
	public int getHeight() {
		return c.getHeight();
	}
	
	public java.awt.Component java() {
		return c;
	}
	
	public Dim postDisplay() {
		
//		APP.screen.previewPostDisplay(new Dim(getWidth(), getHeight()));
		return new Dim(getWidth(), getHeight());
	}
	
	public Point lastPressPreviewPoint;
	public Point lastDragPreviewPoint;
	public Point penDragPreviewPoint;
	long lastPressTime;
	long lastDragTime;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressPreviewPoint = p;
		lastPressTime = System.currentTimeMillis();
		
		lastDragPreviewPoint = null;
		lastDragTime = -1;
		
	}
	
	public void dragged(InputEvent ev) {
		
		Point p = ev.p;
		
		penDragPreviewPoint = lastDragPreviewPoint;
		lastDragPreviewPoint = p;
		lastDragTime = System.currentTimeMillis();
		
		APP.screen.dragged(ev);
	}
	
	public void repaint() {
		c.repaint();
	}
	
}
