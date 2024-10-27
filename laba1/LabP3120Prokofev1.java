package laba1;

public class LabP3120Prokofev1 {

    public static void main(String[] args) {
        int[] z = new int[8];

        int number = 1;

        for (int i = 0; i < 8; i++) {
            z[i] = number;
            number += 2;
        }

        float[] x = new float[15];

        for (int i = 0; i < x.length; i++) {
            x[i] = (float) ((Math.random() * 27) - 14);
        }

        double[][] doublearray = new double[8][15];

        for (int i = 0; i < doublearray.length; i++) {// перебирает строки
            for (int j = 0; j < doublearray[i].length; j++) {// перебирает столбцы
                if (z[i] == 9) {
                    doublearray[i][j] = firstFormula(x[j]);
                } else if ((z[i] == 1) || (z[i] == 5) || (z[i] == 7) || (z[i] == 11)) {
                    doublearray[i][j] = secondFormula(x[i]);
                } else {
                    doublearray[i][j] = thirdFormula(x[j]);
                }
            }
        }
        matrixOut(doublearray);
    }

    private static double firstFormula(float x) {
        return Math.tan(Math.cos(2 / 3.0 * x));
    }

    private static void matrixOut(double[][] doublearray) {
        for (double[] doubles : doublearray) {// перебирает строки
            for (double aDouble : doubles) {// перебирает столбцы
                System.out.printf("%.2f ", Math.round(aDouble * 100.0) / 100.0);
            }
            System.out.println();
        }
    }

    private static double secondFormula(float x) {
        double v = Math.pow(Math.sin(x), 2);
        v = (2 / 3.0) * Math.log10(v);
        return Math.pow(Math.pow((2 * Math.tan(x)), 2), v);
    }

    private static double thirdFormula(float x) {
        double g = -(Math.abs(x));
        g = Math.atan(Math.exp(g));
        g = Math.pow(g, (1 / 3.0));
        return Math.asin(Math.sin(g));
    }
}


