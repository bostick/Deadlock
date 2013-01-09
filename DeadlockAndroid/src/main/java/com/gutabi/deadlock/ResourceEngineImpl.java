package com.gutabi.deadlock;

public class ResourceEngineImpl implements ResourceEngine {

	public Resource resource(String name) {
		
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

}
