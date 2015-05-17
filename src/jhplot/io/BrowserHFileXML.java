package jhplot.io;

import java.util.Map;
import java.util.TreeMap;
import jhplot.*;
import jhplot.gui.GHFrame;

/**
 * 
 * A GUI to browser data stored in a HFileXML format (the extension *.jxml).
 * This is human-readable XML Java serialisation.
 * 
 * @author S.Chekanov
 * 
 */
public class BrowserHFileXML extends BrowserDataGeneric {

	private static final long serialVersionUID = 1L;
	boolean take = true;
	private Map<String, Object> map;

	public BrowserHFileXML() {

		super();
	}

	/**
	 * Show the BrowserHFileXML.
	 * 
	 * @param frame
	 * @param hfile
	 * @param ishow
	 */
	public BrowserHFileXML(final GHFrame frame, HFileXML hfile, boolean ishow) {

		super();
		setFile(frame, hfile, ishow);

	}

	/**
	 * Open HFileXML file in a pop-up window to browser it and check all objects
	 * 
	 * @param frame
	 *            Canvas for plotting
	 * @param hfile
	 *            input HFile with serialized data
	 * @param ishow
	 *            true if frame should be show.
	 **/
	public void setFile(GHFrame frame, HFileXML hfile, boolean ishow) {

		map = new TreeMap<String, Object>();

		while (take = true) {
			Object obj = hfile.read();
			if (obj == null) {
				take = false;
				break;
			}
			Class<? extends Object> cls = obj.getClass();
			String name = cls.getName();
			String title = "None";

			if (name.equalsIgnoreCase("jhplot.P0D"))
				title = ((P0D) obj).getTitle();
			if (name.equalsIgnoreCase("jhplot.P0I"))
				title = ((P0I) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.P1D"))
				title = ((P1D) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.PND"))
				title = ((PND) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.PNI"))
				title = ((PNI) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.F1D"))
				title = ((F1D) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.F2D"))
				title = ((F2D) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.FND"))
				title = ((FND) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.FPR"))
				title = ((FPR) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.H1D"))
				title = ((H1D) obj).getTitle();
			else if (name.equalsIgnoreCase("jhplot.H2D"))
				title = ((H2D) obj).getTitle();

			title = title.replaceAll("jhplot.", "");
			map.put(name + " : " + title, obj);
			// System.out.println(name);
		}

		map = hfile.getObjectMap();

		// now add the rest
		// map.putAll(hfile.getObjectMap());

		hfile.close();

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
