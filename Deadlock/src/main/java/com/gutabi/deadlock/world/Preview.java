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
	
	public PreviewCamera cam = new PreviewCamera();
	
	public BufferedImage previewImage;
	
	public Preview(WorldScreen screen) {
		this.screen = screen;
	}
	
	public void previewPostDisplay(Dim dim) {
		
		cam.previewWidth = (int)dim.width;
		cam.previewHeight = (int)dim.height;
		
		previewImage = new BufferedImage(cam.previewWidth, cam.previewHeight, BufferedImage.TYPE_INT_RGB);
		
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
		
		double pixelsPerMeterWidth = cam.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = cam.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((1/s) * p.x, (1/s) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		
		double pixelsPerMeterWidth = cam.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = cam.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((s) * p.x, (s) * p.y);
	}
	
	public void pan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		screen.world.cam.worldViewport = new AABB(
				screen.world.cam.worldViewport.x + worldDP.x,
				screen.world.cam.worldViewport.y + worldDP.y,
				screen.world.cam.worldViewport.width,
				screen.world.cam.worldViewport.height);
	}
	
	public void render() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		previewImageG2.setColor(Color.LIGHT_GRAY);
		previewImageG2.fillRect(0, 0, cam.previewWidth, cam.previewHeight);
		
		double pixelsPerMeterWidth = cam.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = cam.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		AffineTransform origTrans = previewContext.getTransform();
		previewContext.translate(cam.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2), cam.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
		
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
					0, 0, cam.previewWidth, cam.previewHeight,
					0, 0, cam.previewWidth, cam.previewHeight);
			
			Point prevLoc = worldToPreview(screen.world.cam.worldViewport.ul);
			
			Point prevDim = worldToPreview(new Point(screen.world.cam.worldViewport.width,screen. world.cam.worldViewport.height));
			
			AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
			
			double pixelsPerMeterWidth = cam.previewWidth / screen.world.quadrantMap.worldWidth;
			double pixelsPerMeterHeight = cam.previewHeight / screen.world.quadrantMap.worldHeight;
			double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
			
			AffineTransform origTrans = ctxt.getTransform();
			ctxt.translate(cam.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2), cam.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
			
			ctxt.setColor(Color.BLUE);
			prev.draw(ctxt);
			
			ctxt.setTransform(origTrans);
			
		}
		
	}
	
}
