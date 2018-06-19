public class Constants {
    public static final int KeyLength = 16;
    public static final int PlaintextLength = KeyLength;
    public static final int PlaintextStringLength = PlaintextLength * 2;

    public static final int NbBitsInByte = 8;
    public static final int NbPossibleQueryGuesses = (int) Math.pow(2, NbBitsInByte); // 2**8 = 256
    public static final int MaxTraceValue = 255; // [0, 255]

    public static final String PasswordCorrect = "1";
    public static final String PasswordIncorrect = "0";

    public static final String Username = "bazwsx";
    public static final int NbTraces = 6000;

    public static final String ServerUrl = "http://aoi.ise.bgu.ac.il/";
    public static final String RequestTracesUrl = ServerUrl + "encrypt?user=%s/";
    public static final String VerifyUrl = ServerUrl + "verify?user=%s&key=%s/";
}
