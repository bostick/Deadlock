package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.model.Car;


@SuppressWarnings("serial")
public class WorldPanel extends JPanel {
	
	Logger logger = Logger.getLogger("deadlock");
	
	public WorldPanel() {
		
		setPreferredSize(new Dimension(MODEL.WORLD_WIDTH, MODEL.WORLD_HEIGHT));
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Dimension d = getSize();
		
		Graphics2D g2 = (Graphics2D)g;
		
		g2.drawImage(VIEW.backgroundImage, 0, 0, d.width, d.height, null);
		
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		ControlMode modeCopy;
		List<Point> curStrokeCopy;
		Point lastPointCopy;
		List<Car> movingCarsCopy;
		List<Car> crashedCarsCopy;
		
		synchronized (MODEL) {
			modeCopy = MODEL.getMode();
			curStrokeCopy = new ArrayList<Point>(MODEL.curStrokeRaw);
			lastPointCopy = MODEL.lastPointRaw;
			movingCarsCopy = new ArrayList<Car>(MODEL.movingCars);
			crashedCarsCopy = new ArrayList<Car>(MODEL.crashedCars);
		}
		
		if (modeCopy == ControlMode.DRAFTING) {
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
				g2.fillOval((int)(lastPointCopy.getX()-5), (int)(lastPointCopy.getY()-5), 10, 10);
			}
		} else if (modeCopy == ControlMode.RUNNING) {
			
			for (Car c : movingCarsCopy) {
				Point pos = c.getPosition().getPoint();
				switch (c.getState()) {
				case NEW:
				case FORWARD:
				case BACKWARD:
				case VERTEX:
					g2.setColor(Color.BLUE);
					break;
				default:
					assert false;
				}
				g2.fillOval((int)(pos.getX()-5), (int)(pos.getY()-5), 10, 10);
			}
			
			for (Car c : crashedCarsCopy) {
				Point pos = c.getPosition().getPoint();
				switch (c.getState()) {
				case CRASHED:
					g2.setColor(Color.ORANGE);
					break;
				default:
					assert false;
				}
				g2.fillOval((int)(pos.getX()-5), (int)(pos.getY()-5), 10, 10);
			}
		}
		
	}
	
	public static class DraftingStroke extends BasicStroke {
		
		public DraftingStroke() {
			super(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}
		
	}
	
}
