/**
 *    Copyright (C)  DataMelt project. The jHPLot package.
 *    Includes coding developed for Centre d'Informatique Geologique
 *    by J.V.Lee priory 2000 GNU license.
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

package jplot.panels;

import javax.swing.*;
import java.awt.*;


/**
 * A customized preview panel for the color chooser.
 *
 * @author J. van der Lee
 * @see JColorChooser
 */
public class ColorPreview extends JPanel {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Dimension window;
  
  public ColorPreview() {
    window = new Dimension(70,40);
  }

  public Dimension getPreferredSize() {
    return window;
  }

  public void paintComponent(Graphics g) {
    g.setColor(Color.black);
    g.drawLine(0,0,window.width-1,0);
    g.drawLine(0,0,0,window.height-1);
    g.setColor(Color.white);
    g.drawLine(window.width-1,0,window.width-1,window.height-1);
    g.drawLine(window.width-1,window.height-1,0,window.height-1);
    g.setColor(getForeground());
    g.fillRect(1,1,window.width-1,window.height-1);
  }
}
