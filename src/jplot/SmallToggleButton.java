/*
 * Class defining a small toggle button
 * 
 * Copyright stuff goes here.
 */

package jplot;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;

import java.awt.event.*;
import java.awt.*;

/**
 * Class small button displays a toggle button in the new 'window' style,
 * i.e., pressed lesses the button in a 'lowered' state, else it
 * remains raised.
 */
public class SmallToggleButton extends JToggleButton implements ItemListener {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
protected Border raised;
  protected Border lowered;

  /**
   * Principal constructor, builds the button with a specific
   * action and a tool tip.
   * @param selected true if this button is currently selected
   * @param imgUnselected icon used for the unselected state
   * @param imgSelected icon used for the selected state
   * @param tip tool tip
   */
  public SmallToggleButton(boolean selected, ImageIcon imgUnselected,
			   ImageIcon imgSelected, String tip) {
    super(imgUnselected,selected);
    setHorizontalAlignment(CENTER);
    setBorderPainted(true);
    raised = new BevelBorder(BevelBorder.RAISED);
    lowered = new BevelBorder(BevelBorder.LOWERED);
    setBorder(selected? lowered : raised);
    setMargin(new Insets(1,1,1,1));
    setToolTipText(tip);
    addItemListener(this);
    setSelectedIcon(imgSelected);
    setRequestFocusEnabled(false);
  }

  /**
   * Set the border of the button to the appropiate state. The button
   * border is raised if selected, lowered otherwise.
   */
  public void resetBorder() {
    setBorder(isSelected()? lowered : raised);
  }

  /**
   * No idea why I need this method but ala.
   */
  public float getAlignmentY() {
    return 0.5f;
  }

  /**
   * Sets the button to the appropiate state.
   * @param e event
   */
  public void itemStateChanged(ItemEvent e) {
    setBorder(isSelected()? lowered : raised);
  }
}
