package solver;


public class CarInfo {
	Orientation o;
	int row;
	int col;
	int size;
	
	private int hash;
	
	public CarInfo() {
		
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + o.hashCode();
			h = 37 * h + row;
			h = 37 * h + col;
			h = 37 * h + size;
			hash = h;
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		assert false;
		return false;
	}
	
}
