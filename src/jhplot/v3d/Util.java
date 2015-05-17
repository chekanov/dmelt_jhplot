/*
 *  Util in org.jpws.front.util
 *  file: Util.java
 * 
 *  Project Jpws-Front
 *  @author Wolfgang Keller
 *  Created 28.09.2004
 *  Version
 * 
 *  Copyright (c) 2005 by Wolfgang Keller, Munich, Germany
 * 
 This program is not freeware software but copyright protected to the author(s)
 stated above. However, you can use, redistribute and/or modify it under the terms 
 of the GNU General Public License as published by the Free Software Foundation, 
 version 2 of the License.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place - Suite 330, Boston, MA 02111-1307, USA, or go to
 http://www.gnu.org/copyleft/gpl.html.
 */

package jhplot.v3d;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.BitSet;
import java.util.zip.CRC32;


/**
 * Various useful functions of general purpose.
 * 
 * @author Wolfgang Keller / sergei chekanov
 */
public final class Util
{
	/**
	 * 
	 */
	private Util()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Calculates the X and Y coordinates that a child component should have to be centered
	 * within its parent.  This method does not try to constrain the calculation so that
	 * the point remains wholly onscreen.
	 * 
	 * @param rec  the bounding rectangle of the parent.
	 * @param dim  the dimensions of the child.
	 * 
	 * @return the X and Y coordinates the child should have.
	 */
	public static Point centreWithin( Rectangle rec, Dimension dim )
	{
		Point	pos;

		pos		= new Point();
		pos.x	= rec.x + ((rec.width - dim.width) >> 1);
		pos.y	= rec.y + ((rec.height - dim.height) >> 1);
		
		return pos;
	}


        // put right 
        public static Point rightWithin( Rectangle rec, Dimension dim )
        {
                Point   pos;

                pos             = new Point();
         //       pos.x   = rec.x + ((rec.width - dim.width) >> 1);
         //       pos.y   = rec.y + ((rec.height - dim.height) >> 1);
                 pos.x   = rec.x + rec.width;
                 pos.y   = rec.y;

                return pos;
        }



     // put below 
     public static Point belowWithin( Rectangle rec, Dimension dim )
        {
                Point   pos;
                pos             = new Point();
         //       pos.x   = rec.x + ((rec.width - dim.width) >> 1);
         //       pos.y   = rec.y + ((rec.height - dim.height) >> 1);
                 pos.x   = rec.x;
                 pos.y   = rec.y+rec.height;
                return pos;
        }



	/**
	 * Centres the child component within its parent.
	 * 
	 * @param parent  the parent component.
	 * @param child   the child component.
	 */
	public static void centreWithin( Component parent, Component child )
	{
      Point p;
      Dimension screen, win;
      int diff;
      
      screen = Toolkit.getDefaultToolkit().getScreenSize();
      win = child.getSize();
      p = centreWithin( parent.getBounds(), win );
      if ( (diff = screen.width - p.x - win.width) < 0 )
         p.x += diff;
      if ( (diff = screen.height - p.y - win.height) < 0 )
         p.y += diff;
      p.x = Math.max( 0, p.x );
      p.y = Math.max( 0, p.y );
		child.setLocation( p );
	}

/**
         * Puts to the right 
         *
         * @param parent  the parent component.
         * @param child   the child component.
         */

 
// put right  
      public static void rightWithin( Component parent, Component child )
        {
      Point p;
      Dimension screen, win;
      int diff;

      screen = Toolkit.getDefaultToolkit().getScreenSize();
      win = child.getSize();
      p = rightWithin( parent.getBounds(), win );
      if ( (diff = screen.width - p.x - win.width) < 0 )
         p.x += diff;
      if ( (diff = screen.height - p.y - win.height) < 0 )
         p.y += diff;
      p.x = Math.max( 0, p.x );
      p.y = Math.max( 0, p.y );
                child.setLocation( p );
        }


// put below
    public static void belowWithin( Component parent, Component child )
        {
      Point p;
      Dimension screen, win;
      int diff;

      screen = Toolkit.getDefaultToolkit().getScreenSize();
      win = child.getSize();
      p = belowWithin( parent.getBounds(), win );
      if ( (diff = screen.width - p.x - win.width) < 0 )
         p.x += diff;
      if ( (diff = screen.height - p.y - win.height) < 0 )
         p.y += diff;
      p.x = Math.max( 0, p.x );
      p.y = Math.max( 0, p.y );
                child.setLocation( p );
        }



 
   public static String fileNameOfPath ( String path )
   {
      File f;
      
      if ( path == null )
         return "";
      
      f = new File( path );
      return f.getName();
   }
   
   public static String pathNameOfPath ( String path )
   {
      File f;
      
      if ( path == null )
         return "";
      
      f = new File( path );
      return f.getParent();
   }
   
   /** Renders a string based on <code>text</code> where any occurence of
    *  <code>token</code> is replaced by <code>substitute</code>.
    *  @return String
    */
   public static String substituteText ( String text, String token, String substitute )
   {
      int index;

      if ( token == null || substitute == null || (index=text.indexOf( token )) < 0 )
         return text;

      while ( index > -1 )
      {
         text = text.substring( 0, index ) + substitute +
                text.substring( index+token.length() );
         index = text.indexOf( token );
      }
      return text;
   }  // substituteText

   /** Renders a string based on <code>text</code> where any occurence of
    *  <code>token</code> is replaced by <code>substitute</code>.
    *  @return String
    */
   public static String substituteTextS ( String text, String token, 
         String substitute )
   {
      int index;

      if ( token == null || substitute == null || (index=text.indexOf( token )) < 0 )
         return text;

      if ( index > -1 )
      {
         text = text.substring( 0, index ) + substitute +
                text.substring( index+token.length() );
      }
      return text;
   }  // substituteText

   /** Substitutes conflicting characters in <code>text</code> with html
    *  masked expressions. The returned string is displayable in html interpreting
    *  components.
    * 
    * @param text
    * @return
    */
   public static String htmlEncoded ( String text )
   {
      StringBuffer b;
      int i, len;
      char c;
      
      len = text.length();
      b = new StringBuffer( len * 3 );
      for ( i = 0; i < len; i++ )
      {
         c = text.charAt( i );
         switch ( c )
         {
         case '<':  b.append( "&lt;" );
         break;
         case '>':  b.append( "&gt;" );
         break;
         case '&':  b.append( "&amp;" );
         break;
         case '"':  b.append( "&quot;" );
         break;
         default: b.append( c );
         }
      }
      return b.toString();
   }
   
   /** Copies the contents of any disk file to a specified output file.  If the
    *  output file is a relative path, it is made absolute against the directory
    *  of the input file.  The output file will be overwritten if it exist.
    *  A CRC32 check is performed to compare source and copy after the copy process
    *  and if results negative a <code>StreamCorruptedException</code> is thrown.
    *  Function reports errors to <code>System.err</code>.
    *  @param inputFile a File object
    *  @param outputFile a File object
    *  @throws java.io.IOException if the function could not be completed
    *  because of an IO or CRC check error
    */
   public static void copyFile( File inputFile, File outputFile )
                                   throws java.io.IOException
   {
      File parent;
      FileInputStream in = null;
      FileOutputStream out = null;
      CRC32 crcSum;
      int writeCrc;
//      long time;

      // control parameter
      if ( inputFile == null | outputFile == null )
         throw new IllegalArgumentException( "null pointer" );
      if ( inputFile.equals( outputFile ) )
         throw new IllegalArgumentException( "illegal self reference" );

      byte[] buffer = new byte[2048];
      int len;

      try {
         // make output file absolute (if not already)
         parent=inputFile.getAbsoluteFile().getParentFile();
         if ( !outputFile.isAbsolute() )
            outputFile = new File( parent, outputFile.getPath() );

         // make sure the directory for the output file exists
         ensureFilePath( outputFile, parent );

         // create file streams
         in = new FileInputStream(inputFile);
         out = new FileOutputStream(outputFile);
//         time = inputFile.lastModified();
         crcSum = new CRC32();

         while ((len = in.read(buffer)) != -1)
            {
            out.write(buffer, 0, len);
            crcSum.update( buffer, 0, len );
            }

         in.close();
         out.close();
         writeCrc = (int)crcSum.getValue();
//         outputFile.setLastModified( time );

         // control output CRC
         in = new FileInputStream( outputFile );
         crcSum.reset();
         while ((len = in.read(buffer)) != -1)
            crcSum.update( buffer, 0, len );
         if ( writeCrc != (int)crcSum.getValue() )
            throw new StreamCorruptedException( "bad copy CRC" );
         }

      catch (IOException e)
         {
         System.err.println(
            "*** error during file copy: " + outputFile.getAbsolutePath());
         System.err.println(e);
         throw e;
         }
      finally
         {
         if ( in != null )
            in.close();
         if ( out != null )
            out.close();
         }
   } // copyFile

   /** Ensures the existence of the directory which may be part of the path
    *  specification of parameter <code>file</code>. If the specified  file is a
    *  relative path, it is made absolute against <code>defaultDir</code>. If
    *  <code>defaultDir</code> is <b>null</b> the System property "user.dir" is
    *  assumed as default directory.
    *  @return <b>true</b> if and only if the parent directory of the specified file
    *  exists after this function terminates
    */
   public static boolean ensureFilePath ( File file, File defaultDir )
   {
   File parent;

   if ( (parent=file.getParentFile()) != null )
      return ensureDirectory( parent, defaultDir );
   return true;
   }  // ensureFilePath

   /** Ensures the existence of the directory specified by the parameter. If the
    *  directory does not exist, an attempt is performed to create it including all
    *  necessary parent directories that may be implied by the specification.
    *  @param dir File specifying the intended directory; if the specified
    *  path is a relative path, it is made absolute against <code>defaultDir</code>.
   *   If <code>defaultDir</code> is <b>null</b> the System directory "user.dir" is
    *  assumed as default.
    * @param defaultDir default directory.
    *  @return <b>true</b> if and only if the specified file exists and
    *  is a directory after this function terminates
    */
   public static boolean ensureDirectory ( File dir, File defaultDir )
   {
      boolean success = true;

      if ( dir == null )
         throw new IllegalArgumentException( "dir = null" );

      if ( !dir.isAbsolute() )
         dir = new File( defaultDir, dir.getPath() );

      if ( !dir.isDirectory() )
         {
         success = !dir.isFile() && dir.mkdirs();
         if ( !success )
             System.err.println("failed while trying to create directory: "+ dir.toString() );
         }

   return success;
   }  // ensureDirectory

   public static int textVariance ( char[] ca )
   {
      BitSet set = new BitSet();
      int i;
      
      for ( i = 0; i < ca.length; i++ )
         set.set( ca[i] );
      return set.cardinality();         
   }


/**
   *  Deletes all files and subdirectories under dir.
   *  Returns true if all deletions were successful.
   *  If a deletion fails, the method stops attempting to delete and returns false.
*/
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }




}
