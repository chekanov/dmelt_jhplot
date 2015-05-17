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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jhplot.gui.HelpBrowser;
import hep.aida.IAnalysisFactory;
import hep.aida.IManagedObject;
import hep.aida.ITree;

/**
 * Open an AIDA file and retrieve objects for plotting.
 * Used to read AIDA files
 * 
 * @author S.Chekanov
 * 
 */
public class FileAida {

	private IAnalysisFactory af = null;
	private ITree itree = null;

	/**
	 * Create object to read AIDA file. Read only mode.
	 * 
	 * @param file
	 *            Input AIDA file
	 */
	public FileAida(String file) {
		load(file, true);
	}

	
	/**
	 * Create object to read AIDA file. Read only mode.
	 * 
	 * @param file
	 *            Input AIDA file
	 * @param  readOnly          
	 */
	public FileAida(String file, boolean readOnly) {
		load(file, readOnly);
	}
	
	/**
	 * Create FileAida
	 */
	public FileAida() {
	}

	/**
	 * Load a AIDA file
	 * 
	 * 
	 * @param f input file
	 * @param readOnly - only read 
	 */
	public void load(String file, boolean readOnly) {

		try {
			File f = new File(file);
			af = IAnalysisFactory.create();
			itree = af.createTreeFactory().create(f.getAbsolutePath(), "xml", readOnly);
		} catch (IOException e) {
			// e.printStackTrace();
			jhplot.utils.Util.ErrorMessage("Error in openning file");
			
		}

	}

	/**
	 * Load a AIDA file
	 * 
	 * 
	 * @param f input file
	 * @param readOnly - only read 
	 */
	public void load(File f, boolean readOnly) {

		try {
			af = IAnalysisFactory.create();
			itree = af.createTreeFactory().create(f.getAbsolutePath(), "xml", readOnly);
		} catch (IOException e) {
			// e.printStackTrace();
			jhplot.utils.Util.ErrorMessage("Error in openning file");
			
		}

	}

	
	
	
	/**
	 * Get object associated with the key name
	 * 
	 * @param skey
	 *            key name
	 * @return Object
	 */
	public Object get(String skey) {

		Object ob = itree.find(skey);

		return ob;
	}

	
	/**
	 * List name of the objects inside trees
	 * @return object types
	 */
	
	public String[]  listObjectNames() {

		return itree.listObjectNames();
		
	}
	
		
	
	/**
	 * Get list of all objects
	 * @param a path
	 * @return list of all objects
	 */
	
	public ArrayList<String>  getAllNames(String a) {

		ArrayList<String> ar = new ArrayList<String>();
		String[]  s = itree.listObjectNames();
		
		for (int i=0; i<itree.listObjectNames(a).length; i++) {
			 ar.add(s[i] );
			 
		}
			
			return ar;
		
	}
	
	/**
	 * Get list of all objects
	 * @return list of all objects
	 */
	
	public ArrayList<String>  getAllNames() {

		ArrayList<String> ar = new ArrayList<String>();
		String[]  s = itree.listObjectNames();
		
		for (int i=0; i<itree.listObjectNames().length; i++) {
			 ar.add(s[i] );
			 
		}
			
			return ar;
		
	}

	/**
	 * Get list of all types
	 * @return list of all types
	 */
	
	public ArrayList<String>  getAllTypes() {

		ArrayList<String> ar = new ArrayList<String>();
		String[]  s = itree.listObjectTypes();
		
		for (int i=0; i<itree.listObjectTypes().length; i++) {
			 ar.add(s[i] );
			 
		}
			
			return ar;
		
	}
	
	
	/**
	 * Get list of all types
	 * @param a path
	 * @return list of all types
	 */
	
	public ArrayList<String>  getAllTypes(String a) {

		ArrayList<String> ar = new ArrayList<String>();
		String[]  s = itree.listObjectTypes();
		
		for (int i=0; i<itree.listObjectTypes(a).length; i++) {
			 ar.add(s[i] );
			 
		}
			
			return ar;
		
	}
	
	
	
	/**
	 * Navigate to a directory inside the file
	 */
	public void  cd(String dir) {

		itree.cd(dir);
		
		
	}
	
	/**
	 * Print the current directory
	 * @param show current directory
	 */
	public String  pwd() {

		return itree.pwd();
		
		
	}
	/**
	 * Close the file
	 * @throws IOException 
	 */
	public void  close() throws IOException {

		itree.close();
		
		
	}
	
	
	/**
	 * Get list of all objects
	 * @return list of all objects
	 */
	
	public ArrayList<IManagedObject>  getAll() {

		ArrayList<IManagedObject> ar = new ArrayList<IManagedObject>();
		String[]  s = itree.listObjectNames();
		
		for (int i=0; i<itree.listObjectNames().length; i++) {
			 ar.add( itree.find(s[i] )  );
			 
		}
			
			return ar;
		
	}
	
	
	
	/**
	 * Return ITree for further manipulations
	 * 
	 * @return ITree
	 */
	public ITree getITree() {
		return itree;
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
