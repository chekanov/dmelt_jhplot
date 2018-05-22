package jhplot;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
 
/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 *
 */
public class IO {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;



   /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath input zip file 
     * @param destDirectory output directory 
     */
    public static String unzip(String zipFilePath, String destDirectory) throws IOException {


    String tmp="Error unziping: "+zipFilePath;
    try { 
         int nn=unzipFile(zipFilePath, destDirectory);
         tmp="-> Input zip file:"+zipFilePath+"\n";
         tmp=tmp+"-> Successfully unziped "+Integer.toString(nn)+" files to "+destDirectory;
     } catch (IOException e) {
         e.printStackTrace();
     } 
      return tmp;
    }


     /**
     * Extracts a zip file specified by the zipFilePath to the curent directory.
     * @param zipFilePath input zip file  
     */
    public static String unzip(String zipFilePath) throws IOException {


   
    String tmp="Error unziping "+zipFilePath;
    try { 
        File ff=new File(zipFilePath);
        String dir= ff.getAbsoluteFile().getParent();
        int nn=unzipFile(zipFilePath, dir);
        tmp="-> Input zip file:"+zipFilePath+"\n";
        tmp=tmp+"-> Successfully unziped "+Integer.toString(nn)+" files to "+dir;
     } catch (IOException e) {
         e.printStackTrace();
     } 
      return tmp;
    }



    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    private static int unzipFile(String zipFilePath, String destDirectory) throws IOException {

        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
     
        int ntot=0; 
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
                ntot++;
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        return ntot;
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
