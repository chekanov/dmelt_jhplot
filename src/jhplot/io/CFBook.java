// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package jhplot.io;

import java.io.*;
import java.util.*;
import jhplot.*;
import javax.swing.JOptionPane;
import jplot.XMLRead;

/**
 * 
 * This class is used to book  histograms for writing and reading using XML. It can read
 * any input with histograms defined by an external XML file. In particular, it
 * can read histogram files in the form of XML generated by Fortran or C++ external programs. 
 * To create a file with histograms from C++ or Fortran, use CFBook library. You will need gcc or gfortran to compile it. 
 * See  <a href="https://datamelt.org/?id=cfbook-library">CFBook web page</a> 
 * 
 * @see <a href="https://datamelt.org/?id=cfbook-library">CFBook library web page</a>.
 * 
 * @author S.Chekanov
 * 
 */

public class CFBook {

	private static CFBook instance = null;

	private BufferedReader reader = null;

	private Map<Integer, H1D> mapH1D;

	private Map<Integer, H2D> mapH2D;

	private Map<Integer, P1D> mapP1D;

	private P1D p1;

	private H1D h1;

	private H2D h2;

	private String description;

	private String createdBy="none";

	/**
	 * Initialize CFBook. The description will be written in XML file during the
	 * output.
	 * 
	 * @param description
	 *            description.
	 */

	public CFBook(String description) {

		this.description = description;
		mapH1D = new HashMap<Integer, H1D>();
		mapH2D = new HashMap<Integer, H2D>();
		mapP1D = new HashMap<Integer, P1D>();
	}

	/**
	 * Initialize CFBook class.
	 */

	public CFBook() {
		this("NOT SET");
	}

	/**
	 * write an external XML file with all 
	 * CFBook objects (H1D, H2D, P1D)
	 * 
	 * @param file
	 *            output file name
	 */

	public void write(String file) {

		Date dat = new Date();
		String today = String.valueOf(dat);

		try {
			FileOutputStream f1 = new FileOutputStream(new File(file));
			PrintStream tx = new PrintStream(f1);

			tx.println("<jhepwork>");
			tx
					.println("<created-by>DataMelt. Class CFBook</created-by>");
			tx.println("<created-on>" + today + "</created-on>");
			tx.println("<description>" + description + "</description>");
			tx.println("");

			// --- write H1D if any ----//
			Iterator keysIt = mapH1D.keySet().iterator();
			while (keysIt.hasNext()) {
				Object key = keysIt.next();
				h1 = (H1D) mapH1D.get(key);
				tx.println("<histogram-h1d>");
				tx.println("<title=\"" + h1.getTitle() + "\" id=\""
						+ key.toString() + "\"/>");

				tx.println("<x-axis>");
				tx.println("  <range bins=\"" + h1.getBins() + "\" min=\""
						+ h1.getMin() + "\" max=\"" + h1.getMax() + "\"/>");
				tx.println("  <out-of-range underflow=\"" + h1.getUnderflow()
						+ "\"" + " overflow=\"" + h1.getOverflow() + "\"/>");

				tx.println("</x-axis>");

				// statistics
				tx.println("<statistics>");
				tx.println("  <all-entries>" + h1.allEntries()
						+ "  </all-entries>");

				tx.println("  <in-range-entries>" + h1.entries()
						+ "  </in-range-entries>");
				tx.println("  <out-of-range-entries>" + h1.extraEntries()
						+ "  </out-of-range-entries>");

				tx.println("  <mean>" + h1.mean() + "  </mean>");

				tx.println("  <rms>" + h1.rms() + "  </rms>");
				tx.println("</statistics>");

				// bin content
				tx.println("<bincontents order=\"xLow,xHigh,y\">");
				tx.println("  binLower,binHigh,hight,error,entries");
				tx.println("</bincontents>");

				tx.println("<data>");
				// System.out.println("h1.getBins()="+h1.getBins());
				for (int i = 0; i < h1.getBins(); i++) {
					tx.println(h1.binLowerEdge(i) + "," + h1.binUpperEdge(i)
							+ "," + h1.binHeight(i) + "," + h1.binError(i)
							+ "," + h1.binEntries(i));
				}
				tx.println("</data>");
				tx.println("</histogram-h1d>");
				tx.println("");
			} // end loop over H1D

			// --- write H2D if any ----//
			keysIt = mapH2D.keySet().iterator();
			while (keysIt.hasNext()) {
				Object key = keysIt.next();
				h2 = (H2D) mapH2D.get(key);
				tx.println("<histogram-h2d>");
				tx.println("<title=\"" + h1.getTitle() + "\" id=\""
						+ key.toString() + "\"/>");

				// X-axis
				tx.println("<x-axis>");
				tx.println("  <range bins=\"" + h2.getBinsX() + "\" min=\""
						+ h2.getMinX() + "\" max=\"" + h2.getMaxX() + "\"/>");
				tx.println("  <out-of-range underflow=\""
						+ h2.getUnderflowHeightX() + "\"" + " overflow=\""
						+ h2.getOverflowHeightX() + "\"/>");

				tx.println("<variable-width-bins>");
				for (int j = 0; j < h2.getBinsX(); j++) {
					tx.println(h2.getLowerEdgeX(j) + "," + h2.getUpperEdgeX(j));
				}
				tx.println("</variable-width-bins>");
				tx.println("</x-axis>");

				// Y-axis
				tx.println("<y-axis>");
				tx.println("  <range bins=\"" + h2.getBinsY() + "\" min=\""
						+ h2.getMinY() + "\" max=\"" + h2.getMaxY() + "\"/>");
				tx.println("  <out-of-range underflow=\""
						+ h2.getUnderflowHeightY() + "\"" + " overflow=\""
						+ h2.getOverflowHeightY() + "\"/>");

				tx.println("<variable-width-bins>");
				for (int j = 0; j < h2.getBinsY(); j++) {
					tx.println(h2.getLowerEdgeY(j) + "," + h2.getUpperEdgeY(j));
				}
				tx.println("</variable-width-bins>");
				tx.println("</y-axis>");

				// out of range
				// 6 | 7 | 8
				// -----------
				// 3 | 4 | 5
				// -----------
				// 0 | 1 | 2

				double[] outr = new double[9];

				outr[0] = h2.getUnderflowEntriesX() + h2.getUnderflowEntriesY();
				outr[3] = h2.getUnderflowEntriesX();
				outr[6] = h2.getUnderflowEntriesX() + h2.getUnderflowEntriesY();

				outr[0] = h2.getUnderflowEntriesY();
				outr[1] = h2.getUnderflowEntriesY();
				outr[2] = h2.getUnderflowEntriesY() + h2.getOverflowEntriesY();

				outr[6] = h2.getOverflowEntriesY();
				outr[7] = h2.getOverflowEntriesY();
				outr[8] = h2.getOverflowEntriesY() + h2.getOverflowEntriesX();

				outr[2] = h2.getOverflowEntriesX();
				outr[8] = h2.getOverflowEntriesX() + h2.getOverflowEntriesY();
				outr[5] = h2.getOverflowEntriesX();

				// out of range
				tx.println("<out-of-range-data>");
				for (int i = 0; i < 9; i++)
					tx.println(" " + outr[i]);
				tx.println("</out-of-range-data>");

				// statistics
				tx.println("<statistics>");
				tx.println("  <all-entries>" + h2.allEntries()
						+ "  </all-entries>");
				tx.println("  <in-range-entries>" + h2.entries()
						+ "  </in-range-entries>");
				tx.println("  <out-of-range-entries>" + h2.extraEntries()
						+ "  </out-of-range-entries>");
				tx.println("  <all-hights>" + h2.sumAllBinHeights()
						+ "  </all-hights>");
				tx.println("  <in-range-hights>"
						+ (h2.sumExtraBinHeights() - h2.sumExtraBinHeights())
						+ "  </in-range-hights>");
				tx.println("  <out-of-range-hights>" + h2.sumExtraBinHeights()
						+ "  </out-of-range-hights>");

				tx.println("  <x-mean>" + h2.getMeanX() + "  </x-mean>");
				tx.println("  <x-rms>" + h2.getRmsX() + "  </x-rms>");
				tx.println("  <y-mean>" + h2.getMeanY() + "  </y-mean>");
				tx.println("  <y-rms>" + h2.getRmsY() + "  </y-rms>");
				tx.println("</statistics>");

				// bin content
				tx.println("<bincontents order=\"xy\">");
				tx.println("  bin,height,error,entries");
				tx.println("</bincontents>");

				tx.println("<data>");

				// System.out.println("h2.getBinsX()="+Integer.toString(h2.getBinsX()));
				// System.out.println("h2.getBinsY()="+Integer.toString(h2.getBinsY()));
				for (int j1 = 0; j1 < h2.getBinsX(); j1++) {
					for (int j2 = 0; j2 < h2.getBinsY(); j2++) {
						tx.println(j1 + "," + j2 + "," + h2.binHeight(j1, j2)
								+ "," + h2.binError(j1, j2) + ","
								+ h2.binEntries(j1, j2));
					}
				}

				tx.println("</data>");
				tx.println("</histogram-h2d>");
				tx.println("");
			} // end loop over H2D

			// --- write P1D if any ----//
			keysIt = mapP1D.keySet().iterator();
			while (keysIt.hasNext()) {
				Object key = keysIt.next();
				p1 = (P1D) mapP1D.get(key);
				tx.println("<container-p1d>");
				tx.println("<title=\"" + p1.getTitle() + "\" id=\""
						+ key.toString() + "\"/>");

				tx.println("<dimension-p1d>" + p1.dimension()
						+ "</dimension-p1d>");

				tx.println("<data>");
				for (int i = 0; i < p1.size(); i++) {

					if (p1.dimension() == 2)
						tx.println(p1.getX(i) + "," + p1.getY(i));

					if (p1.dimension() == 4)
						tx.println(p1.getX(i) + "," + p1.getY(i) + ","
								+ p1.getYupper(i) + "," + p1.getYlower(i));

					if (p1.dimension() == 6)
						tx.println(p1.getX(i) + "," + p1.getY(i) + ","
								+ p1.getXleft(i) + "," + p1.getXright(i) + ","
								+ p1.getYupper(i) + "," + p1.getYlower(i));

					if (p1.dimension() == 10)
						tx.println(p1.getX(i) + "," + p1.getY(i) + ","
								+ p1.getXleft(i) + "," + p1.getXright(i) + ","
								+ p1.getYupper(i) + "," + p1.getYlower(i) + ","
								+ p1.getXleftSys(i) + "," + p1.getXrightSys(i)
								+ "," + p1.getYupperSys(i) + ","
								+ p1.getYlowerSys(i));
				}
				tx.println("</data>");
				tx.println("</container-p1d>");
				tx.println("");
			} // end loop over P1D

			tx.println("</jhepwork>");
			f1.close();

		} catch (IOException e) {
			ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	/**
	 * Read external XML file with all objects (H1D, H2D, P1D)
	 * 
	 * @param file
	 *            input file name
	 */

	public void read(String file) {

		try {

			reader = new BufferedReader(new FileReader(file));
			XMLRead xr = new XMLRead();

			// read all lines concerning jhplot, i.e between <jhplot> and
			// </jhplot>:
			if (!xr.parse(reader, "jhepwork")) {
				ErrorMessage("This is not valid jhepwork XML file");
				return;
			}

			createdBy = xr.getString("created-by", "NOT SET");
			String time_maker = xr.getString("created-on", "NOT SET");
			description = xr.getString("description", "NOT SET");

			// now read H1D histograms
			double min, max;
			double underflow, overflow;

			int id, bins = 0;
			// 1D histograms
			int k1 = 0;
			while (xr.open("histogram-h1d")) {

				String stitle = xr.getString("title", "NOT SET");
				id = xr.getInt("id", 0);

				// System.out.println(stitle);

				// open axis
				xr.open("x-axis");
				min = xr.getDouble("range/min", 0.0);
				max = xr.getDouble("range/max", 0.0);
				bins = xr.getInt("range/bins", 0);
				// System.out.println(bins);
				underflow = xr.getDouble("out-of-range/underflow", 0.0);
				overflow = xr.getDouble("out-of-range/overflow", 0.0);

				// sufficient to build it
				h1 = new H1D(stitle, bins, min, max);
				xr.close(); // close x-axis

				// open statistics
				xr.open("statistic");
				h1.setNEntries((int) xr.getDouble("all-entries", 0.0));
				h1.setValidEntries((int) xr.getDouble("in-range-entries", 0.0));
				double out_of_range = xr.getDouble("out-of-range-entries", 0.0);
				// h1.setMean(xr.getDouble("mean", 0.0));
				// h1.setRms(xr.getDouble("rms", 0.0));
				h1.setMeanAndRms(xr.getDouble("mean", 0.0), xr.getDouble("rms",
						0.0));
				// System.out.println(mean);
				xr.close(); // statistics

				xr.open("data");
				Vector<String> data = xr.getData();
				int nn = data.size();
				if (nn != bins) {
					System.out
							.println("Not valid H1D histogram definition in XML file");
					System.out.println("data block has the size=" + nn);
					System.out.println("but the number of expected bins="
							+ bins);
					// return;
				}
				// System.out.println("Check=");
				// System.out.println(nn);
				// System.out.println(bins);

				double[] hight = new double[nn + 2];
				double[] errors = new double[nn + 2];
				hight[0] = underflow; // underflow
				hight[nn + 1] = overflow; // overflow

				for (int i = 0; i < nn; i++) {
					String line = (String) data.elementAt(i);
					double d[] = getDoubles(line, ",");
					// System.out.println("h1d data="+line);;
					hight[i + 1] = d[2];
					errors[i + 1] = d[3];
				}
				xr.close(); // close data
				xr.hide("data");

				// now set it
				h1.setContents(hight, errors);
				mapH1D.put(id, h1);

				// System.out.println(" new histo="+Integer.toString(k1));
				xr.close(); // close histogram1d
				xr.hide("histogram-h1d");
				k1++;
			}

			int k2 = 0;
			double minx, miny, maxx, maxy;
			int binsx, binsy;

			while (xr.open("histogram-h2d")) {

				String stitle = xr.getString("title", "NOT SET");
				// System.out.println("2D histogram="+stitle);
				id = xr.getInt("id", 0);

				// open x axis
				xr.open("x-axis");
				binsx = xr.getInt("range/bins", 0);
				minx = xr.getDouble("range/min", 0.0);
				maxx = xr.getDouble("range/max", 0.0);
				// System.out.println(binsx);

				xr.open("variable-width-bins");
				Vector<String> dX = xr.getData();
				int nn = dX.size();

				if (nn != binsx) {
					System.out
							.println("Not valid 2D histogram definition in XML");
					System.out.println("data block has the size in X=" + nn);
					System.out.println("but the number of expected X bins="
							+ bins);

				}

				double[] xbins1 = new double[nn];
				double[] xbins2 = new double[nn];

				for (int i = 0; i < nn; i++) {
					String line = (String) dX.elementAt(i);
					double d[] = getDoubles(line, ",");
					xbins1[i] = d[0];
					xbins2[i] = d[1];
					// System.out.println( line );
				}

				xr.close(); // close variableWidthBins
				xr.hide("variable-width-bins");
				xr.close(); // close x-axis

				// open y axis
				xr.open("y-axis");
				miny = xr.getDouble("range/min", 0.0);
				maxy = xr.getDouble("range/max", 0.0);
				binsy = xr.getInt("range/bins", 0);

				xr.open("variable-width-bins");
				Vector<String> dY = xr.getData();

				nn = dY.size();
				if (nn != binsy) {
					System.out
							.println("Not valid 2D histogram definition in XML");
					System.out.println("data block has the size in Y=" + nn);
					System.out.println("but the number of expected Y bins="
							+ bins);
				}

				double[] ybins1 = new double[nn];
				double[] ybins2 = new double[nn];
				for (int i = 0; i < nn; i++) {
					String line = (String) dY.elementAt(i);
					double d[] = getDoubles(line, ",");
					ybins1[i] = d[0];
					ybins2[i] = d[1];
					// System.out.println( line );
				}

				xr.close(); // close variableWidthBins
				xr.hide("variable-width-bins");
				xr.close(); // close y-axis

				// System.out.println( "x-bins="+Integer.toString(binsx));
				// System.out.println( "y-bins="+Integer.toString(binsy));

				h2 = new H2D(stitle, binsx, minx, maxx, binsy, miny, maxy);
				// open statistics
				xr.open("statistic");
				h2.setNEntries((int) xr.getDouble("all-entries", 0.0));
				h2.setValidEntries((int) xr.getDouble("in-range-entries", 0.0));
				h2.setMeanX(xr.getDouble("x-mean", 0.0));
				h2.setRmsX(xr.getDouble("x-rms", 0.0));
				h2.setMeanY(xr.getDouble("y-mean", 0.0));
				h2.setRmsY(xr.getDouble("y-rms", 0.0));
				// System.out.println(mean);
				xr.close(); // statistics

				// Y // out of range
				// 6 | 7 | 8
				// -----------
				// 3 | 4 | 5
				// -----------
				// 0 | 1 | 2
				// X

				double[][] hight = new double[binsx + 2][binsy + 2];
				double[][] errors = new double[binsx + 2][binsy + 2];

				xr.open("out-of-range-data");
				Vector<String> outR = xr.getData();
				double[] outr = new double[outR.size()];
				for (int i = 0; i < outR.size(); i++) {
					String line = (String) outR.elementAt(i);
					outr[i] = 0;
					try {
						outr[i] = Double.parseDouble(line);
					} catch (NumberFormatException e) {
					}
					// System.out.println(outr[i]);
				}

				xr.close(); // close variableWidthBins
				xr.hide("out-of-range-data");

				hight[0][0] = outr[0];
				hight[binsx + 1][0] = outr[2];
				hight[0][binsy + 1] = outr[6];
				hight[binsx + 1][binsy + 1] = outr[8];

				// open data
				xr.open("data");
				Vector<String> data = xr.getData();

				for (int i = 0; i < data.size(); i++) {
					String line = (String) data.elementAt(i);
					double d[] = getDoubles(line, ",");
					// System.out.println("h2d data="+line);
					// System.out.println( Integer.toString(i) +" " +
					// Integer.toString( (int)d[0] )+ " "+
					// Integer.toString( (int)d[1]) );
					// System.out.println( d[1] );
					// System.out.println( d.length );

					int j1 = (int) d[0] + 1;
					int j2 = (int) d[1] + 1;
					hight[j1][j2] = d[2];
					errors[j1][j2] = d[3];

				}

				xr.hide("data");
				xr.close(); // close data

				h2.setContents(hight, errors);
				mapH2D.put(id, h2);

				// System.out.println(k2);
				xr.close();
				xr.hide("histogram-h2d");
				k2++;
			}

			// ------------------- P1D container
			// ----------------------------------

			while (xr.open("container-p1d")) {

				String stitle = xr.getString("title", "NOT SET");
				int idd = xr.getInt("id", 0);
				int dim = xr.getInt("dimension-p1d", 10);

				// sufficient to build it
				p1 = new P1D(stitle, dim);

				xr.open("data");
				Vector<String> pdata = xr.getData();
				for (int i = 0; i < pdata.size(); i++) {
					String line = (String) pdata.elementAt(i);
					// System.out.println("p1d data="+line);
					double snum[] = getDoubles(line, ",");
					int ncount = snum.length;

					if (ncount == 2)
						p1.add(snum[0], snum[1]);
					if (ncount == 4)
						p1.add(snum[0], snum[1], snum[2], snum[3]);
					if (ncount == 6)
						p1.add(snum[0], snum[1], snum[2], snum[3], snum[4],
								snum[5]);
					if (ncount == 10)
						p1.add(snum[0], snum[1], snum[2], snum[3], snum[4],
								snum[5], snum[6], snum[7], snum[8], snum[9]);

				}
				xr.close(); // close data
				xr.hide("data");
				// now set it
				mapP1D.put(idd, p1);
				xr.close(); // close p1d
				xr.hide("container-p1d");

			}

			// reader.close();
			xr.close(); // close jhepwork

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					// flush and close both "input" and its underlying
					// FileReader
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	} // end of CFBook class

	/**
	 * Get H1D histogram from the index (key)
	 * 
	 * @param id
	 *            index or key of histogram
	 * 
	 * 
	 * @return H1D histogram
	 */
	public H1D getH1D(int id) {

		if (mapH1D.containsKey(id) == false) {
			ErrorMessage("CFBook: No such H1D key!");
			return null;
		}

		return (H1D) mapH1D.get(id);

	}

	/**
	 * Get P1D object from the index (key)
	 * 
	 * @param id
	 *            index or key of P1D
	 * 
	 * 
	 * @return P1D histogram
	 */
	public P1D getP1D(int id) {

		if (mapP1D.containsKey(id) == false) {
			ErrorMessage("CFBook: No such P1D key!");
			return null;
		}

		return (P1D) mapP1D.get(id);

	}

	/**
	 * List all known H1D histograms. It prints indexes (keys) together with
	 * titles.
	 * 
	 * @return String representing all known keys.
	 */
	public String listH1D() {

		String tmp = "";

		if (mapH1D.size() > 0) {

			tmp = "\n\nList of all known H1D histograms:\n";
			Iterator keysIt = mapH1D.keySet().iterator();
			// Iterator valuesIt = mapH1D.values().iterator();
			while (keysIt.hasNext()) {
				Object key = keysIt.next();
				h1 = (H1D) mapH1D.get(key);
				// System.out.println("key="+key+",value="+value);
				tmp = tmp + key.toString() + ":  " + h1.getTitle() + "\n";
			}
		}

		return tmp;

	}

	/**
	 * List all known P1D objects. It prints indexes (keys) together with
	 * titles.
	 * 
	 * @return String representing all known keys.
	 */
	public String listP1D() {

		String tmp = "";

		if (mapP1D.size() > 0) {
			tmp = "\n\nList of all known P1D containers:\n";
			Iterator keysIt = mapP1D.keySet().iterator();
			// Iterator valuesIt = mapH1D.values().iterator();
			while (keysIt.hasNext()) {
				Object key = keysIt.next();
				p1 = (P1D) mapP1D.get(key);
				// System.out.println("key="+key+",value="+value);
				tmp = tmp + key.toString() + ":  " + p1.getTitle() + "\n";
			}
		}

		return tmp;

	}

	/**
	 * Get keys of all known H1D histograms.
	 * 
	 * @return array with all key numbers (integers)
	 */
	public int[] getKeysH1D() {
		Iterator keysIt = mapH1D.keySet().iterator();
		int[] array = new int[mapH1D.size()];
		int jj = 0;
		while (keysIt.hasNext()) {
			Object key = keysIt.next();
			String skey = key.toString();
			array[jj] = getInt(skey);
			jj++;
		}
		return array;

	}

	/**
	 * Get keys of all known P1D containers.
	 * 
	 * @return array with all key numbers (integers)
	 */
	public int[] getKeysP1D() {
		Iterator keysIt = mapP1D.keySet().iterator();
		int[] array = new int[mapP1D.size()];
		int jj = 0;
		while (keysIt.hasNext()) {
			Object key = keysIt.next();
			String skey = key.toString();
			array[jj] = getInt(skey);
			jj++;
		}
		return array;

	}

	/**
	 * Get array with all H1D histograms.
	 * 
	 * @return array with H1D histograms
	 */
	public H1D[] getAllH1D() {
		Iterator keysIt = mapH1D.keySet().iterator();
		H1D[] array = new H1D[mapH1D.size()];
		int jj = 0;
		while (keysIt.hasNext()) {
			Object key = keysIt.next();
			array[jj] = (H1D) mapH1D.get(key);
			jj++;
		}
		return array;
	}

	/**
	 * Get array with all P1D containers.
	 * 
	 * @return array with P1D containers
	 */
	public P1D[] getAllP1D() {
		Iterator keysIt = mapP1D.keySet().iterator();
		P1D[] array = new P1D[mapP1D.size()];
		int jj = 0;
		while (keysIt.hasNext()) {
			Object key = keysIt.next();
			array[jj] = (P1D) mapP1D.get(key);
			jj++;
		}
		return array;
	}

	/**
	 * Get array with all H2D histograms.
	 * 
	 * @return array with H2D histograms
	 */
	public H2D[] getAllH2D() {
		Iterator keysIt = mapH2D.keySet().iterator();
		H2D[] array = new H2D[mapH2D.size()];
		int jj = 0;
		while (keysIt.hasNext()) {
			Object key = keysIt.next();
			array[jj] = (H2D) mapH2D.get(key);
			jj++;
		}
		return array;
	}

	/**
	 * Get keys of all known H2D histograms.
	 * 
	 * @return array with all key numbers (integers)
	 */
	public int[] getKeysH2D() {
		Iterator keysIt = mapH2D.keySet().iterator();
		int[] array = new int[mapH2D.size()];
		int jj = 0;
		while (keysIt.hasNext()) {
			Object key = keysIt.next();
			String skey = key.toString();
			array[jj] = getInt(skey);
			jj++;
		}
		return array;

	}

	/**
	 * List all known H1D histograms.
	 * 
	 * @return String representing all keys and titles of histograms
	 */
	public String listH2D() {

		String tmp = "";

		if (mapH2D.size() > 0) {
			tmp = "\n\nList of all known H2D histograms:\n";
			Iterator keysIt = mapH2D.keySet().iterator();
			// Iterator valuesIt = mapH1D.values().iterator();
			while (keysIt.hasNext()) {
				Object key = keysIt.next();
				h2 = (H2D) mapH2D.get(key);
				// System.out.println("key="+key+",value="+value);
				tmp = tmp + key.toString() + ":  " + h2.getTitle() + "\n";
			}
		}

		return tmp;

	}

	/**
	 * List all objects inside CFBook
	 * 
	 * @return string with description of all objects inside CFBook.
	 * 
	 */
	public String listAll() {

		String tmp = "\n\nList of all known H1D histograms:\n";
		tmp = tmp + listP1D() + "\n" + listH1D() + "\n" + listH2D();
		return tmp;

	}

	/**
	 * Get H2D histogram from the index (key) ID
	 * 
	 * @param id
	 *            index
	 * @return H2D histogram
	 */
	public H2D getH2D(int id) {

		if (mapH2D.containsKey(id) == false) {
			ErrorMessage("CFBook: No such H2D key!");
			return null;
		}

		return (H2D) mapH2D.get(id);

	}

	/**
	 * add H1D histogram to CFBook
	 * 
	 * @param id
	 *            key to be assigned
	 * @param h1d
	 *            H1D histogram to be added
	 * 
	 */
	public void add(int id, H1D h1d) {

		mapH1D.put(id, h1d);

	}

	/**
	 * add H2D histogram to CFBook
	 * 
	 * @param id
	 *            key to be assigned
	 * @param h2d
	 *            H2D histogram to be added
	 * 
	 */
	public void add(int id, H2D h2d) {

		mapH2D.put(id, h2d);

	}

	/**
	 * add P1D histogram to CFBook
	 * 
	 * @param id
	 *            key to be assigned
	 * @param p1d
	 *            P1D histogram to be added
	 * 
	 */
	public void add(int id, P1D p1d) {

		mapP1D.put(id, p1d);

	}

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */

	private void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Get double after "="
	 * 
	 * @param a
	 *            input string
	 */
	private double getValue(String a) {
		double x = 0;
		a = a.trim();
		int i1 = a.lastIndexOf("=");
		if (i1 > -1) {
			String s = a.substring(i1 + 1, a.length());
			try {
				x = Double.parseDouble(s);
			} catch (NumberFormatException e) {
			}
		}
		return x;
	}

	/**
	 * Get single double
	 * 
	 * @param a
	 *            input string
	 */

	private double getDouble(String a) {
		double d1 = 0;
		try {
			d1 = Double.parseDouble(a);
		} catch (NumberFormatException e) {
		}

		return d1;
	}

	/**
	 * Get single integer
	 * 
	 * @param a
	 *            input string
	 */

	private int getInt(String a) {
		int id = 0;
		try {
			id = Integer.parseInt(a);
		} catch (NumberFormatException e) {
		}
		return id;
	}

	/**
	 * Remove all objects from the CFBook
	 * 
	 */

	public void clear() {
		mapH2D.clear();
		mapP1D.clear();
		mapH1D.clear();

	}

	/**
	 * Get array of double numbers from the line
	 * 
	 * @param a
	 *            input string
	 * @param tok
	 *            input token
	 */

	private double[] getDoubles(String a, String tok) {

		a = a.trim();
		StringTokenizer st = new StringTokenizer(a, tok);
		int ncount = st.countTokens(); // number of words
		double[] d = new double[ncount];

		String[] sword = new String[ncount];
		int m = 0;
		while (st.hasMoreTokens()) { // make sure there is stuff
			sword[m] = st.nextToken();
			m++;
		}

		// get the numbers
		for (int i = 0; i < m; i++) {
			try {
				d[i] = Double.parseDouble(sword[i]);
			} catch (NumberFormatException e) {
			}
		}

		return d;
	}

}