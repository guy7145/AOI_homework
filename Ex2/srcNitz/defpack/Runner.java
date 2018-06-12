package defpack;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import parser.JSONParser;

public class Runner {

	private static final String USERNAME = "hello";
    private static int KEY_SIZE = 16;
    private static BufferedWriter output;

	//static int RECORDED_TRACES_NUMBER = 100;
	public static int RECORDED_TRACES_NUMBER = 25000;
	static int NUMBER_TRACE_POINTS = -1;


	static String user="";
	public static String key="";
    private static double[][] traceMatrix;
    private static double[][] correlation;

    public static String main2(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: Inputfile");
            return null;
		}
		String filename = args[0];

		internetTracesToFile(filename);
        fileTracesToOutput(filename);
        user=USERNAME;
		System.out.println(user + "," + key);
        return key;




}
    static ArrayList<Integer> indexes = new ArrayList<>();

    private static void calculateAndPrintMeanAndSTD(List<Float[]> traces, int i) {
        ArrayList<Float> currentRow = new ArrayList<>();
        for(Float[] trace:traces)
        {
            currentRow.add(trace[i]);
        }
        //System.out.println(currentRow.size());
        if(calculateMeanAndSTD(currentRow)>3)
        {
            indexes.add(i);
        }
    }


    private static float calculateMeanAndSTD(ArrayList<Float> traces) {
        // The mean average
        float mean = 0;
        for (float t : traces) {
            mean += t;
        }
        mean /= traces.size();

        float variance = 0;
        for (float t : traces) {
            variance += (t - mean) * (t - mean);
        }
        variance /= traces.size();
        return mean;
    }

	private static void fileTracesToOutput(String filename) throws IOException {
		List<Float[]> traces = new ArrayList<>();
		List<String> plainTexts = new ArrayList<>();
		int numOfMeasures=0;
        int dontadd = 0;

        //reading file into lists
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
                if(line.contains("[")) {//this is power trace line
                    if(dontadd==1)
                    {
                        dontadd=0;
                    }
                    else {
                        line = line.replace("[", "");
                        line = line.replace("]", "");
                        String[] fileOutput = line.split(",");
                        Float[] outputMeasures = new Float[fileOutput.length];
                        for (int i = 0; i < fileOutput.length; i++) {
                            outputMeasures[i] = Float.parseFloat(fileOutput[i]);
                        }
                        traces.add(outputMeasures);
                    }
                }
                else//this is plaintext line
                {

                    if(line.length()==32) {
                        plainTexts.add(line);
                    }
                    else
                    {
                        dontadd=1;
                    }
                }
			}
		}
		//traces = traces.subList(0,RECORDED_TRACES_NUMBER);
        //plainTexts = plainTexts.subList(0,RECORDED_TRACES_NUMBER);

        for(int i=0;i<traces.get(0).length;i++)//calculating what variables are significant
        {
            calculateAndPrintMeanAndSTD(traces,i);
        }

        for(int i=0;i<traces.size();i++)//reducing garbage variables
            traces.set(i,getFloatAfterReduce(indexes,traces.get(i)));

        NUMBER_TRACE_POINTS = traces.get(0).length;
        int[] keyarr = new int[KEY_SIZE];
        RECORDED_TRACES_NUMBER = traces.size();
        PTraceMatrixCreate(traces,plainTexts); //creating trace matrix
        for(int i=1;i<=KEY_SIZE;i++) {//guessing one byte at a time
            hypothesisCreate(i,plainTexts); //creating hypothesis for this byte
            findCorrelation(); //finding correlation
            keyarr[i-1]= findKeyForByte(); //finding the byte
            key+=String.format("%x",keyarr[i-1]); //adding it to the key
        }

	}

    private static Float[] getFloatAfterReduce(ArrayList<Integer> indexes, Float[] floats) {
        Float[] res = new Float[indexes.size()];
        for(int i=0;i<indexes.size();i++)
        {
            res[i]=floats[indexes.get(i)];
        }
        return res;
    }

    private static void findCorrelation() {
        correlation = new double[256][NUMBER_TRACE_POINTS];
        double x[] = new double[RECORDED_TRACES_NUMBER];
        double y[] = new double[RECORDED_TRACES_NUMBER];
        int count = 0;
        while(count<256){
            for(int j = 0; j< RECORDED_TRACES_NUMBER; j++){
                y[j] = (double)hypothesis[j][count]/256;
            }
            for(int i = 0; i< NUMBER_TRACE_POINTS; i++){
                for(int j = 0; j< RECORDED_TRACES_NUMBER; j++){
                    x[j] = traceMatrix[j][i];
                }
                correlation[count][i] = Helper.Correlation(x,y);
//                System.out.printf("correlation: %f\n", correlation[count][i]);
            }
            count++;
        }
    }

    private static int findKeyForByte() {
        //finding the row with best correlation
        double max = correlation[0][0];
        int loc=0;
        for(int i=0;i<256;i++){
            for(int j = 0; j< NUMBER_TRACE_POINTS; j++){
                if(correlation[i][j]>max){
                    max = correlation[i][j];
                    loc = i;
                }
            }
        }
        return loc;
    }
    static int[][] hypothesis;

    private static void hypothesisCreate(int byteindex, List<String> plainTexts) {
        int keyhyp[] = new int[256];
        for(int i=0;i<255;i++){
            keyhyp[i] = i;
        }//keyhyp contains all possible byte values
        hypothesis = new int[plainTexts.size()][keyhyp.length];
        for(int i=0;i<plainTexts.size();i++){
            String mbyte = plainTexts.get(i).substring((2*(byteindex-1)), (2*byteindex)); //get byte of plaintext
            for(int j=0;j<keyhyp.length;j++){
                //hypothesis the calculated value of plaintext and key together
                hypothesis[i][j] = Helper.HW(Helper.AesSbox[(Helper.hex2int(mbyte) ^ keyhyp[j])]);
            }
        }
    }
    private static void PTraceMatrixCreate(List<Float[]> traces, List<String> plainTexts) {
        traceMatrix = new double[RECORDED_TRACES_NUMBER][traces.get(0).length];
        String t[];
        for(int count = 0; count< RECORDED_TRACES_NUMBER; count++){
            for(int j=0;j<traces.get(0).length;j++){
                traceMatrix[count][j] = traces.get(count)[j];
            }
        }
    }


    private static void internetTracesToFile(String filename) throws IOException {
		//storing traces from internet
        output = null;
		File file = new File(filename);
		output = new BufferedWriter(new FileWriter(file));
		storePowerTraces(RECORDED_TRACES_NUMBER);
		output.close();
	}

	private static void storePowerTraces(int pt_number) throws IOException {
		for (int i = 0; i < pt_number; i++) {
			Encryption e1 = encrypt(USERNAME);
            output.append(e1.s1+"\n");
            output.append(e1.l1 + "\n");
        }
		output.flush();
	}

	public static String verify(String user, String key) {
		String res = urlToString(String.format("http://aoi.ise.bgu.ac.il/verify?user=%s&key=%s", user, key));
		return res;
	}

	public static Encryption encrypt(String user) {
		try {
			String res = urlToString(String.format("http://aoi.ise.bgu.ac.il/encrypt?user=%s", user));
			JSONParser parser = new JSONParser();
			JSONObject jo = (JSONObject) parser.parse(res);
			String s1 = (String) jo.get("plaintext");
			List l1 = (List) jo.get("leaks");
			Encryption e1 = new Encryption(s1, l1);
			return e1;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String urlToString(String surl) {
		String res = null;
		try {
			URL url;
			InputStream is = null;
			BufferedReader br;
			String line;
			res = "";
			try {
				url = new URL(surl);
				is = url.openStream(); // throws an IOException
				br = new BufferedReader(new InputStreamReader(is));

				while ((line = br.readLine()) != null) {
					res = res + line;
				}
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
