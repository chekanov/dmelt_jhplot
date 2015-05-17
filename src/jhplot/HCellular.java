package jhplot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jhplot.gui.HelpBrowser;
import jhplot.utils.BrowserHTML;

import cambria.*;

/** A two-dimensional  cellular automata toolkit. 
 * 
* @author S.Chekanov
*/ 
public class HCellular  {

	// User defined classes and these related frames
	private CAConfig caConfig;	// Object of Initial configration
	private CARule rule; 			// Object of CA rule

	
	// Input parameters
        private String srule="";
	private String X_MAX,Y_MAX;		//	lattice size
	private String T_MAX;			//  length of time development when Batch mode is selected
	private String DeltaTime;		//	time interval CA step (*1/1000 sec)
	private String CellSize;		//	Size of a cell
	private String RuleClass = null; // Java class for rule definition
	private String RuleString;		// Detailed sepc. of rule for special 'RuleClasses'
	private String RuleFile;		// Detailed sepc. of rule if RuleString is not given (Application only)
	private String InitType=null;		//	Initial Configration Type
	private String InitFile=null;		//	Initial Configration File
	private String ThreadName = null;  // Type of CA
	private String Torus = null;	// if "True", torus boundary condition
	private String UserMode = "GUI";	// "Demo", "GUI" or "Batch"
	private String BatchNumber;	// the number of batch job when "Batch" is selected
	private String RunNumber;	// the index of when "Batch" save a file

	// Input Paramters for CA Optimization Using GA
	private String PopulationSize=null;
	private String EliteSize=null;
	private String GoalOfHs=null;
	private String MutationRate=null;
	private String CrossoverRate=null;
	private String MaxIter = null;

	private ArrayList<String> RuleClasses;

	
	/**
	 * Build a Cellular automata.
	 */
	public HCellular() {
		//
	

                
               setDefault();
	
		RuleClasses = new ArrayList<String>();
                RuleClasses.add("Aggregation");
		RuleClasses.add("Aqua");
		RuleClasses.add("AquaP2");
		RuleClasses.add("BlockVN");
                RuleClasses.add("Check24");
                RuleClasses.add("Check29");
                RuleClasses.add("Check35");
                RuleClasses.add("Check25ByGA");
                RuleClasses.add("CyclicCA8");
                RuleClasses.add("CyclicCA14");
		RuleClasses.add("VN");
		RuleClasses.add("Life");
		RuleClasses.add("Life2");
		RuleClasses.add("Generation");
		RuleClasses.add("GMBrain");
		RuleClasses.add("Hodge");
		RuleClasses.add("Ising");
                RuleClasses.add("Stripe");

	}
	
	
	
	/**
	 * Get all available rules
	 * @return rule list
	 */
	public ArrayList<String> getRules(){
		
		return RuleClasses;
		
	}
	
	
	/**
	 * Get current rule.
	 * @return 
	 */
   public String getRule(){
		
		return srule;
		
	}
	
   
  
   
   /**
    * Set visible frame
    */
  public void visible(){
	  setNonGraphicItems(); 
		
	 // GUI or TTY?
        String UserMode = getUserModeString();
        if (UserMode==null) UserMode = "GUI";
        if (UserMode.regionMatches(true,0,"Batch",0,5)) {
                        // Goes to batch mode
                        int batchNumber = getBatchNumber();
                        String runNumber = getRunNumber();
                        int t_max = getTMax();
                        CAConfig caConfig = getCAConfig();
                        CABatch.executeBatchJob(caConfig,batchNumber,runNumber,t_max);
        } else if (UserMode.regionMatches(true,0,"GUI",0,3) ||
                        UserMode.regionMatches(true,0,"Demo",0,4)) {
                        // Goes to GUI mode
                        int cellSize = getCellSize();
                        int deltaTime = getDeltaTime();
                        CAConfig caConfig = getCAConfig();
                        CAGraphics caGraphics = new CAGraphics(caConfig,cellSize,deltaTime,UserMode);
        } else if (UserMode.regionMatches(true,0,"Search",0,6)) {
                        // Goes to search mode
                        int populationSize = getPopulationSize();
                        int eliteSize = getEliteSize();
                        double mutationRate = getMutationRate();
                        double crossoverRate = getCrossoverRate();
                        int maxIter = getMaxIter();
                        double goalOfHs = getGoalOfHs();
                        String runNumber = getRunNumber();
                        CAConfig caConfig = getCAConfig();
                        CASearch caSearch = new CASearch(caConfig, populationSize, eliteSize,mutationRate, crossoverRate, maxIter, goalOfHs, runNumber);
                        caSearch.start();
        } else {
                        throw new RuntimeException("Invalid parameter of UserMode");
        }
        }

  


 /**
 *  Set initialization string for the rule 
 *  @param RuleString initialization string 
 * */

   public void  setInitString(String RuleString){

      this.RuleString = RuleString;
   }


  /**
 *  Return initialization string  
 *
 * */
   public String getInitString(){

      return  RuleString;
   }
 
   
   
   
   /**
    * Show documentation
    */
      public void doc() {
    	  
    	  URL  url = CARule.class.getResource("doc/readme.html"); 
		  new HelpBrowser(url); 
		  
		  
      }

/**
 * Set initialization file 
 * @param RuleFile input file for initialization  
 * */
   public void  setInitFile(String RuleFile){

      this.RuleFile = RuleFile;
   }
 
   
   /**
    * Set rule.
    * @param rule
    * @return false if rule does not exists
    */
   public boolean setRule(String rule){
       if (RuleClasses.contains(rule)){

               if (rule == "VN") {
                 RuleString=getInitJar("cyclic8.rul");
                 return true;
                }

               if (rule == "BlockVN") {
                 RuleString=getInitJar("sandOpt01.par");
                 return true;
                }

                if (rule == "Aggregation") {
                 rule ="BlockVN";
                 RuleString=getInitJar("sandOpt01.par");
                 return true;
                }

                 if (rule == "Stripe") {
                 rule ="BlockVN";
                 RuleString=getInitJar("strOpt01.par");
                 return true;
                }


                if (rule == "Check29") {
                 rule ="Block9";
                 RuleString=getInitJar("moore2cry.par");
                 return true;
                }

                if (rule == "Check24") {
                 rule ="Block4";
                 RuleString=getInitJar("acrystal24.par");
                 return true;
                }

                if (rule == "Check35") {
                 rule ="BlockVN";
                 RuleString=getInitJar("acrystal35.par");
                 return true;
                }


               if (rule == "Check25ByGA") {
                 rule ="BlockVN";
                 RuleString=getInitJar("cryOpt01.par");
                 return true;
                }

               if (rule == "CyclicCA8") {
                 rule ="VN";
                 RuleString=getInitJar("cyclic8.rul");
                 return true;
                }
               if (rule == "CyclicCA14") {
                 rule ="VN";
                 RuleString=getInitJar("cyclic14.rul");
                 return true;
                }

                this.srule=rule;
                RuleClass="cambria."+rule+"Rule";


	       return true;
        } else {
        	return false;
        }
	}
	
	
	/** Returns used CA space.*/
	public CAConfig getCAConfig(){
		return caConfig;
	}


	
	/** Creates a CARule and a CAConfig. */
	public  void setNonGraphicItems() {
	
		int x_max;
		int y_max;
		boolean torus;
		
		rule = CARule.createRule(RuleClass);
		if (!(rule instanceof RuleSwitcher)) {
			// Possible to include in constructor?
			rule.setRule(RuleString,RuleFile); 
		} else {
			// For CanonicalRule
			int statePerCell = 2;
			int maxRuleSelection = 2;
			((RuleSwitcher)rule).initializeRuleArray
				(statePerCell,maxRuleSelection);
			rule.setRule(RuleString,RuleFile);
		}
		torus = getTorus(Torus);
		
		// Set up initial configuration
		if (ThreadName == null) ThreadName = rule.getDefaultThread();
		
		if (InitFile != null) {
			if (X_MAX != null || Y_MAX != null || InitType != null) 
				System.out.println("Warning: Some parameters are interfared. ");
			caConfig = new CAConfig(rule,torus,isSynchronous(ThreadName),InitFile);
			x_max = caConfig.getXMax();
			y_max = caConfig.getYMax();
			
		} else {
			x_max = convertIntParameter(X_MAX,100);
			y_max = convertIntParameter(Y_MAX,100);
			caConfig = new CAConfig(rule,torus,InitType,isSynchronous(ThreadName),x_max,y_max);
		}

		// A particlular setting for rules with InteractingEnergy

		if (rule instanceof InteractingEnergy) {
			if (rule instanceof CanonicalRule || rule instanceof CheckRule) {
				Demon demon = new Demon(x_max,y_max,0,1,5);
				((InteractingEnergy)rule).setDemon(demon);
			} else {
				Demon demon = new Demon(x_max,y_max);
				((InteractingEnergy)rule).setDemon(demon);
			}
		}

	}


	static public boolean getTorus(String Torus) {
		boolean torus;
		if (Torus != null) {
			if (Torus.equals("false") || Torus.equals("False"))  torus = false;
			else if (Torus.equals("true") || Torus.equals("True")) torus = true;
			else throw new IllegalArgumentException("Invalid torus argument");
		} else {
			torus = true;
		}
		return torus;
	}


	/*
	private void setTorus(String Torus) {
		if (Torus != null) {
			if (Torus.equals("false") || Torus.equals("False"))  torus = false;
			else if (Torus.equals("true") || Torus.equals("True")) torus = true;
			else throw new IllegalArgumentException("Invalid torus argument");
		} else {
			torus = true;
		}
	}
	*/

	
	/**
	 * Set default parameters
	 */

        public  void setDefault() {

         T_MAX = "200"; 
         X_MAX = "400"; 
         Y_MAX = "200"; 
         DeltaTime = "25"; 
         CellSize =  "3"; 
         RuleClass = "cambria.LifeRule"; 
         RuleString = null;
         RuleFile = null; 
         ThreadName = null;
         InitType = null;
         InitFile = null;
         Torus = null;
         UserMode = "GUI";
         BatchNumber = "1";
         RunNumber = null;
         // GA Related
         PopulationSize = "30";
         EliteSize = "20";
         GoalOfHs = null;
         MutationRate = "0.005";
         CrossoverRate =  "0.005"; 
         MaxIter = "10000"; 
         //
        }


	public String getUserModeString() {
		return UserMode;
	}

	// Whether synchronous or not
	private boolean isSynchronous(String ThreadName) {
		if (ThreadName == null) return true;
		if (ThreadName.regionMatches(true,0,"MCSThread",0,9)) {
			return false;
		} else if (ThreadName.regionMatches(true,0,"CAThread",0,8)) {
			return true;
		} else {
			throw new IllegalArgumentException("No such parameter.");
		}
	}

	/** Returns Delta time */
	public int getDeltaTime() {
		return convertIntParameter(DeltaTime,25);
	}
	
	
	/**
	 * Set set delta time
	 * @param i
	 */
	public void setDetltaTime(int i) {
		DeltaTime = Integer.toBinaryString(i);
	}
	
	/** Returns the cell size in dot*/
	public int getCellSize() {
		return convertIntParameter(CellSize,3);
	}


	/**
	 * Set cell size
	 * @param i
	 */
	public void setCellSize(int i) {
		CellSize = Integer.toBinaryString(i);
	}

	

	/** Returns used t_max */
	public int getTMax() {
		return convertIntParameter(T_MAX,200);
	}

	/** Returns the batch number */
	public int getBatchNumber() {
		return convertIntParameter(BatchNumber,1);
	}

	/** Returns the run number */
	public String getRunNumber() {
		return RunNumber;
	}
	
	/** Returns the populationSize */
	public int getPopulationSize() {
		return convertIntParameter(PopulationSize,20);
	}

	
	/**
	 * Set population size
	 * @param i
	 */
	public void setPopulationSize(int i) {
		PopulationSize = Integer.toBinaryString(i);
	}

	
	
	/** Returns the populationSize */
	public int getEliteSize() {
		return convertIntParameter(EliteSize,20);
	}

	/** Returns the mutation rate */
	public double getMutationRate() {
		return convertDoubleParameter(MutationRate,0.005);
	}	

	/** Returns the crossover rate */
	public double getCrossoverRate() {
		return convertDoubleParameter(CrossoverRate,0.005);
	}	

	/** Returns the maximum generation number for GA evolutions */
	public int getMaxIter() {
		return convertIntParameter(MaxIter,1000);
	}

	/** Returns the crossover rate */
	public double getGoalOfHs() {
		return convertDoubleParameter(GoalOfHs,0.6);
	}	

	/** Converts a String parameter into an integer parameter.*/
	static public int convertIntParameter(String string, int defaultValue) {
		int intParameter = 0;
        if (string != null) {
            try {
            	intParameter = Integer.parseInt(string);
            } catch (NumberFormatException e) {
				intParameter = defaultValue;
				System.out.print("'" + string + "' is not an integer. ");
				System.out.println("Check input parameters.");
			}
        } else {
			intParameter = defaultValue;
		}
		return intParameter;
	}

	/* Converts a String parameter into an integer parameter. No default value allowed. 
	static public int convertIntParameter(String string) {
		int intParameter = 0;
        if (string == null) throw new IllegalArgumentException("null String found");
        try {
            intParameter = Integer.parseInt(string);
        } catch (NumberFormatException e) {
			System.out.print("'" + string + "' is not an integer. ");
			System.out.println("Check input parameters.");
		}
		return intParameter;
	}
	*/

	/** Converts a String parameter into a double parameter.*/
	static public double convertDoubleParameter(String string, double defaultValue) {
		double doubleParameter = 0;
        if (string != null) {
            try {
				doubleParameter = (new Double(string)).doubleValue();
            } catch (NumberFormatException e) {
				System.out.print("'" + string + "' is not a double. ");
				System.out.println("Check input parameters.");
				doubleParameter = defaultValue;
			}
        } else {
			doubleParameter = defaultValue;
		}
		return doubleParameter;
	}





  /**
 * Get initialization 
 *
 * */

       private String getInitJar(String what) {
         return readTextFromJar(what); 
       };




  /**
 * Read from Jar
 *
 * */
   private  String readTextFromJar(String s) {
    InputStream is = null;
    BufferedReader br = null;
    String sline="";
    String line;
 
    try { 
      is = CARule.class.getResourceAsStream("resources/"+s);
      br = new BufferedReader(new InputStreamReader(is));
      while (null != (line = br.readLine())) {
         sline=sline+line+"\n";
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (br != null) br.close();
        if (is != null) is.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return sline;
  }

















}
