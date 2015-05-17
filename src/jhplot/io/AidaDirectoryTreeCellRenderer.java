package jhplot.io;

import hep.aida.*;
import hep.aida.ref.histogram.*;
import javax.swing.*;
import javax.swing.tree.*;


/**
 * A TreeCellRenderer for Root TNamed objects.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootDirectoryTreeCellRenderer.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AidaDirectoryTreeCellRenderer extends DefaultTreeCellRenderer
{
  

   private static final long serialVersionUID = 1L;
   private final static Icon h1Icon = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/h1_t.gif"));
   private final static Icon h2Icon = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/h2_t.gif"));
   private final static Icon h3Icon = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/h3_t.gif"));
   private final static Icon icloud1d = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/icloud1d.png"));
   private final static Icon icloud2d = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/icloud2d.png"));
   private final static Icon icloud3d = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/icloud3d.png"));
   private final static Icon idatapointset = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/idatapointset.png"));
   private final static Icon iprofile1d = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/iprofile1d.png"));
   private final static Icon ifunction1d = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/ifunction1d.png"));
   private final static Icon ituplecolumn = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/ituplecolumn.png"));
   private final static Icon folderclosed = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/folderclosed.png"));
   private final static Icon folderopen = new ImageIcon(AidaDirectoryTreeCellRenderer.class.getResource("icons/folderopen.png"));
 
   public java.awt.Component getTreeCellRendererComponent(JTree p1, Object p2, boolean p3, boolean p4, boolean p5, int p6, boolean p7)
   {
      super.getTreeCellRendererComponent(p1, p2, p3, p4, p5, p6, p7);


      DefaultMutableTreeNode Selnode = (DefaultMutableTreeNode)p2;
      Object  ob = Selnode.getUserObject();
      
      jhplot.io.BrowserAida.OidNode iod=(jhplot.io.BrowserAida.OidNode)ob;
      String stype=iod.getType();
     
      
      
      if (stype.equals("dir")) {
           setIcon(folderopen);
           setText(iod.getName() + " (" + stype + ")"); 
      } else if (stype.equals("IHistogram1D")) {
    	  setIcon(h1Icon);
          setText(iod.getName() + " (" + stype + ")"); 
      } else if (stype.equals("IHistogram2D")) { 
          setText(iod.getName() + " (" + stype + ")"); 
    	  setIcon(h2Icon);
      } else if (stype.equals("IHistogram3D")) { 
    	  setIcon(h3Icon);
          setText(iod.getName() + " (" + stype + ")"); 
      } else if (stype.equals("ICloud1D")) { 
    	  setIcon(icloud1d);
          setText(iod.getName() + " (" + stype + ")"); 
      } else if (stype.equals("ICloud2D")) { 
       	  setIcon(icloud2d); 
          setText(iod.getName() + " (" + stype + ")"); 
      } else if (stype.equals("ICloud3D")) {
          setIcon(icloud3d);
     } else if (stype.equals("IDataPointSet")) {
          setIcon(idatapointset);
          setText(iod.getName() + " (" + stype + ")"); 
      } else if (stype.equals("IFunction")) {
          setIcon(ifunction1d);
     } else if (stype.equals("IProfile1D")) {
          setIcon(iprofile1d);
          setText(iod.getName() + " (" + stype + ")"); 
     } else if (stype.equals("IProfile2D")) {
          setIcon(iprofile1d);
          setText(iod.getName() + " (" + stype + ")"); 
     } else if (stype.equals("ITuple")) {
          setIcon(ituplecolumn);
          setText(iod.getName() + " (" + stype + ")"); 
     }
	
      
      
     return this;
   }
}
