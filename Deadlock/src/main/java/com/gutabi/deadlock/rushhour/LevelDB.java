package com.gutabi.deadlock.rushhour;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LevelDB {
	
	ZipFile z;
	public final int levelCount;
	
	public LevelDB(URL url) throws Exception {
		
		String s = url.getFile();
		
		z = new ZipFile(s);
		levelCount = z.size();
		
	}
	
	public Level readLevel(int index) throws Exception {
		
		Enumeration<? extends ZipEntry> entries = z.entries();
		
		ZipEntry entry;
		int cur = 0;
		while (true) {
			
			entry = entries.nextElement();
			if (cur == index) {
				break;
			}
			
			cur++;
		}
		
		
		
		
		BufferedInputStream is = new BufferedInputStream(z.getInputStream(entry));
		int count;
		byte tmp[] = new byte[100];
		FileOutputStream fos = new FileOutputStream(entry.getName());
		BufferedOutputStream dest = new BufferedOutputStream(fos, 100);
		int totalCount = 0;
		while ((count = is.read(tmp, 0, 100)) != -1) {
			totalCount += count;
			dest.write(tmp, 0, count);
		}
		
		byte[] data = new byte[totalCount];
		System.arraycopy(tmp, 0, data, 0, totalCount);
		
		dest.flush();
		dest.close();
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
	
	
	
	
	
	
//	public Level readLevel(Resource res) throws Exception {
//		
//		Level level = new Level();
//		
//		level.id = ((ResourceImpl)res).given;
//		
//		char[][] board;
//		
//		URL url = this.getClass().getResource(((ResourceImpl)res).full);
//		
//		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//		
//		int rows = 0;
//		int cols = 0;
//		
//		StringBuilder builder = new StringBuilder();
//		String inputLine = in.readLine();
//		
//		if (inputLine.matches("moves: .*")) {
//			String rest = inputLine.substring(7);
//			level.requiredMoves = Integer.parseInt(rest);
//		} else {
//			assert false;
//		}
//		
//		while ((inputLine = in.readLine()) != null) {
//			builder.append(inputLine);
//			cols = inputLine.length();
//			rows++;
//		}
//		in.close();
//		String s = builder.toString();
//		
//		board = new char[rows][cols];
//		for (int i = 0; i < rows; i++) {
//			for (int j = 0; j < cols; j++) {
//				board[i][j] = s.charAt(i * cols + j);
//			}
//		}
//		
//		level.board = board;
//		
//		return level;
//	}

}
