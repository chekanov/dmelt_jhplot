/**
*    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
*    All rights reserved.
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

package jhplot;

//JAIDA 
import jas.hist.VectorGraphicsTransferable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.*;

import jhplot.gui.HelpBrowser;
import jhplot.utils.Util;

import org.freehep.application.Application;
import org.freehep.application.PrintHelper;
import org.freehep.application.studio.Studio;
import org.freehep.swing.popup.GlobalMouseListener;
import org.freehep.swing.popup.GlobalPopupListener;
//import org.freehep.util.export.ExportDialog;
import org.freehep.graphicsbase.util.export.*;

//JAIDA 
import hep.aida.*;
import hep.aida.ref.histogram.*;
import hep.aida.ref.plotter.*;

/**
 * Create a canvas using the JAS plotter API. It has different look & feel than
 * HPlot. Can also be used to show data, histograms, functions, 2D density
 * plots. Several plot regions can be used.
 * 
 * @author S.Chekanov
 * 
 */
public class HPlotter extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private IAnalysisFactory factory = null;
	private IFunctionFactory function = null;
	private IPlotter plotter;
	private IPlotterStyle style;
	private static int N1 = 0; // current
	private static int N2 = 0;
	private int N1final; // final
	private int N2final;
	private int plotID;
	private int xsize;
	private int ysize;
	private JPanel aidaPanel;
	private String title = "";
	private static final String rootKey = HPlotter.class.getName();
	private static final String SAVE_AS_TYPE = rootKey + ".SaveAsType";
	private static final String SAVE_AS_FILE = rootKey + ".SaveAsFile";

	/**
	 * Create HPlot canvas with several plots.
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
	 * 
	 */

	public HPlotter(String title, int xsize, int ysize, int n1, int n2) {

		this.title = title;
		this.xsize = xsize;
		this.ysize = ysize;
		N1final = n1;
		N2final = n2;
		N1 = 1;
		N2 = 1;
		plotID = 0;
		setTitle(title);

		if (n1>24 || n2>24){
			
			N1final=1;
			N2final=1;
			this.xsize=600;
			this.ysize=440;
			Util.ErrorMessage("Too many plot regions given! Maximum number is 25 by 25. Set to the defalts.");
			
		}
		
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item1 = new JMenuItem("Export");
		item1.setActionCommand("savePlotter");
		item1.addActionListener(this);
		// JMenuItem item2 = new JMenuItem("Print");
		// item2.setActionCommand("printPlotter");
		// item2.addActionListener(this);
		JMenuItem item3 = new JMenuItem("Close");
		item3.setActionCommand("Close");
		item3.addActionListener(this);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menu.add(item1);
		// menu.add(item2);
		menu.add(item3);
		GlobalMouseListener gml = new GlobalMouseListener(this);
		gml.addMouseListener(new GlobalPopupListener());

		bar.add(menu);
		setJMenuBar(bar);
		aidaPanel = getAidaPanel();
		setContentPane(aidaPanel);

	}

	/**
	 * Actions
	 */
	public void actionPerformed(ActionEvent actionEvent) {
		String command = actionEvent.getActionCommand();

		if (command.equals("Close")) {
			close();
		} else if (command.equals("saveRegion")) {
			ExportDialog dlg = new ExportDialog(null, true);
			dlg.showExportDialog(this, "Save As...", aidaPanel, "hplotter");
		} else if (command.equals("printRegion")) {
			Studio studio = (Studio) Application.getApplication();
			try {
				PrintHelper ph = new PrintHelper(aidaPanel, studio);
				ph.print();
			} catch (PrinterException x) {
				studio.error("Error printing plot", x);
			}
		} else if (command.equals("savePlotter")) {
			ExportDialog dlg = new ExportDialog(null, true);
			dlg.showExportDialog(aidaPanel, "Save As...", aidaPanel, "hplotter");
		} else if (command.equals("copyPlotter")) {
			Clipboard cb = aidaPanel.getToolkit().getSystemClipboard();
			VectorGraphicsTransferable t = new VectorGraphicsTransferable(
					aidaPanel);
			cb.setContents(t, t);
		} else if (command.equals("printPlotter")) {
			Studio studio = (Studio) Application.getApplication();
			try {
				PrintHelper ph = new PrintHelper(aidaPanel, studio);
				ph.print();
			} catch (PrinterException x) {
				studio.error("Error printing plot", x);
			}
		}
	}

	/**
	 * Get AIDA panel
	 * 
	 * @return AIDA panel
	 */

	protected JPanel getAidaPanel() {

		factory = IAnalysisFactory.create();
		style = factory.createPlotterFactory().createPlotterStyle();
		plotter = factory.createPlotterFactory().create(title);
		plotter.setParameter("plotterWidth", Integer.toString(xsize));
		plotter.setParameter("plotterHeight", Integer.toString(ysize));
		plotter.createRegions(N1final, N2final);
		for (int i = 0; i < plotter.numberOfRegions(); i++) {
			plotter.region(i).style().xAxisStyle().lineStyle().setThickness(2);
			plotter.region(i).style().yAxisStyle().lineStyle().setThickness(2);
		}

		JPanel jp = new JPanel(new BorderLayout());
		jp.add(PlotterUtilities.componentForPlotter(plotter),
				BorderLayout.CENTER);

		return jp;
	}

	/**
	 * Create a new plotting region
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void createRegion(double x, double y, double w, double h) {
		plotter.createRegion(x, y, w, h);

	}

	/**
	 * Navigate to a selected plot. This is necessary if there are a several
	 * plots on the same canvas.
	 * 
	 * @param cols
	 *            Set the location of the current plot in x (horizontal)
	 * @param rows
	 *            Set the location of the current plot in y (vertical)
	 * @param return false if error.
	 */
	public boolean cd(int cols, int rows) {

		N1 = cols;
		N2 = rows;

		if (cols > N1final || rows > N2final) {
			Util.ErrorMessage("Wrong number of canvas in cd() method\n  ");
			// System.out.println("Wrong number of canvas in cd() method");
			// System.out.println("Increase the number of canvas in HPlot!");
			N1 = 1;
			N2 = 1;
			return false;
		}

		if (N1final == 1 && N2final == 1) {
			plotID = 0;
			return true;
		}
		if (N1final == 2 && N2final == 1)
			if (N1 == 2 && N2 == 1) {
				plotID = 1;
				return true;
			}
		if (N1final == 1 && N2final == 2)
			if (N1 == 1 && N2 == 2) {
				plotID = 1;
				return true;
			}
		if (N1final == 2 && N2final == 2) {
			if (N1 == 1 && N2 == 2) {
				plotID = 1;
				return true;
			}
			if (N1 == 2 && N2 == 1) {
				plotID = 2;
				return true;
			}
			if (N1 == 2 && N2 == 2) {
				plotID = 3;
				return true;
			}
		}

		plotID = (N2final - 1) * (N1 - 1) + (N2 - 1);

		// System.out.println(plotID);

		return true;
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
	 * Get location of the graph on the main canvas in Y
	 * 
	 * @return location in Y
	 */

	public int getCdY() {

		return N2;

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
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {

		if (vs) {
			// plotter.show();
			pack();
			setVisible(true);

		} else {
			// plotter.hide();
			setVisible(false);
			dispose();
		}

	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		visible(true);
	}

	/**
	 * Return JAS plotter
	 * 
	 * @return
	 */
	public IPlotter getPlotter() {

		return plotter;
	}

	/**
	 * Construct a HPlot canvas with a single plot.
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 * 
	 */
	public HPlotter(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1);

	}

	/**
	 * Construct a HPlot canvas with a single plot.
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
	public HPlotter(String title, int xs, int ys, boolean set) {

		this(title, xs, ys, 1, 1);

	}

	/**
	 * Construct a HPlot canvas with a plot with the size 600x400.
	 * 
	 * @param title
	 *            Title
	 */
	public HPlotter(String title) {

		this(title, 600, 400, 1, 1);

	}

	/**
	 * Construct a HPlot canvas with a single plot with the size 600x400.
	 * 
	 */
	public HPlotter() {
		this("Default", 600, 400, 1, 1);
	}

	/**
	 * Refresh all the plots on the same canvas HPLOT
	 * 
	 */
	public void updateAll() {

		plotter.refresh();

	}

	/**
	 * Update the current canvas.
	 * 
	 */
	public void update() {

		plotter.region(plotID).refresh();

	}

	/**
	 * Fast export of the canvas to an image file (depends on the extension,
	 * i.e. PNG, PDF, EPS, PS). No questions will be asked, an existing file
	 * will be rewritten
	 * 
	 * @param file
	 *            Output file with the proper extension. If no extension, PNG
	 *            file is assumed.
	 */

	public void export(final String file) {
		aidaPanel.validate();
                jhplot.io.images.ExportVGraphics.export((Component) aidaPanel,rootKey,file);

	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	/**
	 * Return title of this plotter.
	 * 
	 * @return
	 */
	public String getTitle() {

		return plotter.title();
	}

	/**
	 * Close the canvas (and dispose all components).
	 */
	public void close() {
		setVisible(false);
		plotter.clearRegions();
		plotter.destroyRegions();
		plotter = null;
		aidaPanel = null;
		dispose();

	}

	/**
	 * Return color as a string
	 * 
	 * @param c
	 *            color
	 * @return
	 */

	private String colorString(Color c) {

		String rs = Integer.toString(c.getRed());
		String rg = Integer.toString(c.getGreen());
		String rb = Integer.toString(c.getBlue());
		String color = rs + "," + rg + "," + rb;
		return color;

	}

	
	/**
	 * Plot 1D histogram. Use external graphical options from Jaida.
	 * 
	 * @param h1
	 *            Input H1D histogram
	 * @param style
	 *            Plotter style.           
	 */

	public void draw(H1D h1, IPlotterStyle style) {

		plotter.region(plotID).plot((IBaseHistogram) h1.get(), style);
	}

	
	/**
	 * Plot 2D histogram. Use external graphical options from Jaida.
	 * 
	 * @param h2
	 *            Input H2D histogram
	 * @param style
	 *            Plotter style.           
	 */

	public void draw(H2D h2, IPlotterStyle style) {

		plotter.region(plotID).plot((IBaseHistogram) h2.get(), style);
	}
	

	/**
	 * Plot 2D histogram. Use external graphical options from Jaida as strings:  ColorMap,  style2D, Box.      
	 * 
	 * @param h2
	 *            Input H2D histogram
	 * @param style
	 *            Plotter style as a string:  ColorMap,  style2D, Box        
	 */

	public void draw(H2D h2, String style_str) {

		if (style_str.equalsIgnoreCase("ColorMap")){ 
			
		style.setParameter("showStatisticsBox","false");
		style.setParameter("hist2DStyle","colorMap");
		style.dataStyle().fillStyle().setParameter("colorMapScheme","rainbow");
		} 
		else if (style_str.equalsIgnoreCase("Scatter")){ 
			
			style.dataStyle().markerStyle().setColor("blue");
			style.dataStyle().markerStyle().setShape("1");
			style.dataStyle().markerStyle().setParameter("size","5");
			// style.setParameter("showAsScatterPlot","true");
			}
        else if (style_str.equalsIgnoreCase("Box")) {
			
			style.setParameter("backgroundColor","yellow");
			style.setParameter("foregroundColor","green");
			style.setParameter("hist2DStyle","box");
			style.dataStyle().markerStyle().setColor("blue");
			style.setParameter("showAsScatterPlot","false");
			
        } else if (style_str.equalsIgnoreCase("style2D")) {
			
			style.setParameter("backgroundColor","yellow");
			style.setParameter("foregroundColor","green");
			style.setParameter("hist2DStyle","ellipse");
			style.dataStyle().markerStyle().setColor("blue");
			style.setParameter("showAsScatterPlot","false");
			
		} else {
			
			style.setParameter("backgroundColor","yellow");
			style.setParameter("foregroundColor","green");
			style.setParameter("hist2DStyle","ellipse");
			style.dataStyle().markerStyle().setColor("blue");
			style.setParameter("showAsScatterPlot","false");
			
		}
		
		
		plotter.region(plotID).plot((IBaseHistogram) h2.get(), style);
	}
	
	
	/**
	 * Plot 1D histogram. Use graphical options of H1D histogram to use some
	 * style.
	 * 
	 * @param h1
	 *            Input H1D histogram
	 */

	public void draw(H1D h1) {

		IDataStyle dataStyle = factory.createPlotterFactory().createDataStyle();

		Color c = h1.getDrawOption().getFillColor();
		// dataStyle.setParameter("showDataPoints","false");
		dataStyle.setParameter("showErrorBars", "true");
		dataStyle.setParameter("showHistogramBars", "false");
		dataStyle.setParameter("errorBarsColor", "black");
		dataStyle.setParameter("fillHistogramBars", "false");

		if (h1.getLineParm().isBarShown())
			dataStyle.setParameter("showHistogramBars", "true");
		// if ( h1.getLineParm().isDrawSymbol()
		// )dataStyle.setParameter("showDataPoints","true");
		if (h1.isFilled())
			dataStyle.setParameter("fillHistogramBars", "true");

		dataStyle.fillStyle().setColor(colorString(c));
		dataStyle.lineStyle().setColor(colorString(h1.getColor()));
		dataStyle.lineStyle()
				.setThickness((int) h1.getLineParm().getPenWidth());
		dataStyle.errorBarStyle().setVisible(h1.getLineParm().getErrorsY());
		style.setDataStyle(dataStyle);

		plotter.region(plotID).plot((IBaseHistogram) h1.get(), style);
	}

	/**
	 * Draw Aida histogram with some plotter style
	 * 
	 * @param h1
	 *            input Aida histogram
	 * @param style
	 *            style
	 */
	public void draw(Histogram1D h1, IPlotterStyle style) {
		plotter.region(plotID).applyStyle(style);
		plotter.region(plotID).plot((IBaseHistogram) h1, style);
	}

	/**
	 * Draw 2D Aida histogram with some plotter style
	 * 
	 * @param h1
	 *            input Aida histogram
	 * @param style
	 *            style
	 */
	public void draw(Histogram2D h2, IPlotterStyle style) {
		plotter.region(plotID).applyStyle(style);
		plotter.region(plotID).plot((IBaseHistogram) h2, style);
	}

	/**
	 * Draw Aida histogram with default style
	 * 
	 * @param h1
	 *            Aida histogram
	 * @param datastyle
	 *            data style
	 */
	public void draw(Histogram1D h1) {

		plotter.region(plotID).plot((IBaseHistogram) h1);
	}

	/**
	 * Draw Aida histogram with some data style
	 * 
	 * @param h1
	 *            Aida histogram
	 * @param datastyle
	 *            data style
	 */
	public void draw(Histogram1D h1, IDataStyle datastyle) {

		plotter.region(plotID).style().setDataStyle(datastyle);
		plotter.region(plotID).plot((IBaseHistogram) h1);
	}

	/**
	 * Draw 2D Aida histogram with some dat style
	 * 
	 * @param h2
	 *            Aida histogram
	 * @param datastyle
	 *            data style
	 */
	public void draw(Histogram2D h2, IDataStyle datastyle) {

		plotter.region(plotID).style().setDataStyle(datastyle);
		plotter.region(plotID).plot((IBaseHistogram) h2);
	}

	/**
	 * Draw 2D histogram as a density plot.
	 * 
	 * @param h2
	 *            Input H2D histogram
	 */

	public void draw(H2D h2) {

		Color c = h2.getDrawOption().getFillColor();

		plotter.region(plotID).style().dataStyle().fillStyle()
				.setColor(colorString(c));
		plotter.region(plotID).style().dataStyle().lineStyle()
				.setColor(colorString(h2.getColor()));
		plotter.region(plotID).style().dataStyle().lineStyle()
				.setThickness((int) h2.getLineParm().getPenWidth());
		plotter.region(plotID).style().dataStyle().errorBarStyle()
				.setVisible(h2.getLineParm().getErrorsY());

		plotter.region(plotID).style().setParameter("hist2DStyle", "ellipse");
		plotter.region(plotID).style().dataStyle().markerStyle()
				.setColor(colorString(h2.getColor()));

		plotter.region(plotID).plot((IBaseHistogram) h2.get());
	}

	/**
	 * Draw 2D histogram as a density plot.
	 * 
	 * @param h2
	 *            Input 2D histogram
	 */

	public void draw(Histogram2D h2) {

		plotter.region(plotID).style().setParameter("hist2DStyle", "ellipse");
		plotter.region(plotID).style().dataStyle().markerStyle()
				.setColor("blue");

		plotter.region(plotID).plot(h2);
	}

	/**
	 * Plot AIDA data points.
	 * 
	 * @param p1d
	 *            input data points.
	 */

	public void draw(IDataPointSet p1d) {

		plotter.region(plotID).plot(p1d);
	}

	/**
	 * Get current plotter style.
	 * 
	 * @return
	 */
	public IPlotterStyle getPlotterStyle() {

		return plotter.region(plotID).style();
	}

	/**
	 * Get current data style.
	 * 
	 * @return
	 */
	public IDataStyle getDataStyle() {

		return plotter.region(plotID).style().dataStyle();
	}

	/**
	 * Draw Aida data set with some style
	 * 
	 * @param p1d
	 *            Aida data set
	 * @param style
	 *            style
	 */
	public void draw(IDataPointSet p1d, IPlotterStyle style) {
		plotter.region(plotID).plot(p1d, style);
	}

	/**
	 * Draw Aida data set with some data style
	 * 
	 * @param p1d
	 * @param datastyle
	 */
	public void draw(IDataPointSet p1d, IDataStyle datastyle) {

		IPlotterStyle s = plotter.region(plotID).style();
		plotter.region(plotID).plot(p1d, s);
	}

	/**
	 * Plot data points
	 * 
	 * @param p1d
	 *            input data points.
	 */

	public void draw(P1D p1d) {

		IDataStyle dataStyle = factory.createPlotterFactory().createDataStyle();
		Color c = p1d.getDrawOption().getFillColor();
		dataStyle.setParameter("showDataPoints", "true");
		dataStyle.setParameter("showErrorBars", "true");
		dataStyle.setParameter("showHistogramBars", "false");
		dataStyle.setParameter("errorBarsColor", "black");

		if (p1d.getLineParm().isDrawSymbol())
			dataStyle.setParameter("showDataPoints", "true");

		dataStyle.fillStyle().setColor(colorString(c));
		dataStyle.lineStyle().setColor(colorString(p1d.getColor()));
		dataStyle.lineStyle().setThickness(
				(int) p1d.getLineParm().getPenWidth());
		dataStyle.errorBarStyle().setVisible(p1d.getLineParm().getErrorsY());

		dataStyle.markerStyle()
				.setSize((int) p1d.getLineParm().getSymbolSize());
		dataStyle.markerStyle().setColor(colorString(p1d.getColor()));

		String shape = "circle"; // "box" "dot" "square"
									// {"square","dot","box","triangle","diamond","star","circle"};
									// .

		if (p1d.getSymbol() == 0)
			shape = "circle";
		if (p1d.getSymbol() == 1)
			shape = "square";
		if (p1d.getSymbol() == 2)
			shape = "diamond";
		if (p1d.getSymbol() == 3)
			shape = "triangle";
		if (p1d.getSymbol() == 4)
			shape = "dot";
		if (p1d.getSymbol() == 10)
			shape = "star";

		dataStyle.markerStyle().setShape(shape);

		style.setDataStyle(dataStyle);

		plotter.region(plotID).plot(p1d.getIDataPointSet(), style);
	}

	/**
	 * Draw a function. F1D should be created from AIDA.
	 * 
	 * @param f1d
	 *            input function;
	 */

	public void draw(F1D f1d) {

		Color c = f1d.getDrawOption().getFillColor();

		plotter.region(plotID).style().dataStyle().fillStyle()
				.setColor(colorString(c));
		plotter.region(plotID).style().dataStyle().lineStyle()
				.setColor(colorString(f1d.getColor()));
		plotter.region(plotID).style().dataStyle().lineStyle()
				.setThickness((int) f1d.getLineParm().getPenWidth());
		plotter.region(plotID).style().dataStyle().errorBarStyle()
				.setVisible(f1d.getLineParm().getErrorsY());
		IFunction ff = f1d.getIFunction();
		if (ff == null) {
			Util.ErrorMessage("You can plot only functions created by AIDA");
			return;
		}
		plotter.region(plotID).plot(ff);

	}

	/**
	 * Draw a function using AIDA.
	 * 
	 * @param f1d
	 *            input function;
	 */

	public void draw(IFunction f1d) {

		plotter.region(plotID).plot(f1d);

	}

	/**
	 * Draw Aida data set with some style
	 * 
	 * @param f1d
	 *            function.
	 * @param style
	 *            style
	 */
	public void draw(IFunction f1d, IPlotterStyle style) {
		plotter.region(plotID).plot(f1d, style);
	}

	/**
	 * Draw Aida function with some data style
	 * 
	 * @param f1d
	 *            function.
	 * @param datastyle
	 */
	public void draw(IFunction f1d, IDataStyle datastyle) {

		IPlotterStyle s = plotter.region(plotID).style();
		plotter.region(plotID).plot(f1d, s);
	}

	/**
	 * Draw a cloud using AIDA.
	 * 
	 * @param c1d
	 *            input cloud;
	 */

	public void draw(Cloud1D c1d) {

		plotter.region(plotID).plot(c1d);

	}

	/**
	 * Draw Aida cloud with some style.
	 * 
	 * @param c1d
	 *            cloud.
	 * @param style
	 *            style
	 */

	public void draw(Cloud1D c1d, IPlotterStyle style) {
		plotter.region(plotID).plot(c1d, style);
	}

	/**
	 * Draw Aida cloud with some data style
	 * 
	 * @param c1d
	 *            cloud.
	 * @param datastyle
	 */
	public void draw(Cloud1D c1d, IDataStyle datastyle) {

		IPlotterStyle s = plotter.region(plotID).style();
		plotter.region(plotID).plot(c1d, s);
	}

	/**
	 * Draw Aida 2D cloud
	 * 
	 * @param c2d
	 *            2D cloud
	 */

	public void draw(Cloud2D c2d) {
		plotter.region(plotID).plot(c2d);

	}

	/**
	 * Draw Aida 2D cloud with some style
	 * 
	 * @param c2d
	 *            cloud.
	 * @param style
	 *            style
	 */

	public void draw(Cloud2D c2d, IPlotterStyle style) {
		plotter.region(plotID).plot(c2d, style);
	}

	/**
	 * Draw Aida cloud with some data style
	 * 
	 * @param c2d
	 *            cloud.
	 * @param datastyle
	 */

	public void draw(Cloud2D c2d, IDataStyle datastyle) {

		IPlotterStyle s = plotter.region(plotID).style();
		plotter.region(plotID).plot(c2d, s);
	}

	/**
	 * Sets the actual background color for current plot region.
	 * 
	 * 
	 * @param c
	 *            background color.
	 */
	public void setBackgColor(Color c) {

		plotter.region(plotID).style()
				.setParameter("backgroundColor", colorString(c));

	}

	/**
	 * Set statistical box to the current draw area (navigated with the cd()
	 * method.
	 * 
	 * @param set
	 *            true if set.
	 */
	public void setStatBox(boolean set) {
		if (set)
			plotter.region(plotID).style().statisticsBoxStyle()
					.setVisible(true); // style().setParameter("showStatisticsBox","true");
		else
			plotter.region(plotID).style().statisticsBoxStyle()
					.setVisible(false);
	}

	/**
	 * Get statistical box of the current draw area (navigated with the cd()
	 * method).
	 */
	public IStatisticsBoxStyle getStatBoxStyle() {
		return plotter.region(plotID).style().statisticsBoxStyle();
	}

	/**
	 * Get legend box of the current draw area (navigated with the cd() method).
	 */
	public ILegendBoxStyle getLegendStyle() {
		return plotter.region(plotID).style().legendBoxStyle();
	}

	/**
	 * Get style of the current plotting region.
	 */
	public IPlotterStyle getRegionStyle() {
		return plotter.region(plotID).style();
	}

	/**
	 * Return style of axis of the current region
	 * 
	 * @param axis
	 *            0 for X, 1 for Y, 2 for Z
	 * @return
	 */
	public IAxisStyle getAxisStyle(int axis) {
		if (axis == 0)
			return plotter.region(plotID).style().xAxisStyle();
		if (axis == 1)
			return plotter.region(plotID).style().yAxisStyle();
		return plotter.region(plotID).style().zAxisStyle();
	}

	/**
	 * Set colors of statistical box.
	 * 
	 * @param foreg
	 *            foreground color;
	 * @param backg
	 *            background color;
	 */
	public void setStatColor(Color foreg, Color backg) {

		plotter.region(plotID).style().statisticsBoxStyle().textStyle()
				.setColor(colorString(foreg));
		plotter.region(plotID).style().statisticsBoxStyle().boxStyle()
				.foregroundStyle().setColor(colorString(foreg));
		plotter.region(plotID).style().statisticsBoxStyle().boxStyle()
				.backgroundStyle().setColor(colorString(backg));
	}

	/**
	 * Set position for the current statistical box
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setStatFont(Font f) {

		plotter.region(plotID).style().statisticsBoxStyle().textStyle()
				.setFont(f.getFontName());
		plotter.region(plotID).style().statisticsBoxStyle().textStyle()
				.setFontSize(f.getSize());
		plotter.region(plotID).style().statisticsBoxStyle().textStyle()
				.setBold(f.isBold());
		plotter.region(plotID).style().statisticsBoxStyle().textStyle()
				.setItalic(f.isItalic());

	}

	/**
	 * Set position for the current statistical box
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setStatBoxPos(double x, double y) {

		plotter.region(plotID).style().statisticsBoxStyle().boxStyle().setX(x);
		plotter.region(plotID).style().statisticsBoxStyle().boxStyle().setY(y);

	}

	/**
	 * Set position for the current statistical box
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setLegendPos(double x, double y) {

		plotter.region(plotID).style().legendBoxStyle().boxStyle().setX(x);
		plotter.region(plotID).style().legendBoxStyle().boxStyle().setY(y);

	}

	/**
	 * Sets whether or not to draw the legend for the current plot.
	 * 
	 * @param set
	 *            true if you want to show the legend.
	 */
	public void setLegend(boolean set) {
		if (set)
			plotter.region(plotID).style().legendBoxStyle().setVisible(true);
		else
			plotter.region(plotID).style().legendBoxStyle().setVisible(false);
	}

	/**
	 * Set legend fonts
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setLegendFont(Font f) {

		plotter.region(plotID).style().legendBoxStyle().textStyle()
				.setFont(f.getFontName());
		plotter.region(plotID).style().legendBoxStyle().textStyle()
				.setFontSize(f.getSize());
		plotter.region(plotID).style().legendBoxStyle().textStyle()
				.setBold(f.isBold());
		plotter.region(plotID).style().legendBoxStyle().textStyle()
				.setItalic(f.isItalic());

	}

	/**
	 * Get style of the current graph.
	 * 
	 * @return
	 */
	public IPlotterStyle getStyle() {

		return plotter.region(plotID).style();

	}

	/**
	 * Set autorange in X and Y at the same time for the current plot
	 * 
	 */
	public void setAutoRange() {

		setAutoRange(true);

	}

	/**
	 * Set autorange in X and Y at the same time
	 * 
	 * @param b
	 *            if true, sets autorange
	 */
	public void setAutoRange(boolean b) {
		if (b == false)
			setRange(0, 1, 0, 1);
	}

	/**
	 * Sets the range (min-max) displayed on the axis for the current plot.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */
	public void setRange(int axis, double min, double max) {

		if (axis == 0)
			plotter.region(plotID).setXLimits(min, max);
		if (axis == 1)
			plotter.region(plotID).setYLimits(min, max);
	}

	/**
	 * Sets the range (min-max) displayed on X
	 * 
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */
	public void setRangeX(double min, double max) {
		plotter.region(plotID).setXLimits(min, max);
	}

	/**
	 * Sets the range (min-max) displayed on Y
	 * 
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */
	public void setRangeY(double min, double max) {
		plotter.region(plotID).setYLimits(min, max);
	}

	/**
	 * Sets the range (min-max) displayed on all axises.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */

	public void setRangeAll(int axis, double min, double max) {

		int Np = plotter.numberOfRegions();
		for (int i1 = 0; i1 < Np; i1++) {

			if (axis == 0)
				plotter.region(i1).setXLimits(min, max);
			if (axis == 1)
				plotter.region(i1).setYLimits(min, max);

		}
	}

	/**
	 * Sets the range for the current plot
	 * 
	 * @param minX
	 *            Min value in X
	 * @param maxX
	 *            Max value in X
	 * @param minY
	 *            Min value in Y
	 * @param maxY
	 *            Max value in Y
	 */

	public void setRange(double minX, double maxX, double minY, double maxY) {
		plotter.region(plotID).setXLimits(minX, maxX);
		plotter.region(plotID).setYLimits(minY, maxY);

	}

	/**
	 * Sets the range for all plots
	 * 
	 * @param minX
	 *            Min value in X
	 * @param maxX
	 *            Max value in X
	 * @param minY
	 *            Min value in Y
	 * @param maxY
	 *            Max value in Y
	 */

	public void setRangeAll(double minX, double maxX, double minY, double maxY) {

		int Np = plotter.numberOfRegions();
		for (int i1 = 0; i1 < Np; i1++) {

			plotter.region(i1).setXLimits(minX, maxX);
			plotter.region(i1).setYLimits(minY, maxY);

		}
	}

	/**
	 * Set parameters for the axis using strings
	 * 
	 * @param axis
	 *            axis 0=X, 1=Y
	 * @param par
	 *            parameter name
	 * @param opt
	 *            option name
	 * @return true if no error
	 */
	public boolean setParAxis(int axis, String par, String opt) {

		if (axis == 0)
			return plotter.region(plotID).style().xAxisStyle()
					.setParameter(par, opt);
		if (axis == 0)
			return plotter.region(plotID).style().yAxisStyle()
					.setParameter(par, opt);
		return false;
	}

	/**
	 * Set parameters for the current region.
	 * 
	 * @param axis
	 *            axis 0=X, 1=Y
	 * @param par
	 *            parameter name
	 * @param opt
	 *            option name
	 * @return true if no error
	 */
	public boolean setParRegion(int axis, String par, String opt) {

		if (axis == 0)
			return plotter.region(plotID).style().setParameter(par, opt);
		if (axis == 0)
			return plotter.region(plotID).style().setParameter(par, opt);
		return false;
	}

	/**
	 * Get available parameters for axis.
	 * 
	 * @return list of parameters to set style
	 */
	public List<String> getParAxis() {

		String a[] = plotter.region(plotID).style().xAxisStyle()
				.availableParameters();

		return Arrays.asList(a);
	}

	/**
	 * Get options for available parameters of the plotting region.
	 * 
	 * @param parameter
	 *            input parameter
	 * @return
	 */
	public List<String> getOptRegion(String parameter) {

		String a[] = plotter.region(plotID).style()
				.availableParameterOptions(parameter);

		return Arrays.asList(a);
	}

	/**
	 * Get options for available parameters
	 * 
	 * @param parameter
	 *            input parameter
	 * @return
	 */
	public List<String> getOptAxis(String parameter) {

		String a[] = plotter.region(plotID).style().xAxisStyle()
				.availableParameterOptions(parameter);

		return Arrays.asList(a);
	}

	/**
	 * Set global title and its attributes
	 * 
	 * @param sname
	 *            name
	 * @param f
	 *            font
	 * @param c
	 *            color
	 */
	public void setGTitle(String sname, Font f, Color c) {

		plotter.setTitle(sname);
		ITitleStyle s = factory.createPlotterFactory().createTitleStyle();
		// TitleStyle s= new TitleStyle();
		// TextStyle t = new TextStyle();
		ITextStyle t = factory.createPlotterFactory().createTextStyle();
		// BoxStyle b = new BoxStyle();

		t.setColor(colorString(c));

		t.setFont(f.getFontName());
		t.setFontSize(f.getSize());
		t.setBold((f.isItalic()));
		t.setItalic((f.isItalic()));
		s.setTextStyle(t);
		plotter.setTitleStyle(s);
	}

	/**
	 * Set the global title with default attributes. The default color is black.
	 * The default font is ("Arial", Font.BOLD, 20)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setGTitle(String sname, Color c) {
		setGTitle(sname, new Font("Arial", Font.BOLD, 18), c);

	}



	/**
	 * Get the width of the main panel which keeps all margins and the central
	 * panel (in pixels)
	 * 
	 * @return size in X direction (width)
	 */

	public int getSizeX() {

		return xsize;

	}

	/**
	 * Get the height of the main panel which keeps all margins and the central
	 * panel (in pixels)
	 * 
	 * @return size in Y direction (height)
	 */

	public int getSizeY() {

		return ysize;

	}

	/**
	 * Set the global title with default attributes. The default color is black.
	 * The default font is ("Arial", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setGTitle(String sname) {
		setGTitle(sname, new Font("Arial", Font.BOLD, 18), Color.black);
	}

	/**
	 * Close the frame (as close).
	 */
	public void distroy() {

		close();

	}

	/**
	 * Clear the graph characterised by an index in X and Y. This method cleans
	 * the data and all graph settings.
	 * 
	 * @param i1
	 *            location of the graph in X
	 * @param i2
	 *            location of the graph in Y
	 */

	public void clear(int i1, int i2) {

		System.gc();

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
	 * Sets the background color of the current graph. This is the area between
	 * the axes (hence not the entire panel area).
	 * 
	 * @param c
	 *            color.
	 */
	public void setBackgColorGraph(Color c) {

		plotter.region(plotID).style()
				.setParameter("dataAreaColor", colorString(c));
	}

	/**
	 * Sets true or false to plot on a log scale.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the scaling is logarithmic
	 */
	public void setLogScale(int axis, boolean b) {

		if (axis == 0) {

			if (b == false)
				plotter.region(plotID).style().xAxisStyle()
						.setScaling("linear");
			if (b == true)
				plotter.region(plotID).style().xAxisStyle()
						.setScaling("logarithmic");
		}

		if (axis == 1) {

			if (b == false)
				plotter.region(plotID).style().yAxisStyle()
						.setScaling("linear");
			if (b == true)
				plotter.region(plotID).style().yAxisStyle()
						.setScaling("logarithmic");
		}

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
	 * Set the label for the axis in X for the current region.
	 * 
	 * @param s
	 *            label title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setNameX(String s, Font f, Color c) {

		plotter.region(plotID).style().xAxisStyle().setLabel(s);

		plotter.region(plotID).style().xAxisStyle().labelStyle()
				.setColor(colorString(c));
		plotter.region(plotID).style().xAxisStyle().labelStyle()
				.setFont(f.getFontName());
		plotter.region(plotID).style().xAxisStyle().labelStyle()
				.setFontSize(f.getSize());
		plotter.region(plotID).style().xAxisStyle().labelStyle()
				.setBold(f.isBold());
		plotter.region(plotID).style().xAxisStyle().labelStyle()
				.setItalic((f.isItalic()));

	}

	/**
	 * Returns the minimum data value for the specified axis.
	 * 
	 * @param axis
	 *            Defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return Minimum-value of the data range.
	 */
	public double getMinValue(int axis) {

		if (axis == 0)
			return plotter.region(plotID).xLimitMin();
		if (axis == 1)
			return plotter.region(plotID).yLimitMin();
		return 0;
	}

	/**
	 * Returns the maximum data value for the specified axis.
	 * 
	 * @param axis
	 *            Defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return Maximum-value of the data range.
	 */
	public double getMaxValue(int axis) {

		if (axis == 0)
			return plotter.region(plotID).xLimitMax();
		if (axis == 1)
			return plotter.region(plotID).yLimitMax();
		return 0;
	}

	/**
	 * Get available parameters for this canvas.
	 * 
	 * @return list of fonts
	 */
	public List<String> getParPlotter() {

		String a[] = plotter.availableParameters();
		return Arrays.asList(a);
	}

	/**
	 * Get available parameters for this canvas.
	 * 
	 * @return list of fonts
	 */
	public List<String> getParRegion() {

		String a[] = plotter.region(plotID).style().availableParameters();
		return Arrays.asList(a);
	}

	/**
	 * Get available fonts to draw text
	 * 
	 * @return list of fonts
	 */
	public List<String> getTextFonts() {

		String a[] = plotter.region(plotID).style().titleStyle().textStyle()
				.availableFonts();
		return Arrays.asList(a);
	}

	/**
	 * Get available parameters to draw text
	 * 
	 * @return list of parameters
	 */
	public List<String> getParText() {

		// plotter.region(plotID).style().xAxisStyle().
		String a[] = plotter.region(plotID).style().titleStyle().textStyle()
				.availableParameters();
		return Arrays.asList(a);
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
	 * Sets the Title for Y-axis
	 * 
	 * @param s
	 *            Label name
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setNameY(String s, Font f, Color c) {

		plotter.region(plotID).style().yAxisStyle().setLabel(s);

		plotter.region(plotID).style().xAxisStyle().labelStyle()
				.setColor(colorString(c));

		plotter.region(plotID).style().yAxisStyle().labelStyle()
				.setFont(f.getFontName());
		plotter.region(plotID).style().yAxisStyle().labelStyle()
				.setFontSize(f.getSize());
		plotter.region(plotID).style().yAxisStyle().labelStyle()
				.setBold(f.isBold());
		plotter.region(plotID).style().yAxisStyle().labelStyle()
				.setItalic((f.isItalic()));

	}

	/**
	 * Sets a title for the current plot. The default color is black, the font
	 * is ("Arial", Font.BOLD, 18),
	 * 
	 * @param name
	 *            Title
	 */
	public void setName(String name) {

		setName(name, new Font("Arial", Font.BOLD, 18), Color.black);

	}

	/**
	 * Sets a title for the current plot with all attributes
	 * 
	 * @param s
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */

	public void setName(String s, Font f, Color c) {

		plotter.region(plotID).setTitle(s);
		plotter.region(plotID).style().titleStyle().textStyle()
				.setFontSize(f.getSize());

		plotter.region(plotID).style().xAxisStyle().labelStyle()
				.setColor(colorString(c));

		plotter.region(plotID).style().titleStyle().textStyle()
				.setFont(f.getFontName());
		plotter.region(plotID).style().titleStyle().textStyle()
				.setFontSize(f.getSize());
		plotter.region(plotID).style().titleStyle().textStyle()
				.setBold(f.isBold());
		plotter.region(plotID).style().titleStyle().textStyle()
				.setItalic((f.isItalic()));

	}

	/**
	 * Sets a title for the current plot with all attributes
	 * 
	 * @param s
	 *            Title
	 * @param f
	 *            Font
	 */

	public void setName(String s, Font f) {
		setName(s, f, Color.black);
	}

	/**
	 * Sets the color used by the labels drawn at each tick.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @param c
	 *            the new color
	 */
	public void setTicStyle(int axis, Font f, Color c) {

		if (axis == 0) {
			plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
					.setColor(colorString(c));
			plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
					.setFont(f.getFontName());
			plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
					.setFontSize(f.getSize());
			plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
					.setBold(f.isBold());
			plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
					.setItalic((f.isItalic()));
		}
		if (axis == 1) {
			plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
					.setColor(colorString(c));
			plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
					.setFont(f.getFontName());
			plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
					.setFontSize(f.getSize());
			plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
					.setBold(f.isBold());
			plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
					.setItalic((f.isItalic()));
		}

	}

	/**
	 * Sets the color used by the labels drawn at each tick (for all axes).
	 * 
	 * @param c
	 *            the new color for each axis.
	 */
	public void setTicColor(Color c) {

		plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
				.setColor(colorString(c));
		plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
				.setColor(colorString(c));

	}

	/**
	 * Sets the color used by the labels drawn at each tick (for all axises).
	 * 
	 * @param c
	 *            the new color for each axis.
	 */
	public void setTicFont(Font f) {

		plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
				.setFont(f.getFontName());
		plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
				.setFontSize(f.getSize());
		plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
				.setBold(f.isBold());
		plotter.region(plotID).style().xAxisStyle().tickLabelStyle()
				.setItalic((f.isItalic()));

		plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
				.setFont(f.getFontName());
		plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
				.setFontSize(f.getSize());
		plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
				.setBold(f.isBold());
		plotter.region(plotID).style().yAxisStyle().tickLabelStyle()
				.setItalic((f.isItalic()));

	}

	/**
	 * Returns the actual color of the axes of the graph.
	 * 
	 * @return actual color used to draw the axes.
	 */
	public Color getAxesColor() {

		String mm = plotter.region(plotID).style().yAxisStyle().lineStyle()
				.color();
		String[] temp = mm.split(",");
		if (temp.length > 2) {
			int a1 = Integer.parseInt(temp[0]);
			int a2 = Integer.parseInt(temp[1]);
			int a3 = Integer.parseInt(temp[2]);
			return new Color(a1, a2, a3);
		}
		return Color.black;

	}

	/**
	 * Sets the actual color of the axes of the graph.
	 * 
	 * @param c
	 *            new color to draw the axes.
	 */
	public void setAxesColor(Color c) {

		plotter.region(plotID).style().yAxisStyle().lineStyle()
				.setColor(colorString(c));
	}

	

}
