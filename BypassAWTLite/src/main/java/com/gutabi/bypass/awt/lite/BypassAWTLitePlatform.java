package com.gutabi.bypass.awt.lite;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.awt.BypassAWTPlatform;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.bypass.menu.BypassMenu;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.bypass.menu.MainMenu;
import com.gutabi.bypass.menu.MainMenuLite;
import com.gutabi.capsloc.world.sprites.CarSheet;
import com.gutabi.capsloc.world.sprites.SpriteSheet;

public class BypassAWTLitePlatform extends BypassAWTPlatform {
	
	public LevelDB tutorialLevelDB;
	public LevelDB episode1LevelDB;
	
	public void createApplication() throws Exception {
		
		BypassApplication app = new BypassApplication();
		APP = app;
		BYPASSAPP = app;
		
		APP.platform = this;
		BYPASSAPP.bypassPlatform = this;
		
		try {
			
			APP.carSheet = new CarSheet();
			APP.spriteSheet = new SpriteSheet();
			
			APP.carSheet.load();
			APP.spriteSheet.load();
			
			tutorialLevelDB = new LevelDB(levelDBResource("tutorial"));
			episode1LevelDB = new LevelDB(levelDBResource("episode1"));
			
			loadScores(tutorialLevelDB);
			tutorialLevelDB.setFirstUnwon();
			
			loadScores(episode1LevelDB);
			episode1LevelDB.setFirstUnwon();
			
			tutorialLevelDB.computePercentageComplete();
			episode1LevelDB.computePercentageComplete();
			
			tutorialLevelDB.title = " Tutorial ";
			episode1LevelDB.title = " Episode 1 ";
			
			MainMenu.MAINMENU = new MainMenuLite();
			
			LevelMenu.map.put("tutorial", new LevelMenu("tutorial"));
			LevelMenu.map.put("episode1", new LevelMenu("episode1"));
			
			APP.platform.showAppScreen();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	public LevelDB levelDB(String name) {
		
		if (name.equals("tutorial")) {
			return tutorialLevelDB;
		} else if (name.equals("episode1")) {
			return episode1LevelDB;
		} else {
			throw new AssertionError("name: " + name);
		}
		
	}
	
	public void action(@SuppressWarnings("rawtypes")Class newClazz, Object... args) {
		
		@SuppressWarnings("rawtypes")
		Class oldClazz = CURRENTACTIVITYCLASS;
		if (oldClazz == null) {
			
		} else if (oldClazz == MainMenuLite.class) {
			
			BypassMenu.pause();
			
		} else if (oldClazz == LevelMenu.class) {
			
			BypassMenu.pause();
			
		} else if (oldClazz == BypassWorld.class) {
			
			BypassWorld.pause();
			
		} else {
			throw new AssertionError();
		}
		
		
		if (newClazz == MainMenuLite.class) {
			
			CURRENTACTIVITYCLASS = MainMenuLite.class;
			
			MainMenu.create();
			MainMenu.start();
			BypassMenu.resume();
			BypassMenu.surfaceChanged(BypassAWTPlatform.MAINWINDOW_WIDTH, BypassAWTPlatform.MAINWINDOW_HEIGHT);
			
		} else if (newClazz == LevelMenu.class) {
			
			CURRENTACTIVITYCLASS = LevelMenu.class;
			
			LevelMenu.create();
			LevelMenu.start();
			BypassMenu.resume();
			BypassMenu.surfaceChanged(BypassAWTPlatform.MAINWINDOW_WIDTH, BypassAWTPlatform.MAINWINDOW_HEIGHT);
			
		} else if (newClazz == BypassWorld.class) {
			
			CURRENTACTIVITYCLASS = BypassWorld.class;
			
			int ii = (Integer)args[0];
			
			LevelDB levelDB = levelDB(LevelMenu.levelDBName);
			
			BypassWorld.create(levelDB, ii);
			BypassWorld.start();
			BypassWorld.resume();
			BypassWorld.surfaceChanged(BypassAWTPlatform.MAINWINDOW_WIDTH, BypassAWTPlatform.MAINWINDOW_HEIGHT);
			
		} else {
			throw new AssertionError();
		}
		
	}
	
	public void finishAction() {
		
		@SuppressWarnings("rawtypes")
		Class oldClazz = CURRENTACTIVITYCLASS;
		if (oldClazz == MainMenuLite.class) {
			
			BypassMenu.pause();
			MainMenu.stop();
			MainMenu.destroy();
			
			CURRENTACTIVITYCLASS = null;
			
		} else if (oldClazz == LevelMenu.class) {
			
			BypassMenu.pause();
			LevelMenu.stop();
			LevelMenu.destroy();
			
			CURRENTACTIVITYCLASS = MainMenuLite.class;
			
			MainMenu.start();
			BypassMenu.resume();
			BypassMenu.surfaceChanged(BypassAWTPlatform.MAINWINDOW_WIDTH, BypassAWTPlatform.MAINWINDOW_HEIGHT);
			
		} else if (oldClazz == BypassWorld.class) {
			
			BypassWorld.pause();
			BypassWorld.stop();
			BypassWorld.destroy();
			
			CURRENTACTIVITYCLASS = LevelMenu.class;
			
			LevelMenu.start();
			BypassMenu.resume();
			BypassMenu.surfaceChanged(BypassAWTPlatform.MAINWINDOW_WIDTH, BypassAWTPlatform.MAINWINDOW_HEIGHT);
			
		} else {
			throw new AssertionError();
		} 
		
	}
	
}
