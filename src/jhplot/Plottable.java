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

import java.awt.*;
import java.io.PrintStream;

import jplot.*;
import java.io.Serializable;

/**
 * All objects which can be plottable should implement this class.
 * 
 * @author S.Chekanov
 * 
 */

public class Plottable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String title = "";

	protected String labelX = "";

	protected String labelY = "";

	protected String labelZ = "";

	protected boolean is3D = false;

	/**
	 * Sets drawing options
	 * 
	 */
	public Plottable() {

	}

	/**
	 * Set a new title
	 * 
	 * @param title
	 *            New Title
	 */
	public void setTitle(String title) {
		this.title = title;

	}

	/**
	 * Get a new title
	 * 
	 * @return Title
	 */

	public String getTitle() {
		return this.title;

	}

	/**
	 * Set lablel for X axis
	 * 
	 * @param labelX
	 *            label
	 */

	public void setLabelX(String labelX) {
		this.labelX = labelX;
	}

	/**
	 * Set lablel for Y axis
	 * 
	 * @param labelY
	 *            label
	 */

	public void setLabelY(String labelY) {
		this.labelY = labelY;
	}

	/**
	 * Set lablel for Z axis if applicable.
	 * 
	 * @param labelZ
	 *            label
	 */

	public void setLabelZ(String labelZ) {
		this.labelZ = labelZ;
	}

	/**
	 * Get the label X
	 * 
	 * @return label Y
	 */
	public String getLabelX() {
		return this.labelX;
	}

	/**
	 * Get label Y
	 * 
	 * @return label Y
	 */
	public String getLabelY() {
		return this.labelY;
	}

	/**
	 * Get label Z
	 * 
	 * @return label Z
	 */
	public String getLabelZ() {
		return this.labelZ;
	}

	/**
	 * Is this plot for 3D canvas.
	 * 
	 * @return true if 3D canvas.
	 */
	public boolean is3D() {

		return is3D;
	}

	/**
	 * Set 3D if this is for 3D canvas.
	 * 
	 * @param is3d
	 *            true if 3D
	 */
	public void set3D(boolean is3d) {
		is3D = is3d;
	}
}
