package jhplot.gui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Common components for all GUI
 * 
 * @author sergei
 * 
 */

public class CommonGUI {

	/**
	 * File chooser to read data files.
	 * 
	 * @param com
	 *            parent frame.
	 * @return
	 */
	public static JFileChooser openDataFileChooser(Component com) {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.removeChoosableFileFilter(fileChooser
				.getAcceptAllFileFilter());
		// fileChooser.setCurrentDirectory(new
		// File(System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
                                "All *.jser,*.jxml,*.jpbu,*.jdat,*.root,*.aida,*.xml", "jser","jxml","jpbu","jdat","root","aida","xml"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"HFile (*.jser)", "jser"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"HFileXML (*.jxml)", "jxml"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"PFile (*.jpbu)", "jpbu"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"HBook (*.jdat)", "jdat"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"ROOT (*.root)", "root"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"AIDA (*.aida *.xml)", "aida", "xml"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		return fileChooser;

	}

	
	
	/**
	 * File chooser images.
	 * Used to save images for all canvaces.
	 * 
	 * @param com imput frame
	 * @return
	 */
	
	public static JFileChooser openImageFileChooser(Component com){
		
		
		
		  final JFileChooser fileChooser = new JFileChooser();
		
		  
		  fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter()); 
		//  fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));
	      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Encapsulated PostScript (*.eps)", "eps"));
	      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PostScript (*.ps)", "ps"));
	      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Scalable Vector Graphics (*.svg)", "svg"));
	      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Compressed SVG  (*.svgz)", "svgz"));
              fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
	      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG Raster Images (*.jpg, *.jpeg)","jpg", "jpeg"));
	      fileChooser.setAcceptAllFileFilterUsed(true);
	      
	      final File sFile = new File("dmelt.pdf");
	      fileChooser.setSelectedFile(sFile);
	      
	   // debug
		    /*
		    fc.addPropertyChangeListener(new PropertyChangeListener() {

		        public void propertyChange(PropertyChangeEvent evt) {
		            System.out.println("Property name=" + evt.getPropertyName() + ", oldValue=" + evt.getOldValue() + ", newValue=" + evt.getNewValue());
		            System.out.println("getSelectedFile()=" + fc.getSelectedFile());
		        }
		    });
		    */
		    
	      fileChooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener() {

		        public void propertyChange(PropertyChangeEvent evt) {
		            Object o = evt.getNewValue();
		            if (o instanceof FileNameExtensionFilter) {
		                FileNameExtensionFilter filter = (FileNameExtensionFilter) o;

		                String ex = filter.getExtensions()[0];

		                File selectedFile = fileChooser.getSelectedFile();
		                if (selectedFile == null) {
		                    selectedFile = sFile;
		                }
		                String path = selectedFile.getName();
		                path = path.substring(0, path.lastIndexOf("."));

		                fileChooser.setSelectedFile(new File(path + "." + ex));
		            }
		        }
		    });
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      return fileChooser;
	}
	
	
	
	
	
	
	
	
	
	
}
