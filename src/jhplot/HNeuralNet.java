/**
 *    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
 *    All rights reserved.
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms
 *    of the GNU General Public License as published by the Free Software Foundation; either
 *    version 3 of the License, or any later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *    See the GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License along with this program;
 *    if not, see <http://www.gnu.org/licenses>.
 *
 *    Additional permission under GNU GPL version 3 section 7:
 *    If you have received this program as a library with written permission from the DataMelt team,
 *    you can link or combine this library with your non-GPL project to convey the resulting work.
 *    In this case, this library should be considered as released under the terms of
 *    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
 *    provided you include this license notice and a URL through which recipients can access the
 *    Corresponding Source.
 **/

package jhplot;

import java.awt.Dimension;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import org.encog.ml.data.MLData;
import org.encog.ml.train.MLTrain;
import org.encog.neural.data.*;
import org.encog.neural.data.basic.*;
import org.encog.neural.networks.*;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.*;
import org.encog.util.obj.SerializeObject;
import org.encog.workbench.*;
import org.encog.workbench.frames.*;
import org.encog.workbench.frames.document.EncogDocumentFrame;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.visualize.*;
import org.encog.neural.networks.structure.*; 
import jhplot.gui.HelpBrowser;
import jhplot.stat.Statistics; 

/**
 * Neural Netwrork calculations. Based on Backpropagation. 
 * 
 * @author S.Chekanov
 * 
 */
public class HNeuralNet {

	private BasicNetwork network;
	private BasicNeuralDataSet data;
	private MLTrain train;
	private ArrayList<Double> EpochError;

	/**
	 * Create a network net and set name for the network
	 * 
	 * @param name
	 *            name for the network
	 */

	public HNeuralNet() {
	
		
		network = new BasicNetwork();

	}



 /**
 *     Reset the weight matrix and the thresholds. 
 *
 * */
        public void reset(){
               network.getStructure().finalizeStructure();
               network.reset();
         }




	/**
	 * Construct this layer with a sigmoid threshold function.
	 * Use sigmoid for activation.  
	 * @param neuronCount
	 *            How many neurons in this layer
	 */
	public void addFeedForwardLayer(int neuronCount) {

		network.addLayer(
				new BasicLayer(new ActivationSigmoid(), false, neuronCount));
				
	}

        /**
         * Construct this layer with a sigmoid threshold function.
         * Use sigmoid for activation.  
         * @param neuronCount
         *            How many neurons in this layer
         */
        public void addFeedForwardLayerWithBias(int neuronCount) {

                network.addLayer(
                                new BasicLayer(new ActivationSigmoid(), true, neuronCount));

        }



	/**
	 * Construct a data set from an input and idea array.
	 * Used for supervized training.
	 * 
	 * @param input
	 *            The input into the neural network for training.
	 * @param ideal
	 *            The ideal output for training.
	 */
	public void setData(double input[][], double ideal[][]) {

		data = new BasicNeuralDataSet(input, ideal);

	}

/**
	 * Construct a data set from an input
	 * 
	 * @param input
	 *            The input into the neural network for training.
	 */
	public void setData(double input[][]) {

		data = new BasicNeuralDataSet(input,null);

	}


	/**
	 * Set data for training.
	 * 
	 * @param input
	 *            input data set
	 * @param ideal
	 *            expected resul.
	 */
	public void setData(PND input, PND ideal) {
		data = new BasicNeuralDataSet(input.getArray(), ideal.getArray());

	}

/**
	 * Set data
	 * 
	 * @param input
	 *            input data set
	 */
	public void setData(PND input) {
		data = new BasicNeuralDataSet(input.getArray(), null);

	}


 /**
 * Standardize each column. This means
 * S(i)= (X(i)  - mean) / std fot each column in PND;
 * @param input  PND
 * @return new PND after standardize
 * */
       public PND standardize(PND input) {
        return input.standardize();
        }


/**
	 * Get  data
	 * 
	 * @return  data
	 *            
	 */
	public BasicNeuralDataSet getData() {
		return data;
	}


    /**
	 * Evaluate data using current NN
	 * 
	 * @return  data
	 *            
	 */
	public  MLData predict( MLData input ) {
		return network.compute(input);
	}




  /**
 *  Generate prediction for input data  
 *  @param input input data for predictions 
 * */
       public  P0D predict(P0D input ) {
                BasicNeuralData tmp = new BasicNeuralData( input.getArray() ); 
                MLData output = network.compute(tmp);

                return new P0D("prediction",output.getData());
        }






 /**
 *  Generate predictions for all input data.
 *  Assumes that the predicted array has less then 3 dimensions. 
 *  @param input input data for prediction
 *  @return data with predictions 
 * */
      public  PND  predict(PND input) {
                PND tmp = new PND("Predicted");
                for (int i = 0; i < input.size(); i++) {
                  // System.out.println("Debug=");
                  // System.out.println( predict( input.getRow(i) ));
                  P0D t = predict( input.getRow(i) );
                  double[] tt=t.getArray();

                   if (tt.length==1) {
                   for (int j = 0; j < tt.length; j++) {
                     tmp.add(new double[] { tt[0]  });
                   }
                   } else if (tt.length==2) {
                   for (int j = 0; j < tt.length; j++) {
                     tmp.add(new double[] { tt[0],tt[1]  });
                       }
                   } else if (tt.length==3) {
                     for (int j = 0; j < tt.length; j++) {
                     tmp.add(new double[] { tt[0],tt[1], tt[2]  });
                        }
                   }
                }

       return tmp;
       }




	/**
	 * Training neural network.Construct a backpropagation trainer. Typical
	 * example: train(5000, 0.1, 0.25, 0.001);
	 * 
	 * @param isShow
	 *            Show learning on a pop-up plot
	 * @param maxEpoch
	 *            maximum number of epochs
	 * @param learnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param momentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 * @param errorMinEpoch
	 *            min error for epoch.
	 * @return returns the epoch at which training was stopped.
	 * 
	 */

	public int trainBackpropagation(boolean isShow, int maxEpoch,
			double learnRate, double momentum, double errorMinEpoch) {

		SPlot plot=null;
		if (isShow) {
			plot = new SPlot();
			plot.visible();
			// plot.addLegend(0, "Epoch error");
			plot.setMarksStyle("various");
			plot.setConnected(true, 0);
			plot.setNameX("Epoch");
		    plot.setNameY("Train error");
		}
		;

		EpochError = new ArrayList<Double>();
		train = new Backpropagation(network, data, learnRate, momentum);
		int epoch = 1;

		do {

			train.iteration();
			double e = train.getError();
			EpochError.add(new Double(e));
			epoch++;
			if (isShow) {
				if (epoch%100 ==0) {plot.addPoint(0, epoch, e, true); plot.setAutoRange();} 
			}
		} while ((epoch < maxEpoch) && (train.getError() > errorMinEpoch));

		if (isShow) { plot.setAutoRange(); plot.update();};
		
		
		return epoch;
	}

	/**
	 * Save current status of neural net.
	 * 
	 * @param file
	 *            File name
	 * @return what is done
	 */
	public String save(String file) {
		
		try {
			SerializeObject.save(new File(file), network);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "NN saved to " + file;
	}

/**
	 * Read a neural net from a file.
	 * 
	 * @param file 
	 *            File name
	 * @return  0 if it is OK. -1 if file not found; -2: if NN not found.
	 */
	public int read(String file) {
		 
		 
	 File f = new File(file);
         if (f.exists()) {
        	try {
				network = (BasicNetwork) SerializeObject.load(f);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (network == null) return -2;
          } else {
          	return -1;
          }
		return 0;
	}











	/**
	 * Return neural net back.
	 * 
	 * @return network
	 */
	public BasicNetwork getNetwork() {
		return network;
	}






	/**
	 * Show a neural net in a frame. 
	 * 
	 */
	public void showNetwork() {
		
	final NetworkVisualizeFrame frame = new NetworkVisualizeFrame(network);
	Dimension minSize = new Dimension(600,500);
	frame.setMinimumSize(minSize);
        frame.setVisible(true);
      
	}

 /**
 *  Show a neural net weights in a separate frame. 
 **/
    public void showWeights() {
        final NetworkWeightsFrame frame = new NetworkWeightsFrame(network);
        Dimension minSize = new Dimension(600,500);
        frame.setMinimumSize(minSize);
        frame.setVisible(true);
        }



 /**
 *   Analyse the current neural network.
 *   @return analyzer 
 **/
   public AnalyzeNetwork  analyzeNetwork() {
         return new AnalyzeNetwork(network);
        }

	
	/**
	 * Edit a neural net in a frame
	 * 
	 */
	public BasicNetwork editNetwork() {
	//	NetworkFrame a=new NetworkFrame(network);
	//	a.setVisible(true);
	//	network=a.getData();
		return network;
	}
	
	
	/**
	 * Edit data
	 * @return corrected BasicNeuralDataSet
	 */
//	public BasicNeuralDataSet editData() {
//		final TrainingDataFrame frame = new TrainingDataFrame(data);
//        frame.setVisible(true);
 //       return frame.getData();
//	}
	
	
	/**
	 * Show Net in EncodeDocument.
	 */
	public void show() {
		final EncogWorkBench workBench = EncogWorkBench.getInstance();
        workBench.setMainWindow(new EncogDocumentFrame());
        workBench.init();
        workBench.getMainWindow().setVisible(true);
		// final EncogWorkBench workBench = new EncogWorkBench();
       // workBench.setMainWindow(new EncogDocumentFrame());
        //workBench.getMainWindow().setVisible(true);
		
	}
	
	/**
	 * Returns errors for each epoch. If the max epoch number was set in the
	 * train() method. The array may have less entries if learning has reached
	 * the minimum error.
	 * 
	 * @return arrays of errors for each epoch
	 */
	public ArrayList<Double> getEpochError() {

		return EpochError;

	}

	
	

	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
	
	
	
}
