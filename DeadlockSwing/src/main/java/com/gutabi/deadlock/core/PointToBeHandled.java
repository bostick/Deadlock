package com.gutabi.deadlock.core;

import java.util.Comparator;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

public class PointToBeHandled {
	
	private final Point p;
	private final double param;
	private final Event event;
	private final Point eventSource;
	
	private final int hash;
	
	public enum Event {
		
		INTERSECTION { public String toString() { return "+"; } },
		INTERSECTION_START { public String toString() { return "<+"; } },
		INTERSECTION_END { public String toString() { return "+>"; } },
		
		OVERLAP_START { public String toString() { return "<="; } },
		OVERLAP_END { public String toString() { return "=>"; } },
		
		A { public String toString() { return "."; } },
		A_1 { public String toString() { return "(1) ."; } },
		A_2 { public String toString() { return "(2) ."; } },
		A_3 { public String toString() { return "(3) ."; } },
		A_4 { public String toString() { return "(4) ."; } },
		A_5 { public String toString() { return "(5) ."; } },
		A_6 { public String toString() { return "(6) ."; } },
		A_7 { public String toString() { return "(7) ."; } },
		A_8 { public String toString() { return "(8) ."; } },
		A_9 { public String toString() { return "(9) ."; } },
		A_10 { public String toString() { return "(10) ."; } },
		A_11 { public String toString() { return "(11) ."; } },
		A_12 { public String toString() { return "(12) ."; } },
		A_13 { public String toString() { return "(13) ."; } },
		A_14 { public String toString() { return "(14) ."; } },
		A_15 { public String toString() { return "(15) ."; } },
		A_16 { public String toString() { return "(16) ."; } },
		A_17 { public String toString() { return "(17) ."; } },
		A_18 { public String toString() { return "(18) ."; } },
		A_19 { public String toString() { return "(19) ."; } },
		A_20 { public String toString() { return "(20) ."; } },
		A_21 { public String toString() { return "(21) ."; } },
		A_22 { public String toString() { return "(22) ."; } },
		A_23 { public String toString() { return "(23) ."; } },
		A_24 { public String toString() { return "(24) ."; } },
		A_25 { public String toString() { return "(25) ."; } },
		
		B { public String toString() { return "."; } },
		
//		CLOSE_POINT { public String toString() { return "V"; } },
		
		CLOSE_START { public String toString() { return "<<"; } },
		CLOSE_END { public String toString() { return ">>"; } },
		
		//merged
		A_INTERSECTION { public String toString() { return ". +"; } },
		A_1_INTERSECTION { public String toString() { return "(1) . +"; } },
		A_2_INTERSECTION { public String toString() { return "(2) . +"; } },
		A_3_INTERSECTION { public String toString() { return "(3) . +"; } },
		A_4_INTERSECTION { public String toString() { return "(4) . +"; } },
		A_5_INTERSECTION { public String toString() { return "(5) . +"; } },
		A_6_INTERSECTION { public String toString() { return "(6) . +"; } },
		A_7_INTERSECTION { public String toString() { return "(7) . +"; } },
		A_8_INTERSECTION { public String toString() { return "(8) . +"; } },
		A_9_INTERSECTION { public String toString() { return "(9) . +"; } },
		A_10_INTERSECTION { public String toString() { return "(10) . +"; } },
		A_11_INTERSECTION { public String toString() { return "(11) . +"; } },
		A_12_INTERSECTION { public String toString() { return "(12) . +"; } },
		A_13_INTERSECTION { public String toString() { return "(13) . +"; } },
		A_14_INTERSECTION { public String toString() { return "(14) . +"; } },
		A_15_INTERSECTION { public String toString() { return "(15) . +"; } },
		A_16_INTERSECTION { public String toString() { return "(16) . +"; } },
		A_17_INTERSECTION { public String toString() { return "(17) . +"; } },
		A_18_INTERSECTION { public String toString() { return "(18) . +"; } },
		A_19_INTERSECTION { public String toString() { return "(19) . +"; } },
		A_20_INTERSECTION { public String toString() { return "(20) . +"; } },
		A_21_INTERSECTION { public String toString() { return "(21) . +"; } },
		A_22_INTERSECTION { public String toString() { return "(22) . +"; } },
		A_23_INTERSECTION { public String toString() { return "(23) . +"; } },
		A_24_INTERSECTION { public String toString() { return "(24) . +"; } },
		A_25_INTERSECTION { public String toString() { return "(25) . +"; } },
		A_26_INTERSECTION { public String toString() { return "(26) . +"; } },
		A_27_INTERSECTION { public String toString() { return "(27) . +"; } },
		A_28_INTERSECTION { public String toString() { return "(28) . +"; } },
		
		B_CLOSE_END { public String toString() { return ">> ."; } },
		B_INTERSECTION_END { public String toString() { return "+> ."; } },
//		B_CLOSE_END_CLOSE_POINT { public String toString() { return ">> V ."; } },
		B_CLOSE_END_CLOSE_END { public String toString() { return ">> >> ."; } },
		B_INTERSECTION_END_CLOSE_END { public String toString() { return "+> >> ."; } },
		B_INTERSECTION_END_CLOSE_END_CLOSE_END { public String toString() { return "+> >> >> ."; } },
		B_INTERSECTION_END_CLOSE_END_CLOSE_END_CLOSE_END { public String toString() { return "+> >> >> >> ."; } },
		B_CLOSE_END_CLOSE_END_CLOSE_END { public String toString() { return ">> >> >> ."; } },
		B_CLOSE_END_CLOSE_END_CLOSE_END_CLOSE_END { public String toString() { return ">> >> >> >> ."; } },
//		ENDPOINT_CLOSE_POINT { public String toString() { return ". V"; } },
//		ENDPOINT_CLOSE_POINT_INTERSECTION { public String toString() { return ". V +"; } },
//		ENDPOINT_OVERLAP_START { public String toString() { return ". <="; } },
//		ENDPOINT_INTERSECTION_END { public String toString() { return ". +>"; } },
//		ENDPOINT_INTERSECTION_END_CLOSE_END { public String toString() { return ". +> >>"; } },
		INTERSECTION_END_CLOSE_END_CLOSE_END { public String toString() { return "+> >> >>"; } },
//		INTERSECTION_END_CLOSE_END_CLOSE_END_CLOSE_END { public String toString() { return "+> >> >> >>"; } },
		INTERSECTION_END_CLOSE_END { public String toString() { return "+> >>"; } },
		CLOSE_END_CLOSE_END { public String toString() { return ">> >>"; } },
		CLOSE_END_CLOSE_END_CLOSE_END { public String toString() { return ">> >> >>"; } },
		;
		
		int already;
		
		public static Event merge(Event e1, Event e2) {
			switch (e1) {
			case A:
				switch (e2) {
				case INTERSECTION:
					return A_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_1:
				switch (e2) {
				case INTERSECTION:
					return A_1_INTERSECTION;
				case CLOSE_END:
					return A;
				default:
					throw new AssertionError();
				}
			case A_2:
				switch (e2) {
				case INTERSECTION:
					return A_2_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_2_INTERSECTION:
				switch (e2) {
				case CLOSE_END:
					return A_1_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_3:
				switch (e2) {
				case INTERSECTION:
					return A_3_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_4:
				switch (e2) {
				case INTERSECTION:
					return A_4_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_5:
				switch (e2) {
				case INTERSECTION:
					return A_5_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_6:
				switch (e2) {
				case INTERSECTION:
					return A_6_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_7:
				switch (e2) {
				case INTERSECTION:
					return A_7_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_8:
				switch (e2) {
				case INTERSECTION:
					return A_8_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_9:
				switch (e2) {
				case INTERSECTION:
					return A_9_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_10:
				switch (e2) {
				case INTERSECTION:
					return A_10_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_11:
				switch (e2) {
				case INTERSECTION:
					return A_11_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_12:
				switch (e2) {
				case INTERSECTION:
					return A_12_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_13:
				switch (e2) {
				case INTERSECTION:
					return A_13_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_14:
				switch (e2) {
				case INTERSECTION:
					return A_14_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_15:
				switch (e2) {
				case INTERSECTION:
					return A_15_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_16:
				switch (e2) {
				case INTERSECTION:
					return A_16_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_17:
				switch (e2) {
				case INTERSECTION:
					return A_17_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_18:
				switch (e2) {
				case INTERSECTION:
					return A_18_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_19:
				switch (e2) {
				case INTERSECTION:
					return A_19_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_20:
				switch (e2) {
				case INTERSECTION:
					return A_20_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_21:
				switch (e2) {
				case INTERSECTION:
					return A_21_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_22:
				switch (e2) {
				case INTERSECTION:
					return A_22_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_23:
				switch (e2) {
				case INTERSECTION:
					return A_23_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case A_24:
				switch (e2) {
				case INTERSECTION:
					return A_24_INTERSECTION;
				default:
					throw new AssertionError();
				}
			case B:
				switch (e2) {
				case CLOSE_END:
					return B_CLOSE_END;
				case INTERSECTION_END:
					return B_INTERSECTION_END;
				default:
					throw new AssertionError();
				}
			case B_CLOSE_END:
				switch (e2) {
//				case CLOSE_POINT:
//					return B_CLOSE_END_CLOSE_POINT;
				case CLOSE_END:
					return B_CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case B_INTERSECTION_END:
				switch (e2) {
//				case CLOSE_POINT:
//					return B_CLOSE_END_CLOSE_POINT;
				case CLOSE_END:
					return B_INTERSECTION_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case B_CLOSE_END_CLOSE_END:
				switch (e2) {
				case CLOSE_END:
					return B_CLOSE_END_CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case B_CLOSE_END_CLOSE_END_CLOSE_END:
				switch (e2) {
				case CLOSE_END:
					return B_CLOSE_END_CLOSE_END_CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case B_INTERSECTION_END_CLOSE_END:
				switch (e2) {
				case CLOSE_END:
					return B_INTERSECTION_END_CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case B_INTERSECTION_END_CLOSE_END_CLOSE_END:
				switch (e2) {
				case CLOSE_END:
					return B_INTERSECTION_END_CLOSE_END_CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case INTERSECTION_END:
				switch (e2) {
				case CLOSE_END:
					return INTERSECTION_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case INTERSECTION_END_CLOSE_END:
				switch (e2) {
				case CLOSE_END:
					return INTERSECTION_END_CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case CLOSE_END:
				switch (e2) {
				case CLOSE_END:
					return CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
			case CLOSE_END_CLOSE_END:
				switch (e2) {
				case CLOSE_END:
					return CLOSE_END_CLOSE_END_CLOSE_END;
				default:
					throw new AssertionError();
				}
//			case INTERSECTION_END_CLOSE_END_CLOSE_END:
//				switch (e2) {
//				case CLOSE_END:
//					return INTERSECTION_END_CLOSE_END_CLOSE_END_CLOSE_END;
//				default:
//					throw new AssertionError();
//				}
			default:
				throw new AssertionError();
			}
		}
		
		public static Event increment(Event e) {
			switch (e) {
			case A:
				return A_1;
			case A_1:
				return A_2;
			case A_2:
				return A_3;
			case A_3:
				return A_4;
			case A_4:
				return A_5;
			case A_5:
				return A_6;
			case A_6:
				return A_7;
			case A_7:
				return A_8;
			case A_8:
				return A_9;
			case A_9:
				return A_10;
			case A_10:
				return A_11;
			case A_11:
				return A_12;
			case A_12:
				return A_13;
			case A_13:
				return A_14;
			case A_14:
				return A_15;
			case A_15:
				return A_16;
			case A_16:
				return A_17;
			case A_17:
				return A_18;
			case A_18:
				return A_19;
			case A_19:
				return A_20;
			case A_20:
				return A_21;
			case A_21:
				return A_22;
			case A_22:
				return A_23;
			case A_23:
				return A_24;
			case A_24:
				return A_25;
			case A_INTERSECTION:
				return A_1_INTERSECTION;
			case A_1_INTERSECTION:
				return A_2_INTERSECTION;
			case A_2_INTERSECTION:
				return A_3_INTERSECTION;
			case A_3_INTERSECTION:
				return A_4_INTERSECTION;
			case A_4_INTERSECTION:
				return A_5_INTERSECTION;
			case A_5_INTERSECTION:
				return A_6_INTERSECTION;
			case A_6_INTERSECTION:
				return A_7_INTERSECTION;
			case A_7_INTERSECTION:
				return A_8_INTERSECTION;
			case A_8_INTERSECTION:
				return A_9_INTERSECTION;
			case A_9_INTERSECTION:
				return A_10_INTERSECTION;
			case A_10_INTERSECTION:
				return A_11_INTERSECTION;
			case A_11_INTERSECTION:
				return A_12_INTERSECTION;
			case A_12_INTERSECTION:
				return A_13_INTERSECTION;
			case A_13_INTERSECTION:
				return A_14_INTERSECTION;
			case A_14_INTERSECTION:
				return A_15_INTERSECTION;
			case A_15_INTERSECTION:
				return A_16_INTERSECTION;
			case A_16_INTERSECTION:
				return A_17_INTERSECTION;
			case A_17_INTERSECTION:
				return A_18_INTERSECTION;
			case A_18_INTERSECTION:
				return A_19_INTERSECTION;
			case A_19_INTERSECTION:
				return A_20_INTERSECTION;
			case A_20_INTERSECTION:
				return A_21_INTERSECTION;
			case A_21_INTERSECTION:
				return A_22_INTERSECTION;
			case A_22_INTERSECTION:
				return A_23_INTERSECTION;
			case A_23_INTERSECTION:
				return A_24_INTERSECTION;
			case A_24_INTERSECTION:
				return A_25_INTERSECTION;
			case A_25_INTERSECTION:
				return A_26_INTERSECTION;
			case A_26_INTERSECTION:
				return A_27_INTERSECTION;
			case A_27_INTERSECTION:
				return A_28_INTERSECTION;
			default:
				throw new AssertionError();
			}
		}
		
		public static Event decrement(Event e) {
			switch (e) {
			default:
				throw new AssertionError();
			}
		}
	}
	
	public PointToBeHandled(Point p, double param, Event event, Point eventSource) {
//		assert param >= 0.0;
//		assert param <= 1.0;
		this.p = p;
		this.param = param;
		this.event = event;
		this.eventSource = eventSource;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		h = 37 * h + event.hashCode();
		if (eventSource != null) {
			h = 37 * h + eventSource.hashCode();
		}
		hash = h;
		
	}
	
	public Point getPoint() {
		return p;
	}
	
	public double getParam() {
		return param;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public Point getEventSource() {
		return eventSource;
	}
	
	@Override
	public String toString() {
		return event.toString();
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof PointToBeHandled)) {
			return false;
		} else {
			PointToBeHandled b = (PointToBeHandled)o;
			boolean res = doubleEquals(param, b.param);
			if (res) {
				assert p.equals(b.p);
			}
			return res;
		}
	}
	
	static class PTBHComparator implements Comparator<PointToBeHandled> {
		@Override
		public int compare(PointToBeHandled a, PointToBeHandled b) {
			if (doubleEquals(a.param, b.param)) {
				assert a.p.equals(b.p);
				return 0;
			} else if (a.param < b.param) {
				return -1;
			} else {
				return 1;
			}
		}
	}
	
	public static Comparator<PointToBeHandled> COMPARATOR = new PTBHComparator();
	
}
