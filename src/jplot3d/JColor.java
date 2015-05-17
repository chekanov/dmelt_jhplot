package jplot3d;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

import org.freehep.graphics2d.VectorGraphics;

public class JColor extends JPanel
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JColor(int i, int j)
    {
        Dimension dimension = new Dimension(i, j);
        setPreferredSize(dimension);
        setMaximumSize(dimension);
        setMinimumSize(dimension);
        color = Color.RED;
        setAlignmentX(0.0F);
        addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent mouseevent)
            {
                ColorDialog colordialog = new ColorDialog(null, color);
                colordialog.setVisible(true);
                if(!colordialog.cancelled)
                {
                    int k = color.getAlpha();
                    color = Utils.changeAlpha(colordialog.getColor(), k);
                    repaint();
                }
            }

        });
    }

    public void setColor(Color color1)
    {
        color = color1;
        repaint();
    }

    public Color getColor()
    {
        return color;
    }

    public void paintComponent(Graphics g)
    {
        Rectangle rectangle = getBounds();
        VectorGraphics vg = VectorGraphics.create(g);
        
        for(int i = 0; i < rectangle.width; i += 5)
        {
            for(int j = 0; j < rectangle.height; j += 5)
            {
                if((i + j) % 10 == 0)
                    vg.setColor(Color.WHITE);
                else
                    vg.setColor(Color.BLACK);
                    vg.fillRect(i, j, 5, 5);
            }

        }

        vg.setColor(color);
        vg.fill3DRect(0, 0, rectangle.width, rectangle.height, true);
    }

    private Color color;


}
