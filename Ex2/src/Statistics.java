public class Statistics {
    static double CalcMean(double[] array) {
        double sum = 0;
        for (double val : array) sum += val;
        return sum / array.length;
    }

    static double CalcVariance(double[] array) {
        return CalcVariance(array, CalcMean(array));
    }

    static double CalcVariance(double[] array, double mean) {
        double variance = 0;
        for (double val : array)
            variance += (val - mean) * (val - mean);
        return variance / array.length;
    }

    static double CalcCovariance(double[] a, double[] b) throws Exception {
        double meanA = CalcMean(a);
        double meanB = CalcMean(b);
        return CalcCovariance(a, b, meanA, meanB);
    }

    static double CalcCovariance(double[] a, double[] b, double meanA, double meanB) throws Exception {
        if (a.length != b.length) throw new Exception(
                String.format("arrays should be the same size (%d != %d)", a.length, b.length)
        );
        int length = a.length;

        double cov = 0;

        for (int i = 0; i < length; i++)
            cov += (a[i] - meanA) * (b[i] - meanB);

        cov = cov / length;

        return cov;
    }

    static double PearsonCorrCoef(double[] a, double[] b) throws Exception {
//        System.out.printf("a.length: %d, b.length: %d\n", a.length, b.length);
        double meanA = CalcMean(a);
        double meanB = CalcMean(b);
        double varA = CalcVariance(a, meanA);
        double varB = CalcVariance(b, meanB);
        double cov = CalcCovariance(a, b, meanA, meanB);
//        Utils.printArray(a);
//        Utils.printArray(b);
//        System.out.println(cov / Math.sqrt(varA * varB));
//        System.out.println("---");
        return cov / Math.sqrt(varA * varB);
    }

    public static double Correlation(double[] xs, double[] ys) {
        //TODO: check here that arrays are not null, of the same length etc

        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        int n = xs.length;

        for(int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = ys[i];

            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
        }

        // covariation
        double cov = sxy / n - sx * sy / n / n;
        // standard error of x
        double sigmax = Math.sqrt(sxx / n -  sx * sx / n / n);
        // standard error of y
        double sigmay = Math.sqrt(syy / n -  sy * sy / n / n);

        // correlation is just a normalized covariation
        return cov / sigmax / sigmay;
    }
}
