package generator;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.Arrays;

import solver.Config;

public class ConfigTrie {
	
//	HashSet<Config> set = new HashSet<Config>();
	
	int actualArrLength = 0;
	long[] arr = new long[10];
	
	TLongSet tset = new TLongHashSet();
	
	public void add(long key) {
		
//		set.add(c);
//		boolean testRes = sortedAdd(c.getInfoLong());
//		unsortedAdd(c.getInfoLong());
		tset.add(key);
		
	}
	
	public boolean contains(long key) {
		
//		boolean testRes = set.contains(c);
		
//		long l = c.getInfoLong();
//		boolean testRes = sortedContains(l);
//		boolean testRes = unsortedContains(l);
		
		return tset.contains(key);
		
//		return testRes;
	}
	
	public int size() {
		
//		int testRet = set.size();
		
//		int testRet = actualArrLength;
		
//		return testRet;
		
		return tset.size();
	}
	
	
	
	
	
	private boolean sortedAdd(long key) {
		
		int index = Arrays.binarySearch(arr, 0, actualArrLength, key);
		if (index >= 0) {
			/*
			 * key found
			 */
			return false;
		} else {
			/*
			 * key not found
			 */
			int insertionPoint = -(index+1);
			
			if (arr.length == actualArrLength) {
				
				long[] newArr = new long[actualArrLength * 2];
				
				/*
				 * copy from start to insertion point
				 */
				int copyLength = insertionPoint;
				arrayCopy(
						arr,
                        0,
                        newArr,
                        0,
                        copyLength);
				
				/*
				 * add insertion point
				 */
				newArr[insertionPoint] = key;
				
				/*
				 * copy from insertion point to end
				 */
				copyLength = actualArrLength - insertionPoint;
				arrayCopy(
						arr,
						insertionPoint,
                        newArr,
                        insertionPoint+1,
                        copyLength);
				
				arr = newArr;
				actualArrLength++;
				return true;
				
			} else {
				
				/*
				 * copy from insertion point to end
				 */
				int copyLength = actualArrLength - insertionPoint;
				arrayCopy(
						arr,
                        insertionPoint,
                        arr,
                        insertionPoint+1,
                        copyLength);
				
				/*
				 * add insertion point
				 */
				arr[insertionPoint] = key;
				
				actualArrLength++;
				return true;
			}
			
		}
		
	}
	
	private boolean unsortedAdd(long key) {
		
		if (arr.length == actualArrLength) {
			
			long[] newArr = new long[actualArrLength * 2];
			
			arrayCopy(
					arr,
                    0,
                    newArr,
                    0,
                    actualArrLength);
			
			newArr[actualArrLength] = key;
			
			arr = newArr;
			actualArrLength++;
			return true;
			
		} else {
			
			arr[actualArrLength] = key;
			
			actualArrLength++;
			return true;
		}
		
	}
	
	private boolean sortedContains(long key) {
		
		int index = Arrays.binarySearch(arr, 0, actualArrLength, key);
		if (index >= 0) {
			/*
			 * key found
			 */
			return true;
		} else {
			/*
			 * key not found
			 */
			return false;
		}
	}
	
	private boolean unsortedContains(long key) {
		
		for (int i = 0; i < actualArrLength; i++) {
			if (arr[i] == key) {
				return true;
			}
		}
		return false;
	}
	
	public static void arrayCopy(long[] src, int srcPos, long[] dest, int destPos, int length) {
		System.arraycopy(src, srcPos, dest, destPos, length);
	}

}
