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
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;

import jplot.*;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jhplot.gui.HelpBrowser;
/**
 * A container to hold data points in X,Y,Z and their extensions dX,dY,dZ. Use
 * this class to draw a surface segment
 * 
 * @author S.Chekanov
 * 
 */

public class P3D extends Plottable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DataArray3D data;


	private Color c;

	private int w;


         /**
        * Construct an empty container

   **/
        public P3D() {
         this("No title");
        }

	/**
	 * Construct an empty container with a title
	 * 
	 * @param title
	 *            New title
	 */

	public P3D(String title) {
		data = new DataArray3D();
		this.title = title;
		this.c = Color.BLACK;
		this.w = 1;
	}


	/**
	 * Set a new title
	 * 
	 * @param title
	 *            New Title
	 */
	public void setTitle(String title) {
		this.title = title;

	}

	/**
	 * Get a new title
	 * 
	 * @return Title
	 */

	public String getTitle() {
		return this.title;

	}

	/**
	 * Get the color attribute
	 * 
	 * @return Color
	 */
	public Color getPenColor() {
		return c;

	}

	/**
	 * Sets the symbol color
	 * 
	 * @param c
	 *            Color
	 */
	public void setPenColor(Color c) {
		this.c = c;
	}

	/**
	 * Set width of the lines
	 * 
	 * @param w
	 *            Width of the lines
	 */
	public void setPenWidth(int w) {
		this.w = w;
	}

	/**
	 * Get width of the lines
	 * 
	 * @return Width of the lines
	 */
	public int getPenWidth() {
		return this.w;
	}

	/**
	 * Construct a P3D from a file. Data should be separated by space in 6
	 * columns: X,dX,Y,dY,Z,dZ
	 * 
	 * @param title
	 *            Title of the container
	 * @param sfile
	 *            File name with input
	 */
	public P3D(String title, String sfile) {

		this(title);
		read(sfile);
		

	}

	
	
	/**
	 * Read P3D from a file. The old content will be lost. The file should
	 * contain 6 columns: x,y,z, dx,dy,dz.
	 * Comment lines starting with "#" and "*" are ignored.
	 * 
	 * @param sfile
	 *            File name with input
	 * @return zero if success
	 */
	public int read(String sfile) {

		// clear all data
		clear();

		try {

			FileReader inF = new FileReader(new File(sfile));
			BufferedReader br = new BufferedReader(inF);

			String line;
			// dis.available() returns 0 if the file does not have more lines.
			while ((line = br.readLine()) != null) {

				line = line.trim();
				if (!line.startsWith("#") && !line.startsWith("*")) {

					StringTokenizer st = new StringTokenizer(line);
					int ncount = st.countTokens(); // number of words
					

					// String[] sword = new String[ncount];
					double[] snum = new double[ncount];

					if (ncount != 6) {
						ErrorMessage("Error in reading the file:\n"
								+ Integer.toString(ncount)
								+ " entries per line is found!");
					}

					// split this line
					int mm = 0;
					while (st.hasMoreTokens()) { // make sure there is stuff
						// to get
						String tmp = st.nextToken();

						// read double
						double dd = 0;
						try {
							dd = Double.parseDouble(tmp.trim());
						} catch (NumberFormatException e) {
							ErrorMessage("Error in reading the line "
									+ Integer.toString(mm + 1));
							return 3;
						}
						snum[mm] = dd;
						mm++;

					} // end loop over each line

					
						this.data.addPoint(snum[0], snum[1], snum[2], snum[3],
								snum[4], snum[5]);
					

				} // skip #

				// this statement reads the line from the file and print it to
				// System.out.println(line);
			}

			// dispose all the resources after using them.
			inF.close();
			br.close();

		} catch (FileNotFoundException e) {
			ErrorMessage("File not found!");
			e.printStackTrace();
			return 2;
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}

		return 0;

	}


	/**
	 * Write a P0D object to a serialized file 
	 * 
	 * @param name
	 *            serialized file name for output.
	 * 
	 * @return 
	 *        zero if no errors
	 */
	public int  writeSerialized(String name) {
		
		return  jhplot.io.Serialized.write(this, name);
		
	}	


	/**
	 * Read a P3D object from  a serialized file 
	 * 
	 * @param name
	 *            serialized file name for input.
	 * 
	 * @return 
	 *        new P3D object
	 */
	public P3D readSerialized(String name) {
		return (P3D)jhplot.io.Serialized.read(name);
	
	}
		
	
	
	
	/**
	 * Read P3D from a GZipped file. The old content will be lost. The file should
	 * contain 6 columns: x,y,z, dx,dy,dz.
	 * Comment lines starting with "#" and "*" are ignored.
	 * 
	 * @param sfile
	 *            File name with input
	 * @return zero if success
	 */

	public int readGZip(String sfile) {

		// clear all data
		clear();

		try {

			FileInputStream fin = new FileInputStream(sfile);
			GZIPInputStream gzis = new GZIPInputStream(fin);
			InputStreamReader xover = new InputStreamReader(gzis);
			BufferedReader is = new BufferedReader(xover);

			String line;
			while ((line = is.readLine()) != null) {

				line = line.trim();
				if (!line.startsWith("#") && !line.startsWith("*")) {

					StringTokenizer st = new StringTokenizer(line);
					int ncount = st.countTokens(); // number of words
					

					// String[] sword = new String[ncount];
					double[] snum = new double[ncount];

					if (ncount != 6) {
						ErrorMessage("Error in reading the file:\n"
								+ Integer.toString(ncount)
								+ " entries per line is found!");
						return 3;
					}

					// split this line
					int mm = 0;
					while (st.hasMoreTokens()) { // make sure there is stuff
						// to get
						String tmp = st.nextToken();

						// read double
						double dd = 0;
						try {
							dd = Double.parseDouble(tmp.trim());
						} catch (NumberFormatException e) {
							ErrorMessage("Error in reading the line "
									+ Integer.toString(mm + 1));
							return 3;
						}
						snum[mm] = dd;
						mm++;

					} // end loop over each line

					
						this.data.addPoint(snum[0], snum[1], snum[2], snum[3],
								snum[4], snum[5]);
					

				} // skip #

				// this statement reads the line from the file and print it to
				// System.out.println(line);
			}

			fin.close();
			is.close();

		} catch (FileNotFoundException e) {
			ErrorMessage("File not found!");
			e.printStackTrace();
			return 2;
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}

		return 0;
	}

	
	/**
	 * Read P3D from a Zipped file. The old content will be lost. The file should
	 * contain 6 columns: x,y,z, dx,dy,dz.
	 * Comment lines starting with "#" and "*" are ignored.
	 * 
	 * @param sfile
	 *            File name with input
	 * @return zero if success
	 */
	public int readZip(String sfile) {

		// clear all data
		clear();

		try {
			ZipFile zf = new ZipFile(sfile);
			Enumeration entries = zf.entries();

			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			while (entries.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) entries.nextElement();
				// System.out.println("Read " + ze.getName() + "?");
				String inputLine = input.readLine();
				if (inputLine.equalsIgnoreCase("yes")) {
					long size = ze.getSize();
					if (size > 0) {
						// System.out.println("Length is " + size);
						BufferedReader br = new BufferedReader(
								new InputStreamReader(zf.getInputStream(ze)));
						String line;
						while ((line = br.readLine()) != null) {

							line = line.trim();

							if (!line.startsWith("#") && !line.startsWith("*")) {

								StringTokenizer st = new StringTokenizer(line);
								int ncount = st.countTokens(); // number of
																// words
							

								// String[] sword = new String[ncount];
								double[] snum = new double[ncount];

								if (ncount != 6) {
									ErrorMessage("Error in reading the file:\n"
											+ Integer.toString(ncount)
											+ " entries per line is found!");
									return 3;
								}

								// split this line
								int mm = 0;
								while (st.hasMoreTokens()) { // make sure there
																// is stuff
									// to get
									String tmp = st.nextToken();

									// read double
									double dd = 0;
									try {
										dd = Double.parseDouble(tmp.trim());
									} catch (NumberFormatException e) {
										ErrorMessage("Error in reading the line "
												+ Integer.toString(mm + 1));
										return 3;
									}
									snum[mm] = dd;
									mm++;

								} // end loop over each line

									this.data.addPoint(snum[0], snum[1],
											snum[2], snum[3], snum[4], snum[5]);
								
							} // skip #

						}
						br.close();
					}
				}
			}

		} catch (FileNotFoundException e) {
			ErrorMessage("File not found:" + sfile);
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}

		return 0;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Write a P3D to an external file.
	 * 
	 * @param name
	 *            File name with output
	 */
	public void toFile(String name) {

		DecimalFormat dfb = new DecimalFormat("##.#####E00");
		Date dat = new Date();
		String today = String.valueOf(dat);

		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);

			tx.println("# DataMelt: output from P3D " + this.title);
			tx.println("# DataMelt: created at " + today);
			tx.println("# x,y,z");
			tx.println("#");
			for (int i = 0; i < size(); i++) {

				String x = dfb.format(getX(i));
				String y = dfb.format(getY(i));
				String z = dfb.format(getZ(i));

				String dx = dfb.format(getDX(i));
				String dy = dfb.format(getDY(i));
				String dz = dfb.format(getDZ(i));

				tx.println(x + "  " + dx + "  " + y + "  " + dy + "  " + z
						+ "  " + dz);
			}
			f1.close();

		} catch (IOException e) {
			ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	/**
	 * Print a P3D container on the screen
	 * 
	 */
	public void print() {

		this.data.print();

	}

	/**
	 * Merge two P3D containers
	 * 
	 * @param a
	 *            Container to be added
	 * @return New P3D container
	 */

	public P3D merge(P3D a) {

		for (int i = 0; i < a.size(); i++) {
			add(a.getX(i), a.getDX(i), a.getY(i), a.getDY(i), a.getZ(i), a
					.getDZ(i));

		}

		return this;

	}

	/**
	 * Set data in a form of DataArray
	 * 
	 * @param data
	 *            input data
	 * 
	 */
	public void setDataArray(DataArray3D data) {
		this.data = data;

	}

	/**
	 * Return a DataArray3D container.
	 * 
	 * @return Container of type DataArray3D
	 */
	public DataArray3D getDataArray() {

		return data;

	}

	/**
	 * Adds values of a plot-point pair (X,dX,Y,dY,Z,dZ).
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param dx
	 *            extent in X
	 * @param y
	 *            Y-value of the plot-point
	 * @param dy
	 *            extent in Y
	 * @param z
	 *            Z-value of the plot-point
	 * @param dz
	 *            extent in Z
	 * 
	 */
	public void add(double x, double dx, double y, double dy, double z,
			double dz) {
		data.addPoint(x, dx, y, dy, z, dz);
	}

	/**
	 * Sets the values of (x,dx,y,dy,z,dz).
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            X-value of the plot-point
	 * @param dx
	 *            extent in X
	 * @param y
	 *            Y-value of the plot-point
	 * @param dy
	 *            extent in Y
	 * @param z
	 *            Z-value of the plot-point
	 * @param dz
	 *            extent in Z
	 * 
	 * 
	 */
	public void set(int i, double x, double dx, double y, double dy, double z,
			double dz) {
		data.setPoint(i, x, dx, y, dy, z, dz);
	}

	/**
	 * Return the length of the data vector.
	 * 
	 * @return length of the PlotPoint vector
	 */
	public int size() {
		return data.size();
	}

	/**
	 * get a copy of the current holder
	 */
	public P3D getCopy() {
		P3D tmp = new P3D(title);
		tmp.setPenColor(getPenColor());
		tmp.setPenWidth(getPenWidth());
		tmp.setDataArray(data);
		return tmp;
	}

	/**
	 * Get array representing X-values
	 * 
	 * @return array with X values
	 */

	public double[] getArrayX() {

		double[] tmp = new double[data.size()];

		for (int i = 0; i < data.size(); i++) {
			tmp[i] = data.getX(i);
		}

		return tmp;
	}

	/**
	 * Get array representing Y-values
	 * 
	 * @return array with Y values
	 */

	public double[] getArrayY() {

		double[] tmp = new double[data.size()];

		for (int i = 0; i < data.size(); i++) {
			tmp[i] = data.getY(i);
		}

		return tmp;
	}

	/**
	 * Get array representing Z-values
	 * 
	 * @return array with Y values
	 */

	public double[] getArrayZ() {

		double[] tmp = new double[data.size()];

		for (int i = 0; i < data.size(); i++) {
			tmp[i] = data.getZ(i);
		}

		return tmp;
	}

	/**
	 * Return a specific X-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getX(int i) {
		return data.getX(i);
	}

	/**
	 * Return a specific dX-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of dx at index i
	 */
	public double getDX(int i) {
		return data.getDX(i);
	}

	/**
	 * Return a specific Y-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getY(int i) {
		return data.getY(i);
	}

	/**
	 * Return a specific dY-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of dy at index i
	 */
	public double getDY(int i) {
		return data.getDY(i);
	}

	/**
	 * Return a specific Z-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of z at index i
	 */
	public double getZ(int i) {
		return data.getZ(i);
	}

	/**
	 * Return a specific dZ-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of dz at index i
	 */
	public double getDZ(int i) {
		return data.getDZ(i);
	}

	/**
	 * Returns the maximum value in the range.
	 * 
	 * @param axis
	 *            defines to which axis this function applies. axis=0 - X,
	 *            axis=1 - Y, axis=2 - Z,
	 * @return the maximum value.
	 */
	public double getMax(int axis) {
		return data.getMaxValue(axis);
	}

	/**
	 * Returns the minimum value in the range. axis=0 - X, axis=1 - Y, axis=2 -
	 * Z.
	 * 
	 * @param axis
	 *            defines to which axis this function applies.
	 * @return the minimum value.
	 */
	public double getMin(int axis) {
		return data.getMinValue(axis);
	}

	/**
	 * Returns the mean value in X.
	 * 
	 * @return Mean value in X
	 */

	public double meanX() {
		return data.meanX();
	}

	/**
	 * Returns the mean value in Y.
	 * 
	 * @return Mean value in Y
	 */

	public double meanY() {
		return data.meanY();
	}

	/**
	 * Returns the mean value in Z.
	 * 
	 * @return Mean value in Y
	 */

	public double meanZ() {
		return data.meanZ();
	}

	/**
	 * Clear the container
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * Print the P2D container to a Table in a separate Frame. The numbers are
	 * formatted to scientific format. One can sort and search the data in this
	 * table (but not modify)
	 */

	public void toTable() {

		new HTable(this);

	}


	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */

	private void ErrorMessage(String a) {
               jhplot.utils.Util.ErrorMessage(a);
	}


	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
	
	
	
	
	
}
