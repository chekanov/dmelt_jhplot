/**
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package jyplot.XYErrorBar; 
import org.jfree.data.xy.AbstractIntervalXYDataset;

import java.util.ArrayList;
import java.util.List;

/**
 *  Represents a collection of {@link XYErrorBarSeries} objects that can be used
 *  as a dataset.
 *
 *@author    Joshua Gould
 */
public class XYErrorBarSeriesCollection
		 extends AbstractIntervalXYDataset {
	private List data;


	/**  Constructs an empty dataset. */
	public XYErrorBarSeriesCollection() {
		super();
		data = new ArrayList();
	}


	/**
	 *  Constructs a dataset and populates it with a single series.
	 *
	 *@param  series  the series
	 */
	public XYErrorBarSeriesCollection(XYErrorBarSeries series) {
		this();
		addSeries(series);
	}


	/**
	 *  Returns the name of a series.
	 *
	 *@param  series  the series item (zero-based).
	 *@return         The name of a series.
	 */

	public String getSeriesName(int series) {
//		return getSeries(series).getKey().toString();
		
		return null;
		
//		return getSeries(series).getName();
	}


	/**
	 *  Returns the number of series in the collection.
	 *
	 *@return    The series count.
	 */
	public int getSeriesCount() {
		return data.size();
	}


	/**
	 *  Returns the number of items in the specified series.
	 *
	 *@param  series  the series (zero-based item).
	 *@return         The item count.
	 */
	public int getItemCount(int series) {
		return getSeries(series).getItemCount();
	}


	/**
	 *  Removes a series from the collection and sends a {@link DatasetChangeEvent}
	 *  to all registered listeners.
	 *
	 *@param  series  the series item (zero-based).
	 *@return         The series/
	 */

	public XYErrorBarSeries removeSeries(int series) {

		if ((series < 0) || (series > getSeriesCount())) {
			throw new IllegalArgumentException("Series item out of bounds.");
		}

		// fetch the series, remove the change listener, then remove the series.
		XYErrorBarSeries ts = (XYErrorBarSeries) this.data.get(series);
		ts.removeChangeListener(this);
		this.data.remove(series);
		fireDatasetChanged();
		return ts;
	}


	/**
	 *  Returns a series from the collection.
	 *
	 *@param  series  the series item (zero-based).
	 *@return         The series.
	 */
	public XYErrorBarSeries getSeries(int series) {
		return (XYErrorBarSeries) data.get(series);
	}


	/**
	 *  Adds a series to the collection and sends a {@link DatasetChangeEvent} to
	 *  all registered listeners.
	 *
	 *@param  series  the series (<code>null</code> not permitted).
	 */
	public void addSeries(XYErrorBarSeries series) {
		if (series == null) {
			throw new IllegalArgumentException("Null 'series' argument.");
		}
		this.data.add(series);
		series.addChangeListener(this);
		fireDatasetChanged();
	}


	/**
	 *  Returns the y-value for the specified series and item.
	 *
	 *@param  series  the series (zero-based item).
	 *@param  item    the item (zero-based index).
	 *@return         The value.
	 */
	public Number getY(int series, int item) {
		return getSeries(series).getY(item);
	}


	/**
	 *  Returns the starting Y value for the specified series and item.
	 *
	 *@param  series  the series (zero-based item).
	 *@param  item    the item (zero-based index).
	 *@return         The starting Y value.
	 */

	public Number getStartY(int series, int item) {
		return getSeries(series).getStartY(item);
	}


	/**
	 *  Returns the ending Y value for the specified series and item.
	 *
	 *@param  series  the series (zero-based item).
	 *@param  item    the item (zero-based index).
	 *@return         The ending Y value.
	 */
	public Number getEndY(int series, int item) {
		return getSeries(series).getEndY(item);
	}


	/**
	 *  Returns the x-value for the specified series and item.
	 *
	 *@param  series  the series (zero-based item).
	 *@param  item    the item (zero-based index).
	 *@return         The value.
	 */
	public Number getX(int series, int item) {
		return getSeries(series).getX(item);
	}


	/**
	 *  Returns the starting X value for the specified series and item.
	 *
	 *@param  series  the series (zero-based item).
	 *@param  item    the item (zero-based index).
	 *@return         The starting X value.
	 */
	public Number getStartX(int series, int item) {
		return getSeries(series).getX(item);
	}


	/**
	 *  Returns the ending X value for the specified series and item.
	 *
	 *@param  series  the series (zero-based item).
	 *@param  item    the item (zero-based index).
	 *@return         The ending X value.
	 */
	public Number getEndX(int series, int item) {
		return getSeries(series).getX(item);
	}


	@Override
	public Comparable getSeriesKey(int series)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
