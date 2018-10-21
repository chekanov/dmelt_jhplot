// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de).
// * All rights reserved.


package jhplot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.security.CodeSource;
import java.net.URLDecoder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
* Simple file downloader similar to Unix WGET. Use get(url,true) if you want to replace the existiong file.
* Use get(url) for downloading the file only if it does not exist on the local disk. 
* It can also allows to load a jar file from the internet and attach it to the classpath.
* @author S.Chekanov
*/

public class Web {


	public static final String  fSep = System.getProperty("file.separator");
	public static final String  lSep= System.getProperty("line.separator");


	enum WgetStatus {
	    DownloadedSuccessfully, MalformedUrl, IoException, UnableToCloseOutputStream, AlreadyExists;
	}


	/**
	* Download a file to the local disk. It assumes the current directory.  If file exists locally, the file will not be overwritten. 
	* @param url input URL for download. 
	**/
	public static WgetStatus get(String url) {
		return get(url,false);
	}

	/**
	* Download a file to the local disk. It assumes the current directory. 
	* @param url input URL for download. 
	* @param isOverwrite set to true if the local file need to be replaced from the web.  
	**/
	public static WgetStatus get(String url, boolean isOverwrite) {
		String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		return get(url,s+File.separator+fileName,isOverwrite);

	}



	/**
	* Download a file to the local disk. 
	* @param url input URL for download. 
	* @param saveAsFile file name with the path where the file will be saved
	* @param isOverwrite set to true if the local file need to be replaced from the web. 
	**/
	public static WgetStatus get(String url, String saveAsFile, boolean isOverwrite) {


		if (isOverwrite==false) {
			File f = new File(saveAsFile);
			if(f.exists() && !f.isDirectory()) return WgetStatus.AlreadyExists;
		}


		InputStream httpIn = null;
		OutputStream fileOutput = null;
		OutputStream bufferedOut = null;
		try {
			// check the http connection before we do anything to the fs
			httpIn = new BufferedInputStream(new URL(url).openStream());
			// prep saving the file
			fileOutput = new FileOutputStream(saveAsFile);
			bufferedOut = new BufferedOutputStream(fileOutput, 2048);
			byte data[] = new byte[2048];
			boolean fileComplete = false;
			int count = 0;
			while (!fileComplete) {
				count = httpIn.read(data, 0, 2048);
				if (count <= 0) {
					fileComplete = true;
				} else {
					bufferedOut.write(data, 0, count);
				}
			}
		} catch (MalformedURLException e) {
			return WgetStatus.MalformedUrl;
		} catch (IOException e) {
			return WgetStatus.IoException;
		} finally {
			try {
				bufferedOut.close();
				fileOutput.close();
				httpIn.close();
			} catch (IOException e) {
				return WgetStatus.UnableToCloseOutputStream;
			}
		}
		return WgetStatus.DownloadedSuccessfully;
	}



	/**
	 * Download a jar file from the web and attach to the classpath.
	 * The downloaded jar file will be located inside "lib/user" directory.
	 * The existing file will not trigger the download.
	 * @param url2 input URL of jar for download. 
	 * @return string with the status.
	 **/
	public static String load(String url2) {

		return load(url2,false);


	}




	/**
	 * Download a jar file from the web and attach to the classpath.
	 * The downloaded jar file will be located inside "lib/user" directory.
	 * @param url2 input URL of jar for download. 
	 * @param isOwerwrite true if the existing file needs to be replaced. Set to False if skip download in the case of existing file.
	 * @return string with the status.
	 **/
	public static String load(String url2, boolean isOverwrite) {

		if ( (url2.toLowerCase()).endsWith(".jar") == false) {
			System.err.println("The input file is not a jar file and cannot be loaded");
			return WgetStatus.MalformedUrl.toString();
		}

		String fileName = getCurrentPath()+fSep+"lib"+fSep+"user"+fSep+url2.substring( url2.lastIndexOf('/')+1, url2.length() );
		WgetStatus status= get(url2, fileName, isOverwrite);

		try {
			loadLibrary(new File(fileName));
		} catch ( IOException e) {
		}


		String out=status.toString()+" "+fileName;
		return out;



	}




	/**
	 * Adds the supplied Java Archive library to java.class.path. This is benign
	 * if the library is already loaded.
	 * @param jar input file
	 **/
	public static synchronized void loadLibrary(java.io.File jar) throws IOException
	{
		try {
			/*We are using reflection here to circumvent encapsulation; addURL is not public*/
			java.net.URLClassLoader loader = (java.net.URLClassLoader)ClassLoader.getSystemClassLoader();
			java.net.URL url = jar.toURI().toURL();
			/*Disallow if already loaded*/
			for (java.net.URL it : java.util.Arrays.asList(loader.getURLs())){
				if (it.equals(url)){
					return;
				}
			}
			java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{java.net.URL.class});
			method.setAccessible(true); /*promote the method to public access*/
			method.invoke(loader, new Object[]{url});
		} catch (final java.lang.NoSuchMethodException |
		         java.lang.IllegalAccessException |
		         java.net.MalformedURLException |
		         java.lang.reflect.InvocationTargetException e){
			throw new IOException(e);
		}

	}

	/**
	* Return instalation path.
	* @return installation path 
	**/
	public static  String getCurrentPath(){


		String DirPath = System.getProperty("jehep.home");
		if (DirPath == null) {
			try {
				DirPath=getJarContainingFolder(Web.class);
				DirPath = DirPath.replace(fSep+"lib"+fSep+"system",""); // upper level
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (DirPath==null){ // still something wrong?
				Path currentRelativePath = Paths.get("");
				DirPath = currentRelativePath.toAbsolutePath().toString();
				DirPath = DirPath.replace(fSep+"lib"+fSep+"system",""); // upper level
			}

		}
		return DirPath;
	}


	/**
	        * Get folder of the jar file
	        * @param aclass
	        * @return
	        * @throws Exception
	        */
	private static String getJarContainingFolder(Class aclass) throws Exception {
		CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

		File jarFile;

		if (codeSource.getLocation() != null) {
			jarFile = new File(codeSource.getLocation().toURI());
		}
		else {
			String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
			String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
			jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
			jarFile = new File(jarFilePath);
		}
		return jarFile.getParentFile().getAbsolutePath();
	}


}
