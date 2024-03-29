package com.brentonbostick.bypass.awt.full;


import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import com.brentonbostick.bypass.awt.BypassAWTPlatform;
import com.brentonbostick.bypass.awt.ui.WindowInfo;
import com.brentonbostick.bypass.menu.MainMenuFull;

public class Main {
	
	static void createAndShowGUI() throws Exception {
		
		JFrame mainFrame = new JFrame("Bypass");
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        APP.platform.exit();
		    }
		});
		
		mainFrame.setLocation((int)WindowInfo.windowDim().width/2 - BypassAWTPlatform.MAINWINDOW_WIDTH/2, (int)WindowInfo.windowDim().height/2 - BypassAWTPlatform.MAINWINDOW_HEIGHT/2);
		
//		JFrame debuggerFrame = new JFrame("Debug Control Panel");
//		debuggerFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		
//		debuggerFrame.setLocation((int)WindowInfo.windowDim().width/2 + BypassAWTPlatform.MAINWINDOW_WIDTH/2 + 100, 0);
		
		if (BYPASSAPP == null) {
			BypassAWTFullPlatform platform = new BypassAWTFullPlatform();
			platform.appContainer = mainFrame;
//			platform.debuggerContainer = debuggerFrame;
			try {
				platform.createApplication();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mainFrame.setIconImage(((BypassAWTPlatform)APP.platform).titleBarIcon);
		
		APP.DEBUG_DRAW = false;
		
		APP.platform.action(MainMenuFull.class);
		
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
