package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.bypass.level.Level;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class LevelMenuItem extends MenuItem {

	public int index;
	
	public Label gradeLab;
	
	public LevelMenuItem(Menu menu, int index, String fmt) {
		super(menu, String.format(fmt, index));
		
		this.index = index;
	}
	
	public void render() {
		
//		ctxt.getTransform(origTransformRender);
//		
//		int x = (int)origTransformRender.getTranslateX();
//		int y = (int)origTransformRender.getTranslateY();
		
		lab.aabb = new AABB(aabb.x + menu.menuItemWidest[c]/2 - lab.localAABB.width/2, aabb.y + (20 + lab.localAABB.height + 20)/2 - lab.localAABB.height/2, lab.localAABB.width, lab.localAABB.height);
		
		aabb = new AABB(aabb.x, aabb.y, menu.menuItemWidest[c], 20 + lab.aabb.height + 20);
		
		if (BYPASSAPP.levelDB.levelMap.keySet().contains(index)) {
			
			Level l = BYPASSAPP.levelDB.levelMap.get(index);
			
			if (l.isWon) {
				
				gradeLab = new Label(l.grade);
				gradeLab.color = Color.LIGHT_GRAY;
				gradeLab.fontFile = APP.platform.fontResource("visitor1");
				gradeLab.fontStyle = FontStyle.PLAIN;
				gradeLab.fontSize = 36;
				gradeLab.renderLocal();
				
				gradeLab.aabb = new AABB(aabb.brX-gradeLab.localAABB.width, aabb.brY-gradeLab.localAABB.height, gradeLab.localAABB.width, gradeLab.localAABB.height);
				
			} else {
				gradeLab = null;
			}
		} else {
			gradeLab = null;
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.getTransform(origTransformPaint);
		
		if (active) {
			lab.color = Color.WHITE;
		} else {
			lab.color = Color.GRAY;
		}
		
		lab.paint(ctxt);
		
		ctxt.setTransform(origTransformPaint);
		
		if (gradeLab != null) {
			gradeLab.paint(ctxt);
		}
		
		ctxt.setColor(Color.BLUE);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		aabb.draw(ctxt);
	}

}
