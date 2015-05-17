package jhplot.bsom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Vector;


/**
 * Draw area
 * @author sergei
 *
 */
public class BsomDrawArea extends DrawArea {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<Double> buf;

	BsomDrawArea(Bsom demo, int xd, int yd, double scale) {
		super(demo, xd, yd, scale);
	}

	/**
	 * Initialize data
	 */
	void initData() {
		n_data = buf.size() / 2;
		X = new UserData(n_data, scale, 0, xd, yd, buf);

	}

	/* Double-buffering is used for drawing objects. */

	public boolean draw_new_data;
	Graphics datagraphics;
	Image datascreen;

	/**
	 * Update canvas
	 */
	public synchronized void update(Graphics g) {

		Dimension d = getSize();

		if ((offscreen == null) || (d.width != offscreensize.width)
				|| (d.height != offscreensize.height)) {
			offscreen = createImage(d.width, d.height);
			offscreensize = d;
			offgraphics = offscreen.getGraphics();
			draw_new_data = true;
		}
		if (draw_new_data) {
			datascreen = createImage(d.width, d.height);
			datagraphics = datascreen.getGraphics();
			datagraphics.setColor(getBackground());
			datagraphics.fillRect(0, 0, d.width, d.height);
			datagraphics.setColor(Color.black);
			X.draw(datagraphics);
			draw_new_data = false;
		}

		if (!density_mode) {
			offgraphics.drawImage(datascreen, 0, 0, null);
		} else {
			g.drawString("Now, density is calculated.", 0, 10);
			drawDensity(offgraphics);
			offgraphics.setColor(Color.red);

			UserData UX = (UserData) X;
			double s = UX.dscale * UX.dscale;

			offgraphics.drawString("a=" + alpha * s, 0, 10);
			offgraphics.drawString("b=" + beta * s, 0, 20);
			offgraphics.setColor(Color.green);
			X.draw(offgraphics);
		}

		offgraphics.setColor(Color.blue);
		W.draw(offgraphics);
		W.drawCurve(offgraphics);
		paint(g);

	}
	
}
