public class ex02_M1 {
    public static void main(String[] args) {
        final String USERNAME = "matan";
        final String SERVER_URL = String.format("http://aoi.ise.bgu.ac.il/encrypt?user=%s/", USERNAME);
        final int NB_TRACES = 10000;

        try {
            String filename = args[0];
            Utils.DownloadAndSaveTraces(SERVER_URL, NB_TRACES, filename);

            // nbFeatures X nbMeasurements -> double
            double traces[][] = Utils.ReadTracesFromFile(filename);
            int nbFeatures = traces.length, nbMeasurements = traces[0].length;

            double means[] = new double[nbFeatures];
            double variances[] = new double[nbFeatures];

            for (int i = 0; i < nbFeatures; i++) {
                means[i] = Statistics.CalcMean(traces[i]);
                variances[i] = Statistics.CalcVariance(traces[i], means[i]);
            }

            System.out.println("Mean\tVariance");
            for (int i = 0; i < nbFeatures; i++) System.out.println(String.format("%.2f\t%.2f", means[i], variances[i]));

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("an error occured.");
        }
    }
}
