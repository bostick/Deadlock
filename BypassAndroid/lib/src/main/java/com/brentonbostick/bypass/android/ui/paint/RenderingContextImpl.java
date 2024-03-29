package com.brentonbostick.bypass.android.ui.paint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;

import com.brentonbostick.bypass.android.ResourceImpl;
import com.brentonbostick.bypass.android.geom.GeometryPathImpl;
import com.brentonbostick.bypass.android.ui.ImageImpl;
import com.brentonbostick.capsloc.Resource;
import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.geom.Circle;
import com.brentonbostick.capsloc.geom.GeometryPath;
import com.brentonbostick.capsloc.geom.Line;
import com.brentonbostick.capsloc.geom.MutableAABB;
import com.brentonbostick.capsloc.math.Dim;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.Image;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.FontStyle;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public class RenderingContextImpl extends RenderingContext {
	
	public static float RAD = 57.29577951308f;
	
	public Canvas canvas;
	public Paint paint;
	
	public RenderingContextImpl() {

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
	
	
	Rect srcRect = new Rect();
	RectF destRect = new RectF();
	
	public void paintImage(Image img, double origX, double origY, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2) {
		
		Bitmap b = ((ImageImpl)img).b;
		
		srcRect.left = sx1;
		srcRect.top = sy1;
		srcRect.right = sx2;
		srcRect.bottom = sy2;
		
		destRect.left = (float)dx1;
		destRect.top = (float)dy1;
		destRect.right = (float)dx2;
		destRect.bottom = (float)dy2;
		
		canvas.drawBitmap(b, srcRect, destRect, paint);
	}
	
	public void paintImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		
		Bitmap b = ((ImageImpl)img).b;
		
		srcRect.left = sx1;
		srcRect.top = sy1;
		srcRect.right = sx2;
		srcRect.bottom = sy2;
		
		destRect.left = (float)dx1;
		destRect.top = (float)dy1;
		destRect.right = (float)dx2;
		destRect.bottom = (float)dy2;
		
		canvas.drawBitmap(b, srcRect, destRect, paint);
	}
	
	public void dispose() {
		
	}
	
	public void setFont(Resource font, FontStyle style, int size) {
		
		assert style == FontStyle.PLAIN;
		
		paint.setTypeface(((ResourceImpl)font).face);
		paint.setTextSize(size);
	}
	
	public void setXORMode(Color c) {
		
		int c2 = android.graphics.Color.argb(c.a, c.r, c.g, c.b);
		
		paint.setColorFilter(new PorterDuffColorFilter(c2, PorterDuff.Mode.XOR));
	}
	
	public void clearXORMode() {
		
		paint.setColorFilter(null);
	}
	
	
	
	public void pushTransform() {
//		canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.save();
	}
	
	public void popTransform() {
		canvas.restore();
	}
//
//	public void pushClip() {
//		canvas.save(Canvas.CLIP_SAVE_FLAG);
//	}
//
//	public void popClip() {
//		canvas.restore();
//	}
	
	public void drawAABB(AABB a) {
		Style old = paint.getStyle();
		paint.setStyle(Style.STROKE);
		canvas.drawRect((float)a.x, (float)a.y, (float)(a.x+a.width), (float)(a.y+a.height), paint);
		paint.setStyle(old);
	}
	
	public void paintAABB(AABB a) {
		Style old = paint.getStyle();
		paint.setStyle(Style.FILL);
		canvas.drawRect((float)a.x, (float)a.y, (float)(a.x+a.width), (float)(a.y+a.height), paint);
		paint.setStyle(old);
	}
	
	public void drawAABB(MutableAABB a) {
		Style old = paint.getStyle();
		paint.setStyle(Style.STROKE);
		canvas.drawRect((float)a.x, (float)a.y, (float)(a.x+a.width), (float)(a.y+a.height), paint);
		paint.setStyle(old);
	}
	
	public void paintAABB(MutableAABB a) {
		Style old = paint.getStyle();
		paint.setStyle(Style.FILL);
		canvas.drawRect((float)a.x, (float)a.y, (float)(a.x+a.width), (float)(a.y+a.height), paint);
		paint.setStyle(old);
	}
	
	public void drawPath(GeometryPath p) {
		
		GeometryPathImpl pp = (GeometryPathImpl)p;
		
		Style old = paint.getStyle();
		paint.setStyle(Style.STROKE);
		int fs = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		canvas.drawPath(pp.p, paint);
		
		paint.setStyle(old);
		paint.setFlags(fs);
	}
	
	public void paintPath(GeometryPath p) {
		
		GeometryPathImpl pp = (GeometryPathImpl)p;
		
		Style old = paint.getStyle();
		paint.setStyle(Style.FILL);
		
		int fs = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		canvas.drawPath(pp.p, paint);
		
		paint.setStyle(old);
		paint.setFlags(fs);
	}
	
	
	public void drawLine(Line a) {
		canvas.drawLine((float)a.p0.x, (float)a.p0.y, (float)a.p1.x, (float)a.p1.y, paint);
	}
	
	public void drawCircle(Circle c) {
		Style old = paint.getStyle();
		paint.setStyle(Style.STROKE);
		
		int fs = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		canvas.drawCircle((float)c.center.x, (float)c.center.y, (float)c.radius, paint);
		
		paint.setStyle(old);
		paint.setFlags(fs);
	}
	
	public void paintCircle(Circle c) {
		Style old = paint.getStyle();
		paint.setStyle(Style.FILL);
		
		int fs = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		canvas.drawCircle((float)c.center.x, (float)c.center.y, (float)c.radius, paint);
		
		paint.setStyle(old);
		paint.setFlags(fs);
	}
	
	public void scale(double sX, double sY) {
		canvas.scale((float)sX, (float)sY);
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
	
	public void clip(AABB a) {
		canvas.clipRect((float)a.x, (float)a.y, (float)(a.x+a.width), (float)(a.y+a.height));
	}
	
	public void setAlpha(double a) {
		paint.setAlpha((int)(255 * a));
	}
	
	public void paintString(double x, double y, double s, String text) {
		
		canvas.save();
		
		canvas.translate((float)x, (float)y);
		canvas.scale((float)s, (float)s);
		canvas.drawText(text, 0, 0, paint);
		
		canvas.restore();
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
	
	public void setAntialiasing() {
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	}
	
	public void clearAntialiasing() {
		int fs = paint.getFlags();
		paint.setFlags(fs & ~Paint.ANTI_ALIAS_FLAG);
	}
}
