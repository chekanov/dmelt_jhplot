package jhplot;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.io.File;

import javax.swing.JOptionPane;

import jhplot.gui.HelpBrowser;
import ptolemy.plot.EditablePlot;
import ptolemy.plot.plotml.EditablePlotMLApplication;

//////////////////////////////////////////////////////////////////////////

/**
 * A class to build a simple canvas with data points. Based on PTOLEMY package.
 * This class is simpler than HPlot class, and has a low memory consumption.
 * 
 * @author S.Chekanov
 * 
 */
public class SPlot {

	private EditablePlotMLApplication eplot;
	private EditablePlot pplot;
	private int width = 600;
	private int height = 400;
	private int plot_index = 0;
        private boolean  isSetRange=false;
	/**
	 * Construct a frame with a canvas. Default: do not show it.
	 */
	public SPlot() {
                isSetRange=false;
		width = 600;
		height = 400;
		buildGUI();

	}

         /**
         * Construct a frame with a canvas and open XML file with the data. 
         * Default: do not show it.
         * @param f
         *         file to read.
         */
        public SPlot(File f) {
                isSetRange=false;
                width = 600;
                height = 400;
                buildGUI();
                read(f); 
        }


	/**
	 * Construct a frame with the plot. Do not show it.
	 * 
	 * @param width
	 *            frame width
	 * @param height
	 *            frame height
	 */
	public SPlot(int width, int height) {
                isSetRange=false;
		this.width = width;
		this.height = height;
		buildGUI();

	}


 /**
         * Construct a frame with the plot. Do not show it.
         *
         * @param title
         *i          Title
         * @param width
         *            frame width
         * @param height
         *            frame height
         */
        public SPlot(String title, int width, int height) {
                isSetRange=false;
                this.width = width;
                this.height = height;
                buildGUI();
                pplot.setTitle(title);
        }


       
 /**
         * Set size of the frame.
         * @param width
         *            frame width
         * @param height
         *            frame height
         */
        public void setSize(int width, int height) {
                this.width = width;
                this.height = height;
                eplot.setSize(width, height);
 
        }


	/**
	 * Specify which dataset is editable.
	 * 
	 * @param index
	 *            index of data set
	 */
	public void setEditable(int index) {

		pplot.setEditable(index);

	}

	/**
	 * Get the data in the specified dataset.
	 * 
	 * @param index
	 *            index of data set
	 * @return plotted data
	 */
	public double[][] getData(int index) {

		return pplot.getData(index);
	}

	/**
	 * Set background for the plot.
	 * 
	 * @param c
	 *            background color.
	 */
	public void setBackground(Color c) {

		pplot.setBackground(c);

	}

	/**
	 * Set global title for this plot.
	 * 
	 * @param title
	 *            plot title
	 */
	public void setGTitle(String title) {

		pplot.setTitle(title);

	}

	/**
	 * Set global title for this plot.
	 * 
	 * @param title
	 *            plot title
	 */
	public void setTitle(String title) {

		pplot.setTitle(title);

	}

	/**
	 * Set range for X and Y axes.
	 * 
	 * @param xmin
	 *            Min for X
	 * @param xmax
	 *            Max for X
	 * @param ymin
	 *            Min for Y
	 * @param ymax
	 *            Max for Y
	 */
	public void setRange(double xmin, double xmax, double ymin, double ymax) {
		pplot.setXRange(xmin, xmax);
		pplot.setYRange(ymin, ymax);
                isSetRange=true;

	}

	/**
	 * Add a legend (displayed at the upper right) for the specified data set
	 * with the specified string. Short strings generally fit better than long
	 * strings. If the string is empty, or the argument is null, then no legend
	 * is added.
	 * 
	 * @param dataset
	 *            The dataset index.
	 * @param legend
	 *            The label for the dataset.
	 * @see #renameLegend(int, String)
	 */
	public void addLegend(int dataset, String legend) {

		pplot.addLegend(dataset, legend);
	}

	/**
	 * Specify a tick mark for the X axis. The label given is placed on the axis
	 * at the position given by position. 
	 * If this is called once or
	 * more, automatic generation of tick marks is disabled. 
	 * The tick mark will
	 * appear only if it is within the X range.
	 * 
	 * @param label
	 *            The label for the tick mark.
	 * @param position
	 *            The position on the X axis.
	 */
	public synchronized void addXTick(String label, double position) {

		pplot.addXTick(label, position);
	}

	/**
	 * Specify a tick mark for the Y axis. The label given is placed on the axis
	 * at the position given by position. 
	 * If this is called once or
	 * more, automatic generation of tick marks is disabled. The tick mark will
	 * appear only if it is within the Y range.
	 * 
	 * @param label
	 *            The label for the tick mark.
	 * @param position
	 *            The position on the Y axis.
	 */
	public synchronized void addYTick(String label, double position) {

		pplot.addYTick(label, position);

	}

	/**
	 *  Clear the plot of all data points. 
	 */
	public synchronized void clear() {
		pplot.clear(true);
                update(); 
	}



	/**
	 * Clear all legends. This will show up on the next redraw.
	 */
	public synchronized void clearLegends() {
		pplot.clearLegends();
                update();
	}

	/**
	 * Get the point colors.
	 * 
	 * @return Array of colors
	 * @see #setColors(Color[])
	 */
	public Color[] getColors() {
		return pplot.getColors();
	}

	/**
	 * Return whether the grid is drawn.
	 * 
	 * @return True if a grid is drawn.
	 */
	public boolean getGrid() {
		return pplot.getGrid();
	}

	/**
	 * Get the legend for a dataset, or null if there is none.
	 * 
	 * @param dataset
	 *            The dataset index.
	 * @return The legend label, or null if there is none.
	 */
	public String getLegend(int dataset) {

		return pplot.getLegend(dataset);
	}

	/**
	 * Given a legend string, return the corresponding dataset or -1 if no
	 * legend was added with that legend string.
	 * 
	 * @param legend
	 *            The String naming the legend
	 * @return The legend dataset, or -1 if not found.
	 */
	public int getLegendDataset(String legend) {

		return pplot.getLegendDataset(legend);
	}

	/**
	 * Get the current plot rectangle. Note that Rectangle returned by this
	 * method is calculated from the values. The value passed in by
	 * setPlotRectangle() is not directly used, thus calling getPlotRectangle()
	 * may not return the same rectangle that was passed in with
	 * setPlotRectangle().
	 * 
	 * @return Rectangle
	 */
	public Rectangle getPlotRectangle() {
		return pplot.getPlotRectangle();
	}

	/**
	 * Get the title of the graph, or an empty string if there is none.
	 * 
	 * @return The title.
	 */
	public String getTitle() {

		return pplot.getTitle();
	}

	/**
	 * Get the range for X values of the data points registered so far. Usually,
	 * derived classes handle managing the range by checking each new point
	 * against the current range.
	 * 
	 * @return An array of two doubles where the first element is the minimum
	 *         and the second element is the maximum.
	 * @see #getXRange()
	 */
	public double[] getXAutoRange() {
		return pplot.getXAutoRange();
	}

	/**
	 * If the argument is false, draw the plot without using color (in black and
	 * white). Otherwise, draw it in color (the default).
	 * 
	 * @param useColor
	 *            False to draw in back and white.
	 */
	public void setColor(boolean useColor) {
		pplot.setColor(useColor);
	}

	/**
	 * Set the point colors. Note that the default colors have been carefully
	 * selected to maximize readability and that it is easy to use colors that
	 * result in a very ugly plot.
	 * 
	 * @param colors
	 *            Array of colors to use in succession for data sets.
	 * @see #getColors()
	 */
	public void setColors(Color[] colors) {
		pplot.setColors(colors);

	}

	/**
	 * Set the foreground color.
	 * 
	 * @param foreground
	 *            The foreground color.
	 */
	public void setForeground(Color foreground) {
		pplot.setForeground(foreground);
	}

	/**
	 * Control whether the grid is drawn.
	 * 
	 * @param grid
	 *            If true, a grid is drawn.
	 */
	public void setGrid(boolean grid) {

		pplot.setGrid(grid);
	}

	/**
	 * Set the label font, which is used for axis labels and legend labels. The
	 * font names understood are those understood by java.awt.Font.decode().
	 * 
	 * @param name
	 *            A font name.
	 */
	public void setLabelFont(String name) {

		pplot.setLabelFont(name);

	}

	/**
	 * Get the label for the X (horizontal) axis, or null if none has been set.
	 * 
	 * @return The X label.
	 */
	public String getXLabel() {
		return pplot.getXLabel();
	}

	/**
	 * Return whether the X axis is drawn with a logarithmic scale.
	 * 
	 * @return True if the X axis is logarithmic.
	 */
	public boolean getXLog() {
		return pplot.getXLog();
	}

	/**
	 * Get the X range.
	 * This method returns the values passed in as arguments to
	 * setXRange(double, double). If setXRange(double, double) has not been
	 * called, then this method returns the range of the data to be plotted,
	 * which might not be all of the data due to zooming.
	 * 
	 * @return An array of two doubles where the first element is the minimum
	 *         and the second element is the maximum.
	 * @see #getXAutoRange()
	 */
	public double[] getXRange() {

		return pplot.getXRange();
	}

	/**
	 * Get the X ticks that have been specified, or null if none. The return
	 * value is an array with two vectors, the first of which specifies the X
	 * tick locations (as instances of Double), and the second of which
	 * specifies the corresponding labels.
	 * 
	 * @return The X ticks.
	 */
	public synchronized Vector[] getXTicks() {
		return pplot.getXTicks();
	}

	/**
	 * Get the range for Y values of the data points registered so far. Usually,
	 * derived classes handle managing the range by checking each new point
	 * against the range.
	 * 
	 * @return An array of two doubles where the first element is the minimum
	 *         and the second element is the maximum.
	 * @see #getYRange()
	 */
	public double[] getYAutoRange() {
		return pplot.getYAutoRange();
	}

	/**
	 * Get the label for the Y (vertical) axis, or null if none has been set.
	 * 
	 * @return The Y label.
	 */
	public String getYLabel() {
		return pplot.getYLabel();
	}

	/**
	 * Return whether the Y axis is drawn with a logarithmic scale.
	 * 
	 * @return True if the Y axis is logarithmic.
	 */
	public boolean getYLog() {
		return pplot.getYLog();
	}

	/**
	 * Get the Y range. If {@link #setYRange(double, double)} has been called,
	 * then this method returns the values passed in as arguments to
	 * setYRange(double, double). If setYRange(double, double) has not been
	 * called, then this method returns the range of the data to be plotted,
	 * which might not be all of the data due to zooming.
	 * 
	 * @return An array of two doubles where the first element is the minimum
	 *         and the second element is the maximum.
	 * @see #getYAutoRange()
	 */
	public double[] getYRange() {
		return pplot.getYRange();

	}

	/**
	 * Get the Y ticks that have been specified, or null if none. The return
	 * value is an array with two vectors, the first of which specifies the Y
	 * tick locations (as instances of Double), and the second of which
	 * specifies the corresponding labels.
	 * 
	 * @return The Y ticks.
	 */
	public Vector[] getYTicks() {

		return pplot.getYTicks();
	}

	/**
	 * Rename a legend.
	 * 
	 * @param dataset
	 *            The dataset of the legend to be renamed. If there is no
	 *            dataset with this value, then nothing happens.
	 * @param newName
	 *            The new name of legend.
	 * @see #addLegend(int, String)
	 */
	public void renameLegend(int dataset, String newName) {
		pplot.renameLegend(dataset, newName);
	}

	
	
	/**
	 * Reset the X and Y axes to the ranges that were first specified using
	 * setXRange() and setYRange(). If these methods have not been called, then
	 * reset to the default ranges. This method calls repaint(), which
	 * eventually causes the display to be updated.
	 */
	public void resetAxes() {
		pplot.resetAxes();
	}

	/**
	 * Move and resize this component. The new location of the top-left corner
	 * is specified by x and y, and the new size is specified by width and
	 * height. This overrides the base class method to make a record of the new
	 * size.
	 * 
	 * @param x
	 *            The new x-coordinate of this component.
	 * @param y
	 *            The new y-coordinate of this component.
	 * @param width
	 *            The new width of this component.
	 * @param height
	 *            The new height of this component.
	 */
	public void setBounds(int x, int y, int width, int height) {
		pplot.setBounds(x, y, width, height);
	}

	/**
	 * Set the title font. The font names understood are those understood by
	 * java.awt.Font.decode().
	 * 
	 * @param name
	 *            A font name.
	 */
	public void setTitleFont(String name) {
		pplot.setTitleFont(name);
	}

	/**
	 * Specify whether the X axis is wrapped. If it is, then X values that are
	 * out of range are remapped to be in range using modulo arithmetic. The X
	 * range is determined by the most recent call to setXRange() (or the most
	 * recent zoom). If the X range has not been set, then use the default X
	 * range, or if data has been plotted, then the current fill range.
	 * 
	 * @param wrap
	 *            If true, wrapping of the X axis is enabled.
	 */
	public void setWrap(boolean wrap) {
		pplot.setWrap(wrap);
	}

	/**
	 * Set the label for the X (horizontal) axis.
	 * 
	 * @param label
	 *            The label.
	 */
	public void setNameX(String label) {
		pplot.setXLabel(label);
	}

	/**
	 * Specify whether the X axis is drawn with a logarithmic scale. If you
	 * would like to have the X axis drawn with a logarithmic axis, then
	 * setXLog(true) should be called before adding any data points.
	 * 
	 * @param xlog
	 *            If true, logarithmic axis is used.
	 */
	public void setXLog(boolean xlog) {
		pplot.setXLog(xlog);
	}

	/**
	 * Set the X (horizontal) range of the plot. If this is not done explicitly,
	 * then the range is computed automatically from data available when the
	 * plot is drawn. If min and max are identical, then the range is
	 * arbitrarily spread by 1.
	 * 
	 * @param min
	 *            The left extent of the range.
	 * @param max
	 *            The right extent of the range.
	 */
	public void setXRange(double min, double max) {
		pplot.setXRange(min, max);
	}

	/**
	 * Set the label for the Y (vertical) axis.
	 * 
	 * @param label
	 *            The label.
	 */
	public void setNameY(String label) {
		pplot.setYLabel(label);
	}

	/**
	 * Specify whether the Y axis is drawn with a logarithmic scale. If you
	 * would like to have the Y axis drawn with a logarithmic axis, then
	 * setYLog(true) should be called before adding any data points.
	 * 
	 * @param ylog
	 *            If true, logarithmic axis is used.
	 */
	public void setYLog(boolean ylog) {
		pplot.setYLog(ylog);
	}

	/**
	 * Set the Y (vertical) range of the plot. If this is not done explicitly,
	 * then the range is computed automatically from data available when the
	 * plot is drawn. If min and max are identical, then the range is
	 * arbitrarily spread by 0.1.
	 * 
	 * @param min
	 *            The bottom extent of the range.
	 * @param max
	 *            The top extent of the range.
	 */
	public void setYRange(double min, double max) {
		pplot.setYRange(min, max);
	}

	/**
	 * Zoom in or out to the specified rectangle. This method calls repaint().
	 * 
	 * @param lowx
	 *            The low end of the new X range.
	 * @param lowy
	 *            The low end of the new Y range.
	 * @param highx
	 *            The high end of the new X range.
	 * @param highy
	 *            The high end of the new Y range.
	 */
	public void zoom(double lowx, double lowy, double highx, double highy) {

		pplot.zoom(lowx, lowy, highx, highy);
	}

	/**
	 * Write the current plot to a XML file
	 * 
	 * @param file
	 *            File name
	 */
	public void write(String file) {
		try {
			FileOutputStream fout = new FileOutputStream(file);
			pplot.write(fout);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(pplot, "Error writing file:\n"
					+ ex.toString(), "Ptolemy Plot Error",
					JOptionPane.WARNING_MESSAGE);
		}
	}




	/**
	 * Read a XML file and build a plot
	 * 
	 * @param file
	 *            File name to read
	 */
	public void read(String file) {
		try {
			pplot.clear(true);
			pplot.read(new FileInputStream(file));
			pplot.repaint();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(pplot, "File not found:\n"
					+ ex.toString(), "Ptolemy Plot Error",
					JOptionPane.WARNING_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(pplot, "Error reading input:\n"
					+ ex.toString(), "Ptolemy Plot Error",
					JOptionPane.WARNING_MESSAGE);
		}

	}


      

 /**
         * Read a XML file and build a plot
         *
         * @param file
         *            File name to read
         */
        public void read(File file) {
                try {
                        System.out.println("reading file ..");
                        pplot.clear(true);
                        pplot.read(new FileInputStream(file));
                        pplot.repaint();
                } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(pplot, "File not found:\n"
                                        + ex.toString(), "Ptolemy Plot Error",
                                        JOptionPane.WARNING_MESSAGE);
                } catch (IOException ex) {
                        JOptionPane.showMessageDialog(pplot, "Error reading input:\n"
                                        + ex.toString(), "Ptolemy Plot Error",
                                        JOptionPane.WARNING_MESSAGE);
                }

        }




	/**
	 * Draw multiple sets as marks (default). 
	 * 
	 * @param x
	 *            array of x
	 * @param y
	 *            array of y
	 */
	public void draw(double[][] x, double[][] y) {

               pplot.setMarksStyle("various");
               pplot.setConnected(false,plot_index);

		for (int n = 0; n < x[0].length; n++) {
			for (int i = 0; i < x.length; i++) {
				pplot.addPoint(n, x[i][n], y[i][n], true);
			}
			plot_index++;
		}

                update();

	}



        /**
         * Draw a single data set as marks (default). 
         *
         * @param x
         *            array of x values
         * @param y
         *            array of y values
         */
        public void draw(double[] x, double[] y) {

               pplot.setMarksStyle("various");
               pplot.setConnected(false,plot_index);

                        for (int i = 0; i < x.length; i++) {
                                pplot.addPoint(plot_index, x[i], y[i], true);
                        }
             plot_index++;

             update(); 

        }

  /**
         * Draw a single data set with a legend. 
         *
         * @param legend
         *              legend to show
         * @param x
         *            array of x values
         * @param y
         *            array of y values
         */
        public void draw(String legend, double[] x, double[] y) {

                        pplot.setMarksStyle("various");
                        pplot.setConnected(false,plot_index);

                         pplot.addLegend(plot_index, legend); 
                         for (int i = 0; i < x.length; i++) {
                                pplot.addPoint(plot_index, x[i], y[i], true);
                        }
             plot_index++;
             update(); 

        }


   /**
         * Draw a single data set with errors on Y with legend. 
         * 
         * @param legend
         *            legend to show
         * @param x
         *            array of x values
         * @param y
         *            array of y values
         * @param ey
         *            array of errors on y values
         */
        public void draw(String legend, double[] x, double[] y, double[] ey) {

                        pplot.setMarksStyle("various");
                        pplot.setConnected(false,plot_index);

                        pplot.addLegend(plot_index, legend);
                        for (int i = 0; i < x.length; i++) {
                        pplot.addPointWithErrorBars(plot_index, x[i], y[i], 
                                                     ey[i], ey[i],  true);

                        }
            plot_index++;

            update(); 

        }





	/**
	 * Clear the plot of data points in the specified dataset. This calls
	 * repaint() to request an update of the display.
	 * <p>
	 * In order to work well with swing and be thread safe, this method actually
	 * defers execution to the event dispatch thread, where all user interface
	 * actions are performed. Thus, the point will not be added immediately
	 * (unless you call this method from within the event dispatch thread). If
	 * you call this method, the addPoint() method, and the erasePoint() method
	 * in any order, they are assured of being processed in the order that you
	 * called them.
	 * 
	 * @param dataset
	 *            The dataset to clear.
	 */
	public void clear(int dataset) {

		if (dataset < plot_index)
			pplot.clear(dataset);

                update(); 
	}

	/**
	 * Erase the point at the given index in the given dataset. If lines are
	 * being drawn, also erase the line to the next points (note: not to the
	 * previous point). The point is not checked to see whether it is in range,
	 * so care must be taken by the caller to ensure that it is.
	 * <p>
	 * In order to work well with swing and be thread safe, this method actually
	 * defers execution to the event dispatch thread, where all user interface
	 * actions are performed. Thus, the point will not be erased immediately
	 * (unless you call this method from within the event dispatch thread). All
	 * the methods that do this deferring coordinate so that they are executed
	 * in the order that you called them.
	 * 
	 * @param dataset
	 *            The data set index.
	 * @param index
	 *            The index of the point to erase.
	 */
	public void erasePoint(int dataset, int index) {
		pplot.erasePoint(dataset, index);
	}

	/**
	 * Rescale so that the data that is currently plotted just fits. This
	 * overrides the base class method to ensure that the protected variables
	 * _xBottom, _xTop, _yBottom, and _yTop are valid. This method calls
	 * repaint(), which eventually causes the display to be updated.
	 * <p>
	 * In order to work well with swing and be thread safe, this method actually
	 * defers execution to the event dispatch thread, where all user interface
	 * actions are performed. Thus, the fill will not occur immediately (unless
	 * you call this method from within the event dispatch thread). All the
	 * methods that do this deferring coordinate so that they are executed in
	 * the order that you called them.
	 */
	public void fillPlot() {

		pplot.fillPlot();

	}

        /**
         * Rescale so that the data that is currently plotted just fits. 
         */ 
        public void setAutoRange() {
                isSetRange=false; 
        }


         /**
         * Rescale so that the data that is currently plotted just fits. 
         * @param auto if true, then autorange 
         */
        public void setAutoRange(boolean auto) {
                if ( auto) isSetRange=false;
                if ( !auto) isSetRange=true;         
        }

        /**
         * Repaint the plot and update all graphics.
         */
        public void update() {
               if (isSetRange == false) pplot.fillPlot();
               else  {pplot.repaint(); pplot.updateUI(); } 

               // pplot.resetAxes();
               // pplot.fillPlot();
               // pplot.repaint(); 
        }

	/**
	 * Return the actual number of data sets.
	 * 
	 * @return The number of data sets that have been created.
	 */
	public int getNumDataSets() {
		return pplot.getNumDataSets();
	}

	/**
	 * Turn bars on or off (for bar charts). Note that this is a global
	 * property, not per dataset.
	 * 
	 * @param on
	 *            If true, turn bars on.
	 */
	public void setBars(boolean on) {
		pplot.setBars(on);
	}

	/**
	 * Turn bars on and set the width and offset. Both are specified in units of
	 * the x axis. The offset is the amount by which the ith data
	 * set is shifted to the right, so that it peeks out from behind the earlier
	 * data sets.
	 * 
	 * @param width
	 *            The width of the bars.
	 * @param offset
	 *            The offset per data set.
	 */
	public void setBars(double width, double offset) {
		pplot.setBars(width, offset);
	}

	/**
	 * If the first argument is true, then by default for the specified dataset,
	 * points will be connected by a line. Otherwise, the points will not be
	 * connected. When points are by default connected, individual points can be
	 * not connected by giving the appropriate argument to addPoint(). Note that
	 * this method should be called before adding any points. Note further that
	 * this method should probably be called from the event thread.
	 * 
	 * @param on
	 *            If true, draw lines between points.
	 * @param dataset
	 *            The dataset to which this should apply.
	 */
	public void setConnected(boolean on, int dataset) {

		pplot.setConnected(on, dataset);

	}

	/** In the specified data set, add the specified x, y point to the
     *  plot.  Data set indices begin with zero.  If the data set
     *  does not exist, create it.  The fourth argument indicates
     *  whether the point should be connected by a line to the previous
     *  point.  Regardless of the value of this argument, a line will not
     *  drawn if either there has been no previous point for this dataset
     *  or setConnected() has been called with a false argument.
     *  <p>
     *  In order to work well with swing and be thread safe, this method
     *  actually defers execution to the event dispatch thread, where
     *  all user interface actions are performed.  Thus, the point will
     *  not be added immediately (unless you call this method from within
     *  the event dispatch thread). All the methods that do this deferring
     *  coordinate so that they are executed in the order that you
     *  called them.
     *
     *  @param dataset The data set index.
     *  @param x The X position of the new point.
     *  @param y The Y position of the new point.
     *  @param connected If true, a line is drawn to connect to the previous
     *   point.
     */
	 public void addPoint(int dataset, double x,
             double y, boolean connected) {
      pplot.addPoint(dataset, x, y, connected);

   }
	
	 
	 
	 
	 /** In the specified data set, add the specified x, y point to the
	     *  plot with error bars.  Data set indices begin with zero.  If
	     *  the dataset does not exist, create it.  yLowEB and
	     *  yHighEB are the lower and upper error bars.  The sixth argument
	     *  indicates whether the point should be connected by a line to
	     *  the previous point.
	     *  The new point will be made visible if the plot is visible
	     *  on the screen.  Otherwise, it will be drawn the next time the plot
	     *  is drawn on the screen.
	     *  This method is based on a suggestion by
	     *  Michael Altmann <michael@email.labmed.umn.edu>.
	     *  <p>
	     *  In order to work well with swing and be thread safe, this method
	     *  actually defers execution to the event dispatch thread, where
	     *  all user interface actions are performed.  Thus, the point will
	     *  not be added immediately (unless you call this method from within
	     *  the event dispatch thread).  All the methods that do this deferring
	     *  coordinate so that they are executed in the order that you
	     *  called them.
	     *
	     *  @param dataset The data set index.
	     *  @param x The X position of the new point.
	     *  @param y The Y position of the new point.
	     *  @param yLowEB The low point of the error bar.
	     *  @param yHighEB The high point of the error bar.
	     *  @param connected If true, a line is drawn to connect to the previous
	     *   point.
	     */
	    public void addPointErr(int dataset,
	            double x, double y, double yLowEB,
	            double yHighEB,  boolean connected) {
	    	     pplot.addPointWithErrorBars(dataset, x, y, 
	    	    		 yLowEB, yHighEB, connected);
	    	
	    }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	/**
	 * If the first argument is true, then a line will be drawn from any plotted
	 * point in the specified dataset down to the x axis. Otherwise, this
	 * feature is disabled. A plot with such lines is also known as a stem plot.
	 * 
	 * @param on
	 *            If true, draw a stem plot.
	 * @param dataset
	 *            The dataset to which this should apply.
	 */
	public void setImpulses(boolean on, int dataset) {
		pplot.setImpulses(on, dataset);
	}

	/**
	 * Set the marks style to "none", "points", "dots", or "various". In the
	 * last case, unique marks are used for the first ten data sets, then
	 * recycled. This method should be called only from the event dispatch
	 * thread.
	 * 
	 * @param style
	 *            A string specifying the style for points.
	 */
	public void setMarksStyle(String style) {

		pplot.setMarksStyle(style);

	}

	/**
	 * Set the marks style to "none", "points", "dots", "various", or "pixels"
	 * for the specified dataset. In the last case, unique marks are used for
	 * the first ten data sets, then recycled.
	 * 
	 * @param style
	 *            A string specifying the style for points.
	 * @param dataset
	 *            The dataset to which this should apply.
	 */
	public  void setMarksStyle(String style, int dataset) {
		pplot.setMarksStyle(style, dataset);
	}


         /**
         * Draw H1D histograms 
         *
         * @param h1 H1D histogram to be shown. 
         */
        public void draw(H1D h1) {

         pplot.setBars(true);
         pplot.setMarksStyle("none");
         pplot.setConnected(false,plot_index); 

         hep.aida.ref.histogram.Histogram1D h = h1.get();
         // hep.aida.ref.histogram.IAxis ax = h.axis();
         hep.aida.IAxis ax = h.axis();

          double min = ax.lowerEdge();
          double max = ax.upperEdge();
          int bins = ax.bins();
          double width=(max-min)/bins;
          //pplot.setBars(width, 0.05*width); 

                for (int i = 0; i <bins; i++) {
                        double dd = ax.binCenter(i);
                        double hh = h1.binHeight(i);
                        // double errX1 = dd - ax.binLowerEdge(i);
                        // double errX2 = ax.binUpperEdge(i) - dd;
                        // double errY = h.binError(i); // Math.sqrt(hh);
                        pplot.addPoint(plot_index, dd, hh,true);


                        // pplot.addPointWithErrorBars(plot_index, dd, hh 
                        //                       , errY, errY, false);

                }
 
         plot_index++;

         update();

        }
	
	/**
	 * Draw P1D object. 
	 * If errors are included to P1D, they will be shown.
	 * 
	 * @param p1d P1D object to show
	 */
	public void draw(P1D p1d) {


            setColor(true);
            Color[] _colors = { p1d.getColor(), 
            new Color(0x0000ff), // blue
            new Color(0x00aaaa), // cyan-ish
            new Color(0x000000), // black
            new Color(0xffa500), // orange
            new Color(0x53868b), // cadetblue4
            new Color(0xff7f50), // coral
            new Color(0x45ab1f), // dark green-ish
            new Color(0x90422d), // sienna-ish
            new Color(0xa0a0a0), // grey-ish
            new Color(0x14ff14), // green-ish
             };
              
              setColors(_colors);
  
               if (p1d.getSymbol() == 11) {
                           setMarksStyle( "pixels"); 
               } else {
                        pplot.setMarksStyle("various");
               }

               pplot.setConnected(false,plot_index);

		for (int i = 0; i < p1d.size(); i++) {
			if (p1d.dimension() > 2) {
				pplot.addPointWithErrorBars(plot_index, p1d.getX(i), p1d
						.getY(i), p1d.getYlower(i), p1d.getYupper(i), true);
			} else {
				pplot.addPoint(plot_index, p1d.getX(i), p1d.getY(i), true);
			}

		}
		plot_index++;
                update();	

         }






	/**
	 * Set sets of data with legends
	 * 
	 * @param legend
	 *            array of legends
	 * @param x
	 *            array of x
	 * @param y
	 *            array of y
	 */
	public void draw(String legend[], double[][] x, double[][] y) {


               pplot.setMarksStyle("various");
               pplot.setConnected(false,plot_index);

		for (int n = 0; n < x[0].length; n++) {
			pplot.addLegend(n, legend[n]);
			for (int i = 0; i < x.length; i++) {
				pplot.addPoint(n, x[i][n], y[i][n], true);
			}
			plot_index++;
		}



              update();


	}

	/**
	 * Show or not the frame
	 * 
	 * @param showIt
	 *            true if should be shown
	 */

	public void visible(boolean showIt) {

		eplot.showIt(showIt);
                
	}

	/**
	 * Show the frame.
	 */

	public void visible() {

		eplot.showIt(true);

	}


        /**
         * Set the canvas frame visible. Also set its location.
         * @param posX -  the x-coordinate of the new location's top-left corner in the parent's coordinate space;
         * @param posY - he y-coordinate of the new location's top-left corner in the parent's coordinate space 
         */
        public void visible(int posX, int posY) {
                eplot.setLocation(posX, posY);
                eplot.setVisible(true);

        }
	

 /**
         * Build GUI frame
         */
        private void buildGUI() {
                  plot_index = 0;
                  String[] tt = new String[0];
 
              try {
                  pplot = new EditablePlot();
                  pplot.setLabelFont("SansSerif-BOLD-14");
                  eplot = new EditablePlotMLApplication(pplot, tt);
                  eplot.setSize(width, height);
                  pplot.setConnected(false); 
                  } catch (Exception ex) {
                        System.err.println(ex.toString());
                        ex.printStackTrace();
                }
                pplot.setBackground(Color.white);
  }



 public static void main(String[] args)
   {


    if (args.length>0) {

    System.out.println("reading file="+args[0]);
    SPlot sp= new SPlot(new File(args[0]));
    sp.visible();
    sp.update();

    } else { 

    SPlot sp= new SPlot("Test",600,400);
    sp.visible();
    Random rand = new Random();
    double[] x= new double[500];
    double[] y= new double[500];

    for (int i=0; i<500; i++) { 
        x[i]=rand.nextGaussian();
        y[i]=rand.nextGaussian();
    }
     sp.draw(x,y);
     sp.update();
    }

  }



         /**
         * Fast export of the canvas to an image file (depends on the extension,
         * i.e. PNG, JPG,  EPS, PS). No questions will be asked, an existing file
         * will be rewritten
         * 
         * @param f
         *            Output file with the proper extension. If no extension, PNG
         *            file is assumed.
         *  
         */
           public  void export(String f) {
                pplot.export(f); 
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
