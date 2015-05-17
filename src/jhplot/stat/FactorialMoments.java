/*
 *
 Permission to use, copy and modify this software and its documentation for 
 NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement to the author, Dr Sergei Chekanov
 appears in all copies and associated documentation or publications.

 Redistributions of the source code, or parts of the source codes, 
 must retain the copyright notice, this list of conditions and the following disclaimer 
 (all at the top of each source code) and requires the written permission of Sergei Chekanov

 Redistribution in binary form of all or parts of a class must reproduce the 
 copyright notice, this list of conditions and the following disclaimer in the documentation and/or other 
 materials provided with the distribution and requires the written permission of Sergei Chekanov
 */

package jhplot.stat;

import java.lang.Math;

import jhplot.P1D;
import jhplot.gui.HelpBrowser;

/**
 * Normalised factorial moments (NFM). They characterise local
 * multiplicity fluctuations inside a restricted phase space. It can calculate
 * factorial moments (getFM() method) or normalized factorial moment (getNFM()
 * method). The original version with horizontal binning was proposed in:
 * A.Bialas, R.Peschanski, Nucl. Phys. B273 (1986) 703 B308 (1988) 857
 * 
 * @author S.Chekanov
 * 
 */

public class FactorialMoments {

	private int Bins = 0;
	private double Min = 0;
	private int[][] ICC;
	private int Nmax;
	private int[] IBI;
	private double[] BI;
	private int IH[][][];
	private int CM[][][];
	private int Nev = 0;
	private int IL[][];
	private int ILE[][];
	private int IMAX = 0;
	private double[][] FA;
	private double[][] FAA;
	private double[][] EA;
	private double[][] EAA;

	/**
	 * Initialize factorial-moment calculations.
	 * 
	 * 
	 * @param NmaxOrder
	 *            Maximum order of factorial moments for the calculations
	 * @param Bins
	 *            Defines Max number of bins used to divide the phase space
	 *            (>1). The actual number of divisions is step*Bins. Therefore,
	 *            10 bins with step=4 means 400 divisions between Min and Max
	 * @param step
	 *            used to increase step for divisions
	 * @param Min
	 *            Min value in X
	 * @param Max
	 *            Max value in X
	 */
	public FactorialMoments(int NmaxOrder, int Bins, int step, double Min,
			double Max) {

		this.Nmax = NmaxOrder;
		this.Bins = Bins;
		this.Min = Min;
		Nmax = 5;

		if (Bins < 2) {
			System.out.println("Number of bins should be larger than 2");
			return;
		}
		if (Nmax < 2) {
			System.out.println("Order if BP should be larger than 2");
			return;
		}

		if (Nmax > 10) {
			System.out
					.println("The factorial moment order cannot be larger than 10 (not implemented!)");
			return;
		}

		IBI = new int[Bins];
		for (int i = 0; i < Bins; i++)
			IBI[i] = 1 + i * step;
		IMAX = IBI[Bins - 1];
		BI = new double[Bins];

		for (int i = 0; i < Bins; i++) {
			BI[i] = (Max - Min) / (double) IBI[i];
		}

		ICC = new int[Bins][IMAX];
		IH = new int[Nmax][Bins][IMAX];
		CM = new int[Nmax][Bins][IMAX];
		IL = new int[Nmax][Bins];
		ILE = new int[Nmax][Bins];
		Nev = 0;

		// set to zero
		for (int n = 0; n < Nmax; n++) {
			for (int i = 0; i < Bins; i++) {
				for (int j = 0; j < IBI[i]; j++) {
					IH[n][i][j] = 0;

				}
			}
		}

		for (int n = 0; n < Nmax; n++) {
			for (int i = 0; i < Bins; i++) {
				IL[n][i] = 0;
				ILE[n][i] = 0;

			}
		}

	}

	/**
	 * Collect information about sampling. Put this method in a loop and pass
	 * vector with particle characteristics.
	 * 
	 * @param v
	 *            - vector characterizing particles (like momentum, speed etc)
	 */
	public void run(double[] v) {

		Nev++;

		// set to zero before filling
		for (int i = 0; i < Bins; i++)
			for (int j = 0; j < IBI[i]; j++)
				ICC[i][j] = 0;

		// calculate number of particles in each bin
		for (int i = 0; i < Bins; i++) {
			for (int j = 0; j < IBI[i]; j++) {
				double m1 = Min + BI[i] * j;
				double m2 = Min + BI[i] * (j + 1);
				for (int m = 0; m < v.length; m++)
					if (v[m] > m1 && v[m] < m2)
						ICC[i][j]++;
			}
		}

		// collect event probabilities
		for (int i = 0; i < Bins; i++) {
			for (int j = 0; j < IBI[i]; j++) {
				IH[0][i][j] = ICC[i][j];
				IH[1][i][j] = ICC[i][j] * (ICC[i][j] - 1);
				if (Nmax > 2)
					IH[2][i][j] = IH[1][i][j] * (ICC[i][j] - 2);
				if (Nmax > 3)
					IH[3][i][j] = IH[2][i][j] * (ICC[i][j] - 3);
				if (Nmax > 4)
					IH[4][i][j] = IH[3][i][j] * (ICC[i][j] - 4);
				if (Nmax > 5)
					IH[5][i][j] = IH[4][i][j] * (ICC[i][j] - 5);
				if (Nmax > 6)
					IH[6][i][j] = IH[5][i][j] * (ICC[i][j] - 6);
				if (Nmax > 7)
					IH[7][i][j] = IH[6][i][j] * (ICC[i][j] - 7);
				if (Nmax > 8)
					IH[8][i][j] = IH[7][i][j] * (ICC[i][j] - 8);
				if (Nmax > 9)
					IH[9][i][j] = IH[8][i][j] * (ICC[i][j] - 9);

			}
		}

		for (int n = 0; n < Nmax; n++) {
			for (int i = 0; i < Bins; i++) {
				for (int j = 0; j < IBI[i]; j++)
					CM[n][i][j] = IH[n][i][j] * IH[n][i][j];
			}
		}

		for (int n = 0; n < Nmax; n++) {
			for (int i = 0; i < Bins; i++) {
				for (int j = 0; j < IBI[i]; j++) {
					IL[n][i] = IL[n][i] + IH[n][i][j];
					ILE[n][i] = ILE[n][i] + CM[n][i][j];
				}
			}
		}

	}

	/**
	 * Evaluate factorial moments at the end of the run
	 * 
	 * @return true if success
	 */
	public boolean eval() {

		boolean tmp = true;
		double AN = (double) Nev;

		for (int n = 0; n < Nmax; n++) {
			for (int i = 0; i < Bins; i++) {
				for (int j = 0; j < IBI[i]; j++) {
					IL[n][i] = IL[n][i] + IH[n][i][j];
					ILE[n][i] = ILE[n][i] + CM[n][i][j];
				}
				IL[n][i] = IL[n][i] / IBI[i];
				ILE[n][i] = ILE[n][i] / IBI[i];

			}
		}

		FA = new double[Nmax][Bins];
		FAA = new double[Nmax][Bins];
		EA = new double[Nmax][Bins];
		EAA = new double[Nmax][Bins];

		for (int n = 0; n < Nmax; n++) {
			for (int i = 0; i < Bins; i++) {
				FA[n][i] = IL[n][i] / AN;
				EA[n][i] = ILE[n][i] / AN;
			}
		}

		// NFM calculations
		for (int n = 1; n < Nmax; n++) {
			for (int i = 0; i < Bins; i++) {
				FAA[n][i] = FA[n][i] / Math.pow(FA[0][i], n + 1);
			}
		}

		// error calculations starting from F2:
		for (int n = 1; n < Nmax; n++) {
			int iipp = (n + 1) * 2;
			for (int i = 0; i < Bins; i++) {
				double DWA = EA[n][i] - (FA[n][i] * FA[n][i]);
				double DWAA = Math.pow(FA[0][i], iipp);
				if (DWA < 0) {
					DWA = 0.1;
					tmp = false;
				}
				double RED = DWAA * (AN - 1);
				EAA[n][i] = Math.sqrt(DWA / RED);

			}
		}

		/*
		 * for (int n = 1; n < Nmax; n++) { for (int i = 0; i < Bins; i++) { //
		 * take logs FAA[n][i]=Math.sqrt(
		 * (EAA[n][i]*EAA[n][i])/(FAA[n][i]*FAA[n][i])) ;
		 * EAA[n][i]=Math.log(FAA[n][i]); } }
		 */

		return tmp;

	}; // end get result

	/**
	 * Return results: NFM as a function of number of bins. The order should be
	 * >1 but smaller than 5;
	 * 
	 * @param order
	 *            order of normalized factorial moment (from 2-5)
	 * @return normalized factorial moment
	 */
	public P1D getNFM(int order) {

		if (order > Nmax) {
			System.out.println("NFM order is larger then allowed max 8");
			return null;
		}

		if (order < 2) {
			System.out.println("NFM order is too small");
			return null;
		}

		P1D pp = new P1D("NFM_{" + Integer.toString(order) + "}");

		// always start from second division
		for (int i = 1; i < Bins; i++) {
			pp.add(IBI[i], FAA[order - 1][i], EAA[order - 1][i]);

		}

		return pp;

	}

	/**
	 * Return factorial moments (without normalization): log(FM) as a function
	 * of number of bins. The order should be >1 but smaller than 5;
	 * 
	 * @param order
	 *            order of factorial moment (from 2-10)
	 * @return factorial moment
	 */
	public P1D getFM(int order) {

		if (order > Nmax) {
			System.out.println("FM order is larger then allowed max 8");
			return null;
		}

		if (order < 2) {
			System.out.println("FM order is too small");
			return null;
		}

		P1D pp = new P1D("F_{" + Integer.toString(order) + "}");

		for (int i = 0; i < Bins; i++) {
			pp.add(IBI[i], FA[order - 1][i], EA[order - 1][i]);

		}

		return pp;

	}

	/**
	 * Return average multiplicity as a function of bin
	 * 
	 * @return average multiplicity in bins
	 */
	public P1D getAv() {

		P1D pp = new P1D("average multiplicity");

		for (int i = 0; i < Bins; i++) {
			pp.add(IBI[i], FA[0][i], EA[0][i]);

		}

		return pp;

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
