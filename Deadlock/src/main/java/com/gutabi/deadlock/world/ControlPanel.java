package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.AffineTransform;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.Checkbox;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class ControlPanel extends PanelBase {
	
	AABB previewAABB = APP.platform.createShapeEngine().createAABB(5, 400, 100, 100);
	double previewPixelsPerMeter;
	
	WorldScreen screen;
	
	Label simulationInitLab;
	Label normalCarsLab;
	Label fastCarLab;
	Label reallyCarLab;
	Label truckLab;
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
	public Checkbox truckButton;
	
	public Button startButton;
	public Button stopButton;
	
	public Checkbox fpsCheckBox;
	public Checkbox stopSignCheckBox;
	public Checkbox carTextureCheckBox;
	public Checkbox explosionsCheckBox;
	public Checkbox debugCheckBox;
	
	public ControlPanel(final WorldScreen screen) {
		
		this.screen = screen;
		
		aabb = APP.platform.createShapeEngine().createAABB(aabb.x, aabb.y, 200, 822);
		
		simulationInitLab = new Label("Simulation Init:");
		simulationInitLab.fontName = "Visitor TT1 BRK";
		simulationInitLab.fontStyle = FontStyle.PLAIN;
		simulationInitLab.fontSize = 16;
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
		normalCarsLab.fontName = "Visitor TT1 BRK";
		normalCarsLab.fontStyle = FontStyle.PLAIN;
		normalCarsLab.fontSize = 16;
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
		fastCarLab.fontName = "Visitor TT1 BRK";
		fastCarLab.fontStyle = FontStyle.PLAIN;
		fastCarLab.fontSize = 16;
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
		reallyCarLab.fontName = "Visitor TT1 BRK";
		reallyCarLab.fontStyle = FontStyle.PLAIN;
		reallyCarLab.fontSize = 16;
		reallyCarLab.renderLocal();
		reallyCarLab.setLocation(5 + reallyCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5);
		reallyCarLab.render();
		
		
		truckButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				screen.contentPane.repaint();
				
				APP.TRUCK = selected;
			}
		};
		truckButton.selected = APP.TRUCK;
		truckButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5 + reallyCarButton.getHeight() + 5);
		truckButton.render();
		
		truckLab = new Label("Trucks");
		truckLab.fontName = "Visitor TT1 BRK";
		truckLab.fontStyle = FontStyle.PLAIN;
		truckLab.fontSize = 16;
		truckLab.renderLocal();
		truckLab.setLocation(5 + reallyCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5 + reallyCarButton.getHeight() + 5);
		truckLab.render();
		
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
		startButton.lab.fontName = "Visitor TT1 BRK";
		startButton.lab.fontStyle = FontStyle.PLAIN;
		startButton.lab.fontSize = 16;
		startButton.command = "start";
		startButton.setLocation(5, 140);
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
		stopButton.lab.fontName = "Visitor TT1 BRK";
		stopButton.lab.fontStyle = FontStyle.PLAIN;
		stopButton.lab.fontSize = 16;
		stopButton.command = "stop";
		stopButton.enabled = false;
		stopButton.setLocation(5 + startButton.aabb.width + 5, startButton.aabb.y + stopButton.aabb.height);
		stopButton.render();
		
		
		stateLab = new Label("Simulation State:");
		stateLab.fontName = "Visitor TT1 BRK";
		stateLab.fontStyle = FontStyle.PLAIN;
		stateLab.fontSize = 16;
		stateLab.renderLocal();
		stateLab.setLocation(5, 180);
		stateLab.render();
		
		
		fpsCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				screen.contentPane.repaint();
				
				APP.FPS_DRAW = selected;
				
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
		};
		fpsCheckBox.selected = APP.FPS_DRAW;
		fpsCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5);
		fpsCheckBox.render();
		
		fpsLab = new Label("FPS");
		fpsLab.fontName = "Visitor TT1 BRK";
		fpsLab.fontStyle = FontStyle.PLAIN;
		fpsLab.fontSize = 16;
		fpsLab.renderLocal();
		fpsLab.setLocation(5 + fpsCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5);
		fpsLab.render();
		
		
		stopSignCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				screen.contentPane.repaint();
				
				APP.STOPSIGN_DRAW = selected;
				
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
		};
		stopSignCheckBox.selected = APP.STOPSIGN_DRAW;
		stopSignCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5);
		stopSignCheckBox.render();
		
		stopSignLab = new Label("Stop Signs");
		stopSignLab.fontName = "Visitor TT1 BRK";
		stopSignLab.fontStyle = FontStyle.PLAIN;
		stopSignLab.fontSize = 16;
		stopSignLab.renderLocal();
		stopSignLab.setLocation(5 + stopSignCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5);
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
		carTextureCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5);
		carTextureCheckBox.render();
		
		carTextureLab = new Label("Car Textures");
		carTextureLab.fontName = "Visitor TT1 BRK";
		carTextureLab.fontStyle = FontStyle.PLAIN;
		carTextureLab.fontSize = 16;
		carTextureLab.renderLocal();
		carTextureLab.setLocation(5 + carTextureCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5);
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
		explosionsCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5);
		explosionsCheckBox.render();
		
		explosionsLab = new Label("Explosions");
		explosionsLab.fontName = "Visitor TT1 BRK";
		explosionsLab.fontStyle = FontStyle.PLAIN;
		explosionsLab.fontSize = 16;
		explosionsLab.renderLocal();
		explosionsLab.setLocation(5 + explosionsCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5);
		explosionsLab.render();
		
		
		debugCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				screen.contentPane.repaint();
				
				APP.DEBUG_DRAW = selected;
				
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
		};
		debugCheckBox.selected = APP.DEBUG_DRAW;
		debugCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugCheckBox.render();
		
		debugLab = new Label("debug");
		debugLab.fontName = "Visitor TT1 BRK";
		debugLab.fontStyle = FontStyle.PLAIN;
		debugLab.fontSize = 16;
		debugLab.renderLocal();
		debugLab.setLocation(5 + debugCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugLab.render();
	}
	
	public void setLocation(double x, double y) {
		aabb = APP.platform.createShapeEngine().createAABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
		double pixelsPerMeterWidth = previewAABB.width / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewAABB.height / screen.world.quadrantMap.worldHeight;
		previewPixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		screen.world.previewPostDisplay();
	}
	
	public Point previewToWorld(Point p) {
		
		return new Point((1/previewPixelsPerMeter) * p.x, (1/previewPixelsPerMeter) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		
		return new Point((previewPixelsPerMeter) * p.x, (previewPixelsPerMeter) * p.y);
	}
	
	public Point controlPanelToPreview(Point p) {
		return new Point(p.x - previewAABB.x, p.y - previewAABB.y);
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
		} else if (truckButton.hitTest(ev.p)) {
			truckButton.action();
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
			
			lastPressPreviewPoint = controlPanelToPreview(p);
			
			lastDragPreviewPoint = null;
			
		} else {
			
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		Point p = ev.p;
		
		if (previewAABB.hitTest(p) && lastPressedControlPanelPoint != null && previewAABB.hitTest(lastPressedControlPanelPoint)) {
			
			penDragPreviewPoint = lastDragPreviewPoint;
			lastDragPreviewPoint = controlPanelToPreview(p);
			
			if (penDragPreviewPoint != null) {
				
				double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
				double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
				
				screen.world.previewPan(new Point(dx, dy));
				
				screen.world.render_worldPanel();
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
		truckLab.paint(ctxt);
		
		stateLab.paint(ctxt);
		fpsLab.paint(ctxt);
		stopSignLab.paint(ctxt);
		carTextureLab.paint(ctxt);
		explosionsLab.paint(ctxt);
		debugLab.paint(ctxt);
		
		normalCarButton.paint(ctxt);
		fastCarButton.paint(ctxt);
		reallyCarButton.paint(ctxt);
		truckButton.paint(ctxt);
		
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
				0, 0, (int)previewAABB.width, (int)previewAABB.height);
		
		Point prevLoc = worldToPreview(screen.worldViewport.ul);
		
		Point prevDim = worldToPreview(new Point(screen.worldViewport.width, screen.worldViewport.height));
		
		AABB prev = APP.platform.createShapeEngine().createAABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
		
		ctxt.translate(
				previewAABB.width/2 - (previewPixelsPerMeter * screen.world.quadrantMap.worldWidth / 2),
				previewAABB.height/2 - (previewPixelsPerMeter * screen.world.quadrantMap.worldHeight / 2));
		
		ctxt.setColor(Color.BLUE);
		prev.draw(ctxt);
		
		ctxt.setTransform(origTrans);
		
	}
}
