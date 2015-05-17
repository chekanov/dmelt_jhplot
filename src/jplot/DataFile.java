/**
 *    Copyright (C)  DataMelt project. The jHPLot package.
 *    Includes coding developed for Centre d'Informatique Geologique
 *    by J.V.Lee priory 2000 GNU license.
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

package jplot;

import java.io.*;
import java.util.*;

import jhplot.JHPlot;

/**
 * This class contains all the info specific for one data file. The PlotPanel
 * provides the columns to-be-plotted for a specific file, with a line-style
 * class for each column. This class keeps track of this information.
 */
public class DataFile {
	private File file;
	private DataArray data;
	private Vector items;
	private Vector columns;
	// private Vector columnNames;
	private int xColumn;
	private Vector styles;
	private String abbreviatedFilename;
	private String filenameWithoutPath;
	private final int maxLen = 45;
	private int index;
	private int graphType;
	private long lastModified;

	private GraphSettings gs;

	/**
	 * Builds the class from a filename. As soon as the data file is build, read
	 * the header of the file and make a list of all the available columns.
	 * 
	 * @param file
	 *            file for this set.
	 * @param index
	 *            index, kind of identifier of this instance. It also
	 *            corresponds to the tab number in the PlotPanel.
	 * @param gs
	 *            graph settings.
	 */
	DataFile(File file, int index, GraphSettings gs) {
		this.file = file;
		this.index = index;
		this.gs = gs;
		lastModified = 0;
		if (JHPlot.ReadFile)
			lastModified = file.lastModified();
		columns = new Vector();
		styles = new Vector();
		items = new Vector();
		graphType = gs.GRAPHTYPE_2D;
		int len = 0;
		abbreviatedFilename = data.getName();
		filenameWithoutPath = data.getName();

		if (JHPlot.ReadFile) {
			len = file.toString().length();
			if (len > maxLen) {
				abbreviatedFilename = "..."
						+ file.toString().substring(len - maxLen, len);
			} else
				abbreviatedFilename = file.toString();

			int i = abbreviatedFilename.lastIndexOf(JPlot.FS, 0);
			len = abbreviatedFilename.length();
			if (i > -1)
				filenameWithoutPath = abbreviatedFilename.substring(i, len);
			else
				filenameWithoutPath = abbreviatedFilename;
		}

		loadColumnNames(gs.getLabels());

	}

	/**
	 * Builds the class from a DataArray. As soon as the datafile is build, read
	 * the header of the file and make a list of all the available columns.
	 * 
	 * @param data
	 *            data for this set.
	 * @param index
	 *            index, kind of identifier of this instance. It also
	 *            corresponds to the tab number in the PlotPanel.
	 * @param gs
	 *            graph settings.
	 */
	DataFile(DataArray data, int index, GraphSettings gs) {
		this.data = data;
		this.index = index;
		this.gs = gs;
		lastModified = 0;
		columns = new Vector();
		styles = new Vector();
		items = new Vector();
		graphType = gs.GRAPHTYPE_2D;
		int len = 0;
		abbreviatedFilename = data.getName();
		filenameWithoutPath = data.getName();
		int i = abbreviatedFilename.lastIndexOf(JPlot.FS, 0);
		len = abbreviatedFilename.length();
		if (i > -1)
			filenameWithoutPath = abbreviatedFilename.substring(i, len);
		// loadColumnNamesData(gs.getLabels());
		loadColumnNames(gs.getLabels());

	}

	// void clear() {
	// if (columns.size() > 0) columns.removeAllElements();
	// if (styles.size() > 0) styles.removeAllElements();
	// if (items.size() > 0) items.removeAllElements();
	// //....
	// System.out.println("TODO: clear method in DataFile");
	// }

	/**
	 * Reads the datafile for data. The data is put in a specific purpose
	 * DataArray object, which contains x- and y values. Note that all blank
	 * lines or lines starting with '#' are skipped. One exception: lines
	 * starting with '# column 1:' etc. are supposed to provide the name of the
	 * column, hence '# column 1: nodes' means that column 1 has the name
	 * 'nodes', which will be used by this program in order to easily select
	 * columns.
	 * 
	 * If the list of column names existed already, and if the new list of names
	 * is identical, return true. Else delete the current selection and return
	 * false.
	 * 
	 * @param labels
	 *            vector to store the labels defined in the datafile
	 * @return true if the current selection can be maintained, false otherwise.
	 */
	public boolean loadColumnNamesData(Vector labels) {
		boolean res = true;
		Vector newItems = new Vector();

		graphType = gs.GRAPHTYPE_2D;
		LinePars lp = (LinePars) data;
		// lp.printLinePars();

		// setLinePars(0, lp);
		addItem(data.getName());

		addColumn(0, lp);
		addColumn(1, lp);

		setLinePars(0, lp);
		setLinePars(1, lp);
		// setLinePars(lp);
		setXColumn(0);

		// getSettings(XMLWrite xw);

		// setLinePars(1, lp);

		// gs.setDrawLegend(true);

		// Vector newLabels = new Vector();
		// newLabels.add("X");
		// newLabels.add("Y");

		Vector newLabels = new Vector();
		for (Enumeration e = labels.elements(); e.hasMoreElements();) {
			GraphLabel g = (GraphLabel) e.nextElement();
			if (g.equals(GraphLabel.DATA)) {
				g.setID(GraphLabel.CHECK);
			}
			newLabels.add(g);
		}

		// newLabels.add("X");
		// newLabels.add("Y");

		GraphLabel gl1 = new GraphLabel(GraphLabel.XLABEL, "X");
		addLabel(gl1, newLabels);

		// get the y-label from the dataset, if any
		GraphLabel gl2 = new GraphLabel(GraphLabel.YLABEL, data.getName());
		gl2.setRotation(Math.PI * 1.5);
		addLabel(gl2, newLabels);

		return res;
	}

	/**
	 * Reads the datafile for data. The data is put in a specific purpose
	 * DataArray object, which contains x- and y values. Note that all blank
	 * lines or lines starting with '#' are skipped. One exception: lines
	 * starting with '# column 1:' etc. are supposed to provide the name of the
	 * column, hence '# column 1: nodes' means that column 1 has the name
	 * 'nodes', which will be used by this program in order to easily select
	 * columns.
	 * 
	 * If the list of column names existed already, and if the new list of names
	 * is identical, return true. Else delete the current selection and return
	 * false.
	 * 
	 * @param labels
	 *            vector to store the labels defined in the datafile
	 * @return true if the current selection can be maintained, false otherwise.
	 */
	public boolean loadColumnNames(Vector labels) {
		boolean res = false;
		Vector newItems = new Vector();

		// mark the labels eventually present and given by a dataset
		// with CHECK : if they are found in the new dataset, we rename
		// their type to 'DATA' which allows us to keep the settings
		// such as font, color etc.

		Vector newLabels = new Vector();
		for (Enumeration e = labels.elements(); e.hasMoreElements();) {
			GraphLabel g = (GraphLabel) e.nextElement();
			if (g.equals(GraphLabel.DATA) && g.getFile() == file) {
				g.setID(GraphLabel.CHECK);
			}
			newLabels.add(g);
		}

		/*
		 * // if data are loaded not via file, set show options if
		 * (!SetEnv.ReadFile) {
		 * 
		 * if (this.index<SetEnv.DATA.size()) { LinePars current =
		 * (LinePars)SetEnv.DATA.elementAt( this.index );
		 * styles.addElement(current); } }
		 */

		/*
		 * // if data are loaded not via file, set show options if
		 * (!SetEnv.ReadFile) { LinePars current = (LinePars)SetEnv.CURRENT;
		 * styles.addElement(current); }
		 */

		try {

			String mess = "# column 1: " + data.getNameX() + "\n# column 2: "
					+ data.getNameY();
			mess = mess + "\n dataset : " + data.getName();
			mess = mess + "\n xlabel : " + data.getNameX();
			mess = mess + "\n ylabel : " + data.getNameY();
			mess = mess + "\n";

			StringReader fakefile = new StringReader(mess);
			BufferedReader in = new BufferedReader(fakefile);
			if (JHPlot.ReadFile)
				in = new BufferedReader(new FileReader(file));

			String s;
			int k = 1;
			while ((s = in.readLine()) != null) {
				s = s.trim();
				if (s.length() == 0)
					continue;
				StringTokenizer st = new StringTokenizer(s, " \t,:");
				String token = st.nextToken();
				if (token.startsWith("#")) {
					if (!st.hasMoreTokens())
						continue;
					String nextToken = st.nextToken();
					if (st.hasMoreTokens()) {
						if (nextToken.startsWith("graph")) {
							token = st.nextToken();
							if (token.startsWith("piper"))
								graphType = gs.GRAPHTYPE_PIPER;
							else if (token.startsWith("multi")) {

								// it's a multidata plot. We'll disable the
								// legend by default for this type of graphs:
								// -------------------------------------------
								graphType = gs.GRAPHTYPE_MULTI;
								gs.setDrawLegend(false);
							} else
								graphType = gs.GRAPHTYPE_2D;
							continue;
						} else if (nextToken.equals("column")
								|| nextToken.equals("row")) {
							st.nextToken(); // eat column number, e.g. '2:'
							token = st.nextToken();
							while (st.hasMoreTokens())
								token += " " + st.nextToken();
							newItems.addElement(token);
							continue;
						}

						else if (nextToken.equals("dataset")) {
							st.nextToken(); // eat column number, e.g. '2:'
							token = getLabel(st);
							newItems.addElement(token);
							continue;
						}

						// get the x-label from the dataset, if any
						else if (nextToken.startsWith("xlabel")
								&& st.hasMoreTokens()) {
							GraphLabel gl = new GraphLabel(GraphLabel.XLABEL,
									getLabel(st));
							addLabel(gl, newLabels);
							continue;
						}

						// get the y-label from the dataset, if any
						else if (nextToken.startsWith("ylabel")
								&& st.hasMoreTokens()) {
							GraphLabel gl = new GraphLabel(GraphLabel.YLABEL,
									getLabel(st));
							gl.setRotation(Math.PI * 1.5);
							addLabel(gl, newLabels);
							continue;
						}

						// random labels:
						else if (nextToken.startsWith("label")
								&& st.hasMoreTokens()) {
							GraphLabel gl = new GraphLabel(GraphLabel.DATA,
									getLabel(st));
							gl.setFile(file);
							if (st.hasMoreTokens()) {
								double xx, yy;
								xx = Double.parseDouble(st.nextToken());
								if (st.hasMoreTokens()) {
									yy = Double.parseDouble(st.nextToken());
									gl.setDataLocation(xx, yy);
									if (st.hasMoreTokens()) {
										gl.setDataRotation(Math.PI
												* Double.parseDouble(st
														.nextToken()) / 180.0);
									}
								} else
									gl.setRotation(Math.PI * xx / 180);
							}
							addLabel(gl, newLabels);
							continue;
						} else
							continue;
					}
				} else if (newItems.size() == 0) {
					newItems.addElement("column " + Integer.toString(k++));
					while (st.hasMoreTokens()) {
						token = st.nextToken();
						if (token.startsWith("*"))
							break;
						newItems.addElement("column " + Integer.toString(k++));
					}
					break;
				}
			}
			in.close();
		} catch (Exception e) {
			Utils.oops(null, "Something's wrong with file '" + getName() + "'!");
			return false;
		}

		if (items.size() > 0) {
			if (items.size() == newItems.size()) {
				res = true;
				Enumeration e1 = items.elements();
				Enumeration e2 = newItems.elements();
				while (e1.hasMoreElements()) {
					if (!((String) e1.nextElement()).equals((String) e2
							.nextElement())) {
						res = false;
						break;
					}
				}
			} else
				res = false;
		}
		items = newItems;
		if (!res) {
			if (columns.size() > 0)
				columns.removeAllElements();
			if (styles.size() > 0)
				styles.removeAllElements();
		}

		// if labels read, delete all labels which were marked
		// 'CHECK' and not found in the current dataset:
		if (labels.size() > 0)
			labels.removeAllElements();
		if (newLabels.size() > 0) {
			for (Enumeration e = newLabels.elements(); e.hasMoreElements();) {
				GraphLabel g = (GraphLabel) e.nextElement();
				if (!g.equals(GraphLabel.CHECK))
					labels.add(g);
			}
			newLabels.removeAllElements();
			newLabels = null;
		}
		return res;
	}

	private String getLabel(StringTokenizer st) {
		String token = st.nextToken();
		if (token.startsWith("\"")) { // is a label between " and "
			while (!token.endsWith("\""))
				token += " " + st.nextToken();
			token = token.substring(1, token.length() - 1);
		}
		return token;
	}

	/*
	 * Add a label to the vector of labels. The rule is, for the moment: if the
	 * user reloads the datafile (and hence passes through this procedure), the
	 * label is declared. But if the label already exists (with, perhaps,
	 * another color, font or position), than we keep this label instead BUT we
	 * use the position as defined here in the data set.
	 * 
	 * @param gl new graph label.
	 * 
	 * @param labels vector containing the labels.
	 */
	private void addLabel(GraphLabel gl, Vector labels) {
		boolean labelFound = false;
		for (Enumeration e = labels.elements(); e.hasMoreElements();) {
			GraphLabel g = (GraphLabel) e.nextElement();
			if (gl.equals(g.getText()) && (g.equals(GraphLabel.CHECK))) {
				g.setUsePosition(false);
				g.setDataLocation(gl.getXPos(), gl.getYPos());
				g.setID(gl.getID());
				labelFound = true;
				break;
			}
		}
		if (!labelFound)
			labels.add(gl);
	}

	/**
	 * @return the number of items present in the item vector.
	 */
	public int getNumberOfItems() {
		return items.size();
	}

	/**
	 * @return the item corresponding to index i.
	 */
	public String getItem(int i) {
		// System.out.println((String)items.get(i));
		return (String) items.get(i);
	}

	/**
	 * Adds an item to the vector.
	 * 
	 * @param item
	 *            name of the item added.
	 */
	public void addItem(String item) {
		items.add(item);
	}

	/*
	 * Remove a column at a specified index:
	 */
	private void delCol(Integer index) {
		int k = 0;
		for (Enumeration e = columns.elements(); e.hasMoreElements(); k++) {
			Integer i = (Integer) e.nextElement();
			if (i.equals(index))
				break;
		}
		if (k < columns.size()) {
			columns.remove(k);
			styles.remove(k);
		}
	}

	/**
	 * Adds a column to the vector of columns. The column is added
	 * <strong>even</strong> if the column index is already selected (hence
	 * selecting several times the same column is allowed). This is useful if
	 * the user wants to plot the same data several times but in a different
	 * way. (histogram + lines, for example).
	 * 
	 * @param columnIndex
	 *            index of the column
	 * @param lp
	 *            parameters used to draw the line
	 */
	public void addColumn(int columnIndex, LinePars lp) {
		Integer index = new Integer(columnIndex);
		columns.add(index);
		styles.add(lp);
	}

	/**
	 * Sets the index of the column used for the X-axis.
	 * 
	 * @param columnIndex
	 *            index of the column
	 */
	public void setXColumn(int columnIndex) {
		xColumn = columnIndex;
	}

	/**
	 * Returns the index of the column used for the X-axis
	 * 
	 * @return index of the column
	 */
	public int getXColumn() {
		return xColumn;
	}

	/**
	 * Returns the index of the column used for the Y-axis.
	 * 
	 * @param i
	 *            index of the column item
	 * @return index of the column
	 */
	public int getYColumn(int i) {
		return ((Integer) columns.get(i)).intValue();
	}

	/**
	 * returns the linestyle of one of the vector items.
	 * 
	 * @param index
	 *            index of the column
	 */
	public LinePars getLinePars(int index) {
		if (styles.size() == 0)
			return new LinePars();
		if (index < styles.size())
			return (LinePars) styles.get(index);
		return (LinePars) styles.get(0);
	}

	/**
	 * sets the linestyle of one of the vector items.
	 * 
	 * @param index
	 *            index of the column
	 * @param lp
	 *            linepars instance (can't be null!)
	 */
	public void setLinePars(int index, LinePars lp) {
		styles.set(index, lp);
	}

	/**
	 * sets the linestyle of all the vector items.
	 * 
	 * @param lp
	 *            linepars instance
	 */
	public void setLinePars(LinePars lp) {
		if (styles.size() > 0) {
			styles.removeAllElements();
		}

		// System.out.println(columns.size());

		for (int k = 0; k < columns.size(); k++) {
			lp = new LinePars(lp);
			if (lp.slideColor)
				lp.nextColor(columns.size());
			styles.add(lp);
		}
	}

	public String getLegend(int index) {
		return getLinePars(index).getName();
	}

	/**
	 * remove a column from the vector of columns.
	 * 
	 * @param columnIndex
	 *            index of the column
	 */
	public void removeColumn(int columnIndex) {
		if (columnIndex < columns.size()) {
			columns.remove(columnIndex);
			styles.remove(columnIndex);
		}
	}

	/**
	 * Return the number of columns (selected items)
	 * 
	 * @return number of Y-columns
	 */
	public int getNumberOfColumns() {
		return columns.size();
	}

	/**
	 * Return the file
	 * 
	 * @return file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the modification date of the file in ms, I guess.
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * Sets the modification date of the file to a specific value.
	 * 
	 * @param date
	 *            modification date of the current file.
	 */
	public void setLastModified(long date) {
		lastModified = date;
	}

	/**
	 * Return the abbreviated filename. This name is different from the file
	 * itselves if it exceeds a a certain number of characters (about 45).
	 * Otherwise, the name is too long to fit in decent textboxes...
	 * 
	 * @return an abbreviated version of the filename, if the latter is too
	 *         long.
	 */
	public String getAbbreviatedFilename() {
		return abbreviatedFilename;
	}

	/**
	 * Return the filename with the preceeding path stripped off.
	 * 
	 * @return an abbreviated version of the filename, without an eventual path.
	 */
	public String getFilenameWithoutPath() {
		return filenameWithoutPath;
	}

	/**
	 * Return the filename as such.
	 * 
	 * @return the filename.
	 */
	public String getName() {

		String s = data.getName();
		if (JHPlot.ReadFile)
			s = file.toString();
		return s;
	}

	/**
	 * Checks whether the argument is a column selected for output.
	 * 
	 * @param index
	 *            index which is sought for.
	 */
	public int isSelected(int index) {
		int k = 0;
		for (Enumeration e = columns.elements(); e.hasMoreElements(); k++) {
			if (((Integer) e.nextElement()).intValue() == index) {
				// System.out.print("... " + index + " is found ...");
				return k;
			}
		}
		return -1;
	}

	/**
	 * Returns the name of one of the columns present here.
	 * 
	 * @return name of column with index i
	 */
	public String getColumnName(int i) {
		int index = ((Integer) columns.get(i)).intValue();
		return (String) items.get(index);
	}

	/**
	 * Sets the current datafile index to another value.
	 * 
	 * @param i
	 *            new index
	 */
	public void setIndex(int i) {
		index = i;
	}

	/**
	 * Get the current datafile index
	 * 
	 * @param i
	 *            new index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the graphstyle of the current dataset
	 */
	public int getGraphType() {
		return graphType;
	}

	/**
	 * Reads the current file and returns the data arrays.
	 * 
	 * @param dataArrays
	 *            vector with data-arrays of type DataArray
	 * @param type
	 *            graph type (2D, piper, multiplot...)
	 * @param fileIndex
	 *            index of the file which contains the data. This is simply a
	 *            number, 0 for the first datafile, 1 for the second, etc. etc.,
	 *            used internally to find back the file belonging to a data
	 *            array.
	 * @return false if some weird error might occur during parsing.
	 */
	public boolean fillDataArrays(Vector dataArrays, int type, int fileIndex) {
		int nCol = getNumberOfColumns();
		int nIts = getNumberOfItems();
		double x = 0.0;
		int nY = (type == GraphSettings.GRAPHTYPE_PIPER) ? nIts : nCol;
		double[] y = new double[nY];
		boolean newDataSet = true;
		boolean isFirstLabel = true;
		String s;

		// used by the multiplot graph style:
		int currentDataset = 0;

		// System.out.println("debug: read  SetEnv.DATA"+fileIndex+" "+nY);

		// fill from memory
		// chekanov

		/*
		 * // if data are loaded not via file, set show options if
		 * (!SetEnv.ReadFile) {
		 * 
		 * // dataArrays.removeAllElements();
		 * 
		 * // System.out.println(this.index+" "+SetEnv.DATA.size());
		 * 
		 * // if (fileIndex==SetEnv.DATA.size()-1) // { if (newDataSet) {
		 * dataArrays.removeAllElements();
		 * System.out.println(this.index+" "+SetEnv.DATA.size()); DataArray
		 * current = (DataArray)SetEnv.DATA.elementAt( this.index );
		 * current.setLinePars( getLinePars( this.index ) );
		 * dataArrays.add(current); currentDataset++; newDataSet=false; } //
		 * current.printLinePars(); // } return true; }
		 */

		/*
		 * if (!SetEnv.ReadFile) { boolean liftPen = false;
		 * 
		 * // System.out.println(fileIndex+" "+SetEnv.DATA.size()); if
		 * (newDataSet) { dataArrays.removeAllElements(); Enumeration
		 * e=SetEnv.DATA.elements(); currentDataset=0; while
		 * (e.hasMoreElements()) { DataArray a= (DataArray)e.nextElement(); //
		 * a.setLinePars( getLinePars( currentDataset ) ); // a.print();
		 * dataArrays.add(a); currentDataset++; } newDataSet=false; }
		 * 
		 * return true; } // and fill
		 */

		try {
			BufferedReader in = new BufferedReader(new FileReader(getFile()));

			// more than nY items, in case of the user selecting several
			// times the same item... FIXME: NullPointerException if he/she
			// selects more than 2*nY items :-(
			DataArray[] data = new DataArray[2 * nY];
			int col = 0;
			int k = 0;

			// read a line from the file. Ignore all comments and blank lines:
			while ((s = in.readLine()) != null) {
				s = s.trim();
				if (s.length() == 0)
					continue;
				else if (s.startsWith("#"))
					continue;
				StringTokenizer st = new StringTokenizer(s, " \t,");
				if (st.countTokens() < 2)
					continue;

				// if the current graph-type is a standard (X,Y1,Y2 etc) graph:
				if (type == GraphSettings.GRAPHTYPE_2D) {

					// start new dataset here
					if (newDataSet) {
						if (JPlot.debug)
							System.out.println("starting a new dataset with "
									+ nCol + " Y-columns...");

						for (int i = 0; i < nCol; i++) {
							data[i] = new DataArray(fileIndex, i, 101,
									getLinePars(i));
						}
						newDataSet = false;
					}
					boolean liftPen = false;
					for (int i = 0; st.hasMoreTokens(); i++) {
						String t = st.nextToken();
						if (t.startsWith("*"))
							liftPen = true;
						if (i == getXColumn())
							x = Double.parseDouble(t);
						for (k = 0; k < nCol; k++) {
							if (getYColumn(k) == i)
								y[k] = Double.parseDouble(t);
						}
					}
					for (int i = 0; i < nCol; i++)
						data[i].addPoint(x, y[i], liftPen);
				} else {

					// not a standard graph. Can be a piper diagram or some kind
					// of multiplot graph. The file format of these special
					// graphs
					// is kind of rigid, refer to the docs for info.
					if (type == GraphSettings.GRAPHTYPE_PIPER) {

						// it's a piper diagram. Very rigid format:
						data[0] = new DataArray(fileIndex, 1, 3,
								getLinePars(k++));
						if (st.countTokens() < 4) {
							Utils.oops(null,
									"Incorrect format of Piper diagram datafile '"
											+ getAbbreviatedFilename() + "'!");
							return false;
						} else {
							data[0].addPoint(
									Double.parseDouble(st.nextToken()),
									Double.parseDouble(st.nextToken()));
							data[0].addPoint(
									Double.parseDouble(st.nextToken()),
									Double.parseDouble(st.nextToken()));

							// if there's one more point, it must be the TDS:
							if (st.hasMoreTokens()) {
								data[0].addPoint(
										Double.parseDouble(st.nextToken()), 0.0);
							}
							dataArrays.add(data[0]);
						}
					} else {

						// It's a multiplot type of graph.
						// Note that the multiplot graph can use only two
						// columns (x,y),
						// all eventually following columns are silently ignored
						if (newDataSet) {
							data[0] = new DataArray(fileIndex, 1, 10,
									getLinePars(k));
							newDataSet = false;
						}
						data[0].addPoint(Double.parseDouble(st.nextToken()),
								Double.parseDouble(st.nextToken()));
						while (st.hasMoreTokens()) {
							if (st.nextToken().equals("**")) {
								for (int i = 0; i < nCol; i++) {
									if (getYColumn(i) == currentDataset) {
										k++;
										dataArrays.add(data[0]);
									}
								}
								currentDataset++;
								newDataSet = true;
								break;
							}
						}
					}
				}
			}
			if (type == GraphSettings.GRAPHTYPE_2D) {
				for (int i = 0; i < nCol; i++)
					dataArrays.add(data[i]);
			}
			in.close();
		} catch (Exception exception) {
			Utils.oops(null, "Something's wrong with file '"
					+ getAbbreviatedFilename()
					+ "'.\nCheck the file's format and its content.");
			return false;
		}
		return true;
	}

	/**
	 * Adds the settings of this panel to the XML settings toolbox.
	 * 
	 * @param xw
	 *            instance with settings
	 */
	public void getSettings(XMLWrite xw, String fileName) {

		// xw.add("name",file.toString());

		xw.add("name", fileName);
		xw.open("datafile");

		int N = columns.size();

		// add the column selection used for the x-axis:
		xw.add("index", String.valueOf(xColumn));
		xw.set("x-column");

		// write the number of columns:
		xw.add("number", String.valueOf(N));
		xw.set("y-columns");

		// add the column(s) used for the y-axis:
		for (int i = 0; i < N; i++) {
			xw.add("index", String.valueOf(columns.get(i)));
			xw.open("y" + (i + 1) + "-column");
			getLinePars(i).getSettings(xw);
			xw.close(); // column
		}
		xw.close(); // datafile

	}

	/**
	 * Updates the current settings with new data. The data settings must be
	 * wrapped in a XMLRead object.
	 * 
	 * @param xr
	 *            read object, containing all the data
	 */
	public void updateSettings(XMLRead xr) {
		if (JPlot.debug)
			System.out.println("- updating for new settings in DataFile...");

		// remove default column-selections and styles, since they will
		// be defined by the following settings:
		if (columns.size() > 0)
			columns.removeAllElements();
		if (styles.size() > 0)
			styles.removeAllElements();

		xColumn = xr.getInt("x-column/index", xColumn);
		int N = xr.getInt("y-columns/number", 0);
		for (int i = 0; i < N; i++) {
			if (xr.open("y" + (i + 1) + "-column")) {
				int col = xr.getInt("index", 1);
				LinePars lp = new LinePars();
				lp.updateSettings(xr);
				addColumn(col, lp);
				// System.out.println("column index = " + col + ", dataseries "
				// + columns.size());
				xr.close();
			}
		}
	}

}
