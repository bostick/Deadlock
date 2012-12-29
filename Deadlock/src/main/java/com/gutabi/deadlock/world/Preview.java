package com.gutabi.deadlock.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.ui.RenderingContextType;

public class Preview {
	
	public WorldScreen screen;
	
	public PreviewCamera previewCam = new PreviewCamera();
	
	public BufferedImage previewImage;
	
	AABB aabb = new AABB(0, 0, 100, 100);
	
	public Preview(WorldScreen screen) {
		this.screen = screen;
		
		previewCam.previewWidth = 100;
		previewCam.previewHeight = 100;
		
		previewImage = new BufferedImage(previewCam.previewWidth, previewCam.previewHeight, BufferedImage.TYPE_INT_RGB);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public Point lastPressPreviewPoint;
	public Point lastDragPreviewPoint;
	public Point penDragPreviewPoint;
	long lastPressTime;
	long lastDragTime;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressPreviewPoint = p;
		lastPressTime = System.currentTimeMillis();
		
		lastDragPreviewPoint = null;
		lastDragTime = -1;
		
	}
	
	public void dragged(InputEvent ev) {
		
		Point p = ev.p;
		
		penDragPreviewPoint = lastDragPreviewPoint;
		lastDragPreviewPoint = p;
		lastDragTime = System.currentTimeMillis();
		
		if (penDragPreviewPoint != null) {
			
			double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
			double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
			
			pan(new Point(dx, dy));
			
			screen.render();
			screen.canvas.repaint();
			screen.controlPanel.repaint();
//			screen.controlPanel.previewPanel.repaint();
		}
	}
	
	public Point controlPanelToPreview(Point p) {
		return new Point(p.x - aabb.x, p.y - aabb.y);
	}
	
	public Point previewToWorld(Point p) {
		
		double pixelsPerMeterWidth = previewCam.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewCam.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((1/s) * p.x, (1/s) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		
		double pixelsPerMeterWidth = previewCam.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewCam.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((s) * p.x, (s) * p.y);
	}
	
	public void pan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		screen.cam.worldViewport = new AABB(
				screen.cam.worldViewport.x + worldDP.x,
				screen.cam.worldViewport.y + worldDP.y,
				screen.cam.worldViewport.width,
				screen.cam.worldViewport.height);
	}
	
	public void render() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		RenderingContext previewContext = new RenderingContext(RenderingContextType.PREVIEW);
		previewContext.g2 = previewImageG2;
		
		previewImageG2.setColor(Color.LIGHT_GRAY);
		previewImageG2.fillRect(0, 0, previewCam.previewWidth, previewCam.previewHeight);
		
		double pixelsPerMeterWidth = previewCam.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewCam.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		AffineTransform origTrans = previewContext.getTransform();
		previewContext.translate(previewCam.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2), previewCam.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
		
		previewImageG2.scale(s, s);
		
		screen.world.quadrantMap.render(previewContext);
		
		screen.world.graph.render(previewContext);
		
		previewContext.setTransform(origTrans);
		
		previewImageG2.dispose();
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (previewImage != null) {
			
			AffineTransform origTrans = ctxt.getTransform();
			
			ctxt.translate(aabb.x, aabb.y);
			
			ctxt.paintImage(previewImage,
					0, 0, (int)aabb.width, (int)aabb.height,
					0, 0, previewCam.previewWidth, previewCam.previewHeight);
			
			Point prevLoc = worldToPreview(screen.cam.worldViewport.ul);
			
			Point prevDim = worldToPreview(new Point(screen.cam.worldViewport.width, screen.cam.worldViewport.height));
			
			AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
			
			double pixelsPerMeterWidth = previewCam.previewWidth / screen.world.quadrantMap.worldWidth;
			double pixelsPerMeterHeight = previewCam.previewHeight / screen.world.quadrantMap.worldHeight;
			double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
			
			ctxt.translate(
					previewCam.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2),
					previewCam.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
			
			ctxt.setColor(Color.BLUE);
			prev.draw(ctxt);
			
			ctxt.setTransform(origTrans);
			
		}
		
	}
	
}
