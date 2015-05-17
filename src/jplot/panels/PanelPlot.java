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

package jplot.panels;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import jplot.DataArray;
import jplot.DataFile;
import jplot.GraphSettings;
import jplot.JPlot;
import jplot.LinePars;
import jplot.LineStyleButton;
import jplot.SimpleEditor;
import jplot.SmallButton;

/**
 * Builds the panel with item lists, to select columns, styles etc. This is the
 * panel where the graph data is selected (column selection).
 */

public class PanelPlot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[][] itemDataMatrix;
	private JTable itemTable;
	private TableModel itemDataModel;
	private JScrollPane items;

	private Object[][] selectedDataMatrix;
	private JTable selectedTable;
	private TableModel selectedDataModel;
	private boolean selectedInFocus;
	private JScrollPane selected;

	private Object[][] selectedXDataMatrix;
	private JTable selectedXTable;
	private TableModel selectedXDataModel;
	private boolean selectedXInFocus;

	private File file;
	private DataFile dataFile;
	private GraphSettings gs;
	private JPlot jplot;

	private SimpleEditor dataViewer;

	private JPopupMenu selectedXPopup, selectedYPopup, itemsPopup;
	private final String lf = System.getProperty("line.separator");

	/**
	 * Principal constructor. Builds the selector panel with plotting choices.
	 * 
	 * @param jp
	 *            JPlot instance
	 * @param df
	 *            the datafile object, contains the file for the plot
	 * @param gs
	 *            global graph settings
	 */
	public PanelPlot(JPlot jp, DataFile df, GraphSettings gs) {
		dataFile = df;
		jplot = jp;
		this.gs = gs;
		file = dataFile.getFile();
		JPanel p, q, r, main;
		JButton b;
		setLayout(new BorderLayout());
		EmptyBorder eb = new EmptyBorder(4, 4, 4, 4);
		BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
		setBorder(new CompoundBorder(eb, bb));
		p = new JPanel(new BorderLayout());
		p.setBorder(new EtchedBorder());
		JLabel fn = new JLabel(dataFile.getAbbreviatedFilename());
		fn.setForeground(new Color(0, 0, 100));
		p.add(fn, BorderLayout.WEST);

		JPanel buttons = new JPanel();
		Action action = new AbstractAction("Reload",
				jplot.getImageIcon("Refresh16.gif")) {
			public void actionPerformed(ActionEvent e) {
				reload();
			}
		};
		SmallButton sb = new SmallButton(action,
				"Reload this dataset from file");
		buttons.add(sb);
		action = new AbstractAction("Edit datafile",
				jplot.getImageIcon("Open16.gif")) {
			public void actionPerformed(ActionEvent e) {
				viewData();
			}
		};
		sb = new SmallButton(action, "View or edit the current dataset");
		buttons.add(sb);

		p.add(buttons, BorderLayout.EAST);
		add(p, BorderLayout.NORTH);
		selectedInFocus = selectedXInFocus = false;

		main = new JPanel(new GridLayout(1, 2));
		main.add(getItemPanel());
		main.add(getSelectedPanel());
		defaultSelection();
		add(main, BorderLayout.CENTER);
	}

	/*
	 * Returns a panel with a table of choices.
	 * 
	 * @return a panel with a table of choices.
	 */
	private JPanel getItemPanel() {

		// make a table with the choices:
		final String[] names = { "item" };

		// Create the data from the data-class:
		itemDataMatrix = new Object[0][1];

		// Create a model of the data.
		itemDataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return names.length;
			}

			public int getRowCount() {
				return itemDataMatrix.length;
			}

			public Object getValueAt(int row, int col) {
				return itemDataMatrix[row][col];
			}

			public String getColumnName(int column) {
				return names[column];
			}
		};

		// build a popup menu for the selected X table:
		itemsPopup = new JPopupMenu();
		JMenuItem mi;
		mi = (JMenuItem) itemsPopup.add(new JMenuItem("Set as X"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = itemTable.getSelectedRow();
				if (index > -1 && index < itemTable.getRowCount()) {
					if (selectedXDataMatrix.length == 0) {
						selectedXDataMatrix = new Object[1][1];
					}
					selectedXDataMatrix[0][0] = dataFile.getItem(index);
					dataFile.setXColumn(index);
					selectedXTable.revalidate();
					selectedXTable.repaint();
				}
			}
		});
		mi = (JMenuItem) itemsPopup.add(new JMenuItem("Set as Y"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] index = itemTable.getSelectedRows();
				for (int j = 0; j < index.length; j++) {
					if (index[j] >= 0)
						addSelectedItem(index[j]);
				}
				selectedTable.revalidate();
			}
		});

		// Create the table
		itemTable = new JTable(itemDataModel);
		itemTable.setColumnSelectionAllowed(false);
		itemTable.setSelectionBackground(new Color(248, 248, 215));
		itemTable.setSelectionForeground(Color.black);
		itemTable.getColumn("item").setPreferredWidth(30);
		itemTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()
						|| e.getModifiers() == InputEvent.BUTTON3_MASK) {
					itemsPopup.show(itemTable, e.getX(), e.getY());
				}
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = itemTable.getSelectedRow();
					addSelectedItem(index);
					selectedTable.revalidate();
				}
			}
		});

		// put the stuff in panels and stuff buttons below them to
		// selected items as X or Y columns:
		// --------------------------------------------------------
		items = new JScrollPane(itemTable);
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new TitledBorder(new EtchedBorder(), "Choices"));
		p.add(items, BorderLayout.CENTER);
		JPanel q = new JPanel(new FlowLayout());
		JButton b;
		if (gs.getGraphType() == gs.GRAPHTYPE_2D) {
			b = new JButton("Set as X");
			b.setToolTipText("Set the current selected column as X-axis");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = itemTable.getSelectedRow();
					addSelectedXItem(index);
					// if (selectedXDataMatrix.length == 0) {
					// selectedXDataMatrix = new Object[1][1];
					// }
					// selectedXDataMatrix[0][0] = dataFile.getItem(index);
					// dataFile.setXColumn(index);
					// selectedXTable.revalidate();
					// selectedXTable.repaint();
				}
			});
			q.add(b);
			b = new JButton("Set as Y");
		} else
			b = new JButton("Select");

		b.setToolTipText("Set the current selected column as Y-axis");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] index = itemTable.getSelectedRows();
				for (int j = 0; j < index.length; j++) {
					if (index[j] >= 0)
						addSelectedItem(index[j]);
				}
				selectedTable.revalidate();
			}
		});
		q.add(b);
		p.add(q, BorderLayout.SOUTH);
		return p;
	}

	private LinePars getDefaultLineStyle(int index) {
		LinePars lp = new LinePars(dataFile.getItem(index), gs.getColorIndex());
		if (gs.getGraphType() == gs.GRAPHTYPE_PIPER) {
			lp.setDrawLine(false);
			lp.setSymbol(gs.getPointIndex());
		} else if (gs.getGraphType() == gs.GRAPHTYPE_MULTI) {
			lp.setPenWidth(1.0f);
			lp.setColor(0);
		}
		return lp;
	}

	/**
	 * Adds an item to the table of selected items as X-selection.
	 * 
	 * @param index
	 *            index of the column used as X-asis.
	 */
	private void addSelectedXItem(int index) {
		if (selectedXDataMatrix.length == 0)
			selectedXDataMatrix = new Object[1][1];
		selectedXDataMatrix[0][0] = dataFile.getItem(index);
		dataFile.setXColumn(index);
		selectedXTable.revalidate();
		selectedXTable.repaint();
	}

	/**
	 * Adds a new item to the table of selected items.
	 * 
	 * @param item
	 *            item which will be added to the selection.
	 */
	private void addSelectedItem(int index) {
		int len = selectedDataModel.getRowCount();
		Object[][] newMatrix = new Object[len + 1][2];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < 2; j++)
				newMatrix[i][j] = selectedDataMatrix[i][j];
		}
		newMatrix[len][0] = dataFile.getItem(index);
		LinePars lp = getDefaultLineStyle(index);
		newMatrix[len][1] = new LineStyleButton(lp);
		selectedDataMatrix = null;
		selectedDataMatrix = newMatrix;
		dataFile.addColumn(index, lp);
		if (JPlot.debug) {
			System.out.println("column " + index + " ("
					+ dataFile.getItem(index) + ") added" + ", there are now "
					+ dataFile.getNumberOfColumns() + " columns");
		}

	}

	/**
	 * Removes an item from the table of selected items.
	 * 
	 * @param index
	 *            index of the item which will be removed
	 */
	private void removeSelectedItem(int index) {
		int len = selectedDataModel.getRowCount();
		if (len >= 1) {
			Object[][] newMatrix = new Object[len - 1][2];
			int k = 0;
			for (int i = 0; i < len; i++) {
				if (i != index) {
					newMatrix[k][0] = selectedDataMatrix[i][0];
					newMatrix[k][1] = selectedDataMatrix[i][1];
					k++;
				}
			}
			selectedDataMatrix = null; // hoping for some garbage collecting ...
			selectedDataMatrix = newMatrix;
		} else
			selectedDataMatrix = new Object[0][2];
		dataFile.removeColumn(index);
	}

	/**
	 * Returns a panel with a table of selected X and Y items.
	 * 
	 * @return a panel with a table of selected items.
	 */
	private JPanel getSelectedPanel() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new TitledBorder(new EtchedBorder(), "Selection"));
		p.add(getSelectedYPanel(), BorderLayout.CENTER);
		if (gs.getGraphType() == gs.GRAPHTYPE_2D) {
			p.add(getSelectedXPanel(), BorderLayout.NORTH);
		}
		return p;
	}

	/**
	 * Returns a panel with a table of selected X item.
	 * 
	 * @return a panel with a table of selected items.
	 */
	private JPanel getSelectedXPanel() {

		final String[] names = { "X-item" };

		selectedXDataMatrix = new Object[0][1];

		// Create a model of the data.
		selectedXDataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return names.length;
			}

			public int getRowCount() {
				return selectedXDataMatrix.length;
			}

			public Object getValueAt(int row, int col) {
				return selectedXDataMatrix[row][col];
			}

			public String getColumnName(int column) {
				return names[column];
			}

			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public void setValueAt(Object aValue, int row, int column) {
				selectedXDataMatrix[row][column] = aValue;
			}
		};

		// build a popup menu for the selected X table:
		selectedXPopup = new JPopupMenu();
		JMenuItem mi;
		mi = (JMenuItem) selectedXPopup.add(new JMenuItem("Options..."));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("TODO, X-column options must be added");
			}
		});
		mi = (JMenuItem) selectedXPopup.add(new JMenuItem("Remove"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedXDataMatrix = new Object[0][0];
				dataFile.setXColumn(-1); // -1 unsets the X column index
				selectedXTable.revalidate();
				selectedXInFocus = false;
			}
		});

		// Create the table
		selectedXTable = new JTable(selectedXDataModel);
		selectedXTable.setSelectionBackground(new Color(248, 248, 215));
		selectedXTable.setSelectionForeground(Color.black);
		selectedXTable.setColumnSelectionAllowed(false);
		selectedXTable.setRowSelectionAllowed(false);
		selectedXTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()
						|| e.getModifiers() == InputEvent.BUTTON3_MASK) {
					selectedXPopup.show(selectedXTable, e.getX(), e.getY());
				}
			}

			public void mouseClicked(MouseEvent e) {
				selectedInFocus = false;
				selectedXInFocus = true;
			}
		});
		Dimension d = new Dimension(220, selectedXTable.getRowHeight() + 1);
		selectedXTable.setPreferredScrollableViewportSize(d);
		JScrollPane selectedX = new JScrollPane(selectedXTable);
		JPanel p = new JPanel();
		p.add(selectedX);
		return p;
	}

	/**
	 * Fill the selected table with a default selection.
	 */
	private void defaultSelection() {
		if (JPlot.debug)
			System.out.print("setting default selection...");
		gs.resetColorIndex();
		gs.resetPointIndex();
		int N = dataFile.getNumberOfItems();
		itemDataMatrix = new Object[N][1];
		for (int i = 0; i < N; i++)
			itemDataMatrix[i][0] = dataFile.getItem(i);

		if (gs.getGraphType() == gs.GRAPHTYPE_2D) {
			if (N > 0) {
				selectedXDataMatrix = new Object[1][1];
				selectedXDataMatrix[0][0] = dataFile.getItem(0);
				dataFile.setXColumn(0);
			} else
				selectedXDataMatrix = new Object[0][1];
			selectedXTable.revalidate();
		}

		int k = (gs.getGraphType() != gs.GRAPHTYPE_2D) ? 0 : 1;
		if (N > k) {
			selectedDataMatrix = new Object[N - k][2];
			for (int i = k; i < N; i++) {
				selectedDataMatrix[i - k][0] = dataFile.getItem(i);
				LinePars lp = getDefaultLineStyle(i);
				selectedDataMatrix[i - k][1] = new LineStyleButton(lp);
				dataFile.addColumn(i, lp);
			}
		} else
			selectedDataMatrix = new Object[0][2];
		selectedTable.revalidate();
		itemTable.revalidate();
		repaint();
		if (JPlot.debug)
			System.out.println("OK");
	}

	/**
	 * Update the selected table with new data
	 */
	public void update() {
		if (JPlot.debug)
			System.out.print("updating in PlotPanel...");
		if (gs.getGraphType() == gs.GRAPHTYPE_2D) {
			if (dataFile.getNumberOfItems() > 0) {
				selectedXDataMatrix = new Object[1][1];
				if (dataFile.getXColumn() < dataFile.getNumberOfItems()) {
					selectedXDataMatrix[0][0] = dataFile.getItem(dataFile
							.getXColumn());
				} else
					selectedXDataMatrix[0][0] = dataFile.getItem(0);
			} else
				selectedXDataMatrix = new Object[0][1];
			selectedXTable.revalidate();
		}
		int k = dataFile.getNumberOfColumns();
		if (k > 0) {
			selectedDataMatrix = new Object[k][2];
			for (int i = 0; i < k; i++) {
				int m = dataFile.getYColumn(i);
				if (m >= dataFile.getNumberOfItems())
					break;
				selectedDataMatrix[i][0] = dataFile.getItem(m);
				selectedDataMatrix[i][1] = new LineStyleButton(
						dataFile.getLinePars(i));
			}
		} else
			selectedDataMatrix = new Object[0][2];
		selectedTable.revalidate();
		if (JPlot.debug)
			System.out.println("done");
		repaint();
	}

	void popupStyleChooser(int row) {
		LineStyleButton ls = (LineStyleButton) selectedDataModel.getValueAt(
				row, 1);
		LinePars lp = jplot.styleChooser.show(350, 100, ls.getLinePars());
		// System.out.println("my debug"); lp.printLinePars();
		if (lp != null) {
			// updateStyle(row,lp);
			// replace lp for this set chekanov
			DataFile a = getDataFile();
			jplot.replaceLinePars(a.getIndex(), lp);
			updateStyle(row, lp);
		}

	}

	/**
	 * Update the drawing style. The style can be changed via the StyleChooser
	 * popup within this plotpanel, but also by double-clicking on the data
	 * displayed by the graph.
	 * 
	 * @param row
	 *            row index, corresponds to the y-column index of the file.
	 * @param lp
	 *            new line parameters, as returned by the style chooser.
	 */
	public void updateStyle(int row, LinePars lp) {
		gs.setDataChanged(lp.dataChanged());
		dataFile.setLinePars(row, lp);
		LineStyleButton ls = (LineStyleButton) selectedDataModel.getValueAt(
				row, 1);
		ls.setLinePars(lp);
		selectedTable.repaint();
		if (jplot.plotFrame != null)
			jplot.showGraph(true);
	}

	/**
	 * Returns a panel with a table of selected Y items.
	 * 
	 * @return a panel with a table of selected Y items.
	 */
	private JPanel getSelectedYPanel() {

		// initialize a StyleChooser instance, we'll need it later:
		selectedDataMatrix = new Object[0][2];
		selectedDataModel = new AbstractTableModel() {
			final String[] names = { "Y-items", "style" };

			public int getColumnCount() {
				return names.length;
			}

			public int getRowCount() {
				return selectedDataMatrix.length;
			}

			public Object getValueAt(int row, int col) {
				return selectedDataMatrix[row][col];
			}

			public String getColumnName(int col) {
				return names[col];
			}

			public Class getColumnClass(int col) {
				return getValueAt(0, col).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				if (col == 0)
					return false;
				return true;
			}

			public void setValueAt(Object aValue, int row, int col) {
				selectedDataMatrix[row][col] = aValue;
				fireTableCellUpdated(row, col);
			}
		};

		// build a popup menu for the table:
		selectedYPopup = new JPopupMenu();
		JMenuItem mi;
		mi = (JMenuItem) selectedYPopup.add(new JMenuItem("Plot style..."));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = selectedTable.getSelectedRow();
				if (row < 0 || row >= selectedTable.getRowCount())
					return;
				// System.out.println("debug: open StyleChooser at row "+row);
				popupStyleChooser(row);
			}
		});
		mi = (JMenuItem) selectedYPopup.add(new JMenuItem("Remove"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedInFocus) {
					int[] index = selectedTable.getSelectedRows();
					for (int j = 0; j < index.length; j++)
						removeSelectedItem(index[0]);
					selectedTable.revalidate();
					selectedInFocus = false;
				}
			}
		});

		// Create the table
		selectedTable = new JTable(selectedDataModel);
		selectedTable.setColumnSelectionAllowed(false);
		selectedTable.setSelectionBackground(new Color(248, 248, 215));
		selectedTable.setSelectionForeground(Color.black);
		selectedTable.setCellSelectionEnabled(true);

		selectedTable.getColumn("style").setPreferredWidth(
				(int) selectedTable.getColumn("style").getPreferredWidth() / 3);
		selectedTable.getColumn("Y-items").setPreferredWidth(
				selectedTable.getColumn("Y-items").getPreferredWidth() * 2);
		selectedTable.setDefaultRenderer(LineStyleButton.class,
				new LineStyleButtonRenderer(new LinePars()));
		selectedTable.getColumn("style").setCellEditor(
				new ButtonEditor(new JCheckBox()));

		// implement some mouselistening stuff. Wouldn't it be nice if
		// we could listen to individual cells...
		selectedTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()
						|| e.getModifiers() == InputEvent.BUTTON3_MASK) {
					selectedYPopup.show(selectedTable, e.getX(), e.getY());
				}
				selectedInFocus = true;
				selectedXInFocus = false;
			}

			// public void mouseClicked(MouseEvent e) {
			// selectedInFocus = true;
			// selectedXInFocus = false;
			// if (selectedTable.getSelectedColumn() == 1) {
			// if (e.getClickCount() == 1) {
			// int row = selectedTable.getSelectedRow();
			// if (row < 0 || row >= selectedTable.getRowCount()) return;
			// //popupStyleChooser(row);
			// selectedTable.repaint();
			// }
			// }
			// }
		});

		selectedTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (selectedInFocus && e.getKeyCode() == KeyEvent.VK_DELETE) {
					int[] index = selectedTable.getSelectedRows();
					for (int j = 0; j < index.length; j++)
						removeSelectedItem(index[0]);
					selectedTable.revalidate();
					selectedInFocus = false;
				}
			}
		});

		selected = new JScrollPane(selectedTable);
		JPanel p = new JPanel(new BorderLayout());
		p.add(selected, BorderLayout.CENTER);
		JPanel q = new JPanel(new FlowLayout());
		ImageIcon icon = jplot.getImageIcon("Remove16.gif");
		JButton b = new JButton("Remove", icon);
		b.setToolTipText("Remove the selected item(s)");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedInFocus) {
					int[] index = selectedTable.getSelectedRows();
					for (int j = 0; j < index.length; j++)
						removeSelectedItem(index[0]);
					selectedTable.revalidate();
					selectedInFocus = false;
				} else if (selectedXInFocus) {
					selectedXDataMatrix = new Object[0][0];
					dataFile.setXColumn(-1); // -1 unsets the X column index
					selectedXTable.revalidate();
					selectedXInFocus = false;
				}
			}
		});
		q.add(b);
		icon = jplot.getImageIcon("New16.gif");
		b = new JButton("Clear", icon);
		b.setToolTipText("Clear the list with selected items");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearSelection();
			}
		});
		q.add(b);
		p.add(q, BorderLayout.SOUTH);
		return p;
	}

	/**
	 * Clear all the selected stuff.
	 */
	public void clearSelection() {
		int n = selectedDataModel.getRowCount();
		for (int j = 0; j < n; j++)
			removeSelectedItem(0);
		selectedTable.revalidate();
		if (gs.getGraphType() == gs.GRAPHTYPE_2D) {
			selectedXDataMatrix = new Object[0][0];
			dataFile.setXColumn(-1); // -1 unsets the X column index
			selectedXTable.revalidate();
		}
		gs.resetColorIndex();
		gs.resetPointIndex();
	}

	/**
	 * Reloads the datafile and resets the selection to default.
	 */
	public void reload() {
		if (JPlot.debug)
			System.out.println("reloading " + file.toString());
		if (!dataFile.loadColumnNames(gs.getLabels()))
			defaultSelection();
		jplot.updateGraphIfShowing();
	}

	/**
	 * Reloads the datafile and resets the selection to default.
	 */
	public void viewData() {

		DataArray da = jplot.getDataArraySelected();

		if (dataViewer == null) {
			// System.out.println("public void viewData()");
			dataViewer = new SimpleEditor(jplot, da.getTitle(), true);

			// dataViewer = new SimpleEditor(jplot,file.toString(),true);
		}
		dataViewer.show(100, 20, da.toString());
	}

	/**
	 * Sets the index of the current datafile.
	 * 
	 * @param index
	 *            index of the current datafile.
	 */
	public void setDataFileIndex(int index) {
		dataFile.setIndex(index);
	}

	/**
	 * @return the current datafile used by this panel
	 */
	public DataFile getDataFile() {
		return dataFile;
	}

	/**
	 * Renders the linestyle in a button. Shows the linestyle and pressing the
	 * button allows to change the style.
	 */
	public class LineStyleButtonRenderer extends LineStyleButton implements
			TableCellRenderer {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		public LineStyleButtonRenderer(LinePars lp) {
			super(lp);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setLinePars(((LineStyleButton) value).getLinePars());
			return this;
		}
	}

	/**
   * 
   */
	public class ButtonEditor extends DefaultCellEditor {
		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;
		protected LineStyleButton b;
		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			b = new LineStyleButton();
			// b.setBackground(Color.white);
			b.setOpaque(true);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			b.setLinePars(((LineStyleButton) value).getLinePars());
			isPushed = true;
			return b;
		}

		public Object getCellEditorValue() {
			int row = -1;
			if (isPushed) {
				isPushed = false;
				row = selectedTable.getSelectedRow();
				if (row >= 0 && row < selectedTable.getRowCount()) {
					// System.out.println("debug: open StyleChooser at row "+row);
					popupStyleChooser(row);
					selectedTable.repaint();
					return selectedDataModel.getValueAt(row, 1);
				}
			}
			return null;
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}
}
