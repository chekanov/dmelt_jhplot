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

import hep.aida.ref.histogram.Cloud2D;
import hep.aida.ref.histogram.Histogram2D;

import java.awt.*;

import javax.swing.JOptionPane;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;

import sgtplot.*;
import sgtplot.dm.*;
import sgtplot.swing.JPlotLayout;
import sgtplot.util.Point2D;
import sgtplot.util.Range2D;

/**
 * A class to build a contour (density) plots in 2D. Use it to show H2D histograms, F2D functions, density
 * plots for P1D arrays. 
 * 
 * @author S.Chekanov
 * 
 */
public class HPlot2D extends GHFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SgtContour[][] cp;
	private GridAttribute[][] gridAtt;
	private ContourLevels[][] clevels;
	private ColorMap[][] colormap;
	private int width = 600;
	private int height = 400;
	private String titleG[][];
	private String titleX[][];
	private String titleY[][];
	private Range2D xA[][];
	private Range2D yA[][];
	private Range2D levels[][];
	private boolean autorangeX[][];
        private boolean autorangeY[][];
        private boolean autorangeZ[][];
	private int style[][];
        private Thread1 m_Close;
 
	/**
	 * Create canvas with a several plots
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

	public HPlot2D(String title, int xsize, int ysize, int n1, int n2,
			boolean set) {

		super(title, xsize, ysize, n1, n2, set);

		style = new int[N1final][N2final];
		autorangeX = new boolean[N1final][N2final];
                autorangeY = new boolean[N1final][N2final];
                autorangeZ = new boolean[N1final][N2final];
		levels = new Range2D[N1final][N2final];
		cp = new SgtContour[N1final][N2final];
		gridAtt = new GridAttribute[N1final][N2final];
		clevels = new ContourLevels[N1final][N2final];
		colormap = new ColorMap[N1final][N2final];

		titleG = new String[N1final][N2final];
		titleX = new String[N1final][N2final];
		titleY = new String[N1final][N2final];
		xA = new Range2D[N1final][N2final];
		yA = new Range2D[N1final][N2final];
                Font fleg = new Font("Arial", Font.BOLD, 12);

		// build empty canvas
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				titleG[i1][i2] = "";
				titleX[i1][i2] = "X";
				titleY[i1][i2] = "Y";

				style[i1][i2] = 0;
				autorangeX[i1][i2] = true;
                                autorangeY[i1][i2] = true;
                                autorangeZ[i1][i2] = true;

				cp[i1][i2] = new SgtContour((int) (width / N1final),
						(int) (height / N2final));
				levels[i1][i2] = new Range2D(0, 100, 20);
				colormap[i1][i2] = createColorMap(levels[i1][i2]);
				clevels[i1][i2] = ContourLevels.getDefault(levels[i1][i2]);
				gridAtt[i1][i2] = new GridAttribute(clevels[i1][i2]);
				gridAtt[i1][i2].setColorMap(colormap[i1][i2]);
				gridAtt[i1][i2].setStyle(GridAttribute.RASTER_CONTOUR);
				xA[i1][i2] = new Range2D(0, 1, 0.02);
				yA[i1][i2] = new Range2D(0, 1, 0.02);
                                cp[i1][i2].getPlot().getAxis(0).setLabelFont(fleg);
                                cp[i1][i2].getPlot().getAxis(1).setLabelFont(fleg);
                                cp[i1][i2].getPlot().getAxis(0).setLabelHeightP(0.04);
                                cp[i1][i2].getPlot().getAxis(1).setLabelHeightP(0.04);


	                    /*
				 * Domain dom= new Domain(xA[N1][N2],yA[N1][N2]); try {
				 * cp[N1][N2].getPlot().setRange(dom); } catch
				 * (PropertyVetoException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); }
				 */

				// cp[i1][i2].getPlot().setSize(width, height);
				if (set)
					mainPanel.add(cp[i1][i2]);
			}
		}

	}


    /**
     * Resize the current pad. It calculates the original pad sizes,
     * and then scale it by a given factor. In this case, the pad sizes
     * can be different.
     * 
     * @param widthScale scale factor applied to the width of the current pad
     * @param heightScale scale factor applied the  height of the current pad.
     */

    public void resizePad(double widthScale, double heightScale) {

             Dimension dim=cp[N1][N2].getSize();
             double h=dim.getHeight();
             double w=dim.getWidth();
             cp[N1][N2].setPreferredSize(new Dimension((int)(w*widthScale), (int)(h*heightScale)));
             cp[N1][N2].setMinimumSize(new Dimension((int)(w*widthScale), (int)(h*heightScale)));
             cp[N1][N2].setSize(new Dimension((int)(w*widthScale), (int)(h*heightScale)));
    }


             /**
             * Resize the pad given by the inxX and indxY. It calculates the original pad sizes,
             * and then scale it by a given factor. In this case, the pad sizes
             * can be different.
             * @param n1
             *            the location of the plot in x
             * @param n2
             *            the location of the plot in y
             * 
             * @param widthScale scale factor applied to the width of the current pad
             * @param heightScale scale factor applied the  height of the current pad.
             */
        public void resizePad(int n1, int n2, double widthScale, double heightScale) {
                 Dimension dim=cp[n1][n2].getSize();
                 double h=dim.getHeight();
                 double w=dim.getWidth();
                 cp[n1][n2].setPreferredSize(new Dimension((int)(w*widthScale), (int)(h*heightScale)));
                 cp[n1][n2].setMinimumSize(new Dimension((int)(w*widthScale), (int)(h*heightScale)));
                 cp[n1][n2].setSize(new Dimension((int)(w*widthScale), (int)(h*heightScale)));
        }


	
	/**
	 * Plot cloud 2D. Assume 100 bins in X and Y
	 * 
	 * @param c2D
	 *            Input 2D cloud
	 */

	public void draw(Cloud2D c2d) {

		draw(  new H2D(c2d,100,100)  );
		
	}
	
	
	/**
	 * Draw 2D histogram 
	 * @param h1d input istogram1D 
	 */
	public void draw(Histogram2D  h1d) {
		
		draw( new H2D(h1d) );
		
	}
	
	
	
	
	
	
	
	
	/**
	 * Draw 2D histogram using a style (see setStyle())
	 * 
	 * @param h
	 *            input historam
	 */
	public void draw(H2D h) {

		double Xmin;
		double Xmax;
		double Ymin;
		double Ymax;
		int nx;
		int ny;
		double dx = 1;
		double dy = 1;

		if (h.getLabelX() != null)
			if (h.getLabelX().length()>0) setNameX(h.getLabelX() );
		if (h.getLabelY() != null)
			if (h.getLabelY().length()>0) setNameY(h.getLabelY() );
	
		
		Histogram2D h2d = h.get();
		double binHight = h2d.maxBinHeight();

                 Xmin = h2d.xAxis().lowerEdge();
                 Xmax = h2d.xAxis().upperEdge();
                 nx = h2d.xAxis().bins(); // number of bins
                 Ymin = h2d.yAxis().lowerEdge();
                 Ymax = h2d.yAxis().upperEdge();
                 ny = h2d.yAxis().bins(); // number of bins

/*

		if (autorangeX[N1][N2] == false) {
			Xmin = xA[N1][N2].start;
			Xmax = xA[N1][N2].end;
                        nx = (int) ((Xmax - Xmin) / xA[N1][N2].delta);
                } else {
                        Xmin = h2d.xAxis().lowerEdge();
                        Xmax = h2d.xAxis().upperEdge();
                        nx = h2d.xAxis().bins(); // number of bins
                 }
 


                if (autorangeY[N1][N2] == false) {
                        Ymin = yA[N1][N2].start;
                        Ymax = yA[N1][N2].end;
                        ny = (int) ((Ymax - Ymin) / yA[N1][N2].delta);
                } else { 
                        Ymin = h2d.yAxis().lowerEdge();
                        Ymax = h2d.yAxis().upperEdge();
                        ny = h2d.yAxis().bins(); // number of bins
                }

*/

                 double X1 = xA[N1][N2].start;
                 double X2 = xA[N1][N2].end;
                 double Y1 = yA[N1][N2].start;
                 double Y2 = yA[N1][N2].end;
 

                 // System.out.println( X1);
                 // System.out.println( X2);
                /*
        	dx = Math.abs(Xmax - Xmin) / nx;
        	dy = Math.abs(Ymax - Ymin) / ny;
		xA[N1][N2] = new Range2D(Xmin, Xmax, dx);
		yA[N1][N2] = new Range2D(Ymin, Ymax, dy);
                */


		double[] x = new double[nx];
		double[] y = new double[ny];
                int[] xi = new int[nx];
                int[] yi = new int[ny];

		double x1, y1, x2, y2;
		int count = 0;
		Ymin = Math.min(Ymin, Ymax);
		Xmin = Math.min(Xmin, Xmax);

                int NYY=0;
		for (int j = 0; j < ny; j++) {
			double yy = h2d.yAxis().binCenter(j+1);

                        boolean failedY=false;
                        if (autorangeY[N1][N2] == false) {
                           if (yy<Y1 || yy>Y2) failedY=true; 
                         }

                              if (failedY==false) {
                                    y[NYY]=yy; yi[NYY]=j; 
                                    NYY++;
                               } 

                 } 

          
        int NXX=0;                  
	for (int i = 0; i < nx; i++) {
   		  double xx = h2d.xAxis().binCenter(i+1);
                  boolean failedX=false;
                  if (autorangeX[N1][N2] == false) {
                        if (xx<X1 || xx>X2) { failedX=true;} 
                  }
                      if (failedX==false) {
                                    x[NXX]=xx; xi[NXX]=i; 
                                    NXX++;
                      }
               };

          // System.out.println( NXX );
          // System.out.println( NYY);

              double[] grid = new double[NXX * NYY];
              double[] xx1 = new double[NXX];
              double[] yy1 = new double[NYY];

              count=0;
              for (int j = 0; j < NYY; j++) { 
                 yy1[j]=y[j]; 
                 // System.out.println(yy1[j]); 
                 for (int i = 0; i < NXX; i++) { 
                                xx1[i]=x[i]; 
				grid[count] = h2d.binHeight(xi[i] + 1, yi[j] + 1);
                                count++; 
				} 
		}

		drawer(grid,xx1,yy1,null,binHight);

	}

	
	
	/**
	 * Add a label to the Canvas in NDC coordinates. 
	 * 
	 * @param label
	 *            Label to be added
	 * @textHeight
	 *            height of this label          
	 */
	public void add(HLabel label, double textHeight) {

		
	double x = label.getX();
	double y = label.getY();
        Layer layer= cp[N1][N2].getPlot().getFirstLayer();
        Point2D.Double pp=null;
            
         if (label.getPositionCoordinate() == 1) { // NDC 
            	pp=new Point2D.Double(x, y);
          } else if (label.getPositionCoordinate() == 2) {

                

         	Range2D xR= cp[N1][N2].getPlot().getRange().getXRange();
                Range2D yR= cp[N1][N2].getPlot().getRange().getYRange();
                // System.out.println(xR.toString());
                // System.out.println(yR.toString());
            	double xu=cp[N1][N2].getPlot().fromUserX(x, xR);
            	double yu=cp[N1][N2].getPlot().fromUserY(y, yR);
                // System.out.println(xu); 
            	pp=new Point2D.Double(xu, yu);
             }
            	SGLabel mainTitle = new SGLabel("Line Profile Title",
		                             "Profile Plot",
		                             textHeight, pp,
		                             SGLabel.BOTTOM,
		                             SGLabel.CENTER);
       	   
                    mainTitle.setColor(label.getColor()); 
		    mainTitle.setFont(label.getFont());
		    mainTitle.setText(label.getText());
		    layer.addChild(mainTitle);
		     // update();
			
		
	}


         

/**
* Get color bar (key) for the current plot
* @return color bar 
**/	
         public  ColorKey getColorBar() {
            return cp[N1][N2].getPlot().getColorBar();
        }	


/**
* Set color bar (key) for the current plot
* @param  k key   
**/
         public  void setColorBar(ColorKey k) {
            cp[N1][N2].getPlot().setColorBar(k);
        }




	
         /**
         * Private method for collections
         * 
         * @param collection input data  
         */
        private void drawer(Collection coll) {  
	
         }	
	
	
	
	
	
	/**
	 * Private method for all objects
	 * 
	 * @param grid
	 * @param x
	 * @param y
	 * @param title
	 * @param Zmax
	 */
	private void drawer(double[] grid, double[] x, double[] y, String title,
			double Zmax) {

		SGLabel keyLabel = new SGLabel("Key Label", "", new Point2D.Double(0.0,
				0.0));
		keyLabel.setHeightP(0.16);
		keyLabel.setText(titleG[N1][N2]);
		SGTMetaData xMeta = new SGTMetaData(titleX[N1][N2], "ms");
		SGTMetaData yMeta = new SGTMetaData(titleY[N1][N2], "ms");

		SimpleGrid sg = new SimpleGrid(grid, x, y, title);
		sg.setXMetaData(xMeta);
		sg.setYMetaData(yMeta);
		sg.setKeyTitle(keyLabel);

		if (autorangeZ[N1][N2] == true) {
			double d = (Zmax * 1.1) / 7;
			levels[N1][N2] = new Range2D(0, Zmax * 1.1, d);
		}


          
		colormap[N1][N2] = createColorMap(levels[N1][N2]);
		clevels[N1][N2] = ContourLevels.getDefault(levels[N1][N2]);
		gridAtt[N1][N2] = new GridAttribute(clevels[N1][N2]);
		gridAtt[N1][N2].setColorMap(colormap[N1][N2]);

		if (style[N1][N2] == 0) {
			gridAtt[N1][N2].setStyle(GridAttribute.RASTER);
                        cp[N1][N2].getPlot().setTitles("", "", "");
                        cp[N1][N2].getPlot().addData(sg, gridAtt[N1][N2], title);
                        cp[N1][N2].getPlot().getColorBar().setVisible(true);  
                        cp[N1][N2].buildPlot(true); 
		} else if (style[N1][N2] == 1) {
			gridAtt[N1][N2].setStyle(GridAttribute.CONTOUR);
                        cp[N1][N2].getPlot().addData(sg, gridAtt[N1][N2], title);
                        cp[N1][N2].buildPlot(false);
                        cp[N1][N2].getPlot().getColorBar().setVisible(false);
		} else if (style[N1][N2] == 2) {
			gridAtt[N1][N2].setStyle(GridAttribute.RASTER_CONTOUR);
                        cp[N1][N2].getPlot().setTitles(titleG[N1][N2], titleG[N1][N2], "");
                        cp[N1][N2].getPlot().addData(sg, gridAtt[N1][N2], title);
                        cp[N1][N2].getPlot().getColorBar().setVisible(true);  
                        cp[N1][N2].buildPlot(true);
                } else if (style[N1][N2] == 3) {
                        gridAtt[N1][N2].setStyle(GridAttribute.AREA_FILL);
                        cp[N1][N2].getPlot().setTitles(titleG[N1][N2], titleG[N1][N2], "");
                        cp[N1][N2].getPlot().addData(sg, gridAtt[N1][N2], title);
                        cp[N1][N2].buildPlot(false);
                } else if (style[N1][N2] == 4) {
                        gridAtt[N1][N2].setStyle(GridAttribute.AREA_FILL_CONTOUR);
                        // cp[N1][N2].getPlot().setTitles(titleG[N1][N2], titleG[N1][N2], "");
                        cp[N1][N2].getPlot().addData(sg, gridAtt[N1][N2], title);
                        cp[N1][N2].buildPlot(false);
	        } else if (style[N1][N2] == 5) {
                         cp[N1][N2].buildVectors();
                         SGTVector vector = new SGTVector(sg,sg);
                         VectorAttribute vectorAttr = new VectorAttribute(0.0075, Color.red);
                         vectorAttr.setHeadScale(0.5);
                         cp[N1][N2].getPlot().setTitles(titleG[N1][N2], titleG[N1][N2], "");
                         cp[N1][N2].getPlot().addData(vector, vectorAttr, title);
                }

		cp[N1][N2].revalidate();
		cp[N1][N2].repaint();

	}

	
	
	
	/**
	 * Update current canvas
	 */
	
	public void update(){
		
		cp[N1][N2].revalidate();
		cp[N1][N2].repaint();
	}
	
	
	
	
	/**
	 * Get the original canvas
	 * 
	 * @return
	 */
	public JPlotLayout getPlotCanvas() {

		return cp[N1][N2].getPlot();

	}

	/**
	 * Plot P1D data (x-y) as density plot
	 * 
	 * @param h
	 *            input P1D
	 */

	public void draw(P1D h) {

		double Xmin;
		double Xmax;
		double Ymin;
		double Ymax;
		int nx = 1;
		int ny = 1;
		double dx = 1;
		double dy = 1;


                if (autorangeX[N1][N2] == false) {
                        Xmin = xA[N1][N2].start;
                        Xmax = xA[N1][N2].end;
                        nx = (int) ((Xmax - Xmin) / xA[N1][N2].delta);
                        dx = xA[N1][N2].delta;
                } else {
                        Xmin = h.getMin(0);
                        Xmax = h.getMax(0);
                        nx = 60;
                        dx = Math.abs(Xmax - Xmin) / nx;
                 }



                if (autorangeY[N1][N2] == false) {
                        Ymin = yA[N1][N2].start;
                        Ymax = yA[N1][N2].end;
                        ny = (int) ((Ymax - Ymin) / yA[N1][N2].delta);
                        dy = yA[N1][N2].delta;
                } else { 
                        Ymin = h.getMin(1);
                        Ymax = h.getMax(1);
                        ny = 60;
                        dy = Math.abs(Ymax - Ymin) / ny;
                }


		xA[N1][N2] = new Range2D(Xmin, Xmax, dx);
		yA[N1][N2] = new Range2D(Ymin, Ymax, dy);


		if (nx < 1)
			nx = 1;
		if (ny < 1)
			ny = 1;
            if (style[N1][N2]<6) {
                double[] grid = new double[nx * ny];
                double[] x = new double[nx];
                double[] y = new double[ny];

		double x1, y1, x2, y2;
		int count = 0;
		Ymin = Math.min(Ymin, Ymax);
		Xmin = Math.min(Xmin, Xmax);

		double max = -10e24;
		for (int j = 0; j < ny; j++) {
			y1 = Ymin + j * dy;
			y2 = Ymin + (j + 1) * dy;
			y[j] = y1 + 0.5 * dy;

			for (int i = 0; i < nx; i++) {
				x1 = Xmin + i * dx;
				x2 = Xmin + (i + 1) * dx;
				x[i] = x1 + 0.5 * dx;

				int con = 0;
				for (int m = 0; m < h.size(); m++) {
					if (h.getX(m) > x1 && h.getX(m) < x2 && h.getY(m) > y1
							&& h.getY(m) < y2)
						con++;
				}
				grid[count] = con;
				// count max
				if (con > max)
					max = con;
				count++;
			}
		}
                drawer(grid, x, y,null,max);


             } else if (style[N1][N2]==6) {
                Collection coll = new Collection(h.getTitle(), h.size());               
                for (int j = 0; j < h.size(); j++) {
                  SimplePoint sp = new SimplePoint(h.getX(j), h.getY(j),null);
                  coll.addElement((Object)sp);
                } 
                drawer(coll);
             } 
 



	}

	/**
	 * Plot F2D function (x-y) as density plot
	 * 
	 * @param h
	 *            input 2d function
	 */

	public void draw(F2D h) {

		
		if (h.getLabelX() != null)
			if (h.getLabelX().length()>0) setNameX(h.getLabelX() );
		if (h.getLabelY() != null)
			if (h.getLabelY().length()>0) setNameY(h.getLabelY() );
		
		
		double Xmin;
		double Xmax;
		double Ymin;
		double Ymax;
		int nx = 1;
		int ny = 1;
		double dx = 1;
		double dy = 1;


                if (autorangeX[N1][N2] == false) {
                        Xmin = xA[N1][N2].start;
                        Xmax = xA[N1][N2].end;
                        nx = (int) ((Xmax - Xmin) / xA[N1][N2].delta);
                        dx = xA[N1][N2].delta;
                } else {
                        Xmin =0;
                        Xmax =1;
                        nx = 60;
                        dx = Math.abs(Xmax - Xmin) / nx;
                 }



                if (autorangeY[N1][N2] == false) {
                        Ymin = yA[N1][N2].start;
                        Ymax = yA[N1][N2].end;
                        ny = (int) ((Ymax - Ymin) / yA[N1][N2].delta);
                        dy = yA[N1][N2].delta;
                } else {
                        Ymin = 0;
                        Ymax = 1;
                        ny = 60;
                        dy = Math.abs(Ymax - Ymin) / ny;
                }

			xA[N1][N2] = new Range2D(Xmin, Xmax, dx);
			yA[N1][N2] = new Range2D(Ymin, Ymax, dy);


		if (nx < 1)
			nx = 1;
		if (ny < 1)
			ny = 1;
		double[] grid = new double[nx * ny];
		double[] x = new double[nx];
		double[] y = new double[ny];

		int count = 0;
		Ymin = Math.min(Ymin, Ymax);
		Xmin = Math.min(Xmin, Xmax);
		double max = -10e24;
		for (int j = 0; j < ny; j++) {
			y[j] = Ymin + j * dy + 0.5 * dy;
			for (int i = 0; i < nx; i++) {
				x[i] = Xmin + i * dx + 0.5 * dx;
				grid[count] = h.eval(x[i], y[j]);
				// System.out.println(grid[count] );
				if (grid[count] > max)
					max = grid[count];
				count++;
			}
		}

		drawer(grid, x, y,null, max);

	}

	/**
	 * Set autorange for the current plot
	 */
	public void setAutoRange() {
	  autorangeX[N1][N2] = true;
          autorangeY[N1][N2] = true;       
          autorangeZ[N1][N2] = true;
          cp[N1][N2].getPlot().setXAutoRange(true);
          cp[N1][N2].getPlot().setYAutoRange(true);

	}

	/**
	 * Set autorange or not for the current plot
	 * 
	 * @param autorange
	 *            true if autorange
	 */
	public void setAutoRange(boolean autorange) {
		autorangeX[N1][N2] = autorange;
                autorangeY[N1][N2] = autorange;
                autorangeZ[N1][N2] = autorange;
                cp[N1][N2].getPlot().setXAutoRange(autorange);
                cp[N1][N2].getPlot().setYAutoRange(autorange);

	}




  /**
         * Set autorange for the current plot
         *
         * @param axis axis (0=x, 1=y, 2=z) 
         * @param autorange
         *            true if autorange
         */
        public void setAutoRange(int axis, boolean autorange) {
                 if (axis==0) { this.autorangeX[N1][N2] = autorange; cp[N1][N2].getPlot().setXAutoRange(autorange); } 
                 if (axis==1) { this.autorangeY[N1][N2] = autorange;  cp[N1][N2].getPlot().setYAutoRange(autorange); }
                 if (axis==2) { this.autorangeZ[N1][N2] = autorange; }

        }


	/**
	 * Sets the range for the current plot and axis.
	 * In case of Z-axis (axis=2), we changing the contour levels.
	 * @param axis
	 *            apply for axis X (=0) or axis Y (=1) and axis Z (=2) for
	 *            contours
	 * 
	 * @param min
	 *            Min value
	 * @param max
	 *            Max value
	 * @param bins
	 *            number of bins between min and max
	 */

	public void setRange(int axis, double min, double max, int bins) {
		double d = (max - min) / bins;
		if (axis == 0) {
			xA[N1][N2] = new Range2D(min, max, d);
                        cp[N1][N2].getPlot().setXAutoRange(false);
                        cp[N1][N2].getPlot().setXRange(xA[N1][N2]);
                        this.autorangeX[N1][N2] = false;
		}
		if (axis == 1) {
			yA[N1][N2] = new Range2D(min, max, d);
                        cp[N1][N2].getPlot().setYAutoRange(false);
                        cp[N1][N2].getPlot().setYRange(yA[N1][N2]);
                        this.autorangeY[N1][N2] = false;
		}
		if (axis == 2) {
			levels[N1][N2] = new Range2D(min, max, d);
                        autorangeZ[N1][N2] = false;
		}

	}

	/**
	 * Set autorange in X and Y at the same time for all plots
	 * 
	 */
	public void setAutoRangeAll() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				autorangeX[i1][i2] = true;
                                autorangeY[i1][i2] = true;
                                autorangeZ[i1][i2] = true;

			}

		}
	}

	/**
	 * Set global title to the current chart
	 * 
	 * @param title
	 *            title
	 */

	public void setName(String title) {

		this.titleG[N1][N2] = title;
                 cp[N1][N2].setName1(title);

	}

	/**
	 * Sets the name for X axis.
	 * 
	 * @param s
	 *            Title for X axis.
	 */
	public void setNameX(String s) {

		titleX[N1][N2] = s;

	}

	/**
	 * Sets the name for Y axis.
	 * 
	 * @param s
	 *            Title for Y axis.
	 */
	public void setNameY(String s) {

		titleY[N1][N2] = s;

	}

	/**
	 * Set background for the plot.
	 * 
	 * @param c
	 *            background color.
	 */
	public void setBackground(Color c) {

		getPlotCanvas().setBackground(c);

	}

	/**
	 * Set global title for this plot.
	 * 
	 * @param title
	 *            plot title
	 */
	public void setGTitle(String title) {

		titleG[N1][N2] = title;

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
                if (vs==false) mainFrame.validate();

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
         * Set the canvas frame visible. Also set its location.
         * @param posX -  the x-coordinate of the new location's top-left corner in the parent's coordinate space;
         * @param posY - he y-coordinate of the new location's top-left corner in the parent's coordinate space 
         */
        public void visible(int posX, int posY) {
                mainFrame.setLocation(posX, posY);
                mainFrame.setVisible(true);

        }


	/**
	 * Construct a HChart canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 */
	public HPlot2D(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);

	}

	/**
	 * Construct a HChart canvas with a single plot/graph
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
	public HPlot2D(String title, int xs, int ys, boolean set) {

		this(title, xs, ys, 1, 1, set);

	}

	/**
	 * Construct a HChart canvas with plots/graphs
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
	public HPlot2D(String title, int xs, int ys, int n1, int n2) {

		this(title, xs, ys, n1, n2, true);

	}

	/**
	 * Construct a HChart canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HPlot2D(String title) {

		this(title, 600, 400, 1, 1, true);

	}

	/**
	 * Construct a HChart canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title "Default"
	 * 
	 */
	public HPlot2D() {
		this("Default", 600, 400, 1, 1, true);
	}

	public void showTest() {

		Range2D xr = new Range2D(190.0f, 250.0f, 1.0f);
		Range2D yr = new Range2D(0.0f, 45.0f, 1.0f);
		TestData td = new TestData(TestData.XY_GRID, xr, yr,
				TestData.SINE_RAMP, 12.0f, 30.f, 5.0f);
		SGTData newData = td.getSGTData();

		colormap[N1][N2] = createColorMap(levels[N1][N2]);
		clevels[N1][N2] = ContourLevels.getDefault(levels[N1][N2]);
		gridAtt[N1][N2] = new GridAttribute(clevels[N1][N2]);
		gridAtt[N1][N2].setStyle(GridAttribute.RASTER_CONTOUR);
		gridAtt[N1][N2].setColorMap(colormap[N1][N2]);
		cp[N1][N2].getPlot().addData(newData, gridAtt[N1][N2], "First Data");

	}

	public void setData() {

		/*
		 * Range2D xr = new Range2D(190.0f, 250.0f, 1.0f); Range2D yr = new
		 * Range2D(0.0f, 45.0f, 1.0f); TestData td = new
		 * TestData(TestData.XY_GRID, xr, yr, TestData.SINE_RAMP, 12.0f, 30.f,
		 * 5.0f); SGTData newData = td.getSGTData();
		 */

		SimpleGrid sg;
		SGTMetaData xMeta;
		SGTMetaData yMeta;
		double[] axis1 = new double[10];
		double[] axis2 = new double[10];
		double[] values = new double[10 * 10];

		int count = 0;
		for (int count1 = 0; count1 < 10; count1++) {
			axis1[count1] = count1;
			for (int count2 = 0; count2 < 10; count2++) {
				axis2[count2] = count2;
				values[count] = count1 * count * 2;
				count++;
			}
		}

		SGLabel keyLabel = new SGLabel("Key Label", "", new Point2D.Double(0.0,
				0.0));
		keyLabel.setHeightP(0.16);
		keyLabel.setText(titleG[N1][N2]);
		xMeta = new SGTMetaData(titleX[N1][N2], "");
		yMeta = new SGTMetaData(titleY[N1][N2], "");

		sg = new SimpleGrid(values, axis1, axis2, "Test Series");
		sg.setXMetaData(xMeta);
		sg.setYMetaData(yMeta);
		// sg.setZMetaData(zMeta);
		sg.setKeyTitle(keyLabel);

		colormap[N1][N2] = createColorMap(levels[N1][N2]);
		clevels[N1][N2] = ContourLevels.getDefault(levels[N1][N2]);
		gridAtt[N1][N2] = new GridAttribute(clevels[N1][N2]);
		gridAtt[N1][N2].setColorMap(colormap[N1][N2]);
		cp[N1][N2].getPlot().addData(sg, gridAtt[N1][N2], "First Data");

	}

	/**
	 * Set style to the current plot.
	 * 
	 * @param style
	 *            0 means RASTER style; <br>
	 *            1 means CONTOUR style; <br>
	 *            2 means RASTER_CONTOUR  <br>
         *            3 means AREA_FILL  <br>
         *            4 means AREA_FILL_CONTOUR  <br>
         *            5 means ARROWS    <br>
	 * */
	public void setStyle(int style) {
		this.style[N1][N2] = style;

	}

 
        /**
         * Return axis objects
         * @param axis  axis =0 for X axis, and =1 for Y axis
         * @return axis object  
         **/ 
        public Axis getAxis(int axis) {
                return cp[N1][N2].getPlot().getAxis(axis); 

        }


       /**
         * Set axis objects
         * @param axis  axis =0 for X axis, and =1 for Y axis
         * @param axis object  
         **/ 
        public void setAxis(int axis, Axis axisObject) {
                cp[N1][N2].getPlot().setAxis(axis,axisObject);

        }

        /**
         * Sets whether an axis line will be drawn or not.
         * This does not affect tics and labels. 
         * @param axis
         *            defines to which axis this function applies, generally
         *            something like <a href="#X_AXIS">X_AXIS</a> or <a
         *            href="#Y_AXIS">Y_AXIS</a>.
         * @param b
         *            toggle, true if the axis should be drawn.
         */

        public void setAxis(int axis, boolean b) {
                PlainAxis ax=(PlainAxis)cp[N1][N2].getPlot().getAxis(axis);
                ax.setVisible(b);

        }
       

       
        
        /**
         * Set fonts for axis Label
         * @param fnt font
         */
        public void setAxisLabelFont(Font fnt) {
        	
            cp[N1][N2].getPlot().getAxis(0).setLabelFont(fnt);
            cp[N1][N2].getPlot().getAxis(1).setLabelFont(fnt);
        	
        }



   /**
   * Set the label height in physical units.
   *
   * @param lhgt label height.
   */
        public void setAxisLabelHeight(double lhgt) {

            cp[N1][N2].getPlot().getAxis(0).setLabelHeightP(lhgt);
            cp[N1][N2].getPlot().getAxis(1).setLabelHeightP(lhgt);

        }



        /**
         * Sets the length of the sub-ticks. In fact, the actual sub tick length is the
         * value you set here multiplied by the axis length. By default, the
         * tick-length is exactly 0.007 times the axis length. Using a value
         * proportional to the axes system leads to reasonable proportions even if
         * the graph is blown up to full screen (which users often do, trust me).
         * 
         * @param axis
         *            defines to which axis this function applies, generally
         *            something like <a href="#X_AXIS">X_AXIS</a> or <a
         *            href="#Y_AXIS">Y_AXIS</a>.
         * @param length
         *            subtick length relative to the axis length
         */
        public void setSubTicLength(int axis, double length) {
        	cp[N1][N2].getPlot().getAxis(0).setSmallTicHeightP(length);
        	cp[N1][N2].getPlot().getAxis(1).setSmallTicHeightP(length);
        }

        
        /**
         * Sets the pen width to draw tick axes 
         *
         * @param penWidth 
         *          pen width to draw the tick axes lines 
         */
        public void setAxisPenTicWidth(int penWidth) {
        	cp[N1][N2].getPlot().getAxis(0).setThickTicWidthP(penWidth );
        	cp[N1][N2].getPlot().getAxis(1).setThickTicWidthP(penWidth );     

        }

       

        
        /**
         * Sets the pen color for ticks 
         *
         * @param color
         *          pen color.
         */
        public void setAxisLabelColor(Color color) {
        	cp[N1][N2].getPlot().getAxis(0).setLabelColor(color);
        	cp[N1][N2].getPlot().getAxis(1).setLabelColor(color);     

        }
        
        /**
         * Sets the  number of small sub-ticks
         *
         * @param nstic
         *        number of small ticks  
         */
        public void setAxisNumberSubtics(int nstic) {
        	cp[N1][N2].getPlot().getAxis(0).setNumberSmallTics(nstic);
        	cp[N1][N2].getPlot().getAxis(1).setNumberSmallTics(nstic);     

        }
        
        
        /**
         * Sets the tick heights in NDC units
         *
         * @param lthgt
         *          height of ticks in NDC units
         */
        public void setAxisTicHeight(double lthgt) {
        	cp[N1][N2].getPlot().getAxis(0).setLargeTicHeightP(lthgt);
        	cp[N1][N2].getPlot().getAxis(1).setLargeTicHeightP(lthgt);     

        }
        
        
        /**
         * Position of  label for axis
         * @param axis axis (0 for x, 1 for y)
         * @param labp position
         */
        public void setAxisLabelPosition(int axis, int labp) {
        	cp[N1][N2].getPlot().getAxis(0).setLabelPosition(labp);
        	cp[N1][N2].getPlot().getAxis(1).setLabelPosition(labp);     
        }
        
        
        
        /**
         * Get label title
         * @return axis
         */
        public SGLabel getAxisTitle(int axis) {
        	return cp[N1][N2].getPlot().getAxis(axis).getTitle();
        	    
        }
        
        
        /**
         * Set label for axis
         * @param lab label
         */
        public  void setAxisTitle(int axis, SGLabel lab) {
        	cp[N1][N2].getPlot().getAxis(axis).setTitle(lab);
        	    
        }
        
        
        
        /**
        * Get current style 
        **/
        public int getStyle() {
                return this.style[N1][N2];

        }

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	@Override
	protected void clearFrame() {
		// TODO Auto-generated method stub
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {
		cp[N1][N2].getPlot().clear();
		cp[N1][N2].repaint();
			}; };
			
	}

        /**
        *  Clear all pads
        **/ 
        public  void clearAll() {
                // TODO Auto-generated method stub
                for (int i2 = 0; i2 < N2final; i2++) {
                        for (int i1 = 0; i1 < N1final; i1++) {
                cp[N1][N2].getPlot().clear();
                cp[N1][N2].repaint();
                }; };

        }

       /**
        *  Clear the current pad
        **/
        public  void clear() {
                cp[N1][N2].getPlot().clear();
        }

	@Override
	protected void openReadDataDialog() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");

	}

	@Override
	protected void openReadDialog() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");

	}

	@Override
	protected void openWriteDialog() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");

	}

	@Override
	protected void refreshFrame() {
		// TODO Auto-generated method stub

		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");
	}

	@Override
	protected void quitFrame() {
                 distroy();
	}


/**
         * Remove the canvas frame
         */
        public void distroy() {
                mainFrame.setVisible(false);
                clearAll();
                removeFrame();
        }



       /**
         * Close the canvas (and dispose all components). 
         */
        public void close() {
                mainFrame.setVisible(false);
                m_Close = new Thread1("Closing softly");
                if (!m_Close.Alive())
                        m_Close.Start();

        }
 

  /**
         * Quit the canvas (and dispose all components) Note: a memory leak is found -
         * no time to study  it. set to null all the stuff
         */

        public void quit() {

                doNotShowFrame();
                clear();

                for (int i1 = 0; i1 < N1final; i1++) {
                        for (int i2 = 0; i2 < N2final; i2++) {
                                clear();
                                cp[i1][i2] = null;

                        }
                }

                cp = null;
                removeFrame();

        }


	@Override
	protected void showHelp() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");

	}

	private ColorMap createColorMap(Range2D datar) {
		int[] red = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 7, 23, 39, 55, 71, 87, 103, 119, 135, 151,
				167, 183, 199, 215, 231, 247, 255, 255, 255, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 246, 228,
				211, 193, 175, 158, 140 };
		int[] green = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 27, 43, 59, 75, 91, 107,
				123, 139, 155, 171, 187, 203, 219, 235, 251, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 247, 231, 215, 199, 183, 167, 151, 135, 119, 103, 87, 71,
				55, 39, 23, 7, 0, 0, 0, 0, 0, 0, 0 };
		int[] blue = { 0, 143, 159, 175, 191, 207, 223, 239, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 255, 247, 231, 215, 199, 183, 167, 151, 135, 119, 103, 87,
				71, 55, 39, 23, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0 };

		IndexedColorMap cmap = new IndexedColorMap(red, green, blue);
		cmap.setTransform(new LinearTransform(0.0, (double) red.length,
				datar.start, datar.end));
		return cmap;
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
















}
