package com.brentonbostick.bypass.android.lite;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;
import android.content.Intent;
import android.content.res.Resources;

import com.brentonbostick.bypass.BypassApplication;
import com.brentonbostick.bypass.android.BypassAndroidPlatform;
import com.brentonbostick.bypass.android.R;
import com.brentonbostick.bypass.android.ResourceImpl;
import com.brentonbostick.bypass.android.ResourceType;
import com.brentonbostick.bypass.android.level.BypassWorldActivity;
import com.brentonbostick.bypass.android.menu.LevelMenuActivity;
import com.brentonbostick.bypass.level.BypassWorld;
import com.brentonbostick.bypass.level.LevelDB;
import com.brentonbostick.bypass.menu.LevelMenu;
import com.brentonbostick.bypass.menu.MainMenu;
import com.brentonbostick.capsloc.Resource;
import com.brentonbostick.capsloc.world.sprites.CarSheet;
import com.brentonbostick.capsloc.world.sprites.SpriteSheet;

public class BypassAndroidLitePlatform extends BypassAndroidPlatform {
	
	public LevelDB tutorialLevelDB;
	public LevelDB episode1LevelDB;
	
	public BypassAndroidLitePlatform(Resources resources) {
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
		}
		
		throw new AssertionError();
	}
	
	public String resourceName(Resource res) {
		
		ResourceImpl i = (ResourceImpl)res;
		
		if (i.resId == R.raw.tutorial) {
			return "tutorial";
		} else if (i.resId == R.raw.episode1) {
			return "episode1";
		}
		
		throw new AssertionError();
	}
	
	public void action(@SuppressWarnings("rawtypes") Class clazz, Object... args) {
		
		if (clazz == MainMenuLite.class) {
			
			Intent intent = new Intent(CURRENTACTIVITY, MainMenuActivity.class);
			CURRENTACTIVITY.startActivity(intent);
			
		} else if (clazz == LevelMenu.class) {
			
			Intent intent = new Intent(CURRENTACTIVITY, LevelMenuActivity.class);
			intent.putExtra("com.brentonbostick.bypass.android.PlatformImplClassName", "com.brentonbostick.bypass.android.lite.BypassAndroidLitePlatform");
			CURRENTACTIVITY.startActivity(intent);
			
		} else if (clazz == BypassWorld.class) {
			
			int ii = (Integer)args[0];
			
			Intent intent = new Intent(CURRENTACTIVITY, BypassWorldActivity.class);
			intent.putExtra("com.brentonbostick.bypass.android.PlatformImplClassName", "com.brentonbostick.bypass.android.lite.BypassAndroidLitePlatform");
			intent.putExtra("com.brentonbostick.bypass.level.Index", ii);
			CURRENTACTIVITY.startActivity(intent);
			
		} else {
			throw new AssertionError();
		}
		
	}

}
