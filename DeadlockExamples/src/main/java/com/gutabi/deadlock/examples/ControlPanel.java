package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.Checkbox;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class ControlPanel extends Panel {
	
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
	
	public ControlPanel() {
		
		aabb = new AABB(aabb.x, aabb.y, APP.CONTROLPANEL_WIDTH, APP.CONTROLPANEL_HEIGHT);
		
		Resource visitorFontFile = APP.platform.fontResource("visitor1");
		
		simulationInitLab = new Label("Simulation Init:", 5, 5);
		simulationInitLab.fontFile = visitorFontFile;
		simulationInitLab.fontStyle = FontStyle.PLAIN;
		simulationInitLab.fontSize = 16;
		simulationInitLab.renderLocal();
		simulationInitLab.render();
		
		
		normalCarButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.NORMALCAR = selected;
			}
		};
		normalCarButton.selected = APP.NORMALCAR;
		normalCarButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5);
		normalCarButton.render();
		
		normalCarsLab = new Label("Normal Cars", 5 + normalCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5);
		normalCarsLab.fontFile = visitorFontFile;
		normalCarsLab.fontStyle = FontStyle.PLAIN;
		normalCarsLab.fontSize = 16;
		normalCarsLab.renderLocal();
		normalCarsLab.render();
		
		
		fastCarButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.FASTCAR = selected;
			}
		};
		fastCarButton.selected = APP.FASTCAR;
		fastCarButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5);
		fastCarButton.render();
		
		
		fastCarLab = new Label("Fast Cars", 5 + fastCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5);
		fastCarLab.fontFile = visitorFontFile;
		fastCarLab.fontStyle = FontStyle.PLAIN;
		fastCarLab.fontSize = 16;
		fastCarLab.renderLocal();
		fastCarLab.render();
		
		
		reallyCarButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.REALLYCAR = selected;
			}
		};
		reallyCarButton.selected = APP.REALLYCAR;
		reallyCarButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5);
		reallyCarButton.render();
		
		reallyCarLab = new Label("Really Fast Cars", 5 + reallyCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5);
		reallyCarLab.fontFile = visitorFontFile;
		reallyCarLab.fontStyle = FontStyle.PLAIN;
		reallyCarLab.fontSize = 16;
		reallyCarLab.renderLocal();
		reallyCarLab.render();
		
		
		truckButton = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.TRUCK = selected;
			}
		};
		truckButton.selected = APP.TRUCK;
		truckButton.setLocation(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5 + reallyCarButton.getHeight() + 5);
		truckButton.render();
		
		truckLab = new Label("Trucks", 5 + reallyCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5 + reallyCarButton.getHeight() + 5);
		truckLab.fontFile = visitorFontFile;
		truckLab.fontStyle = FontStyle.PLAIN;
		truckLab.fontSize = 16;
		truckLab.renderLocal();
		truckLab.render();
		
		startButton = new Button() {
			public void action() {
				World world = (World)APP.model;
				
				if (command.equals("start")) {
					lab.text = "Pause";
					command = "pause";
					
					stopButton.enabled = true;
					
					world.startRunning();
				} else if (command.equals("pause")) {
					lab.text = "Unpause";
					command = "unpause";
					
					world.pauseRunning();
				} else if (command.equals("unpause")) {
					lab.text = "Pause";
					command = "pause";
					
					world.unpauseRunning();
				}
				
				render();
				stopButton.render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
		};
		startButton.setLocation(5, 140);
		startButton.lab = new Label("Start");
		startButton.lab.fontFile = visitorFontFile;
		startButton.lab.fontStyle = FontStyle.PLAIN;
		startButton.lab.fontSize = 16;
		startButton.command = "start";
		startButton.render();
		
		stopButton = new Button() {
			public void action() {
				World world = (World)APP.model;
				
				if (command.equals("stop")) {
					startButton.lab.text = "Start";
					startButton.command = "start";
					
					enabled = false;
					
					world.stopRunning();
				}
				
				render();
				startButton.render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
		};
		stopButton.setLocation(5 + startButton.aabb.width + 5, startButton.aabb.y + stopButton.aabb.height);
		stopButton.lab = new Label("Stop");
		stopButton.lab.fontFile = visitorFontFile;
		stopButton.lab.fontStyle = FontStyle.PLAIN;
		stopButton.lab.fontSize = 16;
		stopButton.command = "stop";
		stopButton.enabled = false;
		stopButton.render();
		
		
		stateLab = new Label("Simulation State:", 5, 180);
		stateLab.fontFile = visitorFontFile;
		stateLab.fontStyle = FontStyle.PLAIN;
		stateLab.fontSize = 16;
		stateLab.renderLocal();
		stateLab.render();
		
		
		fpsCheckBox = new Checkbox() {
			public void action() {
				World world = (World)APP.model;
				
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.FPS_DRAW = selected;
				
				world.render_worldPanel();
				
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
		};
		fpsCheckBox.selected = APP.FPS_DRAW;
		fpsCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5);
		fpsCheckBox.render();
		
		fpsLab = new Label("FPS", 5 + fpsCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5);
		fpsLab.fontFile = visitorFontFile;
		fpsLab.fontStyle = FontStyle.PLAIN;
		fpsLab.fontSize = 16;
		fpsLab.renderLocal();
		fpsLab.render();
		
		
		stopSignCheckBox = new Checkbox() {
			public void action() {
				World world = (World)APP.model;
				
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.STOPSIGN_DRAW = selected;
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
			}
		};
		stopSignCheckBox.selected = APP.STOPSIGN_DRAW;
		stopSignCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5);
		stopSignCheckBox.render();
		
		stopSignLab = new Label("Stop Signs", 5 + stopSignCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5);
		stopSignLab.fontFile = visitorFontFile;
		stopSignLab.fontStyle = FontStyle.PLAIN;
		stopSignLab.fontSize = 16;
		stopSignLab.renderLocal();
		stopSignLab.render();
		
		
		carTextureCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.CARTEXTURE_DRAW = selected;
				
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
		};
		carTextureCheckBox.selected = APP.CARTEXTURE_DRAW;
		carTextureCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5);
		carTextureCheckBox.render();
		
		carTextureLab = new Label("Car Textures", 5 + carTextureCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5);
		carTextureLab.fontFile = visitorFontFile;
		carTextureLab.fontStyle = FontStyle.PLAIN;
		carTextureLab.fontSize = 16;
		carTextureLab.renderLocal();
		carTextureLab.render();
		
		
		explosionsCheckBox = new Checkbox() {
			public void action() {
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.EXPLOSIONS_DRAW = selected;
				
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
		};
		explosionsCheckBox.selected = APP.EXPLOSIONS_DRAW;
		explosionsCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5);
		explosionsCheckBox.render();
		
		explosionsLab = new Label("Explosions", 5 + explosionsCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5);
		explosionsLab.fontFile = visitorFontFile;
		explosionsLab.fontStyle = FontStyle.PLAIN;
		explosionsLab.fontSize = 16;
		explosionsLab.renderLocal();
		explosionsLab.render();
		
		
		debugCheckBox = new Checkbox() {
			public void action() {
				World world = (World)APP.model;
				
				selected = !selected;
				render();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.DEBUG_DRAW = selected;
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
		};
		debugCheckBox.selected = APP.DEBUG_DRAW;
		debugCheckBox.setLocation(5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugCheckBox.render();
		
		debugLab = new Label("debug", 5 + debugCheckBox.getWidth() + 5, stateLab.aabb.y + stateLab.getHeight() + 5 + fpsCheckBox.getHeight() + 5 + stopSignCheckBox.getHeight() + 5 + carTextureCheckBox.getHeight() + 5 + explosionsCheckBox.getHeight() + 5);
		debugLab.fontFile = visitorFontFile;
		debugLab.fontStyle = FontStyle.PLAIN;
		debugLab.fontSize = 16;
		debugLab.renderLocal();
		debugLab.render();
	}
	
	public void postDisplay() {
		World world = (World)APP.model;
		
		double pixelsPerMeterWidth = world.worldCamera.previewAABB.width / world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = world.worldCamera.previewAABB.height / world.quadrantMap.worldHeight;
		world.worldCamera.previewPixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		world.previewPostDisplay();
	}
	
	public void clicked(InputEvent ev) {
		super.clicked(ev);
		
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
	
	Point lastPressPreviewPoint;
	Point lastDragPreviewPoint;
	Point penDragPreviewPoint;
	
	public void pressed(InputEvent ev) {
		super.pressed(ev);
		
		World world = (World)APP.model;
		
		Point p = ev.p;
		
		if (world.worldCamera.previewAABB.hitTest(p)) {
			
			lastPressPreviewPoint = Point.controlPanelToPreview(p, world.worldCamera);
			
			lastDragPreviewPoint = null;
			
		} else {
			
		}
		
	}
	
	public void dragged(InputEvent ev) {
		super.dragged(ev);
		
		World world = (World)APP.model;
		
		Point p = ev.p;
		
		if (world.worldCamera.previewAABB.hitTest(p) && lastPressedPanelPoint != null &&
				world.worldCamera.previewAABB.hitTest(lastPressedPanelPoint)) {
			
			penDragPreviewPoint = lastDragPreviewPoint;
			lastDragPreviewPoint = Point.controlPanelToPreview(p, world.worldCamera);
			
			if (penDragPreviewPoint != null) {
				
				double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
				double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
				
				Point worldDP = Point.previewToWorld(new Point(dx, dy), world.worldCamera);
				world.worldCamera.pan(worldDP);
				
				world.render_worldPanel();
				world.render_preview();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
			
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		Transform origTransform = ctxt.getTransform();
		
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
		
		world.paintPreview_controlPanel(ctxt);
		
		ctxt.setTransform(origTransform);
	}
	
}
