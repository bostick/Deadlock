package com.gutabi.deadlock;

import java.net.URL;

public class ResourceEngineImpl implements ResourceEngine {
	
	public Resource resource(String name) {
		
		URL url = DeadlockApplication.class.getResource("/img/" + name + ".png");
		
		return new ResourceImpl(url);
	}
	
}
