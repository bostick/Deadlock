package com.gutabi.deadlock.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class DLSFileChooser extends JFileChooser {
	
//	JComboBox pdfLibList;
	
	public DLSFileChooser() {
		
//		JPanel pdfLibPanel = new JPanel();
//		pdfLibPanel.setLayout(new BoxLayout(pdfLibPanel, BoxLayout.PAGE_AXIS));
//		pdfLibPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		//String[] pdfLibs = { "pdfbox", "gs" };
		
//		pdfLibList = new JComboBox(PDFLibrary.values());
//		Dimension max = pdfLibList.getMaximumSize();
//		Dimension pref = pdfLibList.getPreferredSize();
//		max.height = pref.height;
//		pdfLibList.setMaximumSize(max);
//		pdfLibList.setSelectedIndex(PDFLibrary.getDefault().ordinal());
		
//		JLabel lab = new JLabel("PDF Library");
//		
//		Box labBox = Box.createHorizontalBox();
//		labBox.add(lab);
//		labBox.add(Box.createHorizontalGlue());
//		
//		Box verticalBox = Box.createVerticalBox();
//		verticalBox.add(Box.createVerticalGlue());
//		verticalBox.add(labBox);
//		verticalBox.add(pdfLibList);
//		
//		pdfLibPanel.add(verticalBox);
//		
//		setAccessory(pdfLibPanel);
		
		ViewerFilter filter = new ViewerFilter();
		addChoosableFileFilter(filter);
//		setAcceptAllFileFilterUsed(true);
		setFileFilter(filter);
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		setCurrentDirectory(new File("."));
		//fc.setCurrentDirectory(new File("C:\\Users\\brenton\\Contracting\\Anaptych"));
	}
	
	public class ViewerFilter extends FileFilter {
		
	    @Override
		public boolean accept(File f) {
	        if (f.isDirectory() || f.getName().endsWith("dls")) {
	        	return true;
	        } else {
	        	return false;
	        }
	    }
	    
	    /*
	     * due to http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4150661,
	     * the java buttheads will not automatically add extensions to selected files
	     */
	    
	    //The description of this filter
	    @Override
		public String getDescription() {
	        return "Deadlock Script";
	    }
	}

}

