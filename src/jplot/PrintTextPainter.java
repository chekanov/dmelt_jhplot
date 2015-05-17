/*
 * PrintTextPainter  -- a class by which we can print a text string.
 * 
 * Copyright (C) 2001 Jan van der Lee
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

import java.awt.*;
import java.util.*;
import java.awt.print.*;

/**
 * This class allows to print a file
 */
public class PrintTextPainter implements Printable {
  private String text;   
  private Font fnt = new Font("Monospaced", Font.PLAIN, 10);
  private Color color = Color.black;
  private int rememberedPageIndex = -1;   
  private boolean endOfText = false;
  private Vector<String> lines; 
  private int size=12;
  //private int itemIndex=0;
  private Enumeration e;
  
  /**
   * Builds the painter.
   * @param txt text to print
   */
  public PrintTextPainter(String txt) { 
    text = txt;
    lines = new Vector<String>();
    if (text != null && !text.equals("")) makeLines();
    else endOfText = true;
  }

  public void setFont(Font f) {
    fnt = f;
    size = fnt.getSize()+2;
  }

  public void setForeground(Color c) {
    color = c;
  }

  /*
   * kind of tokenizer. Better suited for the current job
   * than a call to StringTokenizer(text,"\n") since this will
   * eliminate all empty strings...
   */
  private void makeLines() {
    char lf = Utils.lf.charAt(0);
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<text.length(); i++) {
      char c = text.charAt(i);
      if (c == lf) {
	lines.add(sb.toString());
	sb = new StringBuffer();
      }
      else sb.append(c);
    }
    e = lines.elements();
  }
  
  public int print(Graphics g, PageFormat pf, int pageIndex)
    throws PrinterException {
    try { 
      if (pageIndex != rememberedPageIndex) {
	rememberedPageIndex = pageIndex;
	if (endOfText) return Printable.NO_SUCH_PAGE;
      }
      else e = lines.elements();
      g.setColor(color);
      g.setFont(fnt); 
      int x = (int) pf.getImageableX();
      int y = (int) pf.getImageableY();    
      y += 36;
      while (y + size < pf.getImageableY()+pf.getImageableHeight()){
	if (!e.hasMoreElements()) {
	  endOfText = true;
	  break;
	}
	String line = (String)e.nextElement();
	//System.out.println("printing: " + line);
	g.drawString(line, x, y); 
	y += size;
      }
      return Printable.PAGE_EXISTS;    
    } 
    catch (Exception e) {
      return Printable.NO_SUCH_PAGE;
    }
  } 
}
