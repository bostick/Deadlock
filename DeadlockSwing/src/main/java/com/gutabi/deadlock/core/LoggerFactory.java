package com.gutabi.deadlock.core;

public abstract class LoggerFactory {
	
	public static LoggerFactory LOGGERFACTORY;
	
	public abstract Logger getLogger(String name);
	
	public abstract Logger getLogger(Class<?> clazz);
	
}
