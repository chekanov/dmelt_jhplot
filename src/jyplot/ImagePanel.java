/*
 * Created on Jun 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jyplot;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.JPanel;

public class ImagePanel extends JPanel implements Serializable
{
	static final long serialVersionUID = 1;
	
	Image image = null;

	public ImagePanel() {}

	public ImagePanel(Image image) {
		this.image = image;
	}

	public void setImage(Image image)
	{
		this.image = image;
	}

	public Image getImage(Image image)
	{
		return image;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g); // paint background
		
		Graphics2D g2 = (Graphics2D) g.create();
		 
		if(image != null)
		{ // there is a picture: draw it
			int height = this.getSize().height;
			int width = this.getSize().width;
			g2.drawImage(image, 0, 0, width, height, this);
			// g.drawImage(image, 0, 0, this); //original image size
		} // end if
	} 
	
//	public void update(Graphics g)
//	{
//		paintComponent(g);
//	}
} // end class
