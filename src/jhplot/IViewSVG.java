/**
*    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
*    All rights reserved.
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

package jhplot;

import jhplot.gui.HelpBrowser;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;


/**
 * 
 * Simple image viewer for SVG files.
 * 
 * @author S.Chekanov
 * 
 */


public class IViewSVG {



     /**
     * Show SVG file inside the image viewer.
     * @param sfile path to the SVG file. 
     */
    public IViewSVG(String  sfile) {
          showImage(sfile);
     }


     /**
      * Show empty image viewer.
     */
    public IViewSVG() {
          showImage(null);
     }


   /**
    * Show SVG file inside the image viewer.
    * @param sfile path to the SVG file. 
    */
    private void showImage(String sfile) {
        // Create a new JFrame.
        JFrame f = new JFrame("IViewSVG");
        f.getContentPane().add(createComponents());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(600, 400);
        f.setVisible(true);
        load(sfile);
        //f.pack();



    }


        /**
            * Show online documentation.
            */
              public void doc() {

                  String a=this.getClass().getName();
                  a=a.replace(".", "/")+".html";
                          new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);



              }




   /**
    * Show SVG file inside the image viewer.
    * @param sfile path to the SVG file. 
    */
    public void load(String sfile) {

     if (sfile != null) {

     String extension="";

     int i = sfile.lastIndexOf('.');
     if (i >= 0) {
          extension = sfile.substring(i+1);
     }

    if (extension.equalsIgnoreCase("svg")) {
     try {
        System.out.println("Open file="+sfile);
        File ff = new File(sfile);
        svgCanvas.setURI(ff.toURL().toString());
      } catch(Exception fileEx) {
             System.out.println(fileEx.toString());
      }

      } else System.err.println("Not SVG file!");

      }


   }




 // The "Load" button, which displays up a file chooser upon clicking.
    protected JButton button = new JButton("Load...");

    // The status label.
    protected JLabel label = new JLabel();

    // The SVG canvas.
    protected JSVGCanvas svgCanvas = new JSVGCanvas();


    private JComponent createComponents() {
        // Create a panel and add the button, status label and the SVG canvas.
        final JPanel panel = new JPanel(new BorderLayout());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(button);
        p.add(label);

        panel.add("North", p);
        panel.add("Center", svgCanvas);

        // Set the button action.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser(".");
                int choice = fc.showOpenDialog(panel);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    try {
                        svgCanvas.setURI(f.toURL().toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


 // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                label.setText("Document Loading...");
            }
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                label.setText("Document Loaded.");
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                label.setText("Build Started...");
            }
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                label.setText("Build Done.");
                //frame.pack();
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                label.setText("Rendering Started...");
            }
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                label.setText("");
            }
        });

        return panel;
    }
}

