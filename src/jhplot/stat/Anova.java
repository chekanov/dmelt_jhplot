//Computes 1-way ANOVA -- just pass in data into constructor
//getP(), getF(), etc return the stats you want...

package jhplot.stat;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

/**
 * Class for performing simple 1-way ANOVA. For multi-way ANOVA, try
 * mathematica.
 * 
 * @author <a href="mailto:jacobe@csail.mit.edu">Jacob Eisenstein</a>
 * @version 1.0
 */
public class Anova {
	/**
	 * Creates a new <code>Anova</code> instance. Pass in the samples as an
	 * array of double arrays. Naturally, they may have different sizes.
	 * 
	 * @param p_data
	 *            a <code>double[][]</code> value
	 */
	public Anova(double[][] p_data) {
		data = p_data; // maybe do a deep copy?
		double s_between = 0f;
		double s_residual = 0f;
		double all_means[] = new double[p_data.length];
		double overall_mean = 0f;
		int total_size = 0;
		int all_sizes[] = new int[p_data.length];
		Arrays.fill(all_means, 0f);
		Arrays.fill(all_sizes, 0);
		// System.out.println ("pdata.length = "+p_data.length);
		for (int i = 0; i < p_data.length; i++) {
			// Vector cur_group = (Vector) all_groups.elementAt(i);
			// double group_distrib[] = new double[p_data[i].length];
			if (p_data == null)
				System.out.println("pdata null");
			if (p_data[i] == null)
				System.out.println("pdata[" + i + "] null");
			all_sizes[i] = p_data[i].length;
			total_size += all_sizes[i];
			for (int j = 0; j < p_data[i].length; j++) {
				if (!Double.isNaN(p_data[i][j]))
					all_means[i] += p_data[i][j] / all_sizes[i];
				overall_mean += p_data[i][j];
			}
			for (int j = 0; j < p_data[i].length; j++) {
				if (!Double.isNaN(p_data[i][j]))
					s_residual += Math.pow(p_data[i][j] - all_means[i], 2);
			}
		}
		overall_mean /= total_size;
		// double overall_mean = weka.core.Utils.mean (all_means);
		for (int i = 0; i < p_data.length; i++) {
			s_between += all_sizes[i]
					* Math.pow(all_means[i] - overall_mean, 2);
		}

		dof1 = p_data.length - 1;
		dof2 = total_size - p_data.length;
		f = (s_between / (double) dof1) / (s_residual / (double) dof2);
		// System.out.println (f+"\t"+dof1+"\t"+dof2);
		p = Statistics.FProbability(f, dof1, dof2);
	}

	/**
	 * Prettyprints your data and the results of the ANOVA.
	 * 
	 */
	public void printData() {
		System.out.println("---------------------------------------");
		DecimalFormat df = new DecimalFormat("0.0000");
		Vector all_data = new Vector();
		for (int i = 0; i < data.length; i++) {
			System.out.print(df.format(Statistics.mean(data[i])) + " "
					+ df.format(Math.sqrt(Statistics.variance(data[i])))
					+ " |");
			for (int j = 0; j < data[i].length; j++) {
				if (!Double.isNaN(data[i][j])) {
					System.out.print(" " + df.format(data[i][j]));
					all_data.add(new Double(data[i][j]));
				}
			}
			System.out.println();
		}
		System.out.println("  -------------------");
		double[] vall_data = new double[all_data.size()];
		for (int i = 0; i < all_data.size(); i++) {
			vall_data[i] = ((Double) (all_data.elementAt(i))).doubleValue();
		}
		System.out.println(df.format(Statistics.mean(vall_data)) + " "
				+ df.format(Math.sqrt(Statistics.variance(vall_data)))
				+ " |");
		System.out.println("-----------------------------------------");
	}

	/**
	 * Pretty prints your results.
	 * 
	 */
	public void printResults() {
		System.out.println("F=" + getF() + " DOF1=" + getDOF1() + " DOF2="
				+ getDOF2() + " P=" + getP());
	}

	/**
	 * Returns the p-value that your data is all drawn from a single
	 * distribution. Lower <code>p</code> means you have an effect that is more
	 * likely to be significant.
	 * 
	 * @return a <code>double</code> value
	 */
	public double getP() {
		return p;
	}

	/**
	 * Returns the F-value for your data. Sometimes people like to give this
	 * along with the p-value in publications.
	 * 
	 * @return a <code>double</code> value
	 */
	public double getF() {
		return f;
	}

	/**
	 * The first degree of freedom is equal to the number of sample groups,
	 * minus 1.
	 * 
	 * @return an <code>int</code> value
	 */
	public int getDOF1() {
		return dof1;
	}

	/**
	 * The second degree of freedom is equal to the total number of data points,
	 * minus the number of samples.
	 * 
	 * @return an <code>int</code> value
	 */
	public int getDOF2() {
		return dof2;
	}

	/**
	 * Returns an array of means for all of your sample groups.
	 * 
	 * @return a <code>double[]</code> value
	 */
	public double[] getMeans() {
		double means[] = new double[data.length];
		for (int i = 0; i < means.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				if (!Double.isNaN(data[i][j]))
					means[i] += data[i][j];
			}
			means[i] /= data[i].length;
		}
		return means;
	}

	/**
	 * This takes a input array and produces matlab code that can be coppied
	 * directly into a matlab prompt to run anova on it
	 */
	private static String makeMatlab(double[][] m) {
		StringBuffer mat = new StringBuffer("a = [");

		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				mat.append(i + " " + m[i][j] + "; ");
			}
		}
		mat.append("];\n");
		mat.append("anova1(a(:,2)',a(:,1)')");
		return mat.toString();
	}

	/**
	 * A sample set that gives different answers than Matlab.
	 **/
	private static void test() {
		double[][] i_data = new double[][] {
				{ 0.000583 },
				{ 0.000117, 0.001011, 0.00019, 0.003418, 0.000445, 0.000472,
						7.942658E-005, 1.861884E-005, 0.000719, 0.000386,
						0.00034, 0.000994, 0.001243, 0.000481, 0.001052 },
				{ 0.000271, 0.00063, 0.000195, 3.091709E-005, 0.001265,
						0.001154, 0.001111, 0.001169, 0.000982, 0.009851,
						0.000214, 0.000318, 0.000565, 0.004859, 0.006513,
						0.007501, 0.010925, 0.000653, 0.000687, 1.808875E-005,
						0.0004, 0.000102, 0.000122, 0.00731, 0.000329,
						0.000738, 0.000232, 0.000317, 0.000104, 0.000187,
						9.65759E-005, 8.779085E-005, 0.000267, 0.000666,
						0.000213, 0.001207, 5.800928E-005, 0.000146, 0.00021,
						0, 4.213974E-005, 0.000296, 7.076694E-005, 0.000451,
						0.0002, 0.004935, 0.003804, 0.000863, 0.000567,
						0.000737, 0.000631, 0.003083, 0.000357, 0.001986,
						0.000435, 0.003722, 9.99448E-005, 0.000448, 0.001421,
						0.024937, 0.000404, 0.000192, 0.000456, 5.722088E-005,
						0.0025, 0.000147, 0.003904, 0.001772 },
				{ 0.001321, 0.000156, 2.353968E-005, 0.000992, 0.000576,
						1.557194E-005, 0.002012, 0.000501, 0.000527 },
				{ 0.000587, 0.000224, 0.0002, 0.000483, 0.00058, 3.929909E-005,
						0.002227, 0.000543, 3.626242E-005, 0.000224, 0.000256,
						0.00023, 0.000526, 0.002585 }, };

		Anova an = new Anova(i_data);
		System.out.println(an.getP());

		an.printData();
		an.printResults();

		System.out.println(makeMatlab(i_data));
	}

	/**
	 * This will reproduce the "hotdog" example from De Groot & Schervish's
	 * "Probability and Statistics", page 666.
	 **/
	private static void hotDogTest() {
		double i_data[][] = new double[4][];
		double data1[] = { 186, 181, 176, 149, 184, 190, 158, 139, 175, 148,
				152, 111, 141, 153, 190, 157, 131, 149, 135, 132 };
		double data2[] = { 173, 191, 182, 190, 172, 147, 146, 139, 175, 136,
				179, 153, 107, 195, 135, 140, 138 };
		double data3[] = { 129, 132, 102, 106, 94, 102, 87, 99, 107, 113, 135,
				142, 86, 143, 152, 146, 144 };
		double data4[] = { 155, 170, 114, 191, 162, 146, 140, 187, 180 };
		i_data[0] = data1;
		i_data[1] = data2;
		i_data[2] = data3;
		i_data[3] = data4;
		Anova an = new Anova(i_data);
		System.out.println(an.getP());

		an.printData();
		an.printResults();

		System.out.println(makeMatlab(i_data));

	}

	protected double data[][];
	protected double p;
	protected double f;
	protected int dof1;
	protected int dof2;

	public static void main(String argv[]) {
		test();
		// hotDogTest();
	}
}
