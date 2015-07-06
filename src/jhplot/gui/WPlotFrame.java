 /* 
 * This code is part of DataMelt
 *
 */
package jhplot.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import kcl.waterloo.swing.*;
import kcl.waterloo.actions.ActionManager;
import kcl.waterloo.export.ExportFactory;
import kcl.waterloo.logging.CommonLogger;
import kcl.waterloo.swing.GCFrameInterface;
import kcl.waterloo.swing.GCGridContainer;
import kcl.waterloo.swing.GCGridContainerInterface;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import kcl.waterloo.deploy.pde.PDEGraphics2D;
import kcl.waterloo.export.*;
import kcl.waterloo.gui.file.FileUtil;

/**
 * A frame provides a JFrame subclass that supports graphs and grids
 * of graphs.
 *
 * Management of multiple {@code WPlotFrame}s in a JVM instance is coordinated via
 * an instance of the {@code FrameManager} class.
 *
 * Typically, the graphics container for a GCFrame is either:
 * <ol>
 * <li>a <code>GCGridContainer</code> - for single grids</li>
 * <li>a <code>GCTabbedGridContainer</code> - for multiply tabbed grids</li>
 * </ol>
 *
 * By default, a {@code GCGridContainer} is used. Its contents will be
 * automatically added to Tab 0 of a {@code GCTabbedGridContainer} when required
 * and the {@code GCTabbedGridContainer} will become the content pane.
 *
 * However, the content pane can be another component parenting the graphics
 * container e.g. a JPanel with a border layout and the graphics container in
 * its center in custom implementations. Use setGraphicsContainer() to direct a
 * GCFrame to the appropriate component.
 *
 *
 * For convenience, this class includes add and makeTabbed methods that forward
 * calls to the content pane. For other methods, the content pane needs to be
 * accessed explicitly.
 *
 * @author S.Chekanov <a
 */
public class WPlotFrame extends JFrame implements GCFrameInterface, ChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GCGridContainerInterface graphicsContainer = new GCGridContainer();
  //  private final static CommonLogger logger = new CommonLogger(WPlotFrame.class);

    /**
     * Default constructor to make WPlot canvas. 
     * It creates a {@code GCFrame} with a single graph (i.e.
     * a 1x1 grid) in a GCGridContainer.
     */
    public WPlotFrame() {
        this("");
        setContentPane((Container) graphicsContainer);
    }

    /**
     * Creates a {@code GCFrame} with a single graph (i.e. a 1x1 grid)
     *
     * @param titleText the Title for the frame.
     */
    public WPlotFrame(String titleText) {
        super();
        int frameCount = FrameManager.getLowestAvailable();
        setTitle("Frame " + frameCount + ": " + titleText);
        setName("Frame" + frameCount);
        init(frameCount);
        setSize(500, 500);
        setBackground(Color.WHITE);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


     // chekanov
     /**
     * Creates a {@code GCFrame} with a single graph (i.e. a 1x1 grid)
     *
     * @param titleText the Title for the frame.
     */
    public WPlotFrame(String titleText, int xsize, int ysize, boolean isshown) {
        super();
        int frameCount = FrameManager.getLowestAvailable();
        setTitle("Frame " + frameCount + ": " + titleText);
        setName("Frame" + frameCount);
        init(frameCount);
  
        
        setSize(xsize,ysize);
        setBackground(Color.WHITE);
        setVisible(isshown);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }



    private void init(int frameCount) {
        graphicsContainer = new GCGridContainer();
        setContentPane((Container) graphicsContainer);
        FrameManager.getList().put(Double.valueOf(frameCount), this);
       


        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        JMenuItem export = new JMenuItem("Export As");
        export.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	exportImage();
                
            }
        });
        menu.add(export);
 


        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
             setVisible(false);
              dispose();
            }
        });
        menu.add(exit);


        setJMenuBar(menuBar);



        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(FrameManager.getInstance());
    }

    /**
     * Returns the graphics container for this instance. While all standard
     * implementations use the content pane of the frame as the graphics
     * container, this need not be the case with user written replacements.
     *
     * @return the GCGridContainerInterface
     */
    public GCGridContainerInterface getGraphicsContainer() {
        return graphicsContainer;
    }

    /**
     * Sets the supplied graphics container as the content pane for the frame.
     *
     * @param c a GCGridContainerInterface
     */
    public final void setGraphicsContainer(GCGridContainerInterface c) {
        setContentPane((Container) c);
        graphicsContainer = c;
    }

    /**
     * Adds the specified Component to the graphics container at grid location
     * 0,0 in tab 0.
     *
     * @param c the Component to add
     * @return the added Component
     */
    @Override
    public Component add(Component c) {
        return add(c, 0f, 0f, 0);
    }

    /**
     * Adds a component to the grid at the specified row and column position in
     * the specified tab.
     *
     * @param c the component to add.
     * @param row the row position in grid elements
     * @param column the column position in grid elements
     * @param tab the tab position
     * @return the added component
     */
    public final Component add(Component c, double row, double column, int tab)
            throws UnsupportedOperationException {
        return add(c, row, column, 1d, 1d, tab);
    }

    /**
     * Adds a component to the grid at the specified row and column position in
     * the specified tab. The width and height of the component are also
     * specified.
     *
     * @param c the component to add.
     * @param row the row position in grid elements
     * @param column the column position in grid elements
     * @param width the width of the component in grid elements
     * @param height the height of the component in grid elements
     * @param tab the tab position
     * @return the added component
     */
    public final Component add(Component c, double row, double column, double width, double height, int tab)
            throws UnsupportedOperationException {
        GCGridContainerInterface contents = getGraphicsContainer();
        if (contents instanceof GCTabbedGridContainer) {
            GCGridContainerInterface thisTab = (GCGridContainerInterface) contents.getComponentAt(tab);
            return thisTab.add(c, row, column, width, height);
        } else if (contents instanceof GCGridContainer && tab == 0) {
            return contents.add(c, row, column, width, height);
        } else {
           // logger.error("Target does not implement the GCGridContainerInterface");
            return null;
        }
    }

    /**
     * /**
     * Adds a tab with no title to the content pane. If required, a
     * GCTabbedGridContainer will be created and set as the content pane. The
     * previous contents will be copied to tab 0.
     *
     * @param title for the tab
     */
    public void makeTabbed(String title) {
        JTabbedPane tabbedPanel;
        if (getGraphicsContainer() instanceof GCGridContainer) {
            Component contents = (Component) getGraphicsContainer();
            tabbedPanel = new GCTabbedGridContainer();
            tabbedPanel.addChangeListener(this);
            setContentPane(tabbedPanel);
            setGraphicsContainer((GCGridContainerInterface) tabbedPanel);
            tabbedPanel.insertTab(title, null, contents, "", 0);
        }
    }

    /**
     * Adds a tab using the supplied Component.
     *
     * @param s the name for the tab
     * @param c the Component to form the tab
     * @return the Component c
     */
    public Component addTab(String s, Component c) throws UnsupportedOperationException {
        if (graphicsContainer instanceof JTabbedPane) {
            ((JTabbedPane) getGraphicsContainer()).addTab(s, c);
            return c;
        } else {
            //logger.error("Target does not support tabs");
            return null;
        }

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        toFront();
        ActionManager.processAction(ae, (Component) this.getGraphicsContainer());
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
    }



     /**
         * Exports the image to some graphic format.
         */
        protected void exportImage() {

           JFileChooser fileChooser = jhplot.gui.CommonGUI.openImageFileChooser(this);
          
           
          if (fileChooser.showDialog(this, "Save As") == 0) {

                  final File scriptFile = fileChooser.getSelectedFile();
                  if (scriptFile == null) return;
                  else if (scriptFile.exists()) {
                          int res = JOptionPane.showConfirmDialog(this,
                                          "The file exists. Do you want to overwrite the file?",
                                          "", JOptionPane.YES_NO_OPTION);
                          if (res == JOptionPane.NO_OPTION)
                                  return;
                  }
                 
                  Thread t = new Thread("saving image") {
                          public void run() {
                             export(scriptFile.getAbsolutePath());
                          };
                  };
                  t.start();
          }




        }



        /**
    	 * Fast export of the canvas to an image file (depends on the extension,
    	 * i.e. PNG, PDF, EPS, PS, SVG). Images can be saved to files with extension
    	 * ".gz" (compressed SVG). Also, image can be saved to PDE format. No
    	 * questions will be asked, an existing file will be rewritten. If
    	 * 
    	 * @param f
    	 *            Output file with the proper extension. If no extension, PNG
    	 *            file is assumed. Other formats are PNG, PDF, EPS, PS, SVG.
    	 * 
    	 */
    	public void export(String f) {

    		
    		GCGridContainer graphContainer=  (GCGridContainer) graphicsContainer;
    		
    		if (graphContainer == null)
    			return;
    		File file = null;

    		try {
    			file = new File(f);
    			String ext = FileUtil.getExtension(file);
    			if (ext.equalsIgnoreCase("pdf")) {
    				ExportFactory.saveAsPDF(graphContainer, file);
    			} else if (ext.equalsIgnoreCase("eps")) {
    				ExportFactory.saveAsEPS(graphContainer, file);
    			} else if (ext.equalsIgnoreCase("svg")) {
    				ExportFactory.saveAsSVG(graphContainer, file);
    			} else if (ext.equalsIgnoreCase("gz")) {
    				ExportFactory.saveAsCompressedSVG(graphContainer, file);
    			} else if (ext.equalsIgnoreCase("pde")) {
    				PDEGraphics2D g2 = new PDEGraphics2D(
    						(Graphics2D) graphContainer.getGraphics(),
    						graphContainer.getSize());
    				graphContainer.paint(g2);
    				g2.write(file.getPath(), false, "");
    			} else {
    				BufferedImage buffer = graphContainer
    						.getGraphicsConfiguration().createCompatibleImage(
    								graphContainer.getWidth(),
    								graphContainer.getHeight());
    				Graphics2D g2 = (Graphics2D) buffer.getGraphics();
    				graphContainer.paint(g2);
    				if (ext.isEmpty()) {
    					ext = "png";
    					file = new File(file.getPath().concat(".png"));
    				}
    				ImageIO.write(buffer, ext, file);
    			}
    		} catch (IOException ex) {
    			System.out.println(String.format("IOException saving file: %s",
    					file.getPath()));
    		}

    	}








}
