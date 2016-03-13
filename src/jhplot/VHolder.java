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

import hep.aida.IAxis;
import hep.aida.ref.histogram.Histogram1D;
import jhplot.gui.HelpBrowser;
import jminhep.cluster.DataHolder;
import jminhep.cluster.DataPoint;
import java.util.Arrays;

/**
 * A class to transform any HPlot container (H1D,H2D,P1D..) to a vector for
 * further manipulations
 * 
 * @author S.Chekanov
 * 
 */
public class VHolder {

	private String title;

	private String[] names;

	private Double[][] data;
	
	private int nrows=0;
	
	private int ncols=0;

	
	
	/**
	 * Dummy initialisator
	 */
	public VHolder() {}
	
	
	
	
	
	/**
	 * Create a VHolder object with H1D histogram
	 * 
	 * @param hh
	 *            Input histogram
	 */

	public VHolder(H1D hh) {

	
		
		Histogram1D h1 = hh.get();
		IAxis axis = h1.axis();
		
		data = new Double[axis.bins()][6];
		names = new String[] { "Bin", "Bin min", "Bin max", "Mean", "Height", "Error" };
		nrows=axis.bins();
		ncols=names.length;

		for (int i = 0; i < nrows; i++) {
			double x2 = h1.binMean(i);
			double x3 = axis.binLowerEdge(i);
			double x4 = axis.binUpperEdge(i);
			double x5 = h1.binHeight(i);
			double x6 = h1.binError(i);
			data[i][0] = (double) x2;
			data[i][1] = x3;
			data[i][2] = x4;
			data[i][3] = x2;
			data[i][4] = x5;
			data[i][5] = x6;

		}

	}

	/**
	 * Create a VHolder object with F1D function
	 * 
	 * @param ff
	 *            Input function
	 */

	public VHolder(F1D ff) {

		ff.eval();
		
		title = "F1D: " + ff.getTitle();
		data = new Double[ff.getPoints()][3];
		names = new String[]{ "Bin", "X", "Y" };	
		nrows=ff.getPoints();
		ncols=names.length;
		
		for (int i = 0; i <nrows; i++) {
			double x2 = ff.getX(i);
			double x3 = ff.getY(i);
			data[i][0] = (double)(i+1);
			data[i][1] = x2;
			data[i][2] = x3;
		}

	}

	/**
	 * Get Vectors from multidimensional data container
	 * 
	 * @param dh
	 *            Input data
	 */

	public VHolder(DataHolder dh) {

		title = dh.getRelation();
		data = new Double[dh.getSize()][dh.getDimention()+1];
		names = new String[dh.getDimention()+1];
		nrows=dh.getSize();
		names[0]="No";
		
		for (int i = 0; i < dh.getDimention(); i++) {
			if (dh.getName(i) != null) names[i+1]=dh.getName(i);
			if (dh.getName(i) == null)  names[i+1]=Integer.toString(i);
		}

		ncols=names.length;
		for (int i = 0; i < dh.getSize(); i++) {
			DataPoint dp = dh.getRow(i);
			data[i][0] = (double)(i + 1);
			for (int j = 0; j < dh.getDimention(); j++) {
				double x = dp.getAttribute(j);
				data[i][j + 1] = x;

			} // end loop over columns
		}

	}

	/**
	 * Show PND
	 * 
	 * @param hh
	 */
	public VHolder(PND hh) {

		title = "PND: " + hh.getTitle();
		int ntot = hh.getDimension();
		names = new String[ntot+1];
		data = new Double[hh.size()][ntot+1];
		names[0]="No";
			
		
		
		for (int j = 0; j < ntot; j++) {
			String x = "c" + Integer.toString(j + 1);
			names[j+1]=x;
		}
		
		nrows=hh.size();
		ncols=names.length;
		
		for (int i = 0; i < hh.size(); i++) {
			double[] a = hh.get(i);		
			data[i][0]=(double)(i+1);	
			for (int k = 0; k < a.length; k++) data[i][k+1]=a[k];
		} // end loop

	}

	/**
	 * Show PNI
	 * 
	 * @param hh
	 */
	public VHolder(PNI hh) {

		title = "PNI: " + hh.getTitle();
		int ntot = hh.getDimension();
		names = new String[ntot+1];
		data = new Double[hh.size()][ntot+1];
		names[0]="No";
			
		for (int j = 0; j < ntot; j++) {
			String x = "c" + Integer.toString(j + 1);
			names[j+1]=x;
		}
		
		
		nrows=hh.size();
		ncols=names.length;
		
		
		for (int i = 0; i < hh.size(); i++) {
			int[] a = hh.get(i);		
			data[i][0]=(double)(i+1);	
			for (int k = 0; k < a.length; k++) data[i][k+1]=(double)a[k];
		} // end loop

	}

	
	
	


	/**
	 * Create a VHolder object from P1D container. The size of the vectors will
	 * be adjusted depending on the size of P1D
	 * 
	 * @param hh
	 *            Input P1D container
	 */

	public VHolder(P1D hh) {

		

		title = "P1D: " + hh.getTitle();
	

		if (hh.dimension() == 2) {
			data = new Double[hh.size()][hh.dimension()+1];
			names = new String[] { "No", "x", "y" };
			nrows=hh.size();
			ncols=names.length;
			for (int i = 0; i <nrows; i++) {
				int x1 = i + 1;
				data[i][0]=(double)x1;
				data[i][1]=hh.getX(i);
				data[i][2]=hh.getY(i);
			}
			return;
		}
		else if (hh.dimension() == 3) {
		
			data = new Double[hh.size()][hh.dimension()+1];
			names = new String[] { "No", "x", "y", "errY" };
			nrows=hh.size();
			ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				int x1 = i + 1;
				data[i][0]=(double)x1;
				data[i][1]=hh.getX(i);
				data[i][2]=hh.getY(i);
				data[i][3]=hh.getYupper(i);
				
			}
			return;
		} else if (hh.dimension() == 4) {
			data = new Double[hh.size()][hh.dimension()+1];
			names = new String[] { "No", "x", "y", "errY Up","errY Low" };
			nrows=hh.size();
			ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				int x1 = i + 1;
				data[i][0]=(double)x1;
				data[i][1]=hh.getX(i);
				data[i][2]=hh.getY(i);
				data[i][3]=hh.getYupper(i);
				data[i][4]=hh.getYlower(i);
				
			}
			return;
		} else if (hh.dimension() == 6) {
			data = new Double[hh.size()][hh.dimension()+1];
			names = new String[] { "No", "x", "y", "errX Left","errX right","errY Up","errY Low"};
			nrows=hh.size();
			ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				int x1 = i + 1;
				data[i][0]=(double)x1;
				data[i][1]=hh.getX(i);
				data[i][2]=hh.getY(i);
				data[i][3]=hh.getXleft(i);
				data[i][4]=hh.getXright(i);
				data[i][5]=hh.getYupper(i);
				data[i][6]=hh.getYlower(i);
				
			}
			return;
		} else if (hh.dimension() == 10) {
			data = new Double[hh.size()][hh.dimension()+1];
			names = new String[] { "No", "x", "y", "errX Left","errX right","errY Up","errY Low",
				      "errX(sys) Left", "errX(sys) right","errY(sys) Up","errY(sys) Low"};
			nrows=hh.size();
			ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				int x1 = i + 1;
				data[i][0]=(double)x1;
				data[i][1]=hh.getX(i);
				data[i][2]=hh.getY(i);
				data[i][3]=hh.getXleft(i);
				data[i][4]=hh.getXright(i);
				data[i][5]=hh.getYupper(i);
				data[i][6]=hh.getYlower(i);
				data[i][7]=hh.getXleftSys(i);
				data[i][8]=hh.getXrightSys(i);
				data[i][9]=hh.getYupperSys(i);
				data[i][10]=hh.getYlowerSys(i);
				
			}
			return;
		}
       
	}

	/**
	 * Create a VHolder object from P2D container. The size of the vectors will
	 * be adjusted depending on the size of P2D
	 * 
	 * @param hh
	 *            Input P2D container
	 */

	public VHolder(P2D hh) {

		title = "P2D: " + hh.getTitle();
		data = new Double[hh.size()][4];
		names = new String[] { "No", "x", "y", "z" };
		nrows=hh.size();
		ncols=names.length;
		for (int i = 0; i < hh.size(); i++) {
			int x1 = i + 1;
			double x2 = hh.getX(i);
			double x3 = hh.getY(i);
			double x4 = hh.getZ(i);
			data[i][0]=(double) x1;
			data[i][1]=x2;
			data[i][2]=x3;
			data[i][3]=x4;
		}

	}

	/**
	 * Create a VHolder object from P0D container. The size of the vectors will
	 * be adjusted depending on the size of P0D
	 * 
	 * @param hh
	 *            Input P0D container
	 */

	public VHolder(P0D hh) {

		data = new Double[hh.size()][2];
		names = new String[] { "No", "value"};
		title = "P0D: " + hh.getTitle();
		nrows=hh.size();
		ncols=names.length;
		for (int i = 0; i < hh.size(); i++) {
			int x1 = i + 1;
			double x2 = hh.get(i);
			data[i][0]=(double)x1;
			data[i][1]=x2;
		}

	}

	/**
	 * Create a VHolder object from P0I container. The size of the vectors will
	 * be adjusted depending on the size of P0I
	 * 
	 * @param hh
	 *            Input P0I container
	 */

	public VHolder(P0I hh) {

		data = new Double[hh.size()][2];
		names = new String[] { "No", "value"};
		title = "P0D: " + hh.getTitle();
		nrows=hh.size();
		ncols=names.length;
		
		for (int i = 0; i < hh.size(); i++) {
			int x1 = i + 1;
			double x2 = (double)hh.get(i);
			data[i][0]=(double)x1;
			data[i][1]=x2;
		}

	}
	
	
	
	/**
	 * Create a VHolder object from P3D container. The size of the vectors will
	 * be adjusted depending on the size of P3D
	 * 
	 * @param hh
	 *            Input P3D container
	 */

	public VHolder(P3D hh) {

		names = new String[] {"No", "x","dx", "y", "dy", "z", "dz"};
		data = new Double[hh.size()][7];
		title = "P3D: " + hh.getTitle();
		nrows=hh.size();
		ncols=names.length;
		for (int i = 0; i < hh.size(); i++) {
			int x1 = i + 1;
			double x2 = hh.getX(i);
			double x3 = hh.getY(i);
			double x4 = hh.getZ(i);
			double dx2 = hh.getDX(i);
			double dx3 = hh.getDY(i);
			double dx4 = hh.getDZ(i);
			data[i][0]=(double)x1;
			data[i][1]=x2;
			data[i][2]=dx2;
			data[i][3]=x3;
			data[i][4]=dx3;
			data[i][5]=x4;
			data[i][6]=dx4;
		}

	}

	/**
	 * Get title of the container
	 * 
	 * @return Title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Return Vector with the names
	 * 
	 * @return Names of the columns
	 */

	public String[] getNames() {
		return names;
	}

	/**
	 * Return a Vector with the data (rows of the dimension obtained by calling
	 * dimen()).
	 * 
	 * @return actual data
	 */

	public Double[][] getData() {
		return data;
	}
	
	
	
	
	/**
	 * Set data from 2D array
	 * @param data 2D array
	 */

	public void setData(double[][] data) {
		
		nrows=data.length;
		ncols=data[0].length;
		title="1d array";
		names=new String[ncols];
		
		this.data = new Double[data.length][data[0].length];
		for (int row=0; row < data.length; row++) {
			names[row]=Integer.toString(row);
		    // Loop through the columns in the rowth row.
		    for (int col=0; col < data[0].length; col++){

			// Add the addMe value to the [row][col] entry
			// in the array.
			 this.data[row][col] = data[row][col];
		    }
		}
		
	}
	
	
	
	/**
	 * Set data from 1D array
	 * @param data 1D array
	 */

	public void setData(double[] data) {
		
		nrows=data.length;
		ncols=1;
		title="1d array";
		names=new String[ncols];
		this.data = new Double[data.length][0];
		for (int row=0; row < data.length; row++) {
			names[row]=Integer.toString(row);
			this.data[row][0] = data[row]; 
		    
		}
		
	}
	

        public String toString() {
              String s="names="+Arrays.deepToString(names);
              s=s+" data="+Arrays.deepToString(data);
              return s;
       }
	



        public void setData(Double[][] data){
           this.data=data;
        };

	/**
	 * Return the dimension of the data
	 * 
	 * @return Dimension
	 */

	public int dimen() {
		return ncols;
	}

	/**
	 * Return the size of the data, i.e. the number of rows
	 * 
	 * @return The number of rows
	 */

	public int size() {
		return nrows;
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
