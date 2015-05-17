package jplot3d.draw;

import java.awt.Color;
import java.awt.Point;

import jplot3d.JSurface;
import jplot3d.SurfaceModel.PlotColor;




public class DrawPlain2 {
	/**
	*  Olso set color and pen
	*  what =1,2,3,4 - vertical walls
	*  what =0 top
	 * @param sf TODO
	 * @param what TODO
	 * @param x1 TODO
	 * @param y1 TODO
	 * @param z1 TODO
	 * @param dx TODO
	 * @param dy TODO
	 * @param dz TODO
	 * @param lineColor TODO
	 * @param lineWidth TODO
	* **/
	
	
	         public static void run(int what, float x1, float y1, float z1, float dx, float dy, float dz, Color lineColor, int lineWidth, JSurface sf) {
	
	                Point projection=null;
	                int[] x = new int[5];
	                int[] y = new int[5];
	
	
	                if (what == 1) {
	               
	                projection = sf.getProjector().project(x1, y1, z1);
	                x[0] = projection.x;
	                y[0] = projection.y;
	                projection = sf.getProjector().project(x1, y1+dy, z1);
	                x[1] = projection.x;
	                y[1] = projection.y;
	                projection = sf.getProjector().project(x1, y1+dy, z1+dz);
	                x[2] = projection.x;
	                y[2] = projection.y;
	                projection = sf.getProjector().project(x1, y1, z1+dz);
	               
	                
	                } else if (what == 2) { 
	                projection = sf.getProjector().project(x1, y1, z1);
	                x[0] = projection.x;
	                y[0] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1, z1);
	                x[1] = projection.x;
	                y[1] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1, z1+dz);
	                x[2] = projection.x;
	                y[2] = projection.y;
	                projection = sf.getProjector().project(x1, y1, z1+dz);
	                
	                 } else if (what == 3) {
	
	                projection = sf.getProjector().project(x1+dx, y1, z1);
	                x[0] = projection.x;
	                y[0] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1+dy, z1);
	                x[1] = projection.x;
	                y[1] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1+dy, z1+dz);
	                x[2] = projection.x;
	                y[2] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1, z1+dz);
	                
	
	                } else if (what == 4) {
	
	                projection = sf.getProjector().project(x1, y1+dy, z1);
	                x[0] = projection.x;
	                y[0] = projection.y;
	                projection = sf.getProjector().project(x1, y1+dy, z1+dz);
	                x[1] = projection.x;
	                y[1] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1+dy, z1+dz);
	                x[2] = projection.x;
	                y[2] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1+dy, z1);
	                 // bottom 
	                 } else if (what == 5) {
	                projection = sf.getProjector().project(x1, y1, z1+dz);
	                x[0] = projection.x;
	                y[0] = projection.y;
	                projection = sf.getProjector().project(x1, y1+dy, z1+dz);
	                x[1] = projection.x;
	                y[1] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1+dy, z1+dz);
	                x[2] = projection.x;
	                y[2] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1, z1+dz);
	                } else if (what == 0) {
	                projection = sf.getProjector().project(x1, y1, z1);
	                x[0] = projection.x;
	                y[0] = projection.y;
	                projection = sf.getProjector().project(x1, y1+dy, z1);
	                x[1] = projection.x;
	                y[1] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1+dy, z1);
	                x[2] = projection.x;
	                y[2] = projection.y;
	                projection = sf.getProjector().project(x1+dx, y1, z1);
	                }
	
	                x[3] = projection.x;
	                y[3] = projection.y;
	                x[4] = x[0];
	                y[4] = y[0];
	
	
	
	              // this is just for solid fill
	             
	                
	                if (sf.model.getPlotColor() == PlotColor.SPECTRUM) {
	                  sf.graphics.setColor(lineColor);
	                  sf.graphics.fillPolygon(x, y, 5);
	
	              } else {
	
	             // this is the rest
	            sf.graphics.fillPolygon(x, y, 5);
	            sf.graphics.setColor(lineColor);
	            sf.graphics.drawPolygon(x, y, 5);
	
	            }
	            
	
	
	             }


}
