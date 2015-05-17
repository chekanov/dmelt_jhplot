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


package jplot.panels;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

import javax.swing.border.*;

import jplot.GraphSettings;
import jplot.Utils;

/**
 * Builds a panel with a color panel from which defined colors can be selected.
 * Provides a show method which returns the color.
 */
public class PanelColor extends JPanel {

	private static final long serialVersionUID = 1L;
	private Color result;
	private Color selectedColor;
	private SmallColorPreview smallColorPreview;
	private ColorSwatches swatches;
	private JDialog dialog;
	private JColorChooser colorChooser;
	private Frame parent;

	/**
	 * Constructor, builds the panel from a ColorSwatchPanel and a handle to
	 * Swing's ColorChooser. Provide an index which points to the actually
	 * selected swatch (can be -1 if nothing's selected).
	 */
	public PanelColor(Frame fr) {
		parent = fr;
		// setLayout(new BorderLayout());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder(new EtchedBorder(), "Colors"));

		// add a panel with 16 pre-defined colors:
		// ----------------------------------------
		JPanel p = new JPanel();
		swatches = new ColorSwatches();
		p.add(swatches);
		add(p);

		colorChooser = Utils.getColorChooser();

		// make a button for a custom color chooser...
		// --------------------------------------------
		JButton ccButton = new JButton("Custom...");
		ccButton.setPreferredSize(new Dimension(65, 26));
		ccButton.setMargin(new Insets(1, 1, 1, 1));
		ccButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
		ccButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorChooser.setColor(selectedColor);
				Color c = JColorChooser.showDialog(parent, "Color Chooser",
						selectedColor);
				if (c != null) {
					setSelectedColor(c);
					swatches.resetBackgrounds();
				}
			}
		});
		p = new JPanel();
		p.add(ccButton);
		add(p);

		// ...and a small rectangle with the actual color:
		// ------------------------------------------------
		smallColorPreview = new SmallColorPreview(36, 17);
		p = new JPanel();
		p.add(smallColorPreview);
		add(p);
	}

	/**
	 * Constructor, initializes the panel with a color.
	 * 
	 * @param c
	 *            initial color
	 */
	public PanelColor(Frame parent, Color c) {
		this(parent);
		refresh(c);
		// setSelectedColor(c);
	}

	/**
	 * Refreshes the panel with a new color
	 * 
	 * @param c
	 *            new color
	 */
	public void refresh(Color c) {
		setSelectedColor(c);
		int index = swatches.findSwatchIndex(c);
		if (index > -1)
			swatches.setSelectedSwatch(index);
	}

	/**
	 * @return the selected color.
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}

	/**
	 * Sets the selected color.
	 * 
	 * @param c
	 *            color
	 */
	public void setSelectedColor(Color c) {
		selectedColor = c;
		smallColorPreview.repaint();
	}

	/**
	 * Pops up a modal frame including the color panel. Returns the actual color
	 * or null if the user pressed the cancel button.
	 * 
	 * @param parent
	 *            parent frame
	 * @param x
	 *            x-position of the dialog
	 * @param y
	 *            y-position of the dialog
	 * @return the color chosen by the user, null if canceled.
	 */
	public Color show(Frame parent, Color c, int x, int y) {
		if (dialog == null) {
			JPanel panel = new JPanel(new BorderLayout());
			dialog = new JDialog(parent, "Color chooser", true);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dialog.dispose();
				}
			});
			JPanel p = new JPanel(new FlowLayout());
			p.setBorder(BorderFactory.createEtchedBorder());
			JButton b = new JButton("Apply");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					result = selectedColor;
					dialog.dispose();
				}
			});
			p.add(b);
			b = new JButton("Cancel");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					result = null;
					dialog.dispose();
				}
			});
			p.add(b);
			panel.add(this, BorderLayout.CENTER);
			panel.add(p, BorderLayout.SOUTH);
			dialog.getContentPane().add(panel);
			dialog.setLocation(x, y);
			dialog.pack();
		}
		refresh(c);
		dialog.setVisible(true); // blocks until user brings dialog down.
		return result;
	}

	/**
	 * ColorSwatches displays a panel with a number of predefined colors from
	 * which we can choose. Secondly, more refined colors can be obtained by
	 * calling Swing's color chooser.
	 */
	class ColorSwatches extends PanelSeeOption {
		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;
		int index;

		public ColorSwatches() {
			if (selectedColor != null) {
				index = findSwatchIndex(selectedColor);
				if (index > -1)
					setSelectedSwatch(index);
			}
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						index = getIndex(e.getX(), e.getY());
						setSelectedSwatch(index);
						Color[] cc= GraphSettings.getColors();
						setSelectedColor(cc[index]);
						// lp.setColor(index);
					}
				}
			});
		}

		/**
		 * This method finds the index of the button of the panel corresponding
		 * to a color. Returns -1 if the color is not present in the panel.
		 */
		public int findSwatchIndex(Color c) {
			for (int i = 0; i < GraphSettings.getColors().length; i++) {
				Color[] cc= GraphSettings.getColors();
				if (c.equals(cc[i]))
					return i;
			}
			return -1;
		}

		/**
		 * Sets the initial values, notably the panel size (in points) and the
		 * number of panels (rows, columns).
		 */
		protected void initValues() {
			soptionSize = new Dimension(14, 14);
			numSOption = new Dimension(4, 5);
		}

		/**
		 * Paint method for a swatch rectangle.
		 * 
		 * @param g2
		 *            graphics (2D) instance.
		 * @param column
		 *            actual column
		 * @param row
		 *            actual row
		 * @param x
		 *            x-coordinate, left side of the option panel
		 * @param y
		 *            y-coordinate, lower border of the option panel.
		 */
		protected void paintIt(Graphics2D g2, int column, int row, int x, int y) {
			Color[] cc= GraphSettings.getColors();
			g2.setColor(cc[getIndex(x, y)]);
			g2.fillRect(x, y, soptionSize.width, soptionSize.height);
		}

		/**
		 * Returns the tooltip text corresponding to each button.
		 * 
		 * @return tooltip text for a specific button.
		 */
		public String getToolTipText(MouseEvent e) {
			int i = getIndex(e.getX(), e.getY());
			Color[] cc= GraphSettings.getColors();
			if (i >= 0 && i < Ns) {			
				return cc[i].getRed() + ","
						+ cc[i].getGreen() + ","
						+ cc[i].getBlue();
			}
			return "";
		}
	}

	/**
	 * A panel painted with the actual color.
	 */
	class SmallColorPreview extends JPanel {
		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;
		protected Dimension panelSize;
		int height, width;

		/**
		 * Constructs the panel
		 */
		public SmallColorPreview(int width, int height) {
			setOpaque(true);
			setRequestFocusEnabled(false);
			this.width = width;
			this.height = height;
		}

		/**
		 * Color the canvas and add some decoration (shadow).
		 */
		public void paintComponent(Graphics g) {
			g.setColor(getSelectedColor());
			g.fillRect(0, 0, width, height);
			g.setColor(Color.white);
			g.drawLine(0, height - 1, width - 1, height - 1);
			g.drawLine(width - 1, 0, width - 1, height - 1);
			g.setColor(Color.black);
			g.drawLine(0, 0, width - 1, 0);
			g.drawLine(0, 0, 0, height - 1);
		}

		public Dimension getPreferredSize() {
			return new Dimension(width, height);
		}
	}
}
