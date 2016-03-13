/**
 *    Copyright (C)  D2Melt project. The jHPLot package by S.Chekanov and Work.ORG
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
 *    If you have received this program as a library with written permission from the D2Melt team,
 *    you can link or combine this library with your non-GPL project to convey the resulting work.
 *    In this case, this library should be considered as released under the terms of
 *    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
 *    provided you include this license notice and a URL through which recipients can access the
 *    Corresponding Source.
 **/
package jhplot;
import hep.aida.*;
import java.io.Serializable;
import java.util.*;
import jhplot.gui.HelpBrowser;

/**
 * A proxy for all DataMelt objects. This does not save graphical options since designed for interchange between different programs. 
 * 
 */

public class DProxy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String title;

        private Double[] D1;

        private Double[][] D2;

	private String type = "p1d";

	private String[] names;


          public DProxy() {
               title="";
               type = "p1d";
            };


/**
         * Create a proxy for histogram. 
         * 
         * @param P1D
         *            hh 
         */
          public DProxy(H1D h) {
             int ibins = h.getBins() + 2;
             D2 = new Double[ibins-2][7];
             names = new String[] {"lowEdge", "upperEdge","binHeight", "binError", "binEntries", "binMean", "binRms" };
             title=h.getTitle();
             type="h1d";
             for (int i=0; i < ibins-2; i++) {
                  D2[i][0]=h.binLowerEdge(i);
                  D2[i][1]=h.binUpperEdge(i);
                  D2[i][2]=h.binHeight(i);
                  D2[i][3]=h.binError(i);
                  D2[i][4]=(double)h.binEntries(i);
                  D2[i][5]=h.binMean(i);
                  D2[i][6]=h.binRms(i);
            }
            D1 = new Double[9];

            double xb=1;
            if (!h.isFixedBinning()) xb=0;
            D1[0]=xb;
            D1[1]=(double)h.getBins(); 
            D1[2]=h.mean();
            D1[3]=h.rms();
            D1[4]=(double)h.entries();
            D1[5]=h.getMin();
            D1[6]=h.getMax();
            D1[7]=h.getOverflowlowHeight();
            D1[8]=h.getUnderflowHeight();
        
          }


     /**
	 * Create a proxy for D2 of DMelt.
	 * 
	 * @param P1D
         *            hh 
	 */

	public DProxy(P1D hh) {

		title = hh.getTitle();
                this.type="p1d";

                D1 = new Double[2];
                D1[0]=(double)hh.dimension();
                D1[1]=(double)hh.size();

		if (hh.dimension() == 2) {
			names = new String[] {"x", "y" };
			D2 = new Double[hh.size()][hh.dimension()];
			int nrows=hh.size();
			int ncols=names.length;
			for (int i = 0; i <nrows; i++) {
				D2[i][0]=hh.getX(i);
				D2[i][1]=hh.getY(i);
			}
			return;
		}
		else if (hh.dimension() == 3) {
		
			D2 = new Double[hh.size()][hh.dimension()];
			names = new String[] { "x", "y", "errY" };
			int nrows=hh.size();
			int ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				D2[i][0]=hh.getX(i);
				D2[i][1]=hh.getY(i);
				D2[i][2]=hh.getYupper(i);
				
			}
			return;
		} else if (hh.dimension() == 4) {
			D2 = new Double[hh.size()][hh.dimension()];
			names = new String[] { "x", "y", "errY Up","errY Low" };
			int nrows=hh.size();
			int ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				D2[i][0]=hh.getX(i);
				D2[i][1]=hh.getY(i);
				D2[i][2]=hh.getYupper(i);
				D2[i][3]=hh.getYlower(i);
				
			}
			return;
		} else if (hh.dimension() == 6) {
			D2 = new Double[hh.size()][hh.dimension()];
			names = new String[] {"x", "y", "errX Left","errX right","errY Up","errY Low"};
			int nrows=hh.size();
			int ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				D2[i][0]=hh.getX(i);
				D2[i][1]=hh.getY(i);
				D2[i][2]=hh.getXleft(i);
				D2[i][3]=hh.getXright(i);
				D2[i][4]=hh.getYupper(i);
				D2[i][5]=hh.getYlower(i);
				
			}
			return;
		} else if (hh.dimension() == 10) {
			D2 = new Double[hh.size()][hh.dimension()];
			names = new String[] { "x", "y", "errX Left","errX right","errY Up","errY Low",
				      "errX(sys) Left", "errX(sys) right","errY(sys) Up","errY(sys) Low"};
			int nrows=hh.size();
			int ncols=names.length;
			for (int i = 0; i < nrows; i++) {
				D2[i][0]=hh.getX(i);
				D2[i][1]=hh.getY(i);
				D2[i][2]=hh.getXleft(i);
				D2[i][3]=hh.getXright(i);
				D2[i][4]=hh.getYupper(i);
				D2[i][5]=hh.getYlower(i);
				D2[i][6]=hh.getXleftSys(i);
				D2[i][7]=hh.getXrightSys(i);
				D2[i][8]=hh.getYupperSys(i);
				D2[i][9]=hh.getYlowerSys(i);
				
			}
			return;
		}
       
	}
      

         /**
         * Create a proxy for D2 of DMelt.
         * 
         * @param P2D
         *           hh 
         */

        public DProxy(P2D hh) {

                       title = hh.getTitle();
                       this.type="p2d";
                       D1 = new Double[2];
                       D1[0]=(double)3;
                       D1[1]=(double)hh.size();

                        names = new String[] {"x", "y", "z" };
                        D2 = new Double[hh.size()][3];
                        int nrows=hh.size();
                        int ncols=names.length;
                        for (int i = 0; i <nrows; i++) {
                                D2[i][0]=hh.getX(i);
                                D2[i][1]=hh.getY(i);
                                D2[i][2]=hh.getZ(i);
                        }
                        return;
                }


        /**
         * Create a proxy for D2 of DMelt.
         * 
         * @param P2D
         *           hh 
         */

        public DProxy(P0D hh) {

                       title = hh.getTitle();
                       this.type="p0d";
                        names = new String[] {"x"};
                        D1 = new Double[hh.size()];
                        int nrows=hh.size();
                        for (int i = 0; i <nrows; i++) {
                                D1[i]=hh.get(i);
                        }
                        return;
                }

        public DProxy(P0I hh) {
                       title = hh.getTitle();
                       this.type="p0i";
                        names = new String[] {"x"};
                        D1 = new Double[hh.size()];
                        int nrows=hh.size();
                        for (int i = 0; i <nrows; i++) {
                                D1[i]=(double)hh.get(i);
                        }
                        return;
                }


 
         /**
         * Get objects.
         * @return corresponding object. 
         */
        public Object get(){

          if (type.equals("p1d")){
           int dim=D1[0].intValue();
           P1D p1 = new P1D( title, dim );
           for (int i=0; i< D2.length; i++){
               if (dim==2) p1.add(D2[i][0],D2[i][1]);
               else if (dim==3) p1.add(D2[i][0],D2[i][1],D2[i][2]);
               else if (dim==4) p1.add(D2[i][0],D2[i][1],D2[i][2],D2[i][3]);
               else if (dim==6) p1.add(D2[i][0],D2[i][1],D2[i][2],D2[i][3],D2[i][4],D2[i][5]);
               else if (dim==10) p1.add(D2[i][0],D2[i][1],D2[i][2],D2[i][3],D2[i][4],D2[i][5],D2[i][6],D2[i][7],D2[i][8],D2[i][9]);
           }
           return p1;
          } 

          if (type.equals("h1d")){
           int isFixedBin=D1[0].intValue();
           int ibins=D1[1].intValue()+2;
           double Pmean=D1[2];
           double Prms=D1[3];
           int    Pentries=D1[4].intValue();
           double Pmin=D1[5];
           double Pmax=D1[6];
           double OverflowlowHeight=D1[7];
           double UnderflowHeight=D1[8];

           double [] edges = new double[ibins-1];
           double [] heights= new double[ibins];
           double [] errors = new double[ibins];
           double [] means= new double[ibins];
           double [] rms = new double[ibins];
           int [] entries = new int[ibins];
           H1D h1d=null;
           if ( isFixedBin==1) {
                    h1d = new H1D(title,ibins-2,Pmin,Pmax);
            } else {

               for (int i=0; i<ibins-2; i++){
                edges[i]=D2[i][0]; // lower 
               }
               edges[ibins-2]=D2[ibins-3][1]; 
              for (int i=0; i<edges.length; i++) System.out.println(edges[i]);
                   h1d = new H1D(title,edges);
            }

                  heights[0] = UnderflowHeight;
                  heights[ibins-1] = OverflowlowHeight; 

                  h1d.setBins( ibins );
                  h1d.setMin( Pmin );
                  h1d.setMax( Pmax);
                  h1d.setMeanAndRms(Pmean,Prms);

                    for (int i = 0; i < ibins-2; i++) {
                        heights[i+1] =  D2[i][2];
                        errors[i+1] =   D2[i][3];
                        entries[i+1] =  D2[i][4].intValue();
                        means[i+1] =  D2[i][5];
                        rms[i+1] =    D2[i][6];
                    }
                    h1d.setContents(heights,errors,entries,means,rms);
 
            return h1d;
           } // return h1d
    


          else if (this.type.equals("p2d")){
            P2D p1 = new P2D( title );
           for (int i=0; i< D2.length; i++){
              p1.add(D2[i][0],D2[i][1],D2[i][2]);
           }
           return p1;
          }


           else if (this.type.equals("p0d")){
           P0D p1 = new P0D( title );
           for (int i=0; i< D1.length; i++){
              p1.add(D1[i]);
           }
           return p1;
          }


          else if ( this.type.equals("p0i") ){
           P0I p1 = new P0I( title );
           for (int i=0; i< D1.length; i++){
              p1.add(D1[i].intValue());
           }
           return p1;
          }


          return null;
         }

	/**
	 * Get type of this function.
	 * @return type.  100+dimension - P1D type 
	 */
	public String getType() {
		return type;
	}



	/**
	 * Get the title of the function.
	 * 
	 * @returnTitle
	 */
	public String getTitle() {
		return this.title;

	}



	/**
	 * Set title.
	 * 
	 * @param title
	 */

	public void setTitle(String title) {
		this.title = title;
	}


         /*
	 * Set type. 1- F1D, 2-F2D, 3-F3D, 4-FND
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}



         /**
         * Return a 2D Vector with the D2
         * 
         * @return actual D2
         */

        public Double[][] getD2() {
                return D2;
        }



        /**
         * Set D1 from 2D array
         * @param D1 2D array
         */
        public void setD1( Double[] D1) {
                this.D1=D1;
        }






         /**
         * Return a 1D Vector with the D1
         * 
         * @return actual D1
         */

        public Double[] getD1() {
                return D1;
        }



        /**
         * Set D2 from 2D array
         * @param D2 2D array
         */
        public void setD2( Double[][] D2) {
                this.D2=D2;
        }










        /**
         * Set names from 2D array
         * @param D2 2D array
         */
        public void setNames( String[] names) {
                this.names=names;
        }


          public String toString() {
              String s="title="+title;
              s=s+" D2="+Arrays.deepToString(D2);
              return s;
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
         * Return Vector with the names
         * 
         * @return Names of the columns
         */

        public String[] getNames() {
                return names;
        }


}
