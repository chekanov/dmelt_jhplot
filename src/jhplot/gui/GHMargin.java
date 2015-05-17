// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot.gui;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.freehep.graphics2d.VectorGraphics;


import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import jhplot.JHPlot;
import jhplot.utils.GHFontChooser;

import graph.*;
import jplot.*;

/**
 * Panels with global margin. You can access all margins from GHPanel using
 * panel() method.
 * 
 * @author S.Chekanov
 * 
 */

public class GHMargin extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Rectangle selection;

	protected String sname = " ";

	private static boolean insideBox = false;

	private Font f;

	private Color c;

	private FontMetrics fm;

	private int textWidth = 0;

	private int textHeight = 0;

	private Color recColor;

	private Color recColor1;

	private Color bakColor;

	private final Color borderColor = new Color(151, 201, 201);

	private final GHPanel win;

	private int rotation;

	private String sinfo = "";

	private int dXsize;

	private int dYsize;

	private JPopupMenu menu;

	private SelectionChangeMouseListener scml;

	JMenuItem iedit, icolor, imargin1, imargin2, imargin3, idefault;

	/**
	 * Main class to create global margin
	 * 
	 * @param w
	 *            GHpanel
	 * @param wname
	 *            Name of this margin
	 */
	public GHMargin(GHPanel w, final String wname) {
		win = w;

		sinfo = wname + " margin";
		sname = " ";
		f = new Font("Lucida Sans", Font.BOLD, 20);
		c = Color.BLACK;

		this.setBorder(BorderFactory.createEmptyBorder());
		scml = new SelectionChangeMouseListener();
		this.addMouseListener(scml);
		this.addMouseMotionListener(scml);

		// this is necessary if you want to mix AWT with swing
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// pop-up menu for table
		menu = new JPopupMenu();

		JMenuItem info = new JMenuItem(sinfo);
		info.setEnabled(false);
		info.setForeground(borderColor);

		JMenuItem iedit = new JMenuItem("Edit title");
		JMenuItem imargin1 = new JMenuItem("Divider +");
		JMenuItem imargin2 = new JMenuItem("Divider -");
		JMenuItem imargin3 = new JMenuItem("Remove borders");
		JMenuItem idefault = new JMenuItem("Default location");
		menu.add(info);
		menu.addSeparator();
		menu.add(iedit);
		menu.add(imargin1);
		menu.add(imargin2);
		menu.add(imargin3);
		menu.add(idefault);

		recColor = Color.white;
		recColor1 = Color.white;
		bakColor = Color.white;
		rotation = 0;

		imargin3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showBorder(false);

			}
		});

		imargin1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				dXsize = getSizeX();
				dYsize = getSizeY();
				if (wname.equalsIgnoreCase("Left")
						|| wname.equalsIgnoreCase("Right")) {
					dXsize = dXsize + 5;
					setSizeMargin(dXsize, dYsize);
					// System.out.println(dXsize);
				}
				if (wname.equalsIgnoreCase("Top")
						|| wname.equalsIgnoreCase("Bottom")) {
					dYsize = dYsize + 5;
					setSizeMargin(dXsize, dYsize);
				}

				showBorder(true);
			}

		});

		imargin2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				dXsize = getSizeX();
				dYsize = getSizeY();
				if (wname.equalsIgnoreCase("Left")
						|| wname.equalsIgnoreCase("Right")) {
					dXsize = dXsize - 5;
					if (dXsize > 2)
						setSizeMargin(dXsize, dYsize);
				}
				if (wname.equalsIgnoreCase("Top")
						|| wname.equalsIgnoreCase("Bottom")) {
					dYsize = dYsize - 5;
					if (dYsize > 2)
						setSizeMargin(dXsize, dYsize);
				}

				showBorder(true);
			}
		});

		// cut

		
		
		
		
		
		iedit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JHPlot.showStatusBarText("Start margin editor..");

				if (sname == null)
					sname = " ";

				
				final JPanel jj = win.getCanvasPanel();
				GHFontChooser dlg = new GHFontChooser((Container)jj, sname);

				SimpleAttributeSet a = new SimpleAttributeSet();
				StyleConstants.setFontFamily(a, f.getName());
				StyleConstants.setFontSize(a, f.getSize());
				int style = f.getStyle();
				StyleConstants.setItalic(a, false);
				StyleConstants.setBold(a, false);
				if (style == Font.BOLD)
					StyleConstants.setBold(a, true);
				if (style == Font.ITALIC)
					StyleConstants.setItalic(a, true);

				StyleConstants.setBackground(a, bakColor);
				StyleConstants.setForeground(a, getGColor());
				dlg.setAttributes(a);
				dlg.setVisible(true);

				a = (SimpleAttributeSet) dlg.getAttributes();
				String newName = StyleConstants.getFontFamily(a);
				int newSize = StyleConstants.getFontSize(a);
				style = Font.PLAIN;
				if (StyleConstants.isBold(a))
					style = Font.BOLD;
				if (StyleConstants.isItalic(a))
					style = Font.ITALIC;
				Color newc = StyleConstants.getForeground(a);
				Color newcb = StyleConstants.getBackground(a);
				Font ff = new Font(newName, style, newSize);
				String ss = dlg.getInputText();

				if (ff != null && ss != null && newc != null && newcb != null) {

					setGRecBackground(newcb);
					setGBackground(newcb);

					if (wname.equalsIgnoreCase("Left")
							|| wname.equalsIgnoreCase("Right")) {
						setRotation(-90);
					}

					setString(ss, ff, newc,getSize());
					refreshText();
				}

			}
		});

		idefault.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setDefault();

			}
		});

	}

	/**
	 * Set the default location
	 * 
	 */
	public void setDefault() {
		setString(sname, f, c, this.getSize());
		showBorder(false);

	}

  /**
         * Set the default location
         * 
         */
        public void setDefault(Object com) {
                Dimension d=((JPanel)com).getSize(); 
                setString(sname, f, c, d);
                showBorder(false);
        }




	/**
	 * Dispose this canvas
	 * 
	 */
	public void disposeMe() {

		c = null;
		f = null;
		iedit = null;
		icolor = null;
		scml = null;
		imargin1 = null;
		imargin2 = null;
		imargin3 = null;
		idefault = null;
		bakColor = null;
		recColor = null;
		fm = null;
		selection = null;
		menu = null;
		System.gc();

	}

	/**
	 * Set the rotation angle of the text in degrees. Only 0, -90 and 90 are
	 * supported
	 * 
	 * @param rotation
	 *            Rotation of the text (0, -90, 90)
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	/**
	 * Get the rotation angle of the text in degrees. Only 0, -90 and 90 are
	 * supported.
	 * 
	 * @return Rotation of the text (0, -90, 90)
	 */
	public int getRotation() {
		return this.rotation;
	}

	/**
	 * Sets the string.
	 * 
	 * @param sname Text of the string
	 * @param f     Font used
	 * @param c     Color used
	 */
	public void setString(String sname, Font f, Color c, Dimension dd) {
		this.sname = sname;
		this.f = f;
		this.c = c;
		fm = getFontMetrics(f);
		String tmp = Translate.shrink(sname);
		textWidth = fm.stringWidth(tmp);
		textHeight =fm.getHeight();

		dYsize = (int)dd.getHeight();
		dXsize = (int)dd.getWidth();
		int xx = (int) (0.5 * dXsize) - (int) (0.5 * textWidth);
		int yy = (int) (0.5 * dYsize) - (int) (0.5 * dYsize) + 3;

                 // System.out.println(sname);

		if (rotation == 90 || rotation == -90) {
			// int irot=(int)(textHeight);

			// xx = (int) (textHeight);
			xx = (int) (0.5 * dXsize) - (int) (0.5 * textWidth)
					- (int) (textHeight);
			// xx = 10;
			yy = (int) (0.5 * dYsize) - (int) (0.5 * dYsize) + 3;

			this.selection = new Rectangle(xx, yy, Math.min(textWidth, 5), Math
					.min(textHeight, 5));

		} else {

			this.selection = new Rectangle(xx, yy, Math.min(textWidth, 5), Math
					.min(textHeight, 5));

		}

		this.repaint();
	}

	/**
	 * Refresh and repaint
	 * 
	 */
	protected void refreshText() {
		this.repaint();
	}

	/**
	 * Set the font.
	 * 
	 * @param f
	 *            Font
	 */
	public void setGFont(Font f) {
		this.f = f;
		repaint();
	}

	/**
	 * Set the color on this panel.
	 * 
	 * @param c
	 *            Color of the panel
	 */
	public void setGColor(Color c) {
		this.c = c;
		repaint();
	}

	/**
	 * Get the color of this panel
	 * 
	 * @return Color
	 */
	public Color getGColor() {
		return this.c;
	}

	/**
	 * Get the font of the text
	 * 
	 * @return Font Font of the text
	 */
	public Font getGFont() {
		return f;
	}

	/**
	 * Get the color of background
	 * 
	 * @return Color background color
	 * 
	 */
	public Color getGBackground() {
		return bakColor;
	}

	/**
	 * Get the title of this panel
	 * 
	 * @return title of this panel
	 */
	public String getString() {
		return sname;
	}

	/**
	 * Show or not the blue borders during editing.
	 * 
	 * @param show
	 *            true if shown
	 */
	public void showBorder(boolean show) {
		if (show) {
			this.setBorder(BorderFactory.createLineBorder(borderColor, 1));
			recColor1 = borderColor;
			recColor = borderColor;
		} else {
			this.setBorder(BorderFactory.createEmptyBorder());
			recColor1 = getGBackground();
			recColor = recColor1;
		}
		this.updateUI();
	}

	/**
	 * Is boarder shown?
	 * 
	 * @return true if shown
	 */
	public boolean isBorder() {

		if (recColor == borderColor)
			return true;
		return false;

	}

	/**
	 * Show or not the blue borders during editing. 
	 * UI is not updated.
	 * 
	 * @param show
	 *            true if shown
	 */
	public void showBorderNoUpdate(boolean show) {
		if (show) {
			this.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
			recColor1 = Color.blue;
			recColor = Color.blue;
		} else {
			this.setBorder(BorderFactory.createEmptyBorder());
			recColor1 = getGBackground();
			recColor = recColor1;
		}
	}

	/**
	 * Set the text on this panel
	 * 
	 * @param sname
	 *            Text
	 */
	public void setString(String sname) {
		this.sname = sname;
		f = new Font("Lucida Sans", Font.BOLD, 20);
		setString(sname, f, Color.black,this.getSize());

	}



        public void setString(String sname, Font f, Color c) {
                setString(sname, f, c,this.getSize());
        }

	/**
	 * Get the font on this panel
	 * 
	 * @return Font
	 * 
	 */
	public Font getMFont() {
		return f;

	}

	/**
	 * Get the text color
	 * 
	 * @return color margin color
	 * 
	 */
	public Color getMColor() {
		return c;

	}

	private void setSelectionBounds(Rectangle rect) {
		this.selection = rect;
		this.repaint();
	}

	/**
	 * Set the background of this panel
	 * 
	 * @param c
	 *            Color of this panel
	 */
	public void setGBackground(Color c) {
		bakColor = c;
		this.repaint();
	}

	/**
	 * Set the background for rectangle with the text
	 * 
	 * @param c
	 *            Color of the rectangle
	 */
	public void setGRecBackground(Color c) {

		// remove me
		recColor1 = c;
		recColor = c;
		this.repaint();
	}

	/**
	 * Return JPanel of this margin
	 * 
	 * @return JPanel this panel
	 */
	public JPanel getJPanel() {

		return (JPanel) this;
	}

	/**
	 * Set size of this margin panel
	 * 
	 * @param x
	 *            size in X
	 * @param y
	 *            size in Y
	 */

	public void setSizeMargin(int x, int y) {
		dXsize = x;
		dYsize = y;
		this.setPreferredSize(new Dimension(x, y));
		this.setMinimumSize(new Dimension(5, 5));
	}

	/**
	 * Get the size of this margin panel in X
	 * 
	 * @return Size of the panel in X
	 */
	public int getSizeX() {

		// dXsize = this.getSize().width;
		return dXsize;
	}

	/**
	 * Get the size of this margin in Y
	 * 
	 * @return size in Y
	 */

	public int getSizeY() {

		// dYsize = this.getSize().height;
		return dYsize;

	}

	/**
	 * Get the text position in X relative to widths
	 * 
	 * @return position of text in X (from 0 to 1)
	 */

	public double getStringPositionX() {
		return (double) selection.x / getSizeX();
	}

	/**
	 * Get the text position in Y relative to 
	 * the frame height.
	 * 
	 * @return position of text in Y (from 0 to 1)
	 */

	public double getStringPositionY() {
		return (double) selection.y / getSizeY();

	}

	/**
	 * Set the text position in X relative to the frame width
	 * 
	 * @param x
	 *            position of text in X (from 0 to 1)
	 */

	public void setStringPositionX(double x) {

		selection.x = (int) (x * getSizeX());
		// if (selection.x> getSizeX()) selection.x=getSizeX();
		// if (x<0) selection.x=0;

		this.repaint();
	}

	/**
	 * Set the text position in Y relative to the frame 
	 * height.
	 * 
	 * @param y
	 *            position of text in Y (from 0 to 1)
	 */

	public void setStringPositionY(double y) {
		selection.y = (int) (y * getSizeY());
		// if (selection.y> getSizeX()) selection.y=getSizeY();
		// if (y<0) selection.y=0;

		this.repaint();
	}

	/**
	 * Paint this panel
	 * 
	 * @param gr
	 */
	protected void paintComponent(Graphics gr) {
		Dimension size = this.getSize();

		// int dY = getSize().height;
		// int dX = getSize().width;
                // System.out.println(dX);
		// int midY = dY/2;
		// int midX = dX/2;

                if (gr== null) return;


		VectorGraphics g2d = VectorGraphics.create(gr);
		g2d.setColor(bakColor);
		g2d.fillRect(0, 0, size.width, size.height);

		if (f != null && sname != null && this.selection != null) {
			g2d.setFont(f);
			fm = getFontMetrics(f);
			String tmp = Translate.shrink(sname);
			textWidth = fm.stringWidth(tmp);
			textHeight = fm.getHeight();

			if (this.selection.width < textWidth)
				this.selection.width = textWidth;
			if (this.selection.height < textHeight)
				this.selection.height = textHeight;

			g2d.setColor(recColor);

			double rota = rotation * Math.PI / 180.0;
			// g2d.rotate((double)rotation, this.selection.x, this.selection.y);
			// g2d.drawRect(this.selection.x, this.selection.y,
			// this.selection.width, this.selection.height);

			// shift for rotation
			int irot = 0;

			if (rotation == 0) {
				g2d.drawRect(this.selection.x, this.selection.y,
						this.selection.width, this.selection.height);

			}
			if (rotation == 90 || rotation == -90) {
				// / g2d.drawRect(this.selection.x+irot, this.selection.y,
				// this.selection.height, this.selection.width);
				irot = (int) (0.7 * this.selection.width);
				g2d.drawRect(this.selection.x + irot, this.selection.y,
						this.selection.height, this.selection.width);

			}

			g2d.setColor(Color.black);
			// gr.drawString(sname, this.selection.x,
			// this.selection.y +(int)(0.5*textHeight) );
			String stext = Translate.decode(sname);
			RTextLine text = new RTextLine();
			text.setColor(c);
			text.setText(stext);
			text.setFont(f);

			g2d.rotate(rota, this.selection.x + irot, this.selection.y);
			// text.setRotation( rotation );

			// System.out.println(text.getRotation());
			if (rotation == 0) {
				text.draw(g2d, this.selection.x + 2, this.selection.y
						+ (int) textHeight - 2);
			} else if (rotation == 90) {
				text.draw(g2d, this.selection.x + irot, this.selection.y
						- (int) (0.4 * textHeight));
			} else if (rotation == -90) {
				text.draw(g2d, this.selection.x + irot - textWidth,
						this.selection.y + (int) (textHeight));
			} else {

				text.draw(g2d, this.selection.x - this.selection.height,
						this.selection.y);

				// + (int) (1.0 * textHeight) - 2);
			}

			// g2d.rotate(90.0);
			// g2d.drawString("text",this.selection.x + 2, this.selection.y
			// + (int) (1.0 * textHeight) - 2);
			g2d.rotate(-1 * rota, this.selection.x + irot, this.selection.y);

		}


	}

	/*
	 * public static void main(String[] args) { JFrame frm=new
	 * JFrame("ImageMoveResizePanel test");
	 * frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 * 
	 * GTitle imrp=new GTitle(); // Here is the path to the image
	 * imrp.setString("test"); imrp.setSelectionBounds(new Rectangle(0, 0, 400,
	 * 300));
	 * 
	 * frm.getContentPane().add(imrp, BorderLayout.CENTER); frm.setBounds(0, 0,
	 * 800, 600); frm.setVisible(true); }
	 * 
	 */

	private class SelectionChangeMouseListener implements MouseMotionListener,
			MouseListener, Serializable  {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected int frameWidth = 5;

		protected int minSize = 5;

		protected int startDragX, startDragY;

		protected Rectangle startDragRect;

		protected boolean resizeLeft, resizeTop, resizeRight, resizeBottom,
				move;

		/**
		 * Invoked when a mouse button is pressed on a component and then
		 * dragged. <code>MOUSE_DRAGGED</code> events will continue to be
		 * delivered to the component where the drag originated until the mouse
		 * button is released (regardless of whether the mouse position is
		 * within the bounds of the component).
		 * <p>
		 * Due to platform-dependent Drag&Drop implementations,
		 * <code>MOUSE_DRAGGED</code> events may not be delivered during a
		 * native Drag&Drop operation.
		 */
		public void mouseDragged(MouseEvent e) {
			if (startDragRect != null) {
				int x = e.getX();
				int y = e.getY();

				int diffX = startDragX - x;
				int diffY = startDragY - y;

				int newX = startDragRect.x;
				int newY = startDragRect.y;
				int newW = startDragRect.width;
				int newH = startDragRect.height;

				// this is necessary if you want to resize the box. You do not
				// need this. S.Chekanov
				/*
				 * 
				 * if (resizeLeft) { newX = newX - diffX; newW = newW + diffX; }
				 * if (resizeTop) { newY = newY - diffY; newH = newH + diffY; }
				 * if (resizeRight) { newW = newW - diffX; } if (resizeBottom) {
				 * newH = newH - diffY; }
				 */

				if (move) {
					newX = newX - diffX;
					newY = newY - diffY;
				}
				if (newW > minSize && newH > minSize) {
					setSelectionBounds(new Rectangle(newX, newY, newW, newH));

				}
			}
		}

		/**
		 * Invoked when the mouse cursor has been moved onto a component but no
		 * buttons have been pushed.
		 */
		public void mouseMoved(MouseEvent e) {

			insideBox = false;

			if (selection != null) {
				int x = e.getX();
				int y = e.getY();

				int selX = selection.x;
				int selY = selection.y;
				int selW = selection.width;
				int selH = selection.height;

				if (rotation == 90 || rotation == -90) {
					int irot = (int) (0.7 * selection.width);
					selX = selection.x + irot;
					selW = selection.height;
					selH = selection.width;
				}

				Rectangle inside = new Rectangle(selX, selY, selW, selH);

				Rectangle leftFrame = new Rectangle(selX, selY, frameWidth,
						selH);
				Rectangle topFrame = new Rectangle(selX, selY, selW, frameWidth);
				Rectangle rightFrame = new Rectangle(selX + selW - frameWidth,
						selY, frameWidth, selH);
				Rectangle bottomFrame = new Rectangle(selX, selY + selH
						- frameWidth, selW, frameWidth);

				boolean isInside = inside.contains(x, y);
				boolean isLeft = leftFrame.contains(x, y);
				boolean isTop = topFrame.contains(x, y);
				boolean isRight = rightFrame.contains(x, y);
				boolean isBottom = bottomFrame.contains(x, y);

				if (isLeft && isTop) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				} else if (isTop && isRight) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				} else if (isRight && isBottom) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				} else if (isBottom && isLeft) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				} else if (isLeft) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				} else if (isTop) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				} else if (isRight) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				} else if (isBottom) {
					insideBox = true;
					setCursor(Cursor
							.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				}

				else if (isInside) {
					insideBox = true;
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				} else {
					insideBox = false;
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			} else {
				insideBox = false;
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		/**
		 * Invoked when the mouse button has been clicked (pressed and released)
		 * on a component.
		 */
		public void mouseClicked(MouseEvent e) {

			if (selection != null) {
				int x = e.getX();
				int y = e.getY();
				insideBox = true;
				int selX = selection.x;
				int selY = selection.y;
				int selW = selection.width;
				int selH = selection.height;

				if (rotation == 90 || rotation == -90) {
					int irot = (int) (0.7 * selection.width);
					selX = selection.x + irot;
					selW = selection.height;
					selH = selection.width;
				}

				if (e.getClickCount() == 2) {

					if (x > selX && x < selX + selW && y > selY
							&& y < selY + selH) {

					}

				}
			}

			// recColor=Color.black;
			// repaint();

		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		public void mousePressed(MouseEvent e) {

			// only when the mouse outside the box
			if (insideBox == false) {

				if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
					if (e.getClickCount() == 1) {

						// this does not work for windows
						// if (e.isPopupTrigger()) {
						menu.show(e.getComponent(), e.getX(), e.getY());
						return;
					}
				}
			}

			if (selection != null) {
				int x = e.getX();
				int y = e.getY();

				int selX = selection.x;
				int selY = selection.y;
				int selW = selection.width;
				int selH = selection.height;

				if (rotation == 90 || rotation == -90) {
					int irot = (int) (0.7 * selection.width);
					selX = selection.x + irot;
					selW = selection.height;
					selH = selection.width;
				}

				Rectangle inside = new Rectangle(selX, selY, selW, selH);

				Rectangle leftFrame = new Rectangle(selX, selY, frameWidth,
						selH);
				Rectangle topFrame = new Rectangle(selX, selY, selW, frameWidth);
				Rectangle rightFrame = new Rectangle(selX + selW - frameWidth,
						selY, frameWidth, selH);
				Rectangle bottomFrame = new Rectangle(selX, selY + selH
						- frameWidth, selW, frameWidth);

				boolean isInside = inside.contains(x, y);
				boolean isLeft = leftFrame.contains(x, y);
				boolean isTop = topFrame.contains(x, y);
				boolean isRight = rightFrame.contains(x, y);
				boolean isBottom = bottomFrame.contains(x, y);

				if (isLeft && isTop) {
					resizeLeft = true;
					resizeTop = true;
					resizeRight = false;
					resizeBottom = false;
					move = false;
					recColor = borderColor;
				} else if (isTop && isRight) {
					resizeLeft = false;
					resizeTop = true;
					resizeRight = true;
					resizeBottom = false;
					move = false;
					recColor = borderColor;
				} else if (isRight && isBottom) {
					resizeLeft = false;
					resizeTop = false;
					resizeRight = true;
					resizeBottom = true;
					move = false;
					recColor = borderColor;
				} else if (isBottom && isLeft) {
					resizeLeft = true;
					resizeTop = false;
					resizeRight = false;
					resizeBottom = true;
					move = false;
					recColor = borderColor;
				} else if (isLeft) {
					resizeLeft = true;
					resizeTop = false;
					resizeRight = false;
					resizeBottom = false;
					move = false;
					recColor = borderColor;
				} else if (isTop) {
					resizeLeft = false;
					resizeTop = true;
					resizeRight = false;
					resizeBottom = false;
					move = false;
					recColor = borderColor;
				} else if (isRight) {
					resizeLeft = false;
					resizeTop = false;
					resizeRight = true;
					resizeBottom = false;
					move = false;
					recColor = borderColor;
				} else if (isBottom) {
					resizeLeft = false;
					resizeTop = false;
					resizeRight = false;
					resizeBottom = true;
					move = false;
					recColor = borderColor;
				} else if (isInside) {
					resizeLeft = false;
					resizeTop = false;
					resizeRight = false;
					resizeBottom = false;
					move = true;
				} else {
					resizeLeft = false;
					resizeTop = false;
					resizeRight = false;
					resizeBottom = false;
					move = false;
					recColor = recColor1;
				}

				if (move)
					recColor = borderColor;

				this.startDragX = x;
				this.startDragY = y;
				this.startDragRect = (Rectangle) selection.clone();
			}
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 */
		public void mouseReleased(MouseEvent e) {
			recColor = recColor1;
			repaint();
		}

		/**
		 * Invoked when the mouse enters a component.
		 */
		public void mouseEntered(MouseEvent e) {

			JHPlot.showMessage(sinfo);

			// win.showMargineTitle(true);
		}

		/**
		 * Invoked when the mouse exits a component.
		 */
		public void mouseExited(MouseEvent e) {
			recColor = recColor1;
			JHPlot.showMessage(" ");
		}
	}

}
