package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.utils.ImageUtils;

public class DeadlockView {
	
	public JFrame frame;
	public WorldPanel panel;
	public ControlPanel controlPanel;
	
	public BufferedImage backgroundImage;
	
	public double worldViewLocInitX;
	public double worldViewLocInitY;
	
	public BufferedImage normalCar;
	public BufferedImage fastCar;
	public BufferedImage crash;
	public BufferedImage tiledGrass;
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public DeadlockView() {
		;
	}
	
	public void init() throws Exception {
		frame = createFrame(false);
		
		worldViewLocInitX = (panel.getWidth()/2 - (MODEL.world.WORLD_WIDTH * MODEL.world.PIXELS_PER_METER)/2);
		worldViewLocInitY = (panel.getHeight()/2 - (MODEL.world.WORLD_HEIGHT * MODEL.world.PIXELS_PER_METER)/2);
		
		worldToPanelTransform.translate(VIEW.worldViewLocInitX, VIEW.worldViewLocInitY);
		worldToPanelTransform.scale(MODEL.world.PIXELS_PER_METER, MODEL.world.PIXELS_PER_METER);
		
		panelToWorldTransform.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
		panelToWorldTransform.translate(-VIEW.worldViewLocInitX, -VIEW.worldViewLocInitY);
		
		normalCar = ImageIO.read(new File("media\\normalCar.png"));
		normalCar = ImageUtils.createResizedCopy(
				normalCar,
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), true);
		
		fastCar = ImageIO.read(new File("media\\fastCar.png"));
		fastCar = ImageUtils.createResizedCopy(
				fastCar,
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), true);
		
		crash = ImageIO.read(new File("media\\crash.png"));
		crash = ImageUtils.createResizedCopy(
				crash,
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), true);
		
		BufferedImage grass = ImageIO.read(new File("media\\grass.png"));
		
		tiledGrass = new BufferedImage(
				(int)(MODEL.world.WORLD_WIDTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.WORLD_HEIGHT * MODEL.world.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tiledGrass.createGraphics();
		
		for (int i = 0; i < (MODEL.world.WORLD_WIDTH * MODEL.world.PIXELS_PER_METER)/grass.getWidth(); i++) {
			for (int j = 0; j < (MODEL.world.WORLD_HEIGHT * MODEL.world.PIXELS_PER_METER)/grass.getHeight(); j++) {
				g2.drawImage(grass, grass.getWidth() * i, grass.getHeight() * j, null);
			}
		}
		
	}
	
	
	
	public AffineTransform worldToPanelTransform = new AffineTransform();
	public AffineTransform panelToWorldTransform = new AffineTransform();
	
	/**
	 * 
	 * <0, 0> goes to <worldViewLocInitX, worldViewLocInitY>
	 * <WORLD_WIDTH, WORLD_HEIGHT> goes to worldViewLoc + <MODEL.world.WORLD_WIDTH * MODEL.world.PIXELS_PER_METER, MODEL.world.WORLD_HEIGHT * MODEL.world.PIXELS_PER_METER> 
	 */
	public Point worldToPanel(Point worldP) {
		Point2D src = new Point2D.Double(worldP.x, worldP.y);
		Point2D dst = worldToPanelTransform.transform(src, null);
		return new Point(dst.getX(), dst.getY());
	}
	
	public Point panelToWorld(Point panelP) {
		Point2D src = new Point2D.Double(panelP.x, panelP.y);
		Point2D dst = panelToWorldTransform.transform(src, null);
		return new Point(dst.getX(), dst.getY());
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new WorldPanel();
		panel.setFocusable(true);
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		
		Container cp = newFrame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(panel);
		cp.add(controlPanel);
		
		newFrame.pack();
		
		newFrame.setSize(WindowInfo.windowWidth(), WindowInfo.windowHeight());
		newFrame.setLocation(WindowInfo.windowX(), WindowInfo.windowY());
		
		return newFrame;
	}
	
	public void paint(Graphics2D g2) {
		
		int x = panel.getWidth()/2 - (int)((MODEL.world.WORLD_WIDTH * MODEL.world.PIXELS_PER_METER)/2);
		int y = panel.getHeight()/2 - (int)((MODEL.world.WORLD_HEIGHT * MODEL.world.PIXELS_PER_METER)/2);
		g2.translate(x, y);
		
		paintBackground(g2);
		paintScene(g2);
	}
	
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	private void paintScene(Graphics2D g2) {
		
		frameCount++;
		
		logger.debug("draw scene " + frameCount);
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		ControlMode modeCopy;
		Entity hilitedCopy;
		
		List<Car> carsCopy;
		
		synchronized (MODEL) {
			modeCopy = MODEL.getMode();
			hilitedCopy = MODEL.hilited;
			
			carsCopy = new ArrayList<Car>(MODEL.world.getCars());
		}
		
		switch (modeCopy) {
		case DRAFTING: {
			
			MODEL.stroke.paint(g2);
			
			paintFPS(g2);
			
			break;
		}
		case IDLE:
		case RUNNING:
		case PAUSED: {
			
			AffineTransform origTransform = g2.getTransform();
			AffineTransform trans = (AffineTransform)origTransform.clone();
			trans.scale(MODEL.world.PIXELS_PER_METER, MODEL.world.PIXELS_PER_METER);
			g2.setTransform(trans);
			
			for (Car c : carsCopy) {
				c.paint(g2);
			}
			
			if (hilitedCopy != null) {
				hilitedCopy.paintHilite(g2);
			}
			
			g2.setTransform(origTransform);
			
			paintFPS(g2);
			
			break;
		}
		}
		
	}
	
	private void paintBackground(Graphics2D g2) {
		
		int x = -(int)(((Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER));
		int y = -(int)(((Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER));
		
		g2.drawImage(backgroundImage, x, y, null);
	}
	
	public void renderBackground() {
		
		backgroundImage = new BufferedImage(
				(int)((MODEL.world.WORLD_WIDTH + Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER),
				(int)((MODEL.world.WORLD_HEIGHT + Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D backgroundImageG2 = backgroundImage.createGraphics();
		
		backgroundImageG2.translate(
				(int)(Vertex.INIT_VERTEX_RADIUS * MODEL.world.PIXELS_PER_METER),
				(int)(Vertex.INIT_VERTEX_RADIUS * MODEL.world.PIXELS_PER_METER));
		
		backgroundImageG2.drawImage(tiledGrass, 0, 0, null);
		
		backgroundImageG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(MODEL.world.graph.getEdges());
			verticesCopy = new ArrayList<Vertex>(MODEL.world.graph.getAllVertices());
		}
		
		AffineTransform origTransform = backgroundImageG2.getTransform();
		AffineTransform trans = (AffineTransform)origTransform.clone();
		trans.scale(MODEL.world.PIXELS_PER_METER, MODEL.world.PIXELS_PER_METER);
		backgroundImageG2.setTransform(trans);
		
		for (Edge e : edgesCopy) {
			e.paint(backgroundImageG2);
		}
		
		for (Vertex v : verticesCopy) {
			v.paint(backgroundImageG2);
		}
		
		backgroundImageG2.setTransform(origTransform);
		
		for (Vertex v : verticesCopy) {
			v.paintID(backgroundImageG2);
		}
	}
	
	/**
	 * 
	 * @param g2
	 */
	private void paintFPS(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		Point p = new Point(1, 1).multiply(MODEL.world.PIXELS_PER_METER);
		g2.drawString("FPS: " + VIEW.fps, (int)p.x, (int)p.y);
		
		p = new Point(1, 2).multiply(MODEL.world.PIXELS_PER_METER);
		g2.drawString("time: " + MODEL.world.t, (int)p.x, (int)p.y);
		
		p = new Point(1, 3).multiply(MODEL.world.PIXELS_PER_METER);		
		g2.drawString("body count: " + MODEL.world.b2dWorld.getBodyCount(), (int)p.x, (int)p.y);
		
	}
	
	public void repaint() {
		
		int x = panel.getWidth()/2 - (int)(((MODEL.world.WORLD_WIDTH + Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER)/2);
		int y = panel.getHeight()/2 - (int)(((MODEL.world.WORLD_HEIGHT + Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER)/2);
		
		panel.repaint(
				x,
				y,
				(int)(((MODEL.world.WORLD_WIDTH + Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER)),
				(int)(((MODEL.world.WORLD_WIDTH + Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS) * MODEL.world.PIXELS_PER_METER)));
		
//		panel.repaint();
	}
	
}
