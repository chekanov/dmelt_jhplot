package jhplot;

import medusa.display.EditableGraphPanel;
import medusa.georgios.Layouts.Layouts;
import medusa.graph.*;
import javax.swing.JFrame;
import java.io.IOException;
import medusa.dataio.*;
import medusa.MedusaSimplerFrame;
import medusa.DataFormatException;
import jhplot.gui.HelpBrowser;


/**
 * A Medusa frame to display and work with interactive graphs.
 * @author S.Chekanov 
 */
public class HMedusa {
	// ---------------------------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	private MedusaSimplerFrame frame;
	private EditableGraphPanel stringletPanel;

	/**
	 * Initialize the Medusa graph builder.
	 *
	 */
	public HMedusa() {
		frame = new MedusaSimplerFrame();
		stringletPanel = frame.getPanel();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Set the canvas frame visible. 
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		frame.setVisible(vs);
		if (vs == false)
			frame.validate();

	}

	/**
	 * Set the canvas frame visible. 
	 * 
	 */
	public void visible() {
		frame.setVisible(true);

	}


         /**
         * Quit the canvas (and dispose all components).
         */
        public void quit() {
             frame.setVisible(false);
             frame.dispose();
         }

        /**
         * Clear the canvas (and dispose all graphs) 
         */
        public void clear() {
            stringletPanel.clearGraph();
            update();
         }

         /**
         * Update the canvas. 
         */
        public void update() {
            stringletPanel.repaint();
            frame.updateInfo();
         }

	/**
	 * Add a graph to the editor.
	 * 
	 * @param graph
	 *            graph to be added.
	 */
	public void add(Graph g) {

		frame.hide_starting_background();
		frame.clearImageEvent();
		frame.scaleOut();
		Layouts.Apply_Random_Coordinates(g, stringletPanel);
		frame.setCursor(null);
		stringletPanel.setGraph(g);
		frame.updateInfo();
		stringletPanel.repaint();

	}


      /**
         * Add a graph to the editor.
         * 
         * @param graph
         *            graph to be added.
         */
        public void setGraph(Graph g) {
             add(g); 

        }


       /**
         * Get a graph. 
         * 
         * @return  current graph
         *            
         */
        public Graph getGraph() {
          return stringletPanel.getGraph();
        }


	/**
	 * Return medusa frame
	 * 
	 * @return frame;
	 */
	public MedusaSimplerFrame getFrame() {
		return frame;
	}

	/**
	 * Return medusa panel with the graph.
	 * 
	 * @return panel
	 */
	public EditableGraphPanel getPanel() {
		return stringletPanel;
	}

	/**
	 * LOad a file with the graph.
	 * 
	 * @param file
	 *            file with the graph.
	 */
	public void loadFile(String file) {

		frame.hide_starting_background();
		frame.clearImageEvent();
		frame.scaleOut();
		// frame.clearGraphEvent(); // asks questions!

		try {

			stringletPanel.appendGraph(file);
			frame.updateInfo();
			stringletPanel.repaint();
			// frame.waitForLoad(file, DataLoader.LOAD_MEDUSA);
		} catch (IOException | DataFormatException e) {
			e.printStackTrace();
		}

	}


        /**
         * Show online documentation.
         */
        public void doc() {

                String a = this.getClass().getName();
                a = a.replace(".", "/") + ".html";
                new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

        }


	/*
	 * public static void main(final String args[]) {
	 * java.awt.EventQueue.invokeLater(new Runnable() {
	 * 
	 * public void run() { frame = new HMedusa(); try { // do we have args?
	 * frame.checkArgs(args); } catch (DataFormatException ex) { System.out.
	 * println("Data format error! Check your data and make sure it is in the correct format"
	 * ); ex.printStackTrace(); System.exit(0); } catch (IOException ex) {
	 * System.out.
	 * println("File error! Check the integrity of your file. You may need to provide the full path to the file"
	 * ); ex.printStackTrace(); System.exit(0); } frame.setVisible(true); } });
	 * }
	 */

}
