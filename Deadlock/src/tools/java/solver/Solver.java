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
		{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{'K', 'X', 'X', 'X', 'X', 'X', 'X', 'J'},
		{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '},
	};
	
	enum Orientation {
		UPDOWN,
		LEFTRIGHT
	}
	
	
	
	public static void main(String[] args) {
		
		int mostMoves = -1;
		Config best = null;
		
		/*
		 * Config -> Config for shortest path to a winning config
		 */
//		Map<Config, Config> globalWinningMap = new HashMap<Config, Config>();
		
//		Config blank = Config.randomBlankConfig();
		Config blank = new Config(boardIni);
		
		List<Config> possibleRedCarPlacements = blank.possibleRedCarPlacements();
		for (Config c : possibleRedCarPlacements) {
			List<Config> possible2CarPlacements = c.possible2CarPlacements();
			for (Config d : possible2CarPlacements) {
				List<Config> possible3CarPlacements = d.possible3CarPlacements();
				for (Config e : possible3CarPlacements) {
					
//					if (globalWinningMap.containsKey(e)) {
//						
//					} else {
						
						Map<Config, Config> explored = new HashMap<Config, Config>();
						
						explore(e, null, explored);
						
						int mtw = movesToWin(e, explored);
						
						if (mtw > mostMoves) {
							System.out.println(mtw);
							System.out.println(e);
							System.out.println();
							mostMoves = mtw;
							best = e;
						}
//					}
					
				}
			}
		}
		
		String.class.getName();
		best.copy();
		
	}
	
	public static Config shortestWinner(Map<Config, Config> explored) {
		
		Config shortestWinner = null;
		int shortestWinnerLength = Integer.MAX_VALUE;
		for (Config c : explored.keySet()) {
			if (c.isWinning()) {
				
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
	
	public static int movesToWin(Config start, Map<Config, Config> exploredFromStart) {
		
		int shortestWinnerLength = Integer.MAX_VALUE;
		for (Config c : exploredFromStart.keySet()) {
			if (c.isWinning()) {
				
				Config d = c;
				int length = 1;
				while (!d.equals(start)) {
					Config g = exploredFromStart.get(d);
					assert g != null;
					d = g;
					length++;
				}
				
				if (length < shortestWinnerLength) {
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
