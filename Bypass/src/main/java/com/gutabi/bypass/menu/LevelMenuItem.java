package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.bypass.level.Level;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.ui.Label;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.FontStyle;

public abstract class LevelMenuItem extends MenuItem {

	public int index;
	
	public LevelMenuItem(Menu menu, int index) {
		super(menu, Integer.toString(index));
		
		this.index = index;
	}
	
	public void render() {
		
		if (active) {
			lab.color = Color.WHITE;
		} else {
			lab.color = Color.GRAY;
		}
		
		lab.render();
		
		lab.aabb = new AABB(aabb.x + menu.columnWidth[c]/2 - lab.localAABB.width/2, aabb.y + (20 + lab.localAABB.height + 20)/2 - lab.localAABB.height/2, lab.localAABB.width, lab.localAABB.height);
		
		aabb = new AABB(aabb.x, aabb.y, menu.columnWidth[c], 20 + lab.aabb.height + 20);
		
		if (LevelMenu.levelDB.levelMap.keySet().contains(index)) {
			
			Level l = LevelMenu.levelDB.levelMap.get(index);
			
			if (l.isWon) {
				
				auxLab0 = new Label(l.grade);
				auxLab0.color = Color.LIGHT_GRAY;
				auxLab0.fontFile = APP.platform.fontResource("visitor1");
				auxLab0.fontStyle = FontStyle.PLAIN;
				auxLab0.fontSize = 36;
				auxLab0.renderLocal();
				
				auxLab0.aabb = new AABB(aabb.brX-auxLab0.localAABB.width, aabb.brY-auxLab0.localAABB.height, auxLab0.localAABB.width, auxLab0.localAABB.height);
				
				if (LevelMenu.showInfo) {
					auxLab2 = new Label(Integer.toString(l.userMoves));
					auxLab2.color = Color.LIGHT_GRAY;
					auxLab2.fontFile = APP.platform.fontResource("visitor1");
					auxLab2.fontStyle = FontStyle.PLAIN;
					auxLab2.fontSize = 36;
					auxLab2.renderLocal();
					
					auxLab2.aabb = new AABB(aabb.x, aabb.brY-auxLab2.localAABB.height, auxLab2.localAABB.width, auxLab2.localAABB.height);
				} else {
					auxLab2 = null;
				}
				
			} else {
				auxLab0 = null;
				auxLab2 = null;
			}
		} else {
			auxLab0 = null;
			auxLab2 = null;
		}
		
		if (LevelMenu.showInfo) {
			Level l;
			try {
				l = LevelMenu.levelDB.readLevel(index);
				auxLab1 = new Label(Integer.toString(l.requiredMoves));
				auxLab1.color = Color.LIGHT_GRAY;
				auxLab1.fontFile = APP.platform.fontResource("visitor1");
				auxLab1.fontStyle = FontStyle.PLAIN;
				auxLab1.fontSize = 36;
				auxLab1.renderLocal();
				
				auxLab1.aabb = new AABB(aabb.x, aabb.y, auxLab1.localAABB.width, auxLab1.localAABB.height);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			auxLab1 = null;
		}
		
		if (auxLab0 != null) {
			auxLab0.render();
		}
		if (auxLab1 != null) {
			auxLab1.render();
		}
		if (auxLab2 != null) {
			auxLab2.render();
		}
		
	}

}
