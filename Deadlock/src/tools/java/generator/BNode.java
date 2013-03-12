package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class BNode {
	
	public byte value;
	
	List<CNode> cnodes = new ArrayList<CNode>();
	
	public void add(Config c) {
		
		byte cbyte = c.getInfoByte((byte)'C');
		boolean added = false;
		for (CNode cnode : cnodes) {
			if (cnode.value == cbyte) {
				cnode.add(c);
				added = true;
				break;
			}
		}
		if (!added) {
			CNode cnode = new CNode();
			cnode.value = cbyte;
			cnode.add(c);
			cnodes.add(cnode);
		}
		
	}
	
	public boolean contains(Config c) {
		
		byte cbyte = c.getInfoByte((byte)'C');
		boolean found = false;
		boolean testRes = false;
		for (CNode cnode : cnodes) {
			if (cnode.value == cbyte) {
				testRes = cnode.contains(c);
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
		for (CNode cnode : cnodes) {
			testRet += cnode.size();
		}
		
		return testRet;
	}

}
