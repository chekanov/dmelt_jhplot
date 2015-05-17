package jhplot.io;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import jhplot.*;
import jhplot.gui.GHFrame;
import hep.aida.*;
import hep.aida.ref.histogram.*;
import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.interfaces.TH1;
import hep.io.root.interfaces.TH2;
import hep.io.root.interfaces.TKey;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 * 
 * A GUI to browser AIDA data stored in .aida or .xml files.
 * 
 * @author S.Chekanov
 * 
 */
public class BrowserAida extends BrowserDataGeneric {

	static Random random = new Random();
	private static final long serialVersionUID = 1L;
	boolean take = true;
	private Map<String, Object> map;
	protected JTree m_tree;
	protected JScrollPane scroll;
	public JButton closeButton;
	private JButton runButton;
	private JDialog jdialog;
	private GHFrame gframe;
	private JCheckBox cb; // A checkbox.
	private IAnalysisFactory af;
	private ITree itree;
	private TreeModel model;
	private final static TreeModel emptyTree = null;

	public BrowserAida() {
		super();
	}

	/**
	 * Show the BrowserAida.
	 * 
	 * @param h
	 *            Frame.
	 * @param hfile
	 *            file
	 * @param ishow
	 *            true if shown
	 */
	public BrowserAida(final GHFrame h, String hfile, boolean ishow) {
		super();
		// System.out.println("Start Aida browser");

		setFile(h, hfile, ishow);

	}

	/**
	 * Read data from AIDA file.
	 * 
	 * @param frame
	 * @param filepath
	 * @param ishow
	 */
	public void setFile(GHFrame frame, String hb, boolean ishow) {

		map = new TreeMap<String, Object>();
		gframe = frame;

	 	File file = null;
		 m_tree = new JTree(emptyTree);
         m_tree.setRootVisible(false);
         m_tree.getSelectionModel().setSelectionMode(
                         TreeSelectionModel.SINGLE_TREE_SELECTION);
         m_tree.setShowsRootHandles(false);
         m_tree.setEditable(false);

		try {
			file = new File(hb);
			IAnalysisFactory af = IAnalysisFactory.create();
			itree = af.createTreeFactory().create(file.getAbsolutePath(),"xml");
			//if (hb.endsWith(".xml"))
			//	itree = af.createTreeFactory().create(file.getAbsolutePath(),"xml");
			//if (hb.endsWith(".aida"))
			//	itree = af.createTreeFactory().create(file.getAbsolutePath(),"aida");
			
			
			model = new DefaultTreeModel(getModel(itree));
            m_tree.setModel(model);
            m_tree.setCellRenderer(new AidaDirectoryTreeCellRenderer());
            m_tree.setRowHeight(20);
            m_tree.setLargeModel(true);

			
		} catch (IOException x) {
			jhplot.utils.Util.ErrorMessage("Error when opening the file:" + hb);
		}

		
		
/*
		// Set the component to show the popup menu
		m_tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// if (evt.isPopupTrigger()) {
				if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {

					// m_popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
*/
		
		makeDialog();

	}

	/**
	 * Make a dialog
	 * 
	 */

	public void makeDialog() {

		JFrame frame = gframe.getFrame();
		jdialog = new JDialog();

		cb = new JCheckBox("Overlay");

		jdialog.setTitle("Objects");
		jdialog.setResizable(true);

		

		// LocationSize(parent);
		jdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JPanel lowerPanel = new JPanel();
		JPanel mainPanel = new JPanel(new BorderLayout());
		cb = new JCheckBox("Overlay");
		scroll = new JScrollPane();
		scroll.getViewport().add(m_tree);
		mainPanel.add(scroll, BorderLayout.CENTER);

		closeButton = new JButton();
		closeButton.setText("Exit");

		runButton = new JButton();
		runButton.setText("Plot");

		// actions
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jdialog.setVisible(false);
				jdialog.dispose();
				// frame = null;
				// showIt(false);

			}
		});

		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				plotObject();

			}
		});

		lowerPanel.add(runButton, null);
		lowerPanel.add(cb, null);
		lowerPanel.add(closeButton, null);
		jdialog.getContentPane().add(lowerPanel, java.awt.BorderLayout.SOUTH);
		jdialog.getContentPane().add(mainPanel, java.awt.BorderLayout.NORTH);
		// store mainframe dimensions
		Dimension dim = frame.getSize();
		int w = dim.width;
		int h = dim.height;
		// Get the system resolution
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();

		// make sure the dialog is not too big
		Dimension size = new Dimension(Math.min((int) (0.5 * w), res.width),
				Math.min((int) (1.0 * h), res.height));
		jdialog.setSize(size);

		// pack();
		jhplot.utils.Util.rightWithin(frame,jdialog);

		jdialog.setVisible(true);

	}

	/**
	 * Plot the object
	 */
	public void plotObject() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree
				.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		OidNode iod = (OidNode) nodeInfo;

		Color randomColor = Color.getHSBColor(random.nextFloat(), 1.0F, 1.0F);

		HPlot frame = (HPlot) gframe;

		if (cb.isSelected() == false)
			frame.clearAll();

		frame.setAutoRange();

		if (iod.getType().equals("IDataPointSet")) {

			IDataPointSet p1d = (IDataPointSet) itree.find(iod.getName());
			// System.out.println(p1d.dimension());
			P1D p1 = new P1D(p1d);

			p1.setErrAll(true);
			int symbol = (int) (5 * random.nextFloat());
			if (symbol <= 0)
				symbol = 1;
			p1.setSymbol(symbol);
			p1.setSymbolSize(4);
			p1.setStyle("p");
			p1.setPenWidth(3);

			p1.setColor(randomColor);
			frame.draw(p1);

		}
		if (iod.getType().equals("IHistogram1D")) {
			// System.out.println("Found TH1="+((TH1)node).getTitle());
			Histogram1D hh = (Histogram1D) itree.find(iod.getName());
			H1D h1 = new H1D(hh);
			h1.setFill(true);
			h1.setStyle("h");
			h1.setPenWidthErr(1);
			h1.setPenWidth(1);
			h1.setFillColorTransparency(0.6);

			h1.setFillColor(randomColor);
			h1.setColor(Color.black);

			frame.draw(h1);

		} else if (iod.getType().equals("IHistogram2D")) {

			Histogram2D hh = (Histogram2D) itree.find(iod.getName());
			H2D h2s = new H2D(hh);
			// frame.setContour(true);
			// frame.setContourLevels(15);

		} else if (iod.getType().equals("ICloud1D")) {
			// System.out.println("Reading Cloud1D");
			Cloud1D hh = (Cloud1D) itree.find(iod.getName());
			H1D h1 = new H1D(hh, 100);

			h1.setFill(true);
			h1.setStyle("h");
			h1.setPenWidthErr(1);
			h1.setPenWidth(1);
			h1.setFillColorTransparency(0.6);

			h1.setFillColor(randomColor);
			h1.setColor(Color.black);

			frame.draw(h1);
		} else if (iod.getType().equals("ICloud2D")) {
			// System.out.println("Reading Cloud2D");
			Cloud2D c2d = (Cloud2D) itree.find(iod.getName());
			// System.out.println(p1d.dimension());
			P1D p1 = new P1D(c2d);

			p1.setErrAll(true);
			int symbol = (int) (5 * random.nextFloat());
			if (symbol <= 0)
				symbol = 1;
			p1.setSymbol(symbol);
			p1.setSymbolSize(4);
			p1.setStyle("p");
			p1.setPenWidth(3);

			p1.setColor(randomColor);
			frame.draw(p1);
		}

	}

	/**
	 * Return a map with all objects
	 * 
	 * @return
	 */
	public Map<String, Object> getMap() {

		return map;
	}

	
	
	/**
	 * Get model
	 * @param tree
	 * @return
	 */
	private DefaultMutableTreeNode getModel(ITree tree) {

		String[] names = tree.listObjectNames(".", true);
		String[] types = tree.listObjectTypes(".", true);

		/*
		 * for (int i=0; i<names.length; i++) {
		 * System.out.println(types[i]+" = "+names[i]); }
		 */

		Object[] nodes = new Object[names.length + 1];

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new OidNode(1,
				"Data", "none"));
		DefaultMutableTreeNode clus_parent = node;
		DefaultMutableTreeNode parent = clus_parent;
		// parent.add(clus_parent);
		nodes[0] = clus_parent;

		for (int i = 0; i < names.length; i++) {
			node = new DefaultMutableTreeNode(new OidNode(i + 1, names[i],
					types[i]));
			clus_parent.add(node);
			parent = node;
			nodes[i + 1] = parent;
		}

		return clus_parent;

	}

	class OidNode {
		protected int m_id;
		protected String m_type;
		protected String m_name;

		public OidNode(int id, String name, String type) {
			m_id = id;
			m_name = name;
			m_type = type;
		}

		public int getId() {
			return m_id;
		}

		public String getName() {
			return m_name;
		}

		public String getType() {
			return m_type;
		}

		public String toString() {
			return m_name;
		}
	}

}
