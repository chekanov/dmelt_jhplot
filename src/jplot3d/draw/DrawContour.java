package jplot3d.draw;



import jplot3d.*;



public class DrawContour {


	/**
	 * Creates a density plot
	 * @param surfaceCanvas TODO
	 */

	public final static void run(JSurface sf) {
		
		boolean printing=sf.printing;
		boolean plotfunc1=sf.plotfunc1;
		boolean plotfunc2=sf.plotfunc2;
		


                float zi, zx;

        		sf.accumulator.clearAccumulator();

        		try {
        			zi = sf.model.getZMin();
        			zx = sf.model.getZMax();
        			if (zi >= zx)
        				throw new NumberFormatException();
        		} catch (NumberFormatException e) {
        			System.out.println("plotContour:Error in ranges");
        			return;
        		}

        		sf.zmin = zi;
        		sf.zmax = zx;
        		sf.computePlotArea();

        		int plot_density = sf.model.getDispDivisions();
        		int multiple_factor = sf.calc_divisions / plot_density;
        		// model.setDispDivisions(plot_density);

        		Thread.yield();
        		sf.contour_stepz = (zx - zi) / (sf.contour_lines + 1);

        		// model.setMessage("regenerating ...");

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
        					sf.createContour();
        					index += multiple_factor;
        				}
        			}
        		}

        		// Contour lines
        		sf.graphics.setColor(sf.getSurfaceColor().getLineColor());
        		sf.accumulator.drawAll(sf.graphics);

        		// Bounding rectangle
        		sf.drawBoundingRect();

	}

	
	
}
