package jhplot.bsom;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Math;
import java.lang.Double;
import java.util.Vector;

import javax.swing.*;

import jhplot.P1D;

/**
 * Bsom is an application of Bayesian Self-Organizing Maps.
 * 
 * @author Akio Utsugi and Sergei Chekanov
 */
public class Bsom extends BsomDemo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BsomDrawArea bsomDrawArea;

	private JTextField inputText;
	private JTextField dstepText;
	private String input_file;
	private JButton outputButton;
	private P1D p1d = null;
	private Vector<Double> buf;
	static int INIT = 0;
	static double[][] value;

	public int getNiterations() {
		return bsomDrawArea.ntot;
	}

	public double getAlpha() {
		return bsomDrawArea.alpha;
	}

	public double getBeta() {
		return bsomDrawArea.beta;
	}

	/**
	 * 
	 * 
	 * initialize learning
	 * */

	public void initPar(double alpha, double beta, int units) {

		INIT = 0;
		n_data = 100;
		n_unit = units;
		alpha_min_power = 0.0;
		alpha_max_power = 5.0;
		beta_min_power = 0.0;
		beta_max_power = 5.0;

		xd = 250;
		yd = 175;
		scale = 80;
		dstep = 10;
		initial_weight_level = 0.1;

		input_file = "p1d";

		initial_alpha = alpha;
		initial_beta = beta;

		if (buf != null) {
			n_data = buf.size() / 2;
		} else {
			showStatus("Input is data are required.");
			stop();
		}

		// Draw area
		bsomDrawArea = new BsomDrawArea(this, xd, yd, scale);
		bsomDrawArea.n_data = n_data;
		bsomDrawArea.n_unit = n_unit;
		bsomDrawArea.dstep = dstep;
		bsomDrawArea.initial_weight_level = initial_weight_level;
		bsomDrawArea.alpha = initial_alpha;
		bsomDrawArea.beta = initial_beta;
		bsomDrawArea.buf = buf;

		// System.out.println(outputWeightP1D().toString());

		// System.out.println( drawArea.W.row );
		// System.out.println( drawArea.W.col );

		/*
		 * for (int i = 0; i < p1d.size(); i++) {
		 * drawArea.W.value[i][0]=p1d.getX(i);
		 * drawArea.W.value[i][1]=p1d.getY(i); }
		 */

		// Obtain means.
		P1D init = p1d.copy();
		int n = p1d.size();

		if (units == n) {
			INIT = 1;
			double mx = init.meanX();
			double my = init.meanY();
			value = new double[n][2];
			for (int i = 0; i < n; i++) {
				value[i][0] = init.getX(i);
				value[i][1] = init.getY(i);
			}

			// Move the means to zeros and then get the range.
			double dmax = 0;

			for (int i = 0; i < n; i++) {
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
			double dscale = 2. / dmax;
			// System.out.println(dscale);
			for (int i = 0; i < n; i++) {
				value[i][0] *= dscale;
				value[i][1] *= dscale;
			}

		};
		 // end the case

		bsomDrawArea.init();

		UserData UX = (UserData) bsomDrawArea.X;
		double dscale = UX.dscale;
		// System.out.println(dscale);

		drawArea = bsomDrawArea;
		drawArea.W.init();

		/*
		 * for (int i=0; i<drawArea.W.row; i++) { for (int j=0;
		 * j<drawArea.W.col; j++) {
		 * System.out.print(Double.toString(drawArea.W.value[i][j])+" "); }
		 * System.out.print("\n"); }
		 */

	}

	public void init(int units) {
		// String p;

		alpha_min_power = 0.0;
		alpha_max_power = 5.0;
		beta_min_power = 0.0;
		beta_max_power = 5.0;
		initial_alpha = Math.pow(10., alpha_max_power);
		initial_beta = Math.pow(10., beta_min_power);
		initPar(initial_alpha, initial_beta,units);
		if (p1d != null)
			input_file = p1d.getTitle();

		/* Layout */
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		// Draw area
		bsomDrawArea.setBackground(Color.white);
		bsomDrawArea.setSize(new Dimension(600, 400));
		drawArea = bsomDrawArea;
		add("Center", bsomDrawArea);

		// Control panel
		JPanel controlPanel = new JPanel();

		// Button panel
		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new GridLayout(4, 1));

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

				drawArea.W.init();
				if (drawArea.auto_learn) {
					alphaSlider.setValueOfSlider(initial_alpha);
					betaSlider.setValueOfSlider(initial_beta);
					drawArea.alpha = initial_alpha;
					drawArea.beta = initial_beta;
				}
			}
		});

		buttonPanel.add(learnButton);
		buttonPanel.add(autoButton);
		buttonPanel.add(densityButton);
		buttonPanel.add(initButton);

		controlPanel.add(buttonPanel);

		// Weight panel
		JPanel weightPanel = new JPanel();
		weightPanel.setLayout(new GridLayout(2, 1));

		// Alpha slider panel
		JPanel alphaSliderPanel = new JPanel();
		alphaSliderPanel.setLayout(new BorderLayout());
		alphaSlider = new AlphaSlider(bsomDrawArea, alpha_min_power,
				alpha_max_power);
		JLabel alabel = new JLabel("alpha (log10)");
		alphaSliderPanel.add("Center", alphaSlider);
		alphaSliderPanel.add("South", alabel);
		weightPanel.add(alphaSliderPanel);

		// Beta slider panel
		JPanel betaSliderPanel = new JPanel();
		betaSliderPanel.setLayout(new BorderLayout());
		betaSlider = new BetaSlider(bsomDrawArea, beta_min_power,
				beta_max_power);
		JLabel blabel = new JLabel("beta (log10)");
		betaSliderPanel.add("Center", betaSlider);
		betaSliderPanel.add("South", blabel);
		weightPanel.add(betaSliderPanel);

		controlPanel.add(weightPanel);

		// IO Panel
		JPanel IOPanel = new JPanel();
		IOPanel.setLayout(new GridLayout(4, 1));

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 2));
		JLabel inputLabel = new JLabel("Input:");
		inputText = new JTextField(input_file, 10);
		inputText.setEditable(false);
		inputPanel.add(inputLabel);
		inputPanel.add(inputText);
		IOPanel.add(inputPanel);

		JPanel unitNumberPanel = new JPanel();
		unitNumberPanel.setLayout(new GridLayout(1, 2));
		unitNumberText = new JTextField("" + n_unit, 10);
		// unitNumberText.setEditable(false);
		unitNumberText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				n_unit = Integer.parseInt(unitNumberText.getText());
				resetUnitNumber(n_unit);
			}
		});

		JLabel unitNumberLabel = new JLabel("units");
		unitNumberPanel.add(unitNumberLabel);
		unitNumberPanel.add(unitNumberText);
		IOPanel.add(unitNumberPanel);

		/*
		 * JPanel dstepPanel = new JPanel(); dstepPanel.setLayout(new
		 * GridLayout(1, 2)); dstepText = new JTextField(""+dstep, 20);
		 * 
		 * dstepText.addActionListener( new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { dstep =
		 * Integer.parseInt(dstepText.getText()); ; bsomDrawArea.dstep = dstep;
		 * } } );
		 */

		/*
		 * JLabel dstepLabel = new JLabel("dstep"); dstepPanel.add(dstepLabel);
		 * dstepPanel.add(dstepText); IOPanel.add(dstepPanel);
		 */

		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new GridLayout(1, 2));
		outputButton = new JButton("generate");
		outputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				outputWeight();
			}
		});

		JLabel dmy = new JLabel("Output: ");
		outputPanel.add(dmy);
		outputPanel.add(outputButton);
		IOPanel.add(outputPanel);

		controlPanel.add(IOPanel);

		add("South", controlPanel);

		// Set initial values of sliders.
		alphaSlider.setValueOfSlider(initial_alpha);
		betaSlider.setValueOfSlider(initial_beta);

		bsomDrawArea.init();

		repaint();

	}

	public void start() {

		if (!bsomDrawArea.density_mode) {
			bsomDrawArea.start();
		}
	}

	public void stop() {
		bsomDrawArea.stop();
	}

	public synchronized boolean action(Event evt, Object what) {
		if (super.action(evt, what))
			return true;

		if (evt.target == inputText) {
			reloadData((String) what);
			return true;
		}

		if (evt.target == dstepText) {
			dstep = Integer.valueOf((String) what).intValue();
			bsomDrawArea.dstep = dstep;
			return true;
		}

		if (evt.target == outputButton) {
			outputWeight();
			return true;
		}
		return false;
	}

	public void setDelta(double delta) {
		bsomDrawArea.delta = delta;
	}

	public void loadData(P1D p1d) {

		this.buf = new Vector<Double>();
		this.p1d = p1d;
		for (int i = 0; i < p1d.size(); i++) {
			buf.addElement(new Double(p1d.getX(i)));
			buf.addElement(new Double(p1d.getY(i)));
			// System.out.println(p1d.getX(i));
			// System.out.println(p1d.getY(i));
		}
	}

	public void loadData(String input_file) {

		this.buf = new Vector<Double>();
		this.p1d = new P1D(input_file);
		for (int i = 0; i < p1d.size(); i++) {
			buf.addElement(new Double(p1d.getX(i)));
			buf.addElement(new Double(p1d.getY(i)));
		}

	}

	public void reloadData(String input_file) {

		if (p1d != null)
			loadData(p1d);

		bsomDrawArea.alpha = initial_alpha;
		bsomDrawArea.beta = initial_beta;
		bsomDrawArea.buf = buf;
		bsomDrawArea.auto_learn = false;
		bsomDrawArea.learn = false;
		bsomDrawArea.initData();
		bsomDrawArea.W.init();
		bsomDrawArea.draw_new_data = true;

		autoButton.setSelected(false);
		learnButton.setSelected(false);
		alphaSlider.setValueOfSlider(initial_alpha);
		betaSlider.setValueOfSlider(initial_beta);
		repaint();
	}

	public void outputWeight() {
		int i;
		double x, y;
		Weight W = bsomDrawArea.W;
		UserData UX = (UserData) bsomDrawArea.X;
		double dscale = UX.dscale;
		double mx = UX.mx;
		double my = UX.my;

		for (i = 0; i < W.row; i++) {
			x = W.value[i][0] / dscale + mx;
			y = W.value[i][1] / dscale + my;
			System.out.println(x + " " + y);
		}
		System.out.println(" ");
	}

	/**
	 * Return results
	 * 
	 * @return P1D with results
	 */
	public P1D outputWeightP1D() {
		int i;
		double x, y;
		Weight W = bsomDrawArea.W;
		UserData UX = (UserData) bsomDrawArea.X;
		double dscale = UX.dscale;
		double mx = UX.mx;
		double my = UX.my;

		P1D p1d = new P1D("MSOM results");
		for (i = 0; i < W.row; i++) {
			x = W.value[i][0] / dscale + mx;
			y = W.value[i][1] / dscale + my;
			p1d.add(x, y);
		}
		return p1d;
	}

	/**
	 * * Generate error message * * @param a * Message
	 * */
	private void showStatus(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}






}
