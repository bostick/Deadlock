import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.graph.RushHourBoard;


public class Solver {
	
//	char[][] boardIni = new char[][] {
//			{' ', ' ', ' ', ' ', 'J', ' ', ' ', ' ', ' '},
//			{'J', 'C', 'A', 'A', 'D', 'X', 'B', ' ', ' '},
//			{' ', 'C', 'X', 'X', 'D', 'X', 'B', ' ', ' '},
//			{' ', 'C', 'R', 'R', 'D', 'X', 'B', 'Y', ' '},
//			{' ', 'E', 'X', 'X', 'G', 'G', 'G', ' ', ' '},
//			{' ', 'E', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//			{' ', 'X', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
//	};
	static char[][] boardIni = new char[][] {
			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
			{' ', 'X', 'R', 'R', 'X', 'X', 'X', 'Y', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
	};
	
	enum Orientation {
		UPDOWN,
		LEFTRIGHT
	}
	
	static class Info {
		Orientation o;
		int row;
		int col;
		int size;
	}
	
	public static void main(String[] args) {
		
		RushHourBoard b = new RushHourBoard(null, new Point(0, 0), boardIni);
		
		Set<Character> carChars = new HashSet<Character>();
		Map<Character, Info> infoMap = new HashMap<Character, Info>();
		
		for (int i = b.originRow; i < b.originRow+b.rowCount; i++) {
			for (int j = b.originCol; j < b.originCol+b.colCount; j++) {
				char c = boardIni[i][j];
				if (c != 'X') {
					if (!carChars.contains(c)) {
						carChars.add(c);
						Info info = new Info();
						infoMap.put(c, info);
						if (i+1 < b.rowCount && boardIni[i+1][j] == c) {
							if (i+2 < b.rowCount && boardIni[i+2][j] == c) {
								info.o = Orientation.UPDOWN;
								info.row = i;
								info.col = j;
								info.size = 3;
							} else {
								info.o = Orientation.UPDOWN;
								info.row = i;
								info.col = j;
								info.size = 2;
							}
						} else {
							assert boardIni[i][j+1] == c;
							if (j+2 < b.colCount && boardIni[i][j+2] == c) {
								info.o = Orientation.LEFTRIGHT;
								info.row = i;
								info.col = j;
								info.size = 3;
							} else {
								info.o = Orientation.LEFTRIGHT;
								info.row = i;
								info.col = j;
								info.size = 2;
							}
						}
					}
				}
			}
		}
		
		
		
	}
	
}
