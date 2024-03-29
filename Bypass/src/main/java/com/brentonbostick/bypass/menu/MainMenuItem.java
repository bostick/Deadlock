package com.brentonbostick.bypass.menu;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.bypass.level.LevelDB;
import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.ui.Label;
import com.brentonbostick.capsloc.ui.Menu;
import com.brentonbostick.capsloc.ui.MenuItem;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.FontStyle;

import java.util.Locale;

public abstract class MainMenuItem extends MenuItem {
	
	LevelDB levelDB;
	
	public MainMenuItem(Menu menu, LevelDB levelDB) {
		super(menu, levelDB.title);
		
		this.levelDB = levelDB;
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
		
		if (levelDB.percentage != 0.0) {
			String pString = String.format(Locale.US, "%.2f%%", levelDB.percentage * 100);
			
			auxLab1 = new Label(pString);
			auxLab1.color = Color.LIGHT_GRAY;
			auxLab1.fontFile = APP.platform.fontResource("visitor1");
			auxLab1.fontStyle = FontStyle.PLAIN;
			auxLab1.fontSize = 36;
			auxLab1.renderLocal();
			
			auxLab1.aabb = new AABB(aabb.brX-auxLab1.localAABB.width, aabb.brY-auxLab1.localAABB.height, auxLab1.localAABB.width, auxLab1.localAABB.height);
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
