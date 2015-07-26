
package jhplot.math;

import jhplot.stat.Statistics;
import cern.jet.random.*;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import jhplot.math.exp4j.*;

import graph.ParseFunction;


/**
 * A package to create random 1D and 2D arrays.
 * 
 *  
 * @author S.Chekanov and J.Richet
 *
 */

public class StatisticSample {

    
	/**
	 * Random 2D array with integers
	 * @param m Rows
	 * @param n Columns
	 * @param i0 Min value
	 * @param i1 max value
	 * @return 2D array
	 */
    public static int[][] randomInt(int m, int n, int i0, int i1) {
        int[][] A = new int[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.randInt(i0, i1);
        return A;
    }

    /**
     * Random array with integers 
     * @param m  array size
     * @param i0 min value
     * @param i1 max value
     * @return  array
     */
    public  static int[] randomInt(int m, int i0, int i1) {
        int[] A = new int[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.randInt(i0, i1);
        return A;
    }



     /**
      * 2D array with uniform values
      * @param m Total number 
      * @param min  Min value
      * @param max  Max value
      * @return    array
      */
    public static double[] randUniform(int m, double min, double max) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.uniform(min, max);
        return A;
    }
    /**
     * 2D array with random uniform values
     * @param m  Rows
     * @param n  Columns
     * @param min Min value
     * @param max Max value
     * @return   array
     */
    public static double[][] randUniform(int m, int n, double min, double max) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.uniform(min, max);
        return A;
    }

    

    /**
     * 2D array with Dirac random values
     * @param m Rows
     * @param n Columns
     * @param values Values for function
     * @param prob   Probabilities
     * @return     array
     */
    public static double[][] randomDirac(int m, int n, double[] values, double[] prob) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.dirac(values, prob);
        return A;
    }

    /**
     * 1D array with Dirac random values
     * @param m Total number
     * @param values array with values for the function
     * @param prob   probability
     * @return     array
     */
    public static double[] randomDirac(int m, double[] values, double[] prob) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.dirac(values, prob);
        return A;
    }



/** Build an array with  Poisson distribution   
 *  @param mean mean of Poisson distribution 
 **/
    public static int[] randomPoisson(int m, double mean) {
        Poisson pp= new Poisson(mean);
        int[] A = new int[m];
        for (int i = 0; i < A.length; i++)
            A[i] = pp.next();
        return A;
    }








    /**
     * 2D array with Gaussian numbers
     * @param m Rows
     * @param n Columns
     * @param mu mean
     * @param sigma  standard deviation
     * @return   array
     */
    public static double[][] randomNormal(int m, int n, double mu, double sigma) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.normal(mu, sigma);
        return A;
    }






    /**
     * 1D array with Gaussian numbers
     * @param m  Total number
     * @param mu mean 
     * @param sigma standard deviation
     * @return array
     */
    public static double[] randomNormal(int m, double mu, double sigma) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.normal(mu, sigma);
        return A;
    }

    /**
     * 2D array with Chi2
     * @param m Rows
     * @param n Columns
     * @param d degrees of freedom
     * @return array
     */
    public static double[][] randomChi2(int m, int n, int d) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.chi2(d);
        return A;
    }

    /**
     * 1D array with random numbers
     * @param m Total number
     * @param d degree of freedoms
     * @return array
     */
    public static double[] randomChi2(int m, int d) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.chi2(d);
        return A;
    }

    /**
     * 2D Log-normal distribution
     * @param m Rows
     * @param n Columns
     * @param mu mean
     * @param sigma sigma
     * @return array
     */
    public static double[][] randomLogNormal(int m, int n, double mu, double sigma) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.logNormal(mu, sigma);
        return A;
    }

    /**
     * 1D array with random Log-normal values
     * @param m total number
     * @param mu mean 
     * @param sigma sigma
     * @return array
     */
    public static double[] randomLogNormal(int m, double mu, double sigma) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.logNormal(mu, sigma);
        return A;
    }

    /**
     * 2D array with exponential random distribution
     * @param m Rows
     * @param n Colums
     * @param lambda lambda
     * @return array
     */
    public static double[][] randomExponential(int m, int n, double lambda) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.exponential(lambda);
        return A;
    }

    /**
     * 1D array with exponential numbers
     * @param m total numbers
     * @param lambda lambda
     * @return array
     */
    public static double[] randomExponential(int m, double lambda) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.exponential(lambda);
        return A;
    }

    /**
     * 2D array for Triangular random PDF
     * @param m Rows
     * @param n Columns
     * @param min  Min 
     * @param max  max
     * @return array
     */
    public static double[][] randomTriangular(int m, int n, double min, double max) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.triangular(min, max);
        return A;
    }

    /**
     * 1D array with Triangular random PDF
     * @param m total number
     * @param min Min 
     * @param max max
     * @return array
     */
    public static double[] randomTriangular(int m, double min, double max) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.triangular(min, max);
        return A;
    }

    /**
     * 2D array for Triangular
     * @param m Rows
     * @param n Columns
     * @param min Min
     * @param med Median
     * @param max Max
     * @return array
     */
    public static double[][] randomTriangular(int m, int n, double min, double med, double max) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.triangular(min, med, max);
        return A;
    }

    /**
     * 1D array for Triangular
     * @param m total number
     * @param min Min
     * @param med Median
     * @param max Max
     * @return array
     */
    public static double[] randomTriangular(int m, double min, double med, double max) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.triangular(min, med, max);
        return A;
    }

    /**
     * Random beata distribution
     * @param m Rows
     * @param n Columns
     * @param a alpha
     * @param b beta
     * @return array
     */
    public static double[][] randomBeta(int m, int n, double a, double b) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.beta(a, b);
        return A;
    }

    /**
     * 1D Random Beta distribution 
     * @param m total number
     * @param a alpha
     * @param b beta
     * @return array
     */
    public static double[] randomBeta(int m, double a, double b) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.beta(a, b);
        return A;
    }

    /**
     * 2D Cauchy PDF 
     * @param m Rows
     * @param n Colums
     * @param mu Mean
     * @param sigma Sigma
     * @return array
     */
    public static double[][] randomCauchy(int m, int n, double mu, double sigma) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.cauchy(mu, sigma);
        return A;
    }

    /**
     * 1D Cauchy PDF 
     * @param m total number
     * @param mu mean
     * @param sigma sigma
     * @return
     */
    public static double[] randomCauchy(int m, double mu, double sigma) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.cauchy(mu, sigma);
        return A;
    }

    /**
     * 2D Weibull
     * @param m Rows
     * @param n Columns
     * @param lambda lambda
     * @param c C
     * @return array
     */
    public static double[][] randomWeibull(int m, int n, double lambda, double c) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.weibull(lambda, c);
        return A;
    }


/**
 * 1D Weibull
 * @param m Rows
 * @param lambda lambda
 * @param c C
 * @return array
 **/
    public static double[] randomWeibull(int m, double lambda, double c) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.weibull(lambda, c);
        return A;
    }


   /**
 * Build 2D random array using analytic function. 
 * First build F1D, than get parse (getParse()) and use it as input for this method.
 * @param m Number of points
 * @param fun ParseFunction (get it as getParse() for F1D) 
 * @param maxFun max of the function
 * @param min Min value in X
 * @param max Max value in X 
 *
 * */
    public static double[][] randomRejection(int m, int n, Expression fun, double maxFun, double min, double max) {
        double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Random.rejection(fun, maxFun, min, max);
        return A;
    }

/**
 *  Build 1D array using analytic function. 
 *  First build F1D, than get parse (getParse()) and use it as input for this method.
 *  @param m Number of points
 *  @param fun ParseFunction (get it as getParse() for F1D) 
 *  @param maxFun max of the function
 *  @param min Min value in X
 *  @param max Max value in X 
 *  
 **/
    public static double[] randomRejection(int m, Expression fun, double maxFun, double min, double max) {
        double[] A = new double[m];
        for (int i = 0; i < A.length; i++)
            A[i] = Random.rejection(fun, maxFun, min, max);
        return A;
    }

    // Statistics sample methods

    
    /**
     * Get mean value
     * @param v vector
     */
    public static double mean(double[] v) {
        double mean = 0;
        int m = v.length;
        for (int i = 0; i < m; i++)
            mean += v[i];
        mean /= (double) m;
        return mean;
    }

    /**
     * Get mean
     * @param v 2D array
     * @return
     */
    public static double[] mean(double[][] v) {
        int m = v.length;
        int n = v[0].length;
        double[] mean = new double[n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                mean[j] += v[i][j];
        for (int j = 0; j < n; j++)
            mean[j] /= (double) m;
        return mean;
    }

    /**
     * Standard deviation
     * @param v vector
     * @return
     */
    public static double stddeviation(double[] v) {
        return Math.sqrt(variance(v));
    }

    /**
     * Variance
     * @param v
     * @return vector
     */
    public static double variance(double[] v) {
    	return Statistics.variance(v);
    }

    /**
     * Standard deviation
     * @param v
     * @return
     */
    public static double[] stddeviation(double[][] v) {
       
    	return Statistics.stddeviation(v);
    	
    }

    /**
     * Variance
     * @param v vector
     * @return 
     */
    public static double[] variance(double[][] v) {
    	
    	return Statistics.variance(v);
    	
    }

    /**
     * Covariance
     * @param v1 first vector
     * @param v2 second vector
     * @return
     */
    public static double covariance(double[] v1, double[] v2) {
    	
    	return Statistics.covariance(v1,v2);
    	
    }

    /**
     * Covariance
     * @param v1 first 2D  array
     * @param v2 second 2D array
     * @return
     */
    public static double[][] covariance(double[][] v1, double[][] v2) {
    	
    	return Statistics.covariance(v1,v2);
    	
    }

    /**
     * Covariance
     * @param v
     * @return
     */
    public static double[][] covariance(double[][] v) {
    	
    	return Statistics.covariance(v);
       
    }

    /**
     * Correlation coefficient, 
     * covariance(v1, v2) / Math.sqrt(variance(v1) * variance(v2)
     * @param v1 first vector
     * @param v2 second vector
     * @return
     */
    public static double correlation(double[] v1, double[] v2) {
    	
    	return Statistics.correlation(v1,v2);
        
    }

    /**
     * Correlation coefficient, 
     * covariance(v1, v2) / Math.sqrt(variance(v1) * variance(v2)
     * @param v1 first vector
     * @param v2 second vector
     * @return
     */
    public static double[][] correlation(double[][] v1, double[][] v2) {
    	
    	return Statistics.correlation(v1,v2);
        
    }

    /**
     * Correlation
     * @param v
     * @return
     */
    public static double[][] correlation(double[][] v) {
    	
    	
    	return Statistics.correlation(v);
    	
    }

    
    /**
     * Build integer array list with integer numbers from input random number
     * generator
     * @param Ntot total numbers
     * @param dist input random number distribution
     * @return array
     */
    public IntArrayList randomIntArrayList(int Ntot, AbstractDistribution dist) {
    	IntArrayList a= new IntArrayList(Ntot);
    	for (int i=0; i < Ntot; i++) a.add(dist.nextInt());
    	return a;
    	
    }
    
   
    /**
     * Build integer array list with integer numbers from input random number
     * generator
     * @param Ntot total numbers
     * @param dist input random number distribution
     * @return array
     */
    public int[] randomIntArray(int Ntot, Binomial dist 
    ) {
    	int[] a = new int[Ntot];
    	for (int i=0; i < Ntot; i++) a[i]=dist.nextInt();
    	return a;
    	
    }
    
    
    /**
     * Build 2D  integer array list with integer numbers from input random number
     * generator
     * @param rows   rows
     * @param colums  columns
     * @param dist input random number distribution
     * @return array
     */
    public int[][] randomIntArray(int rows, int columns,  AbstractDistribution dist) {
    	int[][] A = new int[rows][columns];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = dist.nextInt();
        return A;
    	
    }
 
    
    /**
     * Build 2D  integer array list with integer numbers from input random number
     * generator
     * @param rows   rows
     * @param colums  columns
     * @param dist input random number distribution
     * @return array
     */
    public double[][] randomDoubleArray(int rows, int columns,  AbstractDistribution dist) {
    	double[][] A = new double[rows][columns];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = dist.nextDouble();
        return A;
    	
    }
    
    
      
    
    /**
     * Build double array list with integer numbers from input random number
     * generator
     * @param Ntot
     * @param dist
     * @return array
     */
    public DoubleArrayList randomDoubleArrayList(int Ntot, AbstractDistribution dist) {
    	
    	DoubleArrayList a= new DoubleArrayList(Ntot);
    	for (int i=0; i < Ntot; i++) a.add(dist.nextDouble());
    	return a;
    	
    }
    

}
