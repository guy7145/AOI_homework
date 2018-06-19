import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import javax.rmi.CORBA.Util;
import java.util.Arrays;
import java.util.List;

public class ex02_M2 {

    public static void main(String[] args) {
        final String SERVER_URL = String.format(Constants.RequestTracesUrl, Constants.Username);

        try {
            String filename = args[0];

            List<String> inputLines = Utils.DownloadTraces(SERVER_URL, Constants.NbTraces);
            Utils.SaveLinesToFile(inputLines, filename);
//            inputLines = Utils.LoadFileLines(filename);

            byte[] key = FindKeyFromInputLines(inputLines);
            int[] arr = new int[Constants.NbTraces];
            for (int i = 0; i < Constants.NbTraces; i++) {
                byte[] keyGuess = FindKeyFromInputLines(inputLines.subList(0, i));
                int counter = 0;
                for (int j = 0; j < Constants.KeyLength; j++) {
                    if (key[j] == keyGuess[j]) counter++;
                }
                arr[i] = counter;
            }

            System.out.printf("%s,%s\n", Constants.Username, Utils.ByteArrayToHex(key));

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("an error occured.");
        }
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
