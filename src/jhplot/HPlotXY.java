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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import jhplot.gui.HelpBrowser;

import org.jplot2d.element.*;
import org.jplot2d.sizing.*;
import org.jplot2d.swing.JPlot2DComponent;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.renderer.*;
import org.jplot2d.util.SymbolShape;
import org.jplot2d.element.XYGraph.ChartType;
import org.jplot2d.element.XYGraph.FillClosureType;

import hep.aida.*;
import hep.aida.ref.histogram.*;

/**
 * Create a plot in X-Y. This canvas is similar to HPlot, but it uses the API
 * based on JPlot2D program for a high-performance multi-threaded 2d plot library
 * which produces publication quality plots. It can be used for scatter plot,
 * line chart, staircase chart, linear axis and logarithmic axis,plot can
 * contains subplots * unlimited undo/redo.
 * 
 * @author S.Chekanov
 * 
 */
public class HPlotXY {
	private static final long serialVersionUID = 1L;

	private int xsize;
	private int ysize;
	private String title = "Tile";
        private JFrame frame;
	private static final String rootKey = HPlotXY.class.getName();

	private ElementFactory ef;
	private org.jplot2d.element.Plot plot;
	private Layer layer;
	private RenderEnvironment env;
	private Axis xaxis;
	private Axis yaxis;
	private Axis top_axis;
	private Axis right_axis;
	private JPlot2DComponent complot;
	/**
	 * Create canvas with a certain size. 
	 * 
	 * @param title
	 *            Title
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 * @param set
	 *            if true, we preset all graphics options. If false, only the
	 *            most basic options are created and the user should design the
	 *            plots yourself.
	 */

	public HPlotXY(String title, int xsize, int ysize, boolean set) {

		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY,
				"ERROR");
		this.title = title;
		this.xsize = xsize;
		this.ysize = ysize;

                frame=new JFrame(title);
		//setTitle(title);
                
		ef = ElementFactory.getInstance();
		plot = ef.createPlot();
		// plot.setPreferredContentSize(xsize, ysize);
		// plot.setSizeMode(new AutoPackSizeMode());
		plot.setSizeMode(new FillContainerSizeMode(1));
		// plot.setSizeMode(new AutoPackSizeMode());
		// plot.getLegend().setVisible(true);
		plot.setFontSize(14);

		/*
		 * Title title = ef.createTitle("Custom Title"); title.setFontScale(2);
		 * plot.addTitle(title);
		 */

	     JMenuBar bar = new JMenuBar();
	     JMenu menu = new JMenu("File");
	     JMenuItem item1 = new JMenuItem("Export");
             item1.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               exportDialog();
            }
        });

		JMenuItem item2 = new JMenuItem("Close");
                 item2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menu.add(item1);
		menu.add(item2);

		bar.add(menu);
		frame.setJMenuBar(bar);

		if (set == true) {
			top_axis = ef.createAxis();
			top_axis.setPosition(AxisPosition.POSITIVE_SIDE);
			top_axis.setLabelVisible(false);

			right_axis = ef.createAxis();
			right_axis.setPosition(AxisPosition.POSITIVE_SIDE);
			right_axis.setLabelVisible(false);
			xaxis = ef.createAxis();
			yaxis = ef.createAxis();
			xaxis.getTitle().setText("x");
			yaxis.getTitle().setText("y");

			xaxis.getTitle().setFont(new Font("Arial", Font.BOLD, 18));
			yaxis.getTitle().setFont(new Font("Arial", Font.BOLD, 18));

			plot.addXAxis(xaxis);
			plot.addYAxis(yaxis);
			plot.addXAxis(top_axis);
			plot.addYAxis(right_axis);

			layer = ef.createLayer();
			plot.addLayer(layer, xaxis.getTickManager().getAxisTransform(),
					yaxis.getTickManager().getAxisTransform());

		} // end presetting

		env = new RenderEnvironment(true);
		env.setPlot(plot);
		complot = new JPlot2DComponent(env);
		frame.add(complot);
		frame.setPreferredSize(new Dimension(xsize, ysize));

	}

	/**
	 * Create a plotting canvas.
	 * 
	 * @param title
	 *            Title
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 */
	public HPlotXY(String title, int xsize, int ysize) {
		this(title, xsize, ysize, true);
	}

	/**
	 * Create canvas for custom plots.
	 * 
	 * @param title
	 *            Title
	 * @param set
	 *            if false, a user should create a custom canvas.
	 */
	public HPlotXY(boolean set) {
		this("Canvas", 600, 400, set);
	}

	/**
	 * Sets the name for X axis. The color is black.
	 * 
	 * @param s
	 *            Title for X axis.
	 */
	public void setNameX(String s) {
		xaxis.getTitle().setText(s);
	}

	/**
	 * Sets the name for Y axis. The color is black.
	 * 
	 * @param s
	 *            Title for Y axis.
	 */
	public void setNameY(String s) {
		yaxis.getTitle().setText(s);

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

		Title title = ef.createTitle(sname);
		title.setFont(f);
		title.setColor(c);
		plot.addTitle(title);

	};

	
	/**
	 * Set title as an object.
	 * @param title
	 */
	public void setGTitle(Title title){
		
		plot.addTitle(title);
	}
	
	
	
	
	/**
	 * Set the global title with default attributes. The default font is
	 * ("Arial", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 * @param c
	 *            color
	 */
	public void setGTitle(String sname, Color c) {
		setGTitle(sname, new Font("Arial", Font.BOLD, 18), c);

	}

	/**
	 * Set the global title with default attributes.
	 * 
	 * @param sname
	 *            Title
	 */
	public void setGTitle(String sname) {
		setGTitle(sname, new Font("Arial", Font.BOLD, 18), Color.black);

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
		xaxis.getTickManager().getAxisTransform()
				.setRange(new Range.Double(min, max));
		top_axis.getTickManager().getAxisTransform()
				.setRange(new Range.Double(min, max));
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
		yaxis.getTickManager().getAxisTransform()
				.setRange(new Range.Double(min, max));
		right_axis.getTickManager().getAxisTransform()
				.setRange(new Range.Double(min, max));
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
		setRangeX(minX, maxX);
		setRangeY(minY, maxY);
	}

	/**
	 * Get the axis object.
	 * 
	 * @return type of the axis. <br>
	 *         0: X axis (bottom) <br>
	 *         1: Y axis (left) <br>
	 *         10: Top X axis <br>
	 *         11: Right Y axis <br>
	 */
	public Axis getAxis(int axis) {
		if (axis == 0)
			return xaxis;
		if (axis == 1)
			return yaxis;
		if (axis == 10)
			return top_axis;
		if (axis == 11)
			return right_axis;
		return xaxis;
	}

	
	/**
	 * Set axis 
	 * @param xaxis Y-axis
	 * @param yaxis Y-axis
	 */
	public void setAxis(Axis xaxis,Axis yaxis){
		this.xaxis = xaxis;
		this.yaxis = yaxis;

		
	}
	
	

	/**
	 * Sets true or false to plot on a log scale.
	 * 
	 * @param axis
	 *            defines to which axis this function applies. 0 means X, 1
	 *            means Y.
	 * @param b
	 *            toggle, true if the scaling is logarithmic
	 */
	public void setLogScale(int axis, boolean b) {
		if (b == true && axis == 0) {
			xaxis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LOGARITHMIC);
			top_axis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LOGARITHMIC);
		}

		if (b == true && axis == 1) {
			yaxis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LOGARITHMIC);
			right_axis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LOGARITHMIC);
		}

		if (b == false && axis == 0) {
			xaxis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LINEAR);
			top_axis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LINEAR);
		}

		if (b == false && axis == 1) {
			yaxis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LINEAR);
			right_axis.getTickManager().getAxisTransform()
					.setTransform(TransformType.LINEAR);
		}

	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {

		if (vs) {
			frame.pack();
			frame.setVisible(true);

		} else {
			frame.setVisible(false);
			frame.dispose();
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
	 * Construct a HPlotXY canvas with a plot with the size 600x400.
	 * 
	 * @param title
	 *            Title
	 */
	public HPlotXY(String title) {

		this(title, 600, 400, true);

	}

	/**
	 * Construct a HPlotXY canvas with a single plot with the size 600x400.
	 * 
	 */
	public HPlotXY() {
		this("Default", 600, 400, true);
	}

	

	/**
	 * Update the current canvas.
	 * 
	 */
	public void update() {

		complot.repaint();
		// plotter.region(plotID).refresh();

	}

	/**
	 * Fast export of the canvas to an image file. This depends on the
	 * extension: <br>
	 * SVG - Scalable Vector Graphics (SVG) <br>
	 * SVGZ - compressed SVG<br>
	 * PNG - raster format<br>
	 * PDF - PDF<br>
	 * EPS - PostScript (encapsulated)<br>
	 * <p>
	 * No questions will be asked and existing file will be rewritten
	 * 
	 * @param file
	 *            Output file with the proper extension (SVG, SVGZ, PNG, PDF,
	 *            EPS). If no extension, PNG file is assumed.
	 */

	public void export(final String file) {

		int dot = file.lastIndexOf('.');
		String base = (dot == -1) ? file : file.substring(0, dot);
		String fext = (dot == -1) ? "" : file.substring(dot + 1);
		fext = fext.trim();

		// System.out.println("Saving="+fext);

		// fixing vector formats
		boolean isSVGZ = false;
		if (fext.equalsIgnoreCase("svgz")) {
			isSVGZ = true;
		}
		boolean isEPS = false;
		if (fext.equalsIgnoreCase("eps")) {
			isEPS = true;
		}
		boolean isPDF = false;
		if (fext.equalsIgnoreCase("pdf")) {
			isPDF = true;
		}
		boolean isPNG = false;
		if (fext.equalsIgnoreCase("png")) {
			isPNG = true;
		}
		boolean isSVG = false;
		if (fext.equalsIgnoreCase("svg")) {
			isSVG = true;
		}
		boolean isJPG = false;
		if (fext.equalsIgnoreCase("jpg") || fext.equalsIgnoreCase("jpeg")) {
			isJPG = true;
		}

		try {
			if (isSVG)
				env.exportPlot(new SVGExporter(file));
			if (isSVGZ)
				env.exportPlot(new SVGZExporter(file));
			if (isPNG)
				env.exportPlot(new PngFileExporter(file));
			if (isPDF)
				env.exportPlot(new PdfExporter(file));
			if (isEPS)
				env.exportPlot(new EpsExporter(file));
			if (isJPG)
				env.exportPlot(new PngFileExporter(file));
		} catch (Exception e) {
			System.err.println(e.toString());
		}

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

		return this.title;
	}

	/**
	 * Close the canvas (and dispose all components).
	 */
	public void close() {
		frame.setVisible(false);
		plot = null;
		env = null;
		ef = null;
		frame.dispose();

	}

	/**
	 * Draw an arbitrary graph.
	 * @param xl
	 */
	public void draw(XYGraph xl){
		layer.addGraph(xl);
		
	}
	
	
	/**
	 * Plot 1D histogram. Use graphical options of H1D histogram to use some
	 * style.
	 * 
	 * @param histogram
	 *            input H1D histogram
	 * @return Graph to style
	 * 
	 */

	public XYGraph draw(H1D histogram) {

		Histogram1D h1 = histogram.get();
		IAxis axis = h1.axis();

		int nbb = axis.bins() + 2;
		double[] xx = new double[nbb];
		double[] yy = new double[nbb];
		double[] xu = new double[nbb];
		double[] ye = new double[nbb];
		double[] xcenter = new double[nbb];
		for (int i = 1; i < axis.bins() + 1; i++) {
			double xl = histogram.binLowerEdge(i - 1); // The weighted mean of a
			double xh = histogram.binUpperEdge(i - 1); // The weighted mean of a
			double y = histogram.binHeight(i - 1);
			double y1 = histogram.binError(i - 1);
			double y2 = histogram.binError(i - 1);
			double cc = histogram.binCenter(i - 1);

			double xxx = xl;
			// if (i>0) xxx=xl+xh;
			xx[i] = xxx;
			yy[i] = y;
			ye[i] = y1;
			xcenter[i] = cc;
			xu[i] = xh;
			// System.out.println(i);
			// System.out.println(xl);

		}

		double d = xcenter[axis.bins()] - xx[axis.bins()];
		xx[0] = histogram.binLowerEdge(0);
		yy[0] = 0.0;
		xx[nbb - 1] = xx[axis.bins()] + 2 * d;
		yy[nbb - 1] = 0; // histogram.binHeight(nbb-2);

		XYGraph graph = ef.createXYGraph(xx, yy, null, null, null, null,
				histogram.getTitle());
		graph.setChartType(ChartType.HISTOGRAM_EDGE);
		// graph.setChartType(ChartType.HISTOGRAM);
		graph.setSymbolVisible(false);
		graph.setSymbolSize(0);
		if (histogram.isFilled())
			graph.setFillEnabled(true);
		graph.setColor(histogram.getColor());

		Color cc = histogram.getFillColor();
		jplot.LinePars lpp = histogram.getDrawOption();
		float ff = lpp.getFillColorTransparency();
		graph.setFillPaint(cc);
		graph.setFillClosureType(FillClosureType.BOTTOM);
		layer.addGraph(graph);
		float width = histogram.getPenWidth();
		graph.setLineStroke(ef.createStroke(width));
		// 0 - solid; 1- dashed; 2-dot-dashed-line; 3: dotted
		int lstyle = histogram.getLineStyle();
		if (lstyle == 1)
			graph.setLineStroke(ef.createStroke(width, new float[] { 12, 6 }));
		if (lstyle == 2)
			graph.setLineStroke(ef.createStroke(width,
					new float[] { 1, 3, 6, 3 }));
		if (lstyle == 3)
			graph.setLineStroke(ef.createStroke(width, new float[] { 6, 6 }));

		if (histogram.isErrY()) {
			XYGraph err = ef.createXYGraph(xcenter, yy, null, null, ye, ye,
					histogram.getTitle() + "_Error");
			// err.setChartType(ChartType.HISTOGRAM_EDGE);
			// err.setChartType(ChartType.HISTOGRAM);
			err.setSymbolVisible(true);
			err.setSymbolSize(0);
			err.setFillEnabled(false);
			err.setLineVisible(false);
			err.getLegendItem().setVisible(false);
			// err.setFillClosureType(FillClosureType.BOTTOM);
			layer.addGraph(err);
		}

		return graph;
	}

	/**
	 * Draw P1D array with errors.
	 * 
	 * @param p1d
	 *            P1D array with errors
	 * @param graph
	 *            to style;
	 * 
	 */

	public XYGraph draw(P1D p1d) {

		XYGraph xl = null;
		if (p1d.getDimension() == 2) {
			xl = ef.createXYGraph(p1d.getArrayX(), p1d.getArrayY(),
					p1d.getTitle());
		}

		if (p1d.getDimension() == 3) {
			xl = ef.createXYGraph(p1d.getArrayX(), p1d.getArrayY(), null, null,
					p1d.getArrayErr(), p1d.getArrayErr(), p1d.getTitle());
		}

		if (p1d.getDimension() == 4) {
			xl = ef.createXYGraph(p1d.getArrayX(), p1d.getArrayY(), null, null,
					p1d.getArrayYlower(), p1d.getArrayYupper(), p1d.getTitle());
		}

		if (p1d.getDimension() > 5) {
			xl = ef.createXYGraph(p1d.getArrayX(), p1d.getArrayY(),
					p1d.getArrayXleft(), p1d.getArrayXright(),
					p1d.getArrayYlower(), p1d.getArrayYupper(), p1d.getTitle());
		}

		xl.setSymbolShape(SymbolShape.FCIRCLE);
		xl.setColor(p1d.getColor());
		jplot.LinePars lpp = p1d.getDrawOption();
		int style = p1d.getLineStyle();
		float dash = lpp.getDashLength();
		int symbol = lpp.getSymbol();

		float width = p1d.getPenWidth();
		xl.setLineStroke(ef.createStroke(width));
		// 0 - solid; 1- dashed; 2-dot-dashed-line; 3: dotted
		int lstyle = p1d.getLineStyle();
		if (lstyle == 1)
			xl.setLineStroke(ef.createStroke(width, new float[] { 12, 6 }));
		if (lstyle == 2)
			xl.setLineStroke(ef.createStroke(width, new float[] { 1, 3, 6, 3 }));
		if (lstyle == 3)
			xl.setLineStroke(ef.createStroke(width, new float[] { 6, 6 }));

		xl.setLineVisible(lpp.isDrawLine());
		xl.setSymbolVisible(lpp.isDrawSymbol());
		xl.setSymbolSize((int) p1d.getSymbolSize());
		xl.setSymbolColor(p1d.getColor());

		String sym = p1d.getSymbolShape();

		if (sym.equals("+"))
			xl.setSymbolShape(SymbolShape.UARROW);
		else if (sym.equals("Dot"))
			xl.setSymbolShape(SymbolShape.FCIRCLE);
		else if (sym.equals("Circle"))
			xl.setSymbolShape(SymbolShape.FCIRCLE);
		else if (sym.equals("o"))
			xl.setSymbolShape(SymbolShape.CIRCLE);
		else if (sym.equals("t"))
			xl.setSymbolShape(SymbolShape.TRIANGLE);
		else if (sym.equals("*"))
			xl.setSymbolShape(SymbolShape.STAR);
		else if (sym.equals("x"))
			xl.setSymbolShape(SymbolShape.VCROSS);
		else if (sym.equals("square"))
			xl.setSymbolShape(SymbolShape.SQUARE);
		else if (sym.equals("s"))
			xl.setSymbolShape(SymbolShape.SQUARE);
		else if (sym.equals("dot"))
			xl.setSymbolShape(SymbolShape.FCIRCLE);
		else if (sym.equals("diamond"))
			xl.setSymbolShape(SymbolShape.DIAMOND);
		else if (sym.equals("d"))
			xl.setSymbolShape(SymbolShape.DIAMOND);
		else
			System.err
					.println("Unrecognizable symbol for drawing. HPlotXY supports: +, Dot, Circle, o, t, *, x, square, s, dot, diamond,d ");

		layer.addGraph(xl);

		return xl;

	}

	/**
	 * Plot X-Y data
	 * 
	 * @param x
	 *            X array
	 * @param y
	 *            Y array
	 * @return Graph to for styling.
	 */
	public XYGraph draw(double[] x, double[] y, String name) {
		XYGraph xl = ef.createXYGraph(x, y, name);
		xl.setLineVisible(false);
		xl.setSymbolVisible(true);
		xl.setSymbolShape(SymbolShape.FCIRCLE);
		layer.addGraph(xl);
		return xl;
	}

	/**
	 * Return the plotting area.
	 * 
	 * @return actual plot.
	 */
	public org.jplot2d.element.Plot getPlot() {
		return plot;
	}

	/**
	 * Get drawing panel.
	 * @return panel with image
	 */
	
	public JPlot2DComponent getPanel(){
		
		return complot;
	}

       /**
         * Get grame that keeps averything
         * @return panel with image
         */

        public JFrame getFrame(){

                return frame;
        }


	
	/**
	 * Get drawing factory for custom plots.
	 * @return drawing factory.
	 */
	public ElementFactory getFactory() {
		return ef;
	}
	
	/**
	 * Get drawing factory for custom plots.
	 * @return drawing environment.
	 */
	public RenderEnvironment getEnv() {
		return env;
	}
	
	/**
	 * Get drawing layer.
	 * @return drawing factory.
	 */
	public Layer getLayer() {
		return layer;
	}

	/**
	 * Draw a function. F1D should be created from AIDA.
	 * If You set range during F1D initialization, it will be used.
	 * If not, canvas range is used.
	 * 
	 * @param f1d
	 *            input function;
	 * @return Graphs that can be styled.
	 */

	public XYGraph draw(F1D f1d) {
		
		
		if (f1d.getMin() == f1d.getMax()) {
		double x1=(xaxis.getTickManager().getAxisTransform()).getRange().getMin();
		double x2=(xaxis.getTickManager().getAxisTransform()).getRange().getMax();
		f1d.eval(x1,x2,f1d.getPoints()); // evaluate first
		} else {	
			f1d.eval(f1d.getMin(), f1d.getMax(), f1d.getPoints()); // evaluate
		}
		
		
		XYGraph xl = ef.createXYGraph(f1d.getArrayX(), f1d.getArrayY(),
				f1d.getName());
		xl.setLineVisible(true);
		xl.setSymbolVisible(false);
		xl.setColor(f1d.getColor());
		float width = f1d.getPenWidth();
		
		xl.setLineStroke(ef.createStroke(width));
		// 0 - solid; 1- dashed; 2-dot-dashed-line; 3: dotted
		int lstyle = f1d.getLineStyle();
		if (lstyle == 1)
			xl.setLineStroke(ef.createStroke(width, new float[] { 12, 6 }));
		if (lstyle == 2)
			xl.setLineStroke(ef.createStroke(width,
					new float[] { 1, 3, 6, 3 }));
		if (lstyle == 3)
			xl.setLineStroke(ef.createStroke(width, new float[] { 6, 6 }));

		layer.addGraph(xl);
		return xl;
	}

	/**
	 * Exports the image to some graphic format.
	 */
	private void exportDialog() {

		    final JFileChooser fc = jhplot.gui.CommonGUI
				.openImageFileChooser(frame);
		
	
		    /*
		
		    //final JFileChooser fc = new JFileChooser();
		    final File sFile = new File("scavis.pdf");
		    fc.setSelectedFile(sFile);
		    // Store this filter in a variable to be able to select this after adding all FileFilter
		    // because addChoosableFileFilter add FileFilter in order in the combo box
		   
		    /*
		    final FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("PDF document (*.pdf)", "pdf");
		    fc.addChoosableFileFilter(excelFilter);
		    fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG image (*.png)", "png"));
		    // Force the excel filter
		    fc.setFileFilter(excelFilter);
		    */
		    // Disable All Files
		    //fc.setAcceptAllFileFilterUsed(false);

		    // debug
		    /*
		    fc.addPropertyChangeListener(new PropertyChangeListener() {

		        public void propertyChange(PropertyChangeEvent evt) {
		            System.out.println("Property name=" + evt.getPropertyName() + ", oldValue=" + evt.getOldValue() + ", newValue=" + evt.getNewValue());
		            System.out.println("getSelectedFile()=" + fc.getSelectedFile());
		        }
		    });
		    */
		    
		    /*
		    fc.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener() {

		        public void propertyChange(PropertyChangeEvent evt) {
		            Object o = evt.getNewValue();
		            if (o instanceof FileNameExtensionFilter) {
		                FileNameExtensionFilter filter = (FileNameExtensionFilter) o;

		                String ex = filter.getExtensions()[0];

		                File selectedFile = fc.getSelectedFile();
		                if (selectedFile == null) {
		                    selectedFile = sFile;
		                }
		                String path = selectedFile.getName();
		                path = path.substring(0, path.lastIndexOf("."));

		                fc.setSelectedFile(new File(path + "." + ex));
		            }
		        }
		    });
		
		    */

			if (fc.showDialog(frame, "Export to ") == 0) {
				// final File scriptFile = fileChooser.getSelectedFile();
				final File scriptFile = jhplot.io.images.ExportVGraphics
						.getSelectedFileWithExtension(fc);
				// System.out.println(scriptFile.getAbsolutePath());

				if (scriptFile == null)
					return;
				else if (scriptFile.exists()) {
					int res = JOptionPane.showConfirmDialog(frame,
							"The file exists. Do you want to overwrite the file?",
							"", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.NO_OPTION)
						return;
				}
				String mess = "write image  file ..";
				Thread t = new Thread(mess) {
					public void run() {
						export(scriptFile.getAbsolutePath());
					};
				};
				t.start();
			}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		JFileChooser fileChooser = jhplot.gui.CommonGUI
				.openImageFileChooser(frame);
		
		

		
		// set predefined file
		File file = new File("scavis.pdf");
		fileChooser.setSelectedFile(file);

		if (fileChooser.showDialog(frame, "Export to ") == 0) {
			// final File scriptFile = fileChooser.getSelectedFile();
			final File scriptFile = jhplot.io.images.ExportVGraphics
					.getSelectedFileWithExtension(fileChooser);
			// System.out.println(scriptFile.getAbsolutePath());

			if (scriptFile == null)
				return;
			else if (scriptFile.exists()) {
				int res = JOptionPane.showConfirmDialog(frame,
						"The file exists. Do you want to overwrite the file?",
						"", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.NO_OPTION)
					return;
			}
			String mess = "write image  file ..";
			Thread t = new Thread(mess) {
				public void run() {
					export(scriptFile.getAbsolutePath());
				};
			};
			t.start();
		}

*/
		    
	}


		
}
