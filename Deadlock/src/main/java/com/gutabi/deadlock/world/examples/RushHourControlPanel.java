package com.gutabi.deadlock.world.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourControlPanel extends Panel {
	
	Label indexLab;
		
	public RushHourControlPanel() {
		
		aabb = new AABB(aabb.x, aabb.y, APP.CONTROLPANEL_WIDTH, APP.CONTROLPANEL_HEIGHT);
	}
	
	public void postDisplay() {
		RushHourWorld world = (RushHourWorld)APP.model;
		
		Resource visitorFontFile = APP.platform.fontResource("visitor1");
		indexLab = new Label(Integer.toString(world.index), 5, 5);
		indexLab.fontFile = visitorFontFile;
		indexLab.fontStyle = FontStyle.PLAIN;
		indexLab.fontSize = 16;
		indexLab.renderLocal();
		indexLab.render();
		
		double pixelsPerMeterWidth = world.worldCamera.previewAABB.width / world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = world.worldCamera.previewAABB.height / world.quadrantMap.worldHeight;
		world.worldCamera.previewPixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		world.previewPostDisplay();
	}
	
	public void paint(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		indexLab.paint(ctxt);
		
		world.paintPreview_controlPanel(ctxt);
		
		ctxt.setTransform(origTransform);
	}
	
}
