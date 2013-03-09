package solver;

import java.util.List;

public class Config {
	
	public Config generatingVal;
	public Config solvingVal;
	public Config previousGeneratingConfig;
	
	public static ParentConfig par;
	
	public final byte[] board;
	
	static byte[] cars = new byte[] {'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	public Config(byte[] old) {
		board = new byte[old.length];
		System.arraycopy(old, 0, board, 0, old.length);
	}
	
	public void copyTo(Config out) {
		System.arraycopy(board, 0, out.board, 0, board.length);
	}
	
	public Config clone() {
		Config n = new Config(par.emptyBoard);
		copyTo(n);
		return n;
	}
	
	public boolean equals(Object o) {
		byte[] other = ((Config)o).board;
		if (board.length != other.length) {
			return false;
		}
		for (int i = 0; i < board.length; i++) {
			if (board[i] != other[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int hashCode() {
		int h = 17;
		for (int i = 0; i < board.length; i++) {
			h = 37 * h + board[i];
		}
		return h;
	}
	
	public String toString() {
		
		StringBuilder b = new StringBuilder();
		b.append(new String(par.ini[0]));
		b.append('\n');
		for (int i = 0; i < par.rowCount; i++) {
			b.append((char)par.ini[i+1][0]);
			for (int j = 0; j < par.colCount; j++) {
				byte bb = boardGet(i, j);
				b.append((char)bb);
			}			
			b.append((char)par.ini[i+1][par.colCount+1]);
			b.append('\n');
		}
		b.append(new String(par.ini[par.ini.length-1]));
		b.append('\n');
		return b.toString();
	};
	
	public static byte[] newBoard(int rows, int cols) {
		int actualCols;
		if (cols % 2 == 0) {
			actualCols = cols / 2;
		} else {
			actualCols = cols / 2 + 1;
		}
		return new byte[rows * actualCols];
	}
	
	byte boardGet(int[] coor) {
		return boardGet(coor[0], coor[1]);
	}
	
	byte boardGet(int r, int c) {
		
		int actualCol;
		int actualColCount = (par.colCount % 2 == 0) ? par.colCount / 2 : par.colCount / 2 + 1;
		int n;
		if (c % 2 == 0) {
			actualCol = c / 2;
			int old8bits = board[r * actualColCount + actualCol];
			n = ((old8bits & 0x0f));
		} else {
			actualCol = c / 2;
			int old8bits = board[r * actualColCount + actualCol];
			n = ((old8bits & 0xf0) >> 4);
		}
		
		switch (n) {
		case 0:
			return 'X';
		case 1:
			return 'R';
		case 2:
			return 'A';
		case 3:
			return 'B';
		case 4:
			return 'C';
		case 5:
			return 'D';
		case 6:
			return 'E';
		case 7:
			return 'F';
		case 8:
			return 'G';
		default:
			assert false;
			return 0;
		}
		
	}
	
	void boardSet(int r, int c, byte b) {
		boardSet(board, r, c, b, par.colCount);
	}
	
	public static void boardSet(byte[] board, int r, int c, byte b, int colCount) {
		
		byte actualByte = 0;
		switch (b) {
		case 'X':
			actualByte = 0;
			break;
		case 'R':
			actualByte = 1;
			break;
		case 'A':
			actualByte = 2;
			break;
		case 'B':
			actualByte = 3;
			break;
		case 'C':
			actualByte = 4;
			break;
		case 'D':
			actualByte = 5;
			break;
		case 'E':
			actualByte = 6;
			break;
		case 'F':
			actualByte = 7;
			break;
		case 'G':
			actualByte = 8;
			break;
		default:
			assert false;
			return;
		}
		
		int actualCol;
		int actualColCount = (colCount % 2 == 0) ? colCount / 2 : colCount / 2 + 1;
		if (c % 2 == 0) {
			actualCol = c / 2;
			int old8bits = board[r * actualColCount + actualCol];
			byte n = (byte)((old8bits & 0xf0) | actualByte);
			board[r * actualColCount + actualCol] = n;
		} else {
			actualCol = c / 2;
			int old8bits = board[r * actualColCount + actualCol];
			byte n = (byte)((old8bits & 0x0f) | (actualByte<<4));
			board[r * actualColCount + actualCol] = n;
		}
	}
	
	public boolean boardContainsCar(byte b) {
		for (int i = 0; i < par.rowCount; i++) {
			for (int j = 0; j < par.colCount; j++) {
				byte c = boardGet(i, j);
				if (c == b) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void clearCar(byte c, Orientation o, int size, int row, int col) {
		
		byte old;
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				old = clearCoor(row, col+0);
				assert old == c;
				old = clearCoor(row, col+1);
				assert old == c;
				break;
			case 3:
				old = clearCoor(row, col+0);
				assert old == c;
				old = clearCoor(row, col+1);
				assert old == c;
				old = clearCoor(row, col+2);
				assert old == c;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				old = clearCoor(row+0, col);
				assert old == c;
				old = clearCoor(row+1, col);
				assert old == c;
				break;
			case 3:
				old = clearCoor(row+0, col);
				assert old == c;
				old = clearCoor(row+1, col);
				assert old == c;
				old = clearCoor(row+2, col);
				assert old == c;
				break;
			}
			break;
		}
		
	}
	
	public boolean isWinning() {
		
		loadScratchInfo((byte)'R');
		Orientation o = par.scratchInfo.o;
		int r = par.scratchInfo.row;
		int c = par.scratchInfo.col;
		
		int side = par.side(par.exit);
		switch (side) {
		case 0:
			return o == Orientation.UPDOWN && Statics.equals(r-2, c, par.exit) && boardGet(r-1, c) == 'X';
		case 1:
			return o == Orientation.LEFTRIGHT && Statics.equals(r, c+3, par.exit) && boardGet(r, c+2) == 'X';
		case 2:
			return o == Orientation.UPDOWN && Statics.equals(r+3, c, par.exit) && boardGet(r+2, c) == 'X';
		case 3:
			return o == Orientation.LEFTRIGHT && Statics.equals(r, c-2, par.exit) && boardGet(r, c-1) == 'X';
		}
		
		assert false;
		return false;
	}
	
	boolean hasClearPathToExit() {
		
		par.cursor.reset(this);
		while (true) {
			if (par.cursor.val() == 'Y' || par.cursor.val() == 'X') {
				par.cursor.move();
			} else if (par.cursor.val() == 'R') {
				loadScratchInfo((byte)'R');
				Orientation o = par.scratchInfo.o;
				
				if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == Orientation.UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == Orientation.LEFTRIGHT))) {
					break;
				} else {
					par.cursor.move();
				}
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * assumes clear path
	 */
	int numberMovesToWin() {
		
		par.cursor.reset(this); 
		int moves = 0;
		while (true) {
			if (par.cursor.val() == 'Y' || par.cursor.val() == 'X') {
				par.cursor.move();
				moves++;
			} else {
				if (par.cursor.val() == 'R') {
					loadScratchInfo((byte)'R');
					Orientation o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == Orientation.UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == Orientation.LEFTRIGHT))) {
						break;
					} else {
						par.cursor.move();
						moves++;
					}
				} else {
					assert false;
				}
			}
		}
		
		return moves;
	}
	
	/**
	 * returns old value
	 */
	private byte clearCoor(int r, int c) {
		byte old = boardGet(r, c);
		boardSet(r, c, (byte)'X');
		return old;
	}
	
	boolean availableForCar(byte c, int size, Orientation o, int row, int col) {
		
		if (row < 0 || row >= par.rowCount) return false;
		if (col < 0 || col >= par.colCount) return false;
		
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				if (col+1 >= par.colCount) return false;
				if (!Statics.isXorC(c, boardGet(row, col+0))) return false;
				if (!Statics.isXorC(c, boardGet(row, col+1))) return false;
				break;
			case 3:
				if (col+2 >= par.colCount) return false;
				if (!Statics.isXorC(c, boardGet(row, col+0))) return false;
				if (!Statics.isXorC(c, boardGet(row, col+1))) return false;
				if (!Statics.isXorC(c, boardGet(row, col+2))) return false;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				if (row+1 >= par.rowCount) return false;
				if (!Statics.isXorC(c, boardGet(row+0, col))) return false;
				if (!Statics.isXorC(c, boardGet(row+1, col))) return false;
				break;
			case 3:
				if (row+2 >= par.rowCount) return false;
				if (!Statics.isXorC(c, boardGet(row+0, col))) return false;
				if (!Statics.isXorC(c, boardGet(row+1, col))) return false;
				if (!Statics.isXorC(c, boardGet(row+2, col))) return false;
				break;
			}
			break;
		}
		
		return true;
	}
	
	public boolean insertCar(byte c, Orientation o, int size, int row, int col) {
		
		if (c == 'R') {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					break;
				case 3:
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					boardSet(row, col+2, c);
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					boardSet(row+0, col, c);
					boardSet(row+1, col, c);
					break;
				case 3:
					boardSet(row+0, col, c);
					boardSet(row+1, col, c);
					boardSet(row+2, col, c);
					break;
				}
				break;
			}
			
		} else {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					break;
				case 3:
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					boardSet(row, col+2, c);
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					boardSet(row+0, col, c);
					boardSet(row+1, col, c);
					break;
				case 3:
					boardSet(row+0, col, c);
					boardSet(row+1, col, c);
					boardSet(row+2, col, c);
					break;
				}
				break;
			}
		}
		
		return true;
	}
	
	public void moveCar(byte c, int size, Orientation oldO, int oldRow, int oldCol, Orientation newO, int newRow, int newCol, Config out) {
		
		assert this.board != out.board;
		
		copyTo(out);
		
		out.clearCar(c, oldO, size, oldRow, oldCol);
		
		out.insertCar(c, newO, size, newRow, newCol);
		
		out.alphaReduce();
		
	}
	
	public List<Config> possiblePreviousMoves() {
		
		par.moves.clear();
		
		if (hasClearPathToExit()) {
			
			/*
			 * for red car, move away from exit
			 * for other cars, unblock path from red car to exit
			 */
			d;
			
		}
		
		carLoop:
		for (byte c : cars) {
			if (!par.carMapContains(c)) {
				continue;
			}
			
			/*
			 * if clearPathToExit,
			 * 		if red, only previous move was going away from exit
			 * 		if other, only previous move was interfering with winning path
			 */
			
			loadScratchInfo(c);
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			Config scratch;
			
			switch (o) {
			case LEFTRIGHT:
				
				scratch = par.scratchLeft(c);
				if (availableForCar(c, size, o, row, col-1)) {
					moveCar(c, size, o, row, col, o, row, col-1, scratch);
					if (!scratch.hasClearPathToExit()) {
						par.moves.add(scratch);
					}
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(c, par.otherJoint(row, col-1), scratch);
					if (res) {
						if (!scratch.hasClearPathToExit()) {
							par.moves.add(scratch);
						}
					}
				}
				
				scratch = par.scratchRight(c);
				if (availableForCar(c, size, o, row, col+1)) {
					moveCar(c, size, o, row, col, o, row, col+1, par.scratchRight(c));
					if (!scratch.hasClearPathToExit()) {
						par.moves.add(scratch);
					}
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(c, par.otherJoint(row, col+size), par.scratchRight(c));
					if (res) {
						if (!scratch.hasClearPathToExit()) {
							par.moves.add(scratch);
						}
					}
				}
				break;
			case UPDOWN:
				
				scratch = par.scratchUp(c);
				if (availableForCar(c, size, o, row-1, col)) {
					moveCar(c, size, o, row, col, o, row-1, col, par.scratchUp(c));
					if (!scratch.hasClearPathToExit()) {
						par.moves.add(scratch);
					}
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						if (!scratch.hasClearPathToExit()) {
							par.moves.add(scratch);
						}
					}
				}
				
				scratch = par.scratchDown(c);
				if (availableForCar(c, size, o, row+1, col)) {
					moveCar(c, size, o, row, col, o, row+1, col, par.scratchDown(c));
					if (!scratch.hasClearPathToExit()) {
						par.moves.add(scratch);
					}
				} else if (par.isJoint(row+size, col)) {
					boolean res = tryJoint(c, par.otherJoint(row+size, col), par.scratchDown(c));
					if (res) {
						if (!scratch.hasClearPathToExit()) {
							par.moves.add(scratch);
						}
					}
				}
				break;
			}
		}
		
		return par.moves;
	}
	
	/*
	 * returns true => continue carLoop
	 */
//	boolean possiblePreviousMoves_lastStep(byte c, Config scratch) {
//		if (!clearPathToExit) {
//			if (!scratch.hasClearPathToExit()) {
//				par.moves.add(scratch);
//			}
//		} else {
//			if (c == 'R') {
//				if (scratch.numberMovesToWin() > this.numberMovesToWin()) {
//					par.moves.add(scratch);
//					return true;
//				}
//			} else {
//				if (!scratch.hasClearPathToExit()) {
//					par.moves.add(scratch);
//					return true;
//				}
//			}
//		}
//		if (!scratch.hasClearPathToExit()) {
//			par.moves.add(scratch);
//		}
//		
//		return false;
//	}
	
	public List<Config> possibleNextMoves() {
		
		par.moves.clear();
		
		if (isWinning()) {
			return par.moves;
		}
		
		if (hasClearPathToExit()) {
			
			loadScratchInfo((byte)'R');
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			Config scratch;
			
			switch (o) {
			case LEFTRIGHT:
				
				scratch = par.scratchLeft((byte)'R');
				if (availableForCar((byte)'R', size, o, row, col-1) && (closerToExit)) {
					moveCar((byte)'R', size, o, row, col, o, row, col-1, scratch);
					par.moves.add(scratch);
					return par.moves;
				} else if (par.isJoint(row, col-1) && (closerToExit)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row, col-1), scratch);
					if (res) {
						par.moves.add(scratch);
						return par.moves;
					}
				}
				
				scratch = par.scratchRight((byte)'R');
				if (availableForCar((byte)'R', size, o, row, col+1) && (closerToExit)) {
					moveCar((byte)'R', size, o, row, col, o, row, col+1, scratch);
					par.moves.add(scratch);
					return par.moves;
				} else if (par.isJoint(row, col+2) && (closerToExit)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row, col+2), scratch);
					if (res) {
						par.moves.add(scratch);
						return par.moves;
					}
				}
				
				break;
			case UPDOWN:
				
				scratch = par.scratchUp((byte)'R');
				if (availableForCar((byte)'R', size, o, row-1, col) && (closerToExit)) {
					moveCar((byte)'R', size, o, row, col, o, row-1, col, scratch);
					par.moves.add(scratch);
					return par.moves;
				} else if (par.isJoint(row-1, col) && (closerToExit)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row-1, col), scratch);
					if (res) {
						par.moves.add(scratch);
						return par.moves;
					}
				}
				
				scratch = par.scratchDown((byte)'R');
				if (availableForCar((byte)'R', size, o, row+1, col) && (closerToExit)) {
					moveCar((byte)'R', size, o, row, col, o, row+1, col, scratch);
					par.moves.add(scratch);
					return par.moves;
				} else if (par.isJoint(row+2, col) && (closerToExit)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row+2, col), scratch);
					if (res) {
						par.moves.add(scratch);
						return par.moves;
					}
				}
				
				break;
			}
			
			assert false;
		}
		
		for (byte c : cars) {
			if (!par.carMapContains(c)) {
				continue;
			}
			
			/*
			 * if clearPathToExit,
			 * 		if red, only previous move was going away from exit
			 * 		if other, only previous move was interfering with winning path
			 */
			
			loadScratchInfo(c);
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			switch (o) {
			case LEFTRIGHT:
				
				if (availableForCar(c, size, o, row, col-1)) {
					moveCar(c, size, o, row, col, o, row, col-1, par.scratchLeft(c));
					par.moves.add(par.scratchLeft(c));
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(c, par.otherJoint(row, col-1), par.scratchLeft(c));
					if (res) {
						par.moves.add(par.scratchLeft(c));
					}
				}
				
				if (availableForCar(c, size, o, row, col+1)) {
					moveCar(c, size, o, row, col, o, row, col+1, par.scratchRight(c));
					par.moves.add(par.scratchRight(c));
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(c, par.otherJoint(row, col+size), par.scratchRight(c));
					if (res) {
						par.moves.add(par.scratchRight(c));
					}
				}
				
				break;
			case UPDOWN:
				
				if (availableForCar(c, size, o, row-1, col)) {
					moveCar(c, size, o, row, col, o, row-1, col, par.scratchUp(c));
					par.moves.add(par.scratchUp(c));
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						par.moves.add(par.scratchUp(c));
					}
				}
				
				if (availableForCar(c, size, o, row+1, col)) {
					moveCar(c, size, o, row, col, o, row+1, col, par.scratchDown(c));
					par.moves.add(par.scratchDown(c));
				} else if (par.isJoint(row+size, col)) {
					boolean res = tryJoint(c, par.otherJoint(row+size, col), par.scratchDown(c));
					if (res) {
						par.moves.add(par.scratchDown(c));
					}
				}
				
				break;
			}
		}
		
		return par.moves;
	}
	
	boolean tryJoint(byte c, int[] joint, Config out) {
		
		int mR = joint[0];
		int mC = joint[1];
		
		loadScratchInfo(c);
		Orientation o = par.scratchInfo.o;
		int size = par.scratchInfo.size;
		int row = par.scratchInfo.row;
		int col = par.scratchInfo.col;
		
		int mSide = par.side(joint);
		switch (mSide) {
		case 0:
			switch (size) {
			case 2:
				if (availableForCar(c, size, Orientation.UPDOWN, mR+1, mC)) {
					moveCar(c, size, o, row, col, Orientation.UPDOWN, mR+1, mC, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(c, size, Orientation.UPDOWN, mR+1, mC)) {
					moveCar(c, size, o, row, col, Orientation.UPDOWN, mR+1, mC, out);
					return true;
				}
				break;
			}
			break;
		case 1:
			switch (size) {
			case 2:
				if (availableForCar(c, size, Orientation.LEFTRIGHT, mR, mC-2)) {
					moveCar(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC-2, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(c, size, Orientation.LEFTRIGHT, mR, mC-3)) {
					moveCar(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC-3, out);
					return true;
				}
				break;
			}
			break;
		case 2:
			switch (size) {
			case 2:
				if (availableForCar(c, size, Orientation.UPDOWN, mR-2, mC)) {
					moveCar(c, size, o, row, col, Orientation.UPDOWN, mR-2, mC, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(c, size, Orientation.UPDOWN, mR-3, mC)) {
					moveCar(c, size, o, row, col, Orientation.UPDOWN, mR-3, mC, out);
					return true;
				}
				break;
			}
			break;
		case 3:
			switch (size) {
			case 2:
				if (availableForCar(c, size, Orientation.LEFTRIGHT, mR, mC+1)) {
					moveCar(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC+1, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(c, size, Orientation.LEFTRIGHT, mR, mC+1)) {
					moveCar(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC+1, out);
					return true;
				}
				break;
			}
			break;
		}
		
		return false;
	}
	
	
	public Config winningConfig() {
		int side = par.side(par.exit);
		
		Config n = new Config(board);
		
		switch (side) {
		case 0:
			n.insertCar((byte)'R', Orientation.UPDOWN, 2, par.exit[0]+2, par.exit[1]);
			return n;
		case 1:
			n.insertCar((byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]-3);
			return n;
		case 2:
			n.insertCar((byte)'R', Orientation.UPDOWN, 2, par.exit[0]-3, par.exit[1]);
			return n;
		case 3:
			n.insertCar((byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]+2);
			return n;
		}
		
		assert false;
		return null;
	}
	
	byte firstAvailableCar() {
		for (byte d : cars) {
			if (!boardContainsCar(d)) {
				return d;
			}
		}
		assert false;
		return -1;
	}
	
	public List<Config> possible2CarPlacements(List<Config> placements, boolean lastToBeAdded) {
		
		byte car = firstAvailableCar();
		int[] coor = firstAvailableCarCoor();
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-1; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(car, 2, Orientation.LEFTRIGHT, r, c) &&
						!completesBlock(2, Orientation.LEFTRIGHT, r, c) &&
						leavesRedCarGap(2, Orientation.LEFTRIGHT, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(2, Orientation.LEFTRIGHT, r, c))) {
					
					Config n = new Config(board);
					n.insertCar(car, Orientation.LEFTRIGHT, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		/*
		 * up-down
		 */
		for (int r = 0; r < par.rowCount-1; r++) {
			for (int c = 0; c < par.colCount; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(car, 2, Orientation.UPDOWN, r, c) &&
						!completesBlock(2, Orientation.UPDOWN, r, c) &&
						leavesRedCarGap(2, Orientation.UPDOWN, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(2, Orientation.UPDOWN, r, c))) {
					
					Config n = new Config(board);
					n.insertCar(car, Orientation.UPDOWN, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public List<Config> possible3CarPlacements(List<Config> placements, boolean lastToBeAdded) {
		
		byte car = firstAvailableCar();
		int[] coor = firstAvailableCarCoor();
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-2; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(car, 3, Orientation.LEFTRIGHT, r, c) &&
						!completesBlock(3, Orientation.LEFTRIGHT, r, c) &&
						leavesRedCarGap(3, Orientation.LEFTRIGHT, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(3, Orientation.LEFTRIGHT, r, c))) {
					
					Config n = new Config(board);
					n.insertCar(car, Orientation.LEFTRIGHT, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		/*
		 * up-down
		 */
		for (int r = 0; r < par.rowCount-2; r++) {
			for (int c = 0; c < par.colCount; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(car, 3, Orientation.UPDOWN, r, c) &&
						!completesBlock(3, Orientation.UPDOWN, r, c) &&
						leavesRedCarGap(3, Orientation.UPDOWN, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(3, Orientation.UPDOWN, r, c))) {
					
					Config n = new Config(board);
					n.insertCar(car, Orientation.UPDOWN, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	int[] firstAvailableCarCoor() {
		
		par.test[0] = 0;
		par.test[1] = 0;
		for (byte b : cars) {
			if (b == 'R') {
				continue;
			}
			boolean res = loadScratchInfo(b);
			
			if (!res) {
				break;
			}
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			res = Statics.next(row, col, par.test);
			if (!res) {
				return null;
			}
		}
		
		return par.test;
	}
	
	/*
	 * returns true if inserting specified car completes an immoveable block from one side to the next
	 */
	boolean completesBlock(int size, Orientation o, int r, int c) {
		
		switch (o) {
		case LEFTRIGHT: {
			
			byte currentCar = 'X';
			for (int i = 0; i < par.colCount; i++) {
				byte b = boardGet(r, i);
				if (b == 'X') {
					if (i >= c && i <= c+size) {
						continue;
					} else {
						return false;
					}
				} else if (b == currentCar) {
					continue;
				} else {
					loadScratchInfo(b);
					Orientation so = par.scratchInfo.o;
					if (so != Orientation.LEFTRIGHT) {
						return false;
					}
					currentCar = b;
				}
			}
			return true;
		}
			
		case UPDOWN: {
			
			byte currentCar = 'X';
			for (int i = 0; i < par.rowCount; i++) {
				byte b = boardGet(i, c);
				if (b == 'X') {
					if (i >= r && i <= r+size) {
						continue;
					} else {
						return false;
					}
				} else if (b == currentCar) {
					continue;
				} else {
					loadScratchInfo(b);
					Orientation so = par.scratchInfo.o;
					if (so != Orientation.UPDOWN) {
						return false;
					}
					currentCar = b;
				}
			}
			return true;	
		}
		
		}
		
		assert false;
		return false;
	}
	
	/*
	 * gap between red car and exit
	 */
	boolean leavesRedCarGap(int size, Orientation o, int r, int c) {
		int side = par.side(par.exit);
		
		int gapR = -1;
		int gapC = -1;
		
		switch (side) {
		case 0:
			gapR = par.exit[0]+1;
			gapC = par.exit[1];
			break;
		case 1:
			gapR = par.exit[0];
			gapC = par.exit[1]-1;
			break;
		case 2:
			gapR = par.exit[0]-1;
			gapC = par.exit[1];
			break;
		case 3:
			gapR = par.exit[0];
			gapC = par.exit[1]+1;
			break;
		}
		
		switch (o) {
		case LEFTRIGHT:
			return gapR != r || (gapC < c || gapC > c+size);
		case UPDOWN:
			return gapC != c || (gapR < r || gapR > r+size);
		}
		
		assert false;
		return false;
	}
	
	boolean someCarIntersectsWinnable(int size, Orientation o, int r, int c) {
		
		for (byte d : cars) {
			boolean res = loadScratchInfo(d);
			if (res) {
				Orientation so = par.scratchInfo.o;
				int ss = par.scratchInfo.size;
				int sr = par.scratchInfo.row;
				int sc = par.scratchInfo.col;
				if (intersectsWithWinnable(ss, so, sr, sc)) {
					return true;
				}
			} else {
				break;
			}
		}
		return intersectsWithWinnable(size, o, r, c);
	}
	
	boolean intersectsWithWinnable(int size, Orientation o, int r, int c) {
		
		switch (o) {
		case LEFTRIGHT:
			return par.winnableCols;
		case UPDOWN:
			return par.winnableRows;
		}
		
		assert false;
		return false;
	}
	
	public boolean loadScratchInfo(byte c) {
		
		for (int i = 0; i < par.rowCount; i++) {
			for (int j = 0; j < par.colCount; j++) {
				byte b = boardGet(i, j);
				if (b == c) {
					if (i+1 < par.rowCount && boardGet(i+1, j) == c) {
						if (i+2 < par.colCount && boardGet(i+2, j) == c) {
							par.scratchInfo.o = Orientation.UPDOWN;
							par.scratchInfo.size = 3;
							par.scratchInfo.row = i;
							par.scratchInfo.col = j;
							return true;
						} else {
							par.scratchInfo.o = Orientation.UPDOWN;
							par.scratchInfo.size = 2;
							par.scratchInfo.row = i;
							par.scratchInfo.col = j;
							return true;
						}
					} else {
						assert boardGet(i, j+1) == c;
						if (j+2 < par.colCount && boardGet(i, j+2) == c) {
							par.scratchInfo.o = Orientation.LEFTRIGHT;
							par.scratchInfo.size = 3;
							par.scratchInfo.row = i;
							par.scratchInfo.col = j;
							return true;
						} else {
							par.scratchInfo.o = Orientation.LEFTRIGHT;
							par.scratchInfo.size = 2;
							par.scratchInfo.row = i;
							par.scratchInfo.col = j;
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	void alphaReduce() {
		
		Orientation so;
		int ss;
		int sr;
		int sc;
		
		boolean res = loadScratchInfo((byte)'A');
		if (res) {
			Orientation ao = par.scratchInfo.o;
			int as = par.scratchInfo.size;
			int ar = par.scratchInfo.row;
			int ac = par.scratchInfo.col;
			
			res = loadScratchInfo((byte)'B');
			if (res) {
				Orientation bo = par.scratchInfo.o;
				int bs = par.scratchInfo.size;
				int br = par.scratchInfo.row;
				int bc = par.scratchInfo.col;
				
				if (!(Statics.lessThan(ar, ac, br, bc))) {
					swap((byte)'A', ao, as, ar, ac, (byte)'B', bo, bs, br, bc);
					so = ao;
					ss = as;
					sr = ar;
					sc = ac;
					ao = bo;
					as = bs;
					ar = br;
					ac = bc;
					bo = so;
					bs = ss;
					br = sr;
					bc = sc;
				}
				
				res = loadScratchInfo((byte)'C');
				if (res) {
					Orientation co = par.scratchInfo.o;
					int cs = par.scratchInfo.size;
					int cr = par.scratchInfo.row;
					int cc = par.scratchInfo.col;
					
					if (!(Statics.lessThan(ar, ac, cr, cc))) {
						swap((byte)'A', ao, as, ar, ac, (byte)'C', co, cs, cr, cc);
						so = ao;
						ss = as;
						sr = ar;
						sc = ac;
						ao = co;
						as = cs;
						ar = cr;
						ac = cc;
						co = so;
						cs = ss;
						cr = sr;
						cc = sc;
					}
					if (!(Statics.lessThan(br, bc, cr, cc))) {
						swap((byte)'B', bo, bs, br, bc, (byte)'C', co, cs, cr, cc);
						so = bo;
						ss = bs;
						sr = br;
						sc = bc;
						bo = co;
						bs = cs;
						br = cr;
						bc = cc;
						co = so;
						cs = ss;
						cr = sr;
						cc = sc;
					}
					
					res = loadScratchInfo((byte)'D');
					if (res) {
						Orientation doo = par.scratchInfo.o;
						int ds = par.scratchInfo.size;
						int dr = par.scratchInfo.row;
						int dc = par.scratchInfo.col;
						
						if (!(Statics.lessThan(ar, ac, dr, dc))) {
							swap((byte)'A', ao, as, ar, ac, (byte)'D', doo, ds, dr, dc);
							so = ao;
							ss = as;
							sr = ar;
							sc = ac;
							ao = doo;
							as = ds;
							ar = dr;
							ac = dc;
							doo = so;
							ds = ss;
							dr = sr;
							dc = sc;
						}
						if (!(Statics.lessThan(br, bc, dr, dc))) {
							swap((byte)'B', bo, bs, br, bc, (byte)'D', doo, ds, dr, dc);
							so = bo;
							ss = bs;
							sr = br;
							sc = bc;
							bo = doo;
							bs = ds;
							br = dr;
							bc = dc;
							doo = so;
							ds = ss;
							dr = sr;
							dc = sc;
						}
						if (!(Statics.lessThan(cr, cc, dr, dc))) {
							swap((byte)'C', co, cs, cr, cc, (byte)'D', doo, ds, dr, dc);
							so = co;
							ss = cs;
							sr = cr;
							sc = cc;
							co = doo;
							cs = ds;
							cr = dr;
							cc = dc;
							doo = so;
							ds = ss;
							dr = sr;
							dc = sc;
						}
						
						res = loadScratchInfo((byte)'E');
						if (res) {
							Orientation eo = par.scratchInfo.o;
							int es = par.scratchInfo.size;
							int er = par.scratchInfo.row;
							int ec = par.scratchInfo.col;
							
							if (!(Statics.lessThan(ar, ac, er, ec))) {
								swap((byte)'A', ao, as, ar, ac, (byte)'E', eo, es, er, ec);
								so = ao;
								ss = as;
								sr = ar;
								sc = ac;
								ao = eo;
								as = es;
								ar = er;
								ac = ec;
								eo = so;
								es = ss;
								er = sr;
								ec = sc;
							}
							if (!(Statics.lessThan(br, bc, er, ec))) {
								swap((byte)'B', bo, bs, br, bc, (byte)'E', eo, es, er, ec);
								so = bo;
								ss = bs;
								sr = br;
								sc = bc;
								bo = eo;
								bs = es;
								br = er;
								bc = ec;
								eo = so;
								es = ss;
								er = sr;
								ec = sc;
							}
							if (!(Statics.lessThan(cr, cc, er, ec))) {
								swap((byte)'C', co, cs, cr, cc, (byte)'E', eo, es, er, ec);
								so = co;
								ss = cs;
								sr = cr;
								sc = cc;
								co = eo;
								cs = es;
								cr = er;
								cc = ec;
								eo = so;
								es = ss;
								er = sr;
								ec = sc;
							}
							if (!(Statics.lessThan(dr, dc, er, ec))) {
								swap((byte)'D', doo, ds, dr, dc, (byte)'E', eo, es, er, ec);
								so = doo;
								ss = ds;
								sr = dr;
								sc = dc;
								doo = eo;
								ds = es;
								dr = er;
								dc = ec;
								eo = so;
								es = ss;
								er = sr;
								ec = sc;
							}
							
							res = loadScratchInfo((byte)'F');
							if (res) {
								Orientation fo = par.scratchInfo.o;
								int fs = par.scratchInfo.size;
								int fr = par.scratchInfo.row;
								int fc = par.scratchInfo.col;
								
								if (!(Statics.lessThan(ar, ac, fr, fc))) {
									swap((byte)'A', ao, as, ar, ac, (byte)'F', fo, fs, fr, fc);
									so = ao;
									ss = as;
									sr = ar;
									sc = ac;
									ao = fo;
									as = fs;
									ar = fr;
									ac = fc;
									fo = so;
									fs = ss;
									fr = sr;
									fc = sc;
								}
								if (!(Statics.lessThan(br, bc, fr, fc))) {
									swap((byte)'B', bo, bs, br, bc, (byte)'F', fo, fs, fr, fc);
									so = bo;
									ss = bs;
									sr = br;
									sc = bc;
									bo = fo;
									bs = fs;
									br = fr;
									bc = fc;
									fo = so;
									fs = ss;
									fr = sr;
									fc = sc;
								}
								if (!(Statics.lessThan(cr, cc, fr, fc))) {
									swap((byte)'C', co, cs, cr, cc, (byte)'F', fo, fs, fr, fc);
									so = co;
									ss = cs;
									sr = cr;
									sc = cc;
									co = fo;
									cs = fs;
									cr = fr;
									cc = fc;
									fo = so;
									fs = ss;
									fr = sr;
									fc = sc;
								}
								if (!(Statics.lessThan(dr, dc, fr, fc))) {
									swap((byte)'D', doo, ds, dr, dc, (byte)'F', fo, fs, fr, fc);
									so = doo;
									ss = ds;
									sr = dr;
									sc = dc;
									doo = fo;
									ds = fs;
									dr = fr;
									dc = fc;
									fo = so;
									fs = ss;
									fr = sr;
									fc = sc;
								}
								if (!(Statics.lessThan(er, ec, fr, fc))) {
									swap((byte)'E', eo, es, er, ec, (byte)'F', fo, fs, fr, fc);
									so = eo;
									ss = es;
									sr = er;
									sc = ec;
									eo = fo;
									es = fs;
									er = fr;
									ec = fc;
									fo = so;
									fs = ss;
									fr = sr;
									fc = sc;
								}
								
								res = loadScratchInfo((byte)'G');
								if (res) {
									Orientation go = par.scratchInfo.o;
									int gs = par.scratchInfo.size;
									int gr = par.scratchInfo.row;
									int gc = par.scratchInfo.col;
									
									if (!(Statics.lessThan(ar, ac, gr, gc))) {
										assert false;
									}
									if (!(Statics.lessThan(br, bc, gr, gc))) {
										assert false;
									}
									if (!(Statics.lessThan(cr, cc, gr, gc))) {
										assert false;
									}
									if (!(Statics.lessThan(dr, dc, gr, gc))) {
										assert false;
									}
									if (!(Statics.lessThan(er, ec, gr, gc))) {
										assert false;
									}
									if (!(Statics.lessThan(fr, fc, gr, gc))) {
										assert false;
									}
									
									assert false;
									
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	void swap(byte a, Orientation ao, int as, int ar, int ac, byte b, Orientation bo, int bs, int br, int bc) {
		
		clearCar(a, ao, as, ar, ac);
		clearCar(b, bo, bs, br, bc);
		
		insertCar(a, bo, bs, br, bc);
		insertCar(b, ao, as, ar, ac);
		
	}
	
}
