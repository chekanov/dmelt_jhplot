// * This code is licensed under:
// * jHplot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot;

import hep.aida.ref.histogram.Histogram2D;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;
import jhplot.utils.HelpDialog;

import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.contour.DefaultContourColoringPolicy;
import org.jzy3d.contour.MapperContourPictureGenerator;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.ControllerType;
import org.jzy3d.chart.controllers.keyboard.ChartKeyController;
import org.jzy3d.chart.controllers.mouse.ChartMouseController;
import org.jzy3d.chart.controllers.thread.ChartThreadController;
import org.jzy3d.events.ControllerEvent;
import org.jzy3d.events.ControllerEventListener;
import org.jzy3d.factories.JzyFactories;
import org.jzy3d.global.Settings;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.*;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.text.drawable.DrawableTextBitmap;
import org.jzy3d.plot3d.text.drawable.DrawableTextTexture;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.AxeFactory;
import org.jzy3d.plot3d.primitives.axes.ContourAxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ScientificNotationTickRenderer;
import org.jzy3d.plot3d.primitives.enlightables.EnlightableSphere;

/**
 * Create a interactive canvas to show objects in 3D. This canvas uses the GL
 * native libraries. Use it for showing 2D histograms (H2D class), functions
 * (F2D), data points (P2D class).
 * 
 * @author S.Chekanov
 * 
 */

public class HPlotXYZ extends GHFrame {

	private static final long serialVersionUID = 1L;

	public boolean set = true;

	private static int lightId = 0;

	private Chart jpp[][];
	protected static int Nframe = 0;
	protected static int isOpen = 0;
	protected ArrayList<AbstractDrawable> datalist[][];

	final private String help_file = "hplot3d";
	
	
	public boolean currentWiredrame = true;
	public Color  currentFillColor = Color.GREEN;
	public Color  currentWiredColor = Color.BLACK;
	public float  currentWiredWidth = 1.0f;
	public boolean   solidColor = false;
 	
	
	

	/**
	 * Create a canvas to display histograms in 3D
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
	 *            number of plots in y
	 */

	public HPlotXYZ(String title, int xsize, int ysize, int n1, int n2,
			boolean set) {

		super(title, xsize, ysize, n1, n2, set);
		
		
		Settings.getInstance().setHardwareAccelerated(true);

		
		datalist = new ArrayList[N1final][N2final];

		jpp = new Chart[N1final][N2final];
		Nframe = 0;
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {
				datalist[i1][i2] = new ArrayList<AbstractDrawable>();
				jpp[i1][i2] = new Chart(Quality.Advanced,"swing" );
				ChartMouseController mouse = new ChartMouseController();
				jpp[i1][i2].addController(mouse);
				
				 BoundingBox3d b= new BoundingBox3d(0,1.0f, 0, 1.0f, 0, 1.0f);
				 AxeBox ax = new AxeBox(b); 
				 jpp[i1][i2].getView().setAxe(ax);
				
			
				
				if (set) {
					mainPanel.add((JComponent) jpp[i1][i2].getCanvas());
					//mainPanel.setMinimumSize(new Dimension(450,450));
				}
				
				Nframe++;

			}
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
	 * @param minZ
	 *            Min value in Z
	 * @param maxZ
	 *            Max value in Z
	 */

	public void setRange(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
		
	 	BoundingBox3d b= new BoundingBox3d((float)minX, (float)maxX, (float)minY, (float)maxY, (float)minZ, (float)maxZ );
	 	jpp[N1][N2].getView().getAxe().setAxe(b);
	    
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Create a canvas to display histograms in 3D
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

	public HPlotXYZ(String title, int xsize, int ysize, int n1, int n2) {
		this(title, xsize, ysize, n1, n2, true);

	}

	/**
	 * Clear all the frames
	 * 
	 */

	protected void clearFrame() {

	}

	/**
	 * Refresh all the frames
	 * 
	 */

	protected void refreshFrame() {

	}

	/**
	 * Set distance to the object
	 * 
	 * @param distance
	 *            distance
	 */

	public void setDistance(double distance) {
		// sp[N1][N2].getProjector().setDistance((float)distance);
	}

	
	
	/**
	 * Get color as needed from AWT
	 * @param c
	 * @return
	 */
	private Color getColor(java.awt.Color c) {
		 
		return new Color((int)c.getRed(), (int)c.getGreen(), (int)c.getBlue(), (int)c.getAlpha() );
		
	
	}
	
	
	/**
	 * Set fill color for the current object
	 * @param color to fill object
	 */
	public void setColorFill(java.awt.Color color) {
		
		this.currentFillColor=getColor(color);
	}
	
	/**
	 * Set fill color for the current object
	 * @param color for wireframe
	 */
	public void setColorWireframe(java.awt.Color color) {
		this.currentWiredColor=getColor(color);
	}
	
	
	
	/**
	 * Set wireframe mode for the current object.
	 * 
	 * @param wiredrame true than no solid fill is used.
	 */
	public void setWireframe(boolean wiredrame) {
		this.currentWiredrame=wiredrame;
	}
	
	
	
	/**
	 * Set width of the line for wireframe
	 * @param width width
	 */
	public void setWireframeWidth(double  width) {
		this.currentWiredWidth=(float)width;
	}
	
	
	/**
	 * If you set true, color will be solid and no colorfull color map is used.
	 * 
	 * @param solid apply a solid color to the current object
	 */
	public void setColorSolid(boolean solid) {
		this.solidColor=solid;
	}
	
	/**
	 * Get the distance
	 * 
	 * @return distance
	 */
	// public double getDistance() {
	// return sp[N1][N2].getProjector().getDistance();
	// }

	/**
	 * Set the scaling
	 * 
	 * @param scale
	 */
	public void setScaling(double scale) {

		// sp[N1][N2].getProjector().set2DScaling((float)scale);
	}

	/**
	 * Get the scaling
	 * 
	 * @return
	 */
	// public double getScaling() {
	// return (double)sp[N1][N2].getProjector().get2DScaling();
	// }

	/**
	 * Get the rotation angle
	 * 
	 * @param angle
	 *            angle
	 */
	public void setRotationAngle(double angle) {
		// sp[N1][N2].getProjector().setRotationAngle((float)angle);
	}

	/**
	 * Set face shown or not
	 * 
	 * @param face
	 *            true is displayed.
	 */
	public void setFaceDisplayed(boolean face) {
		jpp[N1][N2].getView().getAxe().getLayout().setFaceDisplayed(face);

	}

	/**
	 * Set Grid color to the current pad
	 * 
	 * @param color
	 *            grid color
	 */
	public void setGridColor(java.awt.Color color) {
		jpp[N1][N2].getView().getAxe().getLayout()
				.setGridColor(new Color(color));

	}

	
	/**
	 * Set visible and insert
	 * 
	 */

	private void showIt() {
		updateAll();
		mainFrame.setVisible(true);

	}



         /**
         * Set the canvas frame visible. Also set its location.
         * @param posX -  the x-coordinate of the new location's top-left corner in the parent's coordinate space;
         * @param posY - he y-coordinate of the new location's top-left corner in the parent's coordinate space 
         */
        public void visible(int posX, int posY) {
                updateAll();
                mainFrame.setLocation(posX, posY);
                mainFrame.setVisible(true);
        }



	/**
	 * Construct a HView2D canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 */
	public HPlotXYZ(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);

	}

	/**
	 * Construct a HGraph canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HPlotXYZ(String title) {

		this(title, 600, 600, 1, 1, true);

	}

	/**
	 * Construct a HGraph canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title "Default"
	 * 
	 */
	public HPlotXYZ() {
		this("Default", 600, 600, 1, 1, true);
	}

	/**
	 * Clear the current graph including graph settings. Note: the current graph
	 * is set by the cd() method
	 */
	public void clear() {
		jpp[N1][N2].clear();
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

		jpp[i1][i2].clear();

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

	/**
	 * Implemented abstract function to close the frame from the menu
	 */
	protected void quitFrame() {
		close();
	}

	/**
	 * Remove the canvas frame
	 */
	public void distroy() {
		mainFrame.setVisible(false);
		close();
		removeFrame();
	}

	/**
	 * exit the frame and clear all components
	 */

	public void quit() {

		doNotShowFrame();
		clearAll();

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				// System.out.println("Clear graph="+Integer.toString(i1)+
				// " " + Integer.toString(i2));
				// clear data
				jpp[i1][i2].clear();
				jpp[i1][i2].dispose();
				jpp[i1][i2] = null;

			}
		}

		jpp = null;

		removeFrame();

	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		// updateAll();
		if (vs == false) {
			mainFrame.setVisible(false);
			mainFrame.validate();
		}

		if (vs)
			showIt();
	}

	/**
	 * Show the canvas
	 */
	public void visible() {
		visible(true);
	}

	/**
	 * Set a name for X axis
	 * 
	 * @param a
	 *            Name of the label for X axis
	 */
	public void setNameX(String a) {

		jpp[N1][N2].getAxeLayout().setXAxeLabel(a);

	}

	/**
	 * Set a name for Y axis
	 * 
	 * @param a
	 *            Name of the label for Y axis
	 */
	public void setNameY(String a) {
		jpp[N1][N2].getAxeLayout().setYAxeLabel(a);
	}

	/**
	 * Set a name for Z axis
	 * 
	 * @param a
	 *            Name of the label for Z axis
	 */
	public void setNameZ(String a) {
		jpp[N1][N2].getAxeLayout().setZAxeLabel(a);
	}

	/**
	 * Show or not label on axes.
	 * 
	 * @param axis
	 *            axis: 0 for X, 1 for Y, 2 for Z
	 * @param shown
	 *            true then shown.
	 */
	public void setAxesLabel(int axis, boolean shown) {

		if (axis == 0)
			jpp[N1][N2].getAxeLayout().setXAxeLabelDisplayed(shown);
		if (axis == 1)
			jpp[N1][N2].getAxeLayout().setYAxeLabelDisplayed(shown);
		if (axis == 2)
			jpp[N1][N2].getAxeLayout().setZAxeLabelDisplayed(shown);
	}

	
	
	/**
	 * Set view point
	 * @param x X
	 * @param y Y
	 * @param z Z
	 */
	public void setViewPoint(double x, double y, double z) {

			jpp[N1][N2].setViewPoint(new Coord3d(x, y, z)); 
		
	}
	
	
	
	/**
	 * Set font and color for axis labels.
	 * @param font
	 * @param color
	 */
	public void setAxesLabelFont(Font font, Color color) {

		//	jpp[N1][N2].getView().getAxe().  // setXAxeLabelDisplayed(shown);
		
	}
	
	
	
	
	
	/**
	 * Show or not ticks on axes.
	 * 
	 * @param axis
	 *            axis: 0 for X, 1 for Y, 2 for Z
	 * @param shown
	 *            true then shown.
	 */
	public void setAxisTick(int axis, boolean shown) {

		if (axis == 0)
			jpp[N1][N2].getAxeLayout().setXTickLabelDisplayed(shown);
		if (axis == 1)
			jpp[N1][N2].getAxeLayout().setYTickLabelDisplayed(shown);
		if (axis == 2)
			jpp[N1][N2].getAxeLayout().setZTickLabelDisplayed(shown);
	}

	/**
	 * Add a container with X,Y.Z values to be shown. Call update() method to
	 * draw all objects.
	 * 
	 * @param h1
	 */
	public void add(P2D h1) {
		add(h1, 1.0f, java.awt.Color.black);
	}

	/**
	 * Add a container with X,Y.Z values to be shown. Call update() method to
	 * draw all objects.
	 * 
	 * @param h1
	 * @param color
	 *            Color of this data.
	 */
	public void add(P2D h1, java.awt.Color color) {
		add(h1, 1.0f, color);
	}

	/**
	 * Add a container with X,Y.Z values to be shown. Call update() method to
	 * draw all objects.
	 * 
	 * @param h1
	 *            input data
	 * @param pointSize
	 *            size of the point
	 */
	public void add(P2D h1, double pointSize) {
		add(h1, pointSize, java.awt.Color.black);
	}

	/**
	 * Draw X,Y.Z values in 3D using variable colors. Better use "add()" method
	 * to add the data to canvas.
	 * 
	 * @param h1
	 * @param penSize
	 *            size of the points
	 * @param color
	 *            to show points
	 */
	public void draw(P2D h1, double penSize, java.awt.Color color) {

		int size = h1.size();
		Coord3d[] points = new Coord3d[size];
		Color[] colors = new Color[size];
		double x, y, z, a;

		for (int i = 0; i < size; i++) {
			x = h1.getX(i);
			y = h1.getY(i);
			z = h1.getZ(i);
			points[i] = new Coord3d(x, y, z);
			a = 0.25f + (float) (points[i].distance(Coord3d.ORIGIN) / Math
					.sqrt(1.3)) / 2;
			colors[i] = new Color((float) x, (float) y, (float) z, (float) a);
		}
		Scatter scatter = new org.jzy3d.plot3d.primitives.Scatter(points);
		scatter.setColor(getColor(color));
		scatter.setWidth((float) penSize);
		jpp[N1][N2].getScene().add(scatter);

	}

	/**
	 * Draw X,Y.Z values in 3D
	 * 
	 * @param h1
	 *            input data
	 * @param penSize
	 *            size of the points
	 * @param color
	 *            to show points
	 * @param 
	 *        return what was added.
	 */
	public AbstractDrawable add(P2D h1, double penSize, java.awt.Color color) {

		int size = h1.size();
		Coord3d[] points = new Coord3d[size];
		double x, y, z, a;

		for (int i = 0; i < size; i++) {
			x = h1.getX(i);
			y = h1.getY(i);
			z = h1.getZ(i);
			points[i] = new Coord3d(x, y, z);

		}
		Scatter scatter = new org.jzy3d.plot3d.primitives.Scatter(points);
		scatter.setColor(getColor(color));
		scatter.setWidth((float) penSize);
		datalist[N1][N2].add(scatter);
		return scatter;

	}

	/**
	 * Add a object to the list
	 * @param shape
	 */
	
	public void add(AbstractDrawable shape){
		
		datalist[N1][N2].add(shape);
	}
	
	
	/**
	 * Draw a list of shapes on the current canvas
	 * @param shapes
	 */
public void draw(AbstractDrawable[] shapes){
		
	for (int i1 = 0; i1 <shapes.length; i1++) {
			jpp[N1][N2].getScene().add(shapes[i1]);

	}
	}
	
	
	/**
	 * Update current pad. All objects added with the method "add" will be
	 * shown.
	 */
	public void update() {

		jpp[N1][N2].getScene().add((List<AbstractDrawable>) datalist[N1][N2]);
	}

	/**
	 * Refresh only a particular pad.
	 * 
	 * @param n1
	 *            the location of the plot in x
	 * @param n2
	 *            the location of the plot in y
	 */
	public void update(int n1, int n2) {

		jpp[n1][n2].getScene().add((List<AbstractDrawable>) datalist[n1][n2]);
	}

	/**
	 * Refresh all the plots on the same canvas.
	 * 
	 */
	public void updateAll() {

		if (N1final == 0 && N2final == 0)
			return;

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				jpp[i1][i2].getScene().add(
						(List<AbstractDrawable>) datalist[i1][i2]);

			}
		}

	}

	
	
	
	/**
	 * Add  a text to the canvas.
	 * 
	 * @param text text 
	 * @param pos position (x,y,z)
	 * @param color color
	 * @return return what is added
	 */
	public AbstractDrawable addText(String text,  double[] pos, java.awt.Color color) {
		
		 DrawableTextBitmap t4 = new DrawableTextBitmap(text, new Coord3d(pos[0],pos[1],pos[2]), new Color(color));
  
		  
	//	 DrawableTextTexture t = new DrawableTextTexture(text, new Coord2d(0,0), new Coord2d(8,1));
		 datalist[N1][N2].add(t4);
		 
		 return t4;


	}
	
	
	
	/**
	 * Add a F2D function (a function with 2 arguments, x and y). The function
	 * will be shows as a surface. This function is plotted with 40 steps in X
	 * and Y.
	 * @param h function to show
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @return
	 */
	public AbstractDrawable add(final F2D h, double xmin, double xmax, double ymin, double ymax) {
		
		
		return add(h, 40, 40, xmin, xmax, ymin, ymax);

	}

	
	
	/**
	 *  Add a F2D function (a function with 2 arguments, x and y). The function
	 * will be shows as a surface. Use "update()" method to draw it.
	 * @param h1
	 * @param xsteps number of divisions in X
	 * @param ysteps number of divisions in Y
	 * @param xmin min X
	 * @param xmax max X
	 * @param ymin min Y
	 * @param ymax max Y
	 * @return
	 */
	public AbstractDrawable add(final F2D h1, int xsteps, int ysteps, double xmin, double xmax, double ymin, double ymax) {

		
		if (h1.getLabelX() != null)
			if (h1.getLabelX().length()>1) setNameX(h1.getLabelX() );
		if (h1.getLabelY() != null)
			if (h1.getLabelY().length()>1) setNameY(h1.getLabelY() );

		Mapper mapper = new Mapper() {
			public double f(double x, double y) {
				return h1.eval(x, y);
			}
		};

		Range xrange = new Range(xmin, xmax);
		Range yrange = new Range(ymin, ymax);
		Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(
				xrange, xsteps, yrange, ysteps), mapper);
		// ColorMapper myColorMapper = new ColorMapper(new ColorMapRainbow(),
		// surface.getBounds().getZmin(), surface.getBounds().getZmax(),
		// new Color(1, 1, 1, .5f));
		// surface.setColorMapper(myColorMapper);

		// Create a chart with contour axe box, and attach the contour picture
		/*
		 * JzyFactories.axe = new AxeFactory() {
		 * 
		 * @Override public IAxe getInstance() { return new ContourAxeBox(box);
		 * } };
		 */
		
		surface.setColor(currentFillColor);
		surface.setWireframeDisplayed(currentWiredrame);
		surface.setWireframeColor(currentWiredColor);	
		surface.setWireframeWidth(currentWiredWidth);
		
		
		 if (solidColor== false){
			 ColorMapper myColorMapper=new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1,1,1,.5f)); 
			 surface.setColorMapper(myColorMapper);
		 };
		

		
		datalist[N1][N2].add(surface);
	
		return surface;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void addContour(Mapper mapper, ColorMapper myColorMapper, Range xrange, Range yrange){
		
		 // Compute an image of the contour
        MapperContourPictureGenerator contour = new MapperContourPictureGenerator(mapper, xrange, yrange);  
        // Create a chart with contour axe box, and attach the contour picture
        JzyFactories.axe = new AxeFactory(){
                @Override
                public IAxe getInstance() {
                        return new ContourAxeBox(box);
                }
        };

        ContourAxeBox cab = (ContourAxeBox)jpp[N1][N2].getView().getAxe();
        cab.setContourImg( contour.getFilledContourImage(new DefaultContourColoringPolicy(myColorMapper), 400, 400, 10), xrange, yrange);

		
	}
	
	
	
	
	
	
	/**
	 * Add a H2D histogram. The histogram will be shows as a surface. Use
	 * "update()" method to draw it.
	 * 
	 * @param h2d
	 *            Histogram for drawing.
	 * @param return what was added
	 *            
	 */
	public AbstractDrawable addAsSurface(final H2D h2d) {

		Histogram2D h1 = h2d.get();

		Mapper mapper = new Mapper() {
			public double f(double x, double y) {

				int ix = h2d.findBinX(x);
				int iy = h2d.findBinY(y);
				return h2d.binHeight(ix, iy);
			}
		};

		int xsteps = h1.xAxis().bins();
		int ysteps = h1.yAxis().bins();
		Range xrange = new Range(h1.xAxis().lowerEdge(), h1.xAxis().upperEdge());
		Range yrange = new Range(h1.yAxis().lowerEdge(), h1.yAxis().upperEdge());
		Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(
				xrange, xsteps, yrange, ysteps), mapper);

		
		
		surface.setColor(currentFillColor);
		surface.setWireframeDisplayed(currentWiredrame);
		surface.setWireframeColor(currentWiredColor);	
		surface.setWireframeWidth(currentWiredWidth);
		
		if (solidColor== false){
		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, 0.7f)));
		}
		
		
		
		
		datalist[N1][N2].add(surface);

		return surface;
		
	}

	
	/**
	 * Show H2D histogram as bars
	 * @param h2d input histogram
	 * @param reurn what was added
	 */
	public void add(final H2D h2d) {
	addAsBars(h2d);
   }
	
	/**
	 * Add a H2D histogram as bars. The histogram will be shows as a surface.
	 * Use "update()" method to draw it.
	 * 
	 * @param h2d
	 *            Histogram for drawing.
	 * @param 
	 *        return what was added           
	 */
	public void  addAsBars(final H2D h2d) {

		Histogram2D h1 = h2d.get();

		int ibinsX = h1.xAxis().bins() + 2;
		int ibinsY = h1.yAxis().bins() + 2;

		int zmax=(int)h1.maxBinHeight();
		
		
		for (int i = 0; i < ibinsX - 1; i++) {
			double x = h1.xAxis().binLowerEdge(i);
			double wx = h1.xAxis().binWidth(i);

			for (int j = 0; j < ibinsY - 1; j++) {
				double y = h1.yAxis().binLowerEdge(j);
				double wy = h1.yAxis().binWidth(j);

				double h = h1.binHeight(i, j);
		
				// parallepiped does not works
				/*
				Parallelepiped pp=getParallelepiped((float)x,(float)(x+wx),(float)y,(float)(y+wy),0.0f,(float)h);
				pp.setColor(currentFillColor);
				pp.setWireframeDisplayed(currentWiredrame);
				pp.setWireframeColor(currentWiredColor);
				pp.setWireframeWidth(currentWiredWidth);	
				if (solidColor== false){	
					pp.setColorMapper(new ColorMapper(new ColorMapRainbow(), 
										0, zmax,
										new Color(1, 1, 1, 0.8f)));
					
				}
				*/
				
				
				// apply color map
				Polygon[] pp= getSquareBar(x, wx, y, wy, h); 
				
				for (int k=0; k<pp.length; k++){
					
					pp[k].setColor(currentFillColor);
					pp[k].setWireframeDisplayed(currentWiredrame);
					pp[k].setWireframeColor(currentWiredColor);
					pp[k].setWireframeWidth(currentWiredWidth);	
					
					
				if (solidColor== false){	
					pp[k].setColorMapper(new ColorMapper(new ColorMapRainbow(), 
										0, zmax,
										new Color(1, 1, 1, 0.8f)));
					
				}
				datalist[N1][N2].add(pp[k]);
				}
			    	
			
				
			// datalist[N1][N2].add(pp);
				
			}
		}

	
	}

	
	
	
	/**
	 * Set a legend to a given surface
	 * 
	 * @param surface input object
	 */
	public void setLegendBar(AbstractDrawable surface){
		
		ColorbarLegend cbar = new ColorbarLegend(surface, jpp[N1][N2].getView().getAxe().getLayout());
        surface.setLegend(cbar);

		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Draw a H2D histogram. Better use "add" to overlay histograms. Then use
	 * "update()" method to draw it.
	 * 
	 * @param h2d
	 *            Histogram for drawing.
	 * 
	 */
	public void draw(final H2D h2d) {

		Histogram2D h1 = h2d.get();

		Mapper mapper = new Mapper() {
			public double f(double x, double y) {

				int ix = h2d.findBinX(x);
				int iy = h2d.findBinY(y);
				return h2d.binHeight(ix, iy);
			}
		};

		int xsteps = h1.xAxis().bins();
		int ysteps = h1.yAxis().bins();
		Range xrange = new Range(h1.xAxis().lowerEdge(), h1.xAxis().upperEdge());
		Range yrange = new Range(h1.yAxis().lowerEdge(), h1.yAxis().upperEdge());
		Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(
				xrange, xsteps, yrange, ysteps), mapper);

		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true); // draws surface polygons content
		surface.setWireframeDisplayed(true); // draw surface polygons border
		surface.setWireframeColor(Color.BLACK); // set polygon border in black
		// datalist[N1][N2].add(surface);
		jpp[N1][N2].getScene().add(surface);
	}

	/**
	 * Get the current canvas chart.
	 * 
	 * @return
	 */
	public Chart getChart() {
		return jpp[N1][N2];
	}

	/**
	 * Set background for the current pad.
	 * 
	 * @param color
	 */
	public void setBackground(java.awt.Color color) {
		jpp[N1][N2].getView().setBackgroundColor(new Color(color));
	}

	/**
	 * Set scientific notation with a give number of digits
	 * 
	 * @param axis
	 *            axis. 0=X, 1=Y, 2=Z
	 * @param dig
	 *            number of digits
	 */
	public void setTickRenderer(int axis, int dig) {
		if (axis == 0)
			jpp[N1][N2].getAxeLayout().setXTickRenderer(
					new ScientificNotationTickRenderer(dig));
		if (axis == 1)
			jpp[N1][N2].getAxeLayout().setYTickRenderer(
					new ScientificNotationTickRenderer(dig));
		if (axis == 2)
			jpp[N1][N2].getAxeLayout().setZTickRenderer(
					new ScientificNotationTickRenderer(dig));

	}

	
	/**
	 * Set scientific notation with a give number of digits for all ticks
	 * 
	 * @param dig
	 *            number of digits
	 */
	public void setTickRendererAll(int dig) {
		setTickRenderer(0,dig);
		setTickRenderer(1,dig);
		setTickRenderer(2,dig);
	}
	
	
	
	
	/**
	 * The box can be turned on/off for the current pad.
	 * 
	 * @param isbox
	 *            false is box with axes is off
	 */
	public void setAxesBox(boolean isbox) {
		jpp[N1][N2].getView().setAxeBoxDisplayed(isbox);
	}

	/**
	 * Close the canvas.
	 * 
	 */
	public void close() {

		mainFrame.setVisible(false);
		mainFrame.dispose();

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				jpp[i1][i2].clear();
				jpp[i1][i2].dispose();
				jpp[i1][i2] = null;

			}
		}

	}

	@Override
	protected void showHelp() {

		new HelpDialog(getFrame(), help_file + ".html");

		// TODO Auto-generated method stub

	}

	protected void openReadDataDialog() {
		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");
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
	 * Open a dialog to write the file
	 * 
	 */

	protected void openWriteDialog() {
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HGraph");
		// spp[N1][N2].saveToFile(false);
	}

	

	/**
	 * Add a bar for histogram.
	 * 
	 * 
	 * @param x
	 *            X lower edge
	 * @param wx
	 *            bin width in X
	 * @param y
	 *            Y lower edge
	 * @param wy
	 *            Y lower edge
	 * 
	 * @return
	 */
	private  Polygon [] getSquareBar(double x, double wx, double y,
			double wy, double height) {

		

		float xmin=(float)x;
		float xmax=(float)(x+wx);
		float ymin=(float)y;
		float ymax=(float)(y+wy);
		float zmin=0.0f;
		float zmax=(float)height;
		BoundingBox3d bbox = new BoundingBox3d(xmin,xmax,ymin,ymax,zmin,zmax);
	//	Parallelepiped bar = new  Parallelepiped(b);
	
		
		Polygon []quads = new Polygon[6];

        quads[0] = new Polygon();
        quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())));
        quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())));
        quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())));
        quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())));

        quads[1] = new Polygon();
        quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())));
        quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())));
        quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())));
        quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())));

        quads[2] = new Polygon();
        quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())));
        quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())));
        quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())));
        quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())));

        quads[3] = new Polygon();
        quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())));
        quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())));
        quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())));
        quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())));

        quads[4] = new Polygon();
        quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())));
        quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())));
        quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())));
        quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())));

        quads[5] = new Polygon();
        quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())));
        quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())));
        quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())));
        quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())));

		
		
		
		
		
		/*
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);
		bar.setWireframeWidth(currentWiredWidth);
		*/
		
        return quads;
        
        
	}

	/**
	 * Build a bar as a cylinder in 3D and add to the list of shapes on the
	 * current pad. Use "update" method to display it.
	 * 
	 * @param x
	 *            position in X
	 * @param y
	 *            position in Y
	 * @param radius
	 *             radius
	 */
	public void addBar(double x, double y, double z, double height,
			double radius) {
		HistogramBar bar = new HistogramBar();
		bar.setData(new Coord3d(x, y, z), (float) height, (float) radius, currentFillColor);
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);	
		bar.setWireframeWidth(currentWiredWidth);
		datalist[N1][N2].add(bar);

	}

	/**
	 * Add a point to the list of shapes on the current pad. Use "update" method
	 * to display it.
	 * 
	 * @param x
	 *            position in X
	 * @param y
	 *            position in Y
	 * @param z
	 *            position in Z
	 * 
	 * @param width
	 *            width or size
	 * 
	 */
	public void addPoint(double x, double y, double z, double width) {
		Point bar = new Point();
		bar.setData(new Coord3d(x, y, z));
		bar.setColor(currentFillColor);
		bar.setWidth((float) width);
		datalist[N1][N2].add(bar);

	}

	/**
	 * Add a flat line to the current pad. Use "update" method to display it.
	 * 
	 * @param p2d
	 *            array of X,Y,Z which define polygone points
	 */
	public void addPolygon(P2D p2d) {

		Polygon bar = new Polygon();
		for (int i = 0; i < p2d.size(); i++) {

			Point p = new Point(new Coord3d(p2d.getX(i), p2d.getY(i),
					p2d.getZ(i)));
			p.setColor(currentWiredColor);
			bar.add(p);
		}
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);
		bar.setWireframeWidth(currentWiredWidth);
		datalist[N1][N2].add(bar);

	}

	/**
	 * Add a flat line to the current pad. Use "update" method to display it.
	 * 
	 * @param x
	 *            position in X
	 * @param y
	 *            position in Y
	 * 
	 * @param depth
	 *            depth
	 * 
	 * @param color
	 *            color color to fill
	 */
	public void addFlatLine(float[] x, float[] y, double depth) {
		FlatLine2d bar = new FlatLine2d(x, y, (float) depth);	
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);	
		bar.setWireframeWidth(currentWiredWidth);
		datalist[N1][N2].add(bar);

	}

	/**
	 * Add a sphere to the current canvas. Use "update" to show it.
	 * 
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param z
	 *            position Z
	 * @radiusInner inner radius
	 * @radiusOuter outer radius
	 * 
	 * @param slices
	 *            number of vertical slices (i.e. wireframes)
	 * @param stacks
	 *            loops
	 */
	public void addDisk(double x, double y, double z, double radiusInner,
			double radiusOuter, int slices, int loops) {
		Disk bar = new Disk();
		bar.setData(new Coord3d(x, y, z), (float) radiusInner,
				(float) radiusOuter, slices, loops);
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);
		bar.setWireframeWidth(currentWiredWidth);
		datalist[N1][N2].add(bar);

	}

	/**
	 * Add a sphere to the current canvas. Use "update" to show it.
	 * 
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param z
	 *            position Z
	 * @param radius
	 *            radius of the sphere
	 * @param height
	 *            height
	 * @param slices
	 *            number of vertical slices (i.e. wireframes)
	 * @param stacks
	 *            number of horizontal stacks (i.e. wireframes)
	 */
	public void addSphere(double x, double y, double z, double radius, double height, int slices, int stacks) {
		EnlightableSphere bar = new EnlightableSphere();
		bar.setData(new Coord3d(x, y, z), (float) radius, (float) height,
				slices, stacks);
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);	
		bar.setWireframeWidth(currentWiredWidth);
		datalist[N1][N2].add(bar);

	}

	/**
	 * Add a tube to the current canvas. Use "update" to show it.
	 * 
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param z
	 *            position Z
	 * @param radiusBottom
	 *            radius of the bottom circle
	 * @param radiusTop
	 *            radius of the top circle
	 * @param height
	 *            height of the cylinder
	 * @param slices
	 *            number of vertical slices (i.e. wireframes)
	 * @param stacks
	 *            number of horizontal stacks (i.e. wireframes)

	 */
	public void addTube(double x, double y, double z, double radiusBottom,
			double radiusTop, double height, int slices, int stacks) {
		Tube bar = new Tube();
		bar.setData(new Coord3d(x, y, z), (float) radiusBottom,
				(float) radiusTop, (float) height, slices, stacks);
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);
		bar.setWireframeWidth(currentWiredWidth);
		datalist[N1][N2].add(bar);

	}

	
	
	
	/**
	 * Add parallelepiped to the current canvas.
	 * 
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @param zmin
	 * @param zmax
	 */
	private  Parallelepiped getParallelepiped(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax) {
		
		BoundingBox3d b = new BoundingBox3d(xmin,xmax,ymin,ymax,zmin,zmax);
		Parallelepiped bar = new  Parallelepiped(b);
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);
		bar.setWireframeWidth(currentWiredWidth);
		return bar;

	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Add parallelepiped to the current canvas.
	 * 
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @param zmin
	 * @param zmax
	 */
	public void addParallelepiped(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax) {
		
		
		BoundingBox3d b = new BoundingBox3d(xmin,xmax,ymin,ymax,zmin,zmax);
		
		
		Parallelepiped bar = new  Parallelepiped(b);
		bar.setColor(currentFillColor);
		bar.setWireframeDisplayed(currentWiredrame);
		bar.setWireframeColor(currentWiredColor);
		bar.setWireframeWidth(currentWiredWidth);
		datalist[N1][N2].add(bar);

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Add light to the scene.
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @param z
	 *            Z position
	 * @param AmbiantColor
	 *            Ambiant Color
	 * @param DiffuseColor
	 *            Diffuse Color
	 * @param SpecularColor
	 *            Specular Color
	 * @return
	 */

	public void addLight(double x, double y, double z,
			java.awt.Color AmbiantColor, java.awt.Color DiffuseColor,
			java.awt.Color SpecularColor) {
		Light light = new Light(lightId++);
		light.setPosition(new Coord3d(x, y, z));
		light.setAmbiantColor(getColor(AmbiantColor));
		light.setDiffuseColor(getColor(DiffuseColor));
		light.setSpecularColor(getColor(SpecularColor));
		jpp[N1][N2].getScene().add(light);

	}

	/**
	 * Get list of shapes plotted on the current pad given by the cd() method.
	 * 
	 * @return list of plotted shapes.
	 */

	public List<AbstractDrawable> getShapeList() {

		return datalist[N1][N2];

	}

	@Override
	protected void openReadDialog() {
		// TODO Auto-generated method stub
		
	}

	// end
}
