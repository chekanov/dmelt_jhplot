package jplot3dp;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.Border;

import jplot3dp.Utils;

class Utils {

	Utils() {
	}

	static URL getURL(String s) {
		if (applet == null)
			return (Utils.class).getResource(s);
		else
			return applet.getClass().getResource(s);
	}

	static ImageIcon loadIcon(String s) {
		
		String pre = "/jplot3dp/images/";
		URL micon = Utils.class.getResource(pre + s);
		return new ImageIcon(micon);
		
		// return new ImageIcon(getURL("jplot3dp/images/" + s));
	}

	public static Color changeAlpha(Color color, int i) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), i);
	}

	public static Color blendColors(Color color, Color color1, double d) {
		if (color == null || color1 == null) {
			return null;
		} else {
			int i = (int) ((double) color1.getRed() * d + (double) color
					.getRed()
					* (1.0D - d));
			int j = (int) ((double) color1.getGreen() * d + (double) color
					.getGreen()
					* (1.0D - d));
			int k = (int) ((double) color1.getBlue() * d + (double) color
					.getBlue()
					* (1.0D - d));
			int l = color.getAlpha();
			return new Color(i, j, k, l);
		}
	}

	public static Container labeledComponent(String s, JComponent jcomponent,
			boolean flag) {
		Box box = new Box(flag ? 0 : 1);
		box.add(new JLabel(s));
		jcomponent.setAlignmentX(0.0F);
		box.add(jcomponent);
		return box;
	}

	public static void makeHot(final JComponent comp) {
		final Border borderE = BorderFactory.createEmptyBorder(1, 2, 1, 2);
		class _cls1Border3D implements Border {

			public boolean isBorderOpaque() {
				return false;
			}

			public void paintBorder(Component component, Graphics g, int i,
					int j, int k, int l) {
				g.setColor(Color.WHITE);
				g.draw3DRect(i, j, k - 1, l - 1, m_raised);
			}

			public Insets getBorderInsets(Component component) {
				return new Insets(1, 2, 1, 2);
			}

			private boolean m_raised;

			public _cls1Border3D(boolean flag) {
				m_raised = flag;
			}
		}

		final _cls1Border3D borderR = new _cls1Border3D(true);
		final _cls1Border3D borderL = new _cls1Border3D(false);
		comp.setBorder(borderE);
		comp.addMouseListener(new MouseAdapter() {

			private boolean isEnabled() {
				if (comp instanceof JButton)
					return ((JButton) comp).isEnabled();
				else
					return true;
			}

			public void mouseEntered(MouseEvent mouseevent) {
				if (isEnabled())
					comp
							.setBorder((mouseevent.getModifiersEx() & 0x400) != 0 ? borderL
									: borderR);
			}

			public void mouseExited(MouseEvent mouseevent) {
				comp.setBorder(borderE);
			}

			public void mouseReleased(MouseEvent mouseevent) {
				if (!isEnabled())
					comp.setBorder(borderE);
				else if (mouseevent.getButton() == 1
						&& !comp.getBorder().equals(borderE))
					comp.setBorder(borderR);
			}

			public void mousePressed(MouseEvent mouseevent) {
				if (isEnabled() && mouseevent.getButton() == 1)
					comp.setBorder(borderL);
			}

		});
	}

	public static void writeColor(DataOutputStream dataoutputstream, Color color)
			throws IOException {
		dataoutputstream.writeInt(color.getRed());
		dataoutputstream.writeInt(color.getGreen());
		dataoutputstream.writeInt(color.getBlue());
		dataoutputstream.writeInt(color.getAlpha());
	}

	public static Color readColor(DataInputStream datainputstream)
			throws IOException {
		return new Color(datainputstream.readInt(), datainputstream.readInt(),
				datainputstream.readInt(), datainputstream.readInt());
	}

	public static Applet applet = null;

}
