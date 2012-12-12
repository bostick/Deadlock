package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

public class Preview {
	
	public static final int PREVIEW_WIDTH = 100;
	public static final int PREVIEW_HEIGHT = 100;
	
	public BufferedImage previewImage;
	
	public void init() {
		previewImage = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	public void dragged(InputEvent ev) {
		if (VIEW.previewPanel.penDragPreviewPoint != null) {
			
			double dx = VIEW.previewPanel.lastDragPreviewPoint.x - VIEW.previewPanel.penDragPreviewPoint.x;
			double dy = VIEW.previewPanel.lastDragPreviewPoint.y - VIEW.previewPanel.penDragPreviewPoint.y;
			
			pan(new Point(dx, dy));
			
			APP.render();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
		}
	}
	
	public Point previewToWorld(Point p) {
		return new Point((APP.world.worldWidth / PREVIEW_WIDTH) * p.x, (APP.world.worldHeight / PREVIEW_HEIGHT) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		return new Point((PREVIEW_WIDTH / APP.world.worldWidth) * p.x, (PREVIEW_HEIGHT / APP.world.worldHeight) * p.y);
	}
	
	public void pan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		APP.world.worldViewport = new AABB(
				APP.world.worldViewport.x + worldDP.x,
				APP.world.worldViewport.y + worldDP.y,
				APP.world.worldViewport.width,
				APP.world.worldViewport.height);
	}
	
	public void render() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		previewImageG2.setColor(Color.WHITE);
		previewImageG2.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		previewImageG2.scale(PREVIEW_WIDTH / APP.world.worldWidth, PREVIEW_HEIGHT / APP.world.worldHeight);
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		APP.world.map.renderBackground(previewContext);
		
		APP.world.graph.renderBackground(previewContext);
		
		previewImageG2.dispose();
		
	}
	
	public void paint(Graphics2D g2) {
		
		if (previewImage != null) {
			
			g2.drawImage(previewImage, 0, 0, null);
			
			Point prevLoc = worldToPreview(APP.world.worldViewport.ul);
			
			Point prevDim = worldToPreview(new Point(APP.world.worldViewport.width, APP.world.worldViewport.height));
			
			g2.setColor(Color.BLUE);
			g2.drawRect((int)prevLoc.x, (int)prevLoc.y, (int)prevDim.x, (int)prevDim.y);
			
		}
		
	}
	
}
