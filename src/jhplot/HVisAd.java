/**
 *    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
 *    All rights reserved.
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms
 *    of the GNU General Public License as published by the Free Software Foundation; either
 *    version 3 of the License, or any later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *    See the GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License along with this program;
 *    if not, see <http://www.gnu.org/licenses>.
 *
 *    Additional permission under GNU GPL version 3 section 7:
 *    If you have received this program as a library with written permission from the DataMelt team,
 *    you can link or combine this library with your non-GPL project to convey the resulting work.
 *    In this case, this library should be considered as released under the terms of
 *    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
 *    provided you include this license notice and a URL through which recipients can access the
 *    Corresponding Source.
 **/

package jhplot;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import javax.swing.*;
import jhplot.gui.HelpBrowser;
import visad.*;
import visad.java2d.*;
import visad.java3d.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.render.ps.PSTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.freehep.graphicsbase.util.export.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Interactive canvas for visualizing and experimenting with a variety of
 * mathematical objects in 2D and 3D using the VisAd environment.
 * 
 * @author S.Chekanov
 * 
 */

public class HVisAd extends JFrame {

	private static final long serialVersionUID = 1L;
	private DisplayImpl display;
	private DisplayRenderer render;
	private boolean is3D = true;
	private int xsize = 600;
	private int ysize = 600;
	private GraphicsModeControl dispGMC;
	private MouseBehavior mouseBehavior;
	private ProjectionControl proj;
	private ScalarMap rX,rY,rZ;
        private RealType xx,yy,zz;
        private Color background=Color.blue;
        private Color foreground=Color.gray;

	/**
	 * Create 2D/3D canvas based on VisAd classes.
	 * 
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 * 
	 * @param title
	 *            name of this canvas.
	 * @param is3D
	 *            set to true for 3D canvas. If false, assume 2D.
	 */
	public HVisAd(String title, int xsize, int ysize, boolean is3D) {
		this.is3D = is3D;
		this.xsize = xsize;
		this.ysize = ysize;

		if (is3D) {

			try {
				display = new DisplayImplJ3D(title);
				display.getGraphicsModeControl().setPointSize(4.0f);
				DisplayRendererJ3D render = (DisplayRendererJ3D) display.getDisplayRenderer();
				KeyboardBehaviorJ3D key = new KeyboardBehaviorJ3D(render);
				render.addKeyboardBehavior(key); // use shift and arrows to
				dispGMC = (GraphicsModeControl) display.getGraphicsModeControl();
				dispGMC.setScaleEnable(true);
				// Set the display background color
				render.setBackgroundColor(background);
                                render.setCursorColor(Color.red);
                                render.setForegroundColor(foreground);
				// Set box on or off: you choose
                                render.setBoxOn(false);
				proj = render.getDisplay().getProjectionControl();
				mouseBehavior = render.getMouseBehavior();
				double[] tstart = proj.getMatrix();
				double scale = 0.9;
				double[] t1 = mouseBehavior.make_matrix(0.0, 0.0, 0.0, scale, 0.0, 0.0, 0.0);
				t1 = mouseBehavior.multiply_matrix(t1, tstart);
				proj.setMatrix(t1);
                                this.render = (DisplayRendererJ3D)render;

			} catch (RemoteException | VisADException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			try {
				display = new DisplayImplJ2D(title);
				DisplayRendererJ2D render = (DisplayRendererJ2D) display.getDisplayRenderer();
				KeyboardBehaviorJ2D key = new KeyboardBehaviorJ2D((DisplayRendererJ2D)render);
				render.addKeyboardBehavior(key); // use shift and arrows to
                                dispGMC = (GraphicsModeControl) display.getGraphicsModeControl();
                                dispGMC.setScaleEnable(true);
                                // Set the display background color
                                render.setBackgroundColor(background);
                                render.setCursorColor(Color.red);
                                render.setForegroundColor(foreground);
                                // Set box on or off: you choose
                                render.setBoxOn(false);
                                proj = render.getDisplay().getProjectionControl();
                                double[] tstart = proj.getMatrix();
                                mouseBehavior = render.getMouseBehavior();
                                double scale = 0.9;
                                double[] t1 = mouseBehavior.make_matrix(0.0, 0.0, 0.0, scale, 0.0, 0.0, 0.0);
                                t1 = mouseBehavior.multiply_matrix(t1, tstart);
                                proj.setMatrix(t1);
                                this.render = (DisplayRendererJ2D)render;

			} catch (RemoteException | VisADException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		setTitle(title);
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenu help = new JMenu("Help");

		JMenuItem item1 = new JMenuItem("Export");
		item1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportDialog();
			}
		});

		JMenuItem item2 = new JMenuItem("Close");
		item2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		JMenuItem item3 = new JMenuItem("Help");
		item3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				help();
			}
		});

		menu.add(item1);
		menu.add(item2);
		help.add(item3);
		bar.add(menu);
		bar.add(help);
		setJMenuBar(bar);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().add(display.getComponent());
		setSize(xsize, ysize);

	}

	/**
	 * Create 3D canvas based on VisAd classes.
	 * 
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 * 
	 * @param title
	 *            name of this canvas.
	 */
	public HVisAd(String title, int xsize, int ysize) {
		this(title, xsize, ysize, true);
	}


        /**
         * Create 2D/3D canvas based on VisAd classes.
         * 
         * @param title
         *            name of this canvas.
         * @param is3D
         *            set to true for 3D canvas. If false, assume 2D.
         */
        public HVisAd(String title, boolean is3D) {

             this(title, 600, 600, is3D);

        }

	/**
	 * Get current display.
	 * 
	 * @return current display
	 */
	public DisplayImpl getDisplay() {
		return display;
	}

	/**
	 * Set the scaling of the box.
	 * 
	 * @param scale
	 *            factor
	 */
	public void setScaling(double scale) {

		proj = render.getDisplay().getProjectionControl();
		mouseBehavior = render.getMouseBehavior();
		double[] tstart = proj.getMatrix();
		double[] t1 = mouseBehavior.make_matrix(0.0, 0.0, 0.0, scale, 0.0, 0.0, 0.0);
		t1 = mouseBehavior.multiply_matrix(t1, tstart);
		try {
			proj.setMatrix(t1);
		} catch (VisADException | RemoteException e) {
			System.err.println(e.toString());
		}

	}

	/**
	 * Get the scaling.
	 * 
	 * @return scaling factor.
	 */
	public double getScaling() {
		proj = render.getDisplay().getProjectionControl();
		double[] tstart = proj.getMatrix();
		return tstart[2];
	}

	/**
	 * Set the rotation angles in degrees.
	 * 
	 * @param rotX
	 *            rotation in X
	 * @param rotY
	 *            rotation in Y
	 * @param rotZ
	 *            rotation in Z
	 */
	public void setRotation(double rotX, double rotY, double rotZ) {
		proj = render.getDisplay().getProjectionControl();
		mouseBehavior = render.getMouseBehavior();
		double[] tstart = proj.getMatrix();
		double[] t1 = mouseBehavior.make_matrix(rotX, rotY, rotZ, 1.0, 0.0, 0.0, 0.0);
		t1 = mouseBehavior.multiply_matrix(t1, tstart);
		try {
			proj.setMatrix(t1);
		} catch (VisADException | RemoteException e) {
			System.err.println(e.toString());
		}

	}

	/**
	 * Set the translation (0-1 values)
	 * 
	 * @param X
	 *            translate in X
	 * @param Y
	 *            translate in Y
	 * @param Z
	 *            translate in Z
	 */
	public void setTranslation(double X, double Y, double Z) {
		proj = render.getDisplay().getProjectionControl();
		mouseBehavior = render.getMouseBehavior();
		double[] tstart = proj.getMatrix();
		double[] t1 = mouseBehavior.make_matrix(0, 0, 0, 1.0, X, Y, Z);
		t1 = mouseBehavior.multiply_matrix(t1, tstart);

		try {
			proj.setMatrix(t1);
		} catch (VisADException | RemoteException e) {
			System.err.println(e.toString());
		}

	}

	/**
	 * Get the rotation angles in degrees.
	 * 
	 * @return rotation in X, Y,Z
	 */
	public double[] getRotation() {
		proj = render.getDisplay().getProjectionControl();
		mouseBehavior = render.getMouseBehavior();
		double[] tstart = proj.getMatrix();
		double[] rotA = { tstart[0], tstart[1], tstart[2] };
		return rotA;
	}

	/**
	 * Get the translation.
	 * 
	 * @return translation in X, Y,Z
	 */
	public double[] getTranslation() {
		proj = render.getDisplay().getProjectionControl();
		mouseBehavior = render.getMouseBehavior();
		double[] tstart = proj.getMatrix();
		double[] rotA = { tstart[4], tstart[5], tstart[6] };
		return rotA;
	}

	/**
	 * Get current projection.
	 * 
	 * @return current projection.
	 */
	public ProjectionControl getProjection() {
		return proj;
	}

	/**
	 * Get mouse behaviour.
	 * 
	 * @return current behaviour of mouse.
	 */
	public MouseBehavior getMouse() {
		return mouseBehavior;
	}

	/**
	 * Get control of this display.
	 * 
	 * @return control
	 */
	public GraphicsModeControl getControl() {
		return dispGMC;
	}

	/**
	 * Get render of this display.
	 * 
	 * @return render class.
	 */
	public DisplayRenderer getRender() {
		return display.getDisplayRenderer();
	}

	/**
	 * Construct a 3D canvas with a plot with the default parameters. It assumes
	 * 600x600 size and 3D display.
	 */
	public HVisAd() {
		this("VisAd", 600, 600, true);
	}

	/**
	 * Construct a 3D canvas with a plot with the default parameters. It assumes
	 * 600x400 size and 3D display.
	 * 
	 * @param title
	 *            name of this canvas.
	 */
	public HVisAd(String title) {
		this(title, 600, 600, true);
	}

	/**
	 * Clear the current graph including graph settings. Note: the current graph
	 * is set by the cd() method
	 */
	public void clear() {
		// window.dispose();
	}

	/**
	 * Close the canvas (and dispose all components) Note: a memory leak is
	 * found - no time to study it. set to null all the stuff
	 */
	public void close() {
		setVisible(false);
		dispose();

	}

	
	/**
	 * Draw 3D data points.
	 * @param p input array
	 */
	public void draw(P2D p){
          int nn=p.size();
	  float[][] ntuple= new float[3][nn];
          for (int i=0; i<nn; i++){
                 ntuple[0][i]=(float)p.getX(i);
                 ntuple[1][i]=(float)p.getY(i);
                 ntuple[2][i]=(float)p.getZ(i);
             }  	
	
            draw(ntuple, p.getTitle());
		
	}


         /**
         * Draw 3D data points.
         * @param p3d input array 
         */
        public void draw(float[][] ntuple){
              draw(ntuple, "default");
        }



        /**
         * Draw 3D data points.
         * @param p3d input array 
         * @param title title
         */
        public void draw(float[][] ntuple, String title){


                RealTupleType domain_tuple;

                try {
                        domain_tuple = new  RealTupleType(xx, yy, zz);
                        Irregular3DSet domain_set = new Irregular3DSet(domain_tuple, ntuple);
                        DataReferenceImpl data_ref = new DataReferenceImpl( title );
                        data_ref.setData( domain_set );
                        display.addReference( data_ref );

                        display.addMap(rX);
                        display.addMap(rY);
                        display.addMap(rZ);

                } catch (VisADException | RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }



        }



	
	/**
	 * Attach axes to the canvas, and snap them o the box. You can also set min
	 * and max values for the axis.
	 * @param nameX
	 *            Label for X axis
	 * @param minX
	 *            min value of X
	 * @param maxX
	 *            max value of X
	 * @param nameY
	 *            Label for Y axis
	 * @param minY
	 *            min value of Y
	 * @param maxY
	 *            max value of Y
	 * @param nameZ
	 *            Label of Z axis
	 * @param minZ
	 *            min value on Z
	 * @param maxZ
	 *            max value on Z
	 * @return array with  3 objects, which represent  AxisScale to redefine axes.
	 *            
	 */
	public ArrayList<AxisScale> setAxes(String nameX, double minX, double maxX, String nameY, double minY, double maxY,
			String nameZ, double minZ, double maxZ) {

		xx = RealType.getRealType("x",null,null);
		yy = RealType.getRealType("y",null,null);
		zz = RealType.getRealType("z",null,null);

             

		float[] col = { 0.1f, 0.1f, 0.1f }; // color for axis
		Font ff = new Font("Arial", Font.PLAIN, 16);

		try {


			rX = new ScalarMap(xx, Display.XAxis);
			rY = new ScalarMap(yy, Display.YAxis);
			rZ = new ScalarMap(zz, Display.ZAxis);

			rX.setScaleColor(col);
			rY.setScaleColor(col);
			rZ.setScaleColor(col);

			try {
				if (maxX>minX) rX.setRange(minX, maxX);
				if (maxY>minY) rY.setRange(minY, maxY);
				if (maxZ>minZ) rZ.setRange(minZ, maxZ);



				AxisScale axisX = rX.getAxisScale();
				axisX.setFont(ff);
				axisX.setLabelSize(16);
				axisX.setSnapToBox(true);
                                axisX.setColor(foreground);

				AxisScale axisY = rY.getAxisScale();
				axisY.setFont(ff);
				axisY.setLabelSize(16);
				axisY.setSnapToBox(true);
                                axisY.setColor(foreground);

				AxisScale axisZ = rZ.getAxisScale();
				axisZ.setFont(ff);
				axisZ.setLabelSize(16);
				axisZ.setSnapToBox(true);
                                axisZ.setColor(foreground);

				//display.addMap(rX);
				//display.addMap(rY);
				//display.addMap(rZ);

				ArrayList<AxisScale> xyz = new ArrayList<AxisScale>();
				xyz.add(axisX);
				xyz.add(axisY);
				xyz.add(axisZ);

				return xyz;

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (VisADException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		// updateAll();
		if (vs == false) {
			setVisible(false);
		} else {
			setSize(xsize, ysize);
			setVisible(true);
		}
	}

	/**
	 * Update the canvas
	 * 
	 */

	public void update() {
		display.reDisplayAll();
		validate();
	}

	/**
	 * Show the canvas
	 */
	public void visible() {
		visible(true);
	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	/**
	 * 
	 * Export graph into an image file. The the image format is given by
	 * extension. "png", "jpg", "eps", "pdf", "svg". In case of "eps", "pdf" and
	 * "svg", vector graphics is used.
	 * 
	 * @param file
	 *            name of the image
	 */
	public void export(String filename) {
		int calculatedWidth = (int) xsize;
		int calculatedHeight = (int) ysize;

		if (calculatedWidth <= 0)
			calculatedWidth = 600;

		if (calculatedHeight <= 0)
			calculatedHeight = 400;

		this.export(filename, calculatedWidth, calculatedHeight);
	}

	/**
	 * Export graph into an image file. The the image format is given by
	 * extension. "png", "jpg", "eps", "pdf", "svg". In case of "eps", "pdf" and
	 * "svg", vector graphics is used.
	 * 
	 * @param filename
	 *            file name
	 * @param width
	 *            width
	 * @param height
	 *            hight
	 */
	public void export(String filename, int width, int height) {

		File f = new File(filename);
		ExportFileType t = null;

		int dot = filename.lastIndexOf('.');
		String base = (dot == -1) ? filename : filename.substring(0, dot);
		String fext = (dot == -1) ? "" : filename.substring(dot + 1);
		fext = fext.trim();

		List<ExportFileType> list = ExportFileType.getExportFileTypes();
		Iterator<ExportFileType> iterator = list.iterator();
		while (iterator.hasNext()) {
			t = (ExportFileType) iterator.next();
			String[] ext = t.getExtensions();
			if (fext.equalsIgnoreCase(ext[0]))
				break; // not svgz

			// overwrite this since we use svn conversions
			if (ext[0].equalsIgnoreCase("svg"))
				break;
			if (fext.equalsIgnoreCase(ext[0]))
				break; // not any of the above
			t = null;
		}

		String rootKey = "";
		final String SAVE_AS_TYPE = rootKey + ".SaveAsType";
		final String SAVE_AS_FILE = rootKey + ".SaveAsFile";
		final Properties props = new Properties();
		String a = t.getDescription();
		props.put(SAVE_AS_FILE, filename);
		props.put(SAVE_AS_TYPE, a);
		final String metadata = "(C) DataMelt (https://datamelt.org/). S.Chekanov";

		String fname = filename;
		String filetype = "pdf";
		int i = filename.lastIndexOf('.');
		if (i > 0) {
			filetype = fname.substring(i + 1);
		}

		java.awt.Component cp = display.getComponent();

		try {
			if (filetype.equalsIgnoreCase("png")) {

				DisplayRenderer ren = display.getDisplayRenderer();
				BufferedImage b = ren.getImage();
				ImageIO.write(b, "png", new File(fname));

			} else if (filetype.equalsIgnoreCase("jpg") || filetype.equalsIgnoreCase("jpeg")) {

				DisplayRenderer ren = display.getDisplayRenderer();
				BufferedImage b = ren.getImage();
				ImageIO.write(b, "jpg", new File(fname));

			} else if (filetype.equalsIgnoreCase("eps")) {

				/*
				 * ImageType currentImageType = ImageType.EPS; Rectangle r = new
				 * Rectangle (0, 0, width, height); Export.exportComponent(cp,
				 * r, new File(filename), currentImageType); } catch
				 * (IOException e) { e.printStackTrace(); } catch
				 * (org.apache.batik.transcoder.TranscoderException e) {
				 * e.printStackTrace(); }
				 * 
				 */

				ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
				t.exportToFile(bytes2, (Component) cp, (Component) cp, props, metadata);
				bytes2.flush();
				byte[] bb = bytes2.toByteArray();
				bytes2.close();
				if (bb == null)
					return;
				if (bb.length < 1)
					return;
				TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bb));
				FileOutputStream os = new FileOutputStream(filename);
				EPSTranscoder trans = new EPSTranscoder();
				TranscoderOutput output = new TranscoderOutput(os);
				trans.transcode(input, output);
				os.flush();
				os.close();

			} else if (filetype.equalsIgnoreCase("ps")) {

				JOptionPane.showMessageDialog(this, "PS format not supported");
				/*
				 * try { ImageType currentImageType = ImageType.PS; Rectangle r
				 * = new Rectangle(0, 0, width, height);
				 * Export.exportComponent(cp, r, new File( filename),
				 * currentImageType); } catch (IOException e) {
				 * e.printStackTrace(); } catch
				 * (org.apache.batik.transcoder.TranscoderException e) {
				 * e.printStackTrace(); }
				 */

			} else if (filetype.equalsIgnoreCase("pdf")) {
				JOptionPane.showMessageDialog(this, "PDF format not supported");

				/*
				 * 
				 * 
				 * try {
				 * 
				 * ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
				 * t.exportToFile(bytes2, cp, cp, props, metadata);
				 * bytes2.flush(); byte[] bb=bytes2.toByteArray();
				 * bytes2.close(); if (bb==null) return; if (bb.length<1)
				 * return;
				 * 
				 * TranscoderInput input = new TranscoderInput( new
				 * ByteArrayInputStream(bb)); FileOutputStream os = new
				 * FileOutputStream(filename); EPSTranscoder trans = new
				 * EPSTranscoder(); TranscoderOutput output = new
				 * TranscoderOutput(os); trans.transcode(input, output);
				 * os.flush(); os.close();
				 * 
				 * return; } catch (IOException e) { System.err.
				 * println("DataMelt ExportVGraphics: IOException: Error while converting  "
				 * + e.getLocalizedMessage()); } catch (TranscoderException e) {
				 * // System.err.
				 * println("DataMelt ExportVGraphics: Error while converting  "
				 * // + e.getLocalizedMessage()); }
				 * 
				 * 
				 * 
				 */

			} else if (filetype.equalsIgnoreCase("svg")) {
				JOptionPane.showMessageDialog(this, "SVG format not supported");
			}

		} catch (IOException | TranscoderException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Exports the image to some graphic format.
	 */
	protected void exportImage() {

		final JFileChooser fileChooser = new JFileChooser();

		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		// fileChooser.setCurrentDirectory(new
		// File(System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF
		// Documents (*.pdf)", "pdf"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("Encapsulated PostScript (*.eps)", "eps"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("PostScript (*.ps)", "ps"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("Scalable Vector Graphics (*.svg)", "svg"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("Compressed SVG (*.svgz)", "svgz"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		fileChooser.addChoosableFileFilter(
				new FileNameExtensionFilter("JPEG Raster Images (*.jpg, *.jpeg)", "jpg", "jpeg"));
		fileChooser.setAcceptAllFileFilterUsed(true);

		final File sFile = new File("dmelt.png");
		fileChooser.setSelectedFile(sFile);
		fileChooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				Object o = evt.getNewValue();
				if (o instanceof FileNameExtensionFilter) {
					FileNameExtensionFilter filter = (FileNameExtensionFilter) o;

					String ex = filter.getExtensions()[0];

					File selectedFile = fileChooser.getSelectedFile();
					if (selectedFile == null) {
						selectedFile = sFile;
					}
					String path = selectedFile.getName();
					path = path.substring(0, path.lastIndexOf("."));

					fileChooser.setSelectedFile(new File(path + "." + ex));
				}
			}
		});

		// JFileChooser fileChooser = jhplot.gui.CommonGUI
		// .openImageFileChooser(frame);

		if (fileChooser.showDialog(this, "Save As") == 0) {

			final File scriptFile = fileChooser.getSelectedFile();
			if (scriptFile == null)
				return;
			else if (scriptFile.exists()) {
				int res = JOptionPane.showConfirmDialog(this, "The file exists. Do you want to overwrite the file?", "",
						JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.NO_OPTION)
					return;
			}
			String mess = "write image  file ..";
			JHPlot.showStatusBarText(mess);
			Thread t = new Thread(mess) {
				public void run() {
					export(scriptFile.getAbsolutePath());
				};
			};
			t.start();
		}
	}

	/**
	 * Exports the image to some graphic format.
	 */
	private void exportDialog() {

		final JFileChooser fileChooser = new JFileChooser();

		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		// fileChooser.setCurrentDirectory(new
		// File(System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF
		// Documents (*.pdf)", "pdf"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("Encapsulated PostScript (*.eps)", "eps"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("PostScript (*.ps)", "ps"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("Scalable Vector Graphics (*.svg)", "svg"));
		// fileChooser.addChoosableFileFilter(new
		// FileNameExtensionFilter("Compressed SVG (*.svgz)", "svgz"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		fileChooser.addChoosableFileFilter(
				new FileNameExtensionFilter("JPEG Raster Images (*.jpg, *.jpeg)", "jpg", "jpeg"));
		fileChooser.setAcceptAllFileFilterUsed(true);

		final File sFile = new File("dmelt.png");
		fileChooser.setSelectedFile(sFile);
		fileChooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				Object o = evt.getNewValue();
				if (o instanceof FileNameExtensionFilter) {
					FileNameExtensionFilter filter = (FileNameExtensionFilter) o;

					String ex = filter.getExtensions()[0];

					File selectedFile = fileChooser.getSelectedFile();
					if (selectedFile == null) {
						selectedFile = sFile;
					}
					String path = selectedFile.getName();
					path = path.substring(0, path.lastIndexOf("."));

					fileChooser.setSelectedFile(new File(path + "." + ex));
				}
			}
		});

		if (fileChooser.showDialog(this, "Export to ") == 0) {
			// final File scriptFile = fileChooser.getSelectedFile();
			final File scriptFile = jhplot.io.images.ExportVGraphics.getSelectedFileWithExtension(fileChooser);
			// System.out.println(scriptFile.getAbsolutePath());

			if (scriptFile == null)
				return;
			else if (scriptFile.exists()) {
				int res = JOptionPane.showConfirmDialog(this, "The file exists. Do you want to overwrite the file?", "",
						JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.NO_OPTION)
					return;
			}
			String mess = "write image  file ..";
			Thread t = new Thread(mess) {
				public void run() {
					export(scriptFile.getAbsolutePath());
				};
			};
			t.start();
		}

	}

	public void help() {

		String shelp = "Help on 2D display of HVisAd";
		if (is3D)
			shelp = "Help on 3D display of HVisAd";
		String st = "<html><h2>" + shelp
				+ "</h2>Central mouse button: Show coordinates<p>Arrow keys: rotate up, down, left, right <p>Zoom in: Shift + Up arrow <p>Zoom out: Shift + Down arrow <p>Rotate: Shift + Left arrow (Right arrow) <p>Translate: Ctrl + Arrow keys - translate up, down, left, right <p>Reset: Ctrl + R key - reset to original projection <html>";
		JOptionPane.showMessageDialog(this, st);

	}

	// end
}
