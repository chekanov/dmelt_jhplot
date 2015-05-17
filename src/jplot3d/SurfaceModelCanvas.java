// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package jplot3d;


import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.SwingPropertyChangeSupport;

import jhplot.F2D;
import jhplot.H2D;
import jhplot.JHPlot;
import jhplot.P2D;
import jhplot.P3D;
import jplot3d.JPoint;
import hep.aida.ref.histogram.Histogram2D;
/**

 */
public class SurfaceModelCanvas implements SurfaceModel {

	// ##########################################################################
	// PROPERTY AVAILABLE FOR THIS MODEL BEGIN
	// ##########################################################################
	public  final String X_MIN_PROPERTY = "XMin";
	public final String Y_MIN_PROPERTY = "YMin";
	public final String Z_MIN_PROPERTY = "ZMin";
	public  final String X_MAX_PROPERTY = "XMax";
	public  final String Y_MAX_PROPERTY = "YMax";
	public  final String Z_MAX_PROPERTY = "ZMax";
	public  final String EXPECT_DELAY_PROPERTY = "ExpectDelay";
	public  final String BOXED_PROPERTY = "Boxed";
	public  final String MESH_PROPERTY = "Mesh";
	public  final String SCALE_BOX_PROPERTY = "ScaleBox";
	public  final String DISPLAY_Z_PROPERTY = "DisplayZ";
	public  final String DISPLAY_GRIDS_PROPERTY = "DisplayGrids";
	public  final String PLOT_FUNCTION_1_PROPERTY = "PlotFunction1";
	public  final String PLOT_FUNCTION_2_PROPERTY = "PlotFunction2";
	public  final String DATA_AVAILABLE_PROPERTY = "DataAvailable";
	public  final String DISPLAY_X_Y_PROPERTY = "DisplayXY";
	public  final String CALC_DIVISIONS_PROPERTY = "CalcDivisions";
	public  final String CONTOUR_LINES_PROPERTY = "ContourLines";
	public  final String DISP_DIVISIONS_PROPERTY = "DispDivisions";
	public  final String SURFACE_VERTEX_PROPERTY = "SurfaceVertex";
	public  final String AUTO_SCALE_Z_PROPERTY = "AutoScaleZ";
	public  final String PLOT_TYPE_PROPERTY = "PlotType";
	public  final String PLOT_COLOR_PROPERTY = "PlotColor";
	public  final String COLOR_MODEL_PROPERTY = "ColorModel";
	
	
	private float p_distance=70.0f;
	private float p_scaling=15.0f;
	private float p_rotation=125.0f;
	private float p_elevation=10.0f;
    private boolean m_isP2D=false;
    private boolean m_isP3D=false;
    private boolean isempty=false;
    private  JPoint[] jpoint;
    private PlotterImpl plotter;
    private  boolean ish2f2=false;
    private boolean  m_AutoXY;
    private float  m_LabelOffsetX;
    private float  m_LabelOffsetY;
    private float  m_LabelOffsetZ;
    private Color  m_AxesFontColor;
    private Color  m_FontColorLabel;
    private float    m_PenWidth;
    private String  m_Xlabel;
    private String  m_Ylabel;
    private String  m_Zlabel;
    private Font    m_LabelFont;
    private Font    m_AxisFont;
    private Color   m_LabelColor;
    private Font    m_TicFont;
    private float   m_TicOffset;
    private float   m_FrameScale=1.0f;
    private float init_scaling=10f;

	

	// ##########################################################################
	// PROPERTY AVAILABLE FOR THIS MODEL END
	// ##########################################################################

	// simple interface to make this abstractSurface model reusable.
	
    
    public interface Plotter {

		public int getWidth();

		public int getHeight();

        public void setEmpty();
	
		public  float[][] getPointsX();
	    public  float[][] getPointsY();
	    public  float[][] getPointsZ();
	    public  float   getFactorX();
	    public  float   getFactorY();
	    public  float   getFactorZ();
	    public  float   getStepX();
	    public  float   getStepY();

		public void setP2D( ArrayList<P2D> p2d);
		public void setH2D( ArrayList<H2D> h2d);
		public void setF2D( ArrayList<F2D> f2d);
		public void setP3D( ArrayList<P3D> p3d);
		public void setH2DF2D(ArrayList<H2D> hu, ArrayList<F2D> fu);
	}

	// ##########################################################################
	// PROPERTY CHANGE EVENT HANDLER BEGIN
	// ##########################################################################
	protected java.beans.PropertyChangeSupport property;

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		property.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
		property.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		property.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
		property.removePropertyChangeListener(propertyName, listener);
	}

	// ##########################################################################
	// PROPERTY CHANGE EVENT HANDLER END
	// ##########################################################################

	// ##########################################################################
	// CONFIGURATION CONSTANTS BEGIN
	// ##########################################################################
	private  final int INIT_CALC_DIV = 20;
	private  final int INIT_DISP_DIV = 20;

	// ##########################################################################
	// CONFIGURATION CONSTANTS END
	// ##########################################################################

	protected boolean autoScaleZ = true;

	public boolean isAutoScaleZ() {
		return autoScaleZ;
	}

	public void setAutoScaleZ(boolean v) {
		boolean o = this.autoScaleZ;
		this.autoScaleZ = v;
		autoScale();

		property.firePropertyChange(AUTO_SCALE_Z_PROPERTY, o, v);
	}

	public void toggleAutoScaleZ() {
		setAutoScaleZ(!isAutoScaleZ());
	}

	public void autoScale() {
		//compute  auto scale and repaint
		if (! autoScaleZ ) return;
		if (plotFunction1 && plotFunction2) {
			setZMin(Math.min(z1Min, z2Min));
			setZMax(Math.max(z1Max, z2Max));
		} else {
			if (plotFunction1) {
				setZMin(z1Min);
				setZMax(z1Max);
			}
			if (plotFunction2) {
				setZMin(z2Min);
				setZMax(z2Max);
			}
		}
	}

	protected PlotType plotType = PlotType.SURFACE;

	public PlotType getPlotType() {
		return plotType;
	}

	public void setPlotType(PlotType v) {
		PlotType o = this.plotType;
		this.plotType = v;
		if (colorModel != null)
			colorModel.setPlotType(plotType); //this should be handled by the model itself, without any
		property.firePropertyChange(PLOT_TYPE_PROPERTY, o, v);
		fireAllType(o, v);
	}

	protected PlotColor plotColor;

	public PlotColor getPlotColor() {
		return plotColor;
	}

	public void setPlotColor(PlotColor v) {
		PlotColor o = this.plotColor;
		this.plotColor = v;
		if (colorModel != null)
			colorModel.setPlotColor(plotColor); //this should be handled by the model itself, without any
		property.firePropertyChange(PLOT_COLOR_PROPERTY, o, v);
		fireAllMode(o, v);
	}
	
	private void fireAllMode(PlotColor oldValue, PlotColor newValue) {
		for(PlotColor c: PlotColor.values())
			property.firePropertyChange(c.getPropertyName(), oldValue == c, newValue==c);
	}
	
	private void fireAllType(PlotType oldValue, PlotType newValue) {
		for(PlotType c: PlotType.values())
			property.firePropertyChange(c.getPropertyName(), oldValue == c, newValue==c);
	}
	
	private void fireAllFunction(boolean oldHas1, boolean oldHas2) {
		property.firePropertyChange("FirstFunctionOnly", (!oldHas2) && oldHas1, (!plotFunction2) && plotFunction1);
		property.firePropertyChange("SecondFunctionOnly", (!oldHas1) && oldHas2, (!plotFunction1) && plotFunction2);
		property.firePropertyChange("BothFunction", oldHas1 && oldHas2, plotFunction1 && plotFunction2);
		autoScale() ;
		
	}

	
        public boolean isEmpty() { return isempty;}
	
	public boolean isHiddenMode() {return plotColor== PlotColor.OPAQUE;	}
	public void setHiddenMode(boolean val) { setPlotColor(val?PlotColor.OPAQUE: PlotColor.SPECTRUM);}

	public boolean isSpectrumMode() {return plotColor== PlotColor.SPECTRUM;	}
	public void setSpectrumMode(boolean val) { setPlotColor(val?PlotColor.SPECTRUM: PlotColor.GRAYSCALE);}

	public boolean isGrayScaleMode() {return plotColor== PlotColor.GRAYSCALE;	}
	public void setGrayScaleMode(boolean val) { setPlotColor(val?PlotColor.GRAYSCALE: PlotColor.SPECTRUM);}

	public boolean isDualShadeMode() {return plotColor== PlotColor.DUALSHADE;	}
	public void setDualShadeMode(boolean val) { setPlotColor(val?PlotColor.DUALSHADE: PlotColor.SPECTRUM);}

	public boolean isFogMode() {return plotColor== PlotColor.FOG;	}
	public void setFogMode(boolean val) { setPlotColor(val?PlotColor.FOG: PlotColor.SPECTRUM);}

	public boolean isWireframeType() {return plotType == PlotType.WIREFRAME;	}
	public void setWireframeType(boolean val) { if (val) setPlotType(PlotType.WIREFRAME);	else setPlotType(PlotType.SURFACE);}

	public boolean isSurfaceType() {return plotType== PlotType.SURFACE;	}
	public void setSurfaceType(boolean val) { setPlotType(val?PlotType.SURFACE: PlotType.WIREFRAME);}
	
	public boolean isContourType() {return plotType== PlotType.CONTOUR;	}
	public void setContourType(boolean val) { setPlotType(val?PlotType.CONTOUR: PlotType.SURFACE);}

	public boolean isDensityType() {return plotType== PlotType.DENSITY;	}
	public void setDensityType(boolean val) { setPlotType(val?PlotType.DENSITY: PlotType.SURFACE);}
	
	public boolean isBarsType() {return plotType== PlotType.BARS;	}
	public void setBarsType(boolean val) { setPlotType(val?PlotType.BARS: PlotType.SURFACE);}
		
	public boolean isFirstFunctionOnly() { return plotFunction1&& ! plotFunction2;	}
	public void setFirstFunctionOnly(boolean val) { setPlotFunction12(val,!val);}
	
	public boolean isSecondFunctionOnly() { return (!plotFunction1) && plotFunction2;	}
	public void setSecondFunctionOnly(boolean val) { setPlotFunction12(!val, val); }

	public boolean isBothFunction() { return plotFunction1&& plotFunction2;	}
	public void setBothFunction(boolean val) { setPlotFunction12(val,val); }
	
	
	
	
	
	
	
	
	protected SurfaceVertex[][] vertex;

	public SurfaceVertex[][] getSurfaceVertex() {
		return vertex;
	}

	public void setSurfaceVertex(SurfaceVertex[][] v) {
		SurfaceVertex[][] o = this.vertex;
		this.vertex = v;
		property.firePropertyChange(SURFACE_VERTEX_PROPERTY, o, v);
	}

	Projector projector;

	public Projector getProjector() {
		if (projector == null) {
			projector = new Projector();
			projector.setDistance(p_distance);
			 //float initScaling=p_scaling;
			// if (NPads==2) initScaling=8;
             //if (NPads>2 && NPads<5) initScaling=7;
             //if (NPads>4) initScaling=6;		 
			 
			projector.set2DScaling(p_scaling);
			projector.setRotationAngle(p_rotation);
			projector.setElevationAngle(p_elevation);
		

			
			
		}
		return projector;
	}

	public void setDistance(float d){
		p_distance=d;
		if (  projector !=null)  projector.setDistance(d);
	}
	
	public void set2DScaling(float d){
		p_scaling=d;
		if (  projector !=null) projector.set2DScaling(d);
	}
	
	public void setRotationAngle(float d){
		p_rotation=d;
		if (  projector !=null)projector.setRotationAngle(d);
	}
	
	public void setElevationAngle(float d){
		p_elevation=d;
		if (  projector !=null) projector.setElevationAngle(d);
	}
	
	
	public float getDistance(){
		if ( projector == null ) return p_distance;
		else return projector.getDistance();
	}
	
	
	public float get2DScaling(){
		if ( projector == null ) return p_scaling;
		else return projector.get2DScaling();
	}
	public float getRotationAngle(){
		if ( projector == null ) return p_rotation;
		else return projector.getRotationAngle();
	}
	
	public float getElevationAngle(){
		if ( projector == null )return p_elevation;
		else return projector.getElevationAngle();
	}
	
	
	protected int calcDivisions = INIT_CALC_DIV;

	public int getCalcDivisions() {
		return calcDivisions;
	}

	public void setCalcDivisions(int v) {
		int o = this.calcDivisions;
		this.calcDivisions = v;
		property.firePropertyChange(CALC_DIVISIONS_PROPERTY, o, v);
	}

	protected int contourLines;

	public int getContourLines() {
		return contourLines;
	}

	public void setContourLines(int v) {
		int o = this.contourLines;
		this.contourLines = v;
		property.firePropertyChange(CONTOUR_LINES_PROPERTY, o, v);
	}

	protected int dispDivisions = INIT_DISP_DIV;

	public void setDispDivisions(int v) {
		int o = this.dispDivisions;
		this.dispDivisions = v;
		property.firePropertyChange(DISP_DIVISIONS_PROPERTY, o, v);
	}

	public int getDispDivisions() {
		if (dispDivisions > calcDivisions)
			dispDivisions = calcDivisions;
		while ((calcDivisions % dispDivisions) != 0)
			dispDivisions++;
		return dispDivisions;
	}

	protected float xMin;

	public float getXMin() {
		return xMin;
	}

	public void setXMin(float v) {
		float o = this.xMin;
		this.xMin = v;
		property.firePropertyChange(X_MIN_PROPERTY, new Float(o), new Float(v));
	}

	protected float yMin;

	public float getYMin() {
		return yMin;
	}

	public void setYMin(float v) {
		float o = this.yMin;
		this.yMin = v;
		property.firePropertyChange(Y_MIN_PROPERTY, new Float(o), new Float(v));
	}

	protected float zMin;

	public float getZMin() {
		return zMin;
	}

	public void setZMin(float v) {
		if (v >= zMax)
			return;
		float o = this.zMin;
		this.zMin = v;
		property.firePropertyChange(Z_MIN_PROPERTY, new Float(o), new Float(v));
	}

	protected float xMax;

	public float getXMax() {
		return xMax;
	}

	public void setXMax(float v) {
		float o = this.xMax;
		this.xMax = v;
		property.firePropertyChange(X_MAX_PROPERTY, new Float(o), new Float(v));
	}

	protected float yMax;

	public float getYMax() {
		return yMax;
	}

	public void setYMax(float v) {
		float o = this.yMax;
		this.yMax = v;
		property.firePropertyChange(Y_MAX_PROPERTY, new Float(o), new Float(v));
	}

	protected float z1Max;//the max computed
	protected float z1Min;//the min computed
	protected float z2Max;//the max computed
	protected float z2Min;//the min computed

	protected float zMax;

	public float getZMax() {
		return zMax;
	}

	public void setZMax(float v) {
		if (v <= zMin)
			return;
		float o = this.zMax;
		this.zMax = v;
		property.firePropertyChange(Z_MAX_PROPERTY, new Float(o), new Float(v));
	}

	public SurfaceModelCanvas() {
		super();
		property = new SwingPropertyChangeSupport(this);
		setColorModel(new ColorModelSet());
	}

	public ColorModelSet colorModel;

	public SurfaceColor getColorModel() {
		return colorModel;
	}

	protected void setColorModel(ColorModelSet v) {
		SurfaceColor o = this.colorModel;
		this.colorModel = v;
		if (colorModel != null)
			colorModel.setPlotColor(plotColor); //this shouls be handled by the model itself, without any
		if (colorModel != null)
			colorModel.setPlotType(plotType);
		property.firePropertyChange(COLOR_MODEL_PROPERTY, o, v);
	}

	/**
	 * Sets the text of status line
	 * 
	 * @param text
	 *            new text to be displayed
	 */

	public void setMessage(String text) {
	//@todo
	//System.out.println("Message"+text);
	}

	/**
	 * Called when automatic rotation starts.
	 */

	public void rotationStarts() {

	//setting_panel.rotationStarts();
	}

	/**
	 * Called when automatic rotation stops
	 */

	public void rotationStops() {

	//setting_panel.rotationStops();
	}

	public void exportCSV(File file) throws IOException {

		if (file == null)
			return;
		java.io.FileWriter w = new java.io.FileWriter(file);
		float stepx, stepy, x, y, v;
		float xi, xx, yi, yx;
		float min, max;
		boolean f1, f2;
		int i, j, k, total;

		f1 = true;
		f2 = true; // until no method is defined to set functions ...
		// image conversion

		int[] pixels = null;
		int imgwidth = 0;
		int imgheight = 0;

		try {
			xi = getXMin();
			yi = getYMin();
			xx = getXMax();
			yx = getYMax();
			if ((xi >= xx) || (yi >= yx))
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			setMessage("Error in ranges");
			return;
		}

		calcDivisions = getCalcDivisions();
		//func1calc = f1; func2calc = f2;

		stepx = (xx - xi) / calcDivisions;
		stepy = (yx - yi) / calcDivisions;

		total = (calcDivisions + 1) * (calcDivisions + 1);
		if (vertex == null)
			return;

		max = Float.NaN;
		min = Float.NaN;

		//		canvas.destroyImage();
		i = 0;
		j = 0;
		k = 0;
		x = xi;
		y = yi;

		float xfactor = 20 / (xx - xi);
		float yfactor = 20 / (yx - yi);

		w.write("X\\Y->Z;");
		while (j <= calcDivisions) {

			w.write(Float.toString(y));
			if (j != calcDivisions)
				w.write(';');
			j++;
			y += stepy;
			k++;
		}
		w.write("\n");
		//first line written
		i = 0;
		j = 0;
		k = 0;
		x = xi;
		y = yi;

		while (i <= calcDivisions) {
			w.write(Float.toString(x));
			w.write(';');
			while (j <= calcDivisions) {
				w.write(Float.toString(vertex[0][k].z));
				if (j != calcDivisions)
					w.write(';');
				j++;
				y += stepy;
				k++;
				//setMessage("Calculating : " + k*100/total + "% completed");
			}
			w.write('\n');
			//first line written
			j = 0;
			y = yi;
			i++;
			x += stepx;
		}
		w.flush();
		w.close();

	}

	public Plotter newPlotter(int calcDivisions) {
		setCalcDivisions(calcDivisions);
        plotter=new PlotterImpl();
		return plotter;
	}



        public PlotterImpl getPlotter() {
                return plotter;
        }



	/**
	 * Parses defined functions and calculates surface vertices
	 */
	public class PlotterImpl implements Plotter {
		float stepx, stepy;
		float xi, xx, yi, yx, v, x, y;
		float min1, max1, min2, max2;
		boolean f1, f2;
		int i, j, k, total;
		private float[][] pointsX;
	    private float[][] pointsY;
	    private float[][] pointsZ;
		

		int[] pixels = null;
		int imgwidth = 0;
		int imgheight = 0;
	    float xfactor;
	    float yfactor;
	    float zfactor;
	    
		int calcDivisions;

		
		/**
		 * Main constructor
		 */
		public PlotterImpl() {
			
		}

		
		

	 	@Override
	 	public float getFactorX() {
	 		return xfactor;
	 	}

	 	@Override
	 	public float getFactorY() {
	 		return yfactor;
	 	}

	 	@Override
	 	public float getFactorZ() {
	 		return zfactor;
	 	}

	 	
	 	@Override
	 	public float getStepX() {
	 		return stepx;
	 	}

	 	@Override
	 	public float getStepY() {
	 		return stepy;
	 	}

	 	@Override
	 	public float[][] getPointsX() {
	 		return pointsX;
	 	}

	 	@Override
	 	public float[][] getPointsY() {
	 		return pointsY;
	 	}

	 	@Override
	 	public float[][] getPointsZ() {
	 		return pointsZ;
	 	}

	 
	     
		/**
		 * Draw histogram and then function at the same time
		 * @param fu1 first
		 * @param fu2 second
		 */
		public void setH2DF2D(ArrayList<H2D> hu, ArrayList<F2D> fu ) {
			
			min1 = max1 = min2 = max2 = Float.NaN;
		        f1 = false;
			f2 = false;
			m_isP2D=false;
			m_isP3D=false;
			ish2f2=true;
			setDataAvailable(false); // clean space		

		//	System.out.println("Call to H2DF2D");
			setBarsType(false);
			
			f1=false; f2=false;
			Histogram2D  h1=null;
			F2D          h2=null;
			if (hu.size()>0 ) { f1=true;  h1=((H2D)hu.get(0)).get();}
			if (fu.size()>0 && hu.size()>0 ) { f1=f2=true;  h1=((H2D)hu.get(0)).get(); h2=((F2D)fu.get(0));}
			
			if (isBarsType()) f2=false;
			hasFunction1=f1;
			hasFunction2=f2;
			
			
			
			int calc_divisionsX=1;
			int calc_divisionsY=1;
			getProjector();
			
			if (f1) {

			
				int BinX = h1.xAxis().bins(); // number of bins
				int BinY = h1.yAxis().bins(); // number ofn bins
				double hight = h1.maxBinHeight();
				min1=0;
				max1 = (float) (NumUtil.roundUp((double) (hight* 1.2)));

				xi = (float) h1.xAxis().lowerEdge(); // and the lower edge of bin
				yi = (float) h1.yAxis().lowerEdge(); // and the lower edge of bin
				xx = (float) h1.xAxis().upperEdge(); // and the lower edge of bin
				yx = (float) h1.yAxis().upperEdge(); // and the lower edge of bin
				if ((xi >= xx) || (yi >= yx))
					throw new NumberFormatException();

				if (BinX != BinY && isBarsType() == false) {
					if (BinX > BinY)
						BinX = BinY;
					if (BinX < BinY)
						BinY = BinX;
					System.out.println("You cannot use different bins in the surface mode."
							+ "\n Consider the bar mode. At this moment, "
							+ "\n the number of bins were set to the smallest number of bins");

				}

				setCalcDivisions(BinX);
				calcDivisions = getCalcDivisions();
				calc_divisionsX=BinX;
				calc_divisionsY=BinY;
				
				total = (calc_divisionsX + 1) * (calc_divisionsY + 1); // compute total size

			//	if (isBarsType()==false) {
					vertex = allocateMemory(f1, f2, total);
					setSurfaceVertex(vertex); // define as the current vertex
					if (vertex == null)
						return;
			//	}

			}

			
			calcDivisions = getCalcDivisions();
			stepx = (xx - xi) / calc_divisionsX;
			stepy = (yx - yi) / calc_divisionsY;
		
			
			
			i = 0;
			j = 0;
			k = 0;
			x = xi;
			y = yi;
			xfactor = 20 / (xx - xi);
			yfactor = 20 / (yx - yi);
			zfactor = 20 / (max1 - min1);
			
			// System.out.println(zfactor);

			pointsX = null;
			pointsY = null;
			pointsZ = null;

			if (isBarsType()) {
				pointsX = new float[calc_divisionsX][calc_divisionsY];
				pointsY = new float[calc_divisionsX][calc_divisionsY];
				pointsZ = new float[calc_divisionsX][calc_divisionsY];

				// fill bar mode
				while (i < calc_divisionsX) {
					while (j < calc_divisionsY) {

						if (f1) {
							v = (float) (h1.binHeight(i, j));
							// System.out.println(v);
							pointsX[i][j] = (x - xi) * xfactor - 10;
							pointsY[i][j] = (y - yi) * yfactor - 10;
							pointsZ[i][j] =  v;
                                                        // current work
							//System.out.println("Debug x1="+pointsX[i][j]+ " y1="+pointsY[i][j]+ " z="+pointsZ[i][j]);
							
						}

						j++;
						y += stepy;
						k++;
						if (k % 100== 0)
							JHPlot.showMessage("Calculating : " + k * 100 / total
									+ "% completed");
					}
					j = 0;
					y = yi;
					i++;
					x += stepx;
				} // end second loop

			} else {

				// start non-bar mode, surface

				i = 0;
				k = 0;
				j = 0;
				x = xi;
				y = yi;
				while (i <= calc_divisionsX) {
					while (j <= calc_divisionsY) {

						if (f1) {
							// if (i<calc_divisionsX && j<calc_divisionsY)
							v = (float) (h1.binHeight(i, j));
							
							
							

							if (Float.isInfinite(v))
								v = Float.NaN;
							if (!Float.isNaN(v)) {
								if (Float.isNaN(max1) || (v > max1))
									max1 = v;
								else if (Float.isNaN(min1) || (v < min1))
									min1 = v;
							}
							
													
							
							vertex[0][k] = new SurfaceVertex((x - xi) * xfactor
									- 10, (y - yi) * yfactor - 10, v);

						}
						j++;
						y += stepy;
						k++;
						if (k % 100 == 0 || k == total)
							JHPlot.showMessage("Calculating : " + k * 100 / total
									+ "% completed");
					}
					j = 0;
					y = yi;
					i++;
					x += stepx;
				}
				
				
			} // end non-bar mode

	
			
			
			// now dealing with the function
			
			// System.out.println("xmin="+xi+" xmax="+xx);
			// System.out.println("ymin="+yi+" ymax="+yx);
		
			calcDivisions=calc_divisionsX;
			stepx = (xx - xi) / calc_divisionsX;
			stepy = (yx - yi) / calc_divisionsY;
			xfactor = 20 / (xx - xi);
			yfactor = 20 / (yx - yi);			
			 i = 0;
             j = 0;
             k = 0;
             x = xi;
             y = yi;
			 while (i <= calcDivisions) {
                 while (j <= calcDivisions) {
                         if (isBarsType()==true) {
                                 v = (float)h2.eval(x,y);
                                 if (Float.isInfinite(v))
                                         v = Float.NaN;
                                 if (!Float.isNaN(v)) {
                                         if (Float.isNaN(max1) || (v > max1))
                                                 max1 = v;
                                         else if (Float.isNaN(min1) || (v < min1))
                                                 min1 = v;
                                 }
                                 vertex[0][k] = new SurfaceVertex((x - xi) * xfactor - 10,
                                                 (y - yi) * yfactor - 10,v);
                                 
                                // System.out.println(v);
                         }
                         if (isBarsType()==false) {
                        	 
                                 v = (float) h2.eval(x,y);
                                 if (Float.isInfinite(v))
                                         v = Float.NaN;
                                 if (!Float.isNaN(v)) {
                                         if (Float.isNaN(max2) || (v > max2))
                                                 max2 = v;
                                         else if (Float.isNaN(min2) || (v < min2))
                                                 min2 = v;
                                 }
                                 vertex[1][k] = new SurfaceVertex((x - xi) * xfactor - 10,
                                                 (y - yi) * yfactor - 10, v);
                         }

                 j++;
                 y += stepy;
                 k++;
                 if (k % 100 == 0 || k == total)
                         JHPlot.showMessage("Calculating : " + k * 100 / total
                                         + "% completed");
         }
         j = 0;
         y = yi;
         i++;
         x += stepx;
         
		} // end loop
			
			
			
			
			
			
			
			
			
			
			// setting_panel.setMinimumResult(Float.toString(min));
			// setting_panel.setMaximumResult(Float.toString(max));

			if (min1 < 0.2 && min1 > -0.2) {
				DecimalFormat dfb = new DecimalFormat("#.#E00");
				String s1 = dfb.format((double) (min1 * 0.9));
				if (min1 < 0)
					s1 = dfb.format((double) (min1 * 1.1));
				min1 = (float) (Double.valueOf(s1.trim()).doubleValue());
			} else {

				if (min1 > 0)
					min1 = (float) NumUtil.roundUp((double) (min1));

				if (min1 < 0)
					min1 = -1 * (float) NumUtil.roundUp(-1 * (double) (min1));

			}

			max1 = (float) (NumUtil.roundUp((double) (max1 * 1.2)));
			
			// setMessage("Min="+Float.toString(min));
			// setMessage("Max="+Float.toString(max));
			setXMin(xi);
			setXMax(xx);
			setYMin(yi);
			setYMax(yx);
			setZMin(min1);
			setZMax(max1);
		

			setDataAvailable(true);
			autoScale();
			fireStateChanged();
			
			
			
			
			
		
			
		  
		  }
		
		
		
		/**
		 * Set functions values
		 * @param fu1 first
		 * @param fu2 second
		 */
		public void setF2D(ArrayList<F2D> fu) {

                // System.out.println(fu.size());
		

            min1 = max1 = min2 = max2 = Float.NaN;
            
			//reads the calcDivision that will be used
			calcDivisions = getCalcDivisions();
			setDataAvailable(false); // clean space
			total = (calcDivisions + 1) * (calcDivisions + 1); // compute total size
			// System.out.println(calcDivisions);
			
			f1=false; f2=false;
			F2D fu1=null;
			F2D fu2=null;
			
			if (fu.size() ==1 ) { f1=true;  fu1=(F2D)fu.get(0);}
			if (fu.size() ==2 ) { f1=f2=true;  fu1=(F2D)fu.get(0); fu2=(F2D)fu.get(1);}
			hasFunction1=f1;
			hasFunction2=f2;
			
			if (m_AutoXY==true){
				setXMin((float)fu1.getMinX()  );
				setXMax((float)fu1.getMaxX()  );
				setYMin((float)fu1.getMinY()  );
				setYMax((float)fu1.getMaxY()  );				
			}
			
			
			vertex = allocateMemory(f1, f2, total); // allocate vertex
			setSurfaceVertex(vertex); // define as the current vertex
			getProjector();
			try {
				xi = getXMin();
				yi = getYMin();
				xx = getXMax();
				yx = getYMax();
				if ((xi >= xx) || (yi >= yx))
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				setMessage("Error in ranges");
				return;
			}
			
			
			
			// System.out.println("xmin="+xi+" xmax="+xx);
			// System.out.println("ymin="+yi+" ymax="+yx);
			
			stepx = (xx - xi) / calcDivisions;
			stepy = (yx - yi) / calcDivisions;
			xfactor = 20 / (xx - xi);
			yfactor = 20 / (yx - yi);			
			 i = 0;
             j = 0;
             k = 0;
             x = xi;
             y = yi;
			 while (i <= calcDivisions) {
                 while (j <= calcDivisions) {
                         if (f1) {
                                 v = (float)fu1.eval(x,y);
                                 if (Float.isInfinite(v))
                                         v = Float.NaN;
                                 if (!Float.isNaN(v)) {
                                         if (Float.isNaN(max1) || (v > max1))
                                                 max1 = v;
                                         else if (Float.isNaN(min1) || (v < min1))
                                                 min1 = v;
                                 }
                                 vertex[0][k] = new SurfaceVertex((x - xi) * xfactor - 10,
                                                 (y - yi) * yfactor - 10,v);
                                 
                                // System.out.println(v);
                         }
                         if (f2) {
                        	 
                                 v = (float) fu2.eval(x,y);
                                 if (Float.isInfinite(v))
                                         v = Float.NaN;
                                 if (!Float.isNaN(v)) {
                                         if (Float.isNaN(max2) || (v > max2))
                                                 max2 = v;
                                         else if (Float.isNaN(min2) || (v < min2))
                                                 min2 = v;
                                 }
                                 vertex[1][k] = new SurfaceVertex((x - xi) * xfactor - 10,
                                                 (y - yi) * yfactor - 10, v);
                         }

                 j++;
                 y += stepy;
                 k++;
                 if (k % 100 == 0 || k == total)
                         JHPlot.showMessage("Calculating : " + k * 100 / total
                                         + "% completed");
         }
         j = 0;
         y = yi;
         i++;
         x += stepx;
         
		} // end loop
			
			
			
			 
			setXMin(xi);
			setXMax(xx);
			setYMin(yi);
			setYMax(yx);
			z1Min =  (float) floor(min1, 2);
			z1Max =  (float) ceil(max1, 2);
			z2Min =   (float) floor(min2, 2);
			z2Max =   (float) ceil(max2, 2);
			setDataAvailable(true);
			autoScale();
			fireStateChanged();
		    
			
		  
		  }
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
		/**
		 * Empty plot 
		 */
		public void setEmpty() {

                        isempty=true;
			//reads the calcDivision that will be used
			calcDivisions = 100; // getCalcDivisions();
			total = (calcDivisions + 1) * (calcDivisions + 1); // compute total size
			f1=true; f2=false;
			vertex = allocateMemory(f1, f2, total); // allocate vertex
			setSurfaceVertex(vertex); // define as the current vertex
			getProjector();
		
                       	try {
				xi = getXMin();
				yi = getYMin();
				xx = getXMax();
				yx = getYMax();
				if ((xi >= xx) || (yi >= yx))
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				setMessage("Error in ranges");
				return;
			}
	
			setXMin(xi);
			setXMax(xx);
			setYMin(yi);
			setYMax(yx);
			setDataAvailable(true);
			// autoScale();
			fireStateChanged();
		  
		  }
		
	
		
		
		
		
		
		
		
		
		
		
		public int getWidth() {
			return calcDivisions + 1;
		}

		public int getHeight() {
			return calcDivisions + 1;
		}

		

		
		/**
		 * Set histogram values
		 * @param h1
		 * @param h2
		 */
		public void setH2D( ArrayList<H2D> hu) {
			
			min1 = max1 = min2 = max2 = Float.NaN;
		        f1 = false;
			f2 = false;
                        setDataAvailable(false); // clean space
			m_isP2D=false;
			m_isP3D=false;
			Histogram2D  h1=null;
			Histogram2D  h2=null;
                        /*
			if (hu.size() ==1 ) { f1=true;  
			                   if (isContourType()==false && isDensityType()==false) setBarsType(true); 		                   
			                   h1=((H2D)hu.get(0)).get();}
			if (hu.size() ==2 ) { f1=f2=true; setBarsType(false);  h1=((H2D)hu.get(0)).get(); h2=((H2D)hu.get(1)).get();}
                        */
                        // we do not support bar style for 2 histograms. Only surface. 
                        if (hu.size() ==1 ) { f1=true;  
                                           h1=((H2D)hu.get(0)).get();}
                        if (hu.size() ==2 ) { f1=f2=true;  setBarsType(false);  h1=((H2D)hu.get(0)).get(); h2=((H2D)hu.get(1)).get();}


			hasFunction1=f1;
			hasFunction2=f2;
			
			
			int calc_divisionsX=1;
			int calc_divisionsY=1;
			getProjector();
			
			
	            float Mi = getZMin();
		    float Ma = getZMax();
			
			
			
			if (f1) {

			
				int BinX = h1.xAxis().bins(); // number of bins
				int BinY = h1.yAxis().bins(); // number ofn bins
				double hight = h1.maxBinHeight();
				min1=0;
				max1 = (float) (NumUtil.roundUp((double) (hight* 1.2)));

				xi = (float) h1.xAxis().lowerEdge(); // and the lower edge of bin
				yi = (float) h1.yAxis().lowerEdge(); // and the lower edge of bin
				xx = (float) h1.xAxis().upperEdge(); // and the lower edge of bin
				yx = (float) h1.yAxis().upperEdge(); // and the lower edge of bin
				
				
				if ((xi >= xx) || (yi >= yx))
					throw new NumberFormatException();

				if (BinX != BinY && isBarsType() == false) {
					if (BinX > BinY)
						BinX = BinY;
					if (BinX < BinY)
						BinY = BinX;
					System.out.println("You cannot use different bins in the surface mode."
							+ "\n Consider the bar mode. At this moment, "
							+ "\n the number of bins were set to the smallest number of bins");

				}

				
				
				
				setCalcDivisions(BinX);
				calcDivisions = getCalcDivisions();
				calc_divisionsX=BinX;
				calc_divisionsY=BinY;
				
				total = (calc_divisionsX + 1) * (calc_divisionsY + 1); // compute total size

				if (isBarsType()==false) {
					vertex = allocateMemory(f1, f2, total);
					setSurfaceVertex(vertex); // define as the current vertex
					if (vertex == null)
						return;
				}

			}

			
			calcDivisions = getCalcDivisions();
			stepx = (xx - xi) / calc_divisionsX;
			stepy = (yx - yi) / calc_divisionsY;
		
			
			if (f2) {

				double hight = h2.maxBinHeight();
				min2=0;
				max2 = (float) (NumUtil.roundUp((double) (hight* 1.2)));

				calcDivisions = getCalcDivisions();
				calc_divisionsX=calcDivisions;
				calc_divisionsY=calcDivisions;
				setDataAvailable(false); // clean space
				
				stepx = (xx - xi) / calc_divisionsX;
				stepy = (yx - yi) / calc_divisionsY;
				total = (calcDivisions + 1) * (calcDivisions + 1); // compute total size
				

				if (!isBarsType()) {
					vertex = allocateMemory(f1, f2, total);
					setSurfaceVertex(vertex); // define as the current vertex
					if (vertex == null)
						return;
				}				

			}

			
			i = 0;
			j = 0;
			k = 0;
			x = xi;
			y = yi;
			
			
			
			// for autoscale
		
			if (autoScaleZ == false && isBarsType() == false){
				max1=Ma;
				min1=Mi;
				max2=Ma;
				min2=Mi;
				
			}
		
			
			
			xfactor = 20 / (xx - xi);
			yfactor = 20 / (yx - yi);
			zfactor = 20 / (max1 - min1);
			
			// System.out.println(zfactor);

			pointsX = null;
			pointsY = null;
			pointsZ = null;

			if (isBarsType()) {
				pointsX = new float[calc_divisionsX][calc_divisionsY];
				pointsY = new float[calc_divisionsX][calc_divisionsY];
				pointsZ = new float[calc_divisionsX][calc_divisionsY];

				// fill bar mode
				while (i < calc_divisionsX) {
					while (j < calc_divisionsY) {

						if (f1) {
							v = (float) (h1.binHeight(i, j));
							// System.out.println(v);
							pointsX[i][j] = (x - xi) * xfactor - 10;
							pointsY[i][j] = (y - yi) * yfactor - 10;
							pointsZ[i][j] =  v;
						        //System.out.println("Debug x1="+pointsX[i][j]+ " y1="+pointsY[i][j]+ " z="+pointsZ[i][j]);

							
						}

						j++;
						y += stepy;
						k++;
						if (k % 100== 0)
							JHPlot.showMessage("Calculating : " + k * 100 / total
									+ "% completed");
					}
					j = 0;
					y = yi;
					i++;
					x += stepx;
				} // end second loop

			} else {

				// start non-bar mode, surface

				i = 0;
				k = 0;
				j = 0;
				x = xi;
				y = yi;
				while (i <= calc_divisionsX) {
					while (j <= calc_divisionsY) {

						if (f1) {
							// if (i<calc_divisionsX && j<calc_divisionsY)
							v = (float) (h1.binHeight(i, j));
							
							
							

							if (Float.isInfinite(v))
								v = Float.NaN;
							if (!Float.isNaN(v)) {
								if (Float.isNaN(max1) || (v > max1))
									max1 = v;
								else if (Float.isNaN(min1) || (v < min1))
									min1 = v;
							}
							
							
							
							
							
							
							vertex[0][k] = new SurfaceVertex((x - xi) * xfactor
									- 10, (y - yi) * yfactor - 10, v);

						}

						if (f2) {

							// if (i<calc_divisionsX && j<calc_divisionsY)
							v = (float) (h2.binHeight(i, j));

							if (Float.isInfinite(v))
								v = Float.NaN;
							if (!Float.isNaN(v)) {
								if (Float.isNaN(max2) || (v > max2))
									max2 = v;
								else if (Float.isNaN(min2) || (v < min2))
									min2 = v;
							}
							vertex[1][k] = new SurfaceVertex((x - xi) * xfactor
									- 10, (y - yi) * yfactor - 10, v);

						}
						j++;
						y += stepy;
						k++;
						if (k % 100 == 0 || k == total)
							JHPlot.showMessage("Calculating : " + k * 100 / total
									+ "% completed");
					}
					j = 0;
					y = yi;
					i++;
					x += stepx;
				}

			} // end non-bar mode

			
			
			// setting_panel.setMinimumResult(Float.toString(min));
			// setting_panel.setMaximumResult(Float.toString(max));

			if  (autoScaleZ == true){
			if (min1 < 0.2 && min1 > -0.2) {
				DecimalFormat dfb = new DecimalFormat("#.#E00");
				String s1 = dfb.format((double) (min1 * 0.9));
				if (min1 < 0)
					s1 = dfb.format((double) (min1 * 1.1));
				min1 = (float) (Double.valueOf(s1.trim()).doubleValue());
			} else {

				if (min1 > 0)
					min1 = (float) NumUtil.roundUp((double) (min1));

				if (min1 < 0)
					min1 = -1 * (float) NumUtil.roundUp(-1 * (double) (min1));

			}
			max1 = (float) (NumUtil.roundUp((double) (max1 * 1.2)));
			} // end autoscale
			
			zfactor = 20 / (max1 - min1);
			
			// setMessage("Min="+Float.toString(min));
			// setMessage("Max="+Float.toString(max));
			setXMin(xi);
			setXMax(xx);
			setYMin(yi);
			setYMax(yx);
			setZMin(min1);
			setZMax(max1);
		

			setDataAvailable(true);
			autoScale();
			fireStateChanged();
			

		}




		@Override
		public void setP2D(ArrayList<P2D> p2d_hold) {
			
			 if (p2d_hold == null) return;
			 
			 double minX,maxX,minY,maxY,minZ,maxZ;
			 minX = maxX = minY = maxY = minZ = maxZ=Double.NaN;
			 
			 
			 setDataAvailable(false);
			 m_isP2D=true;
			 m_isP3D=false;
	
			  
			 
			 int jpoint_size = 0;	   	   
	           for (int ii = 0; ii < p2d_hold.size(); ii++) {
	        	       P2D p2d = (P2D) p2d_hold.get(ii);
	                   for (int i = 0; i < p2d.size(); i++)
	                           jpoint_size++;
	           }

	           int kk = 0;
	           jpoint = new JPoint[jpoint_size];
	           // System.out.println("1) Load data with size:"+jpoint_size);
	           for (int ii = 0; ii < p2d_hold.size(); ii++) {
	                   P2D p2d = (P2D)p2d_hold.get(ii);
	                   for (int i = 0; i <p2d.size(); i++) {
	                          //  System.out.println(" kk="+kk);
	                	   
	                	   // find Min and Max for auto mode
	                	   double v=p2d.getX(i);
	                	   if (Double.isNaN(maxX) || (v> maxX))
                               maxX = v;
                           else if (Double.isNaN(minX) || (v < minX))
                               minX = v;
	                	   v=p2d.getY(i);
	                	   if (Double.isNaN(maxY) || (v> maxY))
                               maxY = v;
                           else if (Double.isNaN(minY) || (v < minY))
                               minY = v;
	                	   v=p2d.getY(i);
	                	   if (Double.isNaN(maxZ) || (v> maxZ))
                               maxZ = v;
                           else if (Double.isNaN(minZ) || (v < minZ))
                               minZ = v;
	                	   
	                           jpoint[kk] = new JPoint(p2d.getX(i), p2d.getY(i), p2d.getZ(i),
	                                           0, p2d.getSymbolColor(), p2d.getSymbolSize());
	                           kk++;
	                   }
	           }
			 
			 
			 
		
             float zi=0.0f;
             float zx=1.0f;
             float xi=0.0f;
             float xx=1.0f;
             float yi=0.0f;
             float yx=1.0f;
              
           
              
              
              try {
  				xi = getXMin();
  				yi = getYMin();
  				xx = getXMax();
  				yx = getYMax();
  				zi = getZMin();
  				zx = getZMax();
  				if ((xi >= xx) || (yi >= yx))
  					throw new NumberFormatException();
  			} catch (NumberFormatException e) {
  				setMessage("Error in ranges");
  				return;
  			}
              
              
              
              
            

             if (isAutoScaleZ() == true) {

            	 zi = (float)minZ; 
                 zx = (float)maxZ; 
                 
                 if (zi > 0)
                     zi = 0.4f * zi;
                 if (zi < 0)
                     zi = 1.8f * zi;
                 if (zx > 0)
                     zx = 1.8f * zx;
                 if (zx < 0)
                     zx = 1.9f * zx;
            
             zi = (float) NumUtil.roundUp((double) zi);
             zx = (float) NumUtil.roundUp((double) zx);
     
                 
             }
             
             
             if (m_AutoXY==true){
                 // System.out.println("Debug=Range is not set");
                 // max = (float) NumUtil.roundUp(1.25 * p2d.getMax(2));
                 // min = (float) p2d.getMin(2);

                 xi = (float)minX;
                 yi = (float)minY;
                 xx = (float)maxX;
                 yx = (float)maxY;
                 
                 if (xi > 0)
                         xi = 0.4f * xi;
                 if (yi > 0)
                         yi = 0.4f * yi;
                 if (xi < 0)
                         xi = 1.8f * xi;
                 if (yi < 0)
                         yi = 1.8f * yi;
                 if (xx > 0)
                         xx = 1.8f * xx;
                 if (yx > 0)
                         yx = 1.8f * yx;
                 if (xx < 0)
                         xx = 1.8f * xx;
                 if (yx < 0)
                         yx = 1.8f * yx;
                 
                 xx = (float) NumUtil.roundUp((double) xx);
                 yx = (float) NumUtil.roundUp((double) yx);
                 xi = (float) NumUtil.roundUp((double) xi);
                 yi = (float) NumUtil.roundUp((double) yi);
                 
                 
                
                 if ((xi >= xx) || (yi >= yx)  )
                         throw new NumberFormatException();
             } // end automatic range

             xfactor = 20 / (xx - xi);
             yfactor = 20 / (yx - yi);
             zfactor = 20 / (zx - zi);

 			setXMin(xi);
 			setXMax(xx);
 			setYMin(yi);
 			setYMax(yx);
 			setZMin(zi);
 			setZMax(zx);
 		

 			setDataAvailable(true);
 			
 			fireStateChanged();
             
 
             
			
		}




		@Override
		public void setP3D(ArrayList<P3D> p3d_hold) {
			m_isP3D=true;
			
			
             if (p3d_hold == null) return;
			 
			 setDataAvailable(false);
			 m_isP2D=false;
			 m_isP3D=true;
			 
			 
			 int jpoint_size = 0;	   	   
	           for (int ii = 0; ii < p3d_hold.size(); ii++) {
	        	       P3D p3d = (P3D) p3d_hold.get(ii);
	                   for (int i = 0; i < p3d.size(); i++)
	                           jpoint_size++;
	           }

	           int kk = 0;
	           jpoint = new JPoint[jpoint_size];
	           // System.out.println("1) Load data with size:"+jpoint_size);
	           for (int ii = 0; ii < p3d_hold.size(); ii++) {
	                   P3D p3d = (P3D)p3d_hold.get(ii);
	                   for (int i = 0; i <p3d.size(); i++) {
	                	  
	                	   
	                	   JPoint jpp = new JPoint(p3d.getX(i), p3d.getY(i), p3d.getZ(i),
                                   0, p3d.getPenColor(), p3d.getPenWidth());
                                  jpp.setExt(p3d.getDX(i), p3d.getDY(i), p3d.getDZ(i));
                                  jpoint[kk] = jpp;

	                	   
	                      
	                           kk++;
	                   }
	           }
			 
			 
			 
		
             float zi, zx, xi, xx, yi, yx;
              
             // look at the last in the row for autoscales
              P3D p3d=(P3D)p3d_hold.get(p3d_hold.size()-1);
            

             if (isAutoScaleZ() == true) {

                 // System.out.println("Debug=Range is not set");
                 // max = (float) NumUtil.roundUp(1.25 * p2d.getMax(2));
                 // min = (float) p2d.getMin(2);

                 xi = (float) p3d.getMin(0); // and the lower edge of bin i
                 yi = (float) p3d.getMin(1); // and the lower edge of bin i
                 xx = (float) p3d.getMax(0); // and the lower edge of bin i
                 yx = (float) p3d.getMax(1); // and the lower edge of bin i
                 zi = (float) p3d.getMin(2); // and the lower edge of bin i
                 zx = (float) p3d.getMax(2); // and the lower edge of bin i


                 if (xi > 0)
                         xi = 0.4f * xi;
                 if (yi > 0)
                         yi = 0.4f * yi;
                 if (xi < 0)
                         xi = 1.8f * xi;
                 if (yi < 0)
                         yi = 1.8f * yi;
                 if (xx > 0)
                         xx = 1.8f * xx;
                 if (yx > 0)
                         yx = 1.8f * yx;
                 if (xx < 0)
                         xx = 1.8f * xx;
                 if (yx < 0)
                         yx = 1.8f * yx;
                 if (zi > 0)
                         zi = 0.4f * zi;
                 if (zi < 0)
                         zi = 1.8f * zi;
                 if (zx > 0)
                         zx = 1.8f * zx;
                 if (zx < 0)
                         zx = 1.9f * zx;
                 
                 xx = (float) NumUtil.roundUp((double) xx);
                 yx = (float) NumUtil.roundUp((double) yx);
                 xi = (float) NumUtil.roundUp((double) xi);
                 yi = (float) NumUtil.roundUp((double) yi);
                 zi = (float) NumUtil.roundUp((double) zi);
                 zx = (float) NumUtil.roundUp((double) zx);


                 if ((xi >= xx) || (yi >= yx) || (zi >= zx) )
                         throw new NumberFormatException();
             } // end automatic range

             else {
             // this is when range was set already

            	 
            	  //  System.out.println("Ranges are set"+getZMax());
            	 
                	try {
        				xi = getXMin();
        				yi = getYMin();
        				xx = getXMax();
        				yx = getYMax();
        				zi = getZMin();
        				zx = getZMax();
        				if ((xi >= xx) || (yi >= yx))
        					throw new NumberFormatException();
        			} catch (NumberFormatException e) {
        				setMessage("Error in ranges");
        				return;
        			}
            	 

             }
             xfactor = 20 / (xx - xi);
             yfactor = 20 / (yx - yi);
             zfactor = 20 / (zx - zi);

 			setXMin(xi);
 			setXMax(xx);
 			setYMin(yi);
 			setYMax(yx);
 			setZMin(zi);
 			setZMax(zx);
 		

 			setDataAvailable(true);
 			
 			fireStateChanged();
             
			
			
			
			
			
			
			
			
			
			
			
			
		}



		
	} // end class
	
	
	
	/**
	 * Return points for plotting
	 * @return
	 */
	public JPoint[] getJPoints(){
		return jpoint;
		
	}
	
	
	
	
	public  synchronized double floor(double d, int digits) {
		if (d == 0)
			return d;
		// computes order of magnitude
		long og = (long) Math.ceil((Math.log(Math.abs(d)) / Math.log(10)));
		
		double factor = Math.pow(10, digits - og); 
		// the matissa
		double res = Math.floor((d * factor)) / factor; 
		//res contains the closed power of ten
		return res;
	}

	public  synchronized double ceil(double d, int digits) {
		if (d == 0)
			return d;
		long og = (long) Math.ceil((Math.log(Math.abs(d)) / Math.log(10)));
		double factor = Math.pow(10, digits - og); 
		double res = Math.ceil((d * factor)) / factor;
		return res;
	}

	
    
	
	/**
	 * Determines whether the delay regeneration checkbox is checked.
	 * 
	 * @return <code>true</code> if the checkbox is checked, <code>false</code>
	 *         otherwise
	 */
	protected boolean expectDelay = false;

	public boolean isExpectDelay() {
		return expectDelay;
	}

	public void setExpectDelay(boolean v) {
		boolean o = this.expectDelay;
		this.expectDelay = v;
		property.firePropertyChange(EXPECT_DELAY_PROPERTY, o, v);
	}

	public void toggleExpectDelay() {
		setExpectDelay(!isExpectDelay());
	}

	/**
	 * Determines whether to show bounding box.
	 * 
	 * @return <code>true</code> if to show bounding box
	 */
	protected boolean boxed;

	public boolean isBoxed() {
		return boxed;
	}

	public void setBoxed(boolean v) {
		boolean o = this.boxed;
		this.boxed = v;
		property.firePropertyChange(BOXED_PROPERTY, o, v);
	}

	public void toggleBoxed() {
		setBoxed(!isBoxed());
	}

	/**
	 * Determines whether to show x-y mesh.
	 * 
	 * @return <code>true</code> if to show x-y mesh
	 */
	protected boolean mesh;

	public boolean isMesh() {
		return mesh;
	}

	public void setMesh(boolean v) {
		boolean o = this.mesh;
		this.mesh = v;
		property.firePropertyChange(MESH_PROPERTY, o, v);
	}

	public void toggleMesh() {
		setMesh(!isMesh());
	}

	/**
	 * Determines whether to scale axes and bounding box.
	 * 
	 * @return <code>true</code> if to scale bounding box
	 */

	protected boolean scaleBox;

	public boolean isScaleBox() {
		return scaleBox;
	}

	
	public void setScaleBox(boolean v) {
		boolean o = this.scaleBox;
		this.scaleBox = v;
		property.firePropertyChange(SCALE_BOX_PROPERTY, o, v);
	}

	public void toggleScaleBox() {
		setScaleBox(!isScaleBox());
	}

	/**
	 * Determines whether to show x-y ticks.
	 * 
	 * @return <code>true</code> if to show x-y ticks
	 */
	protected boolean displayXY;

	public boolean isDisplayXY() {
		return displayXY;
	}

	public void setDisplayXY(boolean v) {
		boolean o = this.displayXY;
		this.displayXY = v;
		property.firePropertyChange(DISPLAY_X_Y_PROPERTY, o, v);
	}

	public boolean getDisplayXY() {
		return this.displayXY;
	}

	public boolean getDisplayZ() {
		return this.displayZ;
	}

	
	
	public void toggleDisplayXY() {
		setDisplayXY(!isDisplayXY());
	}

	/**
	 * Determines whether to show z ticks.
	 * 
	 * @return <code>true</code> if to show z ticks
	 */
	protected boolean displayZ;

	public boolean isDisplayZ() {
		return displayZ;
	}

	public void setDisplayZ(boolean v) {
		boolean o = this.displayZ;
		this.displayZ = v;
		property.firePropertyChange(DISPLAY_Z_PROPERTY, o, v);
	}

	public void toggleDisplayZ() {
		setDisplayZ(!isDisplayZ());
	}

	/**
	 * Determines whether to show face grids.
	 * 
	 * @return <code>true</code> if to show face grids
	 */
	protected boolean displayGrids;

	public boolean isDisplayGrids() {
		return displayGrids;
	}

	public void setDisplayGrids(boolean v) {
		boolean o = this.displayGrids;
		this.displayGrids = v;
		property.firePropertyChange(DISPLAY_GRIDS_PROPERTY, o, v);
	}

	public void toggleDisplayGrids() {
		setDisplayGrids(!isDisplayGrids());
	}

	/**
	 * Determines whether the first function is selected.
	 * 
	 * @return <code>true</code> if the first function is checked,
	 *         <code>false</code> otherwise
	 */

	protected boolean hasFunction1 = true;
	protected boolean plotFunction1 = hasFunction1;

	public boolean isPlotFunction1() {
		return plotFunction1;
	}

	public void setPlotFunction1(boolean v) {
		boolean o = this.plotFunction1;
		this.plotFunction1 = hasFunction1 && v;
		property.firePropertyChange(PLOT_FUNCTION_1_PROPERTY, o, v);
		fireAllFunction(o, plotFunction2  );
	}
	
	public void setPlotFunction12(boolean p1, boolean p2) {
		boolean o1 = this.plotFunction1;
		boolean o2 = this.plotFunction2;
		
		this.plotFunction1 = hasFunction1 && p1;
		property.firePropertyChange(PLOT_FUNCTION_1_PROPERTY, o1, p1);
		
		this.plotFunction2 = hasFunction2 && p2;
		property.firePropertyChange(PLOT_FUNCTION_2_PROPERTY, o2, p2);
		fireAllFunction(o1, o2 );
	}

	public void togglePlotFunction1() {
		setPlotFunction1(!isPlotFunction1());

	}

	public void togglePlotFunction2() {
		setPlotFunction2(!isPlotFunction2());

	}

	/**
	 * Determines whether the first function is selected.
	 * 
	 * @return <code>true</code> if the first function is checked,
	 *         <code>false</code> otherwise
	 */
	protected boolean hasFunction2 = true;
	protected boolean plotFunction2 = hasFunction2;

	public boolean isPlotFunction2() {
		return plotFunction2;
	}

	public void setPlotFunction2(boolean v) {
		boolean o = this.plotFunction2;
		this.plotFunction2 = hasFunction2 && v;
		property.firePropertyChange(PLOT_FUNCTION_2_PROPERTY, o, v);
		fireAllFunction( plotFunction1, o  );
	}

	
	
	/**
	 * Is this P2D object?
	 * @return
	 */
	public boolean isP2D(){
		return m_isP2D;
	}
	
	/**
	 * Is this P3D object?
	 * @return
	 */
	public boolean isP3D(){
		return m_isP3D;
	}
	/**
	 * Processes menu events
	 * 
	 * @param item
	 *            the selected menu item
	 */

	/*
	warning : pour faire la config (dans un menu ou dans un panel !! il y 
		a plein de cas et de sous cas !
	autour de ces trois actions l�
	
			canvas.setContour(false);
			canvas.setDensity(false);
			canvas.stopRotation();
	*/

	/**
	 * Allocates Memory
	 */

	private SurfaceVertex[][] allocateMemory(boolean f1, boolean f2, int total) {
		SurfaceVertex[][] vertex = null;

		// Releases memory being used    
		//canvas.setValuesArray(null);

		/* The following program:
		
			SurfaceVertex[][] vertex = new SurfaceVertex[2][];
		
			if (f1) vertex[0] = new SurfaceVertex[total];
			if (f2) vertex[1] = new SurfaceVertex[total];
			
			
			Didn't work with my Microsoft Internet Explorer v3.0b2.
			It resulted in a "java.lang.ArrayStoreException"  :(
			
			*/

		try {
			vertex = new SurfaceVertex[2][total];
			if (!f1)
				vertex[0] = null;
			if (!f2)
				vertex[1] = null;
		} catch (OutOfMemoryError e) {
			setMessage("Not enough memory");
		} catch (Exception e) {
			setMessage("Error: " + e.toString());
		}
		return vertex;
	}

	/**
	 * Sets file name
	 */

	/**
	 * Sets data availability flag
	 */
	protected boolean dataAvailable;

	public boolean isDataAvailable() {
		return dataAvailable;
	}

	private void setDataAvailable(boolean v) {
		boolean o = this.dataAvailable;
		this.dataAvailable = v;
		property.firePropertyChange(DATA_AVAILABLE_PROPERTY, o, v);
	}

	javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	protected void fireStateChanged() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		ChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new ChangeEvent(this);
				((ChangeListener) listeners[i + 1]).stateChanged(e);
			}
		}
	}

	public void addChangeListener(ChangeListener ol) {
		listenerList.add(ChangeListener.class, ol);
	}

	public void removeChangeListener(ChangeListener ol) {
		listenerList.remove(ChangeListener.class, ol);
	}

	@Override
	public boolean isH2F2() {
		return ish2f2;
	}

	

	public void setLabelOffsetX(float a) {
		m_LabelOffsetX=a;
		
	}
	
	public Font  getLabelFont() {
		return m_LabelFont;
		
	}
	
	

	public void setLabelOffsetZ(float a) {
		m_LabelOffsetZ=a;
		
	}

	public void setLabelOffsetY(float a) {
		m_LabelOffsetY=a;
		
	}

	public void setTicOffset(float a) {
		m_TicOffset=a;
		
	}

	public float  getTicOffset() {
		return m_TicOffset;
		
	}

	
	
	public void setAxesFontColor(Color fontColorAxes) {
		m_AxesFontColor=fontColorAxes;
		
	}

	public Color getAxesFontColor() {
		return m_AxesFontColor;
	}

	public void setFontColorLabel(Color fontColorLabel) {
		m_FontColorLabel=fontColorLabel;
		
	}
	public Color getFontColorLabel() {
		return m_FontColorLabel;
		
	}

	
	
	public double getLabelOffsetX() {
		return m_LabelOffsetX;
	}

	public double getLabelOffsetY() {
		return m_LabelOffsetY;
	}

	public double getLabelOffsetZ() {
		return m_LabelOffsetZ;
	}

	public void setPenWidth(float w) {
		m_PenWidth=w;
		
	}

	public float getPenWidth() {
		return m_PenWidth;
	}

	public void setXlabel(String a) {
		m_Xlabel=a;
		
	}

	public void setYlabel(String a) {
		m_Ylabel=a;
		
	}

	public void setZlabel(String a) {
		m_Zlabel=a;
		
	}

	public void setLabelFont(Font a) {
		m_LabelFont=a;
		
	}

	public void setAxisFont(Font fontAxis) {
	  m_AxisFont=fontAxis;
		
	}

	public Font getAxisFont() {
		return m_AxisFont;
	}

	public void setLabelColor(Color a) {
		m_LabelColor=a;
		
	}

	public void setTicFont(Font a) {
		 m_TicFont=a;
		
	}

	public Font getTicFont() {
		 return m_TicFont;
		
	}

	public Color  getLabelColor(){
		return m_LabelColor;
	
	}

	@Override
	public float getFrameScale() {
		return m_FrameScale;
	}

	@Override
	public void setFrameScale(float a) {
		m_FrameScale=a;
		
	}

	@Override
	public String getXlabel() {
		return m_Xlabel;
		
	}

	@Override
	public String getYlabel() {
		return m_Ylabel;
	}

	@Override
	public String getZlabel() {
		return m_Zlabel;
	}

	@Override
	public void setInitScaling(float scaling) {
		init_scaling=scaling;
	}
	
	@Override
	public float getInitScaling() {
		return init_scaling;
	}

	public void setAutoScaleXY(boolean b) {
		m_AutoXY=b;
		
	}
	
	public boolean getAutoScaleXY() {
		return m_AutoXY;
		
	}

	
	
}//end of class
