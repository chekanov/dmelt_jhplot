package jplot3d;


import java.awt.*;


/**
 * The <code>JPoint</code> defines a plot point in terms of it's X , Y and Z
 * value. Also sets the lift-pen attribute, which, if true, will lift the pen
 * after having plot this point. It has internal parameter which can be used for
 * sorting
 */

public class JPoint implements Comparable {

	protected double x;

	protected double y;

	protected double z;

	protected double dx;

	protected double dy;

	protected double dz;

	protected double d;

	protected double a;

	protected Color c;

	protected int s; // size

	/**
	 * Default constructor, initializes the class x- and y values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param z
	 *            z-value;
	 */
	public JPoint(double x, double y, double z) {
		setPoint(x, y, z, 0);
	}

	/**
	 * Default constructor, initializes the class x- and y values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param z
	 *            z-value;
	 * @param d
	 *            some attribute value;
	 */
	public JPoint(double x, double y, double z, double d) {
		setPoint(x, y, z, d);
	}

	/**
	 * Default constructor, initializes the class x- and y values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param z
	 *            z-value;
	 * @param d
	 *            distance to the observer
	 */
	public JPoint(double x, double y, double z, double d, Color c, int s) {
		setPoint(x, y, z, d, c, s);
	}

	/**
	 * Another constructor, initializes the class x- and y values.
	 * 
	 * @param pp
	 *            PlotPoint instance
	 */
	public JPoint(JPoint pp) {
		setPoint(pp);
	}

	/**
	 * Sets x-, y and z values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param z
	 *            y-value;
	 */
	public void setPoint(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets x-, y and z values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param z
	 *            y-value;
	 * @param d
	 *            distance to the observer
	 */
	public void setPoint(double x, double y, double z, double d) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.d = d;

	}

	/**
	 * Sets extenstions
	 * 
	 * @param dx
	 *            x-value;
	 * @param dy
	 *            y-value;
	 * @param dz
	 *            y-value;
	 */
	public void setExt(double dx, double dy, double dz) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;

	}

	public void setPoint(double x, double y, double z, double d, Color c, int s) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.d = d;
		this.c = c;
		this.s = s;
	}

	/**
	 * Sets x- and y and z values.
	 * 
	 * @param pp
	 *            PlotPoint instance
	 */
	public void setPoint(JPoint pp) {
		this.x = pp.getX();
		this.y = pp.getY();
		this.z = pp.getZ();
		this.d = pp.getD();

	}

	/**
	 * Sets color.
	 */
	public void setSymbolColor(Color c) {
		this.c = c;

	}

	/**
	 * Get color.
	 */
	public Color getSymbolColor() {
		return this.c;

	}

	/**
	 * Get symbol size
	 */
	public int getSymbolSize() {
		return this.s;

	}

	/**
	 * returns x- and y and z values.
	 * 
	 * @return a PlotPoint2D instance with the current X- and Y values
	 */
	public JPoint getPoint() {
		return new JPoint(x, y, z, d);
	}

	/**
	 * Return the current X-value.
	 * 
	 * @return the current value of x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Return the current DX-value.
	 * 
	 * @return the current value of x
	 */
	public double getDX() {
		return dx;
	}

	/**
	 * Return the current Y-value.
	 * 
	 * @return the current value of y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Return the current DY-value.
	 * 
	 * @return the current value of y
	 */
	public double getDY() {
		return dy;
	}

	/**
	 * Return the current Z-value.
	 * 
	 * @return the current value of y
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Return the current DZ-value.
	 * 
	 * @return the current value of y
	 */
	public double getDZ() {
		return dz;
	}

	/**
	 * Return the current distance to the observer
	 * 
	 * @return the current value of distance
	 */
	public double getD() {
		return d;
	}

	/**
	 * Set the current distance value.
	 * 
	 * @param d
	 *            distance
	 */
	public void setD(double d) {
		this.d = d;
	}

	/* Overload compareTo method */
	/* SORT IN INCREASISING ORDER */

	public int compareTo(Object obj) {
                if (obj == null) return 0;

		JPoint tmp = (JPoint) obj;
                 
		if (this.d < tmp.getD()) {
			/* instance lt received */
			return -1;
		} else if (this.d > tmp.getD()) {
			/* instance gt received */
			return 1;
		}
		/* instance == received */
		return 0;
	}

}
