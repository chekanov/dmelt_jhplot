/**
*    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
*    All rights reserved.
*
*    This program is free software; you can redistribute it and/or modify it under the terms
*    of the GNU General Public License as published by the Free Software Foundation; either
*    version 3 of the License, or any later version.
*
*    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
*    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*    See the GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along with this program;
*    if not, see <http://www.gnu.org/licenses>.
*
*    Additional permission under GNU GPL version 3 section 7:
*    If you have received this program as a library with written permission from the DataMelt team,
*    you can link or combine this library with your non-GPL project to convey the resulting work.
*    In this case, this library should be considered as released under the terms of
*    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
*    provided you include this license notice and a URL through which recipients can access the
*    Corresponding Source.
**/


package jhplot;

import javax.swing.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.jar.*;
import java.util.StringTokenizer;

import jhplot.gui.HelpBrowser;

/**
 * Show information about jHplot. Also keeps some global variables.
 * 
 * @author S.Chekanov
 * 
 */

// keep current data
public class JHPlot {

	public static JLabel statusbar;
	public static boolean ReadFile = false;
	private static String BUILD_DATE;
	private static String BUILD_BY;
	private static String VERSION;
	private static String CREATED_BY;

	// mouse positions
	public static double Xpos;
	public static double Ypos;
	public final static int SECONDS = 3000;
	public static Timer timer;
	private static boolean firstTime = true;

	/**
	 * 
	 * Initialization. Used by internal frames.
	 */

	public static void init() {

		ReadFile = false;
		statusbar = new JLabel("jHPlot is ready");
		statusbar.setFont(new Font("Serif", Font.PLAIN, 12));
                readInfo(); 
                Xpos = 0;
                Ypos = 0;

         }

         public static void readInfo() { 

		timer = new Timer(SECONDS, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//statusbar.setText(" ");
				timer.stop();
			}
		});
		timer.setRepeats(false);

		// this is dammy
		BUILD_DATE = "20070120";
		BUILD_BY = "S.Chekanov";
		VERSION = "1.0";
		CREATED_BY = "SUN 1.5";

		// check build version
		Thread t = new Thread() {
			public void run() {
				if (firstTime)
					version();
			};
		};
		t.start();

	}

	/**
	 * Show mouse positions
	 * 
	 * @param message
	 *            Message to show
	 * @param X
	 *            X in the user coordinate
	 * @param Y
	 *            Y in the user coordinate
	 * @param Xndc
	 *            as above in NDC
	 * @param Yndc
	 *            as above in NDC
         * @param Xpix
         *           X pixels 
         * @param Ypix
         *           Y pixels  

	 * 
	 */
	public static void showMouse(String message, double X, double Y,
			double Xndc, double Yndc, int Xpix, int Ypix) {

		Xpos = X;
		Ypos = Y;

		DecimalFormat dfb1 = new DecimalFormat("#.##E00");
		String s1 = dfb1.format(Xpos);
		String s2 = dfb1.format(Ypos);

		// format it
		if (s1.endsWith("E00"))
			s1 = s1.substring(0, s1.length() - 3);
		if (s2.endsWith("E00"))
			s2 = s2.substring(0, s2.length() - 3);

		DecimalFormat dfb2 = new DecimalFormat("#.##");
		String ss1 = dfb2.format(Xndc);
		String ss2 = dfb2.format(Yndc);

		String user = "USER: (" + s1 + ",  " + s2 + ")";
		String ndc = "NDC: (" + ss1 + ",  " + ss2 + ")";
                String pix = "Pixels: (" + Integer.toString(Xpix) + ",  " + Integer.toString(Ypix) + ")";
		showStatusBarText(message + ": " + user + "  " + ndc+"  " +pix);

	}

	/**
	 * Show a text on status bar for 5 sec.
	 * 
	 * @param text
	 *            Text
	 */

	public static void showStatusBarText(String text) {

		statusbar.setText(text);
		if (timer.isRunning()) {
			timer.restart();
		} else {
			timer.start();
		}

	}

	
	
	
	

	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
	
	
	
	/**
	 * Show a text on status bar permanently.
	 * 
	 * @param text
	 *            Text
	 */

	public static void showMessage(String text) {
		statusbar.setText(text);
	}

	/**
	 * jHplot version information
	 * 
	 */
	public static void printVersion() {

		System.out.println("Author: " + BUILD_BY);
		System.out.println("Version: " + VERSION);
		System.out.println("Build: " + BUILD_DATE);
	}

	/**
	 * Get the author
	 * 
	 * @return author name
	 */
	public static String getAuthor() {

		return BUILD_BY;

	}

	/**
	 * Get version information
	 * 
	 * @return version information
	 */
	public static String getVersion() {

		return VERSION;

	}

	/**
	 * Get build date
	 * 
	 * @return build data
	 */
	public static String getBuildTime() {

		return BUILD_DATE;

	}

	/**
	 * Get JAVA version used to create
	 * 
	 * @return build data
	 */
	public static String getCreatedBy() {

		return CREATED_BY;

	}

	public static String[] getInfo() {

		String[] vv = new String[] { BUILD_BY, VERSION, BUILD_DATE };

		return vv;
	}

	private static void version() {
		// URL u = this.getClass().getResource("image.gif");
		BUILD_DATE = "20070120";
		BUILD_BY = "S.Chekanov";
		VERSION = "1.0";
		CREATED_BY = "SUN 1.5";

		final String JARFILE = "jhplot.jar";
		final String classpath = System.getProperty("java.class.path");
		final String pathsep = System.getProperty("path.separator");
		StringTokenizer t = new StringTokenizer(classpath, pathsep, false);

		while (t.hasMoreElements()) {
			String path = t.nextToken();

			if (path.indexOf(JARFILE) > -1) {

				try {
					// System.out.println(path);
					JarFile jf = new JarFile(path);
					Manifest m = jf.getManifest();
					Attributes attribs = m.getMainAttributes();
					BUILD_DATE = attribs.getValue("Built-Date");
					BUILD_BY = attribs.getValue("Built-By");
					CREATED_BY = attribs.getValue("Created-By");
					VERSION = attribs.getValue("Version");

					firstTime = false;
					if (statusbar != null)
					showStatusBarText("version:" + VERSION + " build:"
							+ BUILD_DATE);
				} catch (IOException e) {
					System.out.println("Cannot read jar-file manifest: "
							+ JARFILE);
				}
				return;
			} // end if

		}

	}

}
