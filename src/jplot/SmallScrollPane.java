/**
 *    Copyright (C)  DataMelt project. The jHPLot package.
 *    Includes coding developed for Centre d'Informatique Geologique
 *    by J.V.Lee priory 2000 GNU license.
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
package jplot;

import javax.swing.*;

/**
 * This panel builds a list and returns it wrapped in a minimized scroll pane
 * panel.
 */
public class SmallScrollPane extends JScrollPane {

	private static final long serialVersionUID = 1L;
	JViewport vp;
	JList list;

	/**
	 * Constructor. We build a list and return it wrapped in a minimized scroll
	 * pane panel.
	 * 
	 * @param a
	 *            array of objects which fill the list
	 * @param index
	 *            initially selected index
	 * @return scroll pane panel
	 */
	public SmallScrollPane(Object[] a, boolean editable) {
		list = new JList(a);
		// list.setSelectedIndex(index);
		list.setVisibleRowCount(1);
		vp = getViewport();
		vp.setView(list);
		// list.ensureIndexIsVisible(index);
		list.setSelectionBackground(list.getBackground());
	}

	/**
	 * Constructor. We build a list and return it wrapped in a minimized
	 * scroll pane panel.
	 * 
	 * @param a
	 *            array of objects which fill the list
	 * @param index
	 *            initially selected index
	 * @return scroll pane panel
	 */
	public SmallScrollPane(Object[] a) {
		this(a, false);
	}

	/**
	 * Sets the selected item and lets the view port scroll to the item
	 * corresponding to this index.
	 * 
	 * @param item
	 *            item which should be visible and selected
	 */
	public void setSelected(Object item) {
		list.setSelectedValue(item, true);
	}

	/**
	 * Returns the selected index of the scroll pane.
	 * 
	 * @return the selected index of the scroll pane.
	 */
	public int getSelectedIndex() {
		return list.locationToIndex(vp.getViewPosition());
	}

	/**
	 * Sets the selected index and lets the viewport scroll to the item
	 * corresponding to this index.
	 * 
	 * @param index
	 *            selected index
	 */
	public void setSelectedIndex(int index) {
		list.setSelectedIndex(index);
		list.ensureIndexIsVisible(index);
		// vp.setViewPosition(list.indexToLocation(selectedIndex));
	}

}
