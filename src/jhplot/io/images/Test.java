package jhplot.io.images;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import org.apache.batik.transcoder.TranscoderException;

    public class Test  {
        public static void main( String [] args ) {
             JFrame frame = new JFrame();
    
             JPanel jPanel2 = new JPanel() { 
                 public void paintComponent( Graphics g ) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D)g;

                    Line2D line = new Line2D.Double(10, 10, 80, 200);
                    g2.setColor(Color.blue);
                    g2.setStroke(new BasicStroke(10));
                    g2.draw(line);
                    g2.drawString("This is my text", 75, 75);
                 }
            };

            jPanel2.setBackground(new java.awt.Color(255, 255, 255));

            frame.setSize(new Dimension(600, 400));

            frame.add( jPanel2);
            frame.setVisible( true );



            ImageType currentImageType;
            String filename="test.svg"; 
            try {
            currentImageType = ImageType.SVG;
            Export.exportComponent(jPanel2, jPanel2.getBounds(), new File(filename), currentImageType);

            filename="test.pdf";
            currentImageType = ImageType.PDF;
            Export.exportComponent(jPanel2, jPanel2.getBounds(), new File(filename), currentImageType);

            filename="test.eps";
            currentImageType = ImageType.EPS;
            Export.exportComponent(jPanel2, jPanel2.getBounds(), new File(filename), currentImageType);


            } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TranscoderException e) {
                        e.printStackTrace();
                    }



        }
    }
