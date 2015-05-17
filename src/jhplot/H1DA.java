/*
/*
Copyright 2007 Flaptor (flaptor.com) 
Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 
http://www.apache.org/licenses/LICENSE-2.0 
Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
 */
package jhplot;

import java.io.Serializable;
import java.util.ArrayList;
import com.flaptor.hist4j.*;

/**
 * Adaptive histogram in one dimension (1D).  
 * This class implements a histogram that adapts to an unknown data distribution.
 * It keeps a more or less constant resolution throughout the data range by increasing 
 * the resolution where the data is more dense.  For example, if the data has such
 * such a distribution that most of the values lie in the 0-5 range and only a few are 
 * in the 5-10 range, the histogram would adapt and assign more counting buckets to
 * the 0-5 range and less to the 5-10 range.
 * This implementation provides a method to obtain the accumulative density function 
 * for a given data point, and a method to obtain the data point that splits the 
 * data set at a given percentile. 
 * @author Jorge Handl and S.Chekanov 
 */
public class H1DA extends  AdaptiveHistogram {

    private static final long serialVersionUID = -1L;
    private long totalCount;     // total number of data points
    private HistogramNode root;  // root of the tree

    /**
     * Create addaptive histogram.
     */
    public H1DA() {
       super(); 
    }


    /*
     * Print the histograms' underlying data structure.
     */
    public void print() {
        System.out.println("Histogram has " + totalCount + " values:");
        if (null != root) {
            root.show(0);
        }
    }


     /**
     * Adds a data point to the histogram.
     * @param value the data point to add.
     */
    public void fill(float value) {
         addValue(value); 
    }



       public String toString() {

        String tmp="";
        tmp=tmp+"Main percentiles:\n";
        tmp=tmp+"   5%: " + Float.toString(getValueForPercentile(5));
        tmp=tmp+"  25%: " + Float.toString(getValueForPercentile(25));
        tmp=tmp+"  50%: " + Float.toString(getValueForPercentile(50));
        tmp=tmp+"  75%: " + Float.toString(getValueForPercentile(75));
        tmp=tmp+"  95%: " + Float.toString(getValueForPercentile(95));
        tmp=tmp+"Cumulative density:\n";
        for (float x=-1; x<=1; x+=0.5) {
            tmp=tmp+"  " + Float.toString(x) + ": " + Float.toString(getAccumCount(x));
        }
        tmp=tmp+"\n";
        return tmp;
       }

 
}

