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


import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.awt.Color;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import jhplot.gui.HelpBrowser;
import jhplot.io.PReader;
import jplot.DataArray2D;


/**
 * A container to hold data points in X,Y,Z. Should be used to plot data in 3D.
 * This is a high-performance array implementation.
 * 
 * @author S.Chekanov
 * 
 */

public class P2D extends Plottable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DoubleArrayList dataX;
	private DoubleArrayList dataY;
	private DoubleArrayList dataZ;
	private Color c;

	private int s;


         /**
        * Construct an empty container

   **/
        public P2D() {
         this("No title");
        }


	/**
	 * Construct an empty container with a title
	 * 
	 * @param title
	 *            New title
	 */

	public P2D(String title) {
		
		dataX = new DoubleArrayList();
		dataY = new DoubleArrayList();
		dataZ = new DoubleArrayList();
		this.title = title;
		this.c = Color.BLACK;
		this.s = 5;
	}

	
	
	

	/**
	 * Get color attribute
	 * 
	 * @return Color
	 */
	public Color getSymbolColor() {
		return c;

	}

	/**
	 * Sets symbol color
	 * 
	 * @param c
	 *            Color
	 */
	public void setSymbolColor(Color c) {
		this.c = c;
	}

	/**
	 * Get size of the symbols
	 * 
	 * @return size of the symbols
	 */
	public int getSymbolSize() {
		return s;

	}

	
	/**
	 * Read P2D from a file. The old content will be lost. The file should
	 * contain 3 columns: x,y,z. Comment lines starting with "#" and "*" are ignored.
	 * 
	 * @param br
	 *            Input buffered reader
	 *            
	 * @return zero if success
	 */
	public int read(BufferedReader br) {

		// clear all data before reading
		clear();

		try {

			String line;
			// dis.available() returns 0 if the file does not have more lines.
			while ((line = br.readLine()) != null) {

				line = line.trim();
				if (!line.startsWith("#") && !line.startsWith("*")) {

					StringTokenizer st = new StringTokenizer(line);
					int ncount = st.countTokens(); // number of words
				

					double[] snum = new double[ncount];

					if (ncount != 3) {
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

					
				    dataX.add(snum[0]);
				    dataY.add(snum[1]);
				    dataZ.add(snum[2]);
					

				} // skip #

				// this statement reads the line from the file and print it to
				// System.out.println(line);
			}

			// dispose all the resources after using them.
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
	 * Read P2D from a file. 
	 * 
	 * The old content will be lost. Use a space to separate values in columns and tab to put new row.
	 * Comment lines starting with "#" and "*" are ignored.
	 * 
	 * 
	 * @param sfile
	 *            input file
	 * @return zero if success
	 */
	public int read(File sfile) {

		
		BufferedReader is = PReader.read(sfile);
		if (is == null) return 1;
		return read(is);


	}

	
	/**
	 * Read data from URL.
	 * Use a space to separate values in columns and tab to put new row.
	 * @param url  URL location of input file
	 */
		
	public int read(URL url)
	{ 
	  
		BufferedReader is = PReader.read(url);
		if (is == null) return 1;
		return read(is);
	  
	}
	
	/**
	 * Read P2D from a file.
	 * It can read URL if the string starts from http or ftp, otherwise a file on the file system is assumed.
	 * <p>
	 * The old content will be lost. Use a space to separate values in columns and tab to put new row.
	 * Comment lines starting with "#" and "*" are ignored.
	 * 
	 * @param sfile
	 *            File name with input
	 * @return zero if success
	 */
	public int read(String sfile) {

				
		BufferedReader is = PReader.read(sfile);
		if (is == null) return 1;
		return read(is);
		


	}
	
	
	/**
	 * Read P2D from a GZiped file. 
	 * It can read URL if the string starts from http or ftp, otherwise a file on the file system is assumed.
	 * <p>
	 * Use a space to separate values in columns and tab to put new row.
	 * @param sfile
	 *            File name with input (extension .gz)
	 * @return zero if success
	 */
	public int readGZip(String sfile) {

		
		BufferedReader is = PReader.readGZip(sfile);
		if (is == null) return 1;
		return read(is);
		
		
	}
	
	
	
	/**
	 * Read P1D from a Zipped file. The old content will be lost. The file
	 * should contain 2, or 4, or 6, or 10 columns: 1) x,y: data without any
	 * errors 2) x,y, y(upper), y(lower) - data with 1st level errors on Y 3)
	 * x,y, x(left), x(right), y(upper), y(lower) - data with 1st level errors
	 * on X and Y 4) x,y, x(left), x(right), y(upper), y(lower), x(leftSys),
	 * x(rightSys), y(upperSys), y(lowerSys) - data with X and Y and 1st and 2nd
	 * level errors
	 * 
	 * @param sfile
	 *            File name with input (extension zip)
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

								if (ncount != 3 ) {
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

								
								dataX.add(snum[0] );
								dataY.add(snum[1] );
								dataZ.add(snum[2] );
								
								

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
	 * Sets the symbol size
	 * 
	 * @param s
	 *            symbol size
	 */
	public void setSymbolSize(int s) {
		this.s = s;
	}

	/**
	 * Construct a P2D from a file. Data should be separated by space in 3
	 * columns.
	 * 
	 * @param title
	 *            Title of the container
	 * @param sfile
	 *            File name with input.   It can be either a file on a file system or URL location (must start from http or ftp)
	 */
	public P2D(String title, String sfile) {

		this(title);
		read(sfile);


	}

	/**
	 * Write a P2D to an external file.
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

			tx.println("# DataMelt: output from P2D " + this.title);
			tx.println("# DataMelt: created at " + today);
			tx.println("# x,y,z");
			tx.println("#");
			for (int i = 0; i < size(); i++) {

				String x = dfb.format(getX(i));
				String y = dfb.format(getY(i));
				String z = dfb.format(getZ(i));
				tx.println(x + "  " + y + "  " + z);
			}
			f1.close();

		} catch (IOException e) {
			ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	
	/**
	 * Merge two P2D containers
	 * 
	 * @param a
	 *            Container to be added
	 * @return New P2D container
	 */

	public P2D merge(P2D a) {

		for (int i = 0; i < a.size(); i++) {
			add(a.getX(i), a.getY(i), a.getZ(i));
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
	public void setDataArray(DataArray2D data) {
		
		for (int i=0; i<data.size(); i++){
		  dataX.add(data.getX(i));
		  dataY.add(data.getY(i));
		  dataZ.add(data.getZ(i));
		}
	}

	/**
	 * Return a DataArray2D container.
	 * 
	 * @return Container of type DataArray
	 */
	public DataArray2D getDataArray() {
		DataArray2D  data = new DataArray2D(size());
		for (int i=0; i<size(); i++)
		data.addPoint(dataX.getQuick(i), dataY.getQuick(i),dataZ.getQuick(i));
		
		return data;

	}

	/**
	 * Adds values of a plot-point pair (X,Y,Z).
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 * @param z
	 *            Z-value of the plot-point
	 */
	public void add(double x, double y, double z) {
		dataX.add(x);
		dataY.add(y);
		dataZ.add(z);
	}





	/**
	 * Fill a P2D container from 3 arrays. If it is not empty, add values will
	 * be appended. It is assumed that all errors are zero.
	 * 
	 * @param xa
	 *            array with X values
	 * @param ya
	 *            array with Y values
	 * @param za
	 *            array with Z values
	 * 
	 */
	public void fill(double[] xa, double[] ya, double[] za) {

		if (xa.length != ya.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		if (xa.length != za.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		dataX.elements(xa);
		dataY.elements(xa);
		dataZ.elements(xa);
		

	}

	/**
	 * Sets the values of (x,y,z).
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param z
	 *            z-value of the plot-point
	 */
	public void set(int i, double x, double y, double z) {
		
		dataX.set(i,x);
		dataY.set(i,y);
		dataZ.set(i,z);
		
	}

	/**
	 * Return the length of the data vector.
	 * 
	 * @return length of the PlotPoint vector
	 */
	public int size() {
		
		return dataX.size();
	}

	/**
	 * Get a copy of the current holder
	 */
	public P2D copy() {
		return copy(this.title);
	}

/**
	 * Convert to a string
	 * @return
	 *        String representing the data
	 */
	public String  toString() {

		String s= new String();
		for (int i = 0; i < dataX.size(); i++) {
			s= s+Double.toString(dataX.getQuick(i))+"  "+ Double.toString(dataY.getQuick(i))+"  "+Double.toString(dataZ.getQuick(i))+"\n";
		}
		return s;

	}




 /**
         * create an exact of the current holder
         *
         * @param newtitle
         *            new title
         */
        public P2D copy(String newtitle) {
                P2D tmp = new P2D(newtitle);
                
                dataX = new DoubleArrayList();
                dataY = new DoubleArrayList();
                dataZ = new DoubleArrayList();
                
                dataX.clear();
                dataY.clear();
                dataZ.clear();
                
                for (int i = 0; i < size(); i++) {
                	dataX.add(getX(i));dataY.add(getY(i)); dataZ.add(getZ(i));
                }
                tmp.setDoubleArrayLists(dataX,dataY,dataZ);
                return tmp;
        }




/**
 * Set the array from DoubleArrayLists
 * @param dataX2 input X 
 * @param dataY2 input Y
 * @param dataZ2 input Z
 */

	public  void setDoubleArrayLists(DoubleArrayList dataX2,
		DoubleArrayList dataY2, DoubleArrayList dataZ2) {
	
		this.dataX=dataX2;
		this.dataY=dataY2;
		this.dataZ=dataZ2;
	
}



	/**
	 * Get array representing X-values
	 * 
	 * @return array with X values
	 */

	public double[] getArrayX() {
                dataX.trimToSize();
		return dataX.elements();
	}


	/**
	 * Write a  object to a serialized file 
	 * 
	 * @param name
	 *            serialized file name for output.
	 * 
	 * @return 
	 *        zero if no errors
	 */
	public int  writeSerialized(String name) {
		
		return jhplot.io.Serialized.write(this, name);
		
	}	


	/**
	 * Read a P2D object from  a serialized file 
	 * 
	 * @param name
	 *            serialized file name for input.
	 * 
	 * @return 
	 *        new P2D object
	 */
	public P2D readSerialized(String name) {
		
		return (P2D)jhplot.io.Serialized.read(name);
		
	}
		
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Get array representing Y-values
	 * 
	 * @return array with Y values
	 */

	public double[] getArrayY() {
                dataY.trimToSize();
		return dataY.elements();
		
	}

	/**
	 * Get array representing Z-values
	 * 
	 * @return array with Y values
	 */

	public double[] getArrayZ() {
                dataZ.trimToSize();
		return dataZ.elements();
		
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
		return dataX.get(i);
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
		return dataY.get(i);
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
		return dataZ.get(i);
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
		
		if (axis==0) return Descriptive.max(dataX);
		if (axis==1) return Descriptive.max(dataY);
		if (axis==2) return Descriptive.max(dataZ);
		return Descriptive.max(dataX);
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
		if (axis==0) return Descriptive.min(dataX);
		if (axis==1) return Descriptive.min(dataY);
		if (axis==2) return Descriptive.min(dataZ);
		return Descriptive.min(dataX);
	}

	/**
	 * Returns the mean value in X.
	 * @return Mean value in X
	 */

	public double meanX() {
		return  Descriptive.mean(dataX);
	}

	/**
	 * Returns the mean value in Y.
	 * @return Mean value in Y
	 */

	public double meanY() {
		return   Descriptive.mean(dataY);
	}

	/**
	 * Returns the mean value in Z.
	 * @return Mean value in Y
	 */

	public double meanZ() {
		return Descriptive.mean(dataZ);
	}

       

        /**
        * Returns the mean value for any axis 
        * @param axis
        *            axis (0,1,2) 
        * @return Mean value for axis (0,1,2) 
        */
        public double mean(int axis) {
                if (axis==0) return meanX();
                if (axis==1) return meanY();
                if (axis==2) return meanZ();
                return -999999.;
        }






	/**
	 * Clear the container
	 */
	public void clear() {
		dataX.clear();
		dataY.clear();
		dataZ.clear();
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
