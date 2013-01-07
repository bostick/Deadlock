package com.gutabi.deadlock.ui.paint;

import com.gutabi.deadlock.math.geom.AABB;

public interface FontEngine {

	AABB bounds(String text, String fontName, FontStyle fontStyle, int fontSize);
	
}
