package jplot3dp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

import jhplot.FPR;

import jplot3dp.HelpDialog;
import jplot3dp.ModelView;
import jplot3dp.OptionsDialog;
import jplot3dp.Utils;

public class MainComponent extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void handleCommand(String s) {
		if (s.equals("FileNew")) {
			if (!isDirty("Do you want to save before creating a new file?"))
				newFile();
		} else if (s.equals("FileOpen"))
			loadFromFile();
		else if (s.equals("FileSave"))
			saveToFile(false);
		else if (s.equals("FileSaveAs"))
			saveToFile(true);
		else if (s.equals("ExportPNG"))
			exportPNG();
		else if (s.equals("TogCulling")) {
			modelView.backCulling = 1 - modelView.backCulling;
			btnSolid.setSelected(modelView.backCulling != 0);
			modelView.repaint();
		} else if (s.equals("TogAxes")) {
			modelView.bShowAxes = !modelView.bShowAxes;
			btnAxes.setSelected(modelView.bShowAxes);
			modelView.repaint();
		} else if (s.equals("EditScene"))
			optionsDialog.setVisible(true);
		else if (s.equals("HelpReadme"))
			helpDialog.setVisible(true);
		else
			JOptionPane.showMessageDialog(this,
					"Invalid command passed to handler: " + s, "Error", 0);
	}

	public boolean isDirty(String s) {
		if (Utils.applet != null || !optionsDialog.isDirty())
			return false;
		int i = JOptionPane.showConfirmDialog(this, s, "File Modified", 1, 2);
		switch (i) {
		case 0: // '\0'
			if (saveToFile(false))
				return false;
			break;

		case 1: // '\001'
			return false;
		}
		return true;
	}

	/**
	 * set function
	 * 
	 */

	public void setFunction(FPR func) {
		
		if (func.getDivU() == 0 || func.getDivV()  == 0)
 			func.setDivisions(21, 21);
		

		if (optionsDialog != null) {
			optionsDialog.addFunction(func);
			optionsDialog.reLoadAll();
			optionsDialog.setDirty(false);
		}

		modelView.bShowAxes = true;
		btnAxes.setSelected(modelView.bShowAxes);

		modelView.backCulling = 1;
		btnSolid.setSelected(modelView.backCulling != 0);

		modelView.repaint();

	}

	public ModelView getModel() {
		return modelView;
	}

	public void setBackgroundFrame(Color c) {
		modelView.setBackgroundFrame(c);
		modelView.repaint();
	}

	public void setFog(boolean fogEnabled) {
		modelView.setFog(fogEnabled);
		modelView.repaint();
	}

	public void setAxes(boolean bShowAxes) {
		modelView.bShowAxes = bShowAxes;
		btnAxes.setSelected(modelView.bShowAxes);

	}

	public void setFill(boolean bfill) {

		modelView.backCulling = 0;
		if (bfill)
			modelView.backCulling = 1;
		btnSolid.setSelected(modelView.backCulling != 0);

	}

	// set ZOOM
	public double getFov() {
		return optionsDialog.getFov();
	}

	
	public void setFillColor(Color c) {
		optionsDialog.setFillColor(c);
	}

	public void setLineColor(Color c) {
		optionsDialog.setLineColor(c);
	}
	
	
	
	// set ZOOM
	public void setFov(double d) {
		optionsDialog.setFov(d);
	}

	private void newFile() {
		modelView.reInitializeVars();

		/*
		 * 
		 * String as[] = {
		 * "ir=.3+.1*sin(4*Pi*u)\nr=ir*sin(2*Pi*v)+.5\nx=r*sin(2*Pi*u)\ny=r*cos(2*Pi*u)\nz=1.5*ir*cos(Pi*v)",
		 * "u=-2+4u;
		 * v=-2+4v\n\nx=u-(u*u*u/3)+u*v*v\ny=v-(v*v*v/3)+u*u*v\nz=u*u-v*v\n\nn=10;
		 * x=x/n; y=y/n; z=z/n",
		 * "ang=atan2(y,x)\nr2=x*x+y*y\nz=sin(5(ang-r2/3))*r2/3" };
		 * 
		 */

		ModelView.ModelFunction modelfunction = modelView.functions
				.addFunction();

		modelfunction.expression = "x=0; y=0; z=0;";
		modelfunction.gridDivsU = 21;
		modelfunction.gridDivsV = 21;
		modelfunction.surfaceColor = Color.WHITE;
		modelfunction.parseFunction();
		if (optionsDialog != null) {
			optionsDialog.reLoadAll();
			optionsDialog.setDirty(false);
		}

		modelView.bShowAxes = false;
		btnAxes.setSelected(modelView.bShowAxes);

		modelView.backCulling = 1;
		btnSolid.setSelected(modelView.backCulling != 0);

		setLastFileName(null);
		// modelView.repaint();
		// btnSolid.setSelected(modelView.backCulling != 0);
		// btnAxes.setSelected(modelView.bShowAxes);

	}

	private void setLastFileName(String s) {
		lastFileName = s;
		if (parentFrame != null)
			if (s == null)
				parentFrame.setTitle("HPlot3DP");
			else
				parentFrame.setTitle("HPlot3DP - "
						+ s.substring(s.lastIndexOf(File.separatorChar) + 1));
	}

	private String getFileName(boolean flag, String s) {
		JFileChooser jfilechooser = new JFileChooser();
		jfilechooser.setCurrentDirectory(new File(
				lastDirectory != null ? lastDirectory : System
						.getProperty("user.dir")));
		int i;
		if (flag)
			i = jfilechooser.showSaveDialog(this);
		else
			i = jfilechooser.showOpenDialog(this);
		if (i == 0) {
			String s1 = jfilechooser.getSelectedFile().getPath();
			lastDirectory = s1;
			lastDirectory = lastDirectory.substring(0, lastDirectory
					.lastIndexOf(File.separatorChar));
			if (flag && s1.indexOf('.') == -1)
				s1 = s1 + s;
			if (flag
					&& (new File(s1)).exists()
					&& JOptionPane.showConfirmDialog(this, "File " + s1
							+ " already exists. Overwrite?", "Warning", 0, 2) == 1)
				return null;
			else
				return s1;
		} else {
			return null;
		}
	}

	public void loadFromFile() {
		loadFromFile(getFileName(false, null));
	}

	public void loadFromFile(String s) {
		if (s == null)
			return;

		DataInputStream datainputstream;

		try {

			datainputstream = new DataInputStream(new FileInputStream(s));
			int i = datainputstream.readInt() - saveSignature;
			if (i != 1) {
				JOptionPane.showMessageDialog(this, "File " + s
						+ " doesn't conform save format.", "Error", 2);
				return;
			}
			modelView.readFromStream(datainputstream);
			btnSolid.setSelected(modelView.backCulling != 0);
			btnAxes.setSelected(modelView.bShowAxes);
			optionsDialog.reLoadAll();
			optionsDialog.setDirty(false);
			modelView.repaint();
			setLastFileName(s);
		} catch (IOException ioexception) {
			JOptionPane.showMessageDialog(this,
					"Error while loading from file " + s, "Error", 0);
		}
		return;
	}

	public boolean saveToFile(boolean bool) {

		String string;
		if (bool || lastFileName == null)
			string = getFileName(true, ".uvm");
		else
			string = lastFileName;
		if (string == null)
			return false;
		boolean bool_4_;
		try {
			DataOutputStream dataoutputstream = new DataOutputStream(
					new FileOutputStream(string));
			dataoutputstream.writeInt(saveSignature + 1);
			modelView.writeToStream(dataoutputstream);
			setLastFileName(string);
			optionsDialog.setDirty(false);
			bool_4_ = true;
		} catch (IOException ioexception) {
			JOptionPane.showMessageDialog(this, "Could not save to file "
					+ string, "Warning", 2);
			return false;
		}
		return bool_4_;

	}

	private void exportPNG() {
		String s = getFileName(true, ".png");
		if (s == null)
			return;
		try {
			modelView.saveImage(s);
		} catch (IOException ioexception) {
			JOptionPane.showMessageDialog(this,
					"Could not export to file " + s, "Warning", 2);
		}
	}

	public void update() {

		modelView.repaint();
	}

	public MainComponent(Frame frame) {
		saveSignature = 0x55560000;
		lastFileName = null;
		lastDirectory = null;
		parentFrame = frame;
		modelView = new ModelView();

		if (Utils.applet == null)
			helpDialog = new HelpDialog("3D Graph Explorer Readme",
					"readme/readme.html");
		ActionListener actionlistener = new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				handleCommand(((AbstractButton) actionevent.getSource())
						.getActionCommand());
			}

		};

		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout());

		JToolBar jtoolbar = new JToolBar();
		jtoolbar.add(btnNew = new JButton(Utils.loadIcon("filenew.gif")));
		Utils.makeHot(btnNew);
		btnNew.setToolTipText("New File");
		btnNew.setActionCommand("load");
		btnNew.setActionCommand("FileNew");
		btnNew.addActionListener(actionlistener);
		if (Utils.applet == null) {
			jtoolbar.add(btnLoad = new JButton(Utils.loadIcon("fileopen.gif")));
			Utils.makeHot(btnLoad);
			btnLoad.setToolTipText("Open File");
			btnLoad.setActionCommand("FileOpen");
			btnLoad.addActionListener(actionlistener);
			jtoolbar.add(btnSave = new JButton(Utils.loadIcon("filesave.gif")));
			Utils.makeHot(btnSave);
			btnSave.setToolTipText("Save File");
			btnSave.setActionCommand("FileSave");
			btnSave.addActionListener(actionlistener);
			jtoolbar.add(btnSaveAs = new JButton(Utils
					.loadIcon("filesaveas.gif")));
			Utils.makeHot(btnSaveAs);
			btnSaveAs.setToolTipText("Save File As...");
			btnSaveAs.setActionCommand("FileSaveAs");
			btnSaveAs.addActionListener(actionlistener);
			jtoolbar.add(btnExportPng = new JButton(Utils
					.loadIcon("fileexportpng.gif")));
			Utils.makeHot(btnExportPng);
			btnExportPng.setToolTipText("Export Picture");
			btnExportPng.setActionCommand("ExportPNG");
			btnExportPng.addActionListener(actionlistener);
		}
		jtoolbar.addSeparator();
		jtoolbar
				.add(btnSolid = new JToggleButton(Utils.loadIcon("hollow.gif")));
		Utils.makeHot(btnSolid);
		btnSolid.setToolTipText("Toggle Wireframe/Solid");
		btnSolid.setSelectedIcon(Utils.loadIcon("solid.gif"));
		btnSolid.setActionCommand("TogCulling");
		btnSolid.addActionListener(actionlistener);
		jtoolbar.add(btnAxes = new JToggleButton(Utils.loadIcon("axes.gif")));
		Utils.makeHot(btnAxes);
		btnAxes.setToolTipText("Toggle Axes");
		btnAxes.setActionCommand("TogAxes");
		btnAxes.addActionListener(actionlistener);
		jtoolbar.addSeparator();
		jtoolbar.add(btnEdit = new JButton(Utils.loadIcon("edit.gif")));
		Utils.makeHot(btnEdit);
		btnEdit.setToolTipText("Edit Scene");
		btnEdit.setActionCommand("EditScene");
		btnEdit.addActionListener(actionlistener);
		newFile();

		javax.swing.border.Border border = null;
		if (Utils.applet == null)
			border = BorderFactory.createBevelBorder(1);
		else
			border = BorderFactory.createLineBorder(Color.BLACK);

		// javax.swing.border.Border border = null;
		modelView.setBorder(BorderFactory.createEmptyBorder());
		optionsDialog = new OptionsDialog(parentFrame, modelView);

		modelView.item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				handleCommand("EditScene");
			}
		});

		modelView.item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				handleCommand("TogAxes");
			}
		});

		modelView.item3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				handleCommand("TogCulling");
			}
		});

		modelView.item4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				modelView.repaint();
			}
		});

		modelView.item5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveToFile(true);
			}
		});

		modelView.item6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				loadFromFile();
			}
		});

		modelView.item7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				helpDialog.setVisible(true);
			}
		});

		add(modelView, "Center");
		// chekanov
		// add(jtoolbar, "North");
	}

	public ModelView modelView;
	private HelpDialog helpDialog;
	private Frame parentFrame;
	private OptionsDialog optionsDialog;
	private int saveSignature;
	private String lastFileName;
	private String lastDirectory;

	private JButton btnNew;
	private JButton btnLoad;
	private JButton btnSave;
	private JButton btnSaveAs;
	private JButton btnExportPng;
	private JButton btnEdit;

	private JToggleButton btnSolid;
	private JToggleButton btnAxes;
}
