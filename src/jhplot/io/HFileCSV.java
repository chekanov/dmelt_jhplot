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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jhplot.*;
import hep.aida.*;
import hep.aida.ref.histogram.*;

import jhplot.gui.HelpBrowser;
import jhplot.io.csv.CSVReader;
import jhplot.io.csv.CSVWriter;

/**
 * 
 * Write or read data in CSV (comma-separated values) format. The CSV is data
 * interchange Format (.csv) used to import/export spreadsheets between
 * spreadsheet programs. The files are simple text files that can be readable.
 * Should be used to write main ScaVis data structures and import them to
 * spreadsheet (such as OpenOffice). You can write only one object in one file.
 * 
 * @author S.Chekanov
 * 
 */
public class HFileCSV {

	private FileWriter outStream;
	private BufferedReader inStream;
	final private int version = 1;
	private int nev = 0;
	private String file;
	private char delim = ',';
	private char quotechar = CSVWriter.NO_QUOTE_CHARACTER;

	/**
	 * Open a file to write/read data to/from a CSV file. If "w" option is set,
	 * the old file will be removed. Use close() to flash the buffer and close
	 * the file. We suppress all quoting. The default value separation is comma.
	 * 
	 * <p>
	 * If the file name starts from http or ftp, we assume it is located on the
	 * Web and will attempt to read it from URL.
	 * 
	 * @param file
	 *            File name. Can be located on URL if starts from http or ftp.
	 * @param option
	 *            Option to create the file. If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 * @param delim
	 *            separator of values. Default is "comma".
	 */
	public HFileCSV(String file, String option, char delim) {

		this.file = file.trim();
		nev = 0;

		this.delim = delim;

		if (option.equalsIgnoreCase("w")) {

			try {
				(new File(file)).delete();
				outStream = new FileWriter(file);

			} catch (IOException e) {
				System.err.println(e.toString());
			}

		} else if (option.equalsIgnoreCase("r")) {

			// check. Is is URL?
			if (file.startsWith("http") || file.startsWith("ftp")) {
				URL url = null;
				try {
					url = new URL(this.file);
				} catch (MalformedURLException e) {
					System.err.println(e.toString());
				}
				try {
					URLConnection urlConn = url.openConnection();
					urlConn.setDoInput(true);
					urlConn.setUseCaches(false);
					inStream = new BufferedReader(new InputStreamReader(
							urlConn.getInputStream()));

				} catch (MalformedURLException e) {
					System.err.println(e.toString());
				} catch (IOException e) {
					System.err.println(e.toString());
				}

			} else { // this is normal file on a file system

				try {
					inStream = new BufferedReader(new FileReader(this.file));
				} catch (IOException e) {
					System.err.println(e.toString());
				}

			}
		} else {

			jhplot.utils.Util
					.ErrorMessage("Wrong option!. Only \"r\" or \"w\"  is allowed");
		}

	};

	/**
	 * Open file for reading assuming comma for value separation.
	 * 
	 * @param file
	 *            File name
	 */
	public HFileCSV(String file) {
		this(file, "r", ',');

	};

	/**
	 * Open file for reading/writing. Assumes comma to separate the values. Now
	 * quotes by default.
	 * 
	 * @param file
	 *            File name
	 * @param option
	 *            option = "w" to write file, "r" to read file.
	 */
	public HFileCSV(String file, String option) {
		this(file, option, ',');

	};

	/**
	 * Set a quote character. By default, none.
	 * 
	 * @param c
	 */
	public void setQuotechar(char c) {

		quotechar = c;
	}

	/**
	 * Get current quote character.
	 * 
	 * @return c
	 */
	public char getQuotechar() {

		return quotechar;
	}

	/**
	 * Write double 1D array into the CSV file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(double[] p) {
		nev++;
		boolean tmp = true;

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(file), delim,
					quotechar);
			String[] nextLine = new String[p.length];
			for (int j = 0; j < p.length; j++)
				nextLine[j] = Double.toString(p[j]);
			writer.writeNext(nextLine);

			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}

		return tmp;

	}

	/**
	 * Write double 1D array into the CSV file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(int[] p) {
		nev++;
		boolean tmp = true;

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(file), delim,
					quotechar);
			String[] nextLine = new String[p.length];
			for (int j = 0; j < p.length; j++)
				nextLine[j] = Integer.toString(p[j]);

			writer.writeNext(nextLine);

			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}
		return tmp;

	}

	/**
	 * Write double 1D array into the CSV file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(String[] p) {

		nev++;
		boolean tmp = true;

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(file), quotechar);
			writer.writeNext(p);
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}
		return tmp;

	}

	/**
	 * Write 1D array into the CSV file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(P0I p) {

		nev++;
		boolean tmp = true;

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(file), delim,
					quotechar);
			String[] nextLine = new String[p.size()];
			for (int j = 0; j < p.size(); j++)
				nextLine[j] = Integer.toString(p.get(j));

			writer.writeNext(nextLine);

			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}
		return tmp;

	}

	/**
	 * Write 1D array into the CSV file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(P0D p) {

		nev++;
		boolean tmp = true;

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(file), delim,
					quotechar);
			String[] nextLine = new String[p.size()];
			for (int j = 0; j < p.size(); j++)
				nextLine[j] = Double.toString(p.get(j));
			writer.writeNext(nextLine);
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}
		return tmp;

	}

	/**
	 * Write N-dimensional array into CSV file.
	 * 
	 * @param p
	 *            multidimensional array
	 * @return true if success
	 */
	public boolean write(PND p) {

		nev++;
		boolean tmp = true;

		CSVWriter writer = new CSVWriter(outStream, delim, quotechar);

		try {

			for (int j = 0; j < p.size(); j++) {
				double[] tt = (double[]) p.get(j);
				int dimension = tt.length;
				String[] nextLine = new String[dimension];

				for (int i = 0; i < dimension; i++)
					nextLine[j] = Double.toString(tt[i]);

				writer.writeNext(nextLine);
			}
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}
		return tmp;

	}

	/**
	 * Write N-dimensional (integer) array into CSV file.
	 * 
	 * @param p
	 *            multidimensional array
	 * @return true if success
	 */
	public boolean write(PNI p) {

		nev++;
		boolean tmp = true;

		CSVWriter writer = new CSVWriter(outStream, delim, quotechar);

		try {

			for (int j = 0; j < p.size(); j++) {
				int[] tt = (int[]) p.get(j);
				int dimension = tt.length;
				String[] nextLine = new String[dimension];

				for (int i = 0; i < dimension; i++)
					nextLine[j] = Double.toString(tt[i]);

				writer.writeNext(nextLine);
			}
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}
		return tmp;

	}

	/**
	 * Write 1-D histograms (including errors) into the CSV file. First 2
	 * columns are lower and upper edges of the bins, 3rd column is height, 4th
	 * is the error on the heights.
	 * 
	 * 
	 * @param p
	 *            histogram
	 * @return true if success
	 */
	public boolean write(H1D p) {

		nev++;
		boolean tmp = true;

		CSVWriter writer = new CSVWriter(outStream, delim, quotechar);
		Histogram1D h1 = p.get();
		IAxis axis = h1.axis();
		int bins = axis.bins();

		try {
			for (int j = 0; j < bins; j++) {
				String[] nextLine = new String[4];
				nextLine[0] = Double.toString(axis.binLowerEdge(j));
				nextLine[1] = Double.toString(axis.binUpperEdge(j));
				nextLine[2] = Double.toString(h1.binHeight(j));
				nextLine[3] = Double.toString(h1.binError(j));
				writer.writeNext(nextLine);
			}

			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}
		return tmp;

	}

	/**
	 * Write 2D array into the CSV file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(P1D p) {
		nev++;
		boolean tmp = true;
		int dimen = p.getDimension();

		CSVWriter writer = new CSVWriter(outStream, delim, quotechar);

		try {

			for (int i = 0; i < p.size(); i++) {

				if (dimen == 2) {
					String[] nextLine = new String[2];
					nextLine[0] = Double.toString(p.getX(i));
					nextLine[1] = Double.toString(p.getY(i));
					writer.writeNext(nextLine);

				} else if (dimen == 3) {

					String[] nextLine = new String[3];
					nextLine[0] = Double.toString(p.getX(i));
					nextLine[1] = Double.toString(p.getY(i));
					nextLine[2] = Double.toString(p.getYupper(i));
					writer.writeNext(nextLine);

				} else if (dimen == 4) {

					String[] nextLine = new String[4];
					nextLine[0] = Double.toString(p.getX(i));
					nextLine[1] = Double.toString(p.getY(i));
					nextLine[2] = Double.toString(p.getYupper(i));
					nextLine[3] = Double.toString(p.getYlower(i));
					writer.writeNext(nextLine);

				} else if (dimen == 6) {

					String[] nextLine = new String[6];
					nextLine[0] = Double.toString(p.getX(i));
					nextLine[1] = Double.toString(p.getY(i));
					nextLine[2] = Double.toString(p.getYupper(i));
					nextLine[3] = Double.toString(p.getYlower(i));
					nextLine[4] = Double.toString(p.getXleft(i));
					nextLine[5] = Double.toString(p.getXright(i));
					writer.writeNext(nextLine);

				}

				else if (dimen == 10) {

					String[] nextLine = new String[10];
					nextLine[0] = Double.toString(p.getX(i));
					nextLine[1] = Double.toString(p.getY(i));
					nextLine[2] = Double.toString(p.getYupper(i));
					nextLine[3] = Double.toString(p.getYlower(i));
					nextLine[4] = Double.toString(p.getXleft(i));
					nextLine[5] = Double.toString(p.getXright(i));
					nextLine[6] = Double.toString(p.getYupperSys(i));
					nextLine[7] = Double.toString(p.getYlowerSys(i));
					nextLine[8] = Double.toString(p.getXleftSys(i));
					nextLine[9] = Double.toString(p.getXrightSys(i));
					writer.writeNext(nextLine);

				}

			}

			writer.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
			tmp = false;
		}

		return tmp;

	}

	/**
	 * Read data as sheet.
	 * 
	 * @return CSV file
	 */

	public CSVReader read() {

		CSVReader reader = null;

		reader = new CSVReader(inStream, delim, quotechar);

		return reader;
	}

	/**
	 * Close the file if needed.
	 * 
	 * @return true if success
	 */
	public boolean close() {

		boolean success = true;
		try {

			if (inStream != null) {
				inStream.close();
				inStream = null;
			}

			if (outStream != null) {
				outStream.close();
				outStream = null;
			}
		} catch (IOException e) {
			success = false;
			System.err.println(e.toString());

		}

		System.gc();
		return success;

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
