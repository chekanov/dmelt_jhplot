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
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import jhplot.gui.HelpBrowser;
import jminhep.cluster.DataHolder;


/**
 * 
 * Build a table in a frame to display data in various containers (P1D, H1D,
 * F1D). One can sort and filter data in this table, but not modify the data For
 * more advance manipulations, use SPsheet class
 * 
 * @author S.Chekanov
 * 
 */

public class HTable extends JFrame {

	private static final long serialVersionUID = 1L;

	private JButton closeButton;

	private JButton filter;

	private JPanel control;



	private DefaultTableModel model;
	private JTable jTable;
	private TableRowSorter<TableModel> rowSorter;
	private JTextField jtfFilter;
	private JButton jbtFilter;

	/**
	 * Main class to build a table from Vectors.
	 * 
	 * @param explanation
	 *            Title of the table or some explanation
	 * 
	 * @param colNames
	 *            Vector with column names
	 * 
	 * @param fillnames
	 *            Vector with some data: First you should fill a vector with the
	 *            row (should have the same size as colNames). Then add this row
	 *            as an element to the global vector
	 * 
	 */

	public HTable(String explanation,  String[] colNames,
	              Double[][] fillnames) {

		// build table
		buildTable(explanation, colNames, fillnames);

	}

	/**
	 * Build a table frame to display data. Default constructor.
	 * 
	 * 
	 */
	public HTable() {

	}




	/**
	 * Show 1D array
	 * @param a input array
	 */
	public HTable(double[] a) {

		VHolder vh = new VHolder();
		vh.setData(a);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}



	/**
	 * Show 2D array
	 * @param a input array
	 */
	public HTable(double[][] a) {

		VHolder vh = new VHolder();
		vh.setData(a);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}





	/**
	 * Build a table frame to display data of H1D histogram Data will be
	 * formated to scientific format
	 * 
	 * @param histogram
	 *            Input H1D histogram
	 * 
	 */
	public HTable(H1D histogram) {

		VHolder vh = new VHolder(histogram);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * 
	 * Show a H1D histogram.
	 * 
	 * @param histogram
	 *            Input H1D histogram
	 * 
	 */
	public void show(H1D histogram) {
		VHolder vh = new VHolder(histogram);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());
	}

	/**
	 * Show a function
	 * 
	 * @param function
	 *            Input function
	 * 
	 */
	public void show(F1D function) {

		VHolder vh = new VHolder(function);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display P0D data container Data will be formated
	 * to scientific format
	 * 
	 * @param data
	 *            Input data
	 */

	public HTable(P0I data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * show P0D data
	 * 
	 * @param data
	 *            Input data
	 */

	public void show(P0D data) {
		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display PND data container Data will be formated
	 * to scientific format
	 * 
	 * @param data
	 *            Input data
	 */

	public HTable(PND data) {
		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display PND data container Data will be formated
	 * to scientific format
	 * 
	 * @param data
	 *            Input data
	 */

	public HTable(PNI data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Show P1D data
	 * 
	 * @param data
	 *            Input data
	 */

	public void show(P1D data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display P2D data container Data will be formated
	 * to scientific format
	 * 
	 * @param data
	 *            Input data
	 */

	public HTable(P2D data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * show P2D data
	 * 
	 * @param data
	 *            Input data
	 */

	public void show(P2D data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display P3D data container Data will be formated
	 * to scientific format
	 * 
	 * @param data
	 *            Input data
	 */

	public HTable(P3D data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * show P3D data
	 * 
	 * @param data
	 *            Input data
	 */

	public void show(P3D data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	
	
	/**
	 * Build a table frame to display data of F1D function.
	 * 
	 * 
	 * @param function
	 *            Input function
	 */
	public HTable(F1D function) {

		VHolder vh = new VHolder(function);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display multidimensional data
	 * 
	 * @param dh
	 *            Input data holder
	 * 
	 */
	public HTable(DataHolder dh) {

		VHolder vh = new VHolder(dh);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display P1D data container
	 * 
	 * @param data
	 *            Input data
	 */

	public HTable(P1D data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}

	/**
	 * Build a table frame to display P0D data container
	 * 
	 * @param data
	 *            Input data
	 * 
	 */

	public HTable(P0D data) {

		VHolder vh = new VHolder(data);
		buildTable(vh.getTitle(), vh.getNames(), vh.getData());

	}


	/**
	 * Main class to build the table.
	 * 
	 * @param explanation
	 *            Title of the table or some explanation
	 * 
	 * @param colNames
	 *            Vector with column names
	 * 
	 * @param fillnames
	 *            Vector with some data: First you should fill a vector with the
	 *            row (should have the same size as colNames). Then add this row
	 *            as an element to the global vector [1] - row, [2] - column
	 * 
	 */

	private void buildTable(String explanation, String[] colNames,
	                        Double[][] fillnames) {


		setTitle(explanation);
		model = new DefaultTableModel(fillnames, colNames);
		jTable = new JTable(model);
		rowSorter=new TableRowSorter<>(jTable.getModel());

		jtfFilter = new JTextField();
		jbtFilter = new JButton("Filter");

		jTable.setRowSorter(rowSorter);


		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Word to match:"),
		          BorderLayout.WEST);
		panel.add(jtfFilter, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(panel, BorderLayout.SOUTH);
		add(new JScrollPane(jTable), BorderLayout.CENTER);

		jtfFilter.getDocument().addDocumentListener(new DocumentListener(){

			                @Override
			                public void insertUpdate(DocumentEvent e) {
				                String text = jtfFilter.getText();

				                if (text.trim().length() == 0) {
					                rowSorter.setRowFilter(null);
				                } else {
					                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				                }
			                }

			                @Override
			                public void removeUpdate(DocumentEvent e) {
				                String text = jtfFilter.getText();

				                if (text.trim().length() == 0) {
					                rowSorter.setRowFilter(null);
				                } else {
					                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				                }
			                }

			                @Override
			                public void changedUpdate(DocumentEvent e) {
				                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			                }

		                });


		//               add(panel);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(explanation);
		setSize(400, 500);
		setLocation(200, 200);
		setVisible(true);


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
