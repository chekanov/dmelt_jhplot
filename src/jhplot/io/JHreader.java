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
package jhplot.io;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

import jhplot.HPlot;
import jplot.DataArray;
import jplot.GraphSettings;
import jplot.JPlot;
import jplot.LinePars;
import jplot.XMLRead;

/**
 * A class to read XML file and build jHPlot graphics.
 * 
 * @author S.Chekanov
 * 
 */

public class JHreader {

	private static String fname; // file name

	private static int IndexData = 0;

	private static Font defFont = new Font("Lucida Sans", Font.BOLD, 16);

	private static Color defColor = Color.BLACK;

	private static Color defColorB = Color.WHITE;

	/**
	 * Read the file. 
	 * 
	 * @param f input file. 
	 * @param hplot HPlot object.  
	 * 
	 * @return true if OK
	 */
	public static boolean readScript(File f, HPlot hplot) {

		fname = f.getName(); // file name
		fname = fname.substring(0, fname.length() - 4);

		String fpath = f.getParent(); // path to this file
		String fpathname = f.getAbsolutePath();
		// String sout = "";

		String script = fname + "/" + fname + ".xml";
		// System.out.println(script);

		try {
			// go to index file first
			JarFile zf = new JarFile(f);
			JarEntry entry = zf.getJarEntry(script);

			java.io.InputStream in = zf.getInputStream(entry);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(isr);

			XMLRead xr = new XMLRead();

			// read all lines concerning jhplot, i.e between <jhplot> and
			// </jhplot>:
			if (!xr.parse(reader, "jhplot"))
				return false;

			hplot.removeGraph();

			// int sbuild = xr.getInt("width",1);
			// System.out.println(sbuild);

			if (xr.open("info")) {
				// String sbuild = xr.getString("build", "now");
				// System.out.println(sbuild);
				// String author = xr.getString("author", " ");
				// System.out.println(author);
				xr.close();
			}

			// System.out.println( xr.getInt("width", 100) );

			if (xr.open("globalpanel")) {
				hplot.setSizePanel(xr.getInt("width", 100), xr.getInt("height",
						100));

				hplot.setPlotsNum(xr.getInt("numberX", 1), xr.getInt("numberY",
						1));

				// System.out.println( xr.getInt("height", 100) );
				xr.close();
			}

			if (xr.open("margintop")) {

				// System.out.println( xr.getString("text"," ") );

				hplot.setTextTop(xr.getString("text", " "), xr.getFont("font",
						defFont), xr.getColor("fcolor", defColor));

				hplot.setMarginSizeTop(xr.getDouble("msize", 0.025));
				hplot.setTextTopColorBack(xr.getColor("bcolor", defColorB));
				hplot.setTextPosTopX(xr.getDouble("mposX", 0.01));
				hplot.setTextPosTopY(xr.getDouble("mposY", 0.01));
				hplot.setTextRotationTop(xr.getInt("mrotation", 0));
				xr.close();
			}

			if (xr.open("marginleft")) {
				hplot.setTextLeft(xr.getString("text", " "), xr.getFont("font",
						defFont), xr.getColor("fcolor", defColor));
				hplot.setTextLeftColorBack(xr.getColor("bcolor", defColorB));
				hplot.setMarginSizeLeft(xr.getDouble("msize", 0.025));
				hplot.setTextPosLeftX(xr.getDouble("mposX", 0.01));
				hplot.setTextPosLeftY(xr.getDouble("mposY", 0.01));
				hplot.setTextRotationLeft(xr.getInt("mrotation", 0));

				xr.close();
			}

			if (xr.open("marginright")) {
				hplot.setTextRight(xr.getString("text", " "), xr.getFont(
						"font", defFont), xr.getColor("fcolor", defColor));
				hplot.setTextRightColorBack(xr.getColor("bcolor", defColorB));
				hplot.setMarginSizeRight(xr.getDouble("msize", 0.025));
				hplot.setTextPosRightX(xr.getDouble("mposX", 0.01));
				hplot.setTextPosRightY(xr.getDouble("mposY", 0.01));
				hplot.setTextRotationRight(xr.getInt("mrotation", 0));
				xr.close();
			}

			if (xr.open("marginbottom")) {
				hplot.setTextBottom(xr.getString("text", " "), xr.getFont(
						"font", defFont), xr.getColor("fcolor", defColor));
				hplot.setTextBottomColorBack(xr.getColor("bcolor", defColorB));
				hplot.setMarginSizeBottom(xr.getDouble("msize", 0.025));
				hplot.setTextPosBottomX(xr.getDouble("mposX", 0.01));
				hplot.setTextPosBottomY(xr.getDouble("mposY", 0.01));
				hplot.setTextRotationBottom(xr.getInt("mrotation", 0));
				xr.close();
			}

			xr.close(); // close global

			// create graph

			// trigger update of the size
			hplot.setGraph();
			hplot.getCanvasPanel().updateUI();

			Vector<DataArray>[][] dataHplot = hplot.getData();

			// System.out.println( hplot.getNtotX() );
			// System.out.println( hplot.getNtotY() );

			for (int i2 = 0; i2 < hplot.getNtotY(); i2++) {
				for (int i1 = 0; i1 < hplot.getNtotX(); i1++) {

					// move to particular plot
					hplot.cd(i1 + 1, i2 + 1);

					// get grah settings
					JPlot jpp = hplot.getJPlot();
					GraphSettings gss = hplot.getGraphSettings();

					String sGraph = "plot" + Integer.toString(i1)
							+ Integer.toString(i2);
					xr.open(sGraph);

					// System.out.println(" ");
					// System.out.println("SCANNING:"+sGraph);

					// update the global graph-settings:
					gss.updateSettings(xr);
					jpp.setGraphSettings(gss);
					// hplot.printGraphSettings();
					// plot.update();
					// hplot.updateGraphIfShowing() ;

					// read data first without settings
					int k = 0;
					while (xr.open("datafile")) {

						// read setting
						LinePars lp = new LinePars("name");
						lp.updateSettings(xr);

						// lp.print();

						// read dat file
						script = fname + File.separator
								+ xr.getString("name", "");
						entry = zf.getJarEntry(script);
						in = zf.getInputStream(entry);
						isr = new InputStreamReader(in);
						reader = new BufferedReader(isr);

						// create dataarray
						IndexData++;
						DataArray data = new DataArray(IndexData, lp);
						boolean res = data.parse(reader);

						// print reading
						// System.out.println(" ");
						// System.out.println(" new plot:
						// "+Integer.toString(k));
						// lp.printLinePars();
						// data.print();

						// draw if no problems
						if (res) {

							// dataHplot[i1][i2].add(data);
							// jpp.insertData( IndexData, data);

							hplot.draw(data, IndexData);

						} else {
							System.out.println("Error in reading:" + script);
						}

						xr.close();
						xr.hide("datafile");
						k++;

					}
					xr.unHide();

					// update the global graph-settings:
					// gss.updateSettings(xr);
					// jpp.setGraphSettings(gss);
					// hplot.printGraphSettings();
					// hplot.update();
					// hplot.updateGraphIfShowing() ;

					// PrintStream out=System.out;
					// gss.print(out);

					// gss.setTitleName("new graph");
					// gss.setDataChanged(false);

					xr.close(); // close sGraph part
				}
			}

		} catch (IOException e) {
			ErrorMessage("Error in reading the file");
		}

		return true;
	}

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */

	private static void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}
