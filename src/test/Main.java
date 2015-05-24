package test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Random;
import jplot.*;
import java.util.*;
import java.awt.*;
import java.lang.*;

import hep.aida.*;
import hep.aida.ref.*;
import javax.swing.*;
import graph.*;
import jhplot.*;
import jhplot.io.*;



/**
 * An example of how to embed a JAIDA IPlotter into your own application.
 */
public class Main
{
  
	
   public static void main(String[] args)
   {


HPlot c1 = new HPlot();
c1.visible();      
c1.setMarginLeft(80); 
c1.setGTitle("Test"); 
c1.setRange(0.8,20, 1, 20); 
c1.setNameX("L [ab^{-1}]"); 
c1.setNameY("m_{&eta;} [TeV]"); 
c1.setLogScale(0,true); 
c1.setLogScale(1,true);

P1D p2=new P1D("data set");
Random rand = new Random();
for (int i=0; i<1000; i++)  {
                    p2.add(10*rand.nextGaussian(),20+rand.nextGaussian()*10);
};
  
c1.draw(p2);

c1.export("test.png");
 
  }

}
  
