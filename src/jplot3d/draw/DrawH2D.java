package jplot3d.draw;

import java.awt.Point;
import java.awt.Rectangle;

import jhplot.JHPlot;
import jplot3d.*;
import jplot3d.SurfaceModel.PlotColor;

/**
 * Show H2D histogram
 * 
 * @author chekanov
 * 
 */
public class DrawH2D {

	/**
	 * Creates a bar plot for histogam. At this moment, it's only functional for
	 * the same number of bins in X and Y need to fix in the future (S.Chekanov)
	 * 
	 * @param surfaceCanvas
	 *            TODO
	 */

	public final static void run(JSurface sf) {
		boolean printing=sf.printing;
		boolean plotfunc1=sf.plotfunc1;
		boolean plotfunc2=sf.plotfunc2;
		float zi, zx;
		int sx, sy;

		float[][] pointsX=sf.model.getPlotter().getPointsX();
		float[][] pointsY=sf.model.getPlotter().getPointsY();
		float[][] pointsZ=sf.model.getPlotter().getPointsZ();
		float stepx=sf.model.getPlotter().getStepX();
		float stepy=sf.model.getPlotter().getStepY();
		float xfactor=sf.model.getPlotter().getFactorX();
		float yfactor=sf.model.getPlotter().getFactorY();
		float zfactor=sf.model.getPlotter().getFactorZ();
		
		Rectangle comp = sf.getBounds();
		int h = (int) comp.getHeight();
		int w = (int) comp.getWidth();

		// JHPlot.showMessage("call to DrawH2D");
		
		
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
		
	    if (pointsX == null || pointsY == null || pointsZ==null ){
	    	System.out.println("DEBUG: No data available");
	    	return;
	    }
	       

		sf.color_factor = 0.8f / (sf.zmax - sf.zmin);
		if (sf.model.getPlotColor() == PlotColor.DUALSHADE)
			sf.color_factor *= 0.6f / 0.8f;

		if (!printing) {
			sf.graphics.setColor(sf.getSurfaceColor().getBackgroundColor());
			sf.graphics.fillRect(0, 0, w, h);
		}

		sf.drawBoxGridsTicksLabels(sf.graphics,false);
		
		if (sf.projector == null) return;

		// check for rotation angle
		float new_value = sf.projector.getRotationAngle();
		
		//System.out.println("Debug: call DrawH2D");

		float dx = stepx * xfactor;
		float dy = stepy * yfactor;
		// System.out.println("Debug zmin="+sf.zmin+ " zmax="+sf.zmax);

		for (int i = 0; i < sf.calc_divisions; i++) {
			for (int j = 0; j < sf.calc_divisions; j++) {

			//	 System.out.println("Debug i="+i+ " j="+j+ " z="+);
			

				// invert loop for different angle
				// important for redrawing
				int ii = i;
				int jj = j;

				if (new_value >= 0 && new_value < 90) {
					ii = i;
					jj = j;
				}

				if (new_value >= 90 && new_value < 180) {
					ii = i;
					jj = sf.calc_divisions - j - 1;
				}

				if (new_value >= 180 && new_value < 270) {
					ii = sf.calc_divisions - i - 1;
					jj = sf.calc_divisions - j - 1;
				}

				if (new_value >= 270 && new_value <= 360) {
					ii = sf.calc_divisions - i - 1;
					jj = j;
				}

				float x1 = pointsX[ii][jj];
				float y1 = pointsY[ii][jj];
				float z1 =  pointsZ[ii][jj]*zfactor -10;
				float dz = -pointsZ[ii][jj]*zfactor;	
				
				// System.out.println("Debug x1="+x1+ " y1="+y1+ " z="+pointsZ[ii][jj]);
				
				// do not draw surface which is not necessary
				if (new_value >= 0 && new_value < 90) {
					// drawPlain(1, x1, y1, z1, dx, dy ,dz);
					// drawPlain(2, x1, y1, z1, dx, dy ,dz);
					DrawPlain.run(3, x1, y1, z1, dx, dy, dz,sf);
					DrawPlain.run(4, x1, y1, z1, dx, dy, dz,sf);
					DrawPlain.run(0, x1, y1, z1, dx, dy, dz,sf);

				} else if (new_value >= 90 && new_value < 180) {
					// drawPlain(1, x1, y1, z1, dx, dy ,dz);
					DrawPlain.run(2, x1, y1, z1, dx, dy, dz, sf);
					DrawPlain.run(3, x1, y1, z1, dx, dy, dz, sf);
					// drawPlain(4, x1, y1, z1, dx, dy ,dz);
					DrawPlain.run(0, x1, y1, z1, dx, dy, dz, sf);
				} else if (new_value >= 180 && new_value < 270) {
					DrawPlain.run(1, x1, y1, z1, dx, dy, dz, sf);
					DrawPlain.run(2, x1, y1, z1, dx, dy, dz, sf);
					// drawPlain(3, x1, y1, z1, dx, dy ,dz);
					// drawPlain(4, x1, y1, z1, dx, dy ,dz);
					DrawPlain.run(0, x1, y1, z1, dx, dy, dz, sf);
				} else if (new_value >= 270 && new_value < 360) {
					DrawPlain.run(1, x1, y1, z1, dx, dy, dz, sf);
					// drawPlain(2, x1, y1, z1, dx, dy ,dz);
					// drawPlain(3, x1, y1, z1, dx, dy ,dz);
					DrawPlain.run(4, x1, y1, z1, dx, dy, dz, sf);
					DrawPlain.run(0, x1, y1, z1, dx, dy, dz, sf);
				}

			}
		}
		
		
		if (sf.isBoxed)
			sf.drawBoundingBox();
		

	
	}

	


}
