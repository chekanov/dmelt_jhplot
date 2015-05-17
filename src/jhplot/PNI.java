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

import jhplot.gui.HelpBrowser;
import jhplot.io.PReader;

/**
 * Data holder in 2D for integer number. It is similar to matrix in 2D. It has
 * columns and rows. The number of columns may not be the same. For example,
 * data can be presented as:
 * <p>
 * 
 * <pre>
 * 1 2 3 4 5
 * 12 3 4
 * 1 3 4 555 5 66 77
 * 1 2 2 33 434 4
 * </pre>
 * <p>
 * All numbers are expected to be integers..
 * <p>
 * It extends ArrayList and adds many new features for data manipulation. The
 * class does not have graphical option (use H1D to show the data or methods of
 * this class which transform P0D to a H1D histogram).
 * 
 * @author S.Chekanov
 * 
 */

public class PNI implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<int[]> data;
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

	public PNI(String title) {
		data = new ArrayList<int[]>();
		this.title = title;
		this.dimension = 0;

	}

	/**
	 * Construct a copy from a PNI. If the last argument is true, a shallow copy
	 * of a collection. In this case a new collection contains references to
	 * same objects as the source collection. Data are not cloned
	 * 
	 * @param title
	 *            new title
	 * @param shallow
	 *            if true, a shallow copy of a collection.
	 * @param PNI
	 *            inpit data
	 */
	public PNI(String title, boolean shallow, PNI pni) {
		this.title = title;
		this.data = new ArrayList<int[]>();
		this.dimension = pni.getDimension();

		if (shallow) {
			// ArrayList<Double> jplot3d=p0d.getArrayList();
			data = (ArrayList<int[]>) (pni.getArrayList().clone());
		} else {
			for (int i = 0; i < pni.size(); i++)
				data.add(pni.get(i));
		}

	}

	/**
	 * Construct a container with a title from external file (see the method
	 * toFile() how to write such ASCII file)
	 * 
	 * @param title
	 *            A title
	 * 
	 * @param file
	 *            input file name. It can be either a file on a file system or
	 *            URL location (must start from http or ftp)
	 */

	public PNI(String title, String file) {
		this.dimension = 0;
		data = new ArrayList<int[]>();
		this.title = title;
		read(file);

	}

	/**
	 * Construct an empty container with no title
	 * 
	 */
	public PNI() {
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
	 * Get a new title
	 * 
	 * @return Title
	 */

	public String getTitle() {
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
	public int[] get(int row) {
		return (int[]) data.get(row);
	}

	/**
	 * Return a specific row as P0D
	 * 
	 * @param row
	 *            index of the row
	 * 
	 * @return array of values
	 */
	public P0I getRowP0D(int row) {
		P0I p = new P0I(title + ":row:" + Integer.toString(row));
		p.fill((int[]) data.get(row));
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
	public int get(int row, int column) {
		int[] tmp = (int[]) data.get(row);
		return tmp[column];
	}

	/**
	 * Get a string representing PNI
	 * 
	 * 
	 * @return String with all values
	 */
	public String toString() {

		String tmp = "\nPNI: " + title + "\n";

		for (int i = 0; i < data.size(); i++) {
			int[] tt = (int[]) data.get(i);
			for (int j = 0; j < tt.length; j++) {
				tmp += Double.toString(tt[j]);
				tmp += " ";

			}
			tmp += "\n";
		}

		return tmp;
	}

	/**
	 * Print PNI to System.out.
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
	 * @return array with values.
	 * 
	 */
	public int[] getColumn(int column) {

		int[] tmp = new int[data.size()];

		if (column >= dimension) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		for (int j = 0; j < data.size(); j++) {
			int[] tt = (int[]) data.get(j);
			tmp[j] = tt[column];
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
			int[] tt = (int[]) data.get(j);
			tmp.add(tt[c1], tt[c2]);
		}

		return tmp;
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
	public P0I getP0I(int column) {

		P0I tmp = new P0I(title + " :" + Integer.toString(column));

		if (column >= dimension) {
			ErrorMessage("Index is too large");
			return tmp;
		}

		for (int j = 0; j < data.size(); j++) {
			int[] tt = (int[]) data.get(j);
			tmp.add(tt[column]);
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
			int[] tt = (int[]) data.get(j);
			tmp.add(tt[c1], tt[c2], tt[c3]);
		}

		return tmp;
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

		int[] tt = (int[]) data.get(row);
		tmp.setArray(tt);
		return tmp;
	}

	/**
	 * Get data in form of ArrayList
	 * 
	 * @return data in form of ArrayList
	 */
	public ArrayList<int[]> getArrayList() {
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
			int[] tt = (int[]) data.get(i);
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
	public void add(int[] values) {

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
	public void add(P0I values) {

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
	public void set(int index, int[] values) {

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
	public void set(int index, P0I values) {

		if (index >= data.size()) {
			ErrorMessage("Index is too large");
		}

		dimension = values.size();
		data.set(index, values.getArray());
	}

	/**
	 * 
	 * Read the data from external file. Old data will be lost. Use "#" or "*"
	 * for comments.
	 * 
	 * @param sfile
	 *            File name with input
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
					int[] snum = new int[dimension];

					int mm = 0;
					while (st.hasMoreTokens()) { // make sure there is stuff
						// to get
						String tmp = st.nextToken();

						// read
						int dd = 0;
						try {
							dd = Integer.parseInt(tmp.trim());
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
								int[] snum = new int[dimension];

								int mm = 0;
								while (st.hasMoreTokens()) { // make sure there
									// is stuff
									// to get
									String tmp = st.nextToken();

									// read double
									int dd = 0;
									try {
										dd = Integer.parseInt(tmp.trim());
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
	 * Read PNI from a file.
	 * 
	 * The old content will be lost. Use a space to separate values in columns
	 * and tab to put new row. Comment lines starting with "#" and "*" are
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
	 * Read data from URL. Use a space to separate values in columns and tab to
	 * put new row.
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
	 * Read PNI from a GZiped file. It can read URL if the string starts from
	 * http or ftp, otherwise a file on the file system is assumed.
	 * <p>
	 * Use a space to separate values in columns and tab to put new row.
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
	 * Read PNI from a file. It can read URL if the string starts from http or
	 * ftp, otherwise a file on the file system is assumed.
	 * <p>
	 * The old content will be lost. Use a space to separate values in columns
	 * and tab to put new row. Comment lines starting with "#" and "*" are
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
	 * Read PNI from a GZiped file. The old content will be lost. Use a space to
	 * separate values in columns and tab to put new row. Comment lines start
	 * from "#" and "*" are ignored.
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
	 * Write a PNI to an external file. Same method as toFile()
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
	 * Read a PNI object from a serialized file
	 * 
	 * @param name
	 *            serialized file name for input.
	 * 
	 * @return new PNI object
	 */
	public PNI readSerialized(String name) {

		return (PNI) jhplot.io.Serialized.read(name);

	}

	/**
	 * Write a PNI to an external file.
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

			tx.println("# DataMelt: output from PNI " + this.title);
			tx.println("# DataMelt: created at " + today);
			tx.println("# values:");
			tx.println("#");
			for (int i = 0; i < data.size(); i++) {

				int[] tt = (int[]) get(i);
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
	public PNI remove(int index) {
		data.remove(index);
		return this;
	}

	/**
	 * Operations on PNI containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes and title.
	 * 
	 * @param PNI
	 *            Input PNI container for operation
	 * @param what
	 *            String representing the operation: "+" add a P0D container to
	 *            the original; "-" subtract a P0D from the original; "*"
	 *            multiply; "/" divide by P0D
	 * @return original PNI after the operation.
	 */

	public PNI oper(PNI PNI, String what) {
		return oper(PNI, getTitle(), what);
	}

	/**
	 * Operations on PNI containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes
	 * 
	 * @param PNI
	 *            Input PNI container for operation
	 * @param title
	 *            New title
	 * @param what
	 *            String representing the operation: "+" add a P0D container to
	 *            the original; "-" subtract a P0D from the original; "*"
	 *            multiply; "/" divide by P0D
	 * @return original PNI after the operation.
	 */

	public PNI oper(PNI PNI, String title, String what) {

		what = what.trim();

		// first check them
		if (data.size() != PNI.size()) {
			ErrorMessage("Sizes of the PNIs are different!");
			return this;
		}
		// first check them
		if (dimension != PNI.getDimension()) {
			ErrorMessage("Dimensions of the PNIs are different!");
			return this;
		}

		if (what.equals("+")) {

			for (int i = 0; i < data.size(); i++) {
				int[] tt1 = (int[]) get(i);
				int[] tt2 = (int[]) PNI.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] + tt2[j];
				data.set(i, tt1);
			}
			return this;
		}

		if (what.equals("-")) {

			for (int i = 0; i < data.size(); i++) {
				int[] tt1 = (int[]) get(i);
				int[] tt2 = (int[]) PNI.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] - tt2[j];
				data.set(i, tt1);
			}
			return this;

		}

		if (what.equals("*")) {
			for (int i = 0; i < data.size(); i++) {
				int[] tt1 = (int[]) get(i);
				int[] tt2 = (int[]) PNI.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] * tt2[j];
				data.set(i, tt1);
			}
			return this;
		}

		if (what.equals("/")) {

			for (int i = 0; i < data.size(); i++) {
				int[] tt1 = (int[]) get(i);
				int[] tt2 = (int[]) PNI.get(i);
				for (int j = 0; j < tt1.length; j++)
					tt1[j] = tt1[j] / tt2[j];
				data.set(i, tt1);
			}
			return this;
		}

		return this;
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
	 * Get last dimension of the data (or number of columns). The stored
	 * dimention is the one set after the last call "add", or if it set
	 * manually.
	 * 
	 * @return dimension (number of elements in a row)
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Get the numbers of columns. The stored dimention is the one set after the
	 * last call "add", or if it set manually.
	 * 
	 * @return dimension (number of elements in a row)
	 */
	public int[] getDimensions() {

		int[] ii = new int[data.size()];
		for (int j = 0; j < data.size(); j++) {
			int[] tt = (int[]) data.get(j);
			ii[j] = tt.length;
		}

		return ii;
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
			int[] tt1 = (int[]) get(i);
			for (int j = 0; j < tt1.length; j++)
				tt1[j] = (int) (tt1[j] * scale);
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
	public PNI copy(String newtitle) {
		PNI tmp = new PNI(newtitle);
		for (int i = 0; i < data.size(); i++) {
			int[] tt = (int[]) data.get(i);
			int[] clone = (int[]) tt.clone();
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
	 * @return new PNI with rows indexMin-indexMax
	 **/
	public PNI getRows(String newtitle, int indexMin, int indexMax) {
		PNI tmp = new PNI(newtitle);
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
	public void setArrayList(ArrayList<int[]> array) {
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

	public int[][] getArray() {
		int[][] tmp = new int[data.size()][dimension];
		for (int i = 0; i < data.size(); i++) {
			int[] tt = (int[]) data.get(i);

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
	 * @return PNI with new values
	 */

	public PNI setArray(int[][] values) {

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
	 * Test
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		HPlot c1 = new HPlot("Canvas", 600, 400);
		c1.visible(true);
		c1.setAutoRange();

		PNI p0 = new PNI("Example");

		p0.add(new int[] { 1, 2, 3 });
		p0.add(new int[] { 2, 5, 1 });

		System.out.println(p0.toString());

		c1.setNameX("X");
		c1.setNameY("Y");
		p0.print();

		P0I ppp = p0.getP0I(1);
		// drow as histogram
		H1D h1 = ppp.getH1D(100);
		c1.draw(h1);

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
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

}
