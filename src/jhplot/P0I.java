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

import cern.jet.random.*;
import cern.jet.stat.Descriptive;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.hep.aida.bin.DynamicBin1D;
import java.io.*;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jhplot.gui.HelpBrowser;
import jhplot.io.PReader;
import jhplot.math.*;
import jhplot.math.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A container to hold integer data points in one dimension. All numbers are
 * expected to be in double precision. It extends ArrayList and adds many new
 * features for data manipulation. The class does not have graphical option (use
 * H1D to show the data or methods of this class which transform P0I to a H1D
 * histogram).
 * 
 * <p>
 * This is a high-speed a small footprint container designed for numerical
 * analysis.
 * 
 * @author S.Chekanov
 * 
 */

public class P0I extends IntArrayList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DynamicBin1D bin = null;

	private String title = "title undefined";

	/**
	 * Construct an empty container with a title
	 * 
	 * @param title
	 *            New title
	 */

	public P0I(String title) {
		super();
		this.title = title;

	}

	/**
	 * Construct P0I from array;
	 * 
	 * @param d
	 *            array
	 */

	public P0I(int[] d) {
		setArray(d);
		this.title = "undefined";

	}

	/**
	 * Create P0I from an array.
	 * 
	 * @param title
	 *            title
	 * @param d
	 *            input array
	 * */

	public P0I(String title, int[] d) {
		this.title = title;
		setArray(d);

	}

	/**
	 * Construct a copy from a P0I. If the last argument is true, a shallow copy
	 * of a collection. In this case a new collection contains references to
	 * same objects as the source collection. Data are not cloned
	 * 
	 * @param title
	 *            new title
	 * @param p
	 *            input data in form of P0I
	 */

	public P0I(String title, P0I p) {
		this.title = title;
		for (int i = 0; i < size(); i++) {
			add(p.getQuick(i));
		}
	}

	/**
	 * Construct an empty container with a title
	 * 
	 */

	public P0I() {
		this("undefined title");
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
	 * Construct a P0I from a file. Use "#" or "*" for comments. each number
	 * should be written in separate line.
	 * 
	 * @param title
	 *            Title of the container
	 * @param sfile
	 *            File name with input. Can be either file location or URL. Must
	 *            start as http or ftp
	 */
	public P0I(String title, String sfile) {
		this(title);
		this.read(sfile);

	}

	/**
	 * Read one dimensional data from uncompressed ASCII file. Use "#" or "*"
	 * for comments. Each number should be written on a separate line.
	 * 
	 * @param sfile
	 *            File name with input
	 * @return error: 0 in case of success. 3: parse error. 1-2: file not found.
	 */
	public int read(BufferedReader br) {

		clear();
		try {

			String line;
			long mm = 0;
			while ((line = br.readLine()) != null) {

				line = line.trim();
				if (!line.startsWith("#") && !line.startsWith("*")) {

					// read double
					int dd = 0;
					try {
						dd = Integer.parseInt(line);
					} catch (NumberFormatException e) {
						jhplot.utils.Util
								.ErrorMessage("Error in reading the line "
										+ Long.toString(mm + 1));
						return 3;
					}
					mm++;
					add(dd);

				} // skip #

			}

			// dispose all the resources after using them.
			br.close();

		} catch (FileNotFoundException e) {
			jhplot.utils.Util.ErrorMessage(e.toString());
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}

		return 0;
	}

	/**
	 * Read P0I from a file.
	 * 
	 * The old content will be lost. Comment lines starting with "#" and "*" are
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
	 * Read P0I from a GZiped file. It can read URL if the string starts from
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
	 * Read PNI from a GZiped file. The old content will be lost. Comment lines
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
	 * Read one dimensional data from ZIP ASCII file. Use "#" or "*" for
	 * comments. Each number should be written in separate line.
	 * 
	 * @param sfile
	 *            Zipped file name with input (file extension .zip)
	 * @return error (int): 0 in case of success. 3: parse error. 1-2: file not
	 *         found.
	 */
	public int readZip(String sfile) {

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
								int dd = 0;
								long mm = 0;
								try {
									dd = Integer.parseInt(line);
								} catch (NumberFormatException e) {
									jhplot.utils.Util
											.ErrorMessage("Error in reading the line "
													+ Long.toString(mm + 1));
									return 3;
								}
								mm++;
								add(new Integer(dd));

							} // skip #

						}
						br.close();
					}
				}
			}

		} catch (FileNotFoundException e) {
			jhplot.utils.Util.ErrorMessage("File not found:" + sfile);
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}

		return 0;
	}

	/**
	 * Write a P0I to an external file.
	 * 
	 * @param name
	 *            File name with output
	 */
	public void toFile(String name) {

		// DecimalFormat dfb = new DecimalFormat("##.#####E00");
		Date dat = new Date();
		String today = String.valueOf(dat);

		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);

			tx.println("# DataMelt: output from P0I " + this.title);
			tx.println("# DataMelt: created at " + today);
			tx.println("#");
			for (int i = 0; i < size(); i++) {
				String x = ((Integer) get(i)).toString();
				tx.println(x);
			}
			f1.close();

		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	/**
	 * Write a P0I to a binary file (big endian by default).
	 * 
	 * @param name
	 *            Binary file name for the output.
	 * 
	 */
	public void writeBinary(String name) {
		BinaryFile.writeIntArray(new File(name), getArray(), "BIG_ENDIAN");

	}

	/**
	 * Write a P0I object to a serialized file
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
	 * Read a P0I object from a serialized file
	 * 
	 * @param name
	 *            serialized file name for input.
	 * 
	 * @return new P0I object
	 */
	public P0I readSerialized(String name) {

		return (P0I) jhplot.io.Serialized.read(name);

	}

	/**
	 * Read a P0I from a binary file (big endian by default). Old elements will
	 * be removed. Only read files created by the method writeBinary().
	 * 
	 * @param name
	 *            Binary file name for input.
	 * @return 0 in case of no problems
	 * 
	 */
	public int readBinary(String name) {
		clear();

		int[] tmp = BinaryFile.readIntArray(new File(name), "BIG_ENDIAN");

		for (int i = 0; i < tmp.length; i++)
			add(new Integer(tmp[i]));

		return 0;
	}

	/**
	 * Get a P0I as a string.
	 * 
	 * @return Output String. Each value separated by new line.
	 */
	public String toString() {

		String tmp = "P0I " + title + "\n";

		for (int i = 0; i < size(); i++) {
			String x = ((Integer) getQuick(i)).toString();
			tmp += x + "\n";
		}

		return tmp;

	}

	/**
	 * Fill with random numbers distributed using the normal (gaussian)
	 * distribution. Previous content will be lost.
	 * 
	 * @param TotNumber
	 *            total number of random values
	 * 
	 * @param mu
	 *            mean value
	 * @param sigma
	 *            Standard deviation of the random variable.
	 */
	public void randomNormal(int TotNumber, double mu, double sigma) {
		clear();
		for (int i = 0; i < TotNumber; i++)
			add((int) Random.normal(mu, sigma));
	}

	/**
	 * Fill with a sequence of numbers between min and max. min and max are
	 * included to the range, If non-empty, previous content will be lost.
	 * <p>
	 * The step is evaluated as (max-min) / (TotNumber -1). For example,
	 * fill(11,0,10) will fill with 11 number: 0,1,2,3,4,5,6,7,8,9,10
	 * 
	 * @param TotNumber
	 *            total number of values.
	 * 
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * 
	 * @return P0I with numbers.
	 */
	public void fill(int TotNumber, double min, double max) {

		if (min > max) {
			jhplot.utils.Util.ErrorMessage("Min is larger than Max");
			return;
		}

		if (TotNumber < 2) {
			jhplot.utils.Util.ErrorMessage("Number of points is too small");
			return;
		}

		clear();
		double step = (max - min) / (double) (TotNumber - 1);
		for (int i = 0; i < TotNumber; i++)
			add((int) (min + i * step));
	}

	/**
	 * Fill with with uniform random numbers between min and max. Previous
	 * content will be lost.
	 * 
	 * @param TotNumber
	 *            of random values
	 * 
	 * @param min
	 *            min random value
	 * @param max
	 *            max random value
	 */
	public void randomUniform(int TotNumber, double min, double max) {
		clear();
		for (int i = 0; i < TotNumber; i++)
			add((int) Random.uniform(min, max));
	}

	/**
	 * Fill array with random numbers Random generators are taken from
	 * cern.jet.random.*. Examples: Beta, Binominal, Poisson,
	 * BreitWigner,ChiSquare,Empirical Exponential, Gamma, Hyperbolic,
	 * Logarithmic, Normal, NegativeBinomial
	 * 
	 * @param TotNumber
	 *            Total number in array
	 * @param dist
	 *            A custom random distribution from cern.jet.random.*.
	 **/
	public void random(int TotNumber, AbstractDistribution dist) {
		clear();
		for (int i = 0; i < TotNumber; i++)
			add((int) dist.nextDouble());

	}

	/**
	 * Return H1D histogram with P0I content. Histogram range is defined by Min
	 * and Max values.
	 * 
	 * @param bins
	 *            Number of bins for the histogram.
	 * 
	 * @return H2D histogram filled with P0I.
	 */
	public H1D getH1D(int bins) {

                double xmin=getMin();
                double xmax=getMax();

                  if (xmin>=xmax) {
                   xmin=0.0;
                   xmax=1.0;
                   jhplot.utils.Util.ErrorMessage("Array was not filled?. Using the default max value 1 for the histogram");
                }



		H1D h1d = new H1D(this.title, bins, xmin, xmax);
		for (int i = 0; i < size(); i++)
			h1d.fill((double) getQuick(i));

		return h1d;

	}

	/**
	 * Return H1D histogram with P0I content. Histogram range is defined by Min
	 * and Max values.
	 * 
	 * @param min
	 *            Min value of histogram
	 * 
	 * @param max
	 *            Max value of histogram
	 * 
	 * @param bins
	 *            Number of bins for the histogram.
	 * 
	 * @return H2D histogram filled with P0I.
	 */
	public H1D getH1D(int bins, double min, double max) {

		H1D h1d = new H1D(this.title, bins, min, max);
		for (int i = 0; i < size(); i++)
			h1d.fill((double) getQuick(i));

		return h1d;

	}

	/**
	 * Print a P0I container.
	 * 
	 */
	public void print() {

		System.out.println(toString());

	}

	/**
	 * Merge two P0I containers
	 * 
	 * @param a
	 *            Container to be merged
	 * @return merged P0I container
	 */

	public P0I merge(P0I a) {
		for (int i = 0; i < a.size(); i++) {
			add(a.getQuick(i));
		}

		return this;

	}

	/**
	 * Fill a P0I container from an array. If it is not empty, add values will
	 * be appended.
	 * 
	 * @param values
	 *            array with double values
	 * 
	 */
	public void fill(double[] values) {

		for (int i = 0; i < values.length; i++)
			add((int) values[i]);
	}

	/**
	 * Fill a P0I container from an array. If it is not empty, add values will
	 * be appended. Integers will be converted to double.
	 * 
	 * @param values
	 *            array with double values
	 * 
	 */
	public void fill(int[] values) {

		for (int i = 0; i < values.length; i++)
			add((int) values[i]);
	}

	/**
	 * Get a copy of the current holder
	 * 
	 * @return a new copy
	 */
	public P0I copy() {
		return copy(this.title);
	}

	/**
	 * Get data in form of ArrayList
	 * 
	 * @return data in form of ArrayList
	 */
	public ArrayList<Double> getArrayList() {

		ArrayList<Double> a = new ArrayList<Double>();

		for (int i = 0; i < size(); i++)
			a.add(new Double(getQuick(i)));

		return a;
	}

	/**
	 * Return all values as a string. Values are separated by space
	 * 
	 * @return values as a single string
	 */
	public String getStringValues() {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < size(); i++) {
			sb.append((Integer) getQuick(i));
			sb.append(" ");
		}

		return sb.toString();

	}

	/**
	 * Sort a slice of the list (ascending) using the Sun quicksort
	 * implementation.
	 * 
	 * @param fromIndex
	 * @param toIndex
	 */
	public void sort(int fromIndex, int toIndex) {

		sortFromTo(fromIndex, toIndex);
	}

	/**
	 * Returns the hash code value for this collection.
	 */
	public int hashCode() {
		return hashCode();
	}

	/**
	 * Searches the list back to front for the last index of value, starting at
	 * offset.
	 * 
	 * @param offset
	 * @param value
	 * @return
	 */
	public int lastIndexOf(int offset, int value) {
		return lastIndexOf(offset, value);
	}

	/**
	 * Tests the collection to determine if all of the elements in array are
	 * present.
	 * 
	 * @param array
	 * @return true if contains
	 */
	public boolean containsAll(double[] array) {
		return containsAll(array);
	}

	/**
	 * Remove elements
	 * 
	 * @param min
	 *            min index
	 * @param max
	 *            max index
	 */
	public void removeAll(int min, int max) {

		removeFromTo(min, max);

	}

	/**
	 * Set the data in form of ArrayList
	 * 
	 * @param array
	 *            ArrayList to be set.
	 */
	public void setArrayList(ArrayList<Integer> array) {

		clear();
		for (int i = 0; i < array.size(); i++)
			add((int) (array.get(i)));

	}

	/**
	 * Set a double array. Old content will be lost.
	 * 
	 * @param array
	 *            array used to fill P0I
	 */
	public void setArray(int[] array) {

		clear();
		elements(array);

	}

	/**
	 * Create an exact copy of the current P0I.
	 * 
	 * @param newtitle
	 *            new title
	 */
	public P0I copy(String newtitle) {
		P0I tmp = new P0I(newtitle);
		for (int i = 0; i < size(); i++) {
			tmp.add(getQuick(i));
		}
		return tmp;
	}

/**
	 * Get P0I with values below, above or equal a specified value. 
	 * Specify in the input string "<,> or =". 
	 * @param d
	 *           input values 
         * @param opi 
         *          if "=", take equal values; <br>
         *          if ">", take above  values; <br>
         *          if "<", take below  values;
	 */
	public P0I get(int d, String opi) {
		P0I tmp = new P0I(title);
		int op = 0;
		if (opi.equals("="))
			op = 1;
		if (opi.equals(">"))
			op = 2;
		if (opi.equals("<"))
			op = 3;
		for (int i = 0; i < size(); i++) {
			if (op == 1) {
				if (getQuick(i) == d) {
					tmp.add(getQuick(i));
					continue;
				}
			}
			;
			if (op == 2) {
				if (getQuick(i) > d) {
					tmp.add(getQuick(i));
					continue;
				}
			}
			;
			if (op == 3) {
				if (getQuick(i) < d) {
					tmp.add(getQuick(i));
					continue;
				}
			}
			;

		}
		return tmp;
	}

	/**
	 * 
	 * Returns the index of first occurrence of the specified element
	 * 
	 * @param d
	 *            input value
	 * 
	 */
	public int find(int d) {

		return indexOf(d);

	}

	/**
	 * Get a double array with P0I values.
	 * 
	 * @return double array with values
	 */

	public int[] getArray() {
                trimToSize();
		return elements();

	}

	/**
	 * Get an integer array with P0I values.
	 * 
	 * @return integer array with values
	 */

	public int[] getArrayInt() {
		int[] tmp = new int[size()];
		for (int i = 0; i < size(); i++) {
			int tt = (Integer) getQuick(i);
			tmp[i] = (int) tt;
		}
		return tmp;
	}

	/**
	 * 
	 * Search for the first occurrence of the given argument
	 * 
	 * @param value
	 *            value for searching
	 * @return first occurrence of the given argument
	 */
	public int search(int value) {
		return indexOf(value);
	}

	/**
	 * Returns the minimum value.
	 * 
	 * @return the minimum value.
	 */
	public int getMax() {
                if (size()==0) return 0;
		int mI = java.lang.Integer.MIN_VALUE;
		for (int i = 1; i < size(); i++)
			if (getQuick(i) > mI)
				mI = getQuick(i);
		return mI;

	}

	/**
	 * Returns index corresponding to maximum value.
	 * 
	 * @return index of maximum value.
	 */
	public int getMaxIndex() {
                 if (size()==0) return 0;
		int maxI = 0;
		for (int i = 1; i < size(); i++)
			if (get(i) > getQuick(maxI))
				maxI = i;
		return maxI;
	}

	/**
	 * Returns index corresponding to minimum value.
	 * 
	 * @return index of minimum value.
	 */
	public int getMinIndex() {
                 if (size()==0) return -1;
		int minI = 0;
		for (int i = 0; i < size(); i++)
			if (get(i) < getQuick(minI))
				minI = i;
		return minI;
	}

	/**
	 * Returns the minimum value.
	 * 
	 * @return the minimum value.
	 */
	public int getMin() {
                if (size()==0) return 0; 
		int mI = java.lang.Integer.MAX_VALUE;
		for (int i = 0; i < size(); i++)
			if (getQuick(i) < mI)
				mI = getQuick(i);
		return mI;

	}

	/**
	 * 
	 * Get range between min and max
	 * 
	 * @param min
	 *            index
	 * @param max
	 *            index
	 * @return same P0I with removed range
	 */
	public P0I range(int min, int max) {

		partFromTo(min, max);
		return this;
	}

	/**
	 * Operations on P0I containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes and the title.
	 * 
	 * @param Pp
	 *            Input P0I container for operation
	 * @param what
	 *            String representing the operation: "+" add a P0I container to
	 *            the original; "-" subtract a P0I from the original; "*"
	 *            multiply; "/" divide by P0I
	 * @return original P0I after the operation.
	 */

	public P0I oper(P0I p, String what) {
		return oper(p, this.title, what);
	}

	/**
	 * Scale P0I with a factor scale. X(i)=scale*X(i)
	 * 
	 * @param scale
	 *            Scale factor
	 * @return P0I after the operation.
	 */
	public P0I operScale(double scale) {
		for (int i = 0; i < size(); i++)
			set(i, (int) (getQuick(i) * scale));
		return this;

	}

	/**
	 * Shift all values in the array by a constant. X(i)=X(i)+shift.
	 * 
	 * @param shift
	 *            constants used to add
	 * @return P0I after shift.
	 * */
	public P0I operShift(double shift) {
		for (int i = 0; i < size(); i++)
			set(i, (int) (getQuick(i) + shift));
		return this;

	}

	/**
	 * Shift all values by a constant "shift", then scale it.
	 * X(i)=(X(i)+shift)*scale.
	 * 
	 * @param shift
	 *            constants used to add to all values
	 * @param scale
	 *            constant used to scale after shifting.
	 * @return P0I after shift and scale.
	 **/
	public P0I operShiftAndScale(double shift, double scale) {
		for (int i = 0; i < size(); i++)
			set(i, (int) ((getQuick(i) + shift) * scale));
		return this;

	}

	/**
	 * Operations on P0I containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes
	 * 
	 * @param p0i
	 *            Input P0I container for operation
	 * @param title
	 *            New title
	 * @param what
	 *            String representing the operation: "+" add a P0I container to
	 *            the original; "-" subtract a P0I from the original; "*"
	 *            multiply; "/" divide by P0I
	 * @return original P0I after the operation.
	 */

	public P0I oper(P0I p0i, String title, String what) {

		what = what.trim();

		// first check them
		if (size() != p0i.size()) {
			jhplot.utils.Util.ErrorMessage("Sizes of the P0Is are different!");
			return this;
		}

		if (what.equals("+")) {
			for (int i = 0; i < size(); i++)
				set(i, (int) (p0i.getQuick(i) + getQuick(i)));
			return this;
		}

		if (what.equals("-")) {
			for (int i = 0; i < size(); i++)
				set(i, getQuick(i) - p0i.getQuick(i));
			return this;
		}

		if (what.equals("*")) {
			for (int i = 0; i < size(); i++)
				set(i, getQuick(i) * p0i.getQuick(i));
			return this;
		}

		if (what.equals("/")) {
			for (int i = 0; i < size(); i++)
				set(i, getQuick(i) / p0i.getQuick(i));
			return this;
		}

		return this;
	}

	/**
	 * Returns the mean value.
	 * 
	 * @return Mean value
	 */

	public double mean() {
		double mean = 0;
		for (int i = 0; i < size(); i++)
			mean += getQuick(i);
		mean /= (double) size();
		return mean;
	}

	/**
	 * Returns the kurtosis. Run getStat() to update or evaluate the sampling
	 * 
	 * @return kurtosis
	 */

	public double kurtosis() {

		if (bin == null) {
			System.out.println("Run first getStat() to evaluate sampling!");
			return -9999.0;
		}
		return bin.kurtosis();

	}

	/**
	 * Returns the median. First run getStat() to evaluate or update.
	 * 
	 * @return median
	 */

	public double median() {
		if (bin == null) {
			System.out.println("Run first getStat() to evaluate sampling!");
			return -9999.0;
		}
		return bin.median();

	}

	/**
	 * Get string with all statistics
	 * 
	 * @return string representing all statistics
	 */

	public String getStatString() {
		bin = new DynamicBin1D();
		bin.addAllOf(getIntegerArrayList());
		return bin.toString();

	}

	/**
	 * Get data in form of DoubleArrayList from cern.colt package
	 * 
	 * @return data in form of DoubleArrayList
	 */
	public DoubleArrayList getIntegerArrayList() {

		DoubleArrayList d = new DoubleArrayList();
		for (int i = 0; i < size(); i++) {
			d.add((double) getQuick(i));
		}
		return d;
	}

	/**
	 * Get complete statistics for this container. It return mean, error on the
	 * mean, RMS, variance, standard deviation.
	 * <p>
	 * The key for the output <b>map are: mean, error, rms, variance, stddev
	 * </b>. Print the key to find out what is inside.
	 * 
	 * @return map representing statistical characteristics of this data
	 */

	public Map<String, Double> getStat() {
		bin = new DynamicBin1D();

		cern.colt.list.IntArrayList a = new cern.colt.list.IntArrayList(
				elements());

		bin.addAllOf(getIntegerArrayList());

		Map<String, Double> tmp = new HashMap<String, Double>();

		double m = bin.mean();
		tmp.put("size", (double) size());
		tmp.put("sum", bin.sum());
		tmp.put("mean", m);
		tmp.put("kurtosis", bin.kurtosis());
		tmp.put("median", bin.median());
		tmp.put("rms", bin.rms());
		tmp.put("skew", bin.skew());
		tmp.put("standardDeviation", bin.standardDeviation());
		tmp.put("mean_error", bin.standardError());
		tmp.put("sumOfInversions", bin.sumOfInversions());
		tmp.put("variance", bin.variance());
		tmp.put("geometricMean", bin.geometricMean());
		tmp.put("harmonicMean", bin.harmonicMean());
		tmp.put("moment_1", bin.moment(0, 0));
		tmp.put("moment_2", bin.moment(2, 0));
		tmp.put("moment_3", bin.moment(3, 0));
		tmp.put("moment_4", bin.moment(4, 0));
		tmp.put("moment_5", bin.moment(5, 0));
		tmp.put("moment_1_mean", bin.moment(0, m));
		tmp.put("moment_2_mean", bin.moment(2, m));
		tmp.put("moment_3_mean", bin.moment(3, m));
		tmp.put("moment_4_mean", bin.moment(4, m));
		tmp.put("moment_5_mean", bin.moment(5, m));

		return tmp;
	}

	/**
	 * Returns the moment of k-th order with value c, which is Sum( (x[i]-c)k )
	 * / size(). First run getStat() for evaluation
	 * 
	 * @param k
	 *            k-th order
	 * @param c
	 *            c
	 * @return moment
	 * 
	 */

	public double moment(int k, double c) {
		if (bin == null) {
			System.out.println("Run first getStat() to evaluate sampling!");
			return -9999.0;
		}
		return bin.moment(k, c);

	}

	/**
	 * Returns the sample standard error, which is Math.sqrt(variance() /
	 * size()). Run getStat(0) before.
	 * 
	 * @return sample standard error
	 */
	public double standardError() {
		if (bin == null) {
			System.out.println("Run first getStat() to evaluate sampling!");
			return -9999.0;
		}
		return bin.standardError();

	}

	/**
	 * Returns the skew, which is moment(3,mean()) / standardDeviation() Run
	 * getStat(0 for evaluation
	 * 
	 * @return skewness
	 */
	public double skew() {
		if (bin == null) {
			System.out.println("Run first getStat() to evaluate sampling!");
			return -9999.0;
		}
		return bin.skew();

	}

	/**
	 * Returns the variance. Run getStat() first to update or evaluate
	 * 
	 * @return variance
	 */

	public double variance() {
		if (bin == null) {
			System.out.println("Run first getStat() to evaluate sampling!");
			return -9999.0;
		}

		return bin.variance();

	}

	/**
	 * Returns the standard deviation (square root of variance)
	 * 
	 * @return variance
	 */

	public double stddeviation() {
		if (bin == null) {
			System.out.println("Run first getStat() to evaluate sampling!");
			return -9999.0;
		}

		return bin.standardDeviation();

	}

	/**
	 * Returns the covariance
	 * 
	 * @param P0I
	 *            P0I object for covariance calculations
	 * 
	 * @return covariance value
	 */

	public double covariance(P0I P0I) {

		return StatisticSample.covariance(getArrayDouble(),
				P0I.getArrayDouble());

	}

	/**
	 * Get a double array with P0I values.
	 * 
	 * @return Double array with values
	 */

	public double[] getArrayDouble() {

		double[] tmp = new double[size()];
		for (int i = 0; i < size(); i++) {
			tmp[i] = (double) getQuick(i);
		}
		return tmp;
	}

	/**
	 * Returns the correlation coefficient. Values between 0 (no correlation)
	 * and 1 (full linear correlation)
	 * 
	 * @param P0I
	 *            P0I object for correlation calculation.
	 * 
	 * @return correlation coefficient.
	 */

	public double correlation(P0I P0I) {

		return StatisticSample.correlation(getArrayDouble(),
				P0I.getArrayDouble());

	}

	/**
	 * Transform P0I array to array with values given by a function.
	 * 
	 * The function may have up to 3 independent variables in it (x,y,z).
	 * <p>
	 * This class is not fool proof. If the answer is wrong then use the
	 * parenthesis to force the order of evaluation. The most likely place this
	 * will be needed is in the use of the power command. The exponent is not
	 * evaluated correctly if it begins with a unary operator.
	 * <h3>List of commands</h3>
	 * <ul>
	 * <li>( ) parenthesis , comma
	 * <li>+, -, unary -, unary +
	 * <li>, /
	 * <li>^ (raise to a power)
	 * <li>pi, e, All the constants in class SpecialFunction
	 * <li>log
	 * <li>sin, cos, tan
	 * <li>asin, acos, atan
	 * <li>sqrt
	 * <li>rand
	 * <li>exp
	 * <li>remainder
	 * <li>atan2
	 * <li>All the functions in class SpecialFunction
	 * <li>Independent variables x,y,z
	 * <li>Scientific notation using "e", "E", "d", "D".
	 * </ul>
	 * 
	 * @param f1d
	 *            function for transformation
	 * @return transformed P0I
	 */
	public P0I func(F1D f1d) {

		for (int i = 0; i < size(); i++) {
			set(i, (int) f1d.eval(getQuick(i)));
		} // end loop

		return this;

	}




        /**
         * Sum all ellements of this array.
         * @return sum of all ellements 
         **/
        public int getSum() {
                int  d=0;
                for (int i = 0; i < size(); i++) d=d+getQuick(i); 
                return d;

        }




	/**
	 * Make transformation of P0I using a function.
	 * 
	 * @param title
	 *            new title
	 * @param f1d
	 *            function for transformation
	 * @return transformed P0I
	 */
	public P0I func(String title, F1D f1d) {
		this.title = title;
		for (int i = 0; i < size(); i++) {
			set(i, (int) f1d.eval(getQuick(i)));
		} // end loop

		return this;

	}

	transient private Comparator cmp = new Comparator() {
		public int compare(Object o1, Object o2) {
			return ((Comparable) o1).compareTo(o2);
		}
	};

	/**
	 * Return indexes of sorted array in increasing order. The array itself will
	 * not be changed, since it acts on a clone. Used quicksort stable
	 * algorithm.
	 * 
	 * @return indexes of sorted array.
	 */
	public int[] sortIndex() {
		// make a clone and sort in increasing order
		Integer[] clone = new Integer[size()];
		for (int i = 0; i < size(); i++)
			clone[i] = (int) getQuick(i);
		int[] indeces = SortUtils.identity(size());
		SortUtils.sort(indeces, clone, cmp, true);
		return indeces;

	}

	/**
	 * Print the P0I container to a Table in a separate Frame. The numbers are
	 * formatted to scientific format. One can sort and search the data in this
	 * table (but not modify)
	 */

	public void toTable() {

		new HTable(this);

	}

	/**
	 * Test
	 * 
	 * @param args
	 */

	/*
	 * public static void main(String[] args) {
	 * 
	 * HPlot c1 = new HPlot("Canvas", 600, 400); c1.visible(true);
	 * c1.setAutoRange();
	 * 
	 * P0I p0 = new P0I("Normal distribution"); p0.randomNormal(1000, 0.0, 1.0);
	 * 
	 * c1.setNameX("X"); c1.setNameY("Y");
	 * 
	 * // make copy and transform. String func1 = "x*cos(x)+2"; P0I p01 =
	 * p0.copy(func1); // p01.func(func1);
	 * 
	 * // make copy and transform. func1 = "exp(x)-2"; P0I p02 = p0.copy(func1);
	 * // p02.func(func1);
	 * 
	 * // draw as histogram H1D h1 = p0.getH1D(20); c1.draw(h1); H1D h2 =
	 * p01.getH1D(100); h2.setFill(true); h2.setFillColor(java.awt.Color.blue);
	 * h2.setColor(java.awt.Color.red); c1.draw(h2);
	 * 
	 * H1D h3 = p02.getH1D(100); h3.setFill(true);
	 * h3.setFillColor(java.awt.Color.green); h3.setColor(java.awt.Color.red);
	 * c1.draw(h3);
	 * 
	 * }
	 */

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

}
