package jhplot.root;

import hep.io.root.*;
import hep.io.root.daemon.RootAuthenticator;
import hep.io.root.daemon.RootURLStreamFactory;
import hep.io.root.interfaces.*;
import hep.io.root.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import jas.hist.*;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple application for browsing histograms in Root Files
 * 
 * @author: Tony Johnson  and   S.Chekanov
 */
public class RootHistogramBrowser extends JPanel implements
		TreeSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static String aboutMessage = "<HTML>RootHistogramBrowser $Id: RootHistogramBrowser.java,v 1.2 2004/06/28 15:21:26 tonyj Exp $<br>Author: Tony Johnson (tonyj@slac.stanford.edu)";

	private final static TreeModel emptyTree = null;

	private JASHist plot;

	private JTree tree;
	
//	private JFrame frame;

	/**
	 * Histogram browser
	 * 
	 * @throws IOException
	 */

	public RootHistogramBrowser() {
		super(new BorderLayout());
	
		try {
		tree = new JTree(emptyTree);
		tree.setCellRenderer(new RootDirectoryTreeCellRenderer());
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(false);

		plot = new JASHist();
		plot.setBackground(Color.white);
		plot.setShowStatistics(true);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(tree), plot);
		add(split, BorderLayout.CENTER);
		add(new RootMenuBar(), BorderLayout.NORTH);

		setPreferredSize(new java.awt.Dimension(500, 300));
		split.setDividerLocation(245);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		}

	public void setRootFile(File file) throws IOException {
		Cursor old = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			RootFileReader reader = new RootFileReader(file);
			TreeModel model = new RootHistogramTreeModel(reader) {
				public void handleException(IOException x) {
					error(x);
					x.printStackTrace();
					super.handleException(x);
				}
			};
			tree.setModel(model);
			tree.setRowHeight(20);
			tree.setLargeModel(true);
		} finally {
			setCursor(old);
		}
	}

	public void setRootFile(URL url) throws IOException {
		Cursor old = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			RootFileReader reader = new RootFileReader(url);
			TreeModel model = new RootHistogramTreeModel(reader) {
				public void handleException(IOException x) {
					error(x);
					x.printStackTrace();
					super.handleException(x);
				}
			};
			tree.setModel(model);
			tree.setRowHeight(20);
			tree.setLargeModel(true);
		} finally {
			setCursor(old);
		}
	}

	public static void main(String[] argv) throws IOException {
		if (argv.length > 1)
			usage();
		if (argv.length == 1 && argv[0].startsWith("-"))
			usage();

		URL.setURLStreamHandlerFactory(new RootURLStreamFactory());

		JFrame frame = new JFrame("Root Histogram Browser");
		RootHistogramBrowser browser = new RootHistogramBrowser();

		Authenticator.setDefault(new RootAuthenticator(browser));
		URLConnection.setDefaultAllowUserInteraction(true);

		if (argv.length == 1) {
			if (argv[0].startsWith("root:"))
				browser.setRootFile(new URL(argv[0]));
			else
				browser.setRootFile(new File(argv[0]));
		}
		frame.setContentPane(browser);
		// Make this exit when the close button is clicked.
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
	}

	private static void usage() {
		System.out.println("java RootHistogramBrowser [<file>]");
		System.exit(0);
	}

	public void valueChanged(TreeSelectionEvent event) {
		Cursor old = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			plot.removeAllData();
			TreePath path = tree.getSelectionPath();
			if (path != null) {
				TKey key = (TKey) path.getLastPathComponent();
				Object node = key.getObject();
				if (node instanceof TH1) {
					DataSource ds = RootHistogramAdapter.create((TH1) node);
					if (ds != null) {
						plot.setTitle(ds.getTitle());
						plot.addData(ds).show(true);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
			error(x);
		} catch (RootClassNotFound x) {
			x.printStackTrace();
			error(x);
		} finally {
			setCursor(old);
		}
	}

	private void error(Exception x) {
		JOptionPane.showMessageDialog(this, x.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private class RootMenuBar extends JMenuBar {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		RootMenuBar() {
			add(new RootFileMenu());
			add(new RootHelpMenu());
		}
	}

	private class RootFileMenu extends JMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		RootFileMenu() {
			super("File");
			add(new JMenuItem("Open File...") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void fireActionPerformed(ActionEvent e) {
					JFileChooser dlg = null;
					// HACK to work around JavaWebStart bug.
					try {
						if (System.getSecurityManager() != null
								&& System.getProperty("os.name").indexOf(
										"indows") > 0
								&& System.getProperty("java.version")
										.startsWith("1.3")) {
							dlg = new JFileChooser("Open Root File...");
						}
					} catch (SecurityException x) {
					}
					if (dlg == null)
						dlg = new JFileChooser("Open Root File...");
					dlg.setFileFilter(new RootFileFilter());
					int rc = dlg.showOpenDialog(RootHistogramBrowser.this);
					if (rc == JFileChooser.APPROVE_OPTION) {
						try {
							setRootFile(dlg.getSelectedFile());
						} catch (IOException x) {
							error(x);
						}
					}
				}
			});
			add(new JMenuItem("Open URL...") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void fireActionPerformed(ActionEvent e) {
					String url = JOptionPane.showInputDialog(
							RootHistogramBrowser.this, "URL", "root://");
					if (url != null) {
						try {
							setRootFile(new URL(url));
						} catch (IOException x) {
							error(x);
						}
					}
				}
			});
			add(new JMenuItem("Exit") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void fireActionPerformed(ActionEvent e) {
				 //  System.exit(0);
		               setVisible(false);
				}
			});
		}
	}

	private class RootHelpMenu extends JMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		RootHelpMenu() {
			super("Help");
			add(new JMenuItem("About...") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void fireActionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(RootHistogramBrowser.this,
							aboutMessage);
				}
			});
		}
	}

	private static class RootFileFilter extends
			javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().endsWith(".root");
		}

		public String getDescription() {
			return "Root Files (*.root)";
		}
	}
}
