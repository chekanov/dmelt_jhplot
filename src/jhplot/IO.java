
/**
*    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
*    All rights reserved.
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

package jhplot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import jhplot.gui.HelpBrowser;
import java.util.ArrayList;
import java.util.zip.*;
import java.util.regex.Matcher;


/**
 * 
 * Methods used for input-output serialization of Java objects. Includes write (read) for the standard
 * serialization. Objects can be compressed on-fly using GZip. Also, one can use
 * XML format for serialization (writeXML(), readXML()). XML serialization is
 * based on XSream and not compressed. Files can be read from URL. This package is equivalent to 
 * jhplot.io.Serialized() used in jHepWork. 
 * <p>This utility also helps  to work with files. You can use it to extract files and directories of a standard zip file to
 * a destination directory, make list of files etc.
 * 
 * @author S.Chekanov
 * 
 */

public class IO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;
	static private String match="";
	static private ArrayList<String> myArr;
	static private boolean doMatch=false;
	static private Pattern pattern;



	/**
	  * Extracts a zip file specified by the zipFilePath to a directory specified by
	  * destDirectory (will be created if does not exists)
	  * @param zipFilePath input zip file 
	  * @param destDirectory output directory 
	  */
	public static String unzip(String zipFilePath, String destDirectory) throws IOException {


		String tmp="Error unziping: "+zipFilePath;
		try {
			int nn=unzipFile(zipFilePath, destDirectory);
			tmp="-> Input zip file:"+zipFilePath+"\n";
			tmp=tmp+"-> Successfully unziped "+Integer.toString(nn)+" files to "+destDirectory;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmp;
	}


	/**
	* Extracts a zip file specified by the zipFilePath to the curent directory.
	* @param zipFilePath input zip file  
	*/
	public static String unzip(String zipFilePath) throws IOException {

		String tmp="Error unziping "+zipFilePath;
		try {
			File ff=new File(zipFilePath);
			String dir= ff.getAbsoluteFile().getParent();
			int nn=unzipFile(zipFilePath, dir);
			tmp="-> Input zip file:"+zipFilePath+"\n";
			tmp=tmp+"-> Successfully unziped "+Integer.toString(nn)+" files to "+dir;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmp;
	}


	/**
	 * Write an object to a serialized file. Use the method read() to read it
	 * back.
	 * 
	 * @param o
	 *            object to serialize into a file
	 * 
	 * @param name
	 *            serialized file name for output.
	 * @param compression
	 * 
	 *            set true if file should be compressed.
	 * 
	 * @return zero if no errors
	 */
	public static int write(Object o, String name, boolean compressed) {

		try {
			FileOutputStream outFile = new FileOutputStream(name);
			ObjectOutputStream outStream;
			if (compressed == true) {
				outStream = new ObjectOutputStream(
				                new GZIPOutputStream(outFile));
			} else {
				outStream = new ObjectOutputStream(outFile);
			}

			outStream.writeObject(o);
			outStream.close();
			outFile.close();
		} catch (FileNotFoundException e) {
			ErrorMessage(e.toString());
			return 2;
		} catch (IOException e) {
			ErrorMessage(e.toString());
			return 1;
		}

		return 0;
	}

	/**
	 * Write an object to a serialized file. Use the method read() to read it
	 * back. File will be compressed.
	 * 
	 * @param o
	 *            object to serialize into a file
	 * 
	 * @param name
	 *            serialized file name for output.
	 * 
	 * @return zero if no errors
	 */
	public static int write(Object o, String name) {

		return write(o, name, true);

	}

	/**
	 * Write an object to a serialized XML file. Use the method readXML() to get
	 * it back.
	 * 
	 * @param o
	 *            object to serialize into a file
	 * 
	 * @param name
	 *            serialized file name for the output.
	 * 
	 * @return zero if no errors
	 */
	public static int writeXML(Object o, String name) {

		try {
			XStream xstream = new XStream(new DomDriver());
			ObjectOutputStream out = xstream
			                         .createObjectOutputStream(new FileOutputStream(name));

			out.writeObject(o);
			out.close();
		} catch (IOException e) {

			ErrorMessage(e.toString());
			return 1;
		}

		return 0;

	}

	/**
	 * Read an object from a file. It can read a file from URL if the string starts from
	 * http or ftp, otherwise a file on the file system is assumed.
	 * 
	 * @param sfile
	 *            file name. Can be URL location of the file.
	 * @param compressed
	 *            true if compressed
	 * @return object from the file
	 */

	private static Object read(String sfile, boolean compressed) {

		sfile = sfile.trim();

		if (sfile.startsWith("http") || sfile.startsWith("ftp")) {

			try {
				return read(new URL(sfile), compressed);
			} catch (MalformedURLException e) {
				ErrorMessage(e.toString());
			}

		} else {
			return read(new File(sfile), compressed);
		}

		return null;

	}

	/**
	 * Read an object from a serialized file
	 * 
	 * @param file
	 *            serialized file for input.
	 * 
	 * @return object
	 * 
	 * @param compressed
	 *            set true if file is compressed.
	 * 
	 */
	public static Object read(File file, boolean compressed) {

		Object p0 = null;

		try {

			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in;

			if (compressed == true) {
				in = new ObjectInputStream(new GZIPInputStream(fileIn));
			} else {
				in = new ObjectInputStream(fileIn);
			}

			p0 = in.readObject();



			in.close();
			fileIn.close();

		} catch (ClassNotFoundException e) {
			ErrorMessage(e.toString());
		} catch (FileNotFoundException e) {
			ErrorMessage(e.toString());
		} catch (IOException e) {
			ErrorMessage(e.toString());
		}

		return p0;

	}

	/**
	 * Read an object from an URL
	 * 
	 * @param name
	 *            serialized file name for input from URL
	 * 
	 * @param compressed
	 *            set true if file is compressed.
	 *            
	 *  @return object          
	 * 
	 */
	public static Object read(URL url, boolean compressed) {

		Object p0 = null;

		try {
			URLConnection urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setUseCaches(false);

			ObjectInputStream in;
			if (compressed == true) {
				in = new ObjectInputStream(new GZIPInputStream(urlConn
				                           .getInputStream()));
			} else {
				in = new ObjectInputStream(urlConn.getInputStream());
			}

			p0 = in.readObject();
			in.close();

		} catch (ClassNotFoundException e) {
			ErrorMessage(e.toString());
		} catch (IOException e) {
			ErrorMessage(e.toString());
		}

		return p0;

	}

	/**
	 * Read an object from a serialized file. File is assumed to be compressed,
	 * i.e when it was saved using write(obj,name) method.
	 * 
	 * @param name
	 *            serialized file name for input.
	 * 
	 * @return object
	 * 
	 */
	public static Object read(String name) {

		return read(name, true);

	}

	/**
	 * Read an object from a serialized XML file (should be written using
	 * writeXML() method).
	 * 
	 * @param name
	 *            serialized XML file name for the input.
	 * 
	 * @return object
	 */
	public static Object readXML(String name) {

		Object p0 = null;

		try {
			XStream xstream = new XStream(new DomDriver());
			FileInputStream fileIn = new FileInputStream(name);
			ObjectInputStream ino = xstream.createObjectInputStream(fileIn);
			try {
				p0 = ino.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				ErrorMessage(e.toString());
				return 2;
			}
			ino.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ErrorMessage(e.toString());
			return 1;
		}

		return p0;

	}

	/**
	 * Convert object to XML using XSream package
	 * 
	 * @param ob
	 * @return XML string
	 */
	public static String toXML(Object ob) {

		XStream xstream = new XStream(new DomDriver());
		String xml = xstream.toXML(ob);
		return xml;
	}

	/**
	 * Get object to XML using XSream package
	 * 
	 * @param xml
	 *            XML string
	 * @return object
	 */
	public static Object fromXML(String xml) {

		XStream xstream = new XStream(new DomDriver());
		return xstream.fromXML(xml);

	}

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */

	static private void ErrorMessage(String a) {
		jhplot.utils.Util.ErrorMessage(a);
	}


	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	private static int unzipFile(String zipFilePath, String destDirectory) throws IOException {

		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}

		int ntot=0;
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
				ntot++;
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
		return ntot;
	}


	/**
	 * Extracts a zip entry (file entry)
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}





	/**
	* Get list of files in all directories (including subdirectories).
	* @param input directory
	* @param m a string for regular expression to find matches in file name           
	**/
	public static ArrayList  getFileList(String dir, String m) {
		match = m.trim();
		if (match.length()>0) {
			doMatch=true;
			pattern = Pattern.compile( m );
		}

		myArr = new ArrayList<String>();
		traverse(new File(dir));
		return myArr;
	}


	/**
	    * Show online documentation.
	    */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}





	private static void processFile(File dir) {

		// only files
		if (dir.isDirectory() == false) {
			String sdir=dir.toString();
			if (doMatch){
				// int index = sdir.indexOf(match);
				Matcher matcher = pattern.matcher(sdir);
				boolean matchFound = matcher.find();
				if (matchFound)
					myArr.add(sdir);
			} else {
				myArr.add(sdir);
			}

		}

	}

	private static void traverse(File dir) {
		processFile(dir);
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				traverse(new File(dir, children[i]));
			}
		}

	}







}
