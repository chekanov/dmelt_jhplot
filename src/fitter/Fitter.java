// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package fitter;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import jhplot.*;
import jhplot.fit.*;
import jhplot.utils.*;
import jhplot.utils.Util;
import hep.aida.*;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.*;

public class Fitter {

	private static String[] labels;

	private String[] tooltip;

	private String[] items;

	private String[] definition, param;

	private int[] dimention;

	private ArrayList<BFunc> afunc;

	private String[] engines;

	private DefaultListModel model;

	private JList jlist;

	private JFrame frame;

	private H1D h1;

	private P1D p1;

	private boolean showFrame = false;

	final JTextPane textArea;

	final JTextField funcText;

	private StyledDocument doc;

	private String mess = "";

	private Style style = null;

	final private String br = "<br>";

	final private HPlot hplot;

	final private Font font = new Font("sansserif", Font.BOLD, 10);

	final private Font fontB = new Font("sansserif", Font.BOLD, 12);

	// Fields for data entry
	private JFormattedTextField minRange;

	private JFormattedTextField maxRange;

	// Formats to format and parse numbers
	private int[] constraint = new int[50];

	private JComboBox method, engine;

	private IAnalysisFactory anFactory;

	private ITree tree;

	private IFunctionFactory funcFactory;

	private IFitFactory fitFactory;

	private IFitter fitter;

	private IFunctionCatalog funcCatalog;

	private IFitResult fitResult;

	private IFunction fresult;

	private IFitData data;

	private SEditor se;

	private double rmin, rmax;

	private String shplot = "c1";

	private String sdata = "h1";

	// fit results
	private double[] fPars;

	private double[] fParErrs;

	private String[] fParNames;

	private F1D f2 = null;

	private java.util.ArrayList<BMark> bSetInit;

	private java.util.ArrayList<BMark> bSetResult; // from previous

	// current function
	private IFunction func;

	private static String sfunc = "";

	private String codeFunc = "";

	private static boolean firstFit, firstShow;

	public static void main(String args[]) {
		new Fitter(null, "c1");
	}

	public void iniData(H1D h1, String name) {

		this.h1 = h1;
		this.sdata = name;
		if (h1 != null && textArea != null) {

			rmin = h1.getMin();
			rmax = h1.getMax();

			maxRange.setValue(new Double(rmax));
			minRange.setValue(new Double(rmin));

			mess = "Histogram <b>" + h1.getTitle() + "</b> is loaded<br>";
			mess = mess + "Entries <b>" + Integer.toString(h1.entries())
					+ "</b> is loaded<p>";
			setHtml(mess);
		}

	}

	public void iniData(P1D p1, String name) {

		this.p1 = p1;
		this.sdata = name;

		if (p1 != null && textArea != null) {

			rmin = p1.getMin(0);
			rmax = p1.getMax(0);

			maxRange.setValue(new Double(rmax));
			minRange.setValue(new Double(rmin));

			mess = "P1D data <b>" + p1.getTitle() + "</b> are loaded<br>";
			mess = mess + "Entries <b>" + Integer.toString(p1.size())
					+ "</b> is loaded<p>";
			setHtml(mess);
		}

	}

	/**
	 * @param hplot
	 *            HPlot object
	 * @param name
	 *            String representing the data object
	 * 
	 **/
	public Fitter(HPlot hplot, String name) {

		this.shplot = name;
		this.hplot = hplot;
		showFrame = false;
		firstFit = true;
		firstShow = true;

		afunc = new ArrayList<BFunc>();
		afunc.add(new BFunc("P0", "p0"));
		afunc.add(new BFunc("P1", "p0+p1*x"));
		afunc.add(new BFunc("P2", "p0+p1*x+p2*x^2"));
		afunc.add(new BFunc("P3", "p0+p1*x+p2*x^2+p3*x^3"));
		afunc.add(new BFunc("P4", "polynom^4"));
		afunc.add(new BFunc("P5", "polynom^5"));
		afunc.add(new BFunc("P6", "polynom^6"));
		afunc.add(new BFunc("Gauss", "p0*exp(arg*arg/2); arg=(x-p1)/p2"));
		afunc.add(new BFunc("BreitWigner",
				"p0*bw/(2*PI); bw=p2/((x-p1)*(x-p1)+p2*p2/4)"));

		afunc.add(new BFunc("Exponent", "p0*exp^(p1*(x-p2))"));
		afunc.add(new BFunc("Landau", "Landau (Moyal formula)"));
		afunc.add(new BFunc("Pow", "p0*(p1-x)^p2"));

		// build predefined functions
		Collections.sort(afunc);
		labels = new String[afunc.size()];
		tooltip = new String[afunc.size()];
		definition = new String[afunc.size()];
		param = new String[afunc.size()];
		dimention = new int[afunc.size()];

		for (int i = 0; i < afunc.size(); i++) {
			BFunc a = afunc.get(i);
			labels[i] = a.getName();
			tooltip[i] = a.getTip();
			definition[i] = a.getDefinition();
			param[i] = a.getParam();
			dimention[i] = a.getDim();
		}

		model = new DefaultListModel();
		for (int i = 0, n = labels.length; i < n; i++) {
			model.addElement(labels[i]);
		}

		jlist = new JList(model) {

			public static final long serialVersionUID = 126;

			public String getToolTipText(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (-1 < index) {
					String item = tooltip[index]; // (String)getModel().getElementAt(index);
					return item;
				} else {
					return null;
				}
			}
		};

		// create all aida staff
		anFactory = IAnalysisFactory.create();
		tree = anFactory.createTreeFactory().create();
		funcFactory = anFactory.createFunctionFactory(tree);
		fitFactory = anFactory.createFitFactory();
		fitter = fitFactory.createFitter("Chi2", "jminuit", "noClone=\"true\"");
		data = fitFactory.createFitData();
		funcCatalog = funcFactory.catalog();

		// function settings
		bSetInit = new java.util.ArrayList<BMark>();
		bSetResult = new java.util.ArrayList<BMark>();

		items = fitFactory.availableFitMethods();
		engines = fitFactory.availableFitEngines();

		frame = new JFrame("Fit Panel");
		frame.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				frame.setVisible(false);
				System.gc();
				frame.dispose();
			}
		});

		// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		funcText = new JTextField("select a function and press \"add\"");
		funcText.setFont(fontB);
		funcText.setEditable(false);

		se = new SEditor(frame, this);
		Container contentPane = frame.getContentPane();

		JScrollPane scrollPane1 = new JScrollPane(jlist);
		contentPane.add(scrollPane1, BorderLayout.WEST);

		HTMLEditorKit editorKit = new HTMLEditorKit();
		HTMLDocument htmlDoc = (HTMLDocument) editorKit.createDefaultDocument();

		// minRange = new JFormattedTextField( new DecimalFormat("#.###E00") );
		// maxRange = new JFormattedTextField( new DecimalFormat("#.###E00") );
		minRange = new JFormattedTextField();
		maxRange = new JFormattedTextField();

		minRange.setToolTipText("min value");
		maxRange.setToolTipText("max value");

		minRange.setInputVerifier(new InputVerifier() {
			public boolean verify(JComponent input) {
				if (!(input instanceof JFormattedTextField))
					return true;
				return ((JFormattedTextField) input).isEditValid();
			}
		});

		maxRange.setInputVerifier(new InputVerifier() {
			public boolean verify(JComponent input) {
				if (!(input instanceof JFormattedTextField))
					return true;
				return ((JFormattedTextField) input).isEditValid();
			}
		});

		minRange.setFont(fontB);
		maxRange.setFont(fontB);
		minRange.setColumns(6);
		maxRange.setColumns(6);
		maxRange.setValue(new Double(0));
		minRange.setValue(new Double(0));

		textArea = new JTextPane();
		textArea.setEditable(false);
		textArea.setEditorKit(editorKit);
		textArea.setContentType("text/html");
		textArea.setDocument(htmlDoc);
		doc = (StyledDocument) textArea.getDocument();
		style = doc.getStyle("text/html");
		// textArea.setText("<html>");
		// textArea.setCaretPosition(0);

		JScrollPane scrollPane2 = new JScrollPane(textArea);
		contentPane.add(scrollPane2, BorderLayout.CENTER);

		method = new JComboBox(items);
		method.setSelectedIndex(3);
		method.setPreferredSize(new java.awt.Dimension(115, 24));
		method.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox combo = (JComboBox) e.getSource();
				int nn = combo.getSelectedIndex();
				String s = items[nn];
				fitter.setFitMethod(s);
				codeFunc = codeFunc + "fitter.setFitMethod(\"" + s + "\")\n";
			}
		});

		engine = new JComboBox(engines);
		if (engines.length > 1) {
			engine.setSelectedIndex(1);
		} else {
			engine.setSelectedIndex(0);
		}
		engine.setPreferredSize(new java.awt.Dimension(100, 24));
		engine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox combo = (JComboBox) e.getSource();
				int nn = combo.getSelectedIndex();
				String s = engines[nn];
				fitter.setEngine(s);
				codeFunc = codeFunc + "fitter.setEngine(\"" + s + "\")\n";
			}
		});

		// Setup buttons
		JPanel jpTOP = new JPanel();
		jpTOP.setLayout(new BorderLayout());
		JPanel jpRIGHT = new JPanel();

		JPanel jp = new JPanel(new GridLayout(2, 1));
		JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
		JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
		jp.add(jp1);
		jp.add(jp2);

		JButton jb = new JButton("add");
		jp1.add(jb);
		jp1.add(method);
		jp1.add(engine);

		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				// Get the first selected item
				Object firstSel = jlist.getSelectedValue();
				int indx = jlist.getSelectedIndex();
				if (firstSel == null)
					return;
				if (indx < 0)
					return;

				final int Index = indx;
				Thread t = new Thread("build function thread") {
					public void run() {
						// build function
						buildFunc(Index);

					};
				};
				t.start();

			}
		});

		jb = new JButton("Settings");
		jp2.add(jb);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				if (func != null) {
					se.fillTable();
					se.showIt();
				}

			}
		});

		jb = new JButton("AIDA code");
		jb.setToolTipText("Generate and insert Jython code");
		jp1.add(jb);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				String tmp = makeCode();
				showOutput(tmp);

			}
		});

		jb = new JButton("Output");
		jb.setToolTipText("Generate and insert output");
		jp1.add(jb);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				String tmp = makeOutput();
				showOutput(tmp);
			}
		});

		jb = new JButton("Fit!");
		jb.setToolTipText("Performe fit");
		jp2.add(jb);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				Thread t = new Thread("do fit thread") {
					public void run() {
						doFit(func, sfunc);
					};
				};
				t.start();

			}
		});

		jb = new JButton("Best guess");
		jp2.add(jb);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				mess = "";
				setHtml(mess);
				ErrorMessage("Not implemented");
			}
		});

		jb = new JButton("Clear");
		jp2.add(jb);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				clearFit();
			}
		});

		jb = new JButton("Exit");
		jp2.add(jb);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				int res = JOptionPane.showConfirmDialog(frame,
						"Do you want to exit?", "", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.NO_OPTION)
					return;

				frame.setVisible(false);
				System.gc();
				frame.dispose();

			}
		});

		jpTOP.add(funcText, BorderLayout.CENTER);
		jpRIGHT.add(minRange);
		jpRIGHT.add(maxRange);
		jpTOP.add(jpRIGHT, BorderLayout.EAST);

		contentPane.add(jpTOP, BorderLayout.NORTH);
		contentPane.add(jp, BorderLayout.SOUTH);
		// frame.setSize(550, 300);

	}

	// show GUI
	public void showIt() {

		showFrame = true;
		Util.rightWithin(hplot.getFrame(), frame);
		frame.pack();
		frame.setVisible(true);

	}

	public void setHtml(String html) {
		textArea.setText("<html>" + html + "</html>");
		textArea.setCaretPosition(doc.getLength());
	}

	// automatic initialisation when a function is adeed added
	// it fill an arraw with the settings
	public void iniFunc() {

		if (func == null)
			return;

		double maxBinHeight = 1;
		double mean = 0.0;
		double rms = 1.0;

		if (h1 != null) {
			maxBinHeight = h1.maxBinHeight();
			mean = h1.mean();
			rms = h1.rms();
		}

		if (p1 != null) {
			maxBinHeight = p1.getMax(1);
			mean = p1.meanX();
			rms = 1.0;
		}

		if (sfunc.indexOf("Gauss") > -1) {
			func.setParameter("norm", maxBinHeight);
			func.setParameter("mean", mean);
			func.setParameter("sigma", rms);
		} else if (sfunc.indexOf("BreitWigner") > -1) {
			func.setParameter("norm", maxBinHeight);
			func.setParameter("mean", mean);
			func.setParameter("gamma", 0.5 * rms);
		} else if (sfunc.indexOf("Exponent") > -1) {
			func.setParameter("pe0", 1.0);
			func.setParameter("pe1", 1.0);
			func.setParameter("pe2", 0.0);
		} else if (sfunc.indexOf("Landau") > -1) {
			func.setParameter("norm", maxBinHeight);
			func.setParameter("peak", mean);
			func.setParameter("sigma", rms);

		}

		// fresh run
		if (bSetResult.size() == 0) {
			bSetInit.clear();
			String[] sname = func.parameterNames();
			double[] par = func.parameters();
			for (int i = 0; i < func.numberOfParameters(); i++) {
				bSetInit.add(new BMark(sname[i], 0, 0, par[i], false,
						constraint[i]));
			}
		} else { // take previus values

			// keep the settings
			bSetInit = bSetResult;
			String[] sname = func.parameterNames();
			double[] par = func.parameters();
			for (int i = bSetResult.size(); i < func.numberOfParameters(); i++) {
				bSetInit.add(new BMark(sname[i], 0, 0, par[i], false, 0));
			}
		}

		// path it to the editor
		se.putSettings(bSetInit);

	} // end parameter

	public void clearFit() {

		mess = "";
		setHtml(mess);
		func = null;
		sfunc = "";
		fitResult = null;
		// hplot.removeLabelsAll();
		// hplot.clearAllData();
		// hplot.RefreshFrame();
		// hplot.draw(h1);
		bSetInit.clear();
		bSetResult.clear();
		funcText.setText("cleared..Add a new function");
		data.reset();

		if (firstFit == false) {
			hplot.clearData(hplot.sizeData() - 1);
			hplot.removeLabel(hplot.numberLabels() - 1);
			hplot.refreshFrame();
			firstFit = true;
		}

		if (firstShow == false) {
			hplot.clearData(hplot.sizeData() - 1);
			hplot.refreshFrame();
			firstShow = true;
		}

		System.gc();

	}

	/**
	 * set range manually
	 * 
	 */

	public void setRange(double rmin, double rmax) {
		this.rmin = rmin;
		this.rmax = rmax;

	}

	/**
	 * Do fit
	 * 
	 * 
	 */

	public void doFit(IFunction func, String sfunc) {

		this.func = func;
		this.sfunc = sfunc;

		// generate final code
		codeFunc = codeFunc
				+ "func = funcFactory.createFunctionByName(\"fit function\",\""
				+ sfunc + "\")\n";

		// get all settings from the editor
		getSettings();

		mess = mess + "Perform fit in the range:" + Double.toString(rmin)
				+ " : " + Double.toString(rmax) + br;

		// fit the data
		data = fitFactory.createFitData();
		codeFunc = codeFunc + "data = fitFactory.createFitData()\n";

		if (h1 != null) {
			data.create1DConnection(h1.get());
			codeFunc = codeFunc + "data.create1DConnection( " + sdata
					+ ".get() )\n";
		}

		if (p1 != null) {
			data.create1DConnection(p1.getIDataPointSet(), 0, 1);
			codeFunc = codeFunc
					+ "data.create1DConnection( p1.getIDataPointSet(),0,"
					+ sdata + ".size() )\n";
		}

		data.range(0).excludeAll();
		data.range(0).include(rmin, rmax);
		fitResult = fitter.fit(data, func);

		codeFunc = codeFunc + "rmin=" + Double.toString(rmin) + "; rmax="
				+ Double.toString(rmax) + "\n";
		codeFunc = codeFunc + "data.range(0).excludeAll()\n";
		codeFunc = codeFunc + "data.range(0).include(rmin,rmax)\n";
		codeFunc = codeFunc + "fitResult = fitter.fit(data,func)\n";

		fPars = fitResult.fittedParameters();
		fParErrs = fitResult.errors();
		fParNames = fitResult.fittedParameterNames();
		String spar = "";
		Format dfb = new DecimalFormat("##.####E00");
		Format dfb0 = new DecimalFormat("#.##E00");

		String[] slabel = new String[fitResult.fittedFunction()
				.numberOfParameters() + 1];

		spar = spar + "Chi2 = " + dfb0.format(fitResult.quality()) + br;
		slabel[0] = "&chi;^{2} / ndf = "
				+ dfb0.format(fitResult.quality() * fitResult.ndf()) + "/"
				+ fitResult.ndf();

		for (int i = 0; i < fitResult.fittedFunction().numberOfParameters(); i++) {

			String a1 = dfb.format(fPars[i]);
			String e1 = dfb.format(fParErrs[i]);
			spar = spar + fParNames[i] + " = " + a1 + "  &plusmn;  " + e1 + br;
			slabel[i + 1] = fParNames[i] + " = " + dfb0.format(fPars[i])
					+ "  &plusmn;  " + dfb0.format(fParErrs[i]);
		}

		fresult = fitResult.fittedFunction();
		codeFunc = codeFunc + "fresult=fitResult.fittedFunction()\n";

		// get the results
		bSetResult.clear();
		String[] sname = fresult.parameterNames();
		double[] par = fresult.parameters();
		for (int i = 0; i < fresult.numberOfParameters(); i++) {
			bSetResult.add(new BMark(sname[i], 0, 0, par[i], false,
					constraint[i]));
		}
		se.putSettings(bSetResult);

		mess = mess + spar + br;
		setHtml(mess);

		if (hplot != null) {
			if (h1 != null || p1 != null) {
				f2 = new F1D(sfunc, fresult, rmin, rmax);
				codeFunc = codeFunc + "f2=F1D(\"" + sfunc
						+ "\", fresult, rmin, rmax)\n";
			}

			f2.setColor(Color.red);
			f2.setPenWidth(2);

			// remove showed function
			if (firstShow == false) {
				hplot.clearData(hplot.sizeData() - 1);
				firstShow = true;
			}

			// remove fit plotted function
			if (firstFit == false) {
				hplot.clearData(hplot.sizeData() - 1);
				hplot.removeLabel(hplot.numberLabels() - 1);
			}

			// add multiline label
			HMLabel lab = new HMLabel(slabel, 0.65, 0.85, "NDC");
			lab.setFont(font);
			hplot.add(lab);

			// draw function
			if (h1 != null)
				hplot.draw(f2, true);
			if (p1 != null)
				hplot.draw(f2, true); // do not start from 0
			codeFunc = codeFunc + shplot + ".draw(f2,1)";
			firstFit = false;
		}

	}

	/**
	 * Generate code
	 */
	private String makeCode() {

		String codeStart = "\n\n# --- code was generated by jHPlot authomatically ---:\n"
				+ "from hep.aida import *\n" + "from jhplot.fit import *\n";

		codeStart = codeStart + "\nanFactory = IAnalysisFactory.create()\n"
				+ "tree        = anFactory.createTreeFactory().create()\n"
				+ "fitFactory  = anFactory.createFitFactory()\n"
				+ "fitter = fitFactory.createFitter(\"Chi2\",\"jminuit\")\n"
				+ "funcFactory = anFactory.createFunctionFactory( tree )\n"
				+ "funcCatalog = funcFactory.catalog()\n";

		String tmp = codeStart + codeFunc;

		tmp = tmp + "\n\n# Fit output:\n";
		tmp = tmp + "print \"fit chi2/ndf =\",fitResult.quality()\n";
		tmp = tmp
				+ "print \"output parameters=\",fitResult.fittedParameterNames()\n";
		tmp = tmp
				+ "print \"output parameter values=\",fitResult.fittedParameters()\n";
		tmp = tmp + "print \"output parameter errors=\",fitResult.errors()\n";
		tmp = tmp + "\n# --- end of authomatically generated code --- \n";

		return tmp;
	}

	/**
	 * Generate output
	 */
	private String makeOutput() {

		String tmp = "\n# fit output\n";

		tmp = tmp + "ndf=" + Integer.toString(fitResult.ndf()) + "\n";
		tmp = tmp + "chi2="
				+ Double.toString(fitResult.ndf() * fitResult.quality()) + "\n";

		// fit parameters
		for (int i = 0; i < fitResult.fittedFunction().numberOfParameters(); i++) {
			tmp = tmp + fParNames[i] + " = " + Double.toString(fPars[i]) + "\n";
			tmp = tmp + fParNames[i] + "_E = " + Double.toString(fParErrs[i])
					+ "\n";
		}

		return tmp;
	}

	/**
	 * Draw a function using current settings
	 * 
	 */
	public void drawFunc() {

		if (firstShow == false) {
			hplot.clearData(hplot.sizeData() - 1);
		}

		// remove fit plotted function
		if (firstFit == false) {
			bSetResult.clear();
			data.reset();
			// hplot.clearData( hplot.sizeData()-1);
			// hplot.removeLabel( hplot.numberLabels()-1);
		}

		getSettings();
		f2 = new F1D(sfunc, func, rmin, rmax);
		// draw function
		if (h1 != null)
			hplot.draw(f2, true);
		if (p1 != null)
			hplot.draw(f2, false);

		firstShow = false;
	}

	/**
	 * Get all settings
	 * 
	 */
	public void getSettings() {

		// get fit range
		if (showFrame) {
			rmin = (Double) minRange.getValue();
			rmax = (Double) maxRange.getValue();
		}

		if (func == null) {
			ErrorMessage("No function selected");
			return;
		}

		if (rmax <= rmin) {
			ErrorMessage("Fit range is wrong");
			return;
		}

		// first get settings
		bSetInit = se.getSettings();
		String[] name1 = func.parameterNames();

		for (int i = 0; i < bSetInit.size(); i++) {
			BMark b = bSetInit.get(i);
			constraint[i] = 0;

			String name = b.getTitle();
			if (b.getMin() < b.getMax()) {
				fitter.fitParameterSettings(name).setBounds(b.getMin(),
						b.getMax());
				codeFunc = codeFunc + "fitter.fitParameterSettings(\"" + name
						+ "\").setBounds(" + Double.toString(b.getMin()) + ","
						+ Double.toString(b.getMax()) + ")\n";
			}

			fitter.fitParameterSettings(name).setFixed(b.getFix());
			String jy_logic = "0";
			if (b.getFix())
				jy_logic = "1";
			codeFunc = codeFunc + "fitter.fitParameterSettings(\"" + name
					+ "\").setFixed(" + jy_logic + ")\n";
			func.setParameter(name, b.getC());
			codeFunc = codeFunc + "func.setParameter(\"" + name + "\","
					+ Double.toString(b.getC()) + ")\n";

			// set constraint
			int jj = b.getCons();
			if (jj > 0 && jj <= name1.length && jj != i) {
				fitter.setConstraint(name + " = " + name1[jj]);
				codeFunc = codeFunc + "fitter.setConstraint(\"" + name
						+ "\" = \"" + name1[jj] + "\")\n";
				constraint[i] = jj;
			}

		}

		mess = mess + "Perform fit in the range:" + Double.toString(rmin)
				+ " : " + Double.toString(rmax) + br;

	}

	/**
	 * Show output in jehep or prompt
	 * 
	 * @param a
	 *            Message
	 */
	public void showOutput(String a) {

		String classname = "jehep.ui.Editor";
		Object o = null;
		Class c = null;
		try {
			c = Class.forName(classname); // Dynamically load the class
			o = c.newInstance(); // Dynamically instantiate it
		} catch (Exception e) {

			/* Handle exceptions */
			System.out.println(a);
			return;
		}

		String meth = "insetTextCaret";
		// Method[] mtz = c.getMethods();
		// System.out.println(Arrays.toString(mtz));

		// call method insert
		Class[] parameterTypes = new Class[] { String.class };
		Method m;
		try {
			m = c.getMethod(meth, parameterTypes);
			m.invoke(c, new Object[] { a });
		} catch (NoSuchMethodException e) {
			System.out.println(e);
		} catch (IllegalAccessException e) {
			System.out.println(e);
		} catch (InvocationTargetException e) {
			System.out.println(e);
		}

	}

	/**
	 * add a new user function with the name and tooltip
	 * 
	 * **/

	public void addFunc(String name1, String tooltip1, String defininion1,
			String param1, int dim) {

		afunc.add(new BFunc(name1, tooltip1, defininion1, param1, dim));

		Collections.sort(afunc);

		labels = new String[afunc.size()];
		tooltip = new String[afunc.size()];
		definition = new String[afunc.size()];
		param = new String[afunc.size()];
		dimention = new int[afunc.size()];
		for (int i = 0; i < afunc.size(); i++) {
			BFunc a = afunc.get(i);
			labels[i] = a.getName();
			tooltip[i] = a.getTip();
			definition[i] = a.getDefinition();
			dimention[i] = a.getDim();
			param[i] = a.getParam();
		}

		model.clear();
		for (int i = 0; i < labels.length; i++) {
			model.addElement(labels[i]);
		}
		jlist.repaint();

	}

	/**
	 * Build function
	 * 
	 * @param a
	 *            intex
	 */
	private void buildFunc(int indx) {

		IFunction f = null;
		func = null;
		String s = labels[indx];

		mess = mess + "<b>" + s + "</b> added<br>";

		if (s.equals("P0")) {
			f = new P0(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", P0(\"" + s
					+ "\"))\n";
		} else if (s.equals("P1")) {
			f = new P1(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", P1(\"" + s
					+ "\"))\n";
		} else if (s.equals("P2")) {
			f = new P2(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", P2(\"" + s
					+ "\"))\n";
		} else if (s.equals("P3")) {
			f = new P3(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", P3(\"" + s
					+ "\"))\n";
		} else if (s.equals("P4")) {
			f = new P4(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", P4(\"" + s
					+ "\"))\n";
		} else if (s.equals("P5")) {
			f = new P5(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", P5(\"" + s
					+ "\"))\n";

		} else if (s.equals("P6")) {
			f = new P6(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", P6(\"" + s
					+ "\"))\n";

		} else if (s.equals("Gauss")) {
			f = new Gauss(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", Gauss(\"" + s
					+ "\"))\n";
		} else if (s.equals("BreitWigner")) {
			f = new BreitWigner(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s
					+ "\", BreitWigner(\"" + s + "\"))\n";

		} else if (s.equals("Exponent")) {
			f = new Exponent(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", Exponent(\""
					+ s + "\"))\n";
		} else if (s.equals("Pow")) {
			f = new Pow(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", Pow(\"" + s
					+ "\"))\n";

		} else if (s.equals("Landau")) {
			f = new Landau(s);
			funcCatalog.add(s, f);
			codeFunc = codeFunc + "funcCatalog.add(\"" + s + "\", Landau(\""
					+ s + "\"))\n";

		} else {

			if (!definition[indx].equals("undefined")) {
				// System.out.println("USER="+s);
				// System.out.println("definition="+definition[indx]);
				// System.out.println("param="+param[indx]);
				// this is user defined
				f = funcFactory.createFunctionFromScript(s, dimention[indx],
						definition[indx], param[indx], s, null);

				// f=funcFactory.createFunctionFromScript(s,1,
				// "a*x[0]*x[0]", "a", s, "2*a*x[0]");
				funcCatalog.add(s, f);

				String dd = Integer.toString(dimention[indx]);
				codeFunc = codeFunc + "funcFactory.createFunctionFromScript(\""
						+ s + "\"," + dd + ",\"" + definition[indx] + "\",\""
						+ param[indx] + "\",\" \",null)\n";
			} // end used defined

		} // end else

		// add
		if (sfunc.length() > 0) {
			sfunc = sfunc + "+" + s;
		} else {
			sfunc = s;
		}
		;

		funcText.setText(sfunc);
		mess = mess + "current function=<b>" + sfunc + "</b><br>";
		func = funcFactory.createFunctionByName("fit function", sfunc);
		iniFunc();

		// String[] list = funcCatalog.list();
		// for (int i=0; i<list.length; i++)
		// System.out.println(i+"\t "+list[i]);
		setHtml(mess);
	}

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */
	private void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}
