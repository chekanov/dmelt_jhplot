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

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

import javax.swing.border.*;

import jplot.GraphSettings;
import jplot.JPlot;

/**
 * LegendPanel creates a panel which allows a user to modify the
 * parameters used to set the legend, such as position, color,
 * font etc.
 * Calling the <code>show</code> method pops up a frame (dialog) which
 * includes the panel.
 */
public class PanelLegend extends PanelGridUI {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private final int X = GraphSettings.X_AXIS;
  private final int Y = GraphSettings.Y_AXIS;

  private JDialog dialog;
  //private JRadioButton rb_reset;
  //private JCheckBox rb_show, rb_hide;
  private PanelFonts fontPanel;
  private GraphSettings gs;

  private JRadioButton hide;
  private JCheckBox autoPos, manuPos;
  private JTextField xPosField, yPosField, rotField;
  private JTextField sepField;
  private JPlot jplot;

  /**
   * Principal constructor. Builds a panel with the options
   * and apply/cancel buttons.
   * @param jp instance of the main app, parent.
   * @param gs instance of the object containing all graphical parameters.
   */
  public PanelLegend (JPlot  jp, GraphSettings gs) {
    dialog = null;
    this.gs = gs;
    jplot = jp;

    /*
    ButtonGroup bg = new ButtonGroup();
    rb_show = new JCheckBox("show");
    bg.add(rb_show);
    rb_hide = new JCheckBox("hide");
    bg.add(rb_hide);
    rb_reset = new JRadioButton("reset");
    */
    
    Dimension mediumField = new Dimension(120,20);
    Dimension cbField = new Dimension(110,20);
    Dimension shortField = new Dimension(80,22);
    EmptyBorder border = new EmptyBorder(new Insets(0,0,0,10));
    setBorder(new EmptyBorder(new Insets(5,5,5,5)));

    // this panel contains the non-font specific stuff:
    PanelGridUI p = new PanelGridUI();
    p.setBorder(new EtchedBorder()); 

    // put location stuff:
    JLabel label = new JLabel("Position:");
    label.setBorder(border);
    p.addComponent(label,1,1);

    ButtonGroup bg = new ButtonGroup();
    autoPos = new JCheckBox("automatic",true);
    autoPos.setPreferredSize(cbField);
    autoPos.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {	
	xPosField.setEnabled(false);
	yPosField.setEnabled(false);
      }
    });
    bg.add(autoPos);
    p.addComponent(autoPos,1,2);

    manuPos = new JCheckBox("manual",false);
    manuPos.setPreferredSize(cbField);
    manuPos.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {	
	xPosField.setEnabled(true);
	yPosField.setEnabled(true);
      }
    });
    bg.add(manuPos);
    p.addComponent(manuPos,1,3);

    JPanel posPanel = new JPanel(new BorderLayout());
    posPanel.add(new JLabel("x: "),BorderLayout.WEST);
    xPosField = new JTextField();
    xPosField.setEnabled(true);
    posPanel.add(xPosField,BorderLayout.CENTER);
    posPanel.setPreferredSize(shortField);
    p.addComponent(posPanel,2,2);

    posPanel = new JPanel(new BorderLayout());
    posPanel.add(new JLabel("y: "),BorderLayout.WEST);
    yPosField = new JTextField();
    yPosField.setEnabled(true);
    posPanel.add(yPosField,BorderLayout.CENTER);
    posPanel.setPreferredSize(shortField);
    p.addComponent(posPanel,2,3);

    hide = new JRadioButton("hide",false);
    hide.setPreferredSize(cbField);
    hide.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {	
      }
    });
    p.addComponent(hide,3,2);

    label = new JLabel("Spacing:");
    label.setBorder(border);
    p.addComponent(label,4,1);
    sepField = new JTextField();
    sepField.setPreferredSize(shortField);
    p.addComponent(sepField,4,2);
    addComponent(p,1,1);
    fontPanel = new PanelFonts(jplot,gs.getLegendFont());
    addFilledComponent(fontPanel,2,1,3,1,GridBagConstraints.VERTICAL);
  }

  /**
   * Updates the panel with the current graphical parameters.
   */
  public void refresh() {
    hide.setSelected(!gs.drawLegend());
    fontPanel.refresh(gs.getLegendFont());
    xPosField.setText(Float.toString((float)gs.getLegendPosition(gs.X_AXIS)));
    yPosField.setText(Float.toString((float) gs
				.getLegendPosition(gs.Y_AXIS)));
    sepField.setText(Float.toString((float)gs.getLegendSpacing()));
    autoPos.setSelected(!gs.useLegendPosition());
    manuPos.setSelected(gs.useLegendPosition());
  }  

  public void setValues() {
    gs.setDrawLegend(!hide.isSelected());
    gs.setLegendFont(fontPanel.getSelectedFont());
    gs.setFontChanged(true);
    if (manuPos.isSelected()) {
      gs.setLegendPosition(Double.parseDouble(xPosField.getText()),
			   Double.parseDouble(yPosField.getText()));
    }
    gs.setUseLegendPosition(!autoPos.isSelected());
    gs.setLegendSpacing(Float.parseFloat(sepField.getText()));
  }

  /**
   * Return a modal JDialog.
   * @param parent frame which is the parent of this dialog.
   * @param x x-position of the upper left corner
   * @param y y-position of the upper left corner
   */
  public void show(Frame parent, int x, int y) {
    if (dialog == null) {
      dialog = new JDialog(parent,"Legend options",false);
      dialog.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  dialog.dispose();
	}
      });

      JPanel p = new JPanel(new FlowLayout());
      p.setBorder(BorderFactory.createEtchedBorder());
      JButton b = new JButton("Apply");
      b.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    setValues();
	    gs.setDataChanged(true);
	    jplot.updateGraphIfShowing();
	    dialog.dispose();
	  }
	});
      p.add(b);
      b = new JButton("Dismiss");
      b.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    dialog.dispose();
	  }
	});
      p.add(b);
      dialog.getContentPane().add(this,BorderLayout.CENTER);
      dialog.getContentPane().add(p,BorderLayout.SOUTH);
      dialog.pack();
      dialog.setLocation(x,y);
    }
    refresh();
    dialog.setVisible(true);  // blocks until user brings dialog down.
  }
}
