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

import javax.swing.*;

import root.Converter;


import java.io.*;
import java.util.*;

import hep.aida.*;
import hep.aida.ref.histogram.*;

import java.text.DecimalFormat;

import jhplot.gui.HelpBrowser;
import jhplot.utils.SHisto;


// reading root files
import hep.io.root.interfaces.*;

/**
 * Create profile histogram in 1D. It is used to
 * show the mean values in each bin of a second variable.
 * To show it, convert it to the usual H1D histogram (getH1D method).
 * You can specify errors as errors on the mean or spread RMS.
 * 
 * @author S.Chekanov
 * 
 */
public class HProf1D  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Profile1D h1;

	private IAxis axis;

	private double min;

	private double max;

	private int bins;
	
	private double[] edges;

	private String title;
	
	

 /**
         * Build 1D histogram. 
         * Default constructor. Does not do anything.  
         */
        public HProf1D() {
              
                this.title = "NOT SET";
        }



	/**
	 * Build 1D profile histogram
	 * 
	 * @param title
	 *            Title
	 * @param bins
	 *            Number of bins
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 */
	public HProf1D(String title, int bins, double min, double max) {

	
		this.title = title;
		this.bins = bins;
		this.min = min;
		this.max = max;
		axis = new FixedAxis(this.bins, this.min, this.max);
		h1 = new Profile1D(this.title, this.title, axis);
	}








	/**
	 * Create 1D profile histogram with variable bin size.
	 * 
	 * @param title
	 *            Title of histogram.
	 * @param edges
	 *            edges
	 */
	public HProf1D(String title, double[] edges) {

		
		this.edges = edges;
	        this.title = title;
		this.bins = edges.length - 1;
		this.min = edges[0];
		this.max = edges[edges.length-1];
		axis=new VariableAxis(edges);
		h1 = new Profile1D(this.title, this.title, axis);
	}

	
	/**
	 * Create 1D histogram with variable bin size.
	 * 
	 * @param title
	 *            Title of histogram.
	 * @param edges
	 *            edges
	 */
	public HProf1D(String title, IAxis axis) {

	
	    this.title = title;
		this.axis=axis;
		min=axis.lowerEdge();
		max=axis.upperEdge();
		bins=axis.bins();
		h1 = new Profile1D(this.title, this.title, axis);
	}

	
	
	
	
	/**
	 * Create H1D histogram from JAIDA Histogram1D class.
	 * 
	 * @param h1
	 *            Profile1D histogram from JAIDA
	 */

	public HProf1D(Profile1D h1) {

		
		this.h1 = h1;
		this.title=h1.title();
		this.axis = h1.axis();
		this.min = axis.lowerEdge();
		this.max = axis.upperEdge();
		this.bins = axis.bins();
	}

	/**
	 * Create profile histogram from JAIDA  class
	 * 
	 * @param h1
	 *            IProfile1D histogram from JAIDA
	 */

	public HProf1D(IProfile1D h1) {

		
		this.h1 = (Profile1D) h1;
		this.title=h1.title();
		this.axis = h1.axis();
		this.min = axis.lowerEdge();
		this.max = axis.upperEdge();
		this.bins = axis.bins();
	}

      /**
         * Create profile histogram  from another instance. 
         *  
         * @param title 
         *              new title
         *  
         * @param h1d 
         *          input H1D 
         */

        public HProf1D(String title, HProf1D h1d) {

              
                this.title=title;
                this.axis = h1d.getAxis();
                this.min = axis.lowerEdge();
                this.max = axis.upperEdge();
                this.bins = axis.bins();
                this.h1 = h1d.get(); 
        }






	/**
	 * Print histogram on screen
	 * 
	 */
	public void print() {

		DecimalFormat dfb = new DecimalFormat("##.#####E00");
		Date dat = new Date();
		String today = String.valueOf(dat);
		IAxis axis = h1.axis();
		System.out.println("");
		System.out.println("# DataMelt: output from H1D: " + this.title);
		System.out.println("# DataMelt: created at " + today);
		System.out.println("# x,  y,  error(upper),  error(lower)");
		System.out.println("#");
		for (int i = 0; i < axis.bins(); i++) {
			String x = dfb.format(h1.binMean(i)); // The weighted mean of a
			// bin.
			String y = dfb.format(h1.binHeight(i));
			String y1 = dfb.format(h1.binError(i));
			String y2 = dfb.format(h1.binError(i));
			System.out.println(x + "    " + y + "    " + y1 + "    " + y2);
		}

	}

	/**
	 * Print a H1D histogram to a Table in a separate frame. The numbers are
	 * formatted to scientific format. One can sort and search the data in this
	 * table (cannot be modified).
	 */
    /*
	public void toTable() {

		new HTable(this);

	}
   */
	
	/**
	 * Fill histogram from two  P0D arrays
	 * 
	 * @param p0d
	 *            input P0D array
	 * @param p1d
	 *            input P1D           
	 */

	public void fillP0D(P0D p0d, P0D p1d) {

		for (int i = 0; i < p0d.size(); i++)
			h1.fill(p0d.get(i), p1d.get(i));

	}

	/**
	 * Get histogram Axis class.
	 * 
	 * @return Axis used for this histogram.
	 */

	public IAxis getAxis() {

		return axis;

	}

	/**
	 * Get bin centres.
	 * 
	 * @param index
	 *            bin index
	 * @return bin centre.
	 */

	public double binCenter(int index) {

		return axis.binCenter(index);

	}

	/**
	 * Get all bin centers in form of array
	 * 
	 * @return double[] array of bin centers
	 */

	public double[] binCenters() {

		double[] tmp = new double[bins];
		for (int i = 0; i < bins; i++)
			tmp[i] = axis.binCenter(i);

		return tmp;
	}

	/**
	 * Get bin lower edge.
	 * 
	 * @param index
	 *            bin index
	 * @return lower edge
	 */

	public double binLowerEdge(int index) {

		return axis.binLowerEdge(index);

	}

	/**
	 * Get all lower edges in form of array
	 * 
	 * @return double[] array of low edges
	 */

	public double[] binLowerEdges() {

		double[] tmp = new double[bins];
		for (int i = 0; i < bins; i++)
			tmp[i] = axis.binLowerEdge(i);

		return tmp;
	}

	/**
	 * Get bin upper edge.
	 * 
	 * @param index
	 *            bin index
	 * @return upper edge
	 */

	public double binUpperEdge(int index) {

		return axis.binUpperEdge(index);

	}

	/**
	 * Get all upper edges in form of array
	 * 
	 * @return double[] array of upper edges
	 */

	public double[] binUpperEdges() {

		double[] tmp = new double[bins];
		for (int i = 0; i < bins; i++)
			tmp[i] = axis.binUpperEdge(i);

		return tmp;
	}

	/**
	 * Print the profile  histogram to a Table in a separate Frame. One can sort and
	 * search the data in this table (but not modify)
	 */

	public void toTable() {

		new HTable(this.getH1D(title));

	}
    
	
	/**
	 * Write the profile  histogram to a file
	 * 
	 * @param name
	 *            File name
	 */

	public void toFile(String name) {

		DecimalFormat dfb = new DecimalFormat("##.#####E00");
		Date dat = new Date();
		String today = String.valueOf(dat);
		IAxis axis = h1.axis();

		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);
			tx.println("# DataMelt: output from H1D " + this.title);
			tx.println("# DataMelt: created at " + today);
			tx.println("# x,  y,  error(upper),  error(lower)");
			tx.println("#");
			for (int i = 0; i < axis.bins(); i++) {
				String x = dfb.format(h1.binMean(i)); // The weighted mean of
				// a bin.
				String y = dfb.format(h1.binHeight(i));
				String y1 = dfb.format(h1.binError(i));
				String y2 = dfb.format(h1.binError(i));
				tx.println(x + "    " + y + "    " + y1 + "    " + y2);
			}
			f1.close();
		} catch (IOException e) {
			ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	
	
	/**
	 * Create profile  histogram from JAIDA  histogram class
	 * 
	 * @param h1t
	 *            TProfile histogram from JAIDA
	 */
	public HProf1D(TProfile h1t) {

		this.title = h1t.getTitle();
		setTitle(this.title);

		TAxis axis = h1t.getXaxis();
		this.min = axis.getXmin();
		this.max = axis.getXmax();
        h1=Converter.convert(h1t,this.title);
     
	}

	/**
	 * Sets the content of  histogram. Start from 1 to bins+2.
	 * 
	 * @param values
	 *            array with values in Y (dimension: bins + 2)
	 * @param errors
	 *            array with errors on Y (dimension: bins + 2)
	 */
	public void setContents(double[] values, double[] errors) {
		h1.setContents(values, errors, null, null, null);
	}

	
	  /**
     * Set the content of the whole Histogram at once. This is a convenience method for saving/restoring
     * Histograms. Of the arguments below the heights array cannot be null. The errors array should in
     * general be non-null, but this depends on the specific binner.
     * The entries array can be null, in which case the entry of a bin is taken to be the integer part
     * of the height.
     * If the means array is null, the mean is defaulted to the geometric center of the bin.
     * If the rms array is null, the rms is taken to be the bin width over the root of 12.
     *
     * @param heights The bins heights
     * @param errors The bins errors
     * @param entries The bin entries.
     * @param means The means of the bins.
     * @param rmss The rmss of the bins
     *
     */
    public void setContents(double[] heights, double[] errors, int[] entries, double[] means, double[] rmss ) {
    	h1.setContents(heights, errors, entries, means, rmss);
    }
	
	
	/**
	 * Sets the Mean and RMS of H1D histogram
	 * 
	 * @param mean
	 *            mean of the histogram
	 * @param rms
	 *            RMS to be set
	 */
	public void setMeanAndRms(double mean, double rms) {
		h1.setMean(mean);
		h1.setRms(rms);
	}

	/**
	 * Sets number of entries of the  histogram.
	 * 
	 * @param entries
	 *            Number of entries
	 */
	public void setNEntries(int entries) {
		h1.setNEntries(entries);
	}

	/**
	 * Sets number of valid entries.
	 * 
	 * @param entries
	 *            Number of valid entries
	 */
	public void setValidEntries(int entries) {
		h1.setValidEntries(entries);
	}

	/**
	 * Sets the mean
	 * 
	 * @param mean
	 *            mean
	 */
	/*
	 * public void setMean(double mean) { h1.setMean(mean); }
	 */

	/**
	 * Sets the title
	 * 
	 * @param title
	 *            Title
	 */
	public void setTitle(String title) {
		this.title = title;

	}

	/**
	 * get Title of the histogram
	 * 
	 * @return Title of histogram
	 */

	public String getTitle() {
		return this.title;

	}

	/**
	 * Get JAIDA histogram
	 * 
	 * @return JAIDA Profile1D histogram
	 */
	public Profile1D get() {

		return h1;

	}

	/**
	 * Set Min value of axis
	 * 
	 * @param min
	 *            Minimum value of axis
	 */
	public void setMin(double min) {
		this.min = min;

	}

	/**
	 * Get Minimum value of the axis
	 * 
	 * @return Minimum value of the axis
	 */

	public double getMin() {
		return this.min;
	}

	/**
	 * Set Maximum value of axis
	 * 
	 * @param max
	 *            Maximum value of axis
	 */

	public void setMax(double max) {
		this.min = max;

	}

	/**
	 * Get Maximum value of axis
	 * 
	 * @return Maximum value of axis
	 */

	public double getMax() {
		return this.max;

	}

	/**
	 * Sets the number of bins
	 * 
	 * @param bins
	 *            Number of bins
	 */

	public void setBins(int bins) {
		this.bins = bins;

	}

	/**
	 * Get the number of bins
	 * 
	 * @return Number of bins
	 */

	public int getBins() {
		return this.bins;

	}


 /**
 *  Get bin width in case of fixed-size bins. 
 *        
 *  @return bin width (max-min) /bins  
 **/
        public double getBinSize() {
                return (this.max - this.min) / this.bins;
        }


        
        
        /**
         * Shift all bins by some value
         * 
         * @param d
         *         parameter used to shift bins
         */
        
     public void  shift(double d) {
    	 
    	 int ibins = h1.axis().bins()+2;
         double[] newHeights = new double[ibins];
         double[] newErrors  = new double[ibins];
         double[] newMeans   = new double[ibins];
         double[] newRmss    = new double[ibins];
         int[]    newEntries = new int   [ibins];
         
         newHeights[0]= getUnderflowHeight();
         newHeights[ibins - 1] = getOverflowlowHeight();

         for(int i=0; i<ibins-1;i++) {
             newHeights[i+1] = h1.binHeight(i);
             newErrors [i+1] = h1.binError(i);
             newEntries[i+1] = h1.binEntries(i);
             newMeans  [i+1] = h1.binMean(i)+d; // shift by d
             newRmss   [i+1] = h1.binRms(i);
         }
         
         
         if (axis.isFixedBinning() ) {
     		axis = new FixedAxis(this.bins, min+d, max+d);
     		
     	} else {
     		for (int i=0; i<edges.length; i++) {
     		 edges[i]=edges[i]+d;	
     		 axis=new VariableAxis(edges);	
     		}
     		
     	}
         
         // set it back with new binning
         double m=h1.mean()+d;
         double r=h1.rms();
         h1 = new Profile1D(this.title, this.title, axis);
         h1.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
         setMeanAndRms( m, r );
         
    	
    	
     
     }
        
     
     
     
     
     
     /**
      * Return true if bins have constant bin width
      * @return true if bin width is fixed
      */
     
     public boolean isFixedBinning() {
       		 return axis.isFixedBinning();
     }
        
        
        
    
     /**
      * Fill profile histogram with X and Y
      * @param x value in X
      * @param y value in Y
      */

	public void fill(double x, double y) {

		h1.fill(x,y);

	}

	
	 /**
     * Fill profile histogram with X and Y
     * @param x value in X
     * @param y value in Y
     * @param weight
     *             weight for entry
     */
	public void fill(double x, double y, double weight) {

		h1.fill(x,y, weight);

	}
	

	/**
	 * Fill histogram with array of double values. Take into account weights.
	 * make sure that both arrays have the same length;
	 * @param x
	 *            array of double X values
	 * @param y
	 *            array of double  Y values
	 */

	public void fill(double[] x, double[] y) {

		if (x.length != y.length ) {
			
        System.out.println("Wrong length for arrays!");			
		} else {
		for (int i = 0; i < x.length; i++)
			h1.fill(x[i], y[i]);

		}
	}

	
	/**
	 * Fill histogram with array of double values. Take into account weights.
	 * make sure that both arrays have the same length;
	 * @param x
	 *            array of double X values
	 * @param y
	 *            array of double  Y values
	 * @param weight
	 *            arrays with weights           
	 */
	public void fill(double[] x, double[] y, double[] weight) {

		if (x.length != y.length ) {
        System.out.println("Wrong length for arrays!");			
		} else {
		for (int i = 0; i < x.length; i++)
			h1.fill(x[i], y[i], weight[i]);

		}
	}


	
	/**
	 * Get mean of the histogram
	 * 
	 * @return Mean of histogram
	 */

	public double mean() {
		return h1.mean();

	}

	/**
	 * Get RMS of histogram
	 * 
	 * @return RMS of histogram
	 */

	public double rms() {
		return h1.rms();

	}

	/**
	 * Get number of all entries
	 * 
	 * @return Number of all entries
	 */

	public int allEntries() {
		return h1.allEntries();

	}

	/**
	 * Number of under and overflow entries in the histogram.
	 * 
	 * @return Number of under and overflow entries
	 */

	public int extraEntries() {
		return h1.extraEntries();

	}

	/**
	 * Number of in-range entries in the histogram
	 * 
	 * @return Number of in-range entries
	 */

	public int entries() {
		return h1.entries();

	}

	/**
	 * Number of underflow entries
	 * 
	 * @return Number of underflow
	 */

	public int getUnderflow() {
		return h1.binEntries(IAxis.UNDERFLOW_BIN);

	}

	/**
	 * Underflow height
	 * 
	 * @return height of underflow
	 */

	public double getUnderflowHeight() {
		return h1.binHeight(IAxis.UNDERFLOW_BIN);

	}

	/**
	 * Overflow height
	 * 
	 * @return Height of underflow
	 */

	public double getOverflowlowHeight() {
		return h1.binHeight(IAxis.OVERFLOW_BIN);

	}

	/**
	 * Number of overflow entries
	 * 
	 * @return Number of overflow entries
	 */

	public int getOverflow() {
		return h1.binEntries(IAxis.OVERFLOW_BIN);

	}

	

	/**
	 * Fill histogram with a value, weighting it by inverse of bin size. Should
	 * be useful for histograms with irregular binning.
	 * 
	 * @param value
	 *            Value to filled histogram with.
	 */
	public void fillInvBinSizeWeight(double value) {

		int binNum = h1.axis().coordToIndex(value);
		h1.fill(value, 1.0 / h1.axis().binWidth(binNum));

	}

	

	
	

	/**
	 * Scale the histogram.
	 * 
	 * @param title
	 *            New title
	 * @param scaleFactor
	 *            Scale factor
	 */

	public void  scale(String title, double scaleFactor) {

		this.title = title;
		h1.scale(scaleFactor);
	}

/**
	 * Scale the histogram. 
	 * 
	 * @param scaleFactor
	 *            Scale factor
	 */

	public void  scale(double scaleFactor) {

		h1.scale(scaleFactor);
	}

	/**
	 * Smooth the histogram.
	 * <p>
	 * Each band of the histogram is smoothed by averaging over a moving window
	 * of a size specified by the method parameter: if the value of the
	 * parameter is <i>k</i> then the width of the window is <i>2*k + 1</i>.
	 * If the window runs off the end of the histogram only those values which
	 * intersect the histogram are taken into consideration. The smoothing may
	 * optionally be weighted to favor the central value using a "triangular"
	 * weighting. For example, for a value of <i>k</i> equal to 2 the central
	 * bin would have weight 1/3, the adjacent bins 2/9, and the next adjacent
	 * bins 1/9. Errors are kept the same as before.
	 * 
	 * @param isWeighted
	 *            Whether bins will be weighted using a triangular weighting
	 *            scheme favoring bins near the central bin.
	 * @param k
	 *            The smoothing parameter which must be non-negative. If zero,
	 *            the histogram object will be returned with no smoothing
	 *            applied.
	 * @return A smoothed version of the histogram.
	 */

	public HProf1D operSmooth(boolean isWeighted, int k) {

		SHisto sh = new SHisto(bins, min, max, 1);

		double[] hh = binHeights();
		double[] ee = binErrors();

		sh.setBins(hh);
		sh = sh.getSmoothed(isWeighted, k);

		double[] hh1 = new double[bins + 2];
		double[] ee1 = new double[bins + 2];

		hh1[0] = getUnderflowHeight();
		hh1[bins - 1] = getOverflowlowHeight();
		for (int i = 1; i < bins + 1; i++) {
			hh1[i] = sh.getBinsFirstBand(i - 1);
			ee1[i] = ee[i - 1];
			// System.out.println(hh1[i]+ " " + ee1[i]);
		}

		h1.setContents(hh1, ee1, null, null, null);
		return this;
	}

	/**
	 * Computes a Gaussian smoothed version of the histogram.
	 * 
	 * <p>
	 * Each band of the histogram is smoothed by discrete convolution with a
	 * kernel approximating a Gaussian impulse response with the specified
	 * standard deviation.
	 * 
	 * @param standardDeviation
	 *            The standard deviation of the Gaussian smoothing kernel which
	 *            must be non-negative or an
	 *            <code>IllegalArgumentException</code> will be thrown. If
	 *            zero, the histogram object will be returned with no smoothing
	 *            applied.
	 * @return A Gaussian smoothed version of the histogram.
	 * 
	 */
	public HProf1D operSmoothGauss(double standardDeviation) {

		SHisto sh = new SHisto(bins, min, max, 1);

		double[] hh = binHeights();
		double[] ee = binErrors();

		sh.setBins(hh);
		sh = sh.getGaussianSmoothed(standardDeviation);

		double[] hh1 = new double[bins + 2];
		double[] ee1 = new double[bins + 2];

		hh1[0] = getUnderflowHeight();
		hh1[bins - 1] = getOverflowlowHeight();
		for (int i = 1; i < bins + 1; i++) {
			hh1[i] = sh.getBinsFirstBand(i - 1);
			ee1[i] = ee[i - 1];
			// System.out.println(hh1[i]+ " " + ee1[i]);
		}

		h1.setContents(hh1, ee1, null, null, null);

		return this;
	}

	/**
	 * Returns the entropy of the histogram.
	 * 
	 * <p>
	 * The entropy is defined to be the negation of the sum of the products of
	 * the probability associated with each bin with the base-2 log of the
	 * probability.
	 * 
	 * @return The entropy of the histogram.
	 * 
	 */
	public double getEntropy() {
		SHisto sh = new SHisto(bins, min, max, 1);
		double[] hh = binHeights();
		sh.setBins(hh);
		double s[] = sh.getEntropy();
		return s[0];
	}

	/**
	 * Make a copy of the data holder
	 * 
	 * @return New data holder
	 */
	public HProf1D copy() {
		return copy(this.title);

	}

	/**
	 * Get exact copy of the current histogram. This means it makes a new
	 * object.
	 * 
	 * @param newtitle
	 *            New title
	 * @return a new copy of the histogram
	 */
	public HProf1D copy(String newtitle) {


		
		 int ibins = bins+2;
         double[] newHeights = new double[ibins];
         double[] newErrors  = new double[ibins];
         double[] newMeans   = new double[ibins];
         double[] newRmss    = new double[ibins];
         int[]    newEntries = new int   [ibins];
         
         newHeights[0]= getUnderflowHeight();
         newHeights[ibins - 1] = getOverflowlowHeight();
         
         for(int i=0; i<ibins-1;i++) {
             newHeights[i+1] = h1.binHeight(i);
             newErrors [i+1] = h1.binError(i);
             newEntries[i+1] = h1.binEntries(i);
             newMeans  [i+1] = h1.binMean(i);
             newRmss   [i+1] = h1.binRms(i);
         }
			
         HProf1D hnew = new HProf1D(newtitle, axis);
         hnew.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
         hnew.setMeanAndRms( h1.mean(), h1.rms() );
		
		
		// get copy
		hnew.setNEntries(entries());
		hnew.setMeanAndRms(mean(), rms());

		return hnew;
	}


	
	
	/**
	 * Make H1D copy of the current histogram.
	 * Default graphical attributes are assumed.
	 * 
	 * @return a new H1D copy of the histogram
	 */
	public H1D  getH1D() {
         return getH1D(title);
	}
	
	
	
	
	
	/**
	 * Convert to H1D for graphical representation using
	 * default graphics attributes. The errors on points
	 * are shown as error o the mean. 
	 * 
	 * @param newtitle
	 *            New title
	 * @return a new H1D copy of the histogram
	 */
	public H1D  getH1D(String newtitle) {
         return getH1D(newtitle,"mean");
	}


/**
	 * Convert to H1D for graphical representation using
	 * default graphics attributes. 
	 * 
	 * @param newtitle
	 *            New title
	 * @param  option
	 *             if option is "s", spread is shown as error
	 * @return a new H1D copy of the histogram
	 */
	public H1D  getH1D(String newtitle, String option) {
		
	     int ibins = bins+2;
         double[] newHeights = new double[ibins];
         double[] newErrors  = new double[ibins];
         double[] newMeans   = new double[ibins];
         double[] newRmss    = new double[ibins];
         int[]    newEntries = new int   [ibins];
         
         newHeights[0]= getUnderflowHeight();
         newHeights[ibins - 1] = getOverflowlowHeight();
         
         for(int i=0; i<ibins-1;i++) {
             newHeights[i+1] = h1.binHeight(i);
             newErrors [i+1] = h1.binError(i);
             if (option == "s" || option == "S")  newErrors [i+1] = h1.binRms(i);
             newEntries[i+1] = h1.binEntries(i);
             newMeans  [i+1] = h1.binMean(i);
             newRmss   [i+1] = h1.binRms(i);
         }
			
         H1D hnew = new H1D(newtitle, axis);
         hnew.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
         hnew.setMeanAndRms( h1.mean(), h1.rms() );
  	     hnew.setNEntries(entries());
	     hnew.setMeanAndRms(mean(), rms());
    	return hnew;
	}



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Number of entries in a bin with index i
	 * 
	 * @param index
	 *            Bin index
	 * @return Number of entries
	 */

	public int binEntries(int index) {

		return (int)h1.binEntries(index);

	}

/**
	 * Get all entries of the histogram as an array
	 * 
	 * @return array with histogram entries.
	 */
	public int[] binEntries() {

		int[] hh = new int[bins];
		for (int i = 0; i < bins; i++)
			hh[i] = (int)h1.binEntries(i);
		return hh;
	}

	/**
	 * Error on the bin with index i
	 * 
	 * @param index
	 *            Bin index
	 * @return Error
	 */

	public double binError(int index) {

		return h1.binError(index);

	}

	/**
	 * Height of the corresponding bin
	 * 
	 * @param index
	 *            Bin index
	 * @return Bin Height
	 */

	public double binHeight(int index) {

		return h1.binHeight(index);

	}

	/**
	 * Get all heights of the histogram as an array
	 * 
	 * @return array with histogram heights.
	 */
	public double[] binHeights() {

		double[] hh = new double[bins];
		for (int i = 0; i < bins; i++)
			hh[i] = h1.binHeight(i);
		return hh;
	}

	/**
	 * Get all bin errors of the histogram as an array
	 * 
	 * @return array with histogram errors.
	 */
	public double[] binErrors() {

		double[] hh = new double[bins];
		for (int i = 0; i < bins; i++)
			hh[i] = h1.binError(i);
		return hh;
	}

	/**
	 * Mean in a single bin with index
	 * 
	 * @param index
	 *            Bin index
	 * @return Mean value inside of this bin
	 */

	public double binMean(int index) {

		return h1.binMean(index);

	}

	/**
	 * RMS of a single bin with index
	 * 
	 * @param index
	 *            Bin index
	 * @return RMS of this bin
	 */

	public double binRms(int index) {

		return h1.binRms(index);

	}

	/**
	 * Max value of all bins
	 * 
	 * @return Max value
	 */

	public double maxBinHeight() {

		return h1.maxBinHeight();

	}

	/**
	 * Min value of all bins
	 * 
	 * @return Min value
	 */
	public double minBinHeight() {

		return h1.minBinHeight();

	}

	/**
	 * Sum of all heights
	 * 
	 * @return Sum of all Heights
	 */

	public double sumAllBinHeights() {

		return h1.sumAllBinHeights();

	}


	/**
	 * Get positions in X,Y and Errors. For x, we take mean values
	 * 
	 * @param mode
	 *            if =1, take center of the bins. In all other cases - mean
	 *            value in each bin
	 * 
	 * @return array with X,Y, and Error on Y, double[3][bins];
	 */
	public double[][] getValues(int mode) {

		// create the array of references of size 3
		double[][] nums = new double[3][];
		// this create the second level of arrays (red squares)
		for (int i = 0; i < 3; i++)
			nums[i] = new double[bins]; // create arrays of integers

		for (int i = 0; i < bins; i++) {
			nums[0][i] = h1.binMean(i);
			if (mode == 1)
				nums[0][i] = binCenter(i);
			nums[1][i] = h1.binHeight(i);
			nums[2][i] = h1.binError(i);
		}
		return nums;
	}

	
	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */
	private void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
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
