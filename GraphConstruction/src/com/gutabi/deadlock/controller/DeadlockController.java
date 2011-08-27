package com.gutabi.deadlock.controller;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gutabi.deadlock.model.DeadlockModel;
import com.gutabi.deadlock.model.Edge;
import com.gutabi.deadlock.model.OverlappingException;
import com.gutabi.deadlock.model.PointFUtils;
import com.gutabi.deadlock.model.Vertex;
import com.gutabi.deadlock.view.DeadlockView;

public class DeadlockController implements OnTouchListener {
	
	private DeadlockModel model;
	private DeadlockView view;
	
	private PointF curPoint;
	
	List<Runnable> ops = new ArrayList<Runnable>();
	
	public DeadlockController(DeadlockModel model, DeadlockView view) {
		this.model = model;
		this.view = view;
	}
	
	
	/*
	 * round a to nearest multiple of b
	 */
	static float round(float a, int b) {
		return Math.round(a / b) * b;
	}
	
	/*
	 * round coords from event to nearest multiple of:
	 */
	int roundingFactor = 60;
	
	public boolean onTouch(View ignored, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		x = round(x, roundingFactor);
		y = round(y, roundingFactor);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touch", "DOWN <"+x+","+y+">");
			touchStart(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touch", "MOVE <"+x+","+y+">");
			touchMove(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("touch", "UP   <"+x+","+y+">");
			touchUp();
			view.invalidate();
			break;
		}
		return true;
	}
	
	private void touchStart(float x, float y) {
		curPoint = new PointF(x, y);
		List<PointF> pointsToBeProcessed = model.getPointsToBeProcessed();
		pointsToBeProcessed.add(curPoint);
	}
	
	private void touchMove(float x, float y) {
		
		PointF a = curPoint;
		
		PointF b = new PointF(x, y);
		
		if (PointFUtils.equals(a, b)) {
			/*
			 * gotcha: sometimes the first move after down is the same point
			 * this will help filter out those repeated points
			 */
			return;
		}
		
		curPoint = b;
		List<PointF> pointsToBeProcessed = model.getPointsToBeProcessed();
		pointsToBeProcessed.add(b);
	}
	
	private void touchUp() {
		
		List<PointF> pointsToBeProcessed = model.getPointsToBeProcessed();
		
		int pointsSize = pointsToBeProcessed.size();
		for (int j = 0; j < pointsSize-1; j++) {
			//ensureNoOverlap(j, j+1);
		}
		
		for (int j = 0; j < pointsSize; j++) {
			process(j-1, j);
		}
		
		pointsToBeProcessed.clear();
		curPoint = null;
	}
	
	void process(int aa, int bb) {
		
		List<PointF> pointsToBeProcessed = model.getPointsToBeProcessed();
		int pointsToBeProcessedSize = pointsToBeProcessed.size();
		
		final PointF b = pointsToBeProcessed.get(bb);
		
		boolean interAdded = false;
		
		List<Vertex> vertices = model.getVertices();
		vertices:
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			int edgeCount = v.getEdges().size();
			final PointF d = v.getPointF();
			if (bb == 0) {
				if (PointFUtils.intersection(b, d)) {
					PointF inter = b;
					if (edgeCount == 1) {
						// starting at vertex with 1 edge
						
						ops.add(new Runnable() {
							public void run() {
								
								//model.checkConsistency();
								
							}
						});
						
						interAdded = true;
						break vertices;
					} else {
						// starting at vertex with a lot of edges
						
						assert false;
						ops.add(new Runnable() {
							public void run() {
								
								//model.checkConsistency();
								
							}
						});
						
						interAdded = true;
						break vertices;
					}
				}
			} else {
				final PointF a = pointsToBeProcessed.get(aa);
				if (PointFUtils.intersection(d, a, b)) {
					PointF inter = d;
					if (!(inter.equals(a.x, a.y))) {
						if (inter.equals(b.x, b.y)) {
							if (bb == pointsToBeProcessedSize-1) {
								if (edgeCount == 1) {
									// ending at vertex with 1 edge
									
									Edge existingABEdge = model.findEdge(a);
									Edge existingDEdge = model.findEdge(d);
									
									if (existingABEdge == existingDEdge) {
										
										ops.add(new Runnable() {
											public void run() {
												
												Vertex dv = model.findVertex(d);
												
												Edge existingDEdge = model.findEdge(d);
												Vertex oldEnd = existingDEdge.getEnd();
												
												existingDEdge.getPoints().add(b);
												
												int s = existingDEdge.getPoints().size();
												PointF first = existingDEdge.getPoints().get(0);
												PointF last = existingDEdge.getPoints().get(s-1);
												assert PointFUtils.equals(first, last);
												
												existingDEdge.setStart(null);
												existingDEdge.setEnd(null);
												
												model.removeVertex(dv);
												model.removeVertex(oldEnd);
												model.checkConsistency();
											}
										});
										
										interAdded = true;
										
									} else {
										assert false;
									}
									
								} else {
									// ending at vertex with a lot of edges
									
									assert false;
									
									ops.add(new Runnable() {
										public void run() {
											
										}
									});
									
									interAdded = true;
								}
							} else {
								if (edgeCount == 1) {
									// middle on vertex with 1 edge
									
									assert false;
									
									ops.add(new Runnable() {
										public void run() {
											
										}
									});
									
									interAdded = true;
								} else {
									// middle on vertex with a lot of edges
									
									assert false;
									
									ops.add(new Runnable() {
										public void run() {
											
										}
									});
									
									interAdded = true;
								}
							}
						} else {
							if (edgeCount == 1) {
								// through a vertex with 1 edge
								
								assert false;
								
								ops.add(new Runnable() {
									public void run() {
										
									}
								});
								
								interAdded = true;
							} else {
								// through a vertex with a lot of edges
								
								assert false;
								
								ops.add(new Runnable() {
									public void run() {
										
									}
								});
								
								interAdded = true;
							}
						}
					}
				}
			}
		}
		
		List<Edge> edges = model.getEdges();
		edges: {
		if (interAdded) {
			break edges;
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			List<PointF> points = e.getPoints();
			int pointsSize = points.size();
			/*
			 * <a, b> is the segment currently being drawn/moved
			 * <c, d> is the segment to be tested against
			 */
			points:
			for (int j = 0; j <  pointsSize; j++) {
				final PointF d = points.get(j);
				if (j == 0) {
					if (bb == 0) {
						if (PointFUtils.intersection(b, d)) {
							PointF inter = b;
							//3 starting from start of existing segment
							
							assert false;
							interAdded = true;
							break edges;
						}
					} else {
						final PointF a = pointsToBeProcessed.get(aa);
						/*
						 * start of <c, d> segment, so only have d
						 */
						if (PointFUtils.intersection(d, a, b)) {
							PointF inter = d;
							if (!(inter.equals(a.x, a.y))) {
								if (inter.equals(b.x, b.y)) {
									if (bb == pointsToBeProcessedSize-1) {
										//7 ending in start of existing segment
										
										assert false;
										interAdded = true;
									} else {
										//11 middle point shares start of existing segment
										
										assert false;
										interAdded = true;
									}
								} else {
									//15 go through start of existing segment
									
									assert false;
									interAdded = true;
								}
							}
						}
					}
					
				} else {
					final PointF c = points.get(j-1);
					if (bb == 0) {
						if (PointFUtils.intersection(b, c, d)) {
							final PointF inter = b;
							if (!(inter.equals(c.x, c.y))) {
								if (inter.equals(d.x, d.y)) {
									if (j == pointsSize-1) {
										//4 starting from end of existing segment
										
										assert false;
										interAdded = true;
										break edges;
									} else {
										//1 starting from middle point of existing segment
										
										ops.add(new Runnable() {
											public void run() {
												
												Vertex v1 = model.createVertex();
												v1.setPointF(inter);
												
												Edge existingCDEdge = model.findEdge(c);
												
												List<PointF> points = existingCDEdge.getPoints();
												Vertex start = existingCDEdge.getStart();
												Vertex end = existingCDEdge.getEnd();
												
												int index = model.findIndex(existingCDEdge, b);
												
												Edge f1 = model.createEdge();
												Edge f2 = model.createEdge();
												for (int k = 0; k <= index; k++) {
													f1.getPoints().add(points.get(k));
												}
												for (int k = index; k < points.size(); k++) {
													f2.getPoints().add(points.get(k));
												}
												f1.setStart(start);
												f1.setEnd(v1);
												f2.setStart(v1);
												f2.setEnd(end);
												
												v1.add(f1);
												v1.add(f2);
												
												model.removeEdge(existingCDEdge);
												
												/*
												 * vertex has 2 edges, so don't check consistency
												 */
												//model.checkConsistency();
											}
										});
										
										interAdded = true;
										break edges;
									}
								} else {
									//2 starting between existing segment
									
									ops.add(new Runnable() {
										public void run() {
											
											Vertex v1 = model.createVertex();
											v1.setPointF(inter);
											
											Edge existingCDEdge = model.findEdge(c);
											
											List<PointF> points = existingCDEdge.getPoints();
											Vertex start = existingCDEdge.getStart();
											Vertex end = existingCDEdge.getEnd();
											
											int cindex = model.findIndex(existingCDEdge, c);
											int dindex = model.findIndex(existingCDEdge, d);
											assert dindex == cindex+1;
											
											Edge f1 = model.createEdge();
											Edge f2 = model.createEdge();
											for (int k = 0; k <= cindex; k++) {
												f1.getPoints().add(points.get(k));
											}
											f1.getPoints().add(inter);
											f2.getPoints().add(inter);
											for (int k = dindex; k < points.size(); k++) {
												f2.getPoints().add(points.get(k));
											}
											f1.setStart(start);
											f1.setEnd(v1);
											f2.setStart(v1);
											f2.setEnd(end);
											
											v1.add(f1);
											v1.add(f2);
											
											model.removeEdge(existingCDEdge);
											
											/*
											 * vertex has 2 edges, so don't check consistency
											 */
											//model.checkConsistency();
										}
									});
									
									interAdded = true;
									break edges;
								}
							}
						}
					} else {
						final PointF a = pointsToBeProcessed.get(aa);
						try {
							final PointF inter = PointFUtils.intersection(a, b, c, d);
							if (inter != null) {
								if (!(inter.equals(a.x, a.y) || inter.equals(c.x, c.y))) {
									/*
									 * gotcha: the intersection could be equal to a, so ignore since it was equal to b of last segment
									 * 			the intersection could be equal to c, so ignore since it was equal to d of last segment
									 */
									
									if (inter.equals(b.x, b.y) && inter.equals(d.x, d.y)) {
										if (j == pointsSize-1) {
											if (bb == pointsToBeProcessedSize-1) {
												//8 ending in end of existing segment
												
												assert false;
												interAdded = true;
											} else {
												//12 middle point shares end of existing segment
												
												assert false;
												interAdded = true;
											}
										} else {
											if (bb == pointsToBeProcessedSize-1) {
												
												//5 ending in middle point of existing segment
												
												ops.add(new Runnable() {
													public void run() {
														
														Vertex v1 = model.createVertex();
														v1.setPointF(inter);
														
														Edge existingABEdge = model.findEdge(a);
														Edge existingCDEdge = model.findEdge(c);
														Vertex existingCDStart = existingCDEdge.getStart();
														Vertex existingCDEnd = existingCDEdge.getEnd();
														existingABEdge.getPoints().add(inter);
														Vertex oldABEnd = existingABEdge.getEnd();
														existingABEdge.setEnd(v1);
														
														Edge newCDEdge1 = model.createEdge();
														Edge newCDEdge2 = model.createEdge();
														List<PointF> cdPoints = existingCDEdge.getPoints();
														int cdIndex = model.findIndex(existingCDEdge, d);
														for (int i = 0; i <= cdIndex; i++) {
															newCDEdge1.getPoints().add(cdPoints.get(i));
														}
														for (int i = cdIndex; i < cdPoints.size(); i++) {
															newCDEdge2.getPoints().add(cdPoints.get(i));
														}
														newCDEdge1.setStart(existingCDStart);
														newCDEdge1.setEnd(v1);
														newCDEdge2.setStart(v1);
														newCDEdge2.setEnd(existingCDEnd);
														
														v1.add(existingABEdge);
														v1.add(newCDEdge1);
														v1.add(newCDEdge2);
														
														model.removeVertex(oldABEnd);
														model.removeEdge(existingCDEdge);
														
														model.checkConsistency();
													}
												});
												
												interAdded = true;
												
											} else {
												//9 middle point shares middle point of existing segment
												
												PointF afterD = points.get(j+1);
												
												if (PointFUtils.equals(afterD, a)) {
													
													assert j+1 == pointsSize-1;
													
													assert false;
													
												} else {
													ops.add(new Runnable() {
														public void run() {
															
															Vertex v1 = model.createVertex();
															v1.setPointF(b);
															
															Edge existingABEdge = model.findEdge(a);
															Edge existingCDEdge = model.findEdge(c);
															Vertex existingCDStart = existingCDEdge.getStart();
															Vertex existingCDEnd = existingCDEdge.getEnd();
															existingABEdge.getPoints().add(b);
															Vertex oldABEnd = existingABEdge.getEnd();
															existingABEdge.setEnd(v1);
															
															Edge newCDEdge1 = model.createEdge();
															Edge newCDEdge2 = model.createEdge();
															List<PointF> cdPoints = existingCDEdge.getPoints();
															int cdIndex = model.findIndex(existingCDEdge, d);
															for (int i = 0; i <= cdIndex; i++) {
																newCDEdge1.getPoints().add(cdPoints.get(i));
															}
															for (int i = cdIndex; i < cdPoints.size(); i++) {
																newCDEdge2.getPoints().add(cdPoints.get(i));
															}
															newCDEdge1.setStart(existingCDStart);
															newCDEdge1.setEnd(v1);
															newCDEdge2.setStart(v1);
															newCDEdge2.setEnd(existingCDEnd);
															
															v1.add(existingABEdge);
															v1.add(newCDEdge1);
															v1.add(newCDEdge2);
															
															model.removeVertex(oldABEnd);
															model.removeEdge(existingCDEdge);
															
															model.checkConsistency();
														}
													});
													
													interAdded = true;
												}
												
											}
										}
									} else if (inter.equals(b.x, b.y)) {
										if (bb == pointsToBeProcessedSize-1) {
											//6 ending between existing segment
											
											assert false;
											interAdded = true;
										} else {
											//10 middle point goes between existing segment
											
											ops.add(new Runnable() {
												public void run() {
													
													Vertex v1 = model.createVertex();
													v1.setPointF(inter);
													
													Edge existingABEdge = model.findEdge(a);
													Edge existingCDEdge = model.findEdge(c);
													Vertex existingABEnd = existingABEdge.getEnd();
													Vertex existingCDStart = existingCDEdge.getStart();
													Vertex existingCDEnd = existingCDEdge.getEnd();
													existingABEdge.getPoints().add(inter);
													existingABEdge.setEnd(v1);
													
													Edge newCDEdge1 = model.createEdge();
													Edge newCDEdge2 = model.createEdge();
													List<PointF> cdPoints = existingCDEdge.getPoints();
													int cdIndex = model.findIndex(existingCDEdge, inter);
													for (int i = 0; i <= cdIndex; i++) {
														newCDEdge1.getPoints().add(cdPoints.get(i));
													}
													for (int i = cdIndex; i < cdPoints.size(); i++) {
														newCDEdge2.getPoints().add(cdPoints.get(i));
													}
													newCDEdge1.setStart(existingCDStart);
													newCDEdge1.setEnd(v1);
													newCDEdge2.setStart(v1);
													newCDEdge2.setEnd(existingCDEnd);
													
													v1.add(existingABEdge);
													v1.add(newCDEdge1);
													v1.add(newCDEdge2);
													
													model.removeVertex(existingABEnd);
													model.removeEdge(existingCDEdge);
													
													model.checkConsistency();
													
												}
											});
											
											interAdded = true;
										}
									} else if (inter.equals(d.x, d.y)) {
										if (j == pointsSize-1) {
											//16 go through end of existing segment
											
											assert false;
											interAdded = true;
										} else {
											//13 go through middle point of existing segment
											
											assert false;
											
											ops.add(new Runnable() {
												public void run() {
													
													Vertex v1 = model.createVertex();
													v1.setPointF(inter);
													
													Vertex v2 = model.createVertex();
													v2.setPointF(b);
													
													Edge existingABEdge = model.findEdge(a);
													Edge existingCDEdge = model.findEdge(c);
													Vertex existingABEnd = existingABEdge.getEnd();
													Vertex existingCDStart = existingCDEdge.getStart();
													Vertex existingCDEnd = existingCDEdge.getEnd();
													existingABEdge.getPoints().add(inter);
													existingABEdge.setEnd(v1);
													
													Edge newCDEdge1 = model.createEdge();
													Edge newCDEdge2 = model.createEdge();
													List<PointF> cdPoints = existingCDEdge.getPoints();
													int cdIndex = model.findIndex(existingCDEdge, inter);
													for (int i = 0; i <= cdIndex; i++) {
														newCDEdge1.getPoints().add(cdPoints.get(i));
													}
													for (int i = cdIndex; i < cdPoints.size(); i++) {
														newCDEdge2.getPoints().add(cdPoints.get(i));
													}
													newCDEdge1.setStart(existingCDStart);
													newCDEdge1.setEnd(v1);
													newCDEdge2.setStart(v1);
													newCDEdge2.setEnd(existingCDEnd);
													
													Edge newABEdge = model.createEdge();
													newABEdge.getPoints().add(inter);
													newABEdge.getPoints().add(b);
													newABEdge.setStart(v1);
													newABEdge.setEnd(v2);
													
													v1.add(existingABEdge);
													v1.add(newCDEdge1);
													v1.add(newCDEdge2);
													v1.add(newABEdge);
													v2.add(newABEdge);
													
													model.removeVertex(existingABEnd);
													model.removeEdge(existingCDEdge);
													
													model.checkConsistency();
												}
											});
											
											interAdded = true;
										}
									} else {
										//14 go through existing segment
										
										ops.add(new Runnable() {
											public void run() {
												
												Vertex v1 = model.createVertex();
												v1.setPointF(inter);
												
												Vertex v2 = model.createVertex();
												v2.setPointF(b);
												
												Edge existingABEdge = model.findEdge(a);
												Edge existingCDEdge = model.findEdge(c);
												//Vertex existingABStart = existingABEdge.getStart();
												Vertex existingABEnd = existingABEdge.getEnd();
												Vertex existingCDStart = existingCDEdge.getStart();
												Vertex existingCDEnd = existingCDEdge.getEnd();
												existingABEdge.getPoints().add(inter);
												existingABEdge.setEnd(v1);
												
												Edge newCDEdge1 = model.createEdge();
												Edge newCDEdge2 = model.createEdge();
												List<PointF> cdPoints = existingCDEdge.getPoints();
												int cdCIndex = model.findIndex(existingCDEdge, c);
												int cdDIndex = model.findIndex(existingCDEdge, d);
												assert cdCIndex + 1 == cdDIndex;
												for (int i = 0; i < cdCIndex; i++) {
													newCDEdge1.getPoints().add(cdPoints.get(i));
												}
												newCDEdge1.getPoints().add(inter);
												newCDEdge2.getPoints().add(inter);
												for (int i = cdDIndex; i < cdPoints.size(); i++) {
													newCDEdge2.getPoints().add(cdPoints.get(i));
												}
												newCDEdge1.setStart(existingCDStart);
												newCDEdge1.setEnd(v1);
												newCDEdge2.setStart(v1);
												newCDEdge2.setEnd(existingCDEnd);
												
												Edge newABEdge = model.createEdge();
												newABEdge.getPoints().add(inter);
												newABEdge.getPoints().add(b);
												newABEdge.setStart(v1);
												newABEdge.setEnd(v2);
												
												v1.add(existingABEdge);
												v1.add(newCDEdge1);
												v1.add(newCDEdge2);
												v1.add(newABEdge);
												v2.add(newABEdge);
												
												model.removeVertex(existingABEnd);
												model.removeEdge(existingCDEdge);
												
												model.checkConsistency();
												
											}
										});
										
										interAdded = true;
									}
									/*
									 * gotcha: a single move could intersect with more than one segment, so don't break after first intersection
									 */
									//break outer;
								}
							}
						} catch (OverlappingException ex) {
							assert false;
						}
					}
				}
			} // points
		}} // edges
		
		if (!interAdded) {
			if (bb == 0) {
				//17 starting from empty
				
				ops.add(new Runnable() {
					public void run() {
						
						Vertex v1 = model.createVertex();
						v1.setPointF(b);
						
						model.checkConsistency();
					}
				});
				
			} else {
				final PointF a = pointsToBeProcessed.get(aa);
				
				Vertex av = model.tryFindVertex(a);
				
				if (av == null) {
					
					if (bb == pointsToBeProcessedSize-1) {
						//18 ending in empty, came from non-vertex
						
						ops.add(new Runnable() {
							public void run() {
								
								Vertex v = model.createVertex();
								v.setPointF(b);
								
								Edge e = model.findEdge(a);
								e.getPoints().add(b);
								Vertex oldEnd = e.getEnd();
								e.setEnd(v);
								v.add(e);
								
								model.removeVertex(oldEnd);
								
								model.checkConsistency();
							}
						});
						
					} else {
						//19 middle point in empty, came from non-vertex
						
						ops.add(new Runnable() {
							public void run() {
								
								Vertex v = model.createVertex();
								v.setPointF(b);
								
								Edge e = model.findEdge(a);
								e.getPoints().add(b);
								Vertex oldEnd = e.getEnd();
								e.setEnd(v);
								v.add(e);
								
								model.removeVertex(oldEnd);
								
								model.checkConsistency();
							}
						});
						
					}
					
				} else {
					
					int aveCount = av.getEdges().size();
					
					if (aveCount == 1) {
						
						if (bb == pointsToBeProcessedSize-1) {
							//18 ending in empty, came from vertex with one edge
							
							ops.add(new Runnable() {
								public void run() {
									
									Vertex v = model.createVertex();
									v.setPointF(b);
									
									Vertex av = model.findVertex(a);
									
									Edge e = model.findEdge(a);
									Vertex oldStart = e.getStart();
									Vertex oldEnd = e.getEnd();
									
									if (av == oldStart) {
										
										e.getPoints().add(0, b);
										e.setStart(v);
										
										v.add(e);
										
										model.removeVertex(oldStart);
										
									} else {
										assert av == oldEnd;
										
										e.getPoints().add(b);
										e.setEnd(v);
										
										v.add(e);
										
										model.removeVertex(oldEnd);
									}
									
									model.checkConsistency();
								}
							});
							
						} else {
							//19 middle point in empty, came from vertex with one edge
							
							ops.add(new Runnable() {
								public void run() {
									
									Vertex v = model.createVertex();
									v.setPointF(b);
									
									Vertex av = model.findVertex(a);
									
									Edge e = model.findEdge(a);
									Vertex oldStart = e.getStart();
									Vertex oldEnd = e.getEnd();
									
									if (av == oldStart) {
										
										e.getPoints().add(0, b);
										e.setStart(v);
										
										v.add(e);
										
										model.removeVertex(oldStart);
										
									} else {
										assert av == oldEnd;
										
										e.getPoints().add(b);
										e.setEnd(v);
										
										v.add(e);
										
										model.removeVertex(oldEnd);
									}
									
									model.checkConsistency();
									
								}
							});
							
						}
						
					} else {
						// aveCount >= 3
						
						if (bb == pointsToBeProcessedSize-1) {
							//18 ending in empty, came from vertex with a lot of edges
							
							ops.add(new Runnable() {
								public void run() {
									
									Vertex v = model.createVertex();
									v.setPointF(b);
									
									Vertex av = model.findVertex(a);
									
									Edge e = model.createEdge();
									e.getPoints().add(a);
									e.getPoints().add(b);
									e.setStart(av);
									e.setEnd(v);
									
									av.add(e);
									v.add(e);
									
									model.checkConsistency();
								}
							});
							
						} else {
							//19 middle point in empty, came from vertex with a lot of edges
							
							ops.add(new Runnable() {
								public void run() {
									
									Vertex v = model.createVertex();
									v.setPointF(b);
									
									Vertex av = model.findVertex(a);
									
									Edge e = model.createEdge();
									e.getPoints().add(a);
									e.getPoints().add(b);
									e.setStart(av);
									e.setEnd(v);
									
									av.add(e);
									v.add(e);
									
									model.checkConsistency();
									
								}
							});
							
						}
						
					}
					
				}
				
			}
		}
		
		for (Runnable op : ops) {
			op.run();
			Log.d("model", "edges: " + model.getEdges().size() + " vertices: " + model.getVertices().size());
		}
		ops.clear();
		
	}
	
}
