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
import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import jhplot.gui.HelpBrowser;

import info.monitorenter.gui.chart.*;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.views.*;
import info.monitorenter.gui.chart.controls.LayoutFactory;



/**
 * 
 * Plot canvas for real-time charts. The "RT" in the name means "real-time".  
 * It is designed for displaying multiple 
 * traces consisting of trace points. Traces can be changed at real-time.
 * Only one canvas area is possible.   
 * 
 * @author S.Chekanov
 * 
 */
public class HPlotRT {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

        private ZoomableChart chart;
        private JFrame frame;

	/**
	 * Create the real-tyme dynamic canvas.
	 * 
	 * @param title
	 *            Title
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 */

	public HPlotRT(String title, int xsize, int ysize){ 

            chart = new ZoomableChart();
            chart.setToolTipType(Chart2D.ToolTipType.VALUE_SNAP_TO_TRACEPOINTS);
           // chart.getAxisY().setPaintScale(false);
           // chart.getAxisX().setPaintScale(false);
             chart.getAxisX().setPaintGrid(true);
             chart.getAxisY().setPaintGrid(true);
            
             ChartPanel chartpanel = new ChartPanel(chart);
             LayoutFactory factory = LayoutFactory.getInstance();


     frame = new JFrame(title);
    // add the chart to the frame: 
   // frame.getContentPane().add(chart);
    Container content = frame.getContentPane();
    content.add(chartpanel);
    content.addPropertyChangeListener(chartpanel);
    frame.setJMenuBar(factory.createChartMenuBar(chartpanel, false));
    frame.setSize(xsize,ysize);
    
    
    
    // Enable the termination button [cross on the upper right edge]: 
    frame.addWindowListener(
        new WindowAdapter(){
          public void windowClosing(WindowEvent e){
              frame.setVisible(false);
              frame.dispose();
              chart=null;
          }
        }
      );
    
    AxisTitle ax1 = new AxisTitle("X");
    ax1.setTitleFont(new Font("Arial", Font.BOLD, 16));
    chart.getAxisX().setAxisTitle(ax1);
    
    AxisTitle ax2 = new AxisTitle("Y");
    ax2.setTitleFont(new Font("Arial", Font.BOLD, 16));           
    chart.getAxisY().setAxisTitle(ax2);
    
    
    frame.setVisible(true); 
	}

	
	
	/**
	 * Return X-axis
	 * @return X-axis
	 */
	public IAxis<?> getAxisX(){
		
		return chart.getAxisX();
	}
	
	/**
	 * Return Y-axis
	 * @return Y-axis
	 */
	public IAxis<?> getAxisY(){
		
		return chart.getAxisY();
	}
	
	

	/**
	 * Construct a real-tyme  plot with the default parameters 600 by
	 * 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HPlotRT(String title) {

		this(title, 600, 400);

	}

	/**
	 * Construct a real-time canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title "Default"
	 * 
	 */
	public HPlotRT() {
		this("Default", 600, 400);
	}




 /**
         * Add dynamic trace with data for showing in the canvas.
         * @param trace data to be added 
         * 
         */
        public void add(ITrace2D trace) {
                chart.addTrace(trace);
        }


 
        /**
         * Returns the current chart.
         * @return  returns the current chart. 
         * 
         */
       public Chart2D getChart() {
                return chart; 
        }





	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}



        /* 
         * Close the canvas (and dispose all components). 
         */
        public void close() {
                frame.setVisible(false);
                frame.dispose();
                chart=null;
        }
 

  /**
         * Quit the canvas (and dispose all components) Note: a memory leak is found -
         * no time to study  it. set to null all the stuff
         */

        public void quit() {

                close();

        }




}
