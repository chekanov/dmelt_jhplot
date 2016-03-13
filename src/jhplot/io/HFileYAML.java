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
import java.util.*;
import jhplot.*;
import jhplot.gui.HelpBrowser;
import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.*;

/**
 * 
 * Read and write data using YAML Ain't Markup Language using Version 1.2. 
 * YAML is a human-friendly, cross language, Unicode based data serialization language 
 * designed around the common native data types of agile programming languages. 
 * It is broadly useful for programming needs ranging from configuration files to 
 * Internet messaging to object persistence to data auditing.
 * <p>
 * The data are accumulated in a map and written when you call the method close().  
 * @author S.Chekanov
 * 
 */
public class HFileYAML {

	private FileInputStream iif;
	private ObjectInputStream iis;
	private int nev = 0;
	private Map<String, Object> hmap;
	final private int version = 2;
        private Yaml yaml;
        private String file;
        private String option;

	/**
	 * Open a YAML file to write or read objects to/from a serialized file in
	 * sequential order. If "w" option is set, the old file will be removed. Use
	 * close() to flash the buffer and close the file. 
	 * 
	 * <p>
	 * File extension is *.yaml
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
	 */
	public HFileYAML(String file, String option) {

		nev = 0;
                this.yaml = new Yaml();
                this.file=file;
                this.option=option;
		if (option.equalsIgnoreCase("r")) {
			try {
                         hmap= (Map)yaml.load(new FileInputStream(new File(file)));
			} catch (IOException e) {
				e.printStackTrace();
			}
                } else if (option.equalsIgnoreCase("w")) {
                       this.file=file;
                       this.hmap = new HashMap<String, Object>();
		} else {

			ErrorMessage("Wrong option!. Only \"r\" or \"w\"  is allowed");
		}

	};


         /**
         * Return Yaml object from the file.
         * @return Yaml object. 
         */
        public Yaml getYaml(){
               return this.yaml;

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

                 // fix serialization for for functions
                if (ob instanceof jhplot.F1D)         ob=((F1D)ob).get();
                else if (ob instanceof jhplot.F2D)    ob=((F2D)ob).get();
                else if (ob instanceof jhplot.F3D)    ob=((F3D)ob).get();
                else if (ob instanceof jhplot.FND)    ob=((FND)ob).get();
                else if (ob instanceof jhplot.P1D)    ob=new DProxy((P1D)ob);
                else if (ob instanceof jhplot.P2D)    ob=new DProxy((P2D)ob);
                else if (ob instanceof jhplot.P0D)    ob=new DProxy((P0D)ob);
                else if (ob instanceof jhplot.P0I)    ob=new DProxy((P0I)ob);
                else if (ob instanceof jhplot.H1D)    ob=new DProxy((H1D)ob);

		if (!hmap.containsKey(key)) {
                        nev++;
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
		return hmap;

	}


	/**
	 * Open a YAML file to read objects.
	 * 
	 * @param file
	 *            File name for reading
	 */
	public HFileYAML(String file) {
              this(file,"r"); 
	};



	/**
	 * Read an object using a key if exits.
	 * 
	 * @param key
	 * @return returned object.
	 */
	public Object read(String key) {

                Object ob = null;

                if (hmap.containsKey(key)) {
                     ob= hmap.get(key);
                     if (ob instanceof jhplot.FProxy) {
                         if ( ((FProxy)ob).getType()==1) ob=new F1D((FProxy)ob);
                         else if ( ((FProxy)ob).getType()==2) ob=new F2D((FProxy)ob);
                         else if ( ((FProxy)ob).getType()==3) ob=new F3D((FProxy)ob);
                         else if ( ((FProxy)ob).getType()==4) ob=new FND((FProxy)ob);
                         return ob; 
                      }

                     if (ob instanceof jhplot.DProxy) {
                         if (((DProxy)ob).getType().equals("p1d"))  ob= (P1D)((DProxy)ob).get(); 
                         else if (((DProxy)ob).getType().equals("p2d"))  ob= (P2D)((DProxy)ob).get();
                         else if (((DProxy)ob).getType().equals("p0d"))  ob= (P0D)((DProxy)ob).get();
                         else if (((DProxy)ob).getType().equals("p0i"))  ob= (P0I)((DProxy)ob).get();
                         else if (((DProxy)ob).getType().equals("h1d"))  ob= (H1D)((DProxy)ob).get();

                         return ob;
                      }



                 }

                return ob;
 
                           
	}

	/**
	 * Close the file and write the objects as needed. 
	 * 
	 * @return
	 */
	public boolean close() {

		boolean success = true;


                if (option.equalsIgnoreCase("w")) {
                        try {
                                (new File(file)).delete();
                                Writer writer = new FileWriter(file);
                                yaml.dump(hmap, writer);
                                writer.close();
                                return success;
                        } catch (IOException e) {
                                e.printStackTrace();
                                success = false;
                                return success;
                        }

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
