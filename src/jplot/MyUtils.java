/**
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


package jplot;

import java.awt.*;
import javax.swing.*;


/**
 * Provides a number of handy functions which I use all the time,
 * but which are too short to bring into a package or class.
 */
public class MyUtils {

  static final Font labelFont = new Font("SansSerif",Font.PLAIN,11);

  /**
   * Make a label specifically used by the tic-panel.
   * @param txt text of the label
   * @return a label with specific font and alignments.
   */
  public static JLabel getLabel(String txt)
  {
    JLabel label = new JLabel(txt);
    label.setVerticalAlignment(SwingConstants.BOTTOM);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setFont(labelFont);
    label.setForeground(Color.black);
    return label;
  }

  /**
   * Builds a personalized checkbox using a smaller than usual font.
   */
  static JCheckBox createCheckBox(String s, boolean b) {
    JCheckBox cb = new JCheckBox(" " + s + " ",b);
    cb.setFont(new Font("serif", Font.PLAIN, 12));
    cb.setHorizontalAlignment(JCheckBox.LEFT);
    return cb;
  }

  /**
   * Very MS-Windoze-like. This panel builds a list and returns it
   * wrapped in a minimized scrollpane panel.
   * @param a array of objects which fill the list
   * @param index initially selected index
   * @return scrollpane panel 
   */
  static JScrollPane getCounter(Object[] a, int index) {
    JList list = new JList(a);
    list.setSelectedIndex(index);
    list.ensureIndexIsVisible(index);
    list.setVisibleRowCount(1);
    list.setSelectionBackground(list.getBackground());
    return new JScrollPane(list);
  }
}

