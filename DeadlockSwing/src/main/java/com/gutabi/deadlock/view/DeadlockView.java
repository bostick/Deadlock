package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
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
				(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER), true);
		
		fastCar = ImageIO.read(new File("media\\fastCar.png"));
		fastCar = ImageUtils.createResizedCopy(
				fastCar,
				(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER), true);
		
		crash = ImageIO.read(new File("media\\crash.png"));
		crash = ImageUtils.createResizedCopy(
				crash,
				(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER), true);
		
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
		Driveable hilitedCopy;
		List<Point> curPanelStrokeCopy;
		Point lastPanelPointCopy;
		List<Car> movingCarsCopy;
		List<Car> crashedCarsCopy;
		
		synchronized (MODEL) {
			modeCopy = MODEL.getMode();
			hilitedCopy = MODEL.hilited;
			curPanelStrokeCopy = new ArrayList<Point>(MODEL.curPanelStroke);
			lastPanelPointCopy = MODEL.lastPanelPoint;
			movingCarsCopy = new ArrayList<Car>(MODEL.world.movingCars);
			crashedCarsCopy = new ArrayList<Car>(MODEL.world.crashedCars);
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
			
			paintCurrentStroke(curPanelStrokeCopy, lastPanelPointCopy, g2);
			
		} else if (modeCopy == ControlMode.RUNNING || modeCopy == ControlMode.PAUSED) {
			for (Car c : movingCarsCopy) {
				c.paint(g2);
			}
			for (Car c : crashedCarsCopy) {
				c.paint(g2);
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
	
	private static void paintFPS(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		Point o = new Point(1, 1);
		Point loc = VIEW.worldToPanel(o.add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g2.drawString("FPS: " + VIEW.fps, (int)loc.getX(), (int)loc.getY());
		
		o = new Point(1, 2);
		loc = VIEW.worldToPanel(o.add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g2.drawString("time: " + MODEL.world.t, (int)loc.getX(), (int)loc.getY());
		
	}
	
	private static void paintCurrentStroke(List<Point> curPanelStroke, Point lastPanelPoint, Graphics2D g2) {
		
		g2.setColor(Color.RED);
		int size = curPanelStroke.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = curPanelStroke.get(i);
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		
		g2.setStroke(new DraftingStroke());
		g2.drawPolyline(xPoints, yPoints, size);
		
		g2.setColor(Color.RED);
		if (lastPanelPoint != null) {
			
			g2.fillOval(
					(int)(lastPanelPoint.getX() - MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER / 2),
					(int)(lastPanelPoint.getY() - MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER / 2),
					(int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER),
					(int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
			
		}
		
	}
	
	private static void paintEdge1(Edge e, Graphics2D g) {
		int size = e.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		
		g.setColor(new Color(0x88, 0x88, 0x88, 0xff));
		g.setStroke(new Road1Stroke());
		
		for (int i = 0; i < size; i++) {
			Point p = VIEW.worldToPanel(e.getPoint(i));
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		
		g.drawPolyline(xPoints, yPoints, size);
		
	}
	
	private static void paintEdge2(Edge e, Graphics2D g) {
		int size = e.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = VIEW.worldToPanel(e.getPoint(i));
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
			Point p = VIEW.worldToPanel(e.getPoint(i));
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		g.setColor(Color.RED);
		g.setStroke(new Road1Stroke());
		g.drawPolyline(xPoints, yPoints, size);
	}
	
	public static void paintIntersection(Intersection v, Graphics2D g) {
		
		g.setColor(new Color(0x44, 0x44, 0x44, 0xff));
		
		Point loc = VIEW.worldToPanel(v.getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
	}
	
	public static void paintSource(Source s, Graphics2D g) {
		
		g.setColor(Color.GREEN);
		
		Point loc = VIEW.worldToPanel(s.getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
		
	}

	public static void paintSink(Sink s, Graphics2D g) {
		
		g.setColor(Color.RED);
		
		Point loc = VIEW.worldToPanel(s.getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
	}
	
	public static void paintIntersectionHilite(Intersection v, Graphics2D g) {
		
		g.setColor(Color.RED);
		
		Point loc = VIEW.worldToPanel(v.getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
		
	}
	
	public static void paintSourceHilite(Source s, Graphics2D g) {
			
		g.setColor(Color.RED);
		
		Point loc = VIEW.worldToPanel(s.getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
		
	}
	
	public static void paintSinkHilite(Sink s, Graphics2D g) {
		
		g.setColor(Color.RED);
		
		Point loc = VIEW.worldToPanel(s.getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
		
	}

	public static class Road1Stroke extends BasicStroke {
		
		public Road1Stroke() {
			super((int)(MODEL.world.ROAD_WIDTH * MODEL.world.PIXELS_PER_METER), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
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
		
//		Point loc = VIEW.worldToPanel(new Point(-MODEL.world.VERTEX_WIDTH, -MODEL.world.VERTEX_WIDTH));
//		
//		panel.repaint(
//				(int)loc.getX(),
//				(int)loc.getY(),
//				(int)(MODEL.world.WORLD_WIDTH + MODEL.world.VERTEX_WIDTH) * MODEL.world.PIXELS_PER_METER,
//				(int)(MODEL.world.WORLD_HEIGHT + MODEL.world.VERTEX_WIDTH) * MODEL.world.PIXELS_PER_METER);
		
		panel.repaint();
	}
	
}
