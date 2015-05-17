package jhplot.stat;

import java.lang.Math;

import jhplot.P1D;
import jhplot.gui.HelpBrowser;



/**
* Calculations of bunching parameters. They haracterize  local multiplicity
* fluctuations inside a phase space. Calculations are done up to BP(6) order 
* For a Poisson distribution it should be 1 
* for all divisions
*
* Here are relevant  papers: <p>
*  1) LOCAL MULTIPLICITY FLUCTUATIONS IN HADRONIC Z DECAY.
*     L3 Collaboration (M. Acciarri et al.). CERN-PPE-97-165, Dec 1997. 18pp.
*     Phys.Lett.B429:375-386,1998 
* <p>
*   Other papers: <br> 
*    1) Bunching Parameter and Intermittency in High-Energy Collisions
*       by S.V.Chekanov and V.I.Kuvshinov
*       Acta Phys. Pol. B25 (1994) p.1189-1197<p> 
*    2) Multifractal Multiplicity Distribution in Bunching-Parameter Analysis
*       by  S.V.Chekanov and V.I.Kuvshinov
*       J. Phys G22 (1996), p.601-610<p> 
*    3) Generalized Bunching Parameters and Multiplicity Fluctuations in Restricted Phase-Space Bins
*       by  S.V.Chekanov, W.Kittel and V.I.Kuvshinov
*       Z. Phys. C74 (1997) p.517-529, hep-ph/9606335 <p> 
*    4) Scale-Invariant Dynamical Fluctuations in Jet Physics
*       by  S.V.Chekanov
*       Eur. Phys. J. C6 (1999), 331-342, hep-ph/9804316 <p> 
*    5) Suppression of multi-gluon fluctuations in jets at a Linear Collider
*       by  S. V. Chekanov
*       Phys.Lett. B509 (2001) 74-80, hep-ph/0101233 
* 
* @author S.Chekanov
*
*/

public class BunchingParameters {
	
	
	private int Bins=0;
	private double Min=0;
	private int [][] ICC;
	private int Nmax;  
	private double[][] BP;
	private double[][] ER;
	private int[]   IBI;
	private double[] BI;
	private int NCO[][][];
	private int IH[][][];
	private int CM[][][][];                    
	private int Nev=0;
	private double[][] HB;
	private int IMAX=0;
	
	
	/**
	 * Initialize Bunching parameter.
	 * 
	 * @param NmaxOrder Max order of the bunching parameter (starting from 2)!
	 * @param Bins Defines Max number of bins used to divide the phase space (>1).
	 *             The actual number of divisions is step*Bins. Therefore, 10
	 *              bins with step=4 means 400  divisions between Min and Max
	 * @param step used to increase step for divisions             
	 * @param Min  Min value in X
	 * @param Max  Max value in X
	 */
	public BunchingParameters(int NmaxOrder, int Bins, int step, double Min, double Max){
		
		this.Bins=Bins;
		this.Min=Min;
		this.Nmax=NmaxOrder+1;
	    
		
		
		if (this.Bins<2){
			System.out.println("Number of bins should be larger than 2");
			return;
		}
		if (this.Nmax<3){
			System.out.println("Order if BP should be larger than 2");
			return;
		}
		
		
		IBI=new int[Bins];
		for (int i = 0; i<Bins; i++) IBI[i]=1+i*step; 
		IMAX=IBI[Bins-1];
		BI=new double[Bins];
		
		
		/*
	
		if (Bins==2) {IBI[0]=1; IBI[1]=2;} 
		if (Bins==3) {IBI[0]=1; IBI[1]=2;   IBI[2]=3; };
		if (Bins==3) {IBI[0]=2; IBI[1]=4; IBI[2]=6;};
		if (Bins==4) {IBI[0]=2; IBI[1]=4; IBI[2]=6; IBI[3]=8;};
		if (Bins==5) {IBI[0]=2; IBI[1]=4; IBI[2]=6; IBI[3]=8; IBI[4]=10; };
		if (Bins>5) {
			IBI[0]=2; IBI[1]=4; IBI[2]=6; IBI[3]=8; IBI[4]=10; 
			for (int i = 5; i < IBI.length; i++) {
				IBI[i]=10+(i-4)*4; 
			}
			
		}
	    */
		
		
	    for (int i = 0; i < Bins; i++) {
			BI[i] = (Max-Min)/ (double)IBI[i];
		}
	    
	   
	    
		
		ICC=new int[Bins][IMAX];
		NCO=new int[Nmax][Bins][IMAX];
		IH=new int[Nmax][Bins][IMAX];
		CM=new int[Nmax][Nmax][Bins][IMAX];
		HB = new double [Nmax][IMAX];
	    BP= new double[Nmax][Bins]; 
	    ER= new double[Nmax][Bins]; 
		Nev=0;
		
		
		// set to zero
		 for (int n = 0; n < Nmax; n++) {
			   for (int i = 0; i < Bins; i++) {
				   for (int j = 0; j < IBI[i]; j++) {  
					   NCO[n][i][j]=0;
					   IH[n][i][j]=0;
				  
			   }}
		   }
		
		
		
		
		
		
	}
	
	
	/**
	 * Collect information about sampling.
	 * Put this method in a loop and pass vector with particle
	 * characteristics.
	 * @param v - vector characterizing particles (like momentum, speed etc)
	 */
   public void run(double[] v){
		
	   
	   Nev++;
	   
	   // set to zero before filling
	   for (int i = 0; i< Bins; i++) 
		   for (int j = 0; j<IBI[i]; j++) ICC[i][j]=0;
	  
	   
	   // calculate number of particles in each bin
	       for (int i = 0; i < Bins; i++)   {
		   for (int j = 0; j < IBI[i]; j++) {
			   double m1=Min+BI[i]*j;
			   double m2=Min+BI[i]*(j+1);
			   for (int m = 0; m < v.length; m++) 
		                  if (v[m]>m1 && v[m]<m2) ICC[i][j]++;	
		   }
		   }
		   
		
		   
		   
		 // collect event  probabilities  
		   for (int n = 0; n < Nmax; n++) {
			   for (int i = 0; i < Bins; i++) {
				   for (int j = 0; j<IBI[i]; j++) { 
				   
				   if (ICC[i][j] == n) {
					       NCO[n][i][j] = 1;
					       IH[n][i][j]++;
				   } else
					        NCO[n][i][j] = 0;
					   
				   }
			   }	   
		   }
		   

		   // calculate  covariance for probabilities
		   for (int i = 0; i < Bins; i++) {
			   for (int j = 0; j < IBI[i]; j++) { 
				   for (int n1 = 0; n1 < Nmax-1; n1++) {
				   for (int n2 = 0; n2 < Nmax-1; n2++) 
        			    CM[n1][n2][i][j] = CM[n1][n2][i][j]+NCO[n1][i][j]*NCO[n2][i][j];	   
					   
			      }
			     }
			   }
			   
		 
	
		   
	   
		
	}
   
   
   
   /**
    * Evaluate bunching parameters at the end of the run
    * @return true if success
    */
   public boolean  eval() {
	
	   
	     boolean tmp=true;
        	   
	    
	   
	   HB = new double[Nmax][Bins];
	   for (int i = 0; i < Nmax-1; i++) 	    
		   for (int j = 0; j < Bins; j++) HB[i][j]=0;  
	
	   
	   
   
	   for (int n=0; n< Nmax; n++) {
		   for (int i = 0; i < Bins; i++) {
			   for (int j = 0; j < IBI[i]; j++) { 
				   HB[n][i]=HB[n][i]+IH[n][i][j];
			   }
			   HB[n][i]= HB[n][i] / (double)IBI[i];
		   }
	   }
	   
	   
	   for (int i = 1; i < Nmax-1; i++) {
		  double f=((double)(i+1)/(double)i); 
		  
		  for (int j = 0; j< Bins; j++)
			  if (HB[i][j] == 0) {
		      	      BP[i][j]=-1.;  
			  } else {
			  BP[i+1][j] = f* (HB[i+1][j]*HB[i-1][j])/(HB[i][j]*HB[i][j]);
			  }
			}                                                      

	 
	   
	   // calculation of BP
	   double[][] H = new double[Nmax][Bins];
	   double AN=(double)Nev;
	   
	   for (int i = 0; i < Nmax; i++) 	    
		   for (int j = 0; j < Bins; j++) H[i][j]= HB[i][j]/AN;
	   
	   double[][][] CE = new double[Nmax][Nmax][Bins];
	   
	   
	   for (int i1 = 0; i1 < Nmax; i1++) 
		   for (int i2 = 0; i2 < Nmax-1; i2++) 
		   for (int j = 0; j < Bins; j++) CE[i1][i2][j]=0.0;
	   
	   
	   for (int i1 = 0; i1 < Nmax; i1++) 
		 for (int i2 = 0; i2 < Nmax; i2++) 
		   for (int j = 0; j < Bins; j++) 
			   for (int i = 0; i < IBI[j]; i++)   
			   CE[i1][i2][j]=CE[i1][i2][j]+CM[i1][i2][j][i];
	   
	   
	    double XN=AN*AN;

	    
	    for (int m = 0; m < Bins; m++) {
			double ba=(double)IBI[m];
			double b=ba*ba;
			for (int i1= 0; i1< Nmax; i1++) {
				for (int i2= 0; i2< Nmax; i2++) {
				CE[i1][i2][m]=CE[i1][i2][m]-AN*ba*H[i1][m]*H[i2][m];
			        CE[i1][i2][m]=CE[i1][i2][m]/(XN*b);
				}
			}
	    }
			
			
	    double A1,A2,A3,A4,A5,A6,AM;
	    
		// get errors for BP	
		for (int i = 1; i< Nmax-1; i++) {	
		for (int m = 0; m<Bins; m++) {
		if (H[i][m] == 0.0) {
			          H[i][m]=0.00001;
		                  tmp=false;
		                  }
		
		double p4=Math.pow(H[i][m],4);	
		double p2=H[i-1][m]*H[i-1][m];
	        double p5=Math.pow(H[i][m],5);
	
		A1=CE[i+1][i+1][m]*p2/p4;
		A2=4*CE[i][i][m]*(H[i+1][m]*H[i+1][m])*p2/Math.pow(H[i][m],6);
		A3=CE[i-1][i-1][m]*(H[i+1][m]*H[i+1][m])/p4;
		A4=2*CE[i+1][i-1][m]*H[i+1][m]*H[i-1][m]/p4;
		A5=4*CE[i+1][i][m]*H[i+1][m]*p2/p5;
		A6=4*CE[i][i-1][m]*(H[i+1][m]*H[i+1][m])*H[i-1][m]/p5;	 
				    
				       
             AM=A1+A2+A3+A4-A5-A6;
	     ER[i+1][m]=( (double)(i+1)/i)*Math.sqrt(AM);
	     
	     // take logs
	     // ER[i+1][m]=Math.sqrt( (ER[i+1][m]*ER[i+1][m])/(BP[i+1][m]*BP[i+1][m])) ;
	     // BP[i+1][m]=Math.log(BP[i+1][m]);

		 }}
			
	   
	   return tmp;
	   
	   
   }; // end get result

   
   
   /**
    * Return results: BP as a function of number of bins.
    * The order should be >1 but smaller than 8
    * @param order order of bunching parameter
    * @return  bunching parameters
    */
    public P1D getBP(int order) {
    	
    	if (order>Nmax){
    		System.out.println("BP order is larger then allowed max 8");
    		return null;
    	}

    	if (order<2){
    		System.out.println("BP order is too small");
    		return null;
    	}
    	
    	P1D pp= new P1D("BP_{"+ Integer.toString(order)+"}");
    
        // always start from second division	
    	for (int i = 1; i < Bins; i++) {
			pp.add(IBI[i],BP[order][i],ER[order][i] );
			
		}
    	
    	return pp;
    	
    	
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
