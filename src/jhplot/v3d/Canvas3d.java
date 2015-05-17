// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 

package jhplot.v3d;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Canvas3d extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Model3d md;

	Image backBuffer;

	Graphics backGC;

	Dimension prefSize, minSize;

	boolean painted;

	private Color c;

	private SelectionChangeMouseListener scml;

	protected JPopupMenu menu;

	public Matrix3d orot, otrans, oscale;

	public float ominScale, omaxScale;

	public Canvas3d(Model3d md) {

		c = Color.white;
		if (md == null)
			return;
		this.md = md;
		prefSize = new Dimension(md.width, md.height);
		minSize = new Dimension(md.width / 2, md.height / 2);

		menu = new JPopupMenu();
		scml = new SelectionChangeMouseListener();
		addMouseListener(scml);
		addMouseMotionListener(scml);

		orot = new Matrix3d(md.rot);
		otrans = new Matrix3d(md.trans);
		oscale = new Matrix3d(md.scale);
		ominScale = md.minScale;
		omaxScale = md.maxScale;

		// this is necessary if you want to mix AWT with swing
		// JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// pop-up menu for table
		JMenuItem iedit = new JMenuItem("Edit ");
		menu.add(iedit);

		final Canvas3d cic = this;
		final Model3d mdm = md;
		iedit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Editor3d(cic, mdm);

			}
		});

	}

	public Dimension minimumSize() {
		return minSize;
	}

	public Dimension preferredSize() {
		return prefSize;
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		// backGC.setColor(getBackground());
		backGC.setColor(c);
		backGC.fillRect(0, 0, size().width, size().height);
		if (md != null)
			md.paint(backGC);
		g.drawImage(backBuffer, 0, 0, this);
		setPainted();
	}

	synchronized void setPainted() {
		painted = true;
		notifyAll();
	}

	public void setColorBack(Color c) {
		this.c = c;
	}

	public Color getColorBack() {
		return c;
	}

	synchronized void waitPainted() {
		while (!painted)
			try {
				wait();
			} catch (Exception e) {
			}
		painted = false;
	}

	public void reshape(int x, int y, int width, int height) {
		super.reshape(x, y, width, height);
		backBuffer = createImage(width, height);
		backGC = backBuffer.getGraphics();
		if (md != null)
			md.resize(width, height);
	}

	void releaseModel() {
		md = null;
	}

	private class SelectionChangeMouseListener implements MouseMotionListener,
			MouseListener {

		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			// System.out.println("drag");
		}

		/**
		 * Invoked when the mouse cursor has been moved onto a component but no
		 * buttons have been pushed.
		 */
		public void mouseMoved(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();

			/*
			 * String name = md.inside(x,y); if (name != null)
			 * md.app.showStatus(name); else md.app.showStatus(" ");
			 * 
			 */

		}

		/**
		 * Invoked when the mouse button has been clicked (pressed and released)
		 * on a component.
		 */
		public void mouseClicked(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();

			md.incTrans(md.bb.getWidth() / 10, 0, 0);

			Vector3d c = md.bb.getCenter();
			// c.print();

			// System.out.println(md.bb.getWidth());
			// System.out.println(md.bb.getHeight());

		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 */
		public void mouseReleased(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();
			// System.out.println("x:"+x);
			// System.out.println("y:"+y);
			// md.incTrans(x,y,0);
			// repaint();
		}

		/**
		 * Invoked when the mouse enters a component.
		 */
		public void mouseEntered(MouseEvent e) {

		}

		/**
		 * Invoked when the mouse exits a component.
		 */
		public void mouseExited(MouseEvent e) {

		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		public void mousePressed(MouseEvent e) {

			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
				if (e.getClickCount() == 1) {

					// this does not work for windows
					// if (e.isPopupTrigger()) {
					menu.show(e.getComponent(), e.getX(), e.getY());
					return;
				}
			}
		}

	} // end moth class

}
