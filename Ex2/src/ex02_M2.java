import javax.rmi.CORBA.Util;
import java.util.Arrays;
import java.util.List;

public class ex02_M2 {

    public static void main(String[] args) {
        final String SERVER_URL = String.format(Constants.RequestTracesUrl, Constants.Username);

        try {
            String filename = args[0];

//            String inputData = Utils.DownloadTraces(SERVER_URL, Constants.NbTraces);
//            Utils.SaveStringToFile(inputData, filename);
            List<String> inputLines = Utils.LoadFileLines(filename);
            byte[][] plaintexts = Utils.ParsePlaintexts(inputLines);
            double[][] traces = Utils.ParseTracesTimestampFirst(inputLines); // traceLength x numOfTraces

            byte[] key = new byte[Constants.KeyLength];
            for (int i = 0; i < key.length; i++) {
                key[i] = (byte) PowerConsumptionAttack.GuessKeyByte(plaintexts, traces, i);
            }

            System.out.printf("%s,%s\n", Constants.Username, Utils.ByteArrayToHex(key));
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("an error occured.");
        }
    }
}
