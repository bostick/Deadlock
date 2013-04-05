package com.gutabi.deadlock.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ResourceImpl;

public class ImageEngineImpl implements ImageEngine {
	
	Resources resources;
	
	public ImageEngineImpl(Resources resources) {
		this.resources = resources;
	}
	
	public Image readImage(Resource res) throws Exception {
		
		Integer id = ((ResourceImpl)res).id;
		
		Bitmap b = BitmapFactory.decodeResource(resources, id);
		
		return new ImageImpl(b);
	}
	
	public Image createTransparentImage(int width, int height) {
		
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		return new ImageImpl(b);
	}

}
