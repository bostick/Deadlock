package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

public class Preview {
	
	public static final int PREVIEW_WIDTH = 100;
	public static final int PREVIEW_HEIGHT = 100;
	
	public final World world;
	
	public BufferedImage previewImage;
	
	public Preview(World world) {
		this.world = world;
	}
	
	public void init() {
		previewImage = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	public void dragged(InputEvent ev) {
		if (VIEW.previewPanel.penDragPreviewPoint != null) {
			
			double dx = VIEW.previewPanel.lastDragPreviewPoint.x - VIEW.previewPanel.penDragPreviewPoint.x;
			double dy = VIEW.previewPanel.lastDragPreviewPoint.y - VIEW.previewPanel.penDragPreviewPoint.y;
			
			pan(new Point(dx, dy));
			
			world.render();
			world.repaint();
		}
	}
	
	public Point previewToWorld(Point p) {
		
		double pixelsPerMeterWidth = PREVIEW_WIDTH / world.worldWidth;
		double pixelsPerMeterHeight = PREVIEW_HEIGHT / world.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((1/s) * p.x, (1/s) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		
		double pixelsPerMeterWidth = PREVIEW_WIDTH / world.worldWidth;
		double pixelsPerMeterHeight = PREVIEW_HEIGHT / world.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((s) * p.x, (s) * p.y);
	}
	
	public void pan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		world.worldViewport = new AABB(
				world.worldViewport.x + worldDP.x,
				world.worldViewport.y + worldDP.y,
				world.worldViewport.width,
				world.worldViewport.height);
	}
	
	public void render() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		previewImageG2.setColor(Color.LIGHT_GRAY);
		previewImageG2.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		double pixelsPerMeterWidth = PREVIEW_WIDTH / world.worldWidth;
		double pixelsPerMeterHeight = PREVIEW_HEIGHT / world.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		AffineTransform origTrans = previewContext.getTransform();
		previewContext.translate(PREVIEW_WIDTH/2 - (s * world.worldWidth / 2), PREVIEW_HEIGHT/2 - (s * world.worldHeight / 2));
		
		previewImageG2.scale(s, s);
		
		world.quadrantMap.renderBackground(previewContext);
		
		world.graph.renderBackground(previewContext);
		
		previewContext.setTransform(origTrans);
		
		previewImageG2.dispose();
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (previewImage != null) {
			
			ctxt.paintImage(
					0, 0,
					previewImage,
					0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT,
					0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
			
			Point prevLoc = worldToPreview(world.worldViewport.ul);
			
			Point prevDim = worldToPreview(new Point(world.worldViewport.width, world.worldViewport.height));
			
			AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
			
			double pixelsPerMeterWidth = PREVIEW_WIDTH / world.worldWidth;
			double pixelsPerMeterHeight = PREVIEW_HEIGHT / world.worldHeight;
			double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
			
			AffineTransform origTrans = ctxt.getTransform();
			ctxt.translate(PREVIEW_WIDTH/2 - (s * world.worldWidth / 2), PREVIEW_HEIGHT/2 - (s * world.worldHeight / 2));
			
			ctxt.setColor(Color.BLUE);
			prev.draw(ctxt);
			
			ctxt.setTransform(origTrans);
			
		}
		
	}
	
}
