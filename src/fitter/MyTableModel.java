// * This code is licensed under:
// * jeHEP License, Version 1.0
// * - for license details see http://jehep.sourceforge.net/license.html 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package fitter;

import java.awt.*;
import javax.swing.*;

import javax.swing.*;
import javax.swing.table.*;


class MyTableModel extends  DefaultTableModel  {
 


    private boolean DEBUG=false; 


   private String[] columnNames = { "No", "par", "min", "max",
        "center", "fixed" };

    private Object[][] data = {
        { new Integer(1), new String("par1"),  new Double(5) , new Double(5), new Double(0),
            new Boolean(false) },
        {  new Integer(1), new String("par1"), new Double(5) , new Double(5), new Double(0),
            new Boolean(false) } 
     };


/*

    public int getColumnCount() {
      return columnNames.length;
    }


    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

*/


/*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column
     * would contain text ("true"/"false"), rather than a check box.
     */
    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }


   /*
     * Don't need to implement this method unless your table's editable.
     */
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.

      if (col == 0) {
        return false;
      } else {
        return true;
      }
    }

    /*
     * Don't need to implement this method unless your table's data can
     * change.
     */
 
/*
   public void setValueAt(Object value, int row, int col) {
      if (DEBUG) {
        System.out.println("Setting value at " + row + "," + col
            + " to " + value + " (an instance of "
            + value.getClass() + ")");
      }

      data[row][col] = value;
      fireTableCellUpdated(row, col);

      if (DEBUG) {
        System.out.println("New value of data:");
        printDebugData();
      }
    }
*/


      private void printDebugData() {
      int numRows = getRowCount();
      int numCols = getColumnCount();

      for (int i = 0; i < numRows; i++) {
        System.out.print("    row " + i + ":");
        for (int j = 0; j < numCols; j++) {
          System.out.print("  " + data[i][j]);
        }
        System.out.println();
      }
      System.out.println("--------------------------");
    }
  }

