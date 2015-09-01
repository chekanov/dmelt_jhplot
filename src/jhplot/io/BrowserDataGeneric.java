package jhplot.io;

import japlot.jaxodraw.JaxoMainPanel;
import java.awt.*;
import java.util.Map;
import jhplot.*;
import jhplot.gui.GHFrame;
import jhplot.shapes.*;
import javax.swing.JFrame;

import javax.swing.*;
import hep.aida.*;
import hep.aida.ref.histogram.*;
import hep.io.root.*;
import hep.io.root.interfaces.*;
import jhplot.HPlot;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * 
 * A generic GUI to browser data stored files.
 * 
 * @author S.Chekanov
 * 
 */
public class BrowserDataGeneric extends JDialog {

	private static final long serialVersionUID = 1L;
	public JButton closeButton;
	private JButton runButton;
	private JButton tableButton;
	private Map<String, Object> map;
	private JCheckBox cb; // A checkbox.
	boolean state; // True/false state of checkbox.
	// private HPlot frame = null;
	// private HPlot3D frame3D = null;
	private GHFrame gframe;
	private JaxoMainPanel jaframe;
	private JTree jtree;
	private HashMap<String, String> tooltips;
	private ArrayList<String[]> data;
	private SortedSet examplekeys;
	private String selected = null;
	private JScrollPane jCandScroll;

	private boolean is3D;
	static Random random = new Random();
	private final static ImageIcon h1Icon = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class.getResource("icons/h1_t.gif"));
	private final static ImageIcon h2Icon = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class.getResource("icons/h2_t.gif"));
	private final static ImageIcon h3Icon = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class.getResource("icons/h3_t.gif"));
	private final static ImageIcon icloud1d = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/icloud1d.png"));
	private final static ImageIcon icloud2d = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/icloud2d.png"));
	private final static ImageIcon icloud3d = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/icloud3d.png"));
	private final static ImageIcon idatapointset = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/idatapointset.png"));
	private final static ImageIcon iprofile1d = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/iprofile1d.png"));
	private final static ImageIcon ifunction1d = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/ifunction1d.png"));
	private final static ImageIcon ituplecolumn = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/ituplecolumn.png"));

	
	private final static ImageIcon folderclosed = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/folderclosed.png"));
	private final static ImageIcon folderopen = new ImageIcon(
			AidaDirectoryTreeCellRenderer.class
					.getResource("icons/folderopen.png"));

	//ImageIcon all = new ImageIcon(getClass().getResource("/images/graph.jpg"));

        ImageIcon all = new ImageIcon(getClass().getResource(
                        "/images/objects/all.png"));
	ImageIcon imageH1D = new ImageIcon(getClass().getResource(
			"/images/objects/h1.png"));
         ImageIcon imageH2D = new ImageIcon(getClass().getResource(
                        "/images/objects/h2.png"));
	ImageIcon imageF1D = new ImageIcon(getClass().getResource(
			"/images/objects/f1.png"));
        ImageIcon imageF2D = new ImageIcon(getClass().getResource(
                        "/images/objects/f2.png"));
        ImageIcon imageP1D = new ImageIcon(getClass().getResource(
                       "/images/objects/p1.png"));
        ImageIcon imageP0D = new ImageIcon(getClass().getResource(
                       "/images/objects/p0.png"));


	ImageIcon imageCloud = new ImageIcon(getClass().getResource(
			"/images/Cloud.gif"));
	ImageIcon closedIcon = new ImageIcon(getClass().getResource(
			"/images/folderclosed.png"));
	ImageIcon openIcon = new ImageIcon(getClass().getResource(
			"/images/folderopen.png"));

	/**
	 * Default constructor.
	 */
	public BrowserDataGeneric() {
		cb = new JCheckBox("Overlay");

		setTitle("Objects");
		// setModal( true );
		setResizable(true);

		//setModal(true);

		// LocationSize(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JPanel lowerPanel = new JPanel();
		// lowerPanel.setPreferredSize(new Dimension(100, 30));

		closeButton = new JButton();
		closeButton.setText("Exit");

		tableButton = new JButton();
		tableButton.setText("Table");
		runButton = new JButton();
		runButton.setText("Plot");

		// actions
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				showIt(false);

			}
		});

		tableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				Object obj = map.get(selected);
				Class<? extends Object> cls = obj.getClass();
				String name = cls.getName();

				if (name.indexOf("P0D") > -1)
					new HTable((P0D) obj);
				else if (name.indexOf("P0I") > -1)
					new HTable((P0I) obj);
				else if (name.indexOf("P1D") > -1)
					new HTable((P1D) obj);
				else if (name.indexOf("H1D") > -1)
					new HTable((H1D) obj);
				else if (name.indexOf("F1D") > -1)
					new HTable((F1D) obj);
				else if (name.indexOf("PND") > -1)
					new HTable((PND) obj);
				else if (name.indexOf("PNI") > -1)
					new HTable((PNI) obj);
				else {
					String a = "The object \"" + name
							+ "\" cannot be shown as a table";
					jhplot.utils.Util.ErrorMessage(a);

				}
			}

		});

		lowerPanel.add(runButton, null);
		lowerPanel.add(cb, null);
		lowerPanel.add(tableButton, null);
		lowerPanel.add(closeButton, null);
		getContentPane().add(lowerPanel, java.awt.BorderLayout.SOUTH);

	}

	
	
	
	/**
	 * Call this for HPlotJa canvas.
	 * @param gframe
	 * @param map
	 * @param ishow
	 */
	public void setDataFileBrowser(final JaxoMainPanel jaframe,
			final Map<String, Object> map, boolean ishow) {

		this.jaframe = jaframe;
		this.map = map;

		fillJTree();

		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				Object obj = map.get(selected);
				Class<? extends Object> cls = obj.getClass();
				String name = cls.getName();
				// System.out.println(name);

				HPlotJa jp = jaframe.getJaPlot();
				Color randomColor = Color.getHSBColor(random.nextFloat(), 1.0F,
						1.0F);

				if (cb.isSelected() == false)
					jp.clearAllData();

				jp.setAutoRange();

				if (name.indexOf("TH1F") > -1 || name.indexOf("TH1D") > -1) {
					H1D h = new H1D((TH1) obj);
					jp.draw(h);
				} else if (name.indexOf("TGraph") > -1) {
					TGraph tt = (TGraph) obj;
					String tname = tt.getTitle();
					double[] x = tt.getX();
					double[] y = tt.getY();
					P1D p = new P1D(tname, x, y);
					p.setColor(randomColor);
					jp.draw(p);
				} else if (name.indexOf("P0D") > -1) {
					P0D a = (P0D) obj;
					H1D h = a.getH1D(100);
					//h.setColor(randomColor);
					jp.draw(h);
				} else if (name.indexOf("P0I") > -1) {
					P0I a = (P0I) obj;
					H1D h = a.getH1D(100);
					h.setColor(randomColor);
					jp.draw(h);
				} else if (name.indexOf("PND") > -1) {
					PND a = (PND) obj;
					H1D h = a.getH1D(100);
					h.setColor(randomColor);
					jp.draw(h);
				} else if (name.indexOf("PNI") > -1) {
					PNI a = (PNI) obj;
					H1D h = a.getH1D(100);
					h.setColor(randomColor);
					jp.draw(h);
				} else if (name.indexOf("P1D") > -1) {
					P1D a = (P1D) obj;
					jp.draw(a);
				} else if (name.indexOf("F1D") > -1) {
					jp.setAutoRange();
					F1D a = (F1D) obj;
					a.setColor(randomColor);
					jp.draw(a);
				} else if (name.indexOf("H1D") > -1) {
					H1D h1 = (H1D) obj;
					jp.draw(h1);
				} else if (name.indexOf("FND") > -1) {
					jp.draw((FND) obj);
				} else if (name.indexOf("FNI") > -1) {
					jp.draw((FND) obj);
				} else if (name.indexOf("IHistogram1D") > -1) {
					H1D h1 = new H1D((Histogram1D) obj);
					h1.setColor(randomColor);
					jp.draw(h1);
				} else if (name.indexOf("ICloud1D") > -1) {
					H1D h1 = new H1D((Cloud1D) obj, 100);
					h1.setColor(randomColor);
					jp.draw(h1);
				} else if (name.indexOf("IDataPointSet") > -1) {
					P1D h1 = new P1D((IDataPointSet) obj);
					h1.setColor(randomColor);
					jp.draw(h1);
				} else if (name.indexOf("ICloud2D") > -1) {
					P1D h1 = new P1D((Cloud2D) obj);
					h1.setColor(randomColor);
					jp.draw(h1);
				} else {
					String a = "The object \"" + name
							+ "\" cannot be drawn on HPlot canvas";
					jhplot.utils.Util.ErrorMessage(a);
					return;

				}

				jp.update();

			}
		});
		showIt(ishow);
	}

	/**
	 * Open a dialog to display a map of objects for HPlot and HPlot3D.
	 * 
	 * @param frame
	 * @param map
	 * @param ishow
	 */
	public void setDataFileBrowser(final GHFrame gframe,
			final Map<String, Object> map, boolean ishow) {

		this.gframe = gframe;
		this.map = map;
		fillJTree();

		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				Object obj = map.get(selected);
				Class<? extends Object> cls = obj.getClass();
				String name = cls.getName();
				// System.out.println(name);
				Color randomColor = Color.getHSBColor(random.nextFloat(), 1.0F,
						1.0F);

				if (gframe instanceof jhplot.HPlot) {
					HPlot frame = (HPlot) gframe;

					if (cb.isSelected() == false)
						frame.clearAll();

					frame.setAutoRange();

					if (name.indexOf("TH1F") > -1 || name.indexOf("TH1D") > -1) {
						H1D h = new H1D((TH1) obj);

						frame.draw(h);
					} else if (name.indexOf("TGraph") > -1) {
						TGraph tt = (TGraph) obj;
						String tname = tt.getTitle();
						double[] x = tt.getX();
						double[] y = tt.getY();
						P1D p = new P1D(tname, x, y);
						frame.draw(p);
					} else if (name.indexOf("P0D") > -1) {
						P0D a = (P0D) obj;
						H1D h = a.getH1D(100);
						frame.draw(h);
					} else if (name.indexOf("P0I") > -1) {
						P0I a = (P0I) obj;
						H1D h = a.getH1D(100);
						frame.draw(h);
					} else if (name.indexOf("PND") > -1) {
						PND a = (PND) obj;
						H1D h = a.getH1D(100);
						frame.draw(h);
					} else if (name.indexOf("PNI") > -1) {
						PNI a = (PNI) obj;
						if (a.size() > 0) {
							H1D h = a.getH1D(100);

							frame.draw(h);
						}
					} else if (name.indexOf("P1D") > -1) {
						P1D a = (P1D) obj;

						a.setColor(randomColor);
						int symbol = (int) (5 * random.nextFloat());
						if (symbol <= 0)
							symbol = 1;
						a.setSymbol(symbol);
						a.setSymbolSize(4);
						a.setStyle("p");
						a.setPenWidth(3);
						a.setColor(randomColor);
						frame.draw(a);
					} else if (name.indexOf("F1D") > -1) {
						F1D a = (F1D)obj;
                                                a.parse();
						a.setColor(randomColor);
						frame.draw(a);
					} else if (name.indexOf("H1D") > -1) {
						H1D h1 = (H1D) obj;
						h1.setFill(true);
						h1.setStyle("h");
						h1.setPenWidthErr(1);
						h1.setPenWidth(1);
						h1.setFillColorTransparency(0.6);
						h1.setFillColor(randomColor);
						h1.setColor(Color.black);
						frame.draw(h1);

					} else if (name.indexOf("FND") > -1) {

						frame.draw((FND) obj);
					} else if (name.indexOf("FNI") > -1) {

						frame.draw((FND) obj);
					} else if (name.indexOf("HLabel") > -1) {
						frame.draw((HLabel) obj);
					} else if (name.equalsIgnoreCase("jhplot.HKey")) {
						frame.draw((HKey) obj);
					} else if (name.equalsIgnoreCase("jhplot.HShape")) {
						frame.draw((HShape) obj);
					} else if (name.equalsIgnoreCase("jhplot.HMLabel")) {
						frame.draw((HMLabel) obj);
					} else if (name.equalsIgnoreCase("java.lang.String")) {
						HLabel a = new HLabel((String) obj);
						frame.draw(a);
					} else if (name.equalsIgnoreCase("javax.swing.JFrame")) {
						((JFrame) obj).setVisible(true);

					} else if (name.indexOf("IHistogram1D") > -1) {

						H1D h1 = new H1D((Histogram1D) obj);
						h1.setColor(randomColor);
						frame.draw(h1);
					} else if (name.indexOf("ICloud1D") > -1) {

						H1D h1 = new H1D((Cloud1D) obj, 100);
						frame.draw(h1);
					} else if (name.indexOf("IDataPointSet") > -1) {

						P1D h1 = new P1D((IDataPointSet) obj);
						h1.setColor(randomColor);
						frame.draw(h1);
					} else if (name.indexOf("ICloud2D") > -1) {

						P1D h1 = new P1D((Cloud2D) obj);
						h1.setColor(randomColor);
						frame.draw(h1);
					} else {
						String a = "The object \"" + name
								+ "\" cannot be drawn on HPlot canvas";
						jhplot.utils.Util.ErrorMessage(a);

					} // end 2D frame

					if (gframe instanceof jhplot.HPlot3D) {
						HPlot3D frame3d = (HPlot3D) gframe;
						frame3d.setAutoRange();

						if (cb.isSelected() == false)
							frame3d.clearAll();

						if (name.indexOf("P2D") > -1) {
							P2D a = (P2D) obj;
							if (a.size() > 0) {
								frame3d.draw(a);
							}
						} else if (name.indexOf("F2D") > -1) {
							F2D a = (F2D) obj;
							frame3d.draw(a);
						} else if (name.indexOf("H2D") > -1) {
							H2D a = (H2D) obj;

							frame3d.draw(a);
						} else if (name.indexOf("TH2F") > -1
								|| name.indexOf("TH2D") > -1) {
							H2D h = new H2D((TH2) obj);

							frame3d.draw(h);

						} else if (name.indexOf("IHistogram2D") > -1) {

							H2D h1 = new H2D((Histogram2D) obj);
							frame3d.draw(h1);

						} else {
							String a = "The object \"" + name
									+ "\" cannot be drawn on HPlot3D canvas";
							jhplot.utils.Util.ErrorMessage(a);

						} // end 2D frame

					}
				}
			}
		});

		showIt(ishow);

	};

	/**
	 * Show the browser on the left of Canvas.
	 */
	public void showIt(boolean show) {

		if (show == false) {
			setVisible(false);
			gframe = null;
		} else {

			JFrame jf = null;
			if (gframe instanceof jhplot.HPlot3D) {
				HPlot3D frame = (HPlot3D) gframe;
				jf = frame.getFrame();
			}

			if (gframe instanceof jhplot.HPlot) {
				HPlot frame = (HPlot) gframe;
				jf = frame.getFrame();
			}

			if (jaframe != null) {
				
				 jf=(JFrame)jaframe;
			}
			
			// store mainframe dimensions
			Dimension dim = jf.getSize();
			int w = dim.width;
			int h = dim.height;
			// Get the system resolution
			Dimension res = Toolkit.getDefaultToolkit().getScreenSize();

			// make sure the dialog is not too big
			Dimension size = new Dimension(
					Math.min((int) (0.4 * w), res.width), Math.min(
							(int) (1.0 * h), res.height));
			this.setSize(size);

			// pack();
			jhplot.utils.Util.rightWithin(jf, this);

			// Util.centreWithin( Global.mainFrame, this );
			setVisible(true);
		}

	}

	/**
	 * Create Jtree from the map.
	 */
	private void fillJTree() {

		int n = 0;
		data = new ArrayList<String[]>();
		examplekeys = new TreeSet();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			String name = value.getClass().getSimpleName();
			String[] words = key.split("/");
			if (words.length > 1) {
				String[] anArray = new String[3];
				anArray[0] = words[0]; // directory
				anArray[1] = words[1]; // key
				anArray[2] = name; // type for tooltips
				examplekeys.add(anArray[0]); // directory, if any
				data.add(anArray);
			} else if (words.length < 2) {
				String[] anArray = new String[2];
				anArray[0] = words[0];
				anArray[1] = name; // type tooltips
				data.add(anArray);

			}

			// data.add(anArray);
			// System.out.println(anArray[0]+" "+anArray[1]);
			n++;
		}

		tooltips = new HashMap<String, String>();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		Iterator<String> iterator = examplekeys.iterator();

		// first keys with directories
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			// System.out.println("key : " + key);
			DefaultMutableTreeNode topic = new DefaultMutableTreeNode(key);
			root.add(topic);

			Iterator<String[]> itr = data.iterator();
			while (itr.hasNext()) {
				String[] element = (String[]) itr.next();
				if (element[0].equals(key)) {
					DefaultMutableTreeNode entry = new DefaultMutableTreeNode(
							element[1]);
					topic.add(entry);
					tooltips.put(element[1], element[2]);
				}
			}
			// System.out.println("key : " + key + " value :" +
			// entries[0]+"|"+entries[1]);
		}

		// keys without directories
		Iterator<String[]> itr0 = data.iterator();
		while (itr0.hasNext()) {
			String[] element = (String[]) itr0.next();
			if (element.length == 2) {
				DefaultMutableTreeNode entry = new DefaultMutableTreeNode(
						element[0]);
				root.add(entry);
				tooltips.put(element[0], element[1]);
			}
		}

		jtree = new JTree(root);
		if (ituplecolumn != null) {
			jtree.setCellRenderer(new OwnRenderer(ituplecolumn));
			ToolTipManager.sharedInstance().registerComponent(jtree);
		}

		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) jtree
				.getCellRenderer();
		renderer.setClosedIcon(folderclosed);
		renderer.setOpenIcon(folderopen);
		renderer.setLeafIcon(ituplecolumn);

		jtree.setRootVisible(false);
		jtree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		jtree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				String sb = "";
				Object objects[] = path.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
						.getPath().getLastPathComponent();
				node.getChildCount();

				for (int j = 1; j < objects.length; j++) {
					TreeNode gn = (TreeNode) objects[j];
					String s = gn.toString();
					sb = sb + "/" + s;
					// if (gn.sLeaf());
				}

				// directory
				if (node.getChildCount() > 0) {
					selected = "";
					return;
				}
				;

				selected = sb;

				selected = selected.substring(1, selected.length());
				// System.out.println("pass= " + selected);

			}
		});

		jCandScroll = new JScrollPane();
		jCandScroll.setPreferredSize(new Dimension(1, 1));
		jCandScroll.setMinimumSize(new java.awt.Dimension(0, 0));
		getContentPane().add(jCandScroll, java.awt.BorderLayout.CENTER);
		jCandScroll.getViewport().add(jtree);

	}

	/**
	 * Render the cells
	 * 
	 * @author sergei
	 * 
	 */
	private class OwnRenderer extends DefaultTreeCellRenderer {

		Icon tutorialIcon;

		public OwnRenderer(Icon icon) {
			tutorialIcon = icon;
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			if (leaf) {
				String svalue = value.toString();
				String name = (String) tooltips.get(svalue);

				ImageIcon img =all ;
				if (name.indexOf("H1D") > -1 || name.indexOf("TH1F") > -1 || name.indexOf("TH1D") > -1)
					img =  imageH1D;
				if (name.indexOf("H2D") > -1)
					img =   imageH2D;
				if (name.indexOf("H3D") > -1)
					img =   imageH2D;
				if (name.indexOf("F1D") > -1)
					img = imageF1D;
				if (name.indexOf("F2D") > -1)
					img = imageF2D;
				if (name.indexOf("Cloud") > -1 || name.indexOf("TGraph")>-1)
					img = imageCloud;
				if (name.indexOf("Histogram1D") > -1)
					img =  h1Icon;
				if (name.indexOf("Histogram2D") > -1 
						       || name.indexOf("TH2F")>-1 
						       ||  name.indexOf("TH2D")>-1)
					img =  h2Icon;
				if (name.indexOf("P0") > -1)
					img =  imageP0D ;
				if (name.indexOf("P1D") > -1)
					img = imageP1D ;
				if (name.indexOf("P2D") > -1)
					img =  imageP0D ;
				if (name.indexOf("PN") > -1)
					img =  imageP0D ;
				if (name.indexOf("HProf") > -1)
					img =  iprofile1d ;
				if (name.indexOf("ICloud1") > -1)
					img =  icloud1d ;
				if (name.indexOf("ICloud2") > -1)
					img =  icloud2d ;
				
				setIcon(img);
				if (tooltips.isEmpty())
					setToolTipText(null);
				setToolTipText(name);
			} else {
				setToolTipText(null); // no tool tip
			}

			return this;
		}
	}

}
