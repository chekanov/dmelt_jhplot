package jyplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer for an {@link XYPlot} that plots a series of standard
 * deviation bars.  The renderer expects a dataset that:
 * <ul>
 * <li>has exactly two series;</li>
 * <li>each series has the same x-values;</li>
 * <li>no <code>null</code> values;
 * </ul>
 */
public class XYStatisticalLineAndShapeRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{

	/** For serialization. */
	private static final long serialVersionUID = -8447915602375584857L;

	//	/** The paint used to highlight positive differences (y(0) > y(1)). */
	//	private transient Paint positivePaint;
	//
	//	/** The paint used to highlight negative differences (y(0) < y(1)). */
	//	private transient Paint negativePaint;
	//
	//	/** Display shapes at each point? */
	//	private boolean shapesVisible;
	//
	//	/** The shape to display in the legend item. */
	//	private transient Shape legendLine;

	/** The paint used to show the error indicator. */
	private transient Paint errorIndicatorPaint;

	/**
	 * Creates a new renderer with default attributes.
	 */
	public XYStatisticalLineAndShapeRenderer()
	{
		//		this(Color.green, Color.red, false);
		this(Color.red);
	}

	/**
	 * Creates a new renderer.
	 *
	 * @param positivePaint  the highlight color for positive differences 
	 *                       (<code>null</code> not permitted).
	 * @param negativePaint  the highlight color for negative differences 
	 *                       (<code>null</code> not permitted).
	 * @param shapes  draw shapes?
	 */
	public XYStatisticalLineAndShapeRenderer(Paint errorIndicatorPaint)//, Paint negativePaint, boolean shapes)
	{
		if(errorIndicatorPaint == null)
		{
			throw new IllegalArgumentException("Null 'errorIndicatorPaint' argument.");
		}
		this.errorIndicatorPaint = errorIndicatorPaint;

		//		if(positivePaint == null)
		//		{
		//			throw new IllegalArgumentException("Null 'positivePaint' argument.");
		//		}
		//		if(negativePaint == null)
		//		{
		//			throw new IllegalArgumentException("Null 'negativePaint' argument.");
		//		}
		//		this.positivePaint = positivePaint;
		//		this.negativePaint = negativePaint;
		//		this.shapesVisible = shapes;
		//		this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
	}

	//	/**
	//	 * Returns the paint used to highlight positive differences.
	//	 *
	//	 * @return The paint (never <code>null</code>).
	//	 */
	//	public Paint getPositivePaint()
	//	{
	//		return this.positivePaint;
	//	}

	//	/**
	//	 * Sets the paint used to highlight positive differences.
	//	 * 
	//	 * @param paint  the paint (<code>null</code> not permitted).
	//	 */
	//	public void setPositivePaint(Paint paint)
	//	{
	//		if(paint == null)
	//		{
	//			throw new IllegalArgumentException("Null 'paint' argument.");
	//		}
	//		this.positivePaint = paint;
	//		notifyListeners(new RendererChangeEvent(this));
	//	}

	//	/**
	//	 * Returns the paint used to highlight negative differences.
	//	 *
	//	 * @return The paint (never <code>null</code>).
	//	 */
	//	public Paint getNegativePaint()
	//	{
	//		return this.negativePaint;
	//	}

	//	/**
	//	 * Sets the paint used to highlight negative differences.
	//	 * 
	//	 * @param paint  the paint (<code>null</code> not permitted).
	//	 */
	//	public void setNegativePaint(Paint paint)
	//	{
	//		if(paint == null)
	//		{
	//			throw new IllegalArgumentException("Null 'paint' argument.");
	//		}
	//		this.negativePaint = paint;
	//		notifyListeners(new RendererChangeEvent(this));
	//	}

	//	/**
	//	 * Returns a flag that controls whether or not shapes are drawn for each 
	//	 * data value.
	//	 * 
	//	 * @return A boolean.
	//	 */
	//	public boolean getShapesVisible()
	//	{
	//		return this.shapesVisible;
	//	}

	/**
	 * Sets a flag that controls whether or not shapes are drawn for each 
	 * data value.
	 * 
	 * @param flag  the flag.
	 */
	//	public void setShapesVisible(boolean flag)
	//	{
	//		this.shapesVisible = flag;
	//		notifyListeners(new RendererChangeEvent(this));
	//	}
	/**
	 * Returns the shape used to represent a line in the legend.
	 * 
	 * @return The legend line (never <code>null</code>).
	 */
	//	public Shape getLegendLine()
	//	{
	//		return this.legendLine;
	//	}
	/**
	 * Sets the shape used as a line in each legend item and sends a 
	 * {@link RendererChangeEvent} to all registered listeners.
	 * 
	 * @param line  the line (<code>null</code> not permitted).
	 */
	//	public void setLegendLine(Shape line)
	//	{
	//		if(line == null)
	//		{
	//			throw new IllegalArgumentException("Null 'line' argument.");
	//		}
	//		this.legendLine = line;
	//		notifyListeners(new RendererChangeEvent(this));
	//	}
	/**
	 * Initialises the renderer and returns a state object that should be 
	 * passed to subsequent calls to the drawItem() method.  This method will 
	 * be called before the first item is rendered, giving the renderer an 
	 * opportunity to initialise any state information it wants to maintain.  
	 * The renderer can do nothing if it chooses.
	 *
	 * @param g2  the graphics device.
	 * @param dataArea  the area inside the axes.
	 * @param plot  the plot.
	 * @param data  the data.
	 * @param info  an optional info collection object to return data back to 
	 *              the caller.
	 *
	 * @return A state object.
	 */
	public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info)
	{

		return super.initialise(g2, dataArea, plot, data, info);

	}

	/**
	 * Returns <code>2</code>, the number of passes required by the renderer.  
	 * The {@link XYPlot} will run through the dataset this number of times.
	 * 
	 * @return The number of passes required by the renderer.
	 */
	public int getPassCount()
	{
		return 2;
	}

	/**
	 * Draws the visual representation of a single data item.
	 *
	 * @param g2  the graphics device.
	 * @param state  the renderer state.
	 * @param dataArea  the area within which the data is being drawn.
	 * @param info  collects information about the drawing.
	 * @param plot  the plot (can be used to obtain standard color 
	 *              information etc).
	 * @param domainAxis  the domain (horizontal) axis.
	 * @param rangeAxis  the range (vertical) axis.
	 * @param dataset  the dataset.
	 * @param series  the series index (zero-based).
	 * @param item  the item index (zero-based).
	 * @param crosshairState  crosshair information for the plot 
	 *                        (<code>null</code> permitted).
	 * @param pass  the pass index.
	 */
	public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass)
	{

		if(series == 0)
		{

//			PlotOrientation orientation = plot.getOrientation();
			RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
//			XYSeriesCollection dataset_ = (XYSeriesCollection)dataset;
			RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

			double x1 = dataset.getXValue(0, item);
			double y0 = dataset.getYValue(0, item);
			double y1 = dataset.getYValue(1, item);
			
//			System.out.println(x1 + " " + y0 + " " + y1);
			double x1trans = domainAxis.valueToJava2D(x1, dataArea, domainAxisLocation);
			double y0trans = rangeAxis.valueToJava2D(y0, dataArea, rangeAxisLocation);
			double y1trans = rangeAxis.valueToJava2D(y1, dataArea, rangeAxisLocation);
			
			

			//			standard deviation lines
			//Assumes that y0 < y1
//			double valueDelta = Math.abs(Math.abs(y0) - Math.abs(y1));
//			double meanValue = (y0 + y1) / 2;

			double highVal, lowVal;
//			if((y1) > rangeAxis.getRange().getUpperBound())
//			{
//				highVal = rangeAxis.valueToJava2D(rangeAxis.getRange().getUpperBound(), dataArea, rangeAxisLocation);
//			}
//			else
//			{
				highVal = rangeAxis.valueToJava2D(y1, dataArea, rangeAxisLocation);
//			}

//			if((y0) < rangeAxis.getRange().getLowerBound())
//			{
//				lowVal = rangeAxis.valueToJava2D(rangeAxis.getRange().getLowerBound(), dataArea, rangeAxisLocation);
//			}
//			else
//			{
				lowVal = rangeAxis.valueToJava2D(y0, dataArea, rangeAxisLocation);
//			}

			if(this.errorIndicatorPaint != null)
			{
				g2.setPaint(this.errorIndicatorPaint);
			}
			else
			{
				g2.setPaint(this.getItemPaint(series, item));
			}
			
			g2.setStroke(new BasicStroke(1.0F));
//			g2.setPaint(Color.red);
			Line2D line = null;
			line = new Line2D.Double(x1trans, lowVal, x1trans, highVal);
//			System.out.println(line.getX1() + ", " + line.getX2() + " " + line.getY1() + " " + line.getY2());
			
			g2.draw(line);
			line = new Line2D.Double(x1trans - 5.0d, highVal, x1trans + 5.0d, highVal);
			
			g2.draw(line);
			line = new Line2D.Double(x1trans - 5.0d, lowVal, x1trans + 5.0d, lowVal);
			g2.draw(line);


		}

	}

	/**
	 * Sets the paint used for the error indicators (if <code>null</code>, 
	 * the item outline paint is used instead)
	 * 
	 * @param paint  the paint (<code>null</code> permitted).
	 */
	public void setErrorIndicatorPaint(Paint paint)
	{
		this.errorIndicatorPaint = paint;
		notifyListeners(new RendererChangeEvent(this));
	}

	/**
	 * Draw a single data item.
	 *
	 * @param g2  the graphics device.
	 * @param state  the renderer state.
	 * @param dataArea  the area in which the data is drawn.
	 * @param plot  the plot.
	 * @param domainAxis  the domain axis.
	 * @param rangeAxis  the range axis.
	 * @param dataset  the dataset.
	 * @param row  the row index (zero-based).
	 * @param column  the column index (zero-based).
	 * @param pass  the pass.
	 */
	//Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass)
//	public void drawItem000(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass)
//	{
//
//		// nothing is drawn for null...
//		Number v = dataset.getValue(row, column);
//		if(v == null)
//		{
//			return;
//		}
//
//		StatisticalCategoryDataset statData = (StatisticalCategoryDataset) dataset;
//
//		Number meanValue = statData.getMeanValue(row, column);
//
//		PlotOrientation orientation = plot.getOrientation();
//
//		// current data point...
//		double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
//
//		double y1 = rangeAxis.valueToJava2D(meanValue.doubleValue(), dataArea, plot.getRangeAxisEdge());
//
//		Shape shape = getItemShape(row, column);
//		if(orientation == PlotOrientation.HORIZONTAL)
//		{
//			shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
//		}
//		else if(orientation == PlotOrientation.VERTICAL)
//		{
//			shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
//		}
//		if(getItemShapeVisible(row, column))
//		{
//
//			if(getItemShapeFilled(row, column))
//			{
//				g2.setPaint(getItemPaint(row, column));
//				g2.fill(shape);
//			}
//			else
//			{
//				if(getUseOutlinePaint())
//				{
//					g2.setPaint(getItemOutlinePaint(row, column));
//				}
//				else
//				{
//					g2.setPaint(getItemPaint(row, column));
//				}
//				g2.setStroke(getItemOutlineStroke(row, column));
//				g2.draw(shape);
//			}
//		}
//
//		if(getItemLineVisible(row, column))
//		{
//			if(column != 0)
//			{
//
//				Number previousValue = statData.getValue(row, column - 1);
//				if(previousValue != null)
//				{
//
//					// previous data point...
//					double previous = previousValue.doubleValue();
//					double x0 = domainAxis.getCategoryMiddle(column - 1, getColumnCount(), dataArea, plot.getDomainAxisEdge());
//					double y0 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
//
//					Line2D line = null;
//					if(orientation == PlotOrientation.HORIZONTAL)
//					{
//						line = new Line2D.Double(y0, x0, y1, x1);
//					}
//					else if(orientation == PlotOrientation.VERTICAL)
//					{
//						line = new Line2D.Double(x0, y0, x1, y1);
//					}
//					g2.setPaint(getItemPaint(row, column));
//					g2.setStroke(getItemStroke(row, column));
//					g2.draw(line);
//				}
//			}
//		}
//
//		RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
//		RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
//		double rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, xAxisLocation);
//
//		rectX = rectX + row * state.getBarWidth();
//
//		g2.setPaint(getItemPaint(row, column));
//		//standard deviation lines
//		double valueDelta = statData.getStdDevValue(row, column).doubleValue();
//
//		double highVal, lowVal;
//		if((meanValue.doubleValue() + valueDelta) > rangeAxis.getRange().getUpperBound())
//		{
//			highVal = rangeAxis.valueToJava2D(rangeAxis.getRange().getUpperBound(), dataArea, yAxisLocation);
//		}
//		else
//		{
//			highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
//		}
//
//		if((meanValue.doubleValue() + valueDelta) < rangeAxis.getRange().getLowerBound())
//		{
//			lowVal = rangeAxis.valueToJava2D(rangeAxis.getRange().getLowerBound(), dataArea, yAxisLocation);
//		}
//		else
//		{
//			lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
//		}
//
//		if(this.errorIndicatorPaint != null)
//		{
//			g2.setPaint(this.errorIndicatorPaint);
//		}
//		else
//		{
//			g2.setPaint(getItemPaint(row, column));
//		}
//		Line2D line = null;
//		line = new Line2D.Double(x1, lowVal, x1, highVal);
//		g2.draw(line);
//		line = new Line2D.Double(x1 - 5.0d, highVal, x1 + 5.0d, highVal);
//		g2.draw(line);
//		line = new Line2D.Double(x1 - 5.0d, lowVal, x1 + 5.0d, lowVal);
//		g2.draw(line);
//
//		// draw the item label if there is one...
//		if(isItemLabelVisible(row, column))
//		{
//			if(orientation == PlotOrientation.HORIZONTAL)
//			{
//				drawItemLabel(g2, orientation, dataset, row, column, y1, x1, (meanValue.doubleValue() < 0.0));
//			}
//			else if(orientation == PlotOrientation.VERTICAL)
//			{
//				drawItemLabel(g2, orientation, dataset, row, column, x1, y1, (meanValue.doubleValue() < 0.0));
//			}
//		}
//
//		// collect entity and tool tip information...
//		if(state.getInfo() != null)
//		{
//			EntityCollection entities = state.getEntityCollection();
//			if(entities != null && shape != null)
//			{
//				String tip = null;
//				CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
//				if(tipster != null)
//				{
//					tip = tipster.generateToolTip(dataset, row, column);
//				}
//				String url = null;
//				if(getItemURLGenerator(row, column) != null)
//				{
//					url = getItemURLGenerator(row, column).generateURL(dataset, row, column);
//				}
//				CategoryItemEntity entity = new CategoryItemEntity(shape, tip, url, dataset, row, dataset.getColumnKey(column), column);
//				entities.add(entity);
//
//			}
//
//		}
//
//	}

	/**
	 * Draws the visual representation of a single data item, second pass.  In 
	 * the second pass, the renderer draws the lines and shapes for the 
	 * individual points in the two series.
	 *
	 * @param g2  the graphics device.
	 * @param dataArea  the area within which the data is being drawn.
	 * @param info  collects information about the drawing.
	 * @param plot  the plot (can be used to obtain standard color information 
	 *              etc).
	 * @param domainAxis  the domain (horizontal) axis.
	 * @param rangeAxis  the range (vertical) axis.
	 * @param dataset  the dataset.
	 * @param series  the series index (zero-based).
	 * @param item  the item index (zero-based).
	 * @param crosshairState  crosshair information for the plot 
	 *                        (<code>null</code> permitted).
	 */
	protected void drawItemPass1(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState)
	{

		Shape entityArea = null;
		EntityCollection entities = null;
		if(info != null)
		{
			entities = info.getOwner().getEntityCollection();
		}

		Paint seriesPaint = getItemPaint(series, item);
		Stroke seriesStroke = getItemStroke(series, item);
		g2.setPaint(seriesPaint);
		g2.setStroke(seriesStroke);

		if(series == 0)
		{

			PlotOrientation orientation = plot.getOrientation();
			RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
			RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

			double x0 = dataset.getXValue(0, item);
			double y0 = dataset.getYValue(0, item);
			double x1 = dataset.getXValue(1, item);
			double y1 = dataset.getYValue(1, item);

			double transX0 = domainAxis.valueToJava2D(x0, dataArea, domainAxisLocation);
			double transY0 = rangeAxis.valueToJava2D(y0, dataArea, rangeAxisLocation);
			double transX1 = domainAxis.valueToJava2D(x1, dataArea, domainAxisLocation);
			double transY1 = rangeAxis.valueToJava2D(y1, dataArea, rangeAxisLocation);

			if(item > 0)
			{
				// get the previous data points...
				double prevx0 = dataset.getXValue(0, item - 1);
				double prevy0 = dataset.getYValue(0, item - 1);
				double prevx1 = dataset.getXValue(1, item - 1);
				double prevy1 = dataset.getYValue(1, item - 1);

				double prevtransX0 = domainAxis.valueToJava2D(prevx0, dataArea, domainAxisLocation);
				double prevtransY0 = rangeAxis.valueToJava2D(prevy0, dataArea, rangeAxisLocation);
				double prevtransX1 = domainAxis.valueToJava2D(prevx1, dataArea, domainAxisLocation);
				double prevtransY1 = rangeAxis.valueToJava2D(prevy1, dataArea, rangeAxisLocation);

				Line2D line0 = null;
				Line2D line1 = null;
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					line0 = new Line2D.Double(transY0, transX0, prevtransY0, prevtransX0);
					line1 = new Line2D.Double(transY1, transX1, prevtransY1, prevtransX1);
				}
				else if(orientation == PlotOrientation.VERTICAL)
				{
					line0 = new Line2D.Double(transX0, transY0, prevtransX0, prevtransY0);
					line1 = new Line2D.Double(transX1, transY1, prevtransX1, prevtransY1);
				}
				if(line0 != null && line0.intersects(dataArea))
				{
					g2.setPaint(getItemPaint(series, item));
					g2.setStroke(getItemStroke(series, item));
					g2.draw(line0);
				}
				if(line1 != null && line1.intersects(dataArea))
				{
					g2.setPaint(getItemPaint(1, item));
					g2.setStroke(getItemStroke(1, item));
					g2.draw(line1);
				}
			}

			if(false)//getShapesVisible())
			{
				Shape shape0 = getItemShape(series, item);
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					shape0 = ShapeUtilities.createTranslatedShape(shape0, transY0, transX0);
				}
				else
				{ // vertical
					shape0 = ShapeUtilities.createTranslatedShape(shape0, transX0, transY0);
				}
				if(shape0.intersects(dataArea))
				{
					g2.setPaint(getItemPaint(series, item));
					g2.fill(shape0);
				}
				entityArea = shape0;

				// add an entity for the item...
				if(entities != null)
				{
					if(entityArea == null)
					{
						entityArea = new Rectangle2D.Double(transX0 - 2, transY0 - 2, 4, 4);
					}
					String tip = null;
					XYToolTipGenerator generator = getToolTipGenerator(series, item);
					if(generator != null)
					{
						tip = generator.generateToolTip(dataset, series, item);
					}
					String url = null;
					if(getURLGenerator() != null)
					{
						url = getURLGenerator().generateURL(dataset, series, item);
					}
					XYItemEntity entity = new XYItemEntity(entityArea, dataset, series, item, tip, url);
					entities.add(entity);
				}

				Shape shape1 = getItemShape(series + 1, item);
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					shape1 = ShapeUtilities.createTranslatedShape(shape1, transY1, transX1);
				}
				else
				{ // vertical
					shape1 = ShapeUtilities.createTranslatedShape(shape1, transX1, transY1);
				}
				if(shape1.intersects(dataArea))
				{
					g2.setPaint(getItemPaint(series + 1, item));
					g2.fill(shape1);
				}
				entityArea = shape1;

				// add an entity for the item...
				if(entities != null)
				{
					if(entityArea == null)
					{
						entityArea = new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4, 4);
					}
					String tip = null;
					XYToolTipGenerator generator = getToolTipGenerator(series, item);
					if(generator != null)
					{
						tip = generator.generateToolTip(dataset, series + 1, item);
					}
					String url = null;
					if(getURLGenerator() != null)
					{
						url = getURLGenerator().generateURL(dataset, series + 1, item);
					}
					XYItemEntity entity = new XYItemEntity(entityArea, dataset, series + 1, item, tip, url);
					entities.add(entity);
				}
			}
			updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, orientation);
		}

	}

	/**
	 * Returns the positive area for a crossover point.
	 * 
	 * @param x0  x coordinate.
	 * @param y0A  y coordinate A.
	 * @param y0B  y coordinate B.
	 * @param x1  x coordinate.
	 * @param y1A  y coordinate A.
	 * @param y1B  y coordinate B.
	 * @param orientation  the plot orientation.
	 * 
	 * @return The positive area.
	 */
	protected Shape getPositiveArea(float x0, float y0A, float y0B, float x1, float y1A, float y1B, PlotOrientation orientation)
	{

		Shape result = null;

		boolean startsNegative = (y0A >= y0B);
		boolean endsNegative = (y1A >= y1B);
		if(orientation == PlotOrientation.HORIZONTAL)
		{
			startsNegative = (y0B >= y0A);
			endsNegative = (y1B >= y1A);
		}

		if(startsNegative)
		{ // starts negative
			if(endsNegative)
			{
				// all negative - return null
				result = null;
			}
			else
			{
				// changed from negative to positive
				float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
				GeneralPath area = new GeneralPath();
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					area.moveTo(y1A, x1);
					area.lineTo(p[1], p[0]);
					area.lineTo(y1B, x1);
					area.closePath();
				}
				else if(orientation == PlotOrientation.VERTICAL)
				{
					area.moveTo(x1, y1A);
					area.lineTo(p[0], p[1]);
					area.lineTo(x1, y1B);
					area.closePath();
				}
				result = area;
			}
		}
		else
		{ // starts positive
			if(endsNegative)
			{
				// changed from positive to negative
				float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
				GeneralPath area = new GeneralPath();
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					area.moveTo(y0A, x0);
					area.lineTo(p[1], p[0]);
					area.lineTo(y0B, x0);
					area.closePath();
				}
				else if(orientation == PlotOrientation.VERTICAL)
				{
					area.moveTo(x0, y0A);
					area.lineTo(p[0], p[1]);
					area.lineTo(x0, y0B);
					area.closePath();
				}
				result = area;

			}
			else
			{
				GeneralPath area = new GeneralPath();
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					area.moveTo(y0A, x0);
					area.lineTo(y1A, x1);
					area.lineTo(y1B, x1);
					area.lineTo(y0B, x0);
					area.closePath();
				}
				else if(orientation == PlotOrientation.VERTICAL)
				{
					area.moveTo(x0, y0A);
					area.lineTo(x1, y1A);
					area.lineTo(x1, y1B);
					area.lineTo(x0, y0B);
					area.closePath();
				}
				result = area;
			}

		}

		return result;

	}

	/**
	 * Returns the negative area for a cross-over section.
	 * 
	 * @param x0  x coordinate.
	 * @param y0A  y coordinate A.
	 * @param y0B  y coordinate B.
	 * @param x1  x coordinate.
	 * @param y1A  y coordinate A.
	 * @param y1B  y coordinate B.
	 * @param orientation  the plot orientation.
	 * 
	 * @return The negative area.
	 */
	protected Shape getNegativeArea(float x0, float y0A, float y0B, float x1, float y1A, float y1B, PlotOrientation orientation)
	{

		Shape result = null;

		boolean startsNegative = (y0A >= y0B);
		boolean endsNegative = (y1A >= y1B);
		if(orientation == PlotOrientation.HORIZONTAL)
		{
			startsNegative = (y0B >= y0A);
			endsNegative = (y1B >= y1A);
		}
		if(startsNegative)
		{ // starts negative
			if(endsNegative)
			{ // all negative
				GeneralPath area = new GeneralPath();
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					area.moveTo(y0A, x0);
					area.lineTo(y1A, x1);
					area.lineTo(y1B, x1);
					area.lineTo(y0B, x0);
					area.closePath();
				}
				else if(orientation == PlotOrientation.VERTICAL)
				{
					area.moveTo(x0, y0A);
					area.lineTo(x1, y1A);
					area.lineTo(x1, y1B);
					area.lineTo(x0, y0B);
					area.closePath();
				}
				result = area;
			}
			else
			{ // changed from negative to positive
				float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
				GeneralPath area = new GeneralPath();
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					area.moveTo(y0A, x0);
					area.lineTo(p[1], p[0]);
					area.lineTo(y0B, x0);
					area.closePath();
				}
				else if(orientation == PlotOrientation.VERTICAL)
				{
					area.moveTo(x0, y0A);
					area.lineTo(p[0], p[1]);
					area.lineTo(x0, y0B);
					area.closePath();
				}
				result = area;
			}
		}
		else
		{
			if(endsNegative)
			{
				// changed from positive to negative
				float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
				GeneralPath area = new GeneralPath();
				if(orientation == PlotOrientation.HORIZONTAL)
				{
					area.moveTo(y1A, x1);
					area.lineTo(p[1], p[0]);
					area.lineTo(y1B, x1);
					area.closePath();
				}
				else if(orientation == PlotOrientation.VERTICAL)
				{
					area.moveTo(x1, y1A);
					area.lineTo(p[0], p[1]);
					area.lineTo(x1, y1B);
					area.closePath();
				}
				result = area;
			}
			else
			{
				// all negative - return null
			}

		}

		return result;

	}

	/**
	 * Returns the intersection point of two lines.
	 * 
	 * @param x1  x1
	 * @param y1  y1
	 * @param x2  x2
	 * @param y2  y2
	 * @param x3  x3
	 * @param y3  y3
	 * @param x4  x4
	 * @param y4  y4
	 * 
	 * @return The intersection point.
	 */
	private float[] getIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)
	{

		float n = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
		float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		float u = n / d;

		float[] result = new float[2];
		result[0] = x1 + u * (x2 - x1);
		result[1] = y1 + u * (y2 - y1);
		return result;

	}

	/**
	 * Returns a default legend item for the specified series.  Subclasses 
	 * should override this method to generate customised items.
	 *
	 * @param datasetIndex  the dataset index (zero-based).
	 * @param series  the series index (zero-based).
	 *
	 * @return A legend item for the series.
	 */
	public LegendItem getLegendItem(int datasetIndex, int series)
	{
		LegendItem result = null;
		XYPlot p = getPlot();
		if(p != null)
		{
			XYDataset dataset = p.getDataset(datasetIndex);
			if(dataset != null)
			{
				if(getItemVisible(series, 0))
				{
					String label = getLegendItemLabelGenerator().generateLabel(dataset, series);
					String description = label;
					String toolTipText = null;
					if(getLegendItemToolTipGenerator() != null)
					{
						toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
					}
					String urlText = null;
					if(getLegendItemURLGenerator() != null)
					{
						urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
					}
					Paint paint = getSeriesPaint(series);
					Stroke stroke = getSeriesStroke(series);
					// TODO:  the following hard-coded line needs generalising
					Line2D line = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
					result = new LegendItem(label, description, toolTipText, urlText, line, stroke, paint);
				}
			}

		}

		return result;

	}

	/**
	 * Tests this renderer for equality with an arbitrary object.
	 * 
	 * @param obj  the object (<code>null</code> permitted).
	 * 
	 * @return A boolean.
	 */
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}
		if(!(obj instanceof XYStatisticalLineAndShapeRenderer))
		{
			return false;
		}
		if(!super.equals(obj))
		{
			return false;
		}
		XYStatisticalLineAndShapeRenderer that = (XYStatisticalLineAndShapeRenderer) obj;
		if(!PaintUtilities.equal(this.errorIndicatorPaint, that.errorIndicatorPaint))
		{
			return false;
		}
		//		if(!PaintUtilities.equal(this.errorIndicatorPaint, that.errorIndicatorPaint))
		//		{
		//			return false;
		//		}
		//		if(this.shapesVisible != that.shapesVisible)
		//		{
		//			return false;
		//		}
		//		if(!ShapeUtilities.equal(this.legendLine, that.legendLine))
		//		{
		//			return false;
		//		}
		return true;
	}

	/**
	 * Returns a clone of the renderer.
	 * 
	 * @return A clone.
	 * 
	 * @throws CloneNotSupportedException  if the renderer cannot be cloned.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream  the output stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException
	{
		stream.defaultWriteObject();
		SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
		//		SerialUtilities.writePaint(this.negativePaint, stream);
		//		SerialUtilities.writeShape(this.legendLine, stream);
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream  the input stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 * @throws ClassNotFoundException  if there is a classpath problem.
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
		//		this.negativePaint = SerialUtilities.readPaint(stream);
		//		this.legendLine = SerialUtilities.readShape(stream);
	}
	public void setStroke(double width)
	{
		
	}

}
