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
//	static char[][] boardIni = new char[][] {
//			{' ', ' ', ' ', ' ', 'J', ' ', 'K', ' '},
//			{'J', 'C', 'A', 'A', 'D', 'X', 'B', ' '},
//			{' ', 'C', 'X', 'X', 'D', 'X', 'B', ' '},
//			{' ', 'C', 'R', 'R', 'D', 'X', 'B', 'Y'},
//			{' ', 'E', 'X', 'X', 'G', 'G', 'G', ' '},
//			{' ', 'E', 'X', 'X', 'F', 'X', 'X', ' '},
//			{'K', 'X', 'X', 'X', 'F', 'X', 'X', ' '},
//			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//	};
	static char[][] boardIni = new char[][] {
		{' ', ' ', ' ', ' ', 'J', ' ', ' ', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{'J', 'X', 'X', 'X', 'X', 'X', 'X', 'Y'},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{'K', 'X', 'X', 'X', 'X', 'R', 'R', ' '},
		{' ', ' ', ' ', ' ', 'K', ' ', ' ', ' '},
	};
	
	enum Orientation {
		UPDOWN,
		LEFTRIGHT
	}
	
	
	
	public static void main(String[] args) {
		
		Map<Config, Config> explored = new HashMap<Config, Config>();
		
		Config bestConfig = null;
		int bestConfigMoves = -1;
		
		while (true) {
			Config c = Config.randomConfig();
			int m = movesToWin(c, explored);
			
			if (m != -1 && m > bestConfigMoves) {
				bestConfig = c;
				bestConfigMoves = m;
				System.out.println("moves: " + m);
				System.out.println(bestConfig);
				System.out.println();
			}
			
		}
		
	}
	
	public static Config shortestWinner(Map<Config, Config> explored) {
		
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
		
		return shortestWinner;
		
	}
	
	public static int movesToWin(Config start, Map<Config, Config> explored) {
		
		explore(start, null, explored);
		
//		Config shortestWinner = null;
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
//					shortestWinner = c;
					shortestWinnerLength = length;
				}
				
			}
		}
		
		if (shortestWinnerLength == Integer.MAX_VALUE) {
			return -1;
		} else {
			return shortestWinnerLength-1;
		}
		
	}
	
	public static int distanceToStart(Config c, Map<Config, Config> explored) {
		int dist = 0;
		Config d = c;
		while (true) {
			if (d == null) {
				break;
			}
			
			dist++;
			d = explored.get(d);
			
		}
		return dist;
	}
	
	static public void explore(Config c, Config pred, Map<Config, Config> explored) {
		
		explored.put(c, pred);
		
		List<Config> moves = c.possibleMoves();
		for (Config m : moves) {
			if (!explored.keySet().contains(m)) {
				explore(m, c, explored);
			} else {
				Config currentMPred = explored.get(m);
				if (distanceToStart(c, explored) < distanceToStart(currentMPred, explored)) {
					explore(m, c, explored);
				}
			} 
		}
	}
	
}
