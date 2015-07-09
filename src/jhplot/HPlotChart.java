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

import java.awt.*;
import java.util.ArrayList;
import java.io.File;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.jfree.chart.renderer.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.event.PlotChangeEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.*;
import java.awt.image.BufferedImage;
import jhplot.gui.HelpBrowser;
import jhplot.io.images.ImageType;
import jhplot.io.images.Export;
import org.freehep.graphicsbase.util.export.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;


/**
 * A canvas to keep jFreeChart charts, interact with them, and export
 * to images. This class can be used to export jFreeChart charts to vector
 * formats.
 *
 * @author S.Chekanov
 *
 */


public class HPlotChart {
	private static final long serialVersionUID = 1L;

	private int xsize;
	private int ysize;
        private JFrame frame;
	private static final String rootKey = HPlotChart.class.getName();
        private final Color DEFAULT_BG_COLOR = Color.white;
        private Thread1 m_Close;
        private JFreeChart chart;
        private ChartPanel cp;

	/**
	 * Create canvas that keeps JFreeChart panel. 
	 * 
	 * @param jchart 
	 *           chart of JFreeChart. 
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 */

	public HPlotChart(JFreeChart jchart, int xsize, int ysize) {

             this.xsize = xsize;
             this.ysize = ysize;
             frame=new JFrame("HPloatChart");

              chart = jchart;
              cp= new ChartPanel(chart);
              cp.setPreferredSize(new Dimension(xsize, ysize));
              cp.setBackground(DEFAULT_BG_COLOR);
              cp.setLayout(new BorderLayout());
              cp.setDomainZoomable(true);
              cp.setRangeZoomable(true);

              
              chart.setAntiAlias(true);
              chart.setBorderPaint(DEFAULT_BG_COLOR);
              chart.setBackgroundPaint(DEFAULT_BG_COLOR);
              chart.setBorderVisible(false);

              org.jfree.chart.plot.Plot pp = (org.jfree.chart.plot.Plot)chart.getPlot();
              pp.setBackgroundPaint(DEFAULT_BG_COLOR);

              setTheme("LEGACY_THEME");
              frame.add(cp);


	     JMenuBar bar = new JMenuBar();
	     JMenu menu = new JMenu("File");
	     JMenuItem item1 = new JMenuItem("Export");
             item1.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               exportDialog();
            }
        });

		JMenuItem item2 = new JMenuItem("Close");
                 item2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menu.add(item1);
		menu.add(item2);

		bar.add(menu);
		frame.setJMenuBar(bar);
		frame.setPreferredSize(new Dimension(xsize, ysize));

	}


	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {

		if (vs) {
			frame.pack();
			frame.setVisible(true);

		} else {
			frame.setVisible(false);
			frame.dispose();
		}

	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		visible(true);
	}

	/**
	 * Construct a HPlotXY canvas with a plot with the size 600x400.
	 * 
	 * @param jchart 
	 *            Chart to be shown. 
	 */
	public HPlotChart(JFreeChart jchart) {

		this(jchart, 600, 400);

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
	 * Close the canvas (and dispose all components).
	 */
	public void close() {
		frame.setVisible(false);
		chart = null;
		cp = null;
		frame.dispose();

	}


        /**
         * Get grame that keeps averything
         * @return panel with image
         */

        public JFrame getFrame(){

                return frame;
        }



        /**
         * Return panel with the chart.
         * @return panel with chart. 
         */
        public ChartPanel getPanel(){
            return cp;
        }

        /**
         * Return JFreeChart 
         * @return chart  
         */
        public JFreeChart getChart(){
            return chart;
        }


/**
	 * Set a custom theme for chart.. It can be: LEGACY_THEME, JFREE_THEME,
	 * DARKNESS_THEME
	 * 
	 * @param s
	 *            a theme, can be either LEGACY_THEME, JFREE_THEME,
	 *            DARKNESS_THEME
	 */
	public void setTheme(String s) {

		if (s.equals("LEGACY_THEME")) {
			ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
	                ChartUtilities.applyCurrentTheme(chart);
		} else if (s.equals("JFREE_THEME")) {
			ChartFactory.setChartTheme(StandardChartTheme.createJFreeTheme());
		        ChartUtilities.applyCurrentTheme(chart);
		} else if (s.equals("DARKNESS_THEME")) {
			ChartFactory
					.setChartTheme(StandardChartTheme.createDarknessTheme());
         	         ChartUtilities.applyCurrentTheme(chart);
	
		}
	}


	/**
	 * Remove the canvas frame
	 */
	public void destroy() {
		close();
	}


/**
	 * 
	 * @author S.Chekanov Aug 17, 2006 update plot showing centers and seeds
	 */
	class Thread1 implements Runnable {

		private Thread t = null;

		private String mess;

		Thread1(String s1) {
			mess = s1;

		}

		public boolean Alive() {
			boolean tt = false;
			if (t != null) {
				if (t.isAlive())
					tt = true;
			}
			return tt;
		}

		public boolean Joint() {
			boolean tt = false;
			try {
				t.join();
				return true; // finished

			} catch (InterruptedException e) {
				// Thread was interrupted
			}
			return tt;
		}

		public void Start() {
			t = new Thread(this, mess);
			t.start();

		}

		public void Stop() {
			t = null;
		}

		public void run() {
			close();
		}
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
	 * Exports the image to some graphic format.
	 */
	protected void exportImage() {

		JFrame jm = getFrame();
		JFileChooser fileChooser = jhplot.gui.CommonGUI
				.openImageFileChooser(jm);

		if (fileChooser.showDialog(jm, "Save As") == 0) {

			final File scriptFile = fileChooser.getSelectedFile();
			if (scriptFile == null)
				return;
			else if (scriptFile.exists()) {
				int res = JOptionPane.showConfirmDialog(jm,
						"The file exists. Do you want to overwrite the file?",
						"", JOptionPane.YES_NO_OPTION);
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

		String fname = filename;
		String filetype = "pdf";
		int i = filename.lastIndexOf('.');
		if (i > 0) {
			filetype = fname.substring(i + 1);
		}

		try {
			if (filetype.equalsIgnoreCase("png")) {
				BufferedImage b = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = b.createGraphics();

				drawToGraphics2D(g, width, height);
				g.dispose();
				ImageIO.write(b, "png", new File(fname));
			} else if (filetype.equalsIgnoreCase("jpg")
					|| filetype.equalsIgnoreCase("jpeg")) {
				BufferedImage b = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = b.createGraphics();
				drawToGraphics2D(g, width, height);
				g.dispose();
				ImageIO.write(b, "jpg", new File(fname));

				/*
				 * } else if (filetype.equalsIgnoreCase("eps")) { try {
				 * ImageType currentImageType = ImageType.EPS; Rectangle r = new
				 * Rectangle (0, 0, width, height);
				 * Export.exportComponent(getCanvasPanel(), r, new
				 * File(filename), currentImageType); } catch (IOException e) {
				 * e.printStackTrace(); } catch
				 * (org.apache.batik.transcoder.TranscoderException e) {
				 * e.printStackTrace(); }
				 */

			} else if (filetype.equalsIgnoreCase("ps")) {
				try {
					ImageType currentImageType = ImageType.PS;
					Rectangle r = new Rectangle(0, 0, width, height);
					Export.exportComponent(cp, r, new File(
							filename), currentImageType);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (org.apache.batik.transcoder.TranscoderException e) {
					e.printStackTrace();
				}

			} else if (filetype.equalsIgnoreCase("eps")) {

				try {
					FileOutputStream outputStream = new FileOutputStream(fname);
					org.jibble.epsgraphics.EpsGraphics2D g = new org.jibble.epsgraphics.EpsGraphics2D(
							"HChart canvas", outputStream, 0, 0, width, height);// #Create
					// a
					// new
					// document
					// with
					// bounding
					// box
					// 0 <=
					// x <=
					// 100
					// and
					// 0 <=
					// y <=
					// 100.
					drawToGraphics2D(g, width, height);
					g.flush();
					g.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Problem writing eps");
				}

			} else if (filetype.equalsIgnoreCase("pdf")) {

				try {
					FileOutputStream outputStream = new FileOutputStream(fname);
					com.lowagie.text.pdf.FontMapper mapper = new com.lowagie.text.pdf.DefaultFontMapper();
					com.lowagie.text.Rectangle pagesize = new com.lowagie.text.Rectangle(
							width, height);
					com.lowagie.text.Document document = new com.lowagie.text.Document(
							pagesize, 50, 50, 50, 50);
					try {
						com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter
								.getInstance(document, outputStream);
						// document.addAuthor("JFreeChart");
						// document.addSubject("Jylab");
						document.open();
						com.lowagie.text.pdf.PdfContentByte cb = writer
								.getDirectContent();
						com.lowagie.text.pdf.PdfTemplate tp = cb
								.createTemplate(width, height);
						Graphics2D g = tp.createGraphics(width, height, mapper);
						// Rectangle2D r2D = new Rectangle2D.Double(0, 0, width,
						// height);

						drawToGraphics2D(g, width, height);
						g.dispose();
						cb.addTemplate(tp, 0, 0);

					} catch (com.lowagie.text.DocumentException de) {
						System.err.println(de.getMessage());
					}
					document.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.err
							.println("Cannot find itext library, cannot create pdf.");
				}

			} else if (filetype.equalsIgnoreCase("svg")) {

				try {

					// import org.apache.batik.dom.GenericDOMImplementation;
					// import org.apache.batik.svggen.SVGGraphics2D;

					org.w3c.dom.DOMImplementation domImpl = org.apache.batik.dom.GenericDOMImplementation
							.getDOMImplementation();
					// Create an instance of org.w3c.dom.Document
					org.w3c.dom.Document document = domImpl.createDocument(
							null, "svg", null);
					// Create an instance of the SVG Generator
					org.apache.batik.svggen.SVGGraphics2D svgGenerator = new org.apache.batik.svggen.SVGGraphics2D(
							document);
					svgGenerator.setSVGCanvasSize(new Dimension(width, height));
					// set the precision to avoid a null pointer exception in
					// Batik 1.5
					svgGenerator.getGeneratorContext().setPrecision(6);
					// Ask the chart to render into the SVG Graphics2D
					// implementation

					drawToGraphics2D(svgGenerator, width, height);
					// chart.draw(svgGenerator, new Rectangle2D.Double(0, 0,
					// width, height), null);
					// Finally, stream out SVG to a file using UTF-8 character
					// to
					// byte encoding
					boolean useCSS = true;
					Writer out = new OutputStreamWriter(new FileOutputStream(
							new File(filename)), "UTF-8");
					svgGenerator.stream(out, useCSS);
					out.close();

				} catch (org.w3c.dom.DOMException e) {
					System.err.println("Problem writing to SVG");
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Missing Batik libraries?");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void drawToGraphics2D(Graphics2D g, int width, int height) {
		// self.chartCoordsMap = {} #Maps a chart to its raw screen coords, used
		// for converting coords
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		//
		// int boxWidth = width / this.chartarray[0].length;
		// int boxHeight = height / this.chartarray.length;

		int cols = 1;
		int rows = 1;
		int boxWidth = width / cols;
		int boxHeight = height / rows;

		//
		// # print "boxWidth ", boxWidth
		// # print "boxHeight ", boxHeight

		// for (int row = 0; row < chartarray.length; row++)
		int currentChartIndex = 0;

		for (int i2 = 0; i2 < rows; i2++) {
			for (int i1 = 0; i1 < cols; i1++) {

				currentChartIndex++;
				if (chart != null) {

					int rowsUsed = 1;
					int colsUsed = 1;
					int chartX = boxWidth * i1;
					int chartY = boxHeight * i2;
					int chartwidth = boxWidth;
					int chartheight = boxHeight;
					// #Get Horizontalspace
					// for (int c = col; c > -1; c--)
					// {
					// // for c in range(col, -1, -1):
					// // if self.chartArray[row][c] == None:
					// if(this.chartarray[row][c] == null)
					// rowsUsed++;
					//
					// // rowsUsed = rowsUsed + 1
					// // #print "adding row"
					// }
					chartwidth = boxWidth * rowsUsed;
					chartheight = boxHeight;
					chartX = chartX - (rowsUsed - 1) * boxWidth;
					//
					// # chart.configureDomainAxes()
					// # chart.configureRangeAxes()
					//
					// #Testing axes ranges not updated
					// from org.jfree.chart.event import PlotChangeEvent
					// chart[i1][i2].plotChanged(new
					// PlotChangeEvent(chart[i1][i2].getXYPlot()));
					chart.plotChanged(new PlotChangeEvent(chart.getPlot()));
					//
					ChartRenderingInfo info = new ChartRenderingInfo();
					//
					chart.draw(g, new java.awt.Rectangle(chartX, chartY,
							chartwidth, chartheight),
							new Point(chartX, chartY), info);
					// self.chartToInfoMap[chart] = info
					//
					// self.chartCoordsMap[chart] = [chartX ,chartY,chartwidth,
					// chartheight]

				}
			}
		}

	}
		


         /**
         * Exports the image to some graphic format.
         */
        private void exportDialog() {

                    final JFileChooser fc = jhplot.gui.CommonGUI
                                .openImageFileChooser(frame);



                     if (fc.showDialog(frame, "Export to ") == 0) {
                                // final File scriptFile = fileChooser.getSelectedFile();
                                final File scriptFile = jhplot.io.images.ExportVGraphics
                                                .getSelectedFileWithExtension(fc);
                                // System.out.println(scriptFile.getAbsolutePath());

                                if (scriptFile == null)
                                        return;
                                else if (scriptFile.exists()) {
                                        int res = JOptionPane.showConfirmDialog(frame,
                                                        "The file exists. Do you want to overwrite the file?",
                                                        "", JOptionPane.YES_NO_OPTION);
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

}
