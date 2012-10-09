package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
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
import com.gutabi.deadlock.core.Hilitable;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
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
		Point2D src = new Point2D.Double(worldP.getX(), worldP.getY());
		Point2D dst = worldToPanelTransform.transform(src, null);
		return new Point(dst.getX(), dst.getY());
	}
	
	public Point panelToWorld(Point panelP) {
		Point2D src = new Point2D.Double(panelP.getX(), panelP.getY());
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
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	public void drawScene(Graphics2D g2) {
		
		frameCount++;
		
//		logger.debug("draw scene " + frameCount);
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		paintFPS(g2);
		
		ControlMode modeCopy;
		Hilitable hilitedCopy;
		
		List<Car> carsCopy;
		
		synchronized (MODEL) {
			modeCopy = MODEL.getMode();
			hilitedCopy = MODEL.hilited;
			
			carsCopy = new ArrayList<Car>(MODEL.world.getCars());
		}
		
		switch (modeCopy) {
		case DRAFTING: {
			
			MODEL.stroke.paint(g2);
			
			break;
		}
		case IDLE:
		case RUNNING:
		case PAUSED: {
			
			for (Car c : carsCopy) {
				c.paint(g2);
			}
			
			if (hilitedCopy != null) {
				
				if (hilitedCopy instanceof Edge) {
					
					Edge e = (Edge)hilitedCopy;
					e.paintHilite(g2);
					
				} else if (hilitedCopy instanceof Intersection) {
					
					Intersection i = (Intersection)hilitedCopy;
					i.paintHilite(g2);
					
				} else if (hilitedCopy instanceof Source) {
					
					Source s = (Source)hilitedCopy;
					s.paintHilite(g2);
					
				} else if (hilitedCopy instanceof Sink) {
					
					Sink s = (Sink)hilitedCopy;
					s.paintHilite(g2);
					
				} else if (hilitedCopy instanceof Car) {
					
					Car c = (Car)hilitedCopy;
					c.paintHilite(g2);
					
				} else {
					assert false;
				}
				
			}
			
			break;
		}
		}
		
	}
	
	public void renderBackground() {
		
		Dimension d = panel.getSize();
		
		backgroundImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = backgroundImage.createGraphics();
		
		Point loc = VIEW.worldToPanel(new Point(0, 0));
		
		g2.drawImage(tiledGrass, (int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.WORLD_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.WORLD_HEIGHT * MODEL.world.PIXELS_PER_METER), null);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		List<Edge> edgesCopy;
		List<Intersection> intersectionsCopy;
		List<Source> sourcesCopy;
		List<Sink> sinksCopy;
		
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(MODEL.world.graph.getEdges());
			intersectionsCopy = new ArrayList<Intersection>(MODEL.world.graph.getIntersections());
			sourcesCopy = new ArrayList<Source>(MODEL.world.graph.getSources());
			sinksCopy = new ArrayList<Sink>(MODEL.world.graph.getSinks());
		}
		
		for (Edge e : edgesCopy) {
			e.paintEdge1(g2);
		}
		for (Source s : sourcesCopy) {
			s.paint(g2);
		}
		for (Sink s : sinksCopy) {
			s.paint(g2);
		}
		for (Intersection i : intersectionsCopy) {
			i.paint(g2);
		}
		for (Edge e : edgesCopy) {
			e.paintEdge2(g2);
		}
		
		
		
		
		
//		AffineTransform origTransform = g2.getTransform();
//		
//		AffineTransform b2dTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
//		Vec2 pos = MODEL.world.b2dGroundBody.getPosition();
//		float angle = MODEL.world.b2dGroundBody.getAngle();
//		b2dTrans.translate(pos.x, pos.y);
//		b2dTrans.rotate(angle);
//		
//		b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
//		
//		g2.setTransform(b2dTrans);
//		
//		g2.setColor(Color.YELLOW);
//		
//		g2.fillRect(
//				(int)(-2 * MODEL.world.PIXELS_PER_METER),
//				(int)(-3 * MODEL.world.PIXELS_PER_METER),
//				(int)(2*2 * MODEL.world.PIXELS_PER_METER),
//				(int)(2*3 * MODEL.world.PIXELS_PER_METER));
//		
//		g2.setTransform(origTransform);
//		
//		
//		
//		
//		origTransform = g2.getTransform();
//		
//		b2dTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
//		pos = MODEL.world.b2dGroundBody2.getPosition();
//		angle = MODEL.world.b2dGroundBody2.getAngle();
//		b2dTrans.translate(pos.x, pos.y);
//		b2dTrans.rotate(angle);
//		
//		b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
//		
//		g2.setTransform(b2dTrans);
//		
//		g2.setColor(Color.ORANGE);
//		
//		g2.fillRect(
//				(int)(-1 * MODEL.world.PIXELS_PER_METER),
//				(int)(-1.5 * MODEL.world.PIXELS_PER_METER),
//				(int)(2*1 * MODEL.world.PIXELS_PER_METER),
//				(int)(2*1.5 * MODEL.world.PIXELS_PER_METER));
//		
//		g2.setTransform(origTransform);
		
		
	}
	
	private static void paintFPS(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		Point o = new Point(1, 1);
		Point loc = VIEW.worldToPanel(o.add(new Point(-MODEL.world.VERTEX_RADIUS, -MODEL.world.VERTEX_RADIUS)));
		
		g2.drawString("FPS: " + VIEW.fps, (int)loc.getX(), (int)loc.getY());
		
		o = new Point(1, 2);
		loc = VIEW.worldToPanel(o.add(new Point(-MODEL.world.VERTEX_RADIUS, -MODEL.world.VERTEX_RADIUS)));
		
		g2.drawString("time: " + MODEL.world.t, (int)loc.getX(), (int)loc.getY());
		
		
		o = new Point(1, 3);
		loc = VIEW.worldToPanel(o.add(new Point(-MODEL.world.VERTEX_RADIUS, -MODEL.world.VERTEX_RADIUS)));
		
		g2.drawString("body count: " + MODEL.world.b2dWorld.getBodyCount(), (int)loc.getX(), (int)loc.getY());
		
	}
	
	public void repaint() {
		
//		Point loc = VIEW.worldToPanel(new Point(-MODEL.world.VERTEX_WIDTH, -MODEL.world.VERTEX_WIDTH));
		
//		panel.repaint(
//				(int)loc.getX(),
//				(int)loc.getY(),
//				(int)(MODEL.world.WORLD_WIDTH + MODEL.world.VERTEX_WIDTH) * MODEL.world.PIXELS_PER_METER,
//				(int)(MODEL.world.WORLD_HEIGHT + MODEL.world.VERTEX_WIDTH) * MODEL.world.PIXELS_PER_METER);
		
		panel.repaint();
	}
	
}
