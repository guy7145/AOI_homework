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
        double meanA = CalcMean(a);
        double meanB = CalcMean(b);
        double varA = CalcVariance(a, meanA);
        double varB = CalcVariance(b, meanB);
        double cov = CalcCovariance(a, b, meanA, meanB);


//        Utils.printArray(a);
//        Utils.printArray(b);
//        System.out.println(cov / Math.sqrt(varA * varB));
//        System.out.println("---");

//        System.out.printf(
//                        "       a   b\n" +
//                        "len:   %d  %d\n" +
//                        "mean:  %f  %f\n" +
//                        "var:   %f  %f\n" +
//                        "cov = %f\n" +
//                        "----\n\n"
//                ,
//                a.length,
//                b.length,
//                meanA,
//                meanB,
//                varA,
//                varB,
//                cov);

        return cov / Math.sqrt(varA * varB);
    }
}
