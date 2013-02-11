package com.gutabi.deadlock.ui.paint;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ResourceImpl;
import com.gutabi.deadlock.geom.AABB;

public class FontEngineImpl implements FontEngine {
	
	public AABB bounds(String text, Resource fontFile, FontStyle fontStyle, int fontSize) {
		
//		FontRenderContext frc = new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		FontRenderContext frc = new FontRenderContext(null, false, false);
		
		ResourceImpl r = (ResourceImpl)fontFile;
	    InputStream is = this.getClass().getResourceAsStream(r.name);
	    
	    AABB aabb = null;
	    Font ttfBase;
		try {
			ttfBase = Font.createFont(Font.TRUETYPE_FONT, is);
			
			int s = -1;
			switch (fontStyle) {
			case PLAIN:
				s = Font.PLAIN;
				break;
			}
			
			Font ttfReal = ttfBase.deriveFont(s, fontSize);
			
			TextLayout layout = new TextLayout(text, ttfReal, frc);
			Rectangle2D bounds = layout.getBounds();
			aabb = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return aabb;
	}
	
}
