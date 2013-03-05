package solver;

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
	boolean[] interferenceConeRows;
	boolean[] interferenceConeCols;
	
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
	
	public ParentConfig(byte[][] boardIni) {
		
		this.ini = boardIni;
		
		this.rowCount = boardIni.length-2;
		this.colCount = boardIni[0].length-2;
		
		winnableRows = new boolean[rowCount];
		winnableCols = new boolean[colCount];
		interferenceConeRows = new boolean[rowCount];
		interferenceConeCols = new boolean[colCount];
		
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
						interferenceConeCols[j-1] = true;
						break;
					case 1:
					case 3:
						winnableRows[i-1] = true;
						interferenceConeRows[i-1] = true;
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
						addToInterference(otherJoint);
						
						other = 1-kConnectedToJ;
						otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
						addToInterference(otherJoint);
						
					} else if (kyConnected) {
						//winnable
						int other = 1-kConnectedToY;
						int[] otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
						addToInterference(otherJoint);
						
						other = 1-jConnectedToK;
						otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
						addToInterference(otherJoint);
					}
					
				} else {
					// j and k are separate
					if (jyConnected) {
						//winnable
						
						int other = 1-jConnectedToY;
						int[] otherJoint = jJoints[other];
						
						addToWinnables(otherJoint);
						addToInterference(otherJoint);
						
					} else if (kyConnected) {
						//winnable
						
						int other = 1-kConnectedToY;
						int[] otherJoint = kJoints[other];
						
						addToWinnables(otherJoint);
						addToInterference(otherJoint);
					}
				}
				
			} else {
				// only j
				if (jyConnected) {
					//winnable
					
					int other = 1-jConnectedToY;
					int[] otherJoint = jJoints[other];
					
					addToWinnables(otherJoint);
					addToInterference(otherJoint);
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
	
	void addToInterference(int[] coor) {
		int side = side(coor);
		switch (side) {
		case 0:
		case 2:
			interferenceConeCols[coor[1]] = true;
			break;
		case 1:
		case 3:
			interferenceConeRows[coor[0]] = true;
			break;
		}
	}
	
	boolean isInterfereRow(int r) {
		if (interferenceConeRows[r]) {
			return true;
		}
		for (boolean b : interferenceConeCols) {
			if (b) {
				return true;
			}
		}
		return false;
	}
	
	boolean isInterfereCol(int c) {
		if (interferenceConeCols[c]) {
			return true;
		}
		for (boolean b : interferenceConeRows) {
			if (b) {
				return true;
			}
		}
		return false;
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
			break;
		case 'A':
			carPresent[1] = true;
			break;
		case 'B':
			carPresent[2] = true;
			break;
		case 'C':
			carPresent[3] = true;
			break;
		case 'D':
			carPresent[4] = true;
			break;
		case 'E':
			carPresent[5] = true;
			break;
		case 'F':
			carPresent[6] = true;
			break;
		case 'G':
			carPresent[7] = true;
			break;
		default:
			assert false;
			break;
		}
	}
	
	public static boolean isJorK(byte b) {
		return b == 'J' || b == 'K';
	}
	
	public boolean isJoint(int[] coor) {
		byte c = val(coor);
		return c == 'J' || c == 'K';
	}
	
	public byte val(int[] coor) {
		return ini[coor[0]+1][coor[1]+1];
	}
	
	public byte val(int r, int c) {
		return ini[r+1][c+1];
	}
	
}
