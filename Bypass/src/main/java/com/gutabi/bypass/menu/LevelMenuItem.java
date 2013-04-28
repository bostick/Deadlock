package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.bypass.level.Level;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontStyle;

public abstract class LevelMenuItem extends MenuItem {

	public int index;
	
	public LevelMenuItem(Menu menu, int index, String fmt) {
		super(menu, String.format(fmt, index));
		
		this.index = index;
	}
	
	public void render() {
		
		super.render();
		
		if (BYPASSAPP.levelDB.levelMap.keySet().contains(index)) {
			
			Level l = BYPASSAPP.levelDB.levelMap.get(index);
			
			if (l.isWon) {
				
				auxLab = new Label(l.grade);
				auxLab.color = Color.LIGHT_GRAY;
				auxLab.fontFile = APP.platform.fontResource("visitor1");
				auxLab.fontStyle = FontStyle.PLAIN;
				auxLab.fontSize = 36;
				auxLab.renderLocal();
				
				auxLab.aabb = new AABB(aabb.brX-auxLab.localAABB.width, aabb.brY-auxLab.localAABB.height, auxLab.localAABB.width, auxLab.localAABB.height);
				
			} else {
				auxLab = null;
			}
		} else {
			auxLab = null;
		}
	}

}
