package solver;

import java.util.List;

public class Config {
	
	public Config vGen;
	public Config vSolve;
	
	final ParentConfig par;
	public final byte[][] board;
	
	static byte[] cars = new byte[] {'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	
	public static long configCounter = 0;
	
	public Config(ParentConfig par, byte[][] old) {
		configCounter++;
		this.par = par;
		board = copy(old);
	}
	
//	public boolean equals(Object o) {
//		return alphaEquivalent(this, (Config)o);
//	}
	
	public boolean equals(Object o) {
		byte[][] other = ((Config)o).board;
		if (board.length != other.length || board[0].length != other[0].length) {
			return false;
		}
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] != other[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int hashCode() {
		int h = 17;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				h = 37 * h + board[i][j];
			}
		}
		return h;
		
//		CarInfo info = carMapGet((byte)'R');
//		h = 37 * h + info.hashCode();
//		
//		List<Byte> cars = new ArrayList<Byte>();
//		
//		for (int i = 0; i < par.rowCount; i++) {
//			for (int j = 0; j < par.colCount; j++) {
//				byte c = val(i, j);
//				switch (c) {
//				case 'A':
//				case 'B':
//				case 'C':
//				case 'D':
//				case 'E':
//				case 'F':
//				case 'G':
//					if (!cars.contains(c)) {
//						cars.add(c);
//					}
//					break;
//				}
//			}
//		}
//		
//		for (int i = 0; i < cars.size(); i++) {
//			byte ab = cars.get(i);
//			info = carMapGet((byte)ab);
//			h = 37 * h + info.hashCode();
//		}
//		
//		return h;
	}
	
	public void copy(Config out) {
		assert out.par == par;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				out.board[i][j] = board[i][j];
			}
		}
	}
	
	public Config copy() {
		Config n = par.newConfig();
		copy(n);
		return n;
	}
	
	byte boardGet(int r, int c) {
		
		int actualCol;
		int n;
		if (c % 2 == 0) {
			actualCol = c / 2;
			int old8bits = board[r][actualCol];
			n = ((old8bits & 0x0f));
		} else {
			actualCol = c / 2;
			int old8bits = board[r][actualCol];
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
		boardSet(board, r, c, b);
	}
	
	public static void boardSet(byte[][] board, int r, int c, byte b) {
		
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
		if (c % 2 == 0) {
			actualCol = c / 2;
			int old8bits = board[r][actualCol];
			byte n = (byte)((old8bits & 0xf0) | actualByte);
			board[r][actualCol] = n;
		} else {
			actualCol = c / 2;
			int old8bits = board[r][actualCol];
			byte n = (byte)((old8bits & 0x0f) | (actualByte<<4));
			board[r][actualCol] = n;
		}
	}
	
	public static byte[][] newBoard(int rows, int cols) {
		int actualCols;
		if (cols % 2 == 0) {
			actualCols = cols / 2;
		} else {
			actualCols = cols / 2 + 1;
		}
		return new byte[rows][actualCols];
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
	
	public static byte[][] copy(byte[][] old) {
		byte[][] newIni = new byte[old.length][old[0].length];
		int cols = old[0].length;
		for (int i = 0; i < newIni.length; i++) {
			System.arraycopy(old[i], 0, newIni[i], 0, cols);
		}
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
	
	boolean available(Orientation o, int size, int row, int col) {
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				if (!isX(boardGet(row, col+0))) return false;
				if (!isX(boardGet(row, col+1))) return false;
				break;
			case 3:
				if (!isX(boardGet(row, col+0))) return false;
				if (!isX(boardGet(row, col+1))) return false;
				if (!isX(boardGet(row, col+2))) return false;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				if (!isX(boardGet(row+0, col))) return false;
				if (!isX(boardGet(row+1, col))) return false;
				break;
			case 3:
				if (!isX(boardGet(row+0, col))) return false;
				if (!isX(boardGet(row+1, col))) return false;
				if (!isX(boardGet(row+2, col))) return false;
				break;
			}
			break;
		}
		
		return true;
	}
	
	public Config insert(byte c, Orientation o, int size, int row, int col) {
		
		par.carMapPresent(c);
		
		Config n = new Config(par, board);
		
		boolean res = n.insertIni(c, o, size, row, col);
		assert res;
		
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
					
					addToInterferenceRow(row);
					
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					break;
				case 3:
					
					addToInterferenceRow(row);
					
					boardSet(row, col+0, c);
					boardSet(row, col+1, c);
					boardSet(row, col+2, c);
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					
					addToInterferenceCol(col);
					
					boardSet(row+0, col, c);
					boardSet(row+1, col, c);
					break;
				case 3:
					
					addToInterferenceCol(col);
					
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
	
	public void move(byte c, int size, Orientation oldO, int oldRow, int oldCol, Orientation newO, int newRow, int newCol, Config out) {
		
		assert this.board != out.board;
		
		copy(out);
		
		out.clearIni(c, oldO, size, oldRow, oldCol);
		
		out.insertIni(c, newO, size, newRow, newCol);
	}
	
	void addToInterferenceRow(int r) {
		par.interferenceConeRows[r] = true;
		
		par.test[0] = r;
		par.test[1] = -1;
		
		if (par.isJoint(par.test)) {
			int[] other = par.otherJoint(par.test);
			par.interferenceConeCols[other[1]] = true;
			
			par.across(other, par.test);
			if (par.isJoint(par.test)) {
				other = par.otherJoint(par.test);
				par.interferenceConeRows[other[0]] = true;
			}
			
		}
		
		par.test[0] = r;
		par.test[1] = par.colCount;
		
		if (par.isJoint(par.test)) {
			int[] other = par.otherJoint(par.test);
			par.interferenceConeCols[other[1]] = true;
			
			par.across(other, par.test);
			if (par.isJoint(par.test)) {
				other = par.otherJoint(par.test);
				par.interferenceConeRows[other[0]] = true;
			}
			
		}
		
	}
	
	void addToInterferenceCol(int c) {
		par.interferenceConeCols[c] = true;
		
		par.test[0] = -1;
		par.test[1] = c;
		
		if (par.isJoint(par.test)) {
			int[] other = par.otherJoint(par.test);
			par.interferenceConeRows[other[0]] = true;
			
			par.across(other, par.test);
			if (par.isJoint(par.test)) {
				other = par.otherJoint(par.test);
				par.interferenceConeCols[other[1]] = true;
			}
			
		}
		
		par.test[0] = par.rowCount;
		par.test[1] = c;
		
		if (par.isJoint(par.test)) {
			int[] other = par.otherJoint(par.test);
			par.interferenceConeRows[other[0]] = true;
			
			par.across(other, par.test);
			if (par.isJoint(par.test)) {
				other = par.otherJoint(par.test);
				par.interferenceConeCols[other[1]] = true;
			}
			
		}
	}
	
	boolean isX(byte c) {
		return c == 'X';
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
				
				/*
				 * info.col-1
				 */
				if ((col-1 >= 0) && boardGet(row, col-1) == 'X') {
					move(c, size, o, row, col,
										o, row, col-1,
										par.scratchLeft(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchLeft(c));
					if (res) {
						continue carLoop;
					}
				} else if ((col-1 == -1) && ParentConfig.isJorK(par.ini[row+1][col-1+1])) {
					boolean res = tryJoint(c, par.otherJoint(row, col-1), par.scratchLeft(c));
					if (res) {
						res = lastStep(c, clearPathToExit, par.scratchLeft(c));
						if (res) {
							continue carLoop;
						}
					}
				}
				
				/*
				 * info.col+1
				 */
				if ((col+size <= par.colCount-1) && boardGet(row, col+size) == 'X') {
					move(c, size, o, row, col,
															o, row, col+1,
															par.scratchRight(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchRight(c));
					if (res) {
						continue carLoop;
					}
				} else if ((col+size == par.colCount) && ParentConfig.isJorK(par.ini[row+1][col+size+1])) {
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
				
				/*
				 * info.row-1
				 */
				if ((row-1 >= 0) && boardGet(row-1, col) == 'X') {
					move(c, size, o, row, col,
															o, row-1, col,
															par.scratchUp(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchUp(c));
					if (res) {
						continue carLoop;
					}
				} else if ((row-1 == -1) && ParentConfig.isJorK(par.ini[row-1+1][col+1])) {
					boolean res = tryJoint(c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						res = lastStep(c, clearPathToExit, par.scratchUp(c));
						if (res) {
							continue carLoop;
						}
					}
				}
				
				/*
				 * info.row+1
				 */
				if ((row+size <= par.rowCount-1) && boardGet(row+size, col) == 'X') {
					move(c, size, o, row, col,
															o, row+1, col,
															par.scratchDown(c));
					boolean res = lastStep(c, clearPathToExit, par.scratchDown(c));
					if (res) {
						continue carLoop;
					}
				} else if ((row+size == par.rowCount) && ParentConfig.isJorK(par.ini[row+size+1][col+1])) {
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
				
				if (col-1 >= 0 && boardGet(row, col-1) == 'X') {
					move((byte)'R', size, o, row, col,
												o, row, col-1,
												par.scratchLeft((byte)'R'));
					if (par.scratchLeft((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchLeft((byte)'R');
					}
				} else if (ParentConfig.isJorK(par.ini[row+1][col-1+1])) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row, col-1), par.scratchLeft((byte)'R'));
					if (res && par.scratchLeft((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchLeft((byte)'R');
					}
				}
				
				
				if (col+2 < par.colCount && boardGet(row, col+2) == 'X') {
					move((byte)'R', size, o, row, col,
												o, row, col+1,
												par.scratchRight((byte)'R'));
					if (par.scratchRight((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchRight((byte)'R');
					}
				} else if (ParentConfig.isJorK(par.ini[row+1][col+2+1])) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row, col+2), par.scratchRight((byte)'R'));
					if (res && par.scratchRight((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchRight((byte)'R');
					}
				}
				
				break;
			case UPDOWN:
				
				if (row-1 >= 0 && boardGet(row-1, col) == 'X') {
					move((byte)'R', size, o, row, col,
																	o, row-1, col,
																	par.scratchUp((byte)'R'));
					if (par.scratchUp((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchUp((byte)'R');
					}
				} else if (ParentConfig.isJorK(par.ini[row-1+1][col+1])) {
					boolean res = tryJoint((byte)'R', par.otherJoint(row-1, col), par.scratchUp((byte)'R'));
					if (res && par.scratchUp((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchUp((byte)'R');
					}
				}
				
				if (row+2 < par.rowCount && boardGet(row+2, col) == 'X') {
					move((byte)'R', size, o, row, col,
																	o, row+1, col,
																	par.scratchDown((byte)'R'));
					if (par.scratchDown((byte)'R').numberMovesToWin() < currentMovesToWin) {
						best = par.scratchDown((byte)'R');
					}
				} else if (ParentConfig.isJorK(par.ini[row+2+1][col+1])) {
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
				
				if ((col-1 >= 0) && boardGet(row, col-1) == 'X') {
					move(c, size, o, row, col,
															o, row, col-1,
															par.scratchLeft(c));
					par.moves.add(par.scratchLeft(c));
				} else if ((col-1 == -1) && ParentConfig.isJorK(par.ini[row+1][col-1+1])) {
					boolean res = tryJoint(c, par.otherJoint(row, col-1), par.scratchLeft(c));
					if (res) {
						par.moves.add(par.scratchLeft(c));
					}
				}
				
				if ((col+size <= par.colCount-1) && boardGet(row, col+size) == 'X') {
					move(c, size, o, row, col,
															o, row, col+1,
															par.scratchRight(c));
					par.moves.add(par.scratchRight(c));
				} else if ((col+size == par.colCount) && ParentConfig.isJorK(par.ini[row+1][col+size+1])) {
					boolean res = tryJoint(c, par.otherJoint(row, col+size), par.scratchRight(c));
					if (res) {
						par.moves.add(par.scratchRight(c));
					}
				}
				
				break;
			case UPDOWN:
				
				if ((row-1 >= 0) && boardGet(row-1, col) == 'X') {
					move(c, size, o, row, col,
															o, row-1, col,
															par.scratchUp(c));
					par.moves.add(par.scratchUp(c));
				} else if ((row-1 == -1) && ParentConfig.isJorK(par.ini[row-1+1][col+1])) {
					boolean res = tryJoint(c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						par.moves.add(par.scratchUp(c));
					}
				}
				
				if ((row+size <= par.rowCount-1) && boardGet(row+size, col) == 'X') {
					move(c, size, o, row, col,
															o, row+1, col,
															par.scratchDown(c));
					par.moves.add(par.scratchDown(c));
				} else if ((row+size == par.rowCount) && ParentConfig.isJorK(par.ini[row+size+1][col+1])) {
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
		int[] coor = firstAvailable2CarCoor();
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-1; c++) {
				
				if (!par.isInterfereRow(r)) {
					continue;
				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.LEFTRIGHT, 2, r, c)) {
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
				
				if (!par.isInterfereCol(c)) {
					continue;
				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.UPDOWN, 2, r, c)) {
					Config n = insert(car, Orientation.UPDOWN, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public List<Config> possible3CarPlacements(List<Config> placements) {
		
		byte car = firstAvailableCar();
		int[] coor = firstAvailable3CarCoor();
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-2; c++) {
				
				if (!par.isInterfereRow(r)) {
					continue;
				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.LEFTRIGHT, 3, r, c)) {
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
				
				if (!par.isInterfereCol(c)) {
					continue;
				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.UPDOWN, 3, r, c)) {
					Config n = insert(car, Orientation.UPDOWN, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public int possible3CarPlacementsCount() {
		
		int[] coor = firstAvailable3CarCoor();
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		int placements = 0;
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-2; c++) {
				
				if (!par.isInterfereRow(r)) {
					continue;
				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.LEFTRIGHT, 3, r, c)) {
					placements++;
				}
			}
		}
		
		/*
		 * up-down
		 */
		for (int r = 0; r < par.rowCount-2; r++) {
			for (int c = 0; c < par.colCount; c++) {
				
				if (!par.isInterfereCol(c)) {
					continue;
				}
				
				if (greaterThanEqual(r, c, firstAvailR, firstAvailC) && available(Orientation.UPDOWN, 3, r, c)) {
					placements++;
				}
			}
		}
		
		return placements;
	}
	
	int[] firstAvailable2CarCoor() {
		
		par.test[0] = 0;
		par.test[1] = 0;
		for (byte b : cars) {
			boolean res = carMapGet(b);
			
			if (!res) {
				break;
			}
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			if (size == 2) {
				res = next(row, col, par.test);
				if (!res) {
					return null;
				}
			}
		}
		
		return par.test;
	}
	
	int[] firstAvailable3CarCoor() {
		
		par.test[0] = 0;
		par.test[1] = 0;
		for (byte b : cars) {
			boolean res = carMapGet(b);
			
			if (!res) {
				break;
			}
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			if (size == 3) {
				res = next(row, col, par.test);
				if (!res) {
					return null;
				}
			}
		}
		
		return par.test;
	}
	
	boolean greaterThanEqual(int r, int c, int r1, int c1) {
		
		return (r > r1) || (r == r1 && c >= c1);
		
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
				if ((boardGet(mR+1, mC) == 'X' || boardGet(mR+1, mC) == c) && (boardGet(mR+2, mC) == 'X' || boardGet(mR+2, mC) == c)) {
					move(c, size, o, row, col,
															Orientation.UPDOWN, mR+1, mC,
															out);
					return true;
				}
				break;
			case 3:
				if ((boardGet(mR+1, mC) == 'X' || boardGet(mR+1, mC) == c) && (boardGet(mR+2, mC) == 'X' || boardGet(mR+2, mC) == c) && (boardGet(mR+3, mC) == 'X' || boardGet(mR+3, mC) == c)) {
					move(c, size, o, row, col,
															Orientation.UPDOWN, mR+1, mC,
															out);
					return true;
				}
				break;
			}
			break;
		case 1:
			switch (size) {
			case 2:
				if ((boardGet(mR, mC-2) == 'X' || boardGet(mR, mC-2) == c) && (boardGet(mR, mC-1) == 'X' || boardGet(mR, mC-1) == c)) {
					move(c, size, o, row, col,
															Orientation.LEFTRIGHT, mR, mC-2,
															out);
					return true;
				}
				break;
			case 3:
				if ((boardGet(mR, mC-3) == 'X' || boardGet(mR, mC-3) == c) && (boardGet(mR, mC-2) == 'X' || boardGet(mR, mC-2) == c) && (boardGet(mR, mC-1) == 'X' || boardGet(mR, mC-1) == c)) {
					move(c, size, o, row, col,
															Orientation.LEFTRIGHT, mR, mC-3,
															out);
					return true;
				}
				break;
			}
			break;
		case 2:
			switch (size) {
			case 2:
				if ((boardGet(mR-2, mC) == 'X' || boardGet(mR-2, mC) == c) && (boardGet(mR-1, mC) == 'X' || boardGet(mR-1, mC) == c)) {
					move(c, size, o, row, col,
															Orientation.UPDOWN, mR-2, mC,
															out);
					return true;
				}
				break;
			case 3:
				if ((boardGet(mR-3, mC) == 'X' || boardGet(mR-3, mC) == c) && (boardGet(mR-2, mC) == 'X' || boardGet(mR-2, mC) == c) && (boardGet(mR-1, mC) == 'X' || boardGet(mR-1, mC) == c)) {
					move(c, size, o, row, col,
															Orientation.UPDOWN, mR-3, mC,
															out);
					return true;
				}
				break;
			}
			break;
		case 3:
			switch (size) {
			case 2:
				if ((boardGet(mR, mC+1) == 'X' || boardGet(mR, mC+1) == c) && (boardGet(mR, mC+2) == 'X' || boardGet(mR, mC+2) == c)) {
					move(c, size, o, row, col,
															Orientation.LEFTRIGHT, mR, mC+1,
															out);
					return true;
				}
				break;
			case 3:
				if ((boardGet(mR, mC+1) == 'X' || boardGet(mR, mC+1) == c) && (boardGet(mR, mC+2) == 'X' || boardGet(mR, mC+2+1) == c) && (boardGet(mR, mC+3) == 'X' || boardGet(mR, mC+3) == c)) {
					move(c, size, o, row, col,
															Orientation.LEFTRIGHT, mR, mC+1,
															out);
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
	
//	public static boolean alphaEquivalent(Config a, Config b) {
//		
//		assert a.par == b.par;
//		
//		if (a.actualEquals(b)) {
//			return true;
//		}
//		
//		CarInfo info = a.carMapGet((byte)'R');
//		Orientation ao = info.o;
//		int as = info.size;
//		int ac = info.col;
//		int ar = info.row;
//		info = b.carMapGet((byte)'R');
//		Orientation bo = info.o;
//		int bs = info.size;
//		int bc = info.col;
//		int br = info.row;
//		
//		if (!(ao == bo && as == bs && ar == br && ac == bc)) {
//			return false;
//		}
//		
//		List<Byte> aCars = new ArrayList<Byte>();
//		List<Byte> bCars = new ArrayList<Byte>();
//		
//		for (int i = 0; i < a.par.rowCount; i++) {
//			for (int j = 0; j < a.par.colCount; j++) {
//				byte c = a.val(i, j);
//				switch (c) {
//				case 'A':
//				case 'B':
//				case 'C':
//				case 'D':
//				case 'E':
//				case 'F':
//				case 'G':
//					if (!aCars.contains(c)) {
//						aCars.add(c);
//					}
//					break;
//				}
//			}
//		}
//		
//		for (int i = 0; i < b.par.rowCount; i++) {
//			for (int j = 0; j < b.par.colCount; j++) {
//				byte c = b.val(i, j);
//				switch (c) {
//				case 'A':
//				case 'B':
//				case 'C':
//				case 'D':
//				case 'E':
//				case 'F':
//				case 'G':
//					if (!bCars.contains(c)) {
//						bCars.add(c);
//					}
//					break;
//				}
//			}
//		}
//		
//		if (aCars.size() != bCars.size()) {
//			return false;
//		}
//		
//		for (int i = 0; i < aCars.size(); i++) {
//			byte ab = aCars.get(i);
//			byte bb = bCars.get(i);
//			info = a.carMapGet((byte)ab);
//			ao = info.o;
//			as = info.size;
//			ac = info.col;
//			ar = info.row;
//			info = b.carMapGet((byte)bb);
//			bo = info.o;
//			bs = info.size;
//			bc = info.col;
//			br = info.row;
//			if (!(ao == bo && as == bs && ar == br && ac == bc)) {
//				return false;
//			}
//		}
//		
//		return true;
//	}
	
}
