package com.gutabi.deadlock.model;

import java.util.List;

import android.graphics.PointF;

public final class EdgeUtils {
	
	private EdgeUtils() {
		
	}
	
	public static Vertex split(Edge e, int index, DeadlockModel model) {
		
		List<PointF> ePoints = e.getPoints();
		
		PointF p = ePoints.get(index);
		
		Vertex v = model.createVertex(p);
		
		Vertex eStart = e.getStart();
		Vertex eEnd = e.getEnd();
		
		if (eStart == null && eEnd == null) {
			// loop
			
			Edge f = model.createEdge();
			
			List<PointF> fPoints = f.getPoints();
			
			for (int i = index; i < ePoints.size(); i++) {
				fPoints.add(ePoints.get(i));
			}
			for (int i = 1; i <= index; i++) {
				fPoints.add(ePoints.get(i));
			}
			
			f.setStart(v);
			f.setEnd(v);
			
			v.add(f);
			v.add(f);
			
			model.removeEdge(e);
			
		} else {
			
			Edge f1 = model.createEdge();
			Edge f2 = model.createEdge();
			
			List<PointF> f1Points = f1.getPoints();
			List<PointF> f2Points = f2.getPoints();
			
			for (int i = 0; i <= index; i++) {
				f1Points.add(ePoints.get(i));
			}
			for (int i = index; i < ePoints.size(); i++) {
				f2Points.add(ePoints.get(i));
			}
			
			f1.setStart(eStart);
			f1.setEnd(v);
			
			f2.setStart(v);
			f2.setEnd(eEnd);
			
			eStart.remove(e);
			eStart.add(f1);
			
			eEnd.remove(e);
			eEnd.add(f2);
			
			v.add(f1);
			v.add(f2);
			
			model.removeEdge(e);
			
		}
		
		return v;
	}
	
	public static Edge merge(Edge e1, Edge e2, DeadlockModel model) {
		
		List<PointF> e1Points = e1.getPoints();
		Vertex e1Start = e1.getStart();
		Vertex e1End = e1.getEnd();
		
		List<PointF> e2Points = e2.getPoints();
		Vertex e2Start = e2.getStart();
		Vertex e2End = e2.getEnd();
		
		if (e1 == e2) {
			assert e1Start == e1End;
			// in the middle of merging a loop
			
			int e1StartEdgeCount = e1Start.getEdges().size();
			
			if (e1StartEdgeCount == 2) {
				// stand-alone loop
				
				e1.setStart(null);
				e1.setEnd(null);
				
//				e1End.remove(e1);
//				e1End.add(newEdge);
//				
//				e2Start.remove(e2);
//				e2Start.add(newEdge);
				
				model.removeVertex(e1Start);
				
			} else {
				// start/end vertex has other edges
				// nothing to do
			}
			
			return e1;
		}
		
		Edge newEdge = model.createEdge();
		List<PointF> newEdgePoints = newEdge.getPoints();
		
		if (e1Start == e2Start) {
			//e1Common = e1Start;
			//e2Common = e2Start;
			for (int i = e1Points.size()-1; i >= 0; i--) {
				newEdgePoints.add(e1Points.get(i));
			}
			assert PointFUtils.equals(e1Points.get(0), e2Points.get(0));
			for (int i = 1; i < e2Points.size(); i++) {
				newEdgePoints.add(e2Points.get(i));
			}
			
			newEdge.setStart(e1End);
			newEdge.setEnd(e2End);
			
			e1End.remove(e1);
			e1End.add(newEdge);
			
			e2End.remove(e2);
			e2End.add(newEdge);
			
			model.removeVertex(e1Start);
			//model.removeVertex(e2Start);
			model.removeEdge(e1);
			model.removeEdge(e2);
			
		} else if (e1Start == e2End) {
			//e1Common = e1Start;
			//e2Common = e2End;
			
			if (e1End == e2Start) {
				// forming a loop
				
				for (int i = e1Points.size()-1; i >= 0; i--) {
					newEdgePoints.add(e1Points.get(i));
				}
				assert PointFUtils.equals(e1Points.get(0), e2Points.get(e2Points.size()-1));
				for (int i = e2Points.size()-2; i >= 0; i--) {
					newEdgePoints.add(e2Points.get(i));
				}
				
				int e1StartEdgeCount = e1Start.getEdges().size();
				
				if (e1StartEdgeCount == 1) {
					// stand-alone loop
					
					newEdge.setStart(null);
					newEdge.setEnd(null);
					
//					e1End.remove(e1);
//					e1End.add(newEdge);
//					
//					e2Start.remove(e2);
//					e2Start.add(newEdge);
					
					model.removeVertex(e1Start);
					model.removeVertex(e1End);
					model.removeEdge(e1);
					model.removeEdge(e2);
				} else {
					// start/end vertex has other edges
					
					newEdge.setStart(e1Start);
					newEdge.setEnd(e1Start);
					
					e1Start.remove(e1);
					e1Start.remove(e2);
					e1Start.add(newEdge);
					e1Start.add(newEdge);
					
					model.removeVertex(e1End);
					model.removeEdge(e1);
					model.removeEdge(e2);
					
				}
				
			} else {
				for (int i = e1Points.size()-1; i >= 0; i--) {
					newEdgePoints.add(e1Points.get(i));
				}
				assert PointFUtils.equals(e1Points.get(0), e2Points.get(e2Points.size()-1));
				for (int i = e2Points.size()-2; i >= 0; i--) {
					newEdgePoints.add(e2Points.get(i));
				}
				
				newEdge.setStart(e1End);
				newEdge.setEnd(e2Start);
				
				e1End.remove(e1);
				e1End.add(newEdge);
				
				e2Start.remove(e2);
				e2Start.add(newEdge);
				
				model.removeVertex(e1Start);
				//model.removeVertex(e2End);
				model.removeEdge(e1);
				model.removeEdge(e2);
			}
			
		} else if (e1End == e2Start) {
			//e1Common = e1End;
			//e2Common = e2Start;
			for (int i = 0; i < e1Points.size(); i++) {
				newEdgePoints.add(e1Points.get(i));
			}
			assert PointFUtils.equals(e1Points.get(e1Points.size()-1), e2Points.get(0));
			for (int i = 1; i < e2Points.size(); i++) {
				newEdgePoints.add(e2Points.get(i));
			}
			
			newEdge.setStart(e1Start);
			newEdge.setEnd(e2End);
			
			e1Start.remove(e1);
			e1Start.add(newEdge);
			
			e2End.remove(e2);
			e2End.add(newEdge);
			
			model.removeVertex(e1End);
			//model.removeVertex(e2Start);
			model.removeEdge(e1);
			model.removeEdge(e2);
			
		} else if (e1End == e2End) {
			//e1Common = e1End;
			//e2Common = e2End;
			for (int i = 0; i < e1Points.size(); i++) {
				newEdgePoints.add(e1Points.get(i));
			}
			assert PointFUtils.equals(e1Points.get(e1Points.size()-1), e2Points.get(e2Points.size()-1));
			for (int i = e2Points.size()-2; i >= 0; i--) {
				newEdgePoints.add(e2Points.get(i));
			}
			
			newEdge.setStart(e1Start);
			newEdge.setEnd(e2Start);
			
			e1Start.remove(e1);
			e1Start.add(newEdge);
			
			e2Start.remove(e2);
			e2Start.add(newEdge);
			
			model.removeVertex(e1End);
			//model.removeVertex(e2End);
			model.removeEdge(e1);
			model.removeEdge(e2);
			
		} else {
			assert false;
		}
		
		return newEdge;
	}
	
}
