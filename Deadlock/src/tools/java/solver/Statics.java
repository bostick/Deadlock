package solver;

public class Statics {
	
	public static int otherSide(int side) {
		switch (side) {
		case 0:
			return 2;
		case 1:
			return 3;
		case 2:
			return 0;
		case 3:
			return 1;
		}
		assert false;
		return -1;
	}
	
	public static boolean equals(int[] c0, int[] c1) {
		return c0[0] == c1[0] && c0[1] == c1[1];
	}
	
	public static boolean equals(int c00, int c01, int[] c1) {
		return c00 == c1[0] && c01 == c1[1];
	}
	
	static boolean greaterThanEqual(int r, int c, int r1, int c1) {
		return (r > r1) || (r == r1 && c >= c1);
	}
	
	static boolean lessThan(int r, int c, int r1, int c1) {
		return (r < r1) || (r == r1 && c < c1);
	}
	
	/**
	 * return if there was a next
	 */
	static boolean next(int r, int c, int[] out) {
		
		if (c == Config.par.colCount-1) {
			
			if (r == Config.par.rowCount-1) {
				
				return false;
				
			} else {
				out[0] = r+1;
				out[1] = 0;
			}
			
		} else {
			out[0] = r;
			out[1] = c+1;
		}
		
		return true;
	}
	
	static public boolean isXorC(byte c, byte b) {
		return b == 'X' || b == c;
	}
	
}
