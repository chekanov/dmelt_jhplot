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

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.ITree;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jhplot.stat.Statistics;
import jhplot.gui.HelpBrowser;
import jhplot.io.PReader;
import jhplot.math.DoubleArray;

/**
 * Data holder in 2D for double values. It is similar  to a matrix in 2D. 
 * It has columns and rows. The number of columns
 * may not be the same. For example, data can be presented as: <p>
 * <pre>
 * 1 2 3 4 5
 * 12 3 4
 * 1 3 4 555 5 66 77
 * 1 2 2 33 434 4
 * </pre>
 * <p>
 * All numbers are expected to be in double precision. <p> 
 * It extends ArrayList and adds many new
 * features for data manipulation. The class does not have graphical option (use
 * H1D to show the data or methods of this class which transform P0D to a H1D
 * histogram).
 * 
 * @author S.Chekanov
 * 
 */

public class PND implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<double[]> data;
	private String title;
	private int dimension;
	private double min = 0;
	private double max = 0;

	/**
	 * Construct an empty container with a title
	 * 
	 * @param title
	 *            A title
	 * 
	 */

	public PND(String title) {
		data = new ArrayList<double[]>();
		this.title = title;
		this.dimension = 0;

	}

	/**
	 * Construct a copy from a PND. If the last argument is true, a shallow copy
	 * of a collection. In this case a new collection contains references to
	 * same objects as the source collection. Data are not cloned
	 * 
	 * @param title
	 *            new title
	 * @param shallow
	 *            if true, a shallow copy of a collection.
	 * @param pnd
	 *            inpit data
	 */
	public PND(String title, boolean shallow, PND pnd) {
		this.title = title;
		this.data = new ArrayList<double[]>();
		this.dimension = pnd.getDimension();

		if (shallow) {
			// ArrayList<Double> jplot3d=p0d.getArrayList();
			data = (ArrayList<double[]>) (pnd.getArrayList().clone());
		} else {
			for (int i = 0; i < pnd.size(); i++)
				data.add(pnd.get(i));
		}

	}

	/**
	 * Construct a container with a title from external file (see the method
	 * toFile() how to write such ASCII file).
	 * 
	 * @param title
	 *            A title
	 * 
	 * @param file
	 *            input file name. It can be either a file on a file system or
	 *            URL location (must start from http or ftp)
	 */

	public PND(String title, String file) {
		this.dimension = 0;
		data = new ArrayList<double[]>();
		this.title = title;
		read(file);

	}

	/**
	 * Construct an empty container with no title
	 * 
	 */
	public PND() {
		this("NOT SET");
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
	 * Set a new title
	 * 
	 * @param title
	 *            New Title
	 */
	public void setName(String title) {
		this.title = title;

	}

	/**
	 * Get a new title.
	 * 
	 * @return Title
	 */

	public String getTitle() {
		return this.title;

	}

	/**
	 * Get a new title
	 * 
	 * @return Title
	 */

	public String getName() {
		return this.title;

	}

	/**
	 * Clear the container
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * Return a specific row as array
	 * 
	 * @param row
	 *            index of the row
	 * 
	 * @return array of values
	 */
	public double[] get(int row) {
		return (double[]) data.get(row);
	}

	/**
	 * Return a specific row as P0D
	 * 
	 * @param row
	 *            index of the row
	 * 
	 * @return array of values
	 */
	public P0D getRowP0D(int row) {
		P0D p = new P0D(title + ":row:" + Integer.toString(row));
		p.fill((double[]) data.get(row));
		return p;
	}

	/**
	 * Return a specific value.
	 * 
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * 
	 * @return value
	 */
	public double get(int row, int column) {
		double[] tmp = (double[]) data.get(row);
		return tmp[column];
	}

	/**
	 * Get a string representing PND
	 * 
	 * 
	 * @return String with all values
	 */
	public String toString() {

		String tmp = "\nPND: " + title + "\n";

		for (int i = 0; i < data.size(); i++) {
			double[] tt = (double[]) data.get(i);
			for (int j = 0; j < tt.length; j++) {
				tmp += Double.toString(tt[j]);
				tmp += " ";

			}
			tmp += "\n";
		}

		return tmp;
	}

	/**
	 * Print PND to System.out.
	 */

	public void print() {

		System.out.println(this.toString());

	}

	/**
	 * Return a specific column as an array
	 * 
	 * @param column
	 *            index of the column
	 * 
	 * @return P0D array with values.
	 * 
	 */
	public P0D getP0D(int column) {

		P0D tmp = new P0D(title + " :" + Integer.toString(column));

		if (column >= dimension) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		for (int j = 0; j < data.size(); j++) {
			double[] tt = (double[]) data.get(j);
			tmp.add(tt[column]);
		}

		return tmp;
	}

	/**
	 * Return a specific column as an array
	 * 
	 * @param column
	 *            index of the column
	 * 
	 * @return array with values.
	 * 
	 */
	public double[] getColumn(int column) {

		double[] tmp = new double[data.size()];

		if (column >= dimension) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		for (int j = 0; j < data.size(); j++) {
			double[] tt = (double[]) data.get(j);
			tmp[j] = tt[column];
		}

		return tmp;
	}

	/**
	 * Return a specific column as P0D object
	 * 
	 * @param column
	 *            index of the column
	 * 
	 * @return array with values.
	 * 
	 */
	public P0D getColumnP0D(int column) {

		P0D tmp = new P0D("Column:" + Integer.toString(column));

		if (column >= dimension) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		for (int j = 0; j < data.size(); j++) {
			double[] tt = (double[]) data.get(j);
			tmp.add(tt[column]);
		}

		return tmp;
	}

	/**
	 * Return 2 columns as P1D to show as a X-Y plot
	 * 
	 * @param c1
	 *            index of the first column
	 * @param c2
	 *            index of the second column
	 * 
	 * @return P1D array with X-Y values.
	 * 
	 */
	public P1D getP1D(int c1, int c2) {

		P1D tmp = new P1D(title + ":" + Integer.toString(c1) + "-"
				+ Integer.toString(c2));
		tmp.setDimension(2);

		if (c1 >= dimension || c2 >= dimension) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		for (int j = 0; j < data.size(); j++) {
			double[] tt = (double[]) data.get(j);
			tmp.add(tt[c1], tt[c2]);
		}

		return tmp;
	}

	/**
	 * Return 3 columns as P2D to show as a X-Y-Z plot
	 * 
	 * @param c1
	 *            index of the first column
	 * @param c2
	 *            index of the second column
	 * @param c3
	 *            index of the third column
	 * 
	 * @return P3D array with X-Y-Z values.
	 * 
	 */
	public P2D getP2D(int c1, int c2, int c3) {

		P2D tmp = new P2D(title + ":" + Integer.toString(c1) + "-"
				+ Integer.toString(c2) + "-" + Integer.toString(c3));

		if (c1 >= dimension || c2 >= dimension || c3 >= dimension) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		for (int j = 0; j < data.size(); j++) {
			double[] tt = (double[]) data.get(j);
			tmp.add(tt[c1], tt[c2], tt[c3]);
		}

		return tmp;
	}

	
	
	
	

	/**
	 * Get last dimension of the data (or number of columns).
	 * The stored dimention is the one set after the last call "add",
	 * or if it set manually.
	 * 
	 * @return dimension (number of elements in a row)
	 */
	public int getDimension() {
		return dimension;
	}

	
	/**
	 * Get the numbers of columns.
	 * The stored dimention is the one set after the last call "add",
	 * or if it set manually.
	 * 
	 * @return dimension (number of elements in a row)
	 */
	public int[] getDimensions() {
		
		int[] ii = new int[data.size()];
		for (int j = 0; j < data.size(); j++) {
			double[] tt = (double[]) data.get(j);
			ii[j]=tt.length;
		}
		
		return ii;
	}

	
	/**
	 * Return a specific row as array
	 * 
	 * @param row
	 *            index of the row
	 * 
	 * @return array of values
	 */
	public P0D getRow(int row) {

		P0D tmp = new P0D(title + " row=" + Integer.toString(row));

		if (row > data.size()) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		double[] tt = (double[]) data.get(row);
		tmp.setArray(tt);
		return tmp;
	}

	/**
	 * Get data in form of ArrayList
	 * 
	 * @return data in form of ArrayList
	 */
	public ArrayList<double[]> getArrayList() {
		return this.data;
	}

	/**
	 * Get the data point set for JAIDA
	 * 
	 * @return
	 */

	public IDataPointSet getIDataPointSet() {

		IAnalysisFactory af = IAnalysisFactory.create();
		ITree tree = af.createTreeFactory().create();
		IDataPointSetFactory dpsf = af.createDataPointSetFactory(tree);
		IDataPointSet fDps2D = dpsf.create(getTitle(), getTitle(), dimension);

		for (int i = 0; i < data.size(); i++) {
			fDps2D.addPoint();
			double[] tt = (double[]) data.get(i);
			for (int j = 0; j < dimension; j++)
				fDps2D.point(i).coordinate(j).setValue(tt[j]);

		}

		return fDps2D;
	}

	/**
	 * Adds (appends) the specified element to the end of this list.
	 * 
	 * @param values
	 *            array of values to be added.
	 * 
	 */
	public void add(double[] values) {

		dimension = values.length;
		data.add(values);
	}

	/**
	 * Adds (appends) P0D to the end of this list.
	 * 
	 * @param values
	 *            array of values to be added.
	 * 
	 */
	public void add(P0D values) {

		dimension = values.size();
		data.add(values.getArray());
	}

	/**
	 * Sets (replace) the specified row.
	 * 
	 * @param index
	 *            position index
	 * @param values
	 *            array of values to be added.
	 * 
	 */
	public void set(int index, double[] values) {

		if (index >= data.size()) {
			ErrorMessage("Index is too large");
		}

		dimension = values.length;
		data.set(index, values);
	}

	/**
	 * Sets (replace) the specified element with P0D.
	 * 
	 * @param index
	 *            position index
	 * @param values
	 *            array of values to be added.
	 * 
	 */
	public void set(int index, P0D values) {

		if (index >= data.size()) {
			ErrorMessage("Index is too large");
		}

		dimension = values.size();
		data.set(index, values.getArray());
	}

	/**
	 * 
	 * Read the data from an external source. Old data will be lost. Use "#" or
	 * "*" for comments. Use a space to separate values in columns and new line
	 * to put new row.
	 * 
	 * @param br
	 *            BufferedReader
	 * 
	 * @return zero if success.
	 */
	public int read(BufferedReader br) {

		data.clear();

		try {

			String line;
			// dis.available() returns 0 if the file does not have more lines.
			while ((line = br.readLine()) != null) {

				line = line.trim();
				if (!line.startsWith("#") && !line.startsWith("*")) {

					StringTokenizer st = new StringTokenizer(line);
					dimension = st.countTokens(); // number of words
					double[] snum = new double[dimension];

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
						}
						snum[mm] = dd;
						mm++;

					} // end loop over each line

					data.add(snum);

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
	 * Read PND from a file.
	 * 
	 * The old content will be lost. Use a space to separate values in columns
	 * and new line to put new row. Comment lines starting with "#" and "*" are
	 * ignored.
	 * 
	 * 
	 * @param sfile
	 *            input file
	 * @return zero if success
	 */
	public int read(File sfile) {

		BufferedReader is = PReader.read(sfile);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * Read data from URL. Use a space to separate values in columns and new
	 * line to put new row.
	 * 
	 * @param url
	 *            URL location of input file
	 */

	public int read(URL url) {

		BufferedReader is = PReader.read(url);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * Read PND from a GZiped file. It can read URL if the string starts from
	 * http or ftp, otherwise a file on the file system is assumed.
	 * <p>
	 * Use a space to separate values in columns and new line to put new row.
	 * 
	 * @param sfile
	 *            File name with input (extension .gz)
	 * @return zero if success
	 */
	public int readGZip(String sfile) {

		BufferedReader is = PReader.readGZip(sfile);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * Read PND from a file. It can read URL if the string starts from http or
	 * ftp, otherwise a file on the file system is assumed.
	 * <p>
	 * The old content will be lost. Use a space to separate values in columns
	 * and new line to put new row. Comment lines starting with "#" and "*" are
	 * ignored.
	 * 
	 * @param sfile
	 *            File name with input
	 * @return zero if success
	 */
	public int read(String sfile) {

		BufferedReader is = PReader.read(sfile);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * Read PND from a GZiped file. The old content will be lost. Use a space to
	 * separate values in columns and ne line to put new row. Comment lines
	 * start from "#" and "*" are ignored.
	 * 
	 * @param sfile
	 *            File name with input (extension .gz)
	 * @return zero if success
	 */
	public int readGZip(File sfile) {

		BufferedReader is = PReader.readGZip(sfile);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * 
	 * Read the data from ZIPed external file. Old data will be lost. Use "#" or
	 * "*" for comments.
	 * 
	 * @param sfile
	 *            File name with the input (extension .zip)
	 * @return zero if success.
	 */
	public int readZip(String sfile) {

		data.clear();

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
								dimension = st.countTokens(); // number of words
								double[] snum = new double[dimension];

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
									}
									snum[mm] = dd;
									mm++;

								} // end loop over each line

								data.add(snum);

							} // skip #
						}
						br.close();
					}
				}
			}
			// this statement reads the line from the file and print it to

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
	 * Write a PND to an external file. Same method as toFile()
	 * 
	 * @param name
	 *            File name with output
	 */
	public void write(String name) {
		toFile(name);
	}

	/**
	 * Write a P0D object to a serialized file
	 * 
	 * @param name
	 *            serialized file name for output.
	 * 
	 * @return zero if no errors
	 */
	public int writeSerialized(String name) {

		return jhplot.io.Serialized.write(this, name);

	}

	/**
	 * Read a PND object from a serialized file
	 * 
	 * @param name
	 *            serialized file name for input.
	 * 
	 * @return new PND object
	 */
	public PND readSerialized(String name) {

		return (PND) jhplot.io.Serialized.read(name);

	}

	/**
	 * Write a PND to an external file.
	 * 
	 * @param name
	 *            File name with output
	 */
	public void toFile(String name) {

		Date dat = new Date();
		String today = String.valueOf(dat);

		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);

			tx.println("# DataMelt: output from PND " + this.title);
			tx.println("# DataMelt: created at " + today);
			tx.println("# values:");
			tx.println("#");
			for (int i = 0; i < data.size(); i++) {

				double[] tt = (double[]) get(i);
				dimension = tt.length;
				for (int j = 0; j < dimension; j++)
					tx.print(tt[j]);
				tx.print("\n");

			}
			f1.close();

		} catch (IOException e) {
			ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	/**
	 * Remove a row
	 * 
	 * @param index
	 *            row index to be removed
	 */
	public PND remove(int index) {
		data.remove(index);
		return this;
	}

	/**
	 * Operations on PND containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes and title.
	 * 
	 * @param pnd
	 *            Input PND container for operation
	 * @param what
	 *            String representing the operation: "+" add a P0D container to
	 *            the original; "-" subtract a P0D from the original; "*"
	 *            multiply; "/" divide by P0D
	 * @return original PND after the operation.
	 */

	public PND oper(PND pnd, String what) {
		return oper(pnd, getTitle(), what);
	}

	/**
	 * Operations on PND containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes
	 * 
	 * @param pnd
	 *            Input PND container for operation
	 * @param title
	 *            New title
	 * @param what
	 *            String representing the operation: "+" add a P0D container to
	 *            the original; "-" subtract a P0D from the original; "*"
	 *            multiply; "/" divide by P0D
	 * @return original PND after the operation.
	 */

	public PND oper(PND pnd, String title, String what) {

		what = what.trim();

		// first check them
		if (data.size() != pnd.size()) {
			ErrorMessage("Sizes of the PNDs are different!");
			return this;
		}
		// first check them
		if (dimension != pnd.getDimension()) {
			ErrorMessage("Dimensions of the PNDs are different!");
			return this;
		}

		if (what.equals("+")) {

			for (int i = 0; i < data.size(); i++) {
				double[] tt1 = (double[]) get(i);
				double[] tt2 = (double[]) pnd.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] + tt2[j];
				data.set(i, tt1);
			}
			return this;
		}

		if (what.equals("-")) {

			for (int i = 0; i < data.size(); i++) {
				double[] tt1 = (double[]) get(i);
				double[] tt2 = (double[]) pnd.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] - tt2[j];
				data.set(i, tt1);
			}
			return this;

		}

		if (what.equals("*")) {
			for (int i = 0; i < data.size(); i++) {
				double[] tt1 = (double[]) get(i);
				double[] tt2 = (double[]) pnd.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] * tt2[j];
				data.set(i, tt1);
			}
			return this;
		}

		if (what.equals("/")) {

			for (int i = 0; i < data.size(); i++) {
				double[] tt1 = (double[]) get(i);
				double[] tt2 = (double[]) pnd.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] / tt2[j];
				data.set(i, tt1);
			}
			return this;
		}

		return this;
	}

	/**
	 * Scale each element of data
	 * 
	 * @param scale
	 *            Scale factor
	 * 
	 */
	public void operScale(double scale) {

		for (int i = 0; i < data.size(); i++) {
			double[] tt1 = (double[]) get(i);
			for (int j = 0; j < tt1.length; j++)
				tt1[j] = tt1[j] * scale;
			data.set(i, tt1);
		}

		return;
	}

	/**
	 * Data size (number of rows)
	 * 
	 * @return number of rows
	 */
	public int size() {

		return data.size();

	}

	/**
	 * Create an exact copy of the current P0D. New object is created.
	 * 
	 * @param newtitle
	 *            new title
	 */
	public PND copy(String newtitle) {
		PND tmp = new PND(newtitle);
		for (int i = 0; i < data.size(); i++) {
			double[] tt = (double[]) data.get(i);
			double[] clone = (double[]) tt.clone();
			tmp.add(clone);
		}
		return tmp;
	}

	/**
	 * Get rows : min is inxluded, max is not;
	 * 
	 * @param title
	 *            New title
	 * @param indexMin
	 *            min index of row
	 * @param indexMax
	 *            max index of row
	 * @return new PND with rows indexMin-indexMax
	 **/
	public PND getRows(String newtitle, int indexMin, int indexMax) {
		PND tmp = new PND(newtitle);
		if (indexMin < 0) {
			ErrorMessage("Wrong min index");
			return null;
		}
		if (indexMax > data.size()) {
			ErrorMessage("Wrong max index");
			return null;
		}
		;
		for (int i = indexMin; i < indexMax; i++)
			tmp.add(get(i));
		return tmp;
	}

	/**
	 * Set the data in form of ArrayList
	 * 
	 * @param array
	 *            ArrayList to be set.
	 */
	public void setArrayList(ArrayList<double[]> array) {
		this.data = array;
	}

	/*
	 * int[][] a = new int[2][4];
	 * 
	 * This two-dimensional array will have two rows and four columns.
	 * 
	 * 
	 * In Java two-dimensional arrays are implemented is a one-dimensional array
	 * of one-dimensional arrays -- like this.
	 */

	/**
	 * Get a double array with values.
	 * 
	 * @return double array with values
	 */

	public double[][] getArray() {
		double[][] tmp = new double[data.size()][dimension];
		for (int i = 0; i < data.size(); i++) {
			double[] tt = (double[]) data.get(i);

			for (int j = 0; j < tt.length; j++)
				tmp[i][j] = tt[j];

		}
		return tmp;
	}

	/**
	 * Set values from double array Old content will be lost.
	 * 
	 * @param values
	 *            array to be set: [ROWS][dimension];
	 * 
	 * @return PND with new values
	 */

	public PND setArray(double[][] values) {

		if (values[0].length != dimension) {
			ErrorMessage("Input array has too large dimension");
			return this;
		}

		data.clear();
		for (int i = 0; i < values.length; i++)
			data.add(values[i]);

		return this;
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
	 * Standardize each column. Useful for Neural Network studies. This means
	 * S(i)= (X(i) - mean) / std; i goes from 0 to size(); for each column in
	 * PND. mean - mean value for data in a certain column; std is the standard
	 * deviation. Usually used for neural net to standardize the input variables
	 * (column vectors). Operates on the original data.
	 * 
	 * 
	 * @return PND after standardize
	 * */
	public PND standardize() {

		int dim = getDimension();
		double values[][] = new double[dim][size()];

		for (int n = 0; n < dim; n++) {
			double a[] = new double[size()];
			for (int j = 0; j < size(); j++)
				a[j] = get(j, n);
			double mean = Statistics.mean(a);
			double std = Statistics.stddeviation(a);
			for (int j = 0; j < size(); j++)
				values[n][j] = (a[j] - mean) / std;

		}

		for (int j = 0; j < size(); j++) {
			double t[] = new double[dim];
			for (int n = 0; n < dim; n++)
				t[n] = values[n][j];
			data.set(j, t);
		}

		return this;
	}

	/**
	 * Rescale the column vectors. S(i)= (X(i) - v[0]) / v[1], where
	 * v[dimension] [2] is the 2D array returned by this function to be able
	 * calculate X(i) back. Usually used for a neural net (rescaling of the
	 * output).
	 * 
	 * <p>
	 * 
	 * @param type
	 *            0: is ths standard rescaling, i.e. all columns are rescaled to
	 *            the range [0,1] This is done as: v[0] is the min value of
	 *            X(i), v[1] is the range (max-min).
	 *            <p>
	 *            type 1: midrange rescaling, i.e. all values are in the range
	 *            [-1,1]. v[0] is 0.5*(min+max), while [1] is (max-min) /2
	 * 
	 * 
	 * @return v[][] used to rescale the data: v[column][0] gives v[0],
	 *         v[column][1] given by v[1]. Use this array to convert the data
	 *         back.
	 */

	public double[][] rescale(int type) {

		int dim = getDimension();
		double values[][] = new double[dim][size()];

		double range[] = new double[dim];
		double min[] = new double[dim];
		double max[] = new double[dim];

		for (int n = 0; n < dim; n++) {
			double a[] = new double[size()];
			for (int j = 0; j < size(); j++)
				a[j] = get(j, n);

			min[n] = DoubleArray.min(a);
			max[n] = DoubleArray.max(a);
			range[n] = max[n] - min[n];

			if (type == 1) { // midrange [-1-1]
				min[n] = 0.5 * (min[n] + max[n]);
				range[n] = 0.5 * range[n];
			}

			for (int j = 0; j < size(); j++)
				values[n][j] = (a[j] - min[n]) / range[n];

		}

		// collect back
		for (int j = 0; j < size(); j++) {
			double t[] = new double[dim];
			for (int n = 0; n < dim; n++)
				t[n] = values[n][j];
			data.set(j, t);
		}

		double tmp[][] = new double[dim][2];
		for (int n = 0; n < dim; n++) {
			tmp[n][0] = min[n];
			tmp[n][1] = range[n];

		}

		return tmp;
	}

	/**
	 * Return H1D histogram with PND content. All values are added. Histogram
	 * range is defined by Min and Max values.
	 * 
	 * @param bins
	 *            Number of bins for the histogram.
	 * @param min
	 *            Min value of histogram
	 * @param max
	 *            Max value of histogram
	 * @return H2D histogram filled with P0D.
	 */
	public H1D getH1D(int bins, double min, double max) {

		H1D h1d = new H1D(this.title, bins, min, max);
		for (int n = 0; n < getDimension(); n++) {
			for (int j = 0; j < size(); j++)
				h1d.fill((double) get(j, n));
		}

		return h1d;

	}

	/**
	 * Make a histogram from a column of PND.
	 * 
	 * @param bins
	 *            Number of bins
	 * @param min
	 *            Min value
	 * @param max
	 *            Max value
	 * @param column
	 *            column of PND (<getDimension)
	 * 
	 * @return histogram
	 */
	public H1D getH1D(int bins, double min, double max, int column) {

		H1D h1d = new H1D(this.title, bins, min, max);
		for (int j = 0; j < size(); j++)
			h1d.fill((double) get(j, column));

		return h1d;

	}

	/**
	 * Create histogram. Min and Max are determined authomatically.
	 * 
	 * @param bins
	 *            Number of bins
	 * @return
	 */

	public H1D getH1D(int bins) {

		getMinMax();
		return getH1D(bins, min, max);

	}

	/**
	 * Get min value
	 * 
	 * @return minimum value
	 */

	public double getMax() {
		double maxValue = get(0, 0);
		for (int n = 0; n < getDimension(); n++) {
			for (int j = 0; j < size(); j++) {

				if (get(j, n) > maxValue) {
					maxValue = get(j, n);
				}
			}
		}
		return maxValue;

	}

	/**
	 * Get max value
	 * 
	 * @return
	 */

	public double getMin() {

		double minValue = get(0, 0);
		for (int n = 0; n < getDimension(); n++) {
			for (int j = 0; j < size(); j++) {
				if (get(j, n) < minValue) {
					minValue = get(j, n);
				}
			}
		}
		return minValue;
	}

	/**
	 * Get min and max at the same time.
	 */
	private void getMinMax() {

		min = get(0, 0);
		min = max;
		for (int n = 0; n < getDimension(); n++) {
			for (int j = 0; j < size(); j++) {
				if (get(j, n) < min) {
					min = get(j, n);
				}
				if (get(j, n) > max) {
					max = get(j, n);
				}

			}
		}
	}

	/**
	 * This is an inverse operation to rescale(type) function. You should give
	 * array v[2], from which the original data can be calculated. Each column
	 * after the rescaling will be transformed to X(i)=S(i)*v[1]+v[0] (we drop
	 * here index for rows).
	 * 
	 * @param v
	 *            array used to rescale the data. applied as X(i)=S(i)*v[1]+v[0]
	 * @return PND after rescaling
	 */
	public PND rescale(double[][] v) {

		int dim = getDimension();
		double values[][] = new double[dim][size()];

		for (int n = 0; n < dim; n++) {
			double a[] = new double[size()];

			for (int j = 0; j < size(); j++)
				a[j] = get(j, n);

			for (int j = 0; j < size(); j++)
				values[n][j] = a[j] * v[n][1] + v[n][0];

		}

		// collect back
		for (int j = 0; j < size(); j++) {
			double t[] = new double[dim];
			for (int n = 0; n < dim; n++)
				t[n] = values[n][j];
			data.set(j, t);
		}

		return this;
	}

	/**
	 * Test
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		HPlot c1 = new HPlot("Canvas", 600, 400);
		c1.visible(true);
		c1.setAutoRange();

		PND p0 = new PND("Example");

		p0.add(new double[] { 1, 2, 3 });
		p0.add(new double[] { 2, 5, 1 });
		p0.add(new double[] { 7, 6, 4 });
		p0.add(new double[] { 10, 2, 0 });
		p0.add(new double[] { 4, 8, 2 });
		System.out.println(p0.toString());

		c1.setNameX("X");
		c1.setNameY("Y");
		p0.print();

		P0D ppp = p0.getP0D(1);
		// drow as histogram
		H1D h1 = ppp.getH1D(100);
		c1.draw(h1);

	}

	/**
	 * Show container to a Table in a separate Frame. The numbers are formatted
	 * to scientific format. One can sort and search the data in this table (but
	 * not modify)
	 */

	public void toTable() {

		new HTable(this);

	}

	

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

}
