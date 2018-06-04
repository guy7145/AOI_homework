public class Constants {
    public static final int KeyLength = 16;
    public static final int PlaintextLength = KeyLength;

    public static final int NbBitsInByte = 8;
    public static final int NbPossibleQueryGuesses = (int) Math.pow(2, NbBitsInByte);

    public static final String PasswordCorrect = "1";
    public static final String PasswordIncorrect = "0";

    public static final String Username = "tom";
    public static final int NbTraces = 10000;

    public static final String ServerUrl = "http://aoi.ise.bgu.ac.il/";
    public static final String RequestTracesUrl = ServerUrl + "encrypt?user=%s/";
    public static final String VerifyUrl = ServerUrl + "verify?user=%s&key=%s/";
}
