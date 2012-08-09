package com.gutabi.deadlock.swing;

import com.gutabi.deadlock.core.Logger;
import com.gutabi.deadlock.core.LoggerFactory;

public class PlatformLoggerFactory extends LoggerFactory {

	@Override
	public Logger getLogger(String name) {
		return new PlatformLogger(name);
	}

	@Override
	public Logger getLogger(Class<?> clazz) {
		return new PlatformLogger(clazz);
	}
	
	public class PlatformLogger implements Logger {
		
		private org.apache.log4j.Logger impl;
		
		private PlatformLogger(String name) {
			impl = org.apache.log4j.Logger.getLogger(name);
		}
		
		@SuppressWarnings("rawtypes")
		private PlatformLogger(Class clazz) {
			impl = org.apache.log4j.Logger.getLogger(clazz);
		}
		
		@Override
		public void debug(String msg) {
			impl.debug(msg);
		}
		
	}
}
