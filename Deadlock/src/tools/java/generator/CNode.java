package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class CNode {
	
	public byte value;
	
	List<DNode> dnodes = new ArrayList<DNode>();
	
	public void add(Config c) {
		
		byte dbyte = c.getInfoByte((byte)'D');
		boolean added = false;
		for (DNode dnode : dnodes) {
			if (dnode.value == dbyte) {
				dnode.add(c);
				added = true;
				break;
			}
		}
		if (!added) {
			DNode dnode = new DNode();
			dnode.value = dbyte;
			dnode.add(c);
			dnodes.add(dnode);
		}
		
	}
	
	public boolean contains(Config c) {
		
		byte dbyte = c.getInfoByte((byte)'D');
		boolean found = false;
		boolean testRes = false;
		for (DNode dnode : dnodes) {
			if (dnode.value == dbyte) {
				testRes = dnode.contains(c);
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
		for (DNode dnode : dnodes) {
			testRet += dnode.size();
		}
		
		return testRet;
	}

}
