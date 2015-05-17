package jhplot.io;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import javax.swing.JOptionPane;



/**
 * Some generic methods to read data and to return  BufferedReader.
 * @author S.Chekanov
 *
 */

public class PReader implements Serializable{

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;





	/**
	 * Read data from a gzipped file (GNU gzip/gunzip commands in linux)
	 *  
	 * @param sfile
	 *            File name with input (extension .gz)
	 * @return zero if success
	 */
	static public  BufferedReader readGZip(File sfile) {

		try {
			FileInputStream fin = new FileInputStream(sfile);
			GZIPInputStream gzis = new GZIPInputStream(fin);
			InputStreamReader xover = new InputStreamReader(gzis);
			BufferedReader is = new BufferedReader(xover);	
			return is;
		} catch (FileNotFoundException e) {
			ErrorMessage(e.toString());
			
		} catch (IOException e) {
			ErrorMessage(e.toString());
		}

		return null;
	}
	
	
	

	/**
	 * Read data from URL.
	 * 
	 * @param url  URL location of input file
	 */
		
	static public BufferedReader  read(URL url)
	{ 
	  
	  try
	  {  
        URLConnection urlConn = url.openConnection(); 
	    urlConn.setDoInput(true); 
	    urlConn.setUseCaches(false);
	    BufferedReader in = new BufferedReader(
                new InputStreamReader(urlConn.getInputStream()));
	    return in;
	  } catch (IOException e) { ErrorMessage(e.toString());} 

	   return null;
	  
	}
	

	
	/**
	 * Read from a file. 
	 * 
	 * 
	 * @param sfile
	 *            input file
	 *            
	 * @return BufferedReader
	 */
	static public BufferedReader read(File sfile) {


		try {

			FileReader inF = new FileReader(sfile);
			BufferedReader br = new BufferedReader(inF);
			return br;		
		} catch (FileNotFoundException e) {
			ErrorMessage(e.toString());
		} 

		return null;

	}


	/**
	 * Read data from a given location.
	 * It can read URL if the string starts from http: or ftp:, otherwise a file on the file system is assumed.
	 * @param sfile
	 *            File name or URL with input
	 * @return zero if success
	 */
	static public BufferedReader  read(String sfile) {

		sfile=sfile.trim();
		
		
        if (sfile.startsWith("http:") || sfile.startsWith("ftp:") || sfile.startsWith("https:")) {
			try {
				return read(new URL(sfile));
			} catch (MalformedURLException e1) {
				ErrorMessage(e1.toString());
			}
        } else {
        
        	return read(new File(sfile));
        }
		
		return null;

	}

	
	/**
	 * Read data from URL.
	 * 
	 * @param url  URL location of input file
	 */
		
	static public   BufferedReader readGZip(URL url) { 
	  

	  try
	  {  
        URLConnection urlConn = url.openConnection(); 
	    urlConn.setDoInput(true); 
	    urlConn.setUseCaches(false);
	    
		GZIPInputStream gzis = new GZIPInputStream(urlConn.getInputStream());
		InputStreamReader xover = new InputStreamReader(gzis);
		return new BufferedReader(xover);
	    
	  } catch (IOException e) {e.toString();} 

	  return null;
	  
	}

	
	
	
	/**
	 * Read data from either URL or file
	 * @param sfile can be either URL or a file location
	 * 
	 * @return BufferedReader 
	 */
	static public  BufferedReader readGZip(String sfile) {



		sfile=sfile.trim();
		
		
        if (sfile.startsWith("http") || sfile.startsWith("ftp")) {
			try {
				return readGZip(new URL(sfile));
			} catch (MalformedURLException e) {
				ErrorMessage(e.toString());

			}
        } else {
        	 return readGZip(new File(sfile));
        }
		
		return null;

		
	}
	
	
	
			
	
	
	
	
	
	
	
	/**
	 * Generate error message
	 * 
	 * @param a
	 *            Message
	 */

	static private void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	
	
	
	
	
	
	
}
