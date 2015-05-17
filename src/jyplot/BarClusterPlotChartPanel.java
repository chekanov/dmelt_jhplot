package jyplot;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PlotOrientation;

/**
 * Custom class for different mouse behaviours.
 * 
 * @author dion
 */
public class BarClusterPlotChartPanel extends BaseChartPanel
{
//	
//	/**
//	 * Mofication to the original class so that mouse clicks are only sent
//	 * when the panel doesn't zooom.
//	 */
//	boolean sendClickEvent = false;
//	
	public BarClusterPlotChartPanel(JFreeChart chart)
	{
		super(chart);
	}
//
//	/**
//	 * Receives notification of mouse clicks on the panel. These are translated
//	 * and passed on to any registered chart mouse click listeners.
//	 * 
//	 * @param event
//	 *            Information about the mouse event.
//	 */
//	public void mouseClicked(MouseEvent event)
//	{
//
//		
//		
//		
//		Insets insets = getInsets();
//		int x = (int) ((event.getX() - insets.left) / this.scaleX);
//		int y = (int) ((event.getY() - insets.top) / this.scaleY);
//
//		this.anchor = new Point2D.Double(x, y);
//		this.chart.setNotify(true); // force a redraw
//		// new entity code...
//		Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
//		if(listeners.length == 0) { return; }
//
//		ChartEntity entity = null;
//		if(this.info != null)
//		{
//			EntityCollection entities = this.info.getEntityCollection();
//			if(entities != null)
//			{
//				entity = entities.getEntity(x, y);
//			}
//		}
//		
//		if(this.sendClickEvent)
//    	{
//			this.sendClickEvent = false;
//			ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), event, entity);
//			for (int i = listeners.length - 1; i >= 0; i -= 1)
//			{
//				((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
//			}
//    	}
//
//	}
//	
//    /**
//     * Handles a 'mouse pressed' event.
//     * <P>
//     * This event is the popup trigger on Unix/Linux.  For Windows, the popup
//     * trigger is the 'mouse released' event.
//     *
//     * @param e  The mouse event.
//     */
//    public void mousePressed(MouseEvent e) {
//        if (this.zoomRectangle == null) {
//            Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
//            if (screenDataArea != null) {
//                this.zoomPoint = getPointInRectangle(
//                    e.getX(), e.getY(), screenDataArea
//                );
//            }
//            else {
//                this.zoomPoint = null;
//            }
//            if (e.isPopupTrigger()) {
//                if (this.popup != null) {
//                    displayPopupMenu(e.getX(), e.getY());
//                }
//            }
//        }
//    }
//    
//    /**
//     * Handles a 'mouse released' event.  On Windows, we need to check if this 
//     * is a popup trigger, but only if we haven't already been tracking a zoom
//     * rectangle.
//     *
//     * @param e  information about the event.
//     */
//    public void mouseReleased(MouseEvent e) {
//
//        if (this.zoomRectangle != null) {
//            boolean hZoom = false;
//            boolean vZoom = false;
//            if (this.orientation == PlotOrientation.HORIZONTAL) {
//                hZoom = this.rangeZoomable;
//                vZoom = this.domainZoomable;
//            }
//            else {
//                hZoom = this.domainZoomable;              
//                vZoom = this.rangeZoomable;
//            }
//            
//            boolean zoomTrigger1 = hZoom && Math.abs(e.getX() 
//                - this.zoomPoint.getX()) >= this.zoomTriggerDistance;
//            boolean zoomTrigger2 = vZoom && Math.abs(e.getY() 
//                - this.zoomPoint.getY()) >= this.zoomTriggerDistance;
//            if (zoomTrigger1 || zoomTrigger2) {
//            	
//            	this.sendClickEvent = false;
//            	
//                if ((hZoom && (e.getX() < this.zoomPoint.getX())) 
//                    || (vZoom && (e.getY() < this.zoomPoint.getY()))) {
//                    restoreAutoBounds();
//                }
//                else {
//                    double x, y, w, h;
//                    Rectangle2D screenDataArea = getScreenDataArea(
//                        (int) this.zoomPoint.getX(), 
//                        (int) this.zoomPoint.getY()
//                    );
//                    // for mouseReleased event, (horizontalZoom || verticalZoom)
//                    // will be true, so we can just test for either being false;
//                    // otherwise both are true
//                    if (!vZoom) {
//                        x = this.zoomPoint.getX();
//                        y = screenDataArea.getMinY();
//                        w = Math.min(
//                            this.zoomRectangle.getWidth(),
//                            screenDataArea.getMaxX() - this.zoomPoint.getX()
//                        );
//                        h = screenDataArea.getHeight();
//                    }
//                    else if (!hZoom) {
//                        x = screenDataArea.getMinX();
//                        y = this.zoomPoint.getY();
//                        w = screenDataArea.getWidth();
//                        h = Math.min(
//                            this.zoomRectangle.getHeight(),
//                            screenDataArea.getMaxY() - this.zoomPoint.getY()
//                        );
//                    }
//                    else {
//                        x = this.zoomPoint.getX();
//                        y = this.zoomPoint.getY();
//                        w = Math.min(
//                            this.zoomRectangle.getWidth(),
//                            screenDataArea.getMaxX() - this.zoomPoint.getX()
//                        );
//                        h = Math.min(
//                            this.zoomRectangle.getHeight(),
//                            screenDataArea.getMaxY() - this.zoomPoint.getY()
//                        );
//                    }
//                    Rectangle2D zoomArea = new Rectangle2D.Double(x, y, w, h);
//                    zoom(zoomArea);
//                }
//                this.zoomPoint = null;
//                this.zoomRectangle = null;
//            }
//            else {
//                Graphics2D g2 = (Graphics2D) getGraphics();
//                g2.setXORMode(java.awt.Color.gray);
//                if (this.fillZoomRectangle) {
//                    g2.fill(this.zoomRectangle);
//                }
//                else {
//                    g2.draw(this.zoomRectangle);
//                }
//                g2.dispose();
//                this.zoomPoint = null;
//                this.zoomRectangle = null;
//                
//                /*
//                 * Only send events of mouse clicks if no zooming is done
//                 */
//                this.sendClickEvent = true;
//            }
//
//        }
//
//        else if (e.isPopupTrigger()) {
//            if (this.popup != null) {
//                displayPopupMenu(e.getX(), e.getY());
//            }
//        }
//        else
//        	this.sendClickEvent = true;
//
//    }
}
