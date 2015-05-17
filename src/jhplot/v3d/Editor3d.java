// * This code is licensed under:
// * jeHEP License, Version 1.0
// * - for license details see http://jehep.sourceforge.net/license.html 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.

package jhplot.v3d;

import java.awt.*;
import javax.swing.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class Editor3d extends JDialog 
              implements ActionListener {
    /**
	 * 
	 */

    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;
    private JButton closeButton;
    private  Canvas3d     win;
    private  Model3d md;
    private  JButton jb1,jb2,jb3,jb4,jb5,jb6,jb7,jb8,jb9,jb10; 

    public Editor3d(Canvas3d  win, Model3d md) {
    this.win=win;
    this.md = md;

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

   setTitle("");
   setModal( true );
   setResizable( true );



   // make sure the dialog is not too big
   Dimension size = new Dimension(80, 400);

//   setLocationRelativeTo(parent);

  JPanel topPanel = new JPanel();
  JPanel lowerPanel = new JPanel();
  lowerPanel.setPreferredSize(new Dimension(80, 40));


  closeButton = new JButton();
  closeButton.setText("Exit");
  closeButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             setVisible(false); dispose();
        }  });

  lowerPanel.add(closeButton, null);
  topPanel.setLayout( new BorderLayout() );
  getContentPane().add( topPanel, java.awt.BorderLayout.CENTER );
  getContentPane().add( lowerPanel, java.awt.BorderLayout.SOUTH );
  topPanel.setLayout( new GridLayout(10,0) );
  
  
  jb1 = new JButton("Home");
  jb2 = new JButton("XRot");
  jb3 = new JButton("YRot");
  jb4 = new JButton("ZRot");
  jb5 = new JButton("Left");
  jb6 = new JButton("Right");
  jb7 = new JButton("Up");
  jb8 = new JButton("Down");
  jb9 = new JButton("ZoomIn");
  jb10 = new JButton("ZoomOut");

  jb1.addActionListener(this);
  jb2.addActionListener(this);
  jb3.addActionListener(this);
  jb4.addActionListener(this);
  jb5.addActionListener(this);
  jb6.addActionListener(this);
  jb7.addActionListener(this);
  jb8.addActionListener(this);
  jb9.addActionListener(this);
  jb10.addActionListener(this);




  topPanel.add(jb1);
  topPanel.add(jb2);
  topPanel.add(jb3); 
  topPanel.add(jb4);
  topPanel.add(jb5);
  topPanel.add(jb6);
  topPanel.add(jb7);
  topPanel.add(jb8);
  topPanel.add(jb9);
  topPanel.add(jb10);


// set visible and put on center

    Util.rightWithin( win, this );

    setSize(size);
    pack();
    this.setVisible(true);
    }



 public void actionPerformed(ActionEvent e) {


     if (md == null) return;


      String s = e.getActionCommand();


        if (s.equals("Home"))
        {
            md.rot.copy(win.orot);
            md.trans.copy(win.otrans);
            if (md.persp)
            {
                md.minScale = win.ominScale;
                md.maxScale = win.omaxScale;
                md.computeMatrix();
            }
            else
                md.scale.copy(win.oscale);
        }
        else if (s.equals("ZRot"))
            md.incRot(10,0,0);
        else if (s.equals("XRot"))
            md.incRot(0,10,0);
        else if (s.equals("YRot"))
            md.incRot(0,0,10);
        else if (s.equals("Right"))
            md.incTrans(md.bb.getWidth()/10,0,0);
        else if (s.equals("Left"))
            md.incTrans(-md.bb.getWidth()/10,0,0);
        else if (s.equals("Down"))
            md.incTrans(0,-md.bb.getHeight()/10,0);
        else if (s.equals("Up"))
            md.incTrans(0,md.bb.getHeight()/10,0);
        else if (s.equals("ZoomOut"))
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
        else if (s.equals("ZoomIn"))
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
        win.repaint();



  }





}
