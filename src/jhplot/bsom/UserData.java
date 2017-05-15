package jhplot.bsom;

import java.util.Vector;

/**
 * User's data.
 * @author sergei
 *
 */
public class UserData extends Data {

	double dscale; // Scale of data
	double mx;
	double my;

	UserData(int n, double scale, int radius, int x, int y, Vector<Double> input) {
		super(n, scale, radius, x, y, 1., 1., 1., 1.);

		int i;

		// Obtain means.
		mx = 0.;
		my = 0.;

		for (i = 0; i < n; i++) {
			value[i][0] = (double) ((Double) (input.elementAt(2 * i)))
					.doubleValue();
			value[i][1] = (double) ((Double) (input.elementAt(2 * i + 1)))
					.doubleValue();
			mx += value[i][0];
			my += value[i][1];
		}

		mx /= n;
		my /= n;

		// Move the means to zeros and then get the range.
		double dmax = 0;

		for (i = 0; i < n; i++) {
			value[i][0] -= mx;
			value[i][1] -= my;
			if (Math.abs(value[i][0]) > dmax) {
				dmax = Math.abs(value[i][0]);
			}
			if (Math.abs(value[i][1]) > dmax) {
				dmax = Math.abs(value[i][1]);
			}
		}

		// Resize the data to go into a 2*2 box.
		dscale = 2. / dmax;

		for (i = 0; i < n; i++) {

			value[i][0] *= dscale;
			value[i][1] *= dscale;
		}

	}
}