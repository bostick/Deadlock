package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

public class Preview {
	
	public final WorldScreen screen;
	
	public int previewWidth;
	public int previewHeight;
	
	public BufferedImage previewImage;
	
	public Preview(WorldScreen screen) {
		this.screen = screen;
	}
	
	public void previewPostDisplay(Dim dim) {
		
		previewWidth = (int)dim.width;
		previewHeight = (int)dim.height;
		
		previewImage = new BufferedImage(previewWidth, previewHeight, BufferedImage.TYPE_INT_RGB);
		
	}
	
	public void dragged(InputEvent ev) {
		if (VIEW.previewPanel.penDragPreviewPoint != null) {
			
			double dx = VIEW.previewPanel.lastDragPreviewPoint.x - VIEW.previewPanel.penDragPreviewPoint.x;
			double dy = VIEW.previewPanel.lastDragPreviewPoint.y - VIEW.previewPanel.penDragPreviewPoint.y;
			
			pan(new Point(dx, dy));
			
			screen.render();
			screen.repaint();
		}
	}
	
	public Point previewToWorld(Point p) {
		
		double pixelsPerMeterWidth = previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((1/s) * p.x, (1/s) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		
		double pixelsPerMeterWidth = previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((s) * p.x, (s) * p.y);
	}
	
	public void pan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		screen.world.worldViewport = new AABB(
				screen.world.worldViewport.x + worldDP.x,
				screen.world.worldViewport.y + worldDP.y,
				screen.world.worldViewport.width,
				screen.world.worldViewport.height);
	}
	
	public void render() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		previewImageG2.setColor(Color.LIGHT_GRAY);
		previewImageG2.fillRect(0, 0, previewWidth, previewHeight);
		
		double pixelsPerMeterWidth = previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		AffineTransform origTrans = previewContext.getTransform();
		previewContext.translate(previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2), previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
		
		previewImageG2.scale(s, s);
		
		screen.world.quadrantMap.render(previewContext);
		
		screen.world.graph.render(previewContext);
		
		previewContext.setTransform(origTrans);
		
		previewImageG2.dispose();
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (previewImage != null) {
			
			ctxt.paintImage(
					0, 0,
					previewImage,
					0, 0, previewWidth, previewHeight,
					0, 0, previewWidth, previewHeight);
			
			Point prevLoc = worldToPreview(screen.world.worldViewport.ul);
			
			Point prevDim = worldToPreview(new Point(screen.world.worldViewport.width,screen. world.worldViewport.height));
			
			AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
			
			double pixelsPerMeterWidth = previewWidth / screen.world.quadrantMap.worldWidth;
			double pixelsPerMeterHeight = previewHeight / screen.world.quadrantMap.worldHeight;
			double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
			
			AffineTransform origTrans = ctxt.getTransform();
			ctxt.translate(previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2), previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
			
			ctxt.setColor(Color.BLUE);
			prev.draw(ctxt);
			
			ctxt.setTransform(origTrans);
			
		}
		
	}
	
}
