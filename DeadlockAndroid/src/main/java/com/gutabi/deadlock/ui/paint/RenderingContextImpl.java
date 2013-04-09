package com.gutabi.deadlock.ui.paint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelXorXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ResourceImpl;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.math.Dim;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.Transform;

public class RenderingContextImpl extends RenderingContext {
	
	public static float RAD = 57.29577951308232087679815481410517033240547246656432154916024386120284714832155263244096899585111094f;
	
	public Canvas canvas;
	public Paint paint;
	
	public RenderingContextImpl(Canvas canvas, Paint paint) {
		this.canvas = canvas;
		this.paint = paint;
	}
	
	public void rotate(double a) {
		canvas.rotate((float)a * RAD);
	}
	
	public void rotate(double a, Point p) {
		canvas.rotate((float)a * RAD, (float)p.x, (float)p.y);
	}
	
	public void rotate(double a, Dim d) {
		canvas.rotate((float)a * RAD, (float)d.width, (float)d.height);
	}
	
	public void paintImage(Image img, double orig, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2) {
		
		Bitmap b = ((ImageImpl)img).b;
		
		canvas.drawBitmap(b, new Rect(sx1, sy1, sx2, sy2), new RectF((float)dx1, (float)dy1, (float)dx2, (float)dy2), paint);
		
	}
	
	public void paintImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		
		Bitmap b = ((ImageImpl)img).b;
		
		canvas.drawBitmap(b, new Rect(sx1, sy1, sx2, sy2), new Rect(dx1, dy1, dx2, dy2), paint);
		
	}
	
	public void dispose() {
		
	}
	
	public void setTransform(Transform trans) {
		
		TransformImpl t2 = (TransformImpl)trans;
		canvas.setMatrix(t2.mat);
		
	}
	
	public void setFont(Resource font, FontStyle style, int size) {
		
		assert style == FontStyle.PLAIN;
		
		paint.setTypeface(((ResourceImpl)font).face);
		paint.setTextSize(size);
	}
	
	public void setXORMode(Color c) {
		
		int c2 = android.graphics.Color.argb(c.a, c.r, c.g, c.b);
		
		paint.setXfermode(new PixelXorXfermode(c2));
	}
	
	public void clearXORMode() {
		
		paint.setXfermode(null);
	}
	
	public Transform getTransform() {
		Matrix mat = canvas.getMatrix();
		return new TransformImpl(mat);
	}
	
	public void fillRect(int x, int y, int width, int height) {
		canvas.drawRect(new Rect(x, y, x+width, y+height), paint);
	}
	
	public void drawAABB(AABB a) {
		paint.setStyle(Style.STROKE);
		canvas.drawRect((float)a.x, (float)a.y, (float)(a.x+a.width), (float)(a.y+a.height), paint);
	}
	
	public void paintAABB(AABB a) {
		paint.setStyle(Style.FILL);
		canvas.drawRect((float)a.x, (float)a.y, (float)(a.x+a.width), (float)(a.y+a.height), paint);
	}
	
	public void drawLine(Line a) {
		canvas.drawLine((float)a.p0.x, (float)a.p0.y, (float)a.p1.x, (float)a.p1.y, paint);
	}
	
	public void scale(double s) {
		canvas.scale((float)s, (float)s);
	}
	
	public void setColor(Color c) {
		int c2 = android.graphics.Color.argb(c.a, c.r, c.g, c.b);
		paint.setColor(c2);
	}
	
	public void translate(double x, double y) {
		canvas.translate((float)x, (float)y);
	}
	
	public void translate(Point p) {
		canvas.translate((float)p.x, (float)p.y);
	}
	
	public void setAlpha(double a) {
		paint.setAlpha((int)(255 * a));
	}
	
	public void paintString(double x, double y, double s, String text) {
		Matrix origMat = canvas.getMatrix();
		
		canvas.translate((float)x, (float)y);
		canvas.scale((float)s, (float)s);
		canvas.drawText(text, 0, 0, paint);
		
		canvas.setMatrix(origMat);
	}
	
	public void setStroke(double width, Cap cap, Join join) {
		
		Paint.Cap c2 = null;
		switch (cap) {
		case BUTT:
			c2 = Paint.Cap.BUTT;
			break;
		case ROUND:
			c2 = Paint.Cap.ROUND;
			break;
		case SQUARE:
			c2 = Paint.Cap.SQUARE;
			break;
		}
		
		Paint.Join j2 = null;
		switch (join) {
		case ROUND:
			j2 = Paint.Join.ROUND;
			break;
		case BEVEL:
			j2 = Paint.Join.BEVEL;
			break;
		case MITER:
			j2 = Paint.Join.MITER;
			break;
		}
		
		paint.setStrokeWidth((float)width);
		paint.setStrokeCap(c2);
		paint.setStrokeJoin(j2);
	}
	
	public Color getColor() {
		int c = paint.getColor();
		
		int a = android.graphics.Color.alpha(c);
		int r = android.graphics.Color.red(c);
		int g = android.graphics.Color.green(c);
		int b = android.graphics.Color.blue(c);
		
		return new Color(r, g, b, a);
	}
	
}
