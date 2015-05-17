package jplot3d.draw;

import jplot3d.*;



public class DrawEmpty {


	/**
	 * Creates a surface plot
	 * @param surfaceCanvas TODO
	 */

	public final static void run(JSurface sf) {
		
		boolean printing=sf.printing;
		boolean plotfunc1=sf.plotfunc1;
		boolean plotfunc2=sf.plotfunc2;
		boolean isBoxed=sf.isBoxed;
		
		float zi, zx;
		int sx, sy;
		int start_lx, end_lx;
		int start_ly, end_ly;
		
		
		try {
			zi = sf.model.getZMin();
			zx = sf.model.getZMax();
			if (zi >= zx)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			return;
		}

		int plot_density = sf.model.getDispDivisions();
		int multiple_factor = sf.calc_divisions / plot_density;
		// model.setDispDivisions(plot_density);

		Thread.yield();
		sf.zmin = zi;
		sf.zmax = zx;
		sf.color_factor = 1f / (sf.zmax - sf.zmin);

		if (!printing) {
			sf.graphics.setColor(sf.getSurfaceColor().getBackgroundColor());
			sf.graphics.fillRect(0, 0, sf.getBounds().width, sf.getBounds().height);
		}

		sf.drawBoxGridsTicksLabels(sf.graphics, false);

		if (!plotfunc1 && !plotfunc2) {
			if (isBoxed)
				sf.drawBoundingBox();
			return;
		}

		
		
		sf.projector.setZRange(sf.zmin, sf.zmax);
		// direction test
		float distance = sf.projector.getDistance()
				* sf.projector.getCosElevationAngle();
		sf.cop = new SurfaceVertex(distance
				* sf.projector.getSinRotationAngle(), distance
				* sf.projector.getCosRotationAngle(), sf.projector.getDistance()
				* sf.projector.getSinElevationAngle());
		sf.cop.transform(sf.projector);
		sf.projector=sf.cop.getProjector();
		
		
		if (isBoxed)
			sf.drawBoundingBox();
	}
		
		
		
			
		
		
		
	
	
	
}
