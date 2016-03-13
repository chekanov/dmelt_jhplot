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
import java.util.Map;
import jhplot.*;
import jhplot.gui.HelpBrowser;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * Open a XML file to write/read objects in sequential order using java
 * serialization in XML. Do not forget apply close() at the end of run.
 * 
 * 
 * @author S.Chekanov
 * 
 */
public class HFileXML {

	public File recordsFile;
	public FileOutputStream oof;
	public ObjectOutputStream oos;
	private FileInputStream iif;
	private ObjectInputStream iis;
	private XStream xstream;
	private int reset = 100;
	private int nev = 0;
	private int buffer;
	private Map<String, Object> hmap;
	final private int version = 2;

	/**
	 * Open a XML file to write or read objects to/from a serialized file in
	 * sequential order. If "w" option is set, the old file will be removed. Use
	 * close() to flash the buffer and close the file. You can set buffer size
	 * for I/O . Make it larger for a heavy I/O. It is best to use buffer sizes
	 * that are multiples of 1024 bytes. That works best with most built-in
	 * buffering in hard disks.
	 * 
	 * <p>
	 * File extension is *.jxml
	 * 
	 * <p>
	 * 
	 * You can also insert objects using the keys and read them back. In this
	 * case, avoid writing many objects without the keys since the extraction of
	 * keys will be very inefficient. Try not mix write/read with keys or
	 * without.
	 * 
	 * @param file
	 *            File name
	 * @param option
	 *            Option to create the file . If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 * @param bufferSize
	 *            set buffer size for I/O. It is best to use buffer sizes that
	 *            are multiples of 1024 bytes.
	 */
	public HFileXML(String file, String option, int bufferSize) {

		buffer = bufferSize;
		nev = 0;
		hmap = new HFileMap<String, Object>(version);

		if (option.equalsIgnoreCase("w")) {

			try {
				(new File(file)).delete();
				xstream = new XStream(new DomDriver());
				oof = new FileOutputStream(file);
				oos = xstream
						.createObjectOutputStream(new BufferedOutputStream(oof,
								buffer));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (option.equalsIgnoreCase("r")) {

			try {
				xstream = new XStream(new DomDriver());
				iif = new FileInputStream(file);
				iis = xstream.createObjectInputStream(new BufferedInputStream(
						iif, buffer));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			ErrorMessage("Wrong option!. Only \"r\" or \"w\"  is allowed");
		}

	};

	/**
	 * Open a XML file to write or read objects to/from a serialized file in
	 * sequential order. If "w" option is set, the old file will be removed. The
	 * default buffer size is set to 12k.
	 * 
	 * @param file
	 *            File name
	 * @param option
	 *            Option to create the file . If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 */
	public HFileXML(String file, String option) {

		this(file, option, 12 * 1024);

	}

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
						hmap = (HFileMap<String, Object>) obj;
						return hmap;
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}

		return hmap;

	}

	/**
	 * Get current buffer size for I/O.
	 */
	public int getBufferSize() {

		return this.buffer;
	}

	/**
	 * Open a XML file to read objects from a file.
	 * 
	 * @param file
	 *            File name for reading
	 */
	public HFileXML(String file) {
                this(file,"r");
	};

	/**
	 * Add an object to a XML file
	 * 
	 * @param ob
	 *            Object
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
			// Woo! no need for XML
			// if (nev>1 && nev%reset==0) oos.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			success = false;
			e.printStackTrace();
		}

		return success;

	};

	/**
	 * Get object from a file. Returns Null if the end of file reached.
	 * 
	 * @return Object object or Null.
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
                hmap = (HFileMap<String,Object>)ob;
                return null;
                }
			
			nev++;
		} catch (IOException | ClassNotFoundException  ex) { // This exception will be caught when EOF is
			// reached
			return null;
			// System.out.println("End of file reached.");
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
				return null;
			}
		}

                if (hmap.containsKey(key)) { 
                         Object ob= hmap.get(key);
                         if (ob instanceof jhplot.FProxy) {
                         if ( ((FProxy)ob).getType()==1) ob=new F1D((FProxy)ob);
                         else if ( ((FProxy)ob).getType()==2) ob=new F2D((FProxy)ob);
                         else if ( ((FProxy)ob).getType()==3) ob=new F3D((FProxy)ob);
                         else if ( ((FProxy)ob).getType()==4) ob=new FND((FProxy)ob);
                         return ob;
                         }
                }

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
				oof.flush();
				oos.close();
				oof.close();
				oos = null;
			}

			if (iis != null) {
				iis.close();
				iif.close();
				iis = null;
			}

		} catch (IOException e) {
			success = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;

	};

	/**
	 * Get number of processed entries
	 * 
	 * @return No of entries
	 * */
	public int getEntries() {
		return nev;
	}

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
