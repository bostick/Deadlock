package com.gutabi.deadlock;

import java.net.URL;

public class ResourceImpl implements Resource {
	
	public URL url;
	
	public ResourceImpl(Object arg) {
		
		this.url = (URL)arg;
		
	}
	
}
