/*
 * Convert SVG to EPS  
 */

package jhplot.io.images;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.render.ps.PSTranscoder;
import org.apache.fop.svg.AbstractFOPTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;

/**
 * Convert a SVG file into a number of formats, such as SVGZ, PDF, EPS, PS.
 * 
 * @author S.Chekanov
 * 
 */
public class ConvertSVG {

	/**
	 * Convert a SVG to EPS, PS, SVGZ or PDF. The file type is given by
	 * extension. SVGZ is compressed SVG image (using gzip compression)
	 * Supported output: <br>
	 * JPEG<br>
	 * PNG <br>
	 * PS <br>
	 * EPS <br>
	 * PDF <br>
	 * SVGZ <br>
	 * 
	 * @param in
	 *            Input file in SVG format
	 * @param out
	 *            Output file in designed format.
	 * @param isSourceRemove
	 *            true if source is removed.
	 */
	public static void SVGTo(String in, String out, boolean isSourceRemove) {

		int dot = out.lastIndexOf('.');
		String base = (dot == -1) ? out : out.substring(0, dot);
		String extension = (dot == -1) ? "" : out.substring(dot + 1);
		extension = extension.trim();

		extension = extension.toLowerCase();

		if (       extension.equals("eps") == false 
				&& extension.equals("ps") == false
				&& extension.equals("pdf") == false
				&& extension.equals("png") == false
				&& extension.equals("jpg") == false
				&& extension.equals("jpeg") == false
				&& extension.equals("svgz") == false) {
			System.out.println("Error: Unsupported format=" + extension);
			return;
		}

		AbstractFOPTranscoder trans = null;
		switch (extension) {
		case "pdf":
			trans = new PDFTranscoder();
			// trans.addTranscodingHint(PDFTranscoder.KEY_MAX_HEIGHT,new
			// Float(19200));
			// trans.addTranscodingHint(PDFTranscoder.KEY_MAX_WIDTH,new
			// Float(19200));
			break;
		case "ps":
			trans = new PSTranscoder();
			break;
		case "eps":
			trans = new EPSTranscoder();
			// Add Transcoding hints
			/*
			 * Dimension dimensions = new Dimension(800,400); if
			 * (dimensions.getWidth() > 0.0) {
			 * trans.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float)
			 * dimensions.getWidth()); } if (dimensions.getHeight() > 0.0) {
			 * trans.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT,
			 * (float) dimensions.getHeight()); }
			 */
			break;
		}

		if (trans != null) {

			try {
				TranscoderInput input = new TranscoderInput(
						new FileInputStream(in));
				FileOutputStream os = new FileOutputStream(out);
				TranscoderOutput output = new TranscoderOutput(os);
				trans.transcode(input, output);
				os.flush();
				os.close();
				os = null;

				if (isSourceRemove) {
					File f = new File(in);
					if (f.exists())
						f.delete();
				}

				return;
			} catch (Exception e) {
				System.err.println("Error while converting from SVG: "
						+ e.getLocalizedMessage());
			}

		}

		ImageTranscoder trans1 = null;
		switch (extension) {
		case "jpeg":
			trans1 = new JPEGTranscoder();
			trans1.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(1000));
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_ALLOWED_SCRIPT_TYPES, "*");
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN, new Boolean(true));
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_EXECUTE_ONLOAD, new Boolean(true));
			break;
		case "jpg":
			trans1 = new JPEGTranscoder();
			trans1.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(1000));
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_ALLOWED_SCRIPT_TYPES, "*");
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN, new Boolean(true));
		    trans1.addTranscodingHint(JPEGTranscoder.KEY_EXECUTE_ONLOAD, new Boolean(true));
			break;
		case "png":
			trans1 = new PNGTranscoder();
			break;

		}

		
		
		if (trans1 != null) {

			try {
				TranscoderInput input = new TranscoderInput(
						new FileInputStream(in));
				FileOutputStream os = new FileOutputStream(out);
				TranscoderOutput output = new TranscoderOutput(os);
				trans1.transcode(input, output);
				os.flush();
				os.close();
				os = null;

				if (isSourceRemove) {
					File f = new File(in);
					if (f.exists())
						f.delete();
				}

				return;
			} catch (IOException | TranscoderException e) {
				System.err.println("Error while converting from SVG: "
						+ e.getLocalizedMessage());
			}

		}

		
		
		
		if (extension.equalsIgnoreCase("svgz")) {

			
			gzipFile(in,out);
			
			if (isSourceRemove) {
				File f = new File(in);
				if (f.exists())
					f.delete();
			}
			
			return;
			
			
		}

		System.err.println("Error: Cannot find output format=" + extension);

	}

	/**
	 * Convert a SVG to EPS, PS, SVGZ or PDF. The file type is given by
	 * extension. SVGZ is compressed SVG image (using gzip compression). The
	 * original source is kept. Supported output: <br>
	 * JPEG<br>
	 * PNG <br>
	 * PS <br>
	 * EPS <br>
	 * PDF <br>
	 * SVGZ <br>
	 * 
	 * @param in
	 *            Input file in SVG format
	 * @param out
	 *            Output file in designed format.
	 */
	public static void SVGTo(String in, String out) {
		SVGTo(in, out, false);

	}

	
	
	
	
	
	/**
	 * Compress file
	 * @param source_filepath
	 * @param destinaton_zip_filepath
	 */
	private static void gzipFile(String source_filepath, String destinaton_zip_filepath) {

		byte[] buffer = new byte[1024];

		try {
			
			FileOutputStream fileOutputStream =new FileOutputStream(destinaton_zip_filepath);

			GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);

			FileInputStream fileInput = new FileInputStream(source_filepath);

			int bytes_read;
			
			while ((bytes_read = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytes_read);
			}

			fileInput.close();

			gzipOuputStream.finish();
			gzipOuputStream.close();

			//System.out.println("The file was compressed successfully!");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
