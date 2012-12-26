package com.gutabi.deadlock.world;

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
	
	public PreviewCamera previewCam = new PreviewCamera();
	
	public BufferedImage previewImage;
	
	public Preview(WorldScreen screen) {
		this.screen = screen;
	}
	
	public void previewPostDisplay(Dim dim) {
		
		previewCam.previewWidth = (int)dim.width;
		previewCam.previewHeight = (int)dim.height;
		
		previewImage = new BufferedImage(previewCam.previewWidth, previewCam.previewHeight, BufferedImage.TYPE_INT_RGB);
		
	}
	
	public void dragged(InputEvent ev) {
		if (screen.controlPanel.previewPanel.penDragPreviewPoint != null) {
			
			double dx = screen.controlPanel.previewPanel.lastDragPreviewPoint.x - screen.controlPanel.previewPanel.penDragPreviewPoint.x;
			double dy = screen.controlPanel.previewPanel.lastDragPreviewPoint.y - screen.controlPanel.previewPanel.penDragPreviewPoint.y;
			
			pan(new Point(dx, dy));
			
			screen.render();
			screen.repaintCanvas();
			screen.repaintControlPanel();
			
			screen.controlPanel.previewPanel.repaint();
		}
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
			
			ctxt.paintImage(
					1.0,
					previewImage,
					0, 0, previewCam.previewWidth, previewCam.previewHeight,
					0, 0, previewCam.previewWidth, previewCam.previewHeight);
			
			Point prevLoc = worldToPreview(screen.cam.worldViewport.ul);
			
			Point prevDim = worldToPreview(new Point(screen.cam.worldViewport.width, screen.cam.worldViewport.height));
			
			AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
			
			double pixelsPerMeterWidth = previewCam.previewWidth / screen.world.quadrantMap.worldWidth;
			double pixelsPerMeterHeight = previewCam.previewHeight / screen.world.quadrantMap.worldHeight;
			double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
			
			AffineTransform origTrans = ctxt.getTransform();
			ctxt.translate(previewCam.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2), previewCam.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
			
			ctxt.setColor(Color.BLUE);
			prev.draw(ctxt);
			
			ctxt.setTransform(origTrans);
			
		}
		
	}
	
}
