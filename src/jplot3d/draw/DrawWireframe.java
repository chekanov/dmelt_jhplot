package jplot3d.draw;


import java.awt.Point;

import jplot3d.*;



public class DrawWireframe {


	/**
	 * Creates a surface plot
	 * @param surfaceCanvas TODO
	 */

	public final static void run(JSurface sf) {
		
		boolean printing=sf.printing;
		boolean plotfunc1=sf.plotfunc1;
		boolean plotfunc2=sf.plotfunc2;
		boolean isBoxed=sf.isBoxed;
		int i, j, k;
		int plot_density, multiple_factor;
		int counter;
		float zi, zx;
		float z;
		float lx = 0, ly = 0, lastz = 0;
		Point lastproj = new Point(0, 0);
		boolean error, lasterror, invalid;

		sf.projection = new Point(0, 0);
		try {
			zi = sf.model.getZMin();
			zx = sf.model.getZMax();
			if (zi >= zx)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			System.out.println("Error in ranges");
			return;
		}

		plot_density = sf.model.getDispDivisions();
		multiple_factor = sf.calc_divisions / plot_density;
		// model.setDispDivisions(plot_density);

		sf.zmin = zi;
		sf.zmax = zx;
		/*
		 * if (rotate) model.setMessage("rotating ..."); else
		 * model.setMessage("regenerating ...");/*
		 */

		if (!printing) {
			sf.graphics.setColor(sf.getSurfaceColor().getBackgroundColor());
			sf.graphics.fillRect(0, 0, sf.getBounds().width, sf.getBounds().height);
		}

		Thread.yield();
		sf.drawBoxGridsTicksLabels(sf.graphics, false);

		sf.projector.setZRange(sf.zmin, sf.zmax);

		for (int func = 0; func < 2; func++) {

			if ((func == 0) && !plotfunc1)
				continue;
			if ((func == 1) && !plotfunc2)
				continue;

			i = 0;
			j = 0;
			k = 0;
			counter = 0;

			// plot - x direction

			while (i <= sf.calc_divisions) {
				lasterror = true;
				if (counter == 0) {
					while (j <= sf.calc_divisions) {
						Thread.yield();
						z = sf.vertex[func][k].z;
						invalid = Float.isNaN(z);
						if (!invalid) {
							sf.graphics.setColor(sf.getSurfaceColor().getLineColor(sf.curve, z));

							if (z < sf.zmin) {
								error = true;
								float ratio = (sf.zmin - lastz) / (z - lastz);
								sf.projection = sf.projector.project(ratio
										* (sf.vertex[func][k].x - lx) + lx, ratio
										* (sf.vertex[func][k].y - ly) + ly, -10);
							} else if (z > sf.zmax) {
								error = true;
								float ratio = (sf.zmax - lastz) / (z - lastz);
								sf.projection = sf.projector.project(ratio
										* (sf.vertex[func][k].x - lx) + lx, ratio
										* (sf.vertex[func][k].y - ly) + ly, 10);
							} else {
								error = false;
								sf.projection = sf.vertex[func][k].projection(sf.projector);
							}
							if (lasterror && (!error) && (j != 0)) {
								if (lastz > sf.zmax) {
									float ratio = (sf.zmax - z) / (lastz - z);
									lastproj = sf.projector.project(ratio
											* (lx - sf.vertex[func][k].x)
											+ sf.vertex[func][k].x, ratio
											* (ly - sf.vertex[func][k].y)
											+ sf.vertex[func][k].y, 10);
								} else if (lastz < sf.zmin) {
									float ratio = (sf.zmin - z) / (lastz - z);
									lastproj = sf.projector.project(ratio
											* (lx - sf.vertex[func][k].x)
											+ sf.vertex[func][k].x, ratio
											* (ly - sf.vertex[func][k].y)
											+ sf.vertex[func][k].y, -10);
								}
							} else
								invalid = error && lasterror;
						} else
							error = true;
						if (!invalid && (j != 0)) {
							sf.graphics.drawLine(lastproj.x, lastproj.y,
									sf.projection.x, sf.projection.y);
						}
						lastproj = sf.projection;
						lasterror = error;
						lx = sf.vertex[func][k].x;
						ly = sf.vertex[func][k].y;
						lastz = z;
						j++;
						k++;
					}
				} else
					k += sf.calc_divisions + 1;
				j = 0;
				i++;
				counter = (counter + 1) % multiple_factor;
			}

			// plot - y direction

			i = 0;
			j = 0;
			k = 0;
			counter = 0;

			while (j <= sf.calc_divisions) {
				lasterror = true;
				if (counter == 0) {
					while (i <= sf.calc_divisions) {
						Thread.yield();
						z = sf.vertex[func][k].z;
						invalid = Float.isNaN(z);
						if (!invalid) {
							if (z < sf.zmin) {
								error = true;
								float ratio = (sf.zmin - lastz) / (z - lastz);
								sf.projection = sf.projector.project(ratio
										* (sf.vertex[func][k].x - lx) + lx, ratio
										* (sf.vertex[func][k].y - ly) + ly, -10);
							} else if (z > sf.zmax) {
								error = true;
								float ratio = (sf.zmax - lastz) / (z - lastz);
								sf.projection = sf.projector.project(ratio
										* (sf.vertex[func][k].x - lx) + lx, ratio
										* (sf.vertex[func][k].y - ly) + ly, 10);
							} else {
								error = false;
								sf.projection = sf.vertex[func][k].projection(sf.projector);
							}
							if (lasterror && (!error) && (i != 0)) {
								if (lastz > sf.zmax) {
									float ratio = (sf.zmax - z) / (lastz - z);
									lastproj = sf.projector.project(ratio
											* (lx - sf.vertex[func][k].x)
											+ sf.vertex[func][k].x, ratio
											* (ly - sf.vertex[func][k].y)
											+ sf.vertex[func][k].y, 10);
								} else if (lastz < sf.zmin) {
									float ratio = (sf.zmin - z) / (lastz - z);
									lastproj = sf.projector.project(ratio
											* (lx - sf.vertex[func][k].x)
											+ sf.vertex[func][k].x, ratio
											* (ly - sf.vertex[func][k].y)
											+ sf.vertex[func][k].y, -10);
								}
							} else
								invalid = error && lasterror;
						} else
							error = true;
						if (!invalid && (i != 0)) {
							sf.graphics.drawLine(lastproj.x, lastproj.y,
									sf.projection.x, sf.projection.y);
						}
						lastproj = sf.projection;
						lasterror = error;
						lx = sf.vertex[func][k].x;
						ly = sf.vertex[func][k].y;
						lastz = z;
						i++;
						k += sf.calc_divisions + 1;
					}
				}
				i = 0;
				k = ++j;
				counter = (counter + 1) % multiple_factor;
			}
		}
		if (isBoxed)
			sf.drawBoundingBox();
		// if (!rotate) model.setMessage("completed");
	}
	
	
}
