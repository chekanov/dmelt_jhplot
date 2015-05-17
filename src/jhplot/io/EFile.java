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

package jhplot.io;


import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.*;

import jhplot.gui.HelpBrowser;
import promc.io.PEventFile.*;

/**
 * 
 * Write data structures in sequential order into ntuples
 * using Google's Prototype Buffer. The same class
 * can be used to read data from ntuples.  
 * Each data record inside files is compressed on-fly using zip. 
 * Normally, files should extension "nbu" (ntuples). 
 * A protocol Buffers file is provided which can be used for C++ input.
 * Use the CBook package to create such files in C++.
 * <p>
 * 
 * @author S.Chekanov
 * 
 */
public class EFile {

	private FileOutputStream oof = null;
	private FileInputStream iif = null;
	private int nev = 0;
	static final private int FILE_VERSION = 1;
	private ZipOutputStream zout;
	static final int BUFFER = 2048;
	private byte data[];
	private ZipInputStream zin;
	private ZipFile zipFile;
	

	/**
	 * Open a file to write/read objects to/from a file in sequential
	 * order. If "w" option is set, the old file will be removed. Use close() to
	 * flash the buffer and close the file. 
         *
	 * @param file
	 *            File name
	 * @param option
	 *            Option to create the file. If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 */
	public EFile(String file, String option) {

	        nev = 0;
		if (option.equalsIgnoreCase("w")) {

			try {
				(new File(file)).delete();
				oof = new FileOutputStream(file);
				zout = new ZipOutputStream(new BufferedOutputStream(oof));
				data = new byte[BUFFER];
				zipFile = null;

				// write file version
				ZipEntry entry = new ZipEntry("info");
				zout.putNextEntry(entry);
				String a = new String(Integer.toString(FILE_VERSION));
				byte[] theByteArray = a.getBytes();
				entry.setSize(theByteArray.length);
				zout.write(theByteArray);
				zout.closeEntry();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (option.equalsIgnoreCase("r")) {

			try {

				zipFile = new ZipFile(file);
				iif = new FileInputStream(file);
				zin = new ZipInputStream(iif);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

			ErrorMessage("Wrong option!. Only \"r\" or \"w\"  is allowed");
		}

	};

	

	/**
	 * Get version of the input file. The version is an integer
	 * written as an additional entry in the file "version".
	 * Check this by unzipping the file.
	 * @return version of the created file.
	 */
	public int getVersion() {
		
		String tmp="0";
		if (zipFile == null)
			return 0;
		ZipEntry ze = zipFile.getEntry("info");
		 long size = ze.getSize();
		 
		 
         if (size > 0) {
          // System.out.println("Length is " + size);
           BufferedReader br;
		try {
			br = new BufferedReader(
			       new InputStreamReader(zipFile.getInputStream(ze)));
			String line;
	           while ((line = br.readLine()) != null) {
	             tmp=line;
	           }
	           br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
           
        }
		return Integer.parseInt(tmp);

	}

	/**
	 * Open file for reading objects from a serialized file in sequential order.
	 * 
	 * @param file
	 *            File name
	 */
	public EFile(String file) {

		this(file, "r");

	};

	
	
	/**
	 * Add a data structure to a file
	 * 
	 * @param ev 
	 *           Data in form HEvent class.
         * 
	 * @return true if success
	 */
	public boolean write(HEvent.Builder ev) {

		boolean success = true;
		nev++;
		String firec = Integer.toString(nev);
		try {
			data = ev.build().toByteArray();
			ZipEntry entry = new ZipEntry(firec);
			zout.putNextEntry(entry);
			entry.setSize(data.length);
			zout.write(data);
			zout.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
			return success; // no support
		}

		return success;

	};

	/**
	 * Get the number of events stored in the file.
	 * 
	 * @return number of stored objects
	 */
	public int size() {

		if (zipFile == null)
			return nev;
		return zipFile.size()-1; // exclude version

	};

	/**
	 * Get number of events stored in the file. Same as size()
	 * 
	 * @return number of stored objects
	 */
	public int getNEntries() {
		return size();

	};
	
	
	/**
	 * Get a string representing file content.
	 * 
	 * @return File content.
	 */
	public String entriesToString() {

		String tmp = "";
		if (iif == null)
			return tmp;

		try {
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				String a=ze.getName();
				if (!a.equals("info"))  tmp = tmp + a + "\n";
				zin.closeEntry();
			}
			zin.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tmp;

	}

	

	
	/**
	 * Read next event 
	 * @return next object.
	 */
	public HEvent read() {
     nev++;
     return read(nev);
	}
	
	
	
	
	
	
	/**
	 * Get object from a file using its index.
	 * 
	 * @param index
	 *            of the object
	 * @return Object extracted object (or null)
	 */
	public HEvent read(int index) {

		HEvent ob = null;
		if (zipFile == null) return ob;
		ZipEntry entry = zipFile.getEntry(Integer.toString(index));

		if (entry == null) return ob;

		InputStream zz = null;
		HEvent  record = null;

		try {
			zz = zipFile.getInputStream(entry);
			record = HEvent.parseFrom(zz);
		} catch (IOException e) {
			e.printStackTrace();
			return ob;
		}
		
		if (record == null) return ob;

		return record;
	};

	/**
	 * Close the file
	 * 
	 * @return
	 */
	public boolean close() {

		boolean success = true;
		try {

			if (iif != null) {
				iif.close();
				zin.close();
				zipFile.close();
				iif = null;
				zin=null;
				zipFile=null;
			}

			if (oof != null) {
				zout.finish();
				zout.close();
				oof.flush();
				oof.close();
				oof = null;
			}
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}
		return success;

	};

	

	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 **/

	private void ErrorMessage(String a) {
              jhplot.utils.Util.ErrorMessage(a);

	}

	 /**
     * Show online documentation.
     */
    public void doc() {

            String a = this.getClass().getName();
            a = a.replace(".", "/") + ".html";
            new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

    }

	
}
