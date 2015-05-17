package jplot3d.draw;


import java.awt.Color;
import java.awt.Point;

import jplot3d.*;
import jplot3d.SurfaceModel.PlotColor;
import jplot3d.SurfaceModel.PlotType;

public class DrawPlain {
	/**
	 * what =1,2,3,4 - vertical walls what =0 top
	 * 
	 * @param surfaceCanvas
	 *            TODO
	 * @param what
	 *            TODO
	 * @param x1
	 *            TODO
	 * @param y1
	 *            TODO
	 * @param z1
	 *            TODO
	 * @param dx
	 *            TODO
	 * @param dy
	 *            TODO
	 * @param dz
	 *            TODO
	 * **/

	public static void run(int what, float x1, float y1, float z1, float dx,
			float dy, float dz, JSurface sf) {

		Point projection = null;
		int[] x = new int[5];
		int[] y = new int[5];

		if (what == 1) {

			projection = sf.projector.project(x1, y1, z1);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = sf.projector.project(x1, y1 + dy, z1);
			x[1] = projection.x;
			y[1] = projection.y;
			projection = sf.projector.project(x1, y1 + dy, z1 + dz);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = sf.projector.project(x1, y1, z1 + dz);

		} else if (what == 2) {
			projection = sf.projector.project(x1, y1, z1);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = sf.projector.project(x1 + dx, y1, z1);
			x[1] = projection.x;
			y[1] = projection.y;
			projection = sf.projector.project(x1 + dx, y1, z1 + dz);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = sf.projector.project(x1, y1, z1 + dz);

		} else if (what == 3) {

			projection = sf.projector.project(x1 + dx, y1, z1);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = sf.projector.project(x1 + dx, y1 + dy, z1);
			x[1] = projection.x;
			y[1] = projection.y;
			projection = sf.projector.project(x1 + dx, y1 + dy, z1 + dz);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = sf.projector.project(x1 + dx, y1, z1 + dz);

		} else if (what == 4) {

			projection = sf.projector.project(x1, y1 + dy, z1);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = sf.projector.project(x1, y1 + dy, z1 + dz);
			x[1] = projection.x;
			y[1] = projection.y;
			projection = sf.projector.project(x1 + dx, y1 + dy, z1 + dz);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = sf.projector.project(x1 + dx, y1 + dy, z1);
			// bottom
		} else if (what == 5) {
			projection = sf.projector.project(x1, y1, z1 + dz);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = sf.projector.project(x1, y1 + dy, z1 + dz);
			x[1] = projection.x;
			y[1] = projection.y;
			projection = sf.projector.project(x1 + dx, y1 + dy, z1 + dz);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = sf.projector.project(x1 + dx, y1, z1 + dz);
		} else if (what == 0) {
			projection = sf.projector.project(x1, y1, z1);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = sf.projector.project(x1, y1 + dy, z1);
			x[1] = projection.x;
			y[1] = projection.y;
			projection = sf.projector.project(x1 + dx, y1 + dy, z1);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = sf.projector.project(x1 + dx, y1, z1);
		}

		x[3] = projection.x;
		y[3] = projection.y;
		x[4] = x[0];
		y[4] = y[0];

		float z = z1;

		int count = 1;

		if (sf.model.getPlotColor() == PlotColor.OPAQUE) {
			sf.graphics.setColor(sf.getSurfaceColor().getBackgroundColor());

		} else if (sf.model.getPlotColor() == PlotColor.SPECTRUM) {
			z = 0.8f - (z / count - sf.zmin) * sf.color_factor;
			sf.graphics.setColor(Color.getHSBColor(z, 1.0f, 1.0f));

		} else if (sf.model.getPlotColor() == PlotColor.GRAYSCALE) {
			z = (z / count - sf.zmin) * sf.color_factor;
			sf.graphics.setColor(Color.getHSBColor(0, 0, z));
			if (z < 0.3f)
				sf.line_color = new Color(0.6f, 0.6f, 0.6f);

		} else if (sf.model.getPlotColor() == PlotColor.DUALSHADE) {
			z = (z / count - sf.zmin) * sf.color_factor + 0.4f;
			sf.graphics.setColor(Color.getHSBColor(0.6f, 0.7f, z)); // ??
																	// correct

		} else {
			sf.graphics.setColor(sf.model.getColorModel().getBackgroundColor());
		}

        // this is just for solid fill
        if (sf.model.getPlotColor()==PlotColor.OPAQUE) {

            sf.graphics.setColor(sf.model.getColorModel().getBackgroundColor());
            sf.graphics.fillPolygon(x, y, 5);
            sf.graphics.setColor(sf.model.getColorModel().getLineColor());
            sf.graphics.drawPolygon(x, y, 5);

        } else {

       // this is the rest
        	sf.graphics.fillPolygon(x, y, 5);
        	sf.graphics.setColor(sf.model.getColorModel().getLineColor());
        	sf.graphics.drawPolygon(x, y, 5);

      }


	}

}
