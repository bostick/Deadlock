package solver;

public class Cursor {
	
	byte[][] board;
	int[] coor;
	int side;
	
	Cursor() {
		coor = new int[2];
	}
	
	void reset(byte[][] board) {
		this.board = board;
		coor[0] = Config.par.exit[0];
		coor[1] = Config.par.exit[1];
		side = Statics.otherSide(Config.par.side(Config.par.exit));
	}
	
	void move() {
		switch (side) {
		case 0:
			coor[0] = coor[0]-1;
			if (coor[0] == -1) {
				assert val() == 'J' || val() == 'K';
				int[] other = Config.par.otherJoint(coor);
				int otherSide = Config.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Config.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Config.par.rowCount-1;
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
			if (coor[1] == Config.par.colCount) {
				assert val() == 'J' || val() == 'K';
				int[] other = Config.par.otherJoint(coor);
				int otherSide = Config.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Config.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Config.par.rowCount-1;
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
			if (coor[0] == Config.par.rowCount) {
				assert val() == 'J' || val() == 'K';
				int[] other = Config.par.otherJoint(coor);
				int otherSide = Config.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Config.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Config.par.rowCount-1;
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
				int[] other = Config.par.otherJoint(coor);
				int otherSide = Config.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Config.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Config.par.rowCount-1;
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
		if (coor[0] == -1 || coor[0] == Config.par.rowCount || coor[1] == -1 || coor[1] == Config.par.colCount) {
			return Config.par.val(coor);
		}
		return Config.boardGet(board, coor);
	}
	
}

