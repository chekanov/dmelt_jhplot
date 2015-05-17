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
import javax.swing.event.*;

import jplot.GPoints;
import jplot.GraphSettings;
import jplot.LinePars;
import jplot.MyUtils;
import jplot.SmallScrollPane;

/**
 * Builds a point-types panel from a number of predefined points,
 * all drawn by the Graph class.
 */
public class PanelPoint extends JPanel {
 
  private static final long serialVersionUID = 1L;
  float actualSize;
  SmallScrollPane ssp;
  JTextField tf_freq;
  String[] ten = {"30","24","20","18","!6","14","13","12","11","10","9","8","7","6","5","4","3","2","1"};
  LinePars lp;
  PointSwatches swatches;
  boolean actionNeeded;

  /**
   * Constructor.
   */
  public PanelPoint(LinePars l) {
    lp = l;
    actionNeeded = false;
    setLayout(new BorderLayout());
    setBorder(new TitledBorder(new EtchedBorder(),"Points"));
    JPanel p = new JPanel();
    swatches = new PointSwatches();
    p.add(swatches);
    add(p,BorderLayout.CENTER);

    // add a widget to choose the symbol size:
    PanelGridUI south = new PanelGridUI();
    actualSize = lp.getSymbolSize();
    if (actualSize == 0.0f) actualSize = 5.0f;
    ssp = new SmallScrollPane(ten,true);
    ssp.getViewport().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	if (actionNeeded) {
	  actualSize = Float.parseFloat(ten[ssp.getSelectedIndex()]);
	  if (lp.getSymbol() <= GraphSettings.NO_SYMBOL) {
	    lp.setSymbolSize(actualSize);
	  }
	}
      }
    });

    // add a widget to choose the 'every' option:
    tf_freq = new JTextField(Integer.toString(lp.getPointFrequency()));
    tf_freq.addCaretListener(new CaretListener() {
      public void caretUpdate(CaretEvent e) {
	if (!tf_freq.getText().equals("")) {
	  int k = Integer.parseInt(tf_freq.getText());
	  if (k > 0) lp.setPointFrequency(k);
	}
      }
    });
    tf_freq.setColumns(3);

    // place the widgets
    south.addComponent(MyUtils.getLabel("frequency"),1,1);
    south.addComponent(tf_freq,1,2);
    south.addComponent(MyUtils.getLabel("point size"),2,1);
    south.addComponent(ssp,2,2);
    add(south,BorderLayout.SOUTH);
  }

  /**
   * Refreshes the panel with the current data.
   * @param l line parameter class
   */
  public void refresh(LinePars l) {
    lp = l;
    actionNeeded = true;
    actualSize = lp.getSymbolSize();
    tf_freq.setText(Integer.toString(lp.getPointFrequency()));
    ssp.setSelected(Integer.toString((int)actualSize));
    swatches.setSelectedSwatch(lp.getSymbol());
  }

  
  /**
   * Set option panel.
   * @author sergei
   *
   */
  class PointSwatches extends PanelSeeOption {
    
	private static final long serialVersionUID = 1L;

	public PointSwatches() {
      setSelectedSwatch(lp.getSymbol());
      addMouseListener(new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
	  if (e.getClickCount() == 1) {
	    int index = getIndex(e.getX(),e.getY());
	    setSelectedSwatch(index);
	    lp.setSymbol(index);
	    lp.setSymbolSize(actualSize);
	  }
	}
      });
    }
    
    protected void initValues() {
      soptionSize = new Dimension(30,12);
      numSOption = new Dimension(2,7);
    }
    
    protected void paintIt(Graphics2D g2, int column, int row, int x, int y) {
      g2.setColor(Color.black);
      int type = (row*numSOption.width)+column;
      GPoints.drawPointType(type,g2,(double)(x+soptionSize.width/2),
			  (float)(y+soptionSize.height/2),5);
    }
    
    public String getToolTipText(MouseEvent e) {
      int i = getIndex(e.getX(),e.getY());
      if (i >= 0 && i < Ns) {
	return "point type " + new Integer(i).toString();
      }
      return "";
    }  
  }
}
