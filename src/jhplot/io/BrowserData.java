package jhplot.io;

import java.awt.Component;
import java.util.Map;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;

/**
 * Open a data browser for generic data. Available: .ser,.xml, .jdat, .root,
 * .pbu".
 * 
 * @author sergei
 * 
 */
public class BrowserData {

	private Map<String, Object> map;

	/**
	 * Construct a data browser using the input file for 2D data. The file can
	 * be URL (starts from http or ftp).
	 * <p>
	 * File must have the extensions <br>: 
         * "jdat" (HBook) <br>
	 * "jser" (HFile) <br>
	 * "jxml" (HFileXML) <br>
         * "jpbu" (PFile) <br>
	 * "root" (ROOT) <br>
	 * "aida/xml" (AIDA) <br>
	 * 
	 * @param file
	 *            input file or URL
	 * @param frame
	 *            frame if any.
	 */
	public BrowserData(final String file, final GHFrame frame) {

		String ss = file.toLowerCase();
		if (ss.endsWith(".jser")) {
			HFile hh = new HFile(file);
			if (frame != null)
				new BrowserHFile(frame, hh, true);
			else {
				BrowserHFile b = new BrowserHFile(null, hh, true);
				map = b.getMap();
			}
		} else if (ss.endsWith(".jxml")) {
			HFileXML hh = new HFileXML(file);
			if (frame != null)
				new BrowserHFileXML(frame, hh, true);
			else {
				BrowserHFileXML b = new BrowserHFileXML(null, hh, true);
				map = b.getMap();
			}
		} else if (ss.endsWith(".jpbu")) {
			PFile hh = new PFile(file);

			if (frame != null)
				new jhplot.io.BrowserPFile(frame, hh, true);
			else {
				BrowserPFile b = new BrowserPFile(null, hh, true);
				map = b.getMap();
			}
		} else if (ss.endsWith(".jdat")) {
			jhplot.io.HBook hb = new HBook(file, "r");
			// System.out.println("Call to HBook");
			if (frame != null)
				new jhplot.io.BrowserHBook(frame, hb, true);
			else {
				BrowserHBook b = new BrowserHBook(null, hb, true);
				map = b.getMap();
			}

		} else if (ss.endsWith(".root")) {

			if (frame != null)
				new jhplot.io.BrowserRoot(frame, file, true);
			else {
				BrowserRoot b = new BrowserRoot(null, file, true);
				map = b.getMap();
			}

		} else if (ss.endsWith(".xml") || ss.endsWith(".aida")) {

			if (frame != null)
				new jhplot.io.BrowserAida(frame, file, true);
			else {
				BrowserAida b = new BrowserAida(null, file, true);
				map = b.getMap();

			}

		} else {

			jhplot.utils.Util
					.ErrorMessage("Not supported file format. Required:  *.jser, *.jxml, *.jdat, *.root, *.jpbu");
		}; 
	}

	/**
	 * Return a map with all objects
	 * 
	 * @return
	 */
	public Map<String, Object> getMap() {

		return map;
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
