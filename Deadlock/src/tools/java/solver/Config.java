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
	
	public final int rowCount;
	public final int colCount;
	
//	private char[][] ini = new char[][] {
//			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}};
	public char[][] ini;
	
	public Map<Character, CarInfo> carMap = new HashMap<Character, CarInfo>();
	
	private int[] exit = new int[]{ -1, -1 };
	
	private int[][] jJoints = new int[][]{ {-1, -1, -1}, {-1, -1, -1} };
	
	private int[][] kJoints = new int[][]{ {-1, -1, -1}, {-1, -1, -1} };
	
//	Set<Character> carChars = new HashSet<Character>();
	
	boolean winning;
	
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
		
		int jCount = 0;
		int kCount = 0;
		
		ini = new char[boardIni.length][boardIni[0].length];
		for (int i = 0; i < boardIni.length; i++) {
			for (int j = 0; j < boardIni[i].length; j++) {
				if (i == 0 || i == boardIni.length-1 || j == 0 || j == boardIni[0].length-1) {
					ini[i][j] = ' ';
				} else {
					ini[i][j] = 'X';
				}
			}
		}
		
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
				case 'R':
					if (!carMap.keySet().contains(c)) {
						CarInfo info = new CarInfo();
						if (i+1-1 < rowCount && boardIni[i+1][j] == c) {
							if (i+2-1 < rowCount && boardIni[i+2][j] == c) {
								info.o = Orientation.UPDOWN;
								info.row = i-1;
								info.col = j-1;
								info.size = 3;
							} else {
								info.o = Orientation.UPDOWN;
								info.row = i-1;
								info.col = j-1;
								info.size = 2;
							}
						} else {
							assert boardIni[i][j+1] == c;
							if (j+2-1 < colCount && boardIni[i][j+2] == c) {
								info.o = Orientation.LEFTRIGHT;
								info.row = i-1;
								info.col = j-1;
								info.size = 3;
							} else {
								info.o = Orientation.LEFTRIGHT;
								info.row = i-1;
								info.col = j-1;
								info.size = 2;
							}
						}
						boolean res = insert(c, info);
						assert res;
					}
					break;
				case 'Y':
					setExit(i-1, j-1);
					break;
				case 'J':
					jJoints[jCount][0] = i-1;
					jJoints[jCount][1] = j-1;
					if (i == 0) {
						jJoints[jCount][2] = 0;
					} else if (i == ini.length-1) {
						jJoints[jCount][2] = 2;
					} else if (j == 0) {
						jJoints[jCount][2] = 3;
					} else {
						assert j == ini[0].length - 1;
						jJoints[jCount][2] = 1;
					}
					jCount++;
					ini[i][j] = 'J';
					break;
				case 'K':
					kJoints[kCount][0] = i-1;
					kJoints[kCount][1] = j-1;
					if (i == 0) {
						kJoints[kCount][2] = 0;
					} else if (i == ini.length-1) {
						kJoints[kCount][2] = 2;
					} else if (j == 0) {
						kJoints[kCount][2] = 3;
					} else {
						assert j == ini[0].length - 1;
						kJoints[kCount][2] = 1;
					}
					kCount++;
					ini[i][j] = 'K';
					break;
				}
			}
		}
		
//		assert exit[0] != -1 && exit[1] != -1;
	}
	
	public boolean equals(Object o) {
		boolean res = Arrays.deepEquals(ini, ((Config)o).ini);
		if (res) {
			String s = toString();
			String t = ((Config)o).toString();
			assert s.equals(t);
			return true;
		} else {
			String s = toString();
			String t = ((Config)o).toString();
			assert !s.equals(t);
			return false;
		}
	}
	
	public int hashCode() {
		return Arrays.deepHashCode(ini);
	}
	
	public Config copy() {
		assert !winning;
		char[][] newIni = new char[ini.length][ini[0].length];
		for (int i = 0; i < newIni.length; i++) {
			for (int j = 0; j < newIni[i].length; j++) {
				newIni[i][j] = ini[i][j];
			}
		}
		return new Config(newIni);
	}
	
	public void clear(char c) {
		
		CarInfo oldInfo = carMap.remove(c);
		switch (oldInfo.o) {
		case LEFTRIGHT:
			switch (oldInfo.size) {
			case 2:
				clearIni(oldInfo.row, oldInfo.col+0);
				clearIni(oldInfo.row, oldInfo.col+1);
				break;
			case 3:
				clearIni(oldInfo.row, oldInfo.col+0);
				clearIni(oldInfo.row, oldInfo.col+1);
				clearIni(oldInfo.row, oldInfo.col+2);
				break;
			}
			break;
		case UPDOWN:
			switch (oldInfo.size) {
			case 2:
				clearIni(oldInfo.row+0, oldInfo.col);
				clearIni(oldInfo.row+1, oldInfo.col);
				break;
			case 3:
				clearIni(oldInfo.row+0, oldInfo.col);
				clearIni(oldInfo.row+1, oldInfo.col);
				clearIni(oldInfo.row+2, oldInfo.col);
				break;
			}
			break;
		}
		
	}
	
	public void setExit(int i, int j) {
		exit[0] = i;
		exit[1] = j;
		ini[i+1][j+1] = 'Y';
	}
	
	private void clearIni(int r, int c) {
		if (r == exit[0] && c == exit[1]) {
			ini[r+1][c+1] = 'Y';
		} else {
			ini[r+1][c+1] = 'X';
		}
	}
	
	public boolean insert(char c, CarInfo info) {
		
		if (carMap.containsKey(c)) {
			return false;
		}
		
		if (c == 'R') {
			switch (info.o) {
			case LEFTRIGHT:
				switch (info.size) {
				case 2:
					if (!isXorY(ini[info.row+1][info.col+1])) return false;
					if (!isXorY(ini[info.row+1][info.col+2])) return false;
					break;
				case 3:
					if (!isXorY(ini[info.row+1][info.col+1])) return false;
					if (!isXorY(ini[info.row+1][info.col+2])) return false;
					if (!isXorY(ini[info.row+1][info.col+3])) return false;
					break;
				}
				break;
			case UPDOWN:
				switch (info.size) {
				case 2:
					if (!isXorY(ini[info.row+1][info.col+1])) return false;
					if (!isXorY(ini[info.row+2][info.col+1])) return false;
					break;
				case 3:
					if (!isXorY(ini[info.row+1][info.col+1])) return false;
					if (!isXorY(ini[info.row+2][info.col+1])) return false;
					if (!isXorY(ini[info.row+3][info.col+1])) return false;
					break;
				}
				break;
			}
		} else {
			switch (info.o) {
			case LEFTRIGHT:
				switch (info.size) {
				case 2:
					if (!isX(ini[info.row+1][info.col+1])) return false;
					if (!isX(ini[info.row+1][info.col+2])) return false;
					break;
				case 3:
					if (!isX(ini[info.row+1][info.col+1])) return false;
					if (!isX(ini[info.row+1][info.col+2])) return false;
					if (!isX(ini[info.row+1][info.col+3])) return false;
					break;
				}
				break;
			case UPDOWN:
				switch (info.size) {
				case 2:
					if (!isX(ini[info.row+1][info.col+1])) return false;
					if (!isX(ini[info.row+2][info.col+1])) return false;
					break;
				case 3:
					if (!isX(ini[info.row+1][info.col+1])) return false;
					if (!isX(ini[info.row+2][info.col+1])) return false;
					if (!isX(ini[info.row+3][info.col+1])) return false;
					break;
				}
				break;
			}
		}
		
		carMap.put(c, info);
		
		switch (info.o) {
		case LEFTRIGHT:
			switch (info.size) {
			case 2:
				ini[info.row+1][info.col+1] = c;
				ini[info.row+1][info.col+2] = c;
				break;
			case 3:
				ini[info.row+1][info.col+1] = c;
				ini[info.row+1][info.col+2] = c;
				ini[info.row+1][info.col+3] = c;
				break;
			}
			break;
		case UPDOWN:
			switch (info.size) {
			case 2:
				ini[info.row+1][info.col+1] = c;
				ini[info.row+2][info.col+1] = c;
				break;
			case 3:
				ini[info.row+1][info.col+1] = c;
				ini[info.row+2][info.col+1] = c;
				ini[info.row+3][info.col+1] = c;
				break;
			}
			break;
		}
		return true;
	}
	
	boolean isX(char c) {
		return c == 'X';
	}
	
	boolean isXorY(char c) {
		return c == 'X' || c == 'Y';
	}
	
	
	
	List<Config> possibleMoves;
	
	public List<Config> possibleMoves() {
		if (possibleMoves == null) {
			computePossibleMoves();
		}
		return possibleMoves;
	}
	
	public void computePossibleMoves() {
		
		List<Config> moves = new ArrayList<Config>();
		
		if (winning) {
			possibleMoves = moves;
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
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.col--;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+2+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.col++;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col-1+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col-1);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row+1][info.col+2+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col+2);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row+1][info.col-1+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col-1);
						tryJoint(c, kJoints, matchingK, moves);
					}
					if (ini[info.row+1][info.col+2+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col+2);
						tryJoint(c, kJoints, matchingK, moves);
					}
					if (c == 'R') {
						if (ini[info.row+1][info.col-1+1] == 'Y') {
							Config newConfig = copy();
							CarInfo newInfo = newConfig.carMap.get(c);
							newConfig.clear(c);
							newInfo.col--;
							newConfig.insert(c, newInfo);
							newConfig.winning = true;
							moves.add(newConfig);
						}
						if (ini[info.row+1][info.col+2+1] == 'Y') {
							Config newConfig = copy();
							CarInfo newInfo = newConfig.carMap.get(c);
							newConfig.clear(c);
							newInfo.col++;
							newConfig.insert(c, newInfo);
							newConfig.winning = true;
							moves.add(newConfig);
						}
					}
					break;
				case 3:
					if (ini[info.row+1][info.col-1+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.col--;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+3+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.col++;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col-1+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col-1);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row+1][info.col+3+1] == 'J') {
						int matchingJ = matchingJRow(info.row, info.col+3);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row+1][info.col-1+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col-1);
						tryJoint(c, kJoints, matchingK, moves);
					}
					if (ini[info.row+1][info.col+3+1] == 'K') {
						int matchingK = matchingKRow(info.row, info.col+3);
						tryJoint(c, kJoints, matchingK, moves);
					}
					break;
				}
				break;
			case UPDOWN:
				switch (info.size) {
				case 2:
					if (ini[info.row-1+1][info.col+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.row--;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+2+1][info.col+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.row++;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row-1+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row-1, info.col);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row+2+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row+2, info.col);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row-1+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row-1, info.col);
						tryJoint(c, kJoints, matchingK, moves);
					}
					if (ini[info.row+2+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row+2, info.col);
						tryJoint(c, kJoints, matchingK, moves);
					}
					if (c == 'R') {
						if (ini[info.row-1+1][info.col+1] == 'Y') {
							Config newConfig = copy();
							CarInfo newInfo = newConfig.carMap.get(c);
							newConfig.clear(c);
							newInfo.row--;
							newConfig.insert(c, newInfo);
							newConfig.winning = true;
							moves.add(newConfig);
						}
						if (ini[info.row+2+1][info.col+1] == 'Y') {
							Config newConfig = copy();
							CarInfo newInfo = newConfig.carMap.get(c);
							newConfig.clear(c);
							newInfo.row++;
							newConfig.insert(c, newInfo);
							newConfig.winning = true;
							moves.add(newConfig);
						}
					}
					break;
				case 3:
					if (ini[info.row-1+1][info.col+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.row--;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+3+1][info.col+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.row++;
						newConfig.insert(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row-1+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row-1, info.col);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row+3+1][info.col+1] == 'J') {
						int matchingJ = matchingJRow(info.row+3, info.col);
						tryJoint(c, jJoints, matchingJ, moves);
					}
					if (ini[info.row-1+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row-1, info.col);
						tryJoint(c, kJoints, matchingK, moves);
					}
					if (ini[info.row+3+1][info.col+1] == 'K') {
						int matchingK = matchingKRow(info.row+3, info.col);
						tryJoint(c, kJoints, matchingK, moves);
					}
					break;
				}
				break;
			}
		}
		
		possibleMoves = moves;
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
	
	void tryJoint(char c, int[][] joints, int j, List<Config> moves) {
		
		int mR = joints[j][0];
		int mC = joints[j][1];
		int mSide = joints[j][2];
		
		CarInfo info = carMap.get(c);
		
		switch (mSide) {
		case 0:
			switch (info.size) {
			case 2:
				if (ini[mR+1+1][mC+1] == 'X' && ini[mR+1+2][mC+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR+1;
					newInfo.col = mC;
					newInfo.o = Orientation.UPDOWN;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			case 3:
				if (ini[mR+1+1][mC+1] == 'X' && ini[mR+1+2][mC+1] == 'X' && ini[mR+1+3][mC+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR+1;
					newInfo.col = mC;
					newInfo.o = Orientation.UPDOWN;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			default:
				assert false;
				break;
			}
			break;
		case 1:
			switch (info.size) {
			case 2:
				if (ini[mR+1][mC-2+1] == 'X' && ini[mR+1][mC-1+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR;
					newInfo.col = mC-2;
					newInfo.o = Orientation.LEFTRIGHT;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			case 3:
				if (ini[mR+1][mC-3+1] == 'X' && ini[mR+1][mC-2+1] == 'X' && ini[mR+1][mC-1+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR;
					newInfo.col = mC-3;
					newInfo.o = Orientation.LEFTRIGHT;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			default:
				assert false;
				break;
			}
			break;
		case 2:
			switch (info.size) {
			case 2:
				if (ini[mR-2+1][mC+1] == 'X' && ini[mR-1+1][mC+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR-2;
					newInfo.col = mC;
					newInfo.o = Orientation.UPDOWN;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			case 3:
				if (ini[mR-3+1][mC+1] == 'X' && ini[mR-2+1][mC+1] == 'X' && ini[mR-1+1][mC+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR-3;
					newInfo.col = mC;
					newInfo.o = Orientation.UPDOWN;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			default:
				assert false;
				break;
			}
			break;
		case 3:
			switch (info.size) {
			case 2:
				if (ini[mR+1][mC+1+1] == 'X' && ini[mR+1][mC+2+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR;
					newInfo.col = mC+1;
					newInfo.o = Orientation.LEFTRIGHT;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			case 3:
				if (ini[mR+1][mC+1+1] == 'X' && ini[mR+1][mC+2+1] == 'X' && ini[mR+1][mC+3+1] == 'X') {
					Config newConfig = copy();
					CarInfo newInfo = newConfig.carMap.get(c);
					newConfig.clear(c);
					newInfo.row = mR;
					newInfo.col = mC+1;
					newInfo.o = Orientation.LEFTRIGHT;
					newConfig.insert(c, newInfo);
					moves.add(newConfig);
				}
				break;
			default:
				assert false;
				break;
			}
			break;
		default:
			assert false;
			break;
		}
		
	}
	
	
	
	boolean isWinnableSet;
	boolean isWinnable;
	
	public boolean isWinnable() {
		if (!isWinnableSet) {
			if (winning) {
				isWinnable = true;
				isWinnableSet = true;
				return true;
			}
			List<Config> possibleMoves = possibleMoves();
			for (Config c : possibleMoves) {
				if (c.isWinnable()) {
					isWinnable = true;
					isWinnableSet = true;
					return true;
				}
			}
			isWinnable = false;
			isWinnableSet = true;
			return false;
		}
		return isWinnable;
	}
	
	public static Config randomConfig() {
		
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
		
		Random rand = new Random();
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
		
		/*
		 * red car
		 */
		CarInfo redInfo = new CarInfo();
		redInfo.size = 2;
		int redR;
		int redC;
		int redOrientation = rand.nextInt(2);
		if (redOrientation == 0) {
			redInfo.o = Orientation.LEFTRIGHT;
			redR = rand.nextInt(ini.length-2);
			redC = rand.nextInt(ini[0].length-3);
		} else {
			redInfo.o = Orientation.UPDOWN;
			redR = rand.nextInt(ini.length-3);
			redC = rand.nextInt(ini[0].length-2);
		}
		redInfo.row = redR;
		redInfo.col = redC;
		c.insert('R', redInfo);
		
		
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
			}
		}
		
//		char[] cars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G'};
		char[] cars = new char[] { 'A', 'B'};
//		int currentCar = 0;
		for (int currentCar = 0; currentCar < cars.length; currentCar++) {
//			if (rand.nextBoolean()) {
//				break;
//			}
//			if (currentCar == cars.length) {
//				break;
//			}
			for (int i = 0; i < 1000; i++) {
				CarInfo info = new CarInfo();
				info.size = rand.nextInt(2)+2;
				int row;
				int col;
				int o = rand.nextInt(2);
				if (o == 0) {
					info.o = Orientation.LEFTRIGHT;
					row = rand.nextInt(ini.length-2);
					col = rand.nextInt(ini[0].length-3);
				} else {
					info.o = Orientation.UPDOWN;
					row = rand.nextInt(ini.length-3);
					col = rand.nextInt(ini[0].length-2);
				}
				info.row = row;
				info.col = col;
				boolean res = c.insert(cars[currentCar], info);
				if (res) {
					break;
				}
			}
//			currentCar++;
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
