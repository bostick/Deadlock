package com.gutabi.bypass.level;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.gutabi.deadlock.Resource;

public class LevelDB {
	
	Resource res;
	public final int levelCount;
	
	public LevelDB(Resource res) throws Exception {
		
		this.res = res;
		
		InputStream is = APP.platform.openResourceInputStream(res);
		ZipInputStream zis = new ZipInputStream(is);
		
		while (true) {
			
			ZipEntry entry = zis.getNextEntry();
			
			if (entry.getName().equals("metadata.txt")) {
				
				int count;
				byte tmp[] = new byte[100];
				
				count = zis.read(tmp, 0, 100);
				assert count != -1;
				
				byte[] data = new byte[count];
				System.arraycopy(tmp, 0, data, 0, count);
				
				levelCount = Integer.parseInt(new String(data));
				
				zis.close();
				is.close();
				break;
			}
			
		}
	}
	
	public Level readLevel(int index) throws Exception {
		
		InputStream is = APP.platform.openResourceInputStream(res);
		ZipInputStream zis = new ZipInputStream(is);
		
		ZipEntry entry = zis.getNextEntry();
		if (entry.getName().equals("metadata.txt")) {
			zis.getNextEntry();
		}
		int curIndex = 0;
		while (curIndex != index) {
			entry = zis.getNextEntry();
			if (entry.getName().equals("metadata.txt")) {
				zis.getNextEntry();
			}
			curIndex++;
		}
		
		int count;
		byte tmp[] = new byte[100];
		
		count = zis.read(tmp, 0, 100);
		assert count != -1;
		
		byte[] data = new byte[count];
		System.arraycopy(tmp, 0, data, 0, count);
		
		zis.close();
		is.close();
		
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		
		
		Level level = new Level();
		char[][] board;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(bais));
		
		int rows = 0;
		int cols = 0;
		
		StringBuilder builder = new StringBuilder();
		String inputLine = in.readLine();
		
		if (inputLine.matches("moves: .*")) {
			String rest = inputLine.substring(7);
			level.requiredMoves = Integer.parseInt(rest);
		} else {
			assert false;
		}
		
		while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
			cols = inputLine.length();
			rows++;
		}
		in.close();
		String s = builder.toString();
		
		board = new char[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				board[i][j] = s.charAt(i * cols + j);
			}
		}
		
		level.index = index;
		level.board = board;
		
		return level;
		
	}

}