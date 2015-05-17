// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot.gui;

import javax.swing.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;

import java.util.zip.*;

import jhplot.JHPlot;

import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.render.ps.PSTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.transcoder.image.JPEGTranscoder;
// FREEHEP 
//import org.freehep.util.export.ExportDialog;
//import org.freehep.util.export.ExportFileType;
import org.freehep.graphicsbase.util.export.*; 

/**
 * Class to build the global panel with graphics. It should be inserted to some
 * frame. It has 4 margins which can be obtained from this class.
 * 
 * @author S.Chekanov
 * 
 */

public class GHPanel implements ComponentListener, Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	protected JPanel CanvasPanel;

	protected JPanel mainPanel; // keeps graphs

	protected GHMargin topPanel;// keeps mainPannel and margins

	protected int topSize;

	protected GHMargin leftPanel;

	protected int leftSize;

	protected GHMargin rightPanel;

	protected int rightSize;

	protected GHMargin bottomPanel;

	protected int bottomSize;

	protected Color backMargin;

	protected String gTitleText;

	protected Font gTitleFont;

	protected Color gTitleColor;

	protected int Width;

	protected int Height;

	private static final String rootKey = GHPanel.class.getName();
	private static final String SAVE_AS_TYPE = rootKey + ".SaveAsType";
	private static final String SAVE_AS_FILE = rootKey + ".SaveAsFile";


	/**
	 * Create a panel with graphics. By default, all margins have size of 10
	 * pixels. Background color is white. Initialization of JHPLot classes are
	 * done here.
	 * 
	 * @param Width
	 *            in pixels
	 * @param Height
	 *            in pixels
	 */
	public GHPanel(int Width, int Height) {

                System.setProperty("org.apache.commons.logging.Log",
                           "org.apache.commons.logging.impl.NoOpLog");

		CanvasPanel = new JPanel();

		// build export file tipes
		// list = new List<ExportFileType>();
		// List<ExportFileType> list = ExportFileType.getExportFileTypes();
		// addAllExportFileTypes();

		this.Width = Width;
		this.Height = Height;

		// what if the screen size is smaller?
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int Sheight = screenSize.height;
		int Swidth = screenSize.width;
		if (Swidth < this.Width || Sheight < this.Height) {
			this.Width = (int) (0.5 * Swidth);
			this.Height = (int) (0.5 * Sheight);
		}

		CanvasPanel.addComponentListener(this);
		CanvasPanel.setPreferredSize(new Dimension(Width, Height));
		CanvasPanel.setMinimumSize(new Dimension(20, 20));

		leftSize = (int) (Width * 0.025);
		rightSize = (int) (Width * 0.025);
		bottomSize = (int) (Height * 0.025);
		topSize = (int) (Height * 0.025);

		backMargin = Color.white;

		mainPanel = new JPanel();

		topPanel = new GHMargin(this, "Top"); // left panel
		topPanel.setGBackground(backMargin);
		topPanel.setGRecBackground(backMargin);

		leftPanel = new GHMargin(this, "Left"); // left panel
		leftPanel.setGBackground(backMargin);
		leftPanel.setGRecBackground(backMargin);

		rightPanel = new GHMargin(this, "Right"); // right panel
		rightPanel.setGBackground(backMargin);
		rightPanel.setGRecBackground(backMargin);

		bottomPanel = new GHMargin(this, "Bottom"); // buttom panel
		bottomPanel.setGBackground(backMargin);
		rightPanel.setGRecBackground(backMargin);

		leftPanel.setSizeMargin(leftSize, Height);
		rightPanel.setSizeMargin(rightSize, Height);
		bottomPanel.setSizeMargin(Width, bottomSize);
		topPanel.setSizeMargin(Width, topSize);

		// invert to GridLayout(); Gaps are zero
		mainPanel.setBackground(Color.white);
		mainPanel.setBorder(BorderFactory.createEmptyBorder());

		CanvasPanel.setLayout(new BorderLayout());

		CanvasPanel.add(mainPanel, BorderLayout.CENTER);
		CanvasPanel.add(leftPanel, BorderLayout.WEST);
		CanvasPanel.add(rightPanel, BorderLayout.EAST);
		CanvasPanel.add(bottomPanel, BorderLayout.SOUTH);
		CanvasPanel.add(topPanel, BorderLayout.NORTH);

		// no boarder
		CanvasPanel.setBorder(BorderFactory.createEmptyBorder());

		// initialisation
		JHPlot.init();

	}

	
	
	/**
	 * Export SVG to various image formats. Supported output: JPEG<br>
	 * PNG <br>
	 * PS <br>
	 * EPS <br>
	 * PDF <br>
	 * SVGZ <br>
	 * 
	 * @param source
	 *            Input file in SVG format
	 * @param target
	 *            Output file in designed format.
	 */
	public void convertSVG(String source, String target) {
		jhplot.io.images.ConvertSVG.SVGTo(source, target, false);
	}

	/**
	 * Export SVG to various image formats. Supported output: JPEG<br>
	 * PNG <br>
	 * PS <br>
	 * EPS <br>
	 * PDF <br>
	 * SVGZ <br>
	 * 
	 * @param source
	 *            Input file in SVG format
	 * @param target
	 *            Output file in designed format.
	 * @param isRemove
	 *            true if the source should be removed.
	 */
	public void convertSVG(String source, String target, boolean isRemove) {
		jhplot.io.images.ConvertSVG.SVGTo(source, target, isRemove);
	}

	
	/**
	 * Add a graph or any component in the location given by i1 and i2
	 * 
	 * @param i1
	 *            location in x
	 * @param i2
	 *            location in y
	 * @param a
	 *            component
	 */
	public void addGraph(int i1, int i2, Component a) {
		mainPanel.add(a);
	}

	/**
	 * Set color for all global margins
	 * 
	 * @param backMargin
	 */
	public void setMarginBackground(Color backMargin) {
		this.backMargin = backMargin;

		leftPanel.setGBackground(backMargin);
		leftPanel.setGRecBackground(backMargin);
		rightPanel.setGBackground(backMargin);
		rightPanel.setGRecBackground(backMargin);
		bottomPanel.setGBackground(backMargin);
		bottomPanel.setGRecBackground(backMargin);
		topPanel.setGBackground(backMargin);
		topPanel.setGRecBackground(backMargin);

	}

	/**
	 * Registe all file types
	 * 
	 */

	/*
	 * private void addAllExportFileTypes() {
	 * 
	 * list = ExportFileType.getExportFileTypes(); // List<ExportFileType>
	 * exportFileTypes = ExportFileType.getExportFileTypes(); //
	 * Collections.sort(exportFileTypes); // Iterator<ExportFileType> iterator =
	 * exportFileTypes.iterator(); // while(iterator.hasNext()) { //
	 * ExportFileType type = (ExportFileType)iterator.next(); //
	 * list.addElement(type); // } }
	 */

	/**
	 * Get color for all global margins
	 * 
	 * @return color color of the margins
	 */
	public Color getMarginBackground() {
		return backMargin;
	}

	/**
	 * Add a component to the main panel
	 * 
	 * @param a
	 *            Component
	 */
	public void addComp(Component a) {

		mainPanel.add(a);

	}

	/**
	 * Get the width of the main panel which keeps all margins and the central
	 * panel (in pixels)
	 * 
	 * @return size in X direction (width)
	 */

	public int getSizeX() {

		return CanvasPanel.getSize().width;

	}

	/**
	 * Get the height of the main panel which keeps all margins and the central
	 * panel (in pixels)
	 * 
	 * @return size in Y direction (height)
	 */

	public int getSizeY() {

		return CanvasPanel.getSize().height;

	}

	/**
	 * Sets the global title
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setGTitle(String sname, Font f, Color c) {

		setTextTop(sname, f, c);
		gTitleText = sname;
		gTitleFont = f;
		gTitleColor = c;

	}

	/**
	 * Sets a text on global top margin
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setTextTop(String sname, Font f, Color c) {
		topPanel.setRotation(0);
		topPanel.setString(sname, f, c);
		setMarginSizeTop(0.1);
	}

	/**
	 * Sets a text on global left margin
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setTextLeft(String sname, Font f, Color c) {
		leftPanel.setRotation(-90);
		leftPanel.setString(sname, f, c);
		setMarginSizeLeft(0.1);
	}

	/**
	 * Sets a text on global right margin
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setTextRight(String sname, Font f, Color c) {
		rightPanel.setRotation(-90);
		rightPanel.setString(sname, f, c);
		setMarginSizeRight(0.1);
	}

	/**
	 * Sets a text on global bottom margin.
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setTextBottom(String sname, Font f, Color c) {
		bottomPanel.setRotation(0);
		bottomPanel.setString(sname, f, c);
		setMarginSizeBottom(0.1);
	}

	/**
	 * Get the text of bottom margin.
	 * 
	 * @return Text
	 */

	public String getTextBottom() {
		return bottomPanel.getString();

	}

	/**
	 * Get the font of bottom margin.
	 * 
	 * @return Font
	 */

	public Font getTextBottomFont() {
		return bottomPanel.getMFont();

	}

	/**
	 * Get the color of bottom margin for the text.
	 * 
	 * @return Color of text
	 */

	public Color getTextBottomColor() {
		return bottomPanel.getMColor();

	}

	/**
	 * Get the text of left margin.
	 * 
	 * @return Text
	 */

	public String getTextLeft() {
		return leftPanel.getString();

	}

	/**
	 * Get the font of left margin.
	 * 
	 * @return Font
	 */

	public Font getTextLeftFont() {
		return leftPanel.getMFont();

	}

	
	/**
	 * Fast export of the canvas to an image file. This depends on the
	 * extension: <br>
	 * SVG - Scalable Vector Graphics (SVG) <br>
	 * SVGZ - compressed SVG<br>
	 * JPG <br>
	 * PNG <br>
	 * PDF <br>
	 * EPS <br>
	 * PS. <br>
	 * Note: EPS, PDF and PS are derived from SVG. Use SVGZ to have smaller file
	 * sizes.
	 * <p>
	 * No questions will be asked and existing file will be rewritten
	 * 
	 * @param file
	 *            Output file with the proper extension (SVG, SVGZ, JPG, PNG,
	 *            PDF, EPS, PS). If no extension, PNG file is assumed.
	 */

	public void export(final String file) {

		if (isBorderShown())
			showBorders(false);

		CanvasPanel.validate();
		
		jhplot.io.images.ExportVGraphics.export(CanvasPanel,rootKey,file);
		
	}
	
	/**
	 * Get the color of bottom margin for the text.
	 * 
	 * @return Color of text
	 */

	public Color getTextLeftColor() {
		return leftPanel.getMColor();

	}

	/**
	 * Get text of top margin (title).
	 * 
	 * @return Text of the top margin
	 */
	public String getTextTop() {
		return topPanel.getString();

	}

	/**
	 * Get the font of top margin
	 * 
	 * @return Font
	 */

	public Font getTextTopFont() {
		return topPanel.getMFont();

	}

	/**
	 * Get the color of top margin for the text.
	 * 
	 * @return Color
	 */

	public Color getTextTopColor() {
		return topPanel.getMColor();

	}

	/**
	 * Get text of right margin.
	 * 
	 * @return text on the right panel
	 */
	public String getTextRight() {
		return rightPanel.getString();

	}

	/**
	 * Get the font of right margin.
	 * 
	 * @return Font of the right panel
	 */

	public Font getTextRightFont() {
		return rightPanel.getMFont();

	}

	/**
	 * Get the color of right margin for the text.
	 * 
	 * @return Color
	 */

	public Color getTextRightColor() {
		return rightPanel.getMColor();

	}

	/**
	 * Sets the global title using black color.
	 * 
	 * @param sname
	 *            Title name
	 * @param f
	 *            Font
	 */
	public void setGTitle(String sname, Font f) {
		setTextTop(sname, f, Color.black);
	}

	/**
	 * Sets a text on global left margin using black color.
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 */
	public void setTextLeft(String sname, Font f) {
		setTextLeft(sname, f, Color.black);
	}

	/**
	 * Sets a text on global top margin using black color.
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 */
	public void setTextTop(String sname, Font f) {
		setTextTop(sname, f, Color.black);
	}

	/**
	 * Sets a text on global right margin using black color.
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 */
	public void setTextRight(String sname, Font f) {
		setTextRight(sname, f, Color.black);
	}

	/**
	 * Sets a text on global bottom margin using black color.
	 * 
	 * @param sname
	 *            Title
	 * @param f
	 *            Font
	 */
	public void setTextBottom(String sname, Font f) {
		setTextBottom(sname, f, Color.black);
	}

	/**
	 * Set the global title with default attributes. The default color is black.
	 * The default font is ("Lucida Sans", Font.BOLD, 20)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setGTitle(String sname) {
		setTextTop(sname, new Font("Lucida Sans", Font.BOLD, 18), Color.black);
	}

	/**
	 * Set the global title with default attributes. The default color is black.
	 * The default font is ("Lucida Sans", Font.BOLD, 20)
	 * 
	 * @param sname
	 *            Title
	 * @param space
	 *            size of the top margin (0-1)
	 */
	public void setGTitle(String sname, double space) {
		setTextTop(sname, new Font("Lucida Sans", Font.BOLD, 18), Color.black);
		setMarginSizeTop(space);
	}

	/**
	 * Set the global left margin with default attributes. The default color is
	 * black. The default font is ("Lucida Sans", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setTextLeft(String sname) {
		setTextLeft(sname, new Font("Lucida Sans", Font.BOLD, 16), Color.black);
	}

	/**
	 * Set the global left margin with default attributes. The default color is
	 * black. The default font is ("Lucida Sans", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 * @param space
	 *            size of the left margin (0-1)
	 */
	public void setTextLeft(String sname, double space) {
		setTextLeft(sname, new Font("Lucida Sans", Font.BOLD, 16), Color.black);
		setMarginSizeLeft(space);
	}

	/**
	 * Set the global right margin with default attributes. The default color is
	 * black. The default font is ("Lucida Sans", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setTextRight(String sname) {
		setTextRight(sname, new Font("Lucida Sans", Font.BOLD, 16), Color.black);
	}

	/**
	 * Set the global right margin with default attributes. The default color is
	 * black. The default font is ("Lucida Sans", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 * @param space
	 *            size of the right margin (0-1)
	 */
	public void setTextRight(String sname, double space) {
		setTextRight(sname, new Font("Lucida Sans", Font.BOLD, 16), Color.black);
		setMarginSizeRight(space);
	}

	/**
	 * Set the global bottom margin with default attributes. The default color
	 * is black. The default font is ("Lucida Sans", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setTextBottom(String sname) {
		setTextBottom(sname, new Font("Lucida Sans", Font.BOLD, 16),
				Color.black);
	}

	/**
	 * Set the global top margin with default attributes. The default color is
	 * black. The default font is ("Lucida Sans", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 */
	public void setTextTop(String sname) {
		setTextTop(sname, new Font("Lucida Sans", Font.BOLD, 16), Color.black);
	}

	/**
	 * Reset all margins to the default values.
	 * 
	 */
	public void resetMargins() {

		setMarginSizeLeft(0.01);
		setMarginSizeRight(0.01);
		setMarginSizeBottom(0.01);
		setMarginSizeTop(0.01);
		setTextBottom(" ");
		setTextLeft(" ");
		setTextTop(" ");
		setTextRight(" ");

	}

	/**
	 * Set the global bottom margin with default attributes. The default color
	 * is black. The default font is ("Lucida Sans", Font.BOLD, 18)
	 * 
	 * @param sname
	 *            Title
	 * @param space
	 *            size of the bottom margin (0-1)
	 */
	public void setTextBottom(String sname, double space) {
		setTextBottom(sname, new Font("Lucida Sans", Font.BOLD, 16),
				Color.black);
		setMarginSizeBottom(space);
	}

	/**
	 * Set the global title. The default font is ("Lucida Sans", Font.BOLD, 20)
	 * 
	 * @param sname
	 *            Title
	 * @param c
	 *            Color
	 */
	public void setGTitle(String sname, Color c) {
		// System.out.println("TESTT");
		setGTitle(sname, new Font("Lucida Sans", Font.BOLD, 20), c);
	}

	/**
	 * get JPanel for left margin
	 * 
	 * @return JPanel
	 */
	public JPanel getMarginPanelLeft() {

		return leftPanel;
	}

	/**
	 * get JPanel for right margin
	 * 
	 * @return JPanel for right margin
	 */
	public JPanel getMarginPanelRight() {

		return rightPanel;
	}

	/**
	 * get JPanel for top margin
	 * 
	 * @return JPanel for top margin
	 */
	public JPanel getMarginPanelTop() {

		return topPanel;
	}

	/**
	 * get JPanel for bottom margin
	 * 
	 * @return JPanel for bottom margin
	 */
	public JPanel getMarginPanelBottom() {

		return bottomPanel;
	}

	/**
	 * get JPanel for central with drawings
	 * 
	 * @return central JPanel
	 */
	public JPanel getMarginPanelCenter() {

		return mainPanel;
	}

	/**
	 * get JPanel for the main panel which keeps the central panel and all 4
	 * margins
	 * 
	 * @return JPanel Main panel
	 */
	public JPanel getCanvasPanel() {

		return CanvasPanel;
	}

	/**
	 * Set the text rotation for top margin
	 * 
	 * @param r
	 *            rotation (0, 90, -90)
	 */
	public void setTextRotationTop(int r) {

		topPanel.setRotation(r);

	}

	/**
	 * Get the text rotation for top margin
	 * 
	 * @return rotation (0, 90, -90)
	 */
	public int getTextRotationTop() {

		return topPanel.getRotation();

	}

	/**
	 * Set the text rotation for left margin
	 * 
	 * @param r
	 *            rotation (0, 90, -90)
	 */
	public void setTextRotationLeft(int r) {

		leftPanel.setRotation(r);

	}

	/**
	 * Get the text rotation for left margin
	 * 
	 * @return rotation (0, 90, -90)
	 */
	public int getTextRotationLeft() {

		return leftPanel.getRotation();

	}

	/**
	 * Set the text rotation for right margin
	 * 
	 * @param r
	 *            rotation (0, 90, -90)
	 */
	public void setTextRotationRight(int r) {

		rightPanel.setRotation(r);

	}

	/**
	 * Get the text rotation for right margin
	 * 
	 * @return rotation (0, 90, -90)
	 */
	public int getTextRotationRight() {

		return rightPanel.getRotation();

	}

	/**
	 * Set the text rotation for bottom margin
	 * 
	 * @param r
	 *            rotation (0, 90, -90)
	 */
	public void setTextRotationBottom(int r) {

		bottomPanel.setRotation(r);

	}

	/**
	 * Get the text rotation for bottom margin
	 * 
	 * @return rotation (0, 90, -90)
	 */
	public int getTextRotationBottom() {

		return bottomPanel.getRotation();

	}

	/**
	 * Set the text position in X relative to width for top margin
	 * 
	 * @param x
	 *            position of text in X (from 0 to 1)
	 */
	public void setTextPosTopX(double x) {

		topPanel.setStringPositionX(x);

	}

	/**
	 * Set the text position in Y relative to width for top margin
	 * 
	 * @param y
	 *            position of text in Y (from 0 to 1)
	 */
	public void setTextPosTopY(double y) {

		topPanel.setStringPositionY(y);

	}

	/**
	 * Get the text position in X relative to widths for top margin
	 * 
	 * @return position of text in X (from 0 to 1)
	 */

	public double getTextPosTopX() {
		return topPanel.getStringPositionX();
	}

	/**
	 * Get the text position in Y relative to widths for top margin
	 * 
	 * @return position of text in Y (from 0 to 1)
	 */

	public double getTextPosTopY() {
		return topPanel.getStringPositionY();
	}

	/**
	 * Set the text position in X relative to width for left margin
	 * 
	 * @param x
	 *            position of text in X (from 0 to 1)
	 */
	public void setTextPosLeftX(double x) {

		leftPanel.setStringPositionX(x);

	}

	/**
	 * Set the text position in Y relative to width for left margin
	 * 
	 * @param y
	 *            position of text in Y (from 0 to 1)
	 */
	public void setTextPosLeftY(double y) {

		leftPanel.setStringPositionY(y);

	}

	/**
	 * Get the text position in X relative to widths for left margin
	 * 
	 * @return position of text in X (from 0 to 1)
	 */

	public double getTextPosLeftX() {
		return leftPanel.getStringPositionX();
	}

	/**
	 * Get the text position in Y relative to widths for left margin
	 * 
	 * @return position of text in Y (from 0 to 1)
	 */

	public double getTextPosLeftY() {
		return leftPanel.getStringPositionY();
	}

	/**
	 * Set the text position in X relative to width for right margin
	 * 
	 * @param x
	 *            position of text in X (from 0 to 1)
	 */
	public void setTextPosRightX(double x) {

		rightPanel.setStringPositionX(x);

	}

	/**
	 * Set the text position in Y relative to width for right margin
	 * 
	 * @param y
	 *            position of text in Y (from 0 to 1)
	 */
	public void setTextPosRightY(double y) {

		rightPanel.setStringPositionY(y);

	}

	/**
	 * Get the text position in X relative to widths for right margin
	 * 
	 * @return position of text in X (from 0 to 1)
	 */

	public double getTextPosRightX() {
		return rightPanel.getStringPositionX();
	}

	/**
	 * Get the text position in Y relative to widths for right margin
	 * 
	 * @return position of text in Y (from 0 to 1)
	 */

	public double getTextPosRightY() {
		return rightPanel.getStringPositionY();
	}

	/**
	 * Set the text position in X relative to width for bottom margin
	 * 
	 * @param x
	 *            position of text in X (from 0 to 1)
	 */
	public void setTextPosBottomX(double x) {

		bottomPanel.setStringPositionX(x);

	}

	/**
	 * Set the text position in Y relative to width for bottom margin
	 * 
	 * @param y
	 *            position of text in Y (from 0 to 1)
	 */
	public void setTextPosBottomY(double y) {

		bottomPanel.setStringPositionY(y);

	}

	/**
	 * Get the text position in X relative to widths for bottom margin
	 * 
	 * @return position of text in X (from 0 to 1)
	 */

	public double getTextPosBottomX() {
		return bottomPanel.getStringPositionX();
	}

	/**
	 * Get the text position in Y relative to widths for bottom margin
	 * 
	 * @return position of text in Y (from 0 to 1)
	 */

	public double getTextPosBottomY() {
		return bottomPanel.getStringPositionY();
	}

	/**
	 * Get the background color of bottom margin
	 * 
	 * @return Color
	 */

	public Color getTextBottomColorBack() {
		return bottomPanel.getGBackground();

	}

	/**
	 * Get the background color of bottom margin
	 * 
	 * @param c
	 *            Color
	 */

	public void setTextBottomColorBack(Color c) {
		bottomPanel.setGBackground(c);

	}

	/**
	 * Get the background color of top margin
	 * 
	 * @return Color
	 */

	public Color getTextTopColorBack() {
		return topPanel.getGBackground();

	}

	/**
	 * Get the background color of top margin
	 * 
	 * @param c
	 *            Color
	 */

	public void setTextTopColorBack(Color c) {
		topPanel.setGBackground(c);

	}

	/**
	 * Get the background color of left margin
	 * 
	 * @return Color
	 */

	public Color getTextLeftColorBack() {
		return leftPanel.getGBackground();

	}

	/**
	 * Get the background color of left margin
	 * 
	 * @param c
	 *            Color
	 */

	public void setTextLeftColorBack(Color c) {
		leftPanel.setGBackground(c);

	}

	/**
	 * Get the background color of right margin
	 * 
	 * @return Color
	 */

	public Color getTextRightColorBack() {
		return rightPanel.getGBackground();

	}

	/**
	 * Get the background color of left margin
	 * 
	 * @param c
	 *            Color
	 */

	public void setTextRightColorBack(Color c) {
		rightPanel.setGBackground(c);

	}

	/**
	 * Remove all staff
	 * 
	 */

	protected void disposeGHPanel() {

		CanvasPanel.setVisible(false);
		leftPanel.disposeMe();
		rightPanel.disposeMe();
		bottomPanel.disposeMe();
		topPanel.disposeMe();
		mainPanel = null;
		gTitleColor = null;
		CanvasPanel = null;
		System.gc();

	}

	/**
	 * Show borders of margins for edditing
	 * 
	 * @param show
	 *            true if shown
	 */
	public void showBorders(boolean show) {

		leftPanel.showBorderNoUpdate(show);
		topPanel.showBorderNoUpdate(show);
		bottomPanel.showBorderNoUpdate(show);
		rightPanel.showBorderNoUpdate(show);
		CanvasPanel.updateUI();
	}

	/**
	 * is the boarder of margins shown?
	 * 
	 * @return true if shown.
	 * 
	 */
	public boolean isBorderShown() {

		if (leftPanel.isBorder())
			return true;
		if (rightPanel.isBorder())
			return true;
		if (topPanel.isBorder())
			return true;
		if (rightPanel.isBorder())
			return true;

		return false;

	}


	/**
	 * Print the canvas
	 * 
	 */
	public void printGraph() {

		if (isBorderShown())
			showBorders(false);
		CanvasPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Thread t = new Thread() {
			public void run() {
				try {
					PrinterJob prnJob = PrinterJob.getPrinterJob();
					// set the Printable to the PrinterJob
					prnJob.setPrintable(new Printable() {
						public int print(Graphics graphics,
								PageFormat pageFormat, int pageIndex) {
							if (pageIndex == 0) {
								Graphics2D g2d = (Graphics2D) graphics;
								double ratioX = pageFormat.getImageableWidth()
										/ CanvasPanel.getSize().width;
								double ratioY = pageFormat.getImageableHeight()
										/ CanvasPanel.getSize().height;
								double factor = Math.min(ratioX, ratioY);
								g2d.scale(factor, factor);
								g2d.translate(pageFormat.getImageableX(),
										pageFormat.getImageableY());
								disableDoubleBuffering(CanvasPanel);
								CanvasPanel.print(g2d);
								enableDoubleBuffering(CanvasPanel);
								return Printable.PAGE_EXISTS;
							}
							return Printable.NO_SUCH_PAGE;
						}
					});

					if (prnJob.printDialog()) {
						JHPlot.showStatusBarText("Printing..");
						prnJob.print();
					}
				} catch (PrinterException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		CanvasPanel
				.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Set the size of right margin of the global canvas in terms of the ration
	 * to the global panel in X
	 * 
	 * @param rightSize
	 *            size of the right margin (from 0 to 1)
	 */

	public void setMarginSizeRight(double rightSize) {
		if (rightSize > 1)
			rightSize = 1.0;
		if (rightSize < 0.01)
			rightSize = 0.01;

		this.rightSize = (int) (rightSize * getSizeX());

		rightPanel.setSizeMargin(this.rightSize, getSizeY());
		CanvasPanel.updateUI();

	}

	public static void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	public static void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}

	/**
	 * Get the size of right margin of the global canvas in terms of the ratio
	 * to thye global canvas
	 * 
	 * @return size of the margin from 0 to 1
	 */
	public double getMarginSizeRight() {

		return (double) rightPanel.getSizeX() / getSizeX();
	}

	/**
	 * Set the size of left margin in term of the ratio to the global panel
	 * 
	 * @param leftSize
	 *            size of the margin from 0 to 1
	 */

	public void setMarginSizeLeft(double leftSize) {
		if (leftSize > 1)
			leftSize = 1.0;
		if (leftSize < 0.01)
			leftSize = 0.01;

		this.leftSize = (int) (leftSize * getSizeX());
		leftPanel.setSizeMargin(this.leftSize, getSizeY());
		CanvasPanel.updateUI();

	}

	/**
	 * Get the size of left margin terms of the ratio to the main panel
	 * 
	 * @return left margin size (from 0 to 1)
	 */
	public double getMarginSizeLeft() {

		return (double) leftPanel.getSizeX() / getSizeX();
	}

	/**
	 * Set size of the bottom margin of global canvas in terms of the ratio to
	 * the size of the main panel
	 * 
	 * @param bottomSize
	 *            size of the bottom margin from 0 to 1
	 */
	public void setMarginSizeBottom(double bottomSize) {
		if (bottomSize > 1)
			bottomSize = 1.0;
		if (bottomSize < 0.01)
			bottomSize = 0.01;

		this.bottomSize = (int) (bottomSize * getSizeY());
		bottomPanel.setSizeMargin(getSizeX(), this.bottomSize);
		CanvasPanel.updateUI();
	}

	/**
	 * Set size of the top margin of global canvas in terms of the ratio to the
	 * size of the main panel
	 * 
	 * @param topSize
	 *            size of the top margin from 0 to 1
	 */
	public void setMarginSizeTop(double topSize) {
		if (topSize > 1)
			topSize = 1.0;
		if (topSize < 0.01)
			topSize = 0.01;
		this.topSize = (int) (topSize * getSizeY());
		topPanel.setSizeMargin(getSizeX(), this.topSize);
		CanvasPanel.updateUI();
	}

	/**
	 * Get size of the bottom margin as ratio to the main panel
	 * 
	 * @return size of the bottom margin (from 0 to 1)
	 */
	public double getMarginSizeBottom() {
		bottomSize = bottomPanel.getSizeY();
		return (double) bottomSize / getSizeY();

	}

	/**
	 * Set the size of the global panel which keeps the central panel and the
	 * margins
	 * 
	 * @param Width
	 *            Width
	 * @param Height
	 *            Height
	 */

	public void setSizePanel(int Width, int Height) {

		this.Width = Width;
		this.Width = Height;
		CanvasPanel.setPreferredSize(new Dimension(Width, Height));
		CanvasPanel.updateUI();
	}

	/**
	 * Get size of the top margin of global canvas in terms of ratio to the main
	 * panel
	 * 
	 * @return size of the top margin (from 0 to 1)
	 */
	public double getMarginSizeTop() {
		return (double) topPanel.getSizeY() / getSizeY();
	}

	// Define the methods of the ComponentListener interface
	public void componentResized(ComponentEvent e) {
		// System.out.println("Resized\n" + e.getSource());
		topPanel.setDefault(e.getSource());
		leftPanel.setDefault();
		bottomPanel.setDefault();
		rightPanel.setDefault();

	}// end componentResized()

	public void componentMoved(ComponentEvent e) {
		// System.out.println("Moved\n" + e.getSource());
	}// end componentMoved()

	public void componentShown(ComponentEvent e) {
		// System.out.println("Shown\n" + e.getSource());
	}// end componentShown()

	public void componentHidden(ComponentEvent e) {
		// System.out.println("Hidden\n" + e.getSource());
	}// end componentHidden

}
