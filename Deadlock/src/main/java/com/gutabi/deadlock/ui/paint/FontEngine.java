package com.gutabi.deadlock.ui.paint;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;

public interface FontEngine {

	AABB bounds(String text, Resource fontFile, FontStyle fontStyle, int fontSize);
	
}
