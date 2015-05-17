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
 *    @author: J.V.Lee and S.Chekanov
 **/


package jplot;

import javax.swing.*;

import org.freehep.graphics2d.VectorGraphics;

import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.Array;
import java.math.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.awt.print.*;

import jhplot.JHPlot;
import jhplot.shapes.*;
import jplot.panels.PanelPlot;
import graph.*;

/**
 * The <code>Graph</code> class builds a panel which displays a graph
 * according to the data stored in the {@link DataArray}. Extends JPanel since
 * the graph is returned in a panel (it can be used in other applications or
 * embedded in a frame. Implements Printable, so we can print the graph, and
 * ComponentListener, needed to listen to a resize event.
 * 
 * This class should be sub-classed in order to build a specific graph type (i.e.
 * a 2D graph, piper diagram or 3D, later)
 * 
 * @version 24/08/99
 * @author J. van der Lee & S.Chekanov (Aug. 2000)
 */
public abstract class GraphGeneral extends JPanel implements Printable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// vector of DataArray objects, containing
	// the arrays of datapoints forming the graph:
	Vector data;

	// axes and tic-label stuff:
	final protected int X = GraphSettings.X_AXIS;

	final protected int Y = GraphSettings.Y_AXIS;

	final protected int Na = GraphSettings.N_AXES;

	protected double[] axisLength = new double[Na];

	protected double leftMargin;

	protected double rightMargin;

	protected double bottomMargin;

	protected double topMargin;

	protected JPopupMenu menu;

	public JMenuItem m_edit, m_default, m_refresh;


        protected final int numDigits=3;

	// variables used to calculate tic-seperation distances:
	protected String[][] ticLabel = new String[Na][];

        protected double[][] ticNumber = new double[Na][];

	protected double[] ticLength = new double[Na];
	
	protected double[] subticLength = new double[Na];
	
	protected int[] subticNumber = new int[Na];
	
        protected double[] MinAxis = new double[Na];
        protected double[] MaxAxis = new double[Na];
        protected int[] AxisExponent = new int[Na];


	protected double[] diff = new double[Na];

	protected double[] inv = new double[Na];

	protected int[] numberOfTics = new int[Na];

	// separation between tic-labels and the axis:
	protected double sep[] = new double[Na];

        public float scalingFrame=1.0f;
        public float scalingFrameY=1.0f;
        public float scalingFrameX=1.0f;
        private boolean firstDraw;

        //
        protected  VectorGraphics currentG=null;
 
	// legend stuff:
	protected boolean legendActive = false;

	protected boolean somethingActive = false;

	protected final float xSepIni = 10.0f;
	protected final float ySepIni = 4.0f;
        protected float xSep;
        protected float ySep;

        private int _startX = 0; // start x for dragging of rectangle
        private int _startY = 0; // start y for dragging of rectangle
        private int _lastX = 0; // end x for dragging of rectangle
        private int _lastY = 0; // end y for dragging of rectangle
        private boolean _isDragging = false;


	// this class contains all the parameters
	// which are used to plot the graph.
	protected GraphSettings gs;
        protected Vector<Font> iniFonts;
        protected int numberLabels;

        protected static boolean zoomModeX=false;
        protected static boolean zoomModeY=false;
        protected static int currentZoomX, currentZoomY, startZoomX, startZoomY;
        protected static double defXmin, defXmax, defYmin, defYmax;
        protected static boolean zoomFirst=true;
        protected static boolean defAutoX, defAutoY;
 
	// legend:
	protected double ldy; // distance between two legend labels

	protected double keyLen; // length of the key (drawing style, e.g. a
        protected final double keyLenIni;			// line)

	protected double legendWidth;

	protected double legendHeight;

	// size of the graph panel
	protected Dimension panelSize;
        protected Dimension panelSizeOriginal;

	protected Dimension panelSizeForPrinter;

	// dimension of the panel:
	protected int width;

	protected int height;

        protected Vector<String>[] ticsAxis;
         
	// colors of the label drag'n drop box:
	private final Color boxColor = Color.gray;

	// the value of Ln(10), need it a few times for logscales:
	private final static double LNTEN = Math.log(10.0);

	protected Line2D.Double line = new Line2D.Double();

	protected Rectangle2D.Double rect = new Rectangle2D.Double();
	
	protected Polygon polyg = new Polygon();

	// coordinates used for dragging and zooming.
	protected int x1, y1, boxWidth, boxHeight;

	protected int xStart, yStart;

	// protected BufferedImage bi;

	private boolean initializing;

	// variables used by a piper diagram
	protected double piperSep; // separation gap between rectangles

	protected double normalSep; // (vertical) normal of the gap

	protected double triangleBottom; // length of bottom of a triangle

	protected double triangleSide; // length of the sides of a triangle

	protected double triangleHeight; // height of a triangle

	protected double borderWidth; // white space between panel and border

	protected double arc; // angle of the triangle (lower corners)

	protected double tan, sin, cos; // geometrical coefficients

	// variable to build arrows
	protected int al = 10; // Arrow length

	protected int aw = 5; // Arrow width

	protected int haw = 2; // Half arrow width

	protected double hhaw = 0.5; // Half arrow width
	
	protected int xValues[] = new int[3];

	protected int yValues[] = new int[3];

	protected double mPosX;

	protected double mPosY;

	private JPlot jplot;

	/**
	 * Main constructor. Sets the settings to their default values. If the graph
	 * is used without the jplot GUI (hence without a JPlot instance), set the
	 * first parameter to null or use the other constructor instead.
	 * 
	 * @param jp
	 *            JPlot instance (parent).
	 * @param gs
	 *            container with all the graph settings.
	 */
	public GraphGeneral(JPlot jp, final GraphSettings gs) {
		jplot = jp;
		if (JPlot.debug)
			System.out.print("Initializing Graph(gp)...");
		initializing = true;
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		// setBorder(new BevelBorder(BevelBorder.LOWERED));
		addMouseListener(new GraphMouseListener());
		addMouseMotionListener(new DragListener());
		this.gs = gs;
                zoomFirst=true;
                numberLabels=0;
                iniFonts = new Vector<Font>(); 
                xSep = xSepIni;
                ySep = ySepIni;
                ticsAxis= new Vector[2];
 
		// pop-up menu
		menu = new JPopupMenu();
		m_edit = new JMenuItem("Edit settings");
                m_refresh = new JMenuItem("Refresh canvas");
                m_refresh.addActionListener(new java.awt.event.ActionListener() {
                      public void actionPerformed(
                      java.awt.event.ActionEvent evt) {
                                    jplot.updateGraphIfShowing(); 
                                             }
                       });


                    m_default = new JMenuItem("Default axis range");
                    m_default.addActionListener(new java.awt.event.ActionListener() {
                      public void actionPerformed(
                      java.awt.event.ActionEvent evt) {

                       // set default
                       if (!zoomFirst) {
                       gs.setAutoRange(0,defAutoX);
                       gs.setAutoRange(1,defAutoY);
                       gs.setRange(0,defXmin, defXmax);
                       gs.setRange(1,defYmin, defYmax);
                       jplot.updateGraphIfShowing();
                       }
                        }
                        });


		menu.add(m_edit);
                menu.add(m_refresh);
                menu.add(m_default);

		panelSize = gs.getPanelSize();
                panelSizeOriginal=new Dimension(panelSize);
                firstDraw=true;
 
		height = panelSize.height;
		width = panelSize.width;
                // System.out.println(height);

		leftMargin = gs.getLeftMargin();
		rightMargin = gs.getRightMargin();
		bottomMargin = gs.getBottomMargin();
		topMargin = gs.getTopMargin();
		keyLenIni = 25.0;
                keyLen = keyLenIni;
                zoomModeX=false;
                zoomModeY=false;
		// bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		if (JPlot.debug)
			System.out.println("done");
	}

	/**
	 * Main constructor. Sets the settings to their default values.
	 * 
	 * @param gs
	 *            container with all the graph settings.
	 */
	public GraphGeneral(GraphSettings gs) {
		this(null, gs);
	}

        protected Font scaleFont(Font fo){
           int fontSize = (int)Math.round( fo.getSize() * scalingFrame);
           // int fontSize = (int)Math.round( fo.getSize() );
           return new Font(fo.getName(), fo.getStyle(), fontSize);
        };



	/**
	 * Get the minimum size of this component. This is the panel size defined by
	 * width and height.
	 * 
	 * @return the minimum size.
	 */
	public Dimension getMinimumSize() {
		return new Dimension(width, height);
	}

	/**
	 * Clear the graph.
	 */
	public void clear() {

		// Graphics g = getGraphics();
		getGraphics().dispose();
		ticLength = null;
                ticNumber = null;
		ticLabel = null;
		diff = null;
		inv = null;
		numberOfTics = null;
		sep = null;
		ticLength = null;
		axisLength = null;
//		bi = null;
		gs = null;
		jplot = null;
                data=null; 
	}

	/**
	 * Get the preferred size of this component. This is the panel size defined
	 * by width and height
	 * 
	 * @return the preferred size.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	/*
	 * Sets the new location of the top-left corner (x,y) and the size in terms
	 * of the width (w) and height (h). @param x the new x-coordinate. @param y
	 * the new y-coordinate. @param w the new width. @param h the new height.
	 */
	/*
	 * public void setBounds(int x, int y, int w, int h) { width = w; height =
	 * h; super.setBounds(x,y,width,height); //updateGraph();
	 * //System.out.println("@@@@@@@@@@@@@ resized"); }
	 */

	/*
	 * Set the size of the plot. @param w the width, in pixels. @param h the
	 * height, in pixels.
	 */
	/*
	 * public void setSize(int w, int h) { width = w; height = h;
	 * super.setSize(width,height); }
	 */

	/*
	 * This method is called as the user is zooming and releases the
	 * mousebutton. It rescales the plot according to the selected box
	 * boundaries. @param x The final x position. @param y The final y position.
	 */
	
	/*
	private synchronized void finishZoom(int x1, int y1, int x2, int y2) {
		// Graphics g = getGraphics();
	}
  */
	/*
	 * @param x x position. @param y y position.
	 */
	/*
	private synchronized void zoomRect(int x, int y) {
		Graphics g = getGraphics();
	}
    */
	/**
	 * Returns the log10 of a double value.
	 * 
	 * @param x
	 *            value
	 * @return log10(x)
	 */
	public static double log10(double x) {
		return Math.log(x) / LNTEN;
	}


	/**
	 * Return the number of digits required to display the given number. If more
	 * than 15 digits are required, 15 is returned).
	 */
	public static int getNumDigits(double num) {
		int numDigits = 0;
		if (num == 0.0)
			return 0;
		while (numDigits <= 15 && Math.abs(Math.floor(num) / num - 1.0) > 1e-10) {
			// while (numDigits <= 15 && num != Math.floor(num)) {
			num *= 10.0;
			numDigits++;
		}
		return numDigits;
	}

	/**
	 * formats a double precision number such that it is correctly rounded for
	 * output. Kind of pre-processor for all number for output.
	 * 
	 * @param num
	 *            number to be formatted
	 * @param n
	 *            number of digits (accuracy) after the decimal point.
	 * @return the formatted number in a string.
	 */
	public static String formatNumber(double num, int n) {
		int maxFloat = 6;
		if (num != 0.0) {
			int exp = (int) Math.floor(log10(num));
			int x = Math.abs(exp) + n;
			BigDecimal bd = new BigDecimal(num);
			if (x > maxFloat) {
				if (exp > 0)
					bd = bd.movePointLeft(exp);
				else
					bd = bd.movePointRight(Math.abs(exp));
			} else {
				if (exp < 0)
					n = x;
				else
					n = x - Math.abs(exp);
			}
			bd = bd.setScale(n, BigDecimal.ROUND_HALF_EVEN);
			int nn = getNumDigits(bd.doubleValue());
			if (nn < n)
				bd = bd.setScale(nn);
			String res = bd.toString();
			if (x > maxFloat)
				res += "e" + exp;
			return res;
		} else
			return "0";
	}

	/**
	 * Determine the axis labels, if needed. These are numbers which are
	 * translated in Strings. The length is evalulated and used to set the left
	 * and bottom margins.
	 */
	abstract void makeTicLabels();

	/**
	 * This method updates the margins as a function of the label widths and the
	 * axes texts. Also sets the default positions of the text labels.
	 */
	protected void updateMargins() {
		double xlHeight = 0.0;
		double ylWidth = 0.0;
		double tHeight = 0.0;

		if (JPlot.debug)
			System.out.print("updating margins...");


		// we have to find the height of the X- and Y labels and title
		// in order to get the margins right:
		for (Enumeration e = gs.getLabels().elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();
			if (gl.equals(GraphLabel.XLABEL))
				xlHeight = gl.getHeight() + ySep;
			else if (gl.equals(GraphLabel.YLABEL))
				ylWidth = gl.getWidth() + xSep; 
			else if (gl.equals(GraphLabel.TITLE))
				tHeight = gl.getHeight() + ySep;
			if (gl.equals(GraphLabel.PIPER_X1))
				xlHeight = gl.getHeight() - 3.0;
		}

		double maxYLabel = 0.0;
		// initialize the margins with the default (minimum) values:
		borderWidth =   gs.getBoxOffset()*scalingFrame;
		leftMargin =   (gs.getLeftMargin() + borderWidth);
		bottomMargin = (gs.getBottomMargin() + xlHeight + borderWidth);
		rightMargin =  (gs.getRightMargin() + borderWidth);
		topMargin =    (gs.getTopMargin() + tHeight + borderWidth);
                


		// add some extra space to the bottom-margin for the tic-labels:
		if (gs.drawTicLabels(X))
			bottomMargin += ySep * 2;

		// add some extra space to the left-margin in order to account
		// for the y-labels (take the label with the maximum length):

                /* this is auto left margin in case if not set well  
		if (gs.drawTicLabels(Y)) {
			FontMetrics fm = getFontMetrics(gs.getTicFont(Y));
			for (int i = 0; i < numberOfTics[Y]; i++) {
				double yl = fm.stringWidth(ticLabel[Y][i])*scalingFrame;
				if (yl > maxYLabel)
					maxYLabel = yl;
				yl += xSep * 2 + borderWidth;
				if (yl > leftMargin)
					leftMargin = yl;
			}
		}
		leftMargin += ylWidth;
                // chekanov
                */

		// axes lengths must be something like this:
		axisLength[X] = width - leftMargin - rightMargin;
		axisLength[Y] = height - topMargin - bottomMargin;

		// ... except if someone has fixed the axes ratio:
		double r = axisLength[Y] / axisLength[X];
		if (gs.useAxesRatio()) {

			// at this stage, both axes are at maximum length. Let 'ratio'
			// be the ratio specified by the user. If r > ratio, then we
			// must adapt the Y-axis-length:
			if (r > gs.getAxesRatio()) {
				axisLength[Y] = gs.getAxesRatio() * axisLength[X];
				topMargin = height - bottomMargin - axisLength[Y];
			} else if (r < gs.getAxesRatio()) {
				axisLength[X] = gs.getAxesRatio() * axisLength[Y];
				rightMargin = width - leftMargin - axisLength[X];
			}
		} else
			gs.setAxesRatio(r);

		if (gs.getGraphType() == GraphSettings.GRAPHTYPE_PIPER) {
			if (!gs.useLegendPosition()) {
				gs.setLegendPosition(borderWidth, borderWidth + 3.0f);
			}

			// needed to convert data scale to graphical pixel scaling
			tan = 2.0 * axisLength[Y] / axisLength[X];
			arc = Math.atan(tan);
			sin = Math.sin(arc);
			cos = Math.cos(arc);
			normalSep = piperSep * sin;

			triangleBottom = (axisLength[X] - piperSep) / 2.0;
			triangleHeight = (axisLength[Y] - normalSep) / 2.0;
			triangleSide = triangleHeight / sin;
			sep[X] = triangleBottom / (numberOfTics[X] - 1);
			sep[Y] = triangleHeight / (numberOfTics[Y] - 1);
			if (tHeight > 0.0)
				tHeight += 10.0;
		} else {
			if (!gs.useLegendPosition()) {
				gs.setLegendPosition(leftMargin + ticLength[Y] + 7.0f,
						topMargin + 7.0f);
			}
			sep[X] = axisLength[X] / (numberOfTics[X] - 1);
			sep[Y] = axisLength[Y] / (numberOfTics[Y] - 1);
		}
		ticLength[X] = gs.getTicLength(X) * width;
		ticLength[Y] = gs.getTicLength(Y) * width;
                subticLength[X] = gs.getSubTicLength(X) * width;
                subticLength[Y] = gs.getSubTicLength(Y) * width;
                subticNumber[X] = gs.getSubTicNumber(X);
                subticNumber[Y] = gs.getSubTicNumber(Y);

                MinAxis[X] = gs.getMinValue(X); 
                MinAxis[Y] = gs.getMinValue(Y);

                MaxAxis[X] = gs.getMaxValue(X);
                MaxAxis[Y] = gs.getMaxValue(Y);


		setDefaultLabelPositions(maxYLabel);
		if (JPlot.debug)
			System.out.println("done.");
	}

	/*
	 * Set the label positions to default values.
	 */
	private void setDefaultLabelPositions(double m) {
		int numOfOtherLabels = 0;
		if (gs.getGraphType() == GraphSettings.GRAPHTYPE_PIPER)
			m *= 0.7;
		for (Enumeration e = gs.getLabels().elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();


			if (!gl.usePosition()) {
				double x = 0.0;
				double y = 0.0;
				if (gl.equals(GraphLabel.XLABEL)) {
					x = leftMargin + axisLength[X] / 2;
					y = topMargin + axisLength[Y]
							+ (bottomMargin - borderWidth) / 2 + ySep;
				} else if (gl.equals(GraphLabel.YLABEL)) {
					x = borderWidth + (leftMargin - borderWidth - m) / 3;
					y = topMargin + axisLength[Y] / 2;
				} else if (gl.equals(GraphLabel.TITLE)) {
					x = leftMargin + axisLength[X] / 2;
					y = topMargin / 2;
				} else if (gl.equals(GraphLabel.OTHER)) {
					x = leftMargin + 10.0f + gl.getWidth() / 2;
					y = topMargin + axisLength[Y] - 14.0f
							* (1 + numOfOtherLabels);
					numOfOtherLabels++;
				} else if (gl.equals(GraphLabel.PIPER_X1)) {
					x = leftMargin + triangleBottom / 2;
					y = topMargin + axisLength[Y]
							+ (bottomMargin - borderWidth) / 2 + ySep;
				} else if (gl.equals(GraphLabel.PIPER_X2)) {
					x = leftMargin + axisLength[X] - triangleBottom / 2;
					y = topMargin + axisLength[Y]
							+ (bottomMargin - borderWidth) / 2 + ySep;
				} else if (gl.equals(GraphLabel.PIPER_Y1)) {
					gl.setRotation(-arc);
					double xx = m + sin * (xSep + gl.getTextHeight());
					x = leftMargin + triangleBottom / 4 - xx;
					y = topMargin + axisLength[Y] - triangleHeight / 2 - xx
							/ tan;
				} else if (gl.equals(GraphLabel.PIPER_Y2)) {
					gl.setRotation(arc);
					double xx = m + cos * (xSep + gl.getTextHeight());
					x = leftMargin + axisLength[X] - triangleBottom / 4 + xx;
					y = topMargin + axisLength[Y] - triangleHeight / 2 - xx
							/ tan;
				} else if (gl.equals(GraphLabel.PIPER_Y3)) {
					gl.setRotation(-arc);
					double xx = m + sin * (xSep + gl.getTextHeight());
					x = leftMargin + axisLength[X] / 2 - triangleBottom / 4
							- xx;
					y = topMargin + triangleHeight / 2 - xx / tan;
				} else if (gl.equals(GraphLabel.PIPER_Y4)) {
					gl.setRotation(arc);
					double xx = m + cos * (xSep + gl.getTextHeight());
					x = leftMargin + axisLength[X] / 2 + triangleBottom / 4
							+ xx;
					y = topMargin + triangleHeight / 2 - xx / tan;
				} else if (gl.equals(GraphLabel.DATA)) {
					x = toX(gl.getXPos());
					y = toY(gl.getYPos());
					gl.setUsePosition(true);
					// gl.setID(GraphLabel.OTHER);
				}
				gl.setLocation(x - gl.getWidth() / 2, y - gl.getHeight() / 2);
				gl.setRotation(gl.getRotation());
			}
		}
	}


    // helper functions that scale from user coordinates to screen coordinates and back
    //private static double  scaleX(double x) { return width  * (x - xmin) / (xmax - xmin); }
    //private static double  scaleY(double y) { return height * (ymax - y) / (ymax - ymin); }


	/*
	 * Plot the labels, ALL the labels on the graph. @param g2 a Graphics2D
	 * instance (canvas).
	 */
	protected void plotPrimitives(VectorGraphics  g2) {
		
	       	
		
		for (Enumeration e = gs.getPrimitives().elements(); 
		                  e.hasMoreElements();) {
			HShape gl = (HShape) e.nextElement();

			Stroke old1 = g2.getStroke();
			Color old2 = g2.getColor();
			Composite originalComposite = g2.getComposite();
			Font old3=g2.getFont();

			g2.setComposite(makeComposite(gl.getTransparency()));
			g2.setStroke(gl.getStroke());
			g2.setColor(gl.getColor());
			
			double[] pos = gl.getPosition();
			int x1 = (int) toX(pos[0]);
			int y1 = (int) toY(pos[1]);
			int x2 = (int) toX(pos[2]);
			int y2 = (int) toY(pos[3]);
			
//			 in the NDC coordinnates
			if (gl.getPositionCoordinate()==1){
	                         double w = panelSize.width;
	                         double h = panelSize.height;
	                         x1= (int)(w*pos[0]);
				 y1= (int)(h*(1-pos[1]));
				 x2= (int)(w*pos[2]);
				 y2= (int)(h*(1-pos[3]));
			}
			
			// System.out.println(gl.getWhoAm());
			
			if ( gl.getWhoAm()==HShape.LINE ) {
                                AffineTransform old = g2.getTransform();
                                g2.rotate(-1*gl.getRotation(),x1,y1); // rotate around center 
				g2.drawLine(x1, y1, x2, y2);
                                g2.setTransform(old);

			}
			
			
			if ( gl.getWhoAm()==HShape.IMAGE ) {

                                AffineTransform old = g2.getTransform();
                                g2.rotate(-1*gl.getRotation(),x1,y1); // rotate around center 
				g2.drawImage( ((Picture)gl).getPicture(),x1,y1,null);
                                g2.setTransform(old);

			}
			

			if ( gl.getWhoAm()==HShape.CIRCLE ) {
				
			  double  dd= ((Circle)gl).getRadius();
			  int ddx = (int) toX(pos[0]+2*dd);
			   int ws = ddx-x1;
				if (!gl.getFill()) 
					g2.draw(new Ellipse2D.Double(x1-ws/2, y1- ws/2, ws, ws));	
				if (gl.getFill()) 
					g2.fill(new Ellipse2D.Double(x1-ws/2, y1- ws/2, ws, ws));
			
			}
	

			
			if ( gl.getWhoAm()==HShape.ELLIPSE ) {
			
          		  //int ddx = (int) toX(pos[0]+pos[2]);
			  //int ddy = (int) toY(pos[1]+pos[3]);
			  //int ws1 = (ddx-x1);
			  //int ws2 = (y1-ddy);

                          //ddx = (int) toX(pos[0]+pos[2]);
                          //int ddy = (int) toY(pos[1]+pos[3]);
                          //int ws1 = (int)(toX(pos[2]));
                          //int ws2 = (int)(toY(pos[3]));


                          // build ellipse allighned in X alone major axis
                          // diameter in X
                          int wsMajor = (int)(toX(pos[0]+pos[2])); // point largers in X 
                          int wsMinor = (int)(toY(pos[1]+pos[3])); // point largest in Y
                          int major = (wsMajor-x1); // semiminor (largest radious) 
                          int minor = (y1-wsMinor); // semimajor (smallest radius ) 
 
 

                          //double xs = toX(pos[0]);
                          //double ys = toY(pos[1]);
                          //double ws = factorX(2*pos[2]); // 2* semimajor 
                          //double hs = factorY(2*pos[3]); // 2* semiminor 
                          // if (!gl.getFill()) g2.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
                          // if (gl.getFill()) g2.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
 
                           AffineTransform old = g2.getTransform();
                           g2.rotate(-1*gl.getRotation(),x1,y1); // rotate around center 
			  if (!gl.getFill()) g2.draw(new Ellipse2D.Double(x1-major, y1-minor, major*2, minor*2));	
			  if (gl.getFill()) g2.fill(new Ellipse2D.Double(x1-major, y1- minor,  major*2, minor*2));


                          g2.setTransform(old);
			}
	
			

			if ( gl.getWhoAm()==HShape.POLYGON ) {			
			  
								
			}
			
			

			if ( gl.getWhoAm()==HShape.RECTAN ) {
			
	
			 int ddx = (int) toX(pos[0]+pos[2]);
			 int ddy = (int) toY(pos[1]+pos[3]);
			
                         AffineTransform old = g2.getTransform();
                         g2.rotate(-1*gl.getRotation(),x1,y1);
	
			  int ws = ddx-x1;
			  int hs = y1-ddy;
			  if (!gl.getFill()) g2.drawRect(x1 - ws/2, y1 - hs/2, ws, hs);
			  if (gl.getFill()) g2.fillRect(x1 - ws/2, y1 - hs/2, ws, hs);				
                          g2.setTransform(old);
			}
			
			
			
			
			
		
			
			
			
			if ( gl.getWhoAm()==HShape.TEXT ) {
				// System.out.println(line.toString());

		    Text aa = (Text)gl;
		    RTextLine text = new RTextLine();
			float x = (float)x1;
			float y = (float)y1;
			// text.setFont(aa.getFont());
                        text.setFont( scaleFont(aa.getFont())); 

			text.setColor(aa.getColor());
			String stext = Translate.decode(aa.getText());
			text.setText(stext);

			double rotation = aa.getRotation();
			rotation = aa.getRotation() * 360 / (2 * Math.PI);
			g2.rotate(aa.getRotation(), x, y);
			text.setRotation((int) rotation);
			text.draw(g2, (int) x, (int) y);
			g2.rotate(-aa.getRotation(), x, y);		    
		   
			}
			
			
			
			
			
			
	   		   if (gl.getWhoAm() == HShape.ARROW) {
	   			  
	   			    Arrow aa = (Arrow)gl;
					int[] x = { x1, x2 };
					int[] y = { y1, y2 };

					al =  (int)aa.getEndLength(); // arrow length
					aw =  (int)aa.getEndWidth(); // arrow width
					if (aa.getType()==1)
						  drawPolylineArrow(g2, x, y, al, aw);
					if (aa.getType()==2)
					       drawArrow2(g2, x1, y1, x2, y2);
					if (aa.getType()==3)
						drawArrow(g2, x1, y1, x2, y2);
				}
      
				// g2.drawLine(x1, y1, x2, y2);
			

			// set to the old
			g2.setStroke(old1);
			g2.setColor(old2);
			g2.setFont(old3);
			g2.setComposite(originalComposite);

		}
	}

	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

	/**
	 * Find the minimum and maximum values for X- and Y axes.
	 * 
	 * @param axis
	 *            axis for which the min and max are needed.
	 * @param vector
	 *            array containing the data set.
	 */
	abstract boolean setMinMax(int axis, Vector data);

	/*
	 * Plot the labels, ALL the labels on the graph. @param g2 a Graphics2D
	 * instance (canvas).
	 */
	protected void plotLabels(VectorGraphics g2) {


		for (Enumeration e = gs.getLabels().elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();
			if (!gl.hide() && !gl.getText().equals("")) {


		if (gl.getID() != GraphLabel.STATBOX) {


                         
                        float xtmp=(float)gl.getXPos();
                        float ytmp=(float)gl.getYPos()-(float)gl.getTextDescent();
                  
                        /* 
                        float x = (float) toX( xtmp  );
                        float y = (float) toY( ytmp  );
                        if (gl.useDataPosition()==false){ // in the NDC coordinnates 
                                 float w = panelSize.width;
                                 float h = panelSize.height;
                                 x= (float)(w*xtmp);
                                 y= (float)(h*(1-ytmp));
                        }
                        */
                                     //   System.out.print(gl.getText()+" at useDataPosition=");
                                     //   System.out.println(gl.useDataPosition()); 
                                     //   System.out.println(xtmp);
                                     //   System.out.println(ytmp);
                                     // System.out.println("from jplot="+gl.getText()+" "+Integer.toString((int)xtmp)+" "+Integer.toString((int)ytmp) );

					RTextLine text = new RTextLine();
					float x = xtmp;
					float y = ytmp;
                        		text.setFont( gl.getFont());
					text.setColor(gl.getColor());
					String stext = Translate.decode(gl.getText());
					text.setText(stext);

					double rotation = gl.getRotation();
					rotation = gl.getRotation() * 360 / (2 * Math.PI);

					g2.rotate(gl.getRotation(), x, y);
					text.setRotation((int) rotation);

                                        if (gl.getID() != GraphLabel.KEY) 
					         text.draw(g2, (int) x, (int) y);

                                        // key case
                                        if (gl.getID() == GraphLabel.KEY) { 
                                                 drawKey(g2,gl,x,y); 
                                                 text.draw(g2, (int) x, (int) y);
                                        }


					g2.rotate(-gl.getRotation(), x, y);

					//  plot statbox
				} else {

					String[] ss = gl.getMultiText();
					int hh = (int) (gl.getTextHeight());

					for (int i = 0; i < ss.length; i++) {

						 // other labels
						if (ss[i] != null) {
							
							RTextLine text = new RTextLine();
							float x = (float) gl.getXPos();
							float y = (float) gl.getYPos();
							text.setFont( gl.getFont() );
                                           		text.setColor(gl.getColor());
							String stext = Translate.decode(ss[i]);
							text.setText(stext);
							text.draw(g2, (int) x, (int) y + hh * i);

						}
					}
				}

				/*
				 * g2.setColor(gl.getColor()); float x = (float)gl.getXPos();
				 * float y = (float)gl.getYPos();
				 * g2.rotate(gl.getRotation(),x,y);
				 * g2.drawString(gl.getCharIterator(),x,y);
				 * g2.rotate(-gl.getRotation(),x,y);
				 */

			}
		}
	}

	/*
	 * Finds the height and width of the box which envelops the legend. This box
	 * is used for dragging the legend. @param data vector with data arrays.
	 */

	// chekanov
	protected void getLegendBox(Vector data) {
		String s = "";
		int k = 0;
		for (Enumeration e = data.elements(); e.hasMoreElements();) {
			DataArray da = (DataArray) e.nextElement();
			if (da.drawLegend()) {
				if (da.getName().length() > s.length())
					s = da.getName();
				k++;
			}
		}

		FontMetrics fm = getFontMetrics(scaleFont(gs.getLegendFont()));
		s = Translate.shrink(s); // relace &alpha; by w etc
		legendWidth = (fm.stringWidth(s)+ keyLen + xSep);
		ldy = fm.getHeight() * gs.getLegendSpacing()*scalingFrameY*scalingFrame;
		legendHeight = ldy * (k - 1) + (fm.getHeight() + 2)*scalingFrameY;
	}

	/*
	 * Writes the legend in the upper-left corner of the graph, or whereever the
	 * user has pinned it. Note that the legend is drawn one 'ldy' (separation
	 * in the y-direction) lower than expected, this is a poor-man's trick to
	 * get the bounding-box right as we start to drag'n drop.
	 */
	protected void drawLegend(VectorGraphics  g2, DataArray da, int i) {


		float y = (float) (gs.getLegendPosition(Y) + ldy*(i + 1)*scalingFrameY);
		float x = (float) (gs.getLegendPosition(X));
		float yy = y - (float) ldy / 4;
		if (da.drawLine()) {
			g2.drawLine(x, yy, x + keyLen, yy);
		
		}
		if (da.drawSymbol()) {
			GPoints.drawPointType(da.getSymbol(), g2, x + keyLen / 2, yy,
					da.getSymbolSize()*scalingFrame);
		}
		// g2.drawString(da.getName(),(float)(x+keyLen+xSep),y);

		// chekanov
		RTextLine text = new RTextLine();
		text.setFont( scaleFont(gs.getLegendFont()));
		text.setColor(da.getColor());
		String stext = Translate.decode(da.getName());
		text.setText(stext);
		/// text.setRotation( (int)da.getRotation() );
                // System.out.println(xSep); 
		text.draw(g2, (int)(x +keyLen+xSep), (int) y);

	}

	/**
	 * Returns the X-value scaled to the pixel-availability. This function takes
	 * the X-value and returns the corresponding coordinates for the panel.
	 * 
	 * @param x
	 *            real x-value (as introduced by the data)
	 * @return x-value scaled to the actual panel coordinates
	 */
	public abstract double toX(double x);

	/**
	 * Returns the Y-value scaled to the pixel-availability. This function takes
	 * the Y-value and returns the corresponding coordinates for the panel.
	 * 
	 * @param y
	 *            real y-value (as introduced by the data)
	 * @return y-value scaled to the actual panel coordinates
	 */
	public abstract double toY(double y);

	
	
	
	
	/**
	 * Returns the X-value scaled to the data-availability. This function takes
	 * the X-value and returns the corresponding coordinates for the panel.
	 * 
	 * @param x
	 *            integer x-value (as introduced by the pixels)
	 * @return x-value scaled to the actual data coordinates
	 */
	abstract public double toUserX(int x);

	
	/**
	 * Returns the Y-value scaled to the data-availability. This function takes
	 * the Y-value and returns the corresponding coordinates for the panel.
	 * 
	 * @param x
	 *            integer x-value (as introduced by the pixels)
	 * @return x-value scaled to the actual data coordinate
	 */
	abstract public double toUserY(int x);
	
	

	
	
	/**
	 * Fills the graph area with a background color.
	 * 
	 * @param g2
	 *            Graphics canvas
	 */
	abstract void fillGraphArea(VectorGraphics  g2);

	/**
	 * Draws the graph on the graphics canvas of this panel. Performs a test on
	 * the minimum and maximum values. If they are different by more than 10 %
	 * with respect to their previous values, release all fixed positions of
	 * legend, labels etc and restore the default positions. Otherwise, they may
	 * fall far beyond the screen, leading to a coredump.
	 * 
	 * @param data
	 *            vector of Data Array vectors with the data points
	 */
	public void show(Vector data) {
		        this.data = data;

                 gs.setNoData(false);
		        if (data == null)     gs.setNoData(true);
                if (data.size() < 1)  gs.setNoData(true); 

             
                 // when no data, treat it well 
                 if (gs.getNoData()) { 
                        gs.setAutoRange(0, false);
                        gs.setAutoRange(1, false);
                 } 

		if (gs.drawLegend()) { 
			if (!gs.getNoData()) getLegendBox(data);
                }


		// find the minimum and maximum values of the current datasets:
		for (int k = 0; k < Na; k++) {
			if (!setMinMax(k, data)) {
				Utils.bummer(null,
						"Invalid data, no way to show a decent graph");
				return;
			}
		}
		makeTicLabels();
		updateMargins();
 		/// updateGraph();
 		repaint();
	}

	public void resetLabelPositions(double w0, double h0, double w1, double h1) {
		if (gs.useLegendPosition()) {
			gs.setLegendPosition(w1 * gs.getLegendPosition(GraphSettings.X_AXIS) / w0, h1
					* gs.getLegendPosition(GraphSettings.Y_AXIS) / h0);
		}
		for (Enumeration e = gs.getLabels().elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();
			if (gl.usePosition())
				gl.setLocation(w1 * gl.getX() / w0, h1 * gl.getY() / h0);
			if (gl.equals(GraphLabel.PIPER_Y1))
				gl.setRotation(-arc);
			else if (gl.equals(GraphLabel.PIPER_Y2))
				gl.setRotation(arc);
		}
	}

	
	/**
	 * Overloaded paintComponent, is called at each repaint. If the panel size
	 * changed then the user probably resized the window with the mouse and we
	 * have to reset the margins of the graph.
	 * 
	 * @param g
	 *            graphics context
	 */
	public void paintComponent(Graphics g) {
		//  super.paintComponent(g);
		
		if (g == null ) return;

		currentG=VectorGraphics.create(g);
        	panelSize = getSize();


                // System.out.println(panelSize.height);
                if (firstDraw){
                    panelSizeOriginal=new Dimension(panelSize);
                    firstDraw=false;
                }

               double  s1=Math.sqrt(panelSizeOriginal.height*panelSizeOriginal.height + 
                                   panelSizeOriginal.width*panelSizeOriginal.width);
               double  s2=Math.sqrt(panelSize.height*panelSize.height + panelSize.width*panelSize.width);

               if (gs.getAttResizable()){
               scalingFrame = (float) (s2/s1);
               scalingFrameY = (float) ((panelSize.height)/(double)panelSizeOriginal.height);
               scalingFrameX = (float) ((panelSize.width)/(double)panelSizeOriginal.width);
               } else {
               scalingFrame = 1;
               scalingFrameY =1;
               scalingFrameX =1; 
               }; 

               xSep = xSepIni*scalingFrameX;
               ySep = ySepIni*scalingFrameY;
               keyLen=keyLenIni*scalingFrameX;



 	       drawAllGraphics(panelSize, currentG);
               
                 if (numberLabels != gs.getLabels().size()){
                 numberLabels = gs.getLabels().size();
                 iniFonts.clear();
                 for (Enumeration e = gs.getLabels().elements(); e.hasMoreElements();) {
                        GraphLabel gl = (GraphLabel) e.nextElement();
                                         iniFonts.addElement(gl.getFont());
                     };
                 } else {  
                 int k=0; 
                 for (Enumeration e = gs.getLabels().elements(); e.hasMoreElements();) {
                                       GraphLabel gl = (GraphLabel) e.nextElement();
                                        gl.setFont( scaleFont(iniFonts.elementAt(k)  ));
                                        k++;
                     };
                  }; 


                 // draw zooming rectangle 
                 if(_isDragging){
                    int ww1=_lastX - _startX;
                    int hh1=_lastY - _startY;
                    currentG.setPaint( new Color(180, 180, 220) );
                    currentG.drawRect(_startX, _startY, ww1,hh1);
                    currentG.setPaint( new Color(177, 234, 220, 125) );
                    currentG.fillRect( _startX, _startY, ww1,hh1);
                   }



	       // System.out.println( scalingFrame );
	
	}

	
	void drawAllGraphics(Dimension size, VectorGraphics g2) {
		 
		
		
		if (width != size.getWidth() || height != size.getHeight()) {
			
			double w = width;
			double h = height;
			width = size.width;
			height = size.height;
			updateMargins();
			resetLabelPositions(w, h, size.width, size.height);  
			// updateGraph();

		}

		if (gs.getAntiAlias()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		            RenderingHints.VALUE_ANTIALIAS_ON);
            } else {
            	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            }
		
		
           g2.setColor(gs.getBackgroundColor());
	       g2.fillRect(0, 0, width, height);

	      
	       
	     
	  if (gs.drawBox()) {
		g2.setColor(Color.red); 
	    g2.setColor(gs.getBoxFillColor());
		g2.fillRect( borderWidth, borderWidth, width - 2 * borderWidth,
                                 height - 2 * borderWidth);
		g2.setColor(gs.getBoxColor());
		g2.drawRect( borderWidth, borderWidth, width - 2 * borderWidth,
                                 height - 2 * borderWidth );

	   //  fillGraphArea(g2);	
	    // updateGraph();
	
	  }
	  
	  
	  fillGraphArea(g2);	
	  updateGraph();
	   
		
		
		
	}
	 
	
	/**
	 * This function builds the graph in a double-buffered image zone.
	 */

	// chekanov
	protected  VectorGraphics  createGraphics() {
		
		if (currentG == null) return null;
		
		VectorGraphics g2=currentG;
		
		
		// Graphics2D g2 = bi.createGraphics();

// turn on antialiasing - bad idea !
// S.Chekanov

                if (gs.getAntiAlias()) {
		     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
                } else {
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);
                }



		g2.setColor(gs.getBackgroundColor());
		g2.fillRect(0, 0, width, height);
		if (gs.drawBox()) {
			g2.setColor(gs.getBoxFillColor());
			g2.fillRect( borderWidth, borderWidth, width - 2 * borderWidth,
                                     height - 2 * borderWidth);
			g2.setColor(gs.getBoxColor());
			g2.drawRect( borderWidth, borderWidth, width - 2 * borderWidth,
                                     height - 2 * borderWidth );
		}
		fillGraphArea(g2);
		return g2;
	}

	/**
	 * This function builds the graph in a double-buffered image zone.
	 */
	abstract void updateGraph();



	/**
	 * Prints the graph.
	 * 
	 * @param g
	 *            graphics instance, to which we print the graph.
	 * @param pageFormat
	 *            format of the page
	 * @param pageIndex
	 *            index of the page
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		g.translate((int) pageFormat.getImageableX(), (int) pageFormat
				.getImageableY());
		if (pageIndex >= 1)
			return Printable.NO_SUCH_PAGE;
		// //int wPage = (int)pageFormat.getImageableWidth();
		// //int hPage = (int)pageFormat.getImageableHeight();
		// //paintComponent(g);
		// Color bgColor = gs.backgroundColor;
		// gs.backgroundColor = Color.white;
		width += 1; // trick to rebuild the bufferedImage
		paintComponent(g);
		// gs.backgroundColor = bgColor;
		System.gc();
		return PAGE_EXISTS;
	}

	/**
	 * GraphMouseListener, allows to drag'n drop with the mouse. Currently, this
	 * listener locates the coordinates of the mouse pointer and then checks
	 * what is beneath it: legends, labels, curves, points... Double-clicking
	 * then opens a context-sensitive dialog (to change the label-settings, for
	 * example).
	 */
	public class GraphMouseListener implements MouseListener {

		/**
		 * Picks up a mouse-click.
		 * 
		 * @param e
		 *            mouse-click event
		 */
		public void mouseClicked(MouseEvent e) {


                   xStart = e.getX();
                   yStart = e.getY();



                   //  show coordinated with the middle mouse buttom
                  if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                        if (e.getClickCount() == 1) { 
			height = panelSize.height;
			width = panelSize.width;
			if (xStart > leftMargin-1 && width - rightMargin+1 > xStart
		        && yStart > topMargin-1 && yStart < height - bottomMargin+1) {
                        double Xndc=(double)xStart /  width;
                        double Yndc=1.0-( (double)yStart / height);
		        JHPlot.showMouse("Mouse at",toUserX(xStart), toUserY(yStart), Xndc, Yndc, (int)xStart,(int)yStart);

                          _startX = xStart;
                          _startY = yStart;
                          _lastX = xStart;
                          _lastY = yStart;
                          _isDragging = true;
                          // System.out.println(_isDragging);
			}
                     } // end 3rd button 
                    }





			double xx, yy;
			if (e.getClickCount() == 2 && jplot != null) {
				int a = e.getX() + 20;
				int b = e.getY() + 20;
				// show the legend parameter dialog if the legend is hit:
				xx = gs.getLegendPosition(X);
				yy = gs.getLegendPosition(Y);
				if (gs.drawLegend() && xStart > xx && xStart < xx + legendWidth
						&& yStart > yy && yStart < yy + legendHeight) {
					jplot.legendPanel.show(jplot.frame, a, b);
				} else {
					// show the label parameters dialog if a label is hit:
					boolean found = false;
					for (Enumeration el = gs.getLabels().elements(); el
							.hasMoreElements();) {
						GraphLabel gl = (GraphLabel) el.nextElement();
						if (gl.contains(xStart, yStart)) {
							jplot.labelPanel.show(jplot.frame, a, b);
							found = true;
							break;
						}
					}
					if (!found) {
						// show the drawing properties dialog if a line or point
						// is hit
						// Better put this at the end: the mouse is generally
						// used to
						// drag labels anyway, and the following test is a bit
						// costly:
						for (Enumeration e2 = data.elements(); e2
								.hasMoreElements();) {
							DataArray da = (DataArray) e2.nextElement();
							for (Enumeration e3 = da.getData().elements(); e3
									.hasMoreElements();) {
								PlotPoint p = (PlotPoint) e3.nextElement();
								xx = toX(p.getX());
								yy = toY(p.getY());
								if (xStart > xx - 3 && xStart < xx + 3
										&& yStart > yy - 3 && yStart < yy + 3) {
									LinePars lp = jplot.styleChooser.show(a, b,
											(LinePars) da);
									if (lp != null) {
										gs.setDataChanged(lp.dataChanged());
										PanelPlot pp = (PanelPlot) jplot.plotPanels
												.get(da.getFileIndex());
										pp.updateStyle(da.getColumnIndex(), lp);
									}
									found = true;
									break;
								}
							}
							if (found)
								break;
						}
					}
				}
			}
		}

		/**
		 * Picks up a mouse-Entered event. Does nothing.
		 * 
		 * @param e
		 *            mouse event
		 */
		public void mouseEntered(MouseEvent e) {
		}

		/**
		 * Picks up a mouse-Exited event. Does nothing.
		 * 
		 * @param e
		 *            mouse event
		 */
		public void mouseExited(MouseEvent e) {
		}

		/**
		 * Picks up a mouse-drag event. Legends and labels can be dragged and
		 * dropped.
		 * 
		 * @param active
		 *            true if we activated an item, e.g. a label.
		 * @param x
		 *            x-position
		 * @param y
		 *            y-position
		 * @param w
		 *            width of the object which will be dragged
		 * @param h
		 *            height of the object which will be dragged
		 */
		public boolean startDrag(boolean active, int x, int y, int w, int h) {
			if (!active) {
				active = true;
				x1 = x;
				y1 = y;
				boxWidth = w;
				boxHeight = h;
				Graphics g = getGraphics();

				// draws a box with the upper-left corner at x1,y1 and with the
				// specified width (to the right) and height (direct to the
				// bottom):
				// ------------------------------------------------------------------
				g.drawRect(x1, y1, boxWidth, boxHeight);
			} else
				active = false;
			return active;
		}

		
		
		
		
		
		
		
		/**
		 * Picks up a mouse-pressed event. Starts the dragging event, if there's
		 * something interesting below the mouse.
		 * 
		 * @param e
		 *            mouse event
		 */
		public void mousePressed(MouseEvent e) {
			double xx, yy;
			xStart = e.getX();
			yStart = e.getY();
			if (JPlot.debug)
				System.out.println("  x,y = " + xStart + "," + yStart);



// start zoom 
                   zoomModeX=false;
                   zoomModeY=false;
                  if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                        if (xStart > leftMargin-1 && xStart< leftMargin-1+axisLength[X] 
                        && yStart > topMargin-1 && yStart < topMargin+axisLength[Y]) {
// if the mouse is outside of plot, zoom it
                        } else {
                         zoomModeX=true;
                         zoomModeY=true;
                         startZoomX=xStart;
                         startZoomY=yStart;
                          }
                     } // end 3rd button




/*
// what is this? SC
			Rectangle2D r = new Rectangle2D.Double(10, 20, 100, 100);
			Point p = e.getPoint();
			if (r.contains(p)) {

				return;
			}
*/


			// not my staff
			 if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                          //    System.out.println("  legend x,y = " + xStart + "," + yStart);
				// If the mouse pointer hits the legend box,
				// start dragging of the legend:
				// ------------------------------------------
				xx = gs.getLegendPosition(X);
				yy = gs.getLegendPosition(Y);
				if (gs.drawLegend() && xStart > xx && xStart < xx + legendWidth
						&& yStart > yy && yStart < yy + legendHeight) {
					legendActive = startDrag(legendActive, (int) xx, (int) yy,
							(int)(legendWidth), (int)(legendHeight));
					somethingActive = legendActive;
				}

				// Check whether the mouse hits one of the current labels
				// -------------------------------------------------------
				else {
					for (Enumeration el = gs.getLabels().elements(); el
							.hasMoreElements();) {
						GraphLabel gl = (GraphLabel) el.nextElement();
						if (gl.contains(xStart, yStart)) {
							gl.setActive(startDrag(gl.isActive(), (int) (gl
								.getX()), (int) (gl.getY()), (int)(gl
								.getWidth()), (int) (gl.getHeight())));
							somethingActive = gl.isActive();
							break;
						}
					}
				}

			} // left

			// right mouse: edit this graph
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
		menu.show(e.getComponent(), e.getX(), e.getY());
           	}



		}

		/**
		 * Stops dragging if we are actually dragging, does nothing otherwise.
		 * 
		 * @param e
		 *            mouse event
		 */
		public void mouseReleased(MouseEvent e) {

                        
                       // stop rectangle zooming 
                      if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                      if (_isDragging) {
                         _lastX = e.getX();
                         _lastY = e.getY();
                         _lastX = e.getX();
                         _lastY = e.getY();
                         _isDragging = false;

                         // protect for small moves
                         double ss=Math.sqrt((_lastX-_startX)*(_lastX-_startX) +  
                                             (_lastY-_startY)*(_lastY-_startY)); 

                          if (ss>4) {

 
                          repaint();


                       // remember default
                        if (zoomFirst) {
                        defAutoX=gs.autoRange(0);
                        defAutoY=gs.autoRange(1);
                        defXmin=gs.getMinValue(0);
                        defYmin=gs.getMinValue(1);
                        defXmax=gs.getMaxValue(0);
                        defYmax=gs.getMaxValue(1);
                        zoomFirst=false;
                        }

                        gs.setAutoRange(0,false);
                        gs.setAutoRange(1,false);

                        double newXmin=toUserX(_startX);
                        double newXmax=toUserX(_lastX);
                        double newYmin=toUserY(_startY);
                        double newYmax=toUserY(_lastY);
                         if (newXmax>newXmin) gs.setRange(0,newXmin,newXmax);
                         if (newXmax<newXmin) gs.setRange(0,newXmax,newXmin);
                         if (newYmax>newYmin) gs.setRange(1,newYmin,newYmax);
                         if (newYmax<newYmin) gs.setRange(1,newYmax,newYmin);
                          jplot.updateGraphIfShowing();
                      } }


                      } // end third buttom

                       _isDragging = false;

                      

                        // Axis zooming System.out.println("OK");
                        if (zoomModeX == true || zoomModeY== true ) {

                        int beginX = Math.min(startZoomX, currentZoomX);
                        int beginY = Math.min(startZoomY, currentZoomY);
                        int wX = Math.abs(currentZoomX - startZoomX);
                        int hY = Math.abs(currentZoomY - startZoomY);

                         // remember default
                        if (zoomFirst) {
                        defAutoX=gs.autoRange(0);
                        defAutoY=gs.autoRange(1);
                        defXmin=gs.getMinValue(0);               
                        defYmin=gs.getMinValue(1);
                        defXmax=gs.getMaxValue(0);
                        defYmax=gs.getMaxValue(1);
                        zoomFirst=false;
                        }

                        gs.setAutoRange(0,false);
                        gs.setAutoRange(1,false);

                        if (zoomModeX) {
                        double newXmin=toUserX(beginX);
                        double newXmax=toUserX(beginX+wX);
                        gs.setRange(0, newXmin,  newXmax);
                        jplot.updateGraphIfShowing();
                        }

                       if (zoomModeY) {
                        double newYmin=toUserY(beginY+hY);
                        double newYmax=toUserY(beginY);
                        gs.setRange(1, newYmin,  newYmax);
                        jplot.updateGraphIfShowing();
                        }

                        currentZoomX=startZoomX;
                        currentZoomY=startZoomY;
                        zoomModeX=false;
                        zoomModeY=false;

                        // repaint(); 
                       }



			if (!somethingActive)
				return;
			else if (legendActive) {
				legendActive = false;
				gs.setLegendPosition((double) x1, (double) y1);
				gs.setUseLegendPosition(true);
				gs.setDataChanged(true);
				// updateGraph();
                 repaint();
 
			} else {
				for (Enumeration el = gs.getLabels().elements(); el
						.hasMoreElements();) {
					GraphLabel gl = (GraphLabel) el.nextElement();
					if (gl.isActive()) {
						gl.setActive(false);
						if (x1 != (int) gl.getX() || y1 != (int) gl.getY()) {
							gs.setDataChanged(true);
							gl.setLocation((double) x1, (double) y1);
							gl.setUsePosition(true);
							// updateGraph();
				            repaint();

                              
                                // show label position in status bar
                                double Xndc=(double)x1 /  width;
                                double Yndc=1.0-( (double)y1 / height);
                                JHPlot.showMouse("Label position", toUserX(x1), toUserY(y1), Xndc, Yndc, (int)x1, (int)y1 );



                                			if (JPlot.debug) {
								System.out.println("fixing label "
										+ gl.getText() + ", bb = " + x1 + ","
										+ y1 + "," + (int) gl.getWidth() + ","
										+ (int) gl.getHeight() + ",  text at "
										+ (int) gl.getXPos() + ","
										+ (int) gl.getYPos());
							}
						} else
							repaint();
						break;
					}
				}
			}
			somethingActive = false;
		}



	}

	/**
	 * Drag listener, does the actual painting during dragging.
	 */
	public class DragListener implements MouseMotionListener {

		/**
		 * This method does the actual painting during dragging.
		 * 
		 * @param e
		 *            mouse event
		 */
		public void mouseDragged(MouseEvent e) {


                     int x = e.getX();
                     int y = e.getY();


                        if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) { 
                        if (_isDragging) {
                       _lastX = x;
                       _lastY = y;
                        repaint();
                        // System.out.println("Druggin"); 
                        }
                        } else { _isDragging = false; }


// if this is zoom mode for axis?
                       if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                        if (x > leftMargin-1 && width - rightMargin+1 > x
                        && y > topMargin-1 && y < height - bottomMargin+1) {
// if the mouse is outside of plot, zoom it
                        } else {

                       if (zoomModeX ==  true || zoomModeY == true) {
                        currentZoomX=x;
                        currentZoomY=y; 
                        int beginX = Math.min(startZoomX, currentZoomX);
                        int beginY = Math.min(startZoomY, currentZoomY);
                        int wX = Math.abs(currentZoomX - startZoomX);
                        int hY = Math.abs(currentZoomY - startZoomY);

                        Graphics g = getGraphics();
                        Color old=g.getColor();
                        g.setColor(Color.red);


                       // this is for horizontal zoom
                       if (y > height - bottomMargin+1 && x > leftMargin-1 && 
                         width - rightMargin+1 > x
                         ) { 
                        int ypos=height - (int)(bottomMargin)-4; 
                        g.drawLine(beginX, ypos, (int)(beginX+wX), ypos);
                        zoomModeX =true;
                        zoomModeY=false;
                       }

                      // vertical zoom
                      if (x < leftMargin-1 && y > topMargin-1 && y < height-bottomMargin+1) {
                        int xpos=(int)(leftMargin)+4;
                        g.drawLine(xpos, beginY, xpos,  beginY+hY);
                        zoomModeX=false;
                        zoomModeY=true;
                       }

                       g.setColor(old);
                       
                       }
                       }
                       }
                  


			if (somethingActive) {

				// erase the previous box:
				Graphics g = getGraphics();
				g.setXORMode(boxColor);
				g.drawRect(x1, y1, boxWidth, boxHeight);

				// draw the new box:
				x1 += (x - xStart);
				y1 += (y - yStart);
				g.drawRect(x1, y1, boxWidth, boxHeight);
				g.setPaintMode();
				xStart = x;
				yStart = y;
			}
		}

		/**
		 * Does nothing.
		 * 
		 * @param e
		 *            mouse event
		 */
		public void mouseMoved(MouseEvent e) {


                        _isDragging = false;

			/*
			 * mPosX = e.getX(); mPosY = e.getY(); System.out.println(mPosX);
			 * System.out.println(mPosY); System.out.println("Moved click");
			 */
		}

	}

	/**
	 * Does nothing.
	 * 
	 * @param e
	 *            mouse event
	 */

	/*
	 * public double[] getMousePos() {
	 * 
	 * double [] pos = {(double)mposX,(double)mposY}; return pos; } }
	 * 
	 * 
	 */

	/**
	 * Draw arrow
	 * 
	 * @param g
	 * @param xPoints
	 *            x1,x2 positions
	 * @param yPoints
	 *            y1,y2 positions
	 * @param headLength
	 *            head length
	 * @param headwidth
	 *            head width
	 */

	public void drawPolylineArrow(Graphics g, int[] xPoints, int[] yPoints,
			int headLength, int headwidth) {

		double theta1;
		// calculate the length of the line - convert from Object to Integer to
		// int value
		Object tempX1 = ((Array.get(xPoints, ((xPoints.length) - 2))));
		Object tempX2 = ((Array.get(xPoints, ((xPoints.length) - 1))));
		Integer fooX1 = (Integer) tempX1;
		int x1 = fooX1.intValue();
		Integer fooX2 = (Integer) tempX2;
		int x2 = fooX2.intValue();

		Object tempY1 = ((Array.get(yPoints, ((yPoints.length) - 2))));
		Object tempY2 = ((Array.get(yPoints, ((yPoints.length) - 1))));
		Integer fooY1 = (Integer) tempY1;
		int y1 = fooY1.intValue();
		Integer fooY2 = (Integer) tempY2;
		int y2 = fooY2.intValue();

		int deltaX = (x2 - x1);
		int deltaY = (y2 - y1);

		double theta = Math.atan((double) (deltaY) / (double) (deltaX));

		if (deltaX < 0.0) {
			theta1 = theta + Math.PI; // If theta is negative make it positive
		} else {
			theta1 = theta; // else leave it alone
		}

		int lengthdeltaX = -(int) (Math.cos(theta1) * headLength);
		int lengthdeltaY = -(int) (Math.sin(theta1) * headLength);
		int widthdeltaX = (int) (Math.sin(theta1) * headwidth);
		int widthdeltaY = (int) (Math.cos(theta1) * headwidth);

		g.drawPolyline(xPoints, yPoints, xPoints.length);
		g.drawLine(x2, y2, x2 + lengthdeltaX + widthdeltaX, y2 + lengthdeltaY
				- widthdeltaY);
		g.drawLine(x2, y2, x2 + lengthdeltaX - widthdeltaX, y2 + lengthdeltaY
				+ widthdeltaY);

	}// end drawPolylineArrow

	/**
	 * Another way with closed ends
	 */

	public void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
		// Draw line
		g.drawLine(x1, y1, x2, y2);
		// Calculate x-y values for arrow head
		calcValues(x1, y1, x2, y2);
		g.fillPolygon(xValues, yValues, 3);
	}

	/* CALC VALUES: Calculate x-y values. */

	public void calcValues(int x1, int y1, int x2, int y2) {
		// North or south
		if (x1 == x2) {
			// North
			if (y2 < y1)
				arrowCoords(x2, y2, x2 - haw, y2 + al, x2 + haw, y2 + al);
			// South
			else
				arrowCoords(x2, y2, x2 - haw, y2 - al, x2 + haw, y2 - al);
			return;
		}
		// East or West
		if (y1 == y2) {
			// East
			if (x2 > x1)
				arrowCoords(x2, y2, x2 - al, y2 - haw, x2 - al, y2 + haw);
			// West
			else
				arrowCoords(x2, y2, x2 + al, y2 - haw, x2 + al, y2 + haw);
			return;
		}
		// Calculate quadrant

		calcValuesQuad(x1, y1, x2, y2);
	}

	/*
	 * CALCULATE VALUES QUADRANTS: Calculate x-y values where direction is not
	 * parallel to eith x or y axis.
	 */

	public void calcValuesQuad(int x1, int y1, int x2, int y2) {
		double arrowAng = Math.toDegrees(Math.atan((double) haw / (double) al));
		double dist = Math.sqrt(al * al + aw);
		double lineAng = Math.toDegrees(Math.atan(((double) Math.abs(x1 - x2))
				/ ((double) Math.abs(y1 - y2))));

		// Adjust line angle for quadrant
		if (x1 > x2) {
			// South East
			if (y1 > y2)
				lineAng = 180.0 - lineAng;
		} else {
			// South West
			if (y1 > y2)
				lineAng = 180.0 + lineAng;
			// North West
			else
				lineAng = 360.0 - lineAng;
		}

		// Calculate coords

		xValues[0] = x2;
		yValues[0] = y2;
		calcCoords(1, x2, y2, dist, lineAng - arrowAng);
		calcCoords(2, x2, y2, dist, lineAng + arrowAng);
	}

	/*
	 * CALCULATE COORDINATES: Determine new x-y coords given a start x-y and a
	 * distance and direction
	 */

	public void calcCoords(int index, int x, int y, double dist, double dirn) {
		// System.out.println("dirn = " + dirn);
		while (dirn < 0.0)
			dirn = 360.0 + dirn;
		while (dirn > 360.0)
			dirn = dirn - 360.0;
		// System.out.println("dirn = " + dirn);

		// North-East
		if (dirn <= 90.0) {
			xValues[index] = x + (int) (Math.sin(Math.toRadians(dirn)) * dist);
			yValues[index] = y - (int) (Math.cos(Math.toRadians(dirn)) * dist);
			return;
		}
		// South-East
		if (dirn <= 180.0) {
			xValues[index] = x
					+ (int) (Math.cos(Math.toRadians(dirn - 90)) * dist);
			yValues[index] = y
					+ (int) (Math.sin(Math.toRadians(dirn - 90)) * dist);
			return;
		}
		// South-West
		if (dirn <= 90.0) {
			xValues[index] = x
					- (int) (Math.sin(Math.toRadians(dirn - 180)) * dist);
			yValues[index] = y
					+ (int) (Math.cos(Math.toRadians(dirn - 180)) * dist);
		}
		// Nort-West
		else {
			xValues[index] = x
					- (int) (Math.cos(Math.toRadians(dirn - 270)) * dist);
			yValues[index] = y
					- (int) (Math.sin(Math.toRadians(dirn - 270)) * dist);
		}
	}

	// ARROW COORDS: Load x-y value arrays */

	public void arrowCoords(int x1, int y1, int x2, int y2, int x3, int y3) {
		xValues[0] = x1;
		yValues[0] = y1;
		xValues[1] = x2;
		yValues[1] = y2;
		xValues[2] = x3;
		yValues[2] = y3;
	}

	/**
	 * Draw arrow
	 * 
	 * 
     * this allows you to draw an arrow with
     * headlength (headsize),
     * headwidth (headsize - difference),
     * hedheight (factor (relative to headlength)),
	 * 
	 * @param g
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	
	public  void drawArrow2(VectorGraphics g,int x1, int y1, int x2, int y2) {
		
		
		// g.drawPolygon(getArrow(x1, y1, x2, y2, 10, 5,0.5));
	    g.drawPolygon(getArrow(x1, y1, x2, y2, al, aw, hhaw));
		}

		private Polygon getArrow(int x1, int y1, int x2, int y2, int headsize, int difference, double factor) {
		int[] crosslinebase = getArrowHeadLine(x1, y1, x2, y2, headsize);
		int[] headbase = getArrowHeadLine(x1, y1, x2, y2, headsize - difference);
		int[] crossline = getArrowHeadCrossLine(crosslinebase[0], crosslinebase[1], x2, y2, factor);

		Polygon head = new Polygon();

		head.addPoint(headbase[0], headbase[1]);
		head.addPoint(crossline[0], crossline[1]);
		head.addPoint(x2, y2);
		head.addPoint(crossline[2], crossline[3]);
		head.addPoint(headbase[0], headbase[1]);
		head.addPoint(x1, y1);

		return head;
		}

		private int[] getArrowHeadLine(int xsource, int ysource,int xdest,int ydest, int distance) {
		int[] arrowhead = new int[2];
		int headsize = distance;

		double stretchfactor = 0;
		stretchfactor = 1 - (headsize/(Math.sqrt(((xdest-xsource)*(xdest-xsource))+((ydest-ysource)*(ydest-ysource)))));

		arrowhead[0] = (int) (stretchfactor*(xdest-xsource))+xsource;
		arrowhead[1] = (int) (stretchfactor*(ydest-ysource))+ysource;

		return arrowhead;
		}

		private int[] getArrowHeadCrossLine(int x1, int x2, int b1, int b2, double factor) {
		int [] crossline = new int[4];

		int x_dest = (int) (((b1-x1)*factor)+x1);
		int y_dest = (int) (((b2-x2)*factor)+x2);

		crossline[0] = (int) ((x1+x2-y_dest));
		crossline[1] = (int) ((x2+x_dest-x1));
		crossline[2] = crossline[0]+(x1-crossline[0])*2;
		crossline[3] = crossline[1]+(x2-crossline[1])*2;
		return crossline;
		} 
	
	
	



		/**
		 * Draw a key
		 * @param g2
		 * @param gl
		 * @param x 
		 * @param y
		 */
    protected  void drawKey(VectorGraphics  g2, GraphLabel gl, float x, float y) {

       Color old1=g2.getColor();
       Stroke old2= g2.getStroke();
      
       // set key attributes
       g2.setStroke(new BasicStroke(gl.getKeyLineWidth()));  
       g2.setColor(gl.getKeyColor());
       
       FontMetrics fM = g2.getFontMetrics();

       float hh = (float) (0.7*gl.getTextHeight()*scalingFrame);
       float ww = fM.stringWidth("w")*scalingFrame;
       float ypos= y-(float)( 0.5*gl.getTextHeight()*scalingFrame ) + (float)gl.getTextDescent(); 
       float lsize=0.5f*gl.getKeySize()*ww;

      // symbols 
       if (gl.getKeyType()>-1 && gl.getKeyType()<20) {
               GPoints.drawPointType(gl.getKeyType(), g2, x, ypos+0.25*lsize, lsize);
               return;
       }

       // filled rectangle
       if (gl.getKeyType()==20 ) {
                 g2.fillRect(x-(0.5*lsize),ypos-1.5f*(float)gl.getTextDescent(),lsize,hh);
                 return;        
       }

       // draw rectangle
       if (gl.getKeyType()==21 ) {
                 g2.drawRect(x-(0.5*lsize),ypos-1.5f*(float)gl.getTextDescent(),lsize,hh);
                 return;
       }

       // solid line
       if (gl.getKeyType()==30) {
    	          x=x-(0.5f*lsize);
                  g2.drawLine(x, ypos, x+ lsize, ypos);
                  return;           
       }
       // dashed line
       if (gl.getKeyType()==31) { 
       	  int dash=(int) (lsize/7.0);
          x=x-(0.5f*lsize);
       	  drawDashedLine(g2,(int)x,(int)ypos,(int)(x+lsize),(int)ypos,2*dash,dash);
          return;
       }
  
      // dotted  line
       if (gl.getKeyType()==32) { 
       	  int dash=(int) (lsize/10.0);
          x=x-(0.5f*lsize);
       	  drawDashedLine(g2,(int)x,(int)ypos,(int)(x+lsize),(int)ypos,dash,3*dash);
          return;
       }

       
       g2.setColor(old1);
       g2.setStroke(old2);
    }
  	
	
	/** draw dotted or dashed lines
	 * 
	 * */
	
	public void drawDashedLine(VectorGraphics g,int x1,int y1,int x2,int y2,
                           double dashlength, double spacelength) {
  if((x1==x2)&&(y1==y2)) {
    g.drawLine(x1,y1,x2,y2);
    return;
    }
  double linelength=Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
  double yincrement=(y2-y1)/(linelength/(dashlength+spacelength));
  double xincdashspace=(x2-x1)/(linelength/(dashlength+spacelength));
  double yincdashspace=(y2-y1)/(linelength/(dashlength+spacelength));
  double xincdash=(x2-x1)/(linelength/(dashlength));
  double yincdash=(y2-y1)/(linelength/(dashlength));
  int counter=0;
  for (double i=0;i<linelength-dashlength;i+=dashlength+spacelength){
      g.drawLine((int) (x1+xincdashspace*counter),
                 (int) (y1+yincdashspace*counter),
                 (int) (x1+xincdashspace*counter+xincdash),
                 (int) (y1+yincdashspace*counter+yincdash));
      counter++;
      }
  if ((dashlength+spacelength)*counter<=linelength)
     g.drawLine((int) (x1+xincdashspace*counter),
                (int) (y1+yincdashspace*counter),
                x2,y2);
}
	
	
	
	
	
	
	/*
	
	
	
	
	public void drawDottedLine(Graphics2D g2, float x1, float y1, float x2, float y2) {
		 
		 float[] dashvalues;
		
		Stroke old= g2.getStroke();
		
		Stroke stroke = new BasicStroke((float)width, 
		       BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
		
		g2.setStroke(stroke);
		line.setLine(x1, y1, x2, y2);
        g2.draw(line);
        g2.setStroke(old);
	}
	*/
	

}
