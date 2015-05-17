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

import sharptools.*;

import java.io.*;
import java.util.*;

import jhplot.gui.HelpBrowser;
import jhplot.io.csv.CSVReader;


import jminhep.cluster.DataHolder;

/**
 * 
 * A spreadsheet  to display multidimensional  data.
 * 
 * @author S.Chekanov
 * 
 */

public class SPsheet {

	private SharpTools Sharp;

	private Config config;

	private SharpTableModel model;

	
	
	/**
	 * Show empty spreadsheet
	 *
	 */
	
	public SPsheet() {

		showSPsheet();

	}

	
	/**
	 * Build a spreadsheet from 1D array
	 * 
	 * @param a
	 *            Input array
	 */

	public SPsheet(double[] a) {

		showSPsheet();
		VHolder vh = new VHolder();
		vh.setData(a);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}
	
	/**
	 * Build a spreadsheet from 2D array
	 * 
	 * @param a
	 *            Input array
	 */

	public SPsheet(double[][] a) {

		showSPsheet();
		VHolder vh = new VHolder();
		vh.setData(a);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}
	
	/**
	 * Build a spreadsheet from the P1D container
	 * 
	 * @param p1d
	 *            Input container
	 */

	public SPsheet(P1D p1d) {

		showSPsheet();
		VHolder vh = new VHolder(p1d);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a spreadsheet from the PND container
	 * 
	 * @param pnd
	 *            Input container
	 */

	public SPsheet(PND pnd) {

		showSPsheet();
		VHolder vh = new VHolder(pnd);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}


	/**
	 * Build a spreadsheet from the PND container
	 * 
	 * @param pni
	 *            Input container
	 */

	public SPsheet(PNI pnd) {

		showSPsheet();
		VHolder vh = new VHolder(pnd);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}

	
	
/**
 * Open spreadsheet and load csv file 
 *                   
 * @param reader CSVReader 
 *                 
 **/
        public SPsheet(CSVReader reader) throws IOException {
                showSPsheet();

                int aRow = model.getRowCount();
                int i=0;
                String [] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                  for (int j=0; j<nextLine.length; j++) { 
                          try{
                           double f = Double.valueOf(nextLine[j].trim()).doubleValue();
                           model.doSetValueAt(new Double(f), i, j + 1);
                          }
                            catch (Exception e){
                            model.doSetValueAt(nextLine[j], i, j + 1);
                           }
                       }

                model.addRow(); 
                i++;        
                }


        }


 /**
 *      Adds row to end of table
 **/
    public  void addRow() {
         model.addRow();
}	
	
	
	/**
	 * Build a spreadsheet from the H1D histogram
	 * 
	 * @param h1d
	 *            Input histogram
	 */

	public SPsheet(H1D h1d) {

		showSPsheet();
		VHolder vh = new VHolder(h1d);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a spreadsheet from the F1D function
	 * 
	 * @param f1d
	 *            Input function
	 */

	public SPsheet(F1D f1d) {

		showSPsheet();
		VHolder vh = new VHolder(f1d);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a spreadsheet from multidimensional data holder
	 * 
	 * @param dh
	 *            Input data
	 */

	public SPsheet(DataHolder dh) {

		showSPsheet();
		VHolder vh = new VHolder(dh);
		fillWithVectors(vh.getTitle(), vh.getNames(), vh.getData());

	}

	
	
	
	/**
	 * Show empty spreadsheet
	 * 
	 */
	private void showSPsheet() {

		String OS = System.getProperty("os.name").toLowerCase();
		String INIDIR = System.getProperty("user.home") + File.separator
				+ ".jehep";
		if (OS.indexOf("windows") > -1 || OS.indexOf("nt") > -1) {
			INIDIR = System.getProperty("user.home") + File.separator + "jehep";
		}

		String INIFILE = INIDIR + File.separator + "sharptools.ini";

		// read configuration file
		config = new Config(INIFILE);

		// set default value
		config.setInt("ROWS", 20);
		config.setInt("COLUMNS", 10);
		// config.set("AUTORESIZE", "TRUE");
		config.setInt("HISTOGRAMWIDTH", 600);
		config.setInt("HISTOGRAMHEIGHT", 400);

		// read file
		config.load();

		// only change it when DEBUG is uncommented in the config file
		if (config.get("DEBUG") != null)
			Debug.setDebug(config.getBoolean("DEBUG"));

		// initialize the function handler table object
		Formula.registerFunctions();

		Sharp = new SharpTools(config);
		model = Sharp.getTableModel();
		Sharp.setVisible(true);

		// if (args.length>0)
		// spreadsheet.openInitFile(args[0]);
	}

	/**
	 * This method sets the value of the cell specified with these coordinates
	 * to aValue. It does the parsing of string objects to see if they are
	 * numbers or formulas. If you do not want any parsing at all, use
	 * setCellAt.
	 * 
	 * @param aValue
	 *            value to set cell to
	 * @param aRow
	 *            row coordinate of cell
	 * @param aColumn
	 *            column coordinate of cell
	 */
	public void setValueAt(Object aValue, int aRow, int aColumn) {

		model.doSetValueAt(aValue, aRow, aColumn);

	}

	/**
	 * This fill assumes that the object passes to it is already the correct
	 * object to set the value of the cell as. For a formula, it also
	 * calculate the value of the formula and records that in the cell.
	 * 
	 * @param input
	 *            object to set the Cell value as
	 * @param aRow
	 *            row of cell to set
	 * @param aColumn
	 *            column of cell to set
	 */
	public void setCellAt(Object input, int aRow, int aColumn) {
		model.setCellAt(input, aRow, aColumn);
	}

	/**
	 * Fill the table from the Vectors
	 * 
	 * @param title
	 *            New title
	 * @param names
	 *            Vector with names
	 * @param arrayList
	 *            Vector with the data
	 */

	public void fillWithVectors(String title, String[] names, 
			Double[][] arrayList) {

		// get names
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			model.setValueAt(name, 0, i + 1);
		}

		// get data
		for (int i = 0; i < arrayList.length; i++) {
			for (int j = 0; j < arrayList[0].length; j++) {

                          String a=Double.toString(arrayList[i][j]);
                          try{
                          double f = Double.valueOf(a).doubleValue();
                          model.doSetValueAt(new Double(f), i, j + 1);
                          }
                           catch (Exception e){
                           model.doSetValueAt(a, i, j + 1);
                          }
                           
                             model.addRow();
			}

		}

	}

	/**
	 * This method returns the cell object at those coordinates. It does exactly
	 * the same thing as getCellAt except that the return type is Object. It is
	 * implemented because TableModel requires this method return an Object.
	 * 
	 * @param aRow
	 *            the row coordinate
	 * @param aColumn
	 *            the column coordinate
	 * @return the Cell
	 */
	public Object getValueAt(int aRow, int aColumn) {
		return model.getValueAt(aRow, aColumn);
	}

	
	/**
	 * Set value of a spreadsheet
	 * @param value value
	 * @param aRow row
	 * @param aColumn column
	 */
	public void setValueAt(double value, int aRow, int aColumn) {
		model.setValueAt(new Double(value), aRow, aColumn);
	}

	
	
	/**
	 * Set string value at a position
	 * @param value string
	 * @param aRow row
	 * @param aColumn column
	 */
	public void setStringAt(String value, int aRow, int aColumn) {
		model.setValueAt(new String(value), aRow, aColumn);
	}
	
	
	/**
	 * Get table model.
	 * @return
	 */
	public SharpTableModel getTableModel(){
		
		return model;
	}
	
	
	/**
	 * Add a row from a vector.
	 * @param a input array to be added.
	 */
	public void addRow(double[] a){
		
		List list = Arrays.asList(a);
		Vector vector = new Vector(list);  
		model.addRow(vector);
	}
	
	
	/**
	 * Inser a row at position
	 * @param a array of values
	 * @param row position
	 */
	public void insertRow(double[] a, int row){
		
		List list = Arrays.asList(a);
		Vector vector = new Vector(list);  
		model.insertRow(row, vector);
	}
	
	

	/**
	 * Add a column.
	 * @param a column to added.
	 */
	public void addColumn(double[] a){
		
		List list = Arrays.asList(a);
		Vector vector = new Vector(list);  
		model.addColumn(vector);
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
