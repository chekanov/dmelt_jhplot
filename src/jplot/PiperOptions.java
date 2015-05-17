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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import jplot.panels.*;

/**
 * Here we create a panel which allows a user to modify several
 * parameters specific to piper diagrams.
 * Calling the <code>show</code> method pops up a frame (dialog) which
 * includes the panel.
 */
public class PiperOptions extends JPanel {
	private static final long serialVersionUID = 1L;

  private JDialog dialog;

  private JCheckBox cb_showTds, cb_hideTds;
  private JTextField scalingField;
  private GraphSettings gs;
  private JPlot jplot;
  
  /**
   * Principal constructor. Builds a panel with Piper diagram options.
   * @param jp instance of the main app, parent.
   * @param gs instance of the object containing all graphical parameters
   */
  public PiperOptions (JPlot jp, GraphSettings gs) {
    this.gs = gs;
    jplot = jp;
    dialog = null;
    //setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

    PanelGridUI p = new PanelGridUI();
    p.setBorder(new EmptyBorder(new Insets(5,5,5,5)));

    Dimension cbField = new Dimension(110,20);
    //Dimension shortField = new Dimension(80,22);

    ButtonGroup bg = new ButtonGroup();
    cb_showTds = new JCheckBox("Show");
    bg.add(cb_showTds);
    cb_hideTds = new JCheckBox("Hide");
    bg.add(cb_hideTds);
    scalingField = new JTextField();
    scalingField.setColumns(6);
    //scalingField.setPreferredSize(shortField);
    JPanel scalingPanel = new JPanel(new BorderLayout());
    scalingPanel.add(Utils.makeLabel("scaling"),BorderLayout.WEST);
    scalingPanel.add(scalingField,BorderLayout.CENTER);

    // spacing between labels and fields:
    EmptyBorder border = new EmptyBorder(new Insets(0,0,0,10));

    // place the widgets in the panel:
    JLabel label = new JLabel("Total Dissolved Solids:");
    label.setBorder(border);
    p.addComponent(label,1,1);
    p.addComponent(cb_showTds,1,2);
    p.addComponent(cb_hideTds,1,3);
    p.addComponent(scalingPanel,2,2);
    add(p);
  }

  /**
   * Updates the panel with the current graphical parameters.
   */
  public void refresh() {
    cb_showTds.setSelected(gs.drawTds());
    cb_hideTds.setSelected(!gs.drawTds());
    scalingField.setText(Float.toString(gs.getTdsFactor()));
  }

  public void setValues() {
    gs.setDrawTds(cb_showTds.isSelected());
    gs.setTdsFactor(Float.parseFloat(scalingField.getText()));
  }

 /**
   * Return a modal JDialog.
   * @param parent frame which is the parent of this dialog.
   * @param x x-position of the upper left corner
   * @param y y-position of the upper left corner
   */
  public void show(Frame parent, int x, int y) {
    if (dialog == null) {
      dialog = new JDialog(parent,"Piper diagram options",true);
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
      dialog.setLocation(x,y);
      dialog.pack();
    }
    refresh();
    dialog.setVisible(true);  // blocks until user brings dialog down.
  }
}









