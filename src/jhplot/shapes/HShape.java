// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot.shapes;

import java.awt.*;


import jplot.XMLRead;
import jplot.XMLWrite;
import java.io.*;



/**
 * 
 * Main class to build a JAVA 2D primitives
 * 
 * @author S.Chekanov
 * 
 */

public class HShape implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected double width = 5.;

	protected double length = 10.;
		
	protected Font f=new Font("sansserif", Font.BOLD, 16);

	protected double rotate=0;
	
	protected String text="not set";

	static public final int LINE = 1;

	static public final int ARROW = 2;

	static public final int TEXT = 3;

	static public final int IMAGE = 4;

	static public final int CIRCLE = 5;

	static public final int ELLIPSE = 6;

	static public final int RECTAN = 7;

	static public final int POLYGON = 8;

	static protected final Stroke DFS = new BasicStroke(2);

	protected Stroke strock= new BasicStroke();

	protected Color color;

	protected double X1, Y1, X2, Y2;

	protected float transp;

	protected int whoAm = 0;

	protected boolean fill = false;

	protected double XX[], YY[];
	
	
	protected int usePosition=2; // 1 NDC:  position normilised
                                 // 2 USER: user coordinates 

	/**
	 * Build a primitive
	 * 
	 * @param X1
	 *            X start position
	 * @param Y1
	 *            Y start position
	 * @param X2
	 *            X end position
	 * @param Y2
	 *            Y end position
	 * @param stroke
	 *            Stroke type for line
	 * @param color
	 *            Color
	 */

	public HShape(double X1, double Y1, double X2, double Y2, Stroke stroke,
			Color color) {
		this.strock = stroke;
		this.color = color;
		this.X1 = X1;
		this.Y1 = Y1;
		this.X2 = X2;
		this.Y2 = Y2;
		this.transp = 1.0f;
	}

	
	/**
	 * Build a default primitive
	 * 
	 
	 */

	public HShape() {
		
		this.strock = DFS;
		this.color = Color.black;
		this.X1 = 0;
		this.Y1 = 0;
		this.X2 = 0;
		this.Y2 = 0;
		this.transp = 1.0f;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Build a primitive using the default color and stroke
	 * 
	 * @param X1
	 *            X start position
	 * @param Y1
	 *            Y start position
	 * @param X2
	 *            X end position
	 * @param Y2
	 *            Y end position
	 */
	public HShape(double X1, double Y1, double X2, double Y2) {

		this.X1 = X1;
		this.Y1 = Y1;
		this.X2 = X2;
		this.Y2 = Y2;
		this.strock = new BasicStroke(2);
		this.color = Color.black;
		this.transp = 1.0f;

	}

	/**
	 * Build a primitive using polyline. Not yet done
	 * 
	 * @param XX
	 * @param YY
	 * @param strock
	 * @param color
	 */
	public HShape(double[] XX, double[] YY, Stroke strock, Color color) {
		this.strock = strock;
		this.color = color;
		this.XX = XX;
		this.YY = YY;
		this.transp = 1.0f;
	}

	/**
	 * Get position of a primitive
	 * 
	 * @return double[] positions
	 */
	public double[] getPosition() {

		double[] pos = { X1, Y1, X2, Y2 };
		return pos;
	}


       /**
         * Rotate the shape or text. The rotation angle is defined in radians. 
         * 
         * @param theta rotaion angle for the object (in rad).
         */
        public void setRotation(double theta){
        rotate = theta;
       }


      /**
         * Get rotation angle theta. 
         * 
         * @return get rotation angle theta (rad). 
         */
      public double getRotation(){
        return rotate;
       }


	/**
	 * Scale the positions
	 * 
	 * @param Xscale
	 * @param Yscale
	 * @return  Scale 
	 */
	public double[] getScaledPosition(double Xscale, double Yscale) {
		double[] pos = new double[4];
		pos[0] = X1 * Xscale;
		pos[1] = Y1 * Yscale;
		pos[2] = X2 * Xscale;
		pos[3] = Y2 * Yscale;
		return pos;
	}

	/**
	 * Sets the color to a specific value
	 * 
	 * @param c
	 *            color used to draw the label
	 */
	public void setColor(Color c) {
		color = c;
	}

	/**
	 * Get a color of the primitive
	 * 
	 * @return Color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Get the Stroke to a specific value
	 * 
	 * @return the Stroke used to draw
	 */
	public Stroke getStroke() {
		return strock;
	}

	/**
	 * Set the Stroke to a specific value
	 */
	public void setStroke(Stroke stroke) {

		this.strock = stroke;
	}

	/**
	 * Fill a primitive
	 * 
	 * @param fill
	 *            true if it is filled
	 */
	public void setFill(boolean fill) {

		this.fill = fill;
	}

	/**
	 * Is this primitive filled?
	 * 
	 * @return true if it filled
	 */
	public boolean getFill() {

		return this.fill;
	}

	// do not work
	public boolean contains(double Xm, double Ym) {

		return true;
	}

	/**
	 * Get the transparency level
	 * 
	 * @return the transparency level (0-1)
	 */
	public Float getTransparency() {
		return transp;
	}

	/**
	 * Get the transparency level
	 * 
	 * @param trans the transparency level (0-1)
	 */
	public void setTransparency(double trans) {
		transp = (float) trans;
	}

	
	

	 /**
	  * Is the position set?
	  * 
	  * @return 0 if location is not defined
	  *         1 if the position is defined in the NDC system
	  *         2 if the location is defined in the user coordinates
	  *         
	  */
	 public int getPositionCoordinate() {
	   return usePosition;
	 }


	 /**
	  * How to set the position of this object?
	  * 
	  * @param  0 if location is not defined
	  *         1 if the position is defined in the NDC system
	  *         2 if the location is defined in the USER coordinates
	  *         
	  */
	 public void setPositionCoordinate(int usePosition) {
	   this.usePosition=usePosition;
	 }
	 
	 /**
	  * Set the style of the coodinate position
	  * 
	  * @param howToSet  set it to "NDC" for normilized coordinates (in the range 
	  *                  0-1). This is a data independent position.
	  *                  Set it to "USER" for the user coordinates
	  */


	 public void setPosCoord(String howToSet)
	  {
	 	 
	   usePosition=2;
	   if (howToSet.equalsIgnoreCase("USER")) usePosition=2;
	   if (howToSet.equalsIgnoreCase("NDC"))  {
	 	   usePosition=1;
	 	   if (X1>1) X1=1;
	 	   if (Y1>1) Y1=1;
	 	   if (X1<0) X1=0;
	           if (Y1<0) Y1=0;
	       
	           if (X2>1) X2=1;
	 	   if (Y2>1) Y2=1;
	 	   if (X2<0) X2=0;
	           if (Y2<0) Y2=0;
	       
	       
	       
	   }
	   
	  }
	
	
	
	/**
	 * Set dashed line
	 * @param thikness
	 */
	public void setDashed(double thikness) {

		float thikne = (float) thikness;
		float miterLimit = 10f;
		float[] dashPattern = { 10f };
		float dashPhase = 5f;
		strock = new BasicStroke(thikne, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, miterLimit, dashPattern, dashPhase);

	}

	
	/**
	 * Set dotted line
	 * @param thikness
	 */
	public void setDotted(double thikness) {

		float thikne = (float) thikness;
		float miterLimit = 10f;
		float[] dashPattern = { 1f };
		float dashPhase = 0f;
		strock=
		      new BasicStroke(
		    	 thikne,
		         BasicStroke.CAP_BUTT,
		         BasicStroke.JOIN_MITER,
		         miterLimit,
		         dashPattern,
		         dashPhase
		      );
		
		
		
	}

	
	
	
	/**
	   * Returns the settings of the primitive in a XML instance.
	   * @param xw instance of the class containing the settings.
	   */
	  public void getSettings(XMLWrite xw) { 
	    
		xw.add("name",String.valueOf(text)); 
		xw.add("type",String.valueOf(whoAm));
		xw.open("primitive");
	    
	    
		
		
	    xw.set("color",getColor());
	    xw.set("font",f);
	    xw.set("stroke",(BasicStroke)strock);
	   
	    xw.add("rotate",String.valueOf(rotate));
	    xw.add("fill",String.valueOf(fill));
	    xw.add("transp",String.valueOf(transp));
	    xw.add("usePosition",String.valueOf(usePosition));
	    xw.set("show");
	    
	    xw.add("x1",String.valueOf(X1));
	    xw.add("y1",String.valueOf(Y1));
	    xw.add("x2",String.valueOf(X2));
	    xw.add("y2",String.valueOf(Y2));
	    xw.set("position");
	    
	    xw.add("width",String.valueOf(width)); 
		xw.add("length",String.valueOf(length));
		xw.open("arrow");
	    
	  
	    xw.close();
	  }

	  /**
	   * Update the current primitive with the settings.
	   * These settings are formatted in XML and stored in an XMLRead instance.
	   * @param xr instance of the reader class, containing the settings.
	   */
	  public void updateSettings(XMLRead xr) {
	
		whoAm = xr.getInt("primitive/type",1);
		text = xr.getString("primitive/text",text);
		 
		
		f=xr.getFont("color",f);
	    color=xr.getColor("color",color);
	    strock = xr.getStroke("show/fill",(BasicStroke)strock);
	    	   

	    X1 = xr.getDouble("position/x1",X1);
	    X2 = xr.getDouble("position/x2",X2);
	    Y1 = xr.getDouble("position/y1",Y1);
	    Y2 = xr.getDouble("position/y2",Y2);
	    
	    rotate = xr.getDouble("show/rotate",rotate);
	    fill = xr.getBoolean("show/fill",fill);
	    transp = xr.getFloat("show/transp",transp);
	    usePosition = xr.getInt("show/usePosition", usePosition);
	    
	    width = xr.getDouble("arrow/width",width);
	    length = xr.getDouble("arrow/length",length);
	    
	    
	    
	    
	   
	  }
	  
	  
	  
	  
	  
	  
	  /**
		 * Set primitive type
		 * 
		 * @param whoAm  Primitive types
		 */

		public void setWhoAm(int whoAm) {
			this.whoAm=whoAm ;
		}
	  
	  
	  
	  
	
	
	
	
	/**
	 * Primitive type
	 * 
	 * @return Primitive types
	 */

	public int getWhoAm() {
		return whoAm;
	}

}
