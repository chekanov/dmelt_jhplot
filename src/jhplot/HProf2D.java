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

import hep.aida.*;
import hep.aida.ref.histogram.*;
import java.io.*;

import jhplot.gui.HelpBrowser;

/**
 * Profile  histogram in 2D.
 * The profile histogram is used to show the mean value in each bin 
 * as a function of a second variable. Use getH2D() method
 * for plotting. Points can have errors shown as error on mean or RMS.
 * 
 * Based on JAIDA class Profile2D.
 * 
 * @author S.Chekanov
 * 
 */

public class HProf2D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Profile2D h1;

	private double minX;

	private double maxX;

	private double minY;

	private double maxY;

	private int binsX;

	private int binsY;
	
	private IAxis xAx;
	
	private IAxis yAy; 

	private int region = 0;

	private String title;

	/**
	 * Create 2D histogram
	 * 
	 * @param title
	 *            Title
	 * @param binsX
	 *            Number of bins in X
	 * @param minX
	 *            Min value in X
	 * @param maxX
	 *            Max value in X
	 * @param binsY
	 *            Number of bins in Y
	 * @param minY
	 *            Min value in Y
	 * @param maxY
	 *            Max value in Y
	 */
	public HProf2D(String title, int binsX, double minX, double maxX, int binsY,
			double minY, double maxY) {

		this.title = title;
		this.binsX = binsX;
		this.minX = minX;
		this.maxX = maxX;
		this.binsY = binsY;
		this.minY = minY;
		this.maxY = maxY;

		/*
		 * // cannot treat different binnings yet int Ibin = this.binsX; if
		 * (this.binsY > this.binsX) Ibin = this.binsY; this.binsX = Ibin;
		 * this.binsY = Ibin;
		 */
		xAx = new FixedAxis(this.binsX, this.minX, this.maxX);
	    yAy = new FixedAxis(this.binsY, this.minY, this.maxY);
		h1 = new Profile2D(this.title, this.title, xAx, yAy);

	}

	
	
	

	/**
	 * Create 2D profile histogram with variable bin size.
	 * 
	 * @param title
	 *            Title of histogram.
	 * @param edgesX
	 *            edges for X
	 * @param edgesY
	 *            edges for Y          
	 */
	public HProf2D(String title, double[] edgesX, double[] edgesY ) {

		
		
	    this.title = title;
		
	    this.binsX = edgesX.length - 1;
		this.minX = edgesX[0];
		this.maxX = edgesX[edgesX.length-1];
	
		this.binsY = edgesY.length - 1;
		this.minY = edgesY[0];
		this.maxY = edgesX[edgesY.length-1];
	
		xAx=new VariableAxis(edgesX);
		yAy=new VariableAxis(edgesY);
		
		h1 = new Profile2D(this.title, this.title, xAx, yAy);
	}
	
	

	
	
	
	
	/**
	 * Create a H2D histogram from JAIDA histogram
	 * 
	 * @param h1
	 *            Histogram2D histogram
	 */
	public HProf2D(Profile2D h1) {
		this.h1 = h1;
		setTitle(h1.title());
		xAx = h1.xAxis();
		yAy = h1.yAxis();
		this.minX = xAx.lowerEdge();
		this.maxX = xAx.upperEdge();
		this.minY = yAy.lowerEdge();
		this.maxY = yAy.upperEdge();
		this.binsX = xAx.bins();
		this.binsY = yAy.bins();
	}

	/**
	 * Set title
	 * @param title title
	 */
	public void setTitle(String title) {
		
		this.title = title;
		
	}
	/**
	 * Get the JAIDA Profile2D
	 * 
	 * @return Profile2D
	 */
	public Profile2D get() {

		return h1;

	}

	
	
	
	/**
	 * Make H2D copy of the current histogram.
	 * Default graphical attributes are assumed.
	 * Errors for points show errors on mean.
	 * 
	 * @param newtitle
	 *            New title
	 * @return  H2D histogram
	 */
	public H2D  getH2D(String newtitle) {
		return getH2D(newtitle,"mean"); 
	}
	
	
	
	/**
	 * Make H2D copy of the current histogram.
	 * Default graphical attributes are assumed.
	 * 
	 * @param newtitle
	 *            New title
	 * @param option
	 *              id option is "s", spread (RMS) is used as error
	 * @return  H2D histogram
	 */
	public H2D  getH2D(String newtitle, String option) {

		
	        int xbins = xAx.bins()+2;
	        int ybins = yAy.bins()+2;
	        double[][] newHeights = new double[xbins][ybins];
	        double[][] newErrors  = new double[xbins][ybins];
	        double[][] newMeanXs  = new double[xbins][ybins];
	        double[][] newRmsXs   = new double[xbins][ybins];
	        double[][] newMeanYs  = new double[xbins][ybins];
	        double[][] newRmsYs   = new double[xbins][ybins];   
	        int[][] newEntries = new    int[xbins][ybins];
	      
	        
	        
	        /*
	        for(int i=0; i<xbins-1;i++) {
	        	for(int j=0; j<ybins-1;j++) {
	             newHeights[i+1][j+1] = h1.binHeight(i,j);
	             newErrors [i+1][j+1] = h1.binError(i,j);
	             newEntries[i+1][j+1] = h1.binEntries(i,j);
	             newMeanXs[i+1][j+1] = h1. binMeanX(i,j); 
	             newMeanYs[i+1][j+1] = h1.binMeanY(i,j);  
	             newRmsXs[i+1][j+1]  = h1.binRms(i,j);
	             newRmsYs[i+1][j+1]  = h1.binRms(i,j);
	        	}  
	        	}
		      */
	        
	        
	     //    for(int i=IAxis.UNDERFLOW_BIN; i<=xAx.bins()+1; i++) {
	     //       for(int j=IAxis.UNDERFLOW_BIN; j<=yAy.bins()+1; j++) {
	     //       	 int binx = mapBinNumber(i,xAx);
	      //         int biny = mapBinNumber(j,yAy);
	     
	        for (int i = 0; i < xbins-1; i++) {
				for (int j = 0; j < ybins-1; j++) {
	       
					int binx = i+1;
					int biny = j+1;
					// int binx = mapBinNumber(i+1,xAx);
				    // int biny = mapBinNumber(j+1,yAy);
	                     newHeights[binx][biny] = h1.binHeight(i,j);
		             newErrors [binx][biny] = h1.binError(i,j);
		             if (option == "s" || option == "S")  newErrors [binx][biny] = h1.binRms(i,j);
		             newEntries[binx][biny] = h1.binEntries(i,j);        
		             newMeanXs[binx][biny] = h1. binMeanX(i,j); 
		             newMeanYs[binx][biny] = h1.binMeanY(i,j);  
		             newRmsXs[binx][biny]  = h1.binRms(i,j);
		             newRmsYs[binx][biny]  = h1.binRms(i,j);
	            }
	        }
	       
	        
	        
	     
         H2D hnew = new H2D(newtitle, xAx, yAy);
         hnew.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs);
         hnew.setMeanX( h1.meanX());
         hnew.setMeanY( h1.meanY());
         hnew.setRmsX( h1.rmsX());
         hnew.setRmsY( h1.rmsY());
    	 hnew.setNEntries(entries());

         return hnew;
	}

	
	
	/**
	 * Convert to H2D keeping the original title
	 * 
	 * @return H2D histogram
	 */
	public H2D  getH2D() {
		
     return getH2D(title);
		
	}
	
	
	/**
	 * Set RMS on Y
	 * 
	 * @param rmsY
	 *            RMS on Y
	 */
	public void setRmsY(double rmsY) {
		h1.setRmsY(rmsY);
	}

	/**
	 * Set mean on Y
	 * 
	 * @param mean
	 *            on Y
	 */
	public void setMeanY(double mean) {
		h1.setMeanY(mean);
	}

	/**
	 * Get RMS on Y
	 */
	public double getRmsY() {
		return h1.rmsY();
	}

	/**
	 * Get mean on Y
	 */
	public double getMeanY() {
		return h1.meanY();
	}

	/**
	 * Get mean on X.
	 */
	public double getMeanX() {
		return h1.meanX();
	}

	/**
	 * Get RMS on X
	 */
	public double getRmsX() {
		return h1.rmsX();
	}

	/**
	 * Set all entries.
	 * 
	 * @param entries
	 *            entries
	 */
	public void setNEntries(int entries) {
		h1.setNEntries(entries);
	}

	/**
	 * Set in-range entries.
	 * 
	 * @param entries
	 *            entries
	 */
	public void setValidEntries(int entries) {
		h1.setValidEntries(entries);
	}

	/**
	 * Set RMS on X.
	 */
	public void setRmsX(double rmsX) {
		h1.setRmsX(rmsX);
	}

	/**
	 * Set mean on X.
	 * 
	 * @param mean
	 *            on X
	 */
	public void setMeanX(double mean) {
		h1.setMeanX(mean);
	}

	/**
	 * Set content of H2D histogram
	 * 
	 * @param heights
	 *            heights
	 * @param errors
	 *            errors
	 */
	public void setContents(double[][] heights, double[][] errors) {
		h1.setContents(heights, errors, null, null, null, null);
	}

	/**
	 * Fill  histogram assuming all weights are unity.
	 * 
	 * @param x
	 *            value in X
	 * @param y
	 *            value in Y
	 * @param z
	 *             value in Z           
	 */
	public void fill(double x, double y, double z) {

		h1.fill(x,y,z);

	}

	/**
	 * Fill  histogram assuming all weights are unity.
	 * 
	 * @param x
	 *            value in X
	 * @param y
	 *            value in Y
	 * @param z
	 *             value in Z   
	 * @param w
	 *             weight                    
	 */
	public void fill(double x, double y, double z, double w) {

		h1.fill(x,y,z,w);

	}
	
	
	/**
	 * Total height of the corresponding bin.
	 * 
	 * @param indexX
	 *            The x bin number in the external representation: (0...N-1) or
	 *            OVERFLOW or UNDERFLOW.
	 * @param indexY
	 *            The y bin number in the external representation: (0...N-1) or
	 *            OVERFLOW or UNDERFLOW.
	 * @return The bin height for the corresponding bin.
	 * 
	 */
	public double binHeight(int indexX, int indexY) {
		return h1.binHeight(indexX, indexY);
	}

	/**
	 * Get the number of entries in the underflow and overflow bins.
	 * 
	 * @return The number of entries outside the range of the Histogram.
	 * 
	 */
	public int extraEntries() {
		return h1.extraEntries();
	}

	/**
	 * Get the sum of the bin heights for all the entries outside the
	 * Histogram's range.
	 * 
	 * @return The sum of the out of range bin's heights.
	 * 
	 */
	public double sumExtraBinHeights() {
		return h1.sumExtraBinHeights();
	}

	/**
	 * Get the sum of the bin heights for all the entries, in-range and
	 * out-range ones.
	 * 
	 * @return The sum of all the bin's heights.
	 * 
	 */
	public double sumAllBinHeights() {
		return h1.sumAllBinHeights();
	}

	/**
	 * Get the number of bins in X
	 * 
	 * @return binsX number of bins in X
	 */
	public int getBinsX() {

		return h1.xAxis().bins();
	}

	/**
	 * Get the number of bins in Y
	 * 
	 * @return binsY number of bins in Y
	 */
	public int getBinsY() {
		return h1.yAxis().bins();
	}

	/**
	 * Get min value of axis in X
	 * 
	 * @return min value of X axis
	 */
	public double getMinX() {
		return h1.xAxis().lowerEdge();
	}

	/**
	 * Get max value of axis in X
	 * 
	 * @return max value of X axis
	 */
	public double getMaxX() {
		return h1.xAxis().upperEdge();
	}

	/**
	 * Get max value of Y axis
	 * 
	 * @return max value of Y axis
	 */
	public double getMaxY() {
		return h1.yAxis().upperEdge();
	}

	/**
	 * Get min value of Y axis
	 * 
	 * @return min value of X axis
	 */
	public double getMinY() {
		return h1.yAxis().lowerEdge();
	}

	/**
	 * Get underflow entries in Y
	 * 
	 * @return underflow in Y
	 */
	public int getUnderflowEntriesY() {
		return h1.binEntriesY(IAxis.UNDERFLOW_BIN);
	}

	/**
	 * Get underflow height in Y
	 * 
	 * @return underflow height in Y
	 */
	public double getUnderflowHeightY() {
		return h1.binHeightY(IAxis.UNDERFLOW_BIN);
	}

	/**
	 * Get underflow height in X
	 * 
	 * @return underflow height in X
	 */
	public double getUnderflowHeightX() {
		return h1.binHeightX(IAxis.UNDERFLOW_BIN);
	}

	/**
	 * Get underflow entries in X
	 * 
	 * @return underflow in X
	 */
	public int getUnderflowEntriesX() {
		return h1.binEntriesY(IAxis.UNDERFLOW_BIN);
	}

	/**
	 * Get overflow entries in Y
	 * 
	 * @return overflow in Y
	 */
	public int getOverflowEntriesY() {
		return h1.binEntriesY(IAxis.OVERFLOW_BIN);
	}

	/**
	 * Get overflow height in Y
	 * 
	 * @return overflow in Y
	 */
	public double getOverflowHeightY() {
		return h1.binHeightY(IAxis.OVERFLOW_BIN);
	}

	/**
	 * Get overflow entries in Y
	 * 
	 * @return overflow in Y
	 */
	public int getOverflowEntriesX() {
		return h1.binEntriesX(IAxis.OVERFLOW_BIN);
	}

	/**
	 * Get overflow entries in Y
	 * 
	 * @return overflow in Y
	 */
	public double getOverflowHeightX() {
		return h1.binHeightX(IAxis.OVERFLOW_BIN);
	}

	/**
	 * Get lower edge of the bin in X
	 * 
	 * @param index
	 *            of the bin
	 * @return lower edge of the bin
	 */
	public double getLowerEdgeX(int index) {
		return h1.xAxis().binLowerEdge(index);

	}

	/**
	 * Get upper edge of the bin in X
	 * 
	 * @param index
	 *            of the bin
	 * @return lower edge of the bin
	 */
	public double getUpperEdgeX(int index) {
		return h1.xAxis().binUpperEdge(index);

	}

	/**
	 * Get upper edge of the bin in Y
	 * 
	 * @param index
	 *            of the bin
	 * @return lower edge of the bin
	 */
	public double getUpperEdgeY(int index) {
		return h1.yAxis().binUpperEdge(index);

	}

	/**
	 * Get lower edge of the bins in Y
	 * 
	 * @param index
	 *            of the bin
	 * @return lower edge of the bin
	 */
	public double getLowerEdgeY(int index) {
		return h1.yAxis().binLowerEdge(index);

	}

	/**
	 * Get underflow heights in X
	 * 
	 * @return underflow heights in Y
	 */
	public double getUnderflowHeightsX() {
		return h1.binHeightX(IAxis.UNDERFLOW_BIN);
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
	 * Number of in-range entries in the histogram
	 * 
	 * @return Number of in-range entries
	 */

	public int entries() {
		return h1.entries();

	}

	/**
	 * Get IAxis in X
	 * 
	 * @return axis in X
	 */

	public IAxis getAxisX() {
		return h1.xAxis();

	}

	/**
	 * Get IAxis in Y
	 * 
	 * @return axis in Y
	 */

	public IAxis getAxisY() {
		return h1.yAxis();

	}

	/**
	 * Number of entries in the corresponding bin (i.e. the number of times fill
	 * was called for this bin).
	 * 
	 * @param indexX
	 *            the x bin number in the external representation: (0...N-1) or
	 *            OVERFLOW or UNDERFLOW.
	 * @param indexY
	 *            the y bin number in the external representation: (0...N-1) or
	 *            OVERFLOW or UNDERFLOW.
	 * @return The number of entries for the corresponding bin.
	 * 
	 */
	public int binEntries(int indexX, int indexY) {
		return h1.binEntries(indexX, indexY);
	}

	/**
	 * Error of the corresponding bin.
	 * 
	 * @param indexX
	 *            the x bin number in the external representation: (0...N-1) or
	 *            OVERFLOW or UNDERFLOW.
	 * @param indexY
	 *            the y bin number in the external representation: (0...N-1) or
	 *            OVERFLOW or UNDERFLOW.
	 * @return errors for the corresponding bin.
	 * 
	 */
	public double binError(int indexX, int indexY) {
		return h1.binError(indexX, indexY);
	}

	

	/**
	 * Fill  histogram from 2 arrays. Assume weights =1.
	 * Make sure that all arrays have the same size.
	 * 
	 * @param x
	 *            array with values in X
	 * @param y
	 *            array with values in Y
	 * @param z
	 *           array wih values in z            
	 */
	public void fill(double[] x, double[] y, double[] z) {

		for (int i = 0; i <x.length; i++) {
			for (int j = 0; j < x.length; j++)
				h1.fill(x[i], y[j], z[i]);

		}

	}

	

	/**
	 * Fill  histogram from 2 arrays. Assume weights =1.
	 * Make sure that all arrays have the same size.
	 * 
	 * @param x
	 *            array with values in X
	 * @param y
	 *            array with values in Y
	 * @param z
	 *           array with values in z
	 * @param w
	 *           array with weights                      
	 */
	public void fill(double[] x, double[] y, double[] z, double[] w) {

		for (int i = 0; i <x.length; i++) {
			for (int j = 0; j < x.length; j++)
				h1.fill(x[i], y[j], z[i], w[i]);

		}

	}
	
	 /**
     * Utility method to map the bin number from the external representation (from -2 to nBins-1 where -2 is the overflow and -1 is the underflow)
     * to the internal one (from 0 to nBins+1 where 0 is the underflow and nBins+1 if the overflow bin)
     * @param index The bin number in the external representation.
     * @param axis  The axis to which the bin belongs to.
     * @return The bin number in the internal representation.
     *
     */
    public int mapBinNumber(int index, IAxis axis) {
        int bins = axis.bins()+2;
        if (index >= bins) throw new IllegalArgumentException("bin="+index);
        if (index >= 0) return index+1;
        if (index == IAxis.UNDERFLOW_BIN) return 0;
        if (index == IAxis.OVERFLOW_BIN) return bins-1;
        throw new IllegalArgumentException("bin="+index);
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
