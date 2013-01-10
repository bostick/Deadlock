package com.gutabi.deadlock;

import android.graphics.Typeface;

public class ResourceImpl implements Resource {
	
	public int id;
	public Typeface face;
	
	public ResourceImpl(int id) {
		this.id = id;
	}
	
	public ResourceImpl(Typeface face) {
		this.face = face;
	}
}
