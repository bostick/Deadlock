package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
	
	public Config v;
	
	final ParentConfig par;
	public final byte[][] ini;
	
	static byte[] cars = new byte[] {'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	public Config(ParentConfig par, byte[][] old) {
		this.par = par;
		ini = copy(old);
	}
	
	public boolean equals(Object o) {
		return Arrays.deepEquals(ini, ((Config)o).ini);
	}
	
	public int hashCode() {
		return Arrays.deepHashCode(ini);
	}
	
	public boolean iniContainsCar(byte b) {
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				byte c = ini[i][j];
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
		return ini[coor[0]+1][coor[1]+1];
	}
	
	public static boolean isJorK(byte b) {
		return b == 'J' || b == 'K';
	}
	
	public boolean isJoint(int[] coor) {
		byte c = val(coor);
		return c == 'J' || c == 'K';
	}
	
	public byte charAcross(int[] coor) {
		if (coor[0] == -1) {
			return ini[par.rowCount+1][coor[1]+1];
		} else if (coor[0] == par.rowCount) {
			return ini[-1+1][coor[1]+1];
		} else if (coor[1] == -1) {
			return ini[coor[0]+1][par.colCount+1];
		} else {
			assert coor[1] == par.colCount;
			return ini[coor[0]+1][-1+1];
		}
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
		return val(par.exit) == 'R';
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
		byte old = ini[r+1][c+1];
		if (equals(r, c, par.exit)) {
			ini[r+1][c+1] = 'Y';
		} else {
			ini[r+1][c+1] = 'X';
		}
		return old;
	}
	
	boolean available(Orientation o, int size, int row, int col, int[] unavailable) {
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				if (!isX(ini[row+1][col+1])) return false;
				if (!isX(ini[row+1][col+2])) return false;
				if (equals(row, col, unavailable)) return false;
				if (equals(row, col+1, unavailable)) return false;
				break;
			case 3:
				if (!isX(ini[row+1][col+1])) return false;
				if (!isX(ini[row+1][col+2])) return false;
				if (!isX(ini[row+1][col+3])) return false;
				if (equals(row, col, unavailable)) return false;
				if (equals(row, col+1, unavailable)) return false;
				if (equals(row, col+2, unavailable)) return false;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				if (!isX(ini[row+1][col+1])) return false;
				if (!isX(ini[row+2][col+1])) return false;
				if (equals(row, col, unavailable)) return false;
				if (equals(row+1, col, unavailable)) return false;
				break;
			case 3:
				if (!isX(ini[row+1][col+1])) return false;
				if (!isX(ini[row+2][col+1])) return false;
				if (!isX(ini[row+3][col+1])) return false;
				if (equals(row, col, unavailable)) return false;
				if (equals(row+1, col, unavailable)) return false;
				if (equals(row+2, col, unavailable)) return false;
				break;
			}
			break;
		}
		
		return true;
	}
	
	boolean availableRed(Orientation o, int size, int row, int col) {
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				if (!isXorY(ini[row+1][col+1])) return false;
				if (!isXorY(ini[row+1][col+2])) return false;
				break;
			case 3:
				if (!isXorY(ini[row+1][col+1])) return false;
				if (!isXorY(ini[row+1][col+2])) return false;
				if (!isXorY(ini[row+1][col+3])) return false;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				if (!isXorY(ini[row+1][col+1])) return false;
				if (!isXorY(ini[row+2][col+1])) return false;
				break;
			case 3:
				if (!isXorY(ini[row+1][col+1])) return false;
				if (!isXorY(ini[row+2][col+1])) return false;
				if (!isXorY(ini[row+3][col+1])) return false;
				break;
			}
			break;
		}
		
		return true;
	}
	
	public Config insert(byte c, Orientation o, int size, int row, int col) {
		
		par.carMapPresent(c);
		
		Config n = new Config(par, ini);
		
		boolean res = n.insertIni(c, o, size, row, col);
		assert res;
		
		return n;
	}
	
	public boolean insertIni(byte c, Orientation o, int size, int row, int col) {
		
//		if (c == 'R') {
//			switch (o) {
//			case LEFTRIGHT:
//				switch (size) {
//				case 2:
//					if (!isXorY(ini[row+1][col+1])) return false;
//					if (!isXorY(ini[row+1][col+2])) return false;
//					break;
//				case 3:
//					if (!isXorY(ini[row+1][col+1])) return false;
//					if (!isXorY(ini[row+1][col+2])) return false;
//					if (!isXorY(ini[row+1][col+3])) return false;
//					break;
//				}
//				break;
//			case UPDOWN:
//				switch (size) {
//				case 2:
//					if (!isXorY(ini[row+1][col+1])) return false;
//					if (!isXorY(ini[row+2][col+1])) return false;
//					break;
//				case 3:
//					if (!isXorY(ini[row+1][col+1])) return false;
//					if (!isXorY(ini[row+2][col+1])) return false;
//					if (!isXorY(ini[row+3][col+1])) return false;
//					break;
//				}
//				break;
//			}
//		} else {
//			switch (o) {
//			case LEFTRIGHT:
//				switch (size) {
//				case 2:
//					if (!isX(ini[row+1][col+1])) return false;
//					if (!isX(ini[row+1][col+2])) return false;
//					break;
//				case 3:
//					if (!isX(ini[row+1][col+1])) return false;
//					if (!isX(ini[row+1][col+2])) return false;
//					if (!isX(ini[row+1][col+3])) return false;
//					break;
//				}
//				break;
//			case UPDOWN:
//				switch (size) {
//				case 2:
//					if (!isX(ini[row+1][col+1])) return false;
//					if (!isX(ini[row+2][col+1])) return false;
//					break;
//				case 3:
//					if (!isX(ini[row+1][col+1])) return false;
//					if (!isX(ini[row+2][col+1])) return false;
//					if (!isX(ini[row+3][col+1])) return false;
//					break;
//				}
//				break;
//			}
//		}
		
		if (c == 'R') {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					
					assert par.winnableRows[row];
					
					ini[row+1][col+1] = c;
					ini[row+1][col+2] = c;
					break;
				case 3:
					
					assert par.winnableRows[row];
					
					ini[row+1][col+1] = c;
					ini[row+1][col+2] = c;
					ini[row+1][col+3] = c;
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					
					assert par.winnableCols[col];
					
					ini[row+1][col+1] = c;
					ini[row+2][col+1] = c;
					break;
				case 3:
					
					assert par.winnableCols[col];
					
					ini[row+1][col+1] = c;
					ini[row+2][col+1] = c;
					ini[row+3][col+1] = c;
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
					
					ini[row+1][col+1] = c;
					ini[row+1][col+2] = c;
					break;
				case 3:
					
					addToInterferenceRow(row);
					
					ini[row+1][col+1] = c;
					ini[row+1][col+2] = c;
					ini[row+1][col+3] = c;
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					
					addToInterferenceCol(col);
					
					ini[row+1][col+1] = c;
					ini[row+2][col+1] = c;
					break;
				case 3:
					
					addToInterferenceCol(col);
					
					ini[row+1][col+1] = c;
					ini[row+2][col+1] = c;
					ini[row+3][col+1] = c;
					break;
				}
				break;
			}
		}
		
		return true;
	}
	
	public Config move(byte c, int size, Orientation oldO, int oldRow, int oldCol, Orientation newO, int newRow, int newCol) {
		Config n = new Config(par, ini);
		n.clearIni(c, oldO, size, oldRow, oldCol);
		n.insertIni(c, newO, size, newRow, newCol);
		return n;
	}
	
	void addToInterferenceRow(int r) {
		par.interferenceConeRows[r] = true;
		
		int[] test = new int[] {r, -1};
		
		if (isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeCols[other[1]] = true;
			
			par.across(other, test);
			if (isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeRows[other[0]] = true;
			}
			
		}
		
		test = new int[] {r, par.colCount};
		
		if (isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeCols[other[1]] = true;
			
			par.across(other, test);
			if (isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeRows[other[0]] = true;
			}
			
		}
		
	}
	
	void addToInterferenceCol(int c) {
		par.interferenceConeCols[c] = true;
		
		int[] test = new int[] {-1, c};
		
		if (isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeRows[other[0]] = true;
			
			par.across(other, test);
			if (isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeCols[other[1]] = true;
			}
			
		}
		
		test = new int[] {par.rowCount, c};
		
		if (isJoint(test)) {
			int[] other = par.otherJoint(test);
			par.interferenceConeRows[other[0]] = true;
			
			par.across(other, test);
			if (isJoint(test)) {
				other = par.otherJoint(test);
				par.interferenceConeCols[other[1]] = true;
			}
			
		}
	}
	
	boolean isX(byte c) {
		return c == 'X';
	}
	
	boolean isXorY(byte c) {
		return c == 'X' || c == 'Y';
	}
	
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
				if ((info.col-1 >= 0) && ini[info.row+1][info.col-1+1] == 'X') {
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
				if ((info.col+info.size <= par.colCount-1) && ini[info.row+1][info.col+info.size+1] == 'X') {
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
				if ((info.col-1 == -1) && isJorK(ini[info.row+1][info.col-1+1])) {
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
				if ((info.col+info.size == par.colCount) && isJorK(ini[info.row+1][info.col+info.size+1])) {
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
				if ((info.row-1 >= 0) && ini[info.row-1+1][info.col+1] == 'X') {
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
				if ((info.row+info.size <= par.rowCount-1) && ini[info.row+info.size+1][info.col+1] == 'X') {
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
				if ((info.row-1 == -1) && isJorK(ini[info.row-1+1][info.col+1])) {
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
				if ((info.row+info.size == par.rowCount) && isJorK(ini[info.row+info.size+1][info.col+1])) {
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
				if (ini[info.row+1][info.col-1+1] == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																				info.o, info.row, info.col-1);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col+2+1] == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																				info.o, info.row, info.col+1);

					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (isJorK(ini[info.row+1][info.col-1+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row, info.col-1));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (isJorK(ini[info.row+1][info.col+2+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row, info.col+2));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col-1+1] == 'Y') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																	info.o, info.row, info.col-1);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col+2+1] == 'Y') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																	info.o, info.row, info.col+1);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				break;
			case UPDOWN:
				if (ini[info.row-1+1][info.col+1] == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																	info.o, info.row-1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+2+1][info.col+1] == 'X') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																	info.o, info.row+1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (isJorK(ini[info.row-1+1][info.col+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row-1, info.col));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (isJorK(ini[info.row+2+1][info.col+1])) {
					Config newConfig = tryJoint((byte)'R', par.otherJoint(info.row+2, info.col));
					if (newConfig != null && newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row-1+1][info.col+1] == 'Y') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																	info.o, info.row-1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+2+1][info.col+1] == 'Y') {
					Config newConfig = move((byte)'R', info.size, info.o, info.row, info.col,
																				info.o, info.row+1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
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
				if ((info.col-1 >= 0) && ini[info.row+1][info.col-1+1] == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row, info.col-1);
					moves.add(newConfig);
				}
				if ((info.col+info.size <= par.colCount-1) && ini[info.row+1][info.col+info.size+1] == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row, info.col+1);
					moves.add(newConfig);
				}
				if ((info.col-1 == -1) && isJorK(ini[info.row+1][info.col-1+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row, info.col-1));
					if (newConfig != null) {
						moves.add(newConfig);
					}
				}
				if ((info.col+info.size == par.colCount) && isJorK(ini[info.row+1][info.col+info.size+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row, info.col+info.size));
					if (newConfig != null) {
						moves.add(newConfig);
					}
				}
				break;
			case UPDOWN:
				if ((info.row-1 >= 0) && ini[info.row-1+1][info.col+1] == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row-1, info.col);
					moves.add(newConfig);
				}
				if ((info.row+info.size <= par.rowCount-1) && ini[info.row+info.size+1][info.col+1] == 'X') {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															info.o, info.row+1, info.col);
					moves.add(newConfig);
				}
				if ((info.row-1 == -1) && isJorK(ini[info.row-1+1][info.col+1])) {
					Config newConfig = tryJoint(c, par.otherJoint(info.row-1, info.col));
					if (newConfig != null) {
						moves.add(newConfig);
					}
				}
				if ((info.row+info.size == par.rowCount) && isJorK(ini[info.row+info.size+1][info.col+1])) {
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
			if (availableRed(Orientation.UPDOWN, 2, par.exit[0], par.exit[1])) {
				n = insert((byte)'R', Orientation.UPDOWN, 2, par.exit[0], par.exit[1]);
				return n;
			}
			break;
		case 1:
			if (availableRed(Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]-1)) {
				n = insert((byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]-1);
				return n;
			}
			break;
		case 2:
			if (availableRed(Orientation.UPDOWN, 2, par.exit[0]-1, par.exit[1])) {
				n = insert((byte)'R', Orientation.UPDOWN, 2, par.exit[0]-1, par.exit[1]);
				return n;
			}
			break;
		case 3:
			if (availableRed(Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1])) {
				n = insert((byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]);
				return n;
			}
			break;
		}
		
		assert false;
		return null;
	}
	
	public List<Config> possible2CarPlacements(int[] unavailable) {
		
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
				
				if (available(Orientation.LEFTRIGHT, 2, r, c, unavailable)) {
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
				
				if (available(Orientation.UPDOWN, 2, r, c, unavailable)) {
					Config n = insert(car, Orientation.UPDOWN, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public List<Config> possible3CarPlacements(int[] unavailable) {
		
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
				
				if (available(Orientation.LEFTRIGHT, 3, r, c, unavailable)) {
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
				
				if (available(Orientation.UPDOWN, 3, r, c, unavailable)) {
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
				if ((ini[mR+1+1][mC+1] == 'X' || ini[mR+1+1][mC+1] == c) && (ini[mR+1+2][mC+1] == 'X' || ini[mR+1+2][mC+1] == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.UPDOWN, mR+1, mC);
					return newConfig;
				}
				break;
			case 3:
				if ((ini[mR+1+1][mC+1] == 'X' || ini[mR+1+1][mC+1] == c) && (ini[mR+1+2][mC+1] == 'X' || ini[mR+1+2][mC+1] == c) && (ini[mR+1+3][mC+1] == 'X' || ini[mR+1+3][mC+1] == c)) {
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
				if ((ini[mR+1][mC-2+1] == 'X' || ini[mR+1][mC-2+1] == c) && (ini[mR+1][mC-1+1] == 'X' || ini[mR+1][mC-1+1] == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.LEFTRIGHT, mR, mC-2);
					return newConfig;
				}
				break;
			case 3:
				if ((ini[mR+1][mC-3+1] == 'X' || ini[mR+1][mC-3+1] == c) && (ini[mR+1][mC-2+1] == 'X' || ini[mR+1][mC-2+1] == c) && (ini[mR+1][mC-1+1] == 'X' || ini[mR+1][mC-1+1] == c)) {
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
				if ((ini[mR-2+1][mC+1] == 'X' || ini[mR-2+1][mC+1] == c) && (ini[mR-1+1][mC+1] == 'X' || ini[mR-1+1][mC+1] == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.UPDOWN, mR-2, mC);
					return newConfig;
				}
				break;
			case 3:
				if ((ini[mR-3+1][mC+1] == 'X' || ini[mR-3+1][mC+1] == c) && (ini[mR-2+1][mC+1] == 'X' || ini[mR-2+1][mC+1] == c) && (ini[mR-1+1][mC+1] == 'X' || ini[mR-1+1][mC+1] == c)) {
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
				if ((ini[mR+1][mC+1+1] == 'X' || ini[mR+1][mC+1+1] == c) && (ini[mR+1][mC+2+1] == 'X' || ini[mR+1][mC+2+1] == c)) {
					Config newConfig = move(c, info.size, info.o, info.row, info.col,
															Orientation.LEFTRIGHT, mR, mC+1);
					return newConfig;
				}
				break;
			case 3:
				if ((ini[mR+1][mC+1+1] == 'X' || ini[mR+1][mC+1+1] == c) && (ini[mR+1][mC+2+1] == 'X' || ini[mR+1][mC+2+1] == c) && (ini[mR+1][mC+3+1] == 'X' || ini[mR+1][mC+3+1] == c)) {
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
		for (byte[] i : ini) {
			b.append(new String(i));
			b.append('\n');
		}
		return b.toString();
	};
	
	public CarInfo carMapGet(byte c) {
		CarInfo info = new CarInfo();
		
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				byte b = ini[i][j];
				if (b == c) {
					if ((i+1 < ini.length || (i+1-1 == par.exit[0] && j-1 == par.exit[1])) && ini[i+1][j] == c) {
						if ((i+2 < ini.length || (i+2-1 == par.exit[0] && j-1 == par.exit[1])) && ini[i+2][j] == c) {
							info.o = Orientation.UPDOWN;
							info.row = i-1;
							info.col = j-1;
							info.size = 3;
							return info;
						} else {
							info.o = Orientation.UPDOWN;
							info.row = i-1;
							info.col = j-1;
							info.size = 2;
							return info;
						}
					} else {
						assert ini[i][j+1] == c;
						if ((j+2 < ini[i].length || (i-1 == par.exit[0] && j+2-1 == par.exit[1])) && ini[i][j+2] == c) {
							info.o = Orientation.LEFTRIGHT;
							info.row = i-1;
							info.col = j-1;
							info.size = 3;
							return info;
						} else {
							info.o = Orientation.LEFTRIGHT;
							info.row = i-1;
							info.col = j-1;
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
