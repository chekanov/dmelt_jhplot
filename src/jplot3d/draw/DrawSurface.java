package jplot3d.draw;

import jplot3d.*;



public class DrawSurface {


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
		
		//System.out.println("Before SinRotationAngle()="+sf.projector.getSinRotationAngle());

		// cop : center of projection
		//OMG there is a new sfVertex every time !
		sf.cop = new SurfaceVertex(distance
				* sf.projector.getSinRotationAngle(), distance
				* sf.projector.getCosRotationAngle(), sf.projector.getDistance()
				* sf.projector.getSinElevationAngle());
		sf.cop.transform(sf.projector);
		
		sf.projector=sf.cop.getProjector();
		//System.out.println("  -- after SinRotationAngle()="+sf.projector.getSinRotationAngle());
		
		boolean inc_x = sf.cop.x > 0;
		boolean inc_y = sf.cop.y > 0;

		sf.critical = false;

		if (inc_x) {
			start_lx = 0;
			end_lx = sf.calc_divisions;
			sx = multiple_factor;
		} else {
			start_lx = sf.calc_divisions;
			end_lx = 0;
			sx = -multiple_factor;
		}
		if (inc_y) {
			start_ly = 0;
			end_ly = sf.calc_divisions;
			sy = multiple_factor;
		} else {
			start_ly = sf.calc_divisions;
			end_ly = 0;
			sy = -multiple_factor;
		}

		
		if ((sf.cop.x > 10) || (sf.cop.x < -10)) {
			if ((sf.cop.y > 10) || (sf.cop.y < -10)) {
				sf.plotArea(start_lx, start_ly, end_lx, end_ly, sx, sy);
			} else { // split in y direction
				int split_y = (int) ((sf.cop.y + 10) * plot_density / 20)
						* multiple_factor;
				sf.plotArea(start_lx, 0, end_lx, split_y, sx, multiple_factor);
				sf.plotArea(start_lx, sf.calc_divisions, end_lx, split_y, sx,
						-multiple_factor);
			}
		} else {
			if ((sf.cop.y > 10) || (sf.cop.y < -10)) { // split in x direction
				int split_x = (int) ((sf.cop.x + 10) * plot_density / 20)
						* multiple_factor;
				sf.plotArea(0, start_ly, split_x, end_ly, multiple_factor, sy);
				sf.plotArea(sf.calc_divisions, start_ly, split_x, end_ly,
						-multiple_factor, sy);
			} else { // split in both x and y directions
				int split_x = (int) ((sf.cop.x + 10) * plot_density / 20)
						* multiple_factor;
				int split_y = (int) ((sf.cop.y + 10) * plot_density / 20)
						* multiple_factor;
				sf.critical = true;
				sf.plotArea(0, 0, split_x, split_y, multiple_factor,
						multiple_factor);
				sf.plotArea(0, sf.calc_divisions, split_x, split_y, multiple_factor,
						-multiple_factor);
				sf.plotArea(sf.calc_divisions, 0, split_x, split_y, -multiple_factor,
						multiple_factor);
				sf.plotArea(sf.calc_divisions, sf.calc_divisions, split_x, split_y,
						-multiple_factor, -multiple_factor);
			}
		}
        
		
		if (isBoxed)
			sf.drawBoundingBox();
	}
		
		
		
			
		
		
		
	
	
	
}
