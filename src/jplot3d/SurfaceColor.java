package jplot3d;

import java.awt.Color;

/**
 * Interface used by JSurface for every color. Warning, some color are not
 * suitable for some drawing, be careful to sync it with the SurfaceModel
 * 
 * @author eric and S.Chekanov
 * 
 */
public interface SurfaceColor {

	public Color getBackgroundColor();

	public Color getLineBoxColor();

	public Color getBoxColor();

	public Color getLineColor();

	public Color getTextColor();

	public Color getFillColor();

	public Color getLineColor(int curve, float z);

	public Color getPolygonColor(int curve, float z);

	public Color getFirstPolygonColor(float z);

	public Color getSecondPolygonColor(float z);

	/* Input methods */
	public void setLineBoxColor(Color c);

	public void setBackgroundColor(Color c);

	public void setBoxColor(Color c);

	public void setLineColor(Color c);

	public void setTextColor(Color c);

	void setFillColor(Color c);

}