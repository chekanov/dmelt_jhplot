package jplot3d;


import java.text.NumberFormat;

// keep current data
   public class  NumUtil{

/*
     * Given a number, round up to the nearest power of ten
     * times 1, 2, or 5.
     *
     * Note: The argument must be strictly positive.
     */
    public static double roundUp(double val) {
        int exponent = (int) Math.floor(Math.log10(val));
        val *= Math.pow(10, -exponent);
        if (val > 5.0) val = 10.0;
        else if (val > 2.0) val = 5.0;
        else if (val > 1.0) val = 2.0;
        val *= Math.pow(10, exponent);
        return val;
    }

    /*
     * Return the number of fractional digits required to display the
     * given number.  No number larger than 15 is returned (if
     * more than 15 digits are required, 15 is returned).
     */
    public static int numFracDigits(double num) {
        int numdigits = 0;
        while (numdigits <= 15 && num != Math.floor(num)) {
            num *= 10.0;
            numdigits += 1;
        }
        return numdigits;
    }

    // Number format cache used by formatNum.
    // Note: i'd have put the body of the formatNum method below into
    // a synchronized block for complete thread safety but that causes
    // an abscure null pointer exception in the awt event thread.
    // go figure.
    public  static NumberFormat numberFormat = null;

    /*
     * Return a string for displaying the specified number
     * using the specified number of digits after the decimal point.
     * NOTE: java.text.NumberFormat is only present in JDK1.1
     * We use this method as a wrapper so that we can cache information.
     */
    public  static String formatNum(double num, int numfracdigits) {
        if (numberFormat == null) {
            // Cache the number format so that we don't have to get
            // info about local language etc. from the OS each time.
            numberFormat = NumberFormat.getInstance();
            // force to not include commas because we want the strings
            // to be parsable back into numeric values. - DRG
            numberFormat.setGroupingUsed(false);
        }
        numberFormat.setMinimumFractionDigits(numfracdigits);
        numberFormat.setMaximumFractionDigits(numfracdigits);
        return numberFormat.format(num);
    }
    


/**
   * Format string by removin ".0" 
   * @return formatted string 
   */
  public static  String RemoveZero(String  s)
  {

     String aString=s; 
     if (aString.indexOf(".")>-1 && aString.endsWith("0") ) {
            aString=aString.substring(0,aString.length()-1);
           if (aString.endsWith(".") ) aString=aString.substring(0,aString.length()-1);
      }

     return aString;



   }

  /**
   * Determin: should 0 be remove?
   * @return formatted string 
   */
  public static  boolean NeedRemove(String[] a)
  {

  
     int kk=0;
     for  (int i =0; i <a.length; i++) {
    	 if (a[i].indexOf(".")>-1 && a[i].endsWith("0") ) kk++;
     }
     if (kk == a.length) return true;
     return false;
     
  }
    
  
  public static  String[] ZeroRemover(String[] a) {
     
	  
	 if ( NeedRemove(a)) {
    	 for  (int i =0; i <a.length; i++) {
    		 String aString=a[i];
    		 aString=aString.substring(0,aString.length()-1);
             if (aString.endsWith(".") ) aString=aString.substring(0,aString.length()-1);
             a[i]=aString;
    	 }
     } else return a;
    	 
    
	 // second pass
	 if ( NeedRemove(a)) {
    	 for  (int i =0; i <a.length; i++) {
    		 String aString=a[i];
    		 aString=aString.substring(0,aString.length()-1);
             if (aString.endsWith(".") ) aString=aString.substring(0,aString.length()-1);
             a[i]=aString;
    	 }
     } else return a;
     
	 

	 // 3rd  pass
	 if ( NeedRemove(a)) {
    	 for  (int i =0; i <a.length; i++) {
    		 String aString=a[i];
    		 aString=aString.substring(0,aString.length()-1);
             if (aString.endsWith(".") ) aString=aString.substring(0,aString.length()-1);
             a[i]=aString;
    	 }
     } else return a; 
     
    
     return a;



   }

  
  
  
  
  
  
  
  


   }

