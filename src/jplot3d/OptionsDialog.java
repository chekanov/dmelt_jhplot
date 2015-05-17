// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package jplot3d;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import jhplot.F2D;
import jplot3d.SurfaceModel.PlotColor;
import jplot3d.SurfaceModel.PlotType;


public class OptionsDialog extends JDialog {

	protected JSurfacePanel parentFrame;
	protected SurfaceModelCanvas sm;
	private JButton btnFontsAxes;
	private JButton btnFontsLabel;
	final private Font fdefo = new Font("SansSerif", Font.BOLD, 14);
	protected JSpinner spinStepsX;

	protected JSpinner spinStepsY;

	protected JSpinner spinSurfaceBins;
	protected JSpinner spinContourBins;

	protected JSpinner spinPenWidth;
	protected JSpinner spnLabelOffsetX;
	protected JSpinner spnLabelOffsetY;
	protected JSpinner spnLabelOffsetZ;

	protected JColor surfaceColor;
	protected JColor lineColor;
	private JTabbedPane jtpMain;
	protected JTextField jtObject1;
	protected JTextField jtObject2;
	protected JTextField jtfTitle;
	protected JTextField xLabel, yLabel, zLabel;
	protected JCheckBox chkShowDelay;
	protected JColor boxColor;
	protected JColor frameColor;
        protected JCheckBox chkSurface;
	protected JCheckBox chkFill;
	protected JCheckBox chkBoxed;
	protected JCheckBox chkGrids;
	protected JCheckBox chkMesh;
	protected JCheckBox chkScaleBox;

	protected JCheckBox chkDisplayXY;
	protected JCheckBox chkDisplayZ;

	private JSpinner spinFOV;
	protected JCheckBox axesRange[];
	protected JSpinner spnAxesMin[];
	protected JSpinner spnAxesMax[];
	protected JColor axesColor;
	protected JSpinner spnAxesIncr;
	protected JSpinner spnAxesTicks;
	protected JSpinner spnAxesWidth;
	protected JSlider slider;
	private float zmin, zmax, xmin, xmax, ymin, ymax;
    private boolean[] showRange = new boolean[3];

	protected boolean mustDataToRegenerate = false;

	String[] items = { "spectrum", "dualshade",  "fog", "opaque","graycolor", "wireframe"};
	protected JComboBox colorMode;
	String[] items1 = { "surface", "density", "contour", "bars" };
	protected JComboBox showMode;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OptionsDialog(JSurfacePanel parentFrame) {
		this.parentFrame = parentFrame;
		axesRange = new JCheckBox[3];
		spnAxesMin = new JSpinner[3];
		spnAxesMax = new JSpinner[3];
		mustDataToRegenerate = false;
		jtpMain = new JTabbedPane();
                jtpMain.add("Functions", createFunctionsPane());
		jtpMain.add("Style", createGlobalPane());
		jtpMain.add("Axes", createAxisPane());
		jtpMain.setSelectedIndex(0);
		Box box = new Box(1);
		box.add(jtpMain);
		box.add(Box.createVerticalStrut(3));
		box.add(createDialogButtons());
		getContentPane().add(box);
		setSize(500, 400);
		setLocationRelativeTo(parentFrame);

	}

	public JDialog getFrame() {

		return this;

	}

	private Container createFunctionEditor() {

		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.insets = new Insets(0, 5, 3, 5);
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.fill = 2;
		gridbagconstraints.gridwidth = 0;
		gridbagconstraints.gridy = 0;

		gridbagconstraints.gridy = 0;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(Utils.labeledComponent("Object 1:",
				jtObject1 = new JTextField(20), true), gridbagconstraints);

		gridbagconstraints.gridy = 1;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(Utils.labeledComponent("Object 2:",
				jtObject2 = new JTextField(20), true), gridbagconstraints);

		jtObject1.setFont(fdefo);
		jtObject2.setFont(fdefo);

		gridbagconstraints.gridy = 3;
		gridbagconstraints.gridwidth = 1;
		jpanel.add(Utils.labeledComponent(" H2D  X Bins:",
				spinStepsX = new JSpinner(
						new SpinnerNumberModel(30, 1, 5000, 1)), true),
				gridbagconstraints);

		spinStepsX.setFont(fdefo);

		gridbagconstraints.gridy = 3;
		gridbagconstraints.gridwidth = 2;
		jpanel.add(Utils.labeledComponent("  H2D  Y Bins:",
				spinStepsY = new JSpinner(
						new SpinnerNumberModel(30, 1, 5000, 1)), true),
				gridbagconstraints);

		spinStepsY.setFont(fdefo);

		gridbagconstraints.gridy = 2;
		gridbagconstraints.gridwidth = 1;
		jpanel.add(Utils.labeledComponent("Surface bins:",
				spinSurfaceBins = new JSpinner(new SpinnerNumberModel(30, 1,
						5000, 1)), true), gridbagconstraints);

		spinStepsY.setFont(fdefo);
		spinSurfaceBins.setFont(fdefo);

		gridbagconstraints.gridy = 2;
		gridbagconstraints.gridwidth = 2;
		jpanel.add(Utils.labeledComponent("Contour bins:",
				spinContourBins = new JSpinner(new SpinnerNumberModel(30, 1,
						5000, 1)), true), gridbagconstraints);

		spinContourBins.setFont(fdefo);

		spinStepsX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				mustDataToRegenerate = true;
			}
		});
		spinStepsY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				mustDataToRegenerate = true;
			}
		});

		spinSurfaceBins.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				mustDataToRegenerate = true;
			}
		});

		spinContourBins.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				mustDataToRegenerate = true;
			}
		});

		
		
		return jpanel;
	}

	private Container createFunctionsPane() {
		Box box = new Box(1);
		box.add(Box.createVerticalStrut(2));
		// box.add(createFunctionTools());
		box.add(createFunctionEditor());
		return box;
	}

	/*
	 * // redraw function. Also set all components abstract protected void
	 * setAllFields();
	 * 
	 * // recalculate abstract protected void reCalculate();
	 * 
	 * abstract protected void selectFontsLabel();
	 * 
	 * abstract protected void selectFontsAxes();
	 */

	private Container createGlobalControls() {
		JPanel jpanel = new JPanel(new GridBagLayout());

		slider = new JSlider(JSlider.HORIZONTAL, 5, 30, 11);
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.insets = new Insets(0, 5, 3, 5);
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.fill = 2;
		gridbagconstraints.gridwidth = 0;
		gridbagconstraints.gridy = 0;

		jpanel.add(Utils.labeledComponent("Title to display:",
				jtfTitle = new JTextField(), true), gridbagconstraints);
		jtfTitle.setFont(fdefo);

		JPanel jpanel1 = new JPanel(new GridLayout(6, 0));

		jpanel1.add(Utils.labeledComponent("Box color:", boxColor = new
		 JColor(65, 20), true), gridbagconstraints);
		jpanel1.add(Utils.labeledComponent("Backg. color:",
				frameColor = new JColor(65, 20), true), gridbagconstraints);

		jpanel1.add(chkSurface = new JCheckBox("Surface"), gridbagconstraints);
		jpanel1.add(chkShowDelay = new JCheckBox("Delay regeneration"),
                 gridbagconstraints);
		jpanel1.add(chkBoxed = new JCheckBox("Draw box"), gridbagconstraints);
		jpanel1.add(chkMesh = new JCheckBox("Mesh"), gridbagconstraints);
		jpanel1.add(chkGrids = new JCheckBox("Draw grids"), gridbagconstraints);
		jpanel1.add(chkScaleBox = new JCheckBox("Scale box"),
				gridbagconstraints);
 
		chkSurface.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				AbstractButton abstractButton = (AbstractButton) changeEvent
						.getSource();
				ButtonModel buttonModel = abstractButton.getModel();
				boolean selected = buttonModel.isSelected();
				if (selected) {
					colorMode.setEnabled(true);
					chkFill.setEnabled(false);
				}
				;
				if (!selected) {
					colorMode.setEnabled(false);
					chkFill.setEnabled(true);
				}
				;
				mustDataToRegenerate = true;
			}
		});

		gridbagconstraints.gridy = 2;
		gridbagconstraints.gridwidth = 1;
		jpanel1.add(Utils.labeledComponent("Line color:",
				lineColor = new JColor(50, 20), true), gridbagconstraints);

		jpanel1.add(colorMode = new JComboBox(items), gridbagconstraints);
		jpanel1.add(showMode = new JComboBox(items1), gridbagconstraints);

		gridbagconstraints.gridy = 1;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(jpanel1, gridbagconstraints);

		jpanel1.add(chkFill = new JCheckBox("Fill"), gridbagconstraints);
		jpanel1.add(Utils.labeledComponent("Fill color:",
				surfaceColor = new JColor(50, 20), true), gridbagconstraints);

		gridbagconstraints.gridy = 2;
		gridbagconstraints.gridwidth = 0;

		JLabel sliderLabel = new JLabel("Scaling", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		jpanel1.add(sliderLabel, gridbagconstraints);
		jpanel1.add(slider, gridbagconstraints);

		jpanel.add(jpanel1, gridbagconstraints);
		jpanel.setBorder(BorderFactory.createTitledBorder("Global Settings"));

		return jpanel;
	}

	private Container createGlobalPane() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		jpanel.add(createGlobalControls());
		return jpanel;
	}

	public double getFov() {
		return ((Double) spinFOV.getValue()).doubleValue();
	}

	public void setFov(double d) {
		// modelView.getFov();
		// spinFOV.setValue(new Double( d ));
	}

	private Container createAxisExtents() {
		Box box = new Box(1);
		String as[] = { "X", "Y", "Z" };

		for (int i = 0; i < 3; i++) {
			Box box1 = new Box(0);
			box1.add(new JLabel(as[i]));
			box1.add(axesRange[i] = new JCheckBox());
			box1.add(Box.createHorizontalStrut(5));
			box1.add(Utils.labeledComponent("Min:",
					spnAxesMin[i] = new JSpinner(new SpinnerNumberModel(0.0D,
							-5000D, 5000D, 0.10000000000000001D)), true));
			box1.add(Box.createHorizontalStrut(5));
			box1.add(Utils.labeledComponent("Max:",
					spnAxesMax[i] = new JSpinner(new SpinnerNumberModel(1.0D,
							-5000D, 5000D, 0.10000000000000001D)), true));

			spnAxesMin[i].setFont(fdefo);
			spnAxesMax[i].setFont(fdefo);

			box1.setAlignmentX(1.0F);
			box.add(box1);
		}

		box.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		return box;
	}

	private Container createAxisControls() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridbagconstraints = new GridBagConstraints();

		gridbagconstraints.insets = new Insets(0, 5, 3, 5);
		gridbagconstraints.gridwidth = 1;
		gridbagconstraints.gridy = 0;
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.fill = 2;
		jpanel.add(Utils.labeledComponent("Axis label color:",
				axesColor = new JColor(50, 20), true), gridbagconstraints);
		jpanel.add(Utils
				.labeledComponent("Line width:", spnAxesWidth = new JSpinner(
						new SpinnerNumberModel(2, 1, 10, 1)), true),
				gridbagconstraints);

		spnAxesWidth.setFont(fdefo);

		// jpanel.add(Utils.labeledComponent("Fonts:", = new JColor(50, 20),
		// true), gridbagconstraints);
		jpanel.add(btnFontsAxes = new JButton("Axis Font"), gridbagconstraints);

		gridbagconstraints.gridy = 1;

		jpanel.add(chkDisplayXY = new JCheckBox("Display XY"),
				gridbagconstraints);
		jpanel.add(chkDisplayZ = new JCheckBox("DisplayZ"), gridbagconstraints);
		jpanel.add(btnFontsLabel = new JButton("Label Font"),
				gridbagconstraints);

		gridbagconstraints.gridy = 2;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(createAxisExtents(), gridbagconstraints);

		gridbagconstraints.gridy = 3;
		gridbagconstraints.gridwidth = 0;

		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();

		p1.add(Utils.labeledComponent("X label:", xLabel = new JTextField(20),
				true));
		p1.add(Utils.labeledComponent("Offset:",
				spnLabelOffsetX = new JSpinner(new SpinnerNumberModel(1.0D,
						0.0D, 10D, 0.01D)), true));

		p2.add(Utils.labeledComponent("Y label:", yLabel = new JTextField(20),
				true));
		p2.add(Utils.labeledComponent("Offset:",
				spnLabelOffsetY = new JSpinner(new SpinnerNumberModel(1.0D,
						0.0D, 10D, 0.01D)), true));

		p3.add(Utils.labeledComponent("Z label:", zLabel = new JTextField(20),
				true));
		p3.add(Utils.labeledComponent("Offset:",
				spnLabelOffsetZ = new JSpinner(new SpinnerNumberModel(1.0D,
						0.0D, 10D, 0.01D)), true));

		gridbagconstraints.gridy = 3;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(p1, gridbagconstraints);

		gridbagconstraints.gridy = 4;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(p2, gridbagconstraints);

		gridbagconstraints.gridy = 5;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(p3, gridbagconstraints);

		xLabel.setFont(fdefo);
		yLabel.setFont(fdefo);
		zLabel.setFont(fdefo);

		spnLabelOffsetX.setFont(fdefo);
		spnLabelOffsetY.setFont(fdefo);
		spnLabelOffsetZ.setFont(fdefo);

		btnFontsAxes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				// selectFontsAxes();
			}

		});

		btnFontsLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				// selectFontsLabel();
			}

		});

		jpanel.setBorder(BorderFactory.createTitledBorder("Axis Settings"));
		return jpanel;
	}

	private Container createAxisPane() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		jpanel.add(createAxisControls());
		return jpanel;
	}

	private Container createDialogButtons() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.anchor = 22;
		gridbagconstraints.fill = 2;
		gridbagconstraints.weightx = 5D;
		jpanel.add(new JPanel(), gridbagconstraints);
		gridbagconstraints.weightx = 0.0D;
		gridbagconstraints.insets = new Insets(0, 2, 0, 2);
		JButton jbutton;
		jpanel.add(jbutton = new JButton("Redraw"), gridbagconstraints);
		jbutton.setMnemonic('R');
		JButton jbutton1;
		jpanel.add(jbutton1 = new JButton("Close"), gridbagconstraints);
		jbutton1.setMnemonic('C');

		// redraw function
		jbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setAllFields();



				parentFrame.evaluate();
				
			}
		});

		// close
		jbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				setVisible(false);
				dispose();
			}
		});

		getRootPane().setDefaultButton(jbutton1);
		jpanel.setMaximumSize(new Dimension(0x7fffffff, jbutton1.getHeight()));
		return jpanel;
	}

	/**
	 * Set all fields and redraw
	 */
	private void setAllFields() {
		
		sm=parentFrame.getModel();
	
		if (chkSurface.isSelected() == true) {
			sm.setPlotType(PlotType.SURFACE);
		} else {
			sm.setPlotType(PlotType.CONTOUR);
		}
		int jj = (Integer) (spinStepsX.getValue());
		sm.setCalcDivisions(jj);
		jj = (Integer) (spinStepsY.getValue());
		sm.setCalcDivisions(jj);
	
		jj = (Integer) (spinSurfaceBins.getValue());
		sm.setDispDivisions(jj);
		jj = (Integer) (spinContourBins.getValue());
		sm.setContourLines(jj);
	
		
		// grid and axis
		if (chkGrids.isSelected() == true)
            sm.setDisplayGrids(true);
         else
            sm.setDisplayGrids(false);
		if (chkDisplayXY.isSelected() == true)
            sm.setDisplayXY(true);
        else
            sm.setDisplayXY(false);
		
		if (chkDisplayZ.isSelected() == true)
            sm.setDisplayZ(true);
         else
            sm.setDisplayZ(false);
		
         if (chkMesh.isSelected()==true)
        	 sm.setMesh(true);
         else
        	 sm.setMesh(false);
         
        
          
         if (chkBoxed.isSelected()==true)
        	 sm.setBoxed(true);
         else
        	 sm.setBoxed(false);
         
         if ( xLabel.getText() != null)
                 if ( xLabel.getText().length()>0)        
                           sm.setXlabel(xLabel.getText());
         if ( yLabel.getText() != null)
                 if ( yLabel.getText().length()>0)      
                           sm.setYlabel(yLabel.getText());
         if ( zLabel.getText() != null)
                 if ( zLabel.getText().length()>0)
                           sm.setYlabel(zLabel.getText());

 
         if (chkShowDelay.isSelected() == true)
             sm.setExpectDelay(true);
         else
        	 sm.setExpectDelay(false);

         // set types
         
         
         if (showMode.getSelectedIndex()==0)
        	  sm.setPlotType(PlotType.SURFACE);
         else if (showMode.getSelectedIndex() == 1)
        	 sm.setPlotType(PlotType.DENSITY);
         else if (showMode.getSelectedIndex() == 2)
        	 sm.setPlotType(PlotType.CONTOUR);
         else if (showMode.getSelectedIndex() == 3)
        	 sm.setPlotType(PlotType.BARS);
          

         // set colors       
         if (colorMode.getSelectedIndex() == 0)
        	 sm.setPlotColor(PlotColor.SPECTRUM);  
         else if (colorMode.getSelectedIndex() == 1)
        	 sm.setPlotColor(PlotColor.DUALSHADE);
         else if (colorMode.getSelectedIndex() == 2)
        	 sm.setPlotColor(PlotColor.FOG);
         else if (colorMode.getSelectedIndex() == 3)
        	 sm.setPlotColor(PlotColor.OPAQUE);
         else if (colorMode.getSelectedIndex() == 4)
        	 sm.setPlotColor(PlotColor.GRAYSCALE);
         else if (colorMode.getSelectedIndex() == 5)
                 sm.setPlotType(PlotType.WIREFRAME);
	
         // set ranges
         getRangesFromGUI();
         
         
         sm.setXMax(xmax);
         sm.setXMin(xmin);
         sm.setYMax(ymax);
         sm.setYMin(ymin);
         sm.setZMax(zmax);
         sm.setZMin(zmin);
         
         
         
         
         // setLabelColor(axesColor.getColor());
         sm.getColorModel().setBoxColor(boxColor.getColor());
         sm.getColorModel().setBackgroundColor(frameColor.getColor());
         sm.getColorModel().setLineBoxColor(axesColor.getColor());
         sm.getColorModel().setFillColor(surfaceColor.getColor());
         sm.getColorModel().setLineColor(lineColor.getColor());

         
         jj = (Integer) (spinStepsX.getValue());
         sm.setCalcDivisions(jj);
        // jj = (Integer) (spinStepsY.getValue());
        // sm.setCalcDivisions(jj);
        

         jj = (Integer) (spinSurfaceBins.getValue());
         sm.setDispDivisions(jj);
         jj = (Integer) (spinContourBins.getValue());
         sm.setContourLines(jj);
         sm.set2DScaling( (float)(slider.getValue()));
         sm.setInitScaling((float)(slider.getValue()));
       
         
		// get functions
		getFunctions();
		
		

	}
	
	// update data GUI settings
	public void setGUI() {
		
		sm= parentFrame.getModel();
		
		ArrayList<F2D> func=parentFrame.getF2D();
		
		if ( func.size()>0 )
		jtObject1.setText(  ((F2D)func.get(0)).getName()  );
		
		if (  func.size()>1)
		jtObject2.setText( ((F2D)func.get(1)).getName()   );
		
		// grid and axis
		if (sm.isDisplayGrids() == true)
			chkGrids.setSelected(true);
		else
			chkGrids.setSelected(false);
		
		if (sm.getDisplayXY() == true)
			chkDisplayXY.setSelected(true);
		else
			chkDisplayXY.setSelected(false);

		if (sm.getDisplayZ() == true)
			chkDisplayZ.setSelected(true);
		else
			chkDisplayZ.setSelected(false);
	

           
                 if (sm.getXlabel()!=null) xLabel.setText(sm.getXlabel());
                 if (sm.getYlabel()!=null) yLabel.setText(sm.getYlabel());
                 if (sm.getZlabel()!=null) zLabel.setText(sm.getZlabel());
                 // if (sm.getTitle()!=null)  jtfTitle.setText(sm.getTitle());
	
		/*
		if (sm.isSurfaceType()) {
			colorMode.setEnabled(true);
			chkFill.setEnabled(false);
			chkSurface.setSelected(true);;
		}else {
			colorMode.setEnabled(false);
			chkFill.setEnabled(true);
			chkSurface.setSelected(false);
		};
        */
		

		if (sm.isMesh() == true)
			chkMesh.setSelected(true);
		else
			chkMesh.setSelected(false);
		
		
		// is Filled
		if (sm.isBoxed() == true)
			chkBoxed.setSelected(true);
		else
			chkBoxed.setSelected(false);
		
		
		 // delay regenerations?
        if (sm.isExpectDelay() == true)
                chkShowDelay.setSelected(true);
        if (sm.isExpectDelay() == false)
                chkShowDelay.setSelected(false);

        // which type
        showMode.setSelectedIndex(0);
		if (sm.isDensityType())
			showMode.setSelectedIndex(1);
		if (sm.isContourType())
			showMode.setSelectedIndex(2);
		if (sm.isBarsType())
			showMode.setSelectedIndex(3);

		
		/*
       if (sm.isBarsType() == true)
	    	chkSurface.setSelected(false);
	  	else
	  		chkSurface.setSelected(true);
        */
		
		// is Filled
		if (sm.isWireframeType() == true)
			chkFill.setSelected(false);
		else
			chkFill.setSelected(true);

		// color
		if (sm.getPlotColor() == PlotColor.SPECTRUM)
			colorMode.setSelectedIndex(0);
		else if (sm.getPlotColor() == PlotColor.DUALSHADE )
			showMode.setSelectedIndex(1);
		else if (sm.getPlotColor() == PlotColor.FOG)
			showMode.setSelectedIndex(2);
		else if (sm.getPlotColor() == PlotColor.OPAQUE)
			showMode.setSelectedIndex(3);
		else if (sm.getPlotColor() == PlotColor.GRAYSCALE)
			showMode.setSelectedIndex(4);
	        else if (sm.getPlotType() == PlotType.WIREFRAME)
                        showMode.setSelectedIndex(5);
	
		
		xmin=sm.getXMin();
		xmax=sm.getXMax();
		ymin=sm.getYMin();
		ymax=sm.getYMax();
		zmin=sm.getZMin();
		zmax=sm.getZMax();
		
		spnAxesMin[0].setValue(new Double(xmin));
		spnAxesMax[0].setValue(new Double(xmax));
		if (showRange[0] == true)
			axesRange[0].setSelected(true);
		if (showRange[0] == false)
			axesRange[0].setSelected(false);

		spnAxesMin[1].setValue(new Double(ymin));
		spnAxesMax[1].setValue(new Double(ymax));
		if (showRange[1] == true)
			axesRange[1].setSelected(true);
		if (showRange[1] == false)
			axesRange[1].setSelected(false);

		spnAxesMin[2].setValue(new Double(zmin));
		spnAxesMax[2].setValue(new Double(zmax));
		if (showRange[2] == true)
			axesRange[2].setSelected(true);
		if (showRange[2] == false)
			axesRange[2].setSelected(false);

		
		
		frameColor.setColor( sm.getColorModel().getBackgroundColor());
		boxColor.setColor( sm.getColorModel().getBoxColor());
		surfaceColor.setColor( sm.getColorModel().getFirstPolygonColor(0) );
		lineColor.setColor( sm.getColorModel().getLineColor());
		axesColor.setColor( sm.getColorModel().getLineBoxColor() );
		
		spinStepsX.setValue(new Integer(sm.getCalcDivisions() ));
		spinStepsY.setValue(new Integer(sm.getCalcDivisions()));
		spinSurfaceBins.setValue(new Integer(sm.getDispDivisions()));
		spinContourBins.setValue(new Integer(sm.getContourLines()));
		
		slider.setValue( (int)sm.get2DScaling());
		
		// setMessage("chekanov updateGUI()");

	
	
	}

	private void getRangesFromGUI() {

		double d = 0.0;

		d = (Double) (spnAxesMin[0].getValue());
		xmin = (float) d;
		// setMessage("Xmin "+ Double.toString(d));
		d = (Double) (spnAxesMax[0].getValue());
		// setMessage("Xmax "+ Double.toString(d));
		xmax = (float) d;

		d = (Double) (spnAxesMin[1].getValue());
		ymin = (float) d;
		// setMessage("Z min "+ Double.toString(d));
		d = (Double) (spnAxesMax[1].getValue());
		// setMessage("Z max "+ Double.toString(d));
		ymax = (float) d;

		d = (Double) (spnAxesMin[2].getValue());
		zmin = (float) d;
		// setMessage("Z min "+ Double.toString(d));
		d = (Double) (spnAxesMax[2].getValue());
		// setMessage("Z max "+ Double.toString(d));
		zmax = (float) d;

	}

	 /**
     * Get all functions from GUI
     */
    public void getFunctions() {

            getRangesFromGUI();
            String name1 = jtObject1.getText();
            String name2 = jtObject2.getText();
            name1 = name1.trim();
            name2 = name2.trim();

            // clear all functions in the array
            parentFrame.clearF2D();
            
            if (name1 != null && name1.length() > 1 && name1.indexOf("H2D") < 0
                            && name1.indexOf("P2D") < 0 && name1.indexOf("P3D") < 0) {
            	F2D fu1 = new F2D(name1, xmin, xmax, ymin, ymax);
            	parentFrame.setF2D(fu1);
            	sm.setPlotFunction2(false);
            } 

            if (name2 != null && name2.length() > 1 && name2.indexOf("H2D") < 0
                            && name2.indexOf("P2D") < 0 && name2.indexOf("P3D") < 0) {
            	F2D fu2 = new F2D(name2, xmin, xmax, ymin, ymax);
            	parentFrame.setF2D(fu2);
            	sm.setPlotFunction2(true);
            }

            // setMessage("getFunctions()");
    }

	
	

}
