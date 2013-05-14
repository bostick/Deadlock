package solver;

public class Cursor {
	
	Board board;
	int[] coor;
	int side;
	
	Cursor() {
		coor = new int[2];
	}
	
	void reset(Board board) {
		this.board = board;
		coor[0] = Board.par.exit[0];
		coor[1] = Board.par.exit[1];
		side = Statics.otherSide(Board.par.side(Board.par.exit));
	}
	
	void move() {
		switch (side) {
		case 0:
			coor[0] = coor[0]-1;
			if (coor[0] == -1) {
				switch (val()) {
				case 'J':
				case 'K':
					break;
				default:
					throw new IllegalArgumentException();
				}
				int[] other = Board.par.otherJoint(coor);
				int otherSide = Board.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Board.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Board.par.rowCount-1;
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
			if (coor[1] == Board.par.colCount) {
				switch (val()) {
				case 'J':
				case 'K':
					break;
				default:
					throw new IllegalArgumentException();
				}
				int[] other = Board.par.otherJoint(coor);
				int otherSide = Board.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Board.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Board.par.rowCount-1;
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
			if (coor[0] == Board.par.rowCount) {
				switch (val()) {
				case 'J':
				case 'K':
					break;
				default:
					throw new IllegalArgumentException();
				}
				int[] other = Board.par.otherJoint(coor);
				int otherSide = Board.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Board.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Board.par.rowCount-1;
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
				switch (val()) {
				case 'J':
				case 'K':
					break;
				default:
					throw new IllegalArgumentException();
				}
				int[] other = Board.par.otherJoint(coor);
				int otherSide = Board.par.side(other);
				switch (otherSide) {
				case 0:
					coor[0] = 0;
					coor[1] = other[1];
					side = 2;
					break;
				case 1:
					coor[0] = other[0];
					coor[1] = Board.par.colCount-1;
					side = 3;
					break;
				case 2:
					coor[0] = Board.par.rowCount-1;
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
		if (coor[0] == -1 || coor[0] == Board.par.rowCount || coor[1] == -1 || coor[1] == Board.par.colCount) {
			return Board.par.val(coor);
		}
		return board.boardGet(coor);
	}
	
}

