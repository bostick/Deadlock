package com.gutabi.deadlock.ui.paint;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import com.gutabi.deadlock.math.geom.AABB;

public class FontEngineImpl implements FontEngine {
	
	public AABB bounds(String text, String fontName, FontStyle fontStyle, int fontSize) {
		
//		FontRenderContext frc = new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		FontRenderContext frc = new FontRenderContext(null, false, false);
		
		int s = -1;
		switch (fontStyle) {
		case PLAIN:
			s = Font.PLAIN;
			break;
		}
		
		TextLayout layout = new TextLayout(text, new Font(fontName, s, fontSize), frc);
		Rectangle2D bounds = layout.getBounds();
		AABB aabb = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		return aabb;
	}
	
}
