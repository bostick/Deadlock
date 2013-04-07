package com.gutabi.deadlock.rushhour;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Checkbox;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourControlPanel extends Panel {
	
	Label indexLab;
	Label requiredMovesLab;
	Label userMovesLab;
	
	public Checkbox debugCheckBox;
	
	public RushHourControlPanel() {
//		RushHourWorld world = (RushHourWorld)APP.model;
		
		aabb = new AABB(aabb.x, aabb.y, APP.CONTROLPANEL_WIDTH, APP.CONTROLPANEL_HEIGHT);
	}
	
	public void postDisplay() {
		RushHourWorld world = (RushHourWorld)APP.model;
		
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
	
	public void paint(RenderingContext ctxt) {
		RushHourWorld world = (RushHourWorld)APP.model;
		
		Transform origTransform = ctxt.getTransform();
		
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
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
				
				APP.DEBUG_DRAW = selected;
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
				APP.debuggerScreen.contentPane.repaint();
			}
		};
		debugCheckBox.selected = APP.DEBUG_DRAW;
		debugCheckBox.setLocation(5, 50);
		debugCheckBox.render();
		debugCheckBox.paint(ctxt);
		
		world.paintPreview_controlPanel(ctxt);
		
		ctxt.setTransform(origTransform);
	}
	
}
