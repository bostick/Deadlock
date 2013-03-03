package solver;

public class Cursor {
	Config config;
	int[] coor;
	int side;
	
	Cursor(Config config) {
		this.config = config;
		coor = new int[2];
		coor[0] = config.exit[0];
		coor[1] = config.exit[1];
		side = Config.otherSide(config.side(config.exit));
	}
	
	void move() {
		switch (side) {
		case 0:
			coor[0] = coor[0]-1;
			if (coor[0] == -1) {
				assert val() == 'J' || val() == 'K';
				int[] other = config.otherJoint(coor);
				int otherSide = config.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = config.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = config.rowCount-1;
					coor[1] = other[1];
					side = 0;
					break;
				case 3:
					coor[0] = other[0];
					coor[1] = 0;
					side = 1;
					break;
				}
			}
			break;
		case 1:
			coor[1] = coor[1]+1;
			if (coor[1] == config.colCount) {
				assert val() == 'J' || val() == 'K';
				int[] other = config.otherJoint(coor);
				int otherSide = config.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = config.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = config.rowCount-1;
					coor[1] = other[1];
					side = 0;
					break;
				case 3:
					coor[0] = other[0];
					coor[1] = 0;
					side = 1;
					break;
				}
			}
			break;
		case 2:
			coor[0] = coor[0]+1;
			if (coor[0] == config.rowCount) {
				assert val() == 'J' || val() == 'K';
				int[] other = config.otherJoint(coor);
				int otherSide = config.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = config.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = config.rowCount-1;
					coor[1] = other[1];
					side = 0;
					break;
				case 3:
					coor[0] = other[0];
					coor[1] = 0;
					side = 1;
					break;
				}
			}
			break;
		case 3:
			coor[1] = coor[1]-1;
			if (coor[1] == -1) {
				assert val() == 'J' || val() == 'K';
				int[] other = config.otherJoint(coor);
				int otherSide = config.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = config.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = config.rowCount-1;
					coor[1] = other[1];
					side = 0;
					break;
				case 3:
					coor[0] = other[0];
					coor[1] = 0;
					side = 1;
					break;
				}
			}
			break;
		}
	}
	
	byte val() {
		return config.val(coor);
	}
	
}

