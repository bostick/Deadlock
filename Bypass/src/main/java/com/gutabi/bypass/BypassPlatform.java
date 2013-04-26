package com.gutabi.bypass;

import com.gutabi.bypass.level.Level;
import com.gutabi.deadlock.Platform;

public interface BypassPlatform extends Platform {
	
	void loadScores() throws Exception;
	
	void saveScore(Level l);
	
	void clearScores();
	
}
