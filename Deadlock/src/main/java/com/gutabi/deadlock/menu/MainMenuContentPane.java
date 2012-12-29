package com.gutabi.deadlock.menu;

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

public class MainMenuContentPane extends Container {
	
	MainMenu screen;
	
	private JavaListener jl;
	
	public MainMenuContentPane(MainMenu screen) {
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
			if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
				insertKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
				deleteKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
				qKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_W) {
				wKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_G) {
				gKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_1) {
				d1Key();
			} else if (ev.getKeyCode() == KeyEvent.VK_2) {
				d2Key();
			} else if (ev.getKeyCode() == KeyEvent.VK_3) {
				d3Key();
			} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
				plusKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
				minusKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
				downKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
				upKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
				enterKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_A) {
				aKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_S) {
				
				int mods = ev.getModifiersEx();
				
				if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
					ctrlSKey();
				} else {
					sKey();
				}
				
			} else if (ev.getKeyCode() == KeyEvent.VK_D) {
				dKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_O) {
				
				int mods = ev.getModifiersEx();
				
				if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
					ctrlOKey();
				}
				
			}
		}

		public void keyTyped(KeyEvent ev) {
			;
		}
		
		public void mousePressed(MouseEvent ev) {
			pressed(new InputEvent(new Point(ev.getX(), ev.getY())));
		}

		public void mouseReleased(MouseEvent ev) {
			released(new InputEvent(new Point(ev.getX(), ev.getY())));
		}
		
		public void mouseDragged(MouseEvent ev) {
			dragged(new InputEvent(new Point(ev.getX(), ev.getY())));
		}

		public void mouseMoved(MouseEvent ev) {
			moved(new InputEvent(new Point(ev.getX(), ev.getY())));
		}

		public void mouseClicked(MouseEvent ev) {
			clicked(new InputEvent(new Point(ev.getX(), ev.getY())));
		}

		public void mouseEntered(MouseEvent ev) {
			entered(new InputEvent(new Point(ev.getX(), ev.getY())));
		}

		public void mouseExited(MouseEvent ev) {
			exited(new InputEvent(new Point(ev.getX(), ev.getY())));
		}
		
	}
	
	public void enableKeyListener() {
		addKeyListener(jl);
	}
	
	public void disableKeyListener() {
		removeKeyListener(jl);
	}
	
	public void pressed(InputEvent ev) {
		screen.pressed(ev);
	}
	
	public void dragged(InputEvent ev) {
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		screen.dragged(ev);
	}
	
	public void released(InputEvent ev) {
		
		screen.released(ev);
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	
	public void moved(InputEvent ev) {
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		screen.moved(ev);
	}
	
	Point lastClickedCanvasPoint;
	
	public void clicked(InputEvent ev) {
		
		screen.clicked(ev);
	}
	
	public void entered(InputEvent ev) {
		
	}
	
	public void exited(InputEvent ev) {
		screen.exited(ev);
	}
	
	public void qKey() {
		screen.qKey();
	}
	
	public void wKey() {
		screen.wKey();
	}
	
	public void gKey() {
		screen.gKey();
	}
	
	public void deleteKey() {
		screen.deleteKey();
	}
	
	public void insertKey() {
		screen.insertKey();
	}
	
	public void escKey() {
		screen.escKey();
	}
	
	public void d1Key() {
		screen.d1Key();
	}
	
	public void d2Key() {
		screen.d2Key();
	}
	
	public void d3Key() {
		screen.d3Key();
	}
	
	public void plusKey() {
		screen.plusKey();
	}
	
	public void minusKey() {
		screen.minusKey();
	}
	
	public void downKey() {
		screen.downKey();
	}

	public void upKey() {
		screen.upKey();
	}
	
	public void enterKey() {
		screen.enterKey();
	}
	
	public void aKey() {
		screen.aKey();
	}
	
	public void sKey() {
		screen.sKey();
	}
	
	public void ctrlSKey() {
		screen.ctrlSKey();
	}
	
	public void dKey() {
		screen.dKey();
	}
	
	public void ctrlOKey() {
		screen.ctrlOKey();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		RenderingContext ctxt = new RenderingContext((Graphics2D)g);
		
		screen.canvas.paint(ctxt);
		
	}
}
