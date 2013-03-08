package solver;

import java.util.ArrayList;
import java.util.List;

public class ParentConfig {
	
	public byte[][] ini;
	
	public final int rowCount;
	public final int colCount;
	
	/*
	 * winnableRows and winnableCols are the rows and cols that the red car could be in to be winnable
	 */
	boolean[] winnableRows;
	boolean[] winnableCols;
	
	/*
	 * interferenceCones start with winnableRows and winnableCols, and also include the paths of all cars that interfere with those rows and cols
	 */
//	boolean[] interferenceConeRows;
//	boolean[] interferenceConeCols;
	
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
	
	boolean[] carPresent = new boolean[Config.cars.length+1];
	
	CarInfo scratchInfo = new CarInfo();
	
	Config scratchRUp;
	Config scratchRDown;
	Config scratchRLeft;
	Config scratchRRight;
	
	Config scratchAUp;
	Config scratchADown;
	Config scratchALeft;
	Config scratchARight;
	
	Config scratchBUp;
	Config scratchBDown;
	Config scratchBLeft;
	Config scratchBRight;
	
	Config scratchCUp;
	Config scratchCDown;
	Config scratchCLeft;
	Config scratchCRight;
	
	Config scratchDUp;
	Config scratchDDown;
	Config scratchDLeft;
	Config scratchDRight;
	
	Config scratchEUp;
	Config scratchEDown;
	Config scratchELeft;
	Config scratchERight;
	
	Config scratchFUp;
	Config scratchFDown;
	Config scratchFLeft;
	Config scratchFRight;
	
	Config scratchGUp;
	Config scratchGDown;
	Config scratchGLeft;
	Config scratchGRight;
	
	Cursor cursor;
	int[] test = new int[2];
	
	List<Config> moves = new ArrayList<Config>();
	
	byte[] emptyBoard;
	
	public ParentConfig(byte[][] boardIni) {
		
		this.ini = boardIni;
		
		this.rowCount = boardIni.length-2;
		this.colCount = boardIni[0].length-2;
		
		winnableRows = new boolean[rowCount];
		winnableCols = new boolean[colCount];
//		interferenceConeRows = new boolean[rowCount];
//		interferenceConeCols = new boolean[colCount];
		
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
						winnableCols[j-1] = true;
//						interferenceConeCols[j-1] = true;
						break;
					case 1:
					case 3:
						winnableRows[i-1] = true;
//						interferenceConeRows[i-1] = true;
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
				
//				across = charAcross(exit);
				if (jkConnected) {
					if (jyConnected) {
						//winnable
						int other = 1-jConnectedToY;
						int[] otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
//						addToInterference(otherJoint);
						
						other = 1-kConnectedToJ;
						otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
//						addToInterference(otherJoint);
						
					} else if (kyConnected) {
						//winnable
						int other = 1-kConnectedToY;
						int[] otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
//						addToInterference(otherJoint);
						
						other = 1-jConnectedToK;
						otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
//						addToInterference(otherJoint);
					}
					
				} else {
					// j and k are separate
					if (jyConnected) {
						//winnable
						
						int other = 1-jConnectedToY;
						int[] otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
//						addToInterference(otherJoint);
						
					} else if (kyConnected) {
						//winnable
						
						int other = 1-kConnectedToY;
						int[] otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
//						addToInterference(otherJoint);
					}
				}
				
			} else {
				// only j
				if (jyConnected) {
					//winnable
					
					int other = 1-jConnectedToY;
					int[] otherJoint = jJoints[other];
					
					addToWinnables(otherJoint);
//					addToInterference(otherJoint);
				}
				
			}
		}
		
		/*
		 * cars present
		 */
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				byte c = boardIni[i][j];
				switch (c) {
				case 'R':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
					if (!carMapContains(c)) {
						carMapPresent(c);
					}
					break;
				}
			}
		}
		
		
		emptyBoard = Config.newBoard(ini.length-2, ini[0].length-2);
		for (int i = 0; i < emptyBoard.length; i++) {
			emptyBoard[i] = 17;
		}
		
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
		if (Config.equals(r, c, jJoints[0])) {
			return jJoints[1];
		} else if (Config.equals(r, c, jJoints[1])) {
			return jJoints[0];
		} else if (Config.equals(r, c, kJoints[0])) {
			return kJoints[1];
		} else {
			assert Config.equals(r, c, kJoints[1]);
			return kJoints[0];
		}
	}
	
	void addToWinnables(int[] coor) {
		int side = side(coor);
		switch (side) {
		case 0:
		case 2:
			winnableCols[coor[1]] = true;
			break;
		case 1:
		case 3:
			winnableRows[coor[0]] = true;
			break;
		}
	}
	
//	void addToInterference(int[] coor) {
//		int side = side(coor);
//		switch (side) {
//		case 0:
//		case 2:
//			interferenceConeCols[coor[1]] = true;
//			break;
//		case 1:
//		case 3:
//			interferenceConeRows[coor[0]] = true;
//			break;
//		}
//	}
	
//	boolean isInterfereRow(int r) {
//		if (interferenceConeRows[r]) {
//			return true;
//		}
//		for (boolean b : interferenceConeCols) {
//			if (b) {
//				return true;
//			}
//		}
//		return false;
//	}
	
//	boolean isInterfereCol(int c) {
//		if (interferenceConeCols[c]) {
//			return true;
//		}
//		for (boolean b : interferenceConeRows) {
//			if (b) {
//				return true;
//			}
//		}
//		return false;
//	}
	
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
	
	public boolean carMapContains(byte c) {
		switch (c) {
		case 'R':
			return carPresent[0];
		case 'A':
			return carPresent[1];
		case 'B':
			return carPresent[2];
		case 'C':
			return carPresent[3];
		case 'D':
			return carPresent[4];
		case 'E':
			return carPresent[5];
		case 'F':
			return carPresent[6];
		case 'G':
			return carPresent[7];
		}
		
		assert false;
		return false;
	}
	
	public void carMapPresent(byte c) {
		switch (c) {
		case 'R':
			carPresent[0] = true;
			scratchRUp = newConfig();
			scratchRDown = newConfig();
			scratchRLeft = newConfig();
			scratchRRight = newConfig();
			break;
		case 'A':
			carPresent[1] = true;
			scratchAUp = newConfig();
			scratchADown = newConfig();
			scratchALeft = newConfig();
			scratchARight = newConfig();
			break;
		case 'B':
			carPresent[2] = true;
			scratchBUp = newConfig();
			scratchBDown = newConfig();
			scratchBLeft = newConfig();
			scratchBRight = newConfig();
			break;
		case 'C':
			carPresent[3] = true;
			scratchCUp = newConfig();
			scratchCDown = newConfig();
			scratchCLeft = newConfig();
			scratchCRight = newConfig();
			break;
		case 'D':
			carPresent[4] = true;
			scratchDUp = newConfig();
			scratchDDown = newConfig();
			scratchDLeft = newConfig();
			scratchDRight = newConfig();
			break;
		case 'E':
			carPresent[5] = true;
			scratchEUp = newConfig();
			scratchEDown = newConfig();
			scratchELeft = newConfig();
			scratchERight = newConfig();
			break;
		case 'F':
			carPresent[6] = true;
			scratchFUp = newConfig();
			scratchFDown = newConfig();
			scratchFLeft = newConfig();
			scratchFRight = newConfig();
			break;
		case 'G':
			carPresent[7] = true;
			scratchGUp = newConfig();
			scratchGDown = newConfig();
			scratchGLeft = newConfig();
			scratchGRight = newConfig();
			break;
		default:
			assert false;
			break;
		}
	}
	
//	CarInfo carInfo(byte b) {
//		switch (b) {
//		case 'R':
//			return carInfoR;
//		case 'A':
//			return carInfoA;
//		case 'B':
//			return carInfoB;
//		case 'C':
//			return carInfoC;
//		case 'D':
//			return carInfoD;
//		case 'E':
//			return carInfoE;
//		case 'F':
//			return carInfoF;
//		case 'G':
//			return carInfoG;
//		default:
//			assert false;
//			return null;
//		}
//	}
	
	Config scratchLeft(byte b) {
		switch (b) {
		case 'R':
			return scratchRLeft;
		case 'A':
			return scratchALeft;
		case 'B':
			return scratchBLeft;
		case 'C':
			return scratchCLeft;
		case 'D':
			return scratchDLeft;
		case 'E':
			return scratchELeft;
		case 'F':
			return scratchFLeft;
		case 'G':
			return scratchGLeft;
		default:
			assert false;
			return null;
		}
	}
	
	Config scratchRight(byte b) {
		switch (b) {
		case 'R':
			return scratchRRight;
		case 'A':
			return scratchARight;
		case 'B':
			return scratchBRight;
		case 'C':
			return scratchCRight;
		case 'D':
			return scratchDRight;
		case 'E':
			return scratchERight;
		case 'F':
			return scratchFRight;
		case 'G':
			return scratchGRight;
		default:
			assert false;
			return null;
		}
	}
	
	Config scratchUp(byte b) {
		switch (b) {
		case 'R':
			return scratchRUp;
		case 'A':
			return scratchAUp;
		case 'B':
			return scratchBUp;
		case 'C':
			return scratchCUp;
		case 'D':
			return scratchDUp;
		case 'E':
			return scratchEUp;
		case 'F':
			return scratchFUp;
		case 'G':
			return scratchGUp;
		default:
			assert false;
			return null;
		}
	}
	
	Config scratchDown(byte b) {
		switch (b) {
		case 'R':
			return scratchRDown;
		case 'A':
			return scratchADown;
		case 'B':
			return scratchBDown;
		case 'C':
			return scratchCDown;
		case 'D':
			return scratchDDown;
		case 'E':
			return scratchEDown;
		case 'F':
			return scratchFDown;
		case 'G':
			return scratchGDown;
		default:
			assert false;
			return null;
		}
	}
	
//	public static boolean isJorK(byte b) {
//		return b == 'J' || b == 'K';
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
	
	public Config newConfig() {
		return new Config(this, emptyBoard);
	}
}
