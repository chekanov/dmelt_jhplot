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

import root.*;
import hep.aida.*;
import hep.aida.ref.histogram.*;
import hep.io.root.interfaces.TH2;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import cern.jet.random.AbstractDistribution;
import jhplot.gui.HelpBrowser;



/**
 * Histogram in two dimensions (2D). 
 * Main class to create a 2D histogram. Each bin in 2D is characterized by 2 indexes, i and j.
 * This class is a direct extension of the H1D used for 1D case.
 * Use the get() method to  access JAIDA Histogram2D histogram class.
 * 
 * @author S.Chekanov
 * 
 */

public class H2D extends DrawOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Histogram2D h1;

	private double minX;

	private double maxX;

	private double minY;

	private double maxY;

	private int binsX;

	private int binsY;

//	private int region = 0;

	private IAxis xAx;

	private IAxis yAy;


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
	public H2D(String title, int binsX, double minX, double maxX, int binsY,
			double minY, double maxY) {

		is3D=true;
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
		h1 = new Histogram2D(this.title, this.title, xAx, yAy);

	}

	/**
	 * Create H2D histogram from JAIDA IHistogram2D class
	 * 
	 * @param h1
	 *            IHistogram2D histogram from JAIDA
	 */
	public H2D(IHistogram2D h1) {
		setStyle("h");
		is3D=true;
		this.h1 = (Histogram2D) h1;
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
	 * Create H2D histogram from Cloud2D.
	 * 
	 * @param c2d
	 *            Cloud2D
	 * @param binX number of bins in X
	 * 
	 * @param binY number of bins in Y          
	 */
	public H2D(Cloud2D c2d, int binX, int binY) {
		setStyle("h");
		is3D=true;
		this.title = c2d.title();
		setTitle(c2d.title());
		this.minX = c2d.lowerEdgeX();
		this.maxX = c2d.upperEdgeX();
		this.minY = c2d.lowerEdgeY();
		this.maxY = c2d.upperEdgeY();
		this.binsX = binX;
		this.binsY = binY;
		xAx = new FixedAxis(this.binsX, this.minX, this.maxX);
	 	yAy = new FixedAxis(this.binsY, this.minY, this.maxY);
		h1 = new Histogram2D(this.title, this.title, xAx, yAy);
        fill(c2d);
	}

	
	
	/**
	 * Create a 2D histogram using variable size bins in X and Y
	 * 
	 * @param title
	 *            - bin titles
	 * @param edgesX
	 *            - array with bin edges in X
	 * @param edgesY
	 *            - array with bin edges in Y
	 * */

	public H2D(String title, double[] edgesX, double[] edgesY) {
		
		is3D=true;
		this.title = title;
		this.binsX = edgesX.length - 1;
		this.binsY = edgesY.length - 1;
		this.minX = edgesX[0];
		this.maxX = edgesX[edgesX.length - 1];
		this.minY = edgesY[0];
		this.maxY = edgesY[edgesY.length - 1];
		xAx = new VariableAxis(edgesX);
		yAy = new VariableAxis(edgesY);
		h1 = new Histogram2D(this.title, this.title, xAx, yAy);
	}

	/**
	 * Define H2D in terms of axis
	 * 
	 * @param title
	 *            title
	 * @param xAx
	 *            Axis for X
	 * @param yAy
	 *            Axis for Y
	 */

	public H2D(String title, IAxis xAx, IAxis yAy) {
		is3D=true;
		this.title = title;
		this.binsX = xAx.bins();
		this.minX = xAx.lowerEdge();
		this.maxX = xAx.upperEdge();
		this.binsY = yAy.bins();
		this.minY = yAy.lowerEdge();
		this.maxY = yAy.upperEdge();

		h1 = new Histogram2D(this.title, this.title, xAx, yAy);

	}

	/**
	 * Create a H2D histogram from JAIDA histogram
	 * 
	 * @param h1
	 *            Histogram2D histogram
	 */
	public H2D(Histogram2D h1) {
		
		is3D=true;
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
	 * Get the JAIDA Histogram2D
	 * 
	 * @return Histogram2D
	 */
	public Histogram2D get() {

		return h1;

	}

	
	
	/**
	 * Create H2D histogram from JAIDA TH1 histogram class
	 * 
	 * @param h1t
	 *            TH1 histogram from JAIDA
	 */
	public H2D(TH2 h2t) {

		h1 = Converter.convert(h2t, h2t.getTitle());

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
		h1.setContents(heights, errors, null, null, null, null, null);
	}

	/**
	 * Set the content of the whole Histogram at once. This is a convenience
	 * method for saving/restoring Histograms. Of the arguments below the
	 * heights array cannot be null. The errors array should in general be
	 * non-null, but this depends on the specific binner. The entries array can
	 * be null, in which case the entry of a bin is taken to be the integer part
	 * of the height. If the means array is null, the mean is defaulted to the
	 * geometric center of the bin. If the rms array is null, the rms is taken
	 * to be the bin width over the root of 12.
	 * 
	 * 
	 * @param heights
	 *            The bin heights
	 * @param errors
	 *            The bin errors
	 * @param entries
	 *            The bin entries
	 * @param meanXs
	 *            The means of the bin along the x axis
	 * @param rmsXs
	 *            The rmss of the bin along the x axis
	 * @param meanYs
	 *            The means of the bin along the y axis
	 * @param rmsYs
	 *            The rmss of the bin along the y axis
	 * 
	 */
	public void setContents(double[][] heights, double[][] errors,
			int[][] entries, double[][] meanXs, double[][] rmsXs,
			double[][] meanYs, double[][] rmsYs) {
		h1.setContents(heights, errors, entries, meanXs, rmsXs, meanYs, rmsYs);
	}

	/**
	 * Fill H2D histogram assuming all weights are unity.
	 * 
	 * @param value1
	 *            value in X
	 * @param value2
	 *            value in Y
	 */
	public void fill(double value1, double value2) {

		h1.fill(value1, value2);

	}


             /** Fill the histogram with random numbers from Gaussian (Normal) distribution.
     * Seed is taken from time. 
     * @param TotNumber  number generated events
     * @param meanX mean of the gaussian in X 
     * @param sdX   standard deviation in X 
     * @param meanY mean of the gaussian in Y 
     * @param sdY   standard deviation in Y 
     */
       public void fillGauss(int TotNumber, double meanX, double sdX,  double meanY, double sdY) {
            java.util.Random random = new  java.util.Random();
            for (int i = 0; i < TotNumber; i++)
                    h1.fill(sdX*random.nextGaussian()+meanX, sdY*random.nextGaussian()+meanY);

    }

     /** Fill the histogram with random numbers from fralt distribution.
     * Seed is taken from time. 
     * Using mean=0 and width=1 will give a flat distribution between 0 and 1.  
     * @param TotNumber  number generated events
     * @param meanX mean of the distribution in X 
     * @param widthX width of the distribution  in X
     * @param meanY mean of the distribution in Y 
     * @param widthY width of the distribution  in Y 
     */
       public void fillRnd(int TotNumber, double meanX, double widthX, double meanY, double widthY) {
             java.util.Random random = new  java.util.Random();
            for (int i = 0; i < TotNumber; i++)
                    h1.fill(widthX*random.nextDouble()+meanX, widthY*random.nextDouble()+meanY);

    }


       /**
     * Fill the histogram with random numbers.
     * Random generators are taken from cern.jet.random.*.
     * Examples: Beta, Binominal, Poisson, BreitWigner,ChiSquare,Empirical
     * Exponential, Gamma, Hyperbolic, Logarithmic, Normal, NegativeBinomial
     * 
     * @param TotNumber  number generated events
     * @param random1 generator for X
     * @param random1 generator for Y
     */

    public void fill(int TotNumber, AbstractDistribution random1, AbstractDistribution random2) {
            for (int i = 0; i < TotNumber; i++)
                    h1.fill(random1.nextDouble(),random2.nextDouble());

    }

	/**
	 * Set the error on this bin.
	 * 
	 * @param indexX
	 *            the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
	 * @param indexY
	 *            the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
	 * @param error
	 *            the error.
	 */
	public void setBinError(int indexX, int indexY, double error) {
		h1.setBinError(indexX, indexY, error);
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
	 * Various manipulations with histograms (+,-,*,/). Note: no new object will
	 * be created.
	 * 
	 * @param a
	 *            H2D histogram.
	 * @param title
	 *            New Title
	 * 
	 * @return same H2D object but modified
	 */

	public H2D oper(H2D a, String title, String what) {

		IAnalysisFactory af = IAnalysisFactory.create();
		IHistogramFactory hf = af.createHistogramFactory(af.createTreeFactory()
				.create());

		// first check them
		if (what.equals("+")) {
			IHistogram2D hnew = hf.add(title, get(), a.get());
			return new H2D(hnew);
		}

		if (what.equals("-")) {
			IHistogram2D hnew = hf.subtract(title, get(), a.get());
			return new H2D(hnew);
		}

		if (what.equals("*")) {
			IHistogram2D hnew = hf.multiply(title, get(), a.get());
			return new H2D(hnew);
		}

		if (what.equals("/")) {
			IHistogram2D hnew = hf.divide(title, get(), a.get());
			return new H2D(hnew);
		}

		ErrorMessage("Operation \"" + what + "\" is not implemented");

		return this;

	}

	/**
	 * Scale the histogram.
	 * 
	 * @param title
	 *            New title
	 * @param scaleFactor
	 *            Scale factor
	 */

	public void scale(String title, double scaleFactor) {

		this.title = title;
		h1.scale(scaleFactor);
	}

	/**
	 * Scale the histogram and return scaled object.
	 * 
	 * @param title
	 *            New title
	 * @param scaleFactor
	 *            Scale factor
	 **/
	public H2D operScale(String title, double scaleFactor) {
		scale(title, scaleFactor);
		return this;
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
	 * Calculate complete statistics for this histogram for the X or Y
	 * Unlike other methods (like mean or rms), it performs calculations
	 * on the existing histogram, thus the method is somewhat slow.
	 * It return mean, error on the mean, RMS, variance, standard deviation. <p>
	 * The key for the output <b>map are: mean, error, rms, variance, stddev etc. Print the keys of this
	 * map to get the full access to statistics</b>.
	 * 
	 * 
	 * @return map representing histogram statistics
	 */
	public  Map<String,Double> getStat(){
		
		
		Histogram2D h=get();
		
	
		Map<String,Double> tmp= new  HashMap<String,Double>();
	
		
		
		tmp.put("meanX", h.meanX());
		tmp.put("meanY", h.meanY());
		tmp.put("rmsX",  h.rmsX());
		tmp.put("rmsY",  h.rmsX());
		tmp.put("entries",  (double)h.allEntries());
		
		return tmp;
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

	public void clear() {
		h1.reset();
	}

	
	
	/**
	 * Get bin heights as 2D array. Note 0 index bin means 1st bin etc.
	 * @return bin heights as 2D array
	 */
	public double[][] binHeights(){
		binsX = h1.xAxis().bins();
		binsY = h1.yAxis().bins();
		double[][] newHeights = new double[binsX][binsY];
		for (int i = 0; i < binsX; i++) {
			for (int j = 0; j < binsY; j++) {
				newHeights[i][j] = h1.binHeight(i, j);
			}
		}
		
		return newHeights;
	}
	
	
	/**
	 * Get errors on heights as 2D array. Note 0 index bin means 1st bin etc.
	 * @return errors on heights as 2D array
	 */
	public double[][] binErrors(){
		binsX = h1.xAxis().bins();
		binsY = h1.yAxis().bins();
		double[][] errors = new double[binsX][binsY];
		for (int i = 0; i < binsX; i++) {
			for (int j = 0; j < binsY; j++) {
				errors[i][j] = h1.binError(i, j);
			}
		}
		
		return errors;
	}
	
	/**
	 * Get mean position for X axis as 2D array. Note 0 index bin means 1st bin etc.
	 * @return X mean positions as 2D array
	 */
	public double[][] binMeansX(){
		binsX = h1.xAxis().bins();
		binsY = h1.yAxis().bins();
		double[][] xx = new double[binsX][binsY];
		for (int i = 0; i < binsX; i++) {
			for (int j = 0; j < binsY; j++) {
				xx[i][j] = h1.binMeanX(i, j);
			}
		}
		return xx;
	}
	
	/**
	 * Get lower edges for X bins as 1D array. Note 0 index bin means 1st bin etc.
	 * @return lower edges for X positions as 1D array
	 */
	public double[] getLowerEdgesX(){
		binsX = h1.xAxis().bins();
		double[] xx = new double[binsX];
		for (int i = 0; i < binsX; i++) {
				xx[i] = h1.xAxis().binLowerEdge(i);
			}
		return xx;
	}
	
	
	
	/**
	 * Get lower edges for Y bins as 1D array. Note 0 index bin means 1st bin etc.
	 * @return lower edges for Y positions as 1D array
	 */
	public double[] getLowerEdgesY(){
		binsY = h1.yAxis().bins();
		double[] xx = new double[binsY];
		for (int i = 0; i < binsY; i++) {
				xx[i] = h1.yAxis().binLowerEdge(i);
			}
		return xx;
	}
	
	/**
	 * Get mean position for Y axis as 2D array. Note 0 index bin means 1st bin etc.
	 * @return Y mean positions as 2D array
	 */
	public double[][] binMeansY(){
		binsX = h1.xAxis().bins();
		binsY = h1.yAxis().bins();
		double[][] xx = new double[binsX][binsY];
		for (int i = 0; i < binsX; i++) {
			for (int j = 0; j < binsY; j++) {
				xx[i][j] = h1.binMeanY(i, j);
			}
		}
		return xx;
	}
	
	
	
	/**
	 * Get bin mean position at the location (i,i)
	 * @param i bin position in X
	 * @param j bin position in Y 
	 * @return bin mean in X and Y (2D array).
	 */
	public double[] binMeans(int i, int j){
		double [] center = new double[2];
		center[0] = h1.binMeanX(i,j);
		center[1] = h1.binMeanY(i,j);
		return center;
	}
	
	
	
	
	/**
	 * Get exact copy of the current histogram. This means it makes a new
	 * object.
	 * 
	 * @param newtitle
	 *            New title
	 * @return a new copy of the histogram
	 */

	public H2D copy(String newtitle) {
		xAx = h1.xAxis();
		yAy = h1.yAxis();
		this.binsX = xAx.bins();
		this.binsY = yAy.bins();
		
		int ibinsX = binsX + 2;
		int ibinsY = binsY + 2;

		double[][] newHeights = new double[ibinsX][ibinsY];
		double[][] newErrors = new double[ibinsX][ibinsY];
		double[][] newMeansX = new double[ibinsX][ibinsY];
		double[][] newRmssX = new double[ibinsX][ibinsY];
		double[][] newMeansY = new double[ibinsX][ibinsY];
		double[][] newRmssY = new double[ibinsX][ibinsY];
		int[][] newEntries = new int[ibinsX][ibinsY];

		newHeights[0][0] = getUnderflowHeightX();
		newHeights[ibinsX - 1][ibinsY - 1] = getOverflowHeightY();

		for (int i = 0; i < ibinsX - 1; i++) {
			for (int j = 0; j < ibinsY - 1; j++) {
				newHeights[i + 1][j + 1] = h1.binHeight(i, j);
				newErrors[i + 1][j + 1] = h1.binError(i, j);
				newEntries[i + 1][j + 1] = h1.binEntries(i, j);
				newMeansX[i + 1][j + 1] = h1.binMeanX(i, j);
				newRmssX[i + 1][j + 1] = h1.binRmsX(i, j);
				newMeansY[i + 1][j + 1] = h1.binMeanY(i, j);
				newRmssY[i + 1][j + 1] = h1.binRmsY(i, j);
			}
		}

		H2D hnew = new H2D(newtitle, xAx, yAy);
		hnew.setContents(newHeights, newErrors, newEntries, newMeansX,
				newMeansY, newRmssX, newRmssY);
		hnew.setMeanX(h1.meanX());
		hnew.setMeanY(h1.meanX());
		hnew.setRmsX(h1.rmsX());
		hnew.setRmsY(h1.rmsY());
		hnew.setNEntries(entries());
		return hnew;
	}

	/**
	 * Get a density distribution dividing each bin of the histogram by the bin
	 * width and the total number of heights for all bins.
	 * 
	 * @return density distribution
	 */
	public H2D getDensity() {

		int ibinsX = binsX + 2;
		int ibinsY = binsY + 2;

		double[][] newHeights = new double[ibinsX][ibinsY];
		double[][] newErrors = new double[ibinsX][ibinsY];
		double[][] newMeansX = new double[ibinsX][ibinsY];
		double[][] newRmssX = new double[ibinsX][ibinsY];
		double[][] newMeansY = new double[ibinsX][ibinsY];
		double[][] newRmssY = new double[ibinsX][ibinsY];
		int[][] newEntries = new int[ibinsX][ibinsY];

		newHeights[0][0] = getUnderflowHeightX();
		newHeights[ibinsX - 1][ibinsY - 1] = getOverflowHeightY();
		double sum = sumAllBinHeights();

		for (int i = 0; i < ibinsX -1; i++) {
			double w1 = sum * (xAx.binUpperEdge(i) - xAx.binLowerEdge(i));
			for (int j = 0; j < ibinsY - 1; j++) {
				double ww = w1*(yAy.binUpperEdge(j) - yAy.binLowerEdge(j));
				newHeights[i + 1][j + 1] = h1.binHeight(i, j) / ww;
				newErrors[i + 1][j + 1] = h1.binError(i, j) / ww;
				newEntries[i + 1][j + 1] = h1.binEntries(i, j);
				newMeansX[i + 1][j + 1] = h1.binMeanX(i, j);
				newRmssX[i + 1][j + 1] = h1.binRmsX(i, j);
				newMeansY[i + 1][j + 1] = h1.binMeanY(i, j);
				newRmssY[i + 1][j + 1] = h1.binRmsY(i, j);
			}
		}

		H2D hnew = new H2D(getTitle(),xAx, yAy);
		hnew.setContents(newHeights, newErrors, newEntries, newMeansX,
				newMeansY, newRmssX, newRmssY);
		hnew.setMeanX(h1.meanX());
		hnew.setMeanY(h1.meanY());
		hnew.setRmsX(h1.rmsX());
		hnew.setRmsY(h1.rmsY());
		hnew.setNEntries(entries());
		return hnew;
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
	 * Fill H2D histogram with weights
	 * 
	 * @param value1
	 *            value in X
	 * @param value2
	 *            value in Y
	 * @param weight
	 *            weight
	 */
	public void fill(double value1, double value2, double weight) {
		h1.fill(value1, value2, weight);
	}

	/**
	 * Fill H2D histogram from 2 arrays (X amd Y). Assume weights =1.
	 * 
	 * @param value1
	 *            array with values in X
	 * @param value2
	 *            array with values in Y
	 */
	public void fill(double[] value1, double[] value2) {

		if (value1.length != value2.length) {
		ErrorMessage("Different size of input arrays");
		return; };
		
		for (int i = 0; i < value1.length; i++) h1.fill(value1[i], value2[i]);

		

	}

	
	
	
	/**
	 * Fill H2D histogram from Cloud2D. 
	 * 
	 * @param c2d input Cloud2D
	 *            array with values in X
	 *            
	 */
	public void fill(Cloud2D c2d) {

		for (int i = 0; i <c2d.entries(); i++) 
			h1.fill(c2d.valueX(i), c2d.valueY(i), c2d.weight(i));
		

	}
	
	
	
	
	
	
	

        /**
         * Fill H2D histogram from 2 arrays. Assume weights =1.
         * 
         * @param value1
         *            array with values in X
         * @param value2
         *            array with values in Y
         */
        public void fill(P0D value1, P0D value2) {

        	if (value1.size() != value2.size()) {
        		ErrorMessage("Different size of input arrays");
        		return; };
        	
                for (int i = 0; i < value1.size(); i++)  h1.fill(value1.getQuick(i), value2.getQuick(i));
                 

        }

 /**
         * Fill H2D histogram from 2 arrays. Assume weights =1.
         * 
         * @param value1
         *            array with values in X
         * @param value2
         *            array with values in Y
         */
        public void fill(P0I value1, P0I value2) {
        	if (value1.size() != value2.size()) {
        		ErrorMessage("Different size of input arrays");
        		return; };
        		
        		 for (int i = 0; i < value1.size(); i++)  h1.fill(value1.getQuick(i), value2.getQuick(i));
        	

        }






	/**
	 * Fill H2D histogram from arrays. Weights are defined by the third array. Sizes of the arrays must be the same.
	 * 
	 * @param value1
	 *            array with values in X
	 * @param value2
	 *            array with values in Y
	 * @param weights
	 *            weights
	 */
	public void fill(double[] value1, double[] value2, double[] weights) {

		if (value1.length != value2.length) {
			ErrorMessage("Different size of input arrays");
			return;
		}
		
		if (value1.length != weights.length) {
			ErrorMessage("Different size of input weight");
			return;
		}
		
		for (int i = 0; i < value1.length; i++) {
			h1.fill(value1[i], value2[i], weights[i]);

		}

	}

	/**
	 * Convert a coordinate X on the axis to a bin number
	 * 
	 * @param x
	 *            coordinate on axis
	 * @return index of the corresponding bin
	 */
	public int findBinX(double x) {
		return xAx.coordToIndex(x);

	}

	/**
	 * Convert a coordinate Y on the axis to a bin number
	 * 
	 * @param y
	 *            coordinate on axis
	 * @return index of the corresponding bin
	 */
	public int findBinY(double y) {
		return yAy.coordToIndex(y);

	}

	/**
	 * Integrate histogram in the range. The integral is computed as the sum of
	 * bin contents in the range.
	 * 
	 * @param xMin
	 *            Min value for X integration (included to integration)
	 * @param xMax
	 *            Max index for X integration (included to integration)
	 * @param yMin
	 *            Min index for Y integration (included to integration)
	 * @param yMax
	 *            Max index for Y integration (included to integration)
	 *@param timesBinWidth
	 *            If true, the integral is the sum of the bin contents
	 *            multiplied by the bin width in x.
	 * 
	 * @return integral (sum of all heights)
	 */

	public double integralRange(double xMin, double xMax, double yMin,
			double yMax, boolean timesBinWidth) {

		int i1 = findBinX(xMin);
		int i2 = findBinX(xMax);
		int i3 = findBinY(yMin);
		int i4 = findBinY(yMax);
		return integral(i1, i2, i3, i4, false);
	}

	/**
	 * Return q probability distribution derived from a histogram. The histogram
	 * is scaled by the sum of all contents.
	 * 
	 * @return probability distribution
	 */

	public H2D getProbability() {
		H2D h2d = this.copy(getTitle());
		h2d.scale(1.0 / h2d.sumAllBinHeights());
		return h2d;

	}

	/**
	 * Make a copy of the data holder
	 * 
	 * @return New data holder
	 */
	public H2D copy() {
		return copy(getTitle());

	}

	/**
	 * Scale the histogram.
	 * 
	 * @param scaleFactor
	 *            Scale factor
	 */

	public void scale(double scaleFactor) {

		h1.scale(scaleFactor);
	}

	/**
	 * Integrate histogram between two indices. The integral is computed as the
	 * sum of bin contents in the range.
	 * 
	 * @param BinMinX
	 *            Min index for X integration (included to integration, start
	 *            from 1)
	 * @param BinMaxX
	 *            Max index for X integration (included to integration)
	 * @param BinMinY
	 *            Min index for Y integration (included to integration, start
	 *            from 1)
	 * @param BinMaxY
	 *            Max index for Y integration (included to integration)
	 * 
	 * @return integral (sum of all heights)
	 */

	public double integral(int BinMinX, int BinMaxX, int BinMinY, int BinMaxY) {

		return integral(BinMinX, BinMaxX, BinMinY, BinMaxY, false);
	}

	/**
	 * Integrate histogram between two indices. The integral is computed as the
	 * sum of bin contents in the range if the last parameter is false. If it is
	 * true, he integral is the sum of the bin contents multiplied by the bin
	 * width in x.
	 * 
	 * @param BinMinX
	 *            Min index for X integration (included to integration, start
	 *            from 1)
	 * @param BinMaxX
	 *            Max index for X integration (included to integration)
	 * @param BinMinY
	 *            Min index for Y integration (included to integration, start
	 *            from 1)
	 * @param BinMaxY
	 *            Max index for Y integration (included to integration)
	 *@param timesBinWidth
	 *            If true, the integral is the sum of the bin contents
	 *            multiplied by the bin width in x.
	 * 
	 * @return integral (sum of all heights)
	 */

	public double integral(int BinMinX, int BinMaxX, int BinMinY, int BinMaxY,
			boolean timesBinWidth) {

		if (BinMinX > BinMaxX) {
			ErrorMessage("Wrong bin number for X!");
			return -1;
		}

		if (BinMinY > BinMaxY) {
			ErrorMessage("Wrong bin number for Y!");
			return -1;
		}

		int bX = xAx.bins();
		int bY = yAy.bins();

		if (BinMinX < 1 || BinMaxX > bX) {
			ErrorMessage("Wrong bin number for X!");
			return -1;
		}
		if (BinMinY < 1 || BinMaxY > bY) {
			ErrorMessage("Wrong bin number for Y!");
			return -1;
		}

		double sum = 0.0;

		if (timesBinWidth == false) {
			for (int i = BinMinX - 1; i < BinMaxX; i++) {
				for (int j = BinMinY - 1; j < BinMaxY; j++) {
					sum += h1.binHeight(i, j);

				}
			}

		} else {

			for (int i = BinMinX - 1; i < BinMaxX; i++) {
				double w1 = xAx.binUpperEdge(i) - xAx.binLowerEdge(i);
				for (int j = BinMinY - 1; j < BinMaxY; j++) {
					double w2 = yAy.binUpperEdge(j) - yAy.binLowerEdge(j);
					sum += (h1.binHeight(i, j) * w1 * w2);

				}
			}

		}
		return sum;
	}



         /**
         * Compare the histogram with a function. The comparison tests  hypotheses that
         * the histogram represent identical distribution with a function using Pearson's chi-squared test. 
         * The number chi2/ndf gives the estimate (values close to 1 indicates
         * similarity between 2 histograms.). the function and histogram are identical if chi2=0.
         * Chi2/ndf and p-value probability is 1. Maken sure that  statistical errors are included correctly. 
         * Data with zero errors will be ignored.  
         * @param f1 
         *            function to compare to.  
         * @return map with the result. It gives Chi2, gives number
         *         of degrees of freedom (ndf), probability
         *         ("quality", or p-value).
         */

           public Map<String,Double> compareChi2(F2D f1) {

                Map<String,Double> tmp= new  HashMap<String,Double>();

                int bins1x = get().xAxis().bins();
                int bins1y = get().yAxis().bins();

                double sum1 = 0;
                double nDf = 0;

                 for (int i = 0; i < bins1x; i++) {
                     double xx1=get().xAxis().binLowerEdge(i);
                     double xx2=get().xAxis().binUpperEdge(i);
                     double d1=xx2-xx1;
                     double xx=xx1+0.5*d1;
                        for (int j = 0; j < bins1y; j++) {
                        double y1=get().yAxis().binLowerEdge(j);
                        double y2=get().yAxis().binUpperEdge(j);
                        double d2=y1-y2;
                        double yy=y1+0.5*d2;

                        double bin1 = binHeight(i,j);
                        double e1 = binError(i,j);
                        double ff=f1.eval(xx,yy);
                        if (e1 != 0) {
                               sum1=sum1+((ff-bin1)*(ff-bin1) / (e1*e1));
                               nDf++;
                        }
                }}

                double chi2=sum1;
                tmp.put("chi2", chi2);
                tmp.put("ndf", (double)nDf);

                org.apache.commons.math3.distribution.ChiSquaredDistribution chi2Distribution = new org.apache.commons.math3.distribution.ChiSquaredDistribution(
                                nDf);
                double prob = chi2Distribution.cumulativeProbability(chi2);
                tmp.put("p-value",  1.0-prob);
                return tmp;

         }












	/**
	 * Compare two 2D  histograms. Comparison of two histograms test hypotheses that
	 * two histograms represent identical distributions. It calculates Chi2
	 * between 2 histograms taking into account errors on the heights of the
	 * bins. The number chi2/ndf gives the estimate (values close to 1 indicates
	 * similarity between 2 histograms.) Two histograms are identical if chi2=0.
	 * Chi2/ndf]. Probability (p-value) is
	 * 1. Make sure that both histograms have error (or set them to
	 * small values).
	 * 
	 * @param h2
	 *            second histogram to compare
	 * @return results. It gives Chi2,  the  number
	 *         of degrees of freedom (ndf), and probability
	 *         ("quality", or p-value).
	 */
	 public Map<String,Double> compareChi2(H2D h2) {

                 Map<String,Double> tmp= new  HashMap<String,Double>();

		int bins1x = get().xAxis().bins();
		int bins2x = h2.get().xAxis().bins();

		if (bins1x != bins2x) {
			System.out
					.println("Different histograms! Please use histograms with the same bin numbers in X");
			return tmp;
		}

		int bins1y = get().yAxis().bins();
		int bins2y = h2.get().yAxis().bins();

		if (bins1y != bins2y) {
			System.out
					.println("Different histograms! Please use histograms with the same bin numbers in Y");
			return tmp;
		}
		
		
		double chi2 = 0;
		int nDf = 0;

		double sum1 = 0;
		double sum2 = 0;
		double sumw1 = 0;
		double sumw2 = 0;

		for (int i = 0; i < bins1x; i++) {
			for (int j = 0; j < bins1y; j++) {
						
			double bin1 = binHeight(i,j);
			double bin2 = h2.binHeight(i,j);
			double e1 = binError(i,j);
			double e2 = h2.binError(i,j);

			if (e1 > 0) {
				bin1 *= bin1 / (e1 * e1);

			} else
				bin1 = 0;

			if (e2 > 0) {
				bin2 *= bin2 / (e2 * e2);
			} else
				bin2 = 0;

			// sum contents
			sum1 += bin1;
			sum2 += bin2;
			sumw1 += e1 * e1;
			sumw2 += e2 * e2;

		}
	}

		//double sum = sum1 + sum2;

		if (sumw1 <= 0 || sumw2 <= 0) {
			System.out
					.println("Cannot compare histograms with all zero errors");
			return tmp;
		}

		if (sum1 == 0 || sum2 == 0) {
			System.out.println("One histogram is empty!");
			return tmp;
		}

		for (int i = 0; i < bins1x; i++) {
			for (int j = 0; j < bins1y; j++) {
			double bin1 = binHeight(i,j);
			double bin2 = h2.binHeight(i,j);
			double e1 = binError(i,j);
			double e2 = h2.binError(i,j);
			
			// System.out.println(Double.toString(bin1)+" - "+Double.toString(bin2));

			if (e1 > 0)
				bin1 *= bin1 / (e1 * e1);
			else
				bin1 = 0;

			if (e2 > 0)
				bin2 *= bin2 / (e2 * e2);
			else
				bin2 = 0;

			double binsum = bin1 + bin2;
			double delta = sum2 * bin1 - sum1 * bin2;

			if (binsum > 0) {
				chi2 += delta * delta / binsum;
				System.out.println(chi2);
				nDf++;
			}

		}}

		chi2 /= (sum1 * sum2);
                tmp.put("chi2", chi2);
                tmp.put("ndf", (double)nDf);
 
                org.apache.commons.math3.distribution.ChiSquaredDistribution chi2Distribution = new org.apache.commons.math3.distribution.ChiSquaredDistribution(
                                nDf);
                double prob = chi2Distribution.cumulativeProbability(chi2);
                tmp.put("p-value",  1.0-prob);


		return tmp;

	}
	
	

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */
	private void ErrorMessage(String a) {
              jhplot.utils.Util.ErrorMessage(a);
	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

}
