// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

// public class TestCanvas extends Applet
public class TestCanvas {
   

    public static void main(String args[]) 
    {

        HViewPanel window = new HViewPanel();
      
        JFrame f = new JFrame("A JFrame");
         f.setSize(450, 450);
         // f.setLocation(300,200);
         f.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         f.getContentPane().add(BorderLayout.CENTER, window);
         f.setVisible(true);
         
     
    }

}

class HViewPanel extends JPanel
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Canvas3d canv;
    private Button but[];
    private Matrix3d orot, otrans, oscale;
    private float ominScale, omaxScale;
    Model3d md = null;
    
     
    public HViewPanel() 
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());
      
 

//      this is necessary if you want to mix AWT with swing
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

      
        

         md = new Model3d(null, 400, 300);
         orot   = new Matrix3d(md.rot);
         otrans = new Matrix3d(md.trans);
         oscale = new Matrix3d(md.scale);
         ominScale = md.minScale;
         omaxScale = md.maxScale;
       
         
        Vector3d vcyl = new Vector3d(40,0,0);

        Cube cb3 = new Cube(md,40);
        cb3.setRot(45,45,45);
        cb3.setTrans(vcyl.x, vcyl.y, vcyl.z);
        md.addObject(cb3);



        cb3 = new Cube(md,40);
        cb3.setRot(-25,25,40);
        cb3.setTrans(10, 10, 10);
        md.addObject(cb3);



        // vcyl = new Vector3d(0,40,0);
        Sphere oo = new Sphere(md, 30.0f, 20, 20);
        oo.setTrans(vcyl.x, vcyl.y, vcyl.z);
  //      oo.setRot(0, 90, 0);
        md.addObject(oo);


            Cone o = new Cone(md, 30, 100, 50);
            o.setTrans(-20, 40, 0);
            // o.setRot(90, 0, 0);
            o.setColor(Color.red);
            md.addObject(o);





        Vector3d v[] = new Vector3d[4];
        v[0] = new Vector3d(0, 0, 0);
        v[1] = new Vector3d(6, 0, 0);
        v[2] = new Vector3d(6, 1, 0);
        v[3] = new Vector3d(0, 1, 0);
        Vector3d d = new Vector3d(0, 6, 6);
        Prism o1  = new Prism(md, v, 4, d);
        o1.setTrans(-50, 40, 0);
        md.addObject(o1);


        v[0] = new Vector3d(0, 0, 2);
        v[1] = new Vector3d(6, 0, 2);
        v[2] = new Vector3d(6, 1, 2);
        v[3] = new Vector3d(0, 1, 2);
        d = new Vector3d(3, 0.5f, 4);
        Pyramid p3 = new Pyramid(md, v, 4, d);
        p3.setTrans(30, 20, 0);
        md.addObject(p3);



        Cylinder  o3 = new Cylinder(md, 40, 100, 2);
        o3.setTrans(-1, 0, 0);
        o3.setRot(0, 90, 0);
        o3.setColor(Color.yellow);
        md.addObject(o3);


        canv = new Canvas3d(md);
        add("Center", canv);



          validate();

    }

  

    
    public boolean action(Event  evt, Object  what)
    {
        if (md == null)
            return false;
        String s = (String) evt.arg;
        if (s.equals("Home"))
        {
            md.rot.copy(orot);
            md.trans.copy(otrans);
            if (md.persp)
            {
                md.minScale = ominScale;
                md.maxScale = omaxScale;
                md.computeMatrix();
            }
            else
                md.scale.copy(oscale);
        }
        else if (s.equals("XRot"))
            md.incRot(10,0,0);
        else if (s.equals("YRot"))
            md.incRot(0,10,0);
        else if (s.equals("ZRot"))
            md.incRot(0,0,10);
        else if (s.equals("Left"))
            md.incTrans(md.bb.getWidth()/10,0,0);
        else if (s.equals("Right"))
            md.incTrans(-md.bb.getWidth()/10,0,0);
        else if (s.equals("Up"))
            md.incTrans(0,-md.bb.getHeight()/10,0);
        else if (s.equals("Down"))
            md.incTrans(0,md.bb.getHeight()/10,0);
        else if (s.equals("ZoomIn"))
        {
            if (md.persp)
            {
                md.minScale /= 1.1f;
                md.maxScale /= 1.1f;
                md.computeMatrix();
            }
            else
            {
                float f = 1 / 1.1f;
                md.incScale(f, f, f);
            }
        }
        else if (s.equals("ZoomOut"))
        {
            if (md.persp)
            {
                md.minScale *= 1.1f;
                md.maxScale *= 1.1f;
                md.computeMatrix();
            }
            else
            {
                float f = 1.1f;
                md.incScale(f, f, f);
            }
        }
        else
            return false;
        canv.repaint();
        return true;
    }

    
    
   
    
    
    
    
    
    
    
}
