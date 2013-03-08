package solver;

import java.util.List;

public class Config {
	
	public Config generatingVal;
	public Config solvingVal;
	public Config previousGeneratingConfig;
	
	final ParentConfig par;
	public final byte[] board;
	
//	public boolean bs;
	
	static byte[] cars = new byte[] {'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	
	public static long configCounter = 0;
	
	public Config(ParentConfig par, byte[] old) {
		configCounter++;
		this.par = par;
		board = copy(old);
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
	
	public void copy(Config out) {
		assert out.par == par;
		for (int i = 0; i < board.length; i++) {
			out.board[i] = board[i];
		}
//		out.bs = bs;
	}
	
	public Config copy() {
		Config n = par.newConfig();
		copy(n);
		return n;
	}
	
	byte boardGet(int r, int c) {
		
		int actualCol;
		int actualColCount = (par.colCount % 2 == 0) ? par.colCount / 2 : par.colCount / 2 + 1;
		int n;
		if (c % 2 == 0) {
			actualCol = c / 2;
//			actualColCount = par.colCount / 2;
			int old8bits = board[r * actualColCount + actualCol];
			n = ((old8bits & 0x0f));
		} else {
			actualCol = c / 2;
//			actualColCount = c / 2 + 1;
			int old8bits = board[r * actualColCount + actualCol];
			n = ((old8bits & 0xf0) >> 4);
		}
		
		switch (n) {
		case 0:
			return ' ';
		case 1:
			return 'X';
		case 2:
			return 'Y';
		case 3:
			return 'J';
		case 4:
			return 'K';
		case 5:
			return 'R';
		case 6:
			return 'A';
		case 7:
			return 'B';
		case 8:
			return 'C';
		case 9:
			return 'D';
		case 10:
			return 'E';
		case 11:
			return 'F';
		case 12:
			return 'G';
		}
		
		assert false;
		return 0;
	}
	
	void boardSet(int r, int c, byte b) {
		boardSet(board, r, c, b, par.colCount);
	}
	
	public static void boardSet(byte[] board, int r, int c, byte b, int colCount) {
		
		byte actualByte = 0;
		switch (b) {
		case ' ':
			actualByte = 0;
			break;
		case 'X':
			actualByte = 1;
			break;
		case 'Y':
			actualByte = 2;
			break;
		case 'J':
			actualByte = 3;
			break;
		case 'K':
			actualByte = 4;
			break;
		case 'R':
			actualByte = 5;
			break;
		case 'A':
			actualByte = 6;
			break;
		case 'B':
			actualByte = 7;
			break;
		case 'C':
			actualByte = 8;
			break;
		case 'D':
			actualByte = 9;
			break;
		case 'E':
			actualByte = 10;
			break;
		case 'F':
			actualByte = 11;
			break;
		case 'G':
			actualByte = 12;
			break;
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
	
	public static byte[] newBoard(int rows, int cols) {
		int actualCols;
		if (cols % 2 == 0) {
			actualCols = cols / 2;
		} else {
			actualCols = cols / 2 + 1;
		}
		return new byte[rows * actualCols];
	}
	
	public boolean iniContainsCar(byte b) {
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
	
	public byte val(int[] coor) {
		return boardGet(coor[0], coor[1]);
	}
	
	public byte val(int r, int c) {
		return boardGet(r, c);
	}
	
	public static boolean equals(int[] c0, int[] c1) {
		return c0[0] == c1[0] && c0[1] == c1[1];
	}
	
	public static boolean equals(int c00, int c01, int[] c1) {
		return c00 == c1[0] && c01 == c1[1];
	}
	
	public static byte[] copy(byte[] old) {
		byte[] newIni = new byte[old.length];
		System.arraycopy(old, 0, newIni, 0, old.length);
		return newIni;
	}
	
	public void clearIni(byte c, Orientation o, int size, int row, int col) {
		
		byte old;
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				old = clear(row, col+0);
				assert old == c;
				old = clear(row, col+1);
				assert old == c;
				break;
			case 3:
				old = clear(row, col+0);
				assert old == c;
				old = clear(row, col+1);
				assert old == c;
				old = clear(row, col+2);
				assert old == c;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				old = clear(row+0, col);
				assert old == c;
				old = clear(row+1, col);
				assert old == c;
				break;
			case 3:
				old = clear(row+0, col);
				assert old == c;
				old = clear(row+1, col);
				assert old == c;
				old = clear(row+2, col);
				assert old == c;
				break;
			}
			break;
		}
		
	}
	
	public boolean isWinning() {
		
		int side = par.side(par.exit);
		switch (side) {
		case 0:
			return val(par.exit[0]+1, par.exit[1]) == 'R' && val(par.exit[0]+2, par.exit[1]) == 'R';
		case 1:
			return val(par.exit[0], par.exit[1]-2) == 'R' && val(par.exit[0], par.exit[1]-1) == 'R';
		case 2:
			return val(par.exit[0]-2, par.exit[1]) == 'R' && val(par.exit[0]-1, par.exit[1]) == 'R';
		case 3:
			return val(par.exit[0], par.exit[1]+1) == 'R' && val(par.exit[0], par.exit[1]+2) == 'R';
		}
		
		assert false;
		return false;
	}
	
	boolean isClearPathToExit() {
		
		par.cursor.reset(this);
		while (true) {
			if (par.cursor.val() == 'Y' || par.cursor.val() == 'X') {
				par.cursor.move();
			} else if (par.cursor.val() == 'R') {
				carMapGet((byte)'R');
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
					carMapGet((byte)'R');
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
	
	private byte clear(int r, int c) {
		byte old = boardGet(r, c);
		boardSet(r, c, (byte)'X');
		return old;
	}
	
	boolean available(byte c, int size, Orientation o, int row, int col) {
		
		if (row < 0 || row >= par.rowCount) return false;
		if (col < 0 || col >= par.colCount) return false;
		
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				if (col+1 >= par.colCount) return false;
				if (!isXorC(c, boardGet(row, col+0))) return false;
				if (!isXorC(c, boardGet(row, col+1))) return false;
				break;
			case 3:
				if (col+2 >= par.colCount) return false;
				if (!isXorC(c, boardGet(row, col+0))) return false;
				if (!isXorC(c, boardGet(row, col+1))) return false;
				if (!isXorC(c, boardGet(row, col+2))) return false;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				if (row+1 >= par.rowCount) return false;
				if (!isXorC(c, boardGet(row+0, col))) return false;
				if (!isXorC(c, boardGet(row+1, col))) return false;
				break;
			case 3:
				if (row+2 >= par.rowCount) return false;
				if (!isXorC(c, boardGet(row+0, col))) return false;
				if (!isXorC(c, boardGet(row+1, col))) return false;
				if (!isXorC(c, boardGet(row+2, col))) return false;
				break;
			}
			break;
		}
		
		return true;
	}
	
	public Config insert(byte c, Orientation o, int size, int row, int col) {
		
//		par.carMapPresent(c);
		
		Config n = new Config(par, board);
		
		boolean res = n.insertIni(c, o, size, row, col);
		assert res;
		
//		assert 
		
		return n;
	}
	
	public boolean insertIni(byte c, Orientation o, int size, int row, int col) {
		
		if (c == 'R') {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					
					assert par.winnableRows[row];
					
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					break;
				case 3:
					
					assert par.winnableRows[row];
					
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					boardSet(row, col+2, c);
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					
					assert par.winnableCols[col];
					
					boardSet(row+0, col, c);
					boardSet(row+1, col, c);
					break;
				case 3:
					
					assert par.winnableCols[col];
					
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
					
//					addToInterferenceRow(row);
					
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					break;
				case 3:
					
//					addToInterferenceRow(row);
					
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					boardSet(row, col+2, c);
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					
//					addToInterferenceCol(col);
					
					boardSet(row+0, col, c);
					boardSet(row+1, col, c);
					break;
				case 3:
					
//					addToInterferenceCol(col);
					
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
	
//	static byte[][] testBoard = new byte[][] {
//		{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', 'Y'},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//	};
//	static ParentConfig testPar;
//	static Config test;
//	static {
//		testPar = new ParentConfig(testBoard);
//		testPar.carMapPresent((byte)'R');
//		testPar.carMapPresent((byte)'A');
//		testPar.carMapPresent((byte)'B');
//		testPar.carMapPresent((byte)'C');
//		testPar.carMapPresent((byte)'D');
//		test = testPar.newConfig();
//		test = test.insert((byte)'R', Orientation.LEFTRIGHT, 2, 2, 2);
//		test = test.insert((byte)'A', Orientation.UPDOWN, 2, 4, 2);
//		test = test.insert((byte)'B', Orientation.UPDOWN, 2, 2, 1);
//		test = test.insert((byte)'C', Orientation.LEFTRIGHT, 2, 4, 0);
//		test = test.insert((byte)'D', Orientation.UPDOWN, 2, 0, 4);
//	}
	
	public void move(byte c, int size, Orientation oldO, int oldRow, int oldCol, Orientation newO, int newRow, int newCol, Config out) {
		
		assert this.board != out.board;
		
		copy(out);
		
		out.clearIni(c, oldO, size, oldRow, oldCol);
		
		out.insertIni(c, newO, size, newRow, newCol);
		
		out.alphaReduce();
		
	}
	
//	void addToInterferenceRow(int r) {
//		par.interferenceConeRows[r] = true;
//		
//		par.test[0] = r;
//		par.test[1] = -1;
//		
//		if (par.isJoint(par.test)) {
//			int[] other = par.otherJoint(par.test);
//			par.interferenceConeCols[other[1]] = true;
//			
//			par.across(other, par.test);
//			if (par.isJoint(par.test)) {
//				other = par.otherJoint(par.test);
//				par.interferenceConeRows[other[0]] = true;
//			}
//			
//		}
//		
//		par.test[0] = r;
//		par.test[1] = par.colCount;
//		
//		if (par.isJoint(par.test)) {
//			int[] other = par.otherJoint(par.test);
//			par.interferenceConeCols[other[1]] = true;
//			
//			par.across(other, par.test);
//			if (par.isJoint(par.test)) {
//				other = par.otherJoint(par.test);
//				par.interferenceConeRows[other[0]] = true;
//			}
//			
//		}
//		
//	}
	
//	void addToInterferenceCol(int c) {
//		par.interferenceConeCols[c] = true;
//		
//		par.test[0] = -1;
//		par.test[1] = c;
//		
//		if (par.isJoint(par.test)) {
//			int[] other = par.otherJoint(par.test);
//			par.interferenceConeRows[other[0]] = true;
//			
//			par.across(other, par.test);
//			if (par.isJoint(par.test)) {
//				other = par.otherJoint(par.test);
//				par.interferenceConeCols[other[1]] = true;
//			}
//			
//		}
//		
//		par.test[0] = par.rowCount;
//		par.test[1] = c;
//		
//		if (par.isJoint(par.test)) {
//			int[] other = par.otherJoint(par.test);
//			par.interferenceConeRows[other[0]] = true;
//			
//			par.across(other, par.test);
//			if (par.isJoint(par.test)) {
//				other = par.otherJoint(par.test);
//				par.interferenceConeCols[other[1]] = true;
//			}
//			
//		}
//	}
	
	boolean isXorC(byte c, byte b) {
		return b == 'X' || b == c;
	}
	
	public List<Config> possiblePreviousMoves() {
		
		par.moves.clear();
		
		boolean clearPathToExit = isClearPathToExit();
		
		carLoop:
		for (byte c : cars) {
			if (!par.carMapContains(c)) {
				continue;
			}
			/*
			 * only possible previous move of a winning config is to move R into winning position
			 */
			if (isWinning() && c != 'R') {
				continue;
			}
			
			/*
			 * if clearPathToExit,
			 * 		if red, only previous move was going away from exit
			 * 		if other, only previous move was interfering with winning path
			 */
			
			carMapGet(c);
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			switch (o) {
			case LEFTRIGHT:
				
				if (available(c, size, o, row, col-1)) {
					move(c, size, o, row, col, o, row, col-1, par.scratchLeft(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchLeft(c));
					if (res) {
						continue carLoop;
					}
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(c, par.otherJoint(row, col-1), par.scratchLeft(c));
					if (res) {
						res = lastStep(c, clearPathToExit, par.scratchLeft(c));
						if (res) {
							continue carLoop;
						}
					}
				}
				
				if (available(c, size, o, row, col+1)) {
					move(c, size, o, row, col, o, row, col+1, par.scratchRight(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchRight(c));
					if (res) {
						continue carLoop;
					}
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(c, par.otherJoint(row, col+size), par.scratchRight(c));
					if (res) {
						res = lastStep(c, clearPathToExit, par.scratchRight(c));
						if (res) {
							continue carLoop;
						}
					}
				}
				break;
			case UPDOWN:
				
				if (available(c, size, o, row-1, col)) {
					move(c, size, o, row, col, o, row-1, col, par.scratchUp(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchUp(c));
					if (res) {
						continue carLoop;
					}
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						res = lastStep(c, clearPathToExit, par.scratchUp(c));
						if (res) {
							continue carLoop;
						}
					}
				}
				
				if (available(c, size, o, row+1, col)) {
					move(c, size, o, row, col, o, row+1, col, par.scratchDown(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchDown(c));
					if (res) {
						continue carLoop;
					}
				} else if (par.isJoint(row+size, col)) {
					boolean res = tryJoint(c, par.otherJoint(row+size, col), par.scratchDown(c));
					if (res) {
						res = lastStep(c, clearPathToExit, par.scratchDown(c));
						if (res) {
							continue carLoop;
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
	boolean lastStep(byte c, boolean clearPathToExit, Config scratch) {
		if (!clearPathToExit) {
			if (!scratch.isClearPathToExit()) {
				par.moves.add(scratch);
			}
//			else {
//				/*
//				 * even though it is unintuitive to add a clearly wrong move (the move would be from clear path -> blocked path),
//				 * for completeness, we need to add all configs (this config might branch off, etc.)
//				 */
//				par.moves.add(scratch);
//			}
		} else {
			if (c == 'R') {
				if (scratch.numberMovesToWin() > this.numberMovesToWin()) {
					par.moves.add(scratch);
					return true;
				}
			} else {
				if (!scratch.isClearPathToExit()) {
					par.moves.add(scratch);
					return true;
				}
			}
		}
//		par.moves.add(scratch);
		
		return false;
	}
	
	public List<Config> possibleNextMoves() {
		
		par.moves.clear();
		
		if (isWinning()) {
			return par.moves;
		}
		
		if (isClearPathToExit()) {
			
			int currentMovesToWin = numberMovesToWin();
			Config best = null;
			
			carMapGet((byte)'R');
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			switch (o) {
			case LEFTRIGHT:
				
				if (available((byte)'R', size, o, row, col-1)) {
					move((byte)'R', size, o, row, col, o, row, col-1, par.scratchLeft((byte)'R'));
					if (par.scratchLeft((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchLeft((byte)'R');
					}
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row, col-1), par.scratchLeft((byte)'R'));
					if (res && par.scratchLeft((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchLeft((byte)'R');
					}
				}
				
				
				if (available((byte)'R', size, o, row, col+1)) {
					move((byte)'R', size, o, row, col, o, row, col+1, par.scratchRight((byte)'R'));
					if (par.scratchRight((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchRight((byte)'R');
					}
				} else if (par.isJoint(row, col+2)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row, col+2), par.scratchRight((byte)'R'));
					if (res && par.scratchRight((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchRight((byte)'R');
					}
				}
				
				break;
			case UPDOWN:
				
				if (available((byte)'R', size, o, row-1, col)) {
					move((byte)'R', size, o, row, col, o, row-1, col, par.scratchUp((byte)'R'));
					if (par.scratchUp((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchUp((byte)'R');
					}
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row-1, col), par.scratchUp((byte)'R'));
					if (res && par.scratchUp((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchUp((byte)'R');
					}
				}
				
				if (available((byte)'R', size, o, row+1, col)) {
					move((byte)'R', size, o, row, col, o, row+1, col, par.scratchDown((byte)'R'));
					if (par.scratchDown((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchDown((byte)'R');
					}
				} else if (par.isJoint(row+2, col)) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row+2, col), par.scratchDown((byte)'R'));
					if (res && par.scratchDown((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchDown((byte)'R');
					}
				}
				
				break;
			}
			
			assert best != null;
			
			par.moves.add(best);
			
			return par.moves;
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
			
			carMapGet(c);
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			switch (o) {
			case LEFTRIGHT:
				
				if (available(c, size, o, row, col-1)) {
					move(c, size, o, row, col, o, row, col-1, par.scratchLeft(c));
					par.moves.add(par.scratchLeft(c));
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(c, par.otherJoint(row, col-1), par.scratchLeft(c));
					if (res) {
						par.moves.add(par.scratchLeft(c));
					}
				}
				
				if (available(c, size, o, row, col+1)) {
					move(c, size, o, row, col, o, row, col+1, par.scratchRight(c));
					par.moves.add(par.scratchRight(c));
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(c, par.otherJoint(row, col+size), par.scratchRight(c));
					if (res) {
						par.moves.add(par.scratchRight(c));
					}
				}
				
				break;
			case UPDOWN:
				
				if (available(c, size, o, row-1, col)) {
					move(c, size, o, row, col, o, row-1, col, par.scratchUp(c));
					par.moves.add(par.scratchUp(c));
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						par.moves.add(par.scratchUp(c));
					}
				}
				
				if (available(c, size, o, row+1, col)) {
					move(c, size, o, row, col, o, row+1, col, par.scratchDown(c));
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
	
	public Config redCarWinningConfig() {
		int side = par.side(par.exit);
		
		Config n;
		
		switch (side) {
		case 0:
			n = insert((byte)'R', Orientation.UPDOWN, 2, par.exit[0]+1, par.exit[1]);
			return n;
		case 1:
			n = insert((byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]-2);
			return n;
		case 2:
			n = insert((byte)'R', Orientation.UPDOWN, 2, par.exit[0]-2, par.exit[1]);
			return n;
		case 3:
			n = insert((byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]+1);
			return n;
		}
		
		assert false;
		return null;
	}
	
	byte firstAvailableCar() {
		for (byte d : cars) {
			if (!iniContainsCar(d)) {
				return d;
			}
		}
		assert false;
		return -1;
	}
	
	public List<Config> possible2CarPlacements(List<Config> placements) {
		
		byte car = firstAvailableCar();
		int[] coor = firstAvailableCarCoor();
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-1; c++) {
				
//				if (!par.isInterfereRow(r)) {
//					continue;
//				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(car, 2, Orientation.LEFTRIGHT, r, c)) {
					Config n = insert(car, Orientation.LEFTRIGHT, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		/*
		 * up-down
		 */
		for (int r = 0; r < par.rowCount-1; r++) {
			for (int c = 0; c < par.colCount; c++) {
				
//				if (!par.isInterfereCol(c)) {
//					continue;
//				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(car, 2, Orientation.UPDOWN, r, c)) {
					Config n = insert(car, Orientation.UPDOWN, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public List<Config> possible3CarPlacements(List<Config> placements) {
		
		byte car = firstAvailableCar();
		int[] coor = firstAvailableCarCoor();
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-2; c++) {
				
//				if (!par.isInterfereRow(r)) {
//					continue;
//				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(car, 3, Orientation.LEFTRIGHT, r, c)) {
					Config n = insert(car, Orientation.LEFTRIGHT, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		/*
		 * up-down
		 */
		for (int r = 0; r < par.rowCount-2; r++) {
			for (int c = 0; c < par.colCount; c++) {
				
//				if (!par.isInterfereCol(c)) {
//					continue;
//				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(car, 3, Orientation.UPDOWN, r, c)) {
					Config n = insert(car, Orientation.UPDOWN, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
//	public int possible3CarPlacementsCount() {
//		
//		int[] coor = firstAvailable3CarCoor();
//		int firstAvailR = coor[0];
//		int firstAvailC = coor[1];
//		
//		int placements = 0;
//		
//		/*
//		 * left-right
//		 */
//		for (int r = 0; r < par.rowCount; r++) {
//			for (int c = 0; c < par.colCount-2; c++) {
//				
//				if (!par.isInterfereRow(r)) {
//					continue;
//				}
//				
//				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.LEFTRIGHT, 3, r, c)) {
//					placements++;
//				}
//			}
//		}
//		
//		/*
//		 * up-down
//		 */
//		for (int r = 0; r < par.rowCount-2; r++) {
//			for (int c = 0; c < par.colCount; c++) {
//				
//				if (!par.isInterfereCol(c)) {
//					continue;
//				}
//				
//				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.UPDOWN, 3, r, c)) {
//					placements++;
//				}
//			}
//		}
//		
//		return placements;
//	}
	
	int[] firstAvailableCarCoor() {
		
		par.test[0] = 0;
		par.test[1] = 0;
		for (byte b : cars) {
			if (b == 'R') {
				continue;
			}
			boolean res = carMapGet(b);
			
			if (!res) {
				break;
			}
//			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			//if (size == 2) {
			res = next(row, col, par.test);
			if (!res) {
				return null;
			}
			//}
		}
		
		return par.test;
	}
	
//	int[] firstAvailable3CarCoor() {
//		
//		par.test[0] = 0;
//		par.test[1] = 0;
//		for (byte b : cars) {
//			if (b == 'R') {
//				continue;
//			}
//			
//			boolean res = carMapGet(b);
//			
//			if (!res) {
//				break;
//			}
//			int size = par.scratchInfo.size;
//			int row = par.scratchInfo.row;
//			int col = par.scratchInfo.col;
//			
//			if (size == 3) {
//				res = next(row, col, par.test);
//				if (!res) {
//					return null;
//				}
//			}
//		}
//		
//		return par.test;
//	}
	
	boolean greaterThanEqual(int r, int c, int r1, int c1) {
		return (r > r1) || (r == r1 && c >= c1);
	}
	
	boolean lessThan(int r, int c, int r1, int c1) {
		return (r < r1) || (r == r1 && c < c1);
	}
	
	/**
	 * return if there was a next
	 */
	boolean next(int r, int c, int[] out) {
		
		if (c == par.colCount-1) {
			
			if (r == par.rowCount-1) {
				
				return false;
				
			} else {
				out[0] = r+1;
				out[1] = 1;
			}
			
		} else {
			out[0] = r;
			out[1] = c+1;
		}
		
		return true;
	}
	
	boolean tryJoint(byte c, int[] joint, Config out) {
		
		int mR = joint[0];
		int mC = joint[1];
		
		carMapGet(c);
		Orientation o = par.scratchInfo.o;
		int size = par.scratchInfo.size;
		int row = par.scratchInfo.row;
		int col = par.scratchInfo.col;
		
		int mSide = par.side(joint);
		switch (mSide) {
		case 0:
			switch (size) {
			case 2:
				if (available(c, size, Orientation.UPDOWN, mR+1, mC)) {
					move(c, size, o, row, col, Orientation.UPDOWN, mR+1, mC, out);
					return true;
				}
				break;
			case 3:
				if (available(c, size, Orientation.UPDOWN, mR+1, mC)) {
					move(c, size, o, row, col, Orientation.UPDOWN, mR+1, mC, out);
					return true;
				}
				break;
			}
			break;
		case 1:
			switch (size) {
			case 2:
				if (available(c, size, Orientation.LEFTRIGHT, mR, mC-2)) {
					move(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC-2, out);
					return true;
				}
				break;
			case 3:
				if (available(c, size, Orientation.LEFTRIGHT, mR, mC-3)) {
					move(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC-3, out);
					return true;
				}
				break;
			}
			break;
		case 2:
			switch (size) {
			case 2:
				if (available(c, size, Orientation.UPDOWN, mR-2, mC)) {
					move(c, size, o, row, col, Orientation.UPDOWN, mR-2, mC, out);
					return true;
				}
				break;
			case 3:
				if (available(c, size, Orientation.UPDOWN, mR-3, mC)) {
					move(c, size, o, row, col, Orientation.UPDOWN, mR-3, mC, out);
					return true;
				}
				break;
			}
			break;
		case 3:
			switch (size) {
			case 2:
				if (available(c, size, Orientation.LEFTRIGHT, mR, mC+1)) {
					move(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC+1, out);
					return true;
				}
				break;
			case 3:
				if (available(c, size, Orientation.LEFTRIGHT, mR, mC+1)) {
					move(c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC+1, out);
					return true;
				}
				break;
			}
			break;
		}
		
		return false;
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
	
	public boolean carMapGet(byte c) {
		
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
		
		boolean res = carMapGet((byte)'A');
		if (res) {
			Orientation ao = par.scratchInfo.o;
			int as = par.scratchInfo.size;
			int ar = par.scratchInfo.row;
			int ac = par.scratchInfo.col;
			
			res = carMapGet((byte)'B');
			if (res) {
				Orientation bo = par.scratchInfo.o;
				int bs = par.scratchInfo.size;
				int br = par.scratchInfo.row;
				int bc = par.scratchInfo.col;
				
				if (!(lessThan(ar, ac, br, bc))) {
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
				
				res = carMapGet((byte)'C');
				if (res) {
					Orientation co = par.scratchInfo.o;
					int cs = par.scratchInfo.size;
					int cr = par.scratchInfo.row;
					int cc = par.scratchInfo.col;
					
					if (!(lessThan(ar, ac, cr, cc))) {
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
					if (!(lessThan(br, bc, cr, cc))) {
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
					
					res = carMapGet((byte)'D');
					if (res) {
						Orientation doo = par.scratchInfo.o;
						int ds = par.scratchInfo.size;
						int dr = par.scratchInfo.row;
						int dc = par.scratchInfo.col;
						
						if (!(lessThan(ar, ac, dr, dc))) {
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
						if (!(lessThan(br, bc, dr, dc))) {
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
						if (!(lessThan(cr, cc, dr, dc))) {
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
						
						res = carMapGet((byte)'E');
						if (res) {
							Orientation eo = par.scratchInfo.o;
							int es = par.scratchInfo.size;
							int er = par.scratchInfo.row;
							int ec = par.scratchInfo.col;
							
							if (!(lessThan(ar, ac, er, ec))) {
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
							if (!(lessThan(br, bc, er, ec))) {
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
							if (!(lessThan(cr, cc, er, ec))) {
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
							if (!(lessThan(dr, dc, er, ec))) {
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
							
							res = carMapGet((byte)'F');
							if (res) {
								Orientation fo = par.scratchInfo.o;
								int fs = par.scratchInfo.size;
								int fr = par.scratchInfo.row;
								int fc = par.scratchInfo.col;
								
								if (!(lessThan(ar, ac, fr, fc))) {
									assert false;
								}
								if (!(lessThan(br, bc, fr, fc))) {
									assert false;
								}
								if (!(lessThan(cr, cc, fr, fc))) {
									assert false;
								}
								if (!(lessThan(dr, dc, fr, fc))) {
									assert false;
								}
								if (!(lessThan(er, ec, fr, fc))) {
									assert false;
								}
								
								res = carMapGet((byte)'G');
								if (res) {
									Orientation go = par.scratchInfo.o;
									int gs = par.scratchInfo.size;
									int gr = par.scratchInfo.row;
									int gc = par.scratchInfo.col;
									
									if (!(lessThan(ar, ac, gr, gc))) {
										assert false;
									}
									if (!(lessThan(br, bc, gr, gc))) {
										assert false;
									}
									if (!(lessThan(cr, cc, gr, gc))) {
										assert false;
									}
									if (!(lessThan(dr, dc, gr, gc))) {
										assert false;
									}
									if (!(lessThan(er, ec, gr, gc))) {
										assert false;
									}
									if (!(lessThan(fr, fc, gr, gc))) {
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
		
		clearIni(a, ao, as, ar, ac);
		clearIni(b, bo, bs, br, bc);
		
		insertIni(a, bo, bs, br, bc);
		insertIni(b, ao, as, ar, ac);
		
	}
	
}
