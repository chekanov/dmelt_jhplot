package jyplot;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import cern.colt.list.BooleanArrayList;
import cern.colt.list.IntArrayList;

/**
 * Creates a series of bar charts where the bars are stacked 
 * by the order of present/absent.  Basically, values that 
 * are present for the first time are stacked above the previous 
 * values.
 * @author dion
 *
 */
public class BarClusterPlot implements ChartMouseListener
{
	/**
	 * The following are specified in the constructor
	 */
	boolean[][] booleanarrays;
	String[] columnnames;
	String[][] columnorders;
	String[][] domainlabels;
	String[] plottitles;
	
	
	/*
	 * Other variables
	 */
	Jyplot jplot;
	ArrayList charts;
	HashMap chartToRectGeneMap;
	
	
	/**
	 * The list of currently selected genes.
	 */
	ArrayList genelist;
	
	/**
	 * 
	 * @param booleanarrays Each column is represented as a boolean array.
	 * @param columnnames The names of the columns in the boolean arrays
	 * @param columnorders The order of the columns. The lenght of the array is the number of plots.
	 * @param domainlabels The labels of domains.
	 * @param plottitles The titles of the individual plots.
	 */
	
	public BarClusterPlot(boolean[][] booleanarrays, String[] columnnames, String[][] columnorders, String[][] domainlabels, String[] plottitles)
	{
		
		
		try
		{
			this.booleanarrays = booleanarrays;
			this.columnnames = columnnames;
			this.columnorders = columnorders;
			this.domainlabels = domainlabels;
			this.plottitles = plottitles;
			
			jplot = new Jyplot();
			/*Plotting information*/
			int plotCols = columnorders.length/2;
			int plotRows = 2;
			int plotcount = 1;
			
			genelist = new ArrayList(booleanarrays.length);//Assumes that all arrays are the same length
			
			charts = new ArrayList();
			chartToRectGeneMap = new HashMap();
			
//		ArrayList listOfRectGeneListHashMaps = new ArrayList(columnorders.length);
			
			/* Get the column indices belonging to each set of column orders. */
			for (int i = 0; i < columnorders.length; i++)
			{
				IntArrayList booleanArrayIndices = new IntArrayList(columnorders[i].length);
				
				for (int j = 0; j < columnorders[i].length; j++)
				{
					for (int k = 0; k < columnnames.length; k++)
					{
						if(columnorders[i][j].equalsIgnoreCase(columnnames[k]))
						{
							booleanArrayIndices.add(k);
							break;
						}
					}
				}
				
//			System.out.println("booleanArrayIndices:\n " + booleanArrayIndices);
				
				/*Then make a boolean array from those selected columns*/
				
				boolean[][] tempBooleanArray= new boolean[booleanarrays.length][booleanArrayIndices.size()];
				for (int k = 0; k < booleanarrays.length; k++)
					for (int j = 0; j < booleanArrayIndices.size(); j++)
						tempBooleanArray[k][j] = booleanarrays[k][booleanArrayIndices.get(j)];
				
				
				HashMap rectToGeneListMap = computeRectCoords(tempBooleanArray);
				jplot.subplot(plotRows, plotCols, plotcount);
				plotcount++;
				plotBarClusterData(rectToGeneListMap, jplot);
				if(domainlabels != null )
					//This should not work, its just to make the errors go away
					jplot.xticks(new double[] {1,2,3}, domainlabels[i]);
				if(plottitles != null )
					jplot.title(plottitles[i]);
				
				/* Add the charts and lists to useful data structures */
				charts.add(jplot.getChart());
				chartToRectGeneMap.put(jplot.getChart(), rectToGeneListMap);
				
//			listOfRectGeneListHashMaps.add();
				
			}
			
//		computeRectCoords
			
			/* Before showing, set the custom ChartPanels*/
			HashMap chartPanelMap = jplot.getChartPanelMap();
			
			for (Iterator iter = chartPanelMap.keySet().iterator(); iter.hasNext();)
			{
				JFreeChart element = (JFreeChart) iter.next();
//				BarClusterPlotChartPanel panel = new BarClusterPlotChartPanel(element);
				ChartPanel panel = new ChartPanel(element);
				panel.addChartMouseListener(this);
				chartPanelMap.put(element, panel);
				
				
			}
			this.jplot.connect(this);
			jplot.show(500, 500);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		
	}

	public BarClusterPlot()
	{}
	
	/**
	 * Each rect corresponds to a list of indices (e.g. for genes)
	 * This should be computed once.
	 * Returns a HashMap of Rectangles to IntArrayList of indices matching the 
	 * Rectangles.
	 * @param data The first index is the row, second is column.
	 */
	
	public HashMap computeRectCoords(boolean[][] data)
	{
		/**
		 * Returns an arraylist with one list of rectangles and one list of indices belonging to the rectangles
		 */

		int rows = data.length;
		int cols = data[0].length;
		
		ArrayList indicesLists = new ArrayList();//Matches the computed rectangles
	
		int groups = cols;
		
		/*Create all possible binary arrays up to a given number.*/
		ArrayList binArrays = generateAllBinaryArrays((int)Math.pow(2,groups));
		binArrays.remove(0);//We are not interested in those with 0 for all rows
		
		
		/* These are the final results */
		HashMap rectangleToIndexArray = new HashMap();
		
		/* Each binary map index corresponds to a list of booleanarrays row indices */
		for (int i = 0; i < binArrays.size(); i++)
			indicesLists.add( new IntArrayList());
		
		/* Counts how many rows match the given binary array */
		int[] countMatches = new int[binArrays.size()];

		
		boolean matches = true;
		/*
		 * Go through all the rows, matching rows with the binArrays
		 * Essentially this is for counting how many rows match a 
		 * binary array, which then determines how large the rectangle
		 * is when plotted. 
		 */
		for (int i = 0; i < rows; i++)/* Go through each row*/
		{
			/* Go though the bin arrays*/
			for (int binindex = 0; binindex < binArrays.size(); binindex++)
			{
				/*
				 * Stores indices matching a binary array
				 */
				
				boolean[] binarray = (boolean[])binArrays.get(binindex);
				matches = true;
				/* Go through the columns*/
				for (int j = 0; j < data[i].length; j++)
					if(data[i][j] != binarray[j])
					{
						matches = false;
						break;
					}
						
				if(matches)
				{
					countMatches[binindex]++;
					((IntArrayList)indicesLists.get(binindex)).add(i);
					break;	
				}
			}
		}
		
		/*Each binary array has a pair of coords represented where it can stand in the order 
		  Initially all can go everywhere */
		int total = countMatches.length;
		int[][] coords = new int[total][2];
		
		/* This means initially all of them can go everywhere */
		for (int i = 0; i < coords.length; i++)
		{
			coords[i][0] = 1;
			coords[i][1] = total;
		}
			
		/* Start with the first column 
		 * First sum those with 1 in the first position
		 */
		int totalHeight = 0;
		for (int i = 0; i < binArrays.size(); i++)
		{
			if( ((boolean[])binArrays.get(i))[0] == true)
				totalHeight++;
		}
		for (int i = 0; i < binArrays.size(); i++)
		{
			if( ((boolean[])binArrays.get(i))[0] == true)
				coords[i][1] = totalHeight;
		}
		
		/*
		 * Determine the order of the binarrays
		 */ 
		IntArrayList binArrayOrder = new IntArrayList();
		
		int minPositionOfSelectedArrays = 1;
		
		/* Start by going through the columns */
		for (int columnIndex = 0; columnIndex < cols; columnIndex++)
		{
			/* Get the arrays with [0,0...1...] */ 
			IntArrayList localArrayIndices = new IntArrayList();
			
			for (int binArrayIndex = 0; binArrayIndex < binArrays.size(); binArrayIndex++)
			{
				BooleanArrayList b = new BooleanArrayList(((boolean[])binArrays.get(binArrayIndex)));
				
				if( ( columnIndex == 0 || b.indexOfFromTo(true, 0, columnIndex - 1) == -1 ) && b.get(columnIndex) )
					binArrayOrder.add(binArrayIndex);
			}
			
			int selectedArrayCount = localArrayIndices.size();
			
			for (int i = 0; i < localArrayIndices.size(); i++)
			{
				coords[i][0] = minPositionOfSelectedArrays;
				coords[i][1] = minPositionOfSelectedArrays + selectedArrayCount - 1;
			}
				
			minPositionOfSelectedArrays += selectedArrayCount;
		}
		
		/* It also works to just not order the bin arrays*/
//		IntArrayList temp = new IntArrayList(binArrayOrder.size());
//		for (int i = binArrayOrder.size() - 1; i > -1; i--)
//			temp.add(i);
//		System.out.println("Before reversing: " + temp);
//		binArrayOrder = temp;

		
		
		/*
		 * Now compute the actual positions based on the counts and order of the arrays
		 */
		
		int currentHeight = 1;
		
		for (int i = 0; i < binArrayOrder.size(); i++)
		{
			int binarrayindex = binArrayOrder.get(i);//Remember, not just the i as index
			int height = countMatches[binarrayindex];
			for (int index = 0; index < ((boolean[])binArrays.get(binarrayindex)).length; index++)
			{
				
			
				if( ((boolean[])binArrays.get(binarrayindex))[index] )
				{
					Rectangle rect = new Rectangle(index, currentHeight, 1, height);
//					System.out.println(rect);
					rectangleToIndexArray.put(rect, indicesLists.get(binarrayindex));
//					rectangles.add(rect);
				}
			}
			currentHeight += height;
		}
		
		return rectangleToIndexArray;
		
	}
	private ArrayList generateAllBinaryArrays(int size)
	{
		ArrayList results = new ArrayList(size);
		   		
   		int maxarraylength = denary2Binary(size - 1, 0).length;
   		for (int i = 0; i < size; i++)
		{
   			results.add( denary2Binary(i, maxarraylength));
		} 
   			
   			
   		return results;
	}
	
	/**
	 * Convert denary integer decimalint to binary array
	 * @param decimalint
	 * @param maxarraylength
	 * @return
	 */
	private boolean[] denary2Binary(int decimalint, int maxarraylength)
	{
		BooleanArrayList result = new BooleanArrayList(maxarraylength);
		
		
  		if( decimalint < 0)
  		{
  			System.err.println("denary2Binary(): must be a positive integer");
  			return result.elements();
  		}
  		if( decimalint == 0)
  		{
  			for (int i = 0; i < maxarraylength; i++)
  				result.add(false);
  			result.trimToSize();
  			return result.elements();
  				
  		}
  		while( decimalint > 0)
  		{
  			result.add((decimalint % 2) > 0);
  			decimalint = decimalint >> 1;
  		}
  		
  		if( result.size() < maxarraylength)
		{
			for (int i = result.size(); i < maxarraylength; i++)
			{
				result.add(false);
			}
		}
  		result.reverse();
		
  		result.trimToSize();
		return result.elements();
	}

	private void plotBarClusterData(HashMap rectToGeneListMap, Jyplot plotter)
	{
		
		
		for (Iterator iter = rectToGeneListMap.keySet().iterator(); iter.hasNext();)
		{
			Rectangle rect = (Rectangle) iter.next();
			plotter.bar((double)rect.getX() - 0.5, rect.getHeight(), rect.getWidth(), rect.getY(), new Color(0.6F,0.6F,0.6F));
		}
//		String[] s = {"0.5", "1", "3", "6", "12", "24"};
//		plotter.xticks(s);
		 
	}
	
	public static void main(String[] args)
	{
//		BarClusterPlot b = new BarClusterPlot();
//		System.out.println(Arrays.toString( b.denary2Binary(5,10)));
		
//		ArrayList a = b.generateAllBinaryArrays(5);
//		for (int i = 0; i < a.size(); i++)
//		{
//			System.out.println(Arrays.toString((boolean[])a.get(i)) );
//		}
		
//		boolean[][] barray = {{true, false, false}, {false, false, true},{false, true, true}};
//		System.out.println("1 0 0\n0 0 1\n0 1 1");
//		System.out.println(b.computeRectCoords(barray));
		
		
//		String f = "/home/dion/storage/projects/plantMicroarrayProject/scripts/graphics/BarClusterPlot/data2.csv";
//		NumericTable ntable = new NumericTable(f, NumericTable.BOOLEAN);
//		System.out.println("table rows, cols: " + ntable.rows() + ", " + ntable.cols());
//		ntable.printRows();
		
//		String[] cols = ntable.getColumnLabels();
//		System.out.println("cols:" + Arrays.toString(cols));
//		String[][] columnOrders = {{"SALT_30MIN_ROOT", "SALT_1H_ROOT",  "SALT_3H_ROOT", "SALT_6H_ROOT", "SALT_12H_ROOT",  "SALT_24H_ROOT"}, {"SALT_30MIN_SHOOT", "SALT_1H_SHOOT",  "SALT_3H_SHOOT", "SALT_6H_SHOOT", "SALT_12H_SHOOT",  "SALT_24H_SHOOT"},{"OSMOTIC_30MIN_ROOT", "OSMOTIC_1H_ROOT",  "OSMOTIC_3H_ROOT", "OSMOTIC_6H_ROOT", "OSMOTIC_12H_ROOT",  "OSMOTIC_24H_ROOT"},{"OSMOTIC_30MIN_SHOOT", "OSMOTIC_1H_SHOOT",  "OSMOTIC_3H_SHOOT", "OSMOTIC_6H_SHOOT", "OSMOTIC_12H_SHOOT",  "OSMOTIC_24H_SHOOT"}};
//		boolean[][] data = (boolean[][])ntable.getColsAsArray(cols);
////		System.out.println("rows again:" + data[0].length);
//		String[][] domainLabels = {{"0.5", "1", "3", "6", "12", "24"}, {"0.5", "1", "3", "6", "12", "24"},{"0.5", "1", "3", "6", "12", "24"},{"0.5", "1", "3", "6", "12", "24"}};
//		String[] plottitles = {"Salt Root", "Salt Shoot", "Osmotic Root", "Osmotic Shoot"};
//		
//		BarClusterPlot b = new BarClusterPlot(data, cols, columnOrders, domainLabels, plottitles);
		
		
		
	}
	
	public void chartMouseClicked(ChartMouseEvent event)
	{
		JFreeChart chart = event.getChart();
		
		int x = event.getTrigger().getX(); 
		int y = event.getTrigger().getY();
//		BaseChartPanel panel = (BaseChartPanel) event.getTrigger().getComponent();
		ChartPanel panel = (ChartPanel) event.getTrigger().getComponent();
		
//		if(panel.zoomRectangle != null  && (panel.zoomRectangle.getHeight() > 30 || panel.zoomRectangle.getWidth() > 30))
//		{
//			System.out.println(panel.zoomRectangle);
//			return;
//		}
		
//		System.out.println("zoomRectangle" + panel.zoomRectangle);
        
		/* The following translation takes account of the fact that the 
		 * chart image may have been scaled up or down to fit the panel...
		 * Point2D p = this.panel.translateScreenToJava2D(new Point(x, y));
		 */
		
		/* Now convert the Java2D coordinate to axis coordinates...*/
		XYPlot plot = chart.getXYPlot();
		
		ChartRenderingInfo info = panel.getChartRenderingInfo();
		Rectangle2D dataArea = info.getPlotInfo().getDataArea();
		double xdata = plot.getDomainAxis().java2DToValue(x, dataArea, plot.getDomainAxisEdge());
		double ydata = plot.getRangeAxis().java2DToValue(y, dataArea, plot.getRangeAxisEdge());
			
		xdata += 0.5;/* HACK: To adjust for sliding the bars by -0.5 to make the xticks look nice*/
		System.out.println(xdata + " " + ydata);
		
		if(!this.charts.contains(chart))
		{
			System.out.println("Chart not contained in charts");
			return;
		}

//		
//			genelist = None
//			
//			rectlist = self.rectGeneData[chartindex][0]
//			rectToGenelistMap = self.rectGeneData[chartindex][1]
			
		for (Iterator iter = ((HashMap)this.chartToRectGeneMap.get(chart)).keySet().iterator(); iter.hasNext();)
		{
//			System.out.println(iter.next().getClass().getName());
			Rectangle rect = (Rectangle) iter.next();
			
//			Rectangle rect = new Rectangle();
//			for rect in rectlist:
			
			if(rect.contains(xdata, ydata))
			{
//				if rect.contains(xdata, ydata):
//				if()
//					if self.alreadycomputed.has_key(rect):
//						self.plotOverlay(self.jfreechartlist, self.rectGeneData, genelist, datasets=self.alreadycomputed[rect])
//					else:
						IntArrayList genelist = (IntArrayList)this.chartToRectGeneMap.get(rect);
						System.out.println("Clicked rect "+ rect);
//						# print "Clicked genelist ", len(genelist)
//						self.alreadycomputed[rect] = self.plotOverlay(self.jfreechartlist, self.rectGeneData, genelist)
//						this.plotOverlay(self.jfreechartlist, self.rectGeneData, genelist)
						break;
			}
			
		}
	}

	public void chartMouseMoved(ChartMouseEvent event)
	{

	}
	
	

}
