package com.brentonbostick.bypass.awt.full;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;

import javax.imageio.ImageIO;

import com.brentonbostick.bypass.BypassApplication;
import com.brentonbostick.bypass.awt.BypassAWTPlatform;
import com.brentonbostick.bypass.level.BypassWorld;
import com.brentonbostick.bypass.level.LevelDB;
import com.brentonbostick.bypass.menu.BypassMenu;
import com.brentonbostick.bypass.menu.LevelMenu;
import com.brentonbostick.bypass.menu.MainMenu;
import com.brentonbostick.bypass.menu.MainMenuFull;
import com.brentonbostick.capsloc.world.sprites.CarSheet;
import com.brentonbostick.capsloc.world.sprites.SpriteSheet;

public class BypassAWTFullPlatform extends BypassAWTPlatform {
	
	public LevelDB tutorialLevelDB;
	public LevelDB episode1LevelDB;
	public LevelDB episode2LevelDB;
	
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
			episode2LevelDB = new LevelDB(levelDBResource("episode2"));
			
			loadScores(tutorialLevelDB);
			tutorialLevelDB.setFirstUnwon();
			
			loadScores(episode1LevelDB);
			episode1LevelDB.setFirstUnwon();
			
			loadScores(episode2LevelDB);
			episode2LevelDB.setFirstUnwon();
			
			tutorialLevelDB.computePercentageComplete();
			episode1LevelDB.computePercentageComplete();
			episode2LevelDB.computePercentageComplete();
			
			tutorialLevelDB.title = " Tutorial ";
			episode1LevelDB.title = " Episode 1 ";
			episode2LevelDB.title = " Episode 2 ";
			
			MainMenu.MAINMENU = new MainMenuFull();
			
			LevelMenu.map.put("tutorial", new LevelMenu("tutorial"));
			LevelMenu.map.put("episode1", new LevelMenu("episode1"));
			LevelMenu.map.put("episode2", new LevelMenu("episode2"));
			
			titleBarIcon = ImageIO.read(this.getClass().getResource("/icon72x72.png"));
			
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
		} else if (name.equals("episode2")) {
			return episode2LevelDB;
		} else {
			throw new AssertionError("name: " + name);
		}
		
	}
	
	public void action(@SuppressWarnings("rawtypes")Class newClazz, Object... args) {
		
		@SuppressWarnings("rawtypes")
		Class oldClazz = CURRENTACTIVITYCLASS;
		if (oldClazz == null) {
			
		} else if (oldClazz == MainMenuFull.class) {
			
			BypassMenu.pause();
			
		} else if (oldClazz == LevelMenu.class) {
			
			BypassMenu.pause();
			
		} else if (oldClazz == BypassWorld.class) {
			
			BypassWorld.pause();
			
		} else {
			throw new AssertionError();
		}
		
		
		if (newClazz == MainMenuFull.class) {
			
			CURRENTACTIVITYCLASS = MainMenuFull.class;
			
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
		if (oldClazz == MainMenuFull.class) {
			
			BypassMenu.pause();
			MainMenu.stop();
			MainMenu.destroy();
			
			CURRENTACTIVITYCLASS = null;
			
		} else if (oldClazz == LevelMenu.class) {
			
			BypassMenu.pause();
			LevelMenu.stop();
			LevelMenu.destroy();
			
			CURRENTACTIVITYCLASS = MainMenuFull.class;
			
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
