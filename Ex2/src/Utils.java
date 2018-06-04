import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class Utils {
    static String httpGet(String url) throws Exception {
        URL obj = new URL(url);
        String inputLine;
        StringBuilder response = new StringBuilder();

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();

        return response.toString();
    }

    static String DownloadTraces(String SERVER_URL, int NB_TRACES) throws Exception {
        StringBuilder fileBuilder = new StringBuilder();
        for (int i = 0; i < NB_TRACES; i++) {
            String jsonString = httpGet(SERVER_URL);
            fileBuilder.append(jsonString);
            fileBuilder.append(System.lineSeparator());
        }
        return fileBuilder.toString();
    }

    public static void SaveStringToFile(String data, String filename) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(data);
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static double[] jsonStringToDoubleArray(String json) {
        int i1 = json.indexOf('[');
        int i2 = json.lastIndexOf(']');
        String[] strsArray = json.substring(i1 + 1, i2).split(",");
        double[] result = new double[strsArray.length];

        for (int i = 0; i < strsArray.length; i++)
            result[i] = Double.valueOf(strsArray[i]);

        return result;
    }

    private static byte[] jsonStringToPlaintext(String json) {
        int commaIdx = json.indexOf(',');
        int ddIdx = json.indexOf(':');
        String hexString = json.substring(ddIdx+2, commaIdx-1);
        return hexStringToByteArray(hexString);
    }

    static void DownloadAndSaveTraces(String SERVER_URL, int NB_TRACES, String filename) throws Exception {
        String traces = DownloadTraces(SERVER_URL, NB_TRACES);
        SaveStringToFile(traces, filename);
    }

    static List<String> LoadFileLines(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    static double[][] ParseTracesTimestampFirst(List<String> inputLines) {
        int nbFeatures, nbMeasurements;

        LinkedList<double[]> listOfTraces = new LinkedList<>();
        inputLines.forEach(line -> listOfTraces.add(jsonStringToDoubleArray(line)));

        nbFeatures = listOfTraces.getFirst().length;
        nbMeasurements = listOfTraces.size();
        double[][] measuresMatrix = new double[nbFeatures][nbMeasurements];

        for (int i = 0; i < nbMeasurements; i++) {
            double[] arr = listOfTraces.pop();
            for (int j = 0; j < nbFeatures; j++) {
                measuresMatrix[j][i] = arr[j];
            }
        }

        return measuresMatrix;
    }

    static byte[][] ParsePlaintexts(List<String> inputLines) {
        byte[][] plaintexts = new byte[inputLines.size()][];
        int i = 0;
        for (String line: inputLines) {
            plaintexts[i] = jsonStringToPlaintext(line);
            i++;
        }
        return plaintexts;
    }

    static double[][] ReadTracesFromFile(String filename) throws Exception {
        return ParseTracesTimestampFirst(LoadFileLines(filename));
    }

    public static double[][] FlipMatrix(double[][] mat) {
        int origRows = mat.length;
        int origCols = mat[0].length;
        double[][] res = new double[origCols][origRows];

        for (int i = 0; i < origRows; i++) {
//            System.out.printf("length: %d\n", mat[i].length);
            for (int j = 0; j < origCols; j++) {
                double a = mat[i][j];
                res[j][i] = a;
            }
        }

        return res;
    }

    static void printArray(double[] arr) {
        for (double anArr : arr) {
            System.out.print(anArr);
            System.out.print(", ");
        }
        System.out.println();
    }

    static void printArray(int[] arr) {
        for (int anArr : arr) {
            System.out.print(anArr);
            System.out.print(", ");
        }
        System.out.println();
    }
}
