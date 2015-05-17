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
import java.util.Enumeration;

import jhplot.io.db.RecordReader;
import jhplot.io.db.RecordWriter;
import jhplot.io.db.RecordsFile;
import jhplot.io.db.RecordsFileException;

/**
 * 
 * This raw-level database allows to store and retrieve objects. It associates a
 * key of type String with each record. The keys will be limited to a maximum
 * length, although the record data will not be limited. The record will consist
 * of only one "blob" of binary data. The number of records is not fixed at
 * creation time. The file can grow and shrink as records are inserted and
 * removed. Because our index and record data will be stored in the same file,
 * this restriction will cause us to add extra logic to dynamically increase the
 * index space by reorganizing records. The database operations not depend on
 * the number of records in the file. In other words, they'll be of constant
 * order time with respect to file accesses. The index is small enough to load
 * into memory. This will make it easier for our implementation to fulfill the
 * requirement that dictates access time.
 * 
 * The code is based on:
 * http://www.javaworld.com/javaworld/jw-01-1999/jw-01-step.html
 * 
 * 
 * @author S.Chekanov
 * 
 */
public class HDataBase {

	public RecordsFile recordsFile;
	public RecordWriter rw;
	public RecordReader rr;

	/**
	 * Create a new persistent database. If file exists, and the option "w" is
	 * set, the old file will be removed!
	 * 
	 * 
	 * @param file
	 *            File name
	 * @param option
	 *            Option to create the file . If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 */
	public HDataBase(String file, String option) {

		if (option.equalsIgnoreCase("w")) {

			try {
				(new File(file)).delete();
				recordsFile = new RecordsFile(file, 128);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RecordsFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (option.equalsIgnoreCase("r")) {

			try {
				recordsFile = new RecordsFile(file, "r");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RecordsFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (option.equalsIgnoreCase("rw")) {

			try {
				recordsFile = new RecordsFile(file, 128);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RecordsFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			ErrorMessage("Wrong option!. Only \"r\" or \"w\" or \"rw\" is allowed");
		}

	};

	/**
	 * Returns an enumeration of all the keys in the database.
	 */
	public synchronized Enumeration getKeys() {
		return recordsFile.enumerateKeys();
	}

	/**
	 * Returns the current number of records in the database.
	 */
	public synchronized int getRecords() {
		return recordsFile.getNumRecords();
	}

	/**
	 * Checks if there is a record belonging to the given key.
	 */
	public synchronized boolean isExists(String key) {
		return recordsFile.recordExists(key);
	}

	/**
	 * Open the database file for reading.
	 * 
	 * 
	 * @param file
	 *            File name
	 */
	public HDataBase(String file) {

		try {
			recordsFile = new RecordsFile(file, "r");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordsFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	};

	/**
	 * Insert an object with a key
	 * 
	 * @param key
	 *            key for object
	 * @param obj
	 *            Object for this key
	 * @return true if success
	 */
	public boolean insert(String key, Object obj) {

		boolean success = true;

		rw = new RecordWriter(key);
		try {
			rw.writeObject(obj);
			recordsFile.insertRecord(rw);
		} catch (IOException e) {
			success = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordsFileException e) {
			success = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return success;

	};

	/**
	 * Get object from the database using a key
	 * 
	 * @param key
	 *            Key to get the object
	 * @return Object
	 */
	public Object get(String key) {

		Object ob = null;
		try {
			rr = recordsFile.readRecord(key);
			ob = rr.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordsFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ob;

	};

	/**
	 * Update the database with a new value
	 * 
	 * @param ob
	 *            Object
	 * @param key
	 *            key
	 * @return true if success
	 */

	public boolean update(Object ob, String key) {

		boolean success = true;

		rw = new RecordWriter(key);
		try {
			rw.writeObject(ob);
			recordsFile.updateRecord(rw);
		} catch (IOException e) {
			success = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordsFileException e) {
			success = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	};

	/**
	 * Remove object from the database using the key
	 * 
	 * @param key
	 *            input key
	 * @return true if success
	 */

	public boolean remove(String key) {

		boolean success = true;

		try {
			recordsFile.deleteRecord(key);

		} catch (RecordsFileException e) {
			success = false;
			e.printStackTrace();
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}

		return success;

	};

	/**
	 * Close the file
	 * 
	 * @return
	 * */
	public boolean close() {

		boolean success = true;
		try {
			recordsFile.close();
		} catch (RecordsFileException e) {
			success = false;
			// e.printStackTrace();
		} catch (IOException e) {
			success = false;
			// e.printStackTrace();
		}

		return success;

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

}
