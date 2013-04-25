package com.gutabi.bypass.level;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontStyle;

public class WinnerMenu extends Menu {
	
	public boolean ready;
	
	public static void action() {
		
		BypassWorld world = (BypassWorld)APP.model;
		
		String excl;
		char letter = world.curLevel.grade.charAt(0);
		if (letter == 'A') {
			excl = "Excellent!";
		} else if (letter == 'B') {
			excl = "Good!";
		} else if (letter == 'C') {
			excl = "OK!";
		} else if (letter == 'D') {
			excl = "Meh";
		} else {
			excl = "Effort!";
		}
		
		Resource visitorFontFile = APP.platform.fontResource("visitor1");
		world.winnerLabel = new Label(excl, 0, 0);
		world.winnerLabel.fontFile = visitorFontFile;
		world.winnerLabel.fontStyle = FontStyle.PLAIN;
		world.winnerLabel.fontSize = 72;
		world.winnerLabel.color = Color.WHITE;
		
		world.gradeLabel = new Label("Grade: " + world.curLevel.grade, 0, 0);
		world.gradeLabel.fontFile = visitorFontFile;
		world.gradeLabel.fontStyle = FontStyle.PLAIN;
		world.gradeLabel.fontSize = 72;
		world.gradeLabel.color = Color.WHITE;
		
		world.winnerMenu = new WinnerMenu();
		
		world.winnerLabel.renderLocal();
		world.winnerLabel.render();
		world.gradeLabel.renderLocal();
		world.gradeLabel.render();
		world.winnerMenu.render();
		
		APP.appScreen.postDisplay();
		
		APP.tool = new MenuTool();
	}
	
	public WinnerMenu() {
		
		BypassWorld world = (BypassWorld)APP.model;
		
		int index = world.curLevel.index;
		
		MenuItem nextMenuItem = new MenuItem(WinnerMenu.this, "Next") {
			public void action() {
				
				BypassWorld world = (BypassWorld)APP.model;
				
				int index = world.curLevel.index;
				
				APP.platform.finishAction();
				
				APP.platform.action(BypassWorld.class, index+1);
				
			}
		};
		
		MenuItem againMenuItem = new MenuItem(WinnerMenu.this, "Try Again") {
			public void action() {
				
				BypassWorld world = (BypassWorld)APP.model;
				
				int index = world.curLevel.index;
				
				APP.platform.finishAction();
				
				APP.platform.action(BypassWorld.class, index);
				
			}
		};
		
		MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back") {
			public void action() {
				
				APP.platform.finishAction();
			}
		};
		
		if (index < BYPASSAPP.levelDB.levelCount-1) {
			
			add(nextMenuItem, 0, 0);
			add(againMenuItem, 1, 0);
			add(backMenuItem, 2, 0);
			
		} else {
			
			add(againMenuItem, 0, 0);
			add(backMenuItem, 1, 0);
			
		}
		
	}
	
	public void escape() {
		APP.platform.finishAction();
	}
	
}
