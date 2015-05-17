// * This code is licensed under:
// * jHplot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;

import org.jgraph.*;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.*;

// resolve ambiguity
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

/**
 * Create a frame with interactive graphs.
 * 
 * @author S.Chekanov
 * 
 */

public class HGraph extends GHFrame {

	private static final long serialVersionUID = 1L;

	private JGraphModelAdapter[][] sc;

	private JGraph[][] jp;

	private ListenableGraph[][] ls;

	public boolean set;

	private static final Color DEFAULT_BG_COLOR = Color.white;
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

	private Thread1 m_Close;

	/**
	 * Create HGraph canvas with several graphs.
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

	public HGraph(String title, int xsize, int ysize, int n1, int n2,
			boolean set) {

		super(title, xsize, ysize, n1, n2, set);

		if (set)
			setGraph();

	}

	/**
	 * Clear all the frames.
	 * 
	 */

	protected void clearFrame() {

	}

	/**
	 * Refresh all the frames.
	 * 
	 */

	protected void refreshFrame() {

	}

	/**
	 * Open a dialog to write the file.
	 * 
	 */

	protected void openWriteDialog() {
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HGraph");

	}

	/**
	 * Open a dialog to read the file.
	 * 
	 */

	protected void openReadDialog() {
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HGraph");
	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		// updateAll();
		mainFrame.setVisible(vs);

	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		// updateAll();
		mainFrame.setVisible(true);

	}

	/**
	 * Destroy the canvas frame
	 */
	public void destroy() {
		mainFrame.setVisible(false);
		close();
	}

	/**
	 * Set a graph
	 * 
	 */
	private void setGraph() {

		sc = new JGraphModelAdapter[N1final][N2final];
		jp = new JGraph[N1final][N2final];
		ls = new ListenableGraph[N1final][N2final];

		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				ls[i1][i2] = new ListenableDirectedGraph<String, DefaultEdge>(
						DefaultEdge.class);
				sc[i1][i2] = new JGraphModelAdapter<String, DefaultEdge>(
						ls[i1][i2]);
				jp[i1][i2] = new JGraph(sc[i1][i2]);
				adjustDisplaySettings(jp[i1][i2]);
				mainPanel.add(jp[i1][i2]);

			}

		}

	}

	/**
	 * Position of vertex
	 * 
	 * @param vertex
	 *            Vertex name
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */

	private void positionVertexAt(Object vertex, int x, int y) {

		DefaultGraphCell cell = sc[N1][N2].getVertexCell(vertex);
		AttributeMap attr = cell.getAttributes();
		Rectangle2D bounds = GraphConstants.getBounds(attr);

		Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(),
				bounds.getHeight());

		GraphConstants.setBounds(attr, newBounds);

		// TODO: Clean up generics once JGraph goes generic
		AttributeMap cellAttr = new AttributeMap();
		cellAttr.put(cell, attr);
		sc[N1][N2].edit(cellAttr, null, null, null);

	}

	/**
	 * Set vertex position
	 * 
	 * @param vertex
	 *            Vertex name
	 * @param n1
	 *            Position in X
	 * @param n2
	 *            Position in Y
	 */

	public void setPos(String vertex, int n1, int n2) {
		positionVertexAt(vertex, n1, n2);
	}

	/**
	 * Construct a HPlot canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 */
	public HGraph(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);

	}

	/**
	 * Construct a HGraph canvas with a single plot/graph
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
	public HGraph(String title, int xs, int ys, boolean set) {

		this(title, xs, ys, 1, 1, set);

	}

	/**
	 * Construct a HGraph canvas with plots/graphs
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 * @param n1
	 *            number of plots/graphs in x
	 * @param n2
	 *            number of plots/graphs in y
	 */
	public HGraph(String title, int xs, int ys, int n1, int n2) {

		this(title, xs, ys, n1, n2, true);

	}

	/**
	 * Construct a HGraph canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HGraph(String title) {

		this(title, 600, 400, 1, 1, true);

	}

	/**
	 * Construct a HGraph canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title "Default"
	 * 
	 */
	public HGraph() {
		this("Default", 600, 400, 1, 1, true);
	}

	/**
	 * Clear the current graph including graph settings. Note: the current graph
	 * is set by the cd() method
	 */
	public void clear() {
		clear(N1, N2);
	}

	/**
	 * Clear the graph characterized by an index in X and Y. This method cleans
	 * the data and all graph settings.
	 * 
	 * @param i1
	 *            location of the graph in X
	 * @param i2
	 *            location of the graph in Y
	 */

	public void clear(int i1, int i2) {
		setGTitle("");

		System.gc();

	}

	/**
	 * Clear all graphs from data and settings.
	 */

	public void clearAll() {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				clear(i1, i2);
			}
		}
		System.gc();
	}

	private void adjustDisplaySettings(JGraph jg) {
		jg.setPreferredSize(DEFAULT_SIZE);

		Color c = DEFAULT_BG_COLOR;

		jg.setBackground(c);
	}

	/**
	 * Close the canvas (and dispose all components) Note: a memory leak is
	 * found - no time to investgate it. set to null all the stuff
	 */
	public void close() {

		mainFrame.setVisible(false);
		m_Close = new Thread1("Closing softly");
		if (!m_Close.Alive())
			m_Close.Start();

	}

	/**
	 * Add a new vertex
	 * 
	 * @param vertex
	 *            added vertex
	 */
	public void addVertex(String vertex) {

		ls[N1][N2].addVertex(vertex);

	}

	/**
	 * Add a connector between 2 vertexes
	 * 
	 * @param vertex1
	 *            first vertex
	 * @param vertex2
	 *            second vertex
	 */
	public void addEdge(String vertex1, String vertex2) {

		ls[N1][N2].addEdge(vertex1, vertex2);

	}

	
	
	
	
	/**
	 * Returns the current graph
	 * 
	 * @return Current graph
	 */
	public JGraph getGraph() {

		return jp[N1][N2];

	}

	/**
	 * Returns the current Listenable Graph
	 * 
	 * @return Current graph
	 */
	public ListenableGraph getListenableGraph() {

		return ls[N1][N2];

	}

	public void quit() {

		doNotShowFrame();
		clearAll();

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				// System.out.println("Clear graph="+Integer.toString(i1)+
				// " " + Integer.toString(i2));
				// clear data

				ls[i1][i2] = null;

				jp[i1][i2] = null;

				sc[i1][i2] = null;

			}
		}

		ls = null;
		jp = null;
		sc = null;

		removeFrame();

	}

	/**
	 * 
	 * @author S.Chekanov Aug 17, 2006 update plot showing centers and seeds
	 */
	class Thread1 implements Runnable {

		private Thread t = null;

		private String mess;

		Thread1(String s1) {
			mess = s1;

		}

		public boolean Alive() {
			boolean tt = false;
			if (t != null) {
				if (t.isAlive())
					tt = true;
			}
			return tt;
		}

		public boolean Joint() {
			boolean tt = false;
			try {
				t.join();
				return true; // finished

			} catch (InterruptedException e) {
				// Thread was interrupted
			}
			return tt;
		}

		public void Start() {
			t = new Thread(this, mess);
			t.start();

		}

		public void Stop() {
			t = null;
		}

		public void run() {
			quit();
		}
	}

	/**
	 * Close the frame from menu
	 */
	protected void quitFrame() {
		close();

	}


	protected void openReadDataDialog() {
        JOptionPane.showMessageDialog(getFrame(), "Not implemented for this canvas");
}
	
	@Override
	protected void showHelp() {
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HGraph");
		// TODO Auto-generated method stub
		
	}

	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
}
