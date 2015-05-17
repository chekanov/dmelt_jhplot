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

import java.awt.Color;
import java.awt.Dimension; // carmetal
import carmetal.construction.Construction;
import carmetal.eric.FileTools;
import carmetal.eric.JZirkelCanvas;
import carmetal.eric.GUI.palette.PaletteManager;
import carmetal.eric.GUI.window.MainWindow;
import carmetal.eric.bar.JPropertiesBar;
import carmetal.objects.ConstructionObject;
import carmetal.rene.gui.*;
import carmetal.rene.zirkel.ZirkelCanvas;
import carmetal.rene.zirkel.ZirkelFrame;

/**
 * HZirkel class is used to create a canvas for dynamic manipulation with
 * geometrical objects. Based on CarMatal/Zirkel program adopted for java
 * scripting.
 * 
 * @author S.Chekanov
 * 
 */

public class HZirkel {

	private static final long serialVersionUID = 1L;
	public static final long Version = 110;
	public static boolean IsApplet = false;
	private ZirkelFrame zframe;
	private MainWindow mw;

	/**
	 * Build a canvas for the zirkel with the standard size 600x600
	 * 
	 * @param Xsize
	 *            size in X
	 * @param Ysize
	 *            size in Y
	 */
	public HZirkel() {

		this(600, 500);

	}

	/**
	 * Build a canvas for the Zirkel canvas
	 * 
	 * @param Xsize
	 *            size in X
	 * @param Ysize
	 *            size in Y
	 */
	public HZirkel(int Xsize, int Ysize) {

		carmetal.rene.gui.Global.DetectDesktopSize();

		/** The current user. */
		String USER_HOME = System.getProperty("user.home");
		// file with preferences
		String PREFS_FILENAME = USER_HOME + java.io.File.separator + ".jehep"
				+ java.io.File.separator + "carmetal_config.txt";
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("windows") > -1 || OS.indexOf("nt") > -1) {
			PREFS_FILENAME = USER_HOME + java.io.File.separator + "jehep"
					+ java.io.File.separator + "carmetal_config.txt";
		}

		// Global.renameOldHomeDirectory();
		Global.loadProperties(PREFS_FILENAME);
		Global.initBundles();
		Global.setParameter("jsdumb", Global.getParameter("jsdumb", true));
		Global.initProperties();
		// MacroTools.createLocalDirectory();
		carmetal.eric.JGlobalPreferences.initPreferences();

		JPropertiesBar.CreatePopertiesBar();

		mw = new MainWindow();
		JZirkelCanvas.getCurrentJZF().setPreferredSize(
				new Dimension(Xsize, Ysize));
		PaletteManager.init();
		// PaletteManager.setSelected_with_clic("point", true);
		JZirkelCanvas.ActualiseMacroPanel();
		// getCanvas().setAxis_show(true);

	}

	/**
	 * Return the actual canvas where all objects are shown
	 * @return
	 */
	public ZirkelCanvas getCanvas() {

		return JZirkelCanvas.getCurrentJZF().getZC();

	}

	/**
	 * Draw an object on the canvas. Canvas is updated after the object is placed on the canvas.
	 * 
	 * @param obj
	 *            object
	 */
	public void draw(ConstructionObject obj) {

		add(obj);
		JZirkelCanvas.repaintZC();
	}

	/**
	 * Get construction to draw the objects
	 * 
	 * @return
	 */
	public Construction getConstruction() {

		return getCanvas().getConstruction();
	}

	/**
	 * Add an object on the canvas and update it. Usually, you will need to call
	 * "update" to update the canvas.
	 * 
	 * @param obj
	 *            object
	 */
	public void add(ConstructionObject obj) {

		getCanvas().addObject(obj);

	}

	/**
	 * Update the canvas with new objects. It repaints and update history.
	 */
	public void update() {
		JZirkelCanvas.repaintZC();
		// rebuild relay history
		JZirkelCanvas.getCurrentJZF().getReplay().newHistory();

	}

	/**
	 * close current page
	 */
	public void close() {
		JZirkelCanvas.closeCurrent();

	}

	public void exit() {
		mw.setVisible(true);
		mw.dispose();
	}

	/**
	 * Clear the canvas
	 */
	public void clear() {
		zframe.ZC.clear();
		JZirkelCanvas.repaintZC();
	}

	/**
	 * Show coordinate system on the canvas.  
	 */
	public void setAxis(boolean axis) {
		getCanvas().setAxis_show(axis);
		JZirkelCanvas.repaintZC();
	}

	/**
	 * Export image to a file. The output depends on the file extension. The allowed
	 * file extensions are eps, png, svn
	 * 
	 * @param file
	 *            name
	 */
	public void export(String file) {

		int i = file.lastIndexOf('.');

		String ext = "eps";
		if (i > 0 && i < file.length() - 1)
			ext = file.substring(i + 1).toLowerCase();

		if (ext.equalsIgnoreCase("eps"))
			FileTools.saveeps(file);
		if (ext.equalsIgnoreCase("png"))
			FileTools.savepng(file);
		if (ext.equalsIgnoreCase("svg"))
			FileTools.saveSVG(file);

	}

}
