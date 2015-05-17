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

import java.io.*;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.swing.JOptionPane;

import jhplot.HPlot;
import jhplot.JHPlot;
import jplot.DataArray;
import jplot.GraphSettings;
import jplot.JPlot;
import jplot.Utils;
import jplot.XMLWrite;
import jplot.panels.PanelPlot;

/**
 * A class to write graphics in XML file.
 * 
 * @author S.Chekanov
 * 
 */

public class JHwriter {

	/**
	 * Writes JHPlot file to XML file
	 * 
	 * 
	 */

	private static HPlot hplot;

	private static Vector<String> FileList;

	private static String fname; // file name

	private static String fpath; // path to this file

	private static String fpathname; // name with full path

	private static String fpathout; // output file

	private static boolean res = true;

	private static File scriptFile;

	/**
	 * Write the file
	 * 
	 * @param f input file
         * @param hplot HPlot object
	 */

	public static boolean writeScript(final File f, final HPlot hplot) {

		boolean res = true;

		fname = f.getName(); // file name
		fpath = f.getParent(); // path to this file
		fpathname = f.getAbsolutePath();
		fpathout = fpathname + ".jhp";

		FileList = new Vector<String>();

		// System.out.println(fname);
		// System.out.println(fpath);
		// System.out.println(fpathname);

		// create temporary directory
		boolean succ1;
		succ1 = f.mkdir();
		if (!succ1) {
			System.out.println("Cannot make :" + f);
			return false;
		}

		String sindex = fpathname + File.separator + fname + ".xml";
		String afile = fname + File.separator + fname + ".xml";

		File findex = new File(sindex);

		FileList.addElement(afile);
		res = true;
		XMLWrite xw = new XMLWrite();
		xw.open("jhplot"); // start of writing jplot settings

		// ----- info
		xw.open("info");
		xw.setData("version", JHPlot.getVersion());
		xw.setData("author", JHPlot.getAuthor());
		xw.setData("build", JHPlot.getBuildTime());
		xw.setData("created", JHPlot.getCreatedBy());
		xw.close(); // close into

		// ---- global panels
		xw.open("globalpanel");
		// graph-panel size on which we display the plot:
		xw.setData("width", hplot.getSizeX());
		xw.setData("height", hplot.getSizeY());
		// number of plots
		xw.setData("numberX", hplot.getNtotX());
		xw.setData("numberY", hplot.getNtotY());
		xw.close();

		// global canvas attributes
		xw.open("margintop");
		if (!hplot.getTextTop().equals(" "))
			xw.setData("text", hplot.getTextTop());
		xw.set("fcolor", hplot.getTextTopColor());
		xw.set("font", hplot.getTextTopFont());
		xw.set("bcolor", hplot.getTextTopColorBack());
		xw.setData("msize", hplot.getMarginSizeTop());
		xw.setData("mposX", hplot.getTextPosTopX());
		xw.setData("mposY", hplot.getTextPosTopY());
		xw.setData("mrotation", hplot.getTextRotationTop());
		xw.close();

		xw.open("marginright");
		if (!hplot.getTextRight().equals(" "))
			xw.setData("text", hplot.getTextRight());
		xw.set("fcolor", hplot.getTextRightColor());
		xw.set("font", hplot.getTextRightFont());
		xw.set("bcolor", hplot.getTextRightColorBack());
		xw.setData("msize", hplot.getMarginSizeRight());
		xw.setData("mposX", hplot.getTextPosRightX());
		xw.setData("mposY", hplot.getTextPosRightY());
		xw.setData("mrotation", hplot.getTextRotationRight());
		xw.close();

		xw.open("marginleft");
		if (!hplot.getTextLeft().equals(" "))
			xw.setData("text", hplot.getTextLeft());
		xw.set("fcolor", hplot.getTextLeftColor());
		xw.set("font", hplot.getTextLeftFont());
		xw.set("bcolor", hplot.getTextLeftColorBack());
		xw.setData("msize", hplot.getMarginSizeLeft());
		xw.setData("mposX", hplot.getTextPosLeftX());
		xw.setData("mposY", hplot.getTextPosLeftY());
		xw.setData("mrotation", hplot.getTextRotationLeft());
		xw.close();

		xw.open("marginbottom");
		if (!hplot.getTextBottom().equals(" "))
			xw.setData("text", hplot.getTextBottom());
		xw.set("fcolor", hplot.getTextBottomColor());
		xw.set("font", hplot.getTextBottomFont());
		xw.set("bcolor", hplot.getTextBottomColorBack());
		xw.setData("msize", hplot.getMarginSizeBottom());
		xw.setData("mposX", hplot.getTextPosBottomX());
		xw.setData("mposY", hplot.getTextPosBottomY());
		xw.setData("mrotation", hplot.getTextRotationBottom());
		xw.close();

		// this order necessary for grid layout
		for (int i2 = 0; i2 < hplot.getNtotY(); i2++) {
			for (int i1 = 0; i1 < hplot.getNtotX(); i1++) {

				hplot.cd(i1 + 1, i2 + 1);

				String sGraph = "plot" + Integer.toString(i1)
						+ Integer.toString(i2);

				xw.open(sGraph);
				GraphSettings gss = hplot.getGraphSettings();
				gss.getSettings(xw);

				// write the settings for each datafile:
				JPlot jpp = hplot.getJPlot();
				Vector plotPanels = jpp.getAllPanels();
				Vector data = jpp.getDataArray();

				// System.out.println(nFiles);
				for (int k = 0; k < data.size(); k++) {

					String sdata = sGraph + "-data" + Integer.toString(k)
							+ ".d";

					DataArray da = (DataArray) data.get(k);
					String aname = fname + File.separator + sdata;
					String dataFile = fpathname + File.separator + sdata;

					// open datafile XML line
					((PanelPlot) plotPanels.get(k)).getDataFile().getSettings(
							xw, sdata);
					FileList.addElement(aname);
					da.toFile(dataFile, da.getTitle());

				} // end run over datafilres

				xw.close(); // sGraph
			}
		}

		xw.close(); // close jhplot

		try {
			PrintWriter pw = new PrintWriter(new FileWriter(findex));
			pw.println(xw.getSettings());
			xw.clear();
			pw.close();

		} catch (IOException e) {
			res = false;
			Utils.bummer(hplot.getFrame(),
					"It's somehow impossible to write to " + sindex.toString());
			res = false;
		}

		// zip it
		try {
			ZipDic(new File(fpathout));
			FileList.clear();
		} catch (Exception e) {
			System.out.println("Cannot make ZIP file" + fpathout);
			res = false;
		}

		// remove it
		boolean success = deleteDir(new File(fpathname));
		if (!success) {
			System.out.println("cannot remove directory: " + fpathname);
		}

		return res;
	}

	private static void ZipDic(File zipFile) throws Exception {

		// Create a buffer for reading the files
		byte[] buf = new byte[4096];
		// System.out.println("Create zipped file: "+zipFile);
		try {
			JarOutputStream out = new JarOutputStream(new FileOutputStream(
					zipFile));

			// Compress the files
			for (int i = 0; i < FileList.size(); i++) {

				// System.out.println("Put file " + (String)
				// FileList.elementAt(i)
				// + " to " + zipFile);
				FileInputStream in = new FileInputStream((String) FileList
						.elementAt(i));
				// Add ZIP entry to output stream.
				out.putNextEntry(new JarEntry((String) FileList.elementAt(i)));

				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();
			}

			// Complete the ZIP file
			out.close();
			// remove index file
		} catch (IOException e) {
			// System.out.println("Cannot make zipped file: "+zipFile);
		}

	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
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
