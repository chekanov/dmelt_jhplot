/*
 * The initial conditions are set at random.
 * So every new start creates a different story. 
 * All parameters can be changed.
 * One parameter can be changed step by step during the run (scan).
 *
 */
package sovectors;



/**
 * @author Wolfhard Hoevel
 */
public class EightPoints01 {
    
// Parameters-------------------------------------------------------------------    

    static int dim = 3;           // Dimension of Euclidean space.

    public static int s01 = 1;    // "Spin" of particle01 s01 <= e01.
    public static int e01 = 64;   // "Diameter" of particle01.
    
    public static int s23 = 1;    // "Spin" of particle01 s23 <= e23.
    public static int e23 = 8;   // "Diameter" of particle23.
   
    public static int s45 = 1;    // "Spin" of particle01 s45 <= e45.
    public static int e45 = 8;    // "Diameter" of particle45.
    
    public static int s67 = 1;    // "Spin" of particle01 s67 <= e67.
    public static int e67 = 8;    // "Diameter" of particle67.
        
    public static int q0 = 2;    //"Charge" of point r0.
    public static int q1 = 2;    //"Charge" of point r1.
    
    public static int q2 = -1;     //"Charge" of point r2.
    public static int q3 = -1;     //"Charge" of point r3.
    
    public static int q4 = -1;      //"Charge" of point r4.
    public static int q5 = -1;      //"Charge" of point r5.
    
    public static int q6 = -1;      //"Charge" of point r6.
    public static int q7 = -1;      //"Charge" of point r7.
          
    public static double pull = 0.0; //Radial displacements by the "charges".
                                        
    public static double zoom = 0.02;
    
    public static int ani = 1;  // Animation modus ani = 0 or 1.
    
    public static int scan = 1;  // Scan on or off: parameter scan = 1 or 0
    public static double scanSpeed = 0.00002;
    
    public static double tshowplot = 0.5e4; // Number of pixels for one picture.   
    public static double tParameter = 0.0;  // Change of parameters 
    public static double t = 0.0;           // Time
    public static double tStop = 1.0e12;     // End of Experiment
    public static double a = 10.0;           // Initial condition factor
    //----------------------------------------------------------------------------
    
    public static double tshow = 0;

    public static double[] r0;   // Points.
    public static double[] r1;
    public static double[] r2;
    public static double[] r3;
    public static double[] r4;
    public static double[] r5;
    public static double[] r6;
    public static double[] r7;
    
    public static double[] r01;   // Distance vectors.
    public static double[] r02;   
    public static double[] r12;
    public static double[] r03;
    public static double[] r13;
    public static double[] r23;
    public static double[] r04;   
    public static double[] r14;
    public static double[] r24;
    public static double[] r34;
    public static double[] r05;
    public static double[] r15;   
    public static double[] r25;
    public static double[] r35;
    public static double[] r45;
    public static double[] r06;
    public static double[] r16;
    public static double[] r26;   
    public static double[] r36;
    public static double[] r46;
    public static double[] r56;
    public static double[] r07;
    public static double[] r17;
    public static double[] r27;
    public static double[] r37;   
    public static double[] r47;
    public static double[] r57;
    public static double[] r67;
    
    public static double[] dr01;   // Displacement vectors inside the bivectors
    public static double[] dr23;
    public static double[] dr45;
    public static double[] dr67;
    
    public static double[] drC01;   // Displacement vectors by "charges"
    public static double[] drC02;
    public static double[] drC12;
    public static double[] drC03;
    public static double[] drC13;
    public static double[] drC23;
    public static double[] drC04;
    public static double[] drC14;
    public static double[] drC24;
    public static double[] drC34;
    public static double[] drC05;
    public static double[] drC15;
    public static double[] drC25;
    public static double[] drC35;
    public static double[] drC45;
    public static double[] drC06;
    public static double[] drC16;
    public static double[] drC26;
    public static double[] drC36;
    public static double[] drC46;
    public static double[] drC56;
    public static double[] drC07;
    public static double[] drC17;
    public static double[] drC27;
    public static double[] drC37;
    public static double[] drC47;
    public static double[] drC57;
    public static double[] drC67;
  
     public static void main(String[] args) {

           show();
     } 
  


    /** Main class to show the image 
    **/
    public static void show() {
       
       // Colors and canvas size (StdDraw)
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw. setCanvasSize();
        StdDraw.setXscale(-1.0, 1.0);
        StdDraw.setYscale(-1.0, 1.0);
        StdDraw.clear(StdDraw.BLACK);
        
        // Initial conditions
        r0 = Vec.randomVector(a);
        r1 = Vec.randomVector(a);
        r2 = Vec.randomVector(a);
        r3 = Vec.randomVector(a);
        r4 = Vec.randomVector(a);
        r5 = Vec.randomVector(a);
        r6 = Vec.randomVector(a);
        r7 = Vec.randomVector(a);
        dr01 = Vec.randomVector(a);
        dr23 = Vec.randomVector(a);
        dr45 = Vec.randomVector(a);
        dr67 = Vec.randomVector(a);
        
        while( t < tStop ){
        
        //Calculation for one step t --> t+1.
        
        // Distance vectors
        r01 = Vec.subtract(r1, r0);
        r02 = Vec.subtract(r2, r0);
        r12 = Vec.subtract(r2, r1);
        r03 = Vec.subtract(r3, r0);
        r13 = Vec.subtract(r3, r1);
        r23 = Vec.subtract(r3, r2);
        r04 = Vec.subtract(r4, r0);
        r14 = Vec.subtract(r4, r1);
        r24 = Vec.subtract(r4, r2);
        r34 = Vec.subtract(r4, r3);
        r05 = Vec.subtract(r5, r0);
        r15 = Vec.subtract(r5, r1);
        r25 = Vec.subtract(r5, r2);
        r35 = Vec.subtract(r5, r3);
        r45 = Vec.subtract(r5, r4);
        r06 = Vec.subtract(r6, r0);
        r16 = Vec.subtract(r6, r1);
        r26 = Vec.subtract(r6, r2);
        r36 = Vec.subtract(r6, r3);
        r46 = Vec.subtract(r6, r4);
        r56 = Vec.subtract(r6, r5);
        r07 = Vec.subtract(r7, r0);
        r17 = Vec.subtract(r7, r1);
        r27 = Vec.subtract(r7, r2);
        r37 = Vec.subtract(r7, r3);
        r47 = Vec.subtract(r7, r4);
        r57 = Vec.subtract(r7, r5);
        r67 = Vec.subtract(r7, r6);
        
        //Displacment vectors by "charges"
        if( q0*q1 != 0 ) drC01 = Vec.drCharge(r01, pull, q0, q1);
        if( q0*q2 != 0 ) drC02 = Vec.drCharge(r02, pull, q0, q2);
        if( q1*q2 != 0 ) drC12 = Vec.drCharge(r12, pull, q1, q2);
        if( q0*q3 != 0 ) drC03 = Vec.drCharge(r03, pull, q0, q3);
        if( q1*q3 != 0 ) drC13 = Vec.drCharge(r13, pull, q1, q3);
        if( q2*q3 != 0 ) drC23 = Vec.drCharge(r23, pull, q2, q3);
        if( q0*q4 != 0 ) drC04 = Vec.drCharge(r04, pull, q0, q4);
        if( q1*q4 != 0 ) drC14 = Vec.drCharge(r14, pull, q1, q4);
        if( q2*q4 != 0 ) drC24 = Vec.drCharge(r24, pull, q2, q4);
        if( q3*q4 != 0 ) drC34 = Vec.drCharge(r34, pull, q3, q4);
        if( q0*q5 != 0 ) drC05 = Vec.drCharge(r05, pull, q0, q5);
        if( q1*q5 != 0 ) drC15 = Vec.drCharge(r15, pull, q1, q5);
        if( q2*q5 != 0 ) drC25 = Vec.drCharge(r25, pull, q2, q5);
        if( q3*q5 != 0 ) drC35 = Vec.drCharge(r35, pull, q3, q5);
        if( q4*q5 != 0 ) drC45 = Vec.drCharge(r45, pull, q4, q5);
        if( q0*q6 != 0 ) drC06 = Vec.drCharge(r06, pull, q0, q6);
        if( q1*q6 != 0 ) drC16 = Vec.drCharge(r16, pull, q1, q6);
        if( q2*q6 != 0 ) drC26 = Vec.drCharge(r26, pull, q2, q6);
        if( q3*q6 != 0 ) drC36 = Vec.drCharge(r36, pull, q3, q6);
        if( q4*q6 != 0 ) drC46 = Vec.drCharge(r46, pull, q4, q6);
        if( q5*q6 != 0 ) drC56 = Vec.drCharge(r56, pull, q5, q6);
        if( q0*q7 != 0 ) drC07 = Vec.drCharge(r07, pull, q0, q7);
        if( q1*q7 != 0 ) drC17 = Vec.drCharge(r17, pull, q1, q7);
        if( q2*q7 != 0 ) drC27 = Vec.drCharge(r27, pull, q2, q7);
        if( q3*q7 != 0 ) drC37 = Vec.drCharge(r37, pull, q3, q7);
        if( q4*q7 != 0 ) drC47 = Vec.drCharge(r47, pull, q4, q7);
        if( q5*q7 != 0 ) drC57 = Vec.drCharge(r57, pull, q5, q7);    
        if( q6*q7 != 0 ) drC67 = Vec.drCharge(r67, pull, q6, q7);
        
        //Reflection inside the bivectors
        if (Vec.magnitude(r01) > e01 ) dr01 = Vec.reflect(r01, dr01, s01);
        if (Vec.magnitude(r23) > e23 ) dr23 = Vec.reflect(r23, dr23, s23);
        if (Vec.magnitude(r45) > e45 ) dr45 = Vec.reflect(r45, dr45, s45);
        if (Vec.magnitude(r67) > e67 ) dr67 = Vec.reflect(r67, dr67, s67);
        
        //Displacement by Vec.reflect().
        r0 = Vec.add(r0, dr01);
        r1 = Vec.subtract(r1, dr01);
        r2 = Vec.add(r2, dr23);
        r3 = Vec.subtract(r3, dr23);
        r4 = Vec.add(r4, dr45);
        r5 = Vec.subtract(r5, dr45);
        r6 = Vec.add(r6, dr67);
        r7 = Vec.subtract(r7, dr67);
       
        //Superposition of the displacement influenced by the "charges".
        if( q0*q1 != 0 ) {
            r0 = Vec.add(r0, drC01);
            r1 = Vec.subtract(r1, drC01);
        }
        if( q0*q2 != 0 ) {
            r0 = Vec.add(r0, drC02);
            r2 = Vec.subtract(r2, drC02);
        }       
        if( q1*q2 != 0 ) {
            r1 = Vec.add(r1, drC12);
            r2 = Vec.subtract(r2, drC12);
        }
        if( q0*q3 != 0 ) {
            r0 = Vec.add(r0, drC03);
            r3 = Vec.subtract(r3, drC03);
        }       
        if( q1*q3 != 0 ) {
            r1 = Vec.add(r1, drC13);
            r3 = Vec.subtract(r3, drC13);
        }       
        if( q2*q3 != 0 ) {
            r2 = Vec.add(r2, drC23);
            r3 = Vec.subtract(r3, drC23);
        }
        if( q0*q4 != 0 ) {
            r0 = Vec.add(r0, drC04);
            r4 = Vec.subtract(r4, drC04);
        }       
        if( q1*q4 != 0 ) {
            r1 = Vec.add(r1, drC14);
            r4 = Vec.subtract(r4, drC14);
        }       
        if( q2*q4 != 0 ) {
            r2 = Vec.add(r2, drC24);
            r4 = Vec.subtract(r4, drC24);
        }       
        if( q3*q4 != 0 ) {
            r3 = Vec.add(r3, drC34);
            r4 = Vec.subtract(r4, drC34);
        }
        if( q0*q5 != 0 ) {
            r0 = Vec.add(r0, drC05);
            r5 = Vec.subtract(r5, drC05);
        }       
        if( q1*q5 != 0 ) {
            r1 = Vec.add(r1, drC15);
            r5 = Vec.subtract(r5, drC15);
        }       
        if( q2*q5 != 0 ) {
            r2 = Vec.add(r2, drC25);
            r5 = Vec.subtract(r5, drC25);
        }       
        if( q3*q5 != 0 ) {
            r3 = Vec.add(r3, drC35);
            r5 = Vec.subtract(r5, drC35);
        }       
        if( q4*q5 != 0 ) {
            r4 = Vec.add(r4, drC45);
            r5 = Vec.subtract(r5, drC45);
        }
        if( q0*q6 != 0 ) {
            r0 = Vec.add(r0, drC06);
            r6 = Vec.subtract(r6, drC06);
        }       
        if( q1*q6 != 0 ) {
            r1 = Vec.add(r1, drC16);
            r6 = Vec.subtract(r6, drC16);
        }       
        if( q2*q6 != 0 ) {
            r2 = Vec.add(r2, drC26);
            r6 = Vec.subtract(r6, drC26);
        }       
        if( q3*q6 != 0 ) {
            r3 = Vec.add(r3, drC36);
            r6 = Vec.subtract(r6, drC36);
        }       
        if( q4*q6 != 0 ) {
            r4 = Vec.add(r4, drC46);
            r6 = Vec.subtract(r6, drC46);
        }       
        if( q5*q6 != 0 ) {
            r5 = Vec.add(r5, drC56);
            r6 = Vec.subtract(r6, drC56);
        }       
        if( q0*q7 != 0 ) {
            r0 = Vec.add(r0, drC07);
            r7 = Vec.subtract(r7, drC07);
        }       
        if( q1*q7 != 0 ) {
            r1 = Vec.add(r1, drC17);
            r7 = Vec.subtract(r7, drC17);
        }       
        if( q2*q7 != 0 ) {
            r2 = Vec.add(r2, drC27);
            r7 = Vec.subtract(r7, drC27);
        }       
        if( q3*q7 != 0 ) {
            r3 = Vec.add(r3, drC37);
            r7 = Vec.subtract(r7, drC37);
        }       
        if( q4*q7 != 0 ) {
            r4 = Vec.add(r4, drC47);
            r7 = Vec.subtract(r7, drC47);
        }       
        if( q5*q7 != 0 ) {
            r5 = Vec.add(r5, drC57);
            r7 = Vec.subtract(r7, drC57);
        }       
        if( q6*q7 != 0 ) {
            r6 = Vec.add(r6, drC67);
            r7 = Vec.subtract(r7, drC67);
        }
        
        //Draw pixel with StdDraw.
         StdDraw.setPenColor(StdDraw.RED);
         StdDraw.pixel(zoom*r0[0], zoom*r0[1]);
         StdDraw.setPenColor(StdDraw.GREEN);
         StdDraw.pixel(zoom*r1[0], zoom*r1[1]);
         StdDraw.setPenColor(StdDraw.CYAN);
         StdDraw.pixel(zoom*r2[0], zoom*r2[1]);
         StdDraw.setPenColor(StdDraw.WHITE);
         StdDraw.pixel(zoom*r3[0], zoom*r3[1]);
         StdDraw.setPenColor(StdDraw.YELLOW);
         StdDraw.pixel(zoom*r4[0], zoom*r4[1]);
         StdDraw.setPenColor(StdDraw.MAGENTA);
         StdDraw.pixel(zoom*r5[0], zoom*r5[1]);
         StdDraw.setPenColor(StdDraw.ORANGE);
         StdDraw.pixel(zoom*r6[0], zoom*r6[1]);
         StdDraw.setPenColor(StdDraw.PINK);
         StdDraw.pixel(zoom*r7[0], zoom*r7[1]);

        
        tshow = tshow + 1.0;
        
        //Scan of one parameter, for example pull.
        if( scan == 1){
            tParameter = tParameter + 1.0;
            if( 0.2e6 < tParameter){
               pull = pull + scanSpeed;            
               System.out.printf("%12.7f\n",pull);
               tParameter = 0.0;
            }
        }
        
        //Draw all calculated points for one picture of the animation.
        if(tshow > tshowplot){
          StdDraw.show(0);  
          tshow = 0.0;
          if (ani == 1) StdDraw.clear(StdDraw.BLACK);
          }
        
        t = t + 1.0;
        
        }  //End of loop.
    
    }
    
    
}
