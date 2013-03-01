package solver;

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
	
//	public static void main(String[] args) {
//		
//		int mostMoves = -1;
//		Config best = null;
//		
//		/*
//		 * Config -> Config for shortest path to a winning config
//		 */
//		Map<Config, Config> globalExplored = new HashMap<Config, Config>();
//		
////		Config blank = Config.randomBlankConfig();
//		Config blank = new Config(boardIni);
//		
//		char[][] testIni = new char[][] {
//				{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
//				{' ', 'X', 'X', 'A', 'A', 'X', 'R', ' '},
//				{' ', 'X', 'X', 'X', 'X', 'X', 'R', ' '},
//				{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//				{' ', 'X', 'X', 'X', 'X', 'B', 'X', ' '},
//				{' ', 'X', 'X', 'X', 'X', 'B', 'X', ' '},
//				{'K', 'X', 'X', 'X', 'X', 'B', 'X', 'J'},
//				{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '},
//			};
//		Config test = new Config(testIni);
//		
//		List<Config> possibleRedCarPlacements = blank.possibleRedCarPlacements();
//		for (int i = 0; i < possibleRedCarPlacements.size(); i++) {
//			Config c = possibleRedCarPlacements.get(i);
//			
//			List<Config> possible2CarPlacements = c.possible2CarPlacements();
//			for (int j = 0; j < possible2CarPlacements.size(); j++) {
//				Config d = possible2CarPlacements.get(j);
//				
//				List<Config> possible3CarPlacements = d.possible3CarPlacements();
//				for (int k = 0; k < possible3CarPlacements.size(); k++) {
//					Config e = possible3CarPlacements.get(k);
//					
//					if (e.equals(test)) {
//						String.class.getName();
//					}
//					
//					if (!globalExplored.containsKey(e)) {
//						
//						Map<Config, Config> explored = new HashMap<Config, Config>();
//						
//						explore(e, null, explored);
//						
//						for (Config f : explored.keySet()) {
//							
//							globalExplored.put(f, null);
//							
//							int mtw = movesToWin(f, explored);
//							
//							if (mtw > mostMoves) {
//								
////								if (mtw == 19) {
////									
////									String.class.getName();
////								}
//								
//								mostMoves = mtw;
//								best = e;
//								System.out.println(mtw);
//								System.out.println(e);
//								System.out.println();
//							}
//							
//						}
//						
//					} else {
//						
//					}
//					
//				}
//			}
//		}
//		
//		String.class.getName();
////		best.copy();
//		
//	}
	
//	public static Config shortestWinner(Map<Config, Config> explored) {
//		
//		Config shortestWinner = null;
//		int shortestWinnerLength = Integer.MAX_VALUE;
//		for (Config c : explored.keySet()) {
//			if (c.isWinning()) {
//				
//				Config d = c;
//				int length = 0;
//				while (d != null) {
//					d = explored.get(d);
//					length++;
//				}
//				
//				if (length < shortestWinnerLength) {
//					shortestWinner = c;
//					shortestWinnerLength = length;
//				}
//				
//			}
//		}
//		
//		return shortestWinner;
//		
//	}
	
	/*
	 * iterates through exploredFromStart
	 */
	public static int movesToWin(Config start, Map<Config, Config> exploredFromStart) {
		
//		Config shortestWinner = null;
		
		int shortestWinnerLength = Integer.MAX_VALUE;
		forLoop:
		for (Config c : exploredFromStart.keySet()) {
			
//			globalExplored.put(c, null);
			
			if (c.isWinning()) {
				
				Config d = c;
				int length = 1;
				while (!d.equals(start)) {
					Config g = exploredFromStart.get(d);
//					globalExplored.put(g, d);
					
					if (g == null) {
						continue forLoop;
					}
					
					d = g;
					length++;
				}
				
				if (length < shortestWinnerLength) {
					shortestWinnerLength = length;
//					shortestWinner = c;
				}
				
			} else {
				
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
	
//	static public void explore(Config c, Config pred, Map<Config, Config> explored) {
//		
//		explored.put(c, pred);
//		
//		List<Config> moves = c.possiblePreviousMoves();
//		for (Config m : moves) {
//			if (!explored.keySet().contains(m)) {
//				explore(m, c, explored);
//			} else {
//				Config currentMPred = explored.get(m);
//				if (distanceToStart(c, explored) < distanceToStart(currentMPred, explored)) {
//					explore(m, c, explored);
//				}
//			} 
//		}
//	}
	
}
