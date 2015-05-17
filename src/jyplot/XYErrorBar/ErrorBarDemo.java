package jyplot.XYErrorBar;
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
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;

import org.jfree.data.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.*;

import org.jfree.ui.*;

/**
 *  Demonstrates how to use XYErrorBarSeriesCollection and XYErrorBarRenderer
 *
 *@author    Joshua Gould
 */
public class ErrorBarDemo {

	public ErrorBarDemo() {
		XYDataset dataset = createDataset();
		JPanel chartPanel = createChartPanel(dataset);
		JFrame frame = new JFrame("Error Bar Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(chartPanel);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.pack();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2,
				(screenSize.height - frame.getHeight()) / 2);
		frame.show();
	}


	public static void main(String[] args) {
		new ErrorBarDemo();
	}


	private ChartPanel createChartPanel(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Error Bar Demo",
		// title
				"x",
		// x-axis label
				"y",
		// y-axis label
				dataset,
		// data
				PlotOrientation.VERTICAL,
		// create legend?
				true,
		// generate tooltips?
				true, false
		// generate URLs?
				);
		XYErrorBarRenderer r = new XYErrorBarRenderer();
		chart.getXYPlot().setRenderer(r);
		r.addChangeListener(chart.getXYPlot());// see bug 1177884

		ChartPanel chartPanel = new ChartPanel(chart, false, false, false,
				false, false);
		chartPanel.setMouseZoomable(true, false);
		return chartPanel;
	}


	private XYDataset createDataset() {
		XYErrorBarSeriesCollection dataset = new XYErrorBarSeriesCollection();
		XYErrorBarSeries series = new XYErrorBarSeries("Series 1");
		for (int i = 0; i < 10; i++) {
			series.add(i, i, i - 2, i + 2);
		}
		dataset.addSeries(series);
		return dataset;
	}

}
