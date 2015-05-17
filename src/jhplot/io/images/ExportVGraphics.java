package jhplot.io.images;


import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.render.ps.PSTranscoder;
import org.apache.fop.svg.PDFTranscoder;

import org.freehep.graphicsbase.util.export.*;

//import org.freehep.util.export.ExportFileType;
import javax.swing.filechooser.*;

/**
 * Export graphics in different formats.
 * @author S.Chekanov
 *
 */
public class ExportVGraphics {
	
	
	/**
	 * Fast export of the canvas to an image file. 
	 * The correct export is only possible if Graphics2D is extended by VectorGraphics of FreeHep.
	 * This depends on the
	 * extension: <br>
	 * SVG - Scalable Vector Graphics (SVG) <br>
	 * SVGZ - compressed SVG<br>
	 * JPG <br>
	 * PNG <br>
	 * PDF <br>
	 * EPS <br>
	 * PS. <br>
	 * Note: EPS, PDF and PS are derived from SVG. Use SVGZ to have smaller file
	 * sizes.
	 * <p>
	 * No questions will be asked and existing file will be rewritten
	 * 
	 * @param file
	 *            Output file with the proper extension (SVG, SVGZ, JPG, PNG,
	 *            PDF, EPS, PS). If no extension, PNG file is assumed.
	 *            
	 *   @param     CanvasPane component to export.      
	 */

	public static void export(Component CanvasPanel, final String rootKey, final String file) {

		
	
		 final String SAVE_AS_TYPE = rootKey + ".SaveAsType";
		 final String SAVE_AS_FILE = rootKey + ".SaveAsFile";


		File f = new File(file);
		ExportFileType t = null;

		int dot = file.lastIndexOf('.');
		String base = (dot == -1) ? file : file.substring(0, dot);
		String fext = (dot == -1) ? "" : file.substring(dot + 1);
		fext = fext.trim();

                // System.out.println("Saving="+fext);

		// fixing vector formats
		boolean isSVGZ = false;
		if (fext.equalsIgnoreCase("svgz")) {
			isSVGZ = true;
		}
		boolean isEPS = false;
		if (fext.equalsIgnoreCase("eps")) {
			isEPS = true;
		}
		boolean isPS = false;
		if (fext.equalsIgnoreCase("ps")) {
			isPS = true;
		}
		boolean isPDF = false;
		if (fext.equalsIgnoreCase("pdf")) {
			isPDF = true;
		}

		boolean isJPG = false;
		if (fext.equalsIgnoreCase("jpg") || fext.equalsIgnoreCase("jpeg")) {
			isJPG = true;
		}

		List<ExportFileType> list = ExportFileType.getExportFileTypes();
		Iterator<ExportFileType> iterator = list.iterator();
		while (iterator.hasNext()) {
			t = (ExportFileType) iterator.next();
			String[] ext = t.getExtensions();
			if (fext.equalsIgnoreCase(ext[0]))
				break; // not svgz

			// overwrite this since we use svn conversions
			if (isSVGZ && ext[0].equalsIgnoreCase("svg"))
				break;
			if (isEPS && ext[0].equalsIgnoreCase("svg"))
				break;
			if (isPDF && ext[0].equalsIgnoreCase("svg"))
				break;
			if (isPS && ext[0].equalsIgnoreCase("svg"))
				break;
			if (isJPG && ext[0].equalsIgnoreCase("svg"))
				break;

			// the rest is native
                         if (isSVGZ == false && isEPS == false && isPDF == false && isPS == false && isJPG == false)
			 if (fext.equalsIgnoreCase(ext[0]))
				break; // not any of the above
			t = null;
		}

		if (t == null) {
			String tmp = "svgz; ";
			iterator = list.iterator();
			while (iterator.hasNext()) {
				t = (ExportFileType) iterator.next();
				String[] ext = t.getExtensions();
				tmp = tmp + ext[0] + "; ";
				// String a=t.getDescription();
			}
			System.err.println("File format is not supported!\nTry: " + tmp);
			return;
		}

		final Properties props = new Properties();
		String a = t.getDescription();
		props.put(SAVE_AS_FILE, file);
		props.put(SAVE_AS_TYPE, a);

		final String metadata = "(C) DataMelt. jWork.ORG (http://jwork.org/scavis/). S.Chekanov";

		// first, SVGZ
		if (isSVGZ) {
			try {

				FileOutputStream fos = new FileOutputStream(file);
				GZIPOutputStream gzos = new GZIPOutputStream(fos);
				t.exportToFile(gzos, (Component) CanvasPanel,
						(Component) CanvasPanel, props, metadata);
				gzos.close();
				fos.flush();
				fos.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e.toString());
			}

		}

		// then EPS
		if (isJPG) {
			// then to EPS (in memory)
			try {

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				t.exportToFile(bytes, (Component) CanvasPanel,
						(Component) CanvasPanel, props, metadata);
				bytes.close();
				TranscoderInput input = new TranscoderInput(
						new ByteArrayInputStream(bytes.toByteArray()));
				FileOutputStream os = new FileOutputStream(file);
				JPEGTranscoder trans = new JPEGTranscoder();
				trans.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
			    trans.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(1000));
			    trans.addTranscodingHint(JPEGTranscoder.KEY_ALLOWED_SCRIPT_TYPES, "*");
			    trans.addTranscodingHint(JPEGTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN, new Boolean(true));
			    trans.addTranscodingHint(JPEGTranscoder.KEY_EXECUTE_ONLOAD, new Boolean(true));
				
				TranscoderOutput output = new TranscoderOutput(os);
				trans.transcode(input, output);
				os.flush();
				os.close();

				return;

			} catch (IOException | TranscoderException e) {
				System.err.println("Error while converting  "
						+ e.getLocalizedMessage());
			}
		}

		// then EPS
		if (isEPS) {
			// then to EPS (in memory)
			try {

				ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
				t.exportToFile(bytes2, (Component) CanvasPanel,
						(Component) CanvasPanel, props, metadata);
                                bytes2.flush();
                                byte[] bb=bytes2.toByteArray();
                                bytes2.close();
                                if (bb==null) return;
                                if (bb.length<1) return;

				TranscoderInput input = new TranscoderInput(
						new ByteArrayInputStream(bb));
				FileOutputStream os = new FileOutputStream(file);
				EPSTranscoder trans = new EPSTranscoder();
				TranscoderOutput output = new TranscoderOutput(os);
				trans.transcode(input, output);
				os.flush();
				os.close();

				return;

			} catch (IOException e) {
				System.err.println("DataMelt ExportVGraphics: IOException: Error while converting  "
						+ e.getLocalizedMessage());
                        } catch (TranscoderException e) {
                          //      System.err.println("DataMelt ExportVGraphics: Error while converting  "
                          //                     + e.getLocalizedMessage());
                        }


		}

		// then PS
		if (isPS) {

			// then to PS (in memory)
			try {
				// convert first to SVG
				ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
				t.exportToFile(bytes2, (Component) CanvasPanel,
						(Component) CanvasPanel, props, metadata);

                                bytes2.flush();
                                byte[] bb=bytes2.toByteArray();
                                bytes2.close();
                                if (bb==null) return;
                                if (bb.length<1) return;

				FileOutputStream os = new FileOutputStream(file);
				TranscoderInput input = new TranscoderInput(
						new ByteArrayInputStream(bb));
				PSTranscoder trans = new PSTranscoder();
				TranscoderOutput output = new TranscoderOutput(os);
				trans.transcode(input, output);
				os.flush();
				os.close();
				return;

                        } catch (IOException e) {
                                System.err.println("DataMelt ExportVGraphics: IOException: Error while converting  "
                                                + e.getLocalizedMessage());
                        } catch (TranscoderException e) {
                                //System.err.println("DataMelt ExportVGraphics: Error while converting  "
                                //                + e.getLocalizedMessage());
                        }


		}

		// then PDF
		if (isPDF) {

			// then to PDF (in memory)
			try {


				ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
				t.exportToFile(bytes2, (Component) CanvasPanel,
						(Component) CanvasPanel, props, metadata);
                                bytes2.flush();
                                byte[] bb=bytes2.toByteArray();
                                bytes2.close();
                                if (bb==null) return;
                                if (bb.length<1) return;

                                PDFTranscoder trans = new PDFTranscoder();
				TranscoderInput input = new TranscoderInput(
						new ByteArrayInputStream(bb));

                                FileOutputStream os = new FileOutputStream(file);
				TranscoderOutput output = new TranscoderOutput(os);
				trans.transcode(input, output);
				os.flush();
				os.close();
				return;

                          } catch (IOException e) {
                                System.err.println("DataMelt ExportVGraphics: IOException: Error while converting  "
                                                + e.getLocalizedMessage());
                        } catch (TranscoderException e) {
                           //     System.err.println("DataMelt ExportVGraphics: Error while converting  "
                           //                     + e.getLocalizedMessage());
                        }


		} // end PDF

		// now try everything

		try {

			t.exportToFile(f, (Component) CanvasPanel, (Component) CanvasPanel,
					props, metadata);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.toString());
			// e.printStackTrace();
		}

	}





         /**
         * Exports the image to some graphic format.
         * @param component to export to image
         * @param rootKey is a key from where export is happeing
         * @param frame parent fram if any.
         */
        public static void exportDialog(final Component component, final String rootKey, final JFrame frame) {


          JFileChooser fileChooser = jhplot.gui.CommonGUI.openImageFileChooser(frame);

          // set predefined file
          //File file = new File("scavis");
          //fileChooser.setSelectedFile(file);

           if (fileChooser.showDialog(frame, "Export to ") == 0) {
                        // final File scriptFile = fileChooser.getSelectedFile();
                        final File scriptFile = getSelectedFileWithExtension(fileChooser);

                        // System.out.println(scriptFile.getAbsolutePath());

                        if (scriptFile == null)
                                return;
                        else if (scriptFile.exists()) {
                                int res = JOptionPane.showConfirmDialog(frame,
                                                "The file exists. Do you want to overwrite the file?",
                                                "", JOptionPane.YES_NO_OPTION);
                                if (res == JOptionPane.NO_OPTION)
                                        return;
                        }
                        String mess = "write image  file ..";
                        Thread t = new Thread(mess) {
                                public void run() {
                                        export(component, rootKey, scriptFile.getAbsolutePath());
                                };
                        };
                        t.start();
                }

          }




       /**
 * Returns the selected file from a JFileChooser, including the extension from
 * the file filter.
 */
public static File getSelectedFileWithExtension(JFileChooser c) {
    File file = c.getSelectedFile();
    if (c.getFileFilter() instanceof FileNameExtensionFilter) {
        String[] exts = ((FileNameExtensionFilter)c.getFileFilter()).getExtensions();
        String nameLower = file.getName().toLowerCase();
        for (String ext : exts) { // check if it already has a valid extension
            if (nameLower.endsWith('.' + ext.toLowerCase())) {
                return file; // if yes, return as-is
            }
        }
        // if not, append the first extension from the selected filter
        file = new File(file.toString() + '.' + exts[0]);
    }
    return file;
}








}
