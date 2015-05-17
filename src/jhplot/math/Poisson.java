package jhplot.math;


import java.util.Random;


/**
 * Build a Poisson distribution 
 *
 * */
 
public class Poisson {
	
        /** e^(-lambda) */
    private double elambda;
    private Random rand;

    
   /**
    * Build a Posson generator
    * @param rand input Random generator
    * @param lambda lambda
    */
    public Poisson(Random rand, double lambda) {
        elambda = Math.exp(-lambda);
        this.rand = rand;
    }
    

    /**
     * Build a Poisson random numbers with a given lambda
     * @param lambda Lambda
     */
    public Poisson(double lambda) {
        elambda = Math.exp(-lambda);
        rand = new Random();
    }

	
    /**
     * Build a Posson random numbers
     * @param seed seed to reseed the generator
     * @param lambda lambda
     */
    public Poisson(long seed, double lambda) {
        elambda = Math.exp(-lambda);
        rand = new Random(seed);
    }

  
    /**
     * Get next Poisson random number with the lambda given in constructor
     * @return
     */
    public int next() {
        double product = 1;
        int count =  0;
        int result=0;
        while(product >= elambda) {
            product *= rand.nextDouble();
            result = count;
            count++; // keep result one behind
        }
        return result;
    }

    
    /**
     * Get next Poisson random number with a new lambda (overwrides the default)
     * @param lambda new lambda
     * @return
     */
    public int next(double lambda) {
        double product = 1;
        int count =  0;
        int result=0;
        elambda = Math.exp(-lambda);
        while(product >= elambda) {
            product *= rand.nextDouble();
            result = count;
            count++; // keep result one behind
        }
        return result;
    }



    public static final void main(String[] args) {
        int size = 10;
		
            // Create a distribution with a 
            // "mean arrival time" of 2.5
        Poisson test = new Poisson(2.5);
		
        int total = 0;
		
        for(int line = 0; line < size; ++line) {
            for(int col = 0; col < size; ++col) {
                    // Get the next arrival time from
                    // the distribution
                int next = test.next();
                total += next;
                System.out.printf("%d ", next);
            }
            System.out.println();
        }
        System.out.printf(
                "%nThe actual mean arrival time is %.4f%n",
                (double)total / (size * size));
    }
}
