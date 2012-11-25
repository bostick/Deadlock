package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public JFrame frame;
	public WorldCanvas canvas;
	public ControlPanel controlPanel;
	public PreviewPanel previewPanel;
	
//	public int worldOffsetX;
//	public int worldOffsetY;
	public int worldOriginX;
	public int worldOriginY;
	
	public java.awt.Stroke worldStroke = new BasicStroke(0.05f);
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public DeadlockView() {
		;
	}
	
	public void init() throws Exception {
		frame = createFrame(false);
		
		assert canvas.getWidth() == 1427;
		assert canvas.getHeight() == 822;
		
//		if (DMath.lessThanEquals(MODEL.world.WORLD_WIDTH * MODEL.PIXELS_PER_METER, canvas.getWidth())) {
//			worldOffsetX = (int)(canvas.getWidth() * 0.5 - ((MODEL.world.WORLD_WIDTH * 0.5) * MODEL.PIXELS_PER_METER));
//		} else {
//			worldOffsetX = (int)(canvas.getWidth() * 0.5 - ((MODEL.world.QUADRANT_WIDTH * 0.5) * MODEL.PIXELS_PER_METER));
//		}
//		if (DMath.lessThanEquals(MODEL.world.WORLD_HEIGHT * MODEL.PIXELS_PER_METER, canvas.getHeight())) {
//			worldOffsetY = (int)(canvas.getHeight() * 0.5 - ((MODEL.world.WORLD_HEIGHT * 0.5) * MODEL.PIXELS_PER_METER));
//		} else {
//			worldOffsetY = (int)(canvas.getHeight() * 0.5 - ((MODEL.world.QUADRANT_HEIGHT * 0.5) * MODEL.PIXELS_PER_METER));
//		}
		
//		worldOffsetX = (int)(canvas.getWidth() * 0.5 - (((MODEL.world.getWorldWidth() * 0.5) - (MODEL.world.QUADRANT_WIDTH * 0.5)) * MODEL.PIXELS_PER_METER));
//		worldOffsetY = (int)(canvas.getHeight() * 0.5 - (((MODEL.world.getWorldHeight() * 0.5) - (MODEL.world.QUADRANT_HEIGHT * 0.5)) * MODEL.PIXELS_PER_METER));
		
		worldOriginX = 0;
		worldOriginY = 0;
		
		sheet = ImageIO.read(new File("media\\sheet.png"));
		explosionSheet = ImageIO.read(new File("media\\explosionSheet.png"));
		
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		canvas = new WorldCanvas();
		canvas.setFocusable(true);
		
		previewPanel = new PreviewPanel();
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		
		Container cp = newFrame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(canvas);
		cp.add(controlPanel);
		
		newFrame.pack();
		
		newFrame.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		newFrame.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
		
		return newFrame;
	}
	
	public Point canvasToWorld(Point p) {
		return new Point((p.x - (0 - worldOriginX)) * MODEL.METERS_PER_PIXEL, (p.y - (0 - worldOriginY)) * MODEL.METERS_PER_PIXEL);
	}
	
	public void renderBackgroundFresh() {
		MODEL.world.renderBackgroundFresh();
	}
	
	public void repaint() {
		assert !Thread.holdsLock(MODEL);
		
		Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
		
		MODEL.paint(g2);
		
		canvas.bs.show();
		
		g2.dispose();
		
	}
	
	public void repaintControlPanel() {
		controlPanel.repaint();
	}
	
}
