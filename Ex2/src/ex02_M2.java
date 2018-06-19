import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import javax.rmi.CORBA.Util;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ex02_M2 {

    public static void main(String[] args) {
        final String SERVER_URL = String.format(Constants.RequestTracesUrl, Constants.Username);

        try {
            String filename = args[0];

            List<String> inputLines = Utils.DownloadTraces(SERVER_URL, Constants.NbTraces);
            Utils.SaveLinesToFile(inputLines, filename);

            byte[] key = FindKeyFromInputLines(inputLines);
            System.out.printf("%s,%s\n", Constants.Username, Utils.ByteArrayToHex(key));

//            plotExperiment(inputLines, key);

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("an error occured.");
        }
    }

    private static void plotExperiment(List<String> inputLines, byte[] key) throws Exception {
        int[] arr = new int[Constants.NbTraces];
        int ind = 0;
        for (int i = 0; i < Constants.NbTraces; i += 100, ind++) {
            System.out.printf("\r%d...", i);
            if (i > 0 && arr[ind - 1] == 16) {
                arr[ind] = 16;
                continue;
            }
            byte[] keyGuess = FindKeyFromInputLines(inputLines.subList(0, i + 1));
            int counter = 0;
            for (int j = 0; j < Constants.KeyLength; j++) {
                if (key[j] == keyGuess[j]) counter++;
            }
            arr[ind] = counter;
            System.out.println(counter);
        }
        List<String> csvContent = new LinkedList<>();

        ind = 0;
        for (int i = 0; i < Constants.NbTraces; i += 100, ind++) {
            csvContent.add(String.valueOf(arr[ind]));
        }
        csvContent.add("\n");
        Utils.SaveLinesToFile(csvContent, "results.csv");
    }

    private static byte[] FindKeyFromInputLines(List<String> inputLines) throws Exception {
        byte[][] plaintexts = Utils.ParsePlaintexts(inputLines);
        double[][] traces = Utils.ParseTracesTimestampFirst(inputLines); // traceLength x numOfTraces

        byte[] key = new byte[Constants.KeyLength];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) PowerConsumptionAttack.GuessKeyByte(plaintexts, traces, i);
        }
        return key;
    }
}
