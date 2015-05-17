package jplot3dp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import jhplot.FPR;

import jplot3dp.JColor;
import jplot3dp.ModelView;
import jplot3dp.Utils;

class OptionsDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean flag) {
		dirty = flag;
	}

	private void updateLblFunctions() {
		lblFunctions.setText("Number of function(s): " + functions.size());
	}

	private void updateBorderFunction() {
		TitledBorder titledborder = (TitledBorder) containerFunc.getBorder();
		titledborder.setTitle("\"" + lastFunction.name + "\" properties");
		containerFunc.repaint();
	}

	private void updateLblArea() {
		double d = lastFunction.getArea();
		d = (double) (int) (d * 10000D + 0.5D) / 10000D;
		if (lastFunction.isCurve)
			lblArea.setText("Aproximate length : " + d);
		else
			lblArea.setText("Aproximate surface area : " + d);
	}

	private Container createFunctionTools() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.fill = 2;
		gridbagconstraints.weightx = 0.0D;
		gridbagconstraints.insets = new Insets(0, 2, 0, 0);
		jpanel.add(btnGoLeft = new JButton(Utils.loadIcon("goleft.gif")),
				gridbagconstraints);
		Utils.makeHot(btnGoLeft);
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.insets = new Insets(0, 0, 0, 0);
		jpanel.add(comboFunction = new JComboBox(), gridbagconstraints);
		gridbagconstraints.weightx = 0.0D;
		gridbagconstraints.insets = new Insets(0, 0, 2, 0);
		jpanel.add(btnGoRight = new JButton(Utils.loadIcon("goright.gif")),
				gridbagconstraints);
		Utils.makeHot(btnGoRight);
		class _cls1OnLeftRight implements ActionListener {

			public void actionPerformed(ActionEvent actionevent) {
				int i = actionevent.getSource().equals(btnGoLeft) ? comboFunction
						.getSelectedIndex() - 1
						: comboFunction.getSelectedIndex() + 1;
				i %= comboFunction.getItemCount();
				comboFunction.setSelectedIndex(i);
			}

			_cls1OnLeftRight() {
			}
		}

		btnGoLeft.addActionListener(new _cls1OnLeftRight());
		btnGoRight.addActionListener(new _cls1OnLeftRight());
		comboFunction.setEditable(true);
		comboFunction.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent itemevent) {
				if (itemevent.getStateChange() == 1) {
					Object obj = ((JComboBox) itemevent.getSource())
							.getSelectedItem();
					if (obj instanceof ModelView.ModelFunction) {
						lastFunction = (ModelView.ModelFunction) ((JComboBox) itemevent
								.getSource()).getSelectedItem();
						getFunctionFields();
					}
				} else {
					setFunctionFields(lastFunction);
				}
			}

		});
		comboFunction.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				if (lastFunction != null) {
					lastFunction.name = comboFunction.getSelectedItem()
							.toString();
					updateBorderFunction();
				}
			}

		});
		gridbagconstraints.weightx = 0.0D;
		gridbagconstraints.fill = 0;
		gridbagconstraints.insets = new Insets(0, 2, 0, 0);
		jpanel.add(chkVisible = new JCheckBox(), gridbagconstraints);
		chkVisible.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				setFunctionFields();
			}

		});
		gridbagconstraints.insets = new Insets(0, 0, 0, 5);
		JLabel jlabel = new JLabel(Utils.loadIcon("eye.gif"));
		jpanel.add(jlabel, gridbagconstraints);
		jlabel.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent mouseevent) {
				chkVisible.setSelected(!chkVisible.isSelected());
				setFunctionFields();
			}

		});
		gridbagconstraints.insets = new Insets(0, 2, 0, 2);
		jpanel.add(btnAdd = new JButton("Add"), gridbagconstraints);
		jpanel.add(btnDelete = new JButton("Delete"), gridbagconstraints);
		gridbagconstraints.weightx = 2D;
		gridbagconstraints.anchor = 22;
		gridbagconstraints.insets = new Insets(0, 5, 0, 5);
		jpanel.add(lblFunctions = new JLabel(), gridbagconstraints);
		updateLblFunctions();
		ActionListener actionlistener = new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				if (actionevent.getSource() == btnAdd) {
					ModelView.ModelFunction modelfunction = functions
							.addFunction();
					modelfunction.parseFunction();
					comboFunction.addItem(modelfunction);
					comboFunction
							.setSelectedIndex(comboFunction.getItemCount() - 1);
				} else if (comboFunction.getItemCount() > 1) {
					int i = comboFunction.getSelectedIndex();
					comboFunction.removeItemAt(i);
					functions.removeFunction(i);
				}
				updateLblFunctions();
				modelView.repaint();
			}

		};
		btnAdd.addActionListener(actionlistener);
		btnDelete.addActionListener(actionlistener);
		jpanel.setMaximumSize(new Dimension(32767, 30));
		return jpanel;
	}

	
	/**
	 * Add new function
	 * 
	 * @param fun
	 */
	public void addFunction(FPR fun) {

		ModelView.ModelFunction modelfunction = functions.addFunction();
		modelfunction.expression = fun.getName();
		modelfunction.gridDivsU = fun.getDivU();
		modelfunction.gridDivsV = fun.getDivV();
		modelfunction.curveColor=fun.getLineColor();
		modelfunction.curveWidth=fun.getPenWidth();
		modelfunction.surfaceColor =fun.getFillColor();
		modelfunction.fillSurface=fun.isFilled();
		modelfunction.parseFunction();
		comboFunction.addItem(modelfunction);
		comboFunction.setSelectedIndex(comboFunction.getItemCount() - 1);
		updateLblFunctions();
	}

	
	
	
	
	
	private Container createFunctionControls() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.insets = new Insets(0, 5, 3, 5);
		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridy = -1;
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.fill = 2;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(
				tglIsCurve = new JToggleButton("Function is a curve", false),
				gridbagconstraints);
		tglIsCurve.setSelected(true);
		tglIsCurve.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent itemevent) {
				boolean flag = tglIsCurve.isSelected();
				chkFillSurface.setVisible(!flag);
				((JLabel) paneUSteps.getComponent(0)).setText(flag ? "t steps:"
						: "u steps:");
				paneVSteps.setVisible(!flag);
				jpanVStepsFill.setVisible(flag);
				panePenWidth.setVisible(flag);
				chkAbsoluteWidth.setVisible(flag);
				paneSurfaceColor.setVisible(!flag);
				if (lastFunction.isCurve != flag)
					lblArea.setText("");
				else
					updateLblArea();
			}

		});
		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridwidth = 1;
		jpanel.add(paneUSteps = Utils.labeledComponent("u steps:",
				spinUSteps = new JSpinner(
						new SpinnerNumberModel(30, 1, 5000, 1)), true),
				gridbagconstraints);
		gridbagconstraints.gridx = 1;
		jpanel.add(jpanVStepsFill = new JPanel(), gridbagconstraints);
		jpanel.add(paneVSteps = Utils.labeledComponent("v steps:",
				spinVSteps = new JSpinner(
						new SpinnerNumberModel(30, 1, 5000, 1)), true),
				gridbagconstraints);
		gridbagconstraints.gridx = 0;
		jpanel.add(panePenWidth = Utils.labeledComponent("Pen Width: ",
				spinPenWidth = new JSpinner(new SpinnerNumberModel(2, 0, 1000,
						1)), true), gridbagconstraints);
		gridbagconstraints.gridx = 1;
		jpanel.add(chkAbsoluteWidth = new JCheckBox("Absolute Width"),
				gridbagconstraints);
		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridy = -1;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(chkFillSurface = new JCheckBox("Fill Surface"),
				gridbagconstraints);
		gridbagconstraints.weightx = 0.0D;
		gridbagconstraints.gridy = -1;
		gridbagconstraints.gridwidth = 1;
		jpanel.add(Utils.labeledComponent("Curve color:",
				curveColor = new JColor(50, 20), true), gridbagconstraints);
		gridbagconstraints.gridx = 1;
		gridbagconstraints.gridy = -1;
		jpanel.add(paneSurfaceColor = Utils.labeledComponent("Surface color:",
				surfaceColor = new JColor(50, 20), true), gridbagconstraints);
		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(Utils.labeledComponent("Transparency:",
				sldAlpha = new JSlider(0, 255), true), gridbagconstraints);
		sldAlpha.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent changeevent) {
				int i = 255 - sldAlpha.getValue();
				curveColor
						.setColor(Utils.changeAlpha(curveColor.getColor(), i));
				surfaceColor.setColor(Utils.changeAlpha(
						surfaceColor.getColor(), i));
			}

		});
		jpanel.add(lblArea = new JLabel("Area"), gridbagconstraints);
		lblArea.setHorizontalAlignment(0);
		jpanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		return jpanel;
	}

	private Container createFunctionEditor() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.weightx = 2D;
		gridbagconstraints.weighty = 1.0D;
		gridbagconstraints.fill = 1;
		Container container = Utils.labeledComponent("Function definition:",
				new JScrollPane(textFuncDef = new JTextArea()), false);
		jpanel.add(container, gridbagconstraints);
		gridbagconstraints.weightx = 0.0D;
		gridbagconstraints.fill = 2;
		gridbagconstraints.anchor = 19;
		container = createFunctionControls();
		jpanel.add(container, gridbagconstraints);
		jpanel.setBorder(BorderFactory
				.createTitledBorder("Function Properties"));
		return containerFunc = jpanel;
	}

	private Container createFunctionsPane() {
		Box box = new Box(1);
		box.add(Box.createVerticalStrut(2));
		box.add(createFunctionTools());
		box.add(createFunctionEditor());
		return box;
	}

	private void getFunctionFields() {
		ModelView.ModelFunction modelfunction = lastFunction;
		if (modelfunction == null) {
			return;
		} else {
			updateBorderFunction();
			tglIsCurve.setSelected(modelfunction.isCurve);
			chkVisible.setSelected(modelfunction.visible);
			spinUSteps.setValue(new Integer(modelfunction.gridDivsU - 1));
			spinVSteps.setValue(new Integer(modelfunction.gridDivsV - 1));
			spinPenWidth.setValue(new Integer(modelfunction.curveWidth));
			chkAbsoluteWidth.setSelected(modelfunction.absoluteWidth);
			chkFillSurface.setSelected(modelfunction.fillSurface);
			curveColor.setColor(modelfunction.curveColor);
			surfaceColor.setColor(modelfunction.surfaceColor);
			sldAlpha.setValue(255 - modelfunction.surfaceColor.getAlpha());
			textFuncDef.setText(modelfunction.expression);
			updateLblArea();
			return;
		}
	}

	
	
	public void setFillColor(Color c) {
		ModelView.ModelFunction modelfunction = lastFunction;
		modelfunction.surfaceColor=c;
	}

	public void setLineColor(Color c) {
		ModelView.ModelFunction modelfunction = lastFunction;
		modelfunction.curveColor=c;
	}
	
	
	
	
	private void setFunctionFields() {
		setFunctionFields(lastFunction);
	}

	private void setFunctionFields(ModelView.ModelFunction modelfunction) {
		if (modelfunction == null)
			return;
		boolean flag = false;
		boolean flag1 = false;
		if (tglIsCurve.isSelected() != modelfunction.isCurve) {
			modelfunction.isCurve = tglIsCurve.isSelected();
			flag = true;
		}
		if (modelfunction.visible != chkVisible.isSelected()) {
			modelfunction.visible = chkVisible.isSelected();
			flag1 = true;
		}
		int i = ((Integer) spinUSteps.getValue()).intValue() + 1;
		if (modelfunction.gridDivsU != i) {
			modelfunction.gridDivsU = i;
			flag = true;
		}
		i = ((Integer) spinVSteps.getValue()).intValue() + 1;
		if (modelfunction.gridDivsV != i) {
			modelfunction.gridDivsV = i;
			flag = true;
		}
		i = ((Integer) spinPenWidth.getValue()).intValue();
		if (modelfunction.curveWidth != i) {
			modelfunction.curveWidth = i;
			flag1 = true;
		}
		if (modelfunction.absoluteWidth != chkAbsoluteWidth.isSelected()) {
			modelfunction.absoluteWidth = chkAbsoluteWidth.isSelected();
			flag1 = true;
		}
		if (modelfunction.fillSurface != chkFillSurface.isSelected()) {
			modelfunction.fillSurface = chkFillSurface.isSelected();
			flag1 = true;
		}
		if (!modelfunction.curveColor.equals(curveColor.getColor())) {
			modelfunction.curveColor = curveColor.getColor();
			flag1 = true;
		}
		if (!modelfunction.surfaceColor.equals(surfaceColor.getColor())) {
			modelfunction.surfaceColor = surfaceColor.getColor();
			flag1 = true;
		}
		if (!modelfunction.expression.equals(textFuncDef.getText())) {
			modelfunction.expression = textFuncDef.getText();
			flag = true;
		}
		if (flag) {
			modelfunction.parseFunction();
			updateLblArea();
			flag1 = true;
		}
		if (flag1) {
			modelView.repaint();
			dirty = true;
		}
	}

	private Container createGlobalControls() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.insets = new Insets(0, 5, 3, 5);
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.fill = 2;
		gridbagconstraints.gridwidth = 0;
		gridbagconstraints.gridy = 0;
		jpanel.add(Utils.labeledComponent("Title to display:",
				jtfTitle = new JTextField(), true), gridbagconstraints);
		JPanel jpanel1 = new JPanel(new FlowLayout());
		jpanel1.add(Utils.labeledComponent("Background color:",
				bgColor = new JColor(50, 20), true), gridbagconstraints);
		jpanel1.add(chkFog = new JCheckBox("Fog"), gridbagconstraints);
		gridbagconstraints.gridy = 1;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(jpanel1, gridbagconstraints);
		gridbagconstraints.gridy = 2;
		gridbagconstraints.gridwidth = 1;
		jpanel.add(Utils.labeledComponent("Fog start:",
				spinFogStart = new JSpinner(new SpinnerNumberModel(0.0D, 0.0D,
						1000D, 1.0D)), true), gridbagconstraints);
		jpanel.add(Utils.labeledComponent("Fog end:",
				spinFogEnd = new JSpinner(new SpinnerNumberModel(0.0D, 0.0D,
						1000D, 1.0D)), true), gridbagconstraints);
		gridbagconstraints.gridy = 3;
		jpanel.add(Utils.labeledComponent("Box width:",
				spinBoxWidth = new JSpinner(new SpinnerNumberModel(10, 0, 2000,
						1)), true), gridbagconstraints);
		jpanel.add(Utils.labeledComponent("Box height:",
				spinBoxHeight = new JSpinner(new SpinnerNumberModel(10, 0,
						2000, 1)), true), gridbagconstraints);
		spinBoxWidth.setEnabled(Utils.applet == null);
		spinBoxHeight.setEnabled(Utils.applet == null);
		modelView.addComponentListener(new ComponentAdapter() {

			public void componentResized(ComponentEvent componentevent) {
				spinBoxWidth.setValue(new Integer(modelView.getWidth()));
				spinBoxHeight.setValue(new Integer(modelView.getHeight()));
			}

		});
		gridbagconstraints.gridy = 4;
		gridbagconstraints.gridwidth = 0;
		jpanel1 = new JPanel(new FlowLayout());
		jpanel1.add(new JPanel());
		jpanel1.add(Utils.labeledComponent("Field of Vision:",
				spinFOV = new JSpinner(new SpinnerNumberModel(90D, 10D, 170D,
						1.0D)), true));
		// chekanov
		// jpanel1.add(new _cls9());
		jpanel1.add(new JPanel());
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
		modelView.getFov();
		spinFOV.setValue(new Double(d));
	}

	public void getGlobalFields() {
		jtfTitle.setText(modelView.title);
		bgColor.setColor(modelView.bgColor);
		chkFog.setSelected(modelView.fogEnabled);
		spinFogStart.setValue(new Double(modelView.fogStart));
		spinFogEnd.setValue(new Double(modelView.fogEnd));
		spinFOV.setValue(new Double(modelView.getFov()));
	}

	private void setGlobalFields() {
		boolean flag = false;
		if (!modelView.title.equals(jtfTitle.getText())) {
			modelView.title = jtfTitle.getText();
			flag = true;
		}
		if (!modelView.bgColor.equals(bgColor.getColor())) {
			modelView.bgColor = bgColor.getColor();
			flag = true;
		}
		if (modelView.fogEnabled != chkFog.isSelected()) {
			modelView.fogEnabled = chkFog.isSelected();
			flag = true;
		}
		double d = ((Double) spinFogStart.getValue()).doubleValue();
		if (modelView.fogStart != d) {
			modelView.fogStart = d;
			flag = true;
		}
		d = ((Double) spinFogEnd.getValue()).doubleValue();
		if (modelView.fogEnd != d) {
			modelView.fogEnd = d;
			flag = true;
		}
		d = ((Double) spinFOV.getValue()).doubleValue();
		if (modelView.getFov() != d) {
			modelView.setFov(d);
			flag = true;
		}
		Dimension dimension = modelView.getSize();
		int i = ((Integer) spinBoxWidth.getValue()).intValue();
		int j = ((Integer) spinBoxHeight.getValue()).intValue();
		if (dimension.height != j || dimension.width != i) {
			Dimension dimension1 = parentFrame.getSize();
			parentFrame.setSize((dimension1.width - dimension.width) + i,
					(dimension1.height - dimension.height) + j);
			parentFrame.validate();
		}
		if (flag) {
			modelView.repaint();
			dirty = true;
		}
	}

	private Container createAxisExtents() {
		Box box = new Box(1);
		String as[] = { "X", "Z", "Y" };
		for (int i = 0; i < 3; i++) {
			int j = i != 0 ? 3 - i : 0;
			Box box1 = new Box(0);
			box1.add(new JLabel(as[j]));
			box1.add(axisShown[j] = new JCheckBox());
			box1.add(Box.createHorizontalStrut(5));
			box1.add(Utils.labeledComponent("Min:",
					spnAxesMin[j] = new JSpinner(new SpinnerNumberModel(0.0D,
							-5000D, 5000D, 0.10000000000000001D)), true));
			box1.add(Box.createHorizontalStrut(5));
			box1.add(Utils.labeledComponent("Max:",
					spnAxesMax[j] = new JSpinner(new SpinnerNumberModel(1.0D,
							-5000D, 5000D, 0.10000000000000001D)), true));
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
		jpanel.add(Utils.labeledComponent("Axis color:",
				axesColor = new JColor(50, 20), true), gridbagconstraints);
		jpanel.add(Utils
				.labeledComponent("Line width:", spnAxesWidth = new JSpinner(
						new SpinnerNumberModel(2, 1, 3, 1)), true),
				gridbagconstraints);
		gridbagconstraints.gridy = 1;
		jpanel.add(Utils.labeledComponent("Step density:",
				spnAxesIncr = new JSpinner(new SpinnerNumberModel(
						0.10000000000000001D, 0.0D, 500D, 0.01D)), true),
				gridbagconstraints);
		jpanel.add(Utils.labeledComponent("Tick density:",
				spnAxesTicks = new JSpinner(new SpinnerNumberModel(
						0.10000000000000001D, 0.0D, 5000D,
						0.050000000000000003D)), true), gridbagconstraints);
		gridbagconstraints.gridy = 2;
		gridbagconstraints.gridwidth = 0;
		jpanel.add(createAxisExtents(), gridbagconstraints);
		jpanel.setBorder(BorderFactory.createTitledBorder("Axis Settings"));
		return jpanel;
	}

	private Container createAxisPane() {
		JPanel jpanel = new JPanel(new GridBagLayout());
		jpanel.add(createAxisControls());
		return jpanel;
	}

	private void getAxisFields() {
		ModelView.AxesDefinition axesdefinition = modelView.axesDefinition;
		for (int i = 0; i < 3; i++) {
			axisShown[i].setSelected(axesdefinition.shown[i]);
			spnAxesMin[i].setValue(new Double(axesdefinition.min[i]));
			spnAxesMax[i].setValue(new Double(axesdefinition.max[i]));
		}

		axesColor.setColor(axesdefinition.color);
		spnAxesIncr.setValue(new Double(axesdefinition.incr));
		spnAxesTicks.setValue(new Double(axesdefinition.tickDensity));
		spnAxesWidth.setValue(new Integer(axesdefinition.width));
	}

	private void setAxisFields() {
		boolean flag = false;
		ModelView.AxesDefinition axesdefinition = modelView.axesDefinition;
		for (int j = 0; j < 3; j++) {
			double d = ((Double) spnAxesMin[j].getValue()).doubleValue();
			if (axesdefinition.min[j] != d) {
				axesdefinition.min[j] = d;
				flag = true;
			}
			double d2 = ((Double) spnAxesMax[j].getValue()).doubleValue();
			if (axesdefinition.max[j] != d2) {
				axesdefinition.max[j] = d2;
				flag = true;
			}
			if (d2 <= d)
				axisShown[j].setSelected(false);
			if (axisShown[j].isSelected() != axesdefinition.shown[j]) {
				axesdefinition.shown[j] = axisShown[j].isSelected();
				flag = true;
			}
		}

		if (!axesdefinition.color.equals(axesColor.getColor())) {
			axesdefinition.color = axesColor.getColor();
			flag = true;
		}
		double d1 = ((Double) spnAxesIncr.getValue()).doubleValue();
		if (axesdefinition.incr != d1) {
			axesdefinition.incr = d1;
			flag = true;
		}
		d1 = ((Double) spnAxesTicks.getValue()).doubleValue();
		if (axesdefinition.tickDensity != d1) {
			axesdefinition.tickDensity = d1;
			flag = true;
		}
		int i = ((Integer) spnAxesWidth.getValue()).intValue();
		if (axesdefinition.width != i) {
			axesdefinition.width = i;
			flag = true;
		}
		if (flag && modelView.bShowAxes) {
			modelView.repaint();
			dirty = true;
		}
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
		jbutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				if (jtpMain.getSelectedIndex() == 0)
					setGlobalFields();
				else if (jtpMain.getSelectedIndex() == 1) {
					setFunctionFields();
					textFuncDef.requestFocus();
				} else {
					setAxisFields();
				}
			}

		});
		jbutton1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				if (jtpMain.getSelectedIndex() == 0)
					setGlobalFields();
				else if (jtpMain.getSelectedIndex() == 1)
					setFunctionFields();
				else
					setAxisFields();
				dispose();
			}

		});
		getRootPane().setDefaultButton(jbutton1);
		jpanel.setMaximumSize(new Dimension(0x7fffffff, jbutton1.getHeight()));
		return jpanel;
	}

	public void reLoadAll() {
		lastFunction = null;
		comboFunction.removeAllItems();
		for (int i = 0; i < functions.size(); i++)
			comboFunction.addItem(functions.getFunction(i));

		lastFunction = functions.getFunction(0);
		getFunctionFields();
		getGlobalFields();
		getAxisFields();
	}

	public OptionsDialog(Frame frame, ModelView modelview) {
		super("Functions Editor");
		dirty = false;
		axisShown = new JCheckBox[3];
		spnAxesMin = new JSpinner[3];
		spnAxesMax = new JSpinner[3];
		parentFrame = frame;
		modelView = modelview;
		functions = modelview.functions;
		jtpMain = new JTabbedPane();
		jtpMain.add("Global", createGlobalPane());
		jtpMain.add("Functions", createFunctionsPane());
		jtpMain.add("Axes", createAxisPane());
		jtpMain.setSelectedIndex(1);

		jtpMain.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent changeevent) {
				if (nLastIndex == 0)
					setGlobalFields();
				else if (nLastIndex == 1)
					setFunctionFields();
				else
					setAxisFields();
				nLastIndex = jtpMain.getSelectedIndex();
			}

			int nLastIndex;

			{
				nLastIndex = 1;
			}
		});

		Box box = new Box(1);
		box.add(jtpMain);
		box.add(Box.createVerticalStrut(3));
		box.add(createDialogButtons());
		getContentPane().add(box);
		setSize(500, 300);
		setLocationRelativeTo(null);
		reLoadAll();

	}

	private Frame parentFrame;
	private ModelView modelView;
	private ModelView.FunctionsList functions;
	private boolean dirty;
	private JComboBox comboFunction;
	private JLabel lblFunctions;
	private JToggleButton tglIsCurve;
	private JCheckBox chkVisible;
	private JButton btnAdd;
	private JButton btnDelete;
	private JSpinner spinUSteps;
	private JSpinner spinVSteps;
	private JSpinner spinPenWidth;
	private Container paneUSteps;
	private Container paneVSteps;
	private Container panePenWidth;
	private Container paneSurfaceColor;
	private JTextArea textFuncDef;
	private JCheckBox chkFillSurface;
	private JColor curveColor;
	private JColor surfaceColor;
	private JSlider sldAlpha;
	private JLabel lblArea;
	private JComponent containerFunc;
	private JTabbedPane jtpMain;
	private ModelView.ModelFunction lastFunction;
	private JButton btnGoLeft;
	private JButton btnGoRight;
	private JPanel jpanVStepsFill;
	private JCheckBox chkAbsoluteWidth;
	private JTextField jtfTitle;
	private JColor bgColor;
	private JCheckBox chkFog;
	private JSpinner spinFogStart;
	private JSpinner spinFogEnd;
	private JSpinner spinBoxWidth;
	private JSpinner spinBoxHeight;
	private JSpinner spinFOV;
	private JCheckBox axisShown[];
	private JSpinner spnAxesMin[];
	private JSpinner spnAxesMax[];
	private JColor axesColor;
	private JSpinner spnAxesIncr;
	private JSpinner spnAxesTicks;
	private JSpinner spnAxesWidth;

}
