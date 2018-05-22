/**
 * *    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
 * *    All rights reserved.
 * *
 * *    This program is free software; you can redistribute it and/or modify it under the terms
 * *    of the GNU General Public License as published by the Free Software Foundation; either
 * *    version 3 of the License, or any later version.
 * *
 * *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * *    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * *    See the GNU General Public License for more details.
 * *
 * *    You should have received a copy of the GNU General Public License along with this program;
 * *    if not, see <http://www.gnu.org/licenses>.
 * *
 * *    Additional permission under GNU GPL version 3 section 7:
 * *    If you have received this program as a library with written permission from the DataMelt team,
 * *    you can link or combine this library with your non-GPL project to convey the resulting work.
 * *    In this case, this library should be considered as released under the terms of
 * *    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
 * *    provided you include this license notice and a URL through which recipients can access the
 * *    Corresponding Source.
 * **/
package jhplot;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;

import java.nio.file.Files;
import java.nio.file.Paths;

import net.sourceforge.plantuml.*;
import java.io.*;
import java.nio.charset.*;
import java.util.zip.GZIPOutputStream;


/**
 * Create diagrams from a string or a file using the PlantUML syntax. See http://plantuml.com/.  It allows to make
 * Sequence diagram <br> 
 * Usecase diagram <br> 
 * Class diagram <br> 
 * Activity diagram (here is the legacy syntax) <br> 
 * Component diagram <br>
 * State diagram <br> 
 * Object diagram <br> 
 * Deployment diagram <br> 
 * Timing diagram <br> 
 * In addition, it makes: Specification and Description Language (SDL) <br>
 * Ditaa diagram <br>
 * Gantt diagram <br>
 * Mathematic with AsciiMath or JLaTeXMath notation.
 * <br>
 * Diagrams can be exported to EPS, PDF, PNG, SVG images. 
 * @author S.Chekanov
 * 
 */

public class HDiagram {

	private static final long serialVersionUID = 1L;

	private  SourceStringReader   reader;

	/**
	* Initialize a from a source string. 
	* 
	* @param  source  
	*            diagram using PlantUML Language.  
	*/
	public HDiagram(String source) {
		reader= new SourceStringReader(source);
	}


	/**
	 * Default constructor.
	 */
	public HDiagram() {

	}


	/**
	* Build a diagram from a string.
	* @param source string in PlantUML Language. 
	*/
	public void fromString(String source) {
		reader= new SourceStringReader(source);
	}

	/**
	* Build a diagram from a file
	* @param file file with diagram in PlantUML Language. 
	*/
	public void fromFile(String file) {

		try {
			String content = new String(Files.readAllBytes(Paths.get("file")));
			reader= new SourceStringReader(content);
		} catch (IOException e) {
			System.err.println(e.toString());
		}



	}





	/**
	   * Return reader. 
	   * @return reader.
	   **/
	public SourceStringReader getReader(){
		return reader;
	}

	/**
	       * Fast export of the diagram to an image file. The ouput format depends on the
	       * file extension: <br>
	       * SVG - Scalable Vector Graphics (SVG) <br>
	       * SVGZ - compressed SVG<br>
	       * PNG - raster format<br>
	       * PDF - PDF<br>
	       * EPS - PostScript (encapsulated)<br>
	       * HTMP5 - HTML5 <br>
	       * <p>
	       * No questions will be asked and existing file will be rewritten
	       * 
	       * @param file
	       *            Output file with the proper extension (SVG, SVGZ, PNG, PDF,
	       *            EPS). If no extension, PNG file is assumed.
	       */

	public void export(final String file) {

		int dot = file.lastIndexOf('.');
		String base = (dot == -1) ? file :file.substring(0, dot);
		String fext = (dot == -1) ? "" : file.substring(dot + 1);
		fext = fext.trim();

		// System.out.println("Saving="+fext);

		// fixing vector formats
		boolean isSVGZ = false;
		if (fext.equalsIgnoreCase("svgz")) {
			isSVGZ = true;
		}
		boolean isEPS = false;
		if (fext.equalsIgnoreCase("eps")) {
			isEPS = true;
		}
		boolean isPDF = false;
		if (fext.equalsIgnoreCase("pdf")) {
			isPDF = true;
		}
		boolean isPNG = false;
		if (fext.equalsIgnoreCase("png")) {
			isPNG = true;
		}
		boolean isSVG = false;
		if (fext.equalsIgnoreCase("svg")) {
			isSVG = true;
		}
		boolean isJPG = false;
		if (fext.equalsIgnoreCase("jpg") || fext.equalsIgnoreCase("jpeg")) {
			isJPG = true;
		}
		boolean isHTML = false;
		if (fext.equalsIgnoreCase("html") || fext.equalsIgnoreCase("htm")) {
			isHTML = true;
		}

		if (isSVGZ == false && isEPS == false &&  isPDF == false && isPNG == false &&  isSVG==false && isJPG  == false && isHTML == false){
			System.err.println("Unsuported format file!  Only svg, svgz, eps, pmg, jpeg, html, eps, pdf are supported");
			return;
		}

		try {


			if (isSVG) {

				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
				os.close();
				String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
				BufferedWriter writer = null;
				try
				{
					writer = new BufferedWriter( new FileWriter(file));
					writer.write(svg);

				}
				catch ( IOException e ) {}
				finally
				{
					try
					{
						if ( writer != null)
							writer.close( );
					}
					catch ( IOException e)
					{
					}
				}

			}



			else if (isSVGZ) {

				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
				os.close();
				String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));




				try {
					InputStream is = new ByteArrayInputStream(svg.getBytes());
					GZIPOutputStream gzipOS = new GZIPOutputStream(new FileOutputStream(file));
					byte[] buffer = new byte[1024];
					int len;
					while ((len = is.read(buffer)) != -1) {
						gzipOS.write(buffer, 0, len);
					}
					gzipOS.close();
					is.close();
				} catch (IOException ex) {
					// Handle exception
				}


			}




			else if (isPNG) {
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.PNG));
				os.close();
				FileOutputStream fos = new FileOutputStream( new File(file));
				os.writeTo(fos);
			}



			else if (isJPG) {
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.MJPEG));
				os.close();
				FileOutputStream fos = new FileOutputStream( new File(file));
				os.writeTo(fos);
			}



			else if (isPDF) {
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.PDF));
				os.close();
				FileOutputStream fos = new FileOutputStream( new File(file));
				os.writeTo(fos);
			}


			else if (isEPS) {
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.EPS));
				os.close();

				String eps = new String(os.toByteArray(), Charset.forName("UTF-8"));
				BufferedWriter writer = null;
				try {
					writer = new BufferedWriter( new FileWriter(file));
					writer.write(eps);

				}
				catch ( IOException e ) {}
				finally
				{
					try
					{
						if ( writer != null)
							writer.close( );
					}
					catch ( IOException e)
					{
					}
				}

			}

			else if (isHTML) {
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.HTML5));
				os.close();

				String eps = new String(os.toByteArray(), Charset.forName("UTF-8"));
				BufferedWriter writer = null;
				try {
					writer = new BufferedWriter( new FileWriter(file));
					writer.write(eps);

				}
				catch ( IOException e ) {}
				finally
				{
					try
					{
						if ( writer != null)
							writer.close( );
					}
					catch ( IOException e)
					{
					}
				}

			}







		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}


	/*
	* Show online documentation.
	*/
	public void doc() {

		String a=this.getClass().getName();
		a=a.replace(".", "/")+".html";
		new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);



	}
}
