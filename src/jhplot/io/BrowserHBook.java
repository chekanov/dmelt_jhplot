package jhplot.io;

import java.util.Map;

import javax.swing.JComponent;

import jhplot.HPlot;
import jhplot.HPlot3D;
import jhplot.gui.GHFrame;

/**
 * 
 * A GUI to browser data stored in HBook XML format (extension "jdat").
 * 
 * @author S.Chekanov
 * 
 */
public class BrowserHBook extends BrowserDataGeneric {

	private static final long serialVersionUID = 1L;
	boolean take = true;
	private Map<String, Object> map;

	public BrowserHBook() {

		super();
	}

	/**
	 * Show the browser.
	 * 
	 * @param h
	 * @param hfile
	 * @param ishow
	 */
	public BrowserHBook(final GHFrame h, HBook hfile, boolean ishow) {

		super();
		setFile(h, hfile, ishow);

	}

	/**
	 * Read data from HBook path.
	 * 
	 * @param frame
	 * @param filepath
	 * @param ishow
	 */
	public void setFile(GHFrame frame, HBook hb, boolean ishow) {

              //  System.out.println("Call to setFile");

		map = hb.getAll();
		if (frame != null)
			setDataFileBrowser(frame, map, ishow);
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
