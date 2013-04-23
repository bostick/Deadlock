package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.bypass.level.BypassCarTool;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.Tool;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Checkbox;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.cars.Car;

public class BypassControlPanel extends Panel {
	
	Label indexLab;
	Label requiredMovesLab;
	Label userMovesLab;
	Label currentCar;
	Label lastMotion;
	
	public Checkbox debugCheckBox;
	
	public BypassControlPanel() {
		
		aabb = new AABB(aabb.x, aabb.y, APP.CONTROLPANEL_WIDTH, APP.CONTROLPANEL_HEIGHT);
	}
	
	public void postDisplay() {
		BypassWorld world = (BypassWorld)APP.model;
		
		double pixelsPerMeterWidth = world.worldCamera.previewAABB.width / world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = world.worldCamera.previewAABB.height / world.quadrantMap.worldHeight;
		world.worldCamera.previewPixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		world.previewPostDisplay();
	}
	
	public void clicked(InputEvent ev) {
		super.clicked(ev);
		
		if (debugCheckBox.hitTest(ev.p)) {
			debugCheckBox.action();
		}
		
	}
	
	
	Transform origTransform = APP.platform.createTransform();
	
	public void paint(RenderingContext ctxt) {
		BypassWorld world = (BypassWorld)APP.model;
		
		ctxt.getTransform(origTransform);
		
		ctxt.translate(aabb.x, aabb.y);
		
		Resource visitorFontFile = APP.platform.fontResource("visitor1");
		indexLab = new Label(Integer.toString(world.curLevel.index), 5, 5);
		indexLab.fontFile = visitorFontFile;
		indexLab.fontStyle = FontStyle.PLAIN;
		indexLab.fontSize = 16;
		indexLab.renderLocal();
		indexLab.render();
		indexLab.paint(ctxt);
		
		requiredMovesLab = new Label(Integer.toString(world.curLevel.requiredMoves), 5, 20);
		requiredMovesLab.fontFile = visitorFontFile;
		requiredMovesLab.fontStyle = FontStyle.PLAIN;
		requiredMovesLab.fontSize = 16;
		requiredMovesLab.renderLocal();
		requiredMovesLab.render();
		requiredMovesLab.paint(ctxt);
		
		userMovesLab = new Label(Integer.toString(world.curLevel.userMoves), 5, 35);
		userMovesLab.fontFile = visitorFontFile;
		userMovesLab.fontStyle = FontStyle.PLAIN;
		userMovesLab.fontSize = 16;
		userMovesLab.renderLocal();
		userMovesLab.render();
		userMovesLab.paint(ctxt);
		
		debugCheckBox = new Checkbox() {
			public void action() {
				World world = (World)APP.model;
				
				selected = !selected;
				render();
				
				APP.DEBUG_DRAW = selected;
				
				world.render_worldPanel();
			}
		};
		debugCheckBox.selected = APP.DEBUG_DRAW;
		debugCheckBox.setLocation(5, 50);
		debugCheckBox.render();
		debugCheckBox.paint(ctxt);
		
		Tool tool = APP.tool;
		if (tool instanceof BypassCarTool) {
			BypassCarTool bt = (BypassCarTool)tool;
			
			Car c = bt.car;
			String id = (c == null ? " " : Integer.toString(c.id));
			currentCar = new Label(id, 5, 80);
			currentCar.fontFile = visitorFontFile;
			currentCar.fontStyle = FontStyle.PLAIN;
			currentCar.fontSize = 16;
			currentCar.renderLocal();
			currentCar.render();
			currentCar.paint(ctxt);
			
			String last = (bt.lastMotion == null ? " " : bt.lastMotion.toString());
			lastMotion = new Label(last, 5, 95);
			lastMotion.fontFile = visitorFontFile;
			lastMotion.fontStyle = FontStyle.PLAIN;
			lastMotion.fontSize = 16;
			lastMotion.renderLocal();
			lastMotion.render();
			lastMotion.paint(ctxt);
		}
		
		world.paintPreview_controlPanel(ctxt);
		
		ctxt.setTransform(origTransform);
	}
	
}
