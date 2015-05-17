package jplot3d.draw;


import jplot3d.*;



public class DrawDensity {


	/**
	 * Creates a density plot
	 * @param surfaceCanvas TODO
	 */

	public final static void run(JSurface sf) {
		
		boolean printing=sf.printing;
		boolean plotfunc1=sf.plotfunc1;
		boolean plotfunc2=sf.plotfunc2;
        boolean isMesh=sf.isMesh;

               float zi, zx, z;

		try {
			zi = sf.model.getZMin();
			zx = sf.model.getZMax();
			if (zi >= zx)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			System.out.println("Error in ranges");
			return;
		}

		sf.zmin = zi;
		sf.zmax = zx;

		sf.curve = plotfunc1 ? 1 : plotfunc2 ? 2 : 1;
		// computePlotArea depends on curve .. so, it must be computed
		sf.computePlotArea();
		int plot_density = sf.model.getDispDivisions();
		int multiple_factor = sf.calc_divisions / plot_density;
		// model.setDispDivisions(plot_density);

		// Thread.yield();
		// model.setMessage("regenerating ...");

		sf.color_factor = 1f / (zx - zi); // convert every z in a [0;1] float
		// 0.8 is for red to blue : 0.8 leads to reddish blue and red

		if (!printing) {
			sf.graphics.setColor(sf.getSurfaceColor().getBackgroundColor());
			sf.graphics.fillRect(0, 0, sf.getBounds().width, sf.getBounds().height);
		}

		if (plotfunc1 || plotfunc2) {
			int index = 0;
			int func = 0;
			if (!plotfunc1)
				func = 1; // function 1 has higher priority
			sf.curve = func + 1;
			int delta = (sf.calc_divisions + 1) * multiple_factor;
			for (int i = 0; i < sf.calc_divisions; i += multiple_factor) {
				index = i * (sf.calc_divisions + 1);
				for (int j = 0; j < sf.calc_divisions; j += multiple_factor) {
					sf.contour_vertex[0] = sf.vertex[func][index];
					sf.contour_vertex[1] = sf.vertex[func][index + multiple_factor];
					sf.contour_vertex[2] = sf.vertex[func][index + delta
							+ multiple_factor];
					sf.contour_vertex[3] = sf.vertex[func][index + delta];

					int x = sf.contourConvertX(sf.contour_vertex[1].x);
					int y = sf.contourConvertY(sf.contour_vertex[1].y);
					int w = sf.contourConvertX(sf.contour_vertex[3].x) - x;
					int h = sf.contourConvertY(sf.contour_vertex[3].y) - y;

					z = 0.0f;
					boolean error = false;
					for (int loop = 0; loop < 4; loop++) {
						if (Float.isNaN(sf.contour_vertex[loop].z)) {
							error = true;
							break;
						}
						z += sf.contour_vertex[loop].z;
					}
					if (error) {
						index += multiple_factor;
						continue;
					}

					z /= 4;
					z = (z - zi) * sf.color_factor;// normalise z in[0,1]
					sf.graphics.setColor(sf.getSurfaceColor().getPolygonColor(sf.curve, z));

					// whatever the plot mode

					sf.graphics.fillRect(x, y, w, h);
					if (isMesh) {

						sf.graphics.setColor(sf.getSurfaceColor().getLineColor(sf.curve, z));
						sf.graphics.drawRect(x, y, w, h);
					}
					index += multiple_factor;
				}
			}
		}

		// Bounding rectangle
		sf.drawBoundingRect();

		// model.setMessage("completed");
	}

	
	
}
