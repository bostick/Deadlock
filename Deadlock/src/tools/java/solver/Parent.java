package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parent {
	
	public byte[][] ini;
	
	public final int rowCount;
	public final int colCount;
	
	/*
	 * winnableRows and winnableCols are the rows and cols that the red car could be in to be winnable
	 */
	boolean winnableRows;
	boolean winnableCols;
	
	public int[] exit = new int[]{ -1, -1 };
	
	private int[][] jJoints = new int[][]{ {-1, -1}, {-1, -1} };
	private int[][] kJoints = new int[][]{ {-1, -1}, {-1, -1} };
	int jCount = 0;
	int kCount = 0;
	boolean jkConnected;
	boolean jyConnected;
	boolean kyConnected;
	int jConnectedToK = -1;
	int kConnectedToJ = -1;
	int jConnectedToY = -1;
	int kConnectedToY = -1;
	
	List<Byte> actualCars = new ArrayList<Byte>();
	byte[] possibleCars = new byte[]{ 'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	CarInfo scratchInfo = new CarInfo();
	
	Map<Byte, Board> scratchLeft = new HashMap<Byte, Board>();
	Map<Byte, Board> scratchRight = new HashMap<Byte, Board>();
	Map<Byte, Board> scratchUp = new HashMap<Byte, Board>();
	Map<Byte, Board> scratchDown = new HashMap<Byte, Board>();
	
//	byte[][] scratchUp;
//	byte[][] scratchDown;
//	byte[][] scratchLeft;
//	byte[][] scratchRight;
	
//	byte[][] scratchRUp;
//	byte[][] scratchRDown;
//	byte[][] scratchRLeft;
//	byte[][] scratchRRight;
//	
//	byte[][] scratchAUp;
//	byte[][] scratchADown;
//	byte[][] scratchALeft;
//	byte[][] scratchARight;
//	
//	byte[][] scratchBUp;
//	byte[][] scratchBDown;
//	byte[][] scratchBLeft;
//	byte[][] scratchBRight;
//	
//	byte[][] scratchCUp;
//	byte[][] scratchCDown;
//	byte[][] scratchCLeft;
//	byte[][] scratchCRight;
//	
//	byte[][] scratchDUp;
//	byte[][] scratchDDown;
//	byte[][] scratchDLeft;
//	byte[][] scratchDRight;
//	
//	byte[][] scratchEUp;
//	byte[][] scratchEDown;
//	byte[][] scratchELeft;
//	byte[][] scratchERight;
//	
//	byte[][] scratchFUp;
//	byte[][] scratchFDown;
//	byte[][] scratchFLeft;
//	byte[][] scratchFRight;
//	
//	byte[][] scratchGUp;
//	byte[][] scratchGDown;
//	byte[][] scratchGLeft;
//	byte[][] scratchGRight;
	
	Cursor cursor;
	int[] test = new int[2];
	
	List<Board> generatingMoves = new ArrayList<Board>();
	List<Board> solvingMoves = new ArrayList<Board>();
	
	public Board emptyBoard;
	
	public Parent(byte[][] boardIni) {
		
		this.ini = boardIni;
		
		this.rowCount = boardIni.length-2;
		this.colCount = boardIni[0].length-2;
		
		cursor = new Cursor();
		
		/*
		 * exit
		 */
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				if (!((i == 0 || i == boardIni.length-1) || (j == 0 || j == boardIni[0].length-1))) {
					continue;
				}
				byte c = boardIni[i][j];
				switch (c) {
				case 'R':
				case 'Y':
					exit[0] = i-1;
					exit[1] = j-1;
					
					int side = side(exit);
					switch (side) {
					case 0:
					case 2:
						winnableCols = true;
						break;
					case 1:
					case 3:
						winnableRows = true;
						break;
					}
					break;
				}
			}
		}
		
		/*
		 * joints
		 */
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				if (!((i == 0 || i == boardIni.length-1) || (j == 0 || j == boardIni[0].length-1))) {
					continue;
				}
				byte c = boardIni[i][j];
				switch (c) {
				case 'J':
					jJoints[jCount][0] = i-1;
					jJoints[jCount][1] = j-1;
					jCount++;
					break;
				case 'K':
					kJoints[kCount][0] = i-1;
					kJoints[kCount][1] = j-1;
					kCount++;
					break;
				}
			}
		}
		
		byte across;
		if (jCount == 2) {
			if (kCount == 2) {
				
				across = charAcross(boardIni, jJoints[0]);
				if (across == 'K') {
					jkConnected = true;
					jConnectedToK = 0;
				} else if (across == 'Y') {
					jyConnected = true;
					jConnectedToY = 0;
				}
				
				across = charAcross(boardIni, jJoints[1]);
				if (across == 'K') {
					jkConnected = true;
					jConnectedToK = 1;
				} else if (across == 'Y') {
					jyConnected = true;
					jConnectedToY = 1;
				}
				
				across = charAcross(boardIni, kJoints[0]);
				if (across == 'J') {
					jkConnected = true;
					kConnectedToJ = 0;
				} else if (across == 'Y') {
					kyConnected = true;
					kConnectedToY = 0;
				}
				
				across = charAcross(boardIni, kJoints[1]);
				if (across == 'J') {
					jkConnected = true;
					kConnectedToJ = 1;
				} else if (across == 'Y') {
					kyConnected = true;
					kConnectedToY = 1;
				}
				
				if (jkConnected) {
					if (jyConnected) {
						int other = 1-jConnectedToY;
						int[] otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
						
						other = 1-kConnectedToJ;
						otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
						
					} else if (kyConnected) {
						int other = 1-kConnectedToY;
						int[] otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
						
						other = 1-jConnectedToK;
						otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
					}
					
				} else {
					// j and k are separate
					if (jyConnected) {
						
						int other = 1-jConnectedToY;
						int[] otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
						
					} else if (kyConnected) {
						
						int other = 1-kConnectedToY;
						int[] otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
					}
				}
				
			} else {
				// only j
				if (jyConnected) {
					
					int other = 1-jConnectedToY;
					int[] otherJoint = jJoints[other];
					
					addToWinnables(otherJoint);
				}
				
			}
		}
		
		/*
		 * cars present
		 */
//		for (int i = 0; i < boardIni.length; i++) {
//			for (int j = 0; j < boardIni[i].length; j++) {
//				byte c = boardIni[i][j];
//				switch (c) {
//				case 'R':
//				case 'A':
//				case 'B':
//				case 'C':
//				case 'D':
//				case 'E':
//				case 'F':
//				case 'G':
//					if (!carMapContains(c)) {
//						addCar(c);
//					}
//					break;
//				}
//			}
//		}
		
		
		emptyBoard = new Board(ini.length-2, ini[0].length-2);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				emptyBoard.boardSet(i, j, (byte)' ', colCount);
			}
		}
		
//		scratchUp = Config.newConfig(emptyBoard);
//		scratchDown = Config.newConfig(emptyBoard);
//		scratchLeft = Config.newConfig(emptyBoard);
//		scratchRight = Config.newConfig(emptyBoard);
		
	}
	
	public int side(int[] coor) {
		if (coor[0] == -1) {
			assert coor[1] >= 0 && coor[1] <= colCount-1;
			return 0;
		} else if (coor[0] == rowCount) {
			assert coor[1] >= 0 && coor[1] <= colCount-1;
			return 2;
		} else if (coor[1] == -1) {
			assert coor[0] >= 0 && coor[0] <= rowCount-1;
			return 3;
		} else {
			assert coor[1] == colCount;
			assert coor[0] >= 0 && coor[0] <= rowCount-1;
			return 1;
		}
	}
	
	public void across(int[] coor, int[] out) {
		if (coor[0] == -1) {
			out[0] = rowCount;
			out[1] = coor[1];
		} else if (coor[0] == rowCount) {
			out[0] = 0;
			out[1] = coor[1];
		} else if (coor[1] == -1) {
			out[0] = coor[0];
			out[1] = colCount;
		} else {
			out[0] = coor[0];
			out[1] = 0;
		}
	}
	
	public int[] otherJoint(int[] coor) {
		return otherJoint(coor[0], coor[1]);
	}
	
	public int[] otherJoint(int r, int c) {
		if (Statics.equals(r, c, jJoints[0])) {
			return jJoints[1];
		} else if (Statics.equals(r, c, jJoints[1])) {
			return jJoints[0];
		} else if (Statics.equals(r, c, kJoints[0])) {
			return kJoints[1];
		} else {
			assert Statics.equals(r, c, kJoints[1]);
			return kJoints[0];
		}
	}
	
	void addToWinnables(int[] coor) {
		int side = side(coor);
		switch (side) {
		case 0:
		case 2:
			winnableCols = true;
			break;
		case 1:
		case 3:
			winnableRows = true;
			break;
		}
	}
	
	public byte charAcross(byte[][] ini, int[] coor) {
		if (coor[0] == -1) {
			return ini[rowCount+1][coor[1]+1];
		} else if (coor[0] == rowCount) {
			return ini[-1+1][coor[1]+1];
		} else if (coor[1] == -1) {
			return ini[coor[0]+1][colCount+1];
		} else {
			assert coor[1] == colCount;
			return ini[coor[0]+1][-1+1];
		}
	}
	
//	public boolean carMapContains(byte c) {
//		switch (c) {
//		case 'R':
//			return carPresent[0];
//		case 'A':
//			return carPresent[1];
//		case 'B':
//			return carPresent[2];
//		case 'C':
//			return carPresent[3];
//		case 'D':
//			return carPresent[4];
//		case 'E':
//			return carPresent[5];
//		case 'F':
//			return carPresent[6];
//		case 'G':
//			return carPresent[7];
//		}
//		
//		assert false;
//		return false;
//	}
	
//	public int totalCarCount() {
//		int total = 0;
//		for (int i = 0; i < carPresent.length; i++) {
//			if (carPresent[i]) {
//				total++;
//			} else {
//				break;
//			}
//		}
//		return total;
//	}
	
	public void addCar(byte c) {
		
		actualCars.add(c);
		
//		byte[][] scratchUp = Config.newConfig(emptyBoard);
//		byte[][] scratchDown = Config.newConfig(emptyBoard);
//		byte[][] scratchLeft = Config.newConfig(emptyBoard);
//		byte[][] scratchRight = Config.newConfig(emptyBoard);
		
		scratchLeft.put(c, new Board(emptyBoard));
		scratchRight.put(c, new Board(emptyBoard));
		scratchUp.put(c, new Board(emptyBoard));
		scratchDown.put(c, new Board(emptyBoard));
		
//		switch (c) {
//		case 'R':
//			carPresent[0] = true;
//			scratchRUp = Config.newConfig(emptyBoard);
//			scratchRDown = Config.newConfig(emptyBoard);
//			scratchRLeft = Config.newConfig(emptyBoard);
//			scratchRRight = Config.newConfig(emptyBoard);
//			break;
//		case 'A':
//			carPresent[1] = true;
//			scratchAUp = Config.newConfig(emptyBoard);
//			scratchADown = Config.newConfig(emptyBoard);
//			scratchALeft = Config.newConfig(emptyBoard);
//			scratchARight = Config.newConfig(emptyBoard);
//			break;
//		case 'B':
//			carPresent[2] = true;
//			scratchBUp = Config.newConfig(emptyBoard);
//			scratchBDown = Config.newConfig(emptyBoard);
//			scratchBLeft = Config.newConfig(emptyBoard);
//			scratchBRight = Config.newConfig(emptyBoard);
//			break;
//		case 'C':
//			carPresent[3] = true;
//			scratchCUp = Config.newConfig(emptyBoard);
//			scratchCDown = Config.newConfig(emptyBoard);
//			scratchCLeft = Config.newConfig(emptyBoard);
//			scratchCRight = Config.newConfig(emptyBoard);
//			break;
//		case 'D':
//			carPresent[4] = true;
//			scratchDUp = Config.newConfig(emptyBoard);
//			scratchDDown = Config.newConfig(emptyBoard);
//			scratchDLeft = Config.newConfig(emptyBoard);
//			scratchDRight = Config.newConfig(emptyBoard);
//			break;
//		case 'E':
//			carPresent[5] = true;
//			scratchEUp = Config.newConfig(emptyBoard);
//			scratchEDown = Config.newConfig(emptyBoard);
//			scratchELeft = Config.newConfig(emptyBoard);
//			scratchERight = Config.newConfig(emptyBoard);
//			break;
//		case 'F':
//			carPresent[6] = true;
//			scratchFUp = Config.newConfig(emptyBoard);
//			scratchFDown = Config.newConfig(emptyBoard);
//			scratchFLeft = Config.newConfig(emptyBoard);
//			scratchFRight = Config.newConfig(emptyBoard);
//			break;
//		case 'G':
//			carPresent[7] = true;
//			scratchGUp = Config.newConfig(emptyBoard);
//			scratchGDown = Config.newConfig(emptyBoard);
//			scratchGLeft = Config.newConfig(emptyBoard);
//			scratchGRight = Config.newConfig(emptyBoard);
//			break;
//		default:
//			assert false;
//			break;
//		}
	}
	
//	byte[][] scratchLeft(byte b) {
//		switch (b) {
//		case 'R':
//			return scratchRLeft;
//		case 'A':
//			return scratchALeft;
//		case 'B':
//			return scratchBLeft;
//		case 'C':
//			return scratchCLeft;
//		case 'D':
//			return scratchDLeft;
//		case 'E':
//			return scratchELeft;
//		case 'F':
//			return scratchFLeft;
//		case 'G':
//			return scratchGLeft;
//		default:
//			assert false;
//			return null;
//		}
//	}
	
//	byte[][] scratchRight(byte b) {
//		switch (b) {
//		case 'R':
//			return scratchRRight;
//		case 'A':
//			return scratchARight;
//		case 'B':
//			return scratchBRight;
//		case 'C':
//			return scratchCRight;
//		case 'D':
//			return scratchDRight;
//		case 'E':
//			return scratchERight;
//		case 'F':
//			return scratchFRight;
//		case 'G':
//			return scratchGRight;
//		default:
//			assert false;
//			return null;
//		}
//	}
//	
//	byte[][] scratchUp(byte b) {
//		switch (b) {
//		case 'R':
//			return scratchRUp;
//		case 'A':
//			return scratchAUp;
//		case 'B':
//			return scratchBUp;
//		case 'C':
//			return scratchCUp;
//		case 'D':
//			return scratchDUp;
//		case 'E':
//			return scratchEUp;
//		case 'F':
//			return scratchFUp;
//		case 'G':
//			return scratchGUp;
//		default:
//			assert false;
//			return null;
//		}
//	}
//	
//	byte[][] scratchDown(byte b) {
//		switch (b) {
//		case 'R':
//			return scratchRDown;
//		case 'A':
//			return scratchADown;
//		case 'B':
//			return scratchBDown;
//		case 'C':
//			return scratchCDown;
//		case 'D':
//			return scratchDDown;
//		case 'E':
//			return scratchEDown;
//		case 'F':
//			return scratchFDown;
//		case 'G':
//			return scratchGDown;
//		default:
//			assert false;
//			return null;
//		}
//	}
	
	public boolean isJoint(int[] coor) {
		if (!((coor[0] == -1 || coor[0] == rowCount) || (coor[1] == -1 || coor[1] == colCount))) {
			return false;
		}
		byte b = val(coor);
		return b == 'J' || b == 'K';
	}
	
	public boolean isJoint(int r, int c) {
		if (!((r == -1 || r == rowCount) || (c == -1 || c == colCount))) {
			return false;
		}
		byte b = val(r, c);
		return b == 'J' || b == 'K';
	}
	
	public byte val(int[] coor) {
		return ini[coor[0]+1][coor[1]+1];
	}
	
	public byte val(int r, int c) {
		return ini[r+1][c+1];
	}
	
}
