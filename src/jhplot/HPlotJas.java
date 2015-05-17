package jhplot;


//import jas.hist.FunctionFactoryError;
import jas.hist.FunctionRegistry;
import jas.hist.JASHist;
//import jas.hist.JASHistData;
//import jas.hist.test.Gauss;
import jas.loader.ClassPathLoader;
import jas.plugin.ExtensionPluginContext;
import jas.plugin.IExtensionPlugin;
import jas.swingstudio.JASToolbarHolder;
import jas.swingstudio.JavaAnalysisStudio;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.*;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import jhplot.*;
import jhplot.gui.HelpBrowser;
import jhplot.utils.Util;


import jasext.flatfileserver.FlatFileServer;
import jasext.jhplotserver.*;


import hep.aida.IFunction;
import hepjas.analysis.EventSource;
//import hepjas.analysis.Histogram;
//import hepjas.analysis.Histogram1D;
//import hepjas.analysis.Histogram2D;
//import hepjas.analysis.HistogramFolder;
import hepjas.analysis.Job;
//import hepjas.analysis.Page;
//import hepjas.analysis.Partition;
//import hepjas.analysis.Plot;
//import hepjas.analysis.ScatterPlot;
//import hepjas.analysis.partition.FixedPartition;

//import jasext.jhplotserver.RefHistogram1D;
import java.util.Random;

import jas.swingstudio.JASPage;
//import jas.swingstudio.JASToolbarHolder;
//import jas.swingstudio.JavaAnalysisStudio;
import jas.swingstudio.JASJob;
import jas.swingstudio.LocalJob;
//import jas.hist.DefaultFunctionFactory;
import hep.aida.*;

/**
* Create a canvas using the JAS plotter API. It has different look & feel than
* HPlot. Can also be used to show data, histograms, functions, 2D density
* plots. Several plot regions can be used. <p>
* 
* This API allows to perform an interactive fit after loading the data and rebin one-dimenstional arrays.
* 
* 
* @author S.Chekanov
* 
*/
public class HPlotJas  {


	private static int N1 = 0; // current
	private static int N2 = 0;
	private int xsize;
	private int ysize;
	private String title = "";
	private static final String rootKey = HPlotJas.class.getName();
	private static final String SAVE_AS_TYPE = rootKey + ".SaveAsType";
	private static final String SAVE_AS_FILE = rootKey + ".SaveAsFile";

	private JavaAnalysisStudio theApp;
	private Job job;
	private JASPage page;
	private JASHist plot;
	private JASJob m_job;
	private LocalJob localJob;
	private EventSource esource;
	private jasext.jhplotserver.JHServer jhserver;
	private ClassPathLoader exten;
	private FunctionRegistry funcregister;
	private ArrayList<Object> olist;
	
	// for secure package name
	 private static final Random random = new Random();

	   
	/**
	 * Create HPlot canvas with several plots.
	 * 
	 * @param title
	 *            Title
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 * @param olist
	 *            List of objects for drawing, such as P0D, P1D, P0I, H1D, H2P.          
	 * 
	 */

	public HPlotJas(String title, int xsize, int ysize, ArrayList<Object> olist) {

		this.title = title;
		this.xsize = xsize;
		this.ysize = ysize;
		N1 = 1;
		N2 = 1;
		this.olist=olist;
		
		
	
		
   try {
		 theApp = new JavaAnalysisStudio(); 
		 theApp.setPreferredSize(new Dimension(this.xsize,this.ysize ));
		 theApp.getFrame().setSize(this.xsize,this.ysize );
		 theApp.setNPages(1);
		 
		 JASToolbarHolder jtoolbar=theApp.getToolbar();
         jtoolbar.setCodeToolbarVisible(false);
         jtoolbar.setApplicationToolbarVisible(false);
         jtoolbar.setJobToolbarVisible(false);
         jtoolbar.setWebToolbarVisible(false);
         jtoolbar.setRebinSliderVisible(true);
         
	 } catch (Throwable e)
     {
             System.err.println("Java Analysis Studio Failed to Initialize");
             e.printStackTrace();
             System.exit(1);
     }
         
   
         
         // HistogramFolder f = new HistogramFolder("Plot/jhepwork");
	     // HistogramFolder.setDefaultFolder(f);
  
   // register some server
   
   
      jhserver= new jasext.jhplotserver.JHServer();
      jas.jds.module.ServerRegistry.registerServer(jhserver);
      
      FlatFileServer fhserver= new jasext.flatfileserver.FlatFileServer(new String[]{});
      jas.jds.module.ServerRegistry.registerServer(fhserver);
      
     // theApp.getApp().getJob().loadClass("jasext.hist.Register");
      
      
      funcregister = FunctionRegistry.instance();
      // load the fitter extension automatically
      exten= theApp.getExtensionLoader();
    
      try {
    	Class c =exten.loadClass("jasext.hist.Register",true);
		IExtensionPlugin p = (IExtensionPlugin) c.newInstance();
		ExtensionPluginContext pluginContext =theApp.getPluginContext();
        p.setPluginContext(pluginContext);
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (InstantiationException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	}
    
      
      try {
      	Class c =exten.loadClass("jasext.test.Register",true);
  		IExtensionPlugin p = (IExtensionPlugin) c.newInstance();
  		ExtensionPluginContext pluginContext =theApp.getPluginContext();
          p.setPluginContext(pluginContext);
  	} catch (ClassNotFoundException e) {
  		e.printStackTrace();
  	} catch (InstantiationException e) {
  		e.printStackTrace();
  	} catch (IllegalAccessException e) {
  		e.printStackTrace();
  	} 
      
      
      

/*  also works!
      Class[] noArgc = {};
      Object[] noArgs = {};
      try
      {
    	  Class c =exten.loadClass("jasext.hist.Register",true);
          java.lang.reflect.Method m = c.getMethod("init",noArgc);
          Object reg = c.newInstance();
          m.invoke(reg,noArgs);
      }
      catch (Throwable t)
      {
          System.err.println("Unable to register functions/fitters");
          t.printStackTrace();
      }

  */    
      
      
      
      
      
      
      
      
      /*
      jasext.flatfileserver.FlatFileServer flatFileServer = new jasext.flatfileserver.FlatFileServer(new String[]{});
      jas.jds.module.ServerRegistry.registerServer(flatFileServer);
      
      jasext.test.TestServer  testServer = new jasext.test.TestServer();
      jas.jds.module.ServerRegistry.registerServer(testServer);
      */
      
  //    jasext.test.TestServer  testServer = new jasext.test.TestServer();
   //   jas.jds.module.ServerRegistry.registerServer(testServer);
      
     // jas.jds.module.ServerRegistry.registerServer(flatFileServer);
      
    // jhserver.addData(null);
   // jas.jds.module.ServerRegistry.registerServer(s);
   // esource=jhserver.openDataSet("test");
  
    
   
         String jobName=title;
         m_job = new JASJob(jobName);
 		 localJob = new LocalJob(m_job,jobName);
 		 job=localJob.currentJob();
 		 
 		 
 		 page=theApp.getCurrentPage();
 		 plot=page.getCurrentPlot();
 	 
 		
 		if (olist != null) 
 			if (olist.size()>0)  { visible(true); add(olist);}
 		

	}

	
	/*
	private void addFunction(Class s, String name ){
		
		
		FunctionRegistry func = FunctionRegistry.instance();
		func.registerFunction(s, name);
		// System.out.println(func.size());
		
		 Class[] noArgc = {};
	      Object[] noArgs = {};
	      try
	      {
	    	  Class c =exten.loadClass("jasext.hist.Register",true);
	          java.lang.reflect.Method m = c.getMethod("init",noArgc);
	          Object reg = c.newInstance();
	          m.invoke(reg,noArgs);
	      }
	      catch (Throwable t)
	      {
	          System.err.println("Unable to register functions/fitters");
	          t.printStackTrace();
	      }
	      

	}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Add all data as a list.  Supported types are O0D, P1D, H1D. H2D, P1D, IFunction1D 
	 * @param data list with input objects
	 */
	public void add(ArrayList data){
		
		
        this.olist=data;
	    jhserver.addData(olist);
		esource=jhserver.openDataSet("jhplot");
		localJob.setEventSource(esource); 
	 	m_job.setJob(localJob);
	    theApp.setJob(m_job, true);
	
		
	    FunctionRegistry func = FunctionRegistry.instance();
		
		
	//    Fitter fitter = FitterRegistry.instance().getDefaultFitter();
	//	FunctionRegistry funcregister = FunctionRegistry.instance();
	    // now find functions and insert them
	    for (int i = 0; i < data.size(); i++) {
			Object o = data.get(i);		
			if (o instanceof  IFunction) {		   
			 
				
			    // generate a new class using a templet. This is necessary to register such function
				String className=((IFunction)o).title();
				className=className.trim();
				className=className.replaceAll("\\W","");
				// String packageName = "jasext.jhplotserver." + digits();
				String packageName = "jasext.jhplotserver";
				FunctionData1DWithType ff=newFunction(packageName,className);
				ff.setH((IFunction)o);
				
			
				
			//	RefF1D ff=new RefF1D(); 
			//	ff.setH((IFunction)o);
				
			/*
				DefaultFunctionFactory fw;
				try {
					fw = new DefaultFunctionFactory(ff.getClass(),ff.getTitle());
					funcregister.registerFunction(fw);
				} catch (FunctionFactoryError e) {
					e.printStackTrace();
				}
				*/
				
				func.registerFunction(ff.getClass(),ff.getTitle());
				
			//	System.out.println("Debug="+s);
				
			  //RefF1D ff2=new RefF1D((IFunction)o); 
			    
			   //addFunction(ff2.getClass(), ff2.getTitle() );
			   
			   /*
			   try {
				Class cls = Class.forName("jasext.jhplotserver.RefF1D");
				RefF1D myTestObject = (RefF1D) cls.newInstance();
				addFunction(myTestObject.getClass(), ff2.getTitle() );
			} catch (ClassNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			   
			
			   
			   //ClassType IObj = (ClassType)RefF1D.newInstance();
			   //System.out.println(ff2.getTitle());
			   //System.out.println(ff2.valueAt(3));
			  
			   
			
			
			   
	         }    
	
	    
	    
	    
	   
	    
	    
	    }
	
	
	
	
	
	
	
	
	}
	
	
	/**
	    * @return random hex digits with a '_' prefix
	    */
	   private String digits() {
	      return '_' + Long.toHexString(random.nextLong());
	   }

	
	   
	   /**
	    * Compile a function
	    */
	
	 private final CharSequenceCompiler<FunctionData1DWithType> compiler = new CharSequenceCompiler<FunctionData1DWithType>(
	         getClass().getClassLoader(), Arrays.asList(new String[] { }));

	
	 /**
	    * Generate Java source for a Function which computes f(x)=expr
	    * 
	    * 
	    * @return an object which computes the function denoted by expr
	    */
	private FunctionData1DWithType newFunction(String packageName, String className) {
	    
	      try {
	         final String qName = packageName + '.' + className;
	         // generate the source class as String
	         final String source = fillTemplate(packageName, className);
	         // compile the generated Java source
	         final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
	         Class<FunctionData1DWithType> compiledFunction = compiler.compile(qName, source, errs,
	               new Class<?>[] { FunctionData1DWithType.class });
	       //  System.out.println(errs);
	         return compiledFunction.newInstance();
	      } catch (CharSequenceCompilerException e) {
	    	  System.out.println(e.getDiagnostics());
	      } catch (InstantiationException e) {
	    	  System.out.println(e.getMessage());
	      } catch (IllegalAccessException e) {
	    	  System.out.println(e.getMessage());
	      } 
	      
	      return null;
	   }

	
	
	
	 /**
	    * Return the Plotter function Java source, substituting the given package
	    * name, class name, and double expression
	    * 
	    * @param packageName
	    *           a valid Java package name
	    * @param className
	    *           a valid Java class name
	    * @return source for the new class implementing Function interface using the
	    *         expression
	    * @throws IOException
	    */
	   private String fillTemplate(String packageName, String className) {
	    
		  String template; 
	     template = readTemplate();
	      // simplest "template processor":
	      String source = template.replace("$packageName", packageName)//
	            .replace("$className", className);
	      return source;
	   }

	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {

		if (vs) {
			theApp.showIt(true);

		} else {
			
			theApp.showIt(false);
		
		}

	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		visible(true);
	}

	
	/**
	 * Construct a Jas frame and prepare for plotting.
	 * 
	 * @param title
	 *            Title
	 * @param  list
	 *            list of objects to plot (H1D,P0D,P1D, etc)         
	 */
	public HPlotJas(String title, ArrayList<Object> olist) {

		this(title, 700, 500, olist);
		

	}
	
	/**
	 * Construct a Jas frame and prepare for plotting.
	 * 
	 * @param  list
	 *            list of objects to plot (H1D,P0D,P1D, etc)         
	 */
	public HPlotJas(ArrayList<Object> olist) {

		this("Default", 700, 500, olist);
		

	}
	
	/**
	 * Construct a Jas canvas with a plot with the size 600x400.
	 * 
	 * @param title
	 *            Title
	 */
	public HPlotJas(String title) {

		this(title, 700, 500, null);

	}

	/**
	 * Construct a HPlot canvas with a single plot with the size 600x400.
	 * 
	 */
	public HPlotJas() {
		this("Default", 700, 500, null);
	}

	

	/**
	 * Update the current canvas.
	 * 
	 */
	public void update() {

		 m_job.setJob(localJob);
         theApp.setJob(m_job, true);
	

	}


         /**
         * Fast export of the canvas to an image file. This depends on the
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
         */

        public void export(final String file) {

                jhplot.io.images.ExportVGraphics.export((Component) theApp.getCurrentPage(),rootKey,file);

        }



	
	
	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	/**
	 * Return title of this plotter.
	 * 
	 * @return
	 */
	public String getTitle() {
	 
	 return "HPlotJar";
	}

	/**
	 * Close the canvas (and dispose all components).
	 */
	public void close() {
		
		job=null;
		theApp.close();
		

	}

	/**
	 * Return color as a string
	 * 
	 * @param c
	 *            color
	 * @return
	 */

	private String colorString(Color c) {

		String rs = Integer.toString(c.getRed());
		String rg = Integer.toString(c.getGreen());
		String rb = Integer.toString(c.getBlue());
		String color = rs + "," + rg + "," + rb;
		return color;

	}

	
	/**
	 * Plot 1D histogram. Use external graphical options from Jaida.
	 * 
	 * @param h1
	 *            Input H1D histogram
	 * @param style
	 *            Plotter style.           
	 */

	public void draw(H1D h1) {

		// plotter.region(plotID).plot((IBaseHistogram) h1.get(), style);
	}

	
	
	
	
	 /**
	    * Read the Function source template
	    * 
	    * @return a source template
	    */
	   private String readTemplate() {
		   
		   String tmp="";
		   try {
			   
			Reader paramReader = new InputStreamReader(getClass().getResourceAsStream("/jasext/jhplotserver/RefF1D.java.template"));  
		    BufferedReader br = new BufferedReader(paramReader);
		    String line;
		    while ((line = br.readLine()) != null) 
		    {
		    	tmp=tmp+line+"\n";
		    }
		    br.close();
		    paramReader.close();
		   }catch (Exception e){//Catch exception if any
			   System.err.println("Error: " + e.getMessage());
			   }

		   return tmp;
	   }
		   
	
	
	
	
	
	
	
	
	   public static void main(String[] args) {


		    HPlotJas c1 = new  HPlotJas("Jas");
		    c1.visible(true);

		    P0D p0d = new P0D("P0D");
		    P0I p0i = new P0I("P0I");
		    P1D p1d = new P1D("P1D");

		    IAnalysisFactory analysisFactory = IAnalysisFactory.create();
		    ITreeFactory treeFactory = analysisFactory.createTreeFactory();
		    ITree tree = treeFactory.create();
		    IPlotter plotter = analysisFactory.createPlotterFactory().create("Plot");
		    IHistogramFactory histogramFactory = analysisFactory.createHistogramFactory(tree);
		    IFunctionFactory functionFactory = analysisFactory.createFunctionFactory(tree);
		    IFitFactory fitFactory = analysisFactory.createFitFactory();
		    IFunction gauss = functionFactory.createFunctionFromScript("gauss", 1,
		            "a*exp(-(x[0]-mean)*(x[0]-mean)/sigma/sigma)","a,mean,sigma","AGaussian");
		    gauss.setParameter("a", 10);
		    gauss.setParameter("mean", 1);
		    gauss.setParameter("sigma", 2);


		    IFunction parabola =  functionFactory.createFunctionFromScript(
		            "parabola",1,"background + (a*x[0]*x[0]+b*x[0]+c)","a,b,c,background","AParabola");

		    H1D h1= new H1D("H1D",10,-10,10);
		    H2D h2= new H2D("H2D",100,-10,10,100,-10,10);

		    Random random = new Random();
		    for (int i=0; i<10000; i++)
		        {
		                p0d.add(random.nextGaussian());
		                p0i.add( (int)random.nextGaussian() );
		        //      p1d.add(random.nextGaussian(),random.nextGaussian()*4 );
		                h1.fill(2*random.nextGaussian());
		                h2.fill(random.nextGaussian(),random.nextGaussian()*4 );
		        }
		    p1d.add(0,10,1);
		    p1d.add(1,20,1);
		    p1d.add(2,12,1);

		    ArrayList a= new ArrayList();
		    a.add(p0d);
		    a.add(p0i);
		    a.add(h1);
		    a.add(h2);
		    a.add(p1d);
		    a.add(gauss);
		    a.add(parabola);
		    c1.add(a);
	   }
	
	
	
	

	

}
