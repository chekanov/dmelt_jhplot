package jhplot.math;

/**
 * BSD License
 * 
 * @author Yann RICHET
 */
public class IntegerArray {

    // conversion from/to double methods

    public static int[][] floor(double[][] v) {
        int[][] ia = new int[v.length][];
        for (int i = 0; i < v.length; i++) {
            ia[i] = new int[v[i].length];
            for (int j = 0; j < ia[i].length; j++) {
                ia[i][j] = (int) Math.floor(v[i][j]);
            }
        }
        return ia;
    }

    public static int[] floor(double[] v) {
        int[] ia = new int[v.length];
        for (int i = 0; i < v.length; i++) {
            ia[i] = (int) Math.floor(v[i]);
        }
        return ia;
    }

    public static double[][] int2double(int[][] v) {
        double[][] ia = new double[v.length][];
        for (int i = 0; i < v.length; i++) {
            ia[i] = new double[v[i].length];
            for (int j = 0; j < ia[i].length; j++) {
                ia[i][j] = v[i][j];
            }
        }
        return ia;
    }

    public static double[] int2double(int[] v) {
        double[] ia = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            ia[i] = v[i];
        }
        return ia;
    }

    // Modify rows & colmumns methods

    public static int[] copy(int[] M) {
        int[] array = new int[M.length];
        System.arraycopy(M, 0, array, 0, M.length);
        return array;
    }

    public static int[][] copy(int[][] M) {
        int[][] array = new int[M.length][M[0].length];
        for (int i = 0; i < array.length; i++)
            System.arraycopy(M[i], 0, array[i], 0, M[i].length);
        return array;
    }

    public static int[][] getSubMatrixRangeCopy(int[][] M, int i1, int i2, int j1, int j2) {
        int[][] array = new int[i2 - i1 + 1][j2 - j1 + 1];
        for (int i = 0; i < i2 - i1 + 1; i++)
            System.arraycopy(M[i + i1], j1, array[i], 0, j2 - j1 + 1);
        ;
        return array;
    }

    public static int[][] getColumnsRangeCopy(int[][] M, int j1, int j2) {
        int[][] array = new int[M.length][j2 - j1 + 1];
        for (int i = 0; i < M.length; i++)
            System.arraycopy(M[i], j1, array[i], 0, j2 - j1 + 1);
        return array;
    }

    public static int[][] getColumnsCopy(int[][] M, int... J) {
        int[][] array = new int[M.length][J.length];
        for (int i = 0; i < M.length; i++)
            for (int j = 0; j < J.length; j++)
                array[i][j] = M[i][J[j]];
        return array;
    }

    public static int[] getColumnCopy(int[][] M, int j) {
        int[] array = new int[M.length];
        for (int i = 0; i < M.length; i++)
            array[i] = M[i][j];
        return array;
    }

    public static int[] getColumnCopy(int[][][] M, int j, int k) {
        int[] array = new int[M.length];
        for (int i = 0; i < M.length; i++)
            array[i] = M[i][j][k];
        return array;
    }

    public static int[][] getRowsCopy(int[][] M, int... I) {
        int[][] array = new int[I.length][M[0].length];
        for (int i = 0; i < I.length; i++)
            System.arraycopy(M[I[i]], 0, array[i], 0, M[I[i]].length);
        return array;
    }

    public static int[] getRowCopy(int[][] M, int i) {
        int[] array = new int[M[0].length];
        System.arraycopy(M[i], 0, array, 0, M[i].length);
        return array;
    }

    public static int[][] getRowsRangeCopy(int[][] M, int i1, int i2) {
        int[][] array = new int[i2 - i1 + 1][M[0].length];
        for (int i = 0; i < i2 - i1 + 1; i++)
            System.arraycopy(M[i + i1], 0, array[i], 0, M[i + i1].length);
        return array;
    }

    public static int[] getRangeCopy(int[] M, int j1, int j2) {
        int[] array = new int[j2 - j1 + 1];
        System.arraycopy(M, j1, array, 0, j2 - j1 + 1);
        return array;
    }

    public static int[] getCopy(int[] M, int... I) {
        int[] array = new int[I.length];
        for (int i = 0; i < I.length; i++)
            array[i] = M[I[i]];
        return array;
    }

    public static int getColumnDimension(int[][] M, int i) {
        return M[i].length;
    }

    public static int[][] mergeRows(int[]... x) {
        int[][] array = new int[x.length][];
        for (int i = 0; i < array.length; i++) {
            array[i] = new int[x[i].length];
            System.arraycopy(x[i], 0, array[i], 0, array[i].length);
        }
        return array;
    }

    public static int[][] mergeColumns(int[]... x) {
        int[][] array = new int[x[0].length][x.length];
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[i].length; j++)
                array[i][j] = x[j][i];
        return array;
    }

    public static int[] merge(int[]... x) {
        int[] xlength_array = new int[x.length];
        xlength_array[0] = x[0].length;
        for (int i = 1; i < x.length; i++)
            xlength_array[i] = x[i].length + xlength_array[i - 1];
        int[] array = new int[xlength_array[x.length - 1]];
        System.arraycopy(x[0], 0, array, 0, x[0].length);
        for (int i = 1; i < x.length; i++)
            System.arraycopy(x[i], 0, array, xlength_array[i - 1], x[i].length);
        return array;
    }

    public static int[][] insertColumns(int[][] x, int[][] y, int J) {
        int[][] array = new int[x.length][x[0].length + y[0].length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(x[i], 0, array[i], 0, J);
            System.arraycopy(y[i], 0, array[i], J, y[i].length);
            System.arraycopy(x[i], J, array[i], J + y[i].length, x[i].length - J);
        }
        return array;
    }

    public static int[][] insertColumn(int[][] x, int[] y, int J) {
        int[][] array = new int[x.length][x[0].length + 1];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(x[i], 0, array[i], 0, J);
            array[i][J] = y[i];
            System.arraycopy(x[i], J, array[i], J + 1, x[i].length - J);
        }
        return array;
    }

    public static int[][] insertRows(int[][] x, int[][] y, int I) {
        int[][] array = new int[x.length + y.length][x[0].length];
        for (int i = 0; i < I; i++)
            System.arraycopy(x[i], 0, array[i], 0, x[i].length);
        for (int i = 0; i < y.length; i++)
            System.arraycopy(y[i], 0, array[i + I], 0, y[i].length);
        for (int i = 0; i < x.length - I; i++)
            System.arraycopy(x[i + I], 0, array[i + I + y.length], 0, x[i].length);
        return array;
    }

    public static int[][] insertRow(int[][] x, int[] y, int I) {
        int[][] array = new int[x.length + 1][x[0].length];
        for (int i = 0; i < I; i++)
            System.arraycopy(x[i], 0, array[i], 0, x[i].length);
        System.arraycopy(y, 0, array[I], 0, y.length);
        for (int i = 0; i < x.length - I; i++)
            System.arraycopy(x[i + I], 0, array[i + I + 1], 0, x[i].length);
        return array;
    }

    public static int[] insert(int[] x, int I, int... y) {
        int[] array = new int[x.length + y.length];
        System.arraycopy(x, 0, array, 0, I);
        System.arraycopy(y, 0, array, I, y.length);
        System.arraycopy(x, I, array, I + y.length, x.length - I);
        return array;
    }

    public static int[][] deleteColumnsRange(int[][] x, int J1, int J2) {
        int[][] array = new int[x.length][x[0].length - (J2 - J1 + 1)];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(x[i], 0, array[i], 0, J1);
            System.arraycopy(x[i], J2 + 1, array[i], J1, x[i].length - (J2 + 1));
        }
        return array;
    }

    public static int[][] deleteColumns(int[][] x, int... J) {
        int[][] array = new int[x.length][x[0].length - J.length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(x[i], 0, array[i], 0, J[0]);
            for (int j = 0; j < J.length - 1; j++)
                System.arraycopy(x[i], J[j] + 1, array[i], J[j] - j, J[j + 1] - J[j] - 1);
            System.arraycopy(x[i], J[J.length - 1] + 1, array[i], J[J.length - 1] - J.length + 1, x[i].length - J[J.length - 1] - 1);
        }
        return array;
    }

    public static int[][] deleteRowsRange(int[][] x, int I1, int I2) {
        int[][] array = new int[x.length - (I2 - I1 + 1)][x[0].length];
        for (int i = 0; i < I1; i++)
            System.arraycopy(x[i], 0, array[i], 0, x[i].length);
        for (int i = 0; i < x.length - I2 - 1; i++)
            System.arraycopy(x[i + I2 + 1], 0, array[i + I1], 0, x[i].length);
        return array;
    }

    public static int[][] deleteRows(int[][] x, int... I) {
        int[][] array = new int[x.length - I.length][x[0].length];
        for (int i = 0; i < I[0]; i++)
            System.arraycopy(x[i], 0, array[i], 0, x[i].length);
        for (int j = 0; j < I.length - 1; j++)
            for (int i = I[j] + 1; i < I[j + 1]; i++)
                System.arraycopy(x[i], 0, array[i - j], 0, x[i].length);
        for (int i = I[I.length - 1] + 1; i < x.length; i++)
            System.arraycopy(x[i], 0, array[i - I.length], 0, x[i].length);
        return array;
    }

    public static int[] deleteRange(int[] x, int J1, int J2) {
        int[] array = new int[x.length - (J2 - J1 + 1)];
        System.arraycopy(x, 0, array, 0, J1);
        System.arraycopy(x, J2 + 1, array, J1, x.length - (J2 + 1));
        return array;
    }

    public static int[] delete(int[] x, int... J) {
        int[] array = new int[x.length - J.length];
        System.arraycopy(x, 0, array, 0, J[0]);
        for (int j = 0; j < J.length - 1; j++)
            System.arraycopy(x, J[j] + 1, array, J[j] - j, J[j + 1] - J[j] - 1);
        System.arraycopy(x, J[J.length - 1] + 1, array, J[J.length - 1] - J.length + 1, x.length - J[J.length - 1] - 1);
        return array;
    }

    // min/max methods

    public static int[] min(int[][] M) {
        int[] min = new int[M[0].length];
        for (int j = 0; j < min.length; j++) {
            min[j] = M[0][j];
            for (int i = 1; i < M.length; i++)
                min[j] = Math.min(min[j], M[i][j]);
        }
        return min;
    }

    public static int min(int... M) {
        int min = M[0];
        for (int i = 1; i < M.length; i++)
            min = Math.min(min, M[i]);
        return min;
    }

    public static int[] max(int[][] M) {
        int[] max = new int[M[0].length];
        for (int j = 0; j < max.length; j++) {
            max[j] = M[0][j];
            for (int i = 1; i < M.length; i++)
                max[j] = Math.max(max[j], M[i][j]);
        }
        return max;
    }

    public static int max(int... M) {
        int max = M[0];
        for (int i = 1; i < M.length; i++)
            max = Math.max(max, M[i]);
        return max;
    }

    public static int[] minIndex(int[][] M) {
        int[] minI = new int[M[0].length];
        for (int j = 0; j < minI.length; j++) {
            minI[j] = 0;
            for (int i = 1; i < M.length; i++)
                if (M[i][j] < M[minI[j]][j])
                    minI[j] = i;

        }
        return minI;
    }

    public static int minIndex(int... M) {
        int minI = 0;
        for (int i = 1; i < M.length; i++)
            if (M[i] < M[minI])
                minI = i;
        return minI;
    }

    public static int[] maxIndex(int[][] M) {
        int[] maxI = new int[M[0].length];
        for (int j = 0; j < maxI.length; j++) {
            maxI[j] = 0;
            for (int i = 1; i < M.length; i++)
                if (M[i][j] > M[maxI[j]][j])
                    maxI[j] = i;
        }
        return maxI;
    }

    public static int maxIndex(int... M) {
        int maxI = 0;
        for (int i = 1; i < M.length; i++)
            if (M[i] > M[maxI])
                maxI = i;
        return maxI;
    }

    //  print methods

    public static String toString(int[]... v) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v[i].length; j++)
                str.append(v[i][j] + " ");
            if (i < v.length - 1)
                str.append("\n");
        }
        return str.toString();
    }

}