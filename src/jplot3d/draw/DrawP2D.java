package jplot3d.draw;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;


import jhplot.JHPlot;
import jplot3d.JPoint;
import jplot3d.JSurface;

public class DrawP2D {
	/**
     * Creates scatter plot for P2D. At this moment, it's ony functional
     * for the same number of bins in X and Y need to fix in the future
     * (S.Chekanov)
* @param surfaceCanvas TODO
     */

    public final static void run(JSurface sf) {
            if (sf == null) return;
            
            JPoint[] jpoint=sf.model.getJPoints();
            if (jpoint == null) return;
            boolean printing=sf.printing;
            
            // System.out.println("PLOT P2D");
            float   xi,xx,yi,yx;
            float zi, zx;
            
            float xfactor=sf.model.getPlotter().getFactorX();
    		float yfactor=sf.model.getPlotter().getFactorY();
    		float zfactor=sf.model.getPlotter().getFactorZ();
    		
        	 Rectangle comp = sf.getBounds();
    		 int h = (int) comp.getHeight();
    		 int w = (int) comp.getWidth();

          
            


           	
           	 xi=sf.model.getXMin();
           	 xx=sf.model.getXMax();
           	 yi=sf.model.getYMin();
           	 yx=sf.model.getYMax();
            	
         	
     		try {
     			zi = sf.model.getZMin();
     			zx = sf.model.getZMax();
     			if (zi >= zx)
     				throw new NumberFormatException();
     		} catch (NumberFormatException e) {
     			return;
     		}
     		
     	
     		

     	    Thread.yield();
     	    sf.zmin = zi;
     		sf.zmax = zx;
     		sf.projector.setZRange(sf.zmin, sf.zmax);
               
         
         
            
            zfactor=  20/(zx-zi);
            xfactor = 20/(xx-xi);
            yfactor = 20/(yx-yi);
          
            if (!printing) {
    			sf.graphics.setColor(sf.getSurfaceColor().getBackgroundColor());
    			sf.graphics.fillRect(0, 0, w, h);
    		}

    		sf.drawBoxGridsTicksLabels(sf.graphics,false);
    		
    		if (sf.projector == null) return;
           

         


              


//- drawing part
            Point projection;
            int[] x = new int[5];
            int[] y = new int[5];
            if (sf.getProjector() == null) return; 
            
// check for rotation angle
            // float new_value = projector.getRotationAngle();
            // System.out.println(new_value);
            // drawing part 


// direction test

            float distance = sf.getProjector().getDistance()
                            * sf.getProjector().getCosElevationAngle();

            // cop : center of projection
            /*
            cop = new SurfaceVertex(distance * projector.getSinRotationAngle(),
                            distance * projector.getCosRotationAngle(), projector
                                            .getDistance()
                                            * projector.getSinElevationAngle());
            cop.transform();
            */

            // location of the observer
            float oX = (float) (distance * sf.getProjector().getSinRotationAngle());
            float oY = (float) (distance * sf.getProjector().getCosRotationAngle());
            float oZ = (float) (sf.getProjector().getDistance() * sf.getProjector().getSinElevationAngle());

      //      System.out.println("debug distance");
      //      System.out.println(  center[0] );
      //      System.out.println(  center[1] ); 
      //      System.out.println(  center[2] ); 



     //  System.out.println("start size: "+jpoint_size);
     //    System.out.println("start size: "+jpoint.length);


     // first get the distance to the observer 
     for (int ii=0; ii<jpoint.length; ii++) {
     if (jpoint[ii] == null) continue;
     // System.out.println(sf.jpoint[ii]);
    
     
     JPoint jpp= jpoint[ii]; 
     if  (jpp == null) continue;

     float x1=((float)jpp.getX()-xi) *xfactor-10;
     float y1=((float)jpp.getY()-yi) *yfactor-10;
     float z1=((float)jpp.getZ()-zi) *zfactor-10;

    
     
     
     // float z1=-10+(float)jpp.getZ()*sf.zfactor;
     // set the distance to a viwer
     float dxx=(oX-x1)*(oX-x1); 
     float dyy=(oY-y1)*(oY-y1); 
     float dzz=(oZ-z1)*(oZ-z1); 
     jpoint[ii].setD(  dxx+dyy+dzz ); 
     }
  
     /* Sort array */
     Arrays.sort(jpoint);

     // System.out.println("after sort");


    // plot last the most closest to observer 
    for (int ii=jpoint.length-1; ii>=0; ii--) {
     if (jpoint[ii] == null) continue;
     JPoint jpp= jpoint[ii];
     if  (jpp == null) continue;
     
     float x1=((float)jpp.getX()-xi) *xfactor-10;
     float y1=((float)jpp.getY()-yi) *yfactor-10;
     float z1=((float)jpp.getZ()-zi) *zfactor-10;
     
     

     // System.out.println("Debug==");
     // System.out.println(sf.zfactor);

     projection = sf.getProjector().project(x1, y1, z1);
     x[0] = projection.x;
     y[0] = projection.y;
     // System.out.println(jpp.getA());
     
     
            sf.graphics.setColor(jpp.getSymbolColor());
            int sis=(int)(0.15*jpp.getSymbolSize()*sf.projector.get2DScaling());
            sf.graphics.fillOval(x[0]-(int)(0.5*sis), y[0]-(int)(0.5*sis), sis,sis);

     }


    if (sf.isBoxed)
		sf.drawBoundingBox();

            }
}

