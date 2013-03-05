package solver;

import java.util.ArrayList;
import java.util.List;

public class Config {
	
	public Config vGen;
	public Config vSolve;
	
	final ParentConfig par;
	public final byte[][] board;
	
	static byte[] cars = new byte[] {'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	public Config(ParentConfig par, byte[][] old) {
		this.par = par;
		board = copy(old);
	}
	
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
		
		Cursor cursor = new Cursor(this);
		while (true) {
			if (cursor.val() == 'Y' || cursor.val() == 'X') {
				cursor.move();
			} else if (cursor.val() == 'R') {
				CarInfo red = carMapGet((byte)'R');
				if ((((cursor.side == 0 || cursor.side == 2) && red.o == Orientation.UPDOWN) || ((cursor.side == 1 || cursor.side == 3) && red.o == Orientation.LEFTRIGHT))) {
					break;
				} else {
					cursor.move();
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
		
		Cursor cursor = new Cursor(this); 
		int moves = 0;
		while (true) {
			if (cursor.val() == 'Y' || cursor.val() == 'X') {
				cursor.move();
				moves++;
			} else {
				if (cursor.val() == 'R') {
					CarInfo red = carMapGet((byte)'R');
					if ((((cursor.side == 0 || cursor.side == 2) && red.o == Orientation.UPDOWN) || ((cursor.side == 1 || cursor.side == 3) && red.o == Orientation.LEFTRIGHT))) {
						break;
					} else {
						cursor.move();
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
	
	public Config move(byte c, int size, Orientation oldO, int oldRow, int oldCol, Orientation newO, int newRow, int newCol) {
		Config n = new Config(par, board);
		n.clearIni(c, oldO, size, oldRow, oldCol);
		n.insertIni(c, newO, size, newRow, newCol);
		return n;
	}
	
	void addToInterferenceRow(int r) {
		par.interferenceConeRows[r] = true;
		
		int[] test = new int[] {r, -1};
		
		if (par.isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeCols[other[1]] = true;
			
			par.across(other, test);
			if (par.isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeRows[other[0]] = true;
			}
			
		}
		
		test = new int[] {r, par.colCount};
		
		if (par.isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeCols[other[1]] = true;
			
			par.across(other, test);
			if (par.isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeRows[other[0]] = true;
			}
			
		}
		
	}
	
	void addToInterferenceCol(int c) {
		par.interferenceConeCols[c] = true;
		
		int[] test = new int[] {-1, c};
		
		if (par.isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeRows[other[0]] = true;
			
			par.across(other, test);
			if (par.isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeCols[other[1]] = true;
			}
			
		}
		
		test = new int[] {par.rowCount, c};
		
		if (par.isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeRows[other[0]] = true;
			
			par.across(other, test);
			if (par.isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeCols[other[1]] = true;
			}
			
		}
	}
	
	boolean isX(byte c) {
		return c == 'X';
	}
	
//	boolean isXorY(byte c) {
//		return c == 'X' || c == 'Y';
//	}
	
	public List<Config> possiblePreviousMoves() {
		
		List<Config> moves = new ArrayList<Config>();
		
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
			
			CarInfo info = carMapGet(c);
			switch (info.o) {
			case LEFTRIGHT:
				if ((info.col-1 >= 0) && boardGet(info.row, info.col-1) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row, info.col-1);
					if (!clearPathToExit) {
						if (!newConfig.isClearPathToExit()) {
							moves.add(newConfig);
						}
					} else {
						if (c == 'R') {
							if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
								moves.add(newConfig);
								continue carLoop;
							}
						} else {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
								continue carLoop;
							}
						}
					}
				}
				if ((info.col+info.size <= par.colCount-1) && boardGet(info.row, info.col+info.size) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row, info.col+1);
					if (!clearPathToExit) {
						if (!newConfig.isClearPathToExit()) {
							moves.add(newConfig);
						}
					} else {
						if (c == 'R') {
							if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
								moves.add(newConfig);
								continue carLoop;
							}
						} else {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
								continue carLoop;
							}
						}
					}
				}
				if ((info.col-1 == -1) && ParentConfig.isJorK(par.ini[info.row+1][info.col-1+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row, info.col-1));
					if (newConfig != null) {
						if (!clearPathToExit) {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
							}
						} else {
							if (c == 'R') {
								if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
									moves.add(newConfig);
									continue carLoop;
								}
							} else {
								if (!newConfig.isClearPathToExit()) {
									moves.add(newConfig);
									continue carLoop;
								}
							}
						}
					}
				}
				if ((info.col+info.size == par.colCount) && ParentConfig.isJorK(par.ini[info.row+1][info.col+info.size+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row, info.col+info.size));
					if (newConfig != null) {
						if (!clearPathToExit) {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
							}
						} else {
							if (c == 'R') {
								if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
									moves.add(newConfig);
									continue carLoop;
								}
							} else {
								if (!newConfig.isClearPathToExit()) {
									moves.add(newConfig);
									continue carLoop;
								}
							}
						}
					}
				}
				break;
			case UPDOWN:
				if ((info.row-1 >= 0) && boardGet(info.row-1, info.col) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row-1, info.col);
					if (!clearPathToExit) {
						if (!newConfig.isClearPathToExit()) {
							moves.add(newConfig);
						}
					} else {
						if (c == 'R') {
							if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
								moves.add(newConfig);
								continue carLoop;
							}
						} else {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
								continue carLoop;
							}
						}
					}
				}
				if ((info.row+info.size <= par.rowCount-1) && boardGet(info.row+info.size, info.col) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row+1, info.col);
					if (!clearPathToExit) {
						if (!newConfig.isClearPathToExit()) {
							moves.add(newConfig);
						}
					} else {
						if (c == 'R') {
							if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
								moves.add(newConfig);
								continue carLoop;
							}
						} else {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
								continue carLoop;
							}
						}
					}
				}
				if ((info.row-1 == -1) && ParentConfig.isJorK(par.ini[info.row-1+1][info.col+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row-1, info.col));
					if (newConfig != null) {
						if (!clearPathToExit) {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
							}
						} else {
							if (c == 'R') {
								if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
									moves.add(newConfig);
									continue carLoop;
								}
							} else {
								if (!newConfig.isClearPathToExit()) {
									moves.add(newConfig);
									continue carLoop;
								}
							}
						}
					}
				}
				if ((info.row+info.size == par.rowCount) && ParentConfig.isJorK(par.ini[info.row+info.size+1][info.col+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row+info.size, info.col));
					if (newConfig != null) {
						if (!clearPathToExit) {
							if (!newConfig.isClearPathToExit()) {
								moves.add(newConfig);
							}
						} else {
							if (c == 'R') {
								if (newConfig.numberMovesToWin() > this.numberMovesToWin()) {
									moves.add(newConfig);
									continue carLoop;
								}
							} else {
								if (!newConfig.isClearPathToExit()) {
									moves.add(newConfig);
									continue carLoop;
								}
							}
						}
					}
				}
				break;
			}
		}
		
		return moves;
	}
	
	public List<Config> possibleNextMoves() {
		
		List<Config> moves = new ArrayList<Config>();
		
		if (isWinning()) {
			return moves;
		}
		
		if (isClearPathToExit()) {
			
			int currentMovesToWin = numberMovesToWin();
			Config best = null;
			
			CarInfo info = carMapGet((byte)'R');
			switch (info.o) {
			case LEFTRIGHT:
				if (boardGet(info.row, info.col-1) == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																				info.o, info.row, info.col-1);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (boardGet(info.row, info.col+2) == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																				info.o, info.row, info.col+1);

					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ParentConfig.isJorK(par.ini[info.row+1][info.col-1+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row, info.col-1));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ParentConfig.isJorK(par.ini[info.row+1][info.col+2+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row, info.col+2));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
//				if (ini[info.row+1][info.col-1+1] == 'Y') {
//					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
//																	info.o, info.row, info.col-1);
//					
//					if (newConfig.numberMovesToWin() < currentMovesToWin) {
//						best = newConfig;
//					}
//				}
//				if (ini[info.row+1][info.col+2+1] == 'Y') {
//					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
//																	info.o, info.row, info.col+1);
//					
//					if (newConfig.numberMovesToWin() < currentMovesToWin) {
//						best = newConfig;
//					}
//				}
				break;
			case UPDOWN:
				if (boardGet(info.row-1, info.col) == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																	info.o, info.row-1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (boardGet(info.row+2, info.col) == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																	info.o, info.row+1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ParentConfig.isJorK(par.ini[info.row-1+1][info.col+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row-1, info.col));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ParentConfig.isJorK(par.ini[info.row+2+1][info.col+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row+2, info.col));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
//				if (ini[info.row-1+1][info.col+1] == 'Y') {
//					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
//																	info.o, info.row-1, info.col);
//					
//					if (newConfig.numberMovesToWin() < currentMovesToWin) {
//						best = newConfig;
//					}
//				}
//				if (ini[info.row+2+1][info.col+1] == 'Y') {
//					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
//																				info.o, info.row+1, info.col);
//					
//					if (newConfig.numberMovesToWin() < currentMovesToWin) {
//						best = newConfig;
//					}
//				}
				break;
			}
			
			assert best != null;
			
			moves.add(best);
			
			return moves;
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
			
			CarInfo info = carMapGet(c);
			switch (info.o) {
			case LEFTRIGHT:
				if ((info.col-1 >= 0) && boardGet(info.row, info.col-1) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row, info.col-1);
					moves.add(newConfig);
				}
				if ((info.col+info.size <= par.colCount-1) && boardGet(info.row, info.col+info.size) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row, info.col+1);
					moves.add(newConfig);
				}
				if ((info.col-1 == -1) && ParentConfig.isJorK(par.ini[info.row+1][info.col-1+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row, info.col-1));
					if (newConfig != null) {
						moves.add(newConfig);
					}
				}
				if ((info.col+info.size == par.colCount) && ParentConfig.isJorK(par.ini[info.row+1][info.col+info.size+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row, info.col+info.size));
					if (newConfig != null) {
						moves.add(newConfig);
					}
				}
				break;
			case UPDOWN:
				if ((info.row-1 >= 0) && boardGet(info.row-1, info.col) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row-1, info.col);
					moves.add(newConfig);
				}
				if ((info.row+info.size <= par.rowCount-1) && boardGet(info.row+info.size, info.col) == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row+1, info.col);
					moves.add(newConfig);
				}
				if ((info.row-1 == -1) && ParentConfig.isJorK(par.ini[info.row-1+1][info.col+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row-1, info.col));
					if (newConfig != null) {
						moves.add(newConfig);
					}
				}
				if ((info.row+info.size == par.rowCount) && ParentConfig.isJorK(par.ini[info.row+info.size+1][info.col+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row+info.size, info.col));
					if (newConfig != null) {
						moves.add(newConfig);
					}
				}
				break;
			}
		}
		
		return moves;
	}
	
	/*
	 * try left-right first, then up-down
	 */
//	public List<Config> possibleRedCarPlacements() {
//		
//		List<Config> placements = new ArrayList<Config>();
//		
//		for (int r = 0; r < par.rowCount; r++) {
//			for (int c = 0; c < par.colCount-1; c++) {
//				
//				if (!par.winnableRows[r]) {
//					continue;
//				}
//				
//				if (available(Orientation.LEFTRIGHT, 2, r, c)) {
//					Config n = insert((byte)'R', Orientation.LEFTRIGHT, 2, r, c);
//					placements.add(n);
//				}
//				
//			}
//		}
//		
//		for (int r = 0; r < par.rowCount-1; r++) {
//			for (int c = 0; c < par.colCount; c++) {
//				
//				if (!par.winnableCols[c]) {
//					continue;
//				}
//				
//				if (available(Orientation.UPDOWN, 2, r, c)) {
//					Config n = insert((byte)'R', Orientation.UPDOWN, 2, r, c);
//					placements.add(n);
//				}
//			}
//		}
//		
//		return placements;
//	}
	
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
	
	public List<Config> possible2CarPlacements() {
		
		byte car = ' ';
		for (byte d : cars) {
			if (!iniContainsCar(d)) {
				car = d;
				break;
			}
		}
		assert car != ' ';
		
		List<Config> placements = new ArrayList<Config>();
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-1; c++) {
				
				if (!par.isInterfereRow(r)) {
					continue;
				}
				
				if (available(Orientation.LEFTRIGHT, 2, r, c)) {
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
				
				if (available(Orientation.UPDOWN, 2, r, c)) {
					Config n = insert(car, Orientation.UPDOWN, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public List<Config> possible3CarPlacements() {
		
		byte car = ' ';
		for (byte d : cars) {
			if (!iniContainsCar(d)) {
				car = d;
				break;
			}
		}
		assert car != ' ';
		
		List<Config> placements = new ArrayList<Config>();
		
		/*
		 * left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-2; c++) {
				
				if (!par.isInterfereRow(r)) {
					continue;
				}
				
				if (available(Orientation.LEFTRIGHT, 3, r, c)) {
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
				
				if (available(Orientation.UPDOWN, 3, r, c)) {
					Config n = insert(car, Orientation.UPDOWN, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	Config tryJoint(byte c, int[] joint) {
		
		int mR = joint[0];
		int mC = joint[1];
		
		CarInfo info = carMapGet(c);
		
		int mSide = par.side(joint);
		switch (mSide) {
		case 0:
			switch (info.size) {
			case 2:
				if ((boardGet(mR+1, mC) == 'X' || boardGet(mR+1, mC) == c) && (boardGet(mR+2, mC) == 'X' || boardGet(mR+2, mC) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.UPDOWN, mR+1, mC);
					return newConfig;
				}
				break;
			case 3:
				if ((boardGet(mR+1, mC) == 'X' || boardGet(mR+1, mC) == c) && (boardGet(mR+2, mC) == 'X' || boardGet(mR+2, mC) == c) && (boardGet(mR+3, mC) == 'X' || boardGet(mR+3, mC) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.UPDOWN, mR+1, mC);
					return newConfig;
				}
				break;
			}
			break;
		case 1:
			switch (info.size) {
			case 2:
				if ((boardGet(mR, mC-2) == 'X' || boardGet(mR, mC-2) == c) && (boardGet(mR, mC-1) == 'X' || boardGet(mR, mC-1) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.LEFTRIGHT, mR, mC-2);
					return newConfig;
				}
				break;
			case 3:
				if ((boardGet(mR, mC-3) == 'X' || boardGet(mR, mC-3) == c) && (boardGet(mR, mC-2) == 'X' || boardGet(mR, mC-2) == c) && (boardGet(mR, mC-1) == 'X' || boardGet(mR, mC-1) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.LEFTRIGHT, mR, mC-3);
					return newConfig;
				}
				break;
			}
			break;
		case 2:
			switch (info.size) {
			case 2:
				if ((boardGet(mR-2, mC) == 'X' || boardGet(mR-2, mC) == c) && (boardGet(mR-1, mC) == 'X' || boardGet(mR-1, mC) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.UPDOWN, mR-2, mC);
					return newConfig;
				}
				break;
			case 3:
				if ((boardGet(mR-3, mC) == 'X' || boardGet(mR-3, mC) == c) && (boardGet(mR-2, mC) == 'X' || boardGet(mR-2, mC) == c) && (boardGet(mR-1, mC) == 'X' || boardGet(mR-1, mC) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.UPDOWN, mR-3, mC);
					return newConfig;
				}
				break;
			}
			break;
		case 3:
			switch (info.size) {
			case 2:
				if ((boardGet(mR, mC+1) == 'X' || boardGet(mR, mC+1) == c) && (boardGet(mR, mC+2) == 'X' || boardGet(mR, mC+2) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.LEFTRIGHT, mR, mC+1);
					return newConfig;
				}
				break;
			case 3:
				if ((boardGet(mR, mC+1) == 'X' || boardGet(mR, mC+1) == c) && (boardGet(mR, mC+2) == 'X' || boardGet(mR, mC+2+1) == c) && (boardGet(mR, mC+3) == 'X' || boardGet(mR, mC+3) == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.LEFTRIGHT, mR, mC+1);
					return newConfig;
				}
				break;
			}
			break;
		}
		
		return null;
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
	
	public CarInfo carMapGet(byte c) {
		CarInfo info = new CarInfo();
		
		for (int i = 0; i < par.rowCount; i++) {
			for (int j = 0; j < par.colCount; j++) {
				byte b = boardGet(i, j);
				if (b == c) {
					if (i+1 < par.rowCount && boardGet(i+1, j) == c) {
						if (i+2 < par.colCount && boardGet(i+2, j) == c) {
							info.o = Orientation.UPDOWN;
							info.row = i;
							info.col = j;
							info.size = 3;
							return info;
						} else {
							info.o = Orientation.UPDOWN;
							info.row = i;
							info.col = j;
							info.size = 2;
							return info;
						}
					} else {
						assert boardGet(i, j+1) == c;
						if (j+2 < par.colCount && boardGet(i, j+2) == c) {
							info.o = Orientation.LEFTRIGHT;
							info.row = i;
							info.col = j;
							info.size = 3;
							return info;
						} else {
							info.o = Orientation.LEFTRIGHT;
							info.row = i;
							info.col = j;
							info.size = 2;
							return info;
						}
					}
				}
			}
		}
		
		assert false;
		return null;
	}
}
