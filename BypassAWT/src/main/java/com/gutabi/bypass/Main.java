package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.bypass.BypassApplication.BYPASSAPP;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.menu.MainMenu;
import com.gutabi.bypass.ui.WindowInfo;

public class Main  {
	
	static void createAndShowGUI() throws Exception {
		
		BypassApplication app = new BypassApplication();
		APP = app;
		BYPASSAPP = app;
		
		PlatformImpl platform = new PlatformImpl();
		APP.platform = platform;
		BYPASSAPP.bypassPlatform = platform;
		
		JFrame newFrame;
		newFrame = new JFrame("Bypass");
		newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		newFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        APP.exit();
		    }
		});
		
		newFrame.setLocation((int)WindowInfo.windowDim().width/2 - APP.MAINWINDOW_WIDTH/2, 0);
		
		platform.appContainer = newFrame;
		
		
		newFrame = new JFrame("Debug Control Panel");
		newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		newFrame.setLocation((int)WindowInfo.windowDim().width/2 + APP.MAINWINDOW_WIDTH/2 + 100, 0);
		
		platform.debuggerContainer = newFrame;
		
		APP.DEBUGGER_SCREEN = true;
		APP.DEBUG_DRAW = true;
		
		BYPASSAPP.init();
		
		APP.platform.action(MainMenu.class);
		
	}
	
	public static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable t) {
			System.err.println("Uncaught exception on thread " + thread.getName());
			t.printStackTrace();
		}
	};
	
	public static void main(final String[] args) throws UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
		
	}
	
}
