package com.gutabi.deadlock;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;

public class ResourceEngineImpl implements ResourceEngine {
	
	Resources resources;
	
	public ResourceEngineImpl(Resources resources) {
		this.resources = resources;
	}
	
	public Resource imageResource(String name) {
		
		if (name.equals("carsheet")) {
			return new ResourceImpl(R.drawable.carsheet);
		} else if (name.equals("spritesheet")) {
			return new ResourceImpl(R.drawable.spritesheet);
		} else if (name.equals("explosionsheet")) {
			return new ResourceImpl(R.drawable.explosionsheet);
		} else if (name.equals("title_background")) {
			return new ResourceImpl(R.drawable.title_background);
		} else if (name.equals("title_white")) {
			return new ResourceImpl(R.drawable.title_white);
		} else if (name.equals("copyright")) {
			return new ResourceImpl(R.drawable.copyright);
		}
		
		return null;
	}

	public Resource fontResource(String name) {
		
		AssetManager am = resources.getAssets();
		
		if (name.equals("visitor1")) {
			return new ResourceImpl(Typeface.createFromAsset(am, "fonts/" + name + ".ttf"));
		}
		
		return null;
	}
}
