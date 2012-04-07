package com.gutabi.deadlock.swing.utils;

public class DoubleUtils {
	
	public static boolean doubleEqual(double a, double b) {
		return Math.abs(a-b) < 1.0E-10;
	}
	
}
