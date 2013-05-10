package com.gutabi.bypass.android.full;

import android.content.Intent;
import android.content.res.Resources;

import com.gutabi.bypass.android.BypassAndroidPlatform;
import com.gutabi.bypass.android.ResourceImpl;
import com.gutabi.bypass.android.ResourceType;
import com.gutabi.bypass.android.level.BypassWorldActivity;
import com.gutabi.bypass.android.menu.LevelMenuActivity;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.bypass.menu.MainMenu;
import com.gutabi.capsloc.Resource;

public class BypassAndroidFullPlatform extends BypassAndroidPlatform {
	
	public BypassAndroidFullPlatform(Resources resources) {
		super(resources);
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
		
		if (clazz == MainMenu.class) {
			
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
