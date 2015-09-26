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
import cern.jet.random.AbstractDistribution;
import root.Converter;
import java.io.*;
import java.util.*;
import hep.aida.*;
import hep.aida.ref.histogram.*;
import java.text.DecimalFormat;

import jhplot.gui.HelpBrowser;
import jhplot.utils.SHisto;
import hep.io.root.interfaces.*;
import jplot.LinePars;

/**
 * Histogram in one dimension (1D). 
 * A histogram can be serialized to a file, plotted using HPlot, HPlotJa canvases.
 * Many methods to manipulate with histogram and access its statistics are also available.
 * 
 * @author S.Chekanov 
 * 
 */
public class H1D extends DrawOptions implements Serializable {

	private static final long serialVersionUID = 1L;

	private Histogram1D h1;

	private IAxis axis;

	private double min;

	private double max;

	private int bins;

	private double[] edges;


	/**
	 * Build 1D histogram. Default constructor. Does not do anything.
	 */
	public H1D() {
		setType(LinePars.H1D);
		this.title = "NOT SET";
	}

	/**
	 * Build 1D histogram
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
	public H1D(String title, int bins, double min, double max) {

		setType(LinePars.H1D);
		setStyle("h");
		setTitle(title);
		this.bins = bins;
		this.min = min;
		this.max = max;
		axis = new FixedAxis(this.bins, this.min, this.max);
		h1 = new Histogram1D(this.title, this.title, axis);

	}

	/**
	 * Create 1D histogram with variable bin size.
	 * 
	 * @param title
	 *            Title of histogram.
	 * @param edges
	 *            edges
	 */
	public H1D(String title, double[] edges) {

		setType(LinePars.H1D);
		setStyle("h");
		setTitle(title);
		this.edges = edges;
		this.bins = edges.length - 1;
		this.min = edges[0];
		this.max = edges[edges.length - 1];
		axis = new VariableAxis(edges);
		h1 = new Histogram1D(this.title, this.title, axis);
	}

	/**
	 * Create 1D histogram with variable bin size.
	 * 
	 * @param title
	 *            Title of histogram.
	 * @param edges
	 *            edges
	 */
	public H1D(String title, IAxis axis) {

		setType(LinePars.H1D);
		setStyle("h");
		setTitle(title);
		this.axis = axis;
		min = axis.lowerEdge();
		max = axis.upperEdge();
		bins = axis.bins();
		h1 = new Histogram1D(this.title, this.title, axis);
	}

	/**
	 * Create H1D histogram from JAIDA Histogram1D class.
	 * 
	 * @param h1
	 *            Histogram1D histogram from JAIDA
	 */

	public H1D(Histogram1D h1) {

		setType(LinePars.H1D);
		setStyle("h");
		this.h1 = h1;
		this.axis = h1.axis();
		this.min = axis.lowerEdge();
		this.max = axis.upperEdge();
		this.bins = axis.bins();
	}

	
	/**
	 * Create H1D histogram from JAIDA Cloud1D class.
	 * Min and Max values re determined automatically.
	 * 
	 * @param c1d
	 *            Cloud1D histogram from JAIDA
	 * @param bins
	 *             Number of bins for plotting.           
	 */

	public H1D(Cloud1D c1d, int bins) {

		setType(LinePars.H1D);
		setStyle("h");	
		setTitle(c1d.title());
		this.min = c1d.lowerEdge();
		this.max = c1d.upperEdge();
		this.bins = bins;
		axis = new FixedAxis(this.bins, this.min, this.max);
		h1 = new Histogram1D(this.title, this.title, axis);
	    fill(c1d);
	}

	
	
	
	/**
	 * Create H1D histogram from JAIDA IHistogram1D class
	 * 
	 * @param h1
	 *            IHistogram1D histogram from JAIDA
	 */

	public H1D(IHistogram1D h1) {

		setType(LinePars.H1D);
		setStyle("h");
		this.h1 = (Histogram1D) h1;
		this.title = h1.title();
		this.axis = h1.axis();
		this.min = axis.lowerEdge();
		this.max = axis.upperEdge();
		this.bins = axis.bins();
	}

	/**
	 * Create H1D from another instance.
	 * 
	 * @param title
	 *            new title
	 * 
	 * @param h1d
	 *            input H1D
	 */

	public H1D(String title, H1D h1d) {

		LinePars lnew = copyLinePars(h1d.getLineParm());
		setDrawOption(lnew);
		this.title = title;
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
	 * Convert the histogram to a string
	 * 
	 * @return string representing a histogram
	 */
	public String toString() {

		String tmp = "";

		DecimalFormat dfb = new DecimalFormat("##.#####E00");
		Date dat = new Date();
		String today = String.valueOf(dat);
		IAxis axis = h1.axis();
		tmp = tmp + "# DataMelt: output from H1D: " + this.title + "\n";
		tmp = tmp + "# DataMelt: created at " + today + "\n";
		tmp = tmp + "# x,  y,  error(upper),  error(lower)\n";
		for (int i = 0; i < axis.bins(); i++) {
			String x = dfb.format(h1.binMean(i)); // The weighted mean of a
			// bin.
			String y = dfb.format(h1.binHeight(i));
			String y1 = dfb.format(h1.binError(i));
			String y2 = dfb.format(h1.binError(i));
			tmp = tmp + x + "    " + y + "    " + y1 + "    " + y2 + "\n";
		}

		return tmp;

	}

	/**
	 * Print a H1D histogram to a Table in a separate frame. The numbers are
	 * formatted to scientific format. One can sort and search the data in this
	 * table (cannot be modified).
	 */

	public void toTable() {

		new HTable(this);

	}

	/**
	 * Fill histogram from P0D array
	 * 
	 * @param p0d
	 *            input P0D array
	 */

	public void fill(P0D p0d) {

		for (int i = 0; i < p0d.size(); i++)
			h1.fill(p0d.getQuick(i));

	}


	
     /**
     * Fill the histogram with random numbers.
     * Random generators are taken from cern.jet.random.*.
     * Examples: Beta, Binominal, Poisson, BreitWigner,ChiSquare,Empirical
     * Exponential, Gamma, Hyperbolic, Logarithmic, Normal, NegativeBinomial
     * 
     * @param TotNumber  number generated events
     * @param random generator
     */

    public void fill(int TotNumber, AbstractDistribution random) {

            for (int i = 0; i < TotNumber; i++)
                    h1.fill(random.nextDouble());

    }

     /** Fill the histogram with random numbers from Gaussian (Normal) distribution.
     * Seed is taken from time. 
     * @param TotNumber  number generated events
     * @param mean mean of the gaussian
     * @param sd   standard deviation 
     */
       public void fillGauss(int TotNumber, double mean, double sd) {
            java.util.Random random = new  java.util.Random();
            for (int i = 0; i < TotNumber; i++)
                    h1.fill(sd*random.nextGaussian()+mean);

    }

     /** Fill the histogram with random numbers from a flat distribution.
     * Seed is taken from time. 
     * Using mean=0 and width=1 will give a flat distribution between 0 and 1.  
     * @param TotNumber  number generated events
     * @param mean mean of the distribution 
     * @param width width of the distribution   
     */
       public void fillRnd(int TotNumber, double mean, double width) {
             java.util.Random random = new  java.util.Random(); 
            for (int i = 0; i < TotNumber; i++)
                    h1.fill(width*random.nextDouble()+mean);

    }
	
	
 /**
         * Fill histogram from P0I array
         * 
         * @param p0i 
         *            input P0I array
         */

        public void fill(P0I p0i) {

                for (int i = 0; i < p0i.size(); i++)
                        h1.fill((double)p0i.get(i));

        }

	/**
	 * Get histogram Axis class.
	 * 
	 * @return Axis used for this histogram.
	 */

	public IAxis getAxis() {
		axis= h1.axis();
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
		axis= h1.axis();
		return axis.binCenter(index);

	}

	/**
	 * Get all bin centers in form of array
	 * 
	 * @return double[] array of bin centers
	 */

	public double[] binCenters() {
		axis= h1.axis();
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
		axis= h1.axis();
		return axis.binLowerEdge(index);

	}

	/**
	 * Get all lower edges in form of array
	 * 
	 * @return double[] array of low edges
	 */

	public double[] binLowerEdges() {
		axis= h1.axis();
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
		axis= h1.axis();
		return axis.binUpperEdge(index);

	}

	/**
	 * Get all upper edges in form of array
	 * 
	 * @return double[] array of upper edges
	 */

	public double[] binUpperEdges() {
		axis= h1.axis();
		double[] tmp = new double[bins];
		for (int i = 0; i < bins; i++)
			tmp[i] = axis.binUpperEdge(i);

		return tmp;
	}

	

	/**
	 * Write the H1D histogram to a file
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
	 * Create H1D histogram from JAIDA TH1 histogram class
	 * 
	 * @param h1t
	 *            TH1 histogram from JAIDA
	 */
	public H1D(TH1 h1t) {

		this.title = h1t.getTitle();
		setTitle(this.title);

		TAxis axis = h1t.getXaxis();
		this.min = axis.getXmin();
		this.max = axis.getXmax();
		h1 = Converter.convert(h1t, this.title);

	}

	
	
	/**
	 * Sets the content of H1D histogram (heights). 
	 * Keep errors the same. RMS and Mean will be wrong!.
	 * 
	 * @param values
	 *            array with values in Y (dimension: bins + 2)
	 */
	public void setHeights(double[] values) {

		int ibins = bins + 2;
	    double[] newHeights = new double[ibins];
		double[] newErrors = new double[ibins];
		double[] newMeans = new double[ibins];
		double[] newRmss = new double[ibins];
		int[] newEntries = new int[ibins];

		newHeights[0] = getUnderflowHeight();
		newHeights[ibins - 1] = getOverflowlowHeight();

		for (int i = 0; i < ibins - 1; i++) {
			newHeights[i + 1] = values[i]; // h1.binHeight(i);
			newErrors[i + 1] = h1.binError(i);
			newEntries[i + 1] = h1.binEntries(i);
			newMeans[i + 1] = h1.binMean(i);
			newRmss[i + 1] = h1.binRms(i);
		}

		setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
		setMeanAndRms(h1.mean(), h1.rms());

	}
		
	/**
	 * Sets errors  of H1D histogram (for heights). 
	 * Keep values the same
	 * 
	 * @param errors
	 *            array with errors in Y (dimension: bins + 2)
	 */
	public void setErrors(double[] errors) {

		int ibins = bins + 2;
	    double[] newHeights = new double[ibins];
		double[] newErrors = new double[ibins];
		double[] newMeans = new double[ibins];
		double[] newRmss = new double[ibins];
		int[] newEntries = new int[ibins];

		newHeights[0] = getUnderflowHeight();
		newHeights[ibins - 1] = getOverflowlowHeight();

		for (int i = 0; i < ibins - 1; i++) {
			newHeights[i + 1] = h1.binHeight(i);
			newErrors[i + 1] = errors[i]; // h1.binError(i);
			newEntries[i + 1] = h1.binEntries(i);
			newMeans[i + 1] = h1.binMean(i);
			newRmss[i + 1] = h1.binRms(i);
		}

		setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
		setMeanAndRms(h1.mean(), h1.rms());

	}
		
		
	
	
	
	
	
	/**
	 * Sets the content of H1D histogram. Start from 1 to bins+2.
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
	 * Set the content of the whole Histogram at once. This is a convenience
	 * method for saving/restoring Histograms. Of the arguments below the
	 * heights array cannot be null. The errors array should in general be
	 * non-null, but this depends on the specific binner. The entries array can
	 * be null, in which case the entry of a bin is taken to be the integer part
	 * of the height. If the means array is null, the mean is defaulted to the
	 * geometric center of the bin. If the rms array is null, the rms is taken
	 * to be the bin width over the root of 12.
	 * 
	 * @param heights
	 *            The bins heights
	 * @param errors
	 *            The bins errors
	 * @param entries
	 *            The bin entries.
	 * @param means
	 *            The means of the bins.
	 * @param rmss
	 *            The rmss of the bins
	 * 
	 */
	public void setContents(double[] heights, double[] errors, int[] entries,
			double[] means, double[] rmss) {
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
		h1.setMeanAndRms(mean, rms);
	}

	/**
	 * Sets number of entries of H1D histogram.
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
	 * Get JAIDA histogram
	 * 
	 * @return JAIDA Histogram1D histogram
	 */
	public Histogram1D get() {

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
		this.max = max;

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
		
		this.bins = h1.axis().bins();
		return this.bins;

	}

	/**
	 * Get bin width in case of fixed-size bins.
	 * 
	 * @return bin width (max-min) /bins
	 **/
	public double getBinSize() {
		
		axis = h1.axis();
		bins=axis.bins();
		min=axis.lowerEdge();
		max=axis.upperEdge();
		
		return (max - min) / bins;
	}

	/**
	 * Get bin width for index i
	 * 
	 * @param index of this bin 
	 * 
	 * @return bin width at index 
	 **/
	public double getBinSize(int index) {
		
		axis = h1.axis();
		return axis.binWidth(index);
	}
	
	
	/**
	 * Shift all bins by some value
	 * 
	 * @param d
	 *            parameter used to shift bins
	 */

	public void shift(double d) {

		int ibins = h1.axis().bins() + 2;
		double[] newHeights = new double[ibins];
		double[] newErrors = new double[ibins];
		double[] newMeans = new double[ibins];
		double[] newRmss = new double[ibins];
		int[] newEntries = new int[ibins];

		newHeights[0] = getUnderflowHeight();
		newHeights[ibins - 1] = getOverflowlowHeight();

		for (int i = 0; i < ibins - 1; i++) {
			newHeights[i + 1] = h1.binHeight(i);
			newErrors[i + 1] = h1.binError(i);
			newEntries[i + 1] = h1.binEntries(i);
			newMeans[i + 1] = h1.binMean(i) + d; // shift by d
			newRmss[i + 1] = h1.binRms(i);
		}

		if (axis.isFixedBinning()) {
			axis = new FixedAxis(this.bins, min + d, max + d);

		} else {
			for (int i = 0; i < edges.length; i++) {
				edges[i] = edges[i] + d;
				axis = new VariableAxis(edges);
			}

		}

		// set it back with new binning
		double m = h1.mean() + d;
		double r = h1.rms();
		h1 = new Histogram1D(this.title, this.title, axis);
		h1.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
		setMeanAndRms(m, r);

	}

	/**
	 * Return true if bins have constant bin width
	 * 
	 * @return true if bin width is fixed
	 */

	public boolean isFixedBinning() {
		return axis.isFixedBinning();
	}

	/**
	 * Fill histogram with a value
	 * 
	 * @param value
	 *            Value to be filled
	 */

	public void fill(double value) {

		h1.fill(value);

	}

	/**
	 * Fill histogram with values from a PND array. This means that every entry
	 * from PND contributes to histogram. All weights are 1.
	 * 
	 * @param pnd
	 *            PND used to fill histogram
	 **/

	public void fill(PND pnd) {

		for (int i = 0; i < pnd.size(); i++) {
			double[] tt = (double[]) pnd.get(i);
			for (int j = 0; j < tt.length; j++)
				h1.fill(tt[j]);
		}

	}

	/**
	 * Fill histograms with values from PND array. Also specify weigths in a
	 * form of PND. Make sure that both arrays has the size size and dimension.
	 * 
	 * @param pnd
	 *            input value
	 * @param weigths
	 *            PND with weights
	 * */
	public void fill(PND pnd, PND weigths) {

		if (pnd.size() != weigths.size()) {
			System.out
					.println("Sizes of input and weight arrays are different!");
			return;
		}

		for (int i = 0; i < pnd.size(); i++) {
			double[] tt = (double[]) pnd.get(i);
			double[] ww = (double[]) weigths.get(i);
			for (int j = 0; j < tt.length; j++)
				h1.fill(tt[j], ww[j]);
		}

	}

	/**
	 * Fill histogram with array of double values
	 * 
	 * @param values
	 *            array of double values
	 */

	public void fill(double[] values) {

		for (int i = 0; i < values.length; i++)
			h1.fill(values[i]);

	}

	/**
	 * Fill histogram with array of double values
	 * 
	 * @param values
	 *            array of double values
	 */

	public void fill(Cloud1D c1d) {

		for (int i = 0; i < c1d.entries(); i++)
			h1.fill(c1d.value(i), c1d.weight(i));

	}
	
	
	/**
	 * Fill histogram with array of double values. Take into account weights.
	 * 
	 * @param values
	 *            array of double values
	 * @param weights
	 *            array of double weights
	 */

	public void fill(double[] values, double[] weights) {

		for (int i = 0; i < values.length; i++)
			h1.fill(values[i], weights[i]);

	}

	/**
	 * Fill histogram with array of integer values
	 * 
	 * @param values
	 *            array of integer values
	 */

	public void fill(int[] values) {

		for (int i = 0; i < values.length; i++)
			h1.fill((double) values[i]);

	}

	/**
	 * Fill histogram a with weight
	 * 
	 * @param value
	 *            Value to be filled
	 * @param weight
	 *            Weight of the value
	 */
	public void fill(double value, double weight) {

		h1.fill(value, weight);

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
	 * Calculate complete statistics for this histogram.
	 * Unlike other methods (like mean or rms), it performs calculations
	 * on the existing histogram, thus the method is somewhat slow.
	 * It return mean, error on the mean, RMS, variance, standard deviation. <p>
	 * The key for the output <b>map are: mean, error, rms, variance, stddev etc. Print the keys of this
	 * map to get the full access to statistics</b>.
	 * 
	 * @return map representing histogram statistics
	 */
	public  Map<String,Double> getStat(){
		
		
		Map<String,Double> tmp= new  HashMap<String,Double>();
		IAxis axis = h1.axis();
		Histogram1D h=get();
		
		double sum1=0;
		double sum2=0;
		double sum3=0;
		for (int i = 0; i < axis.bins(); i++) {
			double x = h.binMean(i);  // Find the midpoint of each interval range.
			double y = h.binHeight(i);
			sum1=sum1+y;              // calculate the total weight or the total number of points
			sum2=sum2+x*y;            //  sum of the data
			sum3=sum3+x*x*y;          // estimated sum-of-the-squares of the data.
			
		}	
		
		double mean=sum2/sum1;	
		double mean_square=sum3/sum1;
		double rms=Math.sqrt(sum3/sum1);
		double variance = mean_square - mean*mean;
		double stddev=Math.sqrt(variance);
		
		tmp.put("mean", mean);
		tmp.put("mean_error", stddev / Math.sqrt(sum1));
		tmp.put("rms", rms);
		tmp.put("variance", variance);
		tmp.put("standardDeviation", stddev);
		tmp.put("maxBinHeight", h1.maxBinHeight());
		tmp.put("minBinHeight", h1.minBinHeight());
		tmp.put("allEntries", (double)h1.allEntries());
		tmp.put("entries", (double)h1.entries());
		tmp.put("underflowBin", (double)h1.binEntries(IAxis.UNDERFLOW_BIN));
		tmp.put("underflowHeight", h1.binHeight(IAxis.UNDERFLOW_BIN));
		tmp.put("overflowBin", (double)h1.binEntries(IAxis.OVERFLOW_BIN));
		tmp.put("overflowHeight", h1.binHeight(IAxis.OVERFLOW_BIN));
		
		
		return tmp;
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
	public H1D operScale(String title, double scaleFactor) {
		scale(title, scaleFactor);
		return this;
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
	 * Smooth the histogram.
	 * <p>
	 * Each band of the histogram is smoothed by averaging over a moving window
	 * of a size specified by the method parameter: if the value of the
	 * parameter is <i>k</i> then the width of the window is <i>2*k + 1</i>. If
	 * the window runs off the end of the histogram only those values which
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

	public H1D operSmooth(boolean isWeighted, int k) {

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
	 *            <code>IllegalArgumentException</code> will be thrown. If zero,
	 *            the histogram object will be returned with no smoothing
	 *            applied.
	 * @return A Gaussian smoothed version of the histogram.
	 * 
	 */
	public H1D operSmoothGauss(double standardDeviation) {

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
	public H1D copy() {
		return copy(this.title);

	}
	
	/**
	 * Make a copy of the data holder
	 * 
	 * @return New data holder
	 */
	public void clear() {
		h1.reset();

	}





     /**
     * Rebin a histogram with constant bin width. This function
     * returns a new histogram with the rebinned contents.
     * The bin errors are recomputed during the rebinning.
     *  @param ngroup  indicates how many bins of this have to me merged
     *                 into one bin of hnew
     *  @return a new histogram with rebinned content. 
     */
       public H1D rebin(int ngroup) {

                this.axis = h1.axis();

                if (!axis.isFixedBinning()) {
                      ErrorMessage("Cannot rebin histogram with variable bins");
                }

                int nbins= (int) ( ( (1.0*axis.bins())/ngroup));
                // System.out.println(nbins);
 
                H1D hnew = new H1D(getTitle(),nbins,min,max);

                int ibins = nbins + 2;
                double[] newHeights = new double[ibins];
                double[] newErrors = new double[ibins];
                double[] newMeans = new double[ibins];
                double[] newRmss = new double[ibins];
                int[] newEntries = new int[ibins];

                newHeights[0] = getUnderflowHeight();
                newHeights[ibins - 1] = getOverflowlowHeight();

                for (int i = 0; i < ibins-2; i++) {
                       double h=0;
                       double e2=0;
                       int  t=0;
                       double m=0;
                       double rms=0;
                       int start=i*ngroup;
                       for (int j=start; j<(start+ngroup); j++) {
                          h  += h1.binHeight(j);
                          t  += h1.binEntries(j);
                          e2 += h1.binError(j)*h1.binError(j); 
                          m  += h1.binMean(j);
                          rms+= h1.binRms(j)*h1.binRms(j); 
                          // System.out.println(j);  
                      }
                        h=h/ngroup;
                        e2=Math.sqrt(e2/ngroup);
                        rms=Math.sqrt(rms/ngroup);
                        m=m/ngroup; 
                        // System.out.println(h);

                        newHeights[i + 1] = h;
                        newErrors[i + 1] = e2;
                        newEntries[i + 1] = t;
                        newMeans[i + 1] = m;
                        newRmss[i + 1] = rms;
                }

                hnew.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
                hnew.setMeanAndRms(h1.mean(), h1.rms());

                // get copy
                LinePars lnew = copyLinePars(lpp);
                hnew.setDrawOption(lnew);
                hnew.setNEntries(entries());

                return hnew;



        };



	
	/**
	 * Get exact copy of the current histogram. This makes a new
	 * object.
	 * 
	 * @param newtitle
	 *            New title
	 * @return a new copy of the histogram
	 */

	public H1D copy(String newtitle) {

		int ibins = bins + 2;
		double[] newHeights = new double[ibins];
		double[] newErrors = new double[ibins];
		double[] newMeans = new double[ibins];
		double[] newRmss = new double[ibins];
		int[] newEntries = new int[ibins];

		newHeights[0] = getUnderflowHeight();
		newHeights[ibins - 1] = getOverflowlowHeight();

		for (int i = 0; i < ibins - 1; i++) {
			newHeights[i + 1] = h1.binHeight(i);
			newErrors[i + 1] = h1.binError(i);
			newEntries[i + 1] = h1.binEntries(i);
			newMeans[i + 1] = h1.binMean(i);
			newRmss[i + 1] = h1.binRms(i);
		}

		H1D hnew = new H1D(newtitle, axis);
		hnew.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
		hnew.setMeanAndRms(h1.mean(), h1.rms());

		// get copy
		LinePars lnew = copyLinePars(lpp);
		hnew.setDrawOption(lnew);
		hnew.setNEntries(entries());

		return hnew;
	}


        /**
         * Rescale errors by a some factor.
         * This will return a new object. 
         * 
         * @param scale  
         *            scale factor used to multiply errors 
         * @return a new copy of the histogram with rescaled errors on bin content.
         */

        public H1D scaleErrors(double scale) {

                IAxis a=h1.axis();
                int ibins=a.bins()+2;
                double[] newHeights = new double[ibins];
                double[] newErrors = new double[ibins];
                double[] newMeans = new double[ibins];
                double[] newRmss = new double[ibins];
                int[] newEntries = new int[ibins];
                newHeights[0] = getUnderflowHeight();
                newHeights[ibins - 1] = getOverflowlowHeight();
                for (int i = 0; i < ibins-2; i++) {
                        newHeights[i + 1] = h1.binHeight(i);
                        newErrors[i + 1] = h1.binError(i)*scale;
                        newEntries[i + 1] = h1.binEntries(i);
                        newMeans[i + 1] = h1.binMean(i);
                        newRmss[i + 1] = h1.binRms(i);
                }
                H1D hnew = new H1D(getTitle(), axis);
                hnew.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
                hnew.setMeanAndRms(h1.mean(), h1.rms());
                // get copy
                LinePars lnew = copyLinePars(lpp);
                hnew.setDrawOption(lnew);
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

		return (int) h1.binEntries(index);

	}

	/**
	 * Get all entries of the histogram as an array
	 * 
	 * @return array with histogram entries.
	 */
	public int[] binEntries() {

		int[] hh = new int[bins];
		for (int i = 0; i < bins; i++)
			hh[i] = (int) h1.binEntries(i);
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
                bins = h1.axis().bins();
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
                bins = h1.axis().bins();
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
	 * Sum of all heights multiplied by the bin size.
	 * 
	 * @return Sum of all heights times bin size. 
	 */
	public double integral() {
                IAxis a=h1.axis();
                int Nbins = a.bins();
                return integral(1,Nbins, true);
	}

	/**
	 * Convert a coordinate on the axis to a bin number
	 * 
	 * @param x
	 *            coordinate on axis
	 * @return index of the corresponding bin
	 */
	public int findBin(double x) {
		return h1.axis().coordToIndex(x);

	}

	
	
	
	/**
	 * Integrate a histogram between two bin indices (between 1 and max number)
	 * The integral is computed as the sum of bin contents in the range
	 * if the last parameter is false. If it is true,  the  integral is the sum of
     * the bin contents multiplied by the bin width in x.           
     *
	 * 
	 * @param BinMin
	 *            Min index for integration starting from 1 (included to
	 *            integration)
	 * @param BinMax
	 *            Max index for integration (start from 1, included to integration)
	 * @param timesBinWidth
	 *            If true, the integral is the sum of
     *             the bin contents multiplied by the bin width in x.           
	 *            
	 * @return integral (sum of all heights)
	 */

	public double integral(int BinMin, int BinMax, boolean timesBinWidth) {

		IAxis a=h1.axis();
		int Nbins = a.bins();
		if (BinMin > BinMax) {
			ErrorMessage("Wrong bin number!");
			return -1;
		}
		if (BinMin < 1 || BinMax > Nbins) {
			ErrorMessage("Wrong bin number!");
			return -1;
		}

		double sum = 0.0;
		if (timesBinWidth == false){
		for (int i = BinMin - 1; i < BinMax; i++) {
			sum += h1.binHeight(i);

		}
		
		} else {
			for (int i = BinMin - 1; i < BinMax; i++) {
				double w=a.binUpperEdge(i)-a.binLowerEdge(i);
				sum += h1.binHeight(i)*w;

			}
			
		}
		
		

		return sum;
	}

	
	
	/**
	 * Integrate a histogram between two bin indices (between 1 and max number)
	 * By default the integral is computed as the sum of bin contents in the range.
     *
	 * 
	 * @param BinMin
	 *            Min index for integration starting from 1 (included to
	 *            integration)
	 * @param BinMax
	 *            Max index for integration (included to integration)
	 * @return integral (sum of all heights)
	 */

	public double integral(int BinMin, int BinMax) {

		return integral(BinMin, BinMax,false);
		
	}

	/**
	 * Integrate histogram in a region between Xmin and Xmax.
	 * This will sum up all bin content between the regions.
	 * 
	 * @param Xmin
	 *            Min index for integration
	 * @param Xmax
	 *            Max index for integration
	 * @param  timesBinWidth
	 *            if true, multiply by the bin width.           
	 * @return integral (sum of all heights)
	 */

	public double integralRegion(double xmin, double xmax, boolean timesBinWidth) {

		int Nbins = h1.axis().bins();
		int xmi = h1.axis().coordToIndex(xmin);
		int xma = h1.axis().coordToIndex(xmax);

		if (xmi > xma) {
			ErrorMessage("Wrong bin number!");
			return -1;
		}

		if (xmi < 1 || xma > Nbins) {
			ErrorMessage("Wrong bin number!");
			return -1;
		}

		return integral(xmi, xma, timesBinWidth);

	}

	

	/**
	 * Integrate histogram in a region between Xmin and Xmax.
	 * This will sum up all bin content between the regions.
	 * 
	 * @param Xmin
	 *            Min index for integration
	 * @param Xmax
	 *            Max index for integration
	 * @return integral (sum of all heights)
	 */

	public double integralRegion(double xmin, double xmax) {

		return integralRegion(xmin, xmax, false);
		
		

	}
	
	/**
	 * Return q probability distribution  derived from a histogram.
	 * The histogram is scaled by the sum of all contents.
	 * @return probability distribution
	 */
	
	public H1D getProbability() {
		H1D h1d=this.copy();
		h1d.scale(1.0/h1d.sumAllBinHeights());
		return h1d;
		

	}


        /**
         * Divide heights of each bin by the bin width.
         * Variable bin sizes are supported. Statistics of the histogram 
         * should be treated with care.
         * @return new histogram after the division by bin width. 
         */

        public H1D getDividedByBinWidth() {

                IAxis a=h1.axis();
                int ibins=a.bins()+2;
                double[] newHeights = new double[ibins];
                double[] newErrors = new double[ibins];
                double[] newMeans = new double[ibins];
                double[] newRmss = new double[ibins];
                int[] newEntries = new int[ibins];
                newHeights[0] = getUnderflowHeight();
                newHeights[ibins - 1] = getOverflowlowHeight();
                // double sum=sumAllBinHeights();
                for (int i = 0; i < ibins-2; i++) {
                        double w=(a.binUpperEdge(i)-a.binLowerEdge(i));
                        newHeights[i + 1] = h1.binHeight(i)/w;
                        newErrors[i + 1] = h1.binError(i)/w;
                        newEntries[i + 1] = h1.binEntries(i);
                        newMeans[i + 1] = h1.binMean(i);
                        newRmss[i + 1] = h1.binRms(i);
                        // System.out.println(i);
                        // System.out.println(h1.binHeight(i)/w);
                        // System.out.println(w);
                }

                H1D hnew = new H1D(getTitle(), axis);
                hnew.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
                hnew.setMeanAndRms(h1.mean(), h1.rms());
                // get copy
                LinePars lnew = copyLinePars(lpp);
                hnew.setDrawOption(lnew);
                hnew.setNEntries(entries());
                hnew.setMeanAndRms(mean(), rms());
                return hnew;

        }







	
	
	/**
	 * Get a density distribution  dividing each bin of the histogram
	 * by the bin width  and the total number of heights for all bins.
         * Variable bin sizes are supported. Statistics of the histogram 
         * should be treated with care. This disreibution is normalized to 1. 
	 * @return new histogram with density distribution
	 */
	
	public H1D getDensity() {

		IAxis a=h1.axis();
		int ibins=a.bins()+2;
		double[] newHeights = new double[ibins];
		double[] newErrors = new double[ibins];
		double[] newMeans = new double[ibins];
		double[] newRmss = new double[ibins];
		int[] newEntries = new int[ibins];

		newHeights[0] = getUnderflowHeight();
		newHeights[ibins - 1] = getOverflowlowHeight();

		double sum=sumAllBinHeights();
		for (int i = 0; i < ibins-2; i++) {
			double w=sum*(a.binUpperEdge(i)-a.binLowerEdge(i));
			newHeights[i + 1] = h1.binHeight(i)/w;
			newErrors[i + 1] = h1.binError(i)/w;
			newEntries[i + 1] = h1.binEntries(i);
			newMeans[i + 1] = h1.binMean(i);
			newRmss[i + 1] = h1.binRms(i);
			// System.out.println(i);
			// System.out.println(h1.binHeight(i)/w);
			// System.out.println(w);
		}

		H1D hnew = new H1D(getTitle(), axis);
		hnew.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
		hnew.setMeanAndRms(h1.mean(), h1.rms());
		// get copy
		LinePars lnew = copyLinePars(lpp);
		hnew.setDrawOption(lnew);
		hnew.setNEntries(entries());
		hnew.setMeanAndRms(mean(), rms());
		return hnew;

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

           public Map<String,Double> compareChi2(F1D f1) {

                Map<String,Double> tmp= new  HashMap<String,Double>();

                int bins1 = get().axis().bins();
                double sum1 = 0;
                double nDf = 0;

                for (int i = 0; i < bins1; i++) {
                        double bin1 = binHeight(i);
                        double e1 = binError(i);
                        double x1=get().axis().binLowerEdge(i);
                        double x2=get().axis().binUpperEdge(i);
                        double delta=x2-x1;
                        double x=x1+0.5*delta; 
                        double ff=f1.eval(x); 
                        if (e1 != 0) { 
                               sum1=sum1+((ff-bin1)*(ff-bin1) / (e1*e1));
                               nDf++;
                        }
                }

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
	 * Compare two histograms. Comparison of two histograms test hypotheses that
	 * two histograms represent identical distributions. It calculates Chi2
	 * between 2 histograms taking into account errors on the heights of the
	 * bins. The number chi2/ndf gives the estimate (values close to 1 indicates
	 * similarity between 2 histograms.) Two histograms are identical if chi2=0.
	 * Chi2/ndf and p-value probability is 1. 
	 * Make sure that both histograms have error (or set them to
	 * small values).
	 * 
	 * @param h2
	 *            second histogram to compare to. 
	 * @return map with the result. It gives Chi2, gives number
	 *         of degrees of freedom (ndf), probability
	 *         ("quality", or p-value).
	 */
	public Map<String,Double> compareChi2(H1D h2) {


                Map<String,Double> tmp= new  HashMap<String,Double>();

		int bins1 = get().axis().bins();
		int bins2 = h2.get().axis().bins();

		if (bins1 != bins2) {
			System.out
					.println("Different histograms! Please use histograms with the same bin numbers");
			return tmp;
		}

		double chi2 = 0;
		int nDf = 0;

		double sum1 = 0;
		double sum2 = 0;
		double sumw1 = 0;
		double sumw2 = 0;

		for (int i = 0; i < bins1; i++) {

			double bin1 = binHeight(i);
			double bin2 = h2.binHeight(i);
			double e1 = binError(i);
			double e2 = h2.binError(i);

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

		for (int i = 0; i < bins1; i++) {

			double bin1 = binHeight(i);
			double bin2 = h2.binHeight(i);
			double e1 = binError(i);
			double e2 = h2.binError(i);
			
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
				// System.out.println(chi2);
				nDf++;
			}

		}

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
	 * Various manipulations with histograms (+,-,*,/). Keep the original title.
	 * No new histogram object created.
	 * 
	 * @param a
	 *            H1D histogram.
	 * @param what
	 *            String representing the operation: "+" add a histogram to the
	 *            original "-" subtract a histogram from the original "*"
	 *            multiply "/" divide
	 * @return modified H1D histogram
	 */

	public H1D oper(H1D a, String what) {

		return oper(a, getTitle(), what);

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
			nums[0][i] = binMean(i);
			if (mode == 1)
				nums[0][i] = binCenter(i);
			nums[1][i] = binHeight(i);
			nums[2][i] = binError(i);
		}
		return nums;
	}

	/**
	 * Various manipulations with histograms (+,-,*,/). Note: no new object will
	 * be created.
	 * 
	 * @param a
	 *            H1D histogram.
	 * @param title
	 *            New Title
	 * 
	 * @return same H1D object but modified
	 */

	public H1D oper(H1D a, String title, String what) {

		IAnalysisFactory af = IAnalysisFactory.create();
		IHistogramFactory hf = af.createHistogramFactory(af.createTreeFactory()
				.create());

		// first check them
		if (what.equals("+")) {
			IHistogram1D hnew = hf.add(title, get(), a.get());
			return new H1D(hnew);
		}

		if (what.equals("-")) {
			IHistogram1D hnew = hf.subtract(title, get(), a.get());
			return new H1D(hnew);
		}

		if (what.equals("*")) {
			IHistogram1D hnew = hf.multiply(title, get(), a.get());
			return new H1D(hnew);
		}

		if (what.equals("/")) {
			IHistogram1D hnew = hf.divide(title, get(), a.get());
			return new H1D(hnew);
		}

		ErrorMessage("Operation \"" + what + "\" is not implemented");

		return this;

	}


	
	
	/**
	 * Get statistical parameters of a Histogram as a list of strings
	 * @param h1 histogram H1D
	 * @return list of with mean, RMS etc.
	 */
	public String[] getStatParameters() {
		
		    double mean = h1.mean();
	        double rms = h1.rms();
	        DecimalFormat dfb = new DecimalFormat("##.###E00");
	        String name = getTitle();
	        String sentries = "Entries =" + Integer.toString(h1.entries());
	        String smean = "Mean  =" + dfb.format(mean);
	        String srms = "RMS =" + dfb.format(rms);
	        String extra = "Under/Overflow =" + Integer.toString(h1.extraEntries());

		
		String[] s = { name, sentries, smean, srms, extra };
		return s;
		
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
