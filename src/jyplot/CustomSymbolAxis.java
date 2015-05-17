package jyplot;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class CustomSymbolAxis extends SymbolAxis
{
	/** The list of symbol locations */
	private HashMap<Double, String> locationToSymbolMap;
	private Double[] symbolLocations;
	
	
	
	/*
	 * From superclass
	 */
	/** The list of symbols to display instead of the numeric values. */
	private List symbols;
	
	/** The paint used to color the grid bands (if the bands are visible). */
	private transient Paint gridBandPaint;

	/** Flag that indicates whether or not grid bands are visible. */
	private boolean gridBandsVisible;

	public CustomSymbolAxis(String label, String[] sv, double[] symbolLocations)
	{
		super(label, sv);
		
		// if(sv != null)
		// {
			this.symbols = Arrays.asList(sv);
			this.gridBandsVisible = true;
			this.gridBandPaint = DEFAULT_GRID_BAND_PAINT;
			
			
			locationToSymbolMap = new HashMap<Double, String>();
			this.symbolLocations = new Double[symbolLocations.length];
			
			for (int i = 0; i < symbolLocations.length && i < sv.length; i++)
			{
				locationToSymbolMap.put(new Double(symbolLocations[i]), sv[i]);
				
			}
			Arrays.sort(symbolLocations);
			for (int i = 0; i < symbolLocations.length; i++)
			{
				this.symbolLocations[i] = new Double(symbolLocations[i]);
			}
		// }
		setAutoTickUnitSelection(false, false);
		setAutoRangeStickyZero(false);
	}
	
	/**
	 * Calculates the positions of the tick labels for the axis, storing the 
	 * results in the tick label list (ready for drawing).
	 *
	 * @param g2  the graphics device.
	 * @param state  the axis state.
	 * @param dataArea  the area in which the data should be drawn.
	 * @param edge  the location of the axis.
	 * 
	 * @return A list of ticks.
	 */
	public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge)
	{
		List ticks = null;
		if(RectangleEdge.isTopOrBottom(edge))
		{
			ticks = refreshTicksHorizontal(g2, dataArea, edge);
		}
		else if(RectangleEdge.isLeftOrRight(edge))
		{
			ticks = refreshTicksVertical(g2, dataArea, edge);
		}
		return ticks;
	}
	
	/**
	 * Calculates the positions of the tick labels for the axis, storing the 
	 * results in the tick label list (ready for drawing).
	 *
	 * @param g2  the graphics device.
	 * @param dataArea  the area in which the data should be drawn.
	 * @param edge  the location of the axis.
	 * 
	 * @return The ticks.
	 */
	protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge)
	{

		List ticks = new java.util.ArrayList();

		Font tickLabelFont = getTickLabelFont();
		g2.setFont(tickLabelFont);

		double size = getTickUnit().getSize();
		int count = calculateVisibleTickCount();
		double lowestTickValue = calculateLowestVisibleTickValue();

		double previousDrawnTickLabelPos = 0.0;
		double previousDrawnTickLabelLength = 0.0;

		if(count <= ValueAxis.MAXIMUM_TICK_COUNT)
		{
			//for (int i = 0; i < count; i++)
			for (int i = 0; i < this.symbolLocations.length; i++)
			{
//				this.symbolLocations locationToSymbolMap symbols
				double currentTickValue = this.symbolLocations[i];// lowestTickValue + (i * size);
				double xx = valueToJava2D(currentTickValue, dataArea, edge);
				String tickLabel;
				NumberFormat formatter = getNumberFormatOverride();
				
				if(this.locationToSymbolMap.containsKey(new Double(currentTickValue)))
				{
					tickLabel = this.locationToSymbolMap.get(new Double(currentTickValue));
				}
				else 
				if(formatter != null)
				{
					tickLabel = formatter.format(currentTickValue);
				}
				else
				{
					tickLabel = valueToString(currentTickValue);
				}

				// avoid to draw overlapping tick labels
				Rectangle2D bounds = TextUtilities.getTextBounds(tickLabel, g2, g2.getFontMetrics());
				double tickLabelLength = isVerticalTickLabels() ? bounds.getHeight() : bounds.getWidth();
				boolean tickLabelsOverlapping = false;
				if(i > 0)
				{
					double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0;
					if(Math.abs(xx - previousDrawnTickLabelPos) < avgTickLabelLength)
					{
						tickLabelsOverlapping = true;
					}
				}
				if(tickLabelsOverlapping)
				{
					tickLabel = ""; // don't draw this tick label
				}
				else
				{
					// remember these values for next comparison
					previousDrawnTickLabelPos = xx;
					previousDrawnTickLabelLength = tickLabelLength;
				}

				TextAnchor anchor = null;
				TextAnchor rotationAnchor = null;
				double angle = 0.0;
				if(isVerticalTickLabels())
				{
					anchor = TextAnchor.CENTER_RIGHT;
					rotationAnchor = TextAnchor.CENTER_RIGHT;
					if(edge == RectangleEdge.TOP)
					{
						angle = Math.PI / 2.0;
					}
					else
					{
						angle = -Math.PI / 2.0;
					}
				}
				else
				{
					if(edge == RectangleEdge.TOP)
					{
						anchor = TextAnchor.BOTTOM_CENTER;
						rotationAnchor = TextAnchor.BOTTOM_CENTER;
					}
					else
					{
						anchor = TextAnchor.TOP_CENTER;
						rotationAnchor = TextAnchor.TOP_CENTER;
					}
				}
				Tick tick = new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
				ticks.add(tick);
			}
		}
		return ticks;

	}
	
    /**
     * Draws the axis line, tick marks and tick mark labels.
     * 
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param edge  the edge that the axis is aligned with.
     * 
     * @return The width or height used to draw the axis.
     */
    protected AxisState drawTickMarksAndLabels(Graphics2D g2, 
                                               double cursor,
                                               Rectangle2D plotArea,
                                               Rectangle2D dataArea, 
                                               RectangleEdge edge) {
                                              
        AxisState state = new AxisState(cursor);

        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }

        double ol = getTickMarkOutsideLength();
        double il = getTickMarkInsideLength();

        List ticks = refreshTicks(g2, state, dataArea, edge);
        state.setTicks(ticks);
        g2.setFont(getTickLabelFont());
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            ValueTick tick = (ValueTick) iterator.next();
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                float[] anchorPoint = calculateAnchorPoint(
                    tick, cursor, dataArea, edge
                );
                TextUtilities.drawRotatedString(
                    tick.getText(), g2, 
                    anchorPoint[0], anchorPoint[1],
                    tick.getTextAnchor(), 
                    tick.getAngle(),
                    tick.getRotationAnchor()
                );
            }

            if (isTickMarksVisible()) {
                float xx = (float) valueToJava2D(
                    tick.getValue(), dataArea, edge
                );
                Line2D mark = null;
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                if (edge == RectangleEdge.LEFT) {
                    mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
                }
                else if (edge == RectangleEdge.RIGHT) {
                    mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
                }
                else if (edge == RectangleEdge.TOP) {
                    mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
                }
                else if (edge == RectangleEdge.BOTTOM) {
                    mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
                }
                g2.draw(mark);
            }
        }
        
        // need to work out the space used by the tick labels...
        // so we can update the cursor...
        double used = 0.0;
        if (isTickLabelsVisible()) {
            if (edge == RectangleEdge.LEFT) {
                used += findMaximumTickLabelWidth(
                    ticks, g2, plotArea, isVerticalTickLabels()
                );  
                state.cursorLeft(used);      
            }
            else if (edge == RectangleEdge.RIGHT) {
                used = findMaximumTickLabelWidth(
                    ticks, g2, plotArea, isVerticalTickLabels()
                );
                state.cursorRight(used);      
            }
            else if (edge == RectangleEdge.TOP) {
                used = findMaximumTickLabelHeight(
                    ticks, g2, plotArea, isVerticalTickLabels()
                );
                state.cursorUp(used);
            }
            else if (edge == RectangleEdge.BOTTOM) {
                used = findMaximumTickLabelHeight(
                    ticks, g2, plotArea, isVerticalTickLabels()
                );
                state.cursorDown(used);
            }
        }
       
        return state;
    }
    
    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @return The value of the lowest visible tick on the axis.
     */
    protected double calculateLowestVisibleTickValue() {

//        double unit = getTickUnit().getSize();
//        double index = Math.ceil(getRange().getLowerBound() / unit);
//        return index * unit;

    	double lowerbound = getRange().getLowerBound();
    	for (int i = 0; i < this.symbolLocations.length; i++)
		{
			if(this.symbolLocations[i] > lowerbound)
				return this.symbolLocations[i];
		}
    	return 0;
    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @return The value of the highest visible tick on the axis.
     */
    protected double calculateHighestVisibleTickValue() {

//        double unit = getTickUnit().getSize();
//        double index = Math.floor(getRange().getUpperBound() / unit);
//        return index * unit;
    	
    	double upperbound = getRange().getUpperBound();
    	for (int i = this.symbolLocations.length - 1; i >= 0; i--)
		{
			if(this.symbolLocations[i] < upperbound)
				return this.symbolLocations[i];
		}
    	return 0;

    }

}
