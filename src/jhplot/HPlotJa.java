// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de).
// * All rights reserved.

package jhplot;

import hep.aida.IAnalysisFactory;
import hep.aida.IAxis;
import hep.aida.IFitFactory;
import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunction;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import hep.aida.ref.histogram.Cloud1D;
import hep.aida.ref.histogram.Cloud2D;
import hep.aida.ref.histogram.Histogram1D;

import japlot.Global;
import japlot.jaxodraw.JaxoDraw;
import japlot.jaxodraw.JaxoGraph;
import japlot.jaxodraw.JaxoMainPanel;
import japlot.jaxodraw.JaxoPrefs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.*;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import jhplot.gui.HelpBrowser;
import jhplot.jadraw.JaAxes;
import jhplot.jadraw.JaBox;
import jhplot.jadraw.JaKey;
import jhplot.jadraw.JaLine;
import jhplot.jadraw.JaObject;
import jhplot.jadraw.JaText;
import jhplot.jadraw.JaTextBox;

import jplot.DataArray;
import jplot.LinePars;
import jplot.Translate;

/**
 * A canvas to display all graphical DataMelt objects. Can be
 * used for drawing Feynman diagrams  and plot  H1D, P1D, F1D  and H2D objects.
 * It is analogies to HPlot canvas but all plotted objects are much more interactive.
 * It also include interactive editor to edit plots or show some graphics.
 *  
 * @see jhplot.HPlot 
 * 
 * @author S.Chekanov (ANL)
 * 
 */

public class HPlotJa extends JComponent {

	/**
	 * 
	 */

	public static final int LabelX = 1;
	public static final int LabelY = 2;
	public static final int Title = 3;
	public static final int StatBox = 4;
	public static final int Exponent = 5;
	public static final int Key = 6;

	protected static int isOpen = 0;

	private JaText gTitle;
	private static final long serialVersionUID = 1L;
	private static java.util.ResourceBundle language;
	private final int X = 0;
	private final int Y = 1;

	private double MarginLeft = 0.1;
	private double MarginRight = 0.1;
	private double MarginTop = 0.1;
	private double MarginBottom = 0.1;
	private double MarginX = 0.12;
	private double MarginY = 0.12;
	private DecimalFormat dfb = new DecimalFormat("##.###E00");
	protected JaAxes[][] ja;
	private static String inputFile = null;
	private JaText[][] labelX;
	private JaText[][] labelY;
	private JaTextBox[][] statbox;

	private boolean[][] showStatBox;
	private ArrayList<JaObject> initList = null;

	private JaxoMainPanel feyn;

	private IAnalysisFactory m_IAnalysisFactory = null;

	private IHistogramFactory m_IHistogramFactory = null;

	private ITree m_ITree = null;

	private IFitFactory m_IFitFactory = null;

	private IFunctionFactory m_IFunctionFactory = null;

	// this version of japlot.jaxodraw

	/** The version number of this release of japlot.jaxodraw. */
	public static final String VERSION_NUMBER = "1.0";

	/** The current version of japlot.jaxodraw. */
	public static final String VERSION = "japlot.jaxodraw-" + VERSION_NUMBER;

	/** The japlot.jaxodraw web site. */
	public static final String WEB_SITE = "http://jaxodraw.sourceforge.net";

	// System properties

	/** The current user. */
	public static final String USER_NAME = System.getProperty("user.name");

	/** The current user's home directory. */
	public static final String USER_HOME = System.getProperty("user.home");

	/** The name of the current operating system. */
	public static final String OS_NAME = System.getProperty("os.name");

	/** The architecture of the current operating system. */
	public static final String OS_ARCH = System.getProperty("os.arch");

	/** The version of the current operating system. */
	public static final String OS_VERSION = System.getProperty("os.version");

	/** The current Java version. */
	public static final String JAVA_VERSION = System
			.getProperty("java.version");

	/** The current Java runtime version. */
	public static final String JAVA_RUNTIME_VERSION = System
			.getProperty("java.runtime.version");

	/** The directory where Java is installed on the current machine. */
	public static final String JAVA_HOME = System.getProperty("java.home");

	/** The current Java class path. */
	public static final String JAVA_CLASSPATH = System
			.getProperty("java.class.path");

	/** The file where user-selected preferences will be stored. */
	public static String PREFS_FILENAME = USER_HOME + java.io.File.separator
			+ ".jaPlotrc";

	/** The current directory. */
	private static String curDIR = "";

	private int N1final; // final

	private int N2final;

	private int N1; // current

	private int N2;

	private int xsize = 800; // current

	private int ysize = 650;

	private double keyspace;

	private boolean isSet = false;

	final private Font fontLabel = new Font("Arial", Font.BOLD, 18);
	// final private Font fontTitle = new Font("Arial", Font.BOLD, 24);

	// private static JaxoGraph firstGraph;

	private static double[][] lastKeyLocation;

	/**
	 * Create HPlotJa canvas with several pads.
	 * 
	 * @param title
	 *            Title
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 * @param n1
	 *            number of plots/graphs in x
	 * @param n2
	 *            number of plots/graphs in y
	 * @param setgraph
	 *            true if initials graph with axes should be set Set to false if
	 *            you want just empty canvas.
	 */

	public HPlotJa(String title, int xsize, int ysize, int n1, int n2,
			boolean setgraph) {

		this.xsize = xsize;
		this.ysize = ysize;

		// check the screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int Sheight = screenSize.height;
		int Swidth = screenSize.width;
		if (Swidth < this.xsize || Sheight < this.ysize) {
			this.xsize = (int) (0.5 * Swidth);
			this.ysize = (int) (0.5 * Sheight);
		}

		isSet = setgraph;
		// file with preferences
		PREFS_FILENAME = USER_HOME + java.io.File.separator + ".jehep"
				+ java.io.File.separator + "japlot.pref";
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("windows") > -1 || OS.indexOf("nt") > -1) {
			PREFS_FILENAME = USER_HOME + java.io.File.separator + "jehep"
					+ java.io.File.separator + "japlot.pref";
		}

		N1 = 0;
		N2 = 0;
		// protection
		if (n1 == 0 || n2 == 0) {
			n1 = 1;
			n2 = 1;
		}

		language = java.util.ResourceBundle.getBundle(JaxoPrefs
				.getPref(JaxoPrefs.PREF_LANGUAGE));

		getUserDir();
		// Preferences are read first so that command line arguments
		getPreferences();
		// change preferences for size
		JaxoPrefs.setIntPref(JaxoPrefs.PREF_SCREENSIZEX, xsize);
		JaxoPrefs.setIntPref(JaxoPrefs.PREF_SCREENSIZEY, ysize);

		// this is the main program
		Global.init(xsize, ysize);

		initList = new ArrayList<JaObject>();

		feyn = new JaxoMainPanel(this);
		if (setgraph == true)
			setGraph(n1, n2);

		feyn.init(inputFile, new JaxoGraph(initList));
		feyn.setTitle(title);
		feyn.pack();
		// feyn.setVisible(true);
		if (!JaxoPrefs.getBooleanPref(JaxoPrefs.PREF_GRIDONOFF)) {
			feyn.gridOn(false);
		}

		// read info
		JHPlot.readInfo();
	}

	/**
	 * Set axes frames or pads. Before creating pad, clear the canvas, i.e. call
	 * clearAll(0 and clearData(). One should also call update() to show the
	 * pads.
	 * 
	 * @param n1
	 *            number of pads in X
	 * @param n2
	 *            number of pads in Y
	 **/

	public void setAxesFrame(int n1, int n2) {

		initList.clear();
		setGraph(n1, n2);
		if (!JaxoPrefs.getBooleanPref(JaxoPrefs.PREF_GRIDONOFF)) {
			feyn.gridOn(false);
		}

	}

	/**
	 * Build axes frames and show them. In fact, it calls the methods:
	 * clearAllData(); clearAll(); setAxesFrame(n1,n2); update();.
	 * 
	 * @param n1
	 *            number of pads in X
	 * @param n2
	 *            number of pads in Y
	 **/
	public void buildPads(int n1, int n2) {
		clearAllData();
		clearAll();
		setAxesFrame(n1, n2);
		update();
	}

	/**
	 * Create HPlotJa canvas with several pads and axes.
	 * 
	 * @param title
	 *            Title
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 * @param n1
	 *            number of plots/graphs in x
	 * @param n2
	 *            number of plots/graphs in y
	 */

	public HPlotJa(String title, int xsize, int ysize, int n1, int n2) {
		this(title, xsize, ysize, n1, n2, true);

	}

	
	
	
	
	/**
	 * Navigate to a selected plot. This is necessary if there are a several
	 * plots on the same canvas.
	 * 
	 * @param cols
	 *            Set the location of the current plot in x
	 * @param rows
	 *            Set the location of the current plot in y
	 */
	public void cd(int cols, int rows) {

		/*
		 * System.out.println(" "); System.out.println(N1final);
		 * System.out.println(N1); System.out.println(N2final);
		 * System.out.println(N2);
		 */

		N1 = cols - 1;
		N2 = rows - 1;
		if (cols > N1final || rows > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			// System.out.println("Wrong number of canvas in cd() method");
			// System.out.println("Increase the number of canvas in HPlot!");
			N1 = 0;
			N2 = 0;
			return;
		}

	}

	/**
	 * Get location of the graph in the main canvas in X
	 * 
	 * @return location of the graph in X
	 */
	public int getCdX() {

		return N1;

	}

	/**
	 * Get the total number of graphs in X
	 * 
	 * @return Total number of graphs in X
	 */
	public int getNtotX() {

		return N1final;

	}

	/**
	 * Get the total number of the graphs in Y
	 * 
	 * @return Total number of graphs in Y
	 */
	public int getNtotY() {

		return N2final;

	}

	/**
	 * Get location of the graph on the main canvas in Y
	 * 
	 * @return location in Y
	 */

	public int getCdY() {

		return N2;

	}

	
	/**
	 * Construct a  canvas with a single plot region. 
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size of canvas in x
	 * @param ys
	 *            size of canvas in y
	 * 
	 */
	public HPlotJa(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);

	}
	

         /**
         * Construct a japlot.jaxodraw canvas with a plot with the default
         * parameters 600 by 400, and 10% space for the global title
         * 
         */
        public HPlotJa() {

                this("HPlotJa", 600, 400, 1, 1,true);

        }
	

	/**
	 * Construct a japlot.jaxodraw canvas with a plot with the default
	 * parameters 600 by 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HPlotJa(String title) {

		this(title, 600, 400, 1, 1);

	}

	/**
	 * Sets the actual graph
	 */
	private void setGraph(int N1f, int N2f) {

		this.N1final = N1f;
		this.N2final = N2f;

		isSet = true;
		ja = new JaAxes[N1final][N2final];
		labelX = new JaText[N1final][N2final];
		labelY = new JaText[N1final][N2final];
		statbox = new JaTextBox[N1final][N2final];
		showStatBox = new boolean[N1final][N2final];

		// padX = new double[N1final][N2final];
		// padY = new double[N1final][N2final];
		// padWidth = new double[N1final][N2final];
		// padHight = new double[N1final][N2final];
		lastKeyLocation = new double[N1final][N2final];

		String[] stats = new String[] { "UNDEFINED" };
		double x1 = MarginLeft + MarginRight;
		double y1 = MarginTop + MarginBottom;
		double cwidth = (1.0 - x1 - MarginX * (N1final - 1)) / (double) N1final;
		double chight = (1.0 - y1 - MarginY * (N2final - 1)) / (double) N2final;
		if (N1final == 1)
			cwidth = 1.0 - x1;
		if (N2final == 1)
			chight = 1.0 - y1;

		double y = MarginTop;
		for (int i2 = 0; i2 < N2final; i2++) {

			double x = MarginLeft;
			for (int i1 = 0; i1 < N1final; i1++) {
				ja[i1][i2] = new JaAxes(i1, i2);
				// location of upper left corner
				// System.out.println(x);
				// System.out.println(y);
				ja[i1][i2].setLocation(x, y, "NDC");
				ja[i1][i2].setRelWH(cwidth, chight, "NDC");
				ja[i1][i2].setColor(Color.black);
				ja[i1][i2].setFillColor(Color.white);
                                // ja[i1][i2].setBackgroundColor(Color.white);
				ja[i1][i2].setStroke(2.0f);

				// remember pad locations
				// padX[i1][i2] = x;
				// padY[i1][i2] = y;
				// padWidth[i1][i2] = cwidth;
				// padHight[i1][i2] = chight;
				lastKeyLocation[i1][i2] = 0;
				// labels

				// X label
				labelX[i1][i2] = new JaText();
				labelX[i1][i2].setPadX(i1);
				labelX[i1][i2].setPadY(i2);
				labelX[i1][i2].setType(LabelX);
				labelX[i1][i2].setFont(fontLabel);
				labelX[i1][i2].setColor(Color.black);
				setLabelX("X", i1, i2);

				// set stat box
				showStatBox[i1][i2] = true;
				statbox[i1][i2] = new JaTextBox();
				statbox[i1][i2].setPadX(i1);
				statbox[i1][i2].setPadY(i2);
				statbox[i1][i2].setType(StatBox);
				statbox[i1][i2].setFont(new Font("Dialog", Font.ITALIC, 10));
				statbox[i1][i2].setColor(Color.black);
				// set undefined
				setStatBox(stats, i1, i2);

				// Y label
				labelY[i1][i2] = new JaText();
				labelY[i1][i2].setPadX(i1);
				labelY[i1][i2].setPadY(i2);
				labelY[i1][i2].setType(LabelY);
				labelY[i1][i2].setFont(fontLabel);
				labelY[i1][i2].setColor(Color.black);
				setLabelY("Y", i1, i2);

				initList.add(ja[i1][i2]);
				initList.add(labelX[i1][i2]);
				initList.add(labelY[i1][i2]);
				initList.add(statbox[i1][i2]);

				x = x + (cwidth + MarginX);
			}

			y = y + (chight + MarginY);
		}

	}


	/**
	 * Get the current JaAxes frame
	 * 
	 * @return frame
	 */

	public JaAxes getJaAxes() {
		return ja[N1][N2];
	}

	/**
	 * Set to a contour style
	 * 
	 * @param contour
	 *            set to true for contour style
	 */
	public void setContour(boolean contour) {

		ja[N1][N2].setContour(contour);
	}

	/**
	 * Show a bar indicating levels on contour plot
	 * 
	 * @param bar
	 *            true if the bar is shown
	 */
	public void setContourBar(boolean bar) {

		ja[N1][N2].setContourBar(bar);
	}

	/**
	 * Number of contour levels
	 * 
	 * @param levels
	 *            number of contour levels
	 */
	public void setContourLevels(int levels) {

		ja[N1][N2].setContourLevels(levels);
	}

	/**
	 * Number of contour levels
	 * 
	 * @param levels
	 *            number of contour levels
	 */
	public int getContourLevels() {

		return ja[N1][N2].getContourLevels();
	}

	/**
	 * Show a bar indicating levels on contour plot
	 * 
	 * @param bar
	 *            true if the bar is shown
	 */
	public boolean isContourBar() {

		return ja[N1][N2].isContourBar();
	}

	/**
	 * Set number of contour bins in X and Y
	 * 
	 * @param number
	 *            of bins in X
	 * @param number
	 *            of bins in Y
	 */
	public void setContourBins(int nX, int nY) {

		ja[N1][N2].setContourBins(nX, nY);
	}

	/**
	 * is contour plot?
	 * 
	 * @return
	 */
	public boolean isContour() {
		return ja[N1][N2].isContour();

	}

	/**
	 * Number of contour bins in X
	 * 
	 * @return
	 */
	public int getContourBinsX() {
		return ja[N1][N2].getContourBinX();

	}

	/**
	 * Number of contour bins in Y
	 * 
	 * @return
	 */
	public int getContourBinsY() {
		return ja[N1][N2].getContourBinY();

	}

	/**
	 * find proper location for this object and update coordinates
	 */

	private void findLocation(JaObject ob) {
		if (ob == null)
			return;

		// object is in the user coordinate
		if (ob.isUser()) {
			double x = ob.getXuser();
			double y = ob.getYuser();
			double w = ob.getRelWuser();
			double h = ob.getRelHuser();
			// move to pixel coordinates of the current frame
			int x1 = ja[N1][N2].toX(x);
			int y1 = ja[N1][N2].toY(y);
			int x2 = ja[N1][N2].toX(w);
			int y2 = ja[N1][N2].toY(h);

			if (ob instanceof JaLine) {
				((JaLine) ob).setLocationXY(x1, y1, x2, y2);

			} else if (ob instanceof JaBox) {
				((JaBox) ob).setLocationXY(x1, y1, x2, y2);
			} else {

				ob.setX(x1);
				ob.setY(y1);
				ob.setRelw(x2);
				ob.setRelh(y2);
			}

		} else {
			ob.updateCoor();
		}

	}

	/**
	 * Add an object to the frame. Typically, you will need to call "update" to
	 * trigger redraw.
	 * 
	 * @param ob
	 *            input object
	 */

	public void add(JaObject ob) {

		if (ob == null)
			return;
		findLocation(ob);

		feyn.getGraph().addObject(ob);
		feyn.getGraph().setSaved(true);
	}

	/**
	 * Add an object to background. Typically, you will need to call "update" to
	 * trigger redraw.
	 * 
	 * @param ob
	 *            input object
	 */

	public void addToBack(JaObject ob) {

		if (ob == null)
			return;
		findLocation(ob);
		feyn.getGraph().background(ob);
		feyn.getGraph().setSaved(true);
	}

	/**
	 * Add an object to foreground.
	 * 
	 * @param ob
	 *            input object
	 */

	public void addToFront(JaObject ob) {

		if (ob == null)
			return;
		findLocation(ob);
		feyn.getGraph().foreground(ob);
		feyn.getGraph().setSaved(true);
	}

	/**
	 * Clear the canvas from all objects
	 * 
	 */
	public void clearAll() {
		feyn.getGraph().clearAll();
	}

	/**
	 * Clear all data from all graphs.
	 */

	public void clearAllData() {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				clearData(i1 + 1, i2 + 1);
			}
		}
	}

	/**
	 * Clear the axis frame from the data
	 * 
	 * @param padx
	 *            No of pads in X
	 * @param pady
	 *            No of pads in Y
	 */

	public void clearData(int padx, int pady) {

		if (isSet == false)
			return;

		int n1 = padx - 1;
		int n2 = pady - 1;

		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}

		// clear vector with DataArray
		ja[n1][n2].clear();

		// reset last key location
		lastKeyLocation[n1][n2] = 0;
		if (feyn == null)
			return;

		JaxoGraph jg = feyn.getGraph();
		if (jg == null)
			return;

		Vector<JaObject> ListForRemove = new Vector<JaObject>();
		for (int i = 0; i < jg.listSize(); i++) {
			JaObject jaxoOb = (JaObject) (jg.listElementAt(i));
			// find key
			if (jaxoOb instanceof JaKey) {
				JaKey jk = (JaKey) jaxoOb;
				if (jk.getPadX() == n1 && jk.getPadY() == n2)
					ListForRemove.addElement(jaxoOb);
			}
			// find statistical box
			if (jaxoOb instanceof JaTextBox) {
				JaTextBox text = (JaTextBox) jaxoOb;
				if (text.getType() == StatBox && text.getPadX() == n1
						&& text.getPadY() == n2)
					ListForRemove.addElement(jaxoOb);
			}

			// labels
			if (jaxoOb instanceof JaText) {
				JaText text = (JaText) jaxoOb;
				if ((text.getType() == LabelX || text.getType() == LabelY)
						&& text.getPadX() == n1 && text.getPadY() == n2)
					ListForRemove.addElement(jaxoOb);
			}

			// find exponent lebels
			if (jaxoOb instanceof JaText) {
				JaText text = (JaText) jaxoOb;
				if (text.getType() == Exponent && text.getPadX() == n1
						&& text.getPadY() == n2)
					ListForRemove.addElement(jaxoOb);
			}

		}

		// now remove the objects
		for (int i = 0; i < ListForRemove.size(); i++) {
			JaObject jaxoOb = (JaObject) (ListForRemove.elementAt(i));
			jg.delete(jaxoOb);
		}

		ListForRemove.clear();
		ListForRemove = null;

		// / what if it is a contor?
		if (ja[n1][n2].isContour() == true) {
			ja[n1][n2].setContour(false);
		}

		setRangeX(0.0, 1.0, padx, pady);
		setRangeY(0.0, 1.0, padx, pady);

	}

	/**
	 * Set the location and the size of the current pad in NDC coordinate.
	 * First, navigate to this pad using the cd(i,j) method.
	 * 
	 * @param padx
	 *            pad ID in X
	 * @param pady
	 *            pad ID in Y
	 * @param xpos
	 *            pad location in X
	 * @param ypos
	 *            pad location in Y
	 * @param width
	 *            pad width in X
	 * @param hight
	 *            pad height in Y
	 */
	public void setPad(double xpos, double ypos, double width, double height) {

		ja[N1][N2].setLocation(xpos, ypos, "NDC");
		ja[N1][N2].setRelWH(width, height, "NDC");
		// remember pad locations
		// padX[N1][N2] = xpos;
		// padY[N1][N2] = ypos;
		// padWidth[N1][N2] = width;
		// padHight[N1][N2] = height;
		setLabelX(labelX[N1][N2].getText(), N1, N1);
		setLabelY(labelY[N1][N2].getText(), N2, N2);

		MarginLeft = xpos;
		MarginRight = 1 - width - xpos;
		MarginTop = ypos;
		MarginBottom = 1 - height - ypos;
		update();
	}

	/**
	 * Get the position of the current pad in X (NDC coordinate)
	 * 
	 * @return pad position in X
	 */
	public double getPadPositionX() {

		return ja[N1][N2].getXndc();
		// return padX[N1][N2];

	}

	/**
	 * Get the position of the current pad in Y (in NDC coordinate)
	 * 
	 * @return position in Y
	 */
	public double getPadPositionY() {

		return ja[N1][N2].getYndc();
		// return padY[N1][N2];

	}

	/**
	 * Get the width of the current pad in NDC system.
	 * 
	 * @return width
	 */
	public double getPadWidth() {
		return ja[N1][N2].getRelWndc();

		// return padWidth[N1][N2];

	}

	/**
	 * Get the height of the current pad in NDC system.
	 * 
	 * @return height
	 */
	public double getPadHeight() {
		return ja[N1][N2].getRelHndc();
		// return padHight[N1][N2];

	}

	/**
	 * Set the location and the size of the pad in NDC coordinate. First,
	 * navigate to this pad using the cd(i,j) method.
	 * 
	 * @param padx
	 *            pad ID in X
	 * @param pady
	 *            pad ID in Y
	 * @param xpos
	 *            pad location in X
	 * @param ypos
	 *            pad location in Y
	 * @param width
	 *            pad width in X
	 * @param hight
	 *            pad height in Y
	 */
	public void setPad(int padx, int pady, double xpos, double ypos,
			double width, double height) {

		int i1 = padx - 1;
		int i2 = pady - 1;

		ja[i1][i2].setLocation(xpos, ypos, "NDC");
		ja[i1][i2].setRelWH(width, height, "NDC");
		// remember pad locations
		// padX[i1][i2] = xpos;
		// padY[i1][i2] = ypos;
		// padWidth[i1][i2] = width;
		// padHight[i1][i2] = height;

		setLabelX(labelX[i1][i2].getText(), i1, i2);
		setLabelY(labelY[i1][i2].getText(), i1, i2);

		MarginLeft = xpos;
		MarginRight = 1 - width - xpos;
		MarginTop = ypos;
		MarginBottom = 1 - height - ypos;

		update();
	}

	/**
	 * Set the location and the size of the pad in NDC coordinate. First,
	 * navigate to this pad using the cd(i,j) method.
	 * 
	 * @param padx
	 *            pad ID in X
	 * @param pady
	 *            pad ID in Y
	 * @param xpos
	 *            pad location in X
	 * @param ypos
	 *            pad location in Y
	 */
	public void setPad(int padx, int pady, double xpos, double ypos) {

		int i1 = padx - 1;
		int i2 = pady - 1;

		ja[i1][i2].setLocation(xpos, ypos, "NDC");
		// remember pad locations
		// padX[i1][i2] = xpos;
		// padY[i1][i2] = ypos;
		setLabelX(labelX[i1][i2].getText(), i1, i2);
		setLabelY(labelY[i1][i2].getText(), i1, i2);

		double width = ja[i1][i2].getRelw();
		double height = ja[i1][i2].getRelh();
		MarginLeft = xpos;
		MarginRight = 1 - width - xpos;
		MarginTop = ypos;
		MarginBottom = 1 - height - ypos;

		update();
	}

	

	/**
	 * Remove a particular object
	 * 
	 * @param type
	 *            type of this object
	 * @param padx
	 *            pad
	 * @param pady
	 *            pad
	 */
	public void removeObject(int type, int padx, int pady) {

		int n1 = padx - 1;
		int n2 = pady - 1;
		JaxoGraph jg = feyn.getGraph();
		Vector<JaObject> ListForRemove = new Vector<JaObject>();
		for (int i = 0; i < jg.listSize(); i++) {
			JaObject jaxoOb = (JaObject) (jg.listElementAt(i));
			// find key
			if (jaxoOb instanceof JaKey) {
				JaKey jk = (JaKey) jaxoOb;
				if (jk.getPadX() == n1 && jk.getPadY() == n2
						&& jk.getType() == type)
					ListForRemove.addElement(jaxoOb);
			}
			// find statistical box
			if (jaxoOb instanceof JaTextBox) {
				JaTextBox text = (JaTextBox) jaxoOb;
				if (text.getType() == StatBox && text.getPadX() == n1
						&& text.getPadY() == n2)
					ListForRemove.addElement(jaxoOb);
			}

			// labels sssss
			if (jaxoOb instanceof JaText) {
				JaText text = (JaText) jaxoOb;
				if ((text.getType() == LabelX || text.getType() == LabelY)
						&& text.getPadX() == n1 && text.getPadY() == n2)
					ListForRemove.addElement(jaxoOb);
			}

			// find exponent labels
			if (jaxoOb instanceof JaText) {
				JaText text = (JaText) jaxoOb;
				if (text.getType() == Exponent && text.getPadX() == n1
						&& text.getPadY() == n2)
					ListForRemove.addElement(jaxoOb);
			}

		}

		// now remove the objects
		for (int i = 0; i < ListForRemove.size(); i++) {
			JaObject jaxoOb = (JaObject) (ListForRemove.elementAt(i));
			jg.delete(jaxoOb);
		}

		ListForRemove.clear();
		ListForRemove = null;

	}

	/**
	 * Get list of all objects on the frame.
	 */
	public ArrayList getArrayList() {
		JaxoGraph jg = feyn.getGraph();
		return jg.getObjectList();
	}

	/**
	 * Clear the axis frame from the data for the current pad.
	 */

	public void clearData() {
		clearData(N1 + 1, N2 + 1);
	}

	/**
	 * Update the canvas by repainting all objects
	 * 
	 */
	public void update() {
		showExponentX();
		showExponentY();

		// JaxoGraph jg = feyn.getGraph();
		// jg.reverse();
		// ArrayList array=jg.getObjectList();
		// for(int i = 0; i < array.size(); i++)
		// System.out.println( array.get( i ) );

		// redraw
		feyn.update();
		feyn.getGraph().setSaved(true);
	}

	/**
	 * Close the canvas (and dispose all components).
	 */
	public void close() {
		isOpen = 0;
		feyn.shutdown();
		ja = null;
		labelX = null;
		labelY = null;
		statbox = null;
		// padX = null;
		// padY = null;
		// padWidth = null;

	}

	/**
	 * Shows how many time the canvas was open.
	 * 
	 * @return shows how many time was open
	 */
	protected int isOpen() {
		return isOpen;
	}

	/**
	 * Sets the location of a text object at the center of the canvas
	 * 
	 * @param text
	 *            input text object
	 */
	public void setLocationAtCenter(JaText text) {
		FontMetrics fm = getFontMetrics(text.getFont());
		String stext = Translate.shrink(text.getText());
		int width = fm.stringWidth(stext);
		int height = fm.getHeight();
		double gx = Global.toX(width);
		double gy = Global.toY(height);
		text.setLocation(0.5 - 0.5 * gx, 0.5 * MarginTop + 0.4 * gy, "NDC");
	}

	/**
	 * Sets whether all axis lines will be drawn or not.
	 * 
	 * @param show
	 *            toggle, true if the axis should be drawn.
	 */
	public void setAxisAll(boolean show) {
		ja[N1][N2].setShow(X, show);
		ja[N1][N2].setShowMirror(X, show);
		ja[N1][N2].setShow(Y, show);
		ja[N1][N2].setShowMirror(Y, show);

	}

	/**
	 * remove X and Y axes, tics, axis labels on the current plot.
	 * 
	 */
	public void removeAxes() {
		setAxisAll(false);

		removeObject(Key, N1 + 1, N2 + 1);
		removeObject(LabelX, N1 + 1, N2 + 1);
		removeObject(LabelY, N1 + 1, N2 + 1);
		removeObject(LabelX, N1 + 1, N2 + 1);
		removeObject(StatBox, N1 + 1, N2 + 1);
		removeObject(Exponent, N1 + 1, N2 + 1);

		ja[N1][N2].setTicksLabels(X, false);
		ja[N1][N2].setTicksLabels(Y, false);

	}

	/**
	 * Set tick labels for the current pad
	 * 
	 * @param axis
	 *            axis id (0- X, 1- Y)
	 * @param set
	 *            set to true, if you want to show it
	 */
	public void setTicksLabels(int axis, boolean set) {

		if (axis == 0)
			ja[N1][N2].setTicksLabels(X, set);
		if (axis == 1)
			ja[N1][N2].setTicksLabels(Y, set);

	}

	/**
	 * Set or no a mirror axis
	 * 
	 * @param axis
	 *            axis id (0- X, 1- Y)
	 * @param set
	 *            set to true, if you want to show it
	 */
	public void setShowMirror(int axis, boolean set) {

		ja[N1][N2].setShowMirror(axis, set);
	}

	/**
	 * Show a particular axis (no mirror)
	 * 
	 * @param axis
	 *            0 for X, 1 for Y
	 * 
	 */
	public void showAxis(int axis) {

		ja[N1][N2].setShow(axis, true);
		ja[N1][N2].setTicksLabels(axis, true);

	}

	/**
	 * Sets or not the grid lines for all plots on the same canvas
	 * 
	 * @param axis
	 *            Axis (0 means X, 1 means Y)
	 * @param b
	 *            true is grid is shown
	 */

	public void setGridAll(int axis, boolean b) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				ja[i1][i2].setShowGrid(axis, b);
			}
		}
	}

	/**
	 * Sets a label for X at the default location
	 * 
	 * @param label
	 *            current label
	 * @param n1
	 *            pad position in X (-1)
	 * @param n2
	 *            pad position in Y (-1)
	 */
	public void setLabelX(String label, int n1, int n2) {

		labelX[n1][n2].setText(label);
		FontMetrics fm = getFontMetrics(fontLabel);
		String stext = Translate.shrink(label);
		int width = fm.stringWidth(stext);
		int height = fm.getHeight();
		double gx = Global.toX(width);
		double gy = Global.toY(height);

		double labelYshift = 0.05;

		double x = ja[n1][n2].getXndc();
		double y = ja[n1][n2].getYndc();
		double cwidth = ja[n1][n2].getRelWndc();
		double chight = ja[n1][n2].getRelHndc();
		// double x = padX[n1][n2];
		// double y = padY[n1][n2];
		// double cwidth = padWidth[n1][n2];
		// double chight = padHight[n1][n2];
		double dy = ja[n1][n2].getLabelSpace(Y);
		labelX[n1][n2].setLocation(x + cwidth - gx, y + chight + dy + 0.5 * gy
				+ labelYshift, "NDC");

	}

	
	
	
	/**
	 * Set label (or update it) for the current pad selected with the cd(i1,i2) method.
	 * After setting a new label, use "update()" method to apply changes
	 * 
	 * @param axis axis. 0 means X, 1 means Y
	 * @param x    X-position in the NDC
	 * @param y    Y-position in NDC
	 * @param text text 
	 * @param f    font of this label
	 * @param c    Color of this label
	 * @param howToSet    How to set. Use "NDC" (coordinate independent, between 0 and 1) or USER (depends on the plotted coordinates)
	 */
	
	public void setLabel(int axis, double x, double y, String text, Font f, Color c, String howToSet) {
		
		if (axis==0){
		labelX[N1][N2].setText(text);
		labelX[N1][N2].setLocation(x, y, howToSet);
		labelX[N1][N2].setColor(c);
		labelX[N1][N2].setFont(f);
		}
		
		if (axis==1){
			labelY[N1][N2].setText(text);
			labelY[N1][N2].setLocation(x, y, howToSet);
			labelY[N1][N2].setColor(c);
			labelY[N1][N2].setFont(f);
			}
		
	}
	
	

	/**
	 * Set label (or update it) for the current pad selected with the cd(i1,i2) method. This method uses NDC as the default frame
	 * for label position.
	 * After setting a new label, use "update()" method to apply changes
	 * 
	 * @param axis axis. 0 means X, 1 means Y
	 * @param x    X-position in the NDC
	 * @param y    Y-position in NDC
	 * @param text text 
	 * @param f    font of this label
	 * @param c    Color of this label
	 */
	
	public void setLabel(int axis, double x, double y, String text, Font f, Color c) {	
		setLabel(axis,x,y,text,f,c,"NDC");
		
	}

	
	
	/**
	 * Set label (or update it) for the current pad selected with the cd(i1,i2) method. This method uses NDC as the default frame
	 * for label position.
	 * After setting a new label, use "update()" method to apply changes
	 * 
	 * @param axis axis. 0 means X, 1 means Y
	 * @param x    X-position in the NDC
	 * @param y    Y-position in NDC
	 * @param text text 
	 * @param f    font of this label
	 */
	
	public void setLabel(int axis, double x, double y, String text, Font f) {	
		setLabel(axis,x,y,text,f,Color.black,"NDC");
		
	}
	
	
	/**
	 * Set label (or update it) for the current pad selected with the cd(i1,i2) method. This method uses NDC as the default frame
	 * for label position.
	 * After setting a new label, use "update()" method to apply changes
	 * 
	 * @param axis axis. 0 means X, 1 means Y
	 * @param x    X-position in the NDC
	 * @param y    Y-position in NDC
	 * @param text text 
	 */
	
	public void setLabel(int axis, double x, double y, String text) {	
		
		setLabel(axis,x,y,text,fontLabel,Color.black,"NDC");
		
	}
	
	
	
	
	
	
	/**
	 * Return the current lable
	 * @param axis if 0 then for X-axis, if 1 then Y-axis
	 * @return object representing this label
	 */
	public  JaText getLabel(int axis){
		
		if (axis==0) return labelX[N1][N2];
		if (axis==1) return labelY[N1][N2];
		return null;
		
	}
	
	
	/**
	 * Shift position of a label with respect to the axis by about defined in
	 * NDC (0-1). Applied for the current pad. To trigger redraw, call update()
	 * method.
	 * 
	 * @param axis
	 *            axis. 0 means Y, 1 means Y
	 * @param shiftX
	 *            value for the shift in X direction
	 * @param shiftY
	 *            value for the shift in Y direction
	 * 
	 */
	public void setLabelShift(int axis, double shiftX, double shiftY) {

		if (axis == 0) {
			double x = labelX[N1][N2].getXndc();
			double y = labelX[N1][N2].getYndc();

			// System.out.println(x);
			// System.out.println(y);
			double new_x = x + shiftX;
			double new_y = y - shiftY;
			if (new_y < 0 || new_y > 1) {
				ErrorMessage("The shift in Y is too much! (outside 0-1 range in NDC)");
				return;
			}
			if (new_x < 0 || new_x > 1) {
				ErrorMessage("This shiftin X is too much! (outside 0-1 range in NDC)");
				return;
			}
			labelX[N1][N2].setLocation(new_x, new_y, "NDC");

		} else if (axis == 1) {

			double x = labelY[N1][N2].getXndc();
			double y = labelY[N1][N2].getYndc();

			// System.out.println(x);
			// System.out.println(y);

			double new_x = x - shiftX;
			double new_y = y + shiftY;

			if (new_y < 0 || new_y > 1) {
				ErrorMessage("The shift in Y is too much! (outside 0-1 range in NDC)");
				return;
			}
			if (new_x < 0 || new_x > 1) {
				ErrorMessage("This shiftin X is too much! (outside 0-1 range in NDC)");
				return;
			}

			labelY[N1][N2].setLocation(new_x, new_y, "NDC");
		} else {

			ErrorMessage("Wrong axis! Should be 0 (X) or 1 (Y)");
			return;
		}

	}

	/**
	 * Set statistical box.
	 * 
	 * @param label
	 *            text
	 * @param n1
	 *            pad ID in X (-1)
	 * @param n2
	 *            pad ID in Y (-1)
	 */
	public void setStatBox(String[] label, int n1, int n2) {

		statbox[n1][n2].setMultiText(label);
		// double x = padX[n1][n2];
		// double y = padY[n1][n2];
		// double cwidth = padWidth[n1][n2];
		// double chight = padHight[n1][n2];

		double x = ja[n1][n2].getXndc();
		double y = ja[n1][n2].getYndc();
		double cwidth = ja[n1][n2].getRelWndc();
		double chight = ja[n1][n2].getRelHndc();

		// fix overlay with axis
		float a = ja[n1][n2].getStroke();
		double gy = Global.toY((int) a + 1);
		double gx = Global.toX((int) a + 1);
		double xw = statbox[n1][n2].getRelWndc();
		double xh = statbox[n1][n2].getRelHndc();
		statbox[n1][n2].setRotAngle(0);
		statbox[n1][n2].setLocation(x + cwidth - xw - gx, y + gy - (0.5 * xh),
				"NDC");
	}

	/**
	 * Set label at the default position
	 * 
	 * @param label
	 *            text of the Y label
	 * @param n1
	 *            pad ID in X (-1)
	 * @param n2
	 *            pad ID in Y (-1)
	 */
	public void setLabelY(String label, int n1, int n2) {

		labelY[n1][n2].setText(label);
		FontMetrics fm = getFontMetrics(fontLabel);
		String stext = Translate.shrink(label);
		int width = fm.stringWidth(stext);
		int height = fm.getHeight();
		double gx = Global.toX(width);
		double gy = Global.toY(height);
		// double x = padX[n1][n2];
		// double y = padY[n1][n2];

		double x = ja[n1][n2].getXndc();
		double y = ja[n1][n2].getYndc();

		double dx = ja[n1][n2].getLabelSpace(X);
		labelY[n1][n2].setRotAngle(-90);
                double xpos=x-dx-gy;
 		labelY[n1][n2].setLocation(xpos, y + gx, "NDC");

	}

	// ///////////////////////

	/**
	 * Checks whether preferences have been saved from an earlier session and if
	 * yes, imports them.
	 */
	public static void getPreferences() {

		java.io.File prefFile = new java.io.File(PREFS_FILENAME);

		if (prefFile.exists()) {
			JaxoPrefs.importPrefs(PREFS_FILENAME);
		}
	}

	/** get X size */
	public int getSizeX() {
		return feyn.getSizeX();
	}

	/** get Y size */
	public int getSizeY() {
		return feyn.getSizeY();
	}

	/**
	 * Processes the command line arguments.
	 * 
	 * @param args
	 *            The array of command line parameters as passed from the main
	 *            method.
	 * @return The file name of an input file to be opened.
	 */
	private static String getCommandLineArgs(String[] args) {
		inputFile = "";
		int nofJax = 0;

		if (args.length > 0) {
			if (args[0].startsWith("--")) {
				if (args[0].equals("--version")) {
					System.out.println(VERSION);
				} else if (args[0].equals("--info")) {
					info();
				} else if (args[0].equals("--help")) {
					help();
				} else {
					System.out.println(language.getString("Unknown_option:")
							+ args[0]);
				}
				System.exit(0);
			} else {
				for (int i = 0; i < args.length; i++) {
					if (args[i].endsWith(".xml")) {
						System.out.println(language.getString("Reading_file")
								+ args[i]);
						inputFile = args[i];

						if (nofJax > 0) {
							System.out
									.println(language
											.getString("Warning:_You_can_only_read_one_input_file!"));
						}

						nofJax++;
					} else if (args[i].equals("-verbose")) {
						JaxoPrefs.setVerbose(true);
					} else if (args[i].equals("-quiet")) {
						JaxoPrefs.setVerbose(false);
					} else {
						System.out.println(language
								.getString("Unknown_option:") + args[i]);
					}
				}
			}
		}

		return inputFile;
	}

	/**
	 * Returns the current working directory.
	 * 
	 * @return The current working directory.
	 */
	public static String getCurDir() {
		return curDIR;
	}

	/**
	 * Sets the current working directory.
	 * 
	 * @param newDir
	 *            The new working directory.
	 */
	public static void setCurDir(String newDir) {
		curDIR = newDir;
	}

	/** Determines the user's home directory from system properties. */
	private static void getUserDir() {
		try {
			curDIR = System.getProperty("user.dir");
		} catch (SecurityException aSecurityException) {
			javax.swing.JOptionPane
					.showMessageDialog(
							null,
							language.getString("SecurityException:_Cannot_get_system_properties!"));
			System.err.println(aSecurityException);
		}
	}

	/** Help message. */
	private static void help() {
		System.out
				.println(language.getString("Start_the_program_with:")
						+ language.getString("jaxodraw_[options]")
						+ language
								.getString("and_check_the_User_Guide_in_the_Help_menu.")
						+ language.getString("Command_line_options:")
						+ language
								.getString("--version__prints_out_the_version_number_of_JaxoDraw")
						+ language.getString("--help_____prints_out_this_help")
						+ language
								.getString("--info_____prints_out_some_information_about_your_system")
						+ language
								.getString("_-verbose__turns_on_verbose_error_messaging_(default_in_the_current_version)")
						+ language
								.getString("_-quiet____turns_off_verbose_error_messaging"));
	}

	/** Info message. */
	private static void info() {
		System.out.println(language.getString("OS_name:") + OS_NAME + "\n"
				+ language.getString("OS_architecture:") + OS_ARCH + "\n"
				+ language.getString("OS_version:") + OS_VERSION + "\n"
				+ language.getString("Java_version:") + JAVA_VERSION + "\n"
				+ language.getString("Java_runtime_version:")
				+ JAVA_RUNTIME_VERSION + "\n"
				+ language.getString("Java_home_directory:") + JAVA_HOME + "\n"
				+ language.getString("Java_class_path:") + JAVA_CLASSPATH);
	}

	/**
	 * Set range on X. Call update method to redraw.
	 * 
	 * @param min
	 *            min value on X
	 * @param max
	 *            max value ob X
	 */
	public void setRangeX(double min, double max) {
		setRangeX(min, max, N1 + 1, N2 + 1);
	}

	/**
	 * Set range on Y on the current pad. Call update method to redraw.
	 * 
	 * @param min
	 *            min value on Y
	 * @param max
	 *            max value on Y
	 */
	public void setRangeY(double min, double max) {
		setRangeY(min, max, N1 + 1, N2 + 1);
	}

	/**
	 * Set ranges on X and Y on the current pad. Call update method to redraw.
	 * 
	 * @param minX
	 *            min value on X
	 * @param maxX
	 *            max value on X
	 * @param minY
	 *            min value on Y
	 * @param maxY
	 *            max value on Y
	 */
	public void setRange(double minX, double maxX, double minY, double maxY) {
		setRangeX(minX, maxX, N1 + 1, N2 + 1);
		setRangeY(minY, maxY, N1 + 1, N2 + 1);
	}

	/**
	 * Set X range on pad defined by x and y on the canvas.
	 * 
	 * @param min
	 *            Min value on X
	 * @param max
	 *            Max vallue on X
	 * @param padx
	 *            pad in X
	 * @param pady
	 *            pad in Y
	 */
	public void setRangeX(double min, double max, int padx, int pady) {

		int n1 = padx - 1;
		int n2 = pady - 1;

		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}

		ja[n1][n2].setRange(X, min, max);
	}

	// show all exponents
	private void showExponentY() {

		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				if (ja[i1][i2].isLogScale(Y))
					return;

				String exp = ja[i1][i2].axisExponent(Y);
				if (exp.equals("0"))
					return;

				JaText expJ = new JaText();
				expJ.setText("x10^{" + exp + "}");
				expJ.setFont(ja[i1][i2].getLabelFont());
				expJ.setColor(ja[i2][i2].getLabelColor());
				expJ.setType(Exponent);
				expJ.setPadX(i1);
				expJ.setPadY(i2);

				// double x = padX[i1][i2];
				// double y = padY[i1][i2];
				double x = ja[i1][i2].getXndc();
				double y = ja[i1][i2].getYndc();

				FontMetrics fm = getFontMetrics(ja[i1][i2].getLabelFont());
				double cc = 0.4 * Global.toY((int) fm.getHeight());

				expJ.setLocation(x, y - cc, "NDC");
				add(expJ);

			}
		}

	}

	// show X exponent
	private void showExponentX() {

		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				if (ja[i1][i2].isLogScale(X))
					return;

				String exp = ja[i1][i2].axisExponent(X);
				if (exp.equals("0"))
					return;

				JaText expJ = new JaText();
				expJ.setText("x10^{" + exp + "}");
				expJ.setFont(ja[i1][i2].getLabelFont());
				expJ.setColor(ja[i1][i2].getLabelColor());
				expJ.setType(Exponent);
				expJ.setPadX(i1);
				expJ.setPadY(i2);
				FontMetrics fm = getFontMetrics(ja[i1][i2].getLabelFont());
				double cc = Global.toX((int) fm.stringWidth("0"));

				double x = ja[i1][i2].getXndc();
				double y = ja[i1][i2].getYndc();
				double cwidth = ja[i1][i2].getRelWndc();
				double chight = ja[i1][i2].getRelHndc();

				// double x = padX[i1][i2];
				// double y = padY[i1][i2];
				// double cwidth = padWidth[i1][i2];
				// double chight = padHight[i1][i2];
				expJ.setLocation(x + cwidth + cc, y + chight, "NDC");
				add(expJ);

			}
		}

	};

	/**
	 * Set Y range on pad defined by x and y on the canvas.
	 * 
	 * @param min
	 *            Min value on Y
	 * @param max
	 *            Max vallue on Y
	 * @param padx
	 *            pad in X
	 * @param pady
	 *            pad in Y
	 */
	public void setRangeY(double min, double max, int padx, int pady) {

		int n1 = padx - 1;
		int n2 = pady - 1;

		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}

		ja[n1][n2].setRange(Y, min, max);
	}

	/**
	 * Sets true or false to plot on a log scale.
	 * 
	 * @param axis
	 *            defines to which axis this function applies (0 if X, 1 if Y).
	 * @param b
	 *            toggle, true if the scaling is logarithmic
	 */
	public void setLogScale(int axis, boolean b) {
		ja[N1][N2].setLogScale(axis, b);
	}

	/**
	 * Sets the font used by the labels drawn at each tick.
	 * 
	 * @param font
	 *            the new font
	 */
	public void setTicFont(Font font) {
		ja[N1][N2].setLabelFont(font);
	}


	/**
	 * Show object editor on the left of the canvas.
	 */

	public void showEditor() {

		feyn.showEditorbar(true);

	}

	/**
	 * Show object editor (or not)
	 * 
	 * @param show
	 *            true then show it
	 */
	public void showEditor(boolean show) {

		feyn.showEditorbar(show);

	}

	/**
	 * Fast export of the canvas to an image file (depends on the extension,
	 * i.e. PNG, JPG, EPS, PS, SVG). No questions will be asked, an existing file
	 * will be rewritten
	 * 
	 * @param file
	 *            Output file with the proper extension. If no extension, PNG
	 *            file is assumed.
	 */

	public void export(String file) {
		feyn.export(file);

	}



         /**
         * Export SVG to various image formats. Supported output: 
         * JPEG<br> 
         * PNG <br> 
         * PS <br> 
         * EPS <br> 
         * PDF <br> 
         * SVGZ <br>
         * @param source Input file in SVG format
         * @param target Output file in designed format. 
         */
        public void convertSVG(String source, String target){
                    jhplot.io.images.ConvertSVG.SVGTo(source,target);
        }

        /**
         * Export SVG to various image formats. Supported output: 
         * JPEG<br> 
         * PNG <br> 
         * PS <br> 
         * EPS <br> 
         * PDF <br> 
         * SVGZ <br>
         * @param source Input file in SVG format
         * @param target Output file in designed format.
         * @param isRemove true if the source should be removed. 
         */
        public void convertSVG(String source, String target, boolean isRemove){
                    jhplot.io.images.ConvertSVG.SVGTo(source,target,isRemove);
        }





	/**
	 * Get current pad object
	 * 
	 * @return pad
	 */
	public JaAxes getPad() {
		return ja[N1][N2];
	}

	/**
	 * Get current space between the key symbol and the text for legends
	 * 
	 * @return space defined in NDC
	 */

	public double getKeyTextSpace() {
		return keyspace;
	}

	/**
	 * Set space between the key symbol and the text in a legend.
	 * 
	 * @param space
	 *            space to be set in NDC
	 */
	public void setKeyTextSpace(double space) {
		this.keyspace = space;
	}

	/**
	 * Get pad characterised by n1 and n2
	 * 
	 * @param n1
	 *            location of pad in X
	 * @param n2
	 *            location of pad in Y
	 * @return pad
	 */
	public JaAxes getPad(int n1, int n2) {
		return ja[n1 - 1][n2 - 1];
	}

	/**
	 * Margin from bottom.
	 * 
	 * @param marginBottom
	 */
	public void setMarginBottom(double marginBottom) {

		double x = ja[N1][N2].getXndc();
		double y = ja[N1][N2].getYndc();
		double cwidth = ja[N1][N2].getRelWndc();
		double cheight = ja[N1][N2].getRelHndc();

		if (1 - marginBottom < 0 || 1 - marginBottom > 1) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;

		ja[N1][N2].setLocation(x, 1 - cheight - marginBottom, "NDC");
		ja[N1][N2].setRelWH(cwidth, cheight, "NDC");

		MarginBottom = marginBottom;

	}

	/**
	 * Get size of bottom margin
	 * 
	 * @return size
	 */
	public double getMarginBottom() {
		return MarginBottom;
	}

	/**
	 * Margin from left
	 * 
	 * @param marginLeft
	 */
	public void setMarginLeft(double marginLeft) {

		double x = ja[N1][N2].getXndc();
		double y = ja[N1][N2].getYndc();
		double cwidth = ja[N1][N2].getRelWndc();
		double cheight = ja[N1][N2].getRelHndc();

		if (marginLeft < 0 || marginLeft > 1) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;
		ja[N1][N2].setLocation(marginLeft, y, "NDC");
		ja[N1][N2].setRelWH(cwidth, cheight, "NDC");

		MarginLeft = marginLeft;
	}

	/**
	 * Get margin from left side
	 * 
	 * @return marginLeft
	 */

	public double getMarginLeft() {
		return MarginLeft;
	}

	/**
	 * Set margin from right
	 * 
	 * @param marginRight
	 */
	public void setMarginRight(double marginRight) {

		double x = ja[N1][N2].getXndc();
		double y = ja[N1][N2].getYndc();
		double cwidth = ja[N1][N2].getRelWndc();
		double cheight = ja[N1][N2].getRelHndc();

		if (1 - marginRight < 0) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;

		ja[N1][N2].setLocation(1 - cwidth - marginRight, y, "NDC");
		ja[N1][N2].setRelWH(cwidth, cheight, "NDC");

		MarginRight = marginRight;
	}

	/**
	 * Get margin from right
	 * 
	 * @return marginRight
	 */
	public double getMarginRight() {
		return MarginRight;
	}

	/**
	 * Set antialiasing for the graphics of the current plot
	 * 
	 * @param setit
	 *            true if antialiasing is set
	 */
	public void setAntiAlias(boolean setit) {
		feyn.antialiasOn(setit);
	}

	/**
	 * Sets the actual font of the legend.
	 * 
	 * @param font
	 *            new font to draw the legend.
	 */
	public void setLegendFont(Font font) {
		for (int i = 0; i < (feyn.getGraph()).listSize(); i++) {
			JaObject jaxoOb = (JaObject) ((feyn.getGraph()).listElementAt(i));
			if (jaxoOb instanceof JaKey) {
				((JaKey) jaxoOb).setFont(font);
			}
		}
	}

	
	/**
	 * Return all the keys used for data annotation
	 * @return array with legends
	 */
	public ArrayList<JaKey>  getLegends() {
		
		ArrayList<JaKey> s= new ArrayList<JaKey>();
		
		for (int i = 0; i < (feyn.getGraph()).listSize(); i++) {
			JaObject jaxoOb = (JaObject) ((feyn.getGraph()).listElementAt(i));
			if (jaxoOb instanceof JaKey) {
				 s.add( (JaKey)jaxoOb);
			}
		 
		}
		
		return s;
	}
	
	
	/**
	 * Set new list of legends (old legends are removed).
	 * 
	 * @param  new legends
	 */
	public void  setLegends(ArrayList<JaKey> legends) {
		
		
		
		
		for (int i = 0; i < (feyn.getGraph()).listSize(); i++) {
			JaObject jaxoOb = (JaObject) ((feyn.getGraph()).listElementAt(i));
			if (jaxoOb instanceof JaKey) {
				(feyn.getGraph()).delete( jaxoOb);
			}
		}
		
		for (int i = 0; i < legends.size(); i++) {
			(feyn.getGraph()).addObject(legends.get(i) );
		}
		
	}
	
	

	/**
	 * Do we need to show legends?
	 * @param isSet false, then legends are removed.
	 */
	public void  setLegendAll(boolean isSet) {
		
		
		
		if ( isSet == false){
		for (int i = 0; i < (feyn.getGraph()).listSize(); i++) {
			JaObject jaxoOb = (JaObject) ((feyn.getGraph()).listElementAt(i));
			if (jaxoOb instanceof JaKey) {
				(feyn.getGraph()).delete( jaxoOb);
			}
		}
		}
		
		
	}
	
	
	
	
	
	
	/**
	 * Set the label font, which is used for axis labels and legend labels. The
	 * font names understood are those understood by java.awt.Font.decode().
	 * 
	 * @param name
	 *            A font name.
	 */
	public void setLegendFont(String name) {
		setLegendFont(Font.decode(name));
	}

	/**
	 * Set location of the current pad in NDC You may need to call update.
	 * 
	 * @param x
	 *            position
	 * @param y
	 *            position
	 */
	public void setPadLocation(double x, double y) {

		if (x < 0 || x > 1) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;
		if (y < 0 || y > 1) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;
		ja[N1][N2].setLocation(x, y, "NDC");

	}

	
	
	
	
	
	
	
	
	
	
	/**
	 * Set width and height of the pad in NDC
	 * 
	 * @param width
	 *            width
	 * @param width
	 *            height
	 */
	public void setPadSize(double width, double height) {

		if (width < 0 || width > 1) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;
		if (height < 0 || height > 1) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;
		ja[N1][N2].setRelWH(width, height, "NDC");

	}

	/**
	 * Set margin from top in NDC system.
	 * 
	 * @param marginTop
	 */
	public void setMarginTop(double marginTop) {

		double x = ja[N1][N2].getXndc();
		double y = ja[N1][N2].getYndc();
		double cwidth = ja[N1][N2].getRelWndc();
		double cheight = ja[N1][N2].getRelHndc();

		if (MarginTop < 0 || MarginTop > 1) {
			ErrorMessage("Position is not in NDC");
			return;
		}
		;

		ja[N1][N2].setLocation(x, MarginTop, "NDC");
		ja[N1][N2].setRelWH(cwidth, cheight, "NDC");

		MarginTop = marginTop;
	}

	/**
	 * Get the size of the top margin
	 * 
	 * @return top margin
	 */
	public double getMarginTop() {
		return MarginTop;
	}

	/**
	 * Margin in X between pads
	 * 
	 * @param marginX
	 */
	public void setMarginX(double marginX) {
		MarginX = marginX;
	}

	/**
	 * Get margin in X between pads
	 * 
	 * @return margin in X
	 */
	public double getMarginX() {
		return MarginX;
	}

	/**
	 * Margin in Y between pads
	 * 
	 * @param marginY
	 *            space in Y between pads
	 */
	public void setMarginY(double marginY) {
		MarginY = marginY;
	}

	/**
	 * Get space between any 2 plots in Y
	 * 
	 * @return space between plots in Y
	 */
	public double getMarginY() {
		return MarginY;
	}

	

	
	
	/**
	 * Get labels from all pads.
	 * 
	 * @param axis 0 for X, 1 for Y
	 * @return labels
	 */
	public JaText[][] getLabelsAll(int axis ) {
		if (axis==0) return labelX;
		if (axis==1) return labelY;
		
		return null;
	}

	
	
	
	
	/*
	private void mess(String s, double d) {
		System.out.println("Debug: " + s + Double.toString(d));
	}

	private void mess(String s, int d) {
		System.out.println("Debug: " + s + Integer.toString(d));
	}

	private void mess(String s) {
		System.out.println("Debug: " + s);
	}
   */
	
	/**
	 * Draw H1D histogram on pad X and pad Y
	 * 
	 * @param h1
	 *            input histogram
	 * @param padx
	 *            No of pads in X
	 * @param pady
	 *            No of pads in Y
	 */

	public void draw(H1D h1, int padx, int pady) {
		setContour(false);
		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}

		
		if (h1.getLabelX() != null)
			if (h1.getLabelX().length()>0) setNameX(h1.getLabelX() );
		if (h1.getLabelY() != null)
			if (h1.getLabelY().length()>0) setNameY(h1.getLabelY() );
		
		h1.setType(LinePars.H1D);
		Histogram1D h = h1.get();
		IAxis ax = h.axis();
		int Bin = ax.bins();
		DataArray data1 = new DataArray(0, 1, Bin, h1.getDrawOption());
		// h1.getDrawOption().print();

		for (int i = 0; i < Bin; i++) {
			double dd = ax.binCenter(i);
			double hh = h1.binHeight(i);
			double errX1 = dd - ax.binLowerEdge(i);
			double errX2 = ax.binUpperEdge(i) - dd;
			double errY = h.binError(i);
			data1.addPoint(dd, hh, errX1, errX2, errY, errY);

		}
		data1.setDimension(6);
		data1.setLinePars(h1.getDrawOption());
		data1.setName(h1.getTitle());
		data1.setType(LinePars.H1D);
		ja[padx - 1][pady - 1].addData(data1, LinePars.H1D);
		addKey(data1, padx, pady);
		ja[padx - 1][pady - 1].setStatistics(getStatistics(h1));
		addStatBox(padx, pady);
                // feyn.update();
	}

	/**
	 * Show or not keys for the current plot
	 * 
	 * @param show
	 *            true if shown
	 */
	public void showKey(boolean show) {

		ja[N1][N2].setShowKey(show);

	}

	public void addKey(DataArray data, int padx, int pady) {

		int n1 = padx - 1;
		int n2 = pady - 1;

		if (ja[n1][n2].isShowKey() == false)
			return;

		// double x = padX[n1][n2];
		// double y = padY[n1][n2];
		// double cwidth = padWidth[n1][n2];
		// double chight = padHight[n1][n2];

		double x = ja[n1][n2].getXndc();
		double y = ja[n1][n2].getYndc();
		double cwidth = ja[n1][n2].getRelWndc();
		double chight = ja[n1][n2].getRelHndc();

		JaKey newText = new JaKey();
		newText.setText(data.getName());
		newText.setPadX(n1);
		newText.setPadY(n2);
		newText.setType(Key);
		// change fons
		Font fon = new Font("Dialog", Font.BOLD, 16);
		if (N1final > 1)
			fon = new Font("Dialog", Font.BOLD, 12);
		if (N1final > 2)
			fon = new Font("Dialog", Font.BOLD, 10);
		if (N1final > 3)
			fon = new Font("Dialog", Font.BOLD, 9);
		newText.setFont(fon);

		FontMetrics fm = getFontMetrics(newText.getFont());
		int height = fm.getHeight();
		double gy = Global.toY(height);

		if (lastKeyLocation[n1][n2] == 0) {
			lastKeyLocation[n1][n2] = 0.1 * chight;
		} else {
			lastKeyLocation[n1][n2] = lastKeyLocation[n1][n2] + gy + keyspace;
		}

		double ypos = y + lastKeyLocation[n1][n2];
		newText.setLocation(x + 0.1 * cwidth, ypos, "NDC");
		// newText.setLocation(0.7,0.5, "NDC");
		newText.setRotAngle(0);
		newText.setColor(Color.black);
		newText.setKeyColor(data.getColor());
		if (data.fill() == true)
			newText.setKeyColor(data.getFillColor());
		newText.setKey(getSymbol(data));
		newText.setPenWidth(data.getPenWidth());
		add(newText);

	}

	/**
	 * Show a box with statistics (only for last plotted object) on the current
	 * pad
	 * 
	 * @param showStatBox
	 */
	public void setShowStatBox(boolean showStatBox) {
		this.showStatBox[N1][N2] = showStatBox;
	}

	/**
	 * Is stat box is shown on the current pad?
	 * 
	 * @return true if shown
	 */
	public boolean isShowStatBox() {
		return showStatBox[N1][N2];
	}

	public void addStatBox(int padx, int pady) {

		if (showStatBox[padx - 1][pady - 1] == false)
			return;

		int n1 = padx - 1;
		int n2 = pady - 1;

		String[] stat = ja[n1][n2].getStatistics();
		setStatBox(stat, n1, n2);
		showStatBox[n1][n2] = false;

	}

	public static int getSymbol(DataArray d) {

		int symbol = d.getSymbol();
		if (d.drawLine()) {
			symbol = 20;
		}
		if (d.getGraphStyle() == LinePars.HISTO && d.fill() == false) {
			symbol = 21;
		}
		if (d.getGraphStyle() == LinePars.HISTO && d.fill() == true) {
			symbol = 22;
		}
		return symbol;

	}

	/**
	 * Draw 2D histogram on pads given by X and Y. The contour style is used.
	 * 
	 * @param h2
	 *            input histogram
	 * @param padx
	 *            index of pad in X
	 * @param pady
	 *            index of pad in Y
	 */
	public void draw(H2D h2, int padx, int pady) {

		setContour(true);
		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}
		setContour(true);
		ja[padx - 1][pady - 1].addData(h2, LinePars.H2D);

	}

	/**
	 * Set the global title
	 * 
	 * @param gTitle
	 *            object representing global title
	 */
	public void setGTitle(JaText gTitle) {
		this.gTitle = gTitle;
		this.gTitle.setType(Title);
		setLocationAtCenter(this.gTitle);
		add(this.gTitle);
	}

	/**
	 * Sets the global title
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setGTitle(String sname, Font f, Color c) {
		gTitle = new JaText();
		gTitle.setText(sname);
		gTitle.setFont(f);
		gTitle.setColor(c);
		gTitle.setType(Title);
		setLocationAtCenter(gTitle);
		add(gTitle);
	}

	/**
	 * Sets the global title using black color.
	 * 
	 * @param sname
	 *            Title name
	 * @param f
	 *            Font
	 */
	public void setGTitle(String sname, Font f) {
		setGTitle(sname, f, Color.black);
	}

	/**
	 * Set the global title with default attributes. The default color is black.
	 * The default font is ("Arial", Font.BOLD, 20)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setGTitle(String sname) {
		setGTitle(sname, new Font("Arial", Font.BOLD, 18), Color.black);
	}

	/**
	 * Set the global title with default attributes. The default color is black.
	 * The default font is ("Arial", Font.BOLD, 20)
	 * 
	 * @param sname
	 *            Title
	 * @param color
	 *            Color of the title
	 */
	public void setGTitle(String sname, Color color) {
		setGTitle(sname, new Font("Arial", Font.BOLD, 18), color);
	}

	/**
	 * Get global title
	 * 
	 * @return
	 */
	public JaText getGTitle() {
		return gTitle;
	}

	/**
	 * Set the label for the axis in X
	 * 
	 * @param s
	 *            label title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setNameX(String s, Font f, Color c) {
		labelX[N1][N2].setText(s);
		labelX[N1][N2].setFont(f);
		labelX[N1][N2].setColor(c);
		setLabelX(s, N1, N2);

		if (feyn == null)
			return;
		JaxoGraph jg = feyn.getGraph();
		if (jg == null)
			return;
		jg.delete(labelX[N1][N2]);
		add(labelX[N1][N2]);

	}

	/**
	 * Sets the name for X axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for X axis.
	 */
	public void setNameX(String s) {
		setNameX(s, new Font("Arial", Font.BOLD, 16), Color.black);
	}

	/**
	 * Sets the name for X axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for X axis.
	 * @param f
	 *            Font
	 */
	public void setNameX(String s, Font f) {
		setNameX(s, f, Color.black);
	}

	/**
	 * Set the label for the axis in Y
	 * 
	 * @param s
	 *            label title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setNameY(String s, Font f, Color c) {
		labelY[N1][N2].setText(s);
		labelY[N1][N2].setFont(f);
		labelY[N1][N2].setColor(c);
		setLabelY(s, N1, N2);

		if (feyn == null)
			return;
		JaxoGraph jg = feyn.getGraph();
		if (jg == null)
			return;
		jg.delete(labelY[N1][N2]);
		add(labelY[N1][N2]);

	}

	/**
	 * Sets the name for Y axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for Y axis.
	 */
	public void setNameY(String s) {
		setNameY(s, new Font("Arial", Font.BOLD, 16), Color.black);
	}



/**
         * Sets the fill color of the bounding box drawn around a graph.
         * 
         * @param c
         *            Color for the fill
         */
        public void setBoxFillColor(Color c) {
                ja[N1][N2].setBackgroundColor(c);
        }

        /**
         * Returns the fill-color of the eventual bounding box arround the graph
         * 
         * @return the fill color of the bounding box.
         */
        public Color getBoxFillColor() {
                return ja[N1][N2].getBackgroundColor();
        }


 /**
         * Returns the color used to draw the bounding box.
         * 
         * @return the color of the bounding box.
         */
        public Color getBoxColor() {
                return ja[N1][N2].getFillColor();
        }

        /**
         * Sets the color of the bounding box drawn around a graph.
         * 
         * @param c
         *            drawing color.
         */
        public void setBoxColor(Color c) {
                ja[N1][N2].setFillColor(c);
        }



	/**
	 * Sets the name for Y axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for Y axis.
	 * @param f
	 *            Font
	 */
	public void setNameY(String s, Font f) {
		setNameY(s, f, Color.black);
	}

	/**
	 * Draw JaObject. Use update() method to actually show it. This method is
	 * the same ad add method.
	 * 
	 * @param jo
	 *            input JaObject
	 */
	public void draw(JaObject jo) {
		add(jo);
		feyn.getGraph().setSaved(true);
	}

	/**
	 * Draw H2D histogram on the current pad. Use update() method to actually
	 * show it.
	 * 
	 * @param h2d
	 *            input histogram
	 */
	public void draw(P1D p1d) {
		draw(p1d, N1 + 1, N2 + 1);
	}

	/**
	 * Draw H2D histogram on the current pad.
	 * 
	 * @param h2d
	 *            input histogram
	 */
	public void draw(H2D h2d) {
		draw(h2d, N1 + 1, N2 + 1);

	}

	/**
	 * Get statistics in form of strings
	 * 
	 * @param h1
	 *            input H1D
	 * @return lines with statistics
	 */
	public String[] getStatistics(H1D h1) {

		double mean = h1.mean();
		double rms = h1.rms();
		String name = "<H1D>"; // "+h1.getTitle();
		String sentries = "Entries =" + Integer.toString(h1.entries());
		String smean = "Mean  =" + dfb.format(mean);
		String srms = "RMS =" + dfb.format(rms);
		String extra = "Under/Overflow =" + Integer.toString(h1.extraEntries());
		String[] s = { name, sentries, smean, srms, extra };
		return s;
	}

	/**
	 * Get strings representing the statistics for the given object
	 * 
	 * @param h1
	 *            input P1D
	 * @return lines with statistics
	 */
	public String[] getStatistics(P1D h1) {

		String name = "<P1D> "; // +h1.getTitle();
		String sentries = "Entries =" + Integer.toString(h1.size());
		String smeanX = "Mean X =" + dfb.format(h1.meanX());
		String smeanY = "Mean Y =" + dfb.format(h1.meanY());
		String minX = "MinX  =" + dfb.format(h1.getMin(0));
		String minY = "MinY  =" + dfb.format(h1.getMin(1));
		String maxX = "MaxX  =" + dfb.format(h1.getMax(0));
		String maxY = "MaxY  =" + dfb.format(h1.getMax(1));
		String[] s = { name, sentries, smeanX, smeanY, minX, minY, maxX, maxY };
		return s;
	}

	/**
	 * Draw H1D histogram on the current pad. Use update() method to actually
	 * show it.
	 * 
	 * @param h1
	 *            input histogram
	 */

	public void draw(H1D h1) {

		draw(h1, N1 + 1, N2 + 1);

	}

	/**
	 * Draw array of F1D holders
	 * 
	 * @param f
	 *            array of F1D functions
	 */

	public void draw(F1D[] f) {
		setContour(false);
		for (int i = 0; i < f.length; i++) {
			draw(f[i]);
		}

	}

	/**
	 * Plot cloud in 1D. Assume 100 bins.
	 * 
	 * @param c1d
	 *            Input Cloud1D
	 */

	public void draw(Cloud1D c1d) {

		draw(new H1D(c1d, 100));

	}

	/**
	 * Plot cloud 2D
	 * 
	 * @param c2D
	 *            Input 2D cloud
	 */

	public void draw(Cloud2D c2d) {

		draw(new P1D(c2d));

	}

	/**
	 * Draw 1D histogram
	 * 
	 * @param h1d
	 *            input istogram1D
	 */
	public void draw(Histogram1D h1d) {
		setContour(false);
		H1D h1 = new H1D(h1d);
		draw(h1);

	}

	/**
	 * Fit 1D histogram with the function F1D
	 * 
	 * @param h1
	 *            H1D histogram
	 * @param predefFunc
	 *            String predefined function
	 * @param method
	 *            String - method for the fit
	 * @return int Fit results
	 */
	/**
	 * Fit 1D histogram with the function F1D
	 * 
	 * @param h1
	 *            H1D histogram
	 * @param predefFunc
	 *            String predefined function
	 * @param method
	 *            String - method for the fit
	 * @return int Fit results
	 */
	public int fit(H1D h1, String predefFunc, String method) {

		if (m_IFunctionFactory == null || m_IAnalysisFactory == null) {
			m_IAnalysisFactory = IAnalysisFactory.create();
			m_ITree = m_IAnalysisFactory.createTreeFactory().create();
			m_IHistogramFactory = m_IAnalysisFactory
					.createHistogramFactory(m_ITree);
			m_IFitFactory = m_IAnalysisFactory.createFitFactory();
			m_IFunctionFactory = m_IAnalysisFactory
					.createFunctionFactory(m_ITree);
		}

		IFunction fitfunc = m_IFunctionFactory.createFunctionByName("Gaussian",
				"G");
		IFitter fitter = m_IFitFactory.createFitter("chi2", "jminuit");

		IFitResult result = fitter.fit(h1.get(), fitfunc);

		IFunction fresult = result.fittedFunction();

		double[] fPars = result.fittedParameters();
		double[] fParErrs = result.errors();
		String[] fParNames = result.fittedParameterNames();

		for (int i = 0; i < fresult.numberOfParameters(); i++)
			System.out.println(fParNames[i] + " : " + fPars[i] + " +- "
					+ fParErrs[i]);

		return result.fitStatus();
	}

	/**
	 * Create various JAIDA factories: IAnalysisFactory, ITree, IFitFactory,
	 * IFunctionFactory
	 * 
	 */
	public void factories() {

		m_IAnalysisFactory = IAnalysisFactory.create();
		m_ITree = m_IAnalysisFactory.createTreeFactory().create();
		m_IHistogramFactory = m_IAnalysisFactory
				.createHistogramFactory(m_ITree);
		m_IFitFactory = m_IAnalysisFactory.createFitFactory();
		m_IFunctionFactory = m_IAnalysisFactory.createFunctionFactory(m_ITree);

	}

	/**
	 * Return IAnalysisFactory associated with the plot
	 * 
	 * @return IAnalysisFactory
	 */
	public IAnalysisFactory analF() {
		return m_IAnalysisFactory;
	}

	/**
	 * Return TreeFactory associated with the plot
	 * 
	 * @return ITree
	 */
	public ITree treeF() {
		return m_ITree;
	}

	/**
	 * Return FitFactory associated with the plot
	 * 
	 * @return IFitFactory
	 */
	public IFitFactory fitF() {
		return m_IFitFactory;
	}

	/**
	 * Return FunctionFactory associated with the plot
	 * 
	 * @return IFunctionFactory
	 */
	public IFunctionFactory funcF() {
		return m_IFunctionFactory;
	}

	/**
	 * Return IHistogramFactory associated with the plot
	 * 
	 * @return IHistogramFactory
	 */
	public IHistogramFactory histF() {
		return m_IHistogramFactory;
	}

	/**
	 * Draw data represented by DataArray on the current pad.
	 * 
	 * @param inputDA
	 *            input data container
	 */
	public void draw(DataArray inputDA, String title) {

		ja[N1][N2].addData(inputDA, 0);
		addKey(inputDA, N1 + 1, N2 + 1);

	}

	/**
	 * Draw an one-dimensional function on the current pad.
	 * 
	 * @param f1
	 *            input function
	 * @param padx
	 *            pad in X
	 * @param pady
	 *            pad in Y
	 */
	public void draw(F1D f1, int padx, int pady) {

		
		
		setContour(false);
		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}

		if (f1.getLabelX() != null)
			if (f1.getLabelX().length()>0) setNameX(f1.getLabelX() );
		if (f1.getLabelY() != null)
			if (f1.getLabelY().length()>0) setNameY(f1.getLabelY() );
		
		
		f1.setType(LinePars.F1D);
		f1.eval(); // evaluate first

		int Bin = f1.getPoints();

		f1.setGraphStyle(0);
		f1.setDrawSymbol(false);
		f1.setDrawLine(true);
		DataArray data1 = new DataArray(0, 1, Bin, f1.getDrawOption());

		for (int i = 0; i < Bin; i++) {
			data1.addPoint(f1.getX(i), f1.getY(i));
		}

		data1.setDimension(2);
		data1.setType(LinePars.F1D);
		data1.setName(f1.getTitle());
		ja[padx - 1][pady - 1].addData(data1, LinePars.F1D);
		addKey(data1, padx, pady);
                 // feyn.update();
	}

	/**
	 * Draw an one-dimensional function on the current pad.
	 * 
	 * @param f1
	 *            input function
	 * @param padx
	 *            pad in X
	 * @param pady
	 *            pad in Y
	 */

	public void draw(FND f1, int padx, int pady) {

		
		if (f1.getLabelX() != null)
			if (f1.getLabelX().length()>0) setNameX(f1.getLabelX() );
		if (f1.getLabelY() != null)
			if (f1.getLabelY().length()>0) setNameY(f1.getLabelY() );
		
		setContour(false);
		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}

		if (f1.isEvaluated() == false) {
			ErrorMessage("The function is not avaluated yet!");
			return;
		}

		f1.setType(LinePars.F1D);

		int Bin = f1.getPoints();

		f1.setGraphStyle(0);
		f1.setDrawSymbol(false);
		f1.setDrawLine(true);
		DataArray data1 = new DataArray(0, 1, Bin, f1.getDrawOption());

		for (int i = 0; i < Bin; i++) {
			data1.addPoint(f1.getX(i), f1.getY(i));
		}

		data1.setDimension(2);
		data1.setType(LinePars.F1D);
		data1.setName(f1.getTitle() + "; " + f1.getFixedVars());
		ja[padx - 1][pady - 1].addData(data1, LinePars.F1D);
		addKey(data1, padx, pady);
                 // feyn.update();
	}

	/**
	 * Draw an one-dimensional function on the current pad.
	 * 
	 * @param f1
	 *            input function
	 */
	public void draw(F1D f1) {
		draw(f1, N1 + 1, N2 + 1);
	}

	/**
	 * Draw an one-dimensional function on the current pad.
	 * 
	 * @param f1
	 *            input function
	 */
	public void draw(FND f1) {
		draw(f1, N1 + 1, N2 + 1);
	}

	/**
	 * Draw P1D object on the pad
	 * 
	 * @param p1d
	 *            input
	 * @param padx
	 *            pad in X
	 * @param pady
	 *            pad in Y
	 */

	public void draw(P1D p1d, int padx, int pady) {

		if (padx > N1final || pady > N2final) {
			ErrorMessage("Wrong number of canvas in cd() method\n  ");
			N1 = 0;
			N2 = 0;
			return;
		}
		
		if (p1d.getLabelX() != null)
			if (p1d.getLabelX().length()>0) setNameX(p1d.getLabelX() );
		if (p1d.getLabelY() != null)
			if (p1d.getLabelY().length()>0) setNameY(p1d.getLabelY() );
		
		p1d.setType(LinePars.P1D);
		p1d.setGraphStyle(LinePars.LINES);

		DataArray tmp = p1d.getDataArray();
		tmp.setDimension(p1d.dimension());
		tmp.setLinePars(p1d.getDrawOption());
		tmp.setName(p1d.getTitle());
		if (ja[padx - 1][pady - 1].isContour() == false) {
			ja[padx - 1][pady - 1].addData(tmp, LinePars.P1D);
			tmp.setType(LinePars.P1D);
			addKey(tmp, padx, pady);
			getStatistics(p1d);
			ja[padx - 1][pady - 1].setStatistics(getStatistics(p1d));
		}

		if (ja[padx - 1][pady - 1].isContour() == true) {
			tmp.setType(LinePars.CONTOUR);
			ja[padx - 1][pady - 1].addData(tmp, LinePars.CONTOUR);

		}

		addStatBox(padx, pady);
                // feyn.update();
	}

	/**
	 * Draw array of P1D holders
	 * 
	 * @param d
	 *            array of P1D data holders
	 */

	public void draw(P1D[] d) {

		for (int i = 0; i < d.length; i++) {
			draw(d[i]);
		}

	}

	/**
	 * Get the vector which keeps all the data
	 * 
	 * @return Vector with the data
	 */
	public Vector<DataArray> getData() {
		return ja[N1][N2].getData();

	}

	/**
	 * Sets true or false to use automatic scaling for the current plot.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the the automatic scaling feature is enabled.
	 */
	public void setAutoRange(int axis, boolean b) {
		ja[N1][N2].setAutoRange(axis, b);
	}

	/**
	 * Sets true or false to use automatic scaling for all plots.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the the automatic scaling feature is enabled.
	 */

	public void setAutoRangeAll(int axis, boolean b) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				ja[i1][i2].setAutoRange(axis, b);
			}
		}
	}

	/**
	 * Set autorange in X and Y at the same time
	 * 
	 * @param b
	 *            if true, sets autorange
	 */
	public void setAutoRange(boolean b) {
		ja[N1][N2].setAutoRange(0, b);
		ja[N1][N2].setAutoRange(1, b);
	}

	/**
	 * Set auto-range in X and Y at the same time for all plots
	 * 
	 * @param b
	 *            if true, sets auto-range
	 */
	public void setAutoRangeAll(boolean b) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				ja[i1][i2].setAutoRange(0, b);
				ja[i1][i2].setAutoRange(1, b);
			}
		}
	}

	/**
	 * Set autorange in X and Y at the same time for the current plot
	 * 
	 */
	public void setAutoRange() {
		ja[N1][N2].setAutoRange(0, true);
		ja[N1][N2].setAutoRange(1, true);
	}

	/**
	 * Set autorange in X and Y at the same time for all plots
	 * 
	 */
	public void setAutoRangeAll() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				ja[i1][i2].setAutoRange(0, true);
				ja[i1][i2].setAutoRange(1, true);
			}

		}
	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		feyn.setVisible(vs);
		if (vs == false)
			feyn.validate();

	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		feyn.setVisible(true);

	}


        /**
         * Set the canvas frame visible. Also set its location.
         * @param posX -  the x-coordinate of the new location's top-left corner in the parent's coordinate space;
         * @param posY - he y-coordinate of the new location's top-left corner in the parent's coordinate space 
         */
        public void visible(int posX, int posY) {
                feyn.setLocation(posX, posY);
                feyn.setVisible(true);

        }


	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */

	private void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * The main method of japlot.jaxodraw: determines system properties,
	 * preferences and command line arguments, before bringing up the graphical
	 * user interface.
	 * 
	 * @param args
	 *            The array of command line arguments.
	 */
	public static void main(String[] args) {

		String inputFile = getCommandLineArgs(args);
		// System.out.println(inputFile);
		JaxoDraw jd = new JaxoDraw(inputFile);

		/*
		 * JaFLine temp= new JaFLine(); temp.setLocation(0.2, 0.3);
		 * temp.setColor(Color.red); temp.setRelWH(0.3,0.4);
		 * temp.setStroke(2.0f); jd.add(temp); jd.update();
		 */

		// System.out.println(jd.getSizeX());
		// System.out.println(jd.getSizeY());
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
