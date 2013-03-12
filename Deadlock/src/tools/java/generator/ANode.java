package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class ANode {
	
	public byte value;
	
	List<BNode> bnodes = new ArrayList<BNode>();
	
	public void add(Config c) {
		
		byte bbyte = c.getInfoByte((byte)'B');
		boolean added = false;
		for (BNode bnode : bnodes) {
			if (bnode.value == bbyte) {
				bnode.add(c);
				added = true;
				break;
			}
		}
		if (!added) {
			BNode bnode = new BNode();
			bnode.value = bbyte;
			bnode.add(c);
			bnodes.add(bnode);
		}
		
	}
	
	public boolean contains(Config c) {
		
		byte bbyte = c.getInfoByte((byte)'B');
		boolean found = false;
		boolean testRes = false;
		for (BNode bnode : bnodes) {
			if (bnode.value == bbyte) {
				testRes = bnode.contains(c);
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
		for (BNode bnode : bnodes) {
			testRet += bnode.size();
		}
		
		return testRet;
	}

}
