package jyplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import jnumeric.JNumeric;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.python.core.ArgParser;
import org.python.core.PyArray;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;




/**
 * This is a wrapper for the jfreechart library that 
 * provides Matlab (actually matplotlib/pylab) syntax.
 * It is designed to be used via Jython, but also 
 * simplifies creating charts in Java also. 
 *  <p>
 * Basic usage (in Jython):
 * <pre>
 * j = Jylab()
 * j.plot([1,2,3,2])
 * j.title("Some random plot")
 * j.show()
 * j.savefig(file='/tmp/someplot.png', 300, 200)
 * </pre>
 * <p>
 * Jylab can create png,jpg,eps,svg, and pdf images
 * directly.  All except png and jpg require third
 * party libraries.
 * <p>
 * Technical overview:
 * <p>
 * Each time a plot type command is called, a new dataset is
 * created.  There are rarely multiple series in a single 
 * dataset.  A renderer is associated with each dataset,
 * thus individual plots can be easily customised.
 * <p>
 * Currently all plots use an XYPlot and compatible 
 * datasets (XYDataset).  Category plots are currently
 * not used, but might be in the future, if needed.
 * <p>
 * @author Dion Whitehead (dionjw at gmail dot com)
 */
public class Jyplot extends JyplotVanilla implements Serializable// implements ChartMouseListener
{
	/**
	 * For headless systems, there is the option of disabling the GUI. 
	 * @param enableJFrame true by default
	 */
	public Jyplot(boolean enableJFrame)
	{
		super(enableJFrame);
	}
	public Jyplot()
	{
		super();
	}
	
	/**
	 * Creates a horizontal line along the x axis.
	 * <p>
	 * <b>Jython documentation</b>
	 * <pre>
	 * 		axhline(y=0, xmin=0, xmax=1, **kwargs)
	 * </pre>
	 * <p>
	 Draw a horizontal line at y from xmin to xmax.  With the default
	 values of xmin=0 and xmax=1, this line will always span the horizontal
	 extent of the axes, regardless of the xlim settings, even if you
	 change them, eg with the xlim command.  That is, the horizontal extent
	 is in axes coords: 0=left, 0.5=middle, 1.0=right but the y location is
	 in data coordinates.
	 <p>
	 kwargs are the same as kwargs to
	 plot (however so far only "linewidth" and "color" are implemented)
	 and can be used to control the line properties.  
	 <p>
	 Eg:
	 <p>
	 draw a thick red hline at y=0 that spans the xrange
	 <pre>
	 axhline(linewidth=4, color='r')
	 </pre>
	 <p>
	 draw a default hline at y=1 that spans the xrange
	 <pre>
	 axhline(y=1)
	 </pre>
	 <p>
	 draw a default hline at y=.5 that spans the the middle half of
	 the xrange
	 <pre>
	 axhline(y=.5, xmin=0.25, xmax=0.75)
	 </pre>
	 <p>
	 * @param args Main arguments e.g. axhline(y=0, xmin=0, xmax=1
	 * @param kws Additional arguments e.g. color='r', linewidth=4
	 */
	public void axhline(PyObject[] args, String[] kws)
	{

		try
		{
			ArgParser ap = new ArgParser("axhline", args, kws, new String[]{"y", "xmin", "xmax", "linewidth", "color"});
//			PyTuple figsizetuple = null;
			PyFloat y = (PyFloat) ap.getPyObject(0, new PyFloat(0));

			PyFloat linewidth = new PyFloat(0.5);

			/*
			 * You can't just grab a float if it looks like an int,
			 * so we have to catch the exception and try again with
			 * and int.
			 */
			try
			{
				linewidth = (PyFloat) ap.getPyObject(3, new PyFloat(1));
			}
			catch (RuntimeException e)
			{
				linewidth = new PyFloat((float) ap.getInt(3, 1));

			}

			String colorString = ap.getPyObject(4, new PyString("black")).toString();
			Color color = this.getColor(colorString);
			if(color == null)
			{
				color = Color.black;
			}

			//			 add a labelled marker 
			Marker start = new ValueMarker(y.getValue());
			start.setStroke(new BasicStroke((float) linewidth.getValue()));
			start.setLabelOffsetType(LengthAdjustmentType.EXPAND);
			start.setPaint(color);
			//	        start.setLabel("Bid Start Price");
			//	        start.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
			//	        start.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			this.getChart().getXYPlot().addRangeMarker(start, Layer.BACKGROUND);

		}
		catch (Exception e)
		{
			System.out.println("Problem with axhline arguments.  axhline(y=0, xmin=0, xmax=1, **kwargs)");
			e.printStackTrace();
		}

		//Another possibility
		// add range marker for the cooling period...
		//        Hour hour1 = new Hour(18, 30, 6, 2005);
		//        Hour hour2 = new Hour(20, 30, 6, 2005);
		//        double millis1 = hour1.getFirstMillisecond();
		//        double millis2 = hour2.getFirstMillisecond();
		//        Marker cooling = new IntervalMarker(millis1, millis2);
		//        cooling.setLabelOffsetType(LengthAdjustmentType.EXPAND);
		//        cooling.setPaint(new Color(150, 150, 255));
		//        cooling.setLabel("Automatic Cooling");
		//        cooling.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
		//        cooling.setLabelPaint(Color.blue);
		//        cooling.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		//        cooling.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		//        plot.addDomainMarker(cooling, Layer.BACKGROUND);
	}
	
	/**
	 * Sets the figure size.
	 * <p>
	 * errorbar(x, y, yerr=None, xerr=None, fmt='b-', ecolor=None, capsize=3, barsabove=False
	 * <p>
	 */
	public Dataset errorbar(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("errorbar", args, kws, new String[]{"x", "y", "yerr", "xerr", "color", "ecolor", "fmt", "capsize", "barsabove", "label"});

		double[] x = getDoubleArrayFromPyList(ap.getPyObject(0));
		double[] y = getDoubleArrayFromPyList(ap.getPyObject(1));
		double[] yerr = getDoubleArrayFromPyList(ap.getPyObject(2, null));
		double[] xerr = getDoubleArrayFromPyList(ap.getPyObject(3, null));
		
		String label = ap.getString(9, "");

//		System.out.println("errorbar()");
//
//		System.out.println("x: " + x);
//		System.out.println("y: " + y);
//		System.out.println("yerr: " + yerr);
//		System.out.println("xerr: " + xerr);

		PyObject possibleColor = ap.getPyObject(4, null);
//		String colorstring = ap.getString(4, null);
		Color c = null;
		if(possibleColor != null)
		{
			c = this.getColorFromPyObject(possibleColor);
		}

		if(c == null)
			plot(x,y);
		else
			plot(x, y, c);
		label(label);

		//		c = getMostRecentColor();
		//		System.out.println("c:" + c);

		possibleColor = ap.getPyObject(5, null);
		Color ec = null;
		if(possibleColor != null)
		{
			ec = getColorFromPyObject(possibleColor);
		}
		return errorbar(x, y, xerr, yerr, ec, true);

	}
	/**
	 * Sets the figure size.
	 * figure(num=None, figsize=None, dpi=None, facecolor=None, edgecolor=None, frameon=True)
	 */
	public void figure(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("figure", args, kws, new String[]{"num", "figsize", "dpi", "facecolor", "edgecolor", "frameon"});
		//plot.setBackgroundPaint(Color.lightGray);
		PyTuple figsizetuple = null;
		int dpi = ap.getInt(2, 150);

		try
		{
			figsizetuple = (PyTuple) ap.getPyObject(1, new PyTuple(new PyObject[]{new PyFloat(8), new PyFloat(6)}));
//			this.figureWidth = (float) ((PyFloat) figsizetuple.get(0)).getValue();
//			this.figureHeight = (float) ((PyFloat) figsizetuple.get(1)).getValue();
			this.dpi = dpi;
		}
		catch (Exception e)
		{
			System.out.println("Problem with figsize arguments.  They should be in the form: (8,6)");
			//			e.printStackTrace();
		}

	}
	
	private String[] getStringArrayFromPyList(PyObject list)
	{
		try
		{
			PyList plist = (PyList) list;
			String[] result = new String[plist.__len__()];
			for (int i = 0; i < plist.__len__(); i++)
			{
				result[i] = plist.__getitem__(i).__str__().toString();
			}
			return result;
		}
		catch (Exception e)
		{
			//			e.printStackTrace();
			return null;
		}
	}
	
	public void xticksa(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("xticks", args, kws, "locs", "labels");

		double[] locs = getDoubleArrayFromPyList(ap.getPyObject(0));
		String[] labels = getStringArrayFromPyList(ap.getPyObject(1, null));

		/* Find out the size of the string array */
		/* This is neccesary because of the limitations of the JFreechart */
		double max = locs[0];
		double min = locs[0];
		for (int i = 0; i < locs.length; i++)
		{
			max = Math.max(max, locs[i]);
			min = Math.min(min, locs[i]);
		}
		int maxi = (int) max;
		int mini = (int) min;
		int sizeOfArray = maxi - mini;

		boolean generateLabelsFromLocations = false;

		if(labels == null)
		{
			labels = new String[sizeOfArray];
			generateLabelsFromLocations = true;
		}
		else
		{

		}

		/* Set by default all labels as empty */
		for (int i = 0; i < labels.length; i++)
		{
			labels[i] = new String("");
		}

		/*
		 * If there are no given labels,
		 * create them from the locations
		 */

		for (int i = 0; i < locs.length; i++)
		{

		}

		/*
		 * Now generate the labels from the locations
		 */
		for (int i = 0, j = mini; i < locs.length; i++, j++)
		{
			if(generateLabelsFromLocations)
			{
				//				labels = String.valueOf(locs);
			}
		}

		if(this.chart != null)
		{
			NumberAxis xNumberAxis = (NumberAxis) this.chart.getXYPlot().getDomainAxis();
			//			SymbolAxis symbolaxis = new SymbolAxis(xNumberAxis.getLabel(), ticklabels);
			//			symbolaxis.
			//			symbolaxis.setGridBandsVisible(false);//The grey bands that suddnely appear
			//			this.chart.getXYPlot().setDomainAxis(symbolaxis);
		}
		else
			System.out.println("xticks(): No chart created");

	}
	
	public void setp(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("xticks", args, kws, "locs", "labels");

		double[] locs = getDoubleArrayFromPyList(ap.getPyObject(0));
		String[] labels = getStringArrayFromPyList(ap.getPyObject(1, null));
	}
	
	double[] getDoubleArrayFromPyList(PyObject list)
	{
		if(list == null)
		{
			return null;
		}
		try
		{
			if(list instanceof PyList)
			{
				PyList plist = (PyList) list;
				double[] result = new double[plist.__len__()];
				for (int i = 0; i < plist.__len__(); i++)
				{
					result[i] = plist.__getitem__(i).__float__().getValue();
				}
				return result;
			}
			else if(list instanceof PyArray)
			{
				PyArray parray = (PyArray) list;
				double[] result = new double[parray.__len__()];
				for (int i = 0; i < parray.__len__(); i++)
				{
					result[i] = parray.__getitem__(i).__float__().getValue();
				}
				return result;
			}
			else if(list instanceof PyFloat)
			{
				PyFloat fl = (PyFloat) list;
				double[] result = new double[1];
				result[0] = fl.getValue();
				return result;
			}
			else if(list instanceof PyInteger)
			{
				PyInteger fl = (PyInteger) list;
				double[] result = new double[1];
				result[0] = fl.getValue();
				return result;
			}
		/*
			else if(list instanceof JNumeric.PyMultiarray)
			{
				JNumeric.PyMultiarray parray = (JNumeric.PyMultiarray) list;
				double[] result = new double[parray.__len__()];
				for (int i = 0; i < parray.__len__(); i++)
				{
					result[i] = parray.__getitem__(i).__float__().getValue();
				}
				return result;
			}
			*/
			else
			{
				System.err.println("Failed to convert " + list.getClass().getName() + " into double[]");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * Sets the legend labels and location.
	 * @param args
	 * @param kws
	 */
	public void legendtest(PyObject[] args, String[] kws)
	{
		System.out.println("legend(PyObject[] args, String[] kw) currently does not work");
		//		try
		//		{
		//			ArgParser ap = new ArgParser("legend", args, kws, new String[] {"lines", "labels", "loc"});
		//	
		//			String[] labels = getStringArrayFromPyList( ap.getPyObject(1, null) );
		//			
		//			if(labels != null)
		//			{
		////				XYDataset[] datasets = new XYDataset[labels.length];
		//				
		//				ArrayList<SeriesDataset> datasets = new ArrayList<SeriesDataset>();
		//				PyList pylist = (PyList)ap.getPyObject(0, null);
		//				if(pylist != null)
		//				{
		//					for (Object object : pylist)
		//					{
		//						datasets.add((SeriesDataset)((PyObject)object).__tojava__(SeriesDataset.class));
		//					}
		//					
		//					for (int i = 0; i < datasets.size(); i++)
		//					{
		//						datasets.get(i)...getSeries(0).setKey(labels[i]);
		//					}
		//				}
		//				
		//				
		//				
		//		
		//			}
		//			
		//			
		//		}
		//		catch (Exception e)
		//		{
		//			System.out.println("Problem with legend arguments.");
		//			System.out.println("Try: legend( [dataset1,dataset2], ['Label1', 'label2']) ");
		//			System.out.println("Or: legend( ['Label1', 'label2']) ");
		////			e.printStackTrace();
		//		}

	}


	/**
	 * This assumes
	 * @param filename
	 */
	public void savefig(String filename)
	{
		int calculatedWidth = (int) this.figureWidth * this.dpi;
		int calculatedHeight = (int) this.figureHeight * this.dpi;

		if(calculatedWidth <= 0)
			calculatedWidth = 300;
		
		if(calculatedHeight <= 0)
			calculatedHeight = 200;
		
		this.savefig(filename, calculatedWidth, calculatedHeight);
	}

	public void savefig(String filename, int width, int height)
	{
		try
		{
			String fname = filename;
			String filetype = fname.substring(fname.length() - 3);

			if(filetype.equalsIgnoreCase("png"))
			{
				BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = b.createGraphics();

				drawToGraphics2D(g, width, height);
				g.dispose();
				ImageIO.write(b, "png", new File(fname));
			}
			else if(filetype.equalsIgnoreCase("jpg") || filetype.equalsIgnoreCase("jpeg"))
			{
				BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = b.createGraphics();
				drawToGraphics2D(g, width, height);
				g.dispose();
				ImageIO.write(b, "jpg", new File(fname));
			}
			else if(filetype.equalsIgnoreCase("eps"))
			{
				try
				{
					FileOutputStream outputStream = new FileOutputStream(fname);
					org.jibble.epsgraphics.EpsGraphics2D g = new org.jibble.epsgraphics.EpsGraphics2D("Example", outputStream, 0, 0, width, height);// #Create
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
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.err.println("Problem writing eps");
				}
			}
			else if(filetype.equalsIgnoreCase("pdf"))
			{

				try
				{
		//				import com.lowagie.text.DocumentException;
		//				import com.lowagie.text.Rectangle;
		//				import com.lowagie.text.pdf.DefaultFontMapper;
		//				import com.lowagie.text.pdf.FontMapper;
		//				import com.lowagie.text.pdf.PdfContentByte;
		//				import com.lowagie.text.pdf.PdfTemplate;
		//				import com.lowagie.text.pdf.PdfWriter;
						
						FileOutputStream outputStream = new FileOutputStream(fname);
						com.lowagie.text.pdf.FontMapper mapper = new com.lowagie.text.pdf.DefaultFontMapper();
						com.lowagie.text.Rectangle pagesize = new com.lowagie.text.Rectangle(width, height);
						com.lowagie.text.Document document = new com.lowagie.text.Document(pagesize, 50, 50, 50, 50);
						try
						{
							com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);
							//					document.addAuthor("JFreeChart");
							//					document.addSubject("Jylab");
							document.open();
							com.lowagie.text.pdf.PdfContentByte cb = writer.getDirectContent();
							com.lowagie.text.pdf.PdfTemplate tp = cb.createTemplate(width, height);
							Graphics2D g = tp.createGraphics(width, height, mapper);
//							Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
		
							drawToGraphics2D(g, width, height);
							g.dispose();
							cb.addTemplate(tp, 0, 0);
		
						}
						catch (com.lowagie.text.DocumentException de)
						{
							System.err.println(de.getMessage());
						}
						document.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.err.println("Cannot find itext library, cannot create pdf.");
				}
				
				
			}
			else if(filetype.equalsIgnoreCase("svg"))
			{

				try
				{
					
//					import org.apache.batik.dom.GenericDOMImplementation;
//					import org.apache.batik.svggen.SVGGraphics2D;
					
					
					org.w3c.dom.DOMImplementation domImpl = org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation();
					//				 Create an instance of org.w3c.dom.Document
					org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);
					//				 Create an instance of the SVG Generator
					org.apache.batik.svggen.SVGGraphics2D svgGenerator = new org.apache.batik.svggen.SVGGraphics2D(document);
					svgGenerator.setSVGCanvasSize(new Dimension(width, height));
					//				 set the precision to avoid a null pointer exception in Batik 1.5
					svgGenerator.getGeneratorContext().setPrecision(6);
					//				 Ask the chart to render into the SVG Graphics2D implementation

					drawToGraphics2D(svgGenerator, width, height);
					//					chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, width, height), null);
					//				 Finally, stream out SVG to a file using UTF-8 character to
					//				 byte encoding
					boolean useCSS = true;
					Writer out = new OutputStreamWriter(new FileOutputStream(new File(filename)), "UTF-8");
					svgGenerator.stream(out, useCSS);
					out.close();
					
				}
				catch (org.w3c.dom.DOMException e)
				{
					System.err.println("Problem writing to SVG");
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.err.println("Missing Batik libraries?");
				}

			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * Sets the figure size.
	 * errorbar(x, y, yerr=None, xerr=None, fmt='b-', ecolor=None, capsize=3, barsabove=False
	 */
	public Dataset plot(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("plot", args, kws, new String[]{"x", "y", "shape", "color", "label", "separateYAxis"});

		double[] x = getDoubleArrayFromPyList(ap.getPyObject(0, null));
		double[] y = getDoubleArrayFromPyList(ap.getPyObject(1, null));
		
		
		if(x == null)
		{
			System.err.println("x is null");
			return null;
		}
		
		if(y == null)
		{
			y = x;
			x = new double[y.length];
			for (int i = 0; i < y.length; i++)
			{
				x[i] = i;
			}
		}
		
		String label = ap.getString(4, "");

		/* Then make the data series */
		XYSeries series = new XYSeries(label);
		boolean xlogarithmic = isDomainLogarithmic();
		boolean ylogarithmic = isRangeLogarithmic();
		for (int i = 0; i < x.length; i++)
		{
			if(!( Double.isNaN(x[i]) || Double.isNaN(y[i]) || Double.isInfinite(x[i]) || Double.isInfinite(y[i])))
			{
				if(!(xlogarithmic && x[i] <= 0) && !(ylogarithmic && y[i] <= 0))
					series.add(x[i], y[i]);
			}
		}

		if(label != null)
			series.setKey(label);

		/* If there is no chart, create it (defaults to XY chart).*/
		if(this.chart == null)
		{
			createXYChart();
		}
	        this.chart.setBorderPaint(DEFAULT_BG_COLOR);
            this.chart.setBackgroundPaint(DEFAULT_BG_COLOR);
            this.chart.setBorderVisible(false);

	
		XYPlot plot = this.chart.getXYPlot();
	        plot.setBackgroundPaint(DEFAULT_BG_COLOR);
	
		
		/*
		 * If we are creating a new axis, we need a new dataset,
		 * but only if there is no current dataset with dataseries.
		 */

		int currentDatasetIndex = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		plot.setDataset(currentDatasetIndex, dataset);

		XYLineAndShapeRenderer renderer = null;

		renderer = new XYLineAndShapeRenderer();
		//		renderer.setBasePaint(color);

		plot.setRenderer(currentDatasetIndex, renderer);
		//		plot.setRangeAxisLocation(currentDatasetIndex, AxisLocation.BOTTOM_OR_LEFT);

		
		/*
		 * The shapes of the series
		 */
		String shapeString = ap.getString(2, "");
		setShapesVisible(shapeString.length() > 0);
		
		Color color = getColorFromPyObject(ap.getPyObject(3, null));
		
		/*
		 * If the y axis is separate from the previous plots
		 */
		boolean separateYAxis = ap.getInt(5,0) != 0;
		if(separateYAxis)
			setSeperateYAxis("", color, true);
		
		

		/*
		 * This sets the colour of the series.
		 */
		setSeriesColor(color);
		
		/*
		 * Make the series invisible in the legend if there is no label
		 */
		if(label == null || label.equalsIgnoreCase(""))
		{
			renderer.setSeriesVisibleInLegend(false);
		}

		this.chart.getXYPlot().configureDomainAxes();// #Needed to update the
		// plotted ranges;
		this.chart.getXYPlot().configureRangeAxes();// #Needed to update the
		//
		this.grid(false);
		

		return dataset;
//		plot(double[] x, double[] y, Color color, String dataname, double[] yErrorBars, boolean seperateYAxis, boolean yaxisVisible, String rangeLabel, boolean shapesVisible)
//		return plot(x, y, c, label, null, separateYAxis, true, null, shapesVisible);

	}
	
	

	/**
	 *  Plot an arbitrary number of bars (boxes)
	 *  <pre>
	 *  bar(left, height, width=0.8, bottom=0,
     *      color=None, edgecolor=None, linewidth=None,
     *      yerr=None, xerr=None, ecolor=None, capsize=3,
     *      align='edge', orientation='vertical', log=False)
     *   </pre>
     *  Make a bar plot with rectangles bounded by
     *  left, left+width, bottom, bottom+height
     *      (left, right, bottom and top edges)
     * left, height, width, and bottom can be either scalars or sequences.
	 * @param args
	 * @param kws
	 * @return A Dataset object
	 */
	public Dataset bar(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("bar", args, kws, new String[]{"left", "height", "width", "bottom", "color", "edgecolor", "linewidth", "yerr", "xerr", "ecolor", "capsize", "align", "orientation", "log"});

		double[] left = getDoubleArrayFromPyList(ap.getPyObject(0, null));
		double[] height = getDoubleArrayFromPyList(ap.getPyObject(1, null));
		
		
		if(left == null)
		{
			System.err.println("left is null");
			return null;
		}
		
		/*
		 * If only a single array is given, assume it is the height,
		 * and create for the left array [0, 1...n]
		 */
		if(height == null)
		{
			height = left;
			left = new double[height.length];
			for (int i = 0; i < height.length; i++)
			{
				left[i] = i - 0.5;
			}
		}
		
		double[] width = getDoubleArrayFromPyList(ap.getPyObject(2, null));
		if(width == null)
		{
			width = new double[height.length];
			for (int i = 0; i < height.length; i++)
			{
				width[i] = 1;
			}
		}
		
		double[] bottom = getDoubleArrayFromPyList(ap.getPyObject(3, null));
		if(bottom == null)
		{
			bottom = new double[height.length];
			for (int i = 0; i < height.length; i++)
			{
				bottom[i] = 0;
			}
		}

		
		Color color = null;
		
		PyObject colorObject = ap.getPyObject(4, null);
		
		
		if(colorObject != null)
		{
			try
			{
				color = (Color)colorObject.__tojava__(Color.class);
			}
			catch (RuntimeException e)
			{
//				System.out.println("colorObject.getClass().getName():" + colorObject.getClass().getName());
				color = GraphicsUtil.getColorFromString(colorObject.toString());
			}
		
		}		
		return bar(left, height, width, bottom, color);
		
		

	}
	
	/**
	 * Extract a java.awt.Color object from some PyObject.
	 * This could be a string, a number, a tuple of numbers.
	 * <p>
	 * Single characters are converted to colors, as are strings that match
	 * java.awt.Color fields, e.g. 'g' = green as does 'green'.
	 */
	private Color getColorFromPyObject(PyObject pyobject)
	{
		Color c = null;
		
		if(pyobject == null)
		{
			return null;
		}
		try
		{
			if(pyobject instanceof PyList)
			{
				PyList parray = (PyList) pyobject;
				int[] result = new int[parray.__len__()];
				for (int i = 0; i < parray.__len__(); i++)
				{
					result[i] = (int)parray.__getitem__(i).__float__().getValue();
				}
				if(result.length == 1)
				{
					return new Color(result[0]);
				}
				else if(result.length == 3)
				{
					return new Color(result[0], result[1], result[2]);
				}
				else if(result.length == 4)
				{
					return new Color(result[0], result[1], result[2], result[3]);
				}
				
			}
			else if(pyobject instanceof PyArray)
			{
				PyArray parray = (PyArray) pyobject;
				int[] result = new int[parray.__len__()];
				for (int i = 0; i < parray.__len__(); i++)
				{
					result[i] = (int)parray.__getitem__(i).__float__().getValue();
				}
				if(result.length == 1)
				{
					return new Color(result[0]);
				}
				else if(result.length == 3)
				{
					return new Color(result[0], result[1], result[2]);
				}
				else if(result.length == 4)
				{
					return new Color(result[0], result[1], result[2], result[3]);
				}
			}
			else if(pyobject instanceof PyString)
			{
				return getColor(pyobject.toString());
			}
			//TODO: fix
			else if(Class.forName ( "java.awt.Color" ).isInstance ( pyobject ) ) //|| pyobject.__class__.__name__.equals("java.awt.Color")
			{
				return ((Color) pyobject.__tojava__(Color.class));
			}
			/*
			else if(pyobject instanceof JNumeric.PyMultiarray)
			{
				JNumeric.PyMultiarray parray = (JNumeric.PyMultiarray) pyobject;
				int[] result = new int[parray.__len__()];
				for (int i = 0; i < parray.__len__(); i++)
				{
					result[i] = (int)parray.__getitem__(i).__float__().getValue();
				}
				if(result.length == 1)
				{
					return new Color(result[0]);
				}
				else if(result.length == 3)
				{
					return new Color(result[0], result[1], result[2]);
				}
				else if(result.length == 4)
				{
					return new Color(result[0], result[1], result[2], result[3]);
				}
			}
			*/
			else
			{
				//TODO: fix
//				logger.warn("Failed to convert " + pyobject.toString() + "( " + pyobject.getClass().getName() + ", " + pyobject.__class__.__name__ + ") into A Color constructor");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return c;
	}
	
	/**
	 * Make a semilog plot with log scaling on the x axis.  The args to
	 *	semilog x are the same as the args to plot.  See help plot for more info.
	 *	Optional keyword args supported are any of the kwargs supported by
	 *  plot or set_xscale.  Notable, for log scaling:
     * basex: base of the logarithm
     * subsx: the location of the minor ticks; None defaults to
     *   autosubs, which depend on the number of decades in the
     *   plot; see set_xscale for details
	 */
	public Dataset semilogx(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("semilogx", args, kws, new String[]{"basex", "subsx"});

		/* This part is not used yet 
		double basex = 10;
		PyObject _basex = ap.getPyObject(0, null);
		if(_basex != null && _basex instanceof PyFloat)
		{
			PyFloat fl = (PyFloat) _basex;
			basex = fl.getValue();
		}
		
		*/
		
		Dataset dataset = this.plot(args, kws);
		
		
		XYPlot plot = (XYPlot) chart.getPlot();
		String xlabel = plot.getDomainAxis().getLabel();
		plot.setDomainAxis( new LogarithmicAxis(xlabel));
		
		return dataset;
		
	}
	
	public Dataset semilogy(PyObject[] args, String[] kws)
	{
		ArgParser ap = new ArgParser("semilogy", args, kws, new String[]{"basex", "subsx"});

		/* This part is not used yet 
		double basex = 10;
		PyObject _basex = ap.getPyObject(0, null);
		if(_basex != null && _basex instanceof PyFloat)
		{
			PyFloat fl = (PyFloat) _basex;
			basex = fl.getValue();
		}
		
		*/
		
		Dataset dataset = this.plot(args, kws);
		
		
		XYPlot plot = (XYPlot) chart.getPlot();
		String ylabel = plot.getRangeAxis().getLabel();
		plot.setRangeAxis( new LogarithmicAxis(ylabel));
		
		return dataset;
		
	}
	
	public Dataset loglog(PyObject[] args, String[] kws)
	{
//		ArgParser ap = new ArgParser("semilogx", args, kws, new String[]{"basex", "subsx"});

		/* This part is not used yet 
		double basex = 10;
		PyObject _basex = ap.getPyObject(0, null);
		if(_basex != null && _basex instanceof PyFloat)
		{
			PyFloat fl = (PyFloat) _basex;
			basex = fl.getValue();
		}
		
		*/
		
		Dataset dataset = this.plot(args, kws);
		
		/* Remove items equal or below zero */
		ArrayList<Number> xValuesToRemove = new ArrayList<Number>();
		for (int i = 0; i < ((XYSeriesCollection)dataset).getSeriesCount(); i++)
		{
			 XYSeries series = ((XYSeriesCollection)dataset).getSeries(i);
			 xValuesToRemove.clear();
			 for (int j = 0; j < series.getItemCount(); j++)
			{
				 if( series.getX(j).doubleValue() <= 0 || series.getY(j).doubleValue() <= 0)
				 {
					 xValuesToRemove.add(series.getX(j));
				 }
			}
			for (Number number : xValuesToRemove)
			{
				series.remove(number);
			}
		}
		
		
		
		XYPlot plot = (XYPlot) chart.getPlot();
		
		String ylabel = plot.getRangeAxis().getLabel();
		plot.setRangeAxis( new LogarithmicAxis(ylabel));
		
		String xlabel = plot.getDomainAxis().getLabel();
		plot.setDomainAxis( new LogarithmicAxis(xlabel));
		
		return dataset;
		
	}
	
	public static void main(String[] args)
	{
	
		/* Testing */
		
		/* Log plots */
		
		PythonInterpreter python = new PythonInterpreter();
		
		Jyplot p = new Jyplot();
		PyObject[] x = {new PyFloat(0.1), new PyFloat(1), new PyFloat(10), new PyFloat(100), new PyFloat(1000)};
		PyObject[] y = {new PyFloat(10), new PyFloat(6), new PyFloat(260), new PyFloat(140), new PyFloat(15000)};
		
		PyList xx = new PyList(x);
		PyList yy = new PyList(y);
		
		p.loglog(new PyObject[] {xx,yy}, null);
		
		p.show();
        p.savefig("a.pdf", 300, 200); 

	}
	
	private boolean isDomainLogarithmic()
	{
		if(this.chart == null)
			return false;
		
		if(this.chart.getXYPlot().getDomainAxis().getClass().getName().contains("Logarithmic"))
			return true;
		
		return false;
	}
	
	private boolean isRangeLogarithmic()
	{
		if(this.chart == null)
			return false;
		
		if(this.chart.getXYPlot().getRangeAxis().getClass().getName().contains("Logarithmic"))
			return true;
		
		return false;
	}
}
