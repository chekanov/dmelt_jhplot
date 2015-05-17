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
package jhplot.io.db;

import java.io.*;
import java.util.*;



public class RecordsFile extends BaseRecordsFile {

  /**
   * Hashtable which holds the in-memory index. For efficiency, the entire index 
   * is cached in memory. The hashtable maps a key of type String to a RecordHeader.
   */
  protected Hashtable  memIndex;    

  /**
   * Creates a new database file.  The initialSize parameter determines the 
   * amount of space which is allocated for the index.  The index can grow 
   * dynamically, but the parameter is provide to increase 
   * efficiency. 
   */
  public RecordsFile(String dbPath, int initialSize) throws IOException, RecordsFileException {
    super(dbPath, initialSize);
    memIndex = new Hashtable(initialSize);
  }

  /**
   * Opens an existing database and initializes the in-memory index. 
   */
  public RecordsFile(String dbPath, String accessFlags) throws IOException, RecordsFileException {
    super(dbPath, accessFlags);
    int numRecords = readNumRecordsHeader();
    memIndex = new Hashtable(numRecords);
    for (int i = 0; i < numRecords; i++) {
      String key = readKeyFromIndex(i);
      RecordHeader header = readRecordHeaderFromIndex(i);
      header.setIndexPosition(i);
      memIndex.put(key, header);
    }
  }

  
  /**
   * Returns an enumeration of all the keys in the database.
   */
  public synchronized Enumeration enumerateKeys() {
    return memIndex.keys();
  }

  /**
   * Returns the current number of records in the database. 
   */
  public synchronized int getNumRecords() {
    return memIndex.size();
  }

  /**
   * Checks if there is a record belonging to the given key. 
   */
  public synchronized boolean recordExists(String key) {
    return memIndex.containsKey(key);
  }

  /**
   * Maps a key to a record header by looking it up in the in-memory index.
   */
  protected RecordHeader keyToRecordHeader(String key) throws RecordsFileException {
    RecordHeader h = (RecordHeader)memIndex.get(key);
    if (h==null) {
      throw new RecordsFileException("Key not found: " + key);
    } 
    return h;
  }

  /**
   * This method searches the file for free space and then returns a RecordHeader 
   * which uses the space. (O(n) memory accesses)
   */
  protected RecordHeader allocateRecord(String key, int dataLength) throws RecordsFileException, IOException {
    // search for empty space
    RecordHeader newRecord = null;
    Enumeration e = memIndex.elements();
    while (e.hasMoreElements()) {
      RecordHeader next = (RecordHeader)e.nextElement();
      int free = next.getFreeSpace();
      if (dataLength <= next.getFreeSpace()) {
	newRecord = next.split();
	writeRecordHeaderToIndex(next);
	break;
      }
    }
    if (newRecord == null) {
      // append record to end of file - grows file to allocate space
      long fp = getFileLength();
      setFileLength(fp + dataLength);
      newRecord = new RecordHeader(fp, dataLength);
    } 
    return newRecord;
  }

  /**
   * Returns the record to which the target file pointer belongs - meaning the specified location
   * in the file is part of the record data of the RecordHeader which is returned.  Returns null if 
   * the location is not part of a record. (O(n) mem accesses)
   */
  protected RecordHeader getRecordAt(long targetFp) throws RecordsFileException {
    Enumeration e = memIndex.elements();
    while (e.hasMoreElements()) {
      RecordHeader next = (RecordHeader) e.nextElement();
      if (targetFp >= next.dataPointer &&
	  targetFp < next.dataPointer + (long)next.dataCapacity) {
	return next;
      }
    }
    return null;
  }


  /**
   * Closes the database. 
   */
  public synchronized void close() throws IOException, RecordsFileException {
    try {
      super.close();
    } finally {
      memIndex.clear();
      memIndex = null;
    }
  }

  /**
   * Adds the new record to the in-memory index and calls the super class add
   * the index entry to the file. 
   */
  protected void addEntryToIndex(String key, RecordHeader newRecord, int currentNumRecords) throws IOException, RecordsFileException {
    super.addEntryToIndex(key, newRecord, currentNumRecords);
    memIndex.put(key, newRecord);   
  }
 
  /**
   * Removes the record from the index. Replaces the target with the entry at the 
   * end of the index. 
   */
  protected void deleteEntryFromIndex(String key, RecordHeader header, int currentNumRecords) throws IOException, RecordsFileException {
    super.deleteEntryFromIndex(key, header, currentNumRecords);
    RecordHeader deleted = (RecordHeader)memIndex.remove(key);
  }


}






