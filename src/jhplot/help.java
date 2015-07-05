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

package jhplot;

import jhplot.gui.HelpBrowser;

/**
 * Look at the Javadoc of a given class using the DMelt web help system.
 * Internet access is required. To view certain 3rd party libraries, you should
 * be register as a full DMelt member.
 * 
 * @author S.Chekanov
 * 
 */

public class help {

	private static String JAVA_API = "http://docs.oracle.com/javase/8/docs/api/";

	/**
	 * Brings up a window that show javadoc documentation of a given class.
	 * Internet should be enabled.
	 * 
	 * @param obj
	 *            Object for searching for a documentation.
	 */
	public static void doc(Object ob) {
		new HelpBrowser(url(ob));

	}

	/**
	 * Returns the full name of the class. No INTERNET is required.
	 * 
	 * @param obj
	 *            Object for searching for a documentation.
	 * @param name
	 *            of the class
	 */
	public static String name(Object ob) {
		if (ob == null)
			return "null object!";
		return ob.getClass().getName();
	}

	/**
	 * Returns the javadoc URL for this class. No INTERNET is required.
	 * 
	 * @param obj
	 *            Object for searching for a documentation.
	 * @param url
	 *            javadoc URL.
	 */
	public static String url(Object ob) {
		if (ob == null)
			return "null object!";
		String a = ob.getClass().getName();

		// determine URL location // default
		String DMELT = HelpBrowser.JHPLOT_HTTP;
		if (a.startsWith("java.") || a.startsWith("javax.")) {
			DMELT = JAVA_API;
		}
		a = a.replace(".", "/") + ".html";
		return DMELT + a;
	}

	/**
	 * Set Java API web page. The default is
	 * http://docs.oracle.com/javase/8/docs/api/.  You can redefine this default here.
	 * 
	 * @param url
	 *            URL of the official Java API web page.
	 */
	public static void setJavaURL(String url) {
		JAVA_API = url;
	}

}
