package generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import solver.Config;

public class Generator {
	
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
	
	public static void main(String[] args) {
		
		long total = System.currentTimeMillis();
		long t = total;
		System.out.print("winning base cases... ");
		List<Config> winners = new ArrayList<Config>();
		
		Config c = new Config(boardIni);
		c = c.redCarWinningConfig();
		
		List<Config> possible2CarPlacements = c.possible2CarPlacements();
		for (int i = 0; i < possible2CarPlacements.size(); i++) {
			Config d = possible2CarPlacements.get(i);
			
			List<Config> possible3CarPlacements = d.possible3CarPlacements();
			for (int j = 0; j < possible3CarPlacements.size(); j++) {
				Config e = possible3CarPlacements.get(j);
				
//				winners.add(e);
				List<Config> possible2CarPlacements2 = e.possible2CarPlacements();
				for (int k = 0; k < possible2CarPlacements2.size(); k++) {
					Config f = possible2CarPlacements2.get(k);
					
					winners.add(f);
				}
				
			}
		}
		System.out.print("(" + winners.size() + ")");
		
		StateSpace explored = new StateSpace();
		
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
		System.out.print("(" + winners.size() + ") ");
		for (int i = 0; i < winners.size(); i++) {
			if (i % 100 == 0) {
				System.out.print(".");
			}
			Config w = winners.get(i);
			explore(w, explored);
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println();
		
		int hash = explored.hashCode();
		while (true) {
			t = System.currentTimeMillis();
			System.out.print("exploring again... ");
			List<Config> a = new ArrayList<Config>(explored.keySet());
			System.out.print("(" + a.size() + ") ");
			for (int i = 0; i < a.size(); i++) {
				if (i % 100 == 0) {
					System.out.print(".");
				}
				Config w = a.get(i);
				explore(w, explored);
			}
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
			System.out.println("");
			if (explored.hashCode() == hash) {
				break;
			}
			hash = explored.hashCode();
		}
		System.out.println("reached fixpoint");
		
		int longest = -1;
		Config longestConfig = null;
		for (Config l : explored.keySet()) {
			
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
	
	static public void explore(Config c, StateSpace explored) {
		
		List<Config> moves = c.possibleMoves();
		for (Config m : moves) {
			if (m.isWinning()) {
				continue;
			}
			
			if (!explored.keySet().contains(m)) {
				
				Set<Config> children = explored.childrenMap.get(c);
				assert children == null || !children.contains(m);
				
				explored.put(m, c);
				explore(m, explored);
			} else {
				Config currentMPred = explored.get(m);
				int cDist = distanceToStart(c, explored);
				int currentMPredDist = distanceToStart(currentMPred, explored);
				if (cDist < currentMPredDist) {
					/*
					 * update entire entry
					 */
					explored.remove(m, currentMPred);
					explored.put(m, c);
					explore(m, explored);
				}
			} 
		}
	}
	
	public static int distanceToStart(Config c, StateSpace explored) {
		int testDist = explored.distanceToStart(c);
		return testDist;
	}
}
