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

import jhplot.*; 
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jhplot.gui.HelpBrowser;

/**
 * 
 * Write or read objects in sequential order using the Java serialization. The
 * objects inside files are gzipped on-fly. The size of serialized files should
 * be significantly smaller than when using HFile class. Use "close()" to flush
 * buffered output stream. Use BrowserHFile to browser all entries in a GUI
 * frame. Normally, files should have the extension ".jser". Files can be viewed
 * using BrowserHFile.
 * <p>
 * 
 * You can also insert objects using the keys and read them back. In this case,
 * avoid writing many objects without the keys since the extraction of keys will
 * be very inefficient. Try not mix write/read with keys or without.
 * 
 * @author S.Chekanov
 * 
 */
public class HFile {

	private FileOutputStream oof;
	private ObjectOutputStream oos;
	private ObjectInputStream iis;
	private int reset = 100;
	private int nev = 0;
	private int buffer;
	private Map<String, Object> hmap;
	final private int version = 2;

	/**
	 * Open a file to write/read objects to/from a serialized file in sequential
	 * order. Objects can be gzipped/gunzipped on-fly. If "w" option is set, the
	 * old file will be removed. Use close() to flash the buffer and close the
	 * file. You can set buffer size for I/O . Make it larger for a heavy I/O.
	 * It is best to use buffer sizes that are multiples of 1024 bytes. That
	 * works best with most built-in buffering in hard disks.
	 * <p>
	 * You can also insert an object using a string key.
	 * 
	 * <p>
	 * If the file name starts from http or ftp, we assume it is located on the
	 * Web and will attempt to read it from URL.
	 * 
	 * @param file
	 *            File name. Can be located on URL if starts from http or ftp.
	 * @param option
	 *            Option to create the file. If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 * @param compression
	 *            if true, objects are compressed on-fly using GZIP
	 * @param bufferSize
	 *            set buffer size for I/O. It is best to use buffer sizes that
	 *            are multiples of 1024 bytes.
	 * 
	 */
	public HFile(String file, String option, boolean compression, int bufferSize) {

		buffer = bufferSize;
		nev = 0;
		file = file.trim();
		hmap = new HFileMap<String, Object>(version);

		if (option.equalsIgnoreCase("w")) {

			try {
				(new File(file)).delete();
				oof = new FileOutputStream(file);

				if (compression == true) {
					oos = new ObjectOutputStream(new BufferedOutputStream(
							new GZIPOutputStream(oof), buffer));
				} else {
					oos = new ObjectOutputStream(new BufferedOutputStream(oof,
							buffer));
				}
			} catch (IOException e) {
				jhplot.utils.Util.ErrorMessage(e.toString());
			}

		} else if (option.equalsIgnoreCase("r")) {

			// check. Is is URL?
			if (file.startsWith("http") || file.startsWith("ftp")) {
				URL url = null;
				try {
					url = new URL(file);
				} catch (MalformedURLException e) {
					jhplot.utils.Util.ErrorMessage(e.toString());
				}
				try {
					URLConnection urlConn = url.openConnection();
					urlConn.setDoInput(true);
					urlConn.setUseCaches(false);

					if (compression == true) {
						iis = new ObjectInputStream(new GZIPInputStream(
								urlConn.getInputStream()));
					} else {
						iis = new ObjectInputStream(urlConn.getInputStream());
					}
				} catch (MalformedURLException e) {
					jhplot.utils.Util.ErrorMessage(e.toString());
				} catch (IOException e) {
					jhplot.utils.Util.ErrorMessage(e.toString());
				}

			} else { // this is normal file on a file system

				try {
					FileInputStream iif = new FileInputStream(new File(file));

					if (compression == true) {
						iis = new ObjectInputStream(new BufferedInputStream(
								new GZIPInputStream(iif), buffer));
					} else {
						iis = new ObjectInputStream(new BufferedInputStream(
								iif, buffer));
					}
				} catch (IOException e) {
					jhplot.utils.Util.ErrorMessage(e.toString());
				}

			}

		} else {

			jhplot.utils.Util
					.ErrorMessage("Wrong option!. Only \"r\" or \"w\"  is allowed");
		}

	};

	/**
	 * Open a file to write/read objects to/from a serialized file in sequential
	 * order. Objects can be gzipped/gunzipped on-fly. If "w" option is set, the
	 * old file will be removed. Use close() to flash the buffer and close the
	 * file. The default buffer size is 12 k (i.e. 12 * 1024).
	 * 
	 * @param file
	 *            File name
	 * @param option
	 *            Option to create the file. If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 * @param compression
	 *            if true, objects are compressed on-fly using GZIP
	 * 
	 * @param buffer
	 *            set buffer size for I/O. It is best to use buffer sizes that
	 *            are multiples of 1024 bytes.
	 * 
	 */
	public HFile(String file, String option, boolean compression) {

		this(file, option, compression, 12 * 1024);
	}

	/**
	 * Get current buffer size for I/O.
	 */
	public int getBufferSize() {

		return this.buffer;
	}

	/**
	 * Open file for reading objects from a serialized file in sequential order.
	 * By default, entries are compressed.
	 * 
	 * @param file
	 *            File name
	 */
	public HFile(String file) {

		this(file, "r", true);

	};

	/**
	 * Open file for reading objects from a serialized file in sequential order.
	 * By default, objects are compressed.
	 * 
	 * @param file
	 *            File name
	 * @param option
	 *            Option to create the file. If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 */
	public HFile(String file, String option) {

		this(file, option, true);

	};

	/**
	 * Add an object using a key.
	 * 
	 * @param key
	 *            key to access the object.
	 * @param ob
	 *            object to be inserted.
	 * @return false if the key exits.
	 */
	public boolean write(String key, Object ob) {

		if (!hmap.containsKey(key)) {
			hmap.put(key, ob);
			return true;
		} else
			return false;

	}

	/**
	 * Add an object to a file
	 * 
	 * @param ob
	 *            Object
	 * @param key
	 *            key for object
	 * @return true if success
	 */
	public boolean write(Object ob) {

		boolean success = true;

                // fix serialization for for functions
                if (ob instanceof jhplot.F1D)         ob=((F1D)ob).get(); 
                else if (ob instanceof jhplot.F2D)    ob=((F2D)ob).get(); 
                else if (ob instanceof jhplot.F3D)    ob=((F3D)ob).get();
                else if (ob instanceof jhplot.FND)    ob=((FND)ob).get();


		try {
			oos.writeObject(ob);
			nev++;
			if (nev > 1 && nev % reset == 0)
				oos.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			success = false;
			e.printStackTrace();
		}
		return success;

	};

	/**
	 * Set after how many events data will be flushed. The stream will flush all
	 * the objects from the identity hash table. The default is 100.
	 * 
	 * @param reset
	 *            after how many events data will be reset.
	 * */
	public void setFlush(int reset) {

		this.reset = reset;

	}

	/**
	 * Return objects stored in the form of a map.
	 * 
	 * @return
	 */
	public Map<String, Object> getObjectMap() {

		// try to get this map
		if (hmap.size() == 0 && iis != null) {
			try {
				Object obj = null;
				while ((obj = iis.readObject()) != null) {
					// System.out.println("Read="+obj.getClass().getName() );
					if (obj instanceof jhplot.io.HFileMap) {
						// Cast object to a Vector
						// System.out.println("Read="+obj.getClass().getName()
						// );
						hmap = (HFileMap<String, Object>) obj;
						return hmap;
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				jhplot.utils.Util.ErrorMessage(e.toString());
				return null;
			}
		}

		return hmap;

	}

	/**
	 * Return processed number of entries. Entries with the key not counted.
	 * 
	 * @return No of processed events
	 * */
	public int getEntries() {

		return nev;
	}

	/**
	 * Get object from a file. Return Null if the end if file is reached.
	 * 
	 * @return Object extracted object (or Null)
	 */
	public Object read() {

		Object ob = null;
		try {
			ob = iis.readObject();

                         // restore functions
                        if (ob instanceof jhplot.FProxy) {
                         if ( ((FProxy)ob).getType()==1) ob=new F1D((FProxy)ob); 
                         else if ( ((FProxy)ob).getType()==2) ob=new F2D((FProxy)ob); 
                         else if ( ((FProxy)ob).getType()==3) ob=new F3D((FProxy)ob); 
                         else if ( ((FProxy)ob).getType()==4) ob=new FND((FProxy)ob); 
                         }


			if (ob instanceof jhplot.io.HFileMap) {
				hmap = (HFileMap<String, Object>) ob;
				return null;
			}

			nev++;
			// if (nev>1 && nev%reset==0) iis.reset();
		} catch (ClassNotFoundException |  IOException  ex) { // This exception will be caught when EOF is
			// reached
			return null;
		} 
		return ob;

	};

	/**
	 * Read an object using a key if exits.
	 * 
	 * @param key
	 * @return returned object.
	 */
	public Object read(String key) {

		Object obj = null;

		if (hmap.size() == 0 && iis != null) {
			try {
				while ((obj = iis.readObject()) != null) {
					if (obj instanceof jhplot.io.HFileMap) {
						hmap = (HFileMap<String, Object>) obj;
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}

		if (hmap.containsKey(key))
			return hmap.get(key);

		return null;

	}

	/**
	 * Close the file
	 * 
	 * @return
	 */
	public boolean close() {

		boolean success = true;
		try {

			if (oos != null) {
				oos.writeObject(hmap); // add the map as the last object
				oos.flush();
				oos.close();
				oof.close();
				oos = null;
			}

			if (iis != null) {
				iis.close();
				iis = null;
			}
		} catch (IOException e) {
			success = false;
			// ErrorMessage(e.toString());

		}

		System.gc();
		return success;

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
