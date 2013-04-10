package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.CircleImpl;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.geom.CubicCurveImpl;
import com.gutabi.deadlock.geom.Ellipse;
import com.gutabi.deadlock.geom.EllipseImpl;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.OBBImpl;
import com.gutabi.deadlock.geom.Polygon;
import com.gutabi.deadlock.geom.PolygonImpl;
import com.gutabi.deadlock.geom.Polyline;
import com.gutabi.deadlock.geom.PolylineImpl;
import com.gutabi.deadlock.geom.QuadCurve;
import com.gutabi.deadlock.geom.QuadCurveImpl;
import com.gutabi.deadlock.geom.Triangle;
import com.gutabi.deadlock.geom.TriangleImpl;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.PlatformContentPaneImpl;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PlatformImpl implements Platform {
	
	public RootPaneContainer appContainer;
	public RootPaneContainer debuggerContainer;
	
	public RenderingContext createRenderingContext(Object... args) {
		
		if (args[0] instanceof Graphics2D) {
			
			Graphics2D g2 = (Graphics2D)args[0];
			
			return new RenderingContextImpl(g2);
		} else {
			
			ImageImpl img = (ImageImpl)args[0];
			Graphics2D g2 = img.img.createGraphics();
			
			return new RenderingContextImpl(g2); 
		}
		
	}
	
	public PlatformContentPane createPlatformContentPane(Object... args) {
		
		KeyListener kl = (KeyListener)args[0];
		
		return new PlatformContentPaneImpl(kl);
	}
	
	public void setupAppScreen(Object... args) {
		
		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		content.j.setPreferredSize(new Dimension(APP.MAINWINDOW_WIDTH, APP.MAINWINDOW_HEIGHT));
		
		appContainer.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).pack();
		}
		
	}
	
	public void showAppScreen(Object... args) {
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).setVisible(true);
		}
		
	}
	
	public void setupDebuggerScreen(Object... args) {
		
		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		content.j.setPreferredSize(new Dimension(APP.CONTROLPANEL_WIDTH, APP.CONTROLPANEL_HEIGHT));
		
		debuggerContainer.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (debuggerContainer instanceof JFrame) {
			((JFrame)debuggerContainer).pack();
		}
		
	}
	
	public void showDebuggerScreen(Object... args) {
		
		if (debuggerContainer instanceof JFrame) {
			((JFrame)debuggerContainer).setVisible(true);
		}
		
	}
	
	public void unshowAppScreen(Object... args) {
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).setVisible(false);
		}
		
	}

	public void unshowDebuggerScreen(Object... args) {
		
		if (debuggerContainer instanceof JFrame) {
			((JFrame)debuggerContainer).setVisible(false);
		}
		
	}
	
	/*
	 * font engine
	 */
	
	FontRenderContext frc = new FontRenderContext(null, false, false);
//	Font visitorPlain36;
	Font visitorPlain16;
	Font visitorPlain48;
	Font visitorPlain72;
	{
		
		InputStream is = this.getClass().getResourceAsStream("/visitor1.ttf");
		try {
			Font visitorReal = Font.createFont(Font.TRUETYPE_FONT, is);
//			visitorPlain36 = visitorReal.deriveFont(Font.PLAIN, 36);
			visitorPlain16 = visitorReal.deriveFont(Font.PLAIN, 16);
			visitorPlain48 = visitorReal.deriveFont(Font.PLAIN, 48);
			visitorPlain72 = visitorReal.deriveFont(Font.PLAIN, 72);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public java.awt.Font getRealFont(Resource fontFile, FontStyle fontStyle, int fontSize) {
		
		ResourceImpl r = (ResourceImpl)fontFile;
		
		if (r.full.equals("/visitor1.ttf")) {
			
			if (fontStyle == FontStyle.PLAIN) {
				
				if (fontSize == 16) {
					return visitorPlain16;	
				} else if (fontSize == 48) {
					return visitorPlain48;	
				} else if (fontSize == 72) {
					return visitorPlain72;	
				}
				
			}
			
		}
		
		assert false;
		return null;
	}
	
	public AABB bounds(String preText, Resource fontFile, FontStyle fontStyle, int fontSize) {
		
		AABB aabb = null;
		
		/*
		 * layout returns height of 0 for " "
		 */
		String text;
		if (preText.equals(" ")) {
			text = "X";
		} else {
			text = preText;
		}
		
		Font ttfReal = getRealFont(fontFile, fontStyle, fontSize);
		
		TextLayout layout = new TextLayout(text, ttfReal, frc);
		Rectangle2D bounds = layout.getBounds();
		aabb = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		
		return aabb;
	}
	
	/**
	 * image engine
	 */
	public Image readImage(Resource res) throws Exception {
		
		BufferedImage img = ImageIO.read(this.getClass().getResource(((ResourceImpl)res).full));
		
		return new ImageImpl(img);
	}
	
	public Image createImage(int width, int height) {
		assert width > 0;
		assert height > 0;
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		return new ImageImpl(img);
	}
	
	public Image createTransparentImage(int width, int height) {
		assert width > 0;
		assert height > 0;
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		return new ImageImpl(img);
	}
	
	/*
	 * resource engine
	 */
	public Resource imageResource(String name) {
		
		ResourceImpl res = new ResourceImpl();
		
		res.given = name;
		res.full = "/" + name + ".png";
		
		return res;
	}
	
	public Resource fontResource(String name) {
		
		ResourceImpl res = new ResourceImpl();
		
		res.given = name;
		res.full = "/" + name + ".ttf";
		
		return res;
	}
	
	public Resource levelDBResource(String name) {
		
		String full = "/" + name + ".zip";
		URL url = APP.getClass().getResource(full);
		assert url != null;
		
		ResourceImpl res = new ResourceImpl();
		
		res.given = name;
		res.url = url;
		
		return res;
	}
	
	public InputStream getResourceInputStream(Resource res) throws Exception {
		
		ResourceImpl impl = (ResourceImpl)res;
		
		return impl.url.openStream();
	}





	public void exit() {
		
		try {
			 String pidstr = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		      String pid[] = pidstr.split("@");
		      Runtime.getRuntime().exec("taskkill /F /PID " + pid[0]).waitFor();
			  }
			  catch (Exception e) {
			  }
		
	}
	
	
	
	
	public Circle createCircle(Point center, double radius) {
		return new CircleImpl(center, radius);
	}

	public Polyline createPolyline(Point... pts) {
		return new PolylineImpl(pts);
	}

	public OBB createOBB(Point center, double angle, double xExtant, double yExtant) {
		return new OBBImpl(center, angle, xExtant, yExtant);
	}

	public Ellipse createEllipse(Point center, double x, double y) {
		return new EllipseImpl(center, x, y);
	}

	public Triangle createTriangle(Point p0, Point p1, Point p2) {
		return new TriangleImpl(p0, p1, p2);
	}

	public QuadCurve createQuadCurve(Point start, Point c0, Point end) {
		return new QuadCurveImpl(start, c0, end);
	}

	public CubicCurve createCubicCurve(Point start, Point c0, Point c1, Point end) {
		return new CubicCurveImpl(start, c0, c1, end);
	}
	
	public Polygon createPolygon(Point... pts) {
		return new PolygonImpl(pts);
	}
	
}
