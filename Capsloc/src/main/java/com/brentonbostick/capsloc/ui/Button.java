package com.brentonbostick.capsloc.ui;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public abstract class Button {
	
	public Label lab;
	
	public String command;
	public boolean enabled = true;
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	public Button() {
		
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void setBounds(double x, double y, double width, double height) {
		aabb = new AABB(x, y, width, height);
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public abstract void action();
	
	public void render() {
		if (lab != null) {
			lab.setLocation(aabb.x, aabb.y);
			lab.renderLocal();
			if (enabled) {
				lab.color = Color.BLACK;
			} else {
				lab.color = Color.GRAY;
			}
			lab.render();
			double width = lab.aabb.width;
			double height = lab.aabb.height;
			aabb = new AABB(aabb.x, aabb.y, width, height);
		}
	}
	
	public void paint(RenderingContext ctxt) {
		if (lab != null) {
			lab.paint(ctxt);
			ctxt.setColor(Color.BLACK);
			lab.aabb.draw(ctxt);
		}
	}
	
}
