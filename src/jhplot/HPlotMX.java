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

import java.io.InputStream;
import java.net.URL;
import jhplot.gui.HelpBrowser;
import vmm3d.core.*;
import vmm3d.xm3d.*;



/**
 * Interactive canvas for  visualizing and experimenting with a variety of mathematical objects in 2D and 3D.
 * It is based 3D-XplorMath-J, but rewritted to allow vector graphics rendering. Thus it can be a bit slower,
 * but the quality of graphics is much higher (graphics can be exported to EPS and PDF).
 * 
 * @author S.Chekanov
 * 
 */

public class HPlotMX {

	private static final long serialVersionUID = 1L;
	private Exhibit exhibit;
	private View view;
	private String exhibitName=null;
	private WindowXM window;
	
	/**
	 * Create canvas and show mathematical object. If the input ends with ".xml",
	 * it is assumed to be file. The file cab be on URL (input must start with http), or local file 
	 * (input must start as file:). If the input does not have ".xml", it is assumed to be class
	 * name with object.
	 * 
	 * @param exhibitName
	 *            exhibitName object to be shown.
	 */
	public HPlotMX(String exhibitName) {

		DisplayXM.noSplash();
		 boolean singleExhibit = false;
         boolean singleGallery = false;

		
		
		if (exhibitName != null) {
            try {
                    if (exhibitName.endsWith(".xml")) {
                           
                            URL url = new URL(exhibitName);
                            InputStream in = url.openStream();                                      
                            exhibit = SaveAndRestore.readExhibitFromXML(in,"Settings File");
                            view = exhibit.getViews().get(0);
                    }
                    else {
                            exhibit = (Exhibit)Class.forName(exhibitName).newInstance();
                            if (exhibit instanceof UserExhibit) {
                                    view = ((UserExhibit)exhibit).getUserExhibitSupport().defaults();
                                    if (view == null)
                                            throw new Exception("Error while creating user exhibits with default settings");
                            }
                            else
                                    view = exhibit.getDefaultView();
                    }
            }
            catch (Exception e) {
                    System.out.println("Could not load specified exhibit, \"" + exhibitName + "\".");
                    e.printStackTrace();
                    singleExhibit = singleGallery = false;
                    exhibit = null;
                    view = null;
            }
    }

         int options = Menus.NO_EXIT;
	 window = new WindowXM(options);
         if (view != null)
                         window.getMenus().install(view, exhibit);

		
		
	}

	/**
	 * Construct a  canvas with a plot with the default parameters.
	 * 
	 */
	public HPlotMX() {
		this(null);
	}

	/**
	 * Clear the current graph including graph settings. Note: the current graph
	 * is set by the cd() method
	 */
	public void clear() {
		window.dispose();
	}

	
	/**
	 * Draw mathematical object in form of exhibit.
	 * The default view will be applied.
	 * @param exhibit mathematical object to be shown.
	 */
	public void draw(Exhibit exhibit){
		 this.exhibit=exhibit;
		 view = exhibit.getDefaultView();
		 window.getMenus().install(view, exhibit);
	}


       /**
         * Draw mathematical SurfaceParametric object (parametric function defining a surface). 
         * @param exhibit parametric function to draw surface 
         */

        public void draw(vmm3d.surface.parametric.SurfaceParametric exhibit){
                 view = exhibit.getDefaultView();
                 window.getMenus().install(view, exhibit);
        }

         /**
         * Draw mathematical SurfaceImplicit object (parametric function defining a surface). 
         * @param exhibit parametric function to draw surface 
         */

        public void draw(vmm3d.surface.implicit.SurfaceImplicit exhibit){
                 view = exhibit.getDefaultView();
                 window.getMenus().install(view, exhibit);
        }

         /**
         * Draw RegularPolyhedron object (parametric function defining a surface). 
         * @param exhibit RegularPolyhedron 
         */

        public void draw(vmm3d.polyhedron.RegularPolyhedron  exhibit){
                 view = exhibit.getDefaultView();
                 window.getMenus().install(view, exhibit);
        }


       /**
         * Draw conformal map object.  
         * @param exhibit conformal map 
         */

        public void draw(vmm3d.conformalmap.ConformalMap  exhibit){
                 view = exhibit.getDefaultView();
                 window.getMenus().install(view, exhibit);
        }

          /**
         * Draw parametric space curves  
         * @param exhibit parametric space curves. 
         */

        public void draw(vmm3d.spacecurve.parametric.SpaceCurveParametric  exhibit){
                 view = exhibit.getDefaultView();
                 window.getMenus().install(view, exhibit);
        }
 
	
	/**
	 * Draw any mathematical object in form of exhibit with a given view. 
	 * @param exhibit mathematical object to be shown.
	 * @param view View of this exhibit.
	 */
	public void draw(Exhibit exhibit, View view){
		 this.exhibit=exhibit;
		 this.view=view;
		 window.getMenus().install(view, exhibit);
	}
	
	
	 /**
     * Export graph into an image file. The the image format is given by
     * extension. "png", "jpg", "eps", "pdf", "svg". In case of "eps", "pdf" and
     * "svg", vector graphics is used.
     * 
     * @param filename
     *            file name
     */
	public boolean export (String file){
		 
		 return window.getMenus().export(file);
	}
	
	/**
	 * Get the current frame.
	 * @return current frame.
	 */
	public WindowXM getWindowXM(){
		return window;
	}
	
	
	/**
	 * Get the current display.
	 * @return current display.
	 */
	public Display getDisplay(){
		return window.getDisplay();
	}
	
	/**
	 * Get current view.
	 * @return current view.
	 */
	public View getView(){
		return window.getView();
	}
	
	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		
		if (vs == false) {
			window.setVisible(false);
			return;
		}

		window.setVisible(true);
	}

	

	/**
	 * Show the canvas
	 */
	public void visible() {
		visible(true);
	}

	
	/**
	 * Close the canvas (and dispose all components) Note: a memory leak is
	 * found - no time to study it. set to null all the stuff
	 */
	public void close() {
		window.dispose();
		
	}

	

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	// end
}
