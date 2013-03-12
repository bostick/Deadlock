package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class RootNode {
	
	List<RNode> rnodes = new ArrayList<RNode>();
	
	public void add(Config c) {
		
		byte rbyte = c.getInfoByte((byte)'R');
		boolean added = false;
		for (RNode rnode : rnodes) {
			if (rnode.value == rbyte) {
				rnode.add(c);
				added = true;
				break;
			}
		}
		if (!added) {
			RNode rnode = new RNode();
			rnode.value = rbyte;
			rnode.add(c);
			rnodes.add(rnode);
		}
		
	}
	
	public boolean contains(Config c) {
		
		byte rbyte = c.getInfoByte((byte)'R');
		boolean found = false;
		boolean testRes = false;
		for (RNode rnode : rnodes) {
			if (rnode.value == rbyte) {
				testRes = rnode.contains(c);
				found = true;
				break;
			}
		}
		if (!found) {
			testRes = false;
		}
		
		return testRes;
	}
	
	public int size() {
		
		int testRet = 0;
		for (RNode rnode : rnodes) {
			testRet += rnode.size();
		}
		
		return testRet;
	}
	
}
