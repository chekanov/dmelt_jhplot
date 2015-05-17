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


import hep.io.root.daemon.RootAuthenticator;
import javax.swing.*;

import root.RootHistogramBrowser;

import java.awt.event.*;
import java.net.Authenticator;
import java.net.URLConnection;
import java.io.*;




/**
 * Show Java ROOT browser to view ROOT histograms.
 * Based on RootHistogramBrowser by Tony Johnson
 * 
 * @author S.Chekanov
 * 
 */

public class BRoot {

	private JFrame frame;

	private RootHistogramBrowser browser;

	/**
	 * Show an empty frame with the ROOT browser
	 * 
	 */

	public BRoot() {

		showFrame();

	}

	/**
	 * Show a frame with ROOT browser and load a file
	 * 
	 * @param InputFile
	 *                     file name
	 */

	public BRoot(String InputFile) {

		 showFrame();
		 load(InputFile);
		
	}

	/**
	 * Load a ROOT file
	 * 
	 * @param InputFile
	 *                              file name
	 */

	public void load(String InputFile) {

			try {
				browser.setRootFile(new File(InputFile));
			} catch (IOException e) {
				jhplot.utils.Util.ErrorMessage("File:" + InputFile + "  not found!");
			}


	}

	/**
	 * Show a frame with ROOT browser
	 * 
	 */
	private void showFrame() {

		

			frame = new JFrame("Root Browser");
			browser = new RootHistogramBrowser();

			Authenticator.setDefault(new RootAuthenticator(browser));
			URLConnection.setDefaultAllowUserInteraction(true);
			frame.setContentPane(browser);
			// Make this exit when the close button is clicked.
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {

					frame.setVisible(false);

				}
			});
			frame.pack();
			frame.setVisible(true);

		

	}
	
}
