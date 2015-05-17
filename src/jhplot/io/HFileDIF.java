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
import java.text.ParseException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jhplot.*;
import hep.aida.*;
import hep.aida.ref.histogram.*;

import jhplot.gui.HelpBrowser;

import com.csam.dif.*;

/**
 * 
 * Write or read data in DIF format. The DIF is data interchange Format
 * (.dif) used to import/export spreadsheets between spreadsheet programs. The
 * files are simple text files that can be readable. Should be used to write main ScaVis data
 * structures and import them to spreadsheet (such as OpenOffice).
 * You can write only one object in one file.
 * 
 * @author S.Chekanov
 * 
 */
public class HFileDIF {

	private OutputStream outStream;
	private InputStream inStream;
	final private int version = 1;
	private int nev = 0;
	private DIFSheet sheet;
        private String file;

	/**
	 * Open a file to write/read data to/from a DIF file. If "w" option is set,
	 * the old file will be removed. Use close() to flash the buffer and close
	 * the file. 
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
	 */
	public HFileDIF(String file, String option) {

		this.file = file.trim();
		nev = 0;
		sheet = null;

		if (option.equalsIgnoreCase("w")) {

			try {
				(new File(file)).delete();
				outStream = new FileOutputStream(this.file);
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
					inStream = new DataInputStream(urlConn.getInputStream());
				} catch (MalformedURLException e) {
					System.err.println(e.toString());
				} catch (IOException e) {
					System.err.println(e.toString());
				}

			} else { // this is normal file on a file system

				try {
					inStream = new FileInputStream(new File(this.file));

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
	 * Open file for reading.
	 * 
	 * @param file
	 *            File name
	 */
	public HFileDIF(String file) {

		this(file, "r");

	};

	/**
	 * Write double 1D array into the DIF file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(double[] p) {
		nev++;
		boolean tmp = true;

		DIFSheet sheet = new DIFSheet();
		sheet.setTitle("double[]");
		sheet.setVersion(version);
		try {
			sheet.createRow(0);
			for (int j = 0; j < p.length; j++) {
				try {
					// System.out.println(p[i]);
					sheet.getRow(0).createCell(j).setCellValue(p[j]);
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}

			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		// sheet.write(outStream);
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write double 1D array into the DIF file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(int[] p) {
		nev++;
		boolean tmp = true;

		DIFSheet sheet = new DIFSheet();
		sheet.setTitle("double[]");
		sheet.setVersion(version);
		try {
			sheet.createRow(0);
			for (int j = 0; j < p.length; j++) {
				try {
					sheet.getRow(0).createCell(j).setCellValue(p[j]);
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}

			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		// sheet.write(outStream);
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write double 1D array into the DIF file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(String[] p) {
		nev++;
		boolean tmp = true;

		DIFSheet sheet = new DIFSheet();
		sheet.setTitle("double[]");
		sheet.setVersion(version);
		try {
			sheet.createRow(0);
			for (int j = 0; j < p.length; j++) {
				try {
					sheet.getRow(0).createCell(j).setCellValue(p[j]);
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}

			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		// sheet.write(outStream);
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write 1D array into the DIF file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(P0I p) {
		nev++;
		boolean tmp = true;

		DIFSheet sheet = new DIFSheet();
		sheet.setTitle(p.getTitle());
		sheet.setVersion(version);
		try {
			sheet.createRow(0);
			for (int j = 0; j < p.size(); j++) {
				try {
					sheet.getRow(0).createCell(j).setCellValue(p.get(j));
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}

			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		// sheet.write(outStream);
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write 1D array into the DIF file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(P0D p) {
		nev++;
		boolean tmp = true;

		sheet = new DIFSheet();
		sheet.setTitle(p.getTitle());
		sheet.setVersion(version);
		try {
			sheet.createRow(0);
			for (int j = 0; j < p.size(); j++) {
				try {
					sheet.getRow(0).createCell(j).setCellValue(p.get(j));
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}

			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		// sheet.write(outStream);
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write N-dimensional array into the DIF file.
	 * 
	 * @param p
	 *            multidimensional array
	 * @return true if success
	 */
	public boolean write(PND p) {

		nev++;
		boolean tmp = true;

		sheet = new DIFSheet();
		sheet.setTitle(p.getTitle());
		sheet.setVersion(version);
		try {
			for (int j = 0; j < p.size(); j++) {
				sheet.createRow(j);
				try {
					double[] tt = (double[]) p.get(j);
					int dimension = tt.length;
					for (int i = 0; i < dimension; i++)
						sheet.getRow(j).createCell(i).setCellValue(tt[i]);
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}
			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write N-dimensional array into the DIF file.
	 * 
	 * @param p
	 *            multidimensional (integer) array
	 * @return true if success
	 */
	public boolean write(PNI p) {

		nev++;
		boolean tmp = true;

		sheet = new DIFSheet();
		sheet.setTitle(p.getTitle());
		sheet.setVersion(version);
		try {
			for (int j = 0; j < p.size(); j++) {
				sheet.createRow(j);
				try {
					int[] tt = (int[]) p.get(j);
					int dimension = tt.length;
					for (int i = 0; i < dimension; i++)
						sheet.getRow(j).createCell(i).setCellValue(tt[i]);
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}
			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write 1-D histograms (including errors) into the DIF file. First 2
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

		sheet = new DIFSheet();
		sheet.setTitle(p.getTitle());
		sheet.setVersion(version);

		Histogram1D h1 = p.get();
		IAxis axis = h1.axis();
		int bins = axis.bins();

		try {
			for (int j = 0; j < bins; j++) {
				sheet.createRow(j);
				try {

					sheet.getRow(j).createCell(0)
							.setCellValue(axis.binLowerEdge(j));
					sheet.getRow(j).createCell(1)
							.setCellValue(axis.binUpperEdge(j));
					sheet.getRow(j).createCell(2).setCellValue(h1.binHeight(j));
					sheet.getRow(j).createCell(3).setCellValue(h1.binError(j));
				} catch (DIFCellExistsException e) {
					System.err.println(e.toString());
					tmp = false;
				}
			}
		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Write 2D array into the DIF file.
	 * 
	 * @param p
	 *            input array
	 * @return true if success
	 */
	public boolean write(P1D p) {
		nev++;
		boolean tmp = true;
		int dimen = p.getDimension();
		sheet = new DIFSheet();
		sheet.setTitle(p.getTitle());
		sheet.setVersion(version);
		try {

			for (int i = 0; i < p.size(); i++) {

				sheet.createRow(i);
				try {

					if (dimen == 2) {

						sheet.getRow(i).createCell(0).setCellValue(p.getX(i));
						sheet.getRow(i).createCell(1).setCellValue(p.getY(i));

					} else if (dimen == 3) {

						sheet.getRow(i).createCell(0).setCellValue(p.getX(i));
						sheet.getRow(i).createCell(1).setCellValue(p.getY(i));
						sheet.getRow(i).createCell(2)
								.setCellValue(p.getYupper(i));

					} else if (dimen == 4) {

						sheet.getRow(i).createCell(0).setCellValue(p.getX(i));
						sheet.getRow(i).createCell(1).setCellValue(p.getY(i));
						sheet.getRow(i).createCell(2)
								.setCellValue(p.getYupper(i));
						sheet.getRow(i).createCell(3)
								.setCellValue(p.getYlower(i));

					} else if (dimen == 6) {

						sheet.getRow(i).createCell(0).setCellValue(p.getX(i));
						sheet.getRow(i).createCell(1).setCellValue(p.getY(i));
						sheet.getRow(i).createCell(2)
								.setCellValue(p.getYupper(i));
						sheet.getRow(i).createCell(3)
								.setCellValue(p.getYlower(i));
						sheet.getRow(i).createCell(4)
								.setCellValue(p.getXleft(i));
						sheet.getRow(i).createCell(5)
								.setCellValue(p.getXright(i));

					}

					else if (dimen == 10) {

						sheet.getRow(i).createCell(0).setCellValue(p.getX(i));
						sheet.getRow(i).createCell(1).setCellValue(p.getY(i));
						sheet.getRow(i).createCell(2)
								.setCellValue(p.getYupper(i));
						sheet.getRow(i).createCell(3)
								.setCellValue(p.getYlower(i));
						sheet.getRow(i).createCell(4)
								.setCellValue(p.getXleft(i));
						sheet.getRow(i).createCell(5)
								.setCellValue(p.getXright(i));

						sheet.getRow(i).createCell(6)
								.setCellValue(p.getYupperSys(i));
						sheet.getRow(i).createCell(7)
								.setCellValue(p.getYlowerSys(i));
						sheet.getRow(i).createCell(8)
								.setCellValue(p.getXleftSys(i));
						sheet.getRow(i).createCell(8)
								.setCellValue(p.getXrightSys(i));

					}
				} catch (DIFCellExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (DIFRowExistsException e) {
			System.err.println(e.toString());
			tmp = false;
		}
		// sheet.write(outStream);
		try {
                        outStream = new FileOutputStream(file);
			sheet.write(outStream);
			outStream.close();
		} catch (Throwable thrown) {
			thrown.printStackTrace();
		}

		return tmp;

	}

	/**
	 * Read data as sheet.
	 * 
	 * @return DIFSheet data.
	 */

	public DIFSheet read() {

		try {
			sheet = new DIFSheet(inStream);
		} catch (DIFKeywordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DIFDataPairParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DIFNumberPairInfoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DIFStringFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sheet;
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
