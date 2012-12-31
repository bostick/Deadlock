package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.Checkbox;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.RenderingContext;

public class ControlPanel extends PanelBase {
	
	AABB previewAABB = new AABB(5, 400, 100, 100);
	
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
	
	public ControlPanel(final WorldScreen screen) {
		
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, 200, 822);
		
		simulationInitLab = new Label("Simulation Init:");
		simulationInitLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		simulationInitLab.renderLocal();
		simulationInitLab.setLocation(5, 5);
		simulationInitLab.render();
		
		
		normalCarButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				screen.contentPane.repaint();
				
				APP.NORMALCAR = selected;
			}
		};
		normalCarButton.selected = APP.NORMALCAR;
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
				screen.contentPane.repaint();
				
				APP.FASTCAR = selected;
			}
		};
		fastCarButton.selected = APP.FASTCAR;
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
				screen.contentPane.repaint();
				
				APP.REALLYCAR = selected;
			}
		};
		reallyCarButton.selected = APP.REALLYCAR;
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
				screen.contentPane.repaint();
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
				screen.contentPane.repaint();
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
				screen.contentPane.repaint();
				
				APP.FPS_DRAW = selected;
				
				screen.world.render_canvas();
				screen.contentPane.repaint();
			}
		};
		fpsCheckBox.selected = APP.FPS_DRAW;
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
				screen.contentPane.repaint();
				
				APP.STOPSIGN_DRAW = selected;
				
				screen.world.render_canvas();
				screen.contentPane.repaint();
			}
		};
		stopSignCheckBox.selected = APP.STOPSIGN_DRAW;
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
				screen.contentPane.repaint();
				
				APP.CARTEXTURE_DRAW = selected;
				
				screen.contentPane.repaint();
			}
		};
		carTextureCheckBox.selected = APP.CARTEXTURE_DRAW;
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
				screen.contentPane.repaint();
				
				APP.EXPLOSIONS_DRAW = selected;
				
				screen.contentPane.repaint();
			}
		};
		explosionsCheckBox.selected = APP.EXPLOSIONS_DRAW;
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
				screen.contentPane.repaint();
				
				APP.DEBUG_DRAW = selected;
				
				screen.world.render_canvas();
				screen.contentPane.repaint();
			}
		};
		debugCheckBox.selected = APP.DEBUG_DRAW;
		debugCheckBox.setLocation(5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugCheckBox.render();
		
		debugLab = new Label("debug");
		debugLab.font = new Font("Visitor TT1 BRK", Font.PLAIN, 16);
		debugLab.renderLocal();
		debugLab.setLocation(5 + debugCheckBox.getWidth() + 5, 160 + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugLab.render();
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
	}
	
	public Point lastPressedControlPanelPoint;
	
	public Point lastPressPreviewPoint;
	public Point lastDragPreviewPoint;
	public Point penDragPreviewPoint;
	public Point lastDraggedControlPanelPoint;
	
	
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
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		if (previewAABB.hitTest(p)) {
			
			lastPressedControlPanelPoint = p;
			lastDraggedControlPanelPoint = null;
			
			lastPressPreviewPoint = screen.controlPanelToPreview(p);
			
			lastDragPreviewPoint = null;
			
		} else {
			
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		Point p = ev.p;
		
		if (previewAABB.hitTest(p) && lastPressedControlPanelPoint != null && previewAABB.hitTest(lastPressedControlPanelPoint)) {
			
			penDragPreviewPoint = lastDragPreviewPoint;
			lastDragPreviewPoint = screen.controlPanelToPreview(p);
			
			if (penDragPreviewPoint != null) {
				
				double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
				double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
				
				screen.world.previewPan(new Point(dx, dy));
				
				screen.world.render_canvas();
				screen.world.render_preview();
				screen.contentPane.repaint();
			}
			
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
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
		
		paint_preview(ctxt);
		
		ctxt.setTransform(origTransform);
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(previewAABB.x, previewAABB.y);
		
		ctxt.paintImage(screen.world.previewImage,
				0, 0, (int)previewAABB.width, (int)previewAABB.height,
				0, 0, screen.previewWidth, screen.previewHeight);
		
		Point prevLoc = screen.worldToPreview(screen.cam.worldViewport.ul);
		
		Point prevDim = screen.worldToPreview(new Point(screen.cam.worldViewport.width, screen.cam.worldViewport.height));
		
		AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
		
		double pixelsPerMeterWidth = screen.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = screen.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		ctxt.translate(
				screen.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2),
				screen.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
		
		ctxt.setColor(Color.BLUE);
		prev.draw(ctxt);
		
		ctxt.setTransform(origTrans);
		
	}
}
