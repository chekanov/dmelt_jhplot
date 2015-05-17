/*
 * PrintFilePainter  -- a class by which we can print files.
 *                      Borrowed from the 2D tutorial and adapted.
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
import java.io.*;
import java.awt.print.*;

/**
 * This class allows to print a file
 */
public class PrintFilePainter implements Printable {
  private RandomAccessFile raf;   
  private String fileName="none";   
  private Font fnt = new Font("Monospaced", Font.PLAIN, 11);
  private Color color = Color.black;
  private int rememberedPageIndex = -1;   
  private long rememberedFilePointer = -1;   
  private boolean rememberedEOF = false;
  private int size = 13;
  
  /**
   * Builds the painter. Makes the instance from a file. No
   * checking on the existence of the file is done: do this
   * before building this class. Makes a random access file of
   * of the file for better performance
   * @param file name of the file.
   */
  public PrintFilePainter(String file) { 
    fileName = file;     
    try {
      raf = new RandomAccessFile(file, "r");     
    } 
    catch (Exception e) { rememberedEOF = true; }   
  }

  public void setFont(Font f) {
    fnt = f;
    size = fnt.getSize() + 2;
  }

  public void setForeground(Color c) {
    color = c;
  }
  
  public int print(Graphics g, PageFormat pf, int pageIndex)
    throws PrinterException {
    try { 
      // For catching IOException     
      System.out.println("pageIndex = " + pageIndex);
      System.out.println("rememberedPageIndex = " + rememberedPageIndex);
      if (pageIndex != rememberedPageIndex) { 
	// First time we've visited this page
	rememberedPageIndex = pageIndex;        
	// If encountered EOF on previous page, done 
	if (rememberedEOF) return Printable.NO_SUCH_PAGE;
	// Save current position in input file
	rememberedFilePointer = raf.getFilePointer();
      } 
      else raf.seek(rememberedFilePointer);
      g.setColor(color);
      g.setFont(fnt); 
      int x = (int) pf.getImageableX();
      int y = (int) pf.getImageableY();    
      // Title line     
      //g.drawString("File: " + fileName + ", page: " + (pageIndex+1),  x, y);
      // Generate as many lines as will fit in imageable area
      y += 32;
      while (y + size < pf.getImageableY()+pf.getImageableHeight()){
	String line = raf.readLine();
	if (line == null) { 
	  rememberedEOF = true;
	  break; 
	}
	g.drawString(line, x, y); 
	y += size;     
      }
      return Printable.PAGE_EXISTS;    
    } 
    catch (Exception e) { return Printable.NO_SUCH_PAGE;}
  } 
}
