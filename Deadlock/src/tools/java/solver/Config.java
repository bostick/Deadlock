package solver;

import java.util.List;

public class Config {
	
	public static ParentConfig par;
	
//	static byte[] cars = new byte[] {'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	static final byte LEFTRIGHT = 0;
	static final byte UPDOWN = 1;
	
	private Config() {
		
	}
	
	public static byte[][] newConfig(byte[][] old) {
		byte[][] board = new byte[old.length][old[0].length];
		for (int i = 0; i < old.length; i++) {
			System.arraycopy(old[i], 0, board[i], 0, old[i].length);
		}
		return board;
	}
	
	public static void copyTo(byte[][] in, byte[][] out) {
		for (int i = 0; i < in.length; i++) {
			System.arraycopy(in[i], 0, out[i], 0, in[i].length);
		}
	}
	
	public static byte[][] clone(byte[][] in) {
		byte[][] n = new byte[in.length][in[0].length];
		copyTo(in, n);
		return n;
	}
	
	public static String toString(byte[][] in) {
		
		StringBuilder b = new StringBuilder();
		b.append(new String(par.ini[0]));
		b.append('\n');
		for (int i = 0; i < par.rowCount; i++) {
			b.append((char)par.ini[i+1][0]);
			for (int j = 0; j < par.colCount; j++) {
				byte bb = boardGet(in, i, j);
				b.append((char)bb);
			}
			b.append((char)par.ini[i+1][par.colCount+1]);
			b.append('\n');
		}
		b.append(new String(par.ini[par.ini.length-1]));
		b.append('\n');
		return b.toString();
	}
	
	public static String toString(List<byte[][]> boards) {
		
		StringBuilder b = new StringBuilder();
		for (@SuppressWarnings("unused") byte[][] board : boards) {
			b.append(new String(par.ini[0]));
			b.append("   ");
		}
		b.append("\n");
		for (int i = 0; i < par.rowCount; i++) {
			for (byte[][] board : boards) {
				b.append((char)par.ini[i+1][0]);
				for (int j = 0; j < par.colCount; j++) {
					byte bb = boardGet(board, i, j);
					b.append((char)bb);
				}
				b.append((char)par.ini[i+1][par.colCount+1]);
				b.append("   ");
			}
			b.append("\n");
		}
		for (@SuppressWarnings("unused") byte[][] board : boards) {
			b.append(new String(par.ini[par.ini.length-1]));
			b.append("   ");
		}
		b.append("\n\n");
		return b.toString();
	}
	
	public static byte[][] newBoard(int rows, int cols) {
		return new byte[rows][cols];
	}
	
	static byte boardGet(byte[][] board, int[] coor) {
		return boardGet(board, coor[0], coor[1]);
	}
	
	static byte boardGet(byte[][] board, int r, int c) {
		return board[r][c];
		
	}
	
	static void boardSet(byte[][] board, int r, int c, byte b) {
		boardSet(board, r, c, b, par.colCount);
	}
	
	public static void boardSet(byte[][] board, int r, int c, byte b, int colCount) {
		board[r][c] = b;		
	}
	
	public static boolean boardContainsCar(byte[][] board, byte b) {
		for (int i = 0; i < par.rowCount; i++) {
			for (int j = 0; j < par.colCount; j++) {
				byte c = boardGet(board, i, j);
				if (c == b) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void clearCar(byte[][] board, byte c, byte o, int size, int row, int col) {
		
		byte old;
		if (o == LEFTRIGHT) {
			switch (size) {
			case 2:
				old = clearCoor(board, row, col+0);
				assert old == c;
				old = clearCoor(board, row, col+1);
				assert old == c;
				break;
			case 3:
				old = clearCoor(board, row, col+0);
				assert old == c;
				old = clearCoor(board, row, col+1);
				assert old == c;
				old = clearCoor(board, row, col+2);
				assert old == c;
				break;
			}
		} else {
			switch (size) {
			case 2:
				old = clearCoor(board, row+0, col);
				assert old == c;
				old = clearCoor(board, row+1, col);
				assert old == c;
				break;
			case 3:
				old = clearCoor(board, row+0, col);
				assert old == c;
				old = clearCoor(board, row+1, col);
				assert old == c;
				old = clearCoor(board, row+2, col);
				assert old == c;
				break;
			}
		}
		
	}
	
	public static boolean isWinning(byte[][] board) {
		
		loadScratchInfo(board, (byte)'R');
		byte o = par.scratchInfo.o;
		int r = par.scratchInfo.row;
		int c = par.scratchInfo.col;
		
		int side = par.side(par.exit);
		switch (side) {
		case 0:
			return o == UPDOWN && Statics.equals(r-2, c, par.exit) && boardGet(board, r-1, c) == ' ';
		case 1:
			return o == LEFTRIGHT && Statics.equals(r, c+3, par.exit) && boardGet(board, r, c+2) == ' ';
		case 2:
			return o == UPDOWN && Statics.equals(r+3, c, par.exit) && boardGet(board, r+2, c) == ' ';
		case 3:
			return o == LEFTRIGHT && Statics.equals(r, c-2, par.exit) && boardGet(board, r, c-1) == ' ';
		}
		
		assert false;
		return false;
	}
	
	static boolean hasClearPathToExit(byte[][] board) {
		
		par.cursor.reset(board);
		while (true) {
			if (Statics.isSpaceorY(par.cursor.val())) {
				par.cursor.move();
			} else if (par.cursor.val() == 'R') {
				loadScratchInfo(board, (byte)'R');
				byte o = par.scratchInfo.o;
				
				if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == LEFTRIGHT))) {
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
	
	/**
	 * returns old value
	 */
	private static byte clearCoor(byte[][] board, int r, int c) {
		byte old = boardGet(board, r, c);
		boardSet(board, r, c, (byte)' ');
		return old;
	}
	
	static boolean availableForCar(byte[][] board, byte c, int size, byte o, int row, int col) {
		
		if (row < 0 || row >= par.rowCount) return false;
		if (col < 0 || col >= par.colCount) return false;
		
		switch (o) {
		case LEFTRIGHT:
			switch (size) {
			case 2:
				if (col+1 >= par.colCount) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row, col+0))) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row, col+1))) return false;
				break;
			case 3:
				if (col+2 >= par.colCount) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row, col+0))) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row, col+1))) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row, col+2))) return false;
				break;
			}
			break;
		case UPDOWN:
			switch (size) {
			case 2:
				if (row+1 >= par.rowCount) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row+0, col))) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row+1, col))) return false;
				break;
			case 3:
				if (row+2 >= par.rowCount) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row+0, col))) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row+1, col))) return false;
				if (!Statics.isSpaceorC(c, boardGet(board, row+2, col))) return false;
				break;
			}
			break;
		}
		
		return true;
	}
	
	public static boolean insertCar(byte[][] board, byte c, byte o, int size, int row, int col) {
		
		if (c == 'R') {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					boardSet(board, row, col+0, c);
					boardSet(board, row, col+1, c);
					break;
				case 3:
					boardSet(board, row, col+0, c);
					boardSet(board, row, col+1, c);
					boardSet(board, row, col+2, c);
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					boardSet(board, row+0, col, c);
					boardSet(board, row+1, col, c);
					break;
				case 3:
					boardSet(board, row+0, col, c);
					boardSet(board, row+1, col, c);
					boardSet(board, row+2, col, c);
					break;
				}
				break;
			}
			
		} else {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					boardSet(board, row, col+0, c);
					boardSet(board, row, col+1, c);
					break;
				case 3:
					boardSet(board, row, col+0, c);
					boardSet(board, row, col+1, c);
					boardSet(board, row, col+2, c);
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					boardSet(board, row+0, col, c);
					boardSet(board, row+1, col, c);
					break;
				case 3:
					boardSet(board, row+0, col, c);
					boardSet(board, row+1, col, c);
					boardSet(board, row+2, col, c);
					break;
				}
				break;
			}
		}
		
		return true;
	}
	
	public static void moveCar(byte[][] board, byte c, int size, byte oldO, int oldRow, int oldCol, byte newO, int newRow, int newCol, boolean alphaReduce, byte[][] out) {
		
		assert board != out;
		
		copyTo(board, out);
		
		clearCar(out, c, oldO, size, oldRow, oldCol);
		
		insertCar(out, c, newO, size, newRow, newCol);
		
		if (alphaReduce) {
			alphaReduce(out);
		}
		
	}
	
	public static List<byte[][]> possiblePreviousMoves(byte[][] board) {
		
		par.generatingMoves.clear();
		
		if (hasClearPathToExit(board)) {
			
			for (int i = 0; i < par.actualCars.size(); i++) {
				byte c = par.actualCars.get(i);
				boolean first = (i == 0);
				boolean last = (i == par.actualCars.size()-1);
				
				if (c == 'R') {
					
					/*
					 * for red car, move away from exit
					 */
					loadScratchInfo(board, c);
					byte o = par.scratchInfo.o;
					int size = par.scratchInfo.size;
					int row = par.scratchInfo.row;
					int col = par.scratchInfo.col;
					
					byte[][] scratch;
					
					switch (o) {
					case LEFTRIGHT:
						
						scratch = par.scratchLeft.get(c);
						if (availableForCar(board, c, size, o, row, col-1)) {
							
							moveCar(board, c, size, o, row, col, o, row, col-1, false, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row, col-1)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col-1), false, scratch);
							if (res) {
								if (furtherFromExit(c, scratch, board)) {
									par.generatingMoves.add(scratch);
									continue;
								}
							}
						}
						
						scratch = par.scratchRight.get(c);
						if (availableForCar(board, c, size, o, row, col+1)) {
							moveCar(board, c, size, o, row, col, o, row, col+1, false, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row, col+size)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col+size), false, scratch);
							if (res) {
								if (furtherFromExit(c, scratch, board)) {
									par.generatingMoves.add(scratch);
									continue;
								}
							}
						}
						break;
					case UPDOWN:
						
						scratch = par.scratchUp.get(c);
						if (availableForCar(board, c, size, o, row-1, col)) {
							moveCar(board, c, size, o, row, col, o, row-1, col, false, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row-1, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row-1, col), false, scratch);
							if (res) {
								if (furtherFromExit(c, scratch, board)) {
									par.generatingMoves.add(scratch);
									continue;
								}
							}
						}
						
						scratch = par.scratchDown.get(c);
						if (availableForCar(board, c, size, o, row+1, col)) {
							moveCar(board, c, size, o, row, col, o, row+1, col, false, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row+size, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row+size, col), false, scratch);
							if (res) {
								if (furtherFromExit(c, scratch, board)) {
									par.generatingMoves.add(scratch);
									continue;
								}
							}
						}
						break;
					}
					
				} else {
					
					/*
					 * for other cars, unblock path from red car to exit
					 */
					loadScratchInfo(board, c);
					byte o = par.scratchInfo.o;
					int size = par.scratchInfo.size;
					int row = par.scratchInfo.row;
					int col = par.scratchInfo.col;
					
					byte[][] scratch;
					
					switch (o) {
					case LEFTRIGHT:
						
						scratch = par.scratchLeft.get(c);
						if (availableForCar(board, c, size, o, row, col-1)) {
							
							moveCar(board, c, size, o, row, col, o, row, col-1, false, scratch);
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row, col-1)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col-1), true, scratch);
							if (res) {
								if (nowBlockingPath(scratch, board)) {
									par.generatingMoves.add(scratch);
								}
							}
						}
						
						scratch = par.scratchRight.get(c);
						if (availableForCar(board, c, size, o, row, col+1)) {
							moveCar(board, c, size, o, row, col, o, row, col+1, false, scratch);
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row, col+size)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col+size), true, scratch);
							if (res) {
								if (nowBlockingPath(scratch, board)) {
									par.generatingMoves.add(scratch);
								}
							}
						}
						break;
					case UPDOWN:
						
						scratch = par.scratchUp.get(c);
						if (availableForCar(board, c, size, o, row-1, col)) {
							moveCar(board, c, size, o, row, col, o, row-1, col, (!first), scratch);
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row-1, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row-1, col), true, scratch);
							if (res) {
								if (nowBlockingPath(scratch, board)) {
									par.generatingMoves.add(scratch);
								}
							}
						}
						
						scratch = par.scratchDown.get(c);
						if (availableForCar(board, c, size, o, row+1, col)) {
							moveCar(board, c, size, o, row, col, o, row+1, col, (!last), scratch);
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row+size, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row+size, col), true, scratch);
							if (res) {
								if (nowBlockingPath(scratch, board)) {
									par.generatingMoves.add(scratch);
								}
							}
						}
						break;
					}
					
				}
				
			}
			
			return par.generatingMoves;
		}
		
		for (int i = 0; i < par.actualCars.size(); i++) {
			byte c = par.actualCars.get(i);
			boolean first = (i == 0);
			boolean last = (i == par.actualCars.size()-1);
			
			loadScratchInfo(board, c);
			byte o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			byte[][] scratch;
			
			switch (o) {
			case LEFTRIGHT:
				
				scratch = par.scratchLeft.get(c);
				if (availableForCar(board, c, size, o, row, col-1)) {
					moveCar(board, c, size, o, row, col, o, row, col-1, false, scratch);
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col-1), true, scratch);
					if (res) {
						if (!hasClearPathToExit(scratch)) {
							par.generatingMoves.add(scratch);
						}
					}
				}
				
				scratch = par.scratchRight.get(c);
				if (availableForCar(board, c, size, o, row, col+1)) {
					moveCar(board, c, size, o, row, col, o, row, col+1, false, scratch);
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col+size), true, scratch);
					if (res) {
						if (!hasClearPathToExit(scratch)) {
							par.generatingMoves.add(scratch);
						}
					}
				}
				break;
			case UPDOWN:
				
				scratch = par.scratchUp.get(c);
				if (availableForCar(board, c, size, o, row-1, col)) {
					moveCar(board, c, size, o, row, col, o, row-1, col, (!first), scratch);
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row-1, col), true, scratch);
					if (res) {
						if (!hasClearPathToExit(scratch)) {
							par.generatingMoves.add(scratch);
						}
					}
				}
				
				scratch = par.scratchDown.get(c);
				if (availableForCar(board, c, size, o, row+1, col)) {
					moveCar(board, c, size, o, row, col, o, row+1, col, (!last), scratch);
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row+size, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row+size, col), true, scratch);
					if (res) {
						if (!hasClearPathToExit(scratch)) {
							par.generatingMoves.add(scratch);
						}
					}
				}
				break;
			}
		}
		
		return par.generatingMoves;
	}
	
	public static List<byte[][]> possibleNextMoves(byte[][] board) {
		
		par.solvingMoves.clear();
		
		if (isWinning(board)) {
			return par.solvingMoves;
		}
		
		if (hasClearPathToExit(board)) {
			
			loadScratchInfo(board, (byte)'R');
			byte o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			byte[][] scratch;
			
			switch (o) {
			case LEFTRIGHT:
				
				scratch = par.scratchLeft.get((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row, col-1)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row, col-1, false, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row, col-1), false, scratch);
					if (res) {
						if (closerToExit((byte)'R', scratch, board)) {
							par.solvingMoves.add(scratch);
							return par.solvingMoves;
						}
					}
				}
				
				scratch = par.scratchRight.get((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row, col+1)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row, col+1, false, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row, col+2)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row, col+2), false, scratch);
					if (res) {
						if (closerToExit((byte)'R', scratch, board)) {
							par.solvingMoves.add(scratch);
							return par.solvingMoves;
						}
					}
				}
				
				break;
			case UPDOWN:
				
				scratch = par.scratchUp.get((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row-1, col)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row-1, col, false, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row-1, col), false, scratch);
					if (res) {
						if (closerToExit((byte)'R', scratch, board)) {
							par.solvingMoves.add(scratch);
							return par.solvingMoves;
						}
					}
				}
				
				scratch = par.scratchDown.get((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row+1, col)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row+1, col, false, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row+2, col)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row+2, col), false, scratch);
					if (res) {
						if (closerToExit((byte)'R', scratch, board)) {
							par.solvingMoves.add(scratch);
							return par.solvingMoves;
						}
					}
				}
				
				break;
			}
			
			assert false;
		}
		
		for (int i = 0; i < par.actualCars.size(); i++) {
			byte c = par.actualCars.get(i);
			boolean first = (i == 0);
			boolean last = (i == par.actualCars.size()-1);
			
			loadScratchInfo(board, c);
			byte o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			byte[][] scratch;
			
			switch (o) {
			case LEFTRIGHT:
				
				scratch = par.scratchLeft.get(c);
				if (availableForCar(board, c, size, o, row, col-1)) {
					moveCar(board, c, size, o, row, col, o, row, col-1, false, scratch);
					par.solvingMoves.add(scratch);
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col-1), true, scratch);
					if (res) {
						par.solvingMoves.add(scratch);
					}
				}
				
				scratch = par.scratchRight.get(c);
				if (availableForCar(board, c, size, o, row, col+1)) {
					moveCar(board, c, size, o, row, col, o, row, col+1, false, scratch);
					par.solvingMoves.add(scratch);
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col+size), true, scratch);
					if (res) {
						par.solvingMoves.add(scratch);
					}
				}
				
				break;
			case UPDOWN:
				
				scratch = par.scratchUp.get(c);
				if (availableForCar(board, c, size, o, row-1, col)) {
					moveCar(board, c, size, o, row, col, o, row-1, col, (!first), scratch);
					par.solvingMoves.add(scratch);
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row-1, col), true, scratch);
					if (res) {
						par.solvingMoves.add(scratch);
					}
				}
				
				scratch = par.scratchDown.get(c);
				if (availableForCar(board, c, size, o, row+1, col)) {
					moveCar(board, c, size, o, row, col, o, row+1, col, (!last), scratch);
					par.solvingMoves.add(scratch);
				} else if (par.isJoint(row+size, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row+size, col), true, scratch);
					if (res) {
						par.solvingMoves.add(scratch);
					}
				}
				
				break;
			}
		}
		
		return par.solvingMoves;
	}
	
	static boolean tryJoint(byte[][] board, byte c, int[] joint, boolean alphaReduce, byte[][] out) {
		
		int mR = joint[0];
		int mC = joint[1];
		
		loadScratchInfo(board, c);
		byte o = par.scratchInfo.o;
		int size = par.scratchInfo.size;
		int row = par.scratchInfo.row;
		int col = par.scratchInfo.col;
		
		int mSide = par.side(joint);
		switch (mSide) {
		case 0:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, UPDOWN, mR+1, mC)) {
					moveCar(board, c, size, o, row, col, UPDOWN, mR+1, mC, alphaReduce, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, UPDOWN, mR+1, mC)) {
					moveCar(board, c, size, o, row, col, UPDOWN, mR+1, mC, alphaReduce, out);
					return true;
				}
				break;
			}
			break;
		case 1:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, LEFTRIGHT, mR, mC-2)) {
					moveCar(board, c, size, o, row, col, LEFTRIGHT, mR, mC-2, alphaReduce, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, LEFTRIGHT, mR, mC-3)) {
					moveCar(board, c, size, o, row, col, LEFTRIGHT, mR, mC-3, alphaReduce, out);
					return true;
				}
				break;
			}
			break;
		case 2:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, UPDOWN, mR-2, mC)) {
					moveCar(board, c, size, o, row, col, UPDOWN, mR-2, mC, alphaReduce, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, UPDOWN, mR-3, mC)) {
					moveCar(board, c, size, o, row, col, UPDOWN, mR-3, mC, alphaReduce, out);
					return true;
				}
				break;
			}
			break;
		case 3:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, LEFTRIGHT, mR, mC+1)) {
					moveCar(board, c, size, o, row, col, LEFTRIGHT, mR, mC+1, alphaReduce, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, LEFTRIGHT, mR, mC+1)) {
					moveCar(board, c, size, o, row, col, LEFTRIGHT, mR, mC+1, alphaReduce, out);
					return true;
				}
				break;
			}
			break;
		}
		
		return false;
	}
	
	static boolean furtherFromExit(byte c, byte[][] next, byte[][] cur) {
		assert c == 'R';
		
		par.cursor.reset(next);
		int nextMoves = 0;
		while (true) {
			if (Statics.isSpaceorY(par.cursor.val())) {
				par.cursor.move();
				nextMoves++;
			} else {
				if (par.cursor.val() == 'R') {
					loadScratchInfo(next, (byte)'R');
					byte o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == LEFTRIGHT))) {
						break;
					} else {
						par.cursor.move();
						nextMoves++;
					}
				} else {					
					assert false;
				}
			}
		}
		
		par.cursor.reset(cur); 
		int curMoves = 0;
		while (true) {
			if (Statics.isSpaceorY(par.cursor.val())) {
				par.cursor.move();
				curMoves++;
			} else {
				if (par.cursor.val() == 'R') {
					loadScratchInfo(cur, (byte)'R');
					byte o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == LEFTRIGHT))) {
						break;
					} else {
						par.cursor.move();
						curMoves++;
					}
				} else {
					assert false;
				}
			}
		}
		
		return nextMoves > curMoves;
	}
	
	static boolean closerToExit(byte c, byte[][] next, byte[][] cur) {
		return furtherFromExit(c, cur, next);
	}
	
	static boolean nowBlockingPath(byte[][] next, byte[][] cur) {
		
		par.cursor.reset(next);
		boolean nextBlocking = false;
		while (true) {
			if (Statics.isSpaceorY(par.cursor.val())) {
				par.cursor.move();
			} else {
				if (par.cursor.val() == 'R') {
					loadScratchInfo(next, (byte)'R');
					byte o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == LEFTRIGHT))) {
						break;
					} else {
						par.cursor.move();
					}
				} else {
					nextBlocking = true;
					break;
				}
			}
		}
		
		if (!nextBlocking) {
			return false;
		}
		
		par.cursor.reset(cur);
		boolean curBlocking = false;
		while (true) {
			if (Statics.isSpaceorY(par.cursor.val())) {
				par.cursor.move();
			} else {
				if (par.cursor.val() == 'R') {
					loadScratchInfo(cur, (byte)'R');
					byte o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == LEFTRIGHT))) {
						break;
					} else {
						par.cursor.move();
					}
				} else {
					curBlocking = true;
					break;
				}
			}
		}
		
		return !curBlocking && nextBlocking;
	}
	
	public static byte[][] winningConfig(byte[][] board) {
		int side = par.side(par.exit);
		
		byte[][] n = newConfig(board);
		
		switch (side) {
		case 0:
			insertCar(n, (byte)'R', UPDOWN, 2, par.exit[0]+2, par.exit[1]);
			return n;
		case 1:
			insertCar(n, (byte)'R', LEFTRIGHT, 2, par.exit[0], par.exit[1]-3);
			return n;
		case 2:
			insertCar(n, (byte)'R', UPDOWN, 2, par.exit[0]-3, par.exit[1]);
			return n;
		case 3:
			insertCar(n, (byte)'R', LEFTRIGHT, 2, par.exit[0], par.exit[1]+2);
			return n;
		}
		
		assert false;
		return null;
	}
	
	static byte firstAvailableCar(byte[][] board) {
		for (byte d : par.actualCars) {
			if (!boardContainsCar(board, d)) {
				return d;
			}
		}
		assert false;
		return -1;
	}
	
	public static List<byte[][]> possible2CarPlacements(byte[][] board, List<byte[][]> placements, boolean lastToBeAdded) {
		
		byte car = firstAvailableCar(board);
		int[] coor = firstAvailableCarCoor(board);
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * 2 left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-1; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(board, car, 2, LEFTRIGHT, r, c) &&
						!completesBlock(board, 2, LEFTRIGHT, r, c) &&
						leavesRedCarGap(board, 2, LEFTRIGHT, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 2, LEFTRIGHT, r, c))) {
					
					byte[][] n = newConfig(board);
					insertCar(n, car, LEFTRIGHT, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		/*
		 * 2 up-down
		 */
		for (int r = 0; r < par.rowCount-1; r++) {
			for (int c = 0; c < par.colCount; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(board, car, 2, UPDOWN, r, c) &&
						!completesBlock(board, 2, UPDOWN, r, c) &&
						leavesRedCarGap(board, 2, UPDOWN, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 2, UPDOWN, r, c))) {
					
					byte[][] n = newConfig(board);
					insertCar(n, car, UPDOWN, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public static List<byte[][]> possible3CarPlacements(byte[][] board, List<byte[][]> placements, boolean lastToBeAdded) {
		
		byte car = firstAvailableCar(board);
		int[] coor = firstAvailableCarCoor(board);
		int firstAvailR = coor[0];
		int firstAvailC = coor[1];
		
		/*
		 * 3 left-right
		 */
		for (int r = 0; r < par.rowCount; r++) {
			for (int c = 0; c < par.colCount-2; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(board, car, 3, LEFTRIGHT, r, c) &&
						!completesBlock(board, 3, LEFTRIGHT, r, c) &&
						leavesRedCarGap(board, 3, LEFTRIGHT, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 3, LEFTRIGHT, r, c))) {
					
					byte[][] n = newConfig(board);
					insertCar(n, car, LEFTRIGHT, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		/*
		 * 3 up-down
		 */
		for (int r = 0; r < par.rowCount-2; r++) {
			for (int c = 0; c < par.colCount; c++) {
				
				if (Statics.greaterThanEqual(r, c, firstAvailR, firstAvailC) &&
						availableForCar(board, car, 3, UPDOWN, r, c) &&
						!completesBlock(board, 3, UPDOWN, r, c) &&
						leavesRedCarGap(board, 3, UPDOWN, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 3, UPDOWN, r, c))) {
					
					byte[][] n = newConfig(board);
					insertCar(n, car, UPDOWN, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	static int[] firstAvailableCarCoor(byte[][] board) {
		
		par.test[0] = 0;
		par.test[1] = 0;
		for (byte b : par.actualCars) {
			if (b == 'R') {
				continue;
			}
			boolean res = loadScratchInfo(board, b);
			
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
	
	/**
	 * returns true if inserting specified car completes an immoveable block from one side to the next
	 */
	static boolean completesBlock(byte[][] board, int size, byte o, int r, int c) {
		
		switch (o) {
		case LEFTRIGHT: {
			
			if (par.isJoint(r, -1) || par.isJoint(r, par.colCount)) {
				return false;
			}
			
			byte currentCar = ' ';
			for (int i = 0; i < par.colCount; i++) {
				byte b = boardGet(board, r, i);
				if (b == ' ') {
					if (i >= c && i < c+size) {
						continue;
					} else {
						return false;
					}
				} else if (b == currentCar) {
					continue;
				} else {
					loadScratchInfo(board, b);
					byte so = par.scratchInfo.o;
					if (so != LEFTRIGHT) {
						return false;
					}
					currentCar = b;
				}
			}
			return true;
		}
			
		case UPDOWN: {
			
			if (par.isJoint(-1, c) || par.isJoint(par.rowCount, c)) {
				return false;
			}
			
			byte currentCar = ' ';
			for (int i = 0; i < par.rowCount; i++) {
				byte b = boardGet(board, i, c);
				if (b == ' ') {
					if (i >= r && i < r+size) {
						continue;
					} else {
						return false;
					}
				} else if (b == currentCar) {
					continue;
				} else {
					loadScratchInfo(board, b);
					byte so = par.scratchInfo.o;
					if (so != UPDOWN) {
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
	static boolean leavesRedCarGap(byte[][] board, int size, byte o, int r, int c) {
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
			return gapR != r || (gapC < c || gapC >= c+size);
		case UPDOWN:
			return gapC != c || (gapR < r || gapR >= r+size);
		}
		
		assert false;
		return false;
	}
	
	static boolean someCarIntersectsWinnable(byte[][] board, int size, byte o, int r, int c) {
		
		for (byte d : par.actualCars) {
			boolean res = loadScratchInfo(board, d);
			if (res) {
				byte so = par.scratchInfo.o;
				int ss = par.scratchInfo.size;
				int sr = par.scratchInfo.row;
				int sc = par.scratchInfo.col;
				if (intersectsWithWinnable(board, ss, so, sr, sc)) {
					return true;
				}
			} else {
				break;
			}
		}
		return intersectsWithWinnable(board, size, o, r, c);
	}
	
	static boolean intersectsWithWinnable(byte[][] board, int size, byte o, int r, int c) {
		
		switch (o) {
		case LEFTRIGHT:
			return par.winnableCols;
		case UPDOWN:
			return par.winnableRows;
		}
		
		assert false;
		return false;
	}
	
	public static boolean loadScratchInfo(byte[][] board, byte c) {
		
		for (int i = 0; i < par.rowCount; i++) {
			for (int j = 0; j < par.colCount; j++) {
				byte b = boardGet(board, i, j);
				if (b == c) {
					if (i+1 < par.rowCount && boardGet(board, i+1, j) == c) {
						if (i+2 < par.colCount && boardGet(board, i+2, j) == c) {
							par.scratchInfo.o = UPDOWN;
							par.scratchInfo.size = 3;
							par.scratchInfo.row = i;
							par.scratchInfo.col = j;
							return true;
						} else {
							par.scratchInfo.o = UPDOWN;
							par.scratchInfo.size = 2;
							par.scratchInfo.row = i;
							par.scratchInfo.col = j;
							return true;
						}
					} else {
						assert boardGet(board, i, j+1) == c;
						if (j+2 < par.colCount && boardGet(board, i, j+2) == c) {
							par.scratchInfo.o = LEFTRIGHT;
							par.scratchInfo.size = 3;
							par.scratchInfo.row = i;
							par.scratchInfo.col = j;
							return true;
						} else {
							par.scratchInfo.o = LEFTRIGHT;
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
	
	static void alphaReduce(byte[][] board) {
		
		byte so;
		int ss;
		int sr;
		int sc;
		
		boolean res = loadScratchInfo(board, (byte)'A');
		if (res) {
			byte ao = par.scratchInfo.o;
			int as = par.scratchInfo.size;
			int ar = par.scratchInfo.row;
			int ac = par.scratchInfo.col;
			
			res = loadScratchInfo(board, (byte)'B');
			if (res) {
				byte bo = par.scratchInfo.o;
				int bs = par.scratchInfo.size;
				int br = par.scratchInfo.row;
				int bc = par.scratchInfo.col;
				
				if (!(Statics.lessThan(ar, ac, br, bc))) {
					swap(board, (byte)'A', ao, as, ar, ac, (byte)'B', bo, bs, br, bc);
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
				
				res = loadScratchInfo(board, (byte)'C');
				if (res) {
					byte co = par.scratchInfo.o;
					int cs = par.scratchInfo.size;
					int cr = par.scratchInfo.row;
					int cc = par.scratchInfo.col;
					
					if (!(Statics.lessThan(ar, ac, cr, cc))) {
						swap(board, (byte)'A', ao, as, ar, ac, (byte)'C', co, cs, cr, cc);
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
						swap(board, (byte)'B', bo, bs, br, bc, (byte)'C', co, cs, cr, cc);
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
					
					res = loadScratchInfo(board, (byte)'D');
					if (res) {
						byte doo = par.scratchInfo.o;
						int ds = par.scratchInfo.size;
						int dr = par.scratchInfo.row;
						int dc = par.scratchInfo.col;
						
						if (!(Statics.lessThan(ar, ac, dr, dc))) {
							swap(board, (byte)'A', ao, as, ar, ac, (byte)'D', doo, ds, dr, dc);
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
							swap(board, (byte)'B', bo, bs, br, bc, (byte)'D', doo, ds, dr, dc);
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
							swap(board, (byte)'C', co, cs, cr, cc, (byte)'D', doo, ds, dr, dc);
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
						
						res = loadScratchInfo(board, (byte)'E');
						if (res) {
							byte eo = par.scratchInfo.o;
							int es = par.scratchInfo.size;
							int er = par.scratchInfo.row;
							int ec = par.scratchInfo.col;
							
							if (!(Statics.lessThan(ar, ac, er, ec))) {
								swap(board, (byte)'A', ao, as, ar, ac, (byte)'E', eo, es, er, ec);
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
								swap(board, (byte)'B', bo, bs, br, bc, (byte)'E', eo, es, er, ec);
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
								swap(board, (byte)'C', co, cs, cr, cc, (byte)'E', eo, es, er, ec);
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
								swap(board, (byte)'D', doo, ds, dr, dc, (byte)'E', eo, es, er, ec);
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
							
							res = loadScratchInfo(board, (byte)'F');
							if (res) {
								byte fo = par.scratchInfo.o;
								int fs = par.scratchInfo.size;
								int fr = par.scratchInfo.row;
								int fc = par.scratchInfo.col;
								
								if (!(Statics.lessThan(ar, ac, fr, fc))) {
									swap(board, (byte)'A', ao, as, ar, ac, (byte)'F', fo, fs, fr, fc);
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
									swap(board, (byte)'B', bo, bs, br, bc, (byte)'F', fo, fs, fr, fc);
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
									swap(board, (byte)'C', co, cs, cr, cc, (byte)'F', fo, fs, fr, fc);
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
									swap(board, (byte)'D', doo, ds, dr, dc, (byte)'F', fo, fs, fr, fc);
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
									swap(board, (byte)'E', eo, es, er, ec, (byte)'F', fo, fs, fr, fc);
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
								
								res = loadScratchInfo(board, (byte)'G');
								if (res) {
									byte go = par.scratchInfo.o;
									int gs = par.scratchInfo.size;
									int gr = par.scratchInfo.row;
									int gc = par.scratchInfo.col;
									
									if (!(Statics.lessThan(ar, ac, gr, gc))) {
										swap(board, (byte)'A', ao, as, ar, ac, (byte)'G', go, gs, gr, gc);
										so = ao;
										ss = as;
										sr = ar;
										sc = ac;
										ao = go;
										as = gs;
										ar = gr;
										ac = gc;
										go = so;
										gs = ss;
										gr = sr;
										gc = sc;
									}
									if (!(Statics.lessThan(br, bc, gr, gc))) {
										swap(board, (byte)'B', bo, bs, br, bc, (byte)'G', go, gs, gr, gc);
										so = bo;
										ss = bs;
										sr = br;
										sc = bc;
										bo = go;
										bs = gs;
										br = gr;
										bc = gc;
										go = so;
										gs = ss;
										gr = sr;
										gc = sc;
									}
									if (!(Statics.lessThan(cr, cc, gr, gc))) {
										swap(board, (byte)'C', co, cs, cr, cc, (byte)'G', go, gs, gr, gc);
										so = co;
										ss = cs;
										sr = cr;
										sc = cc;
										co = go;
										cs = gs;
										cr = gr;
										cc = gc;
										go = so;
										gs = ss;
										gr = sr;
										gc = sc;
									}
									if (!(Statics.lessThan(dr, dc, gr, gc))) {
										swap(board, (byte)'D', doo, ds, dr, dc, (byte)'G', go, gs, gr, gc);
										so = doo;
										ss = ds;
										sr = dr;
										sc = dc;
										doo = go;
										ds = gs;
										dr = gr;
										dc = gc;
										go = so;
										gs = ss;
										gr = sr;
										gc = sc;
									}
									if (!(Statics.lessThan(er, ec, gr, gc))) {
										swap(board, (byte)'E', eo, es, er, ec, (byte)'G', go, gs, gr, gc);
										so = eo;
										ss = es;
										sr = er;
										sc = ec;
										eo = go;
										es = gs;
										er = gr;
										ec = gc;
										go = so;
										gs = ss;
										gr = sr;
										gc = sc;
									}
									if (!(Statics.lessThan(fr, fc, gr, gc))) {
										swap(board, (byte)'F', fo, fs, fr, fc, (byte)'G', go, gs, gr, gc);
										so = fo;
										ss = fs;
										sr = fr;
										sc = fc;
										fo = go;
										fs = gs;
										fr = gr;
										fc = gc;
										go = so;
										gs = ss;
										gr = sr;
										gc = sc;
									}
									
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	static void swap(byte[][] board, byte a, byte ao, int as, int ar, int ac, byte b, byte bo, int bs, int br, int bc) {
		
		clearCar(board, a, ao, as, ar, ac);
		clearCar(board, b, bo, bs, br, bc);
		
		insertCar(board, a, bo, bs, br, bc);
		insertCar(board, b, ao, as, ar, ac);
		
	}
	
	public static long getInfoLong(byte[][] board) {
		
//		if (par.totalCarCount() > 8) {
//			throw new IllegalArgumentException("Must have 8 or fewer cars");
//		}
		
		long totalInfo = 0;
		for (byte car : par.possibleCars) {
			
			int carByte = getInfoByte(board, car);
			totalInfo = (totalInfo << 8 | carByte);
			
		}
		
		assert totalInfo != -1;
		
		return totalInfo;
	} 
	
	/**
	 * returns an int, but treat as a byte
	 * this prevents sign-extension jazz 
	 */
	private static int getInfoByte(byte[][] board, byte car) {
		boolean res = loadScratchInfo(board, car);
		if (!res) {
			return 0xff;
		}
		int size = par.scratchInfo.size;
		byte o = par.scratchInfo.o;
		int r = par.scratchInfo.row;
		int c = par.scratchInfo.col;
		
		/* sorrrccc
		 * 76543210
		 */
		
		int ret = (size==2?0:1<<7) | (o==LEFTRIGHT?0:1<<6) | (r << 3) | (c);
		return ret;
	}
	
	static void loadScratchInfo(int b) {
		
		int size = (((b >> 7) & 0x01) == 0) ? 2 : 3;
		byte o = (((b >> 6) & 0x01) == 0) ? LEFTRIGHT : UPDOWN;
		int r = (b >> 3) & 0x07;
		int c = (b >> 0) & 0x07;
		
		par.scratchInfo.size = size;
		par.scratchInfo.o = o;
		par.scratchInfo.row = r;
		par.scratchInfo.col = c;
	}
	
	public static void toBoard(long info, byte[][] out) {
		
		int rByte = (int)((info >>> 56) & 0xff);
		if (rByte != 0xff) {
			loadScratchInfo(rByte);
			insertCar(out, (byte)'R', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		} 
		
		int aByte = (int)((info >>> 48) & 0xff);
		if (aByte != 0xff) {
			loadScratchInfo(aByte);
			insertCar(out, (byte)'A', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		}
		
		int bByte = (int)((info >>> 40) & 0xff);
		if (bByte != 0xff) {
			loadScratchInfo(bByte);
			insertCar(out, (byte)'B', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		}
		
		int cByte = (int)((info >>> 32) & 0xff);
		if (cByte != 0xff) {
			loadScratchInfo(cByte);
			insertCar(out, (byte)'C', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		}
		
		int dByte = (int)((info >>> 24) & 0xff);
		if (dByte != 0xff) {
			loadScratchInfo(dByte);
			insertCar(out, (byte)'D', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		}
		
		int eByte = (int)((info >>> 16) & 0xff);
		if (eByte != 0xff) {
			loadScratchInfo(eByte);
			insertCar(out, (byte)'E', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		}
		
		int fByte = (int)((info >>> 8) & 0xff);
		if (fByte != 0xff) {
			loadScratchInfo(fByte);
			insertCar(out, (byte)'F', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		}
		
		int gByte = (int)((info >>> 0) & 0xff);
		if (gByte != 0xff) {
			loadScratchInfo(gByte);
			insertCar(out, (byte)'G', par.scratchInfo.o, par.scratchInfo.size, par.scratchInfo.row, par.scratchInfo.col);
		}
		
		long testInfo = getInfoLong(out);
		if (testInfo != info) {
			int rRByte = getInfoByte(out, (byte)'R');
			assert rRByte == rByte;
			int rAByte = getInfoByte(out, (byte)'A');
			assert rAByte == aByte;
			int rBByte = getInfoByte(out, (byte)'B');
			assert rBByte == bByte;
			int rCByte = getInfoByte(out, (byte)'C');
			assert rCByte == cByte;
			int rDByte = getInfoByte(out, (byte)'D');
			assert rDByte == dByte;
			int rEByte = getInfoByte(out, (byte)'E');
			assert rEByte == eByte;
			int rFByte = getInfoByte(out, (byte)'F');
			assert rFByte == fByte;
			int rGByte = getInfoByte(out, (byte)'G');
			assert rGByte == gByte;
		}
		
	}
	
	/**
	 * the byte values go:
	 * 0: 0 2cars and 0 3cars
	 * 1: 1 2cars and 0 3car
	 * 2: 0 2car and 1 3cars
	 * 3: 1 2car and 1 3car
	 * 4: 2 2car and 0 3cars
	 * 5: *
	 */
	public static void getPartitionId(byte[][] board, int rowCount, int colCount, byte[] out) {
		
		for (int i = 0; i < out.length; i++) {
			out[i] = 0;
		}
		
		for (byte c : par.actualCars) {
			
			loadScratchInfo(board, c);
			byte o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			if (o == LEFTRIGHT) {
				
				if (row < rowCount) {
					
					if (size == 2) {
						
						//add 2car to row row
						switch (out[row]) {
						case 0:
							out[row] = 1;
							break;
						case 1:
							out[row] = 4;
							break;
						case 2:
							out[row] = 3;
							break;
						case 4:
							/*
							 * joint row
							 */
							out[row] = 5;
							break;
						default:
							assert false;
							break;
						}
						
					} else {
						
						//add 3car to row row
						switch (out[row]) {
						case 0:
							out[row] = 2;
							break;
						case 1:
							out[row] = 3;
							break;
						case 2:
							/*
							 * joint row
							 */
							out[row] = 5;
							break;
						default:
							assert false;
							break;
						}
						
					}
					
				}
				
			} else {
				
				if (col < colCount) {
					
					if (size == 2) {
						
						//add 2car to col col
						switch (out[col]) {
						case 0:
							out[col + par.rowCount] = 1;
							break;
						case 1:
							out[col + par.rowCount] = 4;
							break;
						case 2:
							out[col + par.rowCount] = 3;
							break;
						case 4:
							/*
							 * joint col
							 */
							out[col + par.rowCount] = 5;
							break;
						default:
							assert false;
							break;
						}
						
					} else {
						
						//add 3car to col col
						switch (out[col + par.rowCount]) {
						case 0:
							out[col + par.rowCount] = 2;
							break;
						case 1:
							out[col + par.rowCount] = 3;
							break;
						case 2:
							/*
							 * joint col
							 */
							out[col + par.rowCount] = 5;
							break;
						default:
							assert false;
							break;
						}
						
					}
				}
				
			}
			
		}
		
		/*
		 * go back and overwrite for joints
		 */
		for (int i = 0; i < par.rowCount; i++) {
			if (i < rowCount) {
				if (par.isJoint(i, -1) || par.isJoint(i, par.colCount)) {
					assert out[i] == 0 || out[i] == 5;
					out[i] = 5;
				}
			}
		}
		
		for (int i = 0; i < par.colCount; i++) {
			if (i < colCount) {
				if (par.isJoint(-1, i) || par.isJoint(par.rowCount, i)) {
					assert out[i+par.rowCount] == 0 || out[i] == 5;
					out[i+par.rowCount] = 5;
				}
			}
		}
		
//		System.out.println(Arrays.toString(out));
		
	}
	
}
