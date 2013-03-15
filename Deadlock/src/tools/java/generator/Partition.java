package generator;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class Partition {
	
	int partitionIdHash;
	
	byte[] partitionId;
	
	StateSpace space = new StateSpace();
	
	int totalBoardCount;
	
	List<String> hardestSolution;
	
	public void generate() throws Exception {
		
		long t = System.currentTimeMillis();
		
		byte[][] temp = Config.newConfig(Config.par.emptyBoard);
		
		while (true) {
			System.out.print(".");
			
			space.penGeneratingIteration = space.lastGeneratingIteration;
			space.lastGeneratingIteration = new TLongArrayList();
			
			for (int i = 0; i < space.penGeneratingIteration.size(); i++) {
				long info = space.penGeneratingIteration.get(i);
				Config.copyTo(Config.par.emptyBoard, temp);
				Config.toBoard(info, temp);
				explorePreviousMoves(temp);
			}
			totalBoardCount += space.penGeneratingIteration.size();
			
			if (space.lastGeneratingIteration.isEmpty()) {
				break;
			}
			
		}
		System.out.print("reached fixpoint... ");
		
		long longest = space.lastGeneratingConfig;
		assert longest != 0 : "fix me";
		
		space = null;
		
		if (longest != -1) {
			hardestSolution = solve(longest);
			System.out.println("hardest is " + hardestSolution.size() + " moves, total " + (totalBoardCount) + " boards, time: " + ((System.currentTimeMillis() - t) / 1000) + "s");
		} else {
			System.out.println("hardest is null, time: " + ((System.currentTimeMillis() - t) / 1000) + "s");
		}
		
	}
	
	public void explorePreviousMoves(byte[][] c) {
		
		List<byte[][]> moves = Config.possiblePreviousMoves(c);
		
		for (byte[][] m : moves) {
			space.tryPutGenerating(m);
		}
	}
	
	static List<String> solve(long info) {
		
		byte[][] start = Config.newConfig(Config.par.emptyBoard);
		Config.toBoard(info, start);
		
		StateSpace space = new StateSpace();
		space.putSolving(start, null);
		
		byte[][] winner = null;
		loop:
		while (true) {
			
			List<byte[][]> a = new ArrayList<byte[][]>(space.lastSolvingIteration);
			space.lastSolvingIteration = new ArrayList<byte[][]>();
			
			for (int i = 0; i < a.size(); i++) {
				byte[][] b = a.get(i);
				
				if (Config.isWinning(b)) {
					winner = b;
					break loop;
				}
				
				List<byte[][]> moves = Config.possibleNextMoves(b);
				
				for (byte[][] m : moves) {
					if (!space.allSolvingConfigsContains(m)) {
						space.putSolving(Config.clone(m), b);
					}
				}
				
			}
		}
		
		List<String> solution = new ArrayList<String>();
		
		String l = Config.toString(winner);
		solution.add(l);
		while (true) {
			l = space.getSolving(l);
			if (l == null) {
				break;
			}
			solution.add(0, l);
		}
		
		return solution;
	}

}
