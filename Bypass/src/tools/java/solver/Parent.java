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
	
	public List<Byte> actualCars = new ArrayList<Byte>();
	byte[] possibleCars = new byte[]{ 'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	CarInfo scratchInfo = new CarInfo();
	
	Map<Byte, Board> scratchLeft = new HashMap<Byte, Board>();
	Map<Byte, Board> scratchRight = new HashMap<Byte, Board>();
	Map<Byte, Board> scratchUp = new HashMap<Byte, Board>();
	Map<Byte, Board> scratchDown = new HashMap<Byte, Board>();
	
	Cursor cursor;
	int[] test = new int[2];
	
	List<Board> generatingMoves = new ArrayList<Board>();
	List<Board> solvingMoves = new ArrayList<Board>();
	
	public Board emptyBoard;
	
	public static Parent parent(int[][] boardIni) {
		
		byte[][] converted = new byte[boardIni.length][boardIni[0].length];
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				converted[i][j] = (byte)boardIni[i][j];
			}
		}
		return new Parent(converted);
	}
	
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
		
		emptyBoard = new Board(ini.length-2, ini[0].length-2);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				emptyBoard.boardSet(i, j, (byte)' ', colCount);
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
	
	public void addCar(byte c) {
		
		actualCars.add(c);
		
		scratchLeft.put(c, new Board(emptyBoard));
		scratchRight.put(c, new Board(emptyBoard));
		scratchUp.put(c, new Board(emptyBoard));
		scratchDown.put(c, new Board(emptyBoard));
	}
	
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
