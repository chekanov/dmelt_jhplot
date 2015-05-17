/*
 * JHPLOT * 
 * Copyright (C) 2006 S.Chekanov 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *   Additional permission under GNU GPL version 3 section 7:
 *    If you have received this program as a library with written permission from the DataMelt team,
 *    you can link or combine this library with your non-GPL project to convey the resulting work.
 *    In this case, this library should be considered as released under the terms of
 *    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
 *    provided you include this license notice and a URL through which recipients can access the
 *    Corresponding Source.

 */

package jplot;
import java.text.DecimalFormat;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * This is a special-purpose array for use with the graphical plotting
 * program JHPlot. Its main functionality is to provide a series of
 * plot points (X,Y,Z pairs) which form a line or something to plot.
 * <p>A data array has plotting attributes, e.g. saying whether we
 * should plot symbols, draw lines or fill areas. All the plotting
 * attributes are defined in the base-class LinePars, here we set the
 * actual data array, i.e. a vector of points (PlotPoints) defining
 * X,Y.
 * @see LinePars
 */
public class DataArray2D extends LinePars {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/// index number of the datafile for this array
  protected String title=" ";
  /// column index of the selected column
  protected double meanX=0;
  protected double meanY=0;
  protected double meanZ=0;
  protected int Ntot=0;
  protected ArrayList<PlotPoint2D> points;
  protected double[] maxValue = new double [3];
  protected double[] minValue = new double [3];
  protected double[] lowestNonZeroValue = new double [3];

  /**
   * Default constructor, initializes the class with nothing.
   */
  public DataArray2D() {
    Ntot=0;
    points = new ArrayList<PlotPoint2D>(); 
  }

  /**
   * Default constructor, initializes the class with nothing.
   */
  public DataArray2D(int size) {
    Ntot=0;
    points = new ArrayList<PlotPoint2D>(size); 
  }

  
  /**
   * Updates maximum and minimum values.
   * @param x just entered x-value of the point
   * @param y just entered y-value of the point
   * @param z just entered z-value of the point
   */
  public void updateMinMax(double x, double y, double z) {

    if (x > maxValue[0]) maxValue[0] = x;
    if (x < minValue[0]) minValue[0] = x;
    if (x < lowestNonZeroValue[0] && x > 0.0) {
      lowestNonZeroValue[0] = x;
    }

    if (y > maxValue[1]) maxValue[1] = y;
    if (y < minValue[1]) minValue[1] = y;    
    if (y < lowestNonZeroValue[1] && y > 0.0) {
      lowestNonZeroValue[1] = y;
    }

    if (z > maxValue[2]) maxValue[2] = z;
    if (z < minValue[2]) minValue[2] = z;
    if (z < lowestNonZeroValue[2] && z > 0.0) {
      lowestNonZeroValue[2] = z;
    }



  }

/**
   * Updates Mean values. 
   * @param x just entered x-value of the point
   * @param y just entered y-value of the point
   * @param z just entered y-value of the point
   */
  public void updateMean(double x, double y, double z){ 

     meanX += x;
     meanY += y;
     meanZ += z;
     Ntot++;

  }


 public double meanX()
        {
                return meanX/(double)Ntot; 
        }


 public double meanY()
        {
                return meanY/(double)Ntot;
        }


 public double meanZ()
        {
                return meanZ/(double)Ntot;
        }


  /**
   * Adds the values of a plot-point pair (x,y,z).
   * This point is added at the end of the array.
   * @param x x-value of the plot-point
   * @param y y-value of the plot-point
   * @param z z-value of the plot-point
   */
  public void addPoint(double x, double y,  double z) {
    updateMinMax(x,y,z);
    updateMean(x,y,z);
    points.add(new PlotPoint2D(x,y,z));
  }

/**
   * Sets the values of a plot-point pair (x,y).
   * @param i index of the plot-point
   * @param x x-value of the plot-point
   * @param y y-value of the plot-point
   * @param z z-value of the plot-point
   */
  public void setPoint(int i, double x, double y, double z) {
    if (i >= 0 && i < points.size()) {
      updateMinMax(x,y,z);
      points.add(i,new PlotPoint2D(x,y,z));
    }
  }



 
  /**
   * Returns the plot-point of the specified index.
   * @param i index of the plot-point
   * @return plotpoint at index i
   */
  public PlotPoint2D getPoint(int i) {
    if (i >= 0 && i < points.size()) return (PlotPoint2D) points.get(i);
    return null;
  }
  
  
  /**
   * Return the length of the data vector.
   * @return length of the PlotPoint vector
   */
  public int size() {
    return points.size();
  }

  
  
  /**
   * Return the data vector
   * @return data vector with all the plot points
   */
  public ArrayList getData() {
    return points;
  }


  /**
   * Return a specific X-value. This function returns POSINF (1e300)
   * if index i falls beyond the valid range.
   * @param i index of the array
   * @return the value of x at index i
   */
  public double getX(int i) {
    if (i >= 0 && i < size()) {
      return ((PlotPoint2D)points.get(i)).getX();
    }
    return GraphSettings.INF;
  }


  /**
   * Return a specific Y-value. This function returns POSINF (1e300)
   * if index i falls beyond the valid range.
   * @param i index of the array
   * @return the value of y at index i
   */
   public double getY(int i) {
    if (i >= 0 && i < size()) {
      return ((PlotPoint2D)points.get(i)).getY();
    }
    return GraphSettings.INF;
  }


 /**
   * Return a specific Z-value. This function returns POSINF (1e300)
   * if index i falls beyond the valid range.
   * @param i index of the array
   * @return the value of z at index i
   */
   public double getZ(int i) {
    if (i >= 0 && i < size()) {
      return ((PlotPoint2D)points.get(i)).getZ();
    }
    return GraphSettings.INF;
  }



  /**
   * Returns the maximum value in the range. Careful, no error
   * checking on the value of axis, which should be less than
   * N_AXES, defined in GraphSettings.
   * @param axis defines to which axis this function applies. 0-x, 1-y, 2-z
   * @return the maximum value.
   */
   public double getMaxValue(int axis) {
    return maxValue[axis];
  }

  /**
   * Returns the minimum value in the range. Careful, no error
   * checking on the value of axis, which should be less than
   * N_AXES, defined in GraphSettings.
   * @param axis defines to which axis this function applies.
   * @return the minimum value.
   */
   public double getMinValue(int axis) {
    return minValue[axis];
  }
  
  /**
   * Returns the lowest, but non-zero value in the range. This is a
   * a usefull 'minimum' value for e.g. logarithmic ranges.
   * @param axis defines to which axis this function applies.
   * @return the minimum value.
   */
   public double getLowestNonZeroValue(int axis) {
    return lowestNonZeroValue[axis];
  }
 
 

  /**
   * Clears the current array.
   */
  public void clear() {


    if (points.size() > 0) {
      Ntot=0;
      points.clear();
      for (int k=0; k<3; k++) {
	maxValue[k] = -GraphSettings.INF;
	minValue[k] = GraphSettings.INF;
	lowestNonZeroValue[k] =GraphSettings.INF;
      }
    }
  }




  /**
	 * Write a DataArray to an external file. 
	 * If errors on data points are not given,
	 * they are set to 0
	 * 
	 * @param name
	 *            File name with output
	 * @param title
	 *           Title                     
	 */
	public void toFile(String name, String title) {

		DecimalFormat dfb = new DecimalFormat("##.########E00");
		Date dat = new Date();
		String today = String.valueOf(dat);

		this.title=title;
		
		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);
			tx.println("# 3" ); // dimension first
			tx.println("# jhplot: output data from P1D:"+title);
			tx.println("# jhplot: created at " + today);
			tx.println("# x,y,z");
			tx.println("#");
			for (int i = 0; i < size(); i++) {

				
					String x = dfb.format(getX(i));
					String y = dfb.format(getY(i));
					String z = dfb.format(getZ(i));
					tx.println(x + " " + y+ " "+z);
			
			}
			f1.close();

		} catch (IOException e) {
			System.out.println("Error in the output file");
			e.printStackTrace();
		}

	}
	
	/**
	 * Get the title if any
	 * @return
	 */

	public String getTitle() {
		return title;
	}
	
	/**
	 * Get data from a file
	 * @param file
	 * @return
	 */
	
	public boolean parse(File file) {
		  
		
	    boolean res=false;
	    
	    try {
	        BufferedReader in = new BufferedReader(new FileReader(file));
	        res=parse(in);
	        if (res == false) return false;
	        
	    }catch (IOException e) {
	            Utils.oops(null,"Can't parse data file " + file.toString());
	            return false;
	          }
	         
	  return true;
	  
  }
	
	
	
	
	
		
		/** 
		 * Read DataArray from  BufferedReader
		 * @param in BufferedReader
		 * @return true if no error
		 */
		  public boolean parse(BufferedReader in) {
		
			  String s;
			  clear();
			  
			  
			  try {
				  
			  // String info=in.readLine();
			  // info = info.trim();
			  
			  // info=info.substring(1, info.length() );
			  // dimension=Integer.parseInt(info);
			  
			  while ( (s=in.readLine()) != null) {
					s = s.trim();
					if (s.length() < 1) continue;
					if (s.startsWith("#")) continue;	
					if (s.startsWith("*")) continue;
					
					StringTokenizer st = new StringTokenizer(s);
					int ncount = st.countTokens(); // number of words
//					
				//	String[] sword = new String[ncount];
					double[] snum = new double[ncount];

					
//					 split this line
					int mm = 0;
					while (st.hasMoreTokens()) { // make sure there is stuff
						// to get
						String tmp = st.nextToken();

						// read double
						double dd = 0;
						try {
							dd = Double.parseDouble(tmp.trim());
						} catch (NumberFormatException e) {
							System.out.println("Error in reading the line "
									+ Integer.toString(mm + 1));
						}
						snum[mm] = dd;
						mm++;

					} // end loop over each line

					if (ncount == 3) 
						 addPoint(snum[0], snum[1], snum[2]);
			        }
					
					
					
			  } catch (IOException e) {
			      Utils.oops(null,"Can't parse data from BufferedReader");
			      return false;
			    }
			  
			  
		  return true;	  
		  }
		
	

		  /**
			 * Print a data container on the screen
			 * 
			 */
			public void print() {

				DecimalFormat dfb = new DecimalFormat("##.#####E00");
				Date dat = new Date();
				String today = String.valueOf(dat);

				System.out.println("");
				System.out.println("# jhplot: output from P2D " + this.title);
				System.out.println("# jhplot: created at " + today);
				System.out
						.println("# x,y,z");
				System.out.println("#");
				for (int i = 0; i < size(); i++) {

					String x = dfb.format(getX(i));
					String y = dfb.format(getY(i));
                                        String z = dfb.format(getZ(i));

					System.out
							.println(x + "  " + y + "  " + z);
				}

			}








}

