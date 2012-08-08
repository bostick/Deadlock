package com.gutabi.deadlock.core.view;

import com.gutabi.core.Dim;
import com.gutabi.core.Point;
import com.gutabi.deadlock.core.Logger;
import static com.gutabi.deadlock.core.LoggerFactory.LOGGERFACTORY;

public class DeadlockView {
	
	public Point viewLoc;
	public Dim viewDim;
//	public Point cameraUpperLeft = new Point(0, 0);
//	public double zoom = 1.0;
	
	public final DeadlockWindowInfo window;	
	public final Logger logger = LOGGERFACTORY.getLogger(DeadlockView.class);
	
	public static DeadlockView VIEW;
	
	public DeadlockView(DeadlockWindowInfo w) {
		this.window = w;
	}
	
	public void init() {
		assert window != null;
		viewLoc = new Point(0, 0);
		viewDim = window.windowDim();
	}
	
	public void moveCameraRight() {
		//cameraUpperLeft = new Point(cameraUpperLeft.x+1, cameraUpperLeft.y);
		viewLoc = viewLoc.add(new Point(5, 0));
		Point center = viewDim.divide(2).add(viewLoc);
		logger.debug("right: center=" + center);
	}
	
	public void moveCameraLeft() {
		//cameraUpperLeft = new Point(cameraUpperLeft.x-1, cameraUpperLeft.y);
		viewLoc = viewLoc.add(new Point(-5, 0));
		Point center = viewDim.divide(2).add(viewLoc);
		logger.debug("left: center=" + center);
	}
	
	public void moveCameraUp() {
		//cameraUpperLeft = new Point(cameraUpperLeft.x, cameraUpperLeft.y-1);
		viewLoc = viewLoc.add(new Point(0, -5));
		Point center = viewDim.divide(2).add(viewLoc);
		logger.debug("up: center=" + center);
	}
	
	public void moveCameraDown() {
		//cameraUpperLeft = new Point(cameraUpperLeft.x, cameraUpperLeft.y+1);
		viewLoc = viewLoc.add(new Point(0, 5));
		Point center = viewDim.divide(2).add(viewLoc);
		logger.debug("down: center=" + center);
	}
	
	public void zoomIn() {
		//Dim oldCameraDim = new Dim((int)(window.windowWidth() / zoom), (int)(window.windowHeight() / zoom));
		Point center = viewDim.divide(2).add(viewLoc);
		//zoom += 0.1;
		viewDim = viewDim.times(0.9);
		viewLoc = center.minus(viewDim.divide(2));
		center = viewDim.divide(2).add(viewLoc);
		logger.debug("zoom in : center=" + center);
	}
	
	public void zoomOut() {
		//Dim oldCameraDim = new Dim((int)(window.windowWidth() / zoom), (int)(window.windowHeight() / zoom));
		Point center = viewDim.divide(2).add(viewLoc);
		//zoom += 0.1;
		viewDim = viewDim.times(1.1);
		viewLoc = center.minus(viewDim.divide(2));
		center = viewDim.divide(2).add(viewLoc);
		logger.debug("zoom out : center=" + center);
	}
	
	public void zoomReset() {
		//Dim oldCameraDim = new Dim((int)(window.windowWidth() / zoom), (int)(window.windowHeight() / zoom));
		Point center = viewDim.divide(2).add(viewLoc);
		//zoom += 0.1;
		viewDim = window.windowDim();
		viewLoc = new Point(0, 0);
		center = viewDim.divide(2).add(viewLoc);
		logger.debug("zoom reset: center=" + center);
	}
	
	public double getZoom() {
		return ((double)window.windowWidth()) / ((double)viewDim.width);
	}
	
}
