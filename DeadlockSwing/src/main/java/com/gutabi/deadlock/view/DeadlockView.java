package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Driveable;
import com.gutabi.deadlock.core.Edge;
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
	
	public PreviewPanel previewPanel;
	
	public BufferedImage backgroundImage;
	
	/*
	 * the location and dimension of the main world world view
	 * 
	 */
	public Point worldViewLoc;
	public Dim worldViewDim;
	double worldViewLocInitX;
	double worldViewLocInitY;
	
	BufferedImage car;
	BufferedImage wreck;
	BufferedImage tiledGrass;
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public DeadlockView() {
		;
	}
	
	public void init() throws Exception {
		frame = createFrame(false);
		
		worldViewLocInitX = -(panel.getWidth()/2 - MODEL.WORLD_WIDTH/2);
		worldViewLocInitY = -(panel.getHeight()/2 - MODEL.WORLD_HEIGHT/2);
		
		worldViewLoc = new Point(worldViewLocInitX, worldViewLocInitY);
		worldViewDim = new Dim(panel.getWidth(), panel.getHeight());
		
		car = ImageIO.read(new File("media\\car.png"));
		car = ImageUtils.createResizedCopy(car, (int)MODEL.CAR_WIDTH, (int)MODEL.CAR_WIDTH, true);
		
		wreck = ImageIO.read(new File("media\\wreck.png"));
		wreck = ImageUtils.createResizedCopy(wreck, (int)MODEL.CAR_WIDTH, (int)MODEL.CAR_WIDTH, true);
		
		BufferedImage grass = ImageIO.read(new File("media\\grass.png"));
		
		tiledGrass = new BufferedImage(MODEL.WORLD_WIDTH, MODEL.WORLD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tiledGrass.createGraphics();
		
		for (int i = 0; i < MODEL.WORLD_WIDTH/32; i++) {
			for (int j = 0; j < MODEL.WORLD_HEIGHT/32; j++) {
				g2.drawImage(grass, 32 * i, 32 * j, null);
			}
		}
		
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new WorldPanel();
		panel.setFocusable(true);
		
		previewPanel = new PreviewPanel();
		
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
	
	public void drawScene(Graphics2D g2) {
		
		g2.scale(getZoom(), getZoom());
		g2.translate((double)-worldViewLoc.getX(), (double)-worldViewLoc.getY());
		
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		ControlMode modeCopy;
		Driveable hilitedCopy;
		List<Point> curStrokeCopy;
		Point lastPointCopy;
		List<Car> movingCarsCopy;
		List<Car> crashedCarsCopy;
		
		synchronized (MODEL) {
			modeCopy = MODEL.getMode();
			hilitedCopy = MODEL.hilited;
			curStrokeCopy = new ArrayList<Point>(MODEL.curStrokeRaw);
			lastPointCopy = MODEL.lastPointRaw;
			movingCarsCopy = new ArrayList<Car>(MODEL.movingCars);
			crashedCarsCopy = new ArrayList<Car>(MODEL.crashedCars);
		}
		
		if (modeCopy == ControlMode.IDLE) {
			
			if (hilitedCopy != null) {
				
				if (hilitedCopy instanceof Edge) {
					
					Edge e = (Edge)hilitedCopy;
					paintEdgeHilite(e, g2);
					
				} else if (hilitedCopy instanceof Intersection) {
					
					Intersection v = (Intersection)hilitedCopy;
					paintIntersectionHilite(v, g2);
					
				} else if (hilitedCopy instanceof Source) {
					
					Source s = (Source)hilitedCopy;
					paintSourceHilite(s, g2);
					
				} else if (hilitedCopy instanceof Sink) {
					
					Sink s = (Sink)hilitedCopy;
					paintSinkHilite(s, g2);
					
				} else {
					assert false;
				}
				
			}
			
		} else if (modeCopy == ControlMode.DRAFTING) {
			g2.setColor(Color.RED);
			int size = curStrokeCopy.size();
			int[] xPoints = new int[size];
			int[] yPoints = new int[size];
			for (int i = 0; i < size; i++) {
				Point p = curStrokeCopy.get(i);
				xPoints[i] = (int)p.getX();
				yPoints[i] = (int)p.getY();
			}
			
			g2.setStroke(new DraftingStroke());
			g2.drawPolyline(xPoints, yPoints, size);
			
			g2.setColor(Color.RED);
			if (lastPointCopy != null) {
				g2.fillOval((int)(lastPointCopy.getX()-MODEL.ROAD_WIDTH/2), (int)(lastPointCopy.getY()-MODEL.ROAD_WIDTH/2), (int)MODEL.ROAD_WIDTH, (int)MODEL.ROAD_WIDTH);
			}
		} else if (modeCopy == ControlMode.RUNNING || modeCopy == ControlMode.PAUSED) {
			
			for (Car c : movingCarsCopy) {
				Point pos = c.getPosition().getPoint();
				
				AffineTransform origTransform = g2.getTransform();
				
				AffineTransform carTrans = (AffineTransform)origTransform.clone();
				carTrans.translate(pos.getX(), pos.getY());
				
				Point prevPos = c.getPreviousPosition().getPoint();
				
				double rad = Math.atan2(pos.getY()-prevPos.getY(), pos.getX()-prevPos.getX());
				
				carTrans.rotate(rad);
				
				g2.setTransform(carTrans);
				
				int x = (int)(-car.getWidth()/2);
				int y = (int)(-car.getHeight()/2);
				g2.drawImage(car, x, y, null);
				
				g2.setTransform(origTransform);
			}
			
			for (Car c : crashedCarsCopy) {
				Point pos = c.getPosition().getPoint();
				
				int x = (int)(pos.getX()-MODEL.CAR_WIDTH/2);
				int y = (int)(pos.getY()-MODEL.CAR_WIDTH/2);
				g2.drawImage(wreck, x, y, null);
				
			}
		}
		
	}
	
	public void renderBackground() {
		
		Dimension d = panel.getSize();
		
		backgroundImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = backgroundImage.createGraphics();
		
		g2.scale(getZoom(), getZoom());
		g2.translate((double)-worldViewLoc.getX(), (double)-worldViewLoc.getY());
		
		g2.drawImage(tiledGrass, 0, 0, null);
		
		g2.setColor(Color.DARK_GRAY);
//		g2.fillRect(0, 0, MODEL.WORLD_WIDTH, 10);
//		g2.fillRect(0, 10, 10, MODEL.WORLD_HEIGHT-10);
//		g2.fillRect(MODEL.WORLD_WIDTH-10, 10, 10, MODEL.WORLD_HEIGHT-10);
//		g2.fillRect(10, MODEL.WORLD_HEIGHT-10, MODEL.WORLD_WIDTH-20, 10);
		
		List<Edge> edgesCopy;
		List<Intersection> intersectionsCopy;
		List<Source> sourcesCopy;
		List<Sink> sinksCopy;
		
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(MODEL.getEdges());
			intersectionsCopy = new ArrayList<Intersection>(MODEL.getIntersections());
			sourcesCopy = new ArrayList<Source>(MODEL.getSources());
			sinksCopy = new ArrayList<Sink>(MODEL.getSinks());
		}
		
		for (Edge e : edgesCopy) {
			paintEdge1(e, g2);
		}
		for (Source s : sourcesCopy) {
			paintSource(s, g2);
		}
		for (Sink s : sinksCopy) {
			paintSink(s, g2);
		}
		for (Intersection v : intersectionsCopy) {
			paintIntersection(v, g2);
		}
		for (Edge e : edgesCopy) {
			paintEdge2(e, g2);
		}
	}
	
	private static void paintEdge1(Edge e, Graphics2D g) {
		int size = e.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = e.getPoint(i);
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		g.setColor(new Color(0x88, 0x88, 0x88, 0xff));
		g.setStroke(new Road1Stroke());
		g.drawPolyline(xPoints, yPoints, size);
		
	}
	
	private static void paintEdge2(Edge e, Graphics2D g) {
		int size = e.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = e.getPoint(i);
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		g.setColor(Color.YELLOW);
		g.setStroke(new Road2Stroke());
		g.drawPolyline(xPoints, yPoints, size);
	}
	
	private static void paintEdgeHilite(Edge e, Graphics2D g) {
		int size = e.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = e.getPoint(i);
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		g.setColor(Color.RED);
		g.setStroke(new Road1Stroke());
		g.drawPolyline(xPoints, yPoints, size);
	}
	
	public static void paintIntersection(Intersection v, Graphics2D g) {
		
		g.setColor(new Color(0x44, 0x44, 0x44, 0xff));
		
		Point p = v.getPoint();
		g.fillOval((int)(p.getX()-MODEL.VERTEX_WIDTH/2), (int)(p.getY()-MODEL.VERTEX_WIDTH/2), (int)MODEL.VERTEX_WIDTH, (int)MODEL.VERTEX_WIDTH);
	}
	
	public static void paintSource(Source s, Graphics2D g) {
		
		g.setColor(Color.GREEN);
		
		Point p = s.getPoint();
		g.fillOval((int)(p.getX()-MODEL.VERTEX_WIDTH/2), (int)(p.getY()-MODEL.VERTEX_WIDTH/2), (int)MODEL.VERTEX_WIDTH, (int)MODEL.VERTEX_WIDTH);
		
	}

	public static void paintSink(Sink s, Graphics2D g) {
		
		g.setColor(Color.RED);
		
		Point p = s.getPoint();
		g.fillOval((int)(p.getX()-MODEL.VERTEX_WIDTH/2), (int)(p.getY()-MODEL.VERTEX_WIDTH/2), (int)MODEL.VERTEX_WIDTH, (int)MODEL.VERTEX_WIDTH);
		
	}
	
	public static void paintIntersectionHilite(Intersection v, Graphics2D g) {
		
		g.setColor(Color.RED);
		Point p = v.getPoint();
		g.fillOval((int)(p.getX()-MODEL.VERTEX_WIDTH/2), (int)(p.getY()-MODEL.VERTEX_WIDTH/2), (int)MODEL.VERTEX_WIDTH, (int)MODEL.VERTEX_WIDTH);
		
	}
	
	public static void paintSourceHilite(Source s, Graphics2D g) {
			
			g.setColor(Color.RED);
			Point p = s.getPoint();
			g.fillOval((int)(p.getX()-MODEL.VERTEX_WIDTH/2), (int)(p.getY()-MODEL.VERTEX_WIDTH/2), (int)MODEL.VERTEX_WIDTH, (int)MODEL.VERTEX_WIDTH);
			
		}
	
	public static void paintSinkHilite(Sink s, Graphics2D g) {
		
		g.setColor(Color.RED);
		Point p = s.getPoint();
		g.fillOval((int)(p.getX()-MODEL.VERTEX_WIDTH/2), (int)(p.getY()-MODEL.VERTEX_WIDTH/2), (int)MODEL.VERTEX_WIDTH, (int)MODEL.VERTEX_WIDTH);
		
	}

	public static class Road1Stroke extends BasicStroke {
		
		public Road1Stroke() {
			super((int)MODEL.ROAD_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}
		
	}
	
	public static class Road2Stroke extends BasicStroke {
		
		public Road2Stroke() {
			super(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}

	}
	
	public static class DraftingStroke extends BasicStroke {
		
		public DraftingStroke() {
			super(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}
		
	}
	
	public void repaint() {
		frame.repaint();
	}
	
	
	
	
	public double getZoom() {
		return ((double)panel.getWidth()) / ((double)worldViewDim.width);
	}
	
	public Point panelToWorld(Point pp) {
		Point p = pp;
		p = new Point(p.getX() * 1/getZoom(), p.getY() * 1/getZoom());
		p = Point.add(p, worldViewLoc);
		return p; 
	}
	
	public void moveCameraRight() {
		worldViewLoc = worldViewLoc.add(new Point(5, 0));
		logger.debug("right: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void moveCameraLeft() {
		worldViewLoc = worldViewLoc.add(new Point(-5, 0));
		logger.debug("left: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void moveCameraUp() {
		worldViewLoc = worldViewLoc.add(new Point(0, -5));
		logger.debug("up: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void moveCameraDown() {
		worldViewLoc = worldViewLoc.add(new Point(0, 5));
		logger.debug("down: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void pageUp() {
		worldViewLoc = worldViewLoc.add(new Point(0, -50));
		logger.debug("up: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void pageDown() {
		worldViewLoc = worldViewLoc.add(new Point(0, 50));
		logger.debug("down: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void zoomIn() {
		Point center = worldViewDim.divide(2).add(worldViewLoc);
		worldViewDim = worldViewDim.times(1/1.1);
		worldViewLoc = center.minus(worldViewDim.divide(2));
		logger.debug("zoom in: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void zoomOut() {
		Point center = worldViewDim.divide(2).add(worldViewLoc);
		worldViewDim = worldViewDim.times(1.1);
		worldViewLoc = center.minus(worldViewDim.divide(2));
		logger.debug("zoom out: viewLoc=" + worldViewLoc);
		
		renderBackground();
		repaint();
	}
	
	public void zoomReset() {
		worldViewDim = new Dim(panel.getWidth(), panel.getHeight());
		worldViewLoc = new Point(worldViewLocInitX, worldViewLocInitY);
		Point center = worldViewDim.divide(2).add(worldViewLoc);
		logger.debug("zoom reset: viewLoc=" + worldViewLoc + " center: " + center);
		
		renderBackground();
		repaint();
	}
	
	
}
