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

import hep.aida.ref.histogram.Histogram2D;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;
import jhplot.utils.HelpDialog;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.chart.controllers.mouse.camera.NewtCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.contour.*;
import org.jzy3d.chart.factories.*;
import org.jzy3d.chart.*;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.*;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.*;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.*;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.text.drawable.DrawableTextBitmap;
import org.jzy3d.plot3d.text.drawable.DrawableTextTexture;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.primitives.axes.*;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.*;
import org.jzy3d.plot3d.primitives.enlightables.EnlightableSphere;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.plot3d.rendering.view.*;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.contour.*;
import java.awt.image.BufferedImage;
import com.jogamp.opengl.util.gl2.GLUT;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.renderers.*;
import org.jzy3d.plot3d.rendering.view.*;
import org.jzy3d.chart2d.*;

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
	final private String help_file = "hplotxyz";
	private boolean currentWiredrame = true;
        private Color currentFillColor = Color.GREEN;
	private Color currentWiredColor = Color.BLACK;
        private float currentWiredWidth = 1.0f;
	private boolean solidColor = false;
        private boolean m_setContourMesh3D=false;
        private boolean m_setContourColor3D=false;
        private int xsize,ysize;
        private boolean first=true;
        private int dig=2;
        private boolean showLegentBar=false;
        private int nlevels=10; // number of levels 
        private int fontTick=GLUT.BITMAP_HELVETICA_10;
        private int fontAxisTitles=GLUT.BITMAP_HELVETICA_18;
 
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

                // use minimalistic menu
		super(title, xsize, ysize, n1, n2, set,1);

                this.xsize=xsize;
                this.ysize=ysize;

		// Settings.getInstance().setHardwareAccelerated(true);
		datalist = new ArrayList[N1final][N2final];

		jpp = new Chart[N1final][N2final];
		Nframe = 0;
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {
				datalist[i1][i2] = new ArrayList<AbstractDrawable>();
				AWTChartComponentFactory accf = new AWTChartComponentFactory();
				jpp[i1][i2] = accf.newChart(Quality.Advanced,
						Toolkit.newt.name());
				NewtCameraMouseController ctc = new NewtCameraMouseController(
						jpp[i1][i2]);
				BoundingBox3d b = new BoundingBox3d(0, 1.0f, 0, 1.0f, 0, 1.0f);
				AxeBox ax = new AxeBox(b);
				jpp[i1][i2].getView().setAxe(ax);
				if (N2final * N1final == 2)
					jpp[i1][i2].getView().getCamera().setScale((float) 0.8);
				if (N2final * N1final == 3)
					jpp[i1][i2].getView().getCamera().setScale((float) 0.6);
				if (N2final * N1final > 3)
					jpp[i1][i2].getView().getCamera().setScale((float) 0.5);

				if (set) {
			 		mainPanel.add((CanvasNewtAwt) jpp[i1][i2].getCanvas());
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

	public void setRange(double minX, double maxX, double minY, double maxY,
			double minZ, double maxZ) {

		BoundingBox3d b = new BoundingBox3d((float) minX, (float) maxX,
				(float) minY, (float) maxY, (float) minZ, (float) maxZ);
		jpp[N1][N2].getView().getAxe().setAxe(b);

	}

         /**
         * Set fonts for tick labels in terms of GLUT.BITMAP fonts. 
         * 
         * @param font 
         *           font in terms GLUT.BITMAP 
         */
        public void  setFontAxisTicks(int font){
         //GLUT.BITMAP_HELVETICA_10;
         //  http://www.angelcode.com/products/bmfont/ (future)
         AxeBox axe = (AxeBox)jpp[N1][N2].getView().getAxe();
         TextBitmapRenderer  spp=(TextBitmapRenderer)axe.getTextRenderer();
         spp.setFontAxisTicks(font);
         // org.jzy3d.plot3d.text.renderers.TextBitmapRenderer.font1=font;
        }


         /**
         * Set line width to draw boxes and axis.  
         * 
         * @param pwidth 
         *           with of the pen to draw boxes etc. 
         */
        public void  setAxisPenWidth(int pwidth){
         //GLUT.BITMAP_HELVETICA_10;
         //  http://www.angelcode.com/products/bmfont/ (future)
         AxeBox axe = (AxeBox)jpp[N1][N2].getView().getAxe();
         axe.setLineWidth(pwidth);
         // org.jzy3d.plot3d.text.renderers.TextBitmapRenderer.font1=font;
        }


        /**
         * Get fonts for axis ticks in terms of GLUT.BITMAP fonts. 
         * 
         * @return font 
         *           font in terms GLUT.BITMAP 
         */
        public int getFontAxisTicks(){
          AxeBox axe = (AxeBox)jpp[N1][N2].getView().getAxe();
          TextBitmapRenderer  spp=(TextBitmapRenderer)axe.getTextRenderer(); 
          return spp.getFontAxisTicks();
         //GLUT.BITMAP_HELVETICA_10;
         //  http://www.angelcode.com/products/bmfont/ (future)
         //return org.jzy3d.plot3d.text.renderers.TextBitmapRenderer.font1;
        }


         /**
         * Get fonts for axis labels in terms of GLUT.BITMAP fonts. 
         * 
         * @return font 
         *           font in terms GLUT.BITMAP 
         */
        public int getFontAxisLabels(){
         //GLUT.BITMAP_HELVETICA_10;
          //  http://www.angelcode.com/products/bmfont/ (future)
          // return org.jzy3d.plot3d.text.renderers.TextBitmapRenderer.font2;
          AxeBox axe = (AxeBox)jpp[N1][N2].getView().getAxe();
          TextBitmapRenderer  spp=(TextBitmapRenderer)axe.getTextRenderer(); 
          return spp.getFontAxisLabels();
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
             JOptionPane.showMessageDialog(getFrame(),
                                "Not implemented for this canvas");
	}

	/**
	 * Refresh all the frames
	 * 
	 */

	protected void refreshFrame() {
                JOptionPane.showMessageDialog(getFrame(),
                                "Not implemented for this canvas");
	}


	/**
	 * Get color as needed from AWT
	 * 
	 * @param c
	 * @return
	 */
	private Color getColor(java.awt.Color c) {

		return new Color((int) c.getRed(), (int) c.getGreen(),
				(int) c.getBlue(), (int) c.getAlpha());

	}

	/**
	 * Start animation of the current canvas.
	 * 
	 * @return animation;
	 */
	public CameraThreadController animate() {
		CameraThreadController s = new CameraThreadController(jpp[N1][N2]);
		s.start();
		return s;
	}

	/**
	 * Set fill color for the current object
	 * 
	 * @param color
	 *            to fill object
	 */
	public void setColorFill(java.awt.Color color) {

		this.currentFillColor = getColor(color);
	}

	/**
	 * Set fill color for the current object
	 * 
	 * @param color
	 *            for wireframe
	 */
	public void setColorWireframe(java.awt.Color color) {
		this.currentWiredColor = getColor(color);
	}

	/**
	 * Set wireframe mode for the current object.
	 * 
	 * @param wiredrame
	 *            true than no solid fill is used.
	 */
	public void setWireframe(boolean wiredrame) {
		this.currentWiredrame = wiredrame;
	}

	/**
	 * Set width of the line for wireframe
	 * 
	 * @param width
	 *            width
	 */
	public void setWireframeWidth(double width) {
		this.currentWiredWidth = (float) width;
	}

	/**
	 * If you set true, color will be solid and no colorfull color map is used.
	 * 
	 * @param solid
	 *            apply a solid color to the current object
	 */
	public void setColorSolid(boolean solid) {
		this.solidColor = solid;
	}

	/**
	 * Get current view of the plotted object.
	 * 
	 * @return view
	 */

	public View getView() {
		return jpp[N1][N2].getView();

	}

	/**
	 * Set the scaling for the current image box.
	 * 
	 * @param scale
	 *            scaling factor for the image.
	 */
	public void setScale(double scale) {
		jpp[N1][N2].getView().getCamera().setScale((float) scale);
		// float s=c.getRenderingSphereRadius();
		// jpp[N1][N2].getView().getCamera().setRenderingSphereRadius((float)(jpp[N1][N2].getView().getCamera().getRenderingSphereRadius()*scale));
		jpp[N1][N2].getView().updateBounds();
	}

	/**
	 * Set the scaling for the axis of the current object. Use setScale() to
	 * change image size.
	 * 
	 * @param scale
	 *            scaling factor for the object
	 */
	public void zoom(double scale) {
		(jpp[N1][N2].getView()).zoom((float) scale);
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
	 * @param co
	 *            grid color
	 */
	public void setGridColor(java.awt.Color co) {

		int color = co.getRGB();
		int red = (color & 0x00ff0000) >> 16;
		int green = (color & 0x0000ff00) >> 8;
		int blue = color & 0x000000ff;
		int alpha = (color >> 24) & 0xff;
		org.jzy3d.colors.Color c = new org.jzy3d.colors.Color(red, green, blue,
				alpha);
		jpp[N1][N2].getView().getAxe().getLayout().setGridColor(c);

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
	 * 
	 * @param posX
	 *            - the x-coordinate of the new location's top-left corner in
	 *            the parent's coordinate space;
	 * @param posY
	 *            - he y-coordinate of the new location's top-left corner in the
	 *            parent's coordinate space
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
	 * 
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @param z
	 *            Z
	 */
	public void setViewPoint(double x, double y, double z) {

		jpp[N1][N2].setViewPoint(new Coord3d(x, y, z));

	}

	/**
	 * Set font and color for axis labels.
	 * 
	 * @param font
	 * @param color
	 */
	public void setAxesLabelFont(Font font, Color color) {

		// jpp[N1][N2].getView().getAxe(). // setXAxeLabelDisplayed(shown);

	}


          /**
         * Set (or not) contour mesh style to 3D plots. 
         * 
         * @param contour
         *            set to true for a contour style
         */
        public void setContourMesh3D(boolean contour) {
                m_setContourMesh3D=contour;
        }


        /**
         * Add  (or not) contour color to 3D plot. 
         * 
         * @param contour
         *            set to true for a contour color style
         */
        public void setContourColor3D(boolean contour) {
                m_setContourColor3D=contour;
        }


       /**
         * Set number of levels for contour plots. 
         * 
         * @param nlevels 
         *           number of levels for contour plots. 
         */
       public void setContourLevels(int nlevels) {
                this.nlevels = nlevels;
        }


         /**
         * Get number of levels for contour plots. 
         * 
         * @return  
         *           number of levels for contour plots. 
         */
        public int getContourLevels() {
                return this.nlevels;
        }


        /**
         * Set to contour mesh style to 3D plots. 
         * 
         */
        public void setContourMesh3D() {
               setContourMesh3D(true);
        }

        /**
         * Set to contour color style to 3D plots. 
         * 
         */
        public void setContourColor3D() {
               setContourColor3D(true);
        }



        /**
         * Get to contour mesh style from 3D plots. 
         * 
         */
        public boolean getContourMesh3D() {
               return m_setContourMesh3D;
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
	public AbstractDrawable add(P2D h1, java.awt.Color color) {
		return add(h1, 1.0f, color);
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
	public AbstractDrawable add(P2D h1, double pointSize) {
		return add(h1, pointSize, java.awt.Color.black);
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
	public AbstractDrawable draw(P2D h1, double penSize, java.awt.Color color) {

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
		jpp[N1][N2].getScene().getGraph().add(scatter);
		return scatter;
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
	 * @return return added object for styling
	 */
	public AbstractDrawable add(P2D h1, double penSize, java.awt.Color color) {

		int size = h1.size();
		Coord3d[] points = new Coord3d[size];
		double x, y, z;

		for (int i = 0; i < size; i++) {
			x = h1.getX(i);
			y = h1.getY(i);
			z = h1.getZ(i);
			points[i] = new Coord3d(x, y, z);

		}
		Scatter scatter = new org.jzy3d.plot3d.primitives.Scatter(points);
		scatter = new org.jzy3d.plot3d.primitives.Scatter(points);
		scatter.setColor(getColor(color));
		scatter.setWidth((float) penSize);
		datalist[N1][N2].add(scatter);
		return scatter;

	}

	/**
	 * Add a object to the list
	 * 
	 * @param shape
	 */

	public AbstractDrawable add(AbstractDrawable shape) {

		datalist[N1][N2].add(shape);
		return shape;
	}

	/**
	 * Draw a list of shapes on the current canvas
	 * 
	 * @param shapes
	 */
	public void draw(AbstractDrawable[] shapes) {

		for (int i1 = 0; i1 < shapes.length; i1++) {
			jpp[N1][N2].getScene().getGraph().add(shapes[i1]);

		}
	}

	/**
	 * Update current pad. All objects added with the method "add" will be
	 * shown.
	 */
	public void update() {
                update(N1,N2);
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



                
                if (showLegentBar){
                Shape s= (Shape)datalist[n1][n2].get(0);
                AWTColorbarLegend cbar = new AWTColorbarLegend(s, jpp[n1][n2]
                                .getView().getAxe().getLayout());
                s.setLegend(cbar);
                }

                jpp[n1][n2].getScene().getGraph().add((List<AbstractDrawable>) datalist[n1][n2]);

                // System.out.println(Integer.toString(n1)+" "+Integer.toString(n2));
                 if (set && (m_setContourMesh3D==true || m_setContourColor3D==true)) {
                                    getFrame().setContentPane( (CanvasNewtAwt) jpp[n1][n2].getCanvas() );
                                     SwingUtilities.updateComponentTreeUI(getFrame());

/* 

                    JFrame ff = new JFrame();
                    ff.getContentPane().add( (CanvasNewtAwt)jpp[n1][n2].getCanvas() );
                    ff.setSize(500,500);
                    ff.setVisible(true);
                    //System.out.println("show frame"); 
*/




            }


	}



	/**
	 * Refresh all the plots on the same canvas. Inactive. 
	 * 
	 */
	public void updateAll() {

/*
		if (N1final == 0 && N2final == 0)
			return;

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
                                 updatePlot(i1,i2);
			}
		}
*/
	}

	/**
	 * Add a text to the canvas.
	 * 
	 * @param text
	 *            text
	 * @param pos
	 *            position (x,y,z)
	 * @param color
	 *            color
	 * @return return what is added
	 */

	public AbstractDrawable addText(String text, double[] pos, java.awt.Color co) {

		int color = co.getRGB();
		int red = (color & 0x00ff0000) >> 16;
		int green = (color & 0x0000ff00) >> 8;
		int blue = color & 0x000000ff;
		int alpha = (color >> 24) & 0xff;
		org.jzy3d.colors.Color c = new org.jzy3d.colors.Color(red, green, blue,
				alpha);

		DrawableTextBitmap t4 = new DrawableTextBitmap(text, new Coord3d(
				pos[0], pos[1], pos[2]), c);
		datalist[N1][N2].add(t4);

		return t4;

	}

	/**
	 * Add a F2D function (a function with 2 arguments, x and y). The function
	 * will be shows as a surface. This function is plotted with 50 steps in X
	 * and Y. Min and Max values for axes taken from the definition.
	 * 
	 * @param f 
	 *            function to show
	 * @return added object for styling
	 */
	public AbstractDrawable add(final F2D f) {
		return add(f, 40, 40, f.getMinX(), f.getMaxX(), f.getMinY(),
				f.getMaxY());
	}

         /**
         * Add a F2D function as a 2D contour plot. The function
         * will be shows as a surface. This function is plotted with 50 steps in X
         * and Y. Min and Max values for axes taken from the definition.
         * 
         * @param f 
         *            function to show
         * @return added object for styling
         */
/*
        public AbstractDrawable addAsContour(final F2D f) {
                return addAsContour(f, 40, 40, f.getMinX(), f.getMaxX(), f.getMinY(),
                                f.getMaxY());
        }
*/

	/**
	 * Add a F2D function (a function with 2 arguments, x and y). The function
	 * will be shows as a surface. This function is plotted with 40 steps in X
	 * and Y.
	 * 
	 * @param f 
	 *            function to show
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @return object for styling
	 */
	public AbstractDrawable add(final F2D f, double xmin, double xmax,
			double ymin, double ymax) {

		return add(f, 40, 40, xmin, xmax, ymin, ymax);

	}

	/**
	 * Add a F2D function (a function with 2 arguments, x and y). The function
	 * will be shows as a surface. Use "update()" method to draw it.
	 * 
	 * @param h1
	 * @param xsteps
	 *            number of divisions in X
	 * @param ysteps
	 *            number of divisions in Y
	 * @param xmin
	 *            min X
	 * @param xmax
	 *            max X
	 * @param ymin
	 *            min Y
	 * @param ymax
	 *            max Y
	 * @return
	 */
	public AbstractDrawable add(final F2D h1, int xsteps, int ysteps,
			double xmin, double xmax, double ymin, double ymax) {

		if (h1.getLabelX() != null)
			if (h1.getLabelX().length() > 1)
				setNameX(h1.getLabelX());
		if (h1.getLabelY() != null)
			if (h1.getLabelY().length() > 1)
				setNameY(h1.getLabelY());

		Mapper mapper = new Mapper() {
			public double f(double x, double y) {
				return h1.eval(x, y);
			}
		};

		Range xrange = new Range((float) xmin, (float) xmax);
		Range yrange = new Range((float) ymin, (float) ymax);
		Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(
				xrange, xsteps, yrange, ysteps), mapper);


		surface.setColor(currentFillColor);
		surface.setWireframeDisplayed(currentWiredrame);
		surface.setWireframeColor(currentWiredColor);
		surface.setWireframeWidth(currentWiredWidth);

                

                ColorMapper myColorMapper=null;
		if (solidColor == false) {
			myColorMapper = new ColorMapper(new ColorMapRainbow(),
					surface.getBounds().getZmin(), surface.getBounds()
							.getZmax(), new Color(1, 1, 1, .5f));
			surface.setColorMapper(myColorMapper);
		};


                if (m_setContourMesh3D==true){
                    MapperContourMeshGenerator contour = new MapperContourMeshGenerator(mapper, xrange, yrange);
                    Chart chart = new ContourChart(Quality.Advanced, Toolkit.newt.name()); 
                    ContourAxeBox cab = (ContourAxeBox)chart.getView().getAxe();
                    ContourMesh mesh = contour.getContourMesh(new DefaultContourColoringPolicy(myColorMapper), xsize, ysize, nlevels, 0, false);
                    cab.setContourMesh(mesh);
                    NewtCameraMouseController ctc = new NewtCameraMouseController(
                                                chart);
                    chart.getScene().getGraph().add(surface);
                    jpp[N1][N2]=chart; 
                    // debug
                    // mainPanel.add((CanvasNewtAwt)chart.getCanvas());
                    //JFrame ff = new JFrame();
                    //JPanel pp = new JPanel();
                    //pp.setSize(new Dimension(600, 400));
                    //ff.getContentPane().add( (CanvasNewtAwt)jpp[N1][N2].getCanvas() );
                    //mainPanel.add((CanvasNewtAwt)chart.getCanvas());
                    //ff.setSize(500,500);
                    //ff.setVisible(true);
                    //System.out.println("show frame"); 
                    // jpp[N1][N2]=chart;
                } 

	



                 if (m_setContourColor3D==true){
                    MapperContourPictureGenerator contour = new MapperContourPictureGenerator(mapper, xrange, yrange);
                    Chart chart = new ContourChart(Quality.Advanced, Toolkit.newt.name());
                    ContourAxeBox cab = (ContourAxeBox)chart.getView().getAxe();
                    BufferedImage contourImage = contour.getFilledContourImage((IContourColoringPolicy)new DefaultContourColoringPolicy(myColorMapper), xsize, ysize, nlevels);
                    cab.setContourImg(contourImage, xrange, yrange);
                    NewtCameraMouseController ctc = new NewtCameraMouseController(chart); 
                    chart.getScene().getGraph().add(surface);
                    jpp[N1][N2]=chart;
                }


	
		datalist[N1][N2].add(surface);
		return surface;
	}





	/**
	 * Add a F2D function as a 2D contour plot. (a function with 2 arguments, x and y).
         * Use "update()" method to draw it.
	 * 
	 * @param h1
	 * @param xsteps
	 *            number of divisions in X
	 * @param ysteps
	 *            number of divisions in Y
	 * @param xmin
	 *            min X
	 * @param xmax
	 *            max X
	 * @param ymin
	 *            min Y
	 * @param ymax
	 *            max Y
	 * @return
	 */

/*
	public AbstractDrawable addAsContour(final F2D h1, int xsteps, int ysteps,
			double xmin, double xmax, double ymin, double ymax) {

		if (h1.getLabelX() != null)
			if (h1.getLabelX().length() > 1)
				setNameX(h1.getLabelX());
		if (h1.getLabelY() != null)
			if (h1.getLabelY().length() > 1)
				setNameY(h1.getLabelY());

		Mapper mapper = new Mapper() {
			public double f(double x, double y) {
				return h1.eval(x, y);
			}
		};

		Range xrange = new Range((float) xmin, (float) xmax);
		Range yrange = new Range((float) ymin, (float) ymax);


		Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(
				xrange, xsteps, yrange, ysteps), mapper);


		surface.setColor(currentFillColor);
		surface.setWireframeDisplayed(currentWiredrame);
		surface.setWireframeColor(currentWiredColor);
		surface.setWireframeWidth(currentWiredWidth);
                

//              ColorMapper myColorMapper= new ColorMapper( new ColorMapRainbow(), -600.0f, 600.0f );
                ColorMapper myColorMapper=null;
		if (solidColor == false) {
			myColorMapper = new ColorMapper(new ColorMapRainbow(),
					surface.getBounds().getZmin(), surface.getBounds()
							.getZmax(), new Color(1, 1, 1, .5f));
			surface.setColorMapper(myColorMapper);
		};


                if (m_setContourMesh3D){
                    MapperContourMeshGenerator contour = new MapperContourMeshGenerator(mapper, xrange, yrange);
                    Chart chart = new ContourChart(Quality.Advanced, Toolkit.newt.name()); 
                    ContourAxeBox cab = (ContourAxeBox)chart.getView().getAxe();
                    ContourMesh mesh = contour.getContourMesh(new DefaultContourColoringPolicy(myColorMapper), xsize, ysize, nlevels, 0, false);
                    cab.setContourMesh(mesh);
                    NewtCameraMouseController ctc = new NewtCameraMouseController(chart);
                   // chart.getScene().getGraph().add(surface);
                    jpp[N1][N2]=chart; 
                   
                } 

	



                 if (m_setContourColor3D){
                    MapperContourPictureGenerator contour = new MapperContourPictureGenerator(mapper, xrange, yrange);
                    Chart chart = new ContourChart(Quality.Advanced, Toolkit.newt.name());
                    // Chart2d chart = new ContourChart(Quality.Advanced, Toolkit.newt.name());
                    ContourAxeBox cab = (ContourAxeBox)chart.getView().getAxe();
                    BufferedImage contourImage = contour.getFilledContourImage((IContourColoringPolicy)new DefaultContourColoringPolicy(myColorMapper), xsize, ysize, nlevels);
                    cab.setContourImg(contourImage, xrange, yrange);
                    NewtCameraMouseController ctc = new NewtCameraMouseController(chart); 
                    // chart.getView().setAxe(cab);
                    BufferedImage bb =  cab.getContourImage() ;
                    //chart.getScene().getGraph().add(surface);
                   // jpp[N1][N2]=chart;
                    System.out.println("show frame"); 
                    JFrame ff = new JFrame();
                    //ff.getContentPane().setLayout(new FlowLayout());
                    ff.getContentPane().add(new JLabel(new ImageIcon(bb)));
                    // ff.getContentPane().add(  (CanvasNewtAwt)chart.getCanvas()   );
                    ff.setSize(500,500);
                    ff.setVisible(true);

                    // debug
                }
	
// debug
//		datalist[N1][N2].add(surface);
		return null;
	}

*/



/*

	public void addContour(Mapper mapper, ColorMapper myColorMapper,
			Range xrange, Range yrange) {
		// Compute an image of the contour
		MapperContourPictureGenerator contour = new MapperContourPictureGenerator(
				mapper, xrange, yrange);
		// Create a chart with contour axe box, and attach the contour picture
		ContourAxeBox cab = (ContourAxeBox) jpp[N1][N2].getView().getAxe();
		cab.setContourImg(contour.getFilledContourImage(
				new DefaultContourColoringPolicy(myColorMapper), 400, 400, 10),
				xrange, yrange);

	}
*/

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
		Range xrange = new Range((float) h1.xAxis().lowerEdge(), (float) h1
				.xAxis().upperEdge());
		Range yrange = new Range((float) h1.yAxis().lowerEdge(), (float) h1
				.yAxis().upperEdge());
		Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(
				xrange, xsteps, yrange, ysteps), mapper);

		surface.setColor(currentFillColor);
		surface.setWireframeDisplayed(currentWiredrame);
		surface.setWireframeColor(currentWiredColor);
		surface.setWireframeWidth(currentWiredWidth);


                 ColorMapper myColorMapper=null;
                if (solidColor == false) {
                        myColorMapper = new ColorMapper(new ColorMapRainbow(),
                                        surface.getBounds().getZmin(), surface.getBounds()
                                                        .getZmax(), new Color(1, 1, 1, 0.7f));
                    surface.setColorMapper(myColorMapper);
                };




                  if (m_setContourMesh3D==true){
                    MapperContourMeshGenerator contour = new MapperContourMeshGenerator(mapper, xrange, yrange);
                    Chart chart = new ContourChart(Quality.Advanced, Toolkit.newt.name());
                    ContourAxeBox cab = (ContourAxeBox)chart.getView().getAxe();
                    ContourMesh mesh = contour.getContourMesh(new DefaultContourColoringPolicy(myColorMapper), xsize, ysize, nlevels, 0, false);
                    cab.setContourMesh(mesh);
                    NewtCameraMouseController ctc = new NewtCameraMouseController(
                                                chart);
                    chart.getScene().getGraph().add(surface);
                    jpp[N1][N2]=chart;
                }



                 if (m_setContourColor3D==true){
                    MapperContourPictureGenerator contour = new MapperContourPictureGenerator(mapper, xrange, yrange);
                    Chart chart = new ContourChart(Quality.Advanced, Toolkit.newt.name());
                    ContourAxeBox cab = (ContourAxeBox)chart.getView().getAxe();
                    BufferedImage contourImage = contour.getFilledContourImage((IContourColoringPolicy)new DefaultContourColoringPolicy(myColorMapper), xsize, ysize, nlevels);
                    cab.setContourImg(contourImage, xrange, yrange);
                    NewtCameraMouseController ctc = new NewtCameraMouseController(chart);
                    chart.getScene().getGraph().add(surface);
                    jpp[N1][N2]=chart;
                }






		datalist[N1][N2].add(surface);

		return surface;

	}

	/**
	 * Show H2D histogram as bars
	 * 
	 * @param h2d
	 *            input histogram
	 * @param return what was added
	 */
	public void add(final H2D h2d) {
		addAsBars(h2d);
	}

	/**
	 * Fast export of the canvas to an image file (depends on the extension,
	 * i.e. PNG, JPG). No questions will be asked, an existing file will be
	 * rewritten. Only one image allowed at the time.
	 * 
	 * @param file
	 *            Output file with the proper extension. If no extension, PNG
	 *            file is assumed.
	 */

	public void export(String file) {

          
                  if ((file.toLowerCase()).endsWith( ".png" )==false) { 
                  JOptionPane.showMessageDialog(getFrame(),
                                "Non PNG images are not supported by HPlotXYZ");
                  return; 
                  }


 
		File image = new File(file);
		try {
			jpp[N1][N2].screenshot(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Add a H2D histogram as bars. The histogram will be shows as a surface.
	 * Use "update()" method to draw it.
	 * 
	 * @param h2d
	 *            Histogram for drawing.
	 * @return return what was added
	 */
	public void addAsBars(final H2D h2d) {

		Histogram2D h1 = h2d.get();

		int ibinsX = h1.xAxis().bins() + 2;
		int ibinsY = h1.yAxis().bins() + 2;

		int zmax = (int) h1.maxBinHeight();

		for (int i = 0; i < ibinsX - 1; i++) {
			double x = h1.xAxis().binLowerEdge(i);
			double wx = h1.xAxis().binWidth(i);

			for (int j = 0; j < ibinsY - 1; j++) {
				double y = h1.yAxis().binLowerEdge(j);
				double wy = h1.yAxis().binWidth(j);

				double h = h1.binHeight(i, j);

				// parallepiped does not works
				/*
				 * Parallelepiped
				 * pp=getParallelepiped((float)x,(float)(x+wx),(float
				 * )y,(float)(y+wy),0.0f,(float)h);
				 * pp.setColor(currentFillColor);
				 * pp.setWireframeDisplayed(currentWiredrame);
				 * pp.setWireframeColor(currentWiredColor);
				 * pp.setWireframeWidth(currentWiredWidth); if (solidColor==
				 * false){ pp.setColorMapper(new ColorMapper(new
				 * ColorMapRainbow(), 0, zmax, new Color(1, 1, 1, 0.8f)));
				 * 
				 * }
				 */

				// apply color map
				Polygon[] pp = getSquareBar(x, wx, y, wy, h);

				for (int k = 0; k < pp.length; k++) {

					pp[k].setColor(currentFillColor);
					pp[k].setWireframeDisplayed(currentWiredrame);
					pp[k].setWireframeColor(currentWiredColor);
					pp[k].setWireframeWidth(currentWiredWidth);

					if (solidColor == false) {
						pp[k].setColorMapper(new ColorMapper(
								new ColorMapRainbow(), 0, zmax, new Color(1, 1,
										1, 0.8f)));

					}
					datalist[N1][N2].add(pp[k]);
				}

				// datalist[N1][N2].add(pp);

			}
		}

	}

	/**
	 * Set a legend to a given surface.
	 * 
	 * @param surface
	 *            input object
	 */
	public void setLegendBar() {
                showLegentBar=true;
	}

	/**
	 * Draw a H2D histogram. Better use "add" to overlay histograms. Then use
	 * "update()" method to draw it.
	 * 
	 * @param h2d
	 *            Histogram for drawing.
	 * 
	 */
	public AbstractDrawable draw(final H2D h2d) {

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
		Range xrange = new Range((float) h1.xAxis().lowerEdge(), (float) h1
				.xAxis().upperEdge());
		Range yrange = new Range((float) h1.yAxis().lowerEdge(), (float) h1
				.yAxis().upperEdge());
		Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(
				xrange, xsteps, yrange, ysteps), mapper);

		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true); // draws surface polygons content
		surface.setWireframeDisplayed(true); // draw surface polygons border
		surface.setWireframeColor(Color.BLACK); // set polygon border in black
		jpp[N1][N2].getScene().add(surface);
		return surface;
	}

	/**
	 * Get the current canvas chart.
	 * 
	 * @return current chart.
	 */
	public Chart getChart() {
		return jpp[N1][N2];
	}

	/**
	 * Set background for the current pad.
	 * 
	 * @param co
	 *            color to be set
	 */
	public void setBackground(java.awt.Color co) {
		int color = co.getRGB();
		int red = (color & 0x00ff0000) >> 16;
		int green = (color & 0x0000ff00) >> 8;
		int blue = color & 0x000000ff;
		int alpha = (color >> 24) & 0xff;
		org.jzy3d.colors.Color c = new org.jzy3d.colors.Color(red, green, blue,
				alpha);
		jpp[N1][N2].getView().setBackgroundColor(c);
	}

	/**
	 * Set scientific notation with a given number of digits.
	 * 
	 * @param axis
	 *            axis. 0=X, 1=Y, 2=Z
	 * @param dig
	 *            number of digits
	 */
	public void setTickScientific(int axis, int dig) {
                this.dig=dig;
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
	 * Set decimal precision for a given axis.
	 * 
	 * @param axis
	 *            axis. 0=X, 1=Y, 2=Z
	 * @param dig
	 *            precision.
	 */
	public void setTickDecimal(int axis, int dig) {
                this.dig=dig;
		if (axis == 0)
			jpp[N1][N2].getAxeLayout().setXTickRenderer(
					new DefaultDecimalTickRenderer(dig));
		if (axis == 1)
			jpp[N1][N2].getAxeLayout().setYTickRenderer(
					new DefaultDecimalTickRenderer(dig));
		if (axis == 2)
			jpp[N1][N2].getAxeLayout().setZTickRenderer(
					new DefaultDecimalTickRenderer(dig));

	}

	/**
	 * Set scientific notation with a given number of digits for all ticks.
	 * 
	 * @param dig
	 *            number of digits
	 */
	public void setTickScientificAll(int dig) {
		setTickScientific(0, dig);
		setTickScientific(1, dig);
		setTickScientific(2, dig);
	}

	/**
	 * Set decimal notation with a given number of digits for all ticks.
	 * 
	 * @param dig
	 *            number of digits
	 */
	public void setTickDecimalAll(int dig) {
		setTickDecimal(0, dig);
		setTickDecimal(1, dig);
		setTickDecimal(2, dig);
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


         /**
         * Exports the image to some graphic format.
         */
        protected void exportImage() {

                if (isBorderShown())
                        showBorders(false);
                // System.out.println("OK");
                JHPlot.showStatusBarText("Export to an image file");

                JFrame jm = getFrame();
                JFileChooser fileChooser = jhplot.gui.CommonGUI
                                .openRasterImageFileChooser(jm);

                if (fileChooser.showDialog(jm, "Save As") == 0) {

                        final File scriptFile = fileChooser.getSelectedFile();
                        if (scriptFile == null)
                                return;
                        else if (scriptFile.exists()) {
                                int res = JOptionPane.showConfirmDialog(jm,
                                                "The file exists. Do you want to overwrite the file?",
                                                "", JOptionPane.YES_NO_OPTION);
                                if (res == JOptionPane.NO_OPTION)
                                        return;
                        }
                        String mess = "write image  file ..";
                        JHPlot.showStatusBarText(mess);
                        Thread t = new Thread(mess) {
                                public void run() {
                                        export(scriptFile.getAbsolutePath());
                                };
                        };
                        t.start();
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
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HPlotXYZ");
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
	private Polygon[] getSquareBar(double x, double wx, double y, double wy,
			double height) {

		float xmin = (float) x;
		float xmax = (float) (x + wx);
		float ymin = (float) y;
		float ymax = (float) (y + wy);
		float zmin = 0.0f;
		float zmax = (float) height;
		BoundingBox3d bbox = new BoundingBox3d(xmin, xmax, ymin, ymax, zmin,
				zmax);
		// Parallelepiped bar = new Parallelepiped(b);

		Polygon[] quads = new Polygon[6];

		quads[0] = new Polygon();
		quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox
				.getZmax())));
		quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox
				.getZmin())));
		quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox
				.getZmin())));
		quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox
				.getZmax())));

		quads[1] = new Polygon();
		quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox
				.getZmax())));
		quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox
				.getZmin())));
		quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox
				.getZmin())));
		quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox
				.getZmax())));

		quads[2] = new Polygon();
		quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox
				.getZmax())));
		quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox
				.getZmin())));
		quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox
				.getZmin())));
		quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox
				.getZmax())));

		quads[3] = new Polygon();
		quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox
				.getZmax())));
		quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox
				.getZmin())));
		quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox
				.getZmin())));
		quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox
				.getZmax())));

		quads[4] = new Polygon();
		quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox
				.getZmax())));
		quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox
				.getZmax())));
		quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox
				.getZmax())));
		quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox
				.getZmax())));

		quads[5] = new Polygon();
		quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox
				.getZmin())));
		quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox
				.getZmin())));
		quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox
				.getZmin())));
		quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox
				.getZmin())));

		// bar.setColor(currentFillColor);
		// bar.setWireframeDisplayed(currentWiredrame);
		// bar.setWireframeColor(currentWiredColor);
		// bar.setWireframeWidth(currentWiredWidth);

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
	 *            radius
	 */
	public void addBar(double x, double y, double z, double height,
			double radius) {
		HistogramBar bar = new HistogramBar();
		bar.setData(new Coord3d(x, y, z), (float) height, (float) radius,
				currentFillColor);
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
	public void addSphere(double x, double y, double z, double radius,
			double height, int slices, int stacks) {
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
	private Parallelepiped getParallelepiped(float xmin, float xmax,
			float ymin, float ymax, float zmin, float zmax) {

		BoundingBox3d b = new BoundingBox3d(xmin, xmax, ymin, ymax, zmin, zmax);
		Parallelepiped bar = new Parallelepiped(b);
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
	public void addParallelepiped(float xmin, float xmax, float ymin,
			float ymax, float zmin, float zmax) {

		BoundingBox3d b = new BoundingBox3d(xmin, xmax, ymin, ymax, zmin, zmax);

		Parallelepiped bar = new Parallelepiped(b);
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
