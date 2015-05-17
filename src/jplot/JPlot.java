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
 *    Copyright (C) 1999 J.D.Lee and S.Chekanov

 **/


package jplot;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

import jhplot.JHPlot;
import jplot.panels.PanelAxes;
import jplot.panels.PanelLabel;
import jplot.panels.PanelLegend;
import jplot.panels.PanelPlot;
import jplot.panels.PanelSave;
import jplot.panels.PanelScaling;



/**
 * JPLOT: Jan's Plotting Program.
 * Developped for JCHESS (http://chess.ensmp.fr), but extended for 
 * general use.
 *
 * <p>BUGS/TODO:
 * <ul>
 * <li> rescale the X-scale as well with some value
 * <li> save/read settings stuff must be rewritten in XML format.
 * <li> put the different graph-styles in separate functions. Otherwise
 *   it is impossible to use different graph-styles together (e.g.,
 *   a histogram AND a line connecting the points).
 * <li> decent automatic update method (check datafile's date/time)
 * <li> allow for two Y scales (and 2 X scales ?)
 * </ul>
 */
public class JPlot extends JPanel {

  public final static boolean debug = false;
  
  // dimensions of the app:
  public static int WIDTH = 600;
  public static int HEIGHT = 600;

  // the name of the app
  public final static String AppName = "JPlot";

  public  JFrame frame, about;
  public  JFrame plotFrame;

  private JMenuBar menubar;
  public Vector<PanelPlot> plotPanels;

//  private static JPlot jp;
//  private static JPlot jp;
  private JPlot jplot;
  private JTabbedPane tabbedPane;
  private JToolBar toolBar;

  // vector with classes containing the data
  private Vector<DataArray>  dataArrays = new Vector<DataArray>();

  // the graph
  private GraphGeneral graph;
  
  private static int dIndex=0;

  //Options graphOptions;
  private PanelAxes axesPanel;
  private PanelScaling scalingPanel;
  private PiperOptions piperPanel;
  private PanelSave export;

  // the following panels are public since they may be
  // activated from the graph, which is displayed in an
  // external frame.
  public PanelLabel labelPanel;
  public PanelLegend legendPanel;
  public StyleChooser styleChooser;

  private StyleChooser globalStyleChooser;

  private File scriptFile;   // filename of the script

  // list of font family names available on this platform
  // (must be initialized elsewhere):
  public static String[] fontNames = {"no fonts found"};

  // the following flag yields 'true' when all the classes
  // are fully loaded (they are on a seperate thread):
  public boolean loaded;
  private boolean hideGraph;

  private GraphSettings gs;
  private JPanel mainPanel;

  // declared here, since these items may be dynamically
  // removed/inserted:
  private JMenu plotMenu;
  private JMenuItem scalingMenuItem, piperMenuItem;
  private JCheckBoxMenuItem mi_2D, mi_piper, mi_multi;
  private JCheckBoxMenuItem mi_java, mi_motif, mi_mac, mi_windows, mi_gtk;
  private JCheckBoxMenuItem mi_autoUpdate;

  private JFileChooser dataChooser;
  private javax.swing.Timer timer;

  public static final String FS = System.getProperty("file.separator");

  // stand alone is true if JPlot runs on itself (started by main())
  private boolean isStandAlone=false;

  // insets of an empty panel:
  private final Insets insets = new Insets(0,0,0,0);

  // Possible Look & Feels
  private static final String macLaf = 
    "com.sun.java.swing.plaf.mac.MacLookAndFeel";
  private static final String metalLaf = 
    "javax.swing.plaf.metal.MetalLookAndFeel";
  private static final String motifLaf = 
    "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
  private static final String windowsLaf = 
    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
  private static final String gtkLaf = 
    "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
  static private String currentLookAndFeel;

  private int xPos = 450;
  private int yPos = 20;

  // interval for automatic updating a graph.
  private int autoUpdateInterval=1000;

  /**
   * Principal constructor.
   * Builds the panel with the plotting commands
   *
   * @param files vector of files containing the data arrays. This array
   * is the result of command-line filenames, and may be empty.
   */
  public JPlot(JFrame parent, Vector files, File script, boolean lonely) {
    setLayout(new BorderLayout());
    isStandAlone = lonely;
    loaded = false;
    plotFrame = null;
    hideGraph = false;
    frame = parent;
    jplot = this;
    scriptFile = script;
    dIndex=0;

    // make the item in a item bar, but only if we
    // are running as a stand-alone application:
    //--------------------------------------------
    if (isStandAlone) {
      setBorder(new EtchedBorder());
      menubar = makeMenuBar();      
      add(menubar,BorderLayout.NORTH);
      gs = new GraphSettings(parent);
      if (scriptFile != null) gs.setTitleName(scriptFile.toString());
      gs.setTitleString();
    }
    else gs = new GraphSettings(null);


    mainPanel = new JPanel(new BorderLayout());
    toolBar = get2DToolBar();
    mainPanel.add(toolBar,BorderLayout.NORTH);
    graph = new GraphXY(jplot,gs);

    // create the tabbed pane
    //-----------------------
    tabbedPane = new JTabbedPane();
    plotPanels = new Vector<PanelPlot>();
    tabbedPane.setFont(new Font("serif", Font.PLAIN, 12));
    mainPanel.add(tabbedPane, BorderLayout.CENTER);

    // add a Tab for the general plot pane
    //------------------------------------

/*
    if (scriptFile == null) {
      for (Enumeration e=files.elements(); e.hasMoreElements();) {
	File f = (File)e.nextElement();
	if (f != null && !f.toString().equals("")) insertDatafile(f);
      }
    }
*/

    // make the buttons show, reload etc.
    //-----------------------------------
    add(mainPanel,BorderLayout.CENTER);

   
    
    // pre-build a number of time-consuming instances
    // of dialogs using separate threads:
    //-----------------------------------------------
    Thread t = new Thread() {
      public void run() {
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	if (fontNames.length == 1) {
	  fontNames = env.getAvailableFontFamilyNames();
	}
	legendPanel = new PanelLegend(jplot,gs);
	axesPanel = new PanelAxes(jplot,gs);
	labelPanel = new PanelLabel(jplot,gs);
	styleChooser = new StyleChooser(jplot.frame,"Style chooser",new LinePars());
	scalingPanel = new PanelScaling(jplot,gs);
	piperPanel = new PiperOptions(jplot,gs);
	if (debug) System.out.println("Class loading ready.");
	loaded = true;
      }
    };
    t.start();


    // load script file if any present:
    if (scriptFile != null) loadScript();
  }

  /**
   * Another constructor.
   * Builds the panel without initial files.
   */
  public JPlot(JFrame parent) {
    this(parent,new Vector(),null,false);
  }

  /**
   * Another constructor.
   * Builds the panel with files
   */
  public JPlot(JFrame parent, Vector files) {
    this(parent,files,null,false);
  }




  /**
   * Builds the main menubar
   *
   * @return an instance of a JMenuBar class
   */
  JMenuBar makeMenuBar() {
    JMenuItem mi;
    JMenuBar menuBar = new JMenuBar();

    // make the file-menu:
    JMenu file = (JMenu) menuBar.add(new JMenu("File"));
    file.setMnemonic('F');
    ImageIcon icon = getImageIcon("New16.gif");
    mi = (JMenuItem) file.add(new JMenuItem("New project",icon));
    mi.setMnemonic('N');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    mi.setAccelerator(KeyStroke.getKeyStroke('N',Event.CTRL_MASK,false));

/*
    icon = getImageIcon("Open16.gif");
    mi = (JMenuItem) file.add(new JMenuItem("Open project...",icon));
    mi.setMnemonic('O');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	openScript();
      }
    });
    mi.setAccelerator(KeyStroke.getKeyStroke('O',Event.CTRL_MASK,false));

    icon = getImageIcon("Print16.gif");
    mi = (JMenuItem) file.add(new JMenuItem("Print project...",icon));
    mi.setMnemonic('P');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	printText();
      }
    });

    file.addSeparator();
    icon = getImageIcon("Export16.gif");
    mi = (JMenuItem) file.add(new JMenuItem("Export image...",icon));
    mi.setMnemonic('E');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	exportImage();
      }
    });
    mi.setAccelerator(KeyStroke.getKeyStroke('E',Event.CTRL_MASK,false));

    file.addSeparator();
    icon = getImageIcon("Save16.gif");
    mi = (JMenuItem) file.add(new JMenuItem("Save project",icon));
    mi.setMnemonic('S');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveScript();
      }
    });
    mi.setAccelerator(KeyStroke.getKeyStroke('S',Event.CTRL_MASK,false));

    icon = getImageIcon("SaveAs16.gif");
    mi = (JMenuItem) file.add(new JMenuItem("Save project as...",icon));
    mi.setMnemonic('A');
    file.addSeparator();
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveAsScript();
      }
    });
    mi.setAccelerator(KeyStroke.getKeyStroke('A',Event.CTRL_MASK,false));

    file.addSeparator();
    mi = (JMenuItem) file.add(new JMenuItem("Quit"));
    mi.setMnemonic('Q');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	quitDialog();
      }
    });
    mi.setAccelerator(KeyStroke.getKeyStroke('Q',Event.CTRL_MASK,false));


    // make the data-menu:
    //--------------------
    JMenu data = (JMenu) menuBar.add(new JMenu("Data"));
    data.setMnemonic('D');
    icon = getImageIcon("Add16.gif");
    mi = (JMenuItem) data.add(new JMenuItem("Load datafile",icon));
    mi.setMnemonic('L');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	chooseDatafile();
      }
    });
    mi.setAccelerator(KeyStroke.getKeyStroke('L',Event.CTRL_MASK,false));

    icon = getImageIcon("Remove16.gif");
    mi = (JMenuItem) data.add(new JMenuItem("Close datafile",icon));
    mi.setMnemonic('C');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeDatafile();
      }
    });

    mi.setAccelerator(KeyStroke.getKeyStroke('D',Event.CTRL_MASK,false));
*/

//     icon = getImageIcon("Preferences16.gif");
//     mi = (JMenuItem) data.add(new JMenuItem("Global plotstyle",icon));
//     mi.setMnemonic('G');
//     mi.addActionListener(new ActionListener() {
//       public void actionPerformed(ActionEvent e) {
// 	showGlobal();
//       }
//     });
//    data.addSeparator();



/* 
    ButtonGroup group = new ButtonGroup();
    mi_2D = new JCheckBoxMenuItem("2D graph");
    mi_2D.setSelected(true);
    data.add(mi_2D);
    mi_2D.setMnemonic('2');
    mi_2D.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	switchGraphType(gs.GRAPHTYPE_2D);
      }
    });
    group.add(mi_2D);

    mi_piper = new JCheckBoxMenuItem("Piper graph");
    data.add(mi_piper);
    mi_piper.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	switchGraphType(gs.GRAPHTYPE_PIPER);
      }
    });
    group.add(mi_piper);

    mi_multi = new JCheckBoxMenuItem("Multi graph");
    data.add(mi_multi);
    mi_multi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	switchGraphType(gs.GRAPHTYPE_MULTI);
      }
    });
    group.add(mi_multi);

    // make the plot-menu:
    //--------------------
    plotMenu = (JMenu) menuBar.add(new JMenu("Plot")); 
    plotMenu.setMnemonic('P');
    mi = (JMenuItem) plotMenu.add(new JMenuItem("Show graph"));
    mi.setMnemonic('S');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	showGraph(true);
      }
    });

    mi = (JMenuItem) plotMenu.add(new JMenuItem("Dismiss graph"));
    mi.setMnemonic('D');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	dismissGraph();
      }
    });
    plotMenu.addSeparator();
    icon = getImageIcon("Print16.gif");
    mi = (JMenuItem) plotMenu.add(new JMenuItem("Print graph",icon));
    mi.setMnemonic('r');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	printGraph();
      }
    });
    plotMenu.addSeparator();
    icon = getImageIcon("Axes16.png");
    mi = (JMenuItem) plotMenu.add(new JMenuItem("Axes",icon));
    mi.setMnemonic('A');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	optionsAction(0);
      }
    });
    icon = getImageIcon("Scaling16.png");
    scalingMenuItem = (JMenuItem) plotMenu.add(new JMenuItem("Scaling",icon));
    scalingMenuItem.setMnemonic('c');
    scalingMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	optionsAction(1);
      }
    });
    piperMenuItem = new JMenuItem("Piper");
    piperMenuItem.setMnemonic('i');
    piperMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	optionsAction(4);
      }
    });
    icon = getImageIcon("Labels16.png");
    mi = (JMenuItem) plotMenu.add(new JMenuItem("Labels",icon));
    mi.setMnemonic('L');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	optionsAction(2);
      }
    });
    icon = getImageIcon("Legend16.png");
    mi = (JMenuItem) plotMenu.add(new JMenuItem("Legend",icon));
    mi.setMnemonic('g');
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	optionsAction(3);
      }
    });

    // make the Options menu:
    //-----------------------
    JMenu prefMenu = (JMenu) menuBar.add(new JMenu("Preferences")); 
    prefMenu.setMnemonic('r');
    group = new ButtonGroup();
    mi_java = new JCheckBoxMenuItem("Java Look & Feel");
    prefMenu.add(mi_java);
    mi_java.setMnemonic('J');
    mi_java.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateLookAndFeel(metalLaf);
      }
    });
    if (currentLookAndFeel.equals(metalLaf)) mi_java.setSelected(true);
    mi_java.setEnabled(isAvailableLookAndFeel(metalLaf));
    group.add(mi_java);

    mi_mac = new JCheckBoxMenuItem("Macintosh Look & Feel");
    prefMenu.add(mi_mac);
    mi_mac.setMnemonic('M');
    mi_mac.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateLookAndFeel(macLaf);
      }
    });
    mi_mac.setEnabled(isAvailableLookAndFeel(macLaf));
    group.add(mi_mac);

    mi_motif = new JCheckBoxMenuItem("Motif Look & Feel");
    prefMenu.add(mi_motif);
    mi_motif.setMnemonic('o');
    mi_motif.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateLookAndFeel(motifLaf);
      }
    });
    mi_motif.setEnabled(isAvailableLookAndFeel(motifLaf));
    group.add(mi_motif);

    mi_windows = new JCheckBoxMenuItem("Windows Look & Feel");
    prefMenu.add(mi_windows);
    mi_windows.setMnemonic('n');
    mi_windows.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateLookAndFeel(windowsLaf);
      }
    });
    mi_windows.setEnabled(isAvailableLookAndFeel(windowsLaf));
    if (currentLookAndFeel.equals(windowsLaf)) mi_windows.setSelected(true);
    group.add(mi_windows);

    mi_gtk = new JCheckBoxMenuItem("GTK+ Look & Feel");
    prefMenu.add(mi_gtk);
    mi_gtk.setMnemonic('g');
    mi_gtk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateLookAndFeel(gtkLaf);
      }
    });
    mi_gtk.setEnabled(isAvailableLookAndFeel(gtkLaf));
    if (currentLookAndFeel.equals(gtkLaf)) mi_gtk.setSelected(true);
    group.add(mi_gtk);

    prefMenu.addSeparator();

    mi_autoUpdate = new JCheckBoxMenuItem("Automatic update");
    prefMenu.add(mi_autoUpdate);
    mi_autoUpdate.setMnemonic('u');
    mi_autoUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setAutoUpdate(mi_autoUpdate.isSelected());
      }
    });
    mi_autoUpdate.setSelected(false);

    // make the about-menu:
    JMenu m_about = (JMenu) menuBar.add(new JMenu("About"));
    m_about.setMnemonic('A');
    icon = getImageIcon("About16.gif");
    mi = (JMenuItem) m_about.add(new JMenuItem("about JPlot",icon));
    mi.setMnemonic('J');
    mi.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (about == null) about = getAbout("AboutJPlot.html");
	  about.setVisible(true);
	}
      });
    icon = getImageIcon("WebComponent16.gif");
    mi = (JMenuItem) m_about.add(new JMenuItem("JPLOT web site",icon));
    mi.setMnemonic('w');
    mi.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  // TODO:
	  String command;
	  if (System.getProperty("file.separator").equals("/")) {
	    command = "netscape http://www.cig.ensmp.fr/~vanderlee/jplot";
	  }
	  else command = "cmd /C start http://www.cig.ensmp.fr/~vanderlee/jplot";
	  try {	  
	    Runtime.getRuntime().exec(command);
	  }
	  catch(IOException ex) {
	  }
	}
      });
*/
   
    return menuBar;
  }  

  /**
   * Returns a HTML page which is found in a valid image URL. The basis
   * of the url is where 'intro' is created, which can't be but the
   * place where JChess resides.
   * @param name name of the HTML page
   * @return the URL to the page
   */
  private URL getHTMLUrl(String name) {
    URL url=null;
    try {
      url = frame.getClass().getResource("/html/" + name);
    }
    catch(Exception e) {
      Utils.oops(frame,"Impossible to load the About content.\nSomething's wrong with the installation.");
    }
    return url;
  }

  /*
   * Build an about dialog, HTML in a frame:
   */

/*
  private JFrame getAbout(String page) {
    final JFrame about = new JFrame("About");
    JPanel p = new JPanel(new BorderLayout());
    JEditorPane ed = new JEditorPane();
    JScrollPane sp = new JScrollPane(ed);
    sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    sp.setPreferredSize(new Dimension(580,650));
    try {
      ed.setPage(getHTMLUrl(page));
    } 
    catch (IOException e) {
      Utils.oops(frame,"Installation error, can't load " + getHTMLUrl(page));
    }
    ed.setEditable(false);
    about.getContentPane().add(sp);
    JButton b = new JButton("Dismiss");
    b.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  about.dispose();
	}
      });
    p = new JPanel();
    p.add(b);
    about.getContentPane().add(p,BorderLayout.SOUTH);
    about.pack();
    return about;
  }
 */
  

 
 
  /**
   * Update the graph if the graph is actually showing.
   * Also repains the plot panel because it shows linestyles.
   * @param b true if the graph must be brought to the front
   */
  public void updateGraphIfShowing(boolean b) {
    if (plotFrame != null && plotPanels.size() > 0) {
      showGraph(true,b);
      int i = 0;
      if (plotPanels.size() > 1) i = tabbedPane.getSelectedIndex();
      ((PanelPlot)plotPanels.get(i)).update();
    }
    else showGraph(false,b);
  }

  /**
   * Update the graph if the graph is actually showing.
   * Also repains the plot panel because it shows linestyles.
   */
  public void updateGraphIfShowing() {
    updateGraphIfShowing(true);
  }


// get Graph 
 public   GraphGeneral  getGraph() {
  
      return graph; 
      
  }


// get the graph settings 
 public   GraphSettings  getGraphSettings() {
      return gs;
  }


// set the graph settings
 public  void setGraphSettings( GraphSettings gs) {
      this.gs=gs;
  }



// get the graph settings
 public   StyleChooser   getStyleChooser() {
      return styleChooser;
  }



// get the axis panel 
 public   PanelAxes  getAxesPanel() {
      return axesPanel;
  }

// get the scaling panel
 public   PanelScaling  ScalingPanel() {
      return scalingPanel;
  }

// get the label panel
 public   PanelLabel getLabelPanel() {
      return labelPanel;
  }

// get the label panel
 public    PanelLegend  getLegendPanel() {
      return legendPanel;
  }




  /*
   * Action performed when the user selects the graph-options.
   * test whether the graphOptions instance is ready.
   */
  private void optionsAction(int k) {
    if (loaded) {
      int x = (int)frame.getLocation().getX() + 20;
      int y = (int)frame.getLocation().getY() + 20;
      if (k == 0) axesPanel.show(x,y);
      else if (k == 1) scalingPanel.showIt(frame,x,y);
      else if (k == 2) labelPanel.show(frame,x,y);
      else if (k == 3) legendPanel.show(frame,x,y);
      else if (k == 4) piperPanel.show(frame,x,y);
      //else if (k == 5) showGlobal();
    }
  }

  /**
   * Returns an image which is found in a valid image URL. The basis
   * of the url is where 'jplot' is created.
   * @param name name of the image
   * @return an image or icon 
   */
  public ImageIcon getImageIcon(String name) {
    ImageIcon im=null;
    try {
      URL imageURL = jplot.getClass().getResource("/images/" + name);
      Toolkit tk = Toolkit.getDefaultToolkit();
      im = new ImageIcon(tk.createImage(imageURL));
    }
    catch(Exception e) {
      Utils.oops(frame,"Impossible to load JPlot's icon '" + name + "'.\nSomething's wrong with the installation.");
    }
    return im;
  }

  /**
   * Switch from one graph type to another.  Calling this function
   * switches the GUI in a mode which is specific for a graph. It
   * should (dynamically) change the menu items, options, toolbar
   * etc.. The argument is a graph type, a symbolic constant
   * statically defined in gs.
   *
   * @param graphType type of the graph (i.e. PIPER, 2D...)  
   * @return true if the graph has actually switched
   */
  public boolean switchGraphType(int graphType) {
    boolean res = false;
    if (gs.getGraphType() == graphType) return res;
    else {
      if (gs.dataChanged()) {
	int result = JOptionPane.showConfirmDialog(frame,"The current data set corresponds to a different graph-\ntype.  Loading the data will reset all the settings to\ntheir default values and they have not been saved.\n\nDo you want to save the settings now?","Warning",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
	if ((result == JOptionPane.YES_OPTION && !saveScript()) ||
	    result == JOptionPane.CANCEL_OPTION) return false;
      }
      if (debug) System.out.println("switching graph type to " + graphType);

      gs.reset();
      gs.setGraphType(graphType);


      axesPanel = new PanelAxes(jplot,gs);
      labelPanel = new PanelLabel(jplot,gs);
      scalingPanel = new PanelScaling(jplot,gs);
      if (toolBar != null) mainPanel.remove(toolBar);
      if (graphType == gs.GRAPHTYPE_PIPER) {
	toolBar = getPiperToolBar();
	if (isStandAlone) {
	  plotMenu.remove(scalingMenuItem);
	  plotMenu.add(piperMenuItem);
	}
      }
      else {
	toolBar = get2DToolBar();
	if (isStandAlone) {
	  plotMenu.remove(piperMenuItem);
	  plotMenu.add(scalingMenuItem);
	}
	graph = new GraphXY(jplot,gs);
      }
      mainPanel.add(toolBar,BorderLayout.NORTH);
      mainPanel.repaint();
      res = true;
    }
    if (isStandAlone) {
      mi_2D.setSelected(graphType == gs.GRAPHTYPE_2D);
      mi_piper.setSelected(graphType == gs.GRAPHTYPE_PIPER);
      mi_multi.setSelected(graphType == gs.GRAPHTYPE_MULTI);
    }
    return res;
  }


  /**
   * Add a button of type SmallButton to the toolbar.
   * Done here to bypass a bug in jdk 1.4 which resets the
   * border of a button to some default value when added to the
   * toolbar.
   */
  private void addButton(JToolBar toolbar, Action action, String tip) {
    SmallButton sb = new SmallButton(action,tip);
    toolbar.add(sb);
    sb.resetBorder();    // needed to bypass a bug (since jdk1.4)
  }

 /**
   * Builds the toolbar
   *
   * @return an instance of a JToolBar
   */
  JToolBar getToolBar() {
    JToolBar toolbar = new JToolBar();

/*
    ImageIcon icon = getImageIcon("New24.gif");
    Action action = new AbstractAction("New",icon) {
	public void actionPerformed(ActionEvent e) {
	  if (JOptionPane.showConfirmDialog(jplot,"Clear the current system?\nAll settings will go to default!","",JOptionPane.YES_NO_OPTION) == 
	      JOptionPane.YES_OPTION) clear();
	}
      };
    addButton(toolbar,action,"Back to default settings");

    //ImageIcon icon = getImageIcon("newdata.png");
    icon = getImageIcon("Add24.gif");
    action = new AbstractAction("Load",icon) {
      public void actionPerformed(ActionEvent e) {
	chooseDatafile();
      }
    };
    addButton(toolbar,action,"Load a new datafile");

    //icon = getImageIcon("jp_close.jpg");
    //icon = getImageIcon("fileclose.png");
    icon = getImageIcon("Remove24.png");
    action = new AbstractAction("Close",icon) {
      public void actionPerformed(ActionEvent e) {
	removeDatafile();
      }
    };
    addButton(toolbar,action,"Close the current datafile");
    toolbar.addSeparator();
*/
    return toolbar;

  }

  /*
   * Extends the default toolbar with buttons used by
   * the (default) 2D graph stuff
   * @param toolbar current toolbar
   */

// chekanov
  private JToolBar get2DToolBar() {
    JToolBar toolbar = getToolBar();
    ImageIcon icon = getImageIcon("Refresh24.gif");
    Action action = new AbstractAction("Show",icon) {
      public void actionPerformed(ActionEvent e) {
//	showGraph(true);
        updateGraphIfShowing();
      }
    };
    addButton(toolbar,action,"Refresh the current graph");
    toolbar.addSeparator();

    icon = getImageIcon("Axes24.png");
    action = new AbstractAction("Axes",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(0);
      }
    };
    addButton(toolbar,action,"Axes parameters");

    icon = getImageIcon("Scaling24.png");
    action = new AbstractAction("Scaling",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(1);
      }
    };
    addButton(toolbar,action,"Scaling");

    icon = getImageIcon("Labels24.png");
    action = new AbstractAction("Labels",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(2);
      }
    };
    addButton(toolbar,action,"Text and labels");

    icon = getImageIcon("Legend24.png");
    action = new AbstractAction("Legend",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(3);
      }
    };
    addButton(toolbar,action,"Legend options");


//     toolbar.addSeparator();
//     icon = getImageIcon("Preferences24.gif");
//     action = new AbstractAction("Linestyle",icon) {
//       public void actionPerformed(ActionEvent e) {
// 	optionsAction(5);
//       }
//     };
//     addButton(toolbar,action,"Global style preferences");
    return toolbar;
  }
  
  /*
   * Extends the default toolbar with buttons used by
   * the more exotic Piper diagram
   * @param toolbar current toolbar
   */
  private JToolBar getPiperToolBar() {
    JToolBar toolbar = getToolBar();
    ImageIcon icon = getImageIcon("graph_piper.png");
    Action action = new AbstractAction("Show",icon) {
      public void actionPerformed(ActionEvent e) {
	showGraph(true);
      }
    };
    addButton(toolbar,action,"Show the current graph");

    toolbar.addSeparator();
    icon = getImageIcon("Axes24.png");
    action = new AbstractAction("Axes",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(0);
      }
    };
    addButton(toolbar,action,"Axes parameters");

    icon = getImageIcon("Scaling24.png");
    action = new AbstractAction("Axes",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(4);
      }
    };
    addButton(toolbar,action,"Piper diagram options");

    icon = getImageIcon("Labels24.png");
    action = new AbstractAction("Labels",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(2);
      }
    };
    addButton(toolbar,action,"Text and labels");

    icon = getImageIcon("Legend24.png");
    action = new AbstractAction("Legend",icon) {
      public void actionPerformed(ActionEvent e) {
	optionsAction(3);
      }
    };
    addButton(toolbar,action,"Legend options");

//     toolbar.addSeparator();
//     icon = getImageIcon("Preferences24.gif");
//     action = new AbstractAction("Linestyle",icon) {
//       public void actionPerformed(ActionEvent e) {
// 	optionsAction(5);
//       }
//     };
//     addButton(toolbar,action,"Global style preferences");
    return toolbar;
  }
  
  /**
   * Reads a data file in memory. There may be more than one datafile. 
   * Each data array (a series of X,Y points) are stored in a vector of
   * data arrays (of type DataArray). This vector is used to plot the 
   * data.
   * We're supposing that the file is structured according to
   * X, Y1, Y2, ... etc. All lines starting with # are ignored.
   */
  private boolean parseDatafile() {

// only if we read the file! 
   if (JHPlot.ReadFile) {
     if (plotPanels.size() == 0) {
      Utils.oops(frame,"No datafile loaded yet, open one first.");
      return false;
     }


    double x=0.0;
    double[] y;
    dataArrays.removeAllElements();
    int idx=0;
    for (Enumeration e=plotPanels.elements(); e.hasMoreElements(); idx++) {
      DataFile df = ((PanelPlot)e.nextElement()).getDataFile();
      if (debug) System.out.println(df.getName() + "...");
      if (!df.fillDataArrays(dataArrays,gs.getGraphType(),idx)) {
	return false;
      }
    }
    } // end reading files


    return true;

  }

  private void showGlobal() {
    if (plotPanels.size() > 0) {
      if (globalStyleChooser == null) {
	globalStyleChooser = new StyleChooser(jplot.frame, "Global style-chooser",
					      true);
      }
      int i = tabbedPane.getSelectedIndex();
      PanelPlot pp = (PanelPlot)plotPanels.get(i);
      LinePars init = pp.getDataFile().getLinePars(0);
      LinePars lp = globalStyleChooser.show(350,100,init);
      if (lp != null) {
	gs.setDataChanged(lp.dataChanged());
	pp.getDataFile().setLinePars(lp);
	updateGraphIfShowing();
	pp.update();
      }
    }
    else {
      Utils.oops(frame,"Select global settings of what?\nLoad a dataset and make a selection first.");
    }
  }

  /**
   * Displays the graph in a separate frame.  The first argument can
   * be set to false if the graph shouldn't be shown in a frame. This
   * is useful to make the graph before hand, e.g. to pre-define the
   * graph-parameters or to print the graph.
   * @param b flag, true if the graph must be shown.
   * @param toFront true if the graph must be pushed to the foreground.
   * @return true if the graph could be drawn.
   */
  public boolean showGraph(boolean b, boolean toFront) {
    if (debug) System.out.print("parsing datafile ");

    if (parseDatafile()) {
      if (dataArrays.size() > 0) {
	if (plotFrame == null) {
	  if (debug) {
	    System.out.println("going for " + dataArrays.size() + 
			       " data arrays");
	  }
	  if (b) {
	    plotFrame = new JFrame("JPlot");
            plotFrame.setPreferredSize(new java.awt.Dimension(600, 400));
	 //   plotFrame.setIconImage(getImageIcon("jplot16.png").getImage());
            ImageIcon icone = new ImageIcon(getClass().getClassLoader()
                                .getResource("jhplot/images/logo_jhepwork_24x24.jpg"));
             plotFrame.setIconImage(icone.getImage());
	
            JPanel p = new JPanel(new BorderLayout());
	    p.add(graph,BorderLayout.CENTER);
	    JToolBar tb = new JToolBar();
	    ImageIcon icon;
	    Action action;
	    SmallButton sb;
	    
	    icon = getImageIcon("exit.jpg");
	    action = new AbstractAction("Close",icon) {
		public void actionPerformed(ActionEvent e) {
		  dismissGraph();
		}
	      };
	    addButton(tb,action,"Close the graph");
	    
	    icon = getImageIcon("Refresh24.gif");
	    action = new AbstractAction("Refresh",icon) {
		public void actionPerformed(ActionEvent e) {
		  graph.show(dataArrays);
		}
	      };
	    addButton(tb,action,"Refresh the graph");
	    tb.addSeparator();
	    
	    icon = getImageIcon("Export24.gif");
	    action = new AbstractAction("Export",icon) {
		public void actionPerformed(ActionEvent e) {
		  exportImage();
		}
	      };
	    addButton(tb,action,"Export the graph");

	    icon = getImageIcon("Print24.gif");
	    action = new AbstractAction("Print",icon) {
		public void actionPerformed(ActionEvent e) {
		  printGraph();
		}
	      };
	    addButton(tb,action,"Print the graph");
	    
	    p.add(tb,BorderLayout.NORTH);
	    plotFrame.getContentPane().add(p);
	    plotFrame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		  dismissGraph();
		}
	      });
	    plotFrame.setLocation(xPos,yPos);
	    plotFrame.pack();
	    plotFrame.setVisible(true);
	    //plotFrame.toFront();
	  }
	  graph.show(dataArrays);
	}
	else {
	  if (b) {
	    plotFrame.setState(Frame.NORMAL);
	    if (toFront) {
	      plotFrame.setVisible(true);  // brings frame to the front
	    }
	  }
	  graph.show(dataArrays);
	}
	//gs.updatePlot = false;
      }
    }
    else return false;
    return true;
  }

  /**
   * Display the graph in a separate frame.
   * @param b flag, true if the graph must be shown.
   * @return true if the graph could be drawn, false otherwise.
   */
  public boolean showGraph(boolean b) {
    return showGraph(b,true);
  }

  /**
   * Display the graph in a separate frame.
   * @return true if the graph could be drawn, false otherwise.
   */
  public boolean showGraph() {
    return showGraph(true,true);
  }

  /**
   * Quits the application but asks confirmation if you didn't saved yet.
   * Asks for saving the script, if not yet done and allows
   * to cancel this drastic action.
   */
   public void quitDialog() {
    if (gs.dataChanged()) {
      int result = JOptionPane.showConfirmDialog(jplot,"The settings have changed, save project first?","",JOptionPane.YES_NO_CANCEL_OPTION);
      if (result == JOptionPane.YES_OPTION) {
	if (saveScript()) quit();
      }
      else if (result == JOptionPane.NO_OPTION) quit();
      else return;
    }
    else quit();
  }

  /**
   * Quits the app, really quits. Exists if this is a stand alone
   * instance. Otherwise we clear the graph and dispose the frame (can
   * be popped up later, but without data and with default settings).
   * S.Chekanov: I had to add many null statements. This is
   * really importnat due to memory leaks in this appls
   */
  public void quit() {

    if (graph != null) {
          graph.clear();
          graph=null;
    }

    clearData();
    clear();
    if (scalingPanel != null) scalingPanel=null;
    if (axesPanel != null) axesPanel=null; 
    piperPanel=null;
    export=null;
    labelPanel=null;
    legendPanel=null;
    styleChooser=null;
    globalStyleChooser=null;
    if (gs != null) gs=null;
    if (mainPanel != null) mainPanel=null;
    frame.dispose();
    if (isStandAlone) System.exit(1);
    System.gc(); 
  }

  /**
   * kills the graph frame, stops showing anything.
   */
  public void dismissGraph() {
    if (plotFrame != null) {
      plotFrame.dispose();
      plotFrame = null;
    }
  }

  private void printText() {
    if (scriptFile == null) return;
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Thread t = new Thread() {
	public void run() {
	  PrinterJob job = PrinterJob.getPrinterJob();
	  if (job != null) {
	    PageFormat pf = new PageFormat();
	    PrintFilePainter pfp = new PrintFilePainter(scriptFile.toString());
	    job.setPrintable(pfp,pf);
	    job.setCopies(1);
	    if (job.printDialog()) {
	      try {job.print();}
	      catch (Exception e) {}
	    }
	  }
	}
      };
    t.start();
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  /*
   * Opens a print-dialog for printing the graph on a printer.
   */
  private void printGraph() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Thread t = new Thread() {
	public void run() {
	  try {
	    PrinterJob prnJob = PrinterJob.getPrinterJob();
	    prnJob.setPrintable(graph);
	    if (prnJob.printDialog()) {
	      prnJob.print();
	    }
	  }
	  catch (PrinterException e) {
	    e.printStackTrace();
	  }
	}
      };
    t.start();
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Read a new datafile in.
   * This function pops up a dialog and asks for a filename. If
   * the file is exists (and contains data), then we have to make
   * a new 'plot' tabbedPane, with this new data in it.
   */
  private void chooseDatafile() {
    if (dataChooser == null) {
      dataChooser = new JFileChooser();
      try {
	String path = new File(".").getCanonicalPath();
	dataChooser.setCurrentDirectory(new File(path));
      }
      catch (IOException ex) {}
    }
    FileFilter ff1 = new DataFileFilter1();
    FileFilter ff2 = new DataFileFilter2();
    dataChooser.addChoosableFileFilter(ff1);
    dataChooser.addChoosableFileFilter(ff2);
    dataChooser.setFileFilter(ff1);
    if (dataChooser.showDialog(jplot,"Select") == 0) {      
      insertDatafile(dataChooser.getSelectedFile());
    }
  }

  private class DataFileFilter1 extends FileFilter {
    public boolean accept(File pathname) {
      return pathname.isDirectory() || 
	pathname.getName().endsWith(".res") ||
	pathname.getName().endsWith(".dat");
    }
    public String getDescription() {
      return "data files (*.res, *.dat)";
    }
  }

  private class DataFileFilter2 extends FileFilter {
    public boolean accept(File pathname) {
      return pathname.isDirectory() || 
	pathname.getName().endsWith(".ppr");
    }
    public String getDescription() {
      return "piper files (*.ppr)";
    }
  }





 /*
   * Read a new datafile in from BudufferedReader
   * This function pops up a dialog and asks for a filename. If
   * the file is exists (and contains data), then we have to make
   * a new 'plot' tabbedPane, with this new data in it.
   */
  public boolean insertDataReader(BufferedReader in, LinePars lp) {

    boolean res = true;
    JHPlot.ReadFile=true;

    
    DataArray data = new DataArray(dIndex,lp);
    res=data.parse(in);
    
   // data.print();
    
    if (res == false) {
      Utils.oops(frame,"Impossible to parse data ");
      res = false;
      return res;
    }
  
    insertData(dIndex,data);
    dIndex++;

  return res;

 }



  /*
   * Read a new datafile in.
   * This function pops up a dialog and asks for a filename. If
   * the file is exists (and contains data), then we have to make
   * a new 'plot' tabbedPane, with this new data in it.
   */
  public boolean insertDatafile(File file) {



    boolean res = true;
    JHPlot.ReadFile=true;

    if (!file.exists()) {
      Utils.oops(frame,"Impossible to open file " + file.toString() + 
		 " for reading!");
      res = false;
    }

/*
    else {
      DataFile df = new DataFile(file,plotPanels.size(),gs);
      
      if (plotPanels.size() > 0 && 
	  df.getGraphType() != gs.getGraphType()) {
	Utils.oops(frame,"Data of " + df.getName() + " belongs to another type.\nImpossible to merge, close the current dataset(s) first.");
	return false;
      }
      if (switchGraphType(df.getGraphType())) {

        // System.out.println("Swiched graph type"+gs); 
	// switching graphs resets all graph settings. This causes trouble 
	// if the dataset contains header info (such as labels) which are 
	// lost. We therefore have to re-read the dataset (if any) after 
	// the reset.
	df = new DataFile(file,plotPanels.size(),gs);
      }
      String s = "data(" + (plotPanels.size()+1) + ")";
      PlotPanel pp = new PlotPanel(jplot,df,gs);
      tabbedPane.insertTab(s,null,pp,null,plotPanels.size());
      tabbedPane.setSelectedIndex(plotPanels.size());
      plotPanels.add(pp);
      
      // launch ones the graph-initialization stuff so all the
      // option panels are initialized with the current data:
      showGraph(false);
    }

*/
    return res;
  }




 /*
   * replace the data marked by ndex_data 
   */
  public void replaceData( int index_data, DataArray data) {

      	dataArrays.setElementAt(data, index_data); 

   }



 /*
   * Clear the data array 
   */
  public void clearData() {
        plotPanels.clear();
        tabbedPane.removeAll();
        dataArrays.clear();
   }


/*
   * Remove i-th element of the data 
   */
  public void clearData(int i ) {
        if (i<plotPanels.size() ) { 
          plotPanels.remove(i);
          tabbedPane.remove(i);
          dataArrays.remove(i);
        }
   }

/*
   * Size of the data 
   */
  public int sizeData() {
        return  dataArrays.size();
   }



/*
   * replace LinePars for a set with the index  index_data
   */
  public void replaceLinePars( int index_data, LinePars lp) {

        DataArray current = (DataArray)dataArrays.elementAt( index_data  );
        current.setLinePars(  lp  );
        dataArrays.setElementAt(current, index_data);

   }


/*
   * Chekanov : get  GraphSettings  
   */
  public  GraphSettings getGS() {

   return this.gs;

   }




/*
   * Chekanov : set GraphSettings
   */
  public  void setGS (GraphSettings s)  {
   gs=s;
   updateGraphIfShowing();
   }



 /*
   * Insert new data 
   * This function pops up a dialog and asks for a filename. If
   * the file is exists (and contains data), then we have to make
   * a new 'plot' tabbedPane, with this new data in it.
   */
  public boolean insertData( int index_data, DataArray data) {



    File file = new File(Integer.toString(index_data));
    boolean res = true;
    if ( data  == null ) {
      Utils.oops(frame,"No data for reading!"); 
      res = false;
    }
    else {
 
     DataFile df = new DataFile(data,plotPanels.size(),gs);

      if (plotPanels.size() > 0 &&
          df.getGraphType() != gs.getGraphType()) {
        Utils.oops(frame,"Data of " + df.getName() + " belongs to another type.\nImpossible to merge, close the current dataset(s) first.");
        return false;
      }
      if (switchGraphType(df.getGraphType())) {

        // switching graphs resets all graph settings. This causes trouble
        // if the dataset contains header info (such as labels) which are
        // lost. We therefore have to re-read the dataset (if any) after
        // the reset.
        df = new DataFile(data,plotPanels.size(),gs);

      }
      String s = "data(" + (plotPanels.size()+1) + ")";

     
// get the data
      // LinePars current = (LinePars)data;
     // System.out.println(data.size());

      dataArrays.add(data);


      PanelPlot pp = new PanelPlot(jplot,df,gs);
      tabbedPane.insertTab(s,null,pp,null,plotPanels.size());
      pp.updateStyle(0,(LinePars)data); 
      tabbedPane.setSelectedIndex(plotPanels.size());
      plotPanels.add(pp);


      // launch ones the graph-initialization stuff so all the
      // option panels are initialized with the current data:
     showGraph(false);
 
  }
   return res;
  }




// get data 
  public Vector getDataArray() {
     return dataArrays;
 } 


  // get data at some index
  public DataArray getDataArray(int ind) {
     if (ind>dataArrays.size()) {
     System.out.println("Wrong index to get DataArray");
     return null;
     }
 
     return (DataArray)dataArrays.elementAt( ind );

   }



  // get selected data array
  public DataArray getDataArraySelected() {
         int i = tabbedPane.getSelectedIndex();
         return getDataArray(i);
   }





  /**
   * Remove a datafile from the memory and the GUI.
   * Once you can insert datafiles to the GUI, you must be able to
   * remove them as well, i.e. remove from memory but also remove the tab
   * with the selection. And ask for confirmation before actually doing it.
   */
  void removeDatafile() {
    boolean ret = true;
    int nFiles = plotPanels.size();
    int i = tabbedPane.getSelectedIndex();
    if (i == -1) return;
    if (i > nFiles) {
      if (nFiles > 0) {
	Utils.oops(frame,"Select first a data panel for removal");
      }
      else Utils.oops(frame,"Nothing to remove, no data loaded");
    }
    else {
      String txt = "Close " + tabbedPane.getTitleAt(i) + ", " + 
	((PanelPlot)plotPanels.get(i)).getDataFile().getFilenameWithoutPath() + "?";
      int res = JOptionPane.showConfirmDialog(jplot,txt,"",
					      JOptionPane.YES_NO_OPTION);
      if (res == JOptionPane.YES_OPTION) {
	
	// erase the data from the system:
	//--------------------------------
	tabbedPane.removeTabAt(i);
	plotPanels.removeElementAt(i);
	for (int k=0; k<nFiles-1; k++) {
	  tabbedPane.setTitleAt(k,"data(" + (k+1) + ")");
	  ((PanelPlot)plotPanels.get(k)).setDataFileIndex(k);
	}
	if (plotFrame != null) {
	  if (nFiles-1 == 0) dismissGraph();
	  else updateGraphIfShowing();
	}
      }
    }
  }

  /**
   * Exports the image to some graphic format.
   */
  private void exportImage() {
      jhplot.io.images.ExportVGraphics.exportDialog((Component)jplot,JPlot.class.getName(),jplot.frame);
  }
  
  /**
   * Saves the script. If no script file is specified, it pops up
   * a file-chooser to ask for a valid file
   * @return false if something went wrong, true otherwise.
   */
  private boolean saveScript() {
    boolean res = true;
    if (scriptFile == null) {
      if (!saveAsScript()) res = false;
    }
    else if (!writeScript(scriptFile)) {
      res = false;
      scriptFile = null;
    }
    if (res) {
      gs.setDataChanged(false);
      if (scriptFile != null) gs.setTitleName(scriptFile.toString());
    }
    return res;
  }

  private class InputFileFilter extends FileFilter
  {
    public boolean accept(File pathname) {
      return pathname.isDirectory() || 
	pathname.getName().endsWith(".jpt");
    }
    public String getDescription() {
      return "JPlot scripts (*.jpt)";
    }
  }
    
  /**
   * Saves-as dialog. Pops up a dialog and asks for a valid
   * filename in order to save the current script.
   * @return false if the user canceled, true otherwise.
   */
  private boolean saveAsScript() {
    boolean ret = true;
    JFileChooser chooser = new JFileChooser(new File("."));
    FileFilter ff = new InputFileFilter();
    chooser.addChoosableFileFilter(ff);
    chooser.setFileFilter(ff);
    if (chooser.showDialog(jplot,"Save As") == 0) {
      scriptFile = chooser.getSelectedFile();
      if (scriptFile == null) return false;
      else if (scriptFile.exists()) {
	int res = JOptionPane.showConfirmDialog(jplot,
						"The file exist: do you want to overwrite the file?","",JOptionPane.YES_NO_OPTION);
	if (res == JOptionPane.NO_OPTION) ret = false;
      }
      if (ret) if (!writeScript(scriptFile)) ret = false;
    }
    return ret;
  }

  /**
   * Writes the script to a file.
   * @param f file to which to save the script
   * @return false if something went wrong, true otherwise.
   */
  public  boolean writeScript(File f) {
    boolean res = true;
    XMLWrite xw = new XMLWrite();
    xw.open("jplot");         // start of writing jplot settings
    xw.setData("look-and-feel",currentLookAndFeel);
    
    // write the settings for each datafile:
    int nFiles = plotPanels.size();
    for (int k=0; k<nFiles; k++) {
      ((PanelPlot)plotPanels.get(k)).getDataFile().getSettings(xw,null);
    }

    // write the global graph-settings:
    gs.getSettings(xw);
    xw.close();    // end of writing jplot settings:
    
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(f));
      pw.println(xw.getSettings());
      xw.clear();
      pw.close();
    }
    catch (IOException e) {
      Utils.bummer(frame,"It's somehow impossible to write to " + f.toString());      res = false;
    }
    return res;
  }

  /**
   * Opens a script, popping up a file-chooser to ask for a file
   */
  private void openScript() {
    JFileChooser chooser = new JFileChooser(new File("."));
    FileFilter ff = new InputFileFilter();
    chooser.addChoosableFileFilter(ff);
    chooser.setFileFilter(ff);
    if (chooser.showOpenDialog(jplot) == 0) {
      if (gs.dataChanged()) {
	int result = JOptionPane.showConfirmDialog(jplot,"Settings have changed, save the current project first?");
	if (result == JOptionPane.CANCEL_OPTION) return;
	else if (result == JOptionPane.YES_OPTION) if (!saveScript()) return;
      }
      scriptFile = chooser.getSelectedFile();
      if (scriptFile.exists()) loadScript();
      else scriptFile = null;
    }
  }



/**
   * Load the script from a file in memory after heaving cleared the
   * actual data.
   * TODO: should be threaded.
   */
   public   void readProject(String ffile) {

    scriptFile =new File(ffile);
    loadScript();

    }
 
  /**
   * Load the script from a file in memory after heaving cleared the 
   * actual data. 
   * TODO: should be threaded.
   */
  private  void loadScript() {
    if (!scriptFile.exists() || !scriptFile.canRead()) {
      Utils.bummer(frame,"Can't read from " + scriptFile);
      return;
    }

    // All the settings will be read by the XMLRead toolbox:
    XMLRead xr = new XMLRead();

    // clear the current stuff:
    clear();

    // read all lines concerning jplot, i.e between <jplot> and </jplot>:
    if (!xr.parse(scriptFile,"jplot")) return;

    //xr.printVector();

    String laf = currentLookAndFeel;
    laf = xr.getString("look-and-feel",laf);

    // update the datafile settings
    PanelPlot pp=null;
    int k=1;
    while (xr.open("datafile")) {
      if (insertDatafile(new File(xr.getString("name","")))) {
	pp = (PanelPlot)plotPanels.get(plotPanels.size()-1);
	pp.getDataFile().updateSettings(xr);
	pp.update();
      }
      xr.close();
      xr.hide("datafile");
    }
    xr.unHide();

    // update the global graph-settings:
    gs.updateSettings(xr);

    gs.setTitleName(scriptFile.toString());
    gs.setDataChanged(false);

    // Synchronisation problems since some panels are not yet made
    // updateLookAndFeel(laf); 

    // showing the graph immediately after reading the script leads
    // to weird results, i.e. black screen etc.
    showGraph(true);
  }

  /**
   * Parse a string which contains parameter settings.
   * This string contains keywords and values, separated by a ';'
   * @param str string containing the settings.
   */

/*
  public void parseScriptInString(String str) {
    if (str == null || str.equals("")) return;
    StringTokenizer st = new StringTokenizer(str,";");
    LinePars lp = null;
    PlotPanel pp = null;
    int lpi = -1;
    boolean inDataFile = false;
    boolean inPlotStyle = false;
    while (st.hasMoreTokens()) {
      StringTokenizer s = new StringTokenizer(st.nextToken()," \t,");
      String t = s.nextToken();

      // parse a closing '}':
      if (t.equals("}")) {
	if (inPlotStyle) {
	  pp.getDataFile().addColumn(lpi,lp);
	  inPlotStyle = false;
	}
	else if (inDataFile) {
	  pp.update();
	  inDataFile = false;
	}
      }

      // parse a datafile block:
      else if (t.startsWith("da")) {
	inDataFile = true;
	if (!insertDatafile(new File(s.nextToken()),false)) break;
	pp = (PlotPanel)plotPanels.get(plotPanels.size()-1);
	pp.clearSelection();
      }

      // selection, i.e. 'selection = 1,2,5,8':
      else if (t.startsWith("se")) {
	s.nextToken();  // eat '='
	pp.getDataFile().setXColumn(Integer.parseInt(s.nextToken()));
      }
      // parse stuff like 'plotstyle of 1 {':
      else if (t.startsWith("pl")) {
	s.nextToken();  // eat 'of'
	lpi = Integer.parseInt(s.nextToken());
	lp = new LinePars();
	inPlotStyle = true;
      }
      // parse stuff like 'style = <graphstyle>'
      else if (t.startsWith("st")) {
	s.nextToken();  // eat '='	
	lp.setGraphStyle(Integer.parseInt(s.nextToken()));
      }
      // parse stuff like 'line = <dashlength>,<penwidth>'
      else if (t.startsWith("li")) {
	s.nextToken();  // eat '='
	int k = Integer.parseInt(s.nextToken());
	lp.setDashLength(gs.dashLengths[k],k);
	lp.setPenWidth(Float.parseFloat(s.nextToken()));
	lp.setDrawLine(Boolean.valueOf(s.nextToken()).booleanValue());
      }
      // parse stuff like 'point = <symbol>,<size>,<plotfreq>,<show>'
      else if (t.startsWith("po")) {
	s.nextToken();  // eat '='
	lp.setSymbol(Integer.parseInt(s.nextToken()));
	lp.setSymbolSize(Float.parseFloat(s.nextToken()));
	lp.setPointFrequency(Integer.parseInt(s.nextToken()));
	lp.setDrawSymbol(Boolean.valueOf(s.nextToken()).booleanValue());
      }
      // parse stuff like 'color = <red>,<green>,<blue>'
      else if (t.startsWith("co")) {
	s.nextToken();  // eat '='
	lp.setColor(new Color(Integer.parseInt(s.nextToken()),
			      Integer.parseInt(s.nextToken()),
			      Integer.parseInt(s.nextToken())));
      }
      // parse stuff like 'legend = <legend>,<show>'
      else if (t.startsWith("le")) {
	s.nextToken();  // eat '='
	String n = s.nextToken();
	if (n.startsWith("\"")) {  // is name between " and "
	  while (!n.endsWith("\"")) n += " " + s.nextToken();
	  n = n.substring(1,n.length()-1);
	}
	lp.setName(n);
	lp.setDrawLegend(Boolean.valueOf(s.nextToken()).booleanValue());
      }
      // parse stuff like 'scaling = <multiplier>,<additioner>'
      else if (t.startsWith("sc")) {
	s.nextToken();  // eat '='
	lp.setMultiplier(Float.parseFloat(s.nextToken()));
	lp.setAdditioner(Float.parseFloat(s.nextToken()));
      }
      else if (t.startsWith("graph")) break;
    }
    gs.updateSettings(str);
  }
*/

  /**
   * Refreshes the actual graph with new data. Updates the graph only
   * if showing. This function first checks whether the file
   * 'filename' is already present in the system. If not, that's
   * simple, the file is added. If it is already present, then we must
   * replace the file, the difficulty is to preserve, as much as
   * possible, the former selection.
   * @param fn name of the file with data for JPlot.  */
  public void update(String fn) {
    if (fn.equals("") || fn == null) {
      clear();
      return;
    }
    boolean found = false;
    for (Enumeration e=plotPanels.elements(); e.hasMoreElements();) {
      PanelPlot pp = (PanelPlot) e.nextElement();
      DataFile df = pp.getDataFile();
      
      if (df.getName().equals(fn) || 
	  df.getFilenameWithoutPath().equals(fn)) {
	
	// datafile exists already. Now check if all the
	// column names are identical:
	//----------------------------------------------
	pp.reload();
	found = true;
	break;
      }
    }
    if (!found) insertDatafile(new File(fn));
    updateGraphIfShowing();
  }

  /**
   * Toggles timer which updates the graph every second.
   * <b>TODO</b> This function needs to be amended to update only
   * if the data actually changed.
   */
  public void setAutoUpdate(boolean b) {
    mi_autoUpdate.setSelected(b);
    if (b) {
      if (timer == null) {
	timer = new javax.swing.Timer(autoUpdateInterval,new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      for (Enumeration e1=plotPanels.elements(); e1.hasMoreElements();) {
		DataFile df = ((PanelPlot) e1.nextElement()).getDataFile();
		//System.out.println("Checking date of " + df.getFile());
		File tmp = new File(df.getFile().toString());
		if (tmp.lastModified() > df.getLastModified()) {
		  if (jplot.debug) System.out.println(df.getFile() + " modified, updating graph...");
		  updateGraphIfShowing(false);
		  df.setLastModified(tmp.lastModified());
		}
	      }
	    }
	  });
      }
      timer.start();
    }
    else if (timer != null) timer.stop();
  }
  
  /**
   * Sets the location at which we put the graph
   * @param x x-position in pixels
   * @param y y-position in pixels
   */
  public void setGraphLocation(int x, int y) {
    xPos = x;
    yPos = y;
  }

  /**
   * Refreshes the actual graph with new data. This function calls
   * first 'update' which updates the graph if showing. If the 
   * graph isn't showing, we also popup the graph.
   * @param fn name of the file with data for JPlot.
   */
  public void show(String fn) {
    if (isStandAlone) {
      frame.setState(Frame.NORMAL);  // needed ?!! at least for 1.3.1...
      frame.setVisible(true);
    }
    update(fn);
    if (plotFrame == null) showGraph(true);
  }

  /**
   * Clears the plot-panel, kills eventual plot frames
   */
  public void clear() {
    if (plotFrame != null) dismissGraph();

    if (plotPanels.size() > 0) plotPanels.removeAllElements();
    if (tabbedPane.getTabCount() > 0) tabbedPane.removeAll();

    for (Enumeration e=plotPanels.elements(); e.hasMoreElements();) {
      ((PanelPlot)plotPanels.get(0)).clearSelection();
      //plotPanels.removeElementAt(0);
      if (tabbedPane != null && tabbedPane.getTabCount() > 0) {
	tabbedPane.removeTabAt(0);
      }
    }
    gs.reset();
    repaint();
  }



// get all plot panels

  public Vector getAllPanels() {

  return plotPanels;
  }
 

  /**
   * A utility function that layers on top of the LookAndFeel's
   * isSupportedLookAndFeel() method. Returns true if the LookAndFeel
   * is supported. Returns false if the LookAndFeel is not supported
   * and/or if there is any kind of error checking if the LookAndFeel
   * is supported.
   *
   * The L&F menu will use this method to detemine whether the various
   * L&F options should be active or inactive.
   */
  protected boolean isAvailableLookAndFeel(String laf) {
    if (debug) return true;
    try { 
      Class lnfClass = Class.forName(laf);
      LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
      return newLAF.isSupportedLookAndFeel();
    } catch(Exception e) { // If ANYTHING weird happens, return false
      return false;
    }
  }

  /**
   * Sets the current L&F
   */
  public void updateLookAndFeel(String laf) {
    if (currentLookAndFeel != laf) {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      currentLookAndFeel = laf;
      try {
	UIManager.setLookAndFeel(currentLookAndFeel);
	Frame[] frames = frame.getFrames();
	for (int i=0; i<frames.length; i++) {
	  SwingUtilities.updateComponentTreeUI(frames[i]);
	  Window[] windows = frames[i].getOwnedWindows();
	  for (int j=0; j<windows.length; j++) {
	    SwingUtilities.updateComponentTreeUI(windows[j]);
	  }
	}
      }
      catch (Exception ex) {
	String s;
	if (currentLookAndFeel.equals(macLaf)) s = "Macintosh Look & Feel";
	else if (currentLookAndFeel.equals(metalLaf)) s = "Java Look & Feel";
	else if (currentLookAndFeel.equals(motifLaf)) s = "Motif Look & Feel";
	else if (currentLookAndFeel.equals(gtkLaf)) s = "GTK+ Look & Feel";
	else s = "Windows Look & Feel";
	Utils.oops(frame,"Couldn't load the " + s + ".\nIt's probably not available for this platform (copyright problem?).");
	setCursor(Cursor.getDefaultCursor());
	return;
      }
      mi_mac.setSelected(false);
      mi_java.setSelected(false);
      mi_motif.setSelected(false);
      mi_windows.setSelected(false);
      mi_gtk.setSelected(false);
      if (currentLookAndFeel.equals(macLaf)) mi_mac.setSelected(true);
      else if (currentLookAndFeel.equals(windowsLaf)) mi_windows.setSelected(true);
      else if (currentLookAndFeel.equals(motifLaf)) mi_motif.setSelected(true);
      else if (currentLookAndFeel.equals(gtkLaf)) mi_gtk.setSelected(true);
      else mi_java.setSelected(true);
      setCursor(Cursor.getDefaultCursor());
      gs.setDataChanged(true);
    }
  }


  /**
   * Main for using ChPlot as a stand-alone application. Takes care
   * of command line arguments:
   * -i <scriptfile>   : reads a configuration (script) file
   * Note: all arguments without a '-' are considered to be data files.
   */

/*
  static public void main(String[] args) {
    boolean showNow=false;
    boolean autoUpdate=false;

    // default look and feel defined by the platform. Use GTK+ L&F
    // as the default for unix (personal taste...).
    try {
      String lf = UIManager.getSystemLookAndFeelClassName();
//       if (lf.equals(motifLaf) || lf.equals(metalLaf)) {
// 	lf = gtkLaf;
// 	UIManager.setLookAndFeel(lf);
//       }
      if (lf.equals(motifLaf)) {
	lf = metalLaf;
	UIManager.setLookAndFeel(lf);
      }
      currentLookAndFeel = lf;
    }
    catch (Exception e) {
      System.out.println("Error loading L&F " + e);
    }

    File scriptFile=null;
    Vector files = new Vector();
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
	if (args[i].equals("-i")) {
	  scriptFile = new File(args[++i]);
	}
	else if (args[i].equals("-s")) {
	  showNow = true;
	}
	else if (args[i].equals("-u")) {
	  autoUpdate = true;
	}
	else Utils.bummer(null,"Unknown option " + args[i]);
      } 
      else {
	File file = new File(args[i]);
	if (!file.exists()) {
	  Utils.oops(null,"File '" + file.toString() + "' wasn't found anywhere");
	  file = null;
	}
	files.add(file);
      }
    }
    JFrame frame = new JFrame("JPlot");
    jp = new JPlot(frame,files,scriptFile,true);
    frame.getContentPane().add(jp);

    // quit properly if someone closes the window via the frame
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	jp.quitDialog();
      }
    });

    frame.setIconImage(jp.getImageIcon("jplot16.png").getImage());
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setSize(WIDTH,HEIGHT);
    frame.setLocation(screenSize.width/2-WIDTH/2,screenSize.height/2-HEIGHT/2);
    frame.setVisible(true);
    if (showNow) jp.showGraph(true);
    if (autoUpdate) jp.setAutoUpdate(true);
  }
*/

}
