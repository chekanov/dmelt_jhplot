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
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.File;
import java.util.*;
import java.util.regex.*;

/**
 * This utility classes to work with files. You can use it to extract files and directories of a standard zip file to
 * a destination directory, make list of files etc.
 *
 */
public class IO {
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
