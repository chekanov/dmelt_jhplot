package jhplot.bsom;

/* 
 * @(#)Matrix.java	1.3	20/9/96 Akio Utsugi
 */

import java.lang.Math;
import java.util.Random;

/**
 * Matrix is a class for matrix objects with basic linear calculation methods.
 * @version 1.2 17 Sep 1996
 * @author <a href="http://www.aist.go.jp/NIBH/~b0616/"> Akio Utsugi </a>
 */
public class Matrix
{
    final static double small = 1e-8;
    
    int row, col;
    double value[][];


/**
 * Construct a square matrix object.
 * @param n size
 */
    public Matrix(int n)
    {
	this(n, n);
    }
    
/**
 * Construct a rectangle matrix object.
 * @param row number of row
 * @param col number of column
 */
    public Matrix(int row, int col)
    {
	int i, j;
	
	this.row = row;
	this.col = col;
	value = new double[row][col];

	for(i=0; i<row; i++){
	    for(j=0; j<col; j++){
		value[i][j] = 0;
	    }
	}
	
    }

/*
 * Creation of special matrices
 */
    
/**
 * Make an identity matrix.
 * @param n size
 */
    static public Matrix identity(int n)
    {
	int i;
	
	Matrix ret = new Matrix(n);
	for(i=0; i<n; i++){
	    ret.value[i][i] = 1;
	}
	return ret;
    }

/**    
 * Make a standard Gaussian random Matrix.
 * @param row number of row
 * @param col number of column
 * @param seed random seed
 */
    static public Matrix random(int row, int col, int seed)
    {
	int i, j;
	
	Random rnd = new Random(seed);
	Matrix ret = new Matrix(row, col);

        if (Bsom.INIT == 0) {
	for(i=0; i<row; i++){
	    for(j=0; j<col; j++){
		ret.value[i][j] = rnd.nextGaussian();
	    }
	}
        };


        if (Bsom.INIT == 1) {
        for(i=0; i<row; i++){
            for(j=0; j<col; j++){
                ret.value[i][j] = Bsom.value[i][j];
            }
        }
        };





	return ret;
    }

/**
 * Same as random(row, col, 1).
 */   
    static public Matrix random(int row, int col)
    {
	return random(row, col, 1);
    }
    

/**
 * Copy the matrix.
 */
    public Matrix copy()
    {
	int i, j;
	
	Matrix ret = new Matrix(row, col);
	for(i=0; i<row; i++){
	    for(j=0; j<col; j++){
		ret.value[i][j] = value[i][j];
	    }
	}
	
	return ret;
    }


/*
 * Basic Matrix Calculations
 */


/**
 * Transpose: X'.
 */
    public Matrix transpose()
    {
        int i, j;
        
        Matrix ret = new Matrix(col, row);
        for(i=0; i< row; i++){
            for(j=0; j< col; j++){
                ret.value[j][i] = value[i][j];
            }
        }
        return ret;
    }
    

/**
 * Linear conversion: a*X + b*Y.
 */
    public Matrix linearConv(double a, double b, Matrix Y)
    {
	int i, j;
	
	int Y_row = Y.row;
	int Y_col = Y.col;
	
	if(row == Y_row && col == Y_col){
	    Matrix ret = new Matrix(row, col);
	    
	    for(i=0; i < row; i++){
		for(j=0; j < col; j++){
		    ret.value[i][j] = a*value[i][j] + b*Y.value[i][j];
		}
	    }
	    return ret;
	}
	else{
	    return null;
	}
    }

            
/**
 * Update by Linear conversion: X = a*X + b*Y.
 */
    public void updateLinearConv(double a, double b, Matrix Y)
    {
	int i, j;
	
	int Y_row = Y.row;
	int Y_col = Y.col;	

	for(i=0; i < row; i++){
	   for(j=0; j < col; j++){
	       value[i][j] = a*value[i][j] + b*Y.value[i][j];
	   }
	}
    }

    
/**
 * Scalar product: aX.
 */
    public Matrix multipliedBy(double a)
    {
	int i, j;
	
	Matrix ret = new Matrix(row, col);
	for(i=0; i< row; i++){
	    for(j=0; j< col; j++){
		ret.value[i][j] = a*value[i][j];
	    }
	}
	return ret;
    }


/**
 * Matrix multiplication: X*Y.
 */
    public Matrix multipliedBy(Matrix Y)
    {
	int i, j, k;
	double s;
	
	int Y_row = Y.row, Y_col = Y.col;
	
	if(col != Y_row){
	    return null;
	}
	
	Matrix ret = new Matrix(row, Y_col);
	
	for(i=0; i< row; i++){
	    for(j=0; j<Y_col; j++){
		s = 0;
		for(k=0; k<col; k++){
		    s += value[i][k] * Y.value[k][j];
		}
		ret.value[i][j] = s;
	    }
	}
	
	return ret;
    }
    
/**
 * Matrix division: X\Y.
 */
    public Matrix dividedBy(Matrix Y)
    {
	return this.dividedBy(Y, 2*col-1);
    }

/**
 * Matrix division with bandwidth: X\Y. 
 */
    public Matrix dividedBy(Matrix Y, int bw)
    {
    
	Matrix L, U, ret;
	int n, m, i, j, k;
	double w, s, pp;
	
	L = this.copy();
	U = Y.copy();
	
	m = L.row;
	n = U.col;
	bw = bw/2 + 1;
	
	for(k = 0; k < m; k++){
	    pp=L.value[k][k];
	    
	    if(Math.abs(pp)< small){
		return null;
	    }
	    w = 1/pp;
	    
	    for(j = k+1; j < k+bw && j < m ; j++){
		s = w*L.value[k][j];
		L.value[k][j] = s;
		for(i = k+1; i < k+bw+1 && i < m; i++){
		    s = L.value[i][j] - L.value[i][k]*L.value[k][j];
		    L.value[i][j] = s;
		}
	    }
	    for(j = 0; j < n; j++){
		s=w*U.value[k][j];
		U.value[k][j] = s;
		for(i = k+1; i < k+bw && i < m; i++){
		    s=U.value[i][j] - L.value[i][k] * U.value[k][j];
		    U.value[i][j] = s;
		}
	    }
	}
	
	ret = new Matrix(m, n);
	
	for(i = m; --i>=0;){
	    for(j = 0; j < n; j++){
		w=U.value[i][j];
		
		for(k = i+1; i < k+bw && k < m; k++){
		    w -= L.value[i][k] * ret.value[k][j];
		}
		ret.value[i][j] = w;
	    }	
	}
	
	return ret;
    }


/*
 * Special purpose calculations
 */

/**
 * Cross squared Euclidean distance Matrix: Z_{ij} = ||X_i - Y_j||^2 
 */
    public Matrix crossSqDistance(Matrix Y)
    {
	Matrix ret;
	int i, j, k;
	double s, d;
	int Y_row = Y.row, Y_col = Y.col;
	
	if(col != Y_col){
	    return null;
	}
	
	ret = new Matrix(row, Y_row);
	
	for(i=0; i<row; i++){
	    for(j=0; j<Y_row; j++){
		s = 0;
		for(k=0; k<col; k++){
		    d = value[i][k] - Y.value[j][k];
		    s += d*d;
		}
		ret.value[i][j] = s;
	    }
	}
	
	return ret;
    }
    
/**
 * Update by exponential : exp(X_{ij})
 */
    public void updateExp()
    {
	int i, j;
	
	for(i=0; i<row; i++){
	    for(j=0; j<col; j++){
		value[i][j] = Math.exp(value[i][j]);
	    }
	}
    }
    
/**
 * Make a horizontal-summation vector.
 */
    public Matrix horizontalSum()
    {
	int i, j;
	double s;
	
	Matrix ret = new Matrix(row, 1);
	
	for(i=0; i<row; i++){
	    s = 0;
	    for(j=0; j<col; j++){
		s += value[i][j];
	    }
	    ret.value[i][0] = s;
	}
	return ret;
    }

/**
 * Make a vertical-summation vector.
 */
    public Matrix verticalSum()
    {
	int i, j;
	double s;
	
	Matrix ret = new Matrix(1, col);
	
	for(j=0; j<col; j++){
	    s = 0;
	    for(i=0; i<row; i++){
		s += value[i][j];
	    }
	    ret.value[0][j] = s;
	}
	return ret;
    }

/**
 * Summation of all entries.
 */    
    public double sumEntries()
    {
        int i, j;
        double s = 0;
        
        for(i=0; i<row; i++){
            for(j=0; j<col; j++){
                s += value[i][j];
            }
        }
        return s;
    }
   
/**
 * Summation of squared entries.
 */ 
    public double sumSqrEntries()
    {
        int i, j;
        double s = 0;        
        
        for(i=0; i<row; i++){
            for(j=0; j<col; j++){
                s += value[i][j]*value[i][j];
            }
        }
        return s;
    }

/**
 * Multiply entries.
 */
   public Matrix mulipliedEntriesWith(Matrix Y)
   {
       Matrix ret;
       int i, j;

       ret = new Matrix(row, col);
       for(i=0; i<row; i++){
	   for(j=0; j<col; j++){
	       ret.value[i][j] = value[i][j]*Y.value[i][j];
	   }
       }
       return ret;
    }  
        
        
/** 
 * X_{ij} = X_{ij}/v_{j}.
 */
    public void updateDivideByRowVector(Matrix vec)
    {
	int i, j;
	
	for(j=0; j<col; j++){
	    for(i=0; i<row; i++){
		value[i][j] = value[i][j]/vec.value[0][j];
	    }
	}
    }

/**
 * If the matrix is a row or column vector, make a diagonal matrix from it.
 * Otherwise extract its diagonal as a row vector.
 */
    public Matrix diagonal()
    {
	Matrix ret;
	int i, n;

	if(row == 1){
	    ret = new Matrix(col, col);
	    for(i=0; i< col; i++){
		ret.value[i][i] = value[0][i];
	    }
	}
	else if(col == 1){
	    ret = new Matrix(row, row);
	    for(i=0; i< row; i++){
		ret.value[i][i] = value[i][0];
	    }
	}
	else{
	    n = Math.min(row, col);
	    ret = new Matrix(1, n);
	    for(i=0; i< n; i++){
		ret.value[0][i] = value[i][i];
	    }
	}
	return ret;
    }

/**
 * Make a row vector including the eigenvalues of the matrix.
 * @param eps allowable absolute value of off-diagonal entries
 * @lmax maximum number of repeat
 */
    public Matrix eigenvalues(double eps, int lmax)
    {
	int n;
	int i, j, im = 0, jm = 0, ll;
	double pmax, pii, pjj, pij, d, tn;
	double a, b, p1, p2, q1, q2, dd;
	Matrix px, qx, lmd;

	px = this.copy();
	n = px.row;
	qx = Matrix.identity(n);

	ll=0;

	do{
	    pmax = Math.abs(px.value[0][1]);
	    ll++;
	    for(i=0; i<n-1; i++){
		for(j=i+1; j<n; j++){
		    if(pmax<= Math.abs(px.value[i][j])){
			pmax= Math.abs(px.value[i][j]);
			im=i;
			jm=j;
		    
		    }
		}
	    }
	    if(pmax<=eps)	break;
	    pii=px.value[im][im];
	    pjj=px.value[jm][jm];
	    pij=px.value[im][jm];

	    d=pii-pjj;
	    dd=d*d+4.*pij*pij;
	    tn=2.*pij/(d+Math.sqrt(dd)*(d > 0 ? 1 : -1));
	    a=1./Math.sqrt(1.+tn*tn);
	    b=a*tn;

	    for(i=0; i<n; i++){
		p1=px.value[i][im];
		p2=px.value[i][jm];
		px.value[i][im] = p1*a+p2*b;
		px.value[i][jm] =  -p1*b+p2*a;

		q1=qx.value[i][im];
		q2=qx.value[i][jm];
		qx.value[i][im] = q1*a+q2*b;
		qx.value[i][jm] = -q1*b+q2*a;	    
	    }
	    for(i=0; i<n; i++){
		p1=px.value[im][i];
		p2=px.value[jm][i];
		px.value[im][i] = p1*a+p2*b;
		px.value[jm][i] = -p1*b+p2*a;	    
	    }
	}while(ll<lmax);

	lmd = px.diagonal();
	return lmd;
    }

}
