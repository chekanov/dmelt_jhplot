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




import jminhep.cluster.DataHolder;
import jminhep.cluster.DataPoint;
import jplot.*;
import jhplot.gui.HelpBrowser;
import jhplot.io.PReader;
import jhplot.math.*;
import jhplot.utils.SHisto;

import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.awt.Color;
import java.io.*;
import java.net.URL;
import java.util.*;
import hep.aida.*;
import hep.aida.ref.histogram.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

/**
 * A container to hold data points with 1st and 2nd level errors. The first
 * errors are usually statistical, the second error are systematic. P1D can be
 * used for drawing, manipulation with data etc.
 * 
 * <p>
 * 
 * This is a vast high-performance low-memory footprint data container.
 * 
 * @author S.Chekanov
 * 
 */

public class P1D extends DrawOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private DoubleArrayList X;   // X value
	private DoubleArrayList Y;   // Y value
	private DoubleArrayList XE1left;  // 1st level, X left error
	private DoubleArrayList XE1right;  // 1st level, X right error
	private DoubleArrayList XE2left;  // 2nd level, X left error
	private DoubleArrayList XE2right;  // 2nd level, X right error
	private DoubleArrayList YE1upper;  // 1st level. on Y
	private DoubleArrayList YE1down;     
	private DoubleArrayList YE2upper;  // 2nd level on Y
	private DoubleArrayList YE2down;
	private int dimen; // dimension of this holder


    /**
        * Construct an empty container

   **/
        public P1D() {
         this("No title");
        }

	/**
	 * Construct an empty container with a title. It is assumed that on X and Y values will be stored (dimension 2).
	 * 
	 * @param title
	 *            New title
	 */

	public P1D(String title) {
		
		X = new DoubleArrayList();
		Y = new DoubleArrayList();
		this.title = title;
		lpp.setColor(Color.black);
		lpp.setPenWidth(2.0f);
		lpp.setType(LinePars.P1D);
		lpp.setGraphStyle(0);
		lpp.setDrawLegend(true);
		lpp.setColorErrorsY(Color.black);
		lpp.setColorErrorsX(Color.black);
		lpp.errorsY(true);
		lpp.errorsX(true);
		lpp.setGraphStyle(0);
		lpp.setSymbol(4);
		lpp.setDrawSymbol(true);
		lpp.setDrawLine(false);
		dimen = 2;
	}

	
	/**
	 * Construct an empty container with a title. It is assumed that on X and Y values will be stored (dimension 2).
	 * 
	 * @param title
	 *            New title
	 */

	public P1D(String title, DoubleArrayList Xval, DoubleArrayList Yval) {
		this.X = Xval;
		this.Y = Yval; 
		this.title = title;
		lpp.setColor(Color.black);
		lpp.setPenWidth(2.0f);
		lpp.setType(LinePars.P1D);
		lpp.setGraphStyle(0);
		lpp.setDrawLegend(true);
		lpp.setColorErrorsY(Color.black);
		lpp.setColorErrorsX(Color.black);
		lpp.errorsY(true);
		lpp.errorsX(true);
		lpp.setGraphStyle(0);
		lpp.setSymbol(4);
		lpp.setDrawSymbol(true);
		lpp.setDrawLine(false);
		dimen = 2;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Construct an empty container with a title
	 * 
	 * @param title
	 *            New title
	 * @param dimension
	 *            dimension (2,4,6,10)
	 */

	public P1D(String title, int dimension) {

		this(title);
		setDimension(dimension);
		
		
		

	}

	/**
	 * Construct an empty container with a title and color for points
	 * 
	 * @param title
	 *            New title
	 * @param color
	 *            for points
	 */

	public P1D(String title, Color color) {

		this(title);
		lpp.setColor(color);
	}

	/**
	 * Construct an empty container with a title and color and symbol style for
	 * points
	 * 
	 * @param title
	 *            New title
	 * @param color
	 *            for points
	 * 
	 * @param style
	 *            used to draw symbols (1,2,3,4..)
	 */

	public P1D(String title, Color color, int symbolstyle) {

		this(title);
		lpp.setColor(color);
		lpp.setSymbol(symbolstyle);

	}

	/**
	 * Construct a container from pairs of P0D (one for X, second for Y)
	 * 
	 * @param p1
	 *            P1D representing X
	 * @param p2
	 *            P1D representing Y
	 **/
	public P1D(P0D p1, P0D p2) {
		this(p1.getTitle() + "+" + p2.getTitle());
		fill(p1, p2);
	}

	/**
	 * Construct a container from pairs of P0D (one for X, second for Y)
	 * 
	 * @param title
	 *            new title
	 * @param p1
	 *            P1D representing X
	 * @param p2
	 *            P1D representing Y
	 ***/
	public P1D(String title, P0D p1, P0D p2) {
		this(title);
		fill(p1, p2);
	}

	/**
	 * Construct P1D from a Cloud2D
	 * 
	 * @param c2d
	 *            input cloud2d
	 **/
	public P1D(Cloud2D c2d) {
		this(c2d.title());
		fill(c2d);
	}

	/**
	 * Construct a container from pairs X and Y
	 * 
	 * @param title
	 *            new title
	 * @param p1
	 *            array representing X
	 * @param p2
	 *            array representing Y
	 **/
	public P1D(String title, double[] p1, double[] p2) {
		this(title);
		fill(p1, p2);
	}

         /**
         * Construct a container from pairs X and Y
         * 
         * @param title
         *            new title
         * @param p1
         *            array representing X
         * @param p2
         *            array representing Y
         **/
        public P1D(String title, int[] p1, int[] p2) {
                this(title);
                fill(p1, p2);
        }

	/**
	 * Construct a new copy of the data container.
	 * 
	 * @param title
	 *            New title
	 * @param p1d
	 *            input P1D object
	 */

	public P1D(String title, P1D p1d) {

		LinePars lnew = copyLinePars(p1d.getLineParm());
		setDrawOption(lnew);
		this.title = title;
		dimen = p1d.dimension();
	
		X=p1d.getXval();
		Y=p1d.getYval();
		
		if ( dimen == 2){
		    return;
		}
		
		if ( dimen == 3){
			YE1upper=p1d.getYE1upper();
		    return;
		}
		
		if ( dimen == 4){
			YE1upper=p1d.getYE1upper();
			YE1down=p1d.getYE1down();
			return;
		}
		
		if ( dimen == 6){
			YE1upper=p1d.getYE1upper();
			XE1left=p1d.getXE1left();
			XE1right=p1d.getXE1right();
			YE1down=p1d.getYE1down();
			return;
		}
		
		if ( dimen == 10){
			YE1upper=p1d.getYE1upper();
			XE1left=p1d.getXE1left();
			XE1right=p1d.getXE1right();
			YE1down=p1d.getYE1down();
			
			YE2upper=p1d.getYE2upper();
			XE2left=p1d.getXE2left();
			XE2right=p1d.getXE2right();
			YE2down=p1d.getYE2down();
			
			return;
		}

	

	}

	/**
	 * Set the dimension for the container. Can be:
	 * 2:  for X,Y <br>
	 * 3:  for X,Y and error on Y <br>
	 * 4:  for X,Y and error on Y (up) and Y (down)<br>
	 * 6:  for X,Y and error on Y (up) and Y (down) and X(left) and X(right)<br>
	 * 10: same as before but 2nd level errors (usually systematics)
	 * 
	 * @param dimension
	 *            dimension used.
	 * 
	 */

	public void setDimension(int dimension) {

		if (dimension < 0 || dimension > 10) {
			dimen = 2;
		}
	
		YE1upper=null;
		YE1down=null;
		XE1left=null;
		XE2left=null;
		XE1right=null;
		XE2right=null;
		YE2upper=null;
		YE2down=null;
		

		if (dimension==2){
		    return;
		}
		if (dimension==3){
			YE1upper= new DoubleArrayList();
			return;
		}
		
		if (dimension==4){
			YE1upper= new DoubleArrayList();
			YE1down= new DoubleArrayList();
			return;
		}
		
		if (dimension==6){
			YE1upper= new DoubleArrayList();
			YE1down= new DoubleArrayList();
			XE1left= new DoubleArrayList();
			XE1right= new DoubleArrayList();
			return;
		}
		
		if (dimension==8){
			YE1upper= new DoubleArrayList();
			YE1down= new DoubleArrayList();
			XE1left= new DoubleArrayList();
			XE1right= new DoubleArrayList();
			YE2upper= new DoubleArrayList();
			YE2down= new DoubleArrayList();
			return;
		}
		

		if (dimension==10){
			YE1upper= new DoubleArrayList();
			YE1down= new DoubleArrayList();
			XE1left= new DoubleArrayList();
			XE1right= new DoubleArrayList();
			XE2left= new DoubleArrayList();
			XE2right= new DoubleArrayList();
			YE2upper= new DoubleArrayList();
			YE2down= new DoubleArrayList();
			return;
		}
		
		
		

	}

	
	/**
	 * get LinePars class which holds graphical attributes
	 * 
	 * @return Graphic attributes
	 */
	public LinePars getLinePars() {
		return lpp;

	}

	/**
	 * Sets LinePars class for graphical attributes
	 * 
	 * @param pnew
	 *            Graphic attributes
	 */
	public void setLinePars(LinePars pnew) {
		lpp = pnew;
	}

	/**
	 * Create P1D data holder from multidimensional data holder. You should
	 * specify a slice which you want to export (X,Y) pair of P1D
	 * 
	 * @param dh
	 *            Input data container
	 * @param title
	 *            New title
	 * @param i1
	 *            Index of the first column
	 * @param i2
	 *            Index of the second column
	 */

	public P1D(DataHolder dh, String title, int i1, int i2) {
		this(title);

		int dim = dh.getDimention();
		if (i1 > dim || i2 > dim) {
			ErrorMessage("index is larger than dimension "
					+ Integer.toString(dim));
			return;
		}

		setDimension(2);
		for (int i = 0; i < dh.getSize(); i++) {
			DataPoint dp = dh.getRow(i);
			double x = dp.getAttribute(i1);
			double y = dp.getAttribute(i2);
			X.add(x);
			Y.add(y);
		}
	
	} // end

	/**
	 * Create P1D data holder from multidimensional data holder. You should
	 * specify a slice which you want to export (X,Y) pair of P1D. The title is
	 * default
	 * 
	 * @param dh
	 *            Input data container
	 * @param i1
	 *            Index of the first column
	 * @param i2
	 *            Index of the second column
	 */

	public P1D(DataHolder dh, int i1, int i2) {

		this(dh, dh.getRelation(), i1, i2);

	}

	/**
	 * Construct a P1D from a file.
	 * <p>
	 * The file should contain 2, or 4, or 6, or 10 columns: 1) x,y: data
	 * without any errors 2) x,y, y(upper), y(lower) - data with 1st level
	 * errors on Y 3) x,y, x(left), x(right), y(upper), y(lower) - data with 1st
	 * level errors on X and Y 4) x,y, x(left), x(right), y(upper), y(lower),
	 * x(leftSys), x(rightSys), y(upperSys), y(lowerSys) - data with X and Y and
	 * 1st and 2nd level errors. Comments lines starting with "#" and "*" are
	 * ignored.
	 * 
	 * @param title
	 *            Title of the container
	 * @param sfile
	 *            File name with input. input file name. It can be either a file
	 *            on a file system or URL location (must start from http or ftp)
	 */
	public P1D(String title, String sfile) {

		this(title);
		read(sfile);

	}

	/**
	 * Write a File in form of LaTeX table from values in the container P1D
	 * 
	 * @param name
	 *            Name of the file
	 * @param dx
	 *            Format of x values
	 * @param dy
	 *            Format of y values
	 */

	public void toFileAsLatex(String name, DecimalFormat dx, DecimalFormat dy) {

		Date dat = new Date();
		String today = String.valueOf(dat);
		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);

			tx.println("% DataMelt: output from P1D " + this.title);
			tx.println("% DataMelt: created at " + today);

			// create vectors
			VHolder vh = new VHolder(this);
			String[] names = vh.getNames();
			Double[][] data = vh.getData();

			// only x and y
			if (names.length == 3)
				tx.println("\\begin{tabular}{|c|c|} \\hline");

			// x, y and errors on y
			if ( names.length== 4)
				tx.println("\\begin{tabular}{|c|c|c|} \\hline");

			// x, y, and error on x and y
			if ( names.length == 5)
				tx.println("\\begin{tabular}{|c|c|c|c|c|} \\hline");

			// x,y, full errors on y and 1st level error on x
			if ( names.length == 7)
				tx.println("\\begin{tabular}{|c|c|c|c|c|c|c|} \\hline");

			// all errors
			if (names.length == 11)
				tx.println("\\begin{tabular}{|c|c|c|c|c|c|c|c|c|} \\hline");

			
			// get data
			for (int i = 0; i < vh.size(); i++) {

				double[] dd = new double[vh.dimen()];   
                for (int k=0; k<vh.dimen(); k++) dd[k]=data[i][k];
				

				if ( names.length== 3) {
					double xx = dd[1];
					double yy = dd[2];
					String x = dx.format(xx);
					String y = dy.format(yy);
					tx.println(x + " & " + y + "  \\\\ \\hline");
				}

				if (names.length == 4) {
					double xx = dd[1];
					double yy =  dd[2];
					double ey =  dd[3];
					String x = dx.format(xx);
					String y = dy.format(yy);
					String e = dy.format(ey);
					tx.println(x + " & " + y + " & " + e + "  \\\\ \\hline");
				}
				
				
				if ( names.length== 5) {
					double xx = dd[1];
					double yy = dd[2];
					double yy1 = dd[3];
					double yy2 = dd[4];
					String x = dx.format(xx);
					String y = dy.format(yy);
					String y1 = dy.format(yy1);
					String y2 = dy.format(yy2);
					tx.println("$" + x + "$   &   $" + y + "^{" + y1 + "}_{"
							+ y2 + "}$ \\\\ \\hline");
				}

				if (names.length == 7) {
					double xx = dd[1];
					double yy = dd[2];
					double xx1 = dd[3];
					double xx2 = dd[4];
					double yy1 = dd[5];
					double yy2 = dd[6];
					String x = dx.format(xx);
					String x1 = dx.format(xx1);
					String x2 = dx.format(xx2);
					String y = dy.format(yy);
					String y1 = dy.format(yy1);
					String y2 = dy.format(yy2);
					tx.println("$" + x + "^{" + x1 + "}_{" + x2 + "}$  &  "
							+ "$" + y + "^{" + y1 + "}_{" + y2
							+ "}$  \\\\ \\hline");
				}

				if (names.length== 9) {
					double xx = (Double) dd[1];
					double yy = (Double) dd[2];
					double xx1 = (Double) dd[3];
					double xx2 = (Double) dd[4];
					double yy1 = (Double) dd[5];
					double yy2 = (Double) dd[6];
					double ys1 = (Double) dd[7];
					double ys2 = (Double) dd[8];
					String x = dx.format(xx);
					String x1 = dx.format(xx1);
					String x2 = dx.format(xx2);
					String y = dy.format(yy);
					String y1 = dy.format(yy1);
					String y2 = dy.format(yy2);
					String y1s = dy.format(ys1);
					String y2s = dy.format(ys2);
					tx.println("$" + x + "^{" + x1 + "}_{" + x2 + "}$  &  "
							+ "$" + y + "^{" + y1 + "}_{" + y2 + "}$ (stat.)  "
							+ "$^{" + y1s + "}_{" + y2s
							+ "}$ (syst.)  \\\\ \\hline");
				}

				if (names.length == 11) {
					double xx = (Double) dd[1];
					double yy = (Double) dd[2];
					double xx1 = (Double) dd[3];
					double xx2 = (Double) dd[4];
					double ex1 = (Double) dd[5];
					double ex2 = (Double) dd[6];
					double yy1 = (Double) dd[7];
					double yy2 = (Double) dd[8];
					double ys1 = (Double) dd[9];
					double ys2 = (Double) dd[10];
					String x = dx.format(xx);
					String x1 = dx.format(xx1);
					String x2 = dx.format(xx2);
					String sx1 = dx.format(ex1);
					String sx2 = dx.format(ex2);
					String y = dy.format(yy);
					String y1 = dy.format(yy1);
					String y2 = dy.format(yy2);
					String y1s = dy.format(ys1);
					String y2s = dy.format(ys2);
					tx.println("$" + x + "^{" + x1 + "}_{" + x2
							+ "}$ (stat.)  " + "$^{" + sx1 + "}_{" + sx2
							+ "}$ (syst.) &  " + "$" + y + "^{" + y1 + "}_{"
							+ y2 + "}$ (stat.)  " + "$^{" + y1s + "}_{" + y2s
							+ "}$ (syst.)  \\\\ \\hline");
				}

			}

			tx.println("\\end{tabular}");

		} catch (IOException e) {
			ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	/**
	 * Write a P1D to an external file. If errors on data points are not given,
	 * they are set to 0
	 * 
	 * @param name
	 *            File name with output
	 */
	public void toFile(String name) {

		DecimalFormat dfb = new DecimalFormat("##.#####E00");
		Date dat = new Date();
		String today = String.valueOf(dat);

		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);

			tx.println("# DataMelt: output from P1D " + this.title);
			tx.println("# DataMelt: created at " + today);
			tx.println("# x,y,x(left),x(right),y(upper),y(lower),x(leftSys),x(rightSys),y(upperSys),y(lowerSys)");
			tx.println("#");
			for (int i = 0; i < size(); i++) {

				String x = dfb.format(X.getQuick(i));
				String y = dfb.format(Y.getQuick(i));

				if (dimen == 2) {
					tx.println(x + " " + y);
					continue;
				}

				String y1 = dfb.format(YE1upper.getQuick(i));
				String y2 = dfb.format(YE1down.getQuick(i) );

				if (dimen == 3) {
					tx.println(x + " " + y + " " + y1);
					continue;
				}

				if (dimen == 4) {
					tx.println(x + " " + y + " " + y1 + " " + y2);
					continue;
				}

				String x1 = dfb.format( XE1left.getQuick(i) );
				String x2 = dfb.format( XE1right.getQuick(i));

				if (dimen == 6) {
					tx.println(x + " " + y + " " + x1 + " " + x2 + " " + y1
							+ " " + y2);
					continue;
				}

				String x3 = dfb.format(getXleftSys(i));
				String x4 = dfb.format(getXrightSys(i));

				String y3 = dfb.format(getYupperSys(i));
				String y4 = dfb.format(getYlowerSys(i));

				tx.println(x + "  " + y + "  " + x1 + "  " + x2 + "  " + y1
						+ "  " + y2 + "  " + x3 + "  " + x4 + "  " + y3 + "  "
						+ y4);
			}
			f1.close();

		} catch (IOException e) {
			ErrorMessage("Error in the output file");
			e.printStackTrace();
		}

	}

	/**
	 * Print a P1D container on the screen
	 * 
	 */
	public void print() {

		System.out.println(toString());

	}

	

	
	/**
	 * Convert to a string
	 * 
	 * @return String representing P1D
	 */
	
	public String toString() {

		String tmp="# title=" +getTitle()+"  dimension="+Integer.toString(dimension())+"\n";
		if (dimen == 2) {
			tmp=tmp+"#  X       Y \n";
			for (int i = 0; i < size(); i++) {
			tmp=tmp+Double.toString(X.getQuick(i)) + "  " +Double.toString(Y.getQuick(i))+"\n";
			}
			return tmp;
		}

		
		if (dimen == 3) {
			tmp=tmp+"#  X       Y     ErrorY (symmetric)\n";
			for (int i = 0; i < size(); i++) {
			tmp=tmp+Double.toString(X.getQuick(i)) + " " +Double.toString(Y.getQuick(i))+" "+Double.toString(YE1upper.getQuick(i))+"\n";
			}
			return tmp;
		}
		
		if (dimen == 4) {
			tmp=tmp+"#  X       Y     ErrorY-      ErrorY+ \n";
			for (int i = 0; i < size(); i++) {
			tmp=tmp+Double.toString(X.getQuick(i)) + " " +Double.toString(Y.getQuick(i))+" "+
			Double.toString(YE1down.getQuick(i))+" "+Double.toString(YE1upper.getQuick(i))+"\n";
			}
			return tmp;
		}
		
		if (dimen == 6) {
			tmp=tmp+"#  X       Y     ErrorX-      ErrorY+       ErrorY-      ErrorY+ \n";
			for (int i = 0; i < size(); i++) {
			tmp=tmp+Double.toString(X.getQuick(i)) + " " +Double.toString(Y.getQuick(i))+" "+
		    Double.toString(XE1left.getQuick(i))+" "+Double.toString(XE1right.getQuick(i))+" "+
			Double.toString(YE1down.getQuick(i))+" "+Double.toString(YE1upper.getQuick(i))+"\n";
			}
			return tmp;
		}		
		
		if (dimen == 10) {
			tmp=tmp+"#  X       Y     ErrorX-      ErrorY+       ErrorY+      ErrorY-    ErrorSysX-      ErrorSysY+       ErrorSysY-      ErrorSysY+\n";
			for (int i = 0; i < size(); i++) {
			tmp=tmp+Double.toString(X.getQuick(i)) + " " +Double.toString(Y.getQuick(i))+" "+
		    Double.toString(XE1left.getQuick(i))+" "+Double.toString(XE1right.getQuick(i))+" "+
			Double.toString(YE1down.getQuick(i))+" "+Double.toString(YE1upper.getQuick(i))+" "+
		    Double.toString(XE2left.getQuick(i))+" "+Double.toString(XE2right.getQuick(i))+" " +
			Double.toString(YE2down.getQuick(i))+" "+Double.toString(YE2upper.getQuick(i))+"\n";
						
			}
			return tmp;
		}				
		

	
	
	   return tmp;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Create a P1D container from IDataPointSet.
	 * 
	 * 
	 * @param pd
	 *            Imported IDataPointSet.
	 * 
	 * 
	 */
	public P1D(IDataPointSet pd) {
		this(pd.title());
		
		setDimension(6);
		for (int i = 0; i < pd.size(); i++) {
			IDataPoint p = pd.point(i);
			double x = p.coordinate(0).value();
			double y = p.coordinate(1).value();
			double x1 = p.coordinate(0).errorPlus();
			double x2 = p.coordinate(0).errorMinus();
			double y1 = p.coordinate(1).errorPlus();
			double y2 = p.coordinate(1).errorMinus();
			addQuick(x, y, x2, x1, y2, y1);
		}

	}

	/**
	 * Create a P1D container from a histogram
	 * 
	 * @param title
	 *            New title
	 * 
	 * @param histo
	 *            Imported histogram
	 * 
	 * @param binMean
	 *            If true, Y values are put to the mean values in each bin. If
	 *            false, is just a height in the histogram bin
	 * 
	 * @param fillXerrors
	 *            If true, errors are set to X values (half of the bin width) If
	 *            false, errors on X are set to zero
	 * 
	 */

	public P1D(String title, H1D histo, boolean binMean, boolean fillXerrors) {

		this(title);
		Histogram1D h1 = histo.get();
		IAxis axis = h1.axis();
		int bins = axis.bins();
		clear();
		setDimension(6);
		for (int i = 0; i < bins; i++) {
			double x = axis.binCenter(i); // bin center
			double y = h1.binHeight(i);
			if (binMean)
				y = h1.binMean(i); // The weighted mean of a bin
			double y1 = h1.binError(i);
			double w = 0;
			if (fillXerrors)
				w = axis.binWidth(i);
			add(x, y, 0.5 * w, 0.5 * w, y1,y1);
		}

	}

	/**
	 * Create a P1D container from a histogram. The title is set to the
	 * histogram title.
	 * 
	 * @param histo
	 *            Imported histogram
	 * 
	 * @param binMean
	 *            If true, Y values are put to the mean values in each bin. If
	 *            false, Y corresponds to height of the histogram bin
	 * 
	 * @param fillXerrors
	 *            If true, errors are set to X values (half of the bin width) If
	 *            false, errors on X are set to zero
	 */

	public P1D(H1D histo, boolean binMean, boolean fillXerrors) {

		this(histo.getTitle(), histo, binMean, fillXerrors);
		dimen = 4;
	}

	/**
	 * Create a P1D container from a histogram. The title is set to the
	 * histogram title. Show errors on X as bin width x 0.5
	 * 
	 * 
	 * @param title
	 *            New title
	 * 
	 * @param histo
	 *            Imported histogram
	 * 
	 * @param binMean
	 *            If true, Y values are put to the mean values in each bin. If
	 *            false, Y is just a center of the histogram bin
	 * 
	 */

	public P1D(String title, H1D histo, boolean binMean) {

		this(title, histo, binMean, true);
		dimen = 4;
	}

	/**
	 * Create a P1D container from a histogram. The title is set to the
	 * histogram title. Show errors on X as bin width x 0.5
	 * 
	 * @param histo
	 *            Imported histogram
	 * 
	 * @param binMean
	 *            If true, Y values are put to the mean values in each bin. If
	 *            false, Y is just a height of the histogram bin
	 * 
	 */

	public P1D(H1D histo, boolean binMean) {

		this(histo.getTitle(), histo, binMean, true);

	}

	/**
	 * Create a P1D container from a histogram. The title is set to the
	 * histogram title. Show errors on X as bin width x 0.5. The Y points are
	 * set to the high values in each bin.
	 * 
	 * @param histo
	 *            Imported histogram
	 * 
	 * 
	 */

	public P1D(H1D histo) {

		this(histo.getTitle(), histo, false, true);
	}

	/**
	 * Create a P1D container from a histogram. The title is set to the
	 * histogram title. Show errors on X as bin width x 0.5 The Y points are set
	 * to the mean values in each bin.
	 * 
	 * 
	 * @param title
	 *            New title
	 * 
	 * @param histo
	 *            Imported histogram
	 * 
	 * 
	 */

	public P1D(String title, H1D histo) {

		this(title, histo, true, true);
	}

	/**
	 * Merge two P1D containers.
	 * 
	 * @param p1d
	 *            Container to be added
	 * @return modified P1D container
	 */

	public P1D merge(P1D p1d) {

		
		X.addAllOf(p1d.getXval());
		Y.addAllOf(p1d.getYval());
		if ( dimen == 2)return this;
		
		
		if ( dimen == 3){
			YE1upper.addAllOf(p1d.getYE1upper());
			return this;
		}
		
		
		if ( dimen == 4){	
			YE1upper.addAllOf(p1d.getYE1upper());
			YE1down.addAllOf(p1d.getYE1down());
			return this;
		}
		
		
		if ( dimen == 6){	
			YE1upper.addAllOf(p1d.getYE1upper());
			YE1down.addAllOf(p1d.getYE1down());
		     XE1left.addAllOf(p1d.getXE1left());
			 XE1right.addAllOf(p1d.getXE1right());
			 return this;
		}
		
			
			
		
		
		if ( dimen == 10){	
			
			YE1upper.addAllOf(p1d.getYE1upper());
			YE1down.addAllOf(p1d.getYE1down());
		     XE1left.addAllOf(p1d.getXE1left());
			 XE1right.addAllOf(p1d.getXE1right());
			 
			  YE2upper.addAllOf(p1d.getYE2upper());
			  YE2down.addAllOf(p1d.getYE2down());
			  XE2left.addAllOf(p1d.getXE2left());
		      XE2right.addAllOf(p1d.getXE2right());
				 
		
			
			
			    return this;
		}	
		
	
			return this;
		}

	
	
	
	
	
	/**
	 * Removes at the position
	 * @param position
	 * @return
	 */

	public P1D remove(int position) {

		
		X.remove(position) ;
		Y.remove(position) ;
		
		if ( dimen == 2)return this;
		
		
		if ( dimen == 3){
			YE1upper.remove(position);
			return this;
		}
		
		
		if ( dimen == 4){	
			YE1upper.remove(position);
			YE1down.remove(position);
			return this;
		}
		
		
		if ( dimen == 6){	
			YE1upper.remove(position);
			YE1down.remove(position);
		     XE1left.remove(position);
			 XE1right.remove(position);
			 return this;
		}
		
			
			
		
		
		if ( dimen == 10){	
			
			YE1upper.remove(position);
			YE1down.remove(position);
		     XE1left.remove(position);
			 XE1right.remove(position);
			 
			  YE2upper.remove(position);
			  YE2down.remove(position);
			  XE2left.remove(position);
		      XE2right.remove(position);
				 
		
			
			
			    return this;
		}	
		
	
			return this;
		}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 *  Returns the hash code value for this collection (onoy X,Y values)
	 */
	
	public int hashCode() {
		
		return X.hashCode()+Y.hashCode();
		
	}

	
/**
 *  Searches the list front to back for the index of value, starting at offset.
 *  Here we search for (X,Y) value. The search is done for pairs only.
 * @param valueX X value
 * @param valueY Y value
 * @return index of X with found pair value. If not pair found, return -1.  
 */
	
  public int indexOf(double valueX, double valueY) {
	    int i1=X.indexOf(valueX);
	    int i2=Y.indexOf(valueY);
	    if (i1>-1 && i2>-1 && i1 == i2) return i1;
	    return -1;
		
	}
	
  /**
   *  Searches  for the index of value, starting at offset, but backwards
   *  Here we search for (X,Y) pairs only.
   * @param valueX X value
   * @param valueY Y value
   * @return index with the found pair value  (or -1 if not found)  
   */
  	
    public int lastIndexOf(double valueX, double valueY) {
  		
  	    int i1=X.lastIndexOf(valueX);
  	    int i2=Y.lastIndexOf(valueY);
  		
  	    if (i1>-1 && i2>-1 &&  i1 == i2) return i1;
  	    return -1;
  		
  	}
  
  /**
   *  Searches the list front to back for the index of value, starting at offset.
   * @param valueX X value
   * @return index of found value
   */
  	
    public int indexOfX(double valueX) {
  		
  	    return X.indexOf(valueX);
  	  
  		
  	}
  
    /**
     *  Searches for value in X backwards. 
     * @param valueX X value
     * @return index of found value
     */
    	
      public int lastIndexOfX(double valueX) {
    		
    	    return X.lastIndexOf(valueX);
    	  
    		
    	}
    

    /**
     *  Searches for a Y value  starting at offset.
     *  Here we serch for (Y) value.
     * @param valueY Y value
     * @return index of found value
     */
    	
      public int indexOfY(double valueY) {
    		
    	    return Y.indexOf(valueY);
    	  
    		
    	}
  
      
      /**
       *  Searches for a Y value backewards.
       *  
       * @param valueY Y value
       * @return index of found value
       */
      	
        public int lastIndexOfY(double valueY) {
      		
      	    return Y.indexOf(valueY);
      	  
      		
      	}
  
  
  
  
  
  
	
	
	
	
	
	
	
	/**
	 * Set data in a form of DataArray
	 * 
	 * @param data
	 *            input data
	 * 
	 */
	public void setDataArray(DataArray data) {
		
		setDimension(10);
		for (int i=0; i<data.size(); i++){
			
			X.add(data.getX(i));
			Y.add(data.getY(i));
			XE1left.add(data.getX(i));
			XE1right.add(data.getY(i));
			XE2left.add(data.getXleft(i));
			XE2right.add(data.getXright(i));
			
			YE1upper.add(data.getYupper(i));
			YE1down.add(data.getYlower(i));
			YE2upper.add(data.getYupperSys(i));
			YE2down.add(data.getYlowerSys(i));
		}

	}

	/**
	 * Scale X-values (axis=0) or Y values (axis=1) and their errors with a
	 * scale factor. The object to be returned is the same, only X or Y are
	 * scaled.
	 * 
	 * @param axis
	 *            axis for which scaling is applied. set to 0 for X and set to 1
	 *            for Y.
	 * 
	 * @param scale
	 *            Scale factor
	 * 
	 */

	public void operScale(int axis, double scale) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return;
		}


		// scale X
	     if (axis == 0) { 
	 	 	for (int i = 0; i < size(); i++) {
	 			X.setQuick(i,getQuickX(i)*scale);
	         }
	 	 	
	 	 	
	 		if ( dimen == 6){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,getXleft(i)*scale);
	 				XE1right.setQuick(i,getXright(i)*scale);
	 				}
	 		}
	 		
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,getXleft(i)*scale);
	 				XE1right.setQuick(i,getXright(i)*scale);
	 				XE2left.setQuick(i,getXleftSys(i)*scale);
	 				XE2right.setQuick(i,getXrightSys(i)*scale);
	 				}
	 			    
	 		}	
	     }
	    	 
	    	 
	  // scale Y
	     if (axis == 1) { 
	 	 	for (int i = 0; i < size(); i++) {
	 			Y.setQuick(i,getQuickY(i)*scale);
	         }
	 		
	 	 	if ( dimen == 3){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,getYupper(i)*scale);
	 				}
	 		}
	 	 	
	 	 	if ( dimen == 6 || dimen == 4){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,getYupper(i)*scale);
	 				YE1down.setQuick(i,getYlower(i)*scale);
	 				}
	 		}
	 		
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,getYupper(i)*scale);
	 				YE1down.setQuick(i,getYlower(i)*scale);
	 				YE2upper.setQuick(i,getYupperSys(i)*scale);
	 				YE2down.setQuick(i,getYlowerSys(i)*scale);
	 				}
	 			    
	 		}	
	     }
				
	}

	/**
	 * Set 1st level errors to zero. All other errors are the same as before.
	 * 
	 * @param axis
	 *            axis = 0 for X, axis =1 for Y
	 */

	public void setErrToZero(int axis) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return;
		}

	     if (axis == 0) { 
	 		if ( dimen == 6){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,0);
	 				XE1right.setQuick(i,0);
	 				}
	 		}
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,0);
	 				XE1right.setQuick(i,0);
	 				}
	 			    
	 		}	
	     }
	    	 
	  // scale Y
	     if (axis == 1) { 
	 	 	if ( dimen == 3){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,0);
	 				}
	 		}	
	 	 	if ( dimen == 6 || dimen == 4){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,0);
	 				YE1down.setQuick(i,0);
	 				}
	 		}
	 		
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,0);
	 				YE1down.setQuick(i,0);
	 				}
	 			    
	 		}	
	 		
	     }
	     
	     
	     
		
	}
		
		
		
		
		
	

	/**
	 * Set all (1st and 2nd) level errors to zero. If axis=0, for X values if
	 * axis=1, for Y values
	 * 
	 * @param axis
	 *            axis = 0 for X, axis =1 for Y
	 */

	public void setErrAllToZero(String title, int axis) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return;
		}

	     if (axis == 0) { 
	 		if ( dimen == 6){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,0);
	 				XE1right.setQuick(i,0);
	 				
	 				}
	 		}
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,0);
	 				XE1right.setQuick(i,0);
	 				XE2left.setQuick(i,0);
	 				XE2right.setQuick(i,0);
	 				}
	 			    
	 		}	
	     }
	    	 
	  // scale Y
	     if (axis == 1) { 
	 	 	if ( dimen == 3){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,0);
	 				}
	 		}	
	 	 	if ( dimen == 6 || dimen == 4){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,0);
	 				YE1down.setQuick(i,0);
	 				}
	 		}
	 		
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,0);
	 				YE1down.setQuick(i,0);
	 				YE2upper.setQuick(i,0);
	 				YE2down.setQuick(i,0);	 				
	 				}
	 			    
	 		}	
	 		
	     }
	     
	     
	     
		
	}
		
		
		
		

	/**
	 * Set 2nd level errors to zero.
	 * 
	 * @param axis
	 *            axis = 0 for X, axis =1 for Y
	 */

	public void setErrSysToZero(int axis) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return;
		}

		
	     if (axis == 0) { 
	 		
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				XE2left.setQuick(i,0);
	 				XE2right.setQuick(i,0);
	 				}
	 			    
	 		}	
	     }
	    
	     if (axis == 1) { 
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				YE2upper.setQuick(i,0);
	 				YE2down.setQuick(i,0);
	 				}
	 			    
	 		}	
	 		
	     }
	     
		
		
		
		
		
	}

	/**
	 * Set 1st level error to sqrt of X or Y values. If axis=0, left and right
	 * errors will be set on X as sqrt(X). If axis=1, upper and lower errors
	 * will be set on Y as sqrt(Y).
	 * 
	 * @param axis
	 *            axis (=0 for X, 1 for Y).
	 */
	public void setErrSqrt(int axis) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return;
		}

		
			if (axis == 0) {
				if ( dimen>5){	
		 			for (int i = 0; i < size(); i++) {
		 				XE1left.setQuick(i,Math.sqrt(X.getQuick(i)));
		 				XE1right.setQuick(i,Math.sqrt(X.getQuick(i)));
		 				}
		 		}
		 		
			}
			
			
			 
		     if (axis == 1) { 
		 	 	if ( dimen == 3){	
		 			for (int i = 0; i < size(); i++) {
		 				YE1upper.setQuick(i,Math.sqrt(Y.getQuick(i)));
		 				}
		 		}	
		 	 	if ( dimen>3){	
		 			for (int i = 0; i < size(); i++) {
		 				double e=Math.sqrt(Y.getQuick(i));
		 				YE1upper.setQuick(i,e);
		 				YE1down.setQuick(i,e);
		 				}
		 		}
		 		
		 		
		 		
		     }
			
			
			
			
	}

	/**
	 * Scale 2nd level errors with a scale factor. If axis=0, the scaling is
	 * applied to errors on X, if axis=1, the scale factor is applied for errors
	 * on Y.
	 * 
	 * @param axis
	 *            axis to a which a scale factor is applied (=0 for X, =1 for
	 *            Y).
	 * @param scale
	 *            Scale factor to be applied to 2nd level errors defined by axis
	 * 
	 */

	public void operScaleErrSys(int axis, double scale) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return;
		}

		
		

		// scale X
	     if (axis == 0) { 
	 	 	
	 	 	
	 		if ( dimen > 6){	
	 			for (int i = 0; i < size(); i++) {
	 				XE2left.setQuick(i,getXleftSys(i)*scale);
	 				XE2right.setQuick(i,getXrightSys(i)*scale);
	 				}
	 		}
	 		
	 		
	     }
	    	 
	    	 
	  // scale Y
	     if (axis == 1) { 
	 		if ( dimen>6){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,getYupper(i)*scale);
	 				YE1down.setQuick(i,getYlower(i)*scale);
	 				YE2upper.setQuick(i,getYupperSys(i)*scale);
	 				YE2down.setQuick(i,getYlowerSys(i)*scale);
	 				}
	 			    
	 		}	
	     }
		
	}


     

         /**
         * Add all Y-values from another P1D. X-values will be unchanged. 1st and 2nd errors on Y are added
         * in quadrature assuming uncorrelated Y-values. Dimensions are assumed to be the same. 
         * @param p1d 
         *           P1D to be added to the original. Only Y-values are added, x-values are unchanged. 
         **/ 
         public void operPlusY(P1D p1) {

              if (dimen != p1.getDimension()){
                   ErrorMessage("Different dimensions for operPlusY() operation!");
                   return;
               }

                if (size() != p1.size()){
                   ErrorMessage("Different object sizes for operPlusY() operation!");
                   return;
               }



            for (int i = 0; i < size(); i++) {

               Y.setQuick(i,getY(i)+p1.getY(i));

               if ( dimen ==3 ) {
                 double yup=getYupper(i)*getYupper(i)+ p1.getYupper(i)*p1.getYupper(i);
                 YE1upper.setQuick(i,Math.sqrt(yup));
 
               }

               if ( dimen == 4 || dimen == 6) {
               double yup=getYupper(i)*getYupper(i)+ p1.getYupper(i)*p1.getYupper(i);
               double ylow=getYlower(i)*getYlower(i)+ p1.getYlower(i)*p1.getYlower(i);
               YE1upper.setQuick(i,Math.sqrt(yup));
               YE1down.setQuick(i,Math.sqrt(ylow));
               }


               if ( dimen > 6) { 
               double yup=getYupper(i)*getYupper(i)+ p1.getYupper(i)*p1.getYupper(i);
               double ylow=getYlower(i)*getYlower(i)+ p1.getYlower(i)*p1.getYlower(i);
               double yupS=getYupperSys(i)*getYupperSys(i)+ p1.getYupperSys(i)*p1.getYupperSys(i);
               double ylowS=getYlowerSys(i)*getYlowerSys(i)+ p1.getYlowerSys(i)*p1.getYlowerSys(i);
               
               YE1upper.setQuick(i,Math.sqrt(yup));
               YE1down.setQuick(i,Math.sqrt(ylow));
               YE2upper.setQuick(i,Math.sqrt(yupS));
               YE2down.setQuick(i,Math.sqrt(ylowS));
               }

            }

         }
	

	/**
	 * Scale 1st level errors with a scale factor. If axis=0, the scaling is
	 * applied to errors on X, if axis=1, the scale factor to errors on Y.
	 * 
	 * @param axis
	 *            axis to which a scale factor is applied (=0 for X, 1 for Y).
	 * @param scale
	 *            Scale factor to be applied to 1st level errors defined by axis
	 * 
	 */
	public void operScaleErr(int axis, double scale) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return;
		}


		if (axis==0){
	 		if ( dimen == 6){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,getXleft(i)*scale);
	 				XE1right.setQuick(i,getXright(i)*scale);
	 				}
	 		}
	 		
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				XE1left.setQuick(i,getXleft(i)*scale);
	 				XE1right.setQuick(i,getXright(i)*scale);
	 				XE2left.setQuick(i,getXleftSys(i)*scale);
	 				XE2right.setQuick(i,getXrightSys(i)*scale);
	 				}
	 			    
	 		}	
	     }
	    	 
	    	 
	  // scale Y
	     if (axis == 1) { 
	 	 	
	 	 	if ( dimen == 3){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,getYupper(i)*scale);
	 				}
	 		}
	 	 	
	 	 	if ( dimen ==4 || dimen ==6){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,getYupper(i)*scale);
	 				YE1down.setQuick(i,getYlower(i)*scale);
	 				}
	 		}
	 		
	 		if ( dimen == 10){	
	 			for (int i = 0; i < size(); i++) {
	 				YE1upper.setQuick(i,getYupper(i)*scale);
	 				YE1down.setQuick(i,getYlower(i)*scale);
	 				YE2upper.setQuick(i,getYupperSys(i)*scale);
	 				YE2down.setQuick(i,getYlowerSys(i)*scale);
	 				}
	 			    
	 		}	
	     }
		
		
	
	}

	/**
	 * Obtain a new P1D with 2nd-level errors on X or Y obtained from two P1D
	 * objects, one represents left on X (or lower on Y) error and the other
	 * represents the right on X (or upper on Y) error. To define X or Y axis,
	 * use "axis" variable. If axis=0, this method will be applied for X, if
	 * axis=1, this will be applied for Y. This means the central X,Y values are
	 * given by the original P1D, while 2nd level errors on X or Y of the
	 * original P1D are given by the differences: central-left and right-central
	 * for X or upper-central and central-lower for Y. The statistical errors
	 * (1st-level errors) are the same as for the original P1D. In case if it
	 * happen that the upper P1D is lower than the central value, it takes
	 * absolute value. This method is useful if you need to plot uncertainties
	 * associated with a measurement. Note: the method does create new object.
	 * 
	 * @param title
	 *            New Title
	 * @param axis
	 *            axis=0, errors will be set to X (left and right). If axis=1,
	 *            errors will be set as lower and upper error.
	 * @param left
	 *            P1D used to build left errors on the central values (in X,
	 *            when axis=0) or lower error on Y (when axis=1)
	 * @param right
	 *            P1D used to build right errors on the central values (in X,
	 *            when axis=0) or upper error on Y (when axis=1)
	 * 
	 * @return Same P1D container with the 2nd-level errors on X or Y given by
	 *         the differences between the original P1D and left (or right) P1Ds
	 *         (if axis=0), or upper and lower P1Ds (if axis=1)
	 */

	public P1D operErrSys(String title, int axis, P1D left, P1D right) {
		this.title = title;
		if (size() != left.size() || size() != right.size()) {
			ErrorMessage("Wrong size!");
			return this;
		}

		
		/*
		for (int i = 0; i < data.size(); i++) {

			if (axis == 0) {

				data.setQuickPoint(i, data.getX(i), data.getY(i),
						data.getXleft(i), data.getXright(i), data.getYupper(i),
						data.getYlower(i),
						Math.abs(data.getX(i) - left.getX(i)),
						Math.abs(right.getX(i) - data.getX(i)),
						data.getYupperSys(i), data.getYlowerSys(i));
			}

			if (axis == 1) {
				data.setQuickPoint(i, data.getX(i), data.getY(i),
						data.getXleft(i), data.getXright(i), data.getYupper(i),
						data.getYlower(i), data.getXleftSys(i),
						data.getXrightSys(i),
						Math.abs(right.getY(i) - data.getY(i)),
						Math.abs(data.getY(i) - left.getY(i)));

			}

		}
		;
		data.allUpdate();
		
		*/
		return this;

	}

	/**
	 * Operations on P1D container: add, subtract, multiply, divide. Keep the
	 * same graphical attributes and the title as for the original container.
	 * Note, no new object is created.
	 * 
	 * @param a
	 *            Input P1D container
	 * 
	 * @param what
	 *            the operation type (+,-,*,/)
	 * @return 0 if no problems.
	 */
	public int oper(P1D a, String what) {

		return  oper(a, getTitle(), what);

	}

	/**
	 * Operations on P1D containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes, They are all applied for "Y", rather  than for X.
	 * Errors on Y (1st and 2nd) will be propogared assuming independence of the the containers.
	 * Only 1st level error (statisticals) are propogared, the second-level errors are the same.
	 * 
	 * @param a
	 *            Input P1D container for operation (may contain errors on Y).
	 * @param title
	 *            New title
	 * @param what
	 *            String representing the operation: <br>"+" add a P1D container to
	 *            the original; <br>"-" subtract a P1D from the original; <br>"*"
	 *            multiply; <br>"/" divide by P1D
	 * @return error code: 
	 *   0 means OK<br>
	 *   1: means divistion by zero <br>
	 *   2: error during error evalutaion
	 */

	public int oper(P1D a, String title, String what) {

		what = what.trim();

		// first check them
		if (size() != a.size()) {
			ErrorMessage("Sizes of the P1Ds are different!");
			return 1;
		}

		if (what.equals("+")==false && what.equals("-")==false && what.equals("/")==false && what.equals("/")) { 
			ErrorMessage("Operation \"" + what + "\" is not implemented");
			return 1;	
	  } 
			
			
		
		
		
		    if (dimen==2) {
			if (what.equals("+")){
					for (int i = 0; i < size(); i++) 
					        Y.setQuick(i,Y.getQuick(i) + a.getY(i));
			return 0;         
			}	
			if (what.equals("-")){
				for (int i = 0; i < size(); i++) 
				        Y.setQuick(i,Y.getQuick(i) - a.getY(i));
		    return 0;     
			}
			
			if (what.equals("*")){
				for (int i = 0; i < size(); i++) 
				        Y.setQuick(i,Y.getQuick(i)*a.getY(i));
		    return 0;     
			}
           
			if (what.equals("/")){
				for (int i = 0; i < size(); i++) {
					    if (a.getY(i)==0) return 1;
					    Y.setQuick(i,Y.getQuick(i)/a.getY(i));
				       }
					  return 0;     
			}
		    } // end 2-D
		    
		    
		    if (dimen==3) {
				if (what.equals("+")){
						for (int i = 0; i < size(); i++) {
						        Y.setQuick(i,Y.getQuick(i) + a.getY(i));
						        double y = Math.sqrt(a.getYupper(i) * a.getYupper(i)
										+ a.getYupper(i) * a.getYupper(i));    
						        YE1upper.setQuick(i,y);
						}
				return 0;         
				}	
				if (what.equals("-")){
					for (int i = 0; i < size(); i++) {
					        Y.setQuick(i,Y.getQuick(i) - a.getY(i));
			                double y = Math.sqrt(a.getYupper(i) * a.getYupper(i)
								+ a.getYupper(i) * a.getYupper(i));    
				            YE1upper.setQuick(i,y); 
					}
			    return 0;     
				}
				
				if (what.equals("*")){
					for (int i = 0; i < size(); i++) { 
	                        double y=Y.getQuick(i)*a.getY(i);
						    Y.setQuick(i,y);
							double ad1 = a.getY(i);
							double d1 =  getY(i);
							double ax1;
							double x1;
							if (ad1 != 0)ax1 = a.getYupper(i) / ad1;
							else ax1 = 0;
							if (d1 != 0) x1 = getYupper(i) / d1;
							else x1 = 0;
							double e = y * Math.sqrt(ax1 * ax1 + x1 * x1);
							YE1upper.setQuick(i,e); 
					
					}
			    return 0;     
				}
				
				if (what.equals("/")){
					for (int i = 0; i < size(); i++) { 
	                        double y=Y.getQuick(i)/a.getY(i);
						    Y.setQuick(i,y);
							double ad1 = a.getY(i);
							double d1 =  getY(i);
							double ax1;
							double x1;
							if (ad1 != 0)ax1 = a.getYupper(i) / ad1;
							else ax1 = 0;
							if (d1 != 0) x1 = getYupper(i) / d1;
							else x1 = 0;
							double e = y * Math.sqrt(ax1 * ax1 + x1 * x1);
							YE1upper.setQuick(i,e); 
					
					}
			    return 0;     
				}
			    } // end 3D
		    
			
			
		    
		    // 
		    if (dimen>3) {
				if (what.equals("+")){
						for (int i = 0; i < size(); i++) {
						        Y.setQuick(i,Y.getQuick(i) + a.getY(i));
						        double yU = Math.sqrt(a.getYupper(i) * a.getYupper(i)
										+ a.getYupper(i) * a.getYupper(i));
						        double yD = Math.sqrt(a.getYlower(i) * a.getYlower(i)
										+ a.getYlower(i) * a.getYlower(i));
						        YE1upper.setQuick(i,yU);
						        YE1down.setQuick(i,yD);
						}
				return 0;         
				}	
				if (what.equals("-")){
					for (int i = 0; i < size(); i++) {
					        Y.setQuick(i,Y.getQuick(i) - a.getY(i));
				        	double yU = Math.sqrt(a.getYupper(i) * a.getYupper(i)
							+ a.getYupper(i) * a.getYupper(i));
			                double yD = Math.sqrt(a.getYlower(i) * a.getYlower(i)
							+ a.getYlower(i) * a.getYlower(i));
			        YE1upper.setQuick(i,yU);
			        YE1down.setQuick(i,yD);
					}
			    return 0;     
				}
				
				if (what.equals("*")){
					for (int i = 0; i < size(); i++) { 
	                        double y=Y.getQuick(i)*a.getY(i);
						    Y.setQuick(i,y);
							double ad1 = a.getY(i);
							double d1 =  getY(i);
							double ax1;
							double x1;
							if (ad1 != 0)ax1 = a.getYupper(i) / ad1;
							else ax1 = 0;
							if (d1 != 0) x1 = getYupper(i) / d1;
							else x1 = 0;
							double e1 = y * Math.sqrt(ax1 * ax1 + x1 * x1);
							YE1upper.setQuick(i,e1); 
					
							double ad2 = a.getY(i);
							double d2 =  getY(i);
							double ax2;
							double x2;
							if (ad2 != 0)ax2 = a.getYlower(i) / ad2;
							else ax2 = 0;
							if (d2 != 0) x2 = getYlower(i) / d2;
							else x2 = 0;
							double e2 = y * Math.sqrt(ax2 * ax2 + x2 * x2);
							YE1down.setQuick(i,e2); 
							
							
					}
			    return 0;     
				}
				
				if (what.equals("/")){
					for (int i = 0; i < size(); i++) { 
	                      	
						double y=Y.getQuick(i)/a.getY(i);
					    Y.setQuick(i,y);
						double ad1 = a.getY(i);
						double d1 =  getY(i);
						double ax1;
						double x1;
						if (ad1 != 0)ax1 = a.getYupper(i) / ad1;
						else ax1 = 0;
						if (d1 != 0) x1 = getYupper(i) / d1;
						else x1 = 0;
						double e1 = y * Math.sqrt(ax1 * ax1 + x1 * x1);
						YE1upper.setQuick(i,e1); 
				
						double ad2 = a.getY(i);
						double d2 =  getY(i);
						double ax2=0;
						double x2;
						if (ad2 != 0)ax2 = a.getYlower(i) / ad2;
						else ax1 = 0;
						if (d2 != 0) x2 = getYlower(i) / d2;
						else x2 = 0;
						double e2 = y * Math.sqrt(ax2 * ax2 + x2 * x2);
						YE1down.setQuick(i,e2);
						
					} // end loop
					return 0;
				  } // and
				}; // end dimen>3
			
		   
		    return 1;
		    
	}

	/**
	 * Operations on P1D containers: add, subtract, multiply, divide. Keep the
	 * same graphical attributes. Correlated errors are taken into account via
	 * additional P1D data holder which keeps correlation coefficients. The size
	 * of this P1D holder should be equal to the original size of P1D.
	 * <p>
	 * This container assumes the dimension 10 (2 X,Y values and 6 errors)
	 * 
	 * @param a
	 *            Input P1D container for the operation
	 * @param title
	 *            New title
	 * @param what
	 *            String representing the operation: "+" add a P1D to the
	 *            original; "-" subtract a P1D from the original "*"; multiply
	 *            "/" divide
	 * @param how
	 *            how the operation should be performed: "X" - for X values; "Y"
	 *            - do it for Y values; XY - do for X and Y values. Normally,
	 *            however, you should do this for the Y option.
	 * 
	 * @param corr
	 *            P1D container which keeps correlation coefficients for each
	 *            point. In most general case, this container should be filled
	 *            as: add(0,0,x1,x2,x3,x4,y1,y2,y3,y4), where x1 (left),
	 *            x2(right), x3(leftSys), x4(rightSys) - coefficients on X y1
	 *            (up), y2(down), y3(upSys), x4(downSys) - coefficients on Y.
	 *            For example, in a simplest case when Y has 1st level
	 *            (symmetrical statistical) errors and X does not have any, you
	 *            need just define "corr" by filling it with:
	 *            add(0,0,0,0,0,0,c,c,0,0), where c is a correlation
	 *            coefficient. If "Y" values have both statistical and
	 *            systematic errors, use the correlation P1D holder of the form
	 *            add(0,0,0,0,0,0,c,c,c1,c2),
	 * 
	 * 
	 * @return Output P1D container
	 */

	public P1D oper(P1D a, String title, String what, String how, P1D corr) {

		what = what.trim();

		if (size() != a.size()) {
			ErrorMessage("Sizes of the P1Ds are different!");
			return this;
		}

		if (size() != corr.size()) {
			ErrorMessage("Sizes of the P1D and P1D with correlations are different!");
			return this;
		}

		// check
		if (!what.equals("+") && !what.equals("-") && !what.equals("*")
				&& !what.equals("*")) {
			ErrorMessage("Operation \"" + what + "\" is not implemented");
			return this;
		}

		
		
		if (dimension()==3){
	    ValueErr tmp = new ValueErr();
			
		final int NT = 8;
		ValueErr erros1[] = new ValueErr[NT];
		ValueErr erros2[] = new ValueErr[NT];
		double cc[] = new double[NT];
		for (int i = 0; i < size(); i++) {
			erros1[0] = new ValueErr(getX(i),0);
			erros1[1] = new ValueErr(getX(i),0);
			erros1[2] = new ValueErr(getX(i),0);
			erros1[3] = new ValueErr(getX(i),0);
			erros1[4] = new ValueErr(getY(i), getYupper(i));
			erros1[5] = new ValueErr(getY(i),0);
			erros1[6] = new ValueErr(getY(i),0);
			erros1[7] = new ValueErr(getY(i),0);

			erros2[0] = new ValueErr(a.getX(i),0);
			erros2[1] = new ValueErr(a.getX(i),0);
			erros2[2] = new ValueErr(a.getX(i),0);
			erros2[3] = new ValueErr(a.getX(i),0);
			erros2[4] = new ValueErr(a.getY(i), a.getYupper(i));
			erros2[5] = new ValueErr(a.getY(i),0);
			erros2[6] = new ValueErr(a.getY(i),0);
			erros2[7] = new ValueErr(a.getY(i),0);

			cc[0] = 0;
			cc[1] = 0;
			cc[2] = 0;
			cc[3] = 0;
			cc[4] = corr.getYupper(i);
			cc[5] = 0;
			cc[6] = 0;
			cc[7] = 0;

			if (what.equals("+")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.plus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("-")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.minus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("*")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.times(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("/")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.divide(erros1[j], erros2[j], cc[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros1[0].getVal();
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				setQuick(i, x, y,yu);
			}


			if (how.equalsIgnoreCase("Y")) {
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				setQuick(i, getX(i), y, yu);
			}

		}
		} // end dimesion 3
		
		
		
		
		
		if (dimension()==4){
		final int NT = 8;
		ValueErr erros1[] = new ValueErr[NT];
		ValueErr erros2[] = new ValueErr[NT];
		double cc[] = new double[NT];
		for (int i = 0; i < size(); i++) {
			erros1[0] = new ValueErr(getX(i),0);
			erros1[1] = new ValueErr(getX(i),0);
			erros1[2] = new ValueErr(getX(i),0);
			erros1[3] = new ValueErr(getX(i),0);
			erros1[4] = new ValueErr(getY(i), getYupper(i));
			erros1[5] = new ValueErr(getY(i), getYlower(i));
			erros1[6] = new ValueErr(getY(i), 0);
			erros1[7] = new ValueErr(getY(i), 0);

			erros2[0] = new ValueErr(a.getX(i),0);
			erros2[1] = new ValueErr(a.getX(i),0);
			erros2[2] = new ValueErr(a.getX(i),0);
			erros2[3] = new ValueErr(a.getX(i),0);
			erros2[4] = new ValueErr(a.getY(i), a.getYupper(i));
			erros2[5] = new ValueErr(a.getY(i), a.getYlower(i));
			erros2[6] = new ValueErr(a.getY(i),0);
			erros2[7] = new ValueErr(a.getY(i),0);

			cc[0] = 0;
			cc[1] = 0;
			cc[2] = 0;
			cc[3] = 0;
			cc[4] = corr.getYupper(i);
			cc[5] = corr.getYlower(i);
			cc[6] = 0;
			cc[7] = 0;

			
			ValueErr tmp=new ValueErr();
			if (what.equals("+")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.plus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("-")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.minus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("*")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.times(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("/")) {
				for (int j = 0; j < NT; j++)
					erros1[j] =  tmp.divide(erros1[j], erros2[j], cc[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros1[0].getVal();
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				double yl = erros1[5].getErr();
				setQuick(i, x, y, yu, yl);
			}


			if (how.equalsIgnoreCase("Y")) {
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				double yl = erros1[5].getErr();
				setQuick(i, getX(i), y,  yu, yl);
			}

		}
		} // end dimesion 10
		
		
		
		
		if (dimension()==6){
		final int NT = 8;
		ValueErr erros1[] = new ValueErr[NT];
		ValueErr erros2[] = new ValueErr[NT];
		double cc[] = new double[NT];
		for (int i = 0; i < size(); i++) {
			erros1[0] = new ValueErr(getX(i), getXleft(i));
			erros1[1] = new ValueErr(getX(i), getXright(i));
			erros1[2] = new ValueErr(getX(i), 0);
			erros1[3] = new ValueErr(getX(i), 0);
			erros1[4] = new ValueErr(getY(i), getYupper(i));
			erros1[5] = new ValueErr(getY(i), getYlower(i));
			erros1[6] = new ValueErr(getY(i), 0);
			erros1[7] = new ValueErr(getY(i), 0);

			erros2[0] = new ValueErr(a.getX(i), a.getXleft(i));
			erros2[1] = new ValueErr(a.getX(i), a.getXright(i));
			erros2[2] = new ValueErr(a.getX(i), 0);
			erros2[3] = new ValueErr(a.getX(i), 0);
			erros2[4] = new ValueErr(a.getY(i), a.getYupper(i));
			erros2[5] = new ValueErr(a.getY(i), a.getYlower(i));
			erros2[6] = new ValueErr(a.getY(i), 0);
			erros2[7] = new ValueErr(a.getY(i), 0);

			cc[0] = corr.getXleft(i);
			cc[1] = corr.getXright(i);
			cc[2] = 0;
			cc[3] = 0;
			cc[4] = corr.getYupper(i);
			cc[5] = corr.getYlower(i);
			cc[6] = 0;
			cc[7] = 0;

			ValueErr tmp=new ValueErr();
			if (what.equals("+")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.plus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("-")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.minus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("*")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.times(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("/")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.divide(erros1[j], erros2[j], cc[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros1[0].getVal();
				double xl = erros1[0].getErr();
				double xr = erros1[1].getErr();
				double xls = erros1[2].getErr();
				double xrs = erros1[3].getErr();
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				double yl = erros1[5].getErr();
				double yus = erros1[6].getErr();
				double yls = erros1[7].getErr();
				setQuick(i, x, y, xl, xr, yu, yl);
			}

			if (how.equalsIgnoreCase("X")) {
				double x = erros1[0].getVal();
				double xl = erros1[0].getErr();
				double xr = erros1[1].getErr();
				double xls = erros1[2].getErr();
				double xrs = erros1[3].getErr();
				setQuick(i, x, getY(i), xl, xr,
						getYupper(i), getYlower(i), xls, xrs,
						getYupperSys(i), getYlowerSys(i));
			}
			if (how.equalsIgnoreCase("Y")) {
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				double yl = erros1[5].getErr();
				double yus = erros1[6].getErr();
				double yls = erros1[7].getErr();
				setQuick(i, getX(i), y, getXleft(i),
						getXright(i), yu, yl);
			}

		}
		} // end dimesion 6
		
		
		
		
		
		
		
		
		
		
		
		
		
		if (dimension()==10){
	    ValueErr tmp=new ValueErr();
		final int NT = 8;
		ValueErr erros1[] = new ValueErr[NT];
		ValueErr erros2[] = new ValueErr[NT];
		double cc[] = new double[NT];
		for (int i = 0; i < size(); i++) {
			erros1[0] = new ValueErr(getX(i), getXleft(i));
			erros1[1] = new ValueErr(getX(i), getXright(i));
			erros1[2] = new ValueErr(getX(i), getXleftSys(i));
			erros1[3] = new ValueErr(getX(i), getXrightSys(i));
			erros1[4] = new ValueErr(getY(i), getYupper(i));
			erros1[5] = new ValueErr(getY(i), getYlower(i));
			erros1[6] = new ValueErr(getY(i), getYupperSys(i));
			erros1[7] = new ValueErr(getY(i), getYlowerSys(i));

			erros2[0] = new ValueErr(a.getX(i), a.getXleft(i));
			erros2[1] = new ValueErr(a.getX(i), a.getXright(i));
			erros2[2] = new ValueErr(a.getX(i), a.getXleftSys(i));
			erros2[3] = new ValueErr(a.getX(i), a.getXrightSys(i));
			erros2[4] = new ValueErr(a.getY(i), a.getYupper(i));
			erros2[5] = new ValueErr(a.getY(i), a.getYlower(i));
			erros2[6] = new ValueErr(a.getY(i), a.getYupperSys(i));
			erros2[7] = new ValueErr(a.getY(i), a.getYlowerSys(i));

			cc[0] = corr.getXleft(i);
			cc[1] = corr.getXright(i);
			cc[2] = corr.getXleftSys(i);
			cc[3] = corr.getXrightSys(i);
			cc[4] = corr.getYupper(i);
			cc[5] = corr.getYlower(i);
			cc[6] = corr.getYupperSys(i);
			cc[7] = corr.getYlowerSys(i);

			if (what.equals("+")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.plus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("-")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.minus(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("*")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.times(erros1[j], erros2[j], cc[j]);
			}

			if (what.equals("/")) {
				for (int j = 0; j < NT; j++)
					erros1[j] = tmp.divide(erros1[j], erros2[j], cc[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros1[0].getVal();
				double xl = erros1[0].getErr();
				double xr = erros1[1].getErr();
				double xls = erros1[2].getErr();
				double xrs = erros1[3].getErr();
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				double yl = erros1[5].getErr();
				double yus = erros1[6].getErr();
				double yls = erros1[7].getErr();
				setQuick(i, x, y, xl, xr, yu, yl, xls, xrs, yus, yls);
			}

			if (how.equalsIgnoreCase("X")) {
				double x = erros1[0].getVal();
				double xl = erros1[0].getErr();
				double xr = erros1[1].getErr();
				double xls = erros1[2].getErr();
				double xrs = erros1[3].getErr();
				setQuick(i, x, getY(i), xl, xr,
						getYupper(i), getYlower(i), xls, xrs,
						getYupperSys(i), getYlowerSys(i));
			}
			if (how.equalsIgnoreCase("Y")) {
				double y = erros1[4].getVal();
				double yu = erros1[4].getErr();
				double yl = erros1[5].getErr();
				double yus = erros1[6].getErr();
				double yls = erros1[7].getErr();
				setQuick(i, getX(i), y, getXleft(i),
						getXright(i), yu, yl, getXleftSys(i),
						getXrightSys(i), yus, yls);
			}

		}
		} // end dimesion 10
		
     

		
		return this;

	}

	/**
	 * Transform a P1D data holder to some function with error propagation (for
	 * both levels)
	 * 
	 * @param what
	 *            what operation should be performed: "inverse" - inverse to
	 *            (1/a); "sqrt" - sqrt(a); "exp" - exp(a); "log" - log10(a);
	 *            "cos" - cos(a) ;"sin" - sin(a); "tan" - tan(a); "acos" -
	 *            acos(a); "asin" - asin(a); "atan" - atan(a); "cosh" - cosh(a);
	 *            "sinh" - sinh(a); "tanh" - tanh(a); "square" - a**2
	 * 
	 * @param how
	 *            how the operation should be performed: "X" - for X values; "Y"
	 *            - do it for Y values; XY - do for X and Y
	 * 
	 * @return transformed P1D object
	 */

	// http://www.ee.ucl.ac.uk/~mflanaga/java/tmp.html
	public P1D move(String what, String how) {


		
		//if ( dimen !=  10) {
		//	ErrorMessage(" You should initialize the container with dimension 10 for this operation.");	
		//
		// }
		

		final int NT = 8;
		ValueErr erros[] = new ValueErr[NT];

		
		if (dimension()==2){
			
			
		ValueErr tmp=new ValueErr();
		for (int i = 0; i < size(); i++) {
			erros[0] = new ValueErr(X.getQuick(i),0);
			erros[1] = new ValueErr(X.getQuick(i),0);
			erros[2] = new ValueErr(X.getQuick(i),0);
			erros[3] = new ValueErr(X.getQuick(i),0);
			erros[4] = new ValueErr(Y.getQuick(i),0);
			erros[5] = new ValueErr(Y.getQuick(i),0);
			erros[6] = new ValueErr(Y.getQuick(i),0);
			erros[7] = new ValueErr(Y.getQuick(i),0);

			if (what.equalsIgnoreCase("inverse")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.inverse(erros[j]);
			}
			if (what.equalsIgnoreCase("exp")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.exp(erros[j]);
			}
			if (what.equalsIgnoreCase("log")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.log(erros[j]);
			}
			if (what.equalsIgnoreCase("sqrt")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sqrt(erros[j]);
			}
			if (what.equalsIgnoreCase("square")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.square(erros[j]);
			}
			if (what.equalsIgnoreCase("sin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sin(erros[j]);
			}
			if (what.equalsIgnoreCase("cos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cos(erros[j]);
			}
			if (what.equalsIgnoreCase("tan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tan(erros[j]);
			}
			if (what.equalsIgnoreCase("sinh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sinh(erros[j]);
			}
			if (what.equalsIgnoreCase("cosh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cosh(erros[j]);
			}
			if (what.equalsIgnoreCase("tanh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tanh(erros[j]);
			}
			if (what.equalsIgnoreCase("asin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.asin(erros[j]);
			}
			if (what.equalsIgnoreCase("acos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.acos(erros[j]);
			}
			if (what.equalsIgnoreCase("atan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.atan(erros[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros[0].getVal();				
				double y = erros[4].getVal();
				X.setQuick(i,x);
				Y.setQuick(i,y);
				
			}

			if (how.equalsIgnoreCase("X")) {
				double x = erros[0].getVal();
				X.setQuick(i,x);
				
			}

			if (how.equalsIgnoreCase("Y")) {
				double y = erros[4].getVal();

			
				Y.setQuick(i,y);
				
				
			}

		}
	
		} // end dimesion 2
		
		
		
		
		
		
		
		
		if (dimension()==3){
			ValueErr tmp = new ValueErr();
		for (int i = 0; i < size(); i++) {
			erros[0] = new ValueErr(X.getQuick(i),0);
			erros[1] = new ValueErr(X.getQuick(i),0);
			erros[2] = new ValueErr(X.getQuick(i),0);
			erros[3] = new ValueErr(X.getQuick(i),0);
			erros[4] = new ValueErr(Y.getQuick(i), YE1upper.getQuick(i));
			erros[5] = new ValueErr(Y.getQuick(i),0);
			erros[6] = new ValueErr(Y.getQuick(i),0);
			erros[7] = new ValueErr(Y.getQuick(i),0);

			if (what.equalsIgnoreCase("inverse")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.inverse(erros[j]);
			}
			if (what.equalsIgnoreCase("exp")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.exp(erros[j]);
			}
			if (what.equalsIgnoreCase("log")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.log(erros[j]);
			}
			if (what.equalsIgnoreCase("sqrt")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sqrt(erros[j]);
			}
			if (what.equalsIgnoreCase("square")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.square(erros[j]);
			}
			if (what.equalsIgnoreCase("sin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sin(erros[j]);
			}
			if (what.equalsIgnoreCase("cos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cos(erros[j]);
			}
			if (what.equalsIgnoreCase("tan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tan(erros[j]);
			}
			if (what.equalsIgnoreCase("sinh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sinh(erros[j]);
			}
			if (what.equalsIgnoreCase("cosh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cosh(erros[j]);
			}
			if (what.equalsIgnoreCase("tanh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tanh(erros[j]);
			}
			if (what.equalsIgnoreCase("asin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.asin(erros[j]);
			}
			if (what.equalsIgnoreCase("acos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.acos(erros[j]);
			}
			if (what.equalsIgnoreCase("atan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.atan(erros[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros[0].getVal();	
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				
				
				X.setQuick(i,x);
				Y.setQuick(i,y);			
				YE1upper.setQuick(i,yu);
				
				
				
			}

			

			if (how.equalsIgnoreCase("Y")) {
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				
			
				Y.set(i,y);
				YE1upper.setQuick(i,yu);
				
				
			}

		}
	
		} // end dimesion 10
		
		
		if (dimension()==4){
			ValueErr tmp = new ValueErr();
		for (int i = 0; i < size(); i++) {
			erros[0] = new ValueErr(X.getQuick(i),0);
			erros[1] = new ValueErr(X.getQuick(i),0);
			erros[2] = new ValueErr(X.getQuick(i),0);
			erros[3] = new ValueErr(X.getQuick(i),0);
			erros[4] = new ValueErr(Y.getQuick(i), YE1upper.getQuick(i));
			erros[5] = new ValueErr(Y.getQuick(i), YE1down.getQuick(i));
			erros[6] = new ValueErr(Y.getQuick(i), 0);
			erros[7] = new ValueErr(Y.getQuick(i), 0);

			if (what.equalsIgnoreCase("inverse")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.inverse(erros[j]);
			}
			if (what.equalsIgnoreCase("exp")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.exp(erros[j]);
			}
			if (what.equalsIgnoreCase("log")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.log(erros[j]);
			}
			if (what.equalsIgnoreCase("sqrt")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sqrt(erros[j]);
			}
			if (what.equalsIgnoreCase("square")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.square(erros[j]);
			}
			if (what.equalsIgnoreCase("sin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sin(erros[j]);
			}
			if (what.equalsIgnoreCase("cos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cos(erros[j]);
			}
			if (what.equalsIgnoreCase("tan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tan(erros[j]);
			}
			if (what.equalsIgnoreCase("sinh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sinh(erros[j]);
			}
			if (what.equalsIgnoreCase("cosh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cosh(erros[j]);
			}
			if (what.equalsIgnoreCase("tanh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tanh(erros[j]);
			}
			if (what.equalsIgnoreCase("asin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.asin(erros[j]);
			}
			if (what.equalsIgnoreCase("acos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.acos(erros[j]);
			}
			if (what.equalsIgnoreCase("atan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.atan(erros[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros[0].getVal();			
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				double yl = erros[5].getErr();
				X.setQuick(i,x);
				Y.setQuick(i,y);				
				YE1upper.setQuick(i,yu);
				YE1down.setQuick(i,yl);
				
				
				
			}

			
			if (how.equalsIgnoreCase("Y")) {
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				double yl = erros[5].getErr();
				
			
				Y.set(i,y);
				YE1upper.setQuick(i,yu);
				YE1down.setQuick(i,yl);
				
				
			}

		}
	
		} // end dimesion 4
		
		
		
		if (dimension()==6){
		ValueErr tmp = new ValueErr();
			
		for (int i = 0; i < size(); i++) {
			erros[0] = new ValueErr(X.getQuick(i), XE1left.getQuick(i));
			erros[1] = new ValueErr(X.getQuick(i), XE1right.getQuick(i));
			erros[2] = new ValueErr(X.getQuick(i), 0);
			erros[3] = new ValueErr(X.getQuick(i), 0);
			erros[4] = new ValueErr(Y.getQuick(i), YE1upper.getQuick(i));
			erros[5] = new ValueErr(Y.getQuick(i), YE1down.getQuick(i));
			erros[6] = new ValueErr(Y.getQuick(i), 0);
			erros[7] = new ValueErr(Y.getQuick(i), 0);

			if (what.equalsIgnoreCase("inverse")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.inverse(erros[j]);
			}
			if (what.equalsIgnoreCase("exp")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.exp(erros[j]);
			}
			if (what.equalsIgnoreCase("log")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.log(erros[j]);
			}
			if (what.equalsIgnoreCase("sqrt")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sqrt(erros[j]);
			}
			if (what.equalsIgnoreCase("square")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.square(erros[j]);
			}
			if (what.equalsIgnoreCase("sin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sin(erros[j]);
			}
			if (what.equalsIgnoreCase("cos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cos(erros[j]);
			}
			if (what.equalsIgnoreCase("tan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tan(erros[j]);
			}
			if (what.equalsIgnoreCase("sinh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sinh(erros[j]);
			}
			if (what.equalsIgnoreCase("cosh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cosh(erros[j]);
			}
			if (what.equalsIgnoreCase("tanh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tanh(erros[j]);
			}
			if (what.equalsIgnoreCase("asin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.asin(erros[j]);
			}
			if (what.equalsIgnoreCase("acos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.acos(erros[j]);
			}
			if (what.equalsIgnoreCase("atan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.atan(erros[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros[0].getVal();
				double xl = erros[0].getErr();
				double xr = erros[1].getErr();
				
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				double yl = erros[5].getErr();
				
				
				X.set(i,x);
				Y.set(i,y);
				XE1left.set(i,xl);
				XE1right.set(i,xr);			
				YE1upper.set(i,yu);
				YE1down.set(i,yl);
				
				
				
			}

			if (how.equalsIgnoreCase("X")) {
				double x = erros[0].getVal();
				double xl = erros[0].getErr();
				double xr = erros[1].getErr();
				
				
				X.setQuick(i,x);
				XE1left.setQuick(i,xl);
				XE1right.setQuick(i,xr);
				
				
			}

			if (how.equalsIgnoreCase("Y")) {
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				double yl = erros[5].getErr();
			
			
				Y.set(i,y);
				YE1upper.setQuick(i,yu);
				YE1down.setQuick(i,yl);
				
				
			}

		}
	
		} // end dimesion 6
		
		
		
		
		
		
		
		if (dimension()==10){
			
		ValueErr  tmp = new ValueErr();
		
		for (int i = 0; i < size(); i++) {
			erros[0] = new ValueErr(X.getQuick(i), XE1left.getQuick(i));
			erros[1] = new ValueErr(X.getQuick(i), XE1right.getQuick(i));
			erros[2] = new ValueErr(X.getQuick(i), XE2left.getQuick(i));
			erros[3] = new ValueErr(X.getQuick(i), XE2right.getQuick(i));
			erros[4] = new ValueErr(Y.getQuick(i), YE1upper.getQuick(i));
			erros[5] = new ValueErr(Y.getQuick(i), YE1down.getQuick(i));
			erros[6] = new ValueErr(Y.getQuick(i), YE2upper.getQuick(i));
			erros[7] = new ValueErr(Y.getQuick(i), YE2down.getQuick(i));

			if (what.equalsIgnoreCase("inverse")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.inverse(erros[j]);
			}
			if (what.equalsIgnoreCase("exp")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.exp(erros[j]);
			}
			if (what.equalsIgnoreCase("log")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.log(erros[j]);
			}
			if (what.equalsIgnoreCase("sqrt")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sqrt(erros[j]);
			}
			if (what.equalsIgnoreCase("square")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.square(erros[j]);
			}
			if (what.equalsIgnoreCase("sin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sin(erros[j]);
			}
			if (what.equalsIgnoreCase("cos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cos(erros[j]);
			}
			if (what.equalsIgnoreCase("tan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tan(erros[j]);
			}
			if (what.equalsIgnoreCase("sinh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.sinh(erros[j]);
			}
			if (what.equalsIgnoreCase("cosh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.cosh(erros[j]);
			}
			if (what.equalsIgnoreCase("tanh")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.tanh(erros[j]);
			}
			if (what.equalsIgnoreCase("asin")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.asin(erros[j]);
			}
			if (what.equalsIgnoreCase("acos")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.acos(erros[j]);
			}
			if (what.equalsIgnoreCase("atan")) {
				for (int j = 0; j < NT; j++)
					erros[j] = tmp.atan(erros[j]);
			}

			if (how.equalsIgnoreCase("XY") || how.equalsIgnoreCase("YX")) {
				double x = erros[0].getVal();
				double xl = erros[0].getErr();
				double xr = erros[1].getErr();
				double xls = erros[2].getErr();
				double xrs = erros[3].getErr();
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				double yl = erros[5].getErr();
				double yus = erros[6].getErr();
				double yls = erros[7].getErr();
				
				X.set(i,x);
				Y.set(i,y);
				XE1left.set(i,xl);
				XE1right.set(i,xr);
				XE2left.set(i,xls);
				XE2right.set(i,xrs);
				YE1upper.set(i,yu);
				YE1down.set(i,yl);
				YE2upper.set(i,yus);
				YE2down.set(i,yls);
				
				
			}

			if (how.equalsIgnoreCase("X")) {
				double x = erros[0].getVal();
				double xl = erros[0].getErr();
				double xr = erros[1].getErr();
				double xls = erros[2].getErr();
				double xrs = erros[3].getErr();
				
				X.setQuick(i,x);
				XE1left.setQuick(i,xl);
				XE1right.setQuick(i,xr);
				XE2left.setQuick(i,xls);
				XE2right.setQuick(i,xrs);
				
				
			}

			if (how.equalsIgnoreCase("Y")) {
				double y = erros[4].getVal();
				double yu = erros[4].getErr();
				double yl = erros[5].getErr();
				double yus = erros[6].getErr();
				double yls = erros[7].getErr();
			
				Y.set(i,y);
				YE1upper.setQuick(i,yu);
				YE1down.setQuick(i,yl);
				YE2upper.setQuick(i,yus);
				YE2down.setQuick(i,yls);
				
			}

		}
	
		} // end dimesion 10
		
		return this;

	}

	/**
	 * Return a DataArray container from JPlot
	 * 
	 * @return Container of type DataArray
	 */
	public DataArray getDataArray() {

		DataArray data = new DataArray();
		data.allocate(X.size());
		
		if ( dimen == 2)
		   for (int i = 0; i < X.size(); i++)
               data.addPoint(X.getQuick(i), Y.getQuick(i), 0, 0, 0,0, 0, 0, 0, 0);
		else if ( dimen == 3)
			   for (int i = 0; i < X.size(); i++)
	               data.addPoint(X.getQuick(i), Y.getQuick(i), 0, 0, YE1upper.getQuick(i),YE1upper.getQuick(i), 0, 0, 0, 0);
		else if ( dimen == 4)
			   for (int i = 0; i < X.size(); i++)
	               data.addPoint(X.getQuick(i), Y.getQuick(i), 0, 0, YE1upper.getQuick(i),YE1down.getQuick(i), 0, 0, 0, 0);
		else if ( dimen == 6)
			   for (int i = 0; i < X.size(); i++)
	               data.addPoint(X.getQuick(i), Y.getQuick(i), XE1left.getQuick(i), XE1right.getQuick(i), YE1upper.getQuick(i),YE1down.getQuick(i), 0, 0, 0, 0);
		else if ( dimen == 10)
			   for (int i = 0; i < X.size(); i++)
	               data.addPoint(X.getQuick(i), Y.getQuick(i), XE1left.getQuick(i), XE1right.getQuick(i), YE1upper.getQuick(i),YE1down.getQuick(i),
	            		   XE2left.getQuick(i), XE2right.getQuick(i), YE2upper.getQuick(i),YE2down.getQuick(i));	
		
		return data;

	}

	
	
	
	
	
	
	
	
	/**
	 * Adds values of a plot-point pair (X,Y). All 1st and 2nd level errors on X
	 * and Y are assumed to be 0. The dimension of this container is 2 by default.
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 */
	public void add(double x, double y) {
		
		X.add(x);
		Y.add(y);
		
	}

	
	/**
	 * Adds the values of a plot-point pair (X,Y). It is assumed that Y-values
	 * have 1st level symmetrical errors (i.e. statistical errors). All other
	 * errors are set to 0 This point is added at the end of the array.
	 * <p>
	 * This is a fast implementation. Not check of the dimension is done.
	 * The dimension of this container should be set to 3 before you use this method.
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 * @param err
	 *            an error on Y (assume symmetrical)
	 */
	public void addQuick(double x, double y, double err) {
		X.add(x);
		Y.add(y);
		YE1upper.add(err);
	}

	
	
	/**
	 * Adds the values of a plot-point pair (X,Y). It is assumed that Y-values
	 * have 1st level symmetrical errors (i.e. statistical errors). All other
	 * errors are set to 0 This point is added at the end of the array.
	 * <p>
	 * This is a slower implementation than addQuick(), since if the dimension
	 * is set wrong, it will be adjusted to 3.
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 * @param err
	 *            an error on Y (assume symmetrical)
	 */
	public void add(double x, double y, double err) {

		if (X.size()==0 && YE1upper==null)  {YE1upper=new DoubleArrayList(); dimen=3;}
		X.add(x);
		Y.add(y);
		YE1upper.add(err);
	}

	
	
	
	/**
	 * Add values of a plot-point pair (X,Y). The points include upper and lower
	 * errors on Y. All other errors are set to 0. This point is added at the
	 * end of the array.
	 * 
	 * <p>
	 * The container should be initialized with dimenstion 4 before using this method.
	 * This is a fast implementation without dimension check.
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 * @param upper
	 *            - upper error on Y
	 * @param lower
	 *            - lower error on Y
	 */
	public void addQuick(double x, double y, double upper, double lower) {
		
		
		X.add(x);
		Y.add(y);
		YE1upper.add(upper);
		YE1down.add(lower);
		// dimen = 4;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Add values of a plot-point pair (X,Y). The points include upper and lower
	 * errors on Y. All other errors are set to 0. This point is added at the
	 * end of the array.
	 * 
	 * <p>
	 * This is a slower implementation than addQuick(), since if the dimension
	 * is set wrong, it will be adjusted to 4.
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 * @param upper
	 *            - upper error on Y
	 * @param lower
	 *            - lower error on Y
	 */
	public void add(double x, double y, double upper, double lower) {
		
		if (X.size()==0)  {YE1upper=new DoubleArrayList(); YE1down=new DoubleArrayList(); dimen=4;}
		X.add(x);
		Y.add(y);
		YE1upper.add(upper);
		YE1down.add(lower);
		
	}

	/**
	 * Adds the values of a plot-point pair (X,Y). It includes upper and lower
	 * errors on Y and left and right error on X. 2nd level errors are assumed
	 * to be 0.  This point is added at the end of the array.
	 * 
	 * <p>
	 * This is a slower implementation than addQuick(), since if the dimension
	 * is set wrong, it will be adjusted to 6.
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 * @param left
	 *            - left error on X
	 * @param right
	 *            - right error on X
	 * @param upper
	 *            - upper error on Y
	 * @param lower
	 *            - lower error on Y
	 */
	public void add(double x, double y, double left, double right,
			double upper, double lower) {

		if (X.size()==0)  {YE1upper=new DoubleArrayList(); 
		                   YE1down=new DoubleArrayList(); 
		                   XE1left=new DoubleArrayList();
		                   XE1right=new DoubleArrayList();
		                   dimen=6;}
		X.add(x);
		Y.add(y);
		YE1upper.add(upper);
		YE1down.add(lower);
		XE1left.add(left);
		XE1right.add(right);
		
	}

	
	/**
	 * Adds the values of a plot-point pair (X,Y). It includes upper and lower
	 * errors on Y and left and right error on X. 2nd level errors are assumed
	 * to be 0.  This point is added at the end of the array.
	 * 
	 * <p>
	 * The container should be initialized with dimenstion 6 before using this method.
	 * This is a fast implementation without dimension check.
	 *  
	 * 
	 * @param x
	 *            X-value of the plot-point
	 * @param y
	 *            Y-value of the plot-point
	 * @param left
	 *            - left error on X
	 * @param right
	 *            - right error on X
	 * @param upper
	 *            - upper error on Y
	 * @param lower
	 *            - lower error on Y
	 */
	public void addQuick(double x, double y, double left, double right,
			double upper, double lower) {

		X.add(x);
		Y.add(y);
		YE1upper.add(upper);
		YE1down.add(lower);
		XE1left.add(left);
		XE1right.add(right);
		// dimen = 6;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Adds the values of a plot-point pair (x,y). It includes upper and lower
	 * errors on X and Y, including 1st and 2nd level errors (i.e. statistical
	 * and systematic). This point is added at the end of the array.
	 * 
	 * <p>
	 * 	 
	 * This is a slower implementation than addQuick(), since if the dimension
	 * is set wrong, it will be adjusted to 10.
	 * 
	 * 
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 */
	public void add(double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		
		
	if (X.size()==0)  {
	 YE1upper=new DoubleArrayList(); 
         YE1down=new DoubleArrayList(); 
         XE1left=new DoubleArrayList();
         XE1right=new DoubleArrayList();
         YE2upper=new DoubleArrayList(); 
         YE2down=new DoubleArrayList(); 
         XE2left=new DoubleArrayList();
         XE2right=new DoubleArrayList();
         dimen=10;}
		
		X.add(x);
		Y.add(y);
		YE1upper.add(upper);
		YE1down.add(lower);
		XE1left.add(left);
		XE1right.add(right);
		YE2upper.add(upper_sys);
		YE2down.add(lower_sys);
		XE2left.add(left_sys);
		XE2right.add(right_sys);
		
		
		// dimen = 10;
	}

	
	
	
	/**
	 * Adds the values of a plot-point pair (x,y). It includes upper and lower
	 * errors on X and Y, including 1st and 2nd level errors (i.e. statistical
	 * and systematic). This point is added at the end of the array.
	 * 
	 * <p>
	 * The container should be initialized with dimenstion 6 before using this method.
	 * This is a fast implementation without dimension check.
	 * 
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 */
	public void addQuick(double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		
		
		X.add(x);
		Y.add(y);
		YE1upper.add(upper);
		YE1down.add(lower);
		XE1left.add(left);
		XE1right.add(right);
		YE2upper.add(upper_sys);
		YE2down.add(lower_sys);
		XE2left.add(left_sys);
		XE2right.add(right_sys);
		
		
		// dimen = 10;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Sets the values of a plot-point pair (X,Y). All errors are assumed to be
	 * 0
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 */
	public void set(int i, double x, double y) {
		X.set(i,x);
		Y.set(i,y);
	}

	/**
	 * Sets the values of a plot-point pair (x,y).
	 * The dimension of the container should be 4.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param upper
	 *            upper error on y
	 * @param lower
	 *            lower error on y
	 */
	public void set(int i, double x, double y, double upper, double lower) {
		
		X.set(i,x);
		Y.set(i,y);
		YE1upper.set(i,upper);
		YE1down.set(i,lower);
		// dimen = 4	
	}
	/**
	 * Sets the values of a plot-point pair (x,y).
	 * The dimension of the container should be 3.
	 * 
	 * <p>
	 * If dimension is wrong, extend it to 3.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param err
	 *            error on Y (symmetric)
	
	 */
	public void set(int i, double x, double y, double err) {
		
		
		if (X.size()==0 && dimen==2) {ErrorMessage("The dimension is nor 3!"); return;}
		X.set(i,x);
		Y.set(i,y);
		YE1upper.set(i,err);
		// dimen = 3	
	}
	
	
	
	/**
	 * Sets the values of a plot-point pair (x,y).
	 * The dimension must be 6.
	 * 
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 */
	public void set(int i, double x, double y, double left, double right,
			double upper, double lower) {
		
		X.set(i,x);
		Y.set(i,y);
		YE1upper.set(i,upper);
		YE1down.set(i,lower);
		XE1left.set(i,left);
		XE1right.set(i,right);	
		
	}

	/**
	 * Sets the values of a plot-point pair (x,y).
	 * 
	 * The dimension must be 10.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 * 
	 */
	public void set(int i, double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		
		X.set(i,x);
		Y.set(i,y);
		YE1upper.set(i,upper);
		YE1down.set(i,lower);
		XE1left.set(i,left);
		XE1right.set(i,right);	
		
		YE2upper.set(i,upper_sys);
		YE2down.set(i,lower_sys);
		XE2left.set(i,left_sys);
		XE2right.set(i,right_sys);	
	}

	/**
	 * Update summary of the data. This is necessary after using "setQuick"
	 * method, since a single setQuickment of a data point does not trigger
	 * update of min and max for the data ranges. You do not need to do this if
	 * you use "add" or "set" methods.
	 * 
	 * @deprecated Debricated!
	 */
	public void updateSummary() {

		ErrorMessage("updateSummary is Debricarted ");

	}

	/**
	 * setQuicks the ith value of a plot-point pair (x,y). After all setQuickments
	 * are done, call updateSummary method to get autorange axis values.
	 * The dimension must be 10.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 * 
	 */
	public void setQuick(int i, double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		
		
		X.setQuick(i,x);
		Y.setQuick(i,y);
		YE1upper.setQuick(i,upper);
		YE1down.setQuick(i,lower);
		XE1left.setQuick(i,left);
		XE1right.setQuick(i,right);	
		
		YE2upper.setQuick(i,upper_sys);
		YE2down.setQuick(i,lower_sys);
		XE2left.setQuick(i,left_sys);
		XE2right.setQuick(i,right_sys);	
		
	}

	
	
	
	/**
	 * setQuicks the ith value of a plot-point pair (x,y). After all setQuickments
	 * are done, call updateSummary method to get autorange axis values.
	 * 
	 * <p>
	 * The dimension must be 6.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)

	 * 
	 */
	public void setQuick(int i, double x, double y, double left, double right,
			double upper, double lower) {
		
		
		X.setQuick(i,x);
		Y.setQuick(i,y);
		YE1upper.setQuick(i,upper);
		YE1down.setQuick(i,lower);
		XE1left.setQuick(i,left);
		XE1right.setQuick(i,right);	
		
	}

	
	/**
	 * setQuicks the ith value of a plot-point pair (x,y). After all setQuickments
	 * are done, call updateSummary method to get autorange axis values.
	 * 
	 * <p>
	 * The dimension must be 4.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)

	 * 
	 */
	public void setQuick(int i, double x, double y, 
			double upper, double lower) {
		
		
		X.setQuick(i,x);
		Y.setQuick(i,y);
		YE1upper.setQuick(i,upper);
		YE1down.setQuick(i,lower);
		
	}

	/**
	 * setQuicks the ith value of a plot-point pair (x,y). After all setQuickments
	 * are done, call updateSummary method to get autorange axis values.
	 * 
	 * <p>
	 * The dimension must be 3.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param err
	 *            - error on y (symmetric)
	 * 
	 */
	public void setQuick(int i, double x, double y, 
			double err) {
		
		
		X.setQuick(i,x);
		Y.setQuick(i,y);
		YE1upper.setQuick(i,err);
		
	}

	/**
	 * setQuicks the ith value of a plot-point pair (x,y). After all setQuickments
	 * are done, call updateSummary method to get autorange axis values.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * 
	 */
	public void setQuick(int i, double x, double y) {
		
		
		X.setQuick(i,x);
		Y.setQuick(i,y);
		
	}
	

         /**
         * Compare data with a function. The comparison tests  hypotheses that
         * the data represent identical distribution with a function using Pearson's chi-squared test. 
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


                double sum1=0;
                double nDf=0;
                for (int i = 0; i < size(); i++) {
                        double x = getX(i);
                        double bin1 = getY(i);
                        double e1 = getYupper(i);
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
	 * Compare two data sets in X-Y. Comparison of two data sets test hypotheses that
	 * two data sets  represent identical distributions in 2D. It calculates Chi2
	 * between values in Y taking into account errors on the Y values.
	 * The number chi2/ndf gives the estimate (values close to 1 indicates
	 * similarity between 2 histograms.) Two P1D are identical if chi2=0.
	 * Chi2/ndf can be obtained as output[0]/output[1]. Probability (p-value) is 1. 
	 * <p>
	 * Make sure that both P1D have symmetric errors on Y (first level, i.e.
	 * obtained with the method  getYupper(i) (or set them to small values).
	 * 
	 * @param h2
	 *            second P1D
	 *            
	 * @return the result. It gives Chi2, gives number
	 *         of degrees of freedom (ndf), and probability
	 *         ("quality", or p-value).
	 */
	public Map<String,Double> compareChi2(P1D h2) {

        Map<String,Double> tmp= new  HashMap<String,Double>();

		int bins1x = size();
		int bins2x = h2.size();

		if (bins1x != bins2x) {
			System.out
					.println("Different histograms! Please use histograms with the same bin numbers in X");
			return tmp;
		}

		
		double chi2 = 0;
		int nDf = 0;

		double sum1 = 0;
		double sum2 = 0;
		double sumw1 = 0;
		double sumw2 = 0;

		for (int i = 0; i < bins1x; i++) {
						
			double bin1 = getY(i);
			double bin2 = h2.getY(i);
			double e1 = getYupper(i);
			double e2 = h2.getYupper(i);

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

		for (int i = 0; i < bins1x; i++) {
			double bin1 = getY(i);
			double bin2 = h2.getY(i);
			double e1 = getYupper(i);
			double e2 = h2.getYupper(i);
			
			//System.out.println(Double.toString(bin1)+" - "+Double.toString(bin2));

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
	 * Return the length of the data vector.
	 * 
	 * @return length of the PlotPoint vector
	 */
	public int size() {
		return X.size();
	}

	/**
	 * Get array representing X-values
	 * 
	 * @return array with X values
	 */

	public double[] getArrayX() {
                X.trimToSize(); 
		return X.elements();
		
		
	}

	
	/**
	 * create a copy of this container.
	 * 
	 * @param newtitle
	 *            new title
	 */
	public P1D copy(String newtitle) {
		P1D tmp = new P1D(newtitle);

		LinePars lppp = copyLinePars(lpp);
		tmp.setLinePars(lppp);
		tmp.setDimension(dimen);
		tmp.setXval(X.copy());
		tmp.setYval(Y.copy());
		
		
		
		if ( dimen == 3){
			tmp.setYE1upper(YE1upper.copy() );
		    return tmp;
		}
		
		if ( dimen == 4){
			
			tmp.setYE1upper(YE1upper.copy() );
			tmp.setYE1down(YE1down.copy() );
			
		    return tmp;
		}
		
		if ( dimen == 6){
			
			tmp.setYE1upper(YE1upper.copy() );
			tmp.setYE1down(YE1down.copy() );
			tmp.setXE1left(XE1left.copy() );
			tmp.setXE1right(XE1right.copy() );
			
		    return tmp;
			
		}
		
		if ( dimen == 10){
			
			
			tmp.setYE1upper(YE1upper.copy() );
			tmp.setYE1down(YE1down.copy() );
			tmp.setXE1left(XE1left.copy() );
			tmp.setXE1right(XE1right.copy() );
			
			tmp.setYE2upper(YE2upper.copy() );
			tmp.setYE2down(YE2down.copy() );
			tmp.setXE2left(XE2left.copy() );
			tmp.setXE2right(XE2right.copy() );
			
		    return tmp;
			
		}
		
		
		return tmp;
	}

	
	/**
	 * Set left second-level errors
	 * @param xE2left
	 */
	public void setXE2left(DoubleArrayList xE2left) {
		this.XE2left = xE2left;
	}
	
	/**
	 * Set right second-level errors
	 * @param xE2right
	 */
	
	public void setXE2right(DoubleArrayList xE2right) {
		this.XE2right = xE2right;
	}
	/**
	 * Write a P0D object to a serialized file
	 * 
	 * @param name
	 *            serialized file name for output.
	 * 
	 * @return zero if no errors
	 */
	public int writeSerialized(String name) {

		return jhplot.io.Serialized.write(this, name);

	}

	/**
	 * Read a P1D object from a serialized file
	 * 
	 * @param name
	 *            serialized file name for input. Can be URL if starts from
	 *            http.
	 * 
	 * @return new P1D object
	 */
	public P1D readSerialized(String name) {

		return (P1D) jhplot.io.Serialized.read(name);

	}

	/**
	 * Construct a P1D from a file on the we web. The file should contain 2, or
	 * 4, or 6, or 10 columns: 1) x,y: data without any errors 2) x,y, y(upper),
	 * y(lower) - data with 1st level errors on Y 3) x,y, x(left), x(right),
	 * y(upper), y(lower) - data with 1st level errors on X and Y 4) x,y,
	 * x(left), x(right), y(upper), y(lower), x(leftSys), x(rightSys),
	 * y(upperSys), y(lowerSys) - data with X and Y and 1st and 2nd level
	 * errors. Comments lines starting with "#" and "*" are ignored.
	 * 
	 * @param title
	 *            Title of the container
	 * @param url
	 *            URL location
	 */
	public P1D(String title, URL url) {

		this(title);
		read(url);

	}

	/**
	 * Construct a P1D from a file on the we web. The file should contain 2, or
	 * 4, or 6, or 10 columns: 1) x,y: data without any errors 2) x,y, y(upper),
	 * y(lower) - data with 1st level errors on Y 3) x,y, x(left), x(right),
	 * y(upper), y(lower) - data with 1st level errors on X and Y 4) x,y,
	 * x(left), x(right), y(upper), y(lower), x(leftSys), x(rightSys),
	 * y(upperSys), y(lowerSys) - data with X and Y and 1st and 2nd level
	 * errors. Comments lines starting with "#" and "*" are ignored.
	 * 
	 * @param url
	 *            URL location
	 */
	public P1D(URL url) {
		this("None");
		read(url);

	}

	/**
	 * Read data using 10-column format. Each line corresponds to a new data
	 * point.
	 * 
	 * @param br
	 *            BufferedReader
	 * @return 0 if no errors
	 */
	public int read(BufferedReader br) {

		String line;
		clear();

	
		try {
			while ((line = br.readLine()) != null) {

				line = line.trim();
				if (!line.startsWith("#") && !line.startsWith("*")) {

					StringTokenizer st = new StringTokenizer(line);
					int ncount = st.countTokens(); // number of words
                    clear();
                    setDimension(ncount);
					
					double[] snum = new double[ncount];

					if (ncount != 2 && ncount != 4 && ncount != 6
							&& ncount != 10) {
						ErrorMessage("Error in reading the file:\n"
								+ Integer.toString(ncount)
								+ " entries per line is found!");
					}

					// split this line
					int mm = 0;
					while (st.hasMoreTokens()) { // make sure there is stuff
						// to get
						String tmp = st.nextToken();

						// read double
						double dd = 0;
						try {
							dd = Double.parseDouble(tmp.trim());
						} catch (NumberFormatException e) {
							ErrorMessage("Error in reading the line "
									+ Integer.toString(mm + 1));
							return 3;
						}
						snum[mm] = dd;
						mm++;

					} // end loop over each line

					if (ncount == 2)
						add(snum[0], snum[1]);
					if (ncount == 3)
					     add(snum[0], snum[1], snum[2], snum[2]);
					if (ncount == 4)
						 add(snum[0], snum[1], snum[2], snum[3]);
					if (ncount == 6)
						  add(snum[0], snum[1], snum[2], snum[3],
								snum[4], snum[5]);
					if (ncount == 10)
						   add(snum[0], snum[1], snum[2], snum[3],
								snum[4], snum[5], snum[6], snum[7], snum[8],
								snum[9]);

				} // skip #

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
			// e.printStackTrace();
		}

        
		return 0;

	}

	/**
	 * Read data from URL. Use a space to separate values in columns and new
	 * line to put new data point.
	 * 
	 * @param url
	 *            URL location of input file
	 */

	public int read(URL url) {

		BufferedReader is = PReader.read(url);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * Read P1D from a GZiped file. It can read URL if the string starts from
	 * http or ftp, otherwise a file on the file system is assumed.
	 * <p>
	 * Use a space to separate values in columns and new line to put new row.
	 * 
	 * @param sfile
	 *            File name with input (extension .gz)
	 * @return zero if success
	 */
	public int readGZip(String sfile) {

		BufferedReader is = PReader.readGZip(sfile);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * Read P1D from a file.
	 * <p>
	 * The old content will be lost. The file should contain 2, or 4, or 6, or
	 * 10 columns: 1) x,y: data without any errors 2) x,y, y(upper), y(lower) -
	 * data with 1st level errors on Y 3) x,y, x(left), x(right), y(upper),
	 * y(lower) - data with 1st level errors on X and Y 4) x,y, x(left),
	 * x(right), y(upper), y(lower), x(leftSys), x(rightSys), y(upperSys),
	 * y(lowerSys) - data with X and Y and 1st and 2nd level errors. Comment
	 * lines starting with "#" and "*" are ignored. It can read URL if the
	 * string starts from http or ftp, otherwise a file on the file system is
	 * assumed.
	 * 
	 * @param sfile
	 *            File name with input. If the string starts from http or ftp,
	 *            otherwise a file on the file system is assumed.
	 * @return zero if success
	 */

	public int read(String sfile) {

		BufferedReader is = PReader.read(sfile);
		if (is == null)
			return 1;
		return read(is);

	}

	/**
	 * Read P1D from a Zipped file. The old content will be lost. The file
	 * should contain 2, or 4, or 6, or 10 columns: 1) x,y: data without any
	 * errors 2) x,y, y(upper), y(lower) - data with 1st level errors on Y 3)
	 * x,y, x(left), x(right), y(upper), y(lower) - data with 1st level errors
	 * on X and Y 4) x,y, x(left), x(right), y(upper), y(lower), x(leftSys),
	 * x(rightSys), y(upperSys), y(lowerSys) - data with X and Y and 1st and 2nd
	 * level errors. Comment lines starting with "#" and "*" are ignored.
	 * 
	 * @param sfile
	 *            File name with input (extension zip)
	 * @return zero if success
	 */
	public int readZip(String sfile) {

		// clear all data
		clear();

		try {
			ZipFile zf = new ZipFile(sfile);
			Enumeration entries = zf.entries();
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));

			while (entries.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) entries.nextElement();
				// System.out.println("Read " + ze.getName() + "?");
				String inputLine = input.readLine();
				if (inputLine.equalsIgnoreCase("yes")) {
					long size = ze.getSize();
					if (size > 0) {
						// System.out.println("Length is " + size);

						BufferedReader br = new BufferedReader(
								new InputStreamReader(zf.getInputStream(ze)));

						read(br); // read data
						br.close();
					}
				}
			}

		} catch (FileNotFoundException e) {
			ErrorMessage("File not found:" + sfile);
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}

		return 0;

	}

	/**
	 * Make a new data holder with the same title from the current one.
	 * 
	 * @return new data holder
	 */
	public P1D copy() {

		return copy(this.title);
	}

	/**
	 * Smooth P1D data points in either X or Y.
	 * <p>
	 * It is smoothed by averaging over a moving window of a size specified by
	 * the method parameter: if the value of the parameter is <i>k</i> then the
	 * width of the window is <i>2*k + 1</i>. If the window runs off the end of
	 * the P1D only those values which intersect the histogram are taken into
	 * consideration. The smoothing may optionally be weighted to favor the
	 * central value using a "triangular" weighting. For example, for a value of
	 * <i>k</i> equal to 2 the central bin would have weight 1/3, the adjacent
	 * bins 2/9, and the next adjacent bins 1/9. Errors are kept the same as
	 * before.
	 * 
	 * @param axis
	 *            axis to which smoothing is applied (axis=0 for X, axis=1 for
	 *            Y)
	 * 
	 * @param isWeighted
	 *            Whether values in X or Y will be weighted using a triangular
	 *            weighting scheme favoring bins near the central bin.
	 * @param k
	 *            The smoothing parameter which must be non-negative. If zero,
	 *            the histogram object will be returned with no smoothing
	 *            applied.
	 * @return A smoothed version of P1D.
	 */

	public P1D operSmooth(int axis, boolean isWeighted, int k) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return this;
		}

		
		SHisto sh = new SHisto(size(), getMin(axis), getMax(axis), 1);

		double[] hh = null;
		if (axis == 0)
			hh = getArrayX();
		if (axis == 1)
			hh = getArrayY();

		sh.setBins(hh);
		sh = sh.getSmoothed(isWeighted, k);

		for (int i = 0; i < size(); i++) {

			if (axis == 0)
				setQuick(i, sh.getBinsFirstBand(i), getQuickY(i));
						
			if (axis == 1)
				setQuick(i, getQuickX(i), sh.getBinsFirstBand(i));
	

		}


		
		return this;
	}

	/**
	 * Computes a Gaussian smoothed version of P1D. Smoothing can be done either
	 * to X or Y
	 * 
	 * <p>
	 * Each band of the P1D is smoothed by discrete convolution with a kernel
	 * approximating a Gaussian impulse response with the specified standard
	 * deviation.
	 * 
	 * @param axis
	 *            axis to which smoothing is applied (axis=0 for X, axis=1 for
	 *            Y)
	 * 
	 * @param standardDeviation
	 *            The standard deviation of the Gaussian smoothing kernel which
	 *            must be non-negative or an
	 *            <code>IllegalArgumentException</code> will be thrown. If zero,
	 *            the P1D object will be returned with no smoothing applied.
	 * @return A Gaussian smoothed version of the histogram.
	 * 
	 */
	public P1D operSmoothGauss(int axis, double standardDeviation) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
			return this;
		}

		
		SHisto sh = new SHisto(size(),getMin(axis),getMax(axis), 1);

		double[] hh = null;
		if (axis == 0)
			hh = getArrayX();
		if (axis == 1)
			hh = getArrayY();

		sh.setBins(hh);
		sh = sh.getGaussianSmoothed(standardDeviation);

		for (int i = 0; i < size(); i++) {

			if (axis == 0)
				setQuick(i, sh.getBinsFirstBand(i), getY(i));
		

			if (axis == 1)
				 setQuick(i, getX(i), sh.getBinsFirstBand(i));

		}
		
		
		return this;

	}

	/**
	 * Return P1D array which contains 2nd level errors (or systematic errors)
	 * evaluated from an array of P1D data holders. This means that this method
	 * returns a P1D , but now it has systematic errors evaluated from the input
	 * array. All systematic variations are treated independently and thus are
	 * added in quadrature. Use this method for evaluation of systematic
	 * uncertainties. This function implies only for Y values.
	 * 
	 * @param p1darray
	 *            Input P1D arrays
	 * @return output P1D arrays with the same values and systematic errors, but
	 *         systematic errors are evaluated from the input set of P1D objects
	 */
	public P1D getSys(P1D[] p1darray) {

		
		
		P1D tmp = new P1D("systematics");
		tmp.setDimension(10);
		
		for (int i = 0; i < size(); i++) {
			// estimate systematics
			double d = 0;
			double up = 0;
			double down = 0;

			for (int m = 0; m < p1darray.length; m++) {
				d = p1darray[m].getQuickY(i) - getQuickY(i);
				if (d > 0)
					up = up + d * d;
				if (d < 0)
					down = down + d * d;
			}
			up = Math.sqrt(up);
			down = Math.sqrt(down);
			// System.out.println(up);
	
			if(p1darray[0].dimension()==2) 
			tmp.add(getX(i),getY(i),0,0,0,0,0,0,up,down);
			
			
			
			if(p1darray[0].dimension()==3) 
				tmp.add(getX(i),getY(i),0,0, getYupper(i), 0, 0, 0, up, down);			
			if(p1darray[0].dimension()==4) 
				tmp.add(getX(i),getY(i),0,0, getYupper(i), getYlower(i),
						0, 0, up, down);
				
			if(p1darray[0].dimension()==6) 
				tmp.add(getX(i),getY(i),getXleft(i),
						getXright(i), getYupper(i), getYlower(i),
						0, 0, up, down);
			if(p1darray[0].dimension()==10) 
				tmp.add(getX(i),getY(i),getXleft(i),
						getXright(i), getYupper(i), getYlower(i),
						getXleftSys(i), getXrightSys(i), up, down);
			
			
			
		}
		
		return tmp;
	}

	/**
	 * Add 1st and 2nd level in quadrature and attribute the combined error to
	 * 1st level error. This is useful to simplify presentation of data, but
	 * this certainly not always correct approach. The original object is
	 * modified.
	 * 
	 * <p>
	 * Dimension should be 10.
	 * 
	 * @param axis
	 *            is 0 for X errors, 1 for Y errors
	 */
	public void combineErr(int axis) {

		

		if (dimension() !=10) {
			ErrorMessage("The dimension for this operation must be 10. Exit now.");
			return;
		}
		
		for (int i = 0; i < size(); i++) {
			if (axis == 0) {
				double eLeft = Math.sqrt(getXleft(i) * getXleft(i)
						+ getXleftSys(i) * getXleftSys(i));

				double eRight = Math.sqrt(getXright(i) * getXright(i)
						+ getXrightSys(i) * getXrightSys(i));

				setQuick(i, getX(i), getY(i), eLeft, eRight,
						getYupper(i), getYlower(i), 0, 0,
						getYupperSys(i),getYlowerSys(i));
			}

			if (axis == 1) {

				double eUpper = Math.sqrt(getYupper(i) * getYupper(i)
						+ getYupperSys(i) * getYupperSys(i));

				double eLower = Math.sqrt(getYlower(i) * getYlower(i)
						+ getYlowerSys(i) * getYlowerSys(i));

				setQuick(i, getQuickX(i), getQuickY(i),
						getXleft(i), getXright(i), eUpper, eLower,
						getXleftSys(i),getXrightSys(i), 0, 0);

			}

		}


		
	}

	/**
	 * Return P1D array with weighted average of several measurements. This is a
	 * standard weighted least-squares procedure to combine experimental data
	 * with errors. Measurements represented by input P1Ds are assumed to be
	 * uncorrelated. Errors in X positions are not affected during averaging.
	 * Only 1st and 2nd level errors on Y are used. It is also assumed that
	 * upper and lower 1st-level errors on Y have the same size. In case if the
	 * are not the same, I average them (and print error!)
	 * 
	 * @param p1darray
	 *            Input P1D arrays. They will be added to the original P1D
	 * @return ouput P1D arrays with the same values and systematical errors,
	 *         but systematical errors are evaluated from the input set of P1D
	 *         objects
	 */
	public P1D addAndAverage(P1D[] p1darray) {

		if (dimen==2){
			 return this;
		}
		
		
		P1D tmp = new P1D("Average");
		tmp.setDimension(10);
		
		for (int i = 0; i < size(); i++) {

			double wa1 = 1.0;
			if (dimen==3) {
			if (getYupper(i) != 0)
				wa1 = 1.0 / (getYupper(i) * getYupper(i));
			}
			
			double wa2 = 1.0;
			if (dimen>3) {
			if (getYlower(i) != 0)
				wa2 = 1.0 / (getYlower(i) * getYlower(i));
			}
			
			
			double wsa1 = 1.0;
			double wsa2 = 1.0;
			if (dimen>9) {
	    		if (getYupperSys(i) != 0)
				wsa1 = 1.0 / (getYupperSys(i) * getYupperSys(i));

    			if (getYlowerSys(i) != 0)
				wsa2 = 1.0 / (getYlowerSys(i) * getYlowerSys(i));
			}
			
		
			if (dimen>3) {
			if (getYupper(i) != getYlower(i)) {
				System.out
						.println("Check! Upper and Lower error for Y should be the same!");
				System.out.println("Now I'm averaging them..");
				wa1 = 0.5 * (wa1 + wa2);
			}
			}
			
			double ynew = wa1 * getY(i);
			double wsum = wa1;
			double wsum1 = wsa1;
			double wsum2 = wsa2;

			for (int m = 0; m < p1darray.length; m++) {

				if (size() != p1darray[m].size()) {
					System.out.println("input P1D arrays have diferent size!");
					return this;
				}

				double w1 = 1.0;
                if (dimen>2)
				if (p1darray[m].getYupper(i) != 0)
					w1 = 1.0 / (p1darray[m].getYupper(i) * p1darray[m]
							.getYupper(i));

				double w2 = 1.0;
				if (dimen>3)
				if (p1darray[m].getYlower(i) != 0)
					w2 = 1.0 / (p1darray[m].getYlower(i) * p1darray[m]
							.getYlower(i));

				double ws1 = 1.0;
				if (dimen>9)
				 if (p1darray[m].getYupperSys(i) != 0)
					ws1 = 1.0 / (p1darray[m].getYupperSys(i) * p1darray[m]
							.getYupperSys(i));

				double ws2 = 1.0;
				if (dimen>9)
				if (p1darray[m].getYlowerSys(i) != 0)
					ws2 = 1.0 / (p1darray[m].getYlowerSys(i) * p1darray[m]
							.getYlowerSys(i));

				if (dimen>3)
				if (p1darray[m].getYupper(i) != p1darray[m].getYlower(i)) {
					System.out
							.println("Check! Upper and Lower error for Y should be the same!");
					System.out.println("Now I'm averaging them..");
					w1 = 0.5 * (w1 + w2);
				}

				ynew = ynew + w1 * p1darray[m].getY(i);
				wsum = wsum + w1;
				wsum1 = wsum1 + ws1;
				wsum2 = wsum2 + ws2;
			} // end loop over input P1D

			ynew = ynew / wsum;
			wsum = 1.0 / Math.sqrt(wsum);
			wsum1 = 1.0 / Math.sqrt(wsum1);
			wsum2 = 1.0 / Math.sqrt(wsum2);

			if (dimen>9)
			tmp.add(getX(i), ynew, getXleft(i),
					getXright(i), wsum, wsum, getXleftSys(i),
					getXrightSys(i), wsum1, wsum2);
			
			if (dimen==2 || dimen==3 || dimen==4)
				tmp.add(getX(i), ynew,0,
						0, wsum, wsum, 0,
						0, wsum1, wsum2);
			
			
			
		
			
		}


		
		return tmp;
	}

	/**
	 * Get array representing X-left errors
	 * 
	 * @return array with X left errors
	 */

	public double[] getArrayXleft() {
                XE1left.trimToSize();
		return XE1left.elements();
		
	}

	/**
	 * Get array representing X-right errors
	 * 
	 * @return array with X right errors
	 */

	public double[] getArrayXright() {
                XE1right.trimToSize();
		return XE1right.elements();
		
	}

	/**
	 * Get array representing X-right 2nd level errors
	 * 
	 * @return array with X right 2nd level errors
	 */

	public double[] getArrayXrightSys() {
                XE2right.trimToSize();
		return XE2right.elements();
		
	}

	/**
	 * Get array representing X-left 2nd level errors
	 * 
	 * @return array with X left 2nd level values
	 */

	public double[] getArrayXleftSys() {
                XE2left.trimToSize();
		return XE2left.elements();
		
	}

	/**
	 * Get array representing Y-values
	 * 
	 * @return array with Y values
	 */

	public double[] getArrayY() {
                Y.trimToSize();
		return Y.elements();
		
	}

	/**
	 * Return a specific X-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getX(int i) {
		return X.get(i);
	}

	
	
	
	
	/**
	 * Return a specific left error on X-value. if index i falls beyond the
	 * valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXleft(int i) {
		return XE1left.get(i);
	}

	/**
	 * Return a specific right error on X-value. if index i falls beyond the
	 * valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXright(int i) {
		return XE1right.get(i);
	}

	/**
	 * Return a specific left error on X-value (systematic error). if index i
	 * falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXleftSys(int i) {
		return XE2left.get(i);
	}

	/**
	 * Return a specific right error on X-value (systematic error). if index i
	 * falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXrightSys(int i) {
		return XE2right.get(i);
	}

	/**
	 * Return a specific Y-value. This function returns POSINF (1e300) if index
	 * falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getY(int i) {
		return Y.get(i);
	}

     /**
	 * Return (symmetric) error on Y-value. This is equivalent to getYupper().
	 * Errors for Y can be added as add(X,Y,Error).
	 * @param i
	 *            index of the array
	 * @return error value on y at index i
	 */
	public double getErr(int i) {
		return getYupper(i);
	}


	/**
	 * Return a specific Y-value quickly (no bound check).  
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getQuickY(int i) {
		return Y.getQuick(i);
	}

	/**
	 * Return a specific X-value quickly (no bound check).  
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getQuickX(int i) {
		return X.getQuick(i);
	}
	
	/**
	 * Return a specific upper error on Y-value. This function returns POSINF
	 * (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the upper error on value of y at index i
	 */
	public double getYupper(int i) {
		return YE1upper.get(i);
	}

        /**
         * Return error (uncertainty) on the Y value.
         * This is equivalent to getYupper(). It is assumed that upperl and lower error is the same.  
         * @param i
         *            index of the array
         * @return error on value of Y at index i
         */
        public double getError(int i) {
                return YE1upper.get(i);
        }

	/**
	 * Return a specific lower error on Y-value. This function returns POSINF
	 * (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getYlower(int i) {
		return YE1down.get(i);
	}

	/**
	 * Get array representing Y lower errors
	 * 
	 * @return array with Y lower error
	 */

	public double[] getArrayYlower() {
                YE1down.trimToSize();
		if (YE1down==null) ErrorMessage("This container was not initialized with >6 dimensions!");
		return YE1down.elements();
		
	}

	/**
	 * Get array representing Y lower 2nd level errors
	 * 
	 * @return array with Y lower 2nd level error
	 */

	public double[] getArrayYlowerSys() {
                 YE2down.trimToSize();
		if (YE2down==null) ErrorMessage("This container was not initialized with >6 dimensions!");
		return YE2down.elements();
		
		
	}

	/**
	 * Return a specific systematical upper error on Y-value. This function
	 * returns POSINF (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getYupperSys(int i) {
		return YE2upper.get(i);
	}

	/**
	 * Get array representing Y upper errors
	 * 
	 * @return array with Y upper error
	 */

	public double[] getArrayYupper() {
                YE1upper.trimToSize();
		if (YE1upper==null) ErrorMessage("This container was not initialized with >6 dimensions!");
		return YE1upper.elements();
		
	}

        /**
         * Get array representing (symmetric) error on Y. This is equivalent to getArrayYupper(). 
         * 
         * @return array with errors on the Y values. 
         */

        public double[] getArrayErr() {
                return getArrayYupper();
        }


	/**
	 * Returns the dimension of this P1D holder. The convention is: 2: only x
	 * and y 3: x,y and symmetrical stat error on y 4: only x, y, y(up), y(down)
	 * 6: only x, y, x(up), x(down) y(up), y(down) > the rest
	 * 
	 * @return dimension of the P1D
	 */

	public int dimension() {

		return dimen;
	}

        /**
         * Returns the dimension of this P1D holder. The convention is: 2: only x
         * and y 3: x,y and symmetrical stat error on y 4: only x, y, y(up), y(down)
         * 6: only x, y, x(up), x(down) y(up), y(down) > the rest
         * 
         * @return dimension of the P1D
         */
        public int getDimension() {

                return dimen;
        }

	/**
	 * Return a specific total lower error on Y-value. This function returns
	 * POSINF (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getYlowerSys(int i) {
		return YE2down.get(i);
	}

	/**
	 * Get array representing Y upper 2nd level errors
	 * 
	 * @return array with Y upper 2nd level error
	 */

	public double[] getArrayYupperSys() {
                YE2upper.trimToSize();
		if (YE2upper==null) ErrorMessage("This container was not initialized with 10 dimensions!");
		return YE2upper.elements();
		
	}

        /**
         * Get IDataPointSet. If systematical errors included, they are added in
         * quadrature with statistical errors.
         * 
         * @return IDataPointSet made of P1D
         */
        public IDataPointSet getIDataPointSet() {
            IAnalysisFactory af = IAnalysisFactory.create();
            ITree tree = af.createTreeFactory().create();
            IDataPointSetFactory dpsf = af.createDataPointSetFactory(tree);
            IDataPointSet fDps2D = dpsf.create(getTitle(), getTitle(), 2);
            fillIDataPointSet(fDps2D);
            return fDps2D;
        }


       /**
         * Fill IDataPointSet. If systematical errors included, they are added in
         * quadrature with statistical errors.
         * 
         * @return IDataPointSet made of P1D
         */
        public  void fillIDataPointSet(IDataPointSet fDps2D ){

                // System.out.println("DEBUG=conversion");
		double sup, slow, xleft, xright;
		// Fill the data point set with the generated data values.
                 //System.out.println("Size in fillIDataPointSet=");        	
	         //System.out.println(dimen);
	
		if (dimen==2){
			for (int i = 0; i < size(); i++) {
			fDps2D.addPoint();
			fDps2D.point(i).coordinate(0).setValue(X.getQuick(i));
			fDps2D.point(i).coordinate(1).setValue(Y.getQuick(i));
			}
		}
		
		
		if (dimen==3){
			for (int i = 0; i < size(); i++) {
			fDps2D.addPoint();
			fDps2D.point(i).coordinate(0).setValue(X.getQuick(i));
			fDps2D.point(i).coordinate(1).setValue(Y.getQuick(i));
			fDps2D.point(i).coordinate(1).setErrorPlus(YE1upper.getQuick(i));
			fDps2D.point(i).coordinate(1).setErrorMinus(YE1upper.getQuick(i));
			}
		}
		
		
		if (dimen==4){
			for (int i = 0; i < size(); i++) {
			fDps2D.addPoint();
			fDps2D.point(i).coordinate(0).setValue(X.getQuick(i));
			fDps2D.point(i).coordinate(1).setValue(Y.getQuick(i));
			fDps2D.point(i).coordinate(1).setErrorPlus(YE1upper.getQuick(i));
			fDps2D.point(i).coordinate(1).setErrorMinus(YE1down.getQuick(i));
			}
		}
		
		if (dimen==6){
			for (int i = 0; i < size(); i++) {
			fDps2D.addPoint();
			fDps2D.point(i).coordinate(0).setValue(X.getQuick(i));
			fDps2D.point(i).coordinate(0).setErrorPlus(XE1right.getQuick(i));
			fDps2D.point(i).coordinate(0).setErrorMinus(XE1left.getQuick(i));
			
			fDps2D.point(i).coordinate(1).setValue(Y.getQuick(i));
			fDps2D.point(i).coordinate(1).setErrorPlus(YE1upper.getQuick(i));
			fDps2D.point(i).coordinate(1).setErrorMinus(YE1down.getQuick(i));
			}
		}
		
		if (dimen==10){
	
		for (int i = 0; i < size(); i++) {
			fDps2D.addPoint();
			fDps2D.point(i).coordinate(0).setValue(X.getQuick(i));
			fDps2D.point(i).coordinate(1).setValue(Y.getQuick(i));
			sup = Math.sqrt(getYupperSys(i) * getYupperSys(i)
					+ getYupper(i) * getYupper(i));
			slow = Math.sqrt(getYlowerSys(i) * getYlowerSys(i)
					+ getYlower(i) * getYlower(i));
			fDps2D.point(i).coordinate(1).setErrorPlus(sup);
			fDps2D.point(i).coordinate(1).setErrorMinus(slow);

			// errors on X
			if (getXleft(i) != 0 && getXleftSys(i) != 0) {
				xleft = Math.sqrt(getXleftSys(i) * getXleft(i)
						+ getXright(i) * getXright(i));
				fDps2D.point(i).coordinate(0).setErrorMinus(xleft);
			}

			if (getXright(i) != 0 && getXrightSys(i) != 0) {
				xright = Math.sqrt(getXleftSys(i) * getXleft(i)
						+ getXright(i) * getXright(i));
				fDps2D.point(i).coordinate(0).setErrorPlus(xright);
			}

		}


		}
		
	}

	/**
	 * Returns the maximum value in the range.
	 * 
	 * @param axis
	 *            defines to which axis this function applies (0=X; 1=Y);
	 * @return the maximum value.
	 */
	public double getMax(int axis) {
		
		if (axis==0) return Descriptive.max(X);
		if (axis==1) return Descriptive.max(Y);
		
		return 0;
	}

	/**
	 * Returns the index of maximum value in the range.
	 * 
	 * @param axis
	 *            defines to which axis this function applies (0=X; 1=Y);
	 * @return index of maximum value.
	 */
	public int getMaxIndex(int axis) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
		}

		double[] values = null;
		if (axis == 0)
			values = getArrayX();
		if (axis == 1)
			values = getArrayY();
		int index = DoubleArray.maxIndex(values);
		return index;
	}

	/**
	 * Returns the minimum value in the range. Careful, no error checking on the
	 * value of axis, which should be less than N_AXES, defined in
	 * GraphSettings.
	 * 
	 * @param axis
	 *            defines to which axis this function applies (0=X; 1=Y);
	 * @return the minimum value.
	 */
	public double getMin(int axis) {
		
		if (axis==0 && X.size()>0) return Descriptive.min(X);
		if (axis==1 && Y.size()>0) return Descriptive.min(Y);
		
		return 0;
	}

	/**
	 * Returns the index of minimum value in the range.
	 * 
	 * @param axis
	 *            defines to which axis this function applies (0=X; 1=Y);
	 * @return index of maximum value.
	 */
	public int getMinIndex(int axis) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
		}

		double[] values = null;
		if (axis == 0)
			values = getArrayX();
		if (axis == 1)
			values = getArrayY();
		int index = DoubleArray.minIndex(values);
		return index;
	}

	
	/** 
	 * Remove a (X,Y) value at a given index 
	 * @param index index
	 */
	public void removeAt(int index) {
		
		X.remove(index);
		Y.remove(index);
		if (dimen==3) {YE1upper.remove(index); return;}
		if (dimen==4) {YE1upper.remove(index); YE1down.remove(index); return;};
		if (dimen==6) {YE1upper.remove(index); YE1down.remove(index); 
		               XE1left.remove(index); XE1right.remove(index);return;};
		if (dimen==10) {YE1upper.remove(index); YE1down.remove(index); 
		                XE1left.remove(index); XE1right.remove(index);
		                YE2upper.remove(index); YE2down.remove(index); 
		                YE2upper.remove(index); YE2down.remove(index); 
		                return;};
		
		               
		return;
		
	}
	
	/**
	 * Obtain a new PD1 in the range between Min and Max indexes. Min and Max are
	 * included. All errors will be copied to a new P1D. For example,
	 * getRange(2,2) will return a P1D at index=2 (only one point with errors).
	 * 
	 * @param IndexMin
	 *            Min index
	 * @param IndexMax
	 *            Max index
	 * @return a new P1D with the value in the range.
	 *    
	 */
	public P1D  range(int IndexMin, int IndexMax) {


		P1D tmp = new P1D(getTitle());
		LinePars lppp = copyLinePars(lpp);
		tmp.setLinePars(lppp);
		tmp.setDimension(dimen);
		tmp.setXval( (	DoubleArrayList)X.partFromTo(IndexMin,IndexMax));
		tmp.setYval( (	DoubleArrayList)Y.partFromTo(IndexMin,IndexMax));
		if (dimen==2) return tmp;
		
		if (dimen==3) {    tmp.setYE1upper( (	DoubleArrayList)YE1upper.partFromTo(IndexMin,IndexMax)); 
		                   return tmp;}
		
		if (dimen==4) {    tmp.setYE1upper( (	DoubleArrayList)YE1upper.partFromTo(IndexMin,IndexMax)); 
		                   tmp.setYE1down( (	DoubleArrayList)YE1down.partFromTo(IndexMin,IndexMax));  
        return tmp;}
		
		if (dimen==6) {    
			               tmp.setYE1upper( (	DoubleArrayList)YE1upper.partFromTo(IndexMin,IndexMax)); 
                           tmp.setYE1down( (	DoubleArrayList)YE1down.partFromTo(IndexMin,IndexMax));  
			               tmp.setXE1left( (	DoubleArrayList)XE1left.partFromTo(IndexMin,IndexMax)); 
                           tmp.setXE1right( (	DoubleArrayList)XE1right.partFromTo(IndexMin,IndexMax));  
                           
                            return tmp;}
		if (dimen==10) {    
            tmp.setYE1upper( (	DoubleArrayList)YE1upper.partFromTo(IndexMin,IndexMax)); 
            tmp.setYE1down( (	DoubleArrayList)YE1down.partFromTo(IndexMin,IndexMax));  
            tmp.setXE1left( (	DoubleArrayList)XE1left.partFromTo(IndexMin,IndexMax)); 
            tmp.setXE1right( (	DoubleArrayList)XE1right.partFromTo(IndexMin,IndexMax));  
            tmp.setYE2upper( (	DoubleArrayList)YE2upper.partFromTo(IndexMin,IndexMax)); 
            tmp.setYE2down( (	DoubleArrayList)YE2down.partFromTo(IndexMin,IndexMax));  
            tmp.setXE2left( (	DoubleArrayList)XE2left.partFromTo(IndexMin,IndexMax)); 
            tmp.setXE2right( (	DoubleArrayList)XE2right.partFromTo(IndexMin,IndexMax));  
             return tmp;}
		

		return tmp;

	}

	
	/**
	 * Calculate derivative for X-Y data points. It is used to express how fast
	 * a Y-values are changing, and are therefore related, mathematically, to
	 * the slope of a line. It is calculate as: getY(i+1)-getY(i) /
	 * getX(i+1)-getX(i), assuming that all points are ordered in X. For a
	 * series of data X-Y points, you can join each pair of adjacent points with
	 * a straight line and a slope can be associated with each such line
	 * segment. 
         * <p>
         * Data points can have errors (statistical), with upper and lower error on Y (dimen=4).
         *Statistical uncertainties on Y are propagated and included into
	 * the final P1D with derivatives. Second-level errors are ignored.
	 * 
	 * @return a new P1D with derivative in each data point.
	 */
	public P1D derivative() {

		P1D tmp = new P1D("Derivative of " + title);
                 if (dimen ==4) {

		for (int i = 0; i < size() - 1; i++) {

			double dx = getQuickX(i + 1) - getQuickX(i);
			double dy = getQuickY(i + 1) - getQuickY(i);

			double dyUP = (getQuickY(i + 1) + getYupper(i + 1)) - getQuickY(i);
			double dyDW = (getQuickY(i + 1) - getYlower(i + 1)) - getQuickY(i);

			double slope = 0;
			double slopeUP = 0;
			double slopeDW = 0;
			if (dx != 0) {
				slope = dy / dx;
				slopeUP = dyUP / dx;
				slopeDW = dyDW / dx;
			}

			tmp.add(getX(i), slope, 0, 0, slopeUP, slopeDW, 0, 0, 0, 0);
		}
                };


                if (dimen !=4 ) {

                for (int i = 0; i < size() - 1; i++) {
                        double dx = getQuickX(i + 1) - getQuickX(i);
                        double dy = getQuickY(i + 1) - getQuickY(i);
                        double slope = 0;
                        if (dx != 0) {
                                slope = dy / dx;
                        }
                        tmp.add(getX(i), slope);
                }
                };
 





		return tmp;

	}

	/**
	 * Construct P1D removing a range of values defined by Min and Max. All
	 * errors will be copied to a new P1D. TMin and max are included in the
	 * final output.
	 * 
	 * @param axis
	 *            if axis=0, applied for X, if axis=1, applied for Y.
	 * @param Min
	 *            Min value
	 * @param Max
	 *            Max value
	 * @return a new P1D
	 */
	public P1D rangeCut(int axis, double Min, double Max) {

		if (axis != 0 && axis != 1) {
			ErrorMessage("Axis variable should be 0 or 1!");
		}

		P1D tmp = new P1D(title);
		tmp.setLinePars(lpp);

		for (int i = 0; i < size(); i++) {

			if (axis == 0) {
				if (getX(i) < Min || getX(i) > Max)

                                if (dimen==2)    tmp.add(getX(i), getY(i));
                                else if (dimen==3)    tmp.add(getX(i), getY(i), getYupper(i));
                                else if (dimen==4)    tmp.add(getX(i), getY(i), getYupper(i),getYlower(i));
				else if (dimen>4)     tmp.add(getX(i), getY(i), getXleft(i), getXright(i),
						      getYupper(i), getYlower(i), getXleftSys(i),
						      getXrightSys(i), getYupperSys(i), getYlowerSys(i));
			}

			if (axis == 1) {
				if (getY(i) < Min || getY(i) > Max)

                                         if (dimen==2)    tmp.add(getX(i), getY(i));
                                         else if (dimen==3)    tmp.add(getX(i), getY(i), getYupper(i));
                                         else if (dimen==4)    tmp.add(getX(i), getY(i), getYupper(i),getYlower(i));
					 else tmp.add(getX(i), getY(i), getXleft(i), getXright(i),
							getYupper(i), getYlower(i), getXleftSys(i),
							getXrightSys(i), getYupperSys(i), getYlowerSys(i));
			}

		}
		return tmp;

	}


        /**
         * Sorting the original array using either X or Y values. The sorting done in increasing order.
         * We use exchange sort method. Currently supports dimensions 2 (X-Y), 3 (X-Y,errror on Y), 4 (X-Y, errors upper and lower on Y).
         * @param axis
         *            if axis=0, sorting applied for X, if axis=1, sorting is applied for Y.
         */
        public  void sort(int axis) {

                if (axis != 0 && axis != 1) {
                        ErrorMessage("Axis variable should be 0 or 1!");
                }

                // use exchange sort
                if (axis==0) {
                
                
                if (dimen==2) {  
                double tempX;
                double tempY;
                for (int i=0; i < size()-1; i++) {
                  for (int j=i+1; j<size(); j++) {
                              if (X.getQuick(i)>X.getQuick(j)) {
                                    tempX= X.getQuick(i);         
                                    tempY= Y.getQuick(i);         
                                    X.setQuick(i,getQuickX(j));
                                    Y.setQuick(i,getQuickY(j));
                                    X.setQuick(j,tempX);
                                    Y.setQuick(j,tempY);
                                   }; 
                        }
                }
                }
                
                
                if (dimen==3) {  
                double tempX;
                double tempY;
                double tempEY;
                for (int i=0; i < size()-1; i++) {
                  for (int j=i+1; j<size(); j++) {
                              if (X.getQuick(i)>X.getQuick(j)) {
                                    tempX= X.getQuick(i);         // swap
                                    tempY= Y.getQuick(i);         // swap
                                    tempEY= YE1upper.getQuick(i);
                                    X.setQuick(i,getQuickX(j));
                                    Y.setQuick(i,getQuickY(j));
                                    YE1upper.setQuick(i,YE1upper.getQuick(j));
                                    X.setQuick(j,tempX);
                                    Y.setQuick(j,tempY);
                                    YE1upper.setQuick(j,tempEY);
                                   }; 
                        }
                }
                }
                
                
                if (dimen==4) {  
                double tempX;
                double tempY;
                double tempEYu;
                double tempEYd;
                for (int i=0; i < size()-1; i++) {
                  for (int j=i+1; j<size(); j++) {
                              if (X.getQuick(i)>X.getQuick(j)) {
                                    tempX= X.getQuick(i);         // swap
                                    tempY= Y.getQuick(i);         // swap
                                    tempEYu= YE1upper.getQuick(i);
                                    tempEYd= YE1down.getQuick(i);
                                    X.setQuick(i,getQuickX(j));
                                    Y.setQuick(i,getQuickY(j));
                                    YE1upper.setQuick(i, YE1upper.getQuick(j));
                                    YE1down.setQuick(i,  YE1down.getQuick(j) );
                                    X.setQuick(j,tempX);
                                    Y.setQuick(j,tempY);
                                    YE1upper.setQuick(j,tempEYu);
                                    YE1down.setQuick(j,tempEYd);
                                   }; 
                        }
                }
                }
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                }


               if (axis==1) {
                if (dimen==2) {
                double tempX;
                double tempY;
                for (int i=0; i < size()-1; i++) {
                  for (int j=i+1; j<size(); j++) {
                              if (Y.getQuick(i)>Y.getQuick(j)) {
                                    tempX= X.getQuick(i);         // swap
                                    tempY= Y.getQuick(i);         // swap
                                    X.setQuick(i,getQuickX(j));
                                    Y.setQuick(i,getQuickY(j));
                                    X.setQuick(j,tempX);
                                    Y.setQuick(j,tempY);
                                   };
                        }
                }
                }
                
                
                
                
                if (dimen==3) {  
                double tempX;
                double tempY;
                double tempEY;
                for (int i=0; i < size()-1; i++) {
                  for (int j=i+1; j<size(); j++) {
                              if (Y.getQuick(i)>Y.getQuick(j)) {
                                    tempX= X.getQuick(i);         // swap
                                    tempY= Y.getQuick(i);         // swap
                                    tempEY= YE1upper.getQuick(i);
                                    X.setQuick(i,getQuickX(j));
                                    Y.setQuick(i,getQuickY(j));
                                    YE1upper.setQuick(i,YE1upper.getQuick(j));
                                    X.setQuick(j,tempX);
                                    Y.setQuick(j,tempY);
                                    YE1upper.setQuick(j,tempEY);
                                   }; 
                        }
                }
                }
                
                
                if (dimen==4) {  
                double tempX;
                double tempY;
                double tempEYu;
                double tempEYd;
                for (int i=0; i < size()-1; i++) {
                  for (int j=i+1; j<size(); j++) {
                              if (Y.getQuick(i)>Y.getQuick(j)) {
                                    tempX= X.getQuick(i);         // swap
                                    tempY= Y.getQuick(i);         // swap
                                    tempEYu= YE1upper.getQuick(i);
                                    tempEYd= YE1down.getQuick(i);
                                    X.setQuick(i,getQuickX(j));
                                    Y.setQuick(i,getQuickY(j));
                                    YE1upper.setQuick(i, YE1upper.getQuick(j));
                                    YE1down.setQuick(i,  YE1down.getQuick(j) );
                                    X.setQuick(j,tempX);
                                    Y.setQuick(j,tempY);
                                    YE1upper.setQuick(j,tempEYu);
                                    YE1down.setQuick(j,tempEYd);
                                   }; 
                        }
                }
                }
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                }



                return;

        }







	

	/**
	 * Integrate P1D between two indices (between 1 and max index)
	 * 
	 * @param IndexMin
	 *            Min minimal index for integration starting from 1 (included to
	 *            integration)
	 * @param IndexMax
	 *            Max maximal index for integration (included to integration)
	 * @return integral (sum of all Y-values)
	 */

	public double integral(int IndexMin, int IndexMax) {

		int Nbins = size();
		if (IndexMin > IndexMax) {
			ErrorMessage("Wrong index!");
			return -1;
		}
		if (IndexMin < 1 || IndexMax > Nbins) {
			ErrorMessage("Wrong index!");
			return -1;
		}

		double sum = 0.0;
		for (int i = IndexMin - 1; i < IndexMax; i++) {
			sum += getQuickY(i);

		}

		return sum;
	}

	/**
	 * Integrate P1D (sum up all Y values)
	 * 
	 * @return integral (sum of all Y-values)
	 */

	public double integral() {

		double sum = 0.0;
		for (int i = 0; i < size(); i++) {
			sum += getQuickY(i);

		}

		return sum;
	}

	/**
	 * Returns variance for X-values. This is a measure of how far a set of
	 * numbers are spread out from each other.
	 * 
	 * @return variance for X values
	 **/

	public double varianceX() {

		double var;
		int degrees = (size() - 1);
		int m = size();
		double c;
		double s;
		c = 0;
		s = 0;
		for (int k = 0; k < m; k++)
			s += getQuickX(k);
		s = s / m;
		for (int k = 0; k < m; k++)
			c += (getQuickX(k) - s) * (getQuickX(k) - s);
		var = c / degrees;
		return var;

	}

	/**
	 * Standard deviation of values X.
	 * 
	 * @return standard deviation of values X
	 */
	public double stddeviationX() {
		return Math.sqrt(varianceX());
	}

	/**
	 * Standard deviation of values Y.
	 * 
	 * @return standard deviation of values Y
	 */
	public double stddeviationY() {
		return Math.sqrt(varianceY());
	}




	/**
	 * Returns variance for Y-values. This is a measure of how far a set of
	 * numbers are spread out from each other.
	 * 
	 * @return variance for Y values
	 **/

	public double varianceY() {

		double var;
		int degrees = (size() - 1);
		int m = size();
		double c;
		double s;
		c = 0;
		s = 0;
		for (int k = 0; k < m; k++)
			s += getQuickY(k);
		s = s / m;
		for (int k = 0; k < m; k++)
			c += (getQuickY(k) - s) * (getQuickY(k) - s);
		var = c / degrees;
		return var;

	}

	/**
	 * Returns RMS for X-values. It represents a root-mean-square (sometimes
	 * called the quadratic mean), is the square root of mean of the values
	 * x_i^2,
	 * 
	 * @return RMS for X
	 **/

	public double rmsX() {

		double mx = 0.;

		for (int i = 0; i < X.size(); i++) {
			mx += X.getQuick(i) *X.getQuick(i) ;
		}

		mx = mx / X.size();
		mx = Math.sqrt(mx);

		return mx;
	}

	/**
	 * Returns RMS for Y-values. It represents a root-mean-square (sometimes
	 * called the quadratic mean), is the square root of mean of the values
	 * x_i^2,
	 * 
	 * @return RMS for Y
	 **/

	public double rmsY() {

		double mx = 0.;

		for (int i = 0; i < Y.size(); i++) {
			mx += Y.getQuick(i) *Y.getQuick(i) ;
		}

		mx = mx / Y.size();
		mx = Math.sqrt(mx);

		return mx;
	}

	/**
	 * Returns the mean value in X.
	 * 
	 * @return Mean value in X
	 **/

	public double meanX() {

		double mx = 0.;

		for (int i = 0; i < X.size(); i++) {
			mx += X.getQuick(i);
		}

		mx /=  X.size();
		return mx;
	}

	/**
	 * Returns the standard error of the mean values for X.
	 * This is  (standard deviation)/sqrt(size).
	 * 
	 * @return standard error of the mean values for X
	 **/

	public double meanXerror() {
		return stddeviationX()/Math.sqrt(size());
	}
	
	
	
	/**
	 * Returns the standard error of the mean values for Y.
	 * This is  (standard deviation)/sqrt(size).
	 * 
	 * @return standard error of the mean values for X
	 **/

	public double meanYerror() {
		return stddeviationY()/Math.sqrt(size());
	}
	
	
	/**
	 * Get complete statistics for this container for a given axis.
	 * It return mean, error on the mean, RMS, variance, standard deviation. <p>
	 * The key for the output <b>map are: mean, error, rms, variance, stddev </b>.
	 * 
	 * @param axis axis=0 for X and axis=1 for Y
	 * @return map representing statistics
	 */
	
	public Map<String,Double>  getStat(int axis) {
		
		Map<String,Double> tmp= new  HashMap<String,Double>();
		
		if (axis==0){
		tmp.put("mean", meanX());
		tmp.put("mean_error", meanXerror());
		tmp.put("rms", rmsX());
		tmp.put("variance", varianceX());
		tmp.put("standardDeviation", stddeviationX());
		}
		if (axis==1){
			tmp.put("mean", meanY());
			tmp.put("mean_error", meanYerror());
			tmp.put("rms", rmsY());
			tmp.put("variance", varianceY());
			tmp.put("standardDeviation", stddeviationY());
			}
		
		
		
		
		return tmp;
	}
	
	
	
	/**
	 * Returns the mean value in Y.
	 * 
	 * @return Mean value in Y
	 **/

	public double meanY() {

		double mx = 0.;

		for (int i = 0; i <Y.size(); i++) {
			mx += Y.getQuick(i);
		}

		mx /= Y.size();
		return mx;

	}

	/**
	 * Clear the container.
	 */
	public void clear() {

		if (dimen==2){
		X.clear();
		Y.clear();
		} else if (dimen ==3){
			X.clear();
			Y.clear();
			YE1upper.clear();
		} else if (dimen ==4){
			X.clear();
			Y.clear();
			YE1upper.clear();
			YE1down.clear();
		} else if (dimen ==6){
			X.clear();
			Y.clear();
			XE1left.clear();
			XE1right.clear();
			YE1upper.clear();
			YE1down.clear();
		} else if (dimen ==10){
			X.clear();
			Y.clear();
			XE1left.clear();
			XE1right.clear();
			YE1upper.clear();
			YE1down.clear();
			XE2left.clear();
			XE2right.clear();
			YE2upper.clear();
			YE2down.clear();
			
		}
		
	
	}

	/**
	 * Show in  a Table in a separate Frame. The numbers are
	 * formatted to scientific format. One can sort and search the data in this
	 * table (but not modify)
	 */

	public void toTable() {

		new HTable(this);

	}

	

	/**
	 * Fill a P1D container from 2 arrays. If it is not empty, add values will
	 * be appended. It is assumed that all errors are zero.
	 * 
	 * @param xa
	 *            array with X values
	 * @param ya
	 *            array with Y values
	 * 
	 */
	public void fill(double[] xa, double[] ya) {

		if (xa.length != ya.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		setDimension(2);
		for (int i = 0; i < xa.length; i++){
			X.add(xa[i]);
			Y.add(ya[i]);
		}
		
	}


        /**
         * Fill a P1D container from 2 integer arrays. If it is not empty, add values will
         * be appended. It is assumed that all errors are zero.
         * 
         * @param xa
         *            integer array with X values
         * @param ya
         *            integer array with Y values
         * 
         */
        public void fill(int[] xa, int[] ya) {

                if (xa.length != ya.length) {
                        System.out.println("Different dimensions of arrays!");
                        return;
                }

                setDimension(2);
                for (int i = 0; i < xa.length; i++){
                        X.add(xa[i]);
                        Y.add(ya[i]);
                }

        }

	/**
	 * Fill a P1D container from 2 P0D arrays. If it is not empty, add values
	 * will be appended. It is assumed that all errors are zero.
	 * 
	 * @param xa
	 *            P0D with X values
	 * @param ya
	 *            P0D with Y values
	 * 
	 */
	public void fill(P0D xa, P0D ya) {

		if (xa.size() != ya.size()) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		setDimension(2);
		for (int i = 0; i < xa.size(); i++){
			X.add(xa.getQuick(i));
			Y.add(ya.getQuick(i));
		}
		

	}

	/**
	 * Fill a P1D container from a Cloud2D.
	 * 
	 * @param c2D
	 *            input Cloud
	 * 
	 */
	public void fill(Cloud2D c2d) {
		setDimension(2);
		for (int i = 0; i < c2d.entries(); i++){
			X.add(c2d.valueX(i));
			Y.add(c2d.valueY(i));
		}
			

	}

	
	/**
	 * Get array of X values;
	 * @return
	 */
	public DoubleArrayList getXval() {
		return X;
	}
	
	/**
	 * Get array of Y values;
	 * @return
	 */
	public DoubleArrayList getYval() {
		return Y;
	}
	
	
	/**
	 * Left error on X (1st level)
	 * @return
	 */
	public DoubleArrayList getXE1left() {
		return XE1left;
	}
	
	/**
	 * Left error on X (2nd level)
	 * @return
	 */
	public DoubleArrayList getXE2left() {
		return XE2left;
	}
	
	
	/**
	 * Right eroor on X
	 * @return
	 */
	public DoubleArrayList getXE1right() {
		return XE1right;
	}
	
	/**
	 * Right error (2nd level) on X
	 * @return
	 */
	public DoubleArrayList getXE2right() {
		return XE2right;
	}
	
	
	/**
	 * Down error on Y (1st level)
	 * @return
	 */
	public DoubleArrayList getYE1down() {
		return YE1down;
	}
	
	
	/**
	 * Upper error on Y (1st level)
	 * @return
	 */
	public DoubleArrayList getYE1upper() {
		return YE1upper;
	}
	
	/**
	 * Down error on Y (2nd level)
	 * @return
	 */
	public DoubleArrayList getYE2down() {
		return YE2down;
	}
	
	/**
	 * Upper error on Y (2nd level)
	 * @return
	 */
	public DoubleArrayList getYE2upper() {
		return YE2upper;
	}
	
	
	
	
	
	/**
	 * Error on X left.Statistical error.
	 * @param xE1left left statistical error.
	 */
	
	public void setXE1left(DoubleArrayList xE1left) {
		XE1left = xE1left;
	}
	
	/**
	 * Set X values as array
	 * @param array with X values
	 */
	
	public void setXval(DoubleArrayList X) {
		this.X = X;
	}
	
	
	
	/**
	 * Error on X. right  error (1st level).
	 * @param xE1right 
	 */
	public void setXE1right(DoubleArrayList xE1right) {
		XE1right = xE1right;
	}
	
	
	/**
	 * Set statististical down error
	 * @param yE1down down error
	 */
	public void setYE1down(DoubleArrayList yE1down) {
		YE1down = yE1down;
	}
	
	/**
	 * Set statistical error on Y 91st level)
	 * @param yE1upper upper error on Y
	 */
	public void setYE1upper(DoubleArrayList yE1upper) {
		YE1upper = yE1upper;
	}
	
	
	/**
	 * Set second level down error on Y
	 * @param yE2down error on Y down (2nd level)
	 */
	public void setYE2down(DoubleArrayList yE2down) {
		YE2down = yE2down;
	}
	
	
	
	/**
	 * Set second level upper error on Y
	 * @param yE2upper upper error on Y 
	 */
	
	public void setYE2upper(DoubleArrayList yE2upper) {
		YE2upper = yE2upper;
	}
	
	
	/**
	 * Set Y values
	 * @param yval Y value
	 */
	public void setYval(DoubleArrayList yval) {
		Y = yval;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Fill a P1D container from 3 arrays (one represents symmetrical errors on
	 * Y). If it is not empty, add values will be appended. It is assumed that
	 * all other errors are zero. Arrays must be of the same size.
	 * 
	 * @param xa
	 *            array with X values
	 * @param ya
	 *            array with Y values
	 * @param yerror
	 *            array with errors on Y values
	 * 
	 */
	public void fill(double[] xa, double[] ya, double[] yerror) {

		if (xa.length != ya.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}
		if (xa.length != yerror.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		    setDimension(3);
			X.elements (xa);
			Y.elements(ya);
			YE1upper.elements(yerror);
		
	}

	/**
	 * Fill a P1D container from P0D arrays (one represents symmetrical errors
	 * on Y). If it is not empty, add values will be appended. It is assumed
	 * that all other errors are zero. Arrays must be of the same size.
	 * 
	 * @param xa
	 *            P0D array with X values
	 * @param ya
	 *            P0D array with Y values
	 * @param yerror
	 *            P0D array with errors on Y values
	 * 
	 */
	public void fill(P0D xa, P0D ya, P0D yerror) {

		if (xa.size() != ya.size()) {
			System.out.println("Different dimensions of arrays!");
			return;
		}
		if (xa.size() != yerror.size()) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		   setDimension(3);	
			for (int i = 0; i < xa.size(); i++) {
			  	X.add( xa.getQuick(i) );
			    Y.add( ya.getQuick(i) );			    
			    YE1upper.add(  yerror.getQuick(i) );
		 }
		
		

	}

	/**
	 * Fill a P1D container from 4 arrays (last represents asymmetrical upper
	 * and lower errors on Y). If it is not empty, add values will be appended.
	 * It is assumed that all other errors are zero. All arrays must be of the
	 * same size.
	 * 
	 * @param xa
	 *            array with X values
	 * @param ya
	 *            array with Y values
	 * @param yupper
	 *            array with upper errors on Y values
	 * @param ylower
	 *            array with upper errors on Y values
	 * 
	 */
	public void fill(double[] xa, double[] ya, double[] yupper, double[] ylower) {

		if (xa.length != ya.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}
		if (xa.length != yupper.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		if (xa.length != ylower.length) {
			System.out.println("Different dimensions of arrays!");
			return;
		}

		       setDimension(4);	
			
			  	X.elements(xa);
			    Y.elements(ya);			    
			    YE1upper.elements(yupper);
			    YE1down.elements(ylower);
			    
		 
			   
		
	

	}

	/**
	 * Fill the values of a X-Y plot-points with full errors. It includes upper
	 * and lower errors on X and Y, including 1st and 2nd level errors (i.e.
	 * statistical and systematic). All arrays must be of the same size.
	 * 
	 * @param x
	 *            array of x-value of the plot-point
	 * @param y
	 *            array of y-value of the plot-point
	 * @param left
	 *            - array of error on x (left)
	 * @param right
	 *            - array of error on x (right)
	 * @param upper
	 *            - array of error on y (upper)
	 * @param lower
	 *            - array of error on y (lower)
	 * @param left_sys
	 *            - array of error on x (left) - second level, used for
	 *            systematics
	 * @param right_sys
	 *            - array of error on x (right)
	 * @param upper_sys
	 *            - array of error on y (upper)
	 * @param lower_sys
	 *            - array of error on y (lower)
	 */
	public void fill(double[] x, double[] y, double[] left, double[] right,
			double[] upper, double[] lower, double[] left_sys,
			double[] right_sys, double[] upper_sys, double[] lower_sys) {

		
		
	    setDimension(10);
	    X.elements(x);
	    Y.elements(y);
	    XE1left.elements(left);
	    XE1right.elements(right);
	    XE2left.elements(left_sys);
	    XE2right.elements(right_sys);
	    YE1upper.elements(upper);
	    YE1down.elements(lower);
	    YE2upper.elements(upper_sys);
	    YE2down.elements(lower_sys);
	    
	 
		   

		
	}

	
	
	/**
	 * Set array at once
	 * @param x X-values
	 */
	public void setArrayX(double[] x){
		
		X.clear();
		X.elements(x);
		
	}
	
	
	/**
	 * Set array at once
	 * @param x Y-values
	 */
	public void setArrayY(double[] y){
		
		Y.clear();
		Y.elements(y);
		
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
