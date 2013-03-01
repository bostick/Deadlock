package generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import solver.Config;

public class Generator {
	
	static byte[][] boardIni = new byte[][] {
		{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{'K', 'X', 'X', 'X', 'X', 'X', 'X', 'J'},
		{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '},
	};
	
	static StateSpace explored = new StateSpace();
	
	public static void main(String[] args) throws Exception {
		
//		Thread.sleep(20000);
		
		long total = System.currentTimeMillis();
		long t = total;
		System.out.print("winning base cases... ");
		List<Config> winners = new ArrayList<Config>();
		
		Config c = new Config(boardIni);
		c = c.redCarWinningConfig();
		
		List<Config> placements0 = c.possible3CarPlacements();
		for (int i = 0; i < placements0.size(); i++) {
			Config d = placements0.get(i);
			
			List<Config> placements1 = d.possible3CarPlacements();
			for (int j = 0; j < placements1.size(); j++) {
				Config e = placements1.get(j);
				
//				winners.add(e);
				List<Config> possible3CarPlacements3 = e.possible3CarPlacements();
				for (int k = 0; k < possible3CarPlacements3.size(); k++) {
					Config f = possible3CarPlacements3.get(k);
					
					winners.add(f);
//					List<Config> possible3CarPlacements4 = f.possible3CarPlacements();
//					for (int l = 0; l < possible3CarPlacements4.size(); l++) {
//						Config g = possible3CarPlacements4.get(l);
//						
//						winners.add(g);
//					}
				}
				
			}
		}
		System.out.print("(" + winners.size() + ")");
		
		explored.iteration = 0;
		
		for (int i = 0; i < winners.size(); i++) {
			if (i % 100 == 0) {
				System.out.print(".");
			}
			Config w = winners.get(i);
			explored.put(w, null);
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println();
		
		t = System.currentTimeMillis();
		System.out.print("initial exploration... ");
		
		explored.iteration = explored.iteration+1;
		
		List<Config> a = new ArrayList<Config>(explored.lastIteration);
		explored.lastIteration.clear();
		
		System.out.print("(" + a.size() + ") ");
		for (int i = 0; i < a.size(); i++) {
			if (i % 100 == 0) {
				System.out.print(".");
			}
			Config b = a.get(i);
//			if (b.iteration == explored.iteration-1) {
			explore(b);
//			}
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println();
		
		int hash = explored.hashCode();
		while (true) {
			t = System.currentTimeMillis();
			
			explored.iteration = explored.iteration+1;
			
			System.out.print("exploring again... ");
			a = new ArrayList<Config>(explored.lastIteration);
			explored.lastIteration.clear();
			
			System.out.print("(" + a.size() + ") ");
			for (int i = 0; i < a.size(); i++) {
				if (i % 100 == 0) {
					System.out.print(".");
				}
				Config b = a.get(i);
//				if (b.iteration == explored.iteration-1) {
				explore(b);
//				}
			}
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis ");
			System.out.print("hashing... ");
			int curHash = explored.hashCode();
			System.out.print("done");
			System.out.println("");
			if (curHash == hash) {
				break;
			}
			hash = explored.hashCode();
		}
		System.out.println("reached fixpoint");
		
		int longest = -1;
		Config longestConfig = null;
		for (Config l : a) {
			
			Config m = l;
			int dist = 0;
			while (true) {
				if (m == null) {
					break;
				}
				m = explored.get(m);
				dist++;
			}
			
			if (dist > longest) {
				longest = dist;
				longestConfig = l;
			}
			
		}
		
		System.out.println("hardest config:");
		System.out.println(longestConfig);
		
		System.out.println("total time: " + (System.currentTimeMillis() - total) + " millis");
	}
	
	static public void explore(Config c) {
		
		List<Config> moves = c.possiblePreviousMoves();
		
		for (Config m : moves) {
			
			if (!explored.keySet().contains(m)) {
				
				Set<Config> children = explored.childrenMap.get(c);
				assert children == null || !children.contains(m);
				
				explored.put(m, c);
			} else {
				
				Config currentMPred = explored.get(m);
				int cDist = explored.distanceToStart(c);
				int currentMPredDist = explored.distanceToStart(currentMPred);
				if (cDist < currentMPredDist) {
					
					explored.remove(m, currentMPred);
					explored.put(m, c);
				}
			} 
		}
	}
}
