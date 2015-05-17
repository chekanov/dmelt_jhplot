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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import jhplot.H1D;
import jhplot.H2D;
import jhplot.P1D;
import jhplot.gui.HelpBrowser;
import hep.io.root.*;
import hep.io.root.interfaces.*;

/**
 * Class to read ROOT files. To get directory inside the file, use "cd()"
 * method. You can get any objects inside a ROOT file as using keys as: <p>
 * 
 * key1 = rr.getKey("dir1") <br>
 * dir1=key1.getObject() <br>
 * key2=dir1.getKey("dir2") <br>
 * dir2=key2.getObject() <br>
 * key3=dir2.getKey("Histogram") <br>
 * h1d=H1D( key3.getObject() ) <br>
 * 
 * @author S.Chekanov
 * 
 */
public class FileRoot {

	private RootFileReader rfr = null;
	private TDirectory current = null; // current directory

	/**
	 * Main constructor to build root file reader.
	 * 
	 * @param file
	 *            Input root file
	 */
	public FileRoot(String file) {
		try {
			rfr = new RootFileReader(file);
			current = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			jhplot.utils.Util.ErrorMessage("Error in openning file");
		}

	}

	/**
	 * Open file from URL
	 * 
	 * @param url
	 *            Input URL file
	 */
	public FileRoot(URL url) {
		try {
			rfr = new RootFileReader(url);
			current = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			jhplot.utils.Util.ErrorMessage("Error in openning file");
		}

	}

	/**
	 * Navigate to a directory inside the ROOT file.
	 * 
	 * @param directory
	 *            inside the file. Use the standard unix path, i.e.
	 *            dir1/dir2/dir3
	 * 
	 */
	public TDirectory cd(String dir) {

		current = null;

		String[] dirs = dir.split("/");

		if (dirs.length == 0)
			return null;

		TKey key = rfr.getKey(dirs[0]);

		for (int j = 1; j < dirs.length; j++) {
			String d = dirs[j];
			// System.out.println(j);
			// System.out.println(d);
			try {
				current = (TDirectory) key.getObject();
			} catch (RootClassNotFound e) {
				jhplot.utils.Util.ErrorMessage("No such name");
			} catch (IOException e) {
				jhplot.utils.Util.ErrorMessage("No such directory");
			}
			key = current.getKey(d);
		}

		try {
			current = (TDirectory) key.getObject();
		} catch (RootClassNotFound e) {
			jhplot.utils.Util.ErrorMessage("No such name");
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No such name");
		}
		return current;
	}

	/**
	 * 
	 * 
	 * Return all root objects in form of a string. Use cd(dir) to navigate to a
	 * specific directory
	 * 
	 * @return a string showing all ROOT objects in the current directory
	 * 
	 */
	public String toString() {

		String tmp = getTitle() + "\n";
		tmp = tmp + getVersion() + "\n";

		if (current == null) {
			for (int i = 0; i < rfr.nKeys(); i++) {
				TKey k = rfr.getKey(i);
				String s = k.getName();
				String ss = Integer.toString(i) + "  " + s + "\n";
				tmp = tmp + ss;
			}
		} else {
			for (int i = 0; i < current.nKeys(); i++) {
				TKey k = current.getKey(i);
				String s = k.getName();
				String ss = Integer.toString(i) + "  " + s + "\n";
				tmp = tmp + ss;
			}

		}

		return tmp;
	}

	/**
	 * Return all keys of the current directory as a list.
	 * 
	 * @return a list of all keys for the current directory
	 * 
	 */
	public ArrayList<String> getKeys() {

		ArrayList<String> tmp = null;

		if (current == null) {
			tmp =  new ArrayList<String>(rfr.nKeys());
			for (int i = 0; i < rfr.nKeys(); i++) {
				TKey k = rfr.getKey(i);
				tmp.add(k.getName());
			}
		} else {
			tmp =  new ArrayList<String>(current.nKeys());
			for (int i = 0; i < current.nKeys(); i++) {
				TKey k = current.getKey(i);
				tmp.add(k.getName());
			}

		}

		return tmp;
	}

	/**
	 * Get ROOT version
	 * 
	 * @return ROOT version
	 */

	public int getVersion() {

		return rfr.getVersion();
	}

	/**
	 * Get number of objects in the current directory of a ROOT file.
	 * 
	 * @return number of objects
	 */

	public int getNKeys() {

		if (current == null)
			return rfr.nKeys();
		else
			return current.nKeys();
	}

	/**
	 * Get ROOT title
	 * 
	 * @return ROOT title
	 */

	public String getTitle() {

		return rfr.getTitle();
	}

	/**
	 * Get a key using its index. Can be called after navigation to a directory
	 * using the cd() method.
	 * 
	 * @param i
	 *            index of this key
	 * @return TKey object.
	 */
	public TKey getKey(int i) {
		if (current == null)
			return rfr.getKey(i);
		else
			return current.getKey(i);
	}

	/**
	 * Get the StreamerInfo
	 * 
	 * @param streamer
	 *            info
	 */
	public List getInfo() {
		try {
			return rfr.streamerInfo();
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No streamer info");
		}
		return null;
	}

	/**
	 * Get a key
	 * 
	 * @param skey
	 *            key name
	 * @return TKey object.
	 */
	public TKey getKey(String skey) {

		return rfr.getKey(skey);
	}

	/**
	 * Returns RootFileReader
	 * 
	 * @return RootFileReader
	 */

	public RootFileReader getReader() {

		return rfr;
	}

	/**
	 * Get H1D histogram associated with the key name. It is assumed that this
	 * ROOT key keeps histogram (TH1).
	 * 
	 * @param skey
	 *            key name
	 * @return H1D histogram
	 */
	public H1D getH1D(String skey) {

		TKey k = null;
		if (current == null)
			k = rfr.getKey(skey);
		else
			k = current.getKey(skey);
		Object obj = null;
		try {
			obj = k.getObject();
			String name = obj.getClass().getName();
			if (name == "hep.io.root.proxy.TH1F"
					|| name == "hep.io.root.proxy.TH1D") {
				return new H1D((TH1) obj);
			}
		} catch (RootClassNotFound e) {
			jhplot.utils.Util.ErrorMessage("No such name");
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No such object");
		}

		return null;
	}

	/**
	 * Get H2D histogram associated with the key name. It is assumed that this
	 * ROOT key keeps histogram (TH1).
	 * 
	 * @param skey
	 *            key name
	 * @return H2D histogram
	 */
	public H2D getH2D(String skey) {

		TKey k = null;
		if (current == null)
			k = rfr.getKey(skey);
		else
			k = current.getKey(skey);
		Object obj = null;
		try {
			obj = k.getObject();
			String name = obj.getClass().getName();
			if (name == "hep.io.root.proxy.TH2F"
					|| name == "hep.io.root.proxy.TH2D") {
				return new H2D((TH2) obj);
			}
		} catch (RootClassNotFound e) {
			jhplot.utils.Util.ErrorMessage("No such name");
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No such object");
		}

		return null;
	}

	/**
	 * Get P1D associated with the key name. It corresponds to TGraph in ROOT.
	 * It is assumed that this ROOT key keeps histogram (TH1).
	 * 
	 * @param skey
	 *            key name
	 * @return P1D object representing TGraph inside ROOT file
	 */
	public P1D getP1D(String skey) {

		TKey k = null;
		if (current == null)
			k = rfr.getKey(skey);
		else
			k = current.getKey(skey);
		Object obj = null;
		try {
			obj = k.getObject();
			String name = obj.getClass().getName();
			if (name == "hep.io.root.proxy.TGraph") {

				TGraph tt = (TGraph) obj;
				String tname = tt.getTitle();
				double[] x = tt.getX();
				double[] y = tt.getY();
				return new P1D(tname, x, y);

			}
		} catch (RootClassNotFound e) {
			jhplot.utils.Util.ErrorMessage("No such name");
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No such object");
		}

		return null;
	}

	/**
	 * Get object associated with the key name in the current directory. The
	 * mapping is as following: <br>
	 * TGraph will be returned as P1D object<br>
	 * TH1F or TH1D will be returned as H1D object<br>
	 * TH2F or TH2D will be returned as H2D object<br>
	 * other objects will be returned as in hep.io.root.interfaces.<br>
	 * 
	 * @param skey
	 *            key name
	 * @return Object depending on the key.
	 */
	public Object get(String skey) {

		TKey k = null;
		if (current == null)
			k = rfr.getKey(skey);
		else
			k = current.getKey(skey);

		Object obj = null;

		try {
			obj = k.getObject();

			String name = obj.getClass().getName();

			// System.out.println(name);

			if (name == "hep.io.root.proxy.TGraph") {

				TGraph tt = (TGraph) obj;
				String title = tt.getTitle();
				// String tname=tt.getName();
				double[] x = tt.getX();
				double[] y = tt.getY();

				return new P1D(title, x, y);

			} else if (name == "hep.io.root.proxy.TGraphErrors") {

			} else if (name == "hep.io.root.proxy.TGraphAsymmErrors") {

			} else if (name == "hep.io.root.proxy.TH1F"
					|| name == "hep.io.root.proxy.TH1D") {

				return new H1D((TH1) obj);

			} else if (name == "hep.io.root.proxy.TH2F"
					|| name == "hep.io.root.proxy.TH2D") {

				return new H2D((TH2) obj);

			} else {

				return obj;
			}

		} catch (RootClassNotFound e) {
			jhplot.utils.Util.ErrorMessage("No such name");
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No such object");
		}

		return obj;
	}

	/**
	 * Get object associated with the key name in the current directory.
	 * 
	 * @param skey
	 *            key name
	 * @return Object
	 */
	public Object getObject(String skey) {

		TKey k = null;
		if (current == null)
			k = rfr.getKey(skey);
		else
			k = current.getKey(skey);

		try {
			return k.getObject();
		} catch (RootClassNotFound e) {
			jhplot.utils.Util.ErrorMessage("No such name");
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No such object");
		}

		return null;
	}

	/**
	 * Get object associated with the key name in tthe current directory.
	 * 
	 * @param i
	 *            key index
	 * @return Object
	 */
	public Object getObject(int i) {

		TKey k = null;
		if (current == null)
			k = rfr.getKey(i);
		else
			k = current.getKey(i);

		try {
			return k.getObject();
		} catch (RootClassNotFound e) {
			jhplot.utils.Util.ErrorMessage("No such class");
		} catch (IOException e) {
			jhplot.utils.Util.ErrorMessage("No such object");
		}

		return null;
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
