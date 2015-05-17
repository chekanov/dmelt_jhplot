package jhplot.io.images;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import javax.swing.JComponent;
import kcl.waterloo.defaults.GJDefaults;
import kcl.waterloo.deploy.svg.WSVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.XMLAbstractTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.apache.xmlgraphics.java2d.ps.PSDocumentGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

/*
*
* This code is part of Project Waterloo from King's College London
* <http://waterloo.sourceforge.net/>
*
* Copyright King's College London 2011-
*
* @author Malcolm Lidierth, King's College London <a href="http://sourceforge.net/p/waterloo/discussion/"> [Contact]</a>
*
* Project Waterloo is free software:  you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Project Waterloo is distributed in the hope that it will  be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/

public class ExportCanvas {

	
	    private static int containerCounter = 0;
	    private static int graphCounter = 0;
	    private static int axisCounter = 0;
	    private static int plotCounter = 0;

	    private static PDFTranscoder transcoder = null;

	    private ExportCanvas() {}
	
	


	

	    /**
	     * Returns the image in SVG format as a String
	     *
	     * @param c an AWT component
	     * @return
	     * @throws IOException
	     */
	    public static String saveAsSVG(Component c) throws IOException {
	        StringWriter stream = new StringWriter();
	        String s = doSVG(c, stream);
	        return s;
	    }

	    /**
	     * Saves the image to an SVG file
	     *
	     * @param c an AWT component
	     * @param str
	     * @throws IOException
	     */
	    public static void saveAsSVG(Component c, String str) throws IOException {
	        //Create output stream
	        FileOutputStream outputStream = createOutputStream(str);
	        doSVG(c, outputStream);
	    }

	    /**
	     * Saves the image to an SVG file
	     *
	     * @param c an AWT component
	     * @param f the file to save as a File instance
	     * @throws IOException
	     */
	    public static void saveAsSVG(Component c, File f) throws IOException {
	        //Create output stream
	        FileOutputStream outputStream = new FileOutputStream(f);
	        doSVG(c, outputStream);
	        outputStream.close();
	    }

	    public static void saveAsCompressedSVG(Component c, File f) throws IOException {
	        //Create output stream
	        GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(f));
	        doSVG(c, outputStream);
	        outputStream.close();
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @return
	     * @throws IOException
	     */
	    public static ByteArrayInputStream saveAsPDF(Component c) throws IOException {
	        StringWriter stream = new StringWriter();
	        return convertSVG2PDF(doSVG(c, stream));
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @param s
	     * @throws IOException
	     */
	    public static void saveAsPDF(Component c, String s) throws IOException {
	        saveAsPDF(c, new File(s));
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @param f
	     * @throws IOException
	     */
	    public static void saveAsPDF(Component c, File f) throws IOException {
	        StringWriter stream = new StringWriter();
	        String s = doSVG(c, stream);
	        convertSVG2PDF(s, f);
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @return
	     * @throws IOException
	     */
	    public static ByteArrayOutputStream saveAsEPS(Component c) throws IOException {
	        EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
	        g2d.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        g2d.setupDocument(stream, c.getWidth(), c.getHeight());
	        paint(c, g2d);
	        g2d.finish();
	        return stream;
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @param f
	     * @throws IOException
	     */
	    public static void saveAsEPS(Component c, File f) throws IOException {
	        EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
	        g2d.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());
	        FileOutputStream stream = createOutputStream(f);
	        g2d.setupDocument(stream, c.getWidth(), c.getHeight()); //400pt x 200pt
	        paint(c, g2d);
	        g2d.finish();
	        stream.close();
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @return
	     * @throws IOException
	     */
	    public static ByteArrayOutputStream saveAsPS(Component c) throws IOException {
	        PSDocumentGraphics2D g2d = new PSDocumentGraphics2D(false);
	        g2d.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        g2d.setupDocument(stream, c.getWidth(), c.getHeight()); //400pt x 200pt
	        paint(c, g2d);
	        g2d.finish();
	        return stream;
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @param f
	     * @throws IOException
	     */
	    public static void saveAsPS(Component c, File f) throws IOException {
	        PSDocumentGraphics2D g2d = new PSDocumentGraphics2D(false);
	        g2d.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());
	        FileOutputStream stream = createOutputStream(f);
	        g2d.setupDocument(stream, c.getWidth(), c.getHeight()); //400pt x 200pt
	        paint(c, g2d);
	        g2d.finish();
	        stream.close();
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @return
	     * @throws IOException
	     */
	    private static String doSVG(Component c) throws IOException {
	        return doSVG(c, new StringWriter(), false);
	    }

	    /**
	     *
	     * @param c
	     * @param svgOut
	     * @throws IOException
	     */
	    private static void doSVG(Component c, OutputStream svgOut) throws IOException {
	        doSVG(c, svgOut, false);
	    }

	    /**
	     *
	     * @param c
	     * @param svgOut
	     * @throws IOException
	     */
	   
	    private static void doSVG(Component c, OutputStream svgOut, boolean textAsStrokes) throws IOException {
	        // Get a DOMImplementation.
	        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
	        // Create an instance of org.w3c.dom.Document.
	        String svgNS = "http://www.w3.org/2000/svg";
	        org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);
	        // Create an instance of the SVG Generator.
	        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
	        ctx.setComment("Generated by the Project Waterloo Graphics Library using the Apache Batik SVG Generator");
	        SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, textAsStrokes);
	        svgGenerator.setSVGCanvasSize(c.getSize());

	        //See https://issues.apache.org/jira/browse/BATIK-555
	        Element root = document.getDocumentElement();
	        svgGenerator.getRoot(root);

	        WSVGGraphics2D g = new WSVGGraphics2D(svgGenerator);
	        // Render into the SVG Graphics2D implementation.
	        paint(c, g);
	        Writer out = null;
	        try {
	            try {
	                out = new OutputStreamWriter(svgOut, "UTF-8");
	            } catch (UnsupportedEncodingException ex) {
	                System.err.println(ex);
	            }
	            try {
	                svgGenerator.stream(out, textAsStrokes);
	            } catch (SVGGraphics2DIOException ex) {
	                System.err.println(ex);
	            }
	        } finally {
	            svgOut.close();
	        }
	    }

 
	    private static String doSVG(Component c, StringWriter out) throws IOException {
	        return doSVG(c, out, false);
	    }

	    /**
	     *
	     * @param c an AWT component
	     * @param out
	     * @return
	     * @throws IOException
	     */
	    private static String doSVG(Component c, StringWriter out, boolean textAsStrokes) throws IOException {
	        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
	        String svgNS = "http://www.w3.org/2000/svg";
	        org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);
	        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
	        ctx.setEmbeddedFontsOn(true);
	        ctx.setComment("Generated by the Project Waterloo Graphics Library using the Apache Batik SVG Generator");
	        SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, false);
	        svgGenerator.setSVGCanvasSize(c.getSize());

	        //See https://issues.apache.org/jira/browse/BATIK-555
	        Element root = document.getDocumentElement();
	        svgGenerator.getRoot(root);

	        paint(c, svgGenerator);
	        try {
	            try {
	                svgGenerator.stream(out, textAsStrokes);
	            } catch (SVGGraphics2DIOException ex) {
	                System.err.println(ex);
	            }
	        } finally {
	        }
	        return out.toString();
	    }

	    public static void deploySVG(Component c2, File f, Dimension dim, boolean inline,
	            String cssLoc, boolean httpdFlag, ArrayList<String> keyWords, String title,
	            String description) throws IOException {

	        String svg = doSVG(c2);
	        String jsLoc = ((String) GJDefaults.getMap().get("SVG.jsLocation"));
	        if (!((Boolean) GJDefaults.getMap().get("SVG.canvg")).booleanValue()) {
	            jsLoc = "";
	        }
	        WSVGGraphics2D.deploy(f,
	                svg,
	                dim.getSize(),
	                inline,
	                jsLoc,
	                cssLoc,
	                httpdFlag,
	                keyWords,
	                title,
	                description);
	    }

	    public static String doHTML5(Component c) throws IOException {
	        return doSVG(c, new StringWriter());
	    }

	    /**
	     * Converts an FO file to a PDF file using FOP
	     *
	     * @param svg the SVG file
	     * @param pdf the target PDF file
	     * @throws IOException In case of an I/O problem
	     * @throws TranscoderException In case of a transcoding problem
	     */
	    private static void convertSVG2PDF(String svg, File pdf) throws IOException {

	       
	        if (transcoder == null) {
	            transcoder = new PDFTranscoder();
	            transcoder.addTranscodingHint(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
	            transcoder.addTranscodingHint(PDFTranscoder.KEY_STROKE_TEXT, Boolean.FALSE);
	        }
	        ByteArrayInputStream in = new ByteArrayInputStream(svg.getBytes("UTF-8"));
	        try {
	            TranscoderInput input = new TranscoderInput(in);

	            //Setup output
	            OutputStream out = new java.io.FileOutputStream(pdf);
	            out = new java.io.BufferedOutputStream(out);
	            try {
	                TranscoderOutput output = new TranscoderOutput(out);
	                try {
	                    //Do the transformation
	                    transcoder.transcode(input, output);
	                } catch (TranscoderException ex) {
	                    System.err.println(ex);
	                }
	            } finally {
	                out.close();
	            }
	        } finally {
	            in.close();
	        }
	    }

	    /**
	     *
	     * @param svg
	     * @return
	     * @throws IOException
	     */
	    public static ByteArrayInputStream convertSVG2PDF(String svg) throws IOException {
	        if (System.getProperty("waterloo_pdf_supported")!=null && System.getProperty("waterloo_pdf_supported").equals("false")) {
	            System.err.println("PDFTranscoder not supported");
	            return null;
	        }
	        if (transcoder == null) {
	            transcoder = new PDFTranscoder();
	            transcoder.addTranscodingHint(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
	            transcoder.addTranscodingHint(PDFTranscoder.KEY_STROKE_TEXT, Boolean.FALSE);
	        }
	        ByteArrayOutputStream out;
	        TranscoderOutput output;
	        TranscoderInput input = new TranscoderInput(new StringReader(svg));
	        out = new java.io.ByteArrayOutputStream();
	        output = new TranscoderOutput(out);
	        try {
	            transcoder.transcode(input, output);
	        } catch (TranscoderException ex) {
	            System.err.print(ex);
	        }
	        out.flush();
	        ByteArrayInputStream pdf = new ByteArrayInputStream(out.toByteArray());
	        out.close();
	        return pdf;
	    }

	    /**
	     *
	     * @param thisFile
	     * @return
	     */
	    private static FileOutputStream createOutputStream(File thisFile) {
	        FileOutputStream OutputStream = null;
	        try {
	            OutputStream = new FileOutputStream(thisFile);
	        } catch (FileNotFoundException ex) {
	            System.err.println(ex);
	        }
	        return OutputStream;
	    }

	    /**
	     *
	     * @param str
	     * @return
	     */
	    private static FileOutputStream createOutputStream(String str) {
	        File thisFile = new File(str);
	        return createOutputStream(thisFile);
	    }

	    public static void initGroupMarker() {
	        containerCounter = 0;
	        graphCounter = 0;
	        axisCounter = 0;
	        plotCounter = 0;
	    }

	    public static void createGroupMarker(Object o, String s, Object reference, Graphics g) {
	        if (g instanceof SVGGraphics2D) {
	            SVGGraphics2D g2 = (SVGGraphics2D) g;
	            DOMGroupManager domGroupManager = new DOMGroupManager(g2.getGraphicContext(), g2.getDOMTreeManager());
	            g2.getDOMTreeManager().addGroupManager(domGroupManager);
	            // Outer group. ID is hashCode for o
	            Element el0 = g2.getDOMFactory().createElementNS(null, "g");
	            el0.setAttributeNS(null, "class", "waterloo_graphics_element");
	            el0.setAttributeNS(null, "id", String.format("kcl_%x", o.hashCode()));

	            // Inner group.
	            Element el1 = g2.getDOMFactory().createElementNS(null, "g");
	            el0.appendChild(el1);
	            String classString = o.getClass().toString().replaceAll("class ", "");
	            el1.setAttributeNS(null, "class", classString);
	            if (classString.contains("GraphContainer")) {
	                el1.setAttributeNS(null, "id", "Container" + containerCounter);
	                containerCounter++;
	            } else if (classString.contains("Graph")) {
	                el1.setAttributeNS(null, "id", "Graph" + graphCounter);
	                graphCounter++;
	            } else if (classString.contains("AxisPanel")) {
	                el1.setAttributeNS(null, "id", "Axis" + axisCounter);
	                axisCounter++;
	            } else {
	                el1.setAttributeNS(null, "id", "Plot" + plotCounter);
	                plotCounter++;
	            }

	            Element el2 = g2.getDOMFactory().createElementNS(null, "title");
	            el2.setTextContent(s);
	            el1.appendChild(el2);

	            Element el3 = g2.getDOMFactory().createElementNS(null, "desc");
	            el3.setTextContent("Beginning of " + classString + " section");
	            el1.appendChild(el3);
	            domGroupManager.addElement(el0);

	        }
	    }

	    private static void paint(Component c, Graphics g) {
	        if (c instanceof JComponent) {
	            boolean state = c.isOpaque();
	            ((JComponent) c).setOpaque(true);
	            c.paint(g);
	            ((JComponent) c).setOpaque(state);
	        } else {
	            c.paint(g);
	        }
	    }

	    public final static boolean isPresent() {
	        return true;
	    }
	}

