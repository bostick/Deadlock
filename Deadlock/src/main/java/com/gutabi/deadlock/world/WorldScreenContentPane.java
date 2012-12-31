package com.gutabi.deadlock.world;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;

@SuppressWarnings("serial")
public class WorldScreenContentPane extends Container {
	
	WorldScreen screen;
	
	public WorldCanvas canvas;
//	public WorldCanvas oldCanvas;
	public ControlPanel controlPanel;
	
	private JavaListener jl;
	
	static Logger logger = Logger.getLogger(WorldScreenContentPane.class);
	
	public WorldScreenContentPane(WorldScreen screen) {
		this.screen = screen;
		
		canvas = new WorldCanvas(screen);
		canvas.setLocation(0, 0);
		
		controlPanel = new ControlPanel(screen);
		controlPanel.setLocation(0 + canvas.aabb.width, 0);
		
		jl = new JavaListener();
		addKeyListener(jl);
		addMouseListener(jl);
		addMouseMotionListener(jl);
	}
	
	class JavaListener implements KeyListener, MouseListener, MouseMotionListener {
		
		public void mousePressed(MouseEvent ev) {
			if (canvas.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				canvas.pressed_canvas(new InputEvent(screen.contentPaneToCanvas(ev.getX(), ev.getY())));
			} else if (controlPanel.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				controlPanel.pressed_controlpanel(new InputEvent(screen.contentPaneToControlPanel(ev.getX(), ev.getY())));
			}
		}

		public void mouseReleased(MouseEvent ev) {
			if (canvas.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				canvas.released_canvas(new InputEvent(screen.contentPaneToCanvas(ev.getX(), ev.getY())));
			} else if (controlPanel.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				
			}
		}
		
		public void mouseDragged(MouseEvent ev) {
			if (canvas.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				canvas.dragged_canvas(new InputEvent(screen.contentPaneToCanvas(ev.getX(), ev.getY())));
			} else if (controlPanel.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				controlPanel.dragged_controlpanel(new InputEvent(screen.contentPaneToControlPanel(ev.getX(), ev.getY())));
			}
		}

		public void mouseMoved(MouseEvent ev) {
//			logger.debug("moved " + ev.getPoint());
			if (canvas.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				canvas.moved_canvas(new InputEvent(screen.contentPaneToCanvas(ev.getX(), ev.getY())));
			} else if (controlPanel.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				
			}
		}

		public void mouseClicked(MouseEvent ev) {
			if (canvas.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				
			} else if (controlPanel.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				controlPanel.clicked_controlpanel(new InputEvent(screen.contentPaneToControlPanel(ev.getX(), ev.getY())));
			}
		}

		public void mouseEntered(MouseEvent ev) {
			
		}

		public void mouseExited(MouseEvent ev) {
			if (canvas.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				canvas.exited_canvas(new InputEvent(screen.contentPaneToCanvas(ev.getX(), ev.getY())));
			} else if (controlPanel.aabb.hitTest(new Point(ev.getX(), ev.getY()))) {
				
			}
		}
		
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
		
	}
	
	public void enableKeyListener() {
		addKeyListener(jl);
	}
	
	public void disableKeyListener() {
		removeKeyListener(jl);
	}
	
	public void postDisplay() {
		
		Dim canvasDim = canvas.postDisplay();
		
		screen.world.canvasPostDisplay(canvasDim);
		
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
//		ctxt.FPS_DRAW = APP.
		
		canvas.paint(ctxt);
		
		controlPanel.paint(ctxt);
		
	}
}
