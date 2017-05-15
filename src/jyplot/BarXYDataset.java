package jyplot;

import java.util.ArrayList;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;


/**
 * A quick and dirty sample dataset.
 */
public class BarXYDataset extends AbstractIntervalXYDataset implements IntervalXYDataset, DomainInfo, RangeInfo, DatasetChangeListener
{

//	/** The start values. Arraylist of Double[]*/
//	private ArrayList<Double[]> xStart; 
//
//	/** The end values. */
//	private ArrayList<Double[]> xEnd;
//
//	/** The y values. */
//	private ArrayList<Double[]> yStart;
//
//	/** The y values. */
//	private ArrayList<Double[]> yEnd;
//	
//	/** The series keys. */
//	private ArrayList<Comparable> seriesKeys;
	
	/** The start values. Arraylist of Double[]*/
	private ArrayList xStart; 

	/** The end values. */
	private ArrayList xEnd;

	/** The y values. */
	private ArrayList yStart;

	/** The y values. */
	private ArrayList yEnd;
	
	/** The series keys. */
	private ArrayList seriesKeys;
	
	public Range getRangeBounds(boolean includeInterval)
	{	
		double lower = getRangeLowerBound(includeInterval);
		double upper = getRangeUpperBound(includeInterval);
		if(lower <= upper)
			return new Range(lower, upper);
//		System.out.println("getDomainBounds() " + result);
		else
			return new Range(0, 1);
	}

	public double getRangeLowerBound(boolean includeInterval)
	{
		double result = Double.POSITIVE_INFINITY;
		
		for(int i = 0; i <  this.yEnd.size(); i++)
		{
			for(int j = 0; j < ((Double[])yEnd.get(i)).length; j++)
			{
				if(result == Double.POSITIVE_INFINITY)
					result = ((Double[])this.yStart.get(i))[j].doubleValue();
				
				result = Math.min( result,  Math.min(((Double[])this.yStart.get(i))[j].doubleValue(), ((Double[])this.yEnd.get(i))[j].doubleValue()));
			}
		}
		
//		if(result == Double.POSITIVE_INFINITY)
//			return 0;
		return result;
	}

	public double getRangeUpperBound(boolean includeInterval)
	{
		double result = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i <  this.yEnd.size(); i++)
		{
			for(int j = 0; j < ((Double[])this.yEnd.get(i)).length; j++)
			{
				if(result == Double.NEGATIVE_INFINITY)
					result = ((Double[])this.yStart.get(i))[j].doubleValue();
				
				result = Math.max( result,  Math.max(((Double[])this.yStart.get(i))[j].doubleValue(), ((Double[])this.yEnd.get(i))[j].doubleValue()));
			}
		}
		
//		if(result == Double.NEGATIVE_INFINITY)
//			return 1;
		
		return result;
	}

	public Range getDomainBounds(boolean includeInterval)
	{
		double lower = getDomainLowerBound(includeInterval);
		double upper = getDomainUpperBound(includeInterval);
		if(lower <= upper)
			return new Range(lower, upper);
//		System.out.println("getDomainBounds() " + result);
		else
			return new Range(0, 1);
	}

	public double getDomainLowerBound(boolean includeInterval)
	{
		double result = Double.POSITIVE_INFINITY;
		
		for(int i = 0; i <  this.xEnd.size(); i++)
		{
			for(int j = 0; j < ((Double[])this.xEnd.get(i)).length; j++)
			{
				if(result == Double.POSITIVE_INFINITY)
					result = ((Double[])this.xStart.get(i))[j].doubleValue();
				
				result = Math.min( result,  Math.min(((Double[])this.xStart.get(i))[j].doubleValue(), ((Double[])this.xEnd.get(i))[j].doubleValue()));
			}
		}
//		if(result == Double.POSITIVE_INFINITY)
//			return 0;
		return result;
	}

	public double getDomainUpperBound(boolean includeInterval)
	{
		double result = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i <  this.xEnd.size(); i++)
		{
			for(int j = 0; j < ((Double[])this.xEnd.get(i)).length; j++)
			{
				if(result == Double.NEGATIVE_INFINITY)
					result = ((Double[])this.xStart.get(i))[j].doubleValue();
				
				result = Math.max( result,  Math.max(((Double[])this.xStart.get(i))[j].doubleValue(), ((Double[])this.xEnd.get(i))[j].doubleValue()));
			}
		}
//		if(result == Double.NEGATIVE_INFINITY)
//			return 1;
		return result;
	}



	/**
	 * Creates a new dataset.
	 */
//	public BarXYDataset(int dataset)
//	{
//		switch( dataset)
//		{
//			case 1:
//				double[] x = new double[3];
//				double[] y = new double[3];
//				double[] width = new double[3];
//				double[] height = new double[3];
//
//				x[0] = 0.0;
//				x[1] = 2.0;
//				x[2] = 3.5;
//		
//				width[0] = 2.0;
//				width[1] = 1.5;
//				width[2] = 0.5;
//		
//				y[0] = 0.0;
//				y[1] = 0.0;
//				y[2] = 1.0;
//		
//				height[0] = 3.0;
//				height[1] = 4.5;
//				height[2] = 2.5;
//				this.initialise(x, y, width, height);
//				break;
//				
//			case 2:
//				xStart = new Double[1];
//				xEnd = new Double[1];
//				yStart = new Double[1];
//				yEnd = new Double[1];
//			
//				this.xStart[0] = new Double(1.0);
//		
//				this.xEnd[0] = new Double(2.0);
//		
//				this.yStart[0] = new Double(0.0);
//		
//				this.yEnd[0] = new Double(3.0);
//				break;
//				
//			default:
//				
//				this.xStart[0] = new Double(0.0);
//				this.xStart[1] = new Double(2.0);
//				this.xStart[2] = new Double(3.0);
//		
//				this.xEnd[0] = new Double(2.0);
//				this.xEnd[1] = new Double(3.0);
//				this.xEnd[2] = new Double(4.0);
//		
//				this.yStart[0] = new Double(1.0);
//				this.yStart[1] = new Double(0.5);
//				this.yStart[2] = new Double(0.5);
//		
//				this.yEnd[0] = new Double(3.0);
//				this.yEnd[1] = new Double(4.5);
//				this.yEnd[2] = new Double(0.7);
//				break;
//		}
//
//	}

	public BarXYDataset()
	{
		xStart = new ArrayList();
		xEnd = new ArrayList();
		yStart = new ArrayList();
		yEnd = new ArrayList();
		seriesKeys = new ArrayList();
	}
	
	public BarXYDataset(double[] x, double[] y, double[] width,  double[] height)
	{
		this();
		addSeries(x, y, width, height);
	}
	public void addSeries(double x, double y, double width,  double height)
	{
		double[] _x = {x};
		double[] _y = {y};
		double[] _width = {width};
		double[] _height = {height};
		addSeries(_x, _y, _width, _height);
	}
	public BarXYDataset(double[] x, double[] y, double[] width,  double[] height, Comparable seriesKey)
	{
		this();
		addSeries(x, y, width, height, seriesKey);
	}

	public void addSeries(double[] x, double[] y, double[] width,  double[] height)
	{
		Integer key = new Integer(seriesKeys.size());
		addSeries(x, y, width, height, key);
	}
	
	public void addSeries(double[] x, double[] y, double[] width,  double[] height, Comparable seriesKey)
	{
		if(x.length == y.length && x.length == width.length && x.length == height.length)
		{
			Double[] _xStart = new Double[x.length];
			Double[] _yStart = new Double[y.length];
			Double[] _xEnd = new Double[width.length];
			Double[] _yEnd = new Double[height.length];
			                            
			for (int i = 0; i < x.length; i++)
				_xStart[i] = new Double(x[i]); 
	
			for (int i = 0; i < width.length; i++)
				_xEnd[i] = new Double(x[i] + width[i]);
			
			for (int i = 0; i < y.length; i++)
				_yStart[i] = new Double(y[i]);
			
			for (int i = 0; i < height.length; i++)
				_yEnd[i] = new Double(y[i] + height[i]);
			
			this.xStart.add(_xStart);
			this.yStart.add(_yStart);
			this.xEnd.add(_xEnd);
			this.yEnd.add(_yEnd);
			
			this.seriesKeys.add(seriesKey);
			
		}
		else
		{
			System.err.println("BarXYDataset() args length not equal");
		}
	}
	/**
	 * Returns the number of series in the dataset.
	 * 
	 * @return the number of series in the dataset.
	 */
	public int getSeriesCount()
	{
		return this.seriesKeys.size();
	}

	/**
	 * Returns the key for a series.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @return The series key.
	 */
	public Comparable getSeriesKey(int series)
	{
		return (Comparable)this.seriesKeys.get(series);
	}
	
	/**
	 * This is a test
	 * Sets the key for a series.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @return The series key.
	 */
	public void setSeriesKey(int series, Comparable c)
	{
		this.seriesKeys.set(series, c);
	}

	/**
	 * Returns the number of items in a series.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @return the number of items within a series.
	 */
	public int getItemCount(int series)
	{
		return ((Double[])this.xStart.get(series)).length;
	}

	/**
	 * Returns the x-value for an item within a series. <P> The implementation
	 * is responsible for ensuring that the x-values are presented in ascending
	 * order.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item (zero-based index).
	 * @return the x-value for an item within a series.
	 */
	public Number getX(int series, int item)
	{
		return ((Double[])this.xStart.get(series))[item];
	}

	/**
	 * Returns the y-value for an item within a series.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item (zero-based index).
	 * @return the y-value for an item within a series.
	 */
	public Number getY(int series, int item)
	{
		return ((Double[])this.yStart.get(series))[item];
	}

	/**
	 * Returns the starting X value for the specified series and item.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item within a series (zero-based index).
	 * @return The value.
	 */
	public Number getStartX(int series, int item)
	{
		return ((Double[])this.xStart.get(series))[item];
	}

	/**
	 * Returns the ending X value for the specified series and item.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item within a series (zero-based index).
	 * @return the end x value.
	 */
	public Number getEndX(int series, int item)
	{
		return ((Double[])this.xEnd.get(series))[item];
	}

	/**
	 * Returns the starting Y value for the specified series and item.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item within a series (zero-based index).
	 * @return The value.
	 */
	public Number getStartY(int series, int item)
	{
		return ((Double[])this.yStart.get(series))[item];
	}

	/**
	 * Returns the ending Y value for the specified series and item.
	 * 
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item within a series (zero-based index).
	 * @return The value.
	 */
	public Number getEndY(int series, int item)
	{
		return ((Double[])this.yEnd.get(series))[item];
	}

	public double getEndXValue(int series, int item)
	{
		return ((Double[])this.xEnd.get(series))[item].doubleValue();
	}

	public double getEndYValue(int series, int item)
	{
		return ((Double[])this.yEnd.get(series))[item].doubleValue();
	}

	public double getStartXValue(int series, int item)
	{
		return ((Double[])this.xStart.get(series))[item].doubleValue();
	}

	public double getStartYValue(int series, int item)
	{
		return ((Double[])this.yStart.get(series))[item].doubleValue();
	}

	public double getXValue(int series, int item)
	{
		return ((Double[])this.xStart.get(series))[item].doubleValue();
	}

	public double getYValue(int series, int item)
	{
		return ((Double[])this.yStart.get(series))[item].doubleValue();
	}

	public int indexOf(Comparable arg0)
	{
		return this.seriesKeys.indexOf(arg0);
	}
	

	/**
     * Receives notification of an dataset change event.
     *
     * @param event  information about the event.
     */
    public void datasetChanged(DatasetChangeEvent event) {
        this.notifyListeners(event);
    }
    
    public void removeSeries(int index)
    {
    	if(index < this.seriesKeys.size() && index > -1)
    	{
    		this.seriesKeys.remove(index);
    		this.xStart.remove(index);
			this.yStart.remove(index);
			this.xEnd.remove(index);
			this.yEnd.remove(index);
    	}
		
    }

}
