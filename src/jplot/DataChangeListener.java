/*
 * JPLOT
 * 
 * Copyright (C) 1999 Jan van der Lee
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  
 * 02111-1307, USA.
 *
 * Send bugs, suggestions or queries to <jplot@cig.ensmp.fr>
 * The latest releases are found at 
 * http://www.cig.ensmp.fr/~vanderlee/jplot.html
 *
 * Initally developed for use by the Centre d'Informatique Geologique 
 * Ecole des Mines de Paris, Fontainebleau, France.
 */

package jplot;

import javax.swing.*;

/**
 * This specific interface is used by classes which write data to
 * a specific file. The class provides a title displayed in the frame
 * with a typical '*' if the data was changed by the app but not yet saved
 * to the file. The title is the filename or 'scratch' if null.
 */
public class DataChangeListener {
  boolean changed;
  String titleName;
  String frameTitle;
  JFrame frame;

  public DataChangeListener() {
    titleName = "scratch";
    frameTitle = "<untitled>";
    frame = null;
  }
  
  /**
   * @return true if the settings were changed by the user
   */
  public boolean dataChanged() {
    return changed;
  }

  /**
   * @param f frame
   */
  public void setFrame(JFrame f) {
    frameTitle = f.getTitle();
    frame = f;
  }
  
  /**
   * @return the current frame
   */
  public JFrame getFrame() {
    return frame;
  }
  
  /**
   * @param s new name to be displayed in the title
   */
  public void setTitleName(String s) {
    if (s != null) {
      titleName = fileToShortName(s);
      setTitleString();
    }
  }
  
  /**
   * Allows to set the flag indicating that the data has
   * changed to true or false. This function also allows some
   * action in the case of a data change, such as e.g. a '*' in
   * the frame-title bar or saving the data...
   * @param b true if the settings are changed
   */
  public void setDataChanged(boolean b) {
    changed = b;
    setTitleString();
  }

  private String fileToShortName(String s) {
    int len = s.length();
    if (len > 35) {
      return "..." + s.substring(len-30,len);
    }
    return s;
  }

  /**
   * Write the title of the CHESS frame with the actual CHESS script file,
   * adding a '*' if the file is modified but not yet saved. Do nothing
   * if JPlot is not a stand-alone app.
   */
  public void setTitleString() {
    if (frame != null) {
      String name = frameTitle + " - " + titleName;
      if (changed) name += "*";
      frame.setTitle(name);
    }
  }
}
