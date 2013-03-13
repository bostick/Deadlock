package solver;

import java.util.List;

public class Config {
	
	public static ParentConfig par;
	
//	public final byte[] board;
	
	static byte[] cars = new byte[] {'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	private Config() {
		
	}
	
	public static byte[] newConfig(byte[] old) {
		byte[] board = new byte[old.length];
		System.arraycopy(old, 0, board, 0, old.length);
		return board;
	}
	
	public static void copyTo(byte[] in, byte[] out) {
		System.arraycopy(in, 0, out, 0, in.length);
	}
	
	public static byte[] clone(byte[] in) {
		byte[] n = newConfig(par.emptyBoard);
		copyTo(in, n);
		return n;
	}
	
//	public boolean equals(Object o) {
//		byte[] other = ((Config)o).board;
//		if (board.length != other.length) {
//			return false;
//		}
//		for (int i = 0; i < board.length; i++) {
//			if (board[i] != other[i]) {
//				return false;
//			}
//		}
//		return true;
//	}
	
//	public int hashCode() {
//		int h = 17;
//		for (int i = 0; i < board.length; i++) {
//			h = 37 * h + board[i];
//		}
//		return h;
//	}
	
	public static String toString(byte[] in) {
		
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
	};
	
	public static byte[] newBoard(int rows, int cols) {
		return new byte[rows * cols];
	}
	
	static byte boardGet(byte[] board, int[] coor) {
		return boardGet(board, coor[0], coor[1]);
	}
	
	static byte boardGet(byte[] board, int r, int c) {
		
//		int actualCol;
//		int actualColCount = (par.colCount % 2 == 0) ? par.colCount / 2 : par.colCount / 2 + 1;
//		int n;
//		if (c % 2 == 0) {
//			actualCol = c / 2;
//			int old8bits = board[r * actualColCount + actualCol];
//			n = ((old8bits & 0x0f));
//		} else {
//			actualCol = c / 2;
//			int old8bits = board[r * actualColCount + actualCol];
//			n = ((old8bits & 0xf0) >> 4);
//		}
//		
//		switch (n) {
//		case 0:
//			return 'X';
//		case 1:
//			return 'R';
//		case 2:
//			return 'A';
//		case 3:
//			return 'B';
//		case 4:
//			return 'C';
//		case 5:
//			return 'D';
//		case 6:
//			return 'E';
//		case 7:
//			return 'F';
//		case 8:
//			return 'G';
//		default:
//			assert false;
//			return 0;
//		}
		return board[r * par.colCount + c];
		
	}
	
	static void boardSet(byte[] board, int r, int c, byte b) {
		boardSet(board, r, c, b, par.colCount);
	}
	
	public static void boardSet(byte[] board, int r, int c, byte b, int colCount) {
		
//		byte actualByte = 0;
//		switch (b) {
//		case 'X':
//			actualByte = 0;
//			break;
//		case 'R':
//			actualByte = 1;
//			break;
//		case 'A':
//			actualByte = 2;
//			break;
//		case 'B':
//			actualByte = 3;
//			break;
//		case 'C':
//			actualByte = 4;
//			break;
//		case 'D':
//			actualByte = 5;
//			break;
//		case 'E':
//			actualByte = 6;
//			break;
//		case 'F':
//			actualByte = 7;
//			break;
//		case 'G':
//			actualByte = 8;
//			break;
//		default:
//			assert false;
//			return;
//		}
//		
//		int actualCol;
//		int actualColCount = (colCount % 2 == 0) ? colCount / 2 : colCount / 2 + 1;
//		if (c % 2 == 0) {
//			actualCol = c / 2;
//			int old8bits = board[r * actualColCount + actualCol];
//			byte n = (byte)((old8bits & 0xf0) | actualByte);
//			board[r * actualColCount + actualCol] = n;
//		} else {
//			actualCol = c / 2;
//			int old8bits = board[r * actualColCount + actualCol];
//			byte n = (byte)((old8bits & 0x0f) | (actualByte<<4));
//			board[r * actualColCount + actualCol] = n;
//		}
		
		board[r * colCount + c] = b;
		
	}
	
	public static boolean boardContainsCar(byte[] board, byte b) {
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
	
	public static void clearCar(byte[] board, byte c, Orientation o, int size, int row, int col) {
		
		byte old;
		switch (o) {
		case LEFTRIGHT:
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
			break;
		case UPDOWN:
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
			break;
		}
		
	}
	
	public static boolean isWinning(byte[] board) {
		
		loadScratchInfo(board, (byte)'R');
		Orientation o = par.scratchInfo.o;
		int r = par.scratchInfo.row;
		int c = par.scratchInfo.col;
		
		int side = par.side(par.exit);
		switch (side) {
		case 0:
			return o == Orientation.UPDOWN && Statics.equals(r-2, c, par.exit) && boardGet(board, r-1, c) == ' ';
		case 1:
			return o == Orientation.LEFTRIGHT && Statics.equals(r, c+3, par.exit) && boardGet(board, r, c+2) == ' ';
		case 2:
			return o == Orientation.UPDOWN && Statics.equals(r+3, c, par.exit) && boardGet(board, r+2, c) == ' ';
		case 3:
			return o == Orientation.LEFTRIGHT && Statics.equals(r, c-2, par.exit) && boardGet(board, r, c-1) == ' ';
		}
		
		assert false;
		return false;
	}
	
	static boolean hasClearPathToExit(byte[] board) {
		
		par.cursor.reset(board);
		while (true) {
			if (Statics.isSpaceorY(par.cursor.val())) {
				par.cursor.move();
			} else if (par.cursor.val() == 'R') {
				loadScratchInfo(board, (byte)'R');
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
//	static int numberMovesToWin(byte[] board) {
//		
//		par.cursor.reset(board); 
//		int moves = 0;
//		while (true) {
//			if (par.cursor.val() == 'Y' || par.cursor.val() == 'X') {
//				par.cursor.move();
//				moves++;
//			} else {
//				if (par.cursor.val() == 'R') {
//					loadScratchInfo(board, (byte)'R');
//					Orientation o = par.scratchInfo.o;
//					
//					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == Orientation.UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == Orientation.LEFTRIGHT))) {
//						break;
//					} else {
//						par.cursor.move();
//						moves++;
//					}
//				} else {
//					assert false;
//				}
//			}
//		}
//		
//		return moves;
//	}
	
	/**
	 * returns old value
	 */
	private static byte clearCoor(byte[] board, int r, int c) {
		byte old = boardGet(board, r, c);
		boardSet(board, r, c, (byte)' ');
		return old;
	}
	
	static boolean availableForCar(byte[] board, byte c, int size, Orientation o, int row, int col) {
		
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
	
	public static boolean insertCar(byte[] board, byte c, Orientation o, int size, int row, int col) {
		
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
	
	public static void moveCar(byte[] board, byte c, int size, Orientation oldO, int oldRow, int oldCol, Orientation newO, int newRow, int newCol, byte[] out) {
		
		assert board != out;
		
		copyTo(board, out);
		
		clearCar(out, c, oldO, size, oldRow, oldCol);
		
		insertCar(out, c, newO, size, newRow, newCol);
		
		alphaReduce(out);
		
	}
	
	public static List<byte[]> possiblePreviousMoves(byte[] board) {
		
		par.generatingMoves.clear();
		
		if (hasClearPathToExit(board)) {
			
			for (byte c : cars) {
				if (!par.carMapContains(c)) {
					continue;
				}
				
				if (c == 'R') {
					
					/*
					 * for red car, move away from exit
					 */
					loadScratchInfo(board, c);
					Orientation o = par.scratchInfo.o;
					int size = par.scratchInfo.size;
					int row = par.scratchInfo.row;
					int col = par.scratchInfo.col;
					
					byte[] scratch;
					
					switch (o) {
					case LEFTRIGHT:
						
						scratch = par.scratchLeft(c);
						if (availableForCar(board, c, size, o, row, col-1)) {
							
							moveCar(board, c, size, o, row, col, o, row, col-1, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row, col-1)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col-1), scratch);
							if (res) {
								if (furtherFromExit(c, scratch, board)) {
									par.generatingMoves.add(scratch);
									continue;
								}
							}
						}
						
						scratch = par.scratchRight(c);
						if (availableForCar(board, c, size, o, row, col+1)) {
							moveCar(board, c, size, o, row, col, o, row, col+1, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row, col+size)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col+size), scratch);
							if (res) {
								if (furtherFromExit(c, scratch, board)) {
									par.generatingMoves.add(scratch);
									continue;
								}
							}
						}
						break;
					case UPDOWN:
						
						scratch = par.scratchUp(c);
						if (availableForCar(board, c, size, o, row-1, col)) {
							moveCar(board, c, size, o, row, col, o, row-1, col, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row-1, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row-1, col), scratch);
							if (res) {
								if (furtherFromExit(c, scratch, board)) {
									par.generatingMoves.add(scratch);
									continue;
								}
							}
						}
						
						scratch = par.scratchDown(c);
						if (availableForCar(board, c, size, o, row+1, col)) {
							moveCar(board, c, size, o, row, col, o, row+1, col, scratch);
							if (furtherFromExit(c, scratch, board)) {
								par.generatingMoves.add(scratch);
								continue;
							}
						} else if (par.isJoint(row+size, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row+size, col), scratch);
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
					Orientation o = par.scratchInfo.o;
					int size = par.scratchInfo.size;
					int row = par.scratchInfo.row;
					int col = par.scratchInfo.col;
					
					byte[] scratch;
					
					switch (o) {
					case LEFTRIGHT:
						
						scratch = par.scratchLeft(c);
						if (availableForCar(board, c, size, o, row, col-1)) {
							
							moveCar(board, c, size, o, row, col, o, row, col-1, scratch);
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row, col-1)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col-1), scratch);
							if (res) {
								if (nowBlockingPath(scratch, board)) {
									par.generatingMoves.add(scratch);
								}
							}
						}
						
						scratch = par.scratchRight(c);
						if (availableForCar(board, c, size, o, row, col+1)) {
							moveCar(board, c, size, o, row, col, o, row, col+1, par.scratchRight(c));
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row, col+size)) {
							boolean res = tryJoint(board, c, par.otherJoint(row, col+size), par.scratchRight(c));
							if (res) {
								if (nowBlockingPath(scratch, board)) {
									par.generatingMoves.add(scratch);
								}
							}
						}
						break;
					case UPDOWN:
						
						scratch = par.scratchUp(c);
						if (availableForCar(board, c, size, o, row-1, col)) {
							moveCar(board, c, size, o, row, col, o, row-1, col, par.scratchUp(c));
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row-1, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row-1, col), par.scratchUp(c));
							if (res) {
								if (nowBlockingPath(scratch, board)) {
									par.generatingMoves.add(scratch);
								}
							}
						}
						
						scratch = par.scratchDown(c);
						if (availableForCar(board, c, size, o, row+1, col)) {
							moveCar(board, c, size, o, row, col, o, row+1, col, par.scratchDown(c));
							if (nowBlockingPath(scratch, board)) {
								par.generatingMoves.add(scratch);
							}
						} else if (par.isJoint(row+size, col)) {
							boolean res = tryJoint(board, c, par.otherJoint(row+size, col), par.scratchDown(c));
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
		
		for (byte c : cars) {
			if (!par.carMapContains(c)) {
				continue;
			}
			
			loadScratchInfo(board, c);
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			byte[] scratch;
			
			switch (o) {
			case LEFTRIGHT:
				
				scratch = par.scratchLeft(c);
				if (availableForCar(board, c, size, o, row, col-1)) {
					moveCar(board, c, size, o, row, col, o, row, col-1, scratch);
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col-1), scratch);
					if (res) {
						if (!hasClearPathToExit(scratch)) {
							par.generatingMoves.add(scratch);
						}
					}
				}
				
				scratch = par.scratchRight(c);
				if (availableForCar(board, c, size, o, row, col+1)) {
					moveCar(board, c, size, o, row, col, o, row, col+1, par.scratchRight(c));
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col+size), par.scratchRight(c));
					if (res) {
						if (!hasClearPathToExit(scratch)) {
							par.generatingMoves.add(scratch);
						}
					}
				}
				break;
			case UPDOWN:
				
				scratch = par.scratchUp(c);
				if (availableForCar(board, c, size, o, row-1, col)) {
					moveCar(board, c, size, o, row, col, o, row-1, col, par.scratchUp(c));
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						if (!hasClearPathToExit(scratch)) {
							par.generatingMoves.add(scratch);
						}
					}
				}
				
				scratch = par.scratchDown(c);
				if (availableForCar(board, c, size, o, row+1, col)) {
					moveCar(board, c, size, o, row, col, o, row+1, col, par.scratchDown(c));
					if (!hasClearPathToExit(scratch)) {
						par.generatingMoves.add(scratch);
					}
				} else if (par.isJoint(row+size, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row+size, col), par.scratchDown(c));
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
	
	public static List<byte[]> possibleNextMoves(byte[] board) {
		
		par.solvingMoves.clear();
		
		if (isWinning(board)) {
			return par.solvingMoves;
		}
		
		if (hasClearPathToExit(board)) {
			
			loadScratchInfo(board, (byte)'R');
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			byte[] scratch;
			
			switch (o) {
			case LEFTRIGHT:
				
				scratch = par.scratchLeft((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row, col-1)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row, col-1, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row, col-1), scratch);
					if (res) {
						if (closerToExit((byte)'R', scratch, board)) {
							par.solvingMoves.add(scratch);
							return par.solvingMoves;
						}
					}
				}
				
				scratch = par.scratchRight((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row, col+1)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row, col+1, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row, col+2)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row, col+2), scratch);
					if (res) {
						if (closerToExit((byte)'R', scratch, board)) {
							par.solvingMoves.add(scratch);
							return par.solvingMoves;
						}
					}
				}
				
				break;
			case UPDOWN:
				
				scratch = par.scratchUp((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row-1, col)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row-1, col, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row-1, col), scratch);
					if (res) {
						if (closerToExit((byte)'R', scratch, board)) {
							par.solvingMoves.add(scratch);
							return par.solvingMoves;
						}
					}
				}
				
				scratch = par.scratchDown((byte)'R');
				if (availableForCar(board, (byte)'R', size, o, row+1, col)) {
					moveCar(board, (byte)'R', size, o, row, col, o, row+1, col, scratch);
					if (closerToExit((byte)'R', scratch, board)) {
						par.solvingMoves.add(scratch);
						return par.solvingMoves;
					}
				} else if (par.isJoint(row+2, col)) {
					boolean res = tryJoint(board, (byte)'R', par.otherJoint(row+2, col), scratch);
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
		
		for (byte c : cars) {
			if (!par.carMapContains(c)) {
				continue;
			}
			
			/*
			 * if clearPathToExit,
			 * 		if red, only previous move was going away from exit
			 * 		if other, only previous move was interfering with winning path
			 */
			
			loadScratchInfo(board, c);
			Orientation o = par.scratchInfo.o;
			int size = par.scratchInfo.size;
			int row = par.scratchInfo.row;
			int col = par.scratchInfo.col;
			
			switch (o) {
			case LEFTRIGHT:
				
				if (availableForCar(board, c, size, o, row, col-1)) {
					moveCar(board, c, size, o, row, col, o, row, col-1, par.scratchLeft(c));
					par.solvingMoves.add(par.scratchLeft(c));
				} else if (par.isJoint(row, col-1)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col-1), par.scratchLeft(c));
					if (res) {
						par.solvingMoves.add(par.scratchLeft(c));
					}
				}
				
				if (availableForCar(board, c, size, o, row, col+1)) {
					moveCar(board, c, size, o, row, col, o, row, col+1, par.scratchRight(c));
					par.solvingMoves.add(par.scratchRight(c));
				} else if (par.isJoint(row, col+size)) {
					boolean res = tryJoint(board, c, par.otherJoint(row, col+size), par.scratchRight(c));
					if (res) {
						par.solvingMoves.add(par.scratchRight(c));
					}
				}
				
				break;
			case UPDOWN:
				
				if (availableForCar(board, c, size, o, row-1, col)) {
					moveCar(board, c, size, o, row, col, o, row-1, col, par.scratchUp(c));
					par.solvingMoves.add(par.scratchUp(c));
				} else if (par.isJoint(row-1, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row-1, col), par.scratchUp(c));
					if (res) {
						par.solvingMoves.add(par.scratchUp(c));
					}
				}
				
				if (availableForCar(board, c, size, o, row+1, col)) {
					moveCar(board, c, size, o, row, col, o, row+1, col, par.scratchDown(c));
					par.solvingMoves.add(par.scratchDown(c));
				} else if (par.isJoint(row+size, col)) {
					boolean res = tryJoint(board, c, par.otherJoint(row+size, col), par.scratchDown(c));
					if (res) {
						par.solvingMoves.add(par.scratchDown(c));
					}
				}
				
				break;
			}
		}
		
		return par.solvingMoves;
	}
	
	static boolean tryJoint(byte[] board, byte c, int[] joint, byte[] out) {
		
		int mR = joint[0];
		int mC = joint[1];
		
		loadScratchInfo(board, c);
		Orientation o = par.scratchInfo.o;
		int size = par.scratchInfo.size;
		int row = par.scratchInfo.row;
		int col = par.scratchInfo.col;
		
		int mSide = par.side(joint);
		switch (mSide) {
		case 0:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, Orientation.UPDOWN, mR+1, mC)) {
					moveCar(board, c, size, o, row, col, Orientation.UPDOWN, mR+1, mC, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, Orientation.UPDOWN, mR+1, mC)) {
					moveCar(board, c, size, o, row, col, Orientation.UPDOWN, mR+1, mC, out);
					return true;
				}
				break;
			}
			break;
		case 1:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, Orientation.LEFTRIGHT, mR, mC-2)) {
					moveCar(board, c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC-2, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, Orientation.LEFTRIGHT, mR, mC-3)) {
					moveCar(board, c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC-3, out);
					return true;
				}
				break;
			}
			break;
		case 2:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, Orientation.UPDOWN, mR-2, mC)) {
					moveCar(board, c, size, o, row, col, Orientation.UPDOWN, mR-2, mC, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, Orientation.UPDOWN, mR-3, mC)) {
					moveCar(board, c, size, o, row, col, Orientation.UPDOWN, mR-3, mC, out);
					return true;
				}
				break;
			}
			break;
		case 3:
			switch (size) {
			case 2:
				if (availableForCar(board, c, size, Orientation.LEFTRIGHT, mR, mC+1)) {
					moveCar(board, c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC+1, out);
					return true;
				}
				break;
			case 3:
				if (availableForCar(board, c, size, Orientation.LEFTRIGHT, mR, mC+1)) {
					moveCar(board, c, size, o, row, col, Orientation.LEFTRIGHT, mR, mC+1, out);
					return true;
				}
				break;
			}
			break;
		}
		
		return false;
	}
	
	static boolean furtherFromExit(byte c, byte[] next, byte[] cur) {
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
					Orientation o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == Orientation.UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == Orientation.LEFTRIGHT))) {
						break;
					} else {
						par.cursor.move();
						nextMoves++;
					}
				} else {
					
//					String.class.getName();
//					
//					par.cursor.reset(next);
//					
//					while (true) {
//						par.cursor.move();
//						if (false) {
//							break;
//						}
//					}
					
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
					Orientation o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == Orientation.UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == Orientation.LEFTRIGHT))) {
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
	
	static boolean closerToExit(byte c, byte[] next, byte[] cur) {
		return furtherFromExit(c, cur, next);
	}
	
	static boolean nowBlockingPath(byte[] next, byte[] cur) {
		
		par.cursor.reset(next);
		boolean nextBlocking = false;
		while (true) {
			if (Statics.isSpaceorY(par.cursor.val())) {
				par.cursor.move();
			} else {
				if (par.cursor.val() == 'R') {
					loadScratchInfo(next, (byte)'R');
					Orientation o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == Orientation.UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == Orientation.LEFTRIGHT))) {
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
					Orientation o = par.scratchInfo.o;
					
					if ((((par.cursor.side == 0 || par.cursor.side == 2) && o == Orientation.UPDOWN) || ((par.cursor.side == 1 || par.cursor.side == 3) && o == Orientation.LEFTRIGHT))) {
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
	
	public static byte[] winningConfig(byte[] board) {
		int side = par.side(par.exit);
		
		byte[] n = newConfig(board);
		
		switch (side) {
		case 0:
			insertCar(n, (byte)'R', Orientation.UPDOWN, 2, par.exit[0]+2, par.exit[1]);
			return n;
		case 1:
			insertCar(n, (byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]-3);
			return n;
		case 2:
			insertCar(n, (byte)'R', Orientation.UPDOWN, 2, par.exit[0]-3, par.exit[1]);
			return n;
		case 3:
			insertCar(n, (byte)'R', Orientation.LEFTRIGHT, 2, par.exit[0], par.exit[1]+2);
			return n;
		}
		
		assert false;
		return null;
	}
	
	static byte firstAvailableCar(byte[] board) {
		for (byte d : cars) {
			if (!boardContainsCar(board, d)) {
				return d;
			}
		}
		assert false;
		return -1;
	}
	
	public static List<byte[]> possible2CarPlacements(byte[] board, List<byte[]> placements, boolean lastToBeAdded) {
		
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
						availableForCar(board, car, 2, Orientation.LEFTRIGHT, r, c) &&
						!completesBlock(board, 2, Orientation.LEFTRIGHT, r, c) &&
						leavesRedCarGap(board, 2, Orientation.LEFTRIGHT, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 2, Orientation.LEFTRIGHT, r, c))) {
					
					byte[] n = newConfig(board);
					insertCar(n, car, Orientation.LEFTRIGHT, 2, r, c);
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
						availableForCar(board, car, 2, Orientation.UPDOWN, r, c) &&
						!completesBlock(board, 2, Orientation.UPDOWN, r, c) &&
						leavesRedCarGap(board, 2, Orientation.UPDOWN, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 2, Orientation.UPDOWN, r, c))) {
					
					byte[] n = newConfig(board);
					insertCar(n, car, Orientation.UPDOWN, 2, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public static List<byte[]> possible3CarPlacements(byte[] board, List<byte[]> placements, boolean lastToBeAdded) {
		
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
						availableForCar(board, car, 3, Orientation.LEFTRIGHT, r, c) &&
						!completesBlock(board, 3, Orientation.LEFTRIGHT, r, c) &&
						leavesRedCarGap(board, 3, Orientation.LEFTRIGHT, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 3, Orientation.LEFTRIGHT, r, c))) {
					
					byte[] n = newConfig(board);
					insertCar(n, car, Orientation.LEFTRIGHT, 3, r, c);
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
						availableForCar(board, car, 3, Orientation.UPDOWN, r, c) &&
						!completesBlock(board, 3, Orientation.UPDOWN, r, c) &&
						leavesRedCarGap(board, 3, Orientation.UPDOWN, r, c) &&
						(!lastToBeAdded || someCarIntersectsWinnable(board, 3, Orientation.UPDOWN, r, c))) {
					
					byte[] n = newConfig(board);
					insertCar(n, car, Orientation.UPDOWN, 3, r, c);
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	static int[] firstAvailableCarCoor(byte[] board) {
		
		par.test[0] = 0;
		par.test[1] = 0;
		for (byte b : cars) {
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
	static boolean completesBlock(byte[] board, int size, Orientation o, int r, int c) {
		
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
	static boolean leavesRedCarGap(byte[] board, int size, Orientation o, int r, int c) {
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
	
	static boolean someCarIntersectsWinnable(byte[] board, int size, Orientation o, int r, int c) {
		
		for (byte d : cars) {
			boolean res = loadScratchInfo(board, d);
			if (res) {
				Orientation so = par.scratchInfo.o;
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
	
	static boolean intersectsWithWinnable(byte[] board, int size, Orientation o, int r, int c) {
		
		switch (o) {
		case LEFTRIGHT:
			return par.winnableCols;
		case UPDOWN:
			return par.winnableRows;
		}
		
		assert false;
		return false;
	}
	
	public static boolean loadScratchInfo(byte[] board, byte c) {
		
		for (int i = 0; i < par.rowCount; i++) {
			for (int j = 0; j < par.colCount; j++) {
				byte b = boardGet(board, i, j);
				if (b == c) {
					if (i+1 < par.rowCount && boardGet(board, i+1, j) == c) {
						if (i+2 < par.colCount && boardGet(board, i+2, j) == c) {
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
						assert boardGet(board, i, j+1) == c;
						if (j+2 < par.colCount && boardGet(board, i, j+2) == c) {
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
	
	static void alphaReduce(byte[] board) {
		
		Orientation so;
		int ss;
		int sr;
		int sc;
		
		boolean res = loadScratchInfo(board, (byte)'A');
		if (res) {
			Orientation ao = par.scratchInfo.o;
			int as = par.scratchInfo.size;
			int ar = par.scratchInfo.row;
			int ac = par.scratchInfo.col;
			
			res = loadScratchInfo(board, (byte)'B');
			if (res) {
				Orientation bo = par.scratchInfo.o;
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
					Orientation co = par.scratchInfo.o;
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
						Orientation doo = par.scratchInfo.o;
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
							Orientation eo = par.scratchInfo.o;
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
								Orientation fo = par.scratchInfo.o;
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
									Orientation go = par.scratchInfo.o;
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
	
	static void swap(byte[] board, byte a, Orientation ao, int as, int ar, int ac, byte b, Orientation bo, int bs, int br, int bc) {
		
		clearCar(board, a, ao, as, ar, ac);
		clearCar(board, b, bo, bs, br, bc);
		
		insertCar(board, a, bo, bs, br, bc);
		insertCar(board, b, ao, as, ar, ac);
		
	}
	
	public static long getInfoLong(byte[] board) {
		
		if (par.totalCarCount() > 8) {
			throw new IllegalArgumentException("Must have 8 or fewer cars");
		}
		
		long totalInfo = 0;
		for (byte car : cars) {
			
			int carByte = getInfoByte(board, car);
			totalInfo = (totalInfo << 8 | carByte);
			
			if (car == 'R') {
				String.class.getName();
			}
			
		}
		
		assert totalInfo != -1;
		
		return totalInfo;
	} 
	
	/**
	 * returns an int, but treat as a byte
	 * this prevents sign-extension jazz 
	 */
	private static int getInfoByte(byte[] board, byte car) {
		boolean res = loadScratchInfo(board, car);
		if (!res) {
			return 0xff;
		}
		int size = par.scratchInfo.size;
		Orientation o = par.scratchInfo.o;
		int r = par.scratchInfo.row;
		int c = par.scratchInfo.col;
		
		/* sorrrccc
		 * 76543210
		 */
		
		int ret = (size==2?0:1<<7) | (o==Orientation.LEFTRIGHT?0:1<<6) | (r << 3) | (c);
		return ret;
	}
	
	static void loadScratchInfo(int b) {
		
		int size = (((b >> 7) & 0x01) == 0) ? 2 : 3;
		Orientation o = (((b >> 6) & 0x01) == 0) ? Orientation.LEFTRIGHT : Orientation.UPDOWN;
		int r = (b >> 3) & 0x07;
		int c = (b >> 0) & 0x07;
		
		par.scratchInfo.size = size;
		par.scratchInfo.o = o;
		par.scratchInfo.row = r;
		par.scratchInfo.col = c;
	}
	
	public static void toBoard(long info, byte[] out) {
		
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
	
}
