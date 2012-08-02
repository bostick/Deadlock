package com.gutabi.deadlock.swing;

import com.gutabi.deadlock.core.Logger;

public class PlatformLogger implements Logger {
	
	private org.apache.log4j.Logger impl;
	
	public PlatformLogger(String name) {
		impl = org.apache.log4j.Logger.getLogger(name);
	}
	
	@SuppressWarnings("rawtypes")
	public PlatformLogger(Class clazz) {
		impl = org.apache.log4j.Logger.getLogger(clazz);
	}
	
	@Override
	public void debug(String msg) {
		impl.debug(msg);
	}
	
}
