package com.gutabi.bypass.android.full;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;
import android.content.Intent;
import android.content.res.Resources;

import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.android.BypassAndroidPlatform;
import com.gutabi.bypass.android.ResourceImpl;
import com.gutabi.bypass.android.ResourceType;
import com.gutabi.bypass.android.level.BypassWorldActivity;
import com.gutabi.bypass.android.menu.LevelMenuActivity;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.bypass.menu.MainMenuFull;
import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.world.sprites.CarSheet;
import com.gutabi.capsloc.world.sprites.SpriteSheet;

public class BypassAndroidFullPlatform extends BypassAndroidPlatform {
	
	public LevelDB tutorialLevelDB;
	public LevelDB episode1LevelDB;
	public LevelDB episode2LevelDB;
	
	public BypassAndroidFullPlatform(Resources resources) {
		super(resources);
	}
	
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
			throw new AssertionError();
		}
		
	}
	
	public Resource imageResource(String name) {
		
		if (name.equals("carsheet")) {
			return new ResourceImpl(R.drawable.carsheet, ResourceType.DRAWABLE);
		} else if (name.equals("spritesheet")) {
			return new ResourceImpl(R.drawable.spritesheet, ResourceType.DRAWABLE);
		} else if (name.equals("copyright")) {
			return new ResourceImpl(R.drawable.copyright, ResourceType.DRAWABLE);
		} else if (name.equals("logo")) {
			return new ResourceImpl(R.drawable.logo, ResourceType.DRAWABLE);
		}
		
		throw new AssertionError();
	}
	
	public Resource levelDBResource(String name) {
		
		if (name.equals("tutorial")) {
			return new ResourceImpl(R.raw.tutorial, ResourceType.RAW);
		} else if (name.equals("episode1")) {
			return new ResourceImpl(R.raw.episode1, ResourceType.RAW);
		} else if (name.equals("episode2")) {
			return new ResourceImpl(R.raw.episode2, ResourceType.RAW);
		}
		
		throw new AssertionError();
	}
	
	public String resourceName(Resource res) {
		
		ResourceImpl i = (ResourceImpl)res;
		
		if (i.resId == R.raw.tutorial) {
			return "tutorial";
		} else if (i.resId == R.raw.episode1) {
			return "episode1";
		} else if (i.resId == R.raw.episode2) {
			return "episode2";
		}
		
		throw new AssertionError();
	}
	
	public void action(@SuppressWarnings("rawtypes") Class clazz, Object... args) {
		
		if (clazz == MainMenuFull.class) {
			
			Intent intent = new Intent(CURRENTACTIVITY, MainMenuActivity.class);
			CURRENTACTIVITY.startActivity(intent);
			
		} else if (clazz == LevelMenu.class) {
			
			Intent intent = new Intent(CURRENTACTIVITY, LevelMenuActivity.class);
			intent.putExtra("com.gutabi.bypass.android.PlatformImplClassName", "com.gutabi.bypass.android.full.BypassAndroidFullPlatform");
			CURRENTACTIVITY.startActivity(intent);
			
		} else if (clazz == BypassWorld.class) {
			
			int ii = (Integer)args[0];
			
			Intent intent = new Intent(CURRENTACTIVITY, BypassWorldActivity.class);
			intent.putExtra("com.gutabi.bypass.android.PlatformImplClassName", "com.gutabi.bypass.android.full.BypassAndroidFullPlatform");
			intent.putExtra("com.gutabi.bypass.level.Index", ii);
			CURRENTACTIVITY.startActivity(intent);
			
		} else {
			throw new AssertionError();
		}
		
	}

}
