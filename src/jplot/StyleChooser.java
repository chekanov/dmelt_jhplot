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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
// import javax.swing.event.*;
import javax.swing.JColorChooser;
import javax.swing.JColorChooser.*;

import java.net.*;
import java.io.Serializable;

import jplot.panels.PanelColor;
import jplot.panels.PanelDash;
import jplot.panels.PanelGridUI;
import jplot.panels.PanelPoint;
/**
 * StyleChooser is a class which enables the user to set the drawing style.
 * It must be called with a DataArray object, which contains the initial
 * drawing style. This class also provides the data drawing style 
 * chooser, an advanced specific purpose chooser which allows to select
 * a line- and/or point drawing style, a plotting style, colors etc.
 */
public class StyleChooser implements Serializable  {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private LinePars lp;
  private LineStyle lineStyle;
  private JDialog chooser;
  private SmallToggleButton b1,b2,b3,b4;
  private ImageIcon icon1,icon2,icon3;
  private PanelDash dashPanel;
  private PanelPoint pointPanel;
  private PanelColor colorPanel, popupColorPanel;
  private JPanel chooserPanel;
  private JPlot jplot;

  private SmallColorPreview startColorPreview;
  private SmallColorPreview endColorPreview;
  private SmallColorPreview fillColorPreview;
  private JButton b_startColor, b_endColor;
  private JButton b_fillColor;
  private JRadioButton rb_slideColor;

  boolean actionNeeded; 
  private SmallScrollPane ssp,sspSys; 
  String[] ten = {"10","9","8","7","6","5","4","3","2","1"};

  private JCheckBox cb_show, cb_hide;
  private JCheckBox errX_show, errX_hide, errY_show, errY_hide;
  private JCheckBox errSysX_show, errSysX_hide,errSysY_show, errSysY_hide;
  private Color selectedColor;
  private JColorChooser colorChooser;

  private JTextField legendName,divider,additioner;
  private Frame frame;
  private String title;

  private boolean isGlobal;
  
  
 
  
  /**
   * Principal constructor.
   * @param f parent frame instance to which this dialog is attached
   * @param title current title of the dialog
   * @param lp line parameter settings (pointtype, color, linestyle...)
   * @param global true if built as a global chooser
   */
  public StyleChooser(Frame f, String title, LinePars lp, 
		      JPlot jp, boolean global) {
    frame = f;
    this.title = title;
    isGlobal = global;
    jplot=jp;
    if (lp != null) this.lp = lp;
    else this.lp = new LinePars();
    chooser = null;
  }

  /**
   * Constructor.
   * @param f parent frame instance to which this dialog is attached
   * @param title current title of the dialog
   * @param lp line parameter settings (pointtype, color, linestyle...)
   */
  public StyleChooser(Frame f, String title, LinePars lp) {
    this(f,title,lp,null,false);
  }

  /**
   * Principal constructor. The argument is a DataArray object, which
   * contains the drawing styles. Always built as a local chooser.
   * @param f parent frame instance to which this dialog is attached
   * @param title current title of the dialog
   * @param global true if built as a global chooser
   */
  public StyleChooser(Frame f, String title, boolean global) {
    this(f,title,null,null,global);
  }

  /**
   * Another constructor. The argument is a DataArray object, which
   * contains the drawing styles. Always built as a local chooser.
   * @param f parent frame instance to which this dialog is attached
   * @param title current title of the dialog
   */
  public StyleChooser(Frame f, String title) {
    this(f,title,null,null,false);
  }

  /*
   * Returns an image which is found in a valid image URL. The basis
   * of the url is where 'jplot' is created.
   * @param name name of the image
   * @return an image or icon 
   */
  private ImageIcon getImageIcon(String name) {
    ImageIcon im=null;
    try {
      URL imageURL = this.getClass().getResource("/images/" + name);
      Toolkit tk = Toolkit.getDefaultToolkit();
      im = new ImageIcon(tk.createImage(imageURL));
    }
    catch(Exception e) {
      Utils.oops(null,"Impossible to load JPlot's icon '" + name + "'.\nSomething's wrong with the installation.");
    }
    return im;
  }


  /*
   * Returns a chooser panel for selecting a new drawing style.  The
   * panel contains five fields, with the <b>style</b> (e.g., error
   * boxes, lines, points, histograms etc.), the <b>line-type</b>
   * (solid, dashed, etc.), the <b>point-type</b> (none, dots,
   * triangles etc.), the </b>color</b> and a window showing the
   * actual choice.
   * 
   * @param isGlobal true if the style chooser is used for all the 
   * datapoints
   */
  private void getChooserPanel() {
    chooserPanel = new JPanel(new BorderLayout());

    //fillColor = Color.blue;
    Insets insets = new Insets(0,0,0,0);

    JPanel mainPanel = new JPanel(new BorderLayout());

    // the northern part contains the minigraphs
    // which define the drawing styles:
    //------------------------------------------
    JPanel north = new JPanel(new BorderLayout());

    JPanel p = new JPanel();
    north.setBorder(new TitledBorder(new EtchedBorder(),"Styles"));
    ImageIcon icon = getImageIcon("plot1.png");
    b1 = new SmallToggleButton(false,icon,null,"Line connecting points");
    b1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setGraphStyles(lp.LINES);
      }
    });
    b1.setMargin(insets);
    p.add(b1);

    icon = getImageIcon("plot2.png");
    b2 = new SmallToggleButton(false,icon,null,"Histogram");
    b2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setGraphStyles(lp.HISTO);
      }
    });
    b2.setMargin(insets);  
    p.add(b2);

    icon = getImageIcon("plot3.png");
    b3 = new SmallToggleButton(false,icon,null,"Bar graph");
    b3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setGraphStyles(lp.STICKS);
      }
    });
    b3.setMargin(insets);  
    p.add(b3);
    north.add(p,BorderLayout.WEST);

    icon = getImageIcon("plot4.png");
    b4 = new SmallToggleButton(false,icon,null,"Fill color");
    b4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	b_fillColor.setEnabled(b4.isSelected());
      }
    });
    b4.setMargin(insets);  
    p = new JPanel();
    p.add(b4);

    //fillColorPreview = new SmallColorPreview(lp.getFillColor(),28,28);
    fillColorPreview = new SmallColorPreview(lp.getFillColor(),32,17);
    p.add(fillColorPreview);

    b_fillColor = new JButton(getImageIcon("color.jpg"));
    b_fillColor.setMargin(insets);
    b_fillColor.setToolTipText("Text color");
    b_fillColor.setSelected(false);
    b_fillColor.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (popupColorPanel == null) {
	    popupColorPanel = new PanelColor(frame);
	  }
	  Color color = popupColorPanel.show(frame,
					     lp.getFillColor(),100,100);
	  if (color != null) fillColorPreview.setColor(color);
	}
      });
    p.add(b_fillColor);
    north.add(p,BorderLayout.EAST);
    
    mainPanel.add(north,BorderLayout.NORTH);

    // the western part is about linestyle (dash-types):
    //--------------------------------------------------
    dashPanel = new PanelDash(lp);
    mainPanel.add(dashPanel,BorderLayout.WEST);

    // center panel contains the plotting points:
    //-------------------------------------------
    pointPanel = new PanelPoint(lp);
    mainPanel.add(pointPanel,BorderLayout.CENTER);

    // right panel with the colors:
    //-----------------------------
    colorPanel = new PanelColor(frame,lp.getColor());
    mainPanel.add(colorPanel,BorderLayout.EAST);

    // a small panel with the legend label and a checkbox:
    //----------------------------------------------------
    PanelGridUI legend=null;
    PanelGridUI global=null;
    if (!isGlobal) {
      legend = new PanelGridUI();
      legend.setBorder(new TitledBorder(new EtchedBorder(),"Legend label"));
      legendName = new JTextField(lp.getName());
      legend.addFilledComponent(legendName,1,1,2,1,GridBagConstraints.HORIZONTAL);
      ButtonGroup bg = new ButtonGroup();
      cb_show = createCheckBox("show ",lp.drawLegend());
      bg.add(cb_show);
      legend.addComponent(cb_show,1,3);
      cb_hide = createCheckBox("hide ",!lp.drawLegend());
      bg.add(cb_hide);
      legend.addComponent(cb_hide,1,4);
    }

    // button to reset the plot style to default:
    else {
      global = new PanelGridUI();
      global.setBorder(new TitledBorder(new EtchedBorder(),"Global options"));

      rb_slideColor = new JRadioButton("slide color");
      rb_slideColor.setSelected(false);
      rb_slideColor.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    b_startColor.setEnabled(rb_slideColor.isSelected());
	    b_endColor.setEnabled(rb_slideColor.isSelected());
	  }
	});
      global.addComponent(rb_slideColor,1,1);

      b_startColor = new JButton(getImageIcon("color.jpg"));
      b_startColor.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    if (popupColorPanel == null) {
	      popupColorPanel = new PanelColor(frame);
	    }
	    Color color = popupColorPanel.show(frame,
					       lp.getStartColor(),100,100);
	    if (color != null) startColorPreview.setColor(color);
	  }
	});
      b_startColor.setMargin(new Insets(0,0,0,0));
      b_startColor.setToolTipText("Change the start color");
      startColorPreview = new SmallColorPreview(lp.getStartColor(),40,18);
      p = new JPanel(new FlowLayout());
      p.add(startColorPreview);
      p.add(b_startColor);

      JLabel label = new JLabel("Start:");
      //label.setBorder(border);
      global.addComponent(label,1,2);
      global.addComponent(p,1,3);      

      b_endColor = new JButton(getImageIcon("color.jpg"));
      b_endColor.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    if (popupColorPanel == null) {
	      popupColorPanel = new PanelColor(frame);
	    }
	    Color color = popupColorPanel.show(frame,
					       lp.getEndColor(),100,100);
	    if (color != null) endColorPreview.setColor(color);
	  }
	});
      b_endColor.setMargin(new Insets(0,0,0,0));
      b_endColor.setToolTipText("Change the end color");
      endColorPreview = new SmallColorPreview(lp.getEndColor(),40,18);
      p = new JPanel(new FlowLayout());
      p.add(endColorPreview);
      p.add(b_endColor);

      label = new JLabel("End:");
      //label.setBorder(border);
      global.addComponent(label,1,4);
      global.addComponent(p,1,5);      
    }
    

// chekanov: scaling panel. Do not need it
    JPanel scaling = new JPanel(new FlowLayout());
    scaling.setBorder(new TitledBorder(new EtchedBorder(),"Scaling"));
    divider = new JTextField(Float.toString(1.0f/lp.getMultiplier()));
    divider.setPreferredSize(new Dimension(65,24));
    scaling.add(new JLabel("divide by:"));
    scaling.add(divider);
    additioner = new JTextField(Float.toString(lp.getAdditioner()));
    additioner.setPreferredSize(new Dimension(50,24));
    scaling.add(new JLabel("offset:"));
    scaling.add(additioner);

    p = new JPanel();
    p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
    if (!isGlobal) p.add(legend);
    else p.add(global);
// chekanov  Do not need it
//    p.add(scaling);



// first level
    JPanel errors = new JPanel(new FlowLayout());
    errors.setBorder(new TitledBorder(new EtchedBorder(),"1st level errors"));
    errors.setLayout( new GridLayout(3,1));
     JPanel errors1 = new JPanel(new FlowLayout());
     JPanel errors2 = new JPanel(new FlowLayout());
     JPanel errors3 = new JPanel(new FlowLayout());
      errors.add(errors1);
       errors.add(errors2);
         errors.add(errors3); 

    errors1.add(MyUtils.getLabel("pen width"));
    errors1.add(Box.createHorizontalStrut(4));
    ssp = new SmallScrollPane(ten);
    ssp.setSelectedIndex(ten.length - (int)lp.getPenWidthErr());
    ssp.getViewport().addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            lp.setPenWidthErr(Float.parseFloat(ten[ssp.getSelectedIndex()]));
        }
      });
    errors1.add(ssp);


    // make a button for a custom color chooser...
    //--------------------------------------------
   // colorChooser = Utils.getColorChooser();
    final Color colX=lp.getColorErrorsX();
    JButton cButton = new JButton("Custom...");
    cButton.setPreferredSize(new Dimension(65,26));
    cButton.setMargin(new Insets(1,1,1,1));
    cButton.setFont(new Font("SansSerif",Font.PLAIN,11));
    cButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color c = JColorChooser.showDialog(frame, "Dialog Title", colX);
        if (c != null) {
        	lp.setColorErrorsX(c);
        	lp.setColorErrorsY(c);
        }
      }
    });
    errors1.add(cButton);


 
      ButtonGroup errX_bg = new ButtonGroup();
      errX_show = createCheckBox("show on X",lp.errorsShowX);
      errX_bg.add(errX_show);
      errors2.add(errX_show);
      errX_hide = createCheckBox("hide on X",!lp.errorsShowX);
      errX_bg.add(errX_hide);
      errors2.add(errX_hide);

      ButtonGroup errY_bg = new ButtonGroup();
      errY_show = createCheckBox("show on Y",lp.errorsShowY);
      errY_bg.add(errY_show);
      errors3.add(errY_show);
      errY_hide = createCheckBox("hide on Y",!lp.errorsShowY);
      errY_bg.add(errY_hide);
      errors3.add(errY_hide);


    p.add(errors);


// 2nd level
    JPanel errorsSys = new JPanel(new FlowLayout());
    errorsSys.setBorder(new TitledBorder(new EtchedBorder(),"2nd level errors"));
    errorsSys.setLayout( new GridLayout(3,1));

     JPanel errorsSys1 = new JPanel(new FlowLayout());
     JPanel errorsSys2 = new JPanel(new FlowLayout());
     JPanel errorsSys3 = new JPanel(new FlowLayout());
      errorsSys.add(errorsSys1);
       errorsSys.add(errorsSys2);
         errorsSys.add(errorsSys3);


    errorsSys1.add(MyUtils.getLabel("pen width"));
    errorsSys1.add(Box.createHorizontalStrut(4));
    sspSys = new SmallScrollPane(ten);
    sspSys.setSelectedIndex(ten.length - (int)lp.getPenWidthErrSys());
    sspSys.getViewport().addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            lp.setPenWidthErrSys(Float.parseFloat(ten[sspSys.getSelectedIndex()]));
        }
      });
    errorsSys1.add(sspSys);

    final Color colSysX=lp.getColorErrorsSysX();
    JButton ccButton = new JButton("Custom...");
    ccButton.setPreferredSize(new Dimension(65,26));
    ccButton.setMargin(new Insets(1,1,1,1));
    ccButton.setFont(new Font("SansSerif",Font.PLAIN,11));
    ccButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color c = JColorChooser.showDialog(frame, "Dialog Title", colSysX);
        if (c != null) {
        	lp.setColorErrorsSysX(c);
        	lp.setColorErrorsSysY(c);
        }
      }
    });

    errorsSys1.add(ccButton);
    
    

      ButtonGroup errSysX_bg = new ButtonGroup();
      errSysX_show = createCheckBox("show on X",lp.errorsShowX);
      errSysX_bg.add(errSysX_show);
      errorsSys2.add(errSysX_show);
      errSysX_hide = createCheckBox("hide on X",!lp.errorsShowX);
      errSysX_bg.add(errSysX_hide);
      errorsSys2.add(errSysX_hide);

      ButtonGroup errSysY_bg = new ButtonGroup();
      errSysY_show = createCheckBox("show on Y",lp.errorsShowSysY);
      errSysY_bg.add(errSysY_show);
      errorsSys3.add(errSysY_show);
      errSysY_hide = createCheckBox("hide on Y",!lp.errorsShowSysY);
      errSysY_bg.add(errSysY_hide);
      errorsSys3.add(errSysY_hide);


    p.add(errorsSys);




    mainPanel.add(p,BorderLayout.SOUTH);
    chooserPanel.add(mainPanel,BorderLayout.CENTER);

    // south contains buttons OK or Cancel
    //------------------------------------
    p = new JPanel();
    p.setBorder(new EtchedBorder());
    JPanel buttons = new JPanel(new GridLayout(1,2));
    JButton b = new JButton("Apply");
    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	lp.setColor(colorPanel.getSelectedColor());
	if (b4.isSelected()) {
	  lp.fill(true);
	  lp.setFillColor(fillColorPreview.getColor());
	}
	else lp.fill(false);
	if (!isGlobal) {
	  lp.setName(legendName.getText());
	  if (cb_show.isSelected()) lp.setDrawLegend(true);
	  else lp.setDrawLegend(false);

          // naw fix the errors
          lp.errorsX(false);
          lp.errorsY(false);
          lp.errorsSysX(false);
          lp.errorsSysY(false);
          if (errX_show.isSelected()) lp.errorsX(true);
          if (errY_show.isSelected()) lp.errorsY(true);
          if (errSysX_show.isSelected()) lp.errorsSysX(true);
          if (errSysY_show.isSelected()) lp.errorsSysY(true);
         // System.out.println(errY_show.isSelected()); 
          lp.setDataChanged(true);

	}
	else if (rb_slideColor.isSelected()) {
	  lp.setSlideColor(startColorPreview.getColor(),
			   endColorPreview.getColor());
	}
	if (!divider.getText().equals("")) {
	  lp.setMultiplier(1.0f/Float.parseFloat(divider.getText()));
	}
	else lp.setMultiplier(1.0f);
	if (!additioner.getText().equals("")) {
	  lp.setAdditioner(Float.parseFloat(additioner.getText()));
	}
	else lp.setAdditioner(0.0f);
	lp.setDataChanged(true);
        if (jplot != null) jplot.updateGraphIfShowing();
	chooser.dispose();
      }
    });
    buttons.add(b);
    b = new JButton("Cancel");
    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	lp = null;
	chooser.dispose();
      }
    });
    buttons.add(b);
    p.add(buttons);
    chooserPanel.add(p,BorderLayout.SOUTH);
  }

  /*
   * Make a label specifically used by the tic-panel.
   * @return a label with specific font and alignments.
   */
  private JLabel makeLabel(String txt)
  {
    JLabel label = new JLabel(txt + " ");
    label.setVerticalAlignment(SwingConstants.BOTTOM);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setFont(new Font("SansSerif",Font.ITALIC,11));
    label.setForeground(Color.black);
    return label;
  }

  private JCheckBox createCheckBox(String s, boolean b) {
    JCheckBox cb = new JCheckBox(" " + s + " ", b);
    cb.setFont(new Font("serif", Font.PLAIN, 12));
    cb.setHorizontalAlignment(JCheckBox.LEFT);
    return cb;
  }

  /*
   * updates the panel with the current data
   */
  private void setGraphStyles(int type) {
    lp.setGraphStyle(type);
    b1.setSelected(type == lp.LINES);
    b2.setSelected(type == lp.HISTO);
    b3.setSelected(type == lp.STICKS);
    chooserPanel.repaint();
  }

  /*
   * updates the panel with the current data
   */
  private void refresh() {
    setGraphStyles(lp.getGraphStyle());
    b4.setSelected(lp.fill());
    b_fillColor.setEnabled(lp.fill());
    dashPanel.refresh(lp);
    pointPanel.refresh(lp);
    colorPanel.refresh(lp.getColor());
    fillColorPreview.setColor(lp.getFillColor());
    divider.setText(Double.toString(1.0/lp.getMultiplier()));
    additioner.setText(Double.toString(lp.getAdditioner()));
    if (!isGlobal) {
      cb_show.setSelected(lp.drawLegend());
      cb_hide.setSelected(!lp.drawLegend());
      legendName.setText(lp.getName());
    }
    else {
      startColorPreview.setColor(lp.getStartColor());
      endColorPreview.setColor(lp.getEndColor());
      rb_slideColor.setSelected(lp.slideColor());
      b_startColor.setEnabled(rb_slideColor.isSelected());
      b_endColor.setEnabled(rb_slideColor.isSelected());
    }
  }

  /**
   * Return a modal JDialog with a customized linestyle chooser.
   * The panel includes choices for dashes, pointstyles and colors.
   * @param x coordinate of the pin used to fix this frame
   * @param y coordinate of the pin used to fix this frame
   * @param initLinePars initial line parameters
   * @return new line style parameters or null if canceled
   */
  public LinePars show(int x, int y, LinePars initLinePars) {
    if (initLinePars == null) lp = new LinePars();
    else lp = new LinePars(initLinePars);

    if (chooser == null) {
      chooser = new JDialog(frame,title,true);
      chooser.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  lp = null;
	  chooser.dispose();
	}
      });
      getChooserPanel();
      chooser.setLocation(x,y);
      chooser.getContentPane().add(chooserPanel);
      chooser.pack();
    }
    refresh();
    chooser.pack();
    chooser.setVisible(true);  // blocks until user brings dialog down.
    return lp;
  }
}

