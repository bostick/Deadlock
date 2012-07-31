package com.gutabi.deadlock.core.view;

import com.gutabi.core.Dim;
import com.gutabi.core.Point;

public class DeadlockView {
	
	public Point cameraUpperLeft = new Point(0, 0);
	public double zoom = 1.0;
	
	public DeadlockWindow window;
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public void moveCameraRight() {
		cameraUpperLeft = new Point(cameraUpperLeft.x+10, cameraUpperLeft.y);
	}
	
	public void moveCameraLeft() {
		cameraUpperLeft = new Point(cameraUpperLeft.x-10, cameraUpperLeft.y);
	}
	
	public void moveCameraUp() {
		cameraUpperLeft = new Point(cameraUpperLeft.x, cameraUpperLeft.y-10);
	}
	
	public void moveCameraDown() {
		cameraUpperLeft = new Point(cameraUpperLeft.x, cameraUpperLeft.y+10);
	}
	
	public void zoomIn() {
		Dim oldCameraDim = new Dim((int)(window.windowWidth() / zoom), (int)(window.windowHeight() / zoom));
		Point oldCameraCenter = new Point(cameraUpperLeft.x + oldCameraDim.width/2, cameraUpperLeft.y + oldCameraDim.height/2);
		zoom += 0.1;
		Dim newCameraDim = new Dim((int)(window.windowWidth() / zoom), (int)(window.windowHeight() / zoom));
		cameraUpperLeft = new Point(oldCameraCenter.x - newCameraDim.width/2, oldCameraCenter.y - newCameraDim.height/2);
	}
	
	public void zoomOut() {
		Dim oldCameraDim = new Dim((int)(window.windowWidth() / zoom), (int)(window.windowHeight() / zoom));
		Point oldCameraCenter = new Point(cameraUpperLeft.x + oldCameraDim.width/2, cameraUpperLeft.y + oldCameraDim.height/2);
		zoom = Math.max(zoom - 0.1, 0.1);
		Dim newCameraDim = new Dim((int)(window.windowWidth() / zoom), (int)(window.windowHeight() / zoom));
		cameraUpperLeft = new Point(oldCameraCenter.x - newCameraDim.width/2, oldCameraCenter.y - newCameraDim.height/2);
	}
	
}
