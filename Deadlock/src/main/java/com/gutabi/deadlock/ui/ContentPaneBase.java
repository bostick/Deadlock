package com.gutabi.deadlock.ui;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class ContentPaneBase implements ContentPane {
	
	protected List<Panel> children = new ArrayList<Panel>();
	
//	static Logger logger = Logger.getLogger(ContentPane.class);
	
	public ContentPaneBase() {
		
	}
	
	public List<Panel> getChildren() {
		return children;
	}
	
	private Point lastMovedContentPanePoint;
	public Point getLastMovedContentPanePoint() {
		return lastMovedContentPanePoint;
	}
	
	public void setLastMovedContentPanePoint(Point p) {
		this.lastMovedContentPanePoint = p;
	}
	
//	public abstract void enableKeyListener();
//	
//	public abstract void disableKeyListener();
	
	public void ctrlOKey() {
		;
	}

	public void dKey() {
		;
	}

	public void upKey() {
		;
	}

	public void enterKey() {
		;
	}

	public void aKey() {
		;
	}

	public void sKey() {
		;
	}

	public void ctrlSKey() {
		;
	}

	public void downKey() {
		;
	}

	public void minusKey() {
		;
	}

	public void plusKey() {
		;
	}

	public void d3Key() {
		;
	}

	public void d2Key() {
		;
	}

	public void d1Key() {
		;
	}

	public void gKey() {
		;
	}

	public void wKey() {
		;
	}

	public void qKey() {
		;
	}

	public void escKey() {
		;
	}

	public void deleteKey() {
		;
	}

	public void insertKey() {
		;
	}
	
	public void fKey() {
		
	}
	
	public void postDisplay() {
		
		for (Panel child : children) {
			child.postDisplay();
		}
		
	}
	
	public abstract void repaint();
	
}
