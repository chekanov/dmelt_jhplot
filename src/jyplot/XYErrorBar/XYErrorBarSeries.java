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
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.general.Series;

/**
 *  Represents a sequence of zero or more data items in the form (x, y, y start, y end). Items
 *  in the series will be sorted into ascending order by X-value, and duplicate
 *  X-values are permitted. Both the sorting and duplicate defaults can be
 *  changed in the constructor. 
 *
 *@author    Joshua Gould
 */
public class XYErrorBarSeries extends Series {
	private XYSeries delegate;


	/**
	 *  Creates a new empty series. By default, items added to the series will be
	 *  sorted into ascending order by x-value, and duplicate x-values will be
	 *  allowed (these defaults can be modified with another constructor.
	 *
	 *@param  name  the series name (<code>null</code> not permitted).
	 */
	public XYErrorBarSeries(String name) {
		this(name, true, true);
	}


	/**
	 *  Constructs a new xy-series that contains no data. You can specify whether
	 *  or not duplicate x-values are allowed for the series.
	 *
	 *@param  name                   the series name (<code>null</code> not
	 *      permitted).
	 *@param  autoSort               a flag that controls whether or not the items
	 *      in the series are sorted.
	 *@param  allowDuplicateXValues  a flag that controls whether duplicate
	 *      x-values are allowed.
	 */
	public XYErrorBarSeries(String name,
			boolean autoSort,
			boolean allowDuplicateXValues) {
		super(name);
		delegate = new XYSeries(name, autoSort, allowDuplicateXValues);
	}


	/**
	 *  Returns the flag that controls whether the items in the series are
	 *  automatically sorted. There is no setter for this flag, it must be defined
	 *  in the series constructor.
	 *
	 *@return    A boolean.
	 */
	public boolean getAutoSort() {
		return delegate.getAutoSort();
	}


	/**
	 *  Returns a flag that controls whether duplicate x-values are allowed. This
	 *  flag can only be set in the constructor.
	 *
	 *@return    A boolean.
	 */
	public boolean getAllowDuplicateXValues() {
		return delegate.getAllowDuplicateXValues();
	}


	/**
	 *  Returns the number of items in the series.
	 *
	 *@return    The item count.
	 */
	public int getItemCount() {
		return delegate.getItemCount();
	}


	/**
	 *  Returns the maximum number of items that will be retained in the series.
	 *  The default value is <code>Integer.MAX_VALUE</code>).
	 *
	 *@return    The maximum item count.
	 */
	public int getMaximumItemCount() {
		return delegate.getMaximumItemCount();
	}


	/**
	 *  Sets the maximum number of items that will be retained in the series. <P>
	 *
	 *  If you add a new item to the series such that the number of items will
	 *  exceed the maximum item count, then the FIRST element in the series is
	 *  automatically removed, ensuring that the maximum item count is not
	 *  exceeded.
	 *
	 *@param  maximum  the maximum.
	 */
	public void setMaximumItemCount(int maximum) {
		delegate.setMaximumItemCount(maximum);
	}


	/**
	 *  Adds a data item to the series and sends a {@link SeriesChangeEvent} to all
	 *  registered listeners.
	 *
	 *@param  x      the x value.
	 *@param  y      the y value.
	 *@param  ylow   the minimum y value
	 *@param  yhigh  the maximum y value
	 */
	public void add(double x, double y, double ylow, double yhigh) {
		_add(new Double(x), new Double(y), new Double(ylow), new Double(yhigh), true);
	}


	/**
	 *  Adds a data item to the series and, if requested, sends a {@link
	 *  SeriesChangeEvent} to all registered listeners.
	 *
	 *@param  x       the x value.
	 *@param  y       the y value.
	 *@param  ylow    the minimum y value
	 *@param  yhigh   the maximum y value
	 *@param  notify  a flag that controls whether or not a {@link
	 *      SeriesChangeEvent} is sent to all registered listeners.
	 */
	public void add(double x, double y, double ylow, double yhigh, boolean notify) {
		_add(new Double(x), new Double(y), new Double(ylow), new Double(yhigh), notify);
	}


	/**
	 *  Adds new data to the series and sends a {@link SeriesChangeEvent} to all
	 *  registered listeners. <P>
	 *
	 *  Throws an exception if the x-value is a duplicate AND the
	 *  allowDuplicateXValues flag is false.
	 *
	 *@param  x       the x-value (<code>null</code> not permitted).
	 *@param  y       the y-value (<code>null</code> permitted).
	 *@param  ylow    the minimum y value
	 *@param  yhigh   the maximum y value
	 *@param  notify  Description of the Parameter
	 */
	private void _add(Number x, Number y, Number ylow, Number yhigh, boolean notify) {
		delegate.add(new XYErrorBarDataItem(x, y, ylow, yhigh), notify);

	}


	/**
	 *  Deletes a range of items from the series and sends a {@link
	 *  SeriesChangeEvent} to all registered listeners.
	 *
	 *@param  start  the start index (zero-based).
	 *@param  end    the end index (zero-based).
	 */
	public void delete(int start, int end) {
		delegate.delete(start, end);
	}


	/**
	 *  Removes the item at the specified index.
	 *
	 *@param  index  the index.
	 */
	public void remove(int index) {
		delegate.remove(index);
	}


	/**  Removes all data items from the series. */
	public void clear() {
		delegate.clear();
	}


	/**
	 *  Returns the x-value at the specified index.
	 *
	 *@param  index  the index (zero-based).
	 *@return        The x-value (never <code>null</code>).
	 */
	public Number getX(int index) {
		return delegate.getX(index);
	}


	/**
	 *  Returns the y-value at the specified index.
	 *
	 *@param  index  the index (zero-based).
	 *@return        The y-value (possibly <code>null</code>).
	 */
	public Number getY(int index) {
		return delegate.getY(index);
	}


	/**
	 *  Returns the y-value at the specified index.
	 *
	 *@param  index  the index (zero-based).
	 *@return        The y-value (possibly <code>null</code>).
	 */
	public Number getStartY(int index) {
		XYErrorBarDataItem item = (XYErrorBarDataItem) delegate.getDataItem(index);
		return item.getStartY();
	}


	/**
	 *  Returns the y-value at the specified index.
	 *
	 *@param  index  the index (zero-based).
	 *@return        The y-value (possibly <code>null</code>).
	 */
	public Number getEndY(int index) {
		XYErrorBarDataItem item = (XYErrorBarDataItem) delegate.getDataItem(index);
		return item.getEndY();
	}



	/**
	 *  Returns the index of the item with the specified x-value. Note: if the
	 *  series is not sorted in order of ascending x-values, the result is
	 *  undefined.
	 *
	 *@param  x  the x-value (<code>null</code> not permitted).
	 *@return    The index.
	 */
	public int indexOf(Number x) {
		return delegate.indexOf(x);
	}



	private static class XYErrorBarDataItem
			 extends XYDataItem {

		protected Number startY;
		protected Number endY;

		// note we can't override equals without violating the equals contract
		public XYErrorBarDataItem(double x, double y, double startY, double endY) {
			super(x, y);
			this.startY = new Double(startY);
			this.endY = new Double(endY);
		}


		public XYErrorBarDataItem(Number x, Number y, Number startY, Number endY) {
			super(x, y);
			this.startY = startY;
			this.endY = endY;
		}


		public Number getStartY() {
			return startY;
		}


		public Number getEndY() {
			return endY;
		}
	}
}

