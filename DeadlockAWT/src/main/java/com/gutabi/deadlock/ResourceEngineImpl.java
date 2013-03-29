package com.gutabi.deadlock;

public class ResourceEngineImpl implements ResourceEngine {
	
	public Resource imageResource(String name) {
		
		String full = "/img/" + name + ".png";
		
		return new ResourceImpl(full);
	}
	
	public Resource fontResource(String name) {
		
		String full = "/fonts/" + name + ".ttf";
		
		return new ResourceImpl(full);
	}

}
