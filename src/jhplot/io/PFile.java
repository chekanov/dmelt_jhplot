package jhplot.io;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.*;
import promc.io.PBufFile;
import promc.io.PBufFile.*;
import jhplot.*;
import jhplot.gui.HelpBrowser;

/**
 * 
 * Write or read objects in sequential order using Google's Prototype Buffer
 * scheme. Each record inside files is compressed on-fly. The file size are
 * smaller than when using HFile class. All graphical attributes are
 * lost (use HFile class  for this).   You can unzip the file to see its structure.
 * Normally, files should extension "jpbu". Files can be viewed using BrowserPFile.
 * Unlike the usual XML, the file size is small. At this moment,
 * the following objects are supported: a string,P0I, P0D, P1D, PND, PNI, F1D,F2D, FND,PRN,
 * H1D, H2D.
 * A protocol Buffers file is provided which can be used for C++ input.
 * This I/O is mainly for "named" objects (which implement the method setTitle() or setName().
 * Use CBook package to create such files in C++.
 *  <p>
 * 
 * Use this approach for storing P0D, P0I, P1D, H1D, H2D, F1D, F2D objects.
 * 
 * @author S.Chekanov
 * 
 */
public class PFile {

	private FileOutputStream oof = null;
	private FileInputStream iif = null;
	private int nev = 0;
	static final private int FILE_VERSION = 1;
	private ZipOutputStream zout;
	static final int BUFFER = 2048;
	private byte data[];
	private ZipInputStream zin;
	private ZipFile zipFile;
	private  Map<String,Integer> map=null;
	private  ArrayList<FileEntry> entries=null;
	

	/**
	 * Open a file to write/read objects to/from a file in sequential
	 * order. If "w" option is set, the old file will be removed. Use close() to
	 * flash the buffer and close the file. You can set buffer size for I/O . 
	 * Make it larger for a heavy I/O. It is best to use buffer sizes that are
	 * multiples of 1024 bytes. That works best with most built-in buffering in
	 * hard disks
	 * 
	 * @param file
	 *            File name
	 * @param option
	 *            Option to create the file. If "w" - write a file (or read)
	 *            file, if "r" only read created file.
	 * @param  mapnames
	 *            set to true (slower) to make association between object name and its position in the record.
	 *            In this case, one can read objects as read(name). If file is large and you run over records
	 *            sequentially using ID, call with "false" for fast load.         
	 * 
	 */
	public PFile(String file, String option, boolean mapNames) {

		nev = 0;
        map=null;
        entries=null;
        
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
                if (mapNames) mapNames();
                
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

			ErrorMessage("Wrong option!. Only \"r\" or \"w\"  is allowed");
		}

	};

	
	/**
	 * Generate an association between record number and object name.
	 * Call this method only if the constructor was called without map option. 
	 * 
	 * @return false if something is wrong
	 * 
	 */
	public boolean mapNames() {
		
		if (zin == null || zipFile == null) return false;
		
		//it exits!
		if (map != null || entries != null) return false;
		
		
		
		map = new HashMap<String,Integer>();    // hash table
                entries=new ArrayList<FileEntry>();
       
        
		try {
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				String a=ze.getName();
				if (!a.equals("info")) {
					
				Record record=null;
				
				try {
					InputStream zz = zipFile.getInputStream(ze);
					record = PBufFile.Record.parseFrom(zz);
					String title="";
					if (record == null ) return false;
					if (record.hasName()) {
						title = record.getName();
					} else if (record.hasF1D()) {
				        title = record.getF1D().getName();	
                     } else if (record.hasF2D()) {
                        title = record.getF2D().getName();
                     } else if (record.hasFND()) {
                         title = record.getFND().getName();
                     } else if (record.hasFPR()) {
                        title = record.getFPR().getName();    
					} else if (record.hasP0I()) {
				        title = record.getP0I().getName();
					} else if (record.hasP0D()) {
						title = record.getP0D().getName();
					}else if (record.hasPXY()) { // P1D in 2 dimensions					
						title = record.getPXY().getName();
					}else if (record.hasPXYZ()) { // P1D in 2 dimensions
						title = record.getPXYZ().getName();
					}else if (record.hasP1D()) { // P1D in 2 dimensions
						title = record.getP1D().getName();
					}else if (record.hasH1D()) { // P1D in 2 dimensions
						title = record.getH1D().getName();
					}else if (record.hasPND()) { // P1D in 2 dimensions
						title = record.getPND().getName();
					}else if (record.hasPNI()) { // P1D in 2 dimensions
						title = record.getPNI().getName();
					}else if (record.hasH2D()) { // P1D in 2 dimensions
						title = record.getH2D().getName();
					} else {
						title="";
					}
					
					  map.put(title, new Integer(a.toString()));
			          entries.add(new FileEntry(title,new Integer(a.toString()),ze.getCompressedSize(),record.getSerializedSize()));      
			          zin.closeEntry();
			          
				} catch (IOException e) {				
					e.printStackTrace();
					return false;
				}	
				
		 	}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	
		return true;
	}

	
	

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
	 * This constructor maps names automatically.
	 * 
	 * @param file
	 *            File name
	 */
	public PFile(String file) {

		this(file, "r", true);

	};

	/**
	 * Open file for reading objects from a serialized file in sequential order.
	 * This constructor maps names automatically in the read mode.
	 * 
	 * @param option
	 *             set "r" to read and "w" to write.
	 * 
	 * @param file
	 *            File name
	 */
	public PFile(String file, String option) {

		this(file,option, true);

	};

	
	
	
	/**
	 * Add an object to a file. Can be P1D, P0D,H1D,PND, etc. i.e. any jHplot array
	 * 
	 * @param ob
	 *            Object
	 *            
	 * @return true if success
	 */
	public boolean write(Object ob) {

		boolean success = true;
		Record.Builder record = Record.newBuilder();

		if (ob instanceof java.lang.String) {
			record.setName((String) ob);
			success=true;
		} else if (ob instanceof jhplot.F1D) {
			    F1D f1d = (jhplot.F1D)ob;
			    Record.F1D.Builder p = Record.F1D.newBuilder().setName(f1d.getTitle());
			    p.setDefinition(f1d.getName() );
			    p.setMax(f1d.getMax()); p.setMin(f1d.getMin());
		            success=true;	
                  } else if (ob instanceof jhplot.FND) {
                            FND fnd = (jhplot.FND)ob;
                            Record.FND.Builder p = Record.FND.newBuilder().setName(fnd.getTitle());
                            p.setDefinition(fnd.getName()); 
                            p.setVars(fnd.getVarString()); 
                            success=true;
                 } else if (ob instanceof jhplot.F2D) {
                            F2D f2d = (jhplot.F2D)ob;
                            Record.F2D.Builder p = Record.F2D.newBuilder().setName(f2d.getTitle());
                            p.setDefinition(f2d.getName() );
                            p.setMaxX(f2d.getMaxX()); p.setMinX(f2d.getMinX());
                            p.setMaxY(f2d.getMaxY()); p.setMinY(f2d.getMinY());
                            success=true;
                            
       } else if (ob instanceof jhplot.FPR) {
     			    FPR fpr = (jhplot.FPR)ob;
     			    Record.FPR.Builder p = Record.FPR.newBuilder().setName(fpr.getTitle());
     			    p.setDefinition(fpr.getName() );
     			    p.setDivU(fpr.getDivU()); p.setDivV(fpr.getDivV());
     		        success=true;	       
                            
		} else if (ob instanceof jhplot.P0I) {
			Record.P0I.Builder p0i = Record.P0I.newBuilder().setName(
					((jhplot.P0I) ob).getTitle());
			for (int i = 0; i < ((jhplot.P0I) ob).size(); i++)
				p0i.addValue(((jhplot.P0I) ob).get(i));
			record.setP0I(p0i);
			success=true;
		} else if (ob instanceof jhplot.P0D) {
			Record.P0D.Builder p0d = Record.P0D.newBuilder().setName(
					((jhplot.P0D) ob).getTitle());
			for (int i = 0; i < ((jhplot.P0D) ob).size(); i++)
				p0d.addValue(((jhplot.P0D) ob).get(i));
			record.setP0D(p0d);
			success=true;
		}else if (ob instanceof jhplot.P2D) {
			P2D p2d = (jhplot.P2D) ob;
			Record.PXYZ.Builder p = Record.PXYZ.newBuilder().setName(p2d.getTitle());	
			for (int i = 0; i < ((jhplot.P2D) ob).size(); i++) {
			 	p.addX(p2d.getX(i));  p.addY(p2d.getY(i));  p.addZ(p2d.getZ(i));}
			    record.setPXYZ(p);
			    success=true;
		}else if (ob instanceof jhplot.PND) {
			PND pnd = (jhplot.PND) ob;
			Record.PND.Builder p = Record.PND.newBuilder().setName(pnd.getTitle());
			int n=pnd.getDimension();	
			p.setDimension(n);
			for (int i=0; i<pnd.size(); i++)
				for (int j=0; j<n; j++) p.addValue(pnd.get(i,j));			
			record.setPND(p);
		    success=true;
		}else if (ob instanceof jhplot.PNI) {
			PNI pnd = (jhplot.PNI) ob;
			Record.PNI.Builder p = Record.PNI.newBuilder().setName(pnd.getTitle());
			int n=pnd.getDimension();
			p.setDimension(n);
			for (int i=0; i<pnd.size(); i++)
				for (int j=0; j<n; j++) p.addValue(pnd.get(i,j));	
			record.setPNI(p);
		    success=true;
		}else if (ob instanceof jhplot.P1D) {
			P1D p1d = (jhplot.P1D) ob;
			if (p1d.dimension() ==2) {
				Record.PXY.Builder p = Record.PXY.newBuilder().setName(p1d.getTitle());	
			for (int i = 0; i < ((jhplot.P1D) ob).size(); i++) {
			 	p.addX(p1d.getX(i));  p.addY(p1d.getY(i)); }
			    record.setPXY(p);
			    success=true;
			} else {
				Record.P1D.Builder p = Record.P1D.newBuilder().setName(p1d.getTitle());
				for (int i = 0; i < ((jhplot.P1D) ob).size(); i++) {
				 	p.addX(p1d.getX(i)); p.addY(p1d.getY(i)); 
				 	p.addXleft(p1d.getXleft(i)); 
				 	p.addXright(p1d.getXright(i)); 
				 	p.addYupper(p1d.getYupper(i)); 
				 	p.addYlower(p1d.getYlower(i)); 
				 	
				 	p.addXsysleft(p1d.getXleftSys(i)); 
				 	p.addXsysright(p1d.getXrightSys(i)); 
				 	p.addYsysupper(p1d.getYupperSys(i)); 
				 	p.addYsyslower(p1d.getYlowerSys(i)); 
				 	record.setP1D(p);	
				}
			} 
			success=true;
		}else if (ob instanceof jhplot.H1D) {	
			H1D h = (jhplot.H1D) ob;
			Record.H1D.Builder p = Record.H1D.newBuilder().setName(h.getTitle());
			p.setIsFixedBins( h.isFixedBinning());
			p.setMean( h.mean());
			p.setRms( h.rms());
			p.setNentries(h.entries());
			p.setMin( h.getMin());
			p.setMax( h.getMax());
			p.setBins(h.getBins());
			p.setOverflow(h.getOverflowlowHeight());
			p.setUnderflow(h.getUnderflowHeight());	
			 int ibins = h.getBins() + 2;
			
			for (int i=0; i < ibins-1; i++) {
				                        p.addHeights(h.binHeight(i));
				                        p.addErrors(h.binError(i));
				                        p.addEntries(h.binEntries(i));
				                        p.addMeans(h.binMean(i));
				                        p.addRmses(h.binRms(i));
				                        if (i<ibins-2) p.addEdges(h.binLowerEdge(i));
			}
			p.addEdges(h.binUpperEdge(h.getBins()-1));
			record.setH1D(p);
			success=true;
		}else if (ob instanceof jhplot.H2D) {	
			H2D h2 = (jhplot.H2D) ob;
			Record.H2D.Builder p = Record.H2D.newBuilder().setName(h2.getTitle());
			p.setNentries(h2.entries());
			
			// binsX,minX,maxX,  binsY,minY,maxY,  meanX,rmsX,meanY,rmsY
			p.addSummary( h2.getBinsX());
			p.addSummary( h2.getMinX());
			p.addSummary( h2.getMaxX());
			
			p.addSummary( h2.getBinsY());
			p.addSummary( h2.getMinY());
			p.addSummary( h2.getMaxY());
			
			p.addSummary( h2.getMeanX());
			p.addSummary( h2.getRmsX());
		
			p.addSummary( h2.getMeanY());
			p.addSummary( h2.getRmsY());
		
			// out of range
            // 6 | 7 | 8
            // -----------
            // 3 | 4 | 5
            // -----------
            // 0 | 1 | 2

			p.addOutofrange(  h2.getUnderflowHeightY()+h2.getUnderflowHeightX() );
			p.addOutofrange(  h2.getUnderflowHeightY());
			p.addOutofrange(  h2.getUnderflowHeightY() + h2.getOverflowHeightX() );
			
			p.addOutofrange(  h2.getUnderflowHeightX()  );
			p.addOutofrange(  h2.getOverflowHeightX()  );
			p.addOutofrange(  h2.sumAllBinHeights() );
			p.addOutofrange(  h2.getOverflowHeightY()+h2.getUnderflowHeightX()  );
			p.addOutofrange(  h2.getOverflowHeightY()  );
			p.addOutofrange(  h2.getOverflowHeightY() + h2.getOverflowHeightX() );
		
			int ibinsX =  h2.getBinsX() + 2;
            int ibinsY = h2.getBinsY() + 2;

             int kk=0;
			 for (int j1 = 0; j1 <  ibinsX - 1; j1++) {
                 for (int j2 = 0; j2 < ibinsY - 1; j2++) {
                         p.addHeights(h2.get().binHeight(j1, j2));
                         p.addErrors(h2.get().binError(j1, j2));
                         p.addEntries(h2.get().binEntries(j1, j2));
                         p.addMeansX(h2.get().binMeanX(j1, j2));
                         p.addRmsesX(h2.get().binRmsX(j1, j2));
                         p.addMeansY(h2.get().binMeanY(j1, j2));
                         p.addRmsesY(h2.get().binRmsY(j1, j2));
                        // System.out.println(kk);
                        // System.out.println(Integer.toString(j1)+" "+Integer.toString(j2)+ "  value="+
                        //		  Integer.toString(h2.get().binEntries(j1, j2)));
                      
                         kk++;
                 }
             }

			p.setIsFixedBins(true);
			record.setH2D(p);
			success=true;
		
		} else {
			success = false;
			return success; // no support
		}
				

		nev++;
		String firec = Integer.toString(nev);
		try {
			data = record.build().toByteArray();
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
	 * Get number of objects stored in the file.
	 * 
	 * @return number of stored objects
	 */
	public int size() {

		if (zipFile == null)
			return nev;
		return zipFile.size()-1; // exclude version

	};

	/**
	 * Get number of objects stored in the file. Same as size()
	 * 
	 * @return number of stored objects
	 */
	public int getNEntries() {
		return size();

	};
	
	
	/**
	 * Return file entries (ID,name,size).
	 * It does not return the actual object.
	 * 
	 * @return file entry
	 */
	public ArrayList<FileEntry> getEntries() {
		return entries;

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
	 * Get object from a file using its name. You should
	 * run mapNames() first to create association between names and entries.
	 * 
	 * @param name
	 *            Name of the object
	 * @return Object extracted object (or null)
	 */

	public Object read(String name) {

		if (zipFile == null)
			return null;
		
		if (map == null) return null;

		int index=map.get(name);
		return read(index);
		
	
	}
	
	
	/**
	 * Read next object
	 * @return next object.
	 */
	public Object read() {
     nev++;
     return read(nev);
	}
	
	
	
	
	
	/**
	 * List objects stored in the file. It list the  entry ID, name, 
	 * entry size and compression size (if available).
	 * 
	 * @return string with the information about the file
	 */
	public String  listEntries() {
     
	 if (entries == null) mapNames();
     
     Collections.sort(entries);
     String tmp="\nID  |  Title  |   Serialized size (bytes)  |   Compressed size (bytes)\n";
     
     for(int i = 0; i < entries.size() ; i++) {
    	 FileEntry a = entries.get(i);
    	 String s1=Long.toString(a.getSerializedSize());
    	 String s2=Long.toString(a.getCompressedSize());
    	 String s3=Long.toString(a.getID());
    	 tmp=tmp+s3 +"  ->  " + a.getName()+" -->  "+s1 + " --> "+s2+"\n";
        }
     return tmp;
     
     
	}
	
	
	
	
	/**
	 * Get object from a file using its index.
	 * 
	 * @param index
	 *            of the object
	 * @return Object extracted object (or null)
	 */

	public Object read(int index) {

		Object ob = null;
		if (zipFile == null) return ob;
		
		ZipEntry entry = zipFile.getEntry(Integer.toString(index));

		if (entry == null) return ob;

		InputStream zz = null;
		Record record = null;

		try {
			zz = zipFile.getInputStream(entry);
			record = PBufFile.Record.parseFrom(zz);
		} catch (IOException e) {
			e.printStackTrace();
			return ob;
		}
		
		if (record == null) return ob;

		
		
		if (record.hasName()) {
			return record.getName();
		} else if (record.hasF1D()) {
			Record.F1D f1d = record.getF1D();
			F1D p = new F1D(f1d.getName(), f1d.getDefinition(), f1d.getMin(), f1d.getMax() );
			return p;	
                 } else if (record.hasF2D()) {
                        Record.F2D f2d = record.getF2D();
                        F2D p = new F2D(f2d.getName(), f2d.getDefinition(),f2d.getMinX(), 
                                        f2d.getMaxX(), f2d.getMinY(), f2d.getMaxY() );
                        return p;
                } else if (record.hasFND()) {
                        Record.FND fnd = record.getFND();
                        FND p = new FND(fnd.getName(), fnd.getDefinition(), fnd.getVars() );
                        return p;
		} else if (record.hasFPR()) {
			Record.FPR f1d = record.getFPR();
			F1D p = new F1D(f1d.getName(), f1d.getDefinition(), f1d.getDivU(), f1d.getDivV() );
			return p;	                     
		} else if (record.hasP0I()) {
			Record.P0I p0i = record.getP0I();
			P0I p = new P0I(p0i.getName());
			for (int i = 0; i < p0i.getValueCount(); i++)
				p.add((int) p0i.getValue(i));
			return p;
		} else if (record.hasP0D()) {
			Record.P0D p0d = record.getP0D();
			P0D p = new P0D(p0d.getName());
			for (int i = 0; i < p0d.getValueCount(); i++)
				p.add((double) p0d.getValue(i));
			return p;
		} else if (record.hasPND()) {
			Record.PND pnd = record.getPND();
			PND p = new PND(pnd.getName());
			P0D pp=new P0D("row");
			int nn=0;
			for (int i=0; i<pnd.getValueCount(); i++) { 
				pp.add( pnd.getValue(i));
				nn++;
				if (nn ==pnd.getDimension())  {
					     p.add(pp);
					     nn=0;
					     pp.clear();					     
				}			
			}
			return p;
		} else if (record.hasPNI()) {
			Record.PNI pnd = record.getPNI();
			PNI p = new PNI(pnd.getName());
			P0I pp=new P0I("row");
			int nn=0;
			for (int i=0; i<pnd.getValueCount(); i++) { 
				pp.add( pnd.getValue(i));
				nn++;
				if (nn ==pnd.getDimension())  {
					     p.add(pp);
					     nn=0;
					     pp.clear();					     
				}			
			}
			return p;						
		}else if (record.hasPXY()) { // P1D in 2 dimensions
			Record.PXY p1d = record.getPXY();
			P1D p = new P1D(p1d.getName());
			for (int i = 0; i < p1d.getXCount(); i++) {
				p.add((double) p1d.getX(i), (double) p1d.getY(i));
			}
			return p;
		}else if (record.hasPXYZ()) { // P1D in 2 dimensions
			Record.PXYZ p1d = record.getPXYZ();
			P1D p = new P1D(p1d.getName());
			for (int i = 0; i < p1d.getXCount(); i++) {
				p.add((double) p1d.getX(i), (double) p1d.getY(i), (double) p1d.getZ(i));
			};
			return p;
			
		}else if (record.hasP1D()) { // P1D in 2 dimensions
			Record.P1D p1d = record.getP1D();
			P1D p = new P1D(p1d.getName());
			for (int i = 0; i < p1d.getXCount(); i++) {
				p.add((double) p1d.getX(i), (double) p1d.getY(i), 
					  (double) p1d.getXleft(i),(double) p1d.getXright(i),
					  (double) p1d.getYupper(i),(double) p1d.getYlower(i),
					  (double) p1d.getXsysleft(i),(double) p1d.getXsysright(i),
					  (double) p1d.getYsysupper(i),(double) p1d.getYsyslower(i));
		  }
			return p;
		} else if (record.hasH1D()) { // 1D dimensions
			
			Record.H1D h = record.getH1D();	
			int ibins= h.getBins()+2;
			double [] edges = new double[ibins-1];
			double [] heights= new double[ibins];
			double [] errors = new double[ibins];
			double [] means= new double[ibins];
			double [] rms = new double[ibins];
			int [] entries = new int[ibins];
			H1D h1d=null;	
			if ( h.getIsFixedBins()) {	
			    h1d = new H1D(h.getName(),h.getBins(),h.getMin(),h.getMax());
			} else {
				for (int i=0; i<h.getBins()+1; i++) edges[i]=h.getEdges(i);
				// for (int i=0; i<h.getBins(); i++) System.out.println(h.getEdges(i));
				h1d = new H1D(h.getName(),edges);
			}
			
			heights[0] = h.getUnderflow();
			heights[ibins-1] = h.getOverflow();
			
		    h1d.setBins( h.getBins());
		    h1d.setMin( h.getMin());
		    h1d.setMax( h.getMax());
		    h1d.setMeanAndRms(h.getMean(),h.getRms());
		    
		    for (int i = 0; i < ibins-1; i++) {
		    	heights[i+1] =  h.getHeights(i);
		    	errors[i+1] =  h.getErrors(i);
		    	entries[i+1] =  h.getEntries(i);
		    	means[i+1] =  h.getMeans(i);
		    	rms[i+1] =  h.getRmses(i);	
		    }
		    h1d.setContents(heights,errors,entries,means,rms);
			return h1d;
			
		} else if (record.hasH2D()) { // 2D dimensions
			
			Record.H2D h = record.getH2D();
			int binsx = (int)h.getSummary(0);
			int minx = (int)h.getSummary(1);
			int maxx = (int)h.getSummary(2);
			int binsy = (int)h.getSummary(3);
			int miny = (int)h.getSummary(4);
			int maxy = (int)h.getSummary(5);		
			H2D h2 = new H2D( h.getName(),binsx, minx, maxx, binsy, miny, maxy);
			h2.setNEntries(h.getNentries());
			h2.setMeanX(h.getSummary(6));
			h2.setRmsX(h.getSummary(7));
			h2.setMeanY(h.getSummary(8));
			h2.setRmsY(h.getSummary(9));
			// Y // out of range
            // 6 | 7 | 8
            // -----------
            // 3 | 4 | 5
            // -----------
            // 0 | 1 | 2
            // X
			
			  int ibinsX = binsx + 2;
              int ibinsY = binsy + 2;
			 double[][] newHeights = new double[ibinsX][ibinsY];
             double[][] newErrors = new double[ibinsX][ibinsY];
             double[][] newMeansX = new double[ibinsX][ibinsY];
             double[][] newRmssX = new double[ibinsX][ibinsY];
             double[][] newMeansY = new double[ibinsX][ibinsY];
             double[][] newRmssY = new double[ibinsX][ibinsY];
             int[][] newEntries = new int[ibinsX][ibinsY];
            
             
             int k=0;
             for (int j1 = 0; j1 <  ibinsX - 1; j1++) {
                 for (int j2 = 0; j2 < ibinsY - 1; j2++) {
            	            	 
            	  newHeights[j1 +1][j2 + 1] = h.getHeights(k);
                  newErrors[j1 + 1][j2 + 1] = h.getErrors(k);
                  newEntries[j1 + 1][j2 + 1] = h.getEntries(k);
                  newMeansX[j1 + 1][j2 + 1] = h.getMeansX(k);
                  newRmssX[j1 + 1][j2 + 1] = h.getRmsesX(k);
                  newMeansY[j1 + 1][j2 + 1] = h.getMeansY(k);
                  newRmssY[j1 + 1][j2 + 1] = h.getRmsesY(k);
                //  System.out.println("Reading:");
                //  System.out.println(k);
                 // System.out.println(Integer.toString(j1)+" "+Integer.toString(j2)+ "  value="+
                 //		  Integer.toString( h.getEntries(k)));
            	
                  k++;
                 }  

         }
             h2.setContents(newHeights, newErrors, newEntries, newMeansX,
                     newMeansY, newRmssX, newRmssY);

			
			return h2;
		
		}
		

		return ob;
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
	 * Keep info about one entry
	 * @author S.Chekanov
	 *
	 */
	class FileEntry  implements Comparable<FileEntry> {
		  private String  name;
		  private long    id;
		  private long    compression_size;
		  private long    serialized_size;

		  public FileEntry(String name, long id, long compression_size, long serialized_size) {
		    this.name=name;
		    this.id=id;
		    this.compression_size=compression_size;
		    this.serialized_size=serialized_size;
		  }

		  public String getName() {
		    return this.name;
		  }

		  public long getID() {
			    return this.id;
			  }

		  public long getCompressedSize() {
			    return this.compression_size;
			  }
		  public long getSerializedSize() {
			    return this.serialized_size;
			  }

		public int compareTo(FileEntry e) {
			
		  int a=0;
      	  if (e.getID()<getID()) a=1;  else a=-1;
      	  return a;
		}
		 
		  
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
