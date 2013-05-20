package com.brentonbostick.bypass.android;

import java.net.URL;

import android.graphics.Typeface;

import com.brentonbostick.capsloc.Resource;

public class ResourceImpl implements Resource {
	
	public int resId;
	public ResourceType resType;
	
	public Typeface face;
	public URL url;
	
	public ResourceImpl(int resId, ResourceType resType) {
		this.resId = resId;
		this.resType = resType;
	}
	
	public ResourceImpl(Typeface face) {
		this.face = face;
	}
	
	public ResourceImpl(URL url) {
		this.url = url;
	}
	
}
