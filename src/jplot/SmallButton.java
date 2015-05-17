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
import javax.swing.border.*;
import java.awt.event.*;

/**
 * Class small button displays a button in the new 'window' style, i.e., you
 * only see the button in raised or lowered state if you are on it with the
 * mouse.
 */
public class SmallButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 1L;
	protected Border raised;
	protected Border lowered;
	protected Border inactive;

	/**
	 * Principal constructor, builds the button with a specific action and a
	 * tool tip.
	 * 
	 * @param action
	 *            action performed when pressing
	 * @param tip
	 *            tool tip
	 */
	public SmallButton(Action action, String tip) {
		super((Icon) action.getValue(Action.SMALL_ICON));
		addActionListener(action);
		raised = new BevelBorder(BevelBorder.RAISED);
		lowered = new BevelBorder(BevelBorder.LOWERED);
		inactive = new EmptyBorder(2, 2, 2, 2);
		setBorder(inactive);
		setToolTipText(tip);
		addMouseListener(this);
	}

	public void mouseReleased(MouseEvent e) {
		setBorder(inactive);
	}

	public void mouseEntered(MouseEvent e) {
		setBorder(raised);
	}

	public void mouseExited(MouseEvent e) {
		setBorder(inactive);
	}

	public void mouseClicked(MouseEvent e) {
	}
	
	public void resetBorder() {
		setBorder(inactive);
	}

	public float getAlignmentY() {
		return 0.5f;
	}

	public void mousePressed(MouseEvent e) {
		setBorder(lowered);
	}

	

}
