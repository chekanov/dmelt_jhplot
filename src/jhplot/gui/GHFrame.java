// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import jhplot.JHPlot;
import jhplot.utils.*;

/**
 * Create main Frame with several plots.
 * 
 * @author S.Chekanov
 * 
 */

abstract public class GHFrame extends GHPanel implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	protected boolean set;

	protected JFrame mainFrame;

	private MemoryMonitor memMon;

	private JPanel infoPanel;

	protected static int N1 = 0; // current

	protected static int N2 = 0;

	protected int N1final; // final

	protected int N2final;

	protected int N1edit;

	protected int N2edit;

	protected JMenuBar bar;

	protected JMenu menu, about;

	protected JMenuItem item11, item00, item01, item02, item03, item04, item05,
			item06, item12, item13;

	/**
	 * Create main frame window
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
	 * @param set
	 *            set or not the graph
	 */
	public GHFrame(String title, int xsize, int ysize, int n1, int n2,
			boolean set) {

		super(xsize, ysize);

		if (n1 > 24 || n2 > 24) {

			N1final = 1;
			N2final = 1;
			xsize = 600;
			ysize = 440;
			Util.ErrorMessage("Too many plot regions given! Maximum number is 25 by 25. Set to the defalts.");

		}

		mainFrame = new JFrame();

		mainFrame
				.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				quitFrame();
			}
		});

		this.set = set;

		N1 = 0;
		N2 = 0;
		// protection
		if (n1 == 0 || n2 == 0) {
			n1 = 1;
			n2 = 1;
		}

		N1final = n1;
		N2final = n2;

		mainFrame.setTitle(title);

		ImageIcon icone = new ImageIcon(getClass().getClassLoader()
				.getResource("jhplot/images/logo_jhepwork_24x24.jpg"));
		mainFrame.setIconImage(icone.getImage());

		// System.out.println(xsize);
		// CanvasPanel = new GHPanel(xsize,ysize);
		// invert to GridLayout(); Gaps are zero
		mainPanel.setLayout(new VariableSizeGridLayout(N2final, N1final, 0, 0));

		infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());
		infoPanel.setBorder(new javax.swing.border.EtchedBorder());
		infoPanel.add(JHPlot.statusbar, BorderLayout.WEST);
		memMon = new MemoryMonitor();
		memMon.setPreferredSize(new java.awt.Dimension(70, 14));
		memMon.setMinimumSize(new java.awt.Dimension(30, 10));
		infoPanel.add(memMon, BorderLayout.EAST);

		mainFrame.add(CanvasPanel, BorderLayout.CENTER);
		mainFrame.add(infoPanel, BorderLayout.SOUTH);

		bar = new JMenuBar();
		menu = new JMenu("File");
		about = new JMenu("Help");

		item02 = new JMenuItem(new PrintAction());
		item01 = new JMenuItem(new ExportAction());
		item00 = new JMenuItem(new ExitAction());
		item03 = new JMenuItem(new SaveAction());
		item04 = new JMenuItem(new ReadAction());
		item05 = new JMenuItem(new ClearAction());
		item06 = new JMenuItem(new RefreshAction());
		item13 = new JMenuItem(new ReadDataAction());

		menu.add(item01);
		menu.add(item02);
		menu.add(item03);
		menu.add(item04);
		menu.add(item13);
		menu.add(item05);
		menu.add(item06);
		menu.insertSeparator(6);
		menu.add(item00);

		bar.add(menu);

		item11 = new JMenuItem(new ShowAboutAction());
		about.add(item11);

		item12 = new JMenuItem(new ShowHelpAction());
		about.add(item12);

		bar.add(about);

		mainFrame.setJMenuBar(bar);

		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// setSize(xsize, ysize);
		// setLocation(screenSize.width/2-WIDTH/2,screenSize.height/2-HEIGHT/2);
		mainFrame.pack();

	}

	/**
	 * Construct a GHFrame with a single plot/graph. It uses 10% of the space
	 * from the top for the global title
	 * 
	 * @param title
	 *            title for the canvas
	 * @param n1
	 *            size in x
	 * @param n2
	 *            size in y
	 */
	public GHFrame(String title, int n1, int n2) {
		this(title, n1, n2, 1, 1, true);
	}

	/**
	 * Construct a GHFrame canvas with a single plot/graph.
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 * @param set
	 *            set or not the graph (boolean)
	 */
	public GHFrame(String title, int xs, int ys, boolean set) {

		this(title, xs, ys, 1, 1, set);

	}

	/**
	 * Construct a GHFrame canvas with plots/graphs.
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 * 
	 * @param n1
	 *            number of plots/graphs in x
	 * @param n2
	 *            number of plots/graphs in y
	 */
	public GHFrame(String title, int xs, int ys, int n1, int n2) {

		this(title, xs, ys, n1, n2, true);

	}

	/**
	 * Construct a GHFrame canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public GHFrame(String title) {

		this(title, 600, 400, 1, 1, true);

	}

	/**
	 * Construct a GHFrame canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title "Default".
	 * 
	 */
	public GHFrame() {

		this("Default", 600, 400, 1, 1, true);

	}

	/**
	 * Add a graph or any component in the location given by i1 and i2.
	 * 
	 * @param i1
	 *            location in x
	 * @param i2
	 *            location in y
	 * @param a
	 *            component
	 */
	public void addGraph(int i1, int i2, Component a) {

		addComp(a);
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
			errorMessage("Wrong number of canvas in cd() method\n  ");
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
	 * Set the number of plots
	 * 
	 * @param nx
	 *            Number of plots in X
	 * @param ny
	 *            Number of plots in Y
	 */
	public void setPlotsNum(int nx, int ny) {
		N1final = nx;
		N2final = ny;
		// invert to GridLayout(); Gaps are zero
		mainPanel.setLayout(new GridLayout(N2final, N1final, 0, 0));
		CanvasPanel.updateUI();
	}

	/**
	 * update frame UI
	 */
	public void updateFrame() {

		CanvasPanel.repaint();
		CanvasPanel.updateUI();
		mainFrame.repaint();
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
	 * Do not show any frame
	 * 
	 */

	protected void doNotShowFrame() {
		mainFrame.setVisible(false);
	}

	/**
	 * Remove the frame. Should be called by quite() in the superclass.
	 * 
	 */

	protected void removeFrame() {

		item00 = null;
		item11 = null;
		item01 = null;
		item02 = null;
		item03 = null;
		menu = null;
		about = null;
		memMon = null;
		infoPanel = null;
		disposeGHPanel();
		mainFrame.setVisible(false);
		mainFrame.dispose();
		System.gc();
	}

	/**
	 * Get the main frame which keeps the components
	 * 
	 * @return Main frame
	 */
	public JFrame getFrame() {

		return mainFrame;
	}

	/**
	 * Returns a HTML page which is found in a valid image URL. The basis of the
	 * url is where 'intro' is created, which can't be but the place where
	 * JChess resides.
	 * 
	 * @param name
	 *            name of the HTML page
	 * @return the URL to the page
	 */
	public URL getHTMLUrl(String name) {
		URL url = null;
		try {
			url = this.getClass().getResource("/html/" + name);
		} catch (Exception e) {
			jplot.Utils
					.oops(mainFrame,
							"Impossible to load the About content.\nSomething's wrong with the installation.");
		}
		return url;
	}

	/**
	 * Show about dialog
	 * 
	 */
	protected void showAbout() {
		new AboutDialog(mainFrame);
	}

	public void componentMoved(ComponentEvent e) {
		// System.out.println("Moved\n" + e.getSource());
	}// end componentMoved()

	public void componentShown(ComponentEvent e) {
		// System.out.println("Shown\n" + e.getSource());
	}// end componentShown()

	public void componentHidden(ComponentEvent e) {
		// System.out.println("Hidden\n" + e.getSource());
	}// end componentHidden

	private class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ExitAction() {
			super("Exit");
		}

		public void actionPerformed(ActionEvent e) {
			quitFrame();
		}
	}

	private class ExportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ExportAction() {
			super("Export");
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("OK");
			exportImage();

		}
	}

	/*
	 * private class SaveAsAction extends AbstractAction { private static final
	 * long serialVersionUID = 1L;
	 * 
	 * SaveAsAction() { super("Save as"); }
	 * 
	 * public void actionPerformed(ActionEvent e) { new JHwriter(this); } }
	 */

	private class PrintAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		PrintAction() {
			super("Print");
		}

		public void actionPerformed(ActionEvent e) {
			printGraph();
		}
	}

	private class ShowAboutAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ShowAboutAction() {
			super("About");
		}

		public void actionPerformed(ActionEvent e) {
			showAbout();
		}
	}

	private class ShowHelpAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ShowHelpAction() {
			super("Help");
		}

		public void actionPerformed(ActionEvent e) {
			showHelp();
		}
	}

	private class SaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		SaveAction() {
			super("Save as");
		}

		public void actionPerformed(ActionEvent e) {
			openWriteDialog();
		}
	}

	private class ReadAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ReadAction() {
			super("Open graph");
		}

		public void actionPerformed(ActionEvent e) {
			openReadDialog();
		}
	}

	private class ReadDataAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ReadDataAction() {
			super("Open data file");
		}

		public void actionPerformed(ActionEvent e) {
			openReadDataDialog();
		}
	}

	private class ClearAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ClearAction() {
			super("Clear frame");
		}

		public void actionPerformed(ActionEvent e) {
			clearFrame();
		}
	}

	private class RefreshAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		RefreshAction() {
			super("Refresh frame");
		}

		public void actionPerformed(ActionEvent e) {
			refreshFrame();
		}
	}

	/**
	 * Exports the image to some graphic format.
	 */
	protected void exportImage() {

		if (isBorderShown())
			showBorders(false);
		// System.out.println("OK");
		JHPlot.showStatusBarText("Export to an image file");
                jhplot.io.images.ExportVGraphics.exportDialog((Component)CanvasPanel,GHFrame.class.getName(),mainFrame); 

	}

	/**
	 * Open a dialog to write the file
	 * 
	 */

	abstract protected void openWriteDialog();

	/**
	 * Open a dialog to read the file
	 * 
	 */

	abstract protected void openReadDialog();

	abstract protected void openReadDataDialog();

	/**
	 * Clear the graph
	 * 
	 */

	abstract protected void clearFrame();

	/**
	 * Refresh the graph
	 * 
	 */

	abstract protected void refreshFrame();

	/**
	 * quite the entire frame
	 * 
	 */

	abstract protected void showHelp();

	/**
	 * quite the entire frame
	 * 
	 */

	abstract protected void quitFrame();

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */

	private void errorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}
