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

import jplot.JPlot;
import jplot.SmallColorPreview;
import jplot.Utils;

/**
 * Creates a panel with options to set Fonts for text.
 */
public class PanelFonts extends PanelGridUI {

  private static final long serialVersionUID = 1L;
  private String selectedFontname;
  private int selectedFontsize;
  private int selectedFontstyle;
  private Color selectedColor;
  private SmallColorPreview colorPreview;
  PanelColor colorPanel;

  private JComboBox fonts;
  private JComboBox sizes;
  private JPanel colors;

  private JCheckBox cb_bold, cb_italic, cb_underline;
  private JRadioButton rb_normal, rb_superscript, rb_subscript;
  private Font result;
  private JDialog dialog;
  private JPlot jplot;

  String[] fontSizes = {"6","7","8","9","10","11","12","14","16","18","20","24","28"};

  /**
   * Principal constructor. Builds a panel with a list
   * of all avaible fonts from which the user can select, as
   * well as the point size and type (italic, bold ...).
   * @param actualFont name of the initial font.
   */
  public PanelFonts(JPlot jp, Font actualFont, Color actualColor) {
    jplot = jp;
    colorPanel = null;
    selectedColor = actualColor;

    selectedFontname = actualFont.getName();
    selectedFontsize = actualFont.getSize();
    selectedFontstyle = actualFont.getStyle();

    // make the widgets:
    //------------------
    fonts = new JComboBox(jplot.fontNames);
    fonts.setSelectedItem(selectedFontname);
    sizes = new JComboBox(fontSizes);
    sizes.setSelectedItem(Integer.toString(selectedFontsize));

    if (selectedColor != null) {
      colors = new JPanel(new BorderLayout());
      colorPreview = new SmallColorPreview(selectedColor,40,22);
      colors.add(colorPreview,BorderLayout.CENTER);
      JButton b = new JButton(jplot.getImageIcon("color.jpg"));
      b.setMargin(new Insets(0,0,0,0));
      b.setToolTipText("Text color");
      b.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (colorPanel == null) {
	    colorPanel = new PanelColor(jplot.frame,selectedColor);
	  }
	  else colorPanel.refresh(selectedColor);
	  Color _color = colorPanel.show(jplot.frame,selectedColor,100,100);
	  if (_color != null) {
	    selectedColor = _color;
	    colorPreview.setColor(selectedColor);
	  }
	}
      });
      colors.add(b,BorderLayout.EAST);
    }
    
    sizes.setEditable(true);
    cb_bold = new JCheckBox("Bold");
    cb_italic = new JCheckBox("Italic");
    cb_underline = new JCheckBox("Underline");
    cb_underline.setEnabled(false);

    ButtonGroup bg = new ButtonGroup();
    rb_normal = new JRadioButton("Normal");
    bg.add(rb_normal);
    rb_normal.setSelected(true);
    rb_superscript = new JRadioButton("Superscript");
    bg.add(rb_superscript);
    rb_superscript.setEnabled(false);
    rb_subscript = new JRadioButton("Subscript");
    bg.add(rb_subscript);
    rb_subscript.setEnabled(false);

    //setBorder(new TitledBorder(new EtchedBorder(),"Font properties")); 
    //setBorder(new EmptyBorder(new Insets(5,5,5,5)));
    setBorder(new EtchedBorder()); 
    
    Dimension longField = new Dimension(200,22);
    Dimension shortField = new Dimension(60,22);
    Dimension mediumField = new Dimension(120,22);

    // spacing between labels and fields:
    EmptyBorder border1 = new EmptyBorder(new Insets(0,0,0,10));
//    EmptyBorder border2 = new EmptyBorder(new Insets(0,0,0,1));

    JLabel lb1 = new JLabel("Font:");
    lb1.setBorder(border1);
    addComponent(lb1,1,1);
    fonts.setPreferredSize(longField);
    addFilledComponent(fonts,1,2,3,1,GridBagConstraints.HORIZONTAL);

    JLabel lb2 = new JLabel("Size:");
    lb2.setBorder(border1);
    addComponent(lb2,2,1);
    sizes.setPreferredSize(shortField);
    addComponent(sizes,2,2);

    if (selectedColor != null) {
      JLabel lb3 = new JLabel("Color:");
      lb3.setBorder(border1);
      addComponent(lb3,3,1);
      colors.setPreferredSize(shortField);
      addComponent(colors,3,2);
    }

    JLabel lb4 = new JLabel("Style:");
    lb4.setBorder(border1);
    addComponent(lb4,4,1);
    cb_bold.setPreferredSize(mediumField);
    addComponent(cb_bold,4,2);
    rb_normal.setPreferredSize(mediumField);
    addComponent(rb_normal,4,3);

    cb_italic.setPreferredSize(mediumField);
    addComponent(cb_italic,5,2);
    rb_superscript.setPreferredSize(mediumField);
    addComponent(rb_superscript,5,3);

    cb_underline.setPreferredSize(mediumField);
    addComponent(cb_underline,6,2);
    rb_superscript.setPreferredSize(mediumField);
    addComponent(rb_subscript,6,3);
  }  

  /**
   * Another constructor. Builds a panel with a list
   * of all avaible fonts from which the user can select, as
   * well as the point size and type (italic, bold ...),
   * but without a color chooser.
   * @param actualFont name of the initial font.
   */
  public PanelFonts(JPlot jp, Font actualFont) {
    this(jp,actualFont,null);
  }
  
  /**
   * Builds the panel with default values.
   */
  public PanelFonts(JPlot jp) {
    this(jp,Utils.getDefaultFont());
  }
  
  /**
   * @return the selected font.
   */
  public Font getSelectedFont() {
    if (cb_bold.isSelected()) {
      if (cb_italic.isSelected()) selectedFontstyle = Font.BOLD | Font.ITALIC;
      else selectedFontstyle = Font.BOLD;
    }
    else if (cb_italic.isSelected()) selectedFontstyle = Font.ITALIC;
    else selectedFontstyle = Font.PLAIN;
    selectedFontname = (String)fonts.getSelectedItem();
    selectedFontsize = Integer.parseInt((String)sizes.getSelectedItem());
    return new Font(selectedFontname,selectedFontstyle,selectedFontsize);
  }  
  
  /**
   * @return the selected text color.
   */
  public Color getSelectedColor() {
    return selectedColor;
  }  
  
  /**
   * Sets the font from a font name.
   * @param f the selected font.
   */
  public void setSelectedFontname(String f) {
    selectedFontname = f;
  }

  /**
   * Sets the font size.
   * @param s point size of the font.
   */
  public void setSelectedFontsize(int s) {
    selectedFontsize = s;
  }

  /**
   * updates the panel.
   */
  public void refresh(Font f, Color c) {
    if (f != null) {
      selectedFontname = f.getName();
      fonts.setSelectedItem(selectedFontname);
      selectedFontsize = f.getSize();
      sizes.setSelectedItem(Integer.toString(selectedFontsize));
      selectedFontstyle = f.getStyle();
      if (selectedFontstyle == Font.BOLD) cb_bold.setSelected(true);
      else cb_bold.setSelected(false);
      if (selectedFontstyle == Font.ITALIC) cb_italic.setSelected(true);
      else cb_italic.setSelected(false);
    }
    if (c != null) {
      selectedColor = c;
      colorPreview.setColor(selectedColor);
      
    }
  }

  /**
   * updates the panel.
   */
  void refresh(Font f) {
    refresh(f,null);
  }

  /**
   * Pops up a modal frame including the font panel. Returns the
   * actual font or null if the user pressed the cancel button.
   * @param parent parent frame
   * @param x x-position of the dialog
   * @param y y-position of the dialog
   * @return the color chosen by the user, null if canceled.
   */
  Font show(Frame parent, int x, int y) {
    if (dialog == null) {
      JPanel panel = new JPanel(new BorderLayout());
      dialog = new JDialog(parent,"Font chooser",true);
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
	  result = getSelectedFont();
	  dialog.dispose();
	}
      });
      p.add(b);
      b = new JButton("Cancel");
      b.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  result = null;
	  dialog.dispose();
	}
      });
      p.add(b);
      panel.add(this,BorderLayout.CENTER);
      panel.add(p,BorderLayout.SOUTH);
      dialog.getContentPane().add(panel);
      dialog.setLocation(x,y);
      dialog.pack();
    }
    
    dialog.setVisible(true);  
    return result;
  } 
}
