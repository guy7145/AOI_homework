public class Constants {
    public static final int KeyLength = 16;
    public static final int PlaintextLength = KeyLength;

    public static final int NbBitsInByte = 8;
    public static final int NbPossibleQueryGuesses = (int) Math.pow(2, NbBitsInByte); // 2**8 = 256
    public static final int MaxTraceValue = 255; // [0, 255]

    public static final String PasswordCorrect = "1";
    public static final String PasswordIncorrect = "0";

    public static final String Username = "guyGuy";
    public static final int NbTraces = 70000;

    public static final String ServerUrl = "http://aoi.ise.bgu.ac.il/";
    public static final String RequestTracesUrl = ServerUrl + "encrypt?user=%s/";
    public static final String VerifyUrl = ServerUrl + "verify?user=%s&key=%s/";

    // for testing
    public static final byte[] TOMS_KEY = Utils.hexStringToByteArray("2bdb909b05567ac45799be710f4bf80e");
    public static final String TOMS_KEY_STR = Utils.ByteArrayToHex(TOMS_KEY);
    public static final String KEY_STR = "2bdb909b05567ac45799be710f4bf80e";
    public static final boolean EQ_TEST = KEY_STR.equals(TOMS_KEY_STR);

}
