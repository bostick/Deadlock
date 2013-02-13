package solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
//	static char[][] boardIni = new char[][] {
//			{' ', ' ', ' ', ' ', 'J', ' ', ' ', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{'J', 'X', 'R', 'R', 'X', 'X', 'X', 'Y'},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{'K', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', ' ', ' ', ' ', 'K', ' ', ' ', ' '}
//	};
	static char[][] boardIni = new char[][] {
			{' ', ' ', ' ', ' ', 'J', ' ', 'K', ' '},
			{'J', 'C', 'A', 'A', 'D', 'X', 'B', ' '},
			{' ', 'C', 'X', 'X', 'D', 'X', 'B', ' '},
			{' ', 'C', 'R', 'R', 'D', 'X', 'B', 'Y'},
			{' ', 'E', 'X', 'X', 'G', 'G', 'G', ' '},
			{' ', 'E', 'X', 'X', 'F', 'X', 'X', ' '},
			{'K', 'X', 'X', 'X', 'F', 'X', 'X', ' '},
			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
	};
	
	enum Orientation {
		UPDOWN,
		LEFTRIGHT
	}
	
	
	
	static Map<Config, Config> explored = new HashMap<Config, Config>();
	
	public static void main(String[] args) {
		
		Config start = new Config(boardIni);
		
		explore(start, null);
		
//		System.out.println(explored.size());
		
		Config shortestWinner = null;
		int shortestWinnerLength = Integer.MAX_VALUE;
		for (Config c : explored.keySet()) {
			if (c.winning) {
				
				Config d = c;
				int length = 0;
				while (d != null) {
					d = explored.get(d);
					length++;
				}
				
				if (length < shortestWinnerLength) {
					shortestWinner = c;
					shortestWinnerLength = length;
				}
				
			}
		}
		
		Config d = shortestWinner;
		System.out.println(d);
		while (d != null) {
			d = explored.get(d);
			System.out.println(d);
		}
		
	}
	
	static int distanceToStart(Config c, Map<Config, Config> explored) {
		if (c == null) {
			return 0;
		}
		return distanceToStart(explored.get(c), explored) + 1;
	}
	
	static public void explore(Config c, Config pred) {
		
		explored.put(c, pred);
		
		List<Config> moves = c.possibleMoves();
		for (Config m : moves) {
			if (!explored.keySet().contains(m)) {
				explore(m, c);
			} else {
				Config currentMPred = explored.get(m);
				if (distanceToStart(c, explored) < distanceToStart(currentMPred, explored)) {
					explore(m, c);
				}
			} 
		}
	}
	
}
