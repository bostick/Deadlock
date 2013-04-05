package com.gutabi.deadlock.rushhour;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class RushHourControlPanel extends Panel {
	
	Label idLab;
	Label requiredMovesLab;
	Label userMovesLab;
	
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
	
	public void paint(RenderingContext ctxt) {
		RushHourWorld world = (RushHourWorld)APP.model;
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		Resource visitorFontFile = APP.platform.fontResource("visitor1");
		idLab = new Label(world.level.id, 5, 5);
		idLab.fontFile = visitorFontFile;
		idLab.fontStyle = FontStyle.PLAIN;
		idLab.fontSize = 16;
		idLab.renderLocal();
		idLab.render();
		idLab.paint(ctxt);
		
		requiredMovesLab = new Label(Integer.toString(world.level.requiredMoves), 5, 20);
		requiredMovesLab.fontFile = visitorFontFile;
		requiredMovesLab.fontStyle = FontStyle.PLAIN;
		requiredMovesLab.fontSize = 16;
		requiredMovesLab.renderLocal();
		requiredMovesLab.render();
		requiredMovesLab.paint(ctxt);
		
		userMovesLab = new Label(Integer.toString(world.level.userMoves), 5, 35);
		userMovesLab.fontFile = visitorFontFile;
		userMovesLab.fontStyle = FontStyle.PLAIN;
		userMovesLab.fontSize = 16;
		userMovesLab.renderLocal();
		userMovesLab.render();
		userMovesLab.paint(ctxt);
		
		world.paintPreview_controlPanel(ctxt);
		
		ctxt.setTransform(origTransform);
	}
	
}
