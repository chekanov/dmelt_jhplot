package jhplot.io;


import java.awt.Component;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;
import jhplot.HPlot;
import jhplot.HPlot3D;
import jhplot.gui.GHFrame;
import hep.io.root.*;
import hep.io.root.interfaces.*;

/**
 * 
 * A GUI to browser ROOT  data stored in .root  files.
 * 
 * @author S.Chekanov
 * 
 */
public class BrowserRoot extends BrowserDataGeneric {

	
	private static final long serialVersionUID = 1L;
	boolean take = true;
	private Map<String, Object> map;
	
	
	public BrowserRoot(){
		super();
	}
	
	
	
	/**
	 * Show the BrowserRoot
	 * @param h
	 * @param hfile
	 * @param ishow
	 */
public BrowserRoot(GHFrame   h,  String  hfile, boolean ishow){
		super();
		setFile(h, hfile, ishow);
		
	}
	

/**
 * Read data from AIDA file. 
 * @param frame
 * @param filepath
 * @param ishow
 */
public void setFile(GHFrame frame, String  hb, boolean ishow) {
	
	         map = new TreeMap<String, Object>();
	         FileRoot rf = new FileRoot(hb);
                 RootFileReader reader = rf.getReader(); 
                 for (int i = 0; i < reader.nKeys(); i++) {
                     TKey k = reader.getKey(i);
                     String key = k.getName();
                 try {
                        Object obj = k.getObject();
                        map.put(key, obj);
                } catch (RootClassNotFound  | IOException e) {
                        jhplot.utils.Util.ErrorMessage("No such name");
                }
                }

	         if (frame!=null) setDataFileBrowser(frame, map,ishow);


                  }
	


/**
 * Return a map with all objects
 * 
 * @return
 */
public Map<String, Object> getMap() {

	return map;
}



}
