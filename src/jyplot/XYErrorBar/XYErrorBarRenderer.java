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

import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.XYDataset;

/**
 *  An error bar renderer for an XYPlot. This class can draw (a) shapes at each
 *  point, or (b) lines between points, or (c) both shapes and lines. By
 *  default, shapes are drawn and lines are not.
 *
 *@author    Joshua Gould
 */
public class XYErrorBarRenderer
		 extends StandardXYItemRenderer {

	private YIntervalRenderer errorBarRenderer;


	/**  Creates a new <tt>XYErrorBarRenderer</tt>  */
	public XYErrorBarRenderer() {
		errorBarRenderer = new YIntervalRenderer();
		errorBarRenderer.setShape(new Rectangle2D.Double(-5.0, -1.0, 10.0, 1));
		setPlotLines(false);
//		setPlotShapes(true);
	}


	public void drawItem(java.awt.Graphics2D g2, XYItemRendererState state,
			java.awt.geom.Rectangle2D dataArea,
			PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis,
			XYDataset dataset, int series, int item,
			CrosshairState crosshairState, int pass) {
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
				dataset, series, item, crosshairState, pass);

		if (!super.getItemVisible(series, item)) {
			return;
		}

		errorBarRenderer.setPaint(super.getSeriesPaint(series));
		errorBarRenderer.drawItem(g2, state, dataArea, info, plot, domainAxis,
				rangeAxis, dataset, series, item,
				crosshairState, pass);
	}
}

