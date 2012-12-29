package com.gutabi.deadlock.world;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.Checkbox;
import com.gutabi.deadlock.ui.ComponentBase;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.RenderingContext;

public class ControlPanel extends ComponentBase {
	
	private java.awt.Canvas c;
	private JavaListener jl;
	
	WorldScreen screen;
	
	Label simulationInitLab;
	Label normalCarsLab;
	Label fastCarLab;
	Label reallyCarLab;
	Label stateLab;
	Label fpsLab;
	Label stopSignLab;
	Label carTextureLab;
	Label explosionsLab;
	Label debugLab;
	
	public Checkbox normalCarButton;
	public Checkbox fastCarButton;
//	public JCheckBox randomCarButton;
	public Checkbox reallyCarButton;
	
	public Button startButton;
	public Button stopButton;
	
	public Checkbox fpsCheckBox;
	public Checkbox stopSignCheckBox;
	public Checkbox carTextureCheckBox;
	public Checkbox explosionsCheckBox;
	public Checkbox debugCheckBox;
	
	@SuppressWarnings("serial")
	public ControlPanel(final WorldScreen screen) {
		
		this.screen = screen;
		
		this.c = new java.awt.Canvas() {
			public void paint(Graphics g) {
				super.paint(g);
				
				RenderingContext ctxt = new RenderingContext((Graphics2D)g);
				
				simulationInitLab.paint(ctxt);
				
				normalCarsLab.paint(ctxt);
				
				fastCarLab.paint(ctxt);
				reallyCarLab.paint(ctxt);
				stateLab.paint(ctxt);
				fpsLab.paint(ctxt);
				stopSignLab.paint(ctxt);
				carTextureLab.paint(ctxt);
				explosionsLab.paint(ctxt);
				debugLab.paint(ctxt);
				
				normalCarButton.paint(ctxt);
				fastCarButton.paint(ctxt);
				reallyCarButton.paint(ctxt);
				
				startButton.paint(ctxt);
				stopButton.paint(ctxt);
				
				fpsCheckBox.paint(ctxt);
				stopSignCheckBox.paint(ctxt);
				carTextureCheckBox.paint(ctxt);
				explosionsCheckBox.paint(ctxt);
				debugCheckBox.paint(ctxt);
				
				screen.world.paint_preview(ctxt);
				
			}
		};
		
		c.setSize(new Dimension(200, 822));
		c.setPreferredSize(new Dimension(200, 822));
		c.setMaximumSize(new Dimension(200, 822));
		
		c.setFocusable(false);
		
		jl = new JavaListener();
		c.addMouseListener(jl);
		c.addMouseMotionListener(jl);
	}
	
	class JavaListener implements MouseListener, MouseMotionListener {
		
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
	
	public int getWidth() {
		return c.getWidth();
	}
	
	public int getHeight() {
		return c.getHeight();
	}
	
	public java.awt.Component java() {
		return c;
	}
	
	public void init() {
		
		simulationInitLab = new Label("Simulation Init:");
		simulationInitLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		simulationInitLab.renderLocal();
		simulationInitLab.setLocation(5, 5);
		simulationInitLab.render();
		
		
		normalCarButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
			}
		};
		normalCarButton.selected = true;
		normalCarButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5);
		normalCarButton.render();
		
		normalCarsLab = new Label("Normal Cars");
		normalCarsLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		normalCarsLab.renderLocal();
		
		normalCarsLab.setLocation(5 + normalCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5);
		normalCarsLab.render();
		
		
		fastCarButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
			}
		};
		fastCarButton.selected = true;
		fastCarButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5);
		fastCarButton.render();
		
		
		fastCarLab = new Label("Fast Cars");
		fastCarLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		fastCarLab.renderLocal();
		fastCarLab.setLocation(5 + fastCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5);
		fastCarLab.render();
		
		
		reallyCarButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
			}
		};
		reallyCarButton.selected = true;
		reallyCarButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5);
		reallyCarButton.render();
		
		reallyCarLab = new Label("Really Fast Cars");
		reallyCarLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		reallyCarLab.renderLocal();
		reallyCarLab.setLocation(5 + reallyCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5);
		reallyCarLab.render();
		
		
		startButton = new Button() {
			public void action() {
				
				if (command.equals("start")) {
					lab.text = "Pause";
					command = "pause";
					
					stopButton.enabled = true;
					
					screen.startRunning();
				} else if (command.equals("pause")) {
					lab.text = "Unpause";
					command = "unpause";
					
					screen.pauseRunning();
				} else if (command.equals("unpause")) {
					lab.text = "Pause";
					command = "pause";
					
					screen.unpauseRunning();
				}
				
				render();
				stopButton.render();
				ControlPanel.this.repaint();
			}
		};
		startButton.lab = new Label("Start");
		startButton.lab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 32);
		startButton.command = "start";
		startButton.setLocation(5, 120);
		startButton.render();
		
		stopButton = new Button() {
			public void action() {
				
				if (command.equals("stop")) {
					startButton.lab.text = "Start";
					startButton.command = "start";
					
					enabled = false;
					
					screen.stopRunning();
				}
				
				render();
				startButton.render();
				ControlPanel.this.repaint();
			}
		};
		stopButton.lab = new Label("Stop");
		stopButton.lab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 32);
		stopButton.command = "stop";
		stopButton.enabled = false;
		stopButton.setLocation(5 + startButton.aabb.width + 5, 120);
		stopButton.render();
		
		
		stateLab = new Label("Simulation State:");
		stateLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		stateLab.renderLocal();
		stateLab.setLocation(5, 160);
		stateLab.render();
		
		
		fpsCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
				
				screen.FPS_DRAW = selected;
				
				screen.world.render_canvas();
				screen.canvas.repaint();
			}
		};
		fpsCheckBox.selected = screen.FPS_DRAW;
		fpsCheckBox.setLocation(5, 160 + stateLab.getHeight() + 5);
		fpsCheckBox.render();
		
		fpsLab = new Label("FPS");
		fpsLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		fpsLab.renderLocal();
		fpsLab.setLocation(5 + fpsCheckBox.getWidth() + 5, 160 + stateLab.getHeight() + 5);
		fpsLab.render();
		
		
		stopSignCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
				
				screen.STOPSIGN_DRAW = selected;
				
				screen.world.render_canvas();
				screen.canvas.repaint();
			}
		};
		stopSignCheckBox.selected = screen.STOPSIGN_DRAW;
		stopSignCheckBox.setLocation(5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5);
		stopSignCheckBox.render();
		
		stopSignLab = new Label("Stop Signs");
		stopSignLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		stopSignLab.renderLocal();
		stopSignLab.setLocation(5 + stopSignCheckBox.getWidth() + 5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5);
		stopSignLab.render();
		
		
		carTextureCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
				
				screen.CARTEXTURE_DRAW = selected;
				
				screen.canvas.repaint();
			}
		};
		carTextureCheckBox.selected = screen.CARTEXTURE_DRAW;
		carTextureCheckBox.setLocation(5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5);
		carTextureCheckBox.render();
		
		carTextureLab = new Label("Car Textures");
		carTextureLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		carTextureLab.renderLocal();
		carTextureLab.setLocation(5 + carTextureCheckBox.getWidth() + 5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5);
		carTextureLab.render();
		
		
		explosionsCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
				
				screen.EXPLOSIONS_DRAW = selected;
				
				screen.canvas.repaint();
			}
		};
		explosionsCheckBox.selected = screen.EXPLOSIONS_DRAW;
		explosionsCheckBox.setLocation(5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5);
		explosionsCheckBox.render();
		
		explosionsLab = new Label("Explosions");
		explosionsLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		explosionsLab.renderLocal();
		explosionsLab.setLocation(5 + explosionsCheckBox.getWidth() + 5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5);
		explosionsLab.render();
		
		
		debugCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				ControlPanel.this.repaint();
				
				screen.DEBUG_DRAW = selected;
				
				screen.world.render_canvas();
				screen.canvas.repaint();
			}
		};
		debugCheckBox.selected = screen.DEBUG_DRAW;
		debugCheckBox.setLocation(5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugCheckBox.render();
		
		debugLab = new Label("debug");
		debugLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		debugLab.renderLocal();
		debugLab.setLocation(5 + debugCheckBox.getWidth() + 5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugLab.render();
		
		screen.world.setPreviewLocation(5, 400);
	}
	
	public void clicked(InputEvent ev) {
		
		if (normalCarButton.hitTest(ev.p)) {
			normalCarButton.action();
		} else if (fastCarButton.hitTest(ev.p)) {
			fastCarButton.action();
		} else if (reallyCarButton.hitTest(ev.p)) {
			reallyCarButton.action();
		} else if (fpsCheckBox.hitTest(ev.p)) {
			fpsCheckBox.action();
		} else if (stopSignCheckBox.hitTest(ev.p)) {
			stopSignCheckBox.action();
		} else if (carTextureCheckBox.hitTest(ev.p)) {
			carTextureCheckBox.action();
		} else if (explosionsCheckBox.hitTest(ev.p)) {
			explosionsCheckBox.action();
		} else if (debugCheckBox.hitTest(ev.p)) {
			debugCheckBox.action();
		} else if (startButton.hitTest(ev.p)) {
			startButton.action();
		} else if (stopButton.hitTest(ev.p)) {
			stopButton.action();
		}
		
	}
	
	
	public Point controlPanelToPreview(Point p) {
		return new Point(p.x - screen.world.previewAABB.x, p.y - screen.world.previewAABB.y);
	}
	
	public Point lastPressedControlPanelPoint;
	
	public Point lastPressPreviewPoint;
	public Point lastDragPreviewPoint;
	public Point penDragPreviewPoint;
	long lastPressTime;
	long lastDragTime;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		if (screen.world.previewHitTest(p)) {
			
			lastPressedControlPanelPoint = p;
			lastDraggedControlPanelPoint = null;
			
			lastPressPreviewPoint = controlPanelToPreview(p);
			lastPressTime = System.currentTimeMillis();
			
			lastDragPreviewPoint = null;
			lastDragTime = -1;
			
		} else {
			
		}
		
	}
	
	public Point lastDraggedControlPanelPoint;
	
	public void dragged(InputEvent ev) {
		
		Point p = ev.p;
		
		if (screen.world.previewHitTest(p) && lastPressedControlPanelPoint != null && screen.world.previewHitTest(lastPressedControlPanelPoint)) {
			
			penDragPreviewPoint = lastDragPreviewPoint;
			lastDragPreviewPoint = controlPanelToPreview(p);
			lastDragTime = System.currentTimeMillis();
			
			if (penDragPreviewPoint != null) {
				
				double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
				double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
				
				screen.world.previewPan(new Point(dx, dy));
				
				screen.world.render_canvas();
				screen.world.render_preview();
				screen.canvas.repaint();
				screen.controlPanel.repaint();
			}
			
		}
		
	}
	
	public void repaint() {
		c.repaint();
	}
	
}
