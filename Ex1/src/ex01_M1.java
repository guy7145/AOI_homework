import java.util.Arrays;

public class ex01_M1 {
    public static void main(String[] args) {
        if (args.length != 1) System.out.println("invalid arguments");
        else {
            String url = args[0];
            long[] nanoSecondsPlaceholder = new long[1];

            try {

                TimingAttack.httpGet(url, nanoSecondsPlaceholder);
                System.out.println(TimingAttack.nanoToMilli(nanoSecondsPlaceholder[0]));

            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
