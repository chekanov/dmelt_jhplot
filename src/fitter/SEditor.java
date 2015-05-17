// * This code is licensed under:
// * jeHEP License, Version 1.0
// * - for license details see http://jehep.sourceforge.net/license.html 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package fitter;

import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.MouseEvent;
import jhplot.utils.*;


public class SEditor extends JDialog { 
    /**
	 * 
	 */

    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;
    private JButton plotButton;
    private JButton closeButton;
    private JButton saveButton;
    private JFrame win;
    private JTable m_table;
    private  MyTableModel model;
    private  java.util.ArrayList<BMark> setting; 
    private final Fitter fit;

 protected String[] columnToolTips = {"parameter ID",
                                         "name",
                                         "min value",
                                         "max value",
                                         "initial value","fixed or not","parameterID if constrainted" };
    
    public SEditor(JFrame win, final Fitter fit) {
    this.win=win;
    this.fit=fit;
 
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

   setTitle("");
   setModal( true );
   setResizable( true );


   // make sure the dialog is not too big
   Dimension size = new Dimension(300, 250);


  JPanel topPanel = new JPanel();
  JPanel lowerPanel = new JPanel();
  lowerPanel.setPreferredSize(new Dimension(300, 20));


  closeButton = new JButton();
  closeButton.setText("Exit");
  closeButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             setVisible(false); dispose();
        }  });



  plotButton = new JButton();
  plotButton.setText("Plot");
  plotButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
         fit.drawFunc();
   }  });


  saveButton = new JButton();
  saveButton.setText("Save");
  saveButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             setting=getSet();  

   }  });
 


   
  lowerPanel.add(saveButton, null);
  lowerPanel.add(plotButton, null);
  lowerPanel.add(closeButton, null);
  topPanel.setLayout( new BorderLayout() );
  getContentPane().add( topPanel, java.awt.BorderLayout.CENTER );
  getContentPane().add( lowerPanel, java.awt.BorderLayout.SOUTH );
  topPanel.setLayout(  new BorderLayout()  );

  lowerPanel.setPreferredSize(new Dimension(80, 40));
 

    model = new MyTableModel();    
    m_table = new JTable(model)
     {
            public static final long serialVersionUID = 126;
            public Component prepareRenderer(TableCellRenderer renderer,
                                              int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);

            Color colcell = new  Color(255, 250, 240);
            Color colsell = new  Color(230, 230, 255);
            Color colselcel = new  Color(255, 244, 130);

            if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
                c.setBackground(colcell);
            } else {
                // If not shaded, match the table's background
                c.setBackground(getBackground());
            }

            if( m_table.isRowSelected( rowIndex ) ){
                    c.setBackground(colselcel);
              }



            return c;
        }


  //Implement table header tool tips.
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = 
                        columnModel.getColumn(index).getModelIndex();
                return columnToolTips[realIndex];
            }
        };
    }


        }; // end table 



       m_table.setRowHeight(24);
       model.addColumn("No");
       model.addColumn("Par");
       model.addColumn("min");
       model.addColumn("max");
       model.addColumn("center");
       model.addColumn("fix");
       model.addColumn("setTo");

        TableColumn column0 = null;
        column0 = m_table.getColumnModel().getColumn(0);
        column0.setPreferredWidth(20);
        column0.setMinWidth(10);
        column0.setMaxWidth(50);

        column0 = m_table.getColumnModel().getColumn(5);
        column0.setPreferredWidth(30);
        column0.setMinWidth(20);
        column0.setMaxWidth(50);

        column0 = m_table.getColumnModel().getColumn(6);
        column0.setPreferredWidth(40);
        column0.setMinWidth(20);
        column0.setMaxWidth(40);



    JScrollPane scrollPane = new JScrollPane(m_table);
    topPanel.add(scrollPane, BorderLayout.CENTER);

     setSize(size);

 }


// add raw
public void addRaw( String par, double  min, double max, double c, boolean fix, int cons   ) {

   int nn=model.getRowCount()+1;
   model.addRow( new Object[]{new Integer(nn), new String(par),new Double(min),new Double(max),
                              new Double(c),new Boolean(fix),new Integer(cons) }  );
   m_table.repaint();
}


public void putSettings(java.util.ArrayList<BMark> setting) {
      this.setting=setting;
}



public void fillTable() {

      removeAll();
      for (int i=0; i<setting.size(); i++) {
       BMark b=setting.get(i);
       addRaw(b.getTitle(),b.getMin(), b.getMax(), b.getC(), b.getFix(), b.getCons() );
       }

  }

// get all 
private java.util.ArrayList<BMark> getSet() {

         
           java.util.ArrayList bSet = new java.util.ArrayList<BMark>();
 
            int hmax=m_table.getRowCount();
            for (int i=0;i<hmax;i++) {
            Object tmp1=m_table.getValueAt(i, 1);
            Object tmp2=m_table.getValueAt(i, 2);
            Object tmp3=m_table.getValueAt(i, 3);
            Object tmp4=m_table.getValueAt(i, 4);
            Object tmp5=m_table.getValueAt(i, 5);
            Object tmp6=m_table.getValueAt(i, 6);

            if (tmp1 != null &&
                tmp2 != null &&
                tmp3 != null &&
                tmp4 != null  ) {

                bSet.add( new BMark(tmp1.toString(),
                                   (Double)tmp2,
                                   (Double)tmp3,
                                   (Double)tmp4,
                                   (Boolean)tmp5, 
                                   (Integer)tmp6 ) );


               }

              }

             return bSet;
}


// return current settings
public java.util.ArrayList<BMark> getSettings() {
   return setting;
}


// add raw
public void showIt() {

    Util.rightWithin( win, this );
//    pack();
    this.setVisible(true);

}




/**
     * Removes all items from the table.
     */
    public void removeAll()
    {
        while(model.getRowCount() > 0)
            model.removeRow(0);
    }




 //------------ auxilary function- get double from strings
       public  double getDouble(String s) {
        double dd=0.0;
        try { dd=Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) {
        }  return dd;  }

//------------ auxilary function- get double from strings
       public boolean getBool(String s) {
          if (s.equals("true")) return true;
          else 
              return false;
        }





}
