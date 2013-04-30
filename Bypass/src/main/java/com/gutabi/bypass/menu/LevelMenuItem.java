package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;
import static com.gutabi.bypass.BypassApplication.BYPASSAPP;

import com.gutabi.bypass.level.Level;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.ui.Label;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.FontStyle;

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
