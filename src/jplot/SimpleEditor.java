/*
 *    SimpleEditor  -- Originally developed for use by JChess.
 *    This program is free software; you can redistribute it and/or modify it under the terms
 *    of the GNU General Public License as published by the Free Software Foundation; either
 *    version 3 of the License, or any later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *    See the GNU General Public License for more details.
 *    Additional permission under GNU GPL version 3 section 7:
 *    
 *    If you have received this program as a library with written permission from the DataMelt team,
 *    you can link or combine this library with your non-GPL project to convey the resulting work.
 *    In this case, this library should be considered as released under the terms of
 *    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
 *    provided you include this license notice and a URL through which recipients can access the
 *    Corresponding Source.

 *
 */

package jplot;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.net.*;

import jplot.panels.PanelGridUI;

/**
 * The class shows a frame which enables to look at and eventually edit a file,
 * any type of file. Build in a JPanel, so someone can reuse the editor in
 * another class.
 */
public class SimpleEditor extends JPanel {

	private static final long serialVersionUID = 1L;
	private JEditorPane textArea;
	private File file;
	private JFrame frame;
	private String text;
	private JComboBox fonts, sizes;
	private int size, style;
	private Font currentFont = new Font("Monospaced", Font.PLAIN, 12);
	private Color currentColor = Color.black;
	private SmallToggleButton b_bold, b_italic;
	private JScrollPane scrollpane;
	private boolean textChanged;
	private JPanel thisPanel;
	private JPanel parent;
	private FindPanel findPanel;
	private boolean isEditable;

	private Dimension panelSize;

	private final DefaultHighlighter.DefaultHighlightPainter g;

	/**
	 * Principal constructor, builds the panel which includes the report as
	 * text.
	 * 
	 * @param parent
	 *            parent
	 * @param title
	 *            title of the editor
	 * @param isEditable
	 *            true if the editor is actually editable
	 * @param width
	 *            width in pixels of the panel
	 * @param height
	 *            height in pixels of the panel
	 */
	public SimpleEditor(JPanel parent, String title, boolean isEditable,
			int width, int height) {
		setLayout(new BorderLayout());
		setBorder(new EtchedBorder());
		size = 12;
		style = Font.PLAIN;
		thisPanel = this;
		this.parent = parent;
		this.isEditable = isEditable;
		panelSize = new Dimension(width, height);

		JPanel mainPanel = new JPanel(new BorderLayout());
		EmptyBorder eb = new EmptyBorder(2, 2, 2, 2);
		BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
		mainPanel.setBorder(new CompoundBorder(eb, bb));

		textArea = new JEditorPane();
		textArea.setEditable(isEditable);
		textArea.setFont(currentFont);
		scrollpane = new JScrollPane(textArea);
		mainPanel.add(scrollpane, BorderLayout.CENTER);

		JToolBar toolbar = new JToolBar();
		ImageIcon icon = getImageIcon(parent, "exit.jpg");
		Action action = new AbstractAction("Close", icon) {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		};
		addButton(toolbar, action, "Close this panel");

		icon = getImageIcon(parent, "Save24.gif");
		action = new AbstractAction("Save", icon) {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		};
		addButton(toolbar, action, "Save the current file");

		icon = getImageIcon(parent, "Print24.gif");
		action = new AbstractAction("Print", icon) {
			public void actionPerformed(ActionEvent e) {
				printText();
			}
		};
		addButton(toolbar, action, "Print the current content");
		toolbar.addSeparator();

		icon = getImageIcon(parent, "Refresh24.gif");
		action = new AbstractAction("Reload", icon) {
			public void actionPerformed(ActionEvent e) {
				// refresh(file);
			}
		};
		addButton(toolbar, action, "Reload the current file");

		icon = getImageIcon(parent, "Find24.gif");
		action = new AbstractAction("Find", icon) {
			public void actionPerformed(ActionEvent e) {
				find();
			}
		};
		addButton(toolbar, action, "Find a piece of text in this file");

		toolbar.addSeparator();
		fonts = new JComboBox(JPlot.fontNames);
		fonts.setMaximumSize(fonts.getPreferredSize());
		fonts.setEditable(true);
		fonts.setSelectedItem("Monospaced");
		fonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSelectedFont();
			}
		});
		toolbar.add(fonts);

		toolbar.addSeparator();
		sizes = new JComboBox(new String[] { "7", "8", "9", "10", "11", "12",
				"14", "16", "18", "20", "22", "26", "32" });
		sizes.setMaximumSize(sizes.getPreferredSize());
		sizes.setEditable(true);
		sizes.setSelectedItem("12");
		sizes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int fontSize = 0;
				try {
					fontSize = Integer.parseInt(sizes.getSelectedItem()
							.toString());
				} catch (NumberFormatException ex) {
					return;
				}
				size = fontSize;
				setSelectedFont();
			}
		});
		toolbar.add(sizes);

		toolbar.addSeparator();
		icon = getImageIcon(parent, "Bold16.gif");
		b_bold = new SmallToggleButton(false, icon, icon, "Bold font");
		b_bold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSelectedFont();
			}
		});
		toolbar.add(b_bold);
		b_bold.resetBorder();
		icon = getImageIcon(parent, "Italic16.gif");
		b_italic = new SmallToggleButton(false, icon, icon, "Italic font");
		b_italic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSelectedFont();
			}
		});
		toolbar.add(b_italic);
		b_italic.resetBorder();
		mainPanel.add(toolbar, BorderLayout.NORTH);

		add(makeMenuBar(), BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);

		textChanged = false;
		textArea.setSelectionColor(new Color(248, 248, 215));
		g = new DefaultHighlighter.DefaultHighlightPainter(new Color(248, 248,
				215));

		frame = new JFrame(title);
		frame.getContentPane().add(this);
		frame.pack();
	}

	/**
	 * Constructor, builds a simple editor/viewer with default dimensions.
	 * 
	 * @param parent
	 *            parent
	 * @param title
	 *            title of the editor
	 * @param isEditable
	 *            true if the editor is actually editable
	 */
	public SimpleEditor(JPanel parent, String title, boolean isEditable) {
		this(parent, title, isEditable, 640, 550);
	}

	/**
	 * Add a button of type SmallButton to the toolbar. Done here to bypass a
	 * bug in jdk 1.4 which resets the border of a button to some default value
	 * when added to the toolbar.
	 */
	private void addButton(JToolBar toolbar, Action action, String tip) {
		SmallButton sb = new SmallButton(action, tip);
		toolbar.add(sb);
		sb.resetBorder(); // needed to bypass a bug (since jdk1.4)
	}

	/*
	 * Returns an image which is found in a valid image URL. The basis of the
	 * url is where this class is created.
	 * 
	 * @param parent the parent class
	 * 
	 * @param name name of the image
	 * 
	 * @return an image or icon
	 */
	private ImageIcon getImageIcon(JPanel parent, String name) {
		ImageIcon im = null;
		try {
			URL imageURL = parent.getClass().getResource("/images/" + name);
			Toolkit tk = Toolkit.getDefaultToolkit();
			im = new ImageIcon(tk.createImage(imageURL));
		} catch (Exception e) {
			Utils.oops(frame, "Impossible to load the TextEditor's icon '"
					+ name + "'.\nSomething's wrong with the installation.");
		}
		return im;
	}

	/**
	 * Builds the menubar.
	 * 
	 * @return an instance of a JMenuBar class
	 */
	JMenuBar makeMenuBar() {
		JMenuItem mi;
		JMenuBar menuBar = new JMenuBar();

		// make the file-menu:
		JMenu fm = (JMenu) menuBar.add(new JMenu("File"));
		fm.setMnemonic('F');
		ImageIcon icon = getImageIcon(parent, "Open16.gif");
		mi = (JMenuItem) fm.add(new JMenuItem("Open...", icon));
		mi.setMnemonic('O');
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		icon = getImageIcon(parent, "Save16.gif");
		mi = (JMenuItem) fm.add(new JMenuItem("Save...", icon));
		mi.setMnemonic('S');
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		icon = getImageIcon(parent, "SaveAs16.gif");
		mi = (JMenuItem) fm.add(new JMenuItem("Save as...", icon));
		mi.setMnemonic('A');
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		fm.addSeparator();
		icon = getImageIcon(parent, "Print16.gif");
		mi = (JMenuItem) fm.add(new JMenuItem("Print", icon));
		mi.setMnemonic('P');
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printText();
			}
		});

		fm.addSeparator();
		mi = (JMenuItem) fm.add(new JMenuItem("Quit"));
		mi.setMnemonic('Q');
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// make the edit-menu:
		// --------------------
		JMenu edit = (JMenu) menuBar.add(new JMenu("Edit"));
		edit.setMnemonic('E');
		icon = getImageIcon(parent, "Refresh16.gif");
		mi = (JMenuItem) edit.add(new JMenuItem("Reload", icon));
		mi.setMnemonic('R');
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh(file);
			}
		});
		icon = getImageIcon(parent, "Find16.gif");
		mi = (JMenuItem) edit.add(new JMenuItem("Find", icon));
		mi.setMnemonic('F');
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				find();
			}
		});
		return menuBar;
	}

	/*
	 * Repaints the text area with the currently selected font.
	 */
	private void setSelectedFont() {
		if (b_bold.isSelected()) {
			if (b_italic.isSelected()) {
				style = Font.BOLD | Font.ITALIC;
			} else
				style = Font.BOLD;
		} else if (b_italic.isSelected())
			style = Font.ITALIC;
		else
			style = Font.PLAIN;
		currentFont = new Font((String) fonts.getSelectedItem(), style, size);
		textArea.setFont(currentFont);
		textArea.repaint();
	}

	/*
	 * Converts the content of the file to a string.
	 */
	private void fileToString() {
		StringBuffer sb = new StringBuffer();
		if (file.exists() && file.canRead()) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				String s;
				while ((s = in.readLine()) != null) {
					sb.append(s).append("\n");
				}
			} catch (IOException e) {
			}
		}
		text = sb.toString().trim();
	}

	/**
	 * Clears the current canvas
	 */
	public void clear() {
		try {
			Document d = textArea.getDocument();
			d.remove(0, d.getLength());
		} catch (BadLocationException e) {
		}
	}

	/**
	 * Refreshes the text, updates with the content of the current outputfile.
	 * 
	 * @param f
	 *            file containing the report
	 */
	public void refresh(File f) {
		// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		file = f;
		fileToString();
		refresh(text);
		// setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Refreshes the text, updates with the content of the current
	 * viewer/editor.
	 * 
	 * @param txt
	 *            text to display
	 */
	public void refresh(String txt) {
		if (txt.startsWith("<!DOC") || txt.startsWith("<!doc")
				|| txt.startsWith("<html>") || txt.startsWith("<HTML>")) {
			textArea.setContentType("text/html");
		} else
			textArea.setContentType("text/plain");
		textArea.setText(txt);
		if (isEditable)
			textArea.setCaretPosition(0);
		textArea.revalidate();
	}

	/**
	 * Saves the text after an edit to the current file.
	 */
	public void save() {
		if (file == null)
			return;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			pw.println(textArea.getText());
			pw.close();
		} catch (IOException e) {
			Utils.bummer(frame, "Impossible to write to " + file.toString());
		}
	}

	/**
	 * Saves the text after an edit to a new file.
	 */
	public void saveAs() {
		JFileChooser chooser = new JFileChooser(new File("."));
		javax.swing.filechooser.FileFilter ff = new DataFileFilter();
		chooser.addChoosableFileFilter(ff);
		chooser.setFileFilter(ff);
		if (chooser.showDialog(thisPanel, "Save As") == 0) {
			file = chooser.getSelectedFile();
			if (file.exists()) {
				int res = JOptionPane.showConfirmDialog(thisPanel,
						"The file exists: do you want to overwrite this file?",
						"", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.NO_OPTION)
					return;
			}
			save();
		}
	}

	private class DataFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File pathname) {
			return pathname.isDirectory()
					|| pathname.getName().endsWith(".res")
					|| pathname.getName().endsWith(".dat");
		}

		public String getDescription() {
			return "data files (*.res, *.dat)";
		}
	}

	/**
	 * Saves the text after an edit to a new file.
	 */
	public void openFile() {
		JFileChooser chooser = new JFileChooser(new File("."));
		javax.swing.filechooser.FileFilter ff = new DataFileFilter();
		chooser.addChoosableFileFilter(ff);
		chooser.setFileFilter(ff);
		if (chooser.showOpenDialog(thisPanel) == 0) {
			if (textChanged) {
				int result = JOptionPane
						.showConfirmDialog(thisPanel,
								"The file has been changed, shouldn't we save it first?");
				if (result == JOptionPane.CANCEL_OPTION)
					return;
				else if (result == JOptionPane.YES_OPTION)
					save();
			}
			refresh(chooser.getSelectedFile());
		}
	}

	/**
	 * Disposes the frame. Prefer hide(), but then, show doesn't work with my
	 * current jdk (it pops the frame up iconified).
	 */
	public void dispose() {
		clear();
		// frame.hide();
		frame.dispose();
	}

	/*
	 * Print a file. The problem is that this thing only works for a file, not
	 * for a text...
	 */
	private void printText() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Thread t = new Thread() {
			public void run() {
				PrinterJob job = PrinterJob.getPrinterJob();
				if (job != null) {
					PageFormat pf = new PageFormat();
					PrintTextPainter ptp = new PrintTextPainter(text);
					ptp.setFont(currentFont);
					ptp.setForeground(currentColor);
					job.setPrintable(ptp, pf);
					job.setCopies(1);
					if (job.printDialog()) {
						try {
							job.print();
						} catch (Exception e) {
							System.out.println("Oops, error while printing...");
						}
					}
				}
			}
		};
		t.start();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/*
	 * Print a file. The problem is that this thing only works for a file, not
	 * for a text...
	 */
	private void printFile() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Thread t = new Thread() {
			public void run() {
				PrinterJob job = PrinterJob.getPrinterJob();
				if (job != null) {
					PageFormat pf = new PageFormat();
					// PageFormat pf = job.pageDialog(job.defaultPage());
					// Paper paper = new Paper();
					// paper.setSize(597,844); // should be an A4
					// pf.setPaper(paper);
					PrintFilePainter pfp = new PrintFilePainter(file.toString());
					pfp.setFont(currentFont);
					pfp.setForeground(currentColor);
					job.setPrintable(pfp, pf);
					job.setCopies(1);
					if (job.printDialog()) {
						try {
							job.print();
						} catch (Exception e) {
						}
					}
				}
			}
		};
		t.start();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * @return the preferred size of this panel.
	 */
	public Dimension getPreferredSize() {
		return panelSize;
	}

	/**
	 * Pops up a frame with the report printed in it.
	 * 
	 * @param x
	 *            x-position of the upper-left corner of the frame.
	 * @param y
	 *            y-position of the upper-left corner of the frame.
	 * @param f
	 *            file containing the report
	 */
	public void show(int x, int y, File f) {
		refresh(f);
		frame.setLocation(x, y);
		frame.setState(Frame.NORMAL);
		frame.setVisible(true);
	}

	/**
	 * Pops up a frame with the report printed in it.
	 * 
	 * @param x
	 *            x-position of the upper-left corner of the frame.
	 * @param y
	 *            y-position of the upper-left corner of the frame.
	 * @param t
	 *            string with text to display
	 */
	public void show(int x, int y, String t) {
		refresh(t);
		frame.setLocation(x, y);
		frame.setState(Frame.NORMAL);
		frame.setVisible(true);
	}

	/*
	 * Pops up a find dialog.
	 */
	private void find() {
		if (findPanel == null)
			findPanel = new FindPanel();
		findPanel.show(frame, 100, 100);
	}

	/*
	 * Panel with all the widgets needed to find a string. Builds a dialog which
	 * pops up, containing this panel.
	 */
	class FindPanel extends PanelGridUI {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;
		private JDialog dialog;
		private JTextField textField;
		private int startIndex = 0;
		private int findIndex;
		private final Dimension preferredSize = new Dimension(260, 80);

		public FindPanel() {
			JLabel label = new JLabel("Search:");
			label.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			addComponent(label, 1, 1);
			textField = new JTextField();
			textField.setColumns(15);
			addFilledComponent(textField, 1, 2);
		}

		/**
		 * @return the preferred size of this panel.
		 */
		public Dimension getPreferredSize() {
			return preferredSize;
		}

		/*
		 * Finds a string in the text. Scrolls to that position and assures that
		 * the string is visible. Wouldn't it be nice if the string was
		 * high-lighted as well...
		 * 
		 * @param word piece of text which is searched for
		 * 
		 * @return true if the word was found
		 */
		private boolean find(String word) {
			findIndex = -1;
			int wordLen = word.length();
			int N = text.length() - wordLen;
			for (int i = startIndex; i < N; i++) {
				String s = text.substring(i, i + wordLen);
				if (s.equals(word)) {
					findIndex = i;
					try {
						textArea.getHighlighter().removeAllHighlights();
						textArea.getHighlighter().addHighlight(i, i + wordLen,
								g);
					} catch (BadLocationException e) {
					}
					break;
				}
			}
			return (findIndex > -1) ? true : false;
		}

		/*
		 * Performs the actual search. Asks to rewind the file if it hits the
		 * bottom without finding the string.
		 */
		private void findAction() {
			int i = 0;
			if (!textField.getText().equals("")) {
				startIndex = textArea.getCaretPosition();
				boolean tryFind = true;
				while (tryFind) {
					if (!find(textField.getText())) {
						int result = JOptionPane
								.showConfirmDialog(
										frame,
										"Reaching the end of the text,\nrestart the scan from the top?",
										"Search failed",
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.INFORMATION_MESSAGE);
						if (result == JOptionPane.CANCEL_OPTION)
							tryFind = false;
						else
							startIndex = 0;
					} else
						tryFind = false;
					if (i++ > 0)
						break;
				}
				if (findIndex != -1) {
					// System.out.println("Found '" + textField.getText() +
					// "' at position " + findIndex);
					textArea.setCaretPosition(++findIndex);
				}
			}
		}

		/**
		 * Pops up a modal frame including the panel.
		 * 
		 * @param parent
		 *            parent frame
		 * @param x
		 *            x-position of the dialog
		 * @param y
		 *            y-position of the dialog
		 */
		public void show(Frame parent, int x, int y) {
			if (dialog == null) {
				JPanel panel = new JPanel(new BorderLayout());
				dialog = new JDialog(parent, "Find", true);
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						textArea.getHighlighter().removeAllHighlights();
						dialog.dispose();
					}
				});
				JPanel p = new JPanel(new FlowLayout());
				p.setBorder(BorderFactory.createEtchedBorder());
				JButton b = new JButton("Find");
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						findAction();
					}
				});
				p.add(b);
				b = new JButton("Quit");
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						textArea.getHighlighter().removeAllHighlights();
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

			dialog.setVisible(true); // blocks until user brings dialog down.
		}
	}
}
