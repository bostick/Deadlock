package com.brentonbostick.capsloc.ui;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.FontStyle;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public abstract class MenuItem {
	
	public final Menu menu;
	
	protected Label lab;
	protected Label auxLab0;
	protected Label auxLab1;
	protected Label auxLab2;
	
	public boolean border = true;
	
	public AABB localAABB;
	public AABB aabb = new AABB();
	
	public boolean active = true;
	
	public int r;
	public int c;
	
	public static MenuItem minimumWidth = new MenuItem(null, "XXX") {
		public void action() {
			
		}
	};
	
	public MenuItem(Menu menu, String text) {
		this.menu = menu;
		lab = new Label(text);
		lab.fontFile = APP.platform.fontResource("visitor1");
		lab.fontStyle = FontStyle.PLAIN;
		lab.fontSize = 72;
		lab.renderLocal();
		localAABB = new AABB(0, 0, lab.localAABB.width, 20 + lab.localAABB.height + 20);
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public abstract void action();
	
	
	public void render() {
		
		if (active) {
			lab.color = Color.WHITE;
		} else {
			lab.color = Color.GRAY;
		}
		
		lab.render();
		
		lab.aabb = new AABB(aabb.x + menu.columnWidth[c]/2 - lab.localAABB.width/2, aabb.y + (20 + lab.localAABB.height + 20)/2 - lab.localAABB.height/2, lab.localAABB.width, lab.localAABB.height);
		
		if (auxLab0 != null) {
			auxLab0.render();
		}
		if (auxLab1 != null) {
			auxLab1.render();
		}
		if (auxLab2 != null) {
			auxLab2.render();
		}
		
		aabb = new AABB(aabb.x, aabb.y, menu.columnWidth[c], 20 + lab.aabb.height + 20);
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.pushClip();
		
		/*
		 * little pixel differences add up to the label overflowing the aabb a little bit
		 */
		ctxt.clip(aabb);
		
		lab.paint(ctxt);
		
		if (auxLab0 != null) {
			auxLab0.paint(ctxt);
		}
		if (auxLab1 != null) {
			auxLab1.paint(ctxt);
		}
		if (auxLab2 != null) {
			auxLab2.paint(ctxt);
		}
		
		ctxt.popClip();
		
		if (border) {
			ctxt.setColor(Color.BLUE);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			aabb.draw(ctxt);
		}
		
		
	}
	
	public void paintHilited(RenderingContext ctxt) {
		
		ctxt.setColor(Color.RED);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		aabb.draw(ctxt);
		
	}
	
}
