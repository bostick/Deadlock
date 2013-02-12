package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import solver.Solver.Orientation;

public class Config {
	
	public final int rowCount;
	public final int colCount;
	
	private char[][] ini = new char[][] {
			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}};
	
	private Map<Character, CarInfo> carMap = new HashMap<Character, CarInfo>();
	
	private int[] exit = new int[]{ -1, -1 };
	
	int jCount = 0;
	private int[][] jJoints = new int[][]{ {-1, -1, -1}, {-1, -1, -1} };
	
	int kCount = 0;
	private int[][] kJoints = new int[][]{ {-1, -1, -1}, {-1, -1, -1} };
	
	Set<Character> carChars = new HashSet<Character>();
	
	boolean winning;
	
	public Config(char[][] boardIni) {
		this.rowCount = boardIni.length-2;
		this.colCount = boardIni[0].length-2;
		
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
					if (!carChars.contains(c)) {
						carChars.add(c);
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
						update(c, info);
					}
					break;
				case 'Y':
					exit[0] = i-1;
					exit[1] = j-1;
					ini[i][j] = 'Y';
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
		
		assert exit[0] != -1 && exit[1] != -1;
	}
	
	public boolean equals(Object o) {
		return Arrays.deepEquals(ini, ((Config)o).ini);
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
		
		CarInfo oldInfo = carMap.get(c);
		if (oldInfo != null) {
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
		
	}
	
	private void clear(int r, int c) {
		if (r == exit[0] && c == exit[1]) {
			ini[r+1][c+1] = 'Y';
		} else {
			ini[r+1][c+1] = 'X';
		}
	}
	
	public void update(char c, CarInfo info) {
		
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
	}
	
	public List<Config> possibleMoves() {
		
		List<Config> moves = new ArrayList<Config>();
		
		if (winning) {
			return moves;
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
						newConfig.update(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+2+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.col++;
						newConfig.update(c, newInfo);
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
							newConfig.update(c, newInfo);
							newConfig.winning = true;
							moves.add(newConfig);
						}
						if (ini[info.row+1][info.col+2+1] == 'Y') {
							Config newConfig = copy();
							CarInfo newInfo = newConfig.carMap.get(c);
							newConfig.clear(c);
							newInfo.col++;
							newConfig.update(c, newInfo);
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
						newConfig.update(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+1][info.col+3+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.col++;
						newConfig.update(c, newInfo);
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
						newConfig.update(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+2+1][info.col+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.row++;
						newConfig.update(c, newInfo);
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
							newConfig.update(c, newInfo);
							newConfig.winning = true;
							moves.add(newConfig);
						}
						if (ini[info.row+2+1][info.col+1] == 'Y') {
							Config newConfig = copy();
							CarInfo newInfo = newConfig.carMap.get(c);
							newConfig.clear(c);
							newInfo.row++;
							newConfig.update(c, newInfo);
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
						newConfig.update(c, newInfo);
						moves.add(newConfig);
					}
					if (ini[info.row+3+1][info.col+1] == 'X') {
						Config newConfig = copy();
						CarInfo newInfo = newConfig.carMap.get(c);
						newConfig.clear(c);
						newInfo.row++;
						newConfig.update(c, newInfo);
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
		
		return moves;
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
					newConfig.update(c, newInfo);
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
					newConfig.update(c, newInfo);
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
					newConfig.update(c, newInfo);
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
					newConfig.update(c, newInfo);
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
					newConfig.update(c, newInfo);
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
					newConfig.update(c, newInfo);
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
	
	public String toString() {
		
		StringBuilder b = new StringBuilder();
		for (char[] i : ini) {
			b.append(new String(i));
			b.append('\n');
		}
		return b.toString();
	};
	
}
