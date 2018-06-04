import java.util.Arrays;
import java.util.List;

public class ex02_M2 {

    public static void main(String[] args) {
        final String SERVER_URL = String.format(Constants.RequestTracesUrl, Constants.Username);

        try {
            String filename = args[0];

            System.out.println("downloading traces...");
            String inputData = Utils.DownloadTraces(SERVER_URL, Constants.NbTraces);
            System.out.println("saving traces...");
            Utils.SaveStringToFile(inputData, filename);
            System.out.println("parsing plaintext...");
            List<String> inputLines = Arrays.asList(inputData.split("\n"));
//            List<String> inputLines = Utils.LoadFileLines(filename);
            byte[][] plaintexts = Utils.ParsePlaintexts(inputLines);

            System.out.println("parsing traces...");
            double[][] traces = Utils.ParseTracesTimestampFirst(inputLines); // traceLength x numOfTraces
            byte[] key = new byte[Constants.KeyLength];
            for (int i = 0; i < key.length; i++) {
                key[i] = (byte) PowerConsumptionAttack.GuessKeyByte(plaintexts, traces, i);
            }
            System.out.printf("key: ");
            for (int i = 0; i < key.length; i++) {
                System.out.printf("%x", key[i]);
            }
            System.out.println();
            System.out.printf("key correct: %b\n", Utils.VerifyKey(key));
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("an error occured.");
        }
    }
}
