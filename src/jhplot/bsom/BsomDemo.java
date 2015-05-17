package jhplot.bsom;

import java.awt.*;
import java.lang.Math; //  import Slider;
import java.util.Hashtable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * BsomDemo is an applet for the demonstration of Bayesian Self-Organizing Maps.
 * The following parameters are used: 'data' is the number of data points. (def.
 * 100) 'unit' is the number of inner units (weight points). (def. 20) 'amin' is
 * the power of 10 for minimum alpha. (def. 0.) 'amax' is the power of 10 for
 * maximum alpha. (def. 5.) 'bmin' is the power of 10 for minimum beta. (def.
 * 0.) 'bmax' is the power of 10 for maximum beta. (def. 5.) 'init_weight' is
 * the size of initial random weights. (def. 0.1) (xd, yd) is the position of
 * origin of mathematical space in display. (def. 210, 150) 'scale' is the scale
 * factor from mathematical space to display. (def. 70) 'dstep' is the size of a
 * cell for density calculation. (def. 10)
 * 
 * @author A.Utsugi and S.Chekanov
 */

public class BsomDemo extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DrawArea drawArea;

	protected AlphaSlider alphaSlider;
	protected BetaSlider betaSlider;

	protected WidthSlider widthSlider;
	protected HeightSlider heightSlider;
	protected PhaseSlider phaseSlider;
	protected NoiseSlider noiseSlider;

	protected JButton initButton;
	protected JCheckBox learnButton;
	protected JCheckBox autoButton;
	protected JCheckBox densityButton;

	protected JTextField unitNumberText;

	protected int xd, yd;
	protected double scale;
	protected int n_data, n_unit;
	protected int dstep;

	double width_range = 2.;
	double height_range = 2.;
	double phase_range = 0.5;
	double noise_range = 0.5;

	double initial_width = 2.;
	double initial_height = 1.;
	double initial_noise_level = 0.2;
	double initial_phase = 0.;
	double initial_weight_level = 0.1;

	protected double alpha_min_power, alpha_max_power;
	protected double beta_min_power, beta_max_power;
	protected double initial_alpha, initial_beta;

	public void init() {
		String p;

		/* Get parameters. */

		n_data = 100;
		n_unit = 20;
		alpha_min_power = 0.0;
		alpha_max_power = 5.0;
		beta_min_power = 0;
		beta_max_power = 5.0;

		/*
		 * 
		 * p = getParameter("data"); if(p == null){ p = "100"; } n_data =
		 * Integer.valueOf(p).intValue();
		 * 
		 * p = getParameter("unit"); if(p == null){ p = "20"; } n_unit =
		 * Integer.valueOf(p).intValue();
		 * 
		 * p = getParameter("amin"); if(p == null){ p = "0."; } alpha_min_power
		 * = Double.valueOf(p).doubleValue();
		 * 
		 * p = getParameter("amax"); if(p == null){ p = "5."; } alpha_max_power
		 * = Double.valueOf(p).doubleValue();
		 * 
		 * p = getParameter("bmin"); if(p == null){ p = "0."; } beta_min_power =
		 * Double.valueOf(p).doubleValue();
		 * 
		 * p = getParameter("bmax"); if(p == null){ p = "5."; } beta_max_power =
		 * Double.valueOf(p).doubleValue();
		 */

		initial_alpha = Math.pow(10., alpha_max_power);
		initial_beta = Math.pow(10., beta_min_power);

		xd = 210;
		yd = 150;
		scale = 70;
		dstep = 10;
		initial_weight_level = 0.1;

		/*
		 * p = getParameter("xd"); if(p == null){ p = "210"; } xd =
		 * Integer.valueOf(p).intValue();
		 * 
		 * p = getParameter("yd"); if(p == null){ p = "150"; } yd =
		 * Integer.valueOf(p).intValue();
		 * 
		 * p = getParameter("scale"); if(p == null){ p = "70"; } scale =
		 * (double)Integer.valueOf(p).intValue();
		 * 
		 * p = getParameter("dstep"); if(p == null){ p = "10"; } dstep =
		 * Integer.valueOf(p).intValue();
		 * 
		 * 
		 * p = getParameter("init_weight"); if(p == null){ p = "0.1"; }
		 * initial_weight_level = Double.valueOf(p).doubleValue();
		 */

		/* Layout */

		setLayout(new BorderLayout());

		// Draw area
		drawArea = new DrawArea(this, xd, yd, scale);
		drawArea.setBackground(Color.white);
		drawArea.setSize(new Dimension(600, 400));

		drawArea.n_data = n_data;
		drawArea.n_unit = n_unit;
		drawArea.dstep = dstep;

		drawArea.width = initial_width;
		drawArea.height = initial_height;
		drawArea.noise_level = initial_noise_level;
		drawArea.phase = initial_phase;

		drawArea.initial_weight_level = initial_weight_level;

		drawArea.alpha = initial_alpha;
		drawArea.beta = initial_beta;

		add("Center", drawArea);

		// Control panel
		JPanel controlPanel = new JPanel();

		// Button panel
		JPanel buttonPanel = new JPanel();
		JPanel unitNumberPanel;

		buttonPanel.setLayout(new GridLayout(5, 1));

		learnButton = new JCheckBox("learn");
		learnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				drawArea.learn = learnButton.isSelected();
				if (!drawArea.learn && drawArea.auto_learn) {
					drawArea.auto_learn = false;
					autoButton.setSelected(false);
				}

			}
		});

		autoButton = new JCheckBox("auto");
		autoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				drawArea.auto_learn = autoButton.isSelected();
				if (!drawArea.learn && drawArea.auto_learn) {
					drawArea.learn = true;
					learnButton.setSelected(true);
				}

			}
		});

		densityButton = new JCheckBox("density");
		densityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				if (densityButton.isSelected()) {
					drawArea.stop();
					drawArea.density_mode = true;
					drawArea.repaint();
				} else {
					drawArea.density_mode = false;
					drawArea.start();
				}

			}
		});

		initButton = new JButton("init");
		initButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				n_unit = Integer.parseInt(unitNumberText.getText());
				resetUnitNumber(n_unit);

				drawArea.W.init();
				if (drawArea.auto_learn) {
					alphaSlider.setValueOfSlider(initial_alpha);
					betaSlider.setValueOfSlider(initial_beta);
					drawArea.alpha = initial_alpha;
					drawArea.beta = initial_beta;
				}
			}
		});

		unitNumberPanel = new JPanel();
		unitNumberPanel.setLayout(new GridLayout(1, 2));
		unitNumberText = new JTextField(Integer.toString(n_unit), 2);
		unitNumberText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				n_unit = Integer.parseInt(unitNumberText.getText());
				resetUnitNumber(n_unit);
			}
		});

		JLabel unitNumberLabel = new JLabel("#unit");
		unitNumberPanel.add(unitNumberLabel);
		unitNumberPanel.add(unitNumberText);

		buttonPanel.add(learnButton);
		buttonPanel.add(autoButton);
		buttonPanel.add(densityButton);
		buttonPanel.add(initButton);
		buttonPanel.add(unitNumberPanel);

		controlPanel.add(buttonPanel);

		// Weight panel
		Panel weightPanel = new Panel();
		weightPanel.setLayout(new GridLayout(2, 1));

		// Alpha slider panel
		JPanel alphaSliderPanel = new JPanel();
		alphaSliderPanel.setLayout(new BorderLayout());
		alphaSlider = new AlphaSlider(drawArea, alpha_min_power,
				alpha_max_power);
		Label alabel = new Label("alpha");
		alphaSliderPanel.add("Center", alphaSlider);
		alphaSliderPanel.add("South", alabel);
		weightPanel.add(alphaSliderPanel);

		// Beta slider panel
		JPanel betaSliderPanel = new JPanel();
		betaSliderPanel.setLayout(new BorderLayout());
		betaSlider = new BetaSlider(drawArea, beta_min_power, beta_max_power);
		Label blabel = new Label("beta");
		betaSliderPanel.add("Center", betaSlider);
		betaSliderPanel.add("South", blabel);
		weightPanel.add(betaSliderPanel);

		controlPanel.add(weightPanel);

		// First data parameter panel
		JPanel dataPanel1 = new JPanel();
		dataPanel1.setLayout(new GridLayout(2, 1));

		JPanel widthSliderPanel = new JPanel();
		widthSliderPanel.setLayout(new BorderLayout());
		widthSlider = new WidthSlider(drawArea, width_range);
		JLabel wlabel = new JLabel("width");
		widthSliderPanel.add("Center", widthSlider);
		widthSliderPanel.add("South", wlabel);

		JPanel heightSliderPanel = new JPanel();
		heightSliderPanel.setLayout(new BorderLayout());
		heightSlider = new HeightSlider(drawArea, height_range);
		Label hlabel = new Label("height");
		heightSliderPanel.add("Center", heightSlider);
		heightSliderPanel.add("South", hlabel);

		dataPanel1.add(widthSliderPanel);
		dataPanel1.add(heightSliderPanel);

		// Second data parameter panel
		JPanel dataPanel2 = new JPanel();
		dataPanel2.setLayout(new GridLayout(2, 1));

		JPanel phaseSliderPanel = new JPanel();
		phaseSliderPanel.setLayout(new BorderLayout());
		phaseSlider = new PhaseSlider(drawArea, phase_range);
		Label label5 = new Label("phase");
		phaseSliderPanel.add("Center", phaseSlider);
		phaseSliderPanel.add("South", label5);

		JPanel noiseSliderPanel = new JPanel();
		noiseSliderPanel.setLayout(new BorderLayout());
		noiseSlider = new NoiseSlider(drawArea, noise_range);
		Label label6 = new Label("noise level");
		noiseSliderPanel.add("Center", noiseSlider);
		noiseSliderPanel.add("South", label6);

		dataPanel2.add(phaseSliderPanel);
		dataPanel2.add(noiseSliderPanel);

		controlPanel.add(dataPanel1);
		controlPanel.add(dataPanel2);

		add("South", controlPanel);

		// Set initial values of sliders.
		alphaSlider.setValueOfSlider(initial_alpha);
		betaSlider.setValueOfSlider(initial_beta);

		widthSlider.setValueOfSlider(initial_width);
		heightSlider.setValueOfSlider(initial_height);
		phaseSlider.setValueOfSlider(initial_phase);
		noiseSlider.setValueOfSlider(initial_noise_level);

		drawArea.init();

		repaint();
	}

	public void start() {
		if (!drawArea.density_mode) {
			drawArea.start();
		}
	}

	public void startNoThread() {
		drawArea.runNoThread();

	}

	public void stop() {
		drawArea.stop();
	}

	/**
	 * Set learn mode
	 */
	public void learn() {
		drawArea.learn = true;
		drawArea.auto_learn = false;
	}

	/**
	 * Set auto mode
	 */
	public void auto() {
		drawArea.auto_learn = true;
		drawArea.learn = true;

	}

	public synchronized boolean action(Event evt, Object what) {
		if (evt.target == learnButton) {
			drawArea.learn = learnButton.isSelected();
			if (!drawArea.learn && drawArea.auto_learn) {
				drawArea.auto_learn = false;
				autoButton.setSelected(false);
			}
			return true;
		}

		if (evt.target == autoButton) {
			drawArea.auto_learn = autoButton.isSelected();
			if (!drawArea.learn && drawArea.auto_learn) {
				drawArea.learn = true;
				learnButton.setSelected(true);
			}
			return true;
		}

		if (evt.target == initButton) {
			drawArea.W.init();
			if (drawArea.auto_learn) {
				alphaSlider.setValueOfSlider(initial_alpha);
				betaSlider.setValueOfSlider(initial_beta);
				drawArea.alpha = initial_alpha;
				drawArea.beta = initial_beta;
			}
			return true;
		}

		if (evt.target == densityButton) {
			if (densityButton.isSelected()) {
				drawArea.stop();
				drawArea.density_mode = true;
				drawArea.repaint();
			} else {
				drawArea.density_mode = false;
				drawArea.start();
			}
			return true;
		}

		if (evt.target == unitNumberText) {
			resetUnitNumber(Integer.valueOf((String) what).intValue());
			return true;
		}

		return false;
	}

	/**
	 * Set alpha and beta for learning
	 */
	public void setAlphaBeta(double alpha, double beta) {
		// alphaSlider.setValueOfSlider(alpha);
		// betaSlider.setValueOfSlider(beta);
		drawArea.alpha = alpha;
		drawArea.beta = beta;

	}

	public void resetUnitNumber(int r) {
		int rmax = 99;

		if (r > rmax) {
			showStatus("Too many units.");
			return;
		} else if (r < 2) {
			showStatus("Too few units.");
			return;
		} else {
			// showStatus("Unit number is now changed.");
		}

		drawArea.alpha = initial_alpha;
		drawArea.beta = initial_beta;
		drawArea.auto_learn = false;
		drawArea.learn = false;
		drawArea.n_unit = r;
		drawArea.initWeight();

		autoButton.setSelected(false);
		learnButton.setSelected(false);
		alphaSlider.setValueOfSlider(initial_alpha);
		betaSlider.setValueOfSlider(initial_beta);

		// showStatus(" ");

		repaint();
	}

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */
	private void showStatus(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}

/**
 * A Visible2DPointArray has two-dimensional points and can plot them on
 * display.
 */

class Visible2DPointArray extends Matrix {
	int radius; // of a circle expressing a point.
	double scale;
	int dxo, dyo; // the position of origin on display

	private int diameter; // of a circle expressing a point.
	final double xo = 0, yo = 0; // the position of origin in mathematical space

	/**
	 * Construct a Visible2DPointArray.
	 * 
	 * @param n
	 *            The number of points.
	 * @param scale
	 *            Scale factor from mathematical space to display.
	 * @param radius
	 *            The radius of a circle expressing a point.
	 * @param x
	 *            , y The position of origin on display.
	 */
	public Visible2DPointArray(int n, double scale, int radius, int x, int y) {
		super(n, 2);

		this.radius = radius;
		this.scale = scale;

		radius = 2;

		if (radius > 0)
			diameter = radius * 2;
		else
			diameter = 1;

		dxo = x;
		dyo = y;
	}

	/**
	 * Draw points as circles.
	 * 
	 * @param g
	 *            Graphics of the drawn object.
	 */
	public void draw(Graphics g) {
		int i, x, y;

		for (i = 0; i < row; i++) {
			x = (int) (scale * (value[i][0] - xo)) + dxo - radius;
			y = (int) (-scale * (value[i][1] - yo)) + dyo - radius;
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(new Ellipse2D.Float(x, y, diameter, diameter));
			g2.fill(new Ellipse2D.Float(x, y, diameter, diameter));

			// g.drawOval(x, y, diameter, diameter);
			// g.fillOval(Color.black);
		}
	}

	/**
	 * Draw points as linked circles on a graphic object.
	 * 
	 * @param g
	 *            Graphics of the drawn object.
	 */
	public void drawCurve(Graphics g) {
		int i, x, y;
		int xp, yp;

		xp = (int) (scale * (value[0][0] - xo)) + dxo;
		yp = (int) (-scale * (value[0][1] - yo)) + dyo;

		for (i = 1; i < row; i++) {
			x = (int) (scale * (value[i][0] - xo)) + dxo;
			y = (int) (-scale * (value[i][1] - yo)) + dyo;
			g.drawLine(xp, yp, x, y);
			xp = x;
			yp = y;
		}
	}

	int index(int x, int y)
	// Return the index of the point specified by a position (x, y).
	{
		int i;

		for (i = 0; i < row; i++) {
			if (Math.abs(x - (int) (scale * (value[i][0] - xo)) - dxo) <= radius
					&& Math.abs(y - (int) (-scale * (value[i][1] - yo)) - dyo) <= radius)
				return i;
		}
		return -1;
	}

	void move(int id, int ix, int iy)
	// Move a point specified by ID to a position (ix, iy).
	{
		value[id][0] = (ix - dxo) / scale + xo;
		value[id][1] = -(iy - dyo) / scale + yo;
	}
}

/**
 * A data object consists of points made by adding Gaussian perturbation (noise)
 * to points on a sin curve. The width, height and phase of the curve and the
 * magnitude of noise can be changed afterward.
 */
class Data extends Visible2DPointArray {

	double width, height, noise_level, phase;
	private Matrix noise;

	public Data(int n, double scale, int radius, int x, int y, double width,
			double height, double phase, double noise_level) {
		super(n, scale, radius, x, y);

		noise = Matrix.random(row, col);
		this.width = width;
		this.height = height;
		this.phase = phase;
		this.noise_level = noise_level;
		this.remake();
	}

	synchronized public void remake() {
		int i;
		double x;
		int n = row;

		for (i = 0; i < n; i++) {
			x = i;
			value[i][0] = width * (2 * x / n - 1);
			value[i][1] = height * Math.sin(2 * Math.PI * (x / n + phase));
		}

		this.updateLinearConv(1., noise_level, noise);
	}
}

/**
 * A weight object consists of weight points (centroids) of a BSOM model, which
 * are initialized randomly and updated by an EM algorithm.
 */
class Weight extends Visible2DPointArray {
	Matrix Ds, P;
	double initial_level;
	int seed = 1;

	public Weight(int r, double scale, int radius, int x, int y,
			double initial_level) {
		super(r, scale, radius, x, y);
		this.initial_level = initial_level;
		this.init();
	}

	/**
	 * Initialize by Gaussian random numbers.
	 */
	synchronized public void init() {
		Matrix noise;

		noise = Matrix.random(row, col, seed);
		this.updateLinearConv(0., initial_level, noise);
		seed++;
	}

	/**
	 * Update by an EM algorithm.
	 */
	synchronized public void update(Data X, Matrix M, double alpha,
			double beta, int grabed_unit) {
		Matrix Z, N, Ki, Xm, Wn;
		double c = 1;

		// Soft competition
		Ds = this.crossSqDistance(X); // Make squared Euclidean distances
		// between data points and weights.
		P = Ds.multipliedBy(-beta / 2.);
		P.updateExp(); // Convert into 'Boltzmann Factors'.
		Z = P.verticalSum(); // Make partition function.
		dealZeroZ(Z, P, Ds); // Though this is not necessary in many cases, ....
		P.updateDivideByRowVector(Z); // Convert into fuzzy membership
		// (posterior selection probabilities).

		// Update weights by EM algorithm.
		N = P.horizontalSum(); // estimated numbers of members
		N = N.diagonal(); // Convert into a diagonal matrix.
		Ki = M.linearConv(alpha / beta, 1., N); // inverse lateral interaction
		Xm = P.multipliedBy(X); // data weighted by fuzzy membership
		Wn = Ki.dividedBy(Xm, 5); // new temporary estimates of weights

		// A point grabbed by mouse is escaped from weight-update.
		if (grabed_unit >= 0) {
			Wn.value[grabed_unit][0] = value[grabed_unit][0];
			Wn.value[grabed_unit][1] = value[grabed_unit][1];
		}

		this.updateLinearConv(1. - c, c, Wn); // If c=1 this is EM update.
		// Otherwise GEM update.
	}

	/**
	 * If an entry of Z is zero, normal weight-updating is broken down. This do
	 * not occur theoretically but occur actually, due to the underflow in the
	 * calculation of Z. In such a case, the associating column of P is
	 * calculated by hard competition and the entry of Z is set to 1.
	 */
	void dealZeroZ(Matrix z, Matrix P, Matrix D) {
		int i, j, imin;

		for (j = 0; j < P.col; j++) {
			if (z.value[0][j] <= 0.) {
				P.value[0][j] = 1;
				imin = 0;

				for (i = 1; i < P.row; i++) {
					if (D.value[i][j] < D.value[imin][j]) {
						P.value[imin][j] = 0;
						P.value[i][j] = 1;
						imin = i;
					} else
						P.value[i][j] = 0;
				}
				z.value[0][j] = 1;
			}
		}
	}

	/**
	 * Calculate Number of Good Parameters.
	 */

	public double ngp(int n, double alpha, double beta, Matrix lmd) {
		int i;
		double gamma = 0;
		double betad = beta * n / row;

		for (i = 0; i < row; i++) {
			gamma += betad / (betad + alpha * lmd.value[0][i]);
		}

		gamma -= 2;
		return gamma;
	}

	public double updateAlpha(Matrix D, double gamma) {
		double alpha;

		Matrix R = D.multipliedBy(this);
		alpha = (gamma * col) / R.sumSqrEntries();
		return alpha;
	}

	public double updateBeta(int n, double gamma) {
		double beta;

		Matrix PD = P.mulipliedEntriesWith(Ds);
		beta = ((n - gamma - 2) * col) / PD.sumEntries();
		return beta;
	}

	public Matrix makeDensity(Matrix pos, double beta) {
		int k;
		Matrix Ds, P, Z, density;

		Ds = this.crossSqDistance(pos);
		P = Ds.multipliedBy(-beta / 2.);
		P.updateExp();
		Z = P.verticalSum();

		double zmax = 0;
		for (k = 0; k < Z.col; k++) {
			if (Z.value[0][k] > zmax) {
				zmax = Z.value[0][k];
			}
		}

		density = Z.multipliedBy(1. / zmax);
		return density;
	}

}

/**
 * A DrawArea is an area where data and weight points are plotted. This also
 * makes a thread for learning.
 */
class DrawArea extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BsomDemo demo;
	int xd, yd;
	double scale;
	double delta = 0.000001;
	int ntot = 0;

	int n_data, n_unit; // Number of points
	double alpha, beta; // Hyperparameters
	double width, height, noise_level, phase; // Parameters for data
	// distribution
	double initial_weight_level;

	int dstep;
	boolean density_mode = false;

	Data X; // Data
	Weight W; // Weights (centroid parameters)
	Matrix D; // Discrete Laplacian matrix
	Matrix M; // Hesse matrix of log prior (M = D'D)
	Matrix lmd; // Eigenvalues of M

	Thread thread;
	int wait_time = 100;

	boolean learn = false;
	boolean auto_learn = false;
	int grabed_unit = -1;

	/**
	 * Construct a DrawArea on a demonstration applet.
	 * 
	 * @param demo
	 *            Demonstration applet
	 */
	public DrawArea(BsomDemo demo, int xd, int yd, double scale) {
		this.demo = demo;
		this.xd = xd;
		this.yd = yd;
		this.scale = scale;
	}

	/**
	 * Initialize learning by making data (X), weights (W) and Hesse matrix of
	 * prior (M).
	 */
	public void init() {
		initWeight();
		initData();

	}

	void initWeight() {
		W = new Weight(n_unit, scale, 2, xd, yd, initial_weight_level);
		makePriorHessian(n_unit);
	}

	void initData() {
		X = new Data(n_data, scale, 0, xd, yd, width, height, phase,
				noise_level);
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread.stop();
	}

	// run without thread
	public void runNoThread() {

		double ini_gam = 0;
		grabed_unit = -1;
		ntot = 0;
		for (;;) {
			// System.out.println("learn="+Boolean.toString(learn));
			// System.out.println("auto="+Boolean.toString(auto_learn));
			if (learn) {
				W.update(X, M, alpha, beta, grabed_unit);

				if (auto_learn) {
					double gamma = W.ngp(n_data, alpha, beta, lmd);
					alpha = W.updateAlpha(D, gamma);
					beta = W.updateBeta(n_data, gamma);
					// System.out.println("gamma="+Double.toString(gamma));
					// System.out.println("alpha="+Double.toString(alpha));
					// System.out.println("beta="+Double.toString(beta));
					if (Math.abs(gamma - ini_gam) < delta)
						break;
					ini_gam = gamma;
				}
			}
			ntot++;
			// if (i>max) break;

		}
		// System.out.println("Total iterations="+Integer.toString(i));

	}

	/**
	 * Learn and plot if 'learn' is true. If 'auto_learn' is true,
	 * hyperparameters are tuned.
	 */

	public void run() {

		ntot = 0;

		try {
			for (;;) {
				thread.sleep(wait_time);

				if (learn) {
					W.update(X, M, alpha, beta, grabed_unit);

					if (auto_learn) {
						double gamma = W.ngp(n_data, alpha, beta, lmd);

						alpha = W.updateAlpha(D, gamma);
						beta = W.updateBeta(n_data, gamma);

						alpha = demo.alphaSlider.setValueOfSlider(alpha);
						beta = demo.betaSlider.setValueOfSlider(beta);
						// System.out.println("gamma="+Double.toString(gamma));
						// System.out.println("alpha="+Double.toString(alpha));
						// System.out.println("beta="+Double.toString(beta));

					}
				}
				repaint();
				ntot++;
				// if (i>ntot) break;
			}
		} catch (InterruptedException e) {
		}

		// System.out.println("Total iterations="+Integer.toString(i));

	}

	/* Double-buffering is used for drawing objects. */

	Graphics offgraphics;
	Dimension offscreensize;
	Image offscreen;

	public synchronized void update(Graphics g) {

		Dimension d = size();

		if ((offscreen == null) || (d.width != offscreensize.width)
				|| (d.height != offscreensize.height)) {
			offscreen = createImage(d.width, d.height);
			offscreensize = d;
			offgraphics = offscreen.getGraphics();
		}

		if (!density_mode) {
			offgraphics.setColor(getBackground());
			offgraphics.fillRect(0, 0, d.width, d.height);
			offgraphics.setColor(Color.black);

		} else {
			g.drawString("Now, density is calculated.", 0, 10);
			drawDensity(offgraphics);
			offgraphics.setColor(Color.red);

			offgraphics.drawString("a=" + alpha, 0, 10);
			offgraphics.drawString("b=" + beta, 0, 20);
			offgraphics.setColor(Color.green);
		}

		X.draw(offgraphics);
		offgraphics.setColor(Color.blue);
		W.draw(offgraphics);
		W.drawCurve(offgraphics);
		paint(g);

	}

	public void paint(Graphics g) {
		if (offscreen != null)
			g.drawImage(offscreen, 0, 0, null);
	}

	/*
	 * A weight-point can be moved directly by mouse.
	 */

	public synchronized boolean mouseDown(Event evt, int ix, int iy) {
		grabed_unit = W.index(ix, iy);
		return true;
	}

	public synchronized boolean mouseDrag(Event evt, int ix, int iy) {
		if (grabed_unit >= 0) {
			W.move(grabed_unit, ix, iy);
			return true;
		}
		return false;
	}

	public synchronized boolean mouseUp(Event evt, int ix, int iy) {
		grabed_unit = -1;
		return true;
	}

	void makePriorHessian(int r) {
		int i, j;
		int rep_limit = 1000;
		double eps = 0.00001;

		// Make a discrete Laplacian matrix.

		D = new Matrix(r - 2, r);

		for (i = 0; i < r - 2; i++) {
			for (j = 0; j < r; j++) {
				if (j == i + 1)
					D.value[i][j] = -2;
				if (j == i || j == i + 2)
					D.value[i][j] = 1;
			}
		}

		Matrix Dt = D.transpose();
		M = Dt.multipliedBy(D); // M = D'*D

		lmd = M.eigenvalues(eps, rep_limit);

		// Fix the two smallest eigenvalues to zeros.

		double lmd_min1, lmd_min2, lmd_max;
		int i1 = 0, i2 = 0;

		lmd_min1 = lmd_max = lmd.value[0][0];
		for (i = 1; i < r; i++) {
			if (lmd.value[0][i] < lmd_min1) {
				lmd_min1 = lmd.value[0][i];
				i1 = i;
			}
			if (lmd.value[0][i] > lmd_max) {
				lmd_max = lmd.value[0][i];
			}
		}
		lmd.value[0][i1] = 0.;

		lmd_min2 = lmd_max;
		for (i = 0; i < r; i++) {
			if (lmd.value[0][i] > 0. && lmd.value[0][i] < lmd_min2) {
				lmd_min2 = lmd.value[0][i];
				i2 = i;
			}
		}
		lmd.value[0][i2] = 0.;

	}

	public void drawDensity(Graphics g) {
		int i, j;
		float c;
		int k;
		double x, y;

		Dimension s = this.size();
		int nx = s.width / dstep;
		int ny = s.height / dstep;

		Matrix pos = new Matrix(nx * ny, 2);

		k = 0;
		for (i = 0; i < nx; i++) {
			for (j = 0; j < ny; j++) {
				x = (i + 0.5) * dstep;
				y = (j + 0.5) * dstep;
				pos.value[k][0] = (double) ((x - xd) / scale);
				pos.value[k][1] = (double) (-(y - yd) / scale);
				k++;
			}
		}

		Matrix density = W.makeDensity(pos, beta);

		k = 0;
		for (i = 0; i < nx; i++) {
			for (j = 0; j < ny; j++) {
				c = (float) (density.value[0][k]);
				g.setColor(new Color(c, c, c));
				g.fillRect(i * dstep, j * dstep, dstep, dstep);
				k++;
			}
		}
	}

}

/*
 * SLIDERS
 */

/**
 * Slider for setting hyperparameter. This slider is on log scale. Also, while
 * dragging auto-learn is suppressed.
 */
class HpSlider extends JSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final double LOG10 = Math.log(10.);
	protected DrawArea drawArea;
	protected double min_power, max_power;
	protected boolean stored_mode;
	protected boolean dragged = false;
	private int value;
	private double range;

	HpSlider(DrawArea p, double min_power, double max_power) {
		super();
		setMajorTickSpacing(20);
		// setMinorTickSpacing(4);
		setPaintTicks(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0"));
		labelTable.put(new Integer(20), new JLabel("1"));
		labelTable.put(new Integer(40), new JLabel("2"));
		labelTable.put(new Integer(60), new JLabel("3"));
		labelTable.put(new Integer(80), new JLabel("4"));
		labelTable.put(new Integer(100), new JLabel("5"));
		setLabelTable(labelTable);
		setPaintLabels(true);

		drawArea = p;
		this.min_power = min_power;
		this.max_power = max_power;
		range = max_power - min_power;

		// Register a change listener
		addChangeListener(new ChangeListener() {
			// This method is called whenever the slider's value is changed
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				if (!slider.getValueIsAdjusting()) {
					// Get new value
					value = slider.getValue();
					stored_mode = drawArea.auto_learn;
					drawArea.auto_learn = false;
					dragged = true;
				}
			}
		});

		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {

				stored_mode = drawArea.auto_learn;
				drawArea.auto_learn = false;
				dragged = true;

			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {

				value = getValue();
				drawArea.auto_learn = stored_mode;
				dragged = false;
				setValueBySlider();
				// System.out.println(value);
			}

			public void mousePressed(MouseEvent e) {

			}
		});

	}

	public void Motion() {
		if (!dragged) {
			stored_mode = drawArea.auto_learn;
			drawArea.auto_learn = false;
		}
		dragged = true;
		setValueBySlider();
	}

	public void Release() {
		setValueBySlider();
		drawArea.auto_learn = stored_mode;
		dragged = false;
	}

	public void setValueBySlider() {
	}

	double getValueFromSlider() {
		double power = value / 100. * range + min_power;
		double v = Math.pow(10., power);
		return v;
	}

	public double setValueOfSlider(double x) {
		int v = (int) ((Math.log(x) / LOG10 - min_power) / range * 100);

		if (v < 1) {
			v = 1;
			x = Math.pow(10., 1. / 100. * range + min_power);
		} else if (v > 100) {
			v = 100;
			x = Math.pow(10., 1. * range + min_power);
		}

		setValue(v);
		return x;
	}
}

/**
 * Slider for setting alpha
 */
class AlphaSlider extends HpSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	AlphaSlider(DrawArea p, double min_power, double max_power) {
		super(p, min_power, max_power);
	}

	public void setValueBySlider() {
		drawArea.alpha = getValueFromSlider();
	}
}

/**
 * Slider for setting beta.
 */
class BetaSlider extends HpSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BetaSlider(DrawArea p, double min_power, double max_power) {
		super(p, min_power, max_power);
	}

	public void setValueBySlider() {
		drawArea.beta = getValueFromSlider();
	}
}

/**
 * Slider for setting data parameters
 */
class DataSlider extends JSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DrawArea drawArea;
	double range;

	DataSlider(DrawArea p, double range) {
		super();

		drawArea = p;
		this.range = range;
	}

	public void Motion() {
		setValueBySlider();
		drawArea.X.remake();
	}

	public void Release() {
		setValueBySlider();
		drawArea.X.remake();
	}

	void setValueBySlider() {
	}

	double getValueFromSlider() {
		return getValue() / 100. * range;
	}

	public double setValueOfSlider(double x) {
		int v = (int) (x / range * 100);

		if (v < 1) {
			v = 1;
			x = 1. / 100. * range;
		} else if (v > 100) {
			v = 100;
			x = range;
		}

		setValue(v);
		return x;
	}
}

/**
 * Slider for setting the width of data range
 */
class WidthSlider extends DataSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	WidthSlider(DrawArea p, double range) {
		super(p, range);
	}

	void setValueBySlider() {
		drawArea.X.width = getValueFromSlider();
	}
}

/**
 * Slider for setting the height of data range
 */
class HeightSlider extends DataSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HeightSlider(DrawArea p, double range) {
		super(p, range);
	}

	void setValueBySlider() {
		drawArea.X.height = getValueFromSlider();
	}

}

/**
 * Slider for setting the phase of sin data
 */
class PhaseSlider extends DataSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PhaseSlider(DrawArea p, double range) {
		super(p, range);
	}

	void setValueBySlider() {
		drawArea.X.phase = getValueFromSlider();
	}
}

/**
 * Slider for noise level
 */
class NoiseSlider extends DataSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NoiseSlider(DrawArea p, double range) {
		super(p, range);
	}

	void setValueBySlider() {
		drawArea.X.noise_level = getValueFromSlider();
	}
}
