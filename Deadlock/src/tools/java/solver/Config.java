package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import solver.Solver.Orientation;

public class Config {
	
	public static Random rand = new Random();
	
	public final int rowCount;
	public final int colCount;
	
	public char[][] ini;
	
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
	
	public Map<Character, CarInfo> carMap = new HashMap<Character, CarInfo>();
	
	private int[] exit = new int[]{ -1, -1 };
	
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
	
	
	List<Config> possibleMoves;
	
	char[] cars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
	
	public Config(String boardIni, int i, int j) {
		this(toCharMatrix(boardIni, i, j));
	}
	
	static char[][] toCharMatrix(String s, int i, int j) {
		
		if (s.length() != i * j) {
			throw new IllegalArgumentException();
		}
		
		char[][] res = new char[i][j];
		char[] source = s.toCharArray();
		
		for (int ii = 0; ii < i; ii++) {
			for (int jj = 0; jj < j; jj++) {
				res[ii][jj] = source[ii * i + jj];
			}
		}
		
		return res;
	}
	
	public Config(char[][] boardIni) {
		
		this.rowCount = boardIni.length-2;
		this.colCount = boardIni[0].length-2;
		
		ini = new char[boardIni.length][boardIni[0].length];
		winnableRows = new boolean[rowCount];
		winnableCols = new boolean[colCount];
		interferenceConeRows = new boolean[rowCount];
		interferenceConeCols = new boolean[colCount];
		
		/*
		 * init
		 */
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				if (i == 0 || i == boardIni.length-1 || j == 0 || j == boardIni[0].length-1) {
					ini[i][j] = ' ';
				} else {
					ini[i][j] = 'X';
				}
			}
		}
		
		/*
		 * exit
		 */
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				if (!((i == 0 || i == boardIni.length-1) || (j == 0 || j == boardIni[0].length-1))) {
					continue;
				}
				char c = boardIni[i][j];
				switch (c) {
				case 'R':
				case 'Y':
					setExit(i-1, j-1);
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
				char c = boardIni[i][j];
				switch (c) {
				case 'J':
					jJoints[jCount][0] = i-1;
					jJoints[jCount][1] = j-1;
					jCount++;
					
					ini[i][j] = 'J';
					break;
				case 'K':
					kJoints[kCount][0] = i-1;
					kJoints[kCount][1] = j-1;
					kCount++;
					
					ini[i][j] = 'K';
					break;
				}
			}
		}
		
		char across;
		if (jCount == 2) {
			if (kCount == 2) {
				
				across = charAcross(jJoints[0]);
				if (across == 'K') {
					jkConnected = true;
					jConnectedToK = 0;
				} else if (across == 'Y') {
					jyConnected = true;
					jConnectedToY = 0;
				}
				
				across = charAcross(jJoints[1]);
				if (across == 'K') {
					jkConnected = true;
					jConnectedToK = 1;
				} else if (across == 'Y') {
					jyConnected = true;
					jConnectedToY = 1;
				}
				
				across = charAcross(kJoints[0]);
				if (across == 'J') {
					jkConnected = true;
					kConnectedToJ = 0;
				} else if (across == 'Y') {
					kyConnected = true;
					kConnectedToY = 0;
				}
				
				across = charAcross(kJoints[1]);
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
		 * red car
		 */
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				char c = boardIni[i][j];
				switch (c) {
				case 'R':
					if (!carMap.keySet().contains(c)) {
						Orientation o;
						int size;
						int row;
						int col;
						if (boardIni[i+1][j] == c && (i+1-1 < rowCount || (i+1-1 == exit[0] && j-1 == exit[1]))) {
							if (boardIni[i+2][j] == c && (i+2-1 < rowCount || (i+2-1 == exit[0] && j-1 == exit[1]))) {
								o = Orientation.UPDOWN;
								row = i-1;
								col = j-1;
								size = 3;
							} else {
								o = Orientation.UPDOWN;
								row = i-1;
								col = j-1;
								size = 2;
							}
						} else {
							assert boardIni[i][j+1] == c;
							if (boardIni[i][j+2] == c && (j+2-1 < colCount || (i-1 == exit[0] && j+2-1 == exit[1]))) {
								o = Orientation.LEFTRIGHT;
								row = i-1;
								col = j-1;
								size = 3;
							} else {
								o = Orientation.LEFTRIGHT;
								row = i-1;
								col = j-1;
								size = 2;
							}
						}
						boolean res = insert(c, o, size, row, col);
						assert res;
						
					}
					break;
				}
			}
		}
		
		/*
		 * other cars
		 */
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				char c = boardIni[i][j];
				switch (c) {
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
					if (!carMap.keySet().contains(c)) {
						Orientation o;
						int size;
						int row;
						int col;
						if (boardIni[i+1][j] == c && (i+1-1 < rowCount || (i+1-1 == exit[0] && j-1 == exit[1]))) {
							if (boardIni[i+2][j] == c && (i+2-1 < rowCount || (i+2-1 == exit[0] && j-1 == exit[1]))) {
								o = Orientation.UPDOWN;
								row = i-1;
								col = j-1;
								size = 3;
							} else {
								o = Orientation.UPDOWN;
								row = i-1;
								col = j-1;
								size = 2;
							}
						} else {
							assert boardIni[i][j+1] == c;
							if (boardIni[i][j+2] == c && (j+2-1 < colCount || (i-1 == exit[0] && j+2-1 == exit[1]))) {
								o = Orientation.LEFTRIGHT;
								row = i-1;
								col = j-1;
								size = 3;
							} else {
								o = Orientation.LEFTRIGHT;
								row = i-1;
								col = j-1;
								size = 2;
							}
						}
						boolean res = insert(c, o, size, row, col);
						assert res;
						
					}
					break;
				}
			}
		}
		
//		interference fix point
		
	}
	
	public boolean equals(Object o) {
		boolean res = Arrays.deepEquals(ini, ((Config)o).ini);
		if (res) {
			return true;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return Arrays.deepHashCode(ini);
	}
	
	public void setExit(int i, int j) {
		exit[0] = i;
		exit[1] = j;
		
		int side = side(exit);
		switch (side) {
		case 0:
		case 2:
			winnableCols[j] = true;
			interferenceConeCols[j] = true;
			break;
		case 1:
		case 3:
			winnableRows[i] = true;
			interferenceConeRows[i] = true;
			break;
		}
		
		ini[i+1][j+1] = 'Y';
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
	
	public char val(int[] coor) {
		return ini[coor[0]+1][coor[1]+1];
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
	
	int matchingJRow(int r, int c) {
		if (jJoints[0][0] == r && jJoints[0][1] == c) {
			return 1;
		} else {
			assert jJoints[1][0] == r && jJoints[1][1] == c;
			return 0;
		}
	}
	
	int matchingKRow(int r, int c) {
		if (kJoints[0][0] == r && kJoints[0][1] == c) {
			return 1;
		} else {
			assert kJoints[1][0] == r && kJoints[1][1] == c;
			return 0;
		}
	}
	
	public int[] otherJoint(int[] coor) {
		if (equals(coor, jJoints[0])) {
			return jJoints[1];
		} else if (equals(coor, jJoints[1])) {
			return jJoints[0];
		} else if (equals(coor, kJoints[0])) {
			return kJoints[1];
		} else {
			assert equals(coor, kJoints[1]);
			return kJoints[0];
		}
	}
	
	public boolean isJoint(int[] coor) {
		char c = val(coor);
		return c == 'J' || c == 'K';
	}
	
	public char charAcross(int[] coor) {
		if (coor[0] == -1) {
//			out[0] = rowCount;
//			out[1] = coor[1];
			return ini[rowCount+1][coor[1]+1];
		} else if (coor[0] == rowCount) {
//			out[0] = 0;
//			out[1] = coor[1];
			return ini[-1+1][coor[1]+1];
		} else if (coor[1] == -1) {
//			out[0] = coor[0];
//			out[1] = colCount;
			return ini[coor[0]+1][colCount+1];
		} else {
			assert coor[1] == colCount;
//			out[0] = coor[0];
//			out[1] = 0;
			return ini[coor[0]+1][-1+1];
		}
	}
	
	public static boolean equals(int[] c0, int[] c1) {
		return c0[0] == c1[0] && c0[1] == c1[1];
	}
	
	public static boolean equals(int c00, int c01, int[] c1) {
		return c00 == c1[0] && c01 == c1[1];
	}
	
	public Config copy() {
		char[][] newIni = new char[ini.length][ini[0].length];
		for (int i = 0; i < newIni.length; i++) {
			for (int j = 0; j < newIni[i].length; j++) {
				newIni[i][j] = ini[i][j];
			}
		}
		return new Config(newIni);
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
	
	void addToInterferenceRow(int r) {
		interferenceConeRows[r] = true;
		
		int[] test = new int[] {r, -1};
		
		if (isJoint(test)) {
			int[] other = otherJoint(test);
			interferenceConeCols[other[1]] = true;
			
			across(other, test);
			if (isJoint(test)) {
				other = otherJoint(test);
				interferenceConeRows[other[0]] = true;
			}
			
		}
		
		test = new int[] {r, colCount};
		
		if (isJoint(test)) {
			int[] other = otherJoint(test);
			interferenceConeCols[other[1]] = true;
			
			across(other, test);
			if (isJoint(test)) {
				other = otherJoint(test);
				interferenceConeRows[other[0]] = true;
			}
			
		}
		
	}
	
	void addToInterferenceCol(int c) {
		interferenceConeCols[c] = true;
		
		int[] test = new int[] {-1, c};
		
		if (isJoint(test)) {
			int[] other = otherJoint(test);
			interferenceConeRows[other[0]] = true;
			
			across(other, test);
			if (isJoint(test)) {
				other = otherJoint(test);
				interferenceConeCols[other[1]] = true;
			}
			
		}
		
		test = new int[] {rowCount, c};
		
		if (isJoint(test)) {
			int[] other = otherJoint(test);
			interferenceConeRows[other[0]] = true;
			
			across(other, test);
			if (isJoint(test)) {
				other = otherJoint(test);
				interferenceConeCols[other[1]] = true;
			}
			
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
	
	public void clear(char c) {
		
		CarInfo oldInfo = carMap.remove(c);
		switch (oldInfo.o) {
		case LEFTRIGHT:
			switch (oldInfo.size) {
			case 2:
				clear(oldInfo.row, oldInfo.col+0);
				clear(oldInfo.row, oldInfo.col+1);
				break;
			case 3:
				clear(oldInfo.row, oldInfo.col+0);
				clear(oldInfo.row, oldInfo.col+1);
				clear(oldInfo.row, oldInfo.col+2);
				break;
			}
			break;
		case UPDOWN:
			switch (oldInfo.size) {
			case 2:
				clear(oldInfo.row+0, oldInfo.col);
				clear(oldInfo.row+1, oldInfo.col);
				break;
			case 3:
				clear(oldInfo.row+0, oldInfo.col);
				clear(oldInfo.row+1, oldInfo.col);
				clear(oldInfo.row+2, oldInfo.col);
				break;
			}
			break;
		}
		
	}
	
	public boolean isWinning() {
		return val(exit) == 'R';
	}
	
	static class Cursor {
		Config config;
		int[] coor;
		int side;
		
		Cursor(Config config) {
			this.config = config;
			coor = new int[2];
			coor[0] = config.exit[0];
			coor[1] = config.exit[1];
			side = otherSide(config.side(config.exit));
		}
		
		void move() {
			switch (side) {
			case 0:
				coor[0] = coor[0]-1;
				if (coor[0] == -1) {
					assert val() == 'J' || val() == 'K';
					int[] other = config.otherJoint(coor);
					int otherSide = config.side(other);
					switch (otherSide) {
					case 0:
						coor[0] = 0;
						coor[1] = other[1];
						side = 2;
						break;
					case 1:
						coor[0] = other[0];
						coor[1] = config.colCount-1;
						side = 3;
						break;
					case 2:
						coor[0] = config.rowCount-1;
						coor[1] = other[1];
						side = 0;
						break;
					case 3:
						coor[0] = other[0];
						coor[1] = 0;
						side = 1;
						break;
					}
				}
				break;
			case 1:
				coor[1] = coor[1]+1;
				if (coor[1] == config.colCount) {
					assert val() == 'J' || val() == 'K';
					int[] other = config.otherJoint(coor);
					int otherSide = config.side(other);
					switch (otherSide) {
					case 0:
						coor[0] = 0;
						coor[1] = other[1];
						side = 2;
						break;
					case 1:
						coor[0] = other[0];
						coor[1] = config.colCount-1;
						side = 3;
						break;
					case 2:
						coor[0] = config.rowCount-1;
						coor[1] = other[1];
						side = 0;
						break;
					case 3:
						coor[0] = other[0];
						coor[1] = 0;
						side = 1;
						break;
					}
				}
				break;
			case 2:
				coor[0] = coor[0]+1;
				if (coor[0] == config.rowCount) {
					assert val() == 'J' || val() == 'K';
					int[] other = config.otherJoint(coor);
					int otherSide = config.side(other);
					switch (otherSide) {
					case 0:
						coor[0] = 0;
						coor[1] = other[1];
						side = 2;
						break;
					case 1:
						coor[0] = other[0];
						coor[1] = config.colCount-1;
						side = 3;
						break;
					case 2:
						coor[0] = config.rowCount-1;
						coor[1] = other[1];
						side = 0;
						break;
					case 3:
						coor[0] = other[0];
						coor[1] = 0;
						side = 1;
						break;
					}
				}
				break;
			case 3:
				coor[1] = coor[1]-1;
				if (coor[1] == -1) {
					assert val() == 'J' || val() == 'K';
					int[] other = config.otherJoint(coor);
					int otherSide = config.side(other);
					switch (otherSide) {
					case 0:
						coor[0] = 0;
						coor[1] = other[1];
						side = 2;
						break;
					case 1:
						coor[0] = other[0];
						coor[1] = config.colCount-1;
						side = 3;
						break;
					case 2:
						coor[0] = config.rowCount-1;
						coor[1] = other[1];
						side = 0;
						break;
					case 3:
						coor[0] = other[0];
						coor[1] = 0;
						side = 1;
						break;
					}
				}
				break;
			}
		}
		
		char val() {
			return config.val(coor);
		}
		
	}
	
	boolean isClearPathToExit() {
		
		Cursor cursor = new Cursor(this);
		while (true) {
			if (cursor.val() == 'Y' || cursor.val() == 'X') {
				cursor.move();
			} else if (cursor.val() == 'R') {
				break;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	int numberMovesToWin() {
		
		Cursor cursor = new Cursor(this); 
		int moves = 0;
		while (true) {
			if (cursor.val() == 'Y' || cursor.val() == 'X') {
				cursor.move();
				moves++;
			} else if (cursor.val() == 'R') {
				break;
			} else {
				assert false;
			}
		}
		
		return moves;
	}
	
	private void clear(int r, int c) {
		if (equals(r, c, exit)) {
			ini[r+1][c+1] = 'Y';
		} else {
			ini[r+1][c+1] = 'X';
		}
	}
	
	public boolean insert(char c, Orientation o, int size, int row, int col) {
		
		if (carMap.containsKey(c)) {
			return false;
		}
		
		if (c == 'R') {
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
		} else {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					if (!isX(ini[row+1][col+1])) return false;
					if (!isX(ini[row+1][col+2])) return false;
					break;
				case 3:
					if (!isX(ini[row+1][col+1])) return false;
					if (!isX(ini[row+1][col+2])) return false;
					if (!isX(ini[row+1][col+3])) return false;
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					if (!isX(ini[row+1][col+1])) return false;
					if (!isX(ini[row+2][col+1])) return false;
					break;
				case 3:
					if (!isX(ini[row+1][col+1])) return false;
					if (!isX(ini[row+2][col+1])) return false;
					if (!isX(ini[row+3][col+1])) return false;
					break;
				}
				break;
			}
		}
		
		CarInfo info = new CarInfo();
		info.o = o;
		info.size = size;
		info.row = row;
		info.col = col;
		carMap.put(c, info);
		
		if (c == 'R') {
			switch (o) {
			case LEFTRIGHT:
				switch (size) {
				case 2:
					
					assert winnableRows[row];
					
					ini[row+1][col+1] = c;
					ini[row+1][col+2] = c;
					break;
				case 3:
					
					assert winnableRows[row];
					
					ini[row+1][col+1] = c;
					ini[row+1][col+2] = c;
					ini[row+1][col+3] = c;
					break;
				}
				break;
			case UPDOWN:
				switch (size) {
				case 2:
					
					assert winnableCols[col];
					
					ini[row+1][col+1] = c;
					ini[row+2][col+1] = c;
					break;
				case 3:
					
					assert winnableCols[col];
					
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
	
	boolean isX(char c) {
		return c == 'X';
	}
	
	boolean isXorY(char c) {
		return c == 'X' || c == 'Y';
	}
	
	public List<Config> possibleMoves() {
		if (possibleMoves == null) {
			computePossibleMoves();
		}
		return possibleMoves;
	}
	
	private void computePossibleMoves() {
		
		List<Config> moves = new ArrayList<Config>();
		
		if (isWinning()) {
			possibleMoves = moves;
			return;
		}
		
		if (isClearPathToExit()) {
			
			int currentMovesToWin = numberMovesToWin();
			Config best = null;
			
			CarInfo info = carMap.get('R');
			switch (info.o) {
			case LEFTRIGHT:
				if (ini[info.row+1][info.col-1+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row, info.col-1);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col+2+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row, info.col+1);

					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col-1+1] == 'J') {
					int matchingJ = matchingJRow(info.row, info.col-1);
					Config newConfig = tryJoint('R', jJoints, matchingJ);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col+2+1] == 'J') {
					int matchingJ = matchingJRow(info.row, info.col+2);
					Config newConfig = tryJoint('R', jJoints, matchingJ);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col-1+1] == 'K') {
					int matchingK = matchingKRow(info.row, info.col-1);
					Config newConfig = tryJoint('R', kJoints, matchingK);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col+2+1] == 'K') {
					int matchingK = matchingKRow(info.row, info.col+2);
					Config newConfig = tryJoint('R', kJoints, matchingK);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col-1+1] == 'Y') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row, info.col-1);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+1][info.col+2+1] == 'Y') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row, info.col+1);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				break;
			case UPDOWN:
				if (ini[info.row-1+1][info.col+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row-1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+2+1][info.col+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row+1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row-1+1][info.col+1] == 'J') {
					int matchingJ = matchingJRow(info.row-1, info.col);
					Config newConfig = tryJoint('R', jJoints, matchingJ);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+2+1][info.col+1] == 'J') {
					int matchingJ = matchingJRow(info.row+2, info.col);
					Config newConfig = tryJoint('R', jJoints, matchingJ);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row-1+1][info.col+1] == 'K') {
					int matchingK = matchingKRow(info.row-1, info.col);
					Config newConfig = tryJoint('R', kJoints, matchingK);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+2+1][info.col+1] == 'K') {
					int matchingK = matchingKRow(info.row+2, info.col);
					Config newConfig = tryJoint('R', kJoints, matchingK);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row-1+1][info.col+1] == 'Y') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row-1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				if (ini[info.row+2+1][info.col+1] == 'Y') {
					Config newConfig = copy();
					newConfig.clear('R');
					newConfig.insert('R', info.o, info.size, info.row+1, info.col);
					
					if (newConfig.numberMovesToWin() < currentMovesToWin) {
						best = newConfig;
					}
				}
				break;
			}
			
			assert best != null;
			
			moves.add(best);
			
			return;
		}
		
		for (Entry<Character, CarInfo> entry : carMap.entrySet()) {
			char c = entry.getKey();
			CarInfo info = entry.getValue();
			switch (info.o) {
			case LEFTRIGHT:
				switch (info.size) {
				case 2:
					if (ini[info.row+1][info.col-1+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row, info.col-1);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+2+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row, info.col+1);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col-1+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col-1);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+2+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col+2);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col-1+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col-1);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+2+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col+2);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					if (c == 'R') {
						if (ini[info.row+1][info.col-1+1] == 'Y') {
							Config newConfig = copy();
							newConfig.clear(c);
							newConfig.insert(c, info.o, info.size, info.row, info.col-1);
							moves.add(newConfig);
						}
						if (ini[info.row+1][info.col+2+1] == 'Y') {
							Config newConfig = copy();
							newConfig.clear(c);
							newConfig.insert(c, info.o, info.size, info.row, info.col+1);
							moves.add(newConfig);
						}
					}
					break;
				case 3:
					if (ini[info.row+1][info.col-1+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row, info.col-1);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+3+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row, info.col+1);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col-1+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col-1);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+3+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col+3);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col-1+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col-1);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+3+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col+3);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					break;
				}
				break;
			case UPDOWN:
				switch (info.size) {
				case 2:
					if (ini[info.row-1+1][info.col+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row-1, info.col);
						moves.add(newConfig);
					}
					if (ini[info.row+2+1][info.col+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row+1, info.col);
						moves.add(newConfig);
					}
					if (ini[info.row-1+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row-1, info.col);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row+2+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row+2, info.col);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row-1+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row-1, info.col);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					if (ini[info.row+2+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row+2, info.col);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					if (c == 'R') {
						if (ini[info.row-1+1][info.col+1] == 'Y') {
							Config newConfig = copy();
							newConfig.clear(c);
							newConfig.insert(c, info.o, info.size, info.row-1, info.col);
							moves.add(newConfig);
						}
						if (ini[info.row+2+1][info.col+1] == 'Y') {
							Config newConfig = copy();
							newConfig.clear(c);
							newConfig.insert(c, info.o, info.size, info.row+1, info.col);
							moves.add(newConfig);
						}
					}
					break;
				case 3:
					if (ini[info.row-1+1][info.col+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row-1, info.col);
						moves.add(newConfig);
					}
					if (ini[info.row+3+1][info.col+1] == 'X') {
						Config newConfig = copy();
						newConfig.clear(c);
						newConfig.insert(c, info.o, info.size, info.row+1, info.col);
						moves.add(newConfig);
					}
					if (ini[info.row-1+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row-1, info.col);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row+3+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row+3, info.col);
						Config newConfig = tryJoint(c, jJoints, matchingJ);
						moves.add(newConfig);
					}
					if (ini[info.row-1+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row-1, info.col);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					if (ini[info.row+3+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row+3, info.col);
						Config newConfig = tryJoint(c, kJoints, matchingK);
						moves.add(newConfig);
					}
					break;
				}
				break;
			}
		}
		
		possibleMoves = moves;
	}
	
	public List<Config> possibleRedCarPlacements() {
		
		List<Config> placements = new ArrayList<Config>();
		
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < colCount-1; c++) {
				
				if (!winnableRows[r]) {
					continue;
				}
				
				Config n = copy();
				boolean res = n.insert('R', Orientation.LEFTRIGHT, 2, r, c);
				if (res) {
					placements.add(n);
				}
			}
		}
		
		for (int r = 0; r < rowCount-1; r++) {
			for (int c = 0; c < colCount; c++) {
				
				if (!winnableCols[c]) {
					continue;
				}
				
				Config n = copy();
				boolean res = n.insert('R', Orientation.UPDOWN, 2, r, c);
				if (res) {
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public List<Config> possible2CarPlacements() {
		
		char car = ' ';
		for (char d : cars) {
			if (!carMap.containsKey(d)) {
				car = d;
				break;
			}
		}
		assert car != ' ';
		
		List<Config> placements = new ArrayList<Config>();
		
		/*
		 * left-right
		 */
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < colCount-1; c++) {
				
				if (!isInterfereRow(r)) {
					continue;
				}
				
				Config n = copy();
				boolean res = n.insert(car, Orientation.LEFTRIGHT, 2, r, c);
				if (res) {
					placements.add(n);
				}
			}
		}
		
		/*
		 * up-down
		 */
		for (int r = 0; r < rowCount-1; r++) {
			for (int c = 0; c < colCount; c++) {
				
				if (!isInterfereCol(c)) {
					continue;
				}
				
				Config n = copy();
				boolean res = n.insert(car, Orientation.UPDOWN, 2, r, c);
				if (res) {
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	public List<Config> possible3CarPlacements() {
		
		char car = ' ';
		for (char d : cars) {
			if (!carMap.containsKey(d)) {
				car = d;
				break;
			}
		}
		assert car != ' ';
		
		List<Config> placements = new ArrayList<Config>();
		
		/*
		 * left-right
		 */
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < colCount-2; c++) {
				
				if (!isInterfereRow(r)) {
					continue;
				}
				
				Config n = copy();
				boolean res = n.insert(car, Orientation.LEFTRIGHT, 3, r, c);
				if (res) {
					placements.add(n);
				}
			}
		}
		
		/*
		 * up-down
		 */
		for (int r = 0; r < rowCount-2; r++) {
			for (int c = 0; c < colCount; c++) {
				
				if (!isInterfereCol(c)) {
					continue;
				}
				
				Config n = copy();
				boolean res = n.insert(car, Orientation.UPDOWN, 3, r, c);
				if (res) {
					placements.add(n);
				}
			}
		}
		
		return placements;
	}
	
	Config tryJoint(char c, int[][] joints, int j) {
		
		int mR = joints[j][0];
		int mC = joints[j][1];
		
		CarInfo info = carMap.get(c);
		
		int mSide = side(joints[j]);
		switch (mSide) {
		case 0:
			switch (info.size) {
			case 2:
				if (ini[mR+1+1][mC+1] == 'X' && ini[mR+1+2][mC+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.UPDOWN, 2, mR+1, mC);
					return newConfig;
				}
				break;
			case 3:
				if (ini[mR+1+1][mC+1] == 'X' && ini[mR+1+2][mC+1] == 'X' && ini[mR+1+3][mC+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.UPDOWN, 3, mR+1, mC);
					return newConfig;
				}
				break;
			}
			break;
		case 1:
			switch (info.size) {
			case 2:
				if (ini[mR+1][mC-2+1] == 'X' && ini[mR+1][mC-1+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.LEFTRIGHT, 2, mR, mC-2);
					return newConfig;
				}
				break;
			case 3:
				if (ini[mR+1][mC-3+1] == 'X' && ini[mR+1][mC-2+1] == 'X' && ini[mR+1][mC-1+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.LEFTRIGHT, 3, mR, mC-3);
					return newConfig;
				}
				break;
			}
			break;
		case 2:
			switch (info.size) {
			case 2:
				if (ini[mR-2+1][mC+1] == 'X' && ini[mR-1+1][mC+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.UPDOWN, 2, mR-2, mC);
					return newConfig;
				}
				break;
			case 3:
				if (ini[mR-3+1][mC+1] == 'X' && ini[mR-2+1][mC+1] == 'X' && ini[mR-1+1][mC+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.UPDOWN, 3, mR-3, mC);
					return newConfig;
				}
				break;
			}
			break;
		case 3:
			switch (info.size) {
			case 2:
				if (ini[mR+1][mC+1+1] == 'X' && ini[mR+1][mC+2+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.LEFTRIGHT, 2, mR, mC+1);
					return newConfig;
				}
				break;
			case 3:
				if (ini[mR+1][mC+1+1] == 'X' && ini[mR+1][mC+2+1] == 'X' && ini[mR+1][mC+3+1] == 'X') {
					Config newConfig = copy();
					newConfig.clear(c);
					newConfig.insert(c, Orientation.LEFTRIGHT, 3, mR, mC+1);
					return newConfig;
				}
				break;
			}
			break;
		}
		
		assert false;
		return null;
	}
	
	public static Config randomBlankConfig() {
		
		char[][] ini = new char[8][8];
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini.length; j++) {
				if (i == 0 || i == ini.length-1 || j == 0 || j == ini[0].length-1) {
					ini[i][j] = ' ';
				} else {
					ini[i][j] = 'X';
				}
			}
		}
		
		Config c = new Config(ini);
		
		int exitSide = rand.nextInt(4);
		int exitRow = -1;
		int exitCol = -1;
		switch (exitSide) {
		case 0:
			exitRow = -1;
			exitCol = rand.nextInt(ini[0].length-2);
			break;
		case 1:
			exitRow = rand.nextInt(ini.length-2);
			exitCol = ini[0].length-2;
			break;
		case 2:
			exitRow = ini.length-2;
			exitCol = rand.nextInt(ini[0].length-2);
			break;
		case 3:
			exitRow = rand.nextInt(ini[0].length-2);
			exitCol = -1;
			break;
		}
		
		c.setExit(exitRow, exitCol);
		
		if (rand.nextBoolean()) {
			/*
			 * j joints
			 */
			int jside0 = rand.nextInt(4);
			while (jside0 == exitSide) {
				jside0 = rand.nextInt(4);
			}
			
			int jside1 = rand.nextInt(4);
			while (jside1 == exitSide || jside1 == jside0 || jside1 == ((jside0 + 2) % 4)) {
				jside1 = rand.nextInt(4);
			}
			
			int js0R = -1;
			int js0C = -1;
			switch (jside0) {
			case 0:
				js0R = -1;
				js0C = rand.nextInt(ini[0].length-2);
				break;
			case 1:
				js0R = rand.nextInt(ini.length-2);
				js0C = ini[0].length-2;
				break;
			case 2:
				js0R = ini.length-2;
				js0C = rand.nextInt(ini[0].length-2);
				break;
			case 3:
				js0R = rand.nextInt(ini[0].length-2);
				js0C = -1;
				break;
			}
			
			int js1R = -1;
			int js1C = -1;
			switch (jside1) {
			case 0:
				js1R = -1;
				js1C = rand.nextInt(ini[0].length-2);
				break;
			case 1:
				js1R = rand.nextInt(ini.length-2);
				js1C = ini[0].length-2;
				break;
			case 2:
				js1R = ini.length-2;
				js1C = rand.nextInt(ini[0].length-2);
				break;
			case 3:
				js1R = rand.nextInt(ini[0].length-2);
				js1C = -1;
				break;
			}
			
			c.jJoints[0][0] = js0R;
			c.jJoints[0][1] = js0C;
			c.jJoints[0][2] = jside0;
			c.ini[js0R+1][js0C+1] = 'J';
			
			c.jJoints[1][0] = js1R;
			c.jJoints[1][1] = js1C;
			c.jJoints[1][2] = jside1;
			c.ini[js1R+1][js1C+1] = 'J';
			
			c.jCount = 2;
			
			if (rand.nextBoolean()) {
				/*
				 * k joints
				 */
				int kside0 = rand.nextInt(4);
				while (true) {
					if (kside0 == exitSide) {
						
					} else {
						break;
					}
					kside0 = rand.nextInt(4);
				}
				
				int kside1 = rand.nextInt(4);
				while (true) {
					if (kside1 == exitSide) {
						
					} else if (kside1 == kside0) {
						
					} else if (kside1 == ((kside0 + 2) % 4)) {
						
					} else {
						break;
					}
					kside1 = rand.nextInt(4);
				}
				
				int ks0R = -1;
				int ks0C = -1;
				switch (kside0) {
				case 0:
					ks0R = -1;
					while (true) {
						ks0C = rand.nextInt(ini[0].length-2);
						if (kside0 == jside0) {
							if (ks0C == js0C || ks0C == js0C+1 || ks0C == js0C-1) {
								
							} else {
								break;
							}
						} else if (kside0 == jside1) {
							if (ks0C == js1C || ks0C == js1C+1 || ks0C == js1C-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					break;
				case 1:
					while (true) {
						ks0R = rand.nextInt(ini.length-2);
						if (kside0 == jside0) {
							if (ks0R == js0R || ks0R == js0R+1 || ks0R == js0R-1) {
								
							} else {
								break;
							}
						} else if (kside0 == jside1) {
							if (ks0R == js1R || ks0R == js1R+1 || ks0R == js1R-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					ks0C = ini[0].length-2;
					break;
				case 2:
					ks0R = ini.length-2;
					while (true) {
						ks0C = rand.nextInt(ini[0].length-2);
						if (kside0 == jside0) {
							if (ks0C == js0C || ks0C == js0C+1 || ks0C == js0C-1) {
								
							} else {
								break;
							}
						} else if (kside0 == jside1) {
							if (ks0C == js1C || ks0C == js1C+1 || ks0C == js1C-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					break;
				case 3:
					while (true) {
						ks0R = rand.nextInt(ini.length-2);
						if (kside0 == jside0) {
							if (ks0R == js0R || ks0R == js0R+1 || ks0R == js0R-1) {
								
							} else {
								break;
							}
						} else if (kside0 == jside1) {
							if (ks0R == js1R || ks0R == js1R+1 || ks0R == js1R-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					ks0C = -1;
					break;
				}
				
				int ks1R = -1;
				int ks1C = -1;
				switch (kside1) {
				case 0:
					ks1R = -1;
					while (true) {
						ks1C = rand.nextInt(ini[0].length-2);
						if (kside1 == jside0) {
							if (ks1C == js0C || ks1C == js0C+1 || ks1C == js0C-1) {
								
							} else {
								break;
							}
						} else if (kside1 == jside1) {
							if (ks1C == js1C || ks1C == js1C+1 || ks1C == js1C-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					break;
				case 1:
					while (true) {
						ks1R = rand.nextInt(ini.length-2);
						if (kside1 == jside0) {
							if (ks1R == js0R || ks1R == js0R+1 || ks1R == js0R-1) {
								
							} else {
								break;
							}
						} else if (kside1 == jside1) {
							if (ks1R == js1R || ks1R == js1R+1 || ks1R == js1R-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					ks1C = ini[0].length-2;
					break;
				case 2:
					ks1R = ini.length-2;
					while (true) {
						ks1C = rand.nextInt(ini[0].length-2);
						if (kside1 == jside0) {
							if (ks1C == js0C || ks1C == js0C+1 || ks1C == js0C-1) {
								
							} else {
								break;
							}
						} else if (kside1 == jside1) {
							if (ks1C == js1C || ks1C == js1C+1 || ks1C == js1C-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					break;
				case 3:
					while (true) {
						ks1R = rand.nextInt(ini.length-2);
						if (kside1 == jside0) {
							if (ks1R == js0R || ks1R == js0R+1 || ks1R == js0R-1) {
								
							} else {
								break;
							}
						} else if (kside1 == jside1) {
							if (ks1R == js1R || ks1R == js1R+1 || ks1R == js1R-1) {
								
							} else {
								break;
							}
						} else {
							break;
						}
					}
					ks1C = -1;
					break;
				}
				
				c.kJoints[0][0] = ks0R;
				c.kJoints[0][1] = ks0C;
				c.kJoints[0][2] = kside0;
				c.ini[ks0R+1][ks0C+1] = 'K';
				
				c.kJoints[1][0] = ks1R;
				c.kJoints[1][1] = ks1C;
				c.kJoints[1][2] = kside1;
				c.ini[ks1R+1][ks1C+1] = 'K';
				
				c.kCount = 2;
				
			}
		}
		
		return c;
	}
	
	public String toString() {
		
		StringBuilder b = new StringBuilder();
		for (char[] i : ini) {
			b.append(new String(i));
			b.append('\n');
		}
		return b.toString();
	};
	
}
