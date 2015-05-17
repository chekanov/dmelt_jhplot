// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://jwork.org/jhepwork/
// * Based on the work of Eric.Atienza (surfaceplotter) licensed under GNU
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package jplot3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import jhplot.F2D;
import jhplot.H2D;
import jhplot.P2D;
import jhplot.P3D;
import jplot3d.SurfaceModelCanvas.Plotter;
import jplot3d.SurfaceModel.PlotColor;
import jplot3d.SurfaceModel.PlotType;


/**
 * @author Eric.Atienza and S.Cheknov
 * 
 */
public class JSurfacePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SurfaceModelCanvas sm;
	private JSurface surface;
	private OptionsDialog odialog;

	private ArrayList<P2D> p2d_hold = null;
	private ArrayList<P3D> p3d_hold = null;
	private ArrayList<F2D> f2d_hold = null;
	private ArrayList<H2D> h2d_hold = null;

	private boolean isEmpty = false;

	public JSurfacePanel() {
		super(new BorderLayout());
		surface = new JSurface();

		add(surface, BorderLayout.CENTER);

		final JSurfacePanel ff = this;
		surface.item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				odialog = new OptionsDialog(ff);
				odialog.setGUI();
				odialog.setVisible(true);
				// System.out.println("Item1");
			}

		});

		surface.item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				evaluate();
			}
		});

	}

	/**
	 * @return
	 */
	public SurfaceModelCanvas createDefaultSurfaceModel() {
		sm = new SurfaceModelCanvas();
		sm.setPlotFunction2(false);
		sm.setCalcDivisions(50);
		sm.setDispDivisions(50);
		sm.setContourLines(10);
		sm.setXMin(0);
		sm.setXMax(1);
		sm.setYMin(0);
		sm.setYMax(1);

		sm.setBoxed(true);
		sm.setDisplayXY(true);
		sm.setExpectDelay(false);
		sm.setAutoScaleZ(true);
		sm.setDisplayZ(true);
		sm.setMesh(true);
		sm.setPlotType(PlotType.SURFACE);
		// sm.setPlotType(PlotType.WIREFRAME);
		// sm.setPlotType(PlotType.CONTOUR);
		// sm.setPlotType(PlotType.DENSITY);

		sm.setPlotColor(PlotColor.SPECTRUM);
		// sm.setPlotColor(PlotColor.DUALSHADE);
		// sm.setPlotColor(PlotColor.FOG);
		// sm.setPlotColor(PlotColor.OPAQUE);

		sm.set2DScaling(10.0f);
		
		sm.setPenWidth(1);
		sm.setXlabel("X");
		sm.setYlabel("Y");
		sm.setZlabel("Z");
		
		Font fontAxis = new Font("SansSerif", Font.BOLD, 12);
		sm.setAxisFont(fontAxis);
		
		Font fontLabel = new Font("SansSerif", Font.BOLD, 16);
		sm.setLabelFont(fontLabel);
		
		sm.setLabelColor(Color.black );
		sm.setTicFont(new Font("SansSerif", Font.BOLD, 10));
		
		sm.setLabelOffsetX(1.0f);
		sm.setLabelOffsetY(1.0f);
		sm.setLabelOffsetZ(1.0f);
		sm.setTicOffset(1.0f);
		sm.setAxesFontColor(Color.black );
		sm.setFontColorLabel(Color.black );


		sm.getFrameScale();
		sm.setFrameScale(1.0f);
		
		
		
		p2d_hold = new ArrayList<P2D>();
		p3d_hold = new ArrayList<P3D>();
		f2d_hold = new ArrayList<F2D>();
		h2d_hold = new ArrayList<H2D>();

		return sm;
	};

	public void evaluate() {

		new Thread(new Runnable() {

			public void run() {

				Plotter p = sm.newPlotter(sm.getCalcDivisions());

				if (isEmpty == true) {
					sm.setPlotFunction1(true);
					p.setEmpty();
					return;
				}


				
				if (h2d_hold.size() >0  && f2d_hold.size() > 0 && p2d_hold.size()==0) {
						sm.setPlotFunction1(true);
						sm.setPlotFunction2(true);
					     p.setH2DF2D(h2d_hold,f2d_hold);
					return;
				}
				
				
				if (h2d_hold.size() > 0 && f2d_hold.size()==0 && p2d_hold.size()==0) {
					if (h2d_hold.size() == 1)
						sm.setPlotFunction1(true);
					if (h2d_hold.size() == 2)
						sm.setPlotFunction2(true);
					p.setH2D(h2d_hold);
					return;
				}

				if (f2d_hold.size() > 0 && h2d_hold.size()==0 && p2d_hold.size()==0) {
					if (f2d_hold.size() == 1)
						sm.setPlotFunction1(true);
					if (f2d_hold.size() == 2)
						sm.setPlotFunction2(true);
					    p.setF2D(f2d_hold);
					return;
				}

				if (p2d_hold.size() > 0 && h2d_hold.size()==0 && f2d_hold.size()==0) {
					sm.setPlotFunction1(true);
					p.setP2D(p2d_hold);
				}

				if (p3d_hold.size() > 0) {
					sm.setPlotFunction1(true);
					p.setP3D(p3d_hold);
				}
			}

		}).start();

	}

	public void setSurface(SurfaceModel model) {
		surface.setModel(model);

	}

	/**
	 * Set empty frame
	 */
	public void setEmpty() {
		this.isEmpty = true;

	}

	/**
	 * Set GUI
	 */
	public void setGUI() {

		odialog.setGUI();

	}


	public SurfaceModelCanvas getModel() {

		return sm;
	}

	public void setP2D(P2D h) {
		p2d_hold.add(h);
		isEmpty = false;

	}

	public void setP3D(P3D h) {
		p3d_hold.add(h);
		isEmpty = false;

	}

	public void setH2D(H2D h) {
		h2d_hold.add(h);
		isEmpty = false;

	}

	public void setF2D(F2D h) {
		f2d_hold.add(h);
		isEmpty = false;

	}

	
	public ArrayList<H2D> getH2D() {
		return h2d_hold;
	}
	
	public ArrayList<F2D> getF2D() {
		return f2d_hold;
	}
	
	public void clearF2D() {
		f2d_hold.clear();
	}
	
	public void clearH2D() {
		h2d_hold.clear();
	}
	
	public void clearP2D() {
		p2d_hold.clear();
	}
	public void clearP3D() {
		p3d_hold.clear();
	}
	

        public void setXlabel(String s){

         sm.setXlabel(s);
        }

        public void setYlabel(String s){
         sm.setYlabel(s);

        }

         public void setZlabel(String s){
           sm.setZlabel(s);
        }
	
      public String getXlabel(){

         return sm.getXlabel();
        }

     public String getYlabel(){

         return sm.getYlabel();
        }

  public String getZlabel(){

         return sm.getZlabel();
        }

  public String getTitle(){
          return "";
        }

 public void setTitle(String s){
         //  sm.setTitle(s);
        }

	
	
}
